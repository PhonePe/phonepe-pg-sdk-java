package com.phonepe.sdk.pg.payments.v2.models.request;

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
public class PgCheckoutPaymentFlow extends PaymentFlow {

    private String message;
    private MerchantUrls merchantUrls;
    private PaymentModeConfig paymentModeConfig;

    @Builder
    public PgCheckoutPaymentFlow(final String message, final MerchantUrls merchantUrls,
            final PaymentModeConfig paymentModeConfig) {
        super(PaymentFlowType.PG_CHECKOUT);
        this.message = message;
        this.merchantUrls = merchantUrls;
        this.paymentModeConfig = paymentModeConfig;
    }

}
