package ru.gx.fin.gate.quik.converters;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import ru.gx.core.data.AbstractDtoFromDtoConverter;
import ru.gx.core.data.NotAllowedObjectUpdateException;
import ru.gx.core.utils.BigDecimalUtils;
import ru.gx.core.utils.StringUtils;
import ru.gx.fin.gate.quik.model.original.OriginalQuikOrder;
import ru.gx.fin.gate.quik.provider.out.QuikDealDirection;
import ru.gx.fin.gate.quik.provider.out.QuikOrder;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class QuikOrderFromOriginalQuikOrderConverter extends AbstractDtoFromDtoConverter<QuikOrder, OriginalQuikOrder> {
    @Override
    @Nullable
    public QuikOrder findDtoBySource(@Nullable final OriginalQuikOrder source) {
        return null;
    }

    @Override
    @NotNull
    public QuikOrder createDtoBySource(@NotNull final OriginalQuikOrder source) {
        return new QuikOrder(
                source.getRowIndex(),
                source.getExchangeCode(),
                source.getOrderNum(),
                source.getFlags() == 0 ? QuikDealDirection.S : QuikDealDirection.B,
                source.getBrokerRef(),
                StringUtils.nullIf(source.getUserId(), ""),
                StringUtils.nullIf(source.getFirmId(), ""),
                StringUtils.nullIf(source.getAccount(), ""),
                source.getPrice(),
                source.getQuantity(),
                BigDecimalUtils.nullIf(source.getBalance(), BigDecimal.ZERO),
                source.getValue(),
                BigDecimalUtils.nullIf(source.getAccruedInterest(), BigDecimal.ZERO),
                BigDecimalUtils.nullIf(source.getYield(), BigDecimal.ZERO),
                source.getTransactionId(),
                StringUtils.nullIf(source.getClientCode(), ""),
                BigDecimalUtils.nullIf(source.getPrice2(), BigDecimal.ZERO),
                source.getSettleCode(),
                source.getUid(),
                source.getCanceledUid(),
                source.getActivationTime(),
                source.getLinkedOrder(),
                source.getExpiry(),
                source.getSecCode(),
                source.getClassCode(),
                source.getTradeDateTime(),
                source.getWithdrawDateTime(),
                StringUtils.nullIf(source.getBankAccountId(), ""),
                source.getValueEntryType(),
                source.getRepoTerm(),
                BigDecimalUtils.nullIf(source.getRepoValue(), BigDecimal.ZERO),
                BigDecimalUtils.nullIf(source.getRepo2Value(), BigDecimal.ZERO),
                BigDecimalUtils.nullIf(source.getRepoValueBalance(), BigDecimal.ZERO),
                source.getStartDiscount(),
                StringUtils.nullIf(source.getRejectReason(), ""),
                source.getExtOrderFlags(),
                BigDecimalUtils.nullIf(source.getMinQuantity(), BigDecimal.ZERO),
                source.getExecType(),
                source.getSideQualifier(),
                source.getAccountType(),
                BigDecimalUtils.nullIf(source.getCapacity(), BigDecimal.ZERO),
                source.getPassiveOnlyOrder(),
                source.getVisible()
        );
    }

    @Override
    public boolean isDestinationUpdatable(@NotNull final QuikOrder destination) {
        return false;
    }

    @Override
    public void updateDtoBySource(@NotNull final QuikOrder destination, @NotNull final OriginalQuikOrder source) throws NotAllowedObjectUpdateException {
        throw new NotAllowedObjectUpdateException(QuikOrder.class, null);
    }
}