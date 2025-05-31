package com.phonepe.sdk.pg.subscription.v2.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.phonepe.sdk.pg.common.models.PaymentFlowType;
import com.phonepe.sdk.pg.common.models.response.PaymentFlowResponse;
import com.phonepe.sdk.pg.subscription.v2.models.request.AmountType;
import com.phonepe.sdk.pg.subscription.v2.models.request.AuthWorkflowType;
import com.phonepe.sdk.pg.subscription.v2.models.request.Frequency;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionSetupPaymentFlowResponse extends PaymentFlowResponse {

    private String merchantSubscriptionId;
    private AuthWorkflowType authWorkflowType;
    private AmountType amountType;
    private Long maxAmount;
    private Frequency frequency;
    private Long expireAt;
    private String subscriptionId;

    public SubscriptionSetupPaymentFlowResponse() {
        super(PaymentFlowType.SUBSCRIPTION_SETUP);
    }

    @Builder
    public SubscriptionSetupPaymentFlowResponse(String merchantSubscriptionId, AuthWorkflowType authWorkflowType,
            AmountType amountType, Long maxAmount, Frequency frequency, Long expireAt, String subscriptionId) {
        super(PaymentFlowType.SUBSCRIPTION_SETUP);
        this.merchantSubscriptionId = merchantSubscriptionId;
        this.authWorkflowType = authWorkflowType;
        this.amountType = amountType;
        this.maxAmount = maxAmount;
        this.frequency = frequency;
        this.expireAt = expireAt;
        this.subscriptionId = subscriptionId;
    }
}
