package com.phonepe.sdk.pg.common.models.response.paymentinstruments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditCardPaymentInstrumentV2 extends PaymentInstrumentV2 {

    private String bankTransactionId;
    private String bankId;
    private String brn;
    private String arn;

    public CreditCardPaymentInstrumentV2() {
        super(PaymentInstrumentType.CREDIT_CARD);
    }


    @Builder
    public CreditCardPaymentInstrumentV2(String bankTransactionId, String bankId, String brn, String arn) {
        super(PaymentInstrumentType.CREDIT_CARD);
        this.bankTransactionId = bankTransactionId;
        this.bankId = bankId;
        this.brn = brn;
        this.arn = arn;
    }
}
