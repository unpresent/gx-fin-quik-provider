package ru.gx.fin.gate.quik.converters;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.gx.data.AbstractDtoFromDtoConverter;
import ru.gx.data.NotAllowedObjectUpdateException;
import ru.gx.fin.gate.quik.errors.ProviderException;
import ru.gx.fin.gate.quik.model.original.OriginalQuikSecurity;
import ru.gx.fin.gate.quik.provider.out.QuikOrder;
import ru.gx.fin.gate.quik.provider.out.QuikSecurity;
import ru.gx.utils.BigDecimalUtils;
import ru.gx.utils.StringUtils;

import java.math.BigDecimal;

public class QuikSecurityFromOriginalQuikSecurityConverter extends AbstractDtoFromDtoConverter<QuikSecurity, OriginalQuikSecurity> {
    @Override
    public QuikSecurity findDtoBySource(@NotNull OriginalQuikSecurity source) {
        return null;
    }

    @Override
    public QuikSecurity createDtoBySource(@NotNull OriginalQuikSecurity source) {
        return new QuikSecurity(
                source.getRowIndex(),
                source.getCode(),
                StringUtils.nullIf(source.getName(), ""),
                StringUtils.nullIf(source.getShortName(), ""),
                source.getClassCode(),
                StringUtils.nullIf(source.getClassName(), ""),
                BigDecimalUtils.nullIf(source.getFaceValue(), BigDecimal.ZERO),
                StringUtils.nullIf(source.getFaceUnit(), ""),
                source.getScale(),
                source.getMaturityDate(),
                source.getLotSize(),
                StringUtils.nullIf(source.getIsinCode(), ""),
                source.getMinPriceStep()
        );
    }

    @Override
    public boolean isDestinationUpdatable(@NotNull final QuikSecurity destination) {
        return false;
    }

    @Override
    public void updateDtoBySource(@NotNull final QuikSecurity destination, @NotNull final OriginalQuikSecurity source) throws NotAllowedObjectUpdateException {
        throw new NotAllowedObjectUpdateException(QuikOrder.class, null);
    }
}
