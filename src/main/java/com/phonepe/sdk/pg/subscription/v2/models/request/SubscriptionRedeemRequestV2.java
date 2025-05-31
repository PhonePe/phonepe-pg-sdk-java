package com.phonepe.sdk.pg.subscription.v2.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class SubscriptionRedeemRequestV2 {

    private String merchantOrderId;

    public SubscriptionRedeemRequestV2(String merchantOrderId) {
        this.merchantOrderId = merchantOrderId;
    }
}
