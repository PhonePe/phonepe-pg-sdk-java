package com.phonepe.sdk.pg.subscription.v2.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.phonepe.sdk.pg.common.models.PaymentFlowType;
import com.phonepe.sdk.pg.common.models.request.PaymentFlow;
import com.phonepe.sdk.pg.common.models.request.instruments.PaymentV2Instrument;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_NULL)
public class SubscriptionSetupPaymentFlow extends PaymentFlow {

    private String merchantSubscriptionId;
    private AuthWorkflowType authWorkflowType;
    private AmountType amountType;
    private Long maxAmount;
    private Frequency frequency;
    private Long expireAt;
    private PaymentV2Instrument paymentMode;
    
    @Builder
    public SubscriptionSetupPaymentFlow(String merchantSubscriptionId,
            AuthWorkflowType authWorkflowType, AmountType amountType, Long maxAmount, Frequency frequency,
            Long expireAt, PaymentV2Instrument paymentMode) {
        super(PaymentFlowType.SUBSCRIPTION_SETUP);
        this.merchantSubscriptionId = merchantSubscriptionId;
        this.authWorkflowType = authWorkflowType;
        this.amountType = amountType;
        this.maxAmount = maxAmount;
        this.frequency = frequency;
        this.expireAt = expireAt;
        this.paymentMode = paymentMode;
    }
}
