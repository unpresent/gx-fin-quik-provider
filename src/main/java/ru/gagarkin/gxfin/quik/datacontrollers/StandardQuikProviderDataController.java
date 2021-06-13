package ru.gagarkin.gxfin.quik.datacontrollers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gagarkin.gxfin.gate.quik.connector.QuikConnector;
import ru.gagarkin.gxfin.gate.quik.dto.StandardPackage;
import ru.gagarkin.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gagarkin.gxfin.quik.api.Provider;
import ru.gagarkin.gxfin.quik.api.ProviderDataController;
import ru.gagarkin.gxfin.quik.errors.ProviderException;
import ru.gagarkin.gxfin.quik.events.ProviderIterationExecuteEvent;

import java.io.IOException;

/**
 * Шаблон реализации контролера чтения стандартного потока данных
 * @param <P> тип пакета данных
 */
@Slf4j
abstract class StandardQuikProviderDataController<P extends StandardPackage>
        implements ProviderDataController {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields & Properties">
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
    public StandardQuikProviderDataController() {
        super();
        this.lastIndex = -1;
        this.allCount = 0;
    }

    public void init(int packageSize, int intervalWaitOnNextLoad) {
        this.packageSize = packageSize;
        this.intervalWaitOnNextLoad = intervalWaitOnNextLoad;
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
     * Обработка команды на чтение пакета данных
     * @throws ProviderException
     * @throws IOException
     * @throws QuikConnectorException
     */
    @Override
    public void load(ProviderIterationExecuteEvent iterationExecuteEvent) throws ProviderException, IOException, QuikConnectorException {
        if (this.needReload()) {
            var thePackage = this.getPackage(this.getLastIndex() + 1, this.getPackageSize());
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
     * @return true - надо прочитать данные прям сейчас
     */
    public boolean needReload() {
        var now = System.currentTimeMillis();
        return (this.allCount - 1 > this.lastIndex || now - this.lastReadedMilliseconds > this.intervalWaitOnNextLoad);
    }

    @Override
    public void clean() {
        this.lastIndex = -1;
        this.allCount = 0;
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
