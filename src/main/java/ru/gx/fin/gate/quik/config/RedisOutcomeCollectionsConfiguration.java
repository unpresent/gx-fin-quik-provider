package ru.gx.fin.gate.quik.config;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.core.redis.upload.AbstractRedisOutcomeCollectionsConfiguration;
import ru.gx.core.redis.upload.RedisOutcomeCollectionUploadingDescriptor;
import ru.gx.fin.gate.quik.provider.channels.QuikProviderSnapshotSecurityDataPublishChannelApiV1;

import javax.annotation.PostConstruct;

import static lombok.AccessLevel.PROTECTED;

public class RedisOutcomeCollectionsConfiguration extends AbstractRedisOutcomeCollectionsConfiguration {
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikProviderSnapshotSecurityDataPublishChannelApiV1 securitiesChannelApi;

    public RedisOutcomeCollectionsConfiguration(@NotNull String configurationName) {
        super(configurationName);
    }

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init() {
        // this.getDescriptorsDefaults();

        this
                .newDescriptor(this.securitiesChannelApi, RedisOutcomeCollectionUploadingDescriptor.class)
                .init();
    }
}
