package ru.gagarkin.gxfin.quik.datacontrollers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.gagarkin.gxfin.gate.quik.connector.QuikConnector;
import ru.gagarkin.gxfin.gate.quik.dto.StandardPackage;
import ru.gagarkin.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gagarkin.gxfin.quik.api.Provider;
import ru.gagarkin.gxfin.quik.api.ProviderDataController;
import ru.gagarkin.gxfin.quik.errors.ProviderException;
import ru.gagarkin.gxfin.quik.events.AbstractProviderDataEvent;

import java.io.Closeable;
import java.io.IOException;

/**
 * Шаблон реализации контролера чтения стандартного потока данных
 * @param <E> тип события-команды о чтении пакета данных
 * @param <P> тип пакета данных
 */
@Slf4j
abstract class StandardQuikProviderDataController<E extends AbstractProviderDataEvent, P extends StandardPackage>
        implements ProviderDataController<E>, Closeable {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields & Properties">
    /**
     * Ссылка на сам Провайдер, получаем в конструкторе
     */
    @Getter(AccessLevel.PROTECTED)
    private final Provider provider;

    /**
     * Ссылка на коннектор, получаем из провайдера
     */
    @Getter(AccessLevel.PROTECTED)
    private final QuikConnector connector;

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
    private long lastReadedMilliseconds;
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    public StandardQuikProviderDataController(Provider provider) {
        super();
        this.provider = provider;
        this.connector = provider.getConnector();
        this.lastIndex = -1;
        this.allCount = 0;
    }

    public void init(int packageSize, int intervalWaitOnNextLoad) {
        this.packageSize = packageSize;
        this.intervalWaitOnNextLoad = intervalWaitOnNextLoad;
        this.provider.registerDataController(this);
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Implementation Closeable">
    @Override
    public void close() throws IOException {
        this.provider.unRegisterDataController(this);
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Main functional">
    /**
     * Обработка пакета данных, полученного от Quik-а
     * @param standardPackage
     */
    protected synchronized void proceedPackage(P standardPackage) {
        var n = standardPackage.rows.length;
        if (n > 0) {
            this.lastIndex = standardPackage.rows[n - 1].rowIndex;
        }
        this.allCount = standardPackage.allCount;
        this.lastReadedMilliseconds = System.currentTimeMillis();
        log.info("Loaded {}, packageSize = {}, lastIndex = {} / allCount = {}", standardPackage.getClass().getSimpleName(), n, this.lastIndex, this.allCount);
    }

    /**
     * Чтение пакета данных из Quik-а (реализация наследниках)
     * @param lastIndex индекс последней записи, полученной во время предыдущего чтения
     * @param packageSize ограничение в количество записей в пакете (указание для Quik-а)
     * @return сам пакет записей потока данных, который контролирует данный контроллер
     * @throws IOException
     * @throws QuikConnectorException
     * @throws ProviderException
     */
    protected abstract P getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException, ProviderException;

    /**
     * Обработка события-команды на необходимость чтения пакета данных
     * @param event команда о чтении
     * @throws ProviderException
     * @throws IOException
     * @throws QuikConnectorException
     */
    @Override
    public void onEvent(E event) throws ProviderException, IOException, QuikConnectorException {
        if (this.needReload()) {
            var thePackage = this.getPackage(this.getLastIndex() + 1, this.getPackageSize());
            proceedPackage(thePackage);
        }
    }

    /**
     * Вычисляется необходимость прям сейчас чтения данных.
     * Если по нашим сведениям в Quik-е еще есть записи (allCount > lastIndex+1)
     * или прошло достаточно времени (см. intervalWaitOnNextLoad) с момента последнего чтения
     * @return true - надо прочитать данные прям сейчас
     */
    @Override
    public boolean needReload() {
        var now = System.currentTimeMillis();
        return (this.allCount - 1 > this.lastIndex || now - this.lastReadedMilliseconds > this.intervalWaitOnNextLoad);
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Additional">
    /**
     * Используется в Runnner-е для создания события-команды
     * @param source объект-источник для события
     * @return событие-команда
     */
    public abstract E createEvent(Object source);
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
