package com.phonepe.sdk.pg.payments.v2.models.request;

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
public class PgPaymentFlow extends PaymentFlow {

    private PaymentV2Instrument paymentMode;
    private MerchantUrls merchantUrls;

    @Builder
    public PgPaymentFlow(final PaymentV2Instrument paymentMode, final MerchantUrls merchantUrls) {
        super(PaymentFlowType.PG);
        this.paymentMode = paymentMode;
        this.merchantUrls = merchantUrls;
    }
}
