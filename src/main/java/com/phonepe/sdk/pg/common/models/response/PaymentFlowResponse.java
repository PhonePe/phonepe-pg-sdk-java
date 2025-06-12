package com.phonepe.sdk.pg.common.models.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.phonepe.sdk.pg.common.models.PaymentFlowType;
import com.phonepe.sdk.pg.subscription.v2.models.response.SubscriptionRedemptionPaymentFlowResponse;
import com.phonepe.sdk.pg.subscription.v2.models.response.SubscriptionSetupPaymentFlowResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "SUBSCRIPTION_SETUP", value = SubscriptionSetupPaymentFlowResponse.class),
        @JsonSubTypes.Type(name = "SUBSCRIPTION_REDEMPTION", value =
                SubscriptionRedemptionPaymentFlowResponse.class),
})
@NoArgsConstructor
@Data
public class PaymentFlowResponse {

    private PaymentFlowType type;

    public PaymentFlowResponse(PaymentFlowType type) {
        this.type = type;
    }
}
