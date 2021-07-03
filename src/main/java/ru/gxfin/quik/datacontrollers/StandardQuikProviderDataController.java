package ru.gxfin.quik.datacontrollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gxfin.gate.quik.connector.QuikConnector;
import ru.gxfin.gate.quik.model.internal.StandardDataPackage;
import ru.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gxfin.quik.api.Provider;
import ru.gxfin.quik.api.ProviderDataController;
import ru.gxfin.quik.api.ProviderSettingsController;
import ru.gxfin.quik.errors.ProviderException;
import ru.gxfin.quik.events.ProviderIterationExecuteEvent;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Шаблон реализации контролера чтения стандартного потока данных
 *
 * @param <P> тип пакета данных
 */
@Slf4j
abstract class StandardQuikProviderDataController<P extends StandardDataPackage>
        implements ProviderDataController {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields & Properties">
    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private ObjectMapper objectMapper;

    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private ProviderSettingsController settings;

    /**
     * Ссылка на сам Провайдер, получаем в конструкторе
     */
    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private Provider provider;

    /**
     * Ссылка на коннектор, получаем из провайдера
     */
    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private QuikConnector connector;

    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private KafkaProducer<Long, String> kafkaProducer;

    /**
     * Индекс (который этой записи присвоил Quik) последней записи, прочитанной из Quik-а
     */
    @Getter(AccessLevel.PROTECTED)
    private long lastIndex;

    /**
     * Всего записей в данной таблице в Quik-е
     */
    @Getter(AccessLevel.PROTECTED)
    private long allCount;

    /**
     * Лимит количества записей в пакете - указание для Quik-а, чтобы он в пакет нам давал не более
     */
    @Getter(AccessLevel.PROTECTED)
    private int packageSize;

    /**
     * Интервал (с миллисекундах), по истечение которого надо все равно запросить данные из Quik-а
     * Не запрашиваем раньше, если по нашим данным мы уже все прочитали (allCount == lastIndex + 1)
     */
    @Getter(AccessLevel.PROTECTED)
    private int intervalWaitOnNextLoad;

    /**
     * Когда в полседний раз получали данные из Quik-а (System.currentTimeMillis())
     */
    @Getter(AccessLevel.PROTECTED)
    private long lastReadMilliseconds;

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Settings">
    protected abstract String outcomeTopicName();

    protected KafkaProducer getKafkaProducer() {
        return this.kafkaProducer;
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    public StandardQuikProviderDataController() {
        super();
        this.lastIndex = -1;
        this.allCount = 0;
    }

    public void init(int packageSize, int intervalWaitOnNextLoad) {
        this.packageSize = packageSize;
        this.intervalWaitOnNextLoad = intervalWaitOnNextLoad;
    }

    @PostConstruct
    public void postInit() {
        if (this.provider == null) {
            log.error("this.provider == null");
        }
        if (this.connector == null) {
            log.error("this.connector == null");
        }
        if (this.getKafkaProducer() == null) {
            log.error("this.getKafkaPoducer() == null");
        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Main functional">

    /**
     * Обработка пакета данных, полученного от Quik-а
     *
     * @param standardPackage
     */
    protected synchronized void proceedPackage(P standardPackage) throws JsonProcessingException, ExecutionException, InterruptedException {
        var allCountChanged = false;
        final var n = standardPackage.size();
        if (n > 0) {
            this.lastIndex = standardPackage.getItem(n - 1).rowIndex;
        }
        if (this.allCount != standardPackage.allCount) {
            this.allCount = standardPackage.allCount;
            allCountChanged = true;
        }
        this.lastReadMilliseconds = System.currentTimeMillis();
        log.info("Loaded {}, packageSize = {}, lastIndex = {} / allCount = {}", standardPackage.getClass().getSimpleName(), n, this.lastIndex, this.allCount);

        final var kafkaProducer = this.getKafkaProducer();
        if (kafkaProducer != null && (allCountChanged || n > 0)) {
            var message = objectMapper.writeValueAsString(standardPackage);
            final var rmd = (RecordMetadata) kafkaProducer.send(new ProducerRecord<>(outcomeTopicName(), message)).get();
        }
    }

    /**
     * Чтение пакета данных из Quik-а (реализация наследниках)
     *
     * @param lastIndex   индекс последней записи, полученной во время предыдущего чтения
     * @param packageSize ограничение в количество записей в пакете (указание для Quik-а)
     * @return сам пакет записей потока данных, который контролирует данный контроллер
     * @throws IOException
     * @throws QuikConnectorException
     * @throws ProviderException
     */
    protected abstract P getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException, ProviderException;

    /**
     * Обработка команды на чтение пакета данных
     *
     * @throws ProviderException
     * @throws IOException
     * @throws QuikConnectorException
     */
    @Override
    public void load(ProviderIterationExecuteEvent iterationExecuteEvent)
            throws ProviderException, IOException, QuikConnectorException, ExecutionException, InterruptedException {
        if (this.needReload()) {
            final var thePackage = this.getPackage(this.getLastIndex() + 1, this.getPackageSize());
            proceedPackage(thePackage);
            if (!iterationExecuteEvent.isImmediateRunNextIteration() && needReload()) {
                iterationExecuteEvent.setImmediateRunNextIteration(true);
            }
        }
    }

    /**
     * Вычисляется необходимость прям сейчас чтения данных.
     * Если по нашим сведениям в Quik-е еще есть записи (allCount > lastIndex+1)
     * или прошло достаточно времени (см. intervalWaitOnNextLoad) с момента последнего чтения
     *
     * @return true - надо прочитать данные прям сейчас
     */
    public boolean needReload() {
        final var now = System.currentTimeMillis();
        return (this.allCount - 1 > this.lastIndex || now - this.lastReadMilliseconds > this.intervalWaitOnNextLoad);
    }

    @Override
    public void clean() {
        this.lastIndex = -1;
        this.allCount = 0;
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
