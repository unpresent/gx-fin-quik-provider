package ru.gx.fin.gate.quik.converters;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.gx.data.AbstractDtoFromDtoConverter;
import ru.gx.data.NotAllowedObjectUpdateException;
import ru.gx.fin.gate.quik.errors.ProviderException;
import ru.gx.fin.gate.quik.model.original.OriginalQuikAllTrade;
import ru.gx.fin.gate.quik.provider.out.QuikAllTrade;
import ru.gx.fin.gate.quik.provider.out.QuikDealDirection;
import ru.gx.utils.BigDecimalUtils;
import ru.gx.utils.StringUtils;

import java.math.BigDecimal;

public class QuikAllTradeFromOriginalQuikAllTradeConverter extends AbstractDtoFromDtoConverter<QuikAllTrade, OriginalQuikAllTrade> {
    @Override
    public QuikAllTrade findDtoBySource(@NotNull final OriginalQuikAllTrade source) {
        return null;
    }

    @Override
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
