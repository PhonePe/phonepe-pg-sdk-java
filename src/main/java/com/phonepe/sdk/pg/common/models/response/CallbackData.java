package com.phonepe.sdk.pg.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.phonepe.sdk.pg.common.models.MetaInfo;
import com.phonepe.sdk.pg.subscription.v2.models.request.AmountType;
import com.phonepe.sdk.pg.subscription.v2.models.request.AuthWorkflowType;
import com.phonepe.sdk.pg.subscription.v2.models.request.Frequency;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallbackData {

    private String orderId;
    private String merchantId;
    private String merchantRefundId;
    private String originalMerchantOrderId;
    private String refundId;
    private String merchantOrderId;
    private String state;
    private Long amount;
    private Long expireAt;
    private String errorCode;
    private String detailedErrorCode;
    private MetaInfo metaInfo;
    private String merchantSubscriptionId;
    private String subscriptionId;
    private AuthWorkflowType authWorkflowType;
    private AmountType amountType;
    private Long maxAmount;
    private Frequency frequency;
    private Long pauseStartDate;
    private Long pauseEndDate;
    private PaymentFlowResponse paymentFlow;
    private List<PaymentDetail> paymentDetails;
}
