package com.phonepe.sdk.pg.common.models.response.paymentinstruments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebitCardPaymentInstrumentV2 extends PaymentInstrumentV2 {

    private String bankTransactionId;
    private String bankId;
    private String brn;
    private String arn;

    public DebitCardPaymentInstrumentV2() {
        super(PaymentInstrumentType.DEBIT_CARD);
    }


    @Builder
    public DebitCardPaymentInstrumentV2(String bankTransactionId, String bankId, String brn,
            String arn) {
        super(PaymentInstrumentType.DEBIT_CARD);
        this.bankTransactionId = bankTransactionId;
        this.bankId = bankId;
        this.brn = brn;
        this.arn = arn;
    }
}
