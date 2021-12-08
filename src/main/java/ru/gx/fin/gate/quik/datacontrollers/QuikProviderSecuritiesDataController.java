package ru.gx.fin.gate.quik.datacontrollers;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.core.data.NotAllowedObjectUpdateException;
import ru.gx.core.redis.upload.RedisOutcomeCollectionLoadingDescriptor;
import ru.gx.core.redis.upload.RedisOutcomeCollectionsUploader;
import ru.gx.core.redis.upload.SimpleRedisOutcomeCollectionsConfiguration;
import ru.gx.fin.gate.quik.converters.QuikSecurityFromOriginalQuikSecurityConverter;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.fin.gate.quik.provider.config.QuikProviderChannelsNames;
import ru.gx.fin.gate.quik.provider.out.QuikSecuritiesPackage;
import ru.gx.fin.gate.quik.provider.out.QuikSecurity;
import ru.gx.fin.gate.quik.provider.out.QuikSessionedSecuritiesPackage;
import ru.gx.fin.gate.quik.provider.out.QuikSessionedSecurity;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;

import static lombok.AccessLevel.PROTECTED;

/**
 * Контролер чтения поручений
 */
@Slf4j
public class QuikProviderSecuritiesDataController
        extends AbstractQuikProviderDataController<QuikSessionedSecurity, QuikSessionedSecuritiesPackage> {

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikSecurityFromOriginalQuikSecurityConverter converter;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private RedisOutcomeCollectionsUploader redisUploader;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private SimpleRedisOutcomeCollectionsConfiguration redisConfiguration;

    public QuikProviderSecuritiesDataController() {
        super();
        this.init(10, 500);
    }

    @Override
    protected String outcomeTopicName() {
        return QuikProviderChannelsNames.Streams.SECURITIES;
    }

    @SneakyThrows(NotAllowedObjectUpdateException.class)
    @Override
    protected QuikSessionedSecuritiesPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException {
        final var originalPackage = this.getConnector().getSecuritiesPackage(lastIndex, packageSize);
        final var result = new QuikSessionedSecuritiesPackage();
        this.converter.fillDtoCollectionFromSource(result.getObjects(), originalPackage.getObjects());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected synchronized void proceedPackage(QuikSessionedSecuritiesPackage standardPackage) throws Exception {
        super.proceedPackage(standardPackage);

        // Дополнительно публикуем в Redis Snapshot
        final var started = System.currentTimeMillis();
        if (standardPackage.size() > 0) {
            final var descriptor = (RedisOutcomeCollectionLoadingDescriptor<QuikSecurity, QuikSecuritiesPackage>)this.redisConfiguration.get(QuikProviderChannelsNames.Snapshots.SECURITIES);
            final var map = new HashMap<String, QuikSecurity>();
            for (var source : standardPackage.getObjects()) {
                final var security = new QuikSecurity(
                        source.getRowIndex(),
                        LocalDate.now(),
                        source.getCode(),
                        source.getName(),
                        source.getShortName(),
                        source.getClassCode(),
                        source.getClassName(),
                        source.getFaceValue(),
                        source.getFaceUnit(),
                        source.getScale(),
                        source.getMaturityDate(),
                        source.getLotSize(),
                        source.getIsinCode(),
                        source.getMinPriceStep()
                );
                final var key = security.getId();
                map.put(key, security);
            }
            this.redisUploader.uploadObjectsWithKeys(descriptor, map);
        }
        log.info("Uploaded into redis {} securities in {} ms", standardPackage.size(), System.currentTimeMillis() - started);
    }
}
