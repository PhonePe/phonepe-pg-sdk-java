package com.phonepe.sdk.pg.subscription.v2.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.phonepe.sdk.pg.common.models.PaymentFlowType;
import com.phonepe.sdk.pg.common.models.request.PaymentFlow;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_NULL)
public class SubscriptionRedemptionPaymentFlow extends PaymentFlow {

    private String merchantSubscriptionId;
    private RedemptionRetryStrategy redemptionRetryStrategy;
    private boolean autoDebit;

    @Builder
    public SubscriptionRedemptionPaymentFlow(String merchantSubscriptionId,
            RedemptionRetryStrategy redemptionRetryStrategy, boolean autoDebit) {
        super(PaymentFlowType.SUBSCRIPTION_REDEMPTION);
        this.merchantSubscriptionId = merchantSubscriptionId;
        this.redemptionRetryStrategy = redemptionRetryStrategy;
        this.autoDebit = autoDebit;
    }
}
