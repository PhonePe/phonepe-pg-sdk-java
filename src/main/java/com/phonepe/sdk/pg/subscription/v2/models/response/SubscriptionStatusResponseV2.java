package com.phonepe.sdk.pg.subscription.v2.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.phonepe.sdk.pg.subscription.v2.models.request.AmountType;
import com.phonepe.sdk.pg.subscription.v2.models.request.AuthWorkflowType;
import com.phonepe.sdk.pg.subscription.v2.models.request.Frequency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionStatusResponseV2 {

    private String merchantSubscriptionId;
    private String subscriptionId;
    private String state;
    private AuthWorkflowType authWorkflowType;
    private AmountType amountType;
    private Long maxAmount;
    private Frequency frequency;
    private Long expireAt;
    private Long pauseStartDate;
    private Long pauseEndDate;
}
