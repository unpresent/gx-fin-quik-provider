package ru.gx.fin.gate.quik.converters;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.gx.data.AbstractDtoFromDtoConverter;
import ru.gx.fin.gate.quik.errors.ProviderException;
import ru.gx.fin.gate.quik.model.original.OriginalQuikDeal;
import ru.gx.fin.gate.quik.provider.out.QuikDeal;
import ru.gx.fin.gate.quik.provider.out.QuikDealDirection;
import ru.gx.utils.BigDecimalUtils;
import ru.gx.utils.StringUtils;

import java.math.BigDecimal;

public class QuikDealFromOriginalQuikDealConverter extends AbstractDtoFromDtoConverter<QuikDeal, OriginalQuikDeal> {
    @Override
    public QuikDeal findDtoBySource(@NotNull OriginalQuikDeal source) {
        return null;
    }

    @Override
    public QuikDeal createDtoBySource(@NotNull OriginalQuikDeal source) {

        return new QuikDeal(
                source.getRowIndex(),
                source.getExchangeCode(),
                source.getTradeNum(),
                source.getOrderNum(),
                source.getBrokerRef(),
                source.getUserId(),
                source.getFirmId(),
                source.getCanceledUid(),
                StringUtils.nullIf(source.getAccount(), ""),
                source.getPrice(),
                source.getQuantity(),
                source.getValue(),
                BigDecimalUtils.nullIf(source.getAccruedInterest(), BigDecimal.ZERO),
                BigDecimalUtils.nullIf(source.getYield(), BigDecimal.ZERO),
                StringUtils.nullIf(source.getSettleCode(), ""),
                StringUtils.nullIf(source.getCpFirmId(), ""),
                source.getFlags() == 0 ? QuikDealDirection.S : QuikDealDirection.B,
                BigDecimalUtils.nullIf(source.getPrice2(), BigDecimal.ZERO),
                BigDecimalUtils.nullIf(source.getRepoRate(), BigDecimal.ZERO),
                StringUtils.nullIf(source.getClientCode(), ""),
                BigDecimalUtils.nullIf(source.getAccrued2(), BigDecimal.ZERO),
                source.getRepoTerm(),
                BigDecimalUtils.nullIf(source.getRepoValue(), BigDecimal.ZERO),
                BigDecimalUtils.nullIf(source.getRepo2Value(), BigDecimal.ZERO),
                BigDecimalUtils.nullIf(source.getStartDiscount(), BigDecimal.ZERO),
                BigDecimalUtils.nullIf(source.getLowerDiscount(), BigDecimal.ZERO),
                BigDecimalUtils.nullIf(source.getUpperDiscount(), BigDecimal.ZERO),
                source.getBlockSecurities(),
                BigDecimalUtils.nullIf(source.getClearingComission(), BigDecimal.ZERO),
                BigDecimalUtils.nullIf(source.getExchangeComission(), BigDecimal.ZERO),
                BigDecimalUtils.nullIf(source.getTechCenterComission(), BigDecimal.ZERO),
                source.getSettleDate(),
                source.getSettleCurrency(),
                source.getTradeCurrency(),
                source.getStationId(),
                StringUtils.nullIf(source.getSecCode(), ""),
                source.getClassCode(),
                source.getTradeDateTime(),
                source.getBankAccountId(),
                source.getBrokerComission(),
                source.getLinkedTrade(),
                source.getPeriod(),
                source.getTransactionId(),
                source.getKind(),
                StringUtils.nullIf(source.getClearingBankAccountId(), ""),
                source.getCanceledDateTime(),
                StringUtils.nullIf(source.getClearingFirmId(), ""),
                StringUtils.nullIf(source.getSystemRef(), ""),
                source.getUid()
        );
    }

    @Override
    public boolean isDestinationUpdatable(@NotNull QuikDeal destination) {
        return false;
    }

    @SneakyThrows(ProviderException.class)
    @Override
    public void updateDtoBySource(@NotNull QuikDeal destination, @NotNull OriginalQuikDeal source) {
        throw new ProviderException("It isn't supported update QuikDeal!");
    }
}

