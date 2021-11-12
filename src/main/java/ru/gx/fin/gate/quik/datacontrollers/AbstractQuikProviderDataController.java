package ru.gx.fin.gate.quik.datacontrollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Header;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.fin.gate.quik.connector.QuikConnector;
import ru.gx.fin.gate.quik.errors.ProviderException;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.fin.gate.quik.provider.QuikProvider;
import ru.gx.fin.gate.quik.provider.QuikProviderSettingsContainer;
import ru.gx.fin.gate.quik.provider.internal.QuikStandardDataObject;
import ru.gx.fin.gate.quik.provider.internal.QuikStandardDataPackage;
import ru.gx.kafka.LongHeader;
import ru.gx.kafka.upload.OutcomeTopicUploader;
import ru.gx.kafka.upload.OutcomeTopicsConfiguration;
import ru.gx.kafka.upload.StandardOutcomeTopicUploadingDescriptor;
import ru.gx.worker.SimpleOnIterationExecuteEvent;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;

import static lombok.AccessLevel.PROTECTED;

/**
 * Шаблон реализации контролера чтения стандартного потока данных
 *
 * @param <P> тип пакета данных
 */
@Slf4j
public abstract class AbstractQuikProviderDataController<O extends QuikStandardDataObject, P extends QuikStandardDataPackage<O>>
        implements ProviderDataController {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields & Properties">
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private ObjectMapper objectMapper;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikProviderSettingsContainer settings;

    @Getter(PROTECTED)
    @Setter(value = AccessLevel.PROTECTED, onMethod_ = @Autowired)
    private OutcomeTopicUploader uploader;

    @Getter(PROTECTED)
    @Setter(value = AccessLevel.PROTECTED, onMethod_ = @Autowired)
    private OutcomeTopicsConfiguration configuration;

    @Getter(PROTECTED)
    @Setter(PROTECTED)
    private StandardOutcomeTopicUploadingDescriptor<O, P> descriptor;

    @Getter(PROTECTED)
    @NotNull
    private final HashMap<String, Header> headers;

    @Getter(PROTECTED)
    @NotNull
    private final LongHeader headerAllCount;

    @Getter(PROTECTED)
    @NotNull
    private final LongHeader headerLastIndex;

    /**
     * Ссылка на сам Провайдер, получаем в конструкторе
     */
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikProvider provider;

    /**
     * Ссылка на коннектор, получаем из провайдера
     */
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikConnector connector;

    /**
     * Индекс (который этой записи присвоил Quik) последней записи, прочитанной из Quik-а.
     */
    @Getter(PROTECTED)
    private long lastIndex;

    /**
     * Всего записей в данной таблице в Quik-е
     */
    @Getter(PROTECTED)
    private long allCount;

    /**
     * Лимит количества записей в пакете - указание для Quik-а, чтобы он в пакет нам давал не более
     */
    @Getter(PROTECTED)
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
        this.headers = new HashMap<>();
        this.headerAllCount = new LongHeader(headerKeyAllCount, 0);
        this.headers.put(headerKeyAllCount, this.headerAllCount);
        this.headerLastIndex = new LongHeader(headerKeyLastIndex, 0);
        this.headers.put(headerKeyLastIndex, this.headerLastIndex);
    }

    public void init(int packageSize, int intervalWaitOnNextLoad) {
        this.packageSize = packageSize;
        this.intervalWaitOnNextLoad = intervalWaitOnNextLoad;
    }

    @PostConstruct
    public void postInit() {
        if (this.provider == null) {
            throw new InvalidParameterException("this.provider == null");
        }
        if (this.connector == null) {
            throw new InvalidParameterException("this.connector == null");
        }
        if (this.settings == null) {
            throw new InvalidParameterException("this.settings == null");
        }
        if (this.uploader == null) {
            throw new InvalidParameterException("this.uploader == null");
        }
    }

    protected void initDescriptor() {
        setDescriptor(getConfiguration().get(outcomeTopicName()));
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Main functional">

    /**
     * Обработка пакета данных, полученного от Quik-а.
     *
     * @param standardPackage Пакет данных, который обрабатываем.
     */
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
            if (getDescriptor() == null) {
                initDescriptor();
            }

            this.headerAllCount.setValue(standardPackage.allCount);
            this.headerLastIndex.setValue(this.lastIndex);
            final var offset = uploader.uploadDataPackage(
                    getDescriptor(),
                    standardPackage,
                    getHeaders().values()
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
    public void load(SimpleOnIterationExecuteEvent iterationExecuteEvent)
            throws Exception {
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
