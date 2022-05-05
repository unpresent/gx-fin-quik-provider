package ru.gx.fin.gate.quik.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import ru.gx.core.redis.upload.AbstractRedisOutcomeCollectionsConfiguration;
import ru.gx.core.redis.upload.RedisOutcomeCollectionUploadingDescriptor;
import ru.gx.fin.gate.quik.provider.channels.QuikProviderSnapshotSecurityDataPublishChannelApiV1;

import javax.annotation.PostConstruct;

@Configuration
public class RedisOutcomeCollectionsConfiguration extends AbstractRedisOutcomeCollectionsConfiguration {
    @NotNull
    private final QuikProviderSnapshotSecurityDataPublishChannelApiV1 securitiesChannelApi;

    public RedisOutcomeCollectionsConfiguration(
            @NotNull final RedisConnectionFactory redisConnectionFactory,
            @NotNull final QuikProviderSnapshotSecurityDataPublishChannelApiV1 securitiesChannelApi
    ) {
        super("redis-outcome-config", redisConnectionFactory);
        this.securitiesChannelApi = securitiesChannelApi;
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
