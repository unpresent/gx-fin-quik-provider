package ru.gx.fin.gate.quik.converters;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.data.AbstractDtoFromDtoConverter;
import ru.gx.core.data.NotAllowedObjectUpdateException;
import ru.gx.core.utils.BigDecimalUtils;
import ru.gx.core.utils.StringUtils;
import ru.gx.fin.gate.quik.model.original.OriginalQuikAllTrade;
import ru.gx.fin.gate.quik.provider.out.QuikAllTrade;
import ru.gx.fin.gate.quik.provider.out.QuikDealDirection;

import java.math.BigDecimal;

public class QuikAllTradeFromOriginalQuikAllTradeConverter extends AbstractDtoFromDtoConverter<QuikAllTrade, OriginalQuikAllTrade> {
    @Override
    @Nullable
    public QuikAllTrade findDtoBySource(@Nullable final OriginalQuikAllTrade source) {
        return null;
    }

    @Override
    @NotNull
    public QuikAllTrade createDtoBySource(@NotNull final OriginalQuikAllTrade source) {
        return new QuikAllTrade(
                source.getRowIndex(),
                source.getExchangeCode(),
                source.getTradeNum(),
                source.getFlags() == 0 ? QuikDealDirection.S : QuikDealDirection.B,
                source.getTradeDateTime(),
                source.getClassCode(),
                StringUtils.nullIf(source.getSecCode(), ""),
                source.getPrice(),
                source.getQuantity(),
                source.getValue(),
                source.getAccruedInterest(),
                BigDecimalUtils.nullIf(source.getYield(), BigDecimal.ZERO),
                source.getSettleCode(),
                BigDecimalUtils.nullIf(source.getRepoRate(), BigDecimal.ZERO),
                BigDecimalUtils.nullIf(source.getRepoValue(), BigDecimal.ZERO),
                BigDecimalUtils.nullIf(source.getRepo2Value(), BigDecimal.ZERO),
                source.getRepoTerm(),
                source.getPeriod(),
                source.getOpenInterest()
        );
    }

    @Override
    public boolean isDestinationUpdatable(@NotNull final QuikAllTrade destination) {
        return false;
    }

    @Override
    public void updateDtoBySource(@NotNull final QuikAllTrade destination, @NotNull final OriginalQuikAllTrade source) throws NotAllowedObjectUpdateException {
        throw new NotAllowedObjectUpdateException(QuikAllTrade.class, null);
    }
}
