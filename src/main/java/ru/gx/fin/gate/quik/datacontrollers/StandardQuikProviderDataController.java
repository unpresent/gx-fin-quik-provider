package ru.gx.fin.gate.quik.datacontrollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.fin.gate.quik.connector.QuikConnector;
import ru.gx.fin.gate.quik.errors.ProviderException;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.fin.gate.quik.model.internal.QuikStandardDataObject;
import ru.gx.fin.gate.quik.model.internal.QuikStandardDataPackage;
import ru.gx.fin.gate.quik.provider.QuikProvider;
import ru.gx.fin.gate.quik.provider.QuikProviderSettingsController;
import ru.gx.worker.SimpleIterationExecuteEvent;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Шаблон реализации контролера чтения стандартного потока данных
 *
 * @param <P> тип пакета данных
 */
@Slf4j
public abstract class StandardQuikProviderDataController<O extends QuikStandardDataObject, P extends QuikStandardDataPackage<O>>
        implements ProviderDataController {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields & Properties">
    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private ObjectMapper objectMapper;

    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private QuikProviderSettingsController settings;

    /**
     * Ссылка на сам Провайдер, получаем в конструкторе
     */
    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private QuikProvider provider;

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
     * Индекс (который этой записи присвоил Quik) последней записи, прочитанной из Quik-а.
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
     * Интервал (в миллисекундах), по истечение которого надо все равно запросить данные из Quik-а
     * Не запрашиваем раньше, если по нашим данным мы уже все прочитали (allCount == lastIndex + 1)
     */
    @Getter(AccessLevel.PROTECTED)
    private int intervalWaitOnNextLoad;

    /**
     * Когда в последний раз получали данные из Quik-а (System.currentTimeMillis())
     */
    @Getter(AccessLevel.PROTECTED)
    private long lastReadMilliseconds;

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Settings">
    protected abstract String outcomeTopicName();
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
            log.error("this.getKafkaProducer() == null");
        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Main functional">

    /**
     * Обработка пакета данных, полученного от Quik-а.
     * @param standardPackage   Пакет данных, который обрабатываем.
     */
    protected synchronized void proceedPackage(P standardPackage) throws JsonProcessingException, ExecutionException, InterruptedException {
        var allCountChanged = false;
        final var n = standardPackage.size();
        if (n > 0) {
            this.lastIndex = standardPackage.getObject(n - 1).getRowIndex();
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
            kafkaProducer.send(new ProducerRecord<>(outcomeTopicName(), message)).get();
        }
    }

    /**
     * Чтение пакета данных из Quik-а (реализация в наследниках)
     *
     * @param lastIndex   индекс последней записи, полученной во время предыдущего чтения
     * @param packageSize ограничение в количество записей в пакете (указание для Quik-а)
     * @return сам пакет записей потока данных, который контролирует данный контроллер.
     * @throws IOException              Ошибки работы с NamedPipe.
     * @throws QuikConnectorException   Ошибки работы с QuikConnector.
     * @throws ProviderException        Ошибки самого Провайдера.
     */
    protected abstract P getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException, ProviderException;

    /**
     * Обработка команды на чтение пакета данных.
     * @throws ProviderException        Ошибки Провайдера.
     * @throws IOException              Ошибки работы с NamedPipe.
     * @throws QuikConnectorException   Ошибки работы с Connector-ом.
     */
    @Override
    public void load(SimpleIterationExecuteEvent iterationExecuteEvent)
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