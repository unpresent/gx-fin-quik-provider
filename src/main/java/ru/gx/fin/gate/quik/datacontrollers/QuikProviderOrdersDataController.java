package ru.gx.fin.gate.quik.datacontrollers;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.gx.core.data.NotAllowedObjectUpdateException;
import ru.gx.fin.gate.quik.converters.QuikOrderFromOriginalQuikOrderConverter;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.fin.gate.quik.provider.config.QuikProviderChannelNames;
import ru.gx.fin.gate.quik.provider.messages.QuikProviderStreamOrdersPackageDataPublish;
import ru.gx.fin.gate.quik.provider.out.QuikOrder;
import ru.gx.fin.gate.quik.provider.out.QuikOrdersPackage;

import java.io.IOException;

import static lombok.AccessLevel.PROTECTED;

/**
 * Контролер чтения поручений
 */
@Slf4j
@Component
public class QuikProviderOrdersDataController
        extends AbstractQuikProviderDataController<QuikProviderStreamOrdersPackageDataPublish, QuikOrder, QuikOrdersPackage> {

    @Getter(PROTECTED)
    @NotNull
    private final QuikOrderFromOriginalQuikOrderConverter converter;

    public QuikProviderOrdersDataController(@NotNull QuikOrderFromOriginalQuikOrderConverter converter) {
        super();
        this.converter = converter;
        this.init(25, 500);
    }

    @Override
    protected String outcomeTopicName() {
        return QuikProviderChannelNames.Streams.ORDERS_V1;
    }

    @SneakyThrows(NotAllowedObjectUpdateException.class)
    @Override
    protected QuikOrdersPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException {
        final var originalPackage = this.getConnector().getOrdersPackage(lastIndex, packageSize);
        final var result = new QuikOrdersPackage();
        result.allCount = originalPackage.getQuikAllCount();
        this.converter.fillDtoCollectionFromSource(result.getObjects(), originalPackage.getObjects());
        return result;
    }
}
