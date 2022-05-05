package ru.gx.fin.gate.quik.converters;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import ru.gx.core.data.AbstractDtoFromDtoConverter;
import ru.gx.core.data.NotAllowedObjectUpdateException;
import ru.gx.core.utils.BigDecimalUtils;
import ru.gx.core.utils.StringUtils;
import ru.gx.fin.gate.quik.model.original.OriginalQuikDeal;
import ru.gx.fin.gate.quik.provider.out.QuikDeal;
import ru.gx.fin.gate.quik.provider.out.QuikDealDirection;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class QuikDealFromOriginalQuikDealConverter extends AbstractDtoFromDtoConverter<QuikDeal, OriginalQuikDeal> {
    @Override
    @Nullable
    public QuikDeal findDtoBySource(@Nullable final OriginalQuikDeal source) {
        return null;
    }

    @Override
    @NotNull
    public QuikDeal createDtoBySource(@NotNull final OriginalQuikDeal source) {
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
    public boolean isDestinationUpdatable(@NotNull final QuikDeal destination) {
        return false;
    }

    @Override
    public void updateDtoBySource(@NotNull final QuikDeal destination, @NotNull final OriginalQuikDeal source) throws NotAllowedObjectUpdateException {
        throw new NotAllowedObjectUpdateException(QuikDeal.class, null);
    }
}

