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

import java.math.BigDecimal;
import java.time.LocalDate;

public class QuikSecurityFromOriginalQuikSecurityConverter extends AbstractDtoFromDtoConverter<QuikSecurity, OriginalQuikSecurity> {
    @Override
    @Nullable
    public QuikSecurity findDtoBySource(@Nullable final OriginalQuikSecurity source) {
        return null;
    }

    @Override
    @NotNull
    public QuikSecurity createDtoBySource(@NotNull final OriginalQuikSecurity source) {
        return new QuikSecurity(
                source.getRowIndex(),
                LocalDate.now(), // TODO: Возможно ли брать дату последней торговой сессии из Quik*-а ?
                source.getCode(),
                StringUtils.nullIf(source.getName(), ""),
                StringUtils.nullIf(source.getShortName(), ""),
                source.getClassCode(),
                StringUtils.nullIf(source.getClassName(), ""),
                BigDecimalUtils.nullIf(source.getFaceValue(), BigDecimal.ZERO),
                StringUtils.nullIf(source.getFaceUnit(), ""),
                source.getScale(),
                LocalDate.of(1900, 1, 1).plusDays(source.getMaturityDate()), // TODO: Проверить, правильно ли берется "ноль" у quik даты.
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
