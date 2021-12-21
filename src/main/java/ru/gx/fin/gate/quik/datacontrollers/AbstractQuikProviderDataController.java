package ru.gx.fin.gate.quik.datacontrollers;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Header;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.core.channels.ChannelConfigurationException;
import ru.gx.core.kafka.LongHeader;
import ru.gx.core.kafka.upload.KafkaOutcomeTopicUploadingDescriptor;
import ru.gx.core.kafka.upload.KafkaOutcomeTopicsUploader;
import ru.gx.core.messaging.DefaultMessagesFactory;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;
import ru.gx.core.messaging.MessageHeader;
import ru.gx.core.simpleworker.SimpleWorkerOnIterationExecuteEvent;
import ru.gx.fin.gate.quik.config.KafkaOutcomeTopicsConfiguration;
import ru.gx.fin.gate.quik.connector.QuikConnector;
import ru.gx.fin.gate.quik.errors.ProviderException;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.fin.gate.quik.provider.internal.QuikStandardDataObject;
import ru.gx.fin.gate.quik.provider.internal.QuikStandardDataPackage;

import java.io.IOException;
import java.util.HashMap;

import static lombok.AccessLevel.PROTECTED;
import static lombok.AccessLevel.PUBLIC;

/**
 * Шаблон реализации контролера чтения стандартного потока данных
 *
 * @param <P> тип пакета данных
 */
@Slf4j
public abstract class AbstractQuikProviderDataController<M extends Message<? extends MessageHeader, ? extends MessageBody>, O extends QuikStandardDataObject, P extends QuikStandardDataPackage<O>>
        implements ProviderDataController {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields & Properties">

    /**
     * Ссылка на коннектор, получаем из провайдера
     */
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    @NotNull
    private QuikConnector connector;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private DefaultMessagesFactory messagesFactory;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    @NotNull
    private KafkaOutcomeTopicsUploader kafkaUploader;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    @NotNull
    private KafkaOutcomeTopicsConfiguration kafkaConfiguration;

    @Getter(PROTECTED)
    @Setter(PROTECTED)
    private KafkaOutcomeTopicUploadingDescriptor<M> kafkaDescriptor;

    @Getter(PROTECTED)
    @NotNull
    private final HashMap<String, Header> kafkaHeaders;

    @Getter(PROTECTED)
    @NotNull
    private final LongHeader kafkaHeaderAllCount;

    @Getter(PROTECTED)
    @NotNull
    private final LongHeader kafkaHeaderLastIndex;

    /**
     * Индекс (который этой записи присвоил Quik) последней записи, прочитанной из Quik-а.
     */
    @Getter(PUBLIC)
    private long lastIndex;

    /**
     * Всего записей в данной таблице в Quik-е
     */
    @Getter(PUBLIC)
    private long allCount;

    /**
     * Лимит количества записей в пакете - указание для Quik-а, чтобы он в пакет нам давал не более
     */
    @Getter(PUBLIC)
    private int packageSize;

    /**
     * Интервал (в миллисекундах), по истечение которого надо все равно запросить данные из Quik-а
     * Не запрашиваем раньше, если по нашим данным мы уже все прочитали (allCount == lastIndex + 1)
     */
    @Getter(PROTECTED)
    private int intervalWaitOnNextLoad;

    /**
     * Когда в последний раз получали данные из Quik-а (System.currentTimeMillis())
     */
    @Getter(PROTECTED)
    private long lastReadMilliseconds;

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Settings">
    protected abstract String outcomeTopicName();

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    protected AbstractQuikProviderDataController() {
        super();
        this.lastIndex = -1;
        this.allCount = 0;
        this.kafkaHeaders = new HashMap<>();
        this.kafkaHeaderAllCount = new LongHeader(headerKeyAllCount, 0);
        this.kafkaHeaders.put(headerKeyAllCount, this.kafkaHeaderAllCount);
        this.kafkaHeaderLastIndex = new LongHeader(headerKeyLastIndex, 0);
        this.kafkaHeaders.put(headerKeyLastIndex, this.kafkaHeaderLastIndex);
    }

    public void init(int packageSize, int intervalWaitOnNextLoad) {
        this.packageSize = packageSize;
        this.intervalWaitOnNextLoad = intervalWaitOnNextLoad;
    }

    protected void initDescriptor() {
        final var descriptor = this.getKafkaConfiguration().get(outcomeTopicName());
        if (descriptor instanceof KafkaOutcomeTopicUploadingDescriptor) {
            setKafkaDescriptor((KafkaOutcomeTopicUploadingDescriptor<M>) descriptor);
        } else {
            throw new ChannelConfigurationException("Descriptor is unsupported type " + descriptor.getClass().getSimpleName() + "; wait type is " + KafkaOutcomeTopicUploadingDescriptor.class.getSimpleName());
        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Main functional">

    /**
     * Обработка пакета данных, полученного от Quik-а.
     *
     * @param standardPackage Пакет данных, который обрабатываем.
     */
    @SuppressWarnings("unchecked")
    protected synchronized void proceedPackage(P standardPackage) throws Exception {
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

        if (allCountChanged || standardPackage.size() > 0) {
            if (getKafkaDescriptor() == null) {
                initDescriptor();
            }

            this.kafkaHeaderAllCount.setValue(standardPackage.allCount);
            this.kafkaHeaderLastIndex.setValue(this.lastIndex);
            final var messageType = this.kafkaDescriptor.getApi().getMessageType();
            final var version = this.kafkaDescriptor.getApi().getVersion();
            final var message = (M) this.messagesFactory.createByDataPackage(null, messageType, version, standardPackage, null);

            final var offset = kafkaUploader.uploadMessage(
                    getKafkaDescriptor(),
                    message,
                    getKafkaHeaders().values()
            );
            log.info("Loaded {}, packageSize = {}, lastIndex = {} / allCount = {}; Uploaded (p:{}, o:{})", standardPackage.getClass().getSimpleName(), n, this.lastIndex, this.allCount, offset.getPartition(), offset.getOffset());
        } else {
            log.info("Loaded {}, packageSize = {}, lastIndex = {} / allCount = {}", standardPackage.getClass().getSimpleName(), n, this.lastIndex, this.allCount);
        }
    }

    /**
     * Чтение пакета данных из Quik-а (реализация в наследниках)
     *
     * @param lastIndex   индекс последней записи, полученной во время предыдущего чтения
     * @param packageSize ограничение в количество записей в пакете (указание для Quik-а)
     * @return сам пакет записей потока данных, который контролирует данный контроллер.
     * @throws IOException            Ошибки работы с NamedPipe.
     * @throws QuikConnectorException Ошибки работы с QuikConnector.
     * @throws ProviderException      Ошибки самого Провайдера.
     */
    protected abstract P getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException, ProviderException;

    /**
     * Обработка команды на чтение пакета данных.
     *
     * @throws ProviderException      Ошибки Провайдера.
     * @throws IOException            Ошибки работы с NamedPipe.
     * @throws QuikConnectorException Ошибки работы с Connector-ом.
     */
    @Override
    public void load(SimpleWorkerOnIterationExecuteEvent iterationExecuteEvent) throws Exception {
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
        if (getKafkaDescriptor() == null) {
            initDescriptor();
        }
        return getKafkaDescriptor().isEnabled()
                && (this.allCount - 1 > this.lastIndex || now - this.lastReadMilliseconds > this.intervalWaitOnNextLoad);
    }

    @Override
    public void clean() {
        this.lastIndex = -1;
        this.allCount = 0;
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
