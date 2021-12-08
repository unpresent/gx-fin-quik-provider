package ru.gx.fin.gate.quik.converters;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.data.AbstractDtoFromDtoConverter;
import ru.gx.core.data.NotAllowedObjectUpdateException;
import ru.gx.core.utils.BigDecimalUtils;
import ru.gx.core.utils.StringUtils;
import ru.gx.fin.gate.quik.model.original.OriginalQuikSecurity;
import ru.gx.fin.gate.quik.provider.out.QuikOrder;
import ru.gx.fin.gate.quik.provider.out.QuikSecurity;
import ru.gx.fin.gate.quik.provider.out.QuikSessionedSecurity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class QuikSecurityFromOriginalQuikSecurityConverter extends AbstractDtoFromDtoConverter<QuikSessionedSecurity, OriginalQuikSecurity> {
    @Override
    @Nullable
    public QuikSessionedSecurity findDtoBySource(@Nullable final OriginalQuikSecurity source) {
        return null;
    }

    @Override
    @NotNull
    public QuikSessionedSecurity createDtoBySource(@NotNull final OriginalQuikSecurity source) {
        LocalDate maturityDate = null;
        if (source.getMaturityDate() != 0) {
            final int matY = (int) source.getMaturityDate() / 10000;
            final int matM = (int) source.getMaturityDate() / 100 - matY * 100;
            final int matD = (int) source.getMaturityDate() - matY * 10000 - matM * 100;
            maturityDate = LocalDate.of(matY, matM, matD);
        }
        return new QuikSessionedSecurity(
                source.getRowIndex(),
                source.getSessionId(),
                source.getCode(),
                StringUtils.nullIf(source.getName(), ""),
                StringUtils.nullIf(source.getShortName(), ""),
                source.getClassCode(),
                StringUtils.nullIf(source.getClassName(), ""),
                BigDecimalUtils.nullIf(source.getFaceValue(), BigDecimal.ZERO),
                StringUtils.nullIf(source.getFaceUnit(), ""),
                source.getScale(),
                maturityDate,
                source.getLotSize(),
                StringUtils.nullIf(source.getIsinCode(), ""),
                source.getMinPriceStep()
        );
    }

    @Override
    public boolean isDestinationUpdatable(@NotNull final QuikSessionedSecurity destination) {
        return false;
    }

    @Override
    public void updateDtoBySource(@NotNull final QuikSessionedSecurity destination, @NotNull final OriginalQuikSecurity source) throws NotAllowedObjectUpdateException {
        throw new NotAllowedObjectUpdateException(QuikOrder.class, null);
    }
}
