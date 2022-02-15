package ru.gx.fin.gate.quik.datacontrollers;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.core.data.DataObjectKeyExtractor;
import ru.gx.core.data.NotAllowedObjectUpdateException;
import ru.gx.core.redis.upload.RedisOutcomeCollectionUploadingDescriptor;
import ru.gx.core.redis.upload.RedisOutcomeCollectionsUploader;
import ru.gx.fin.gate.quik.config.RedisOutcomeCollectionsConfiguration;
import ru.gx.fin.gate.quik.converters.QuikSecurityFromOriginalQuikSecurityConverter;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.fin.gate.quik.provider.config.QuikProviderChannelNames;
import ru.gx.fin.gate.quik.provider.messages.QuikProviderSnapshotSecurityDataPublish;
import ru.gx.fin.gate.quik.provider.out.QuikSecurity;
import ru.gx.fin.gate.quik.provider.out.QuikSessionedSecuritiesPackage;
import ru.gx.fin.gate.quik.provider.out.QuikSessionedSecurity;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import static lombok.AccessLevel.PROTECTED;

/**
 * Контролер чтения поручений
 */
@Slf4j
public class QuikProviderSecuritiesDataController
        extends AbstractQuikProviderDataController<QuikProviderSnapshotSecurityDataPublish, QuikSessionedSecurity, QuikSessionedSecuritiesPackage>
        implements DataObjectKeyExtractor<QuikSecurity> {

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikSecurityFromOriginalQuikSecurityConverter converter;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private RedisOutcomeCollectionsUploader redisUploader;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private RedisOutcomeCollectionsConfiguration redisConfiguration;

    public QuikProviderSecuritiesDataController() {
        super();
        this.init(10, 500);
    }

    @Override
    protected String outcomeTopicName() {
        return QuikProviderChannelNames.Streams.SECURITIES_V1;
    }

    @SneakyThrows(NotAllowedObjectUpdateException.class)
    @Override
    protected QuikSessionedSecuritiesPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException {
        final var originalPackage = this.getConnector().getSecuritiesPackage(lastIndex, packageSize);
        final var result = new QuikSessionedSecuritiesPackage();
        result.allCount = originalPackage.getQuikAllCount();
        this.converter.fillDtoCollectionFromSource(result.getObjects(), originalPackage.getObjects());
        return result;
    }

    @Override
    protected synchronized void proceedPackage(QuikSessionedSecuritiesPackage standardPackage) throws Exception {
        super.proceedPackage(standardPackage);

        // Дополнительно публикуем в Redis Snapshot
        final var started = System.currentTimeMillis();
        if (standardPackage.size() > 0) {
            final var descriptor = (RedisOutcomeCollectionUploadingDescriptor<QuikProviderSnapshotSecurityDataPublish>)
            // final var descriptor =
                    this.redisConfiguration
                            .<QuikProviderSnapshotSecurityDataPublish>get(QuikProviderChannelNames.Snapshots.SECURITIES_V1);

            final var destObjects = new ArrayList<QuikSecurity>();
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
                        source.getCurrencyId(),
                        source.getMinPriceStep()
                );
                destObjects.add(security);
            }

            this.redisUploader.uploadDataObjects(descriptor, destObjects, this);
        }
        log.info("Uploaded into redis {} securities in {} ms", standardPackage.size(), System.currentTimeMillis() - started);
    }

    @Override
    public Object extractKey(@NotNull final QuikSecurity quikSecurity) {
        return quikSecurity.getId();
    }
}
