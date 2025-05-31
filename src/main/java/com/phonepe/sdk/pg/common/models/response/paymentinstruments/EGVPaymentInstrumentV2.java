package com.phonepe.sdk.pg.common.models.response.paymentinstruments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EGVPaymentInstrumentV2 extends PaymentInstrumentV2 {

    private String cardNumber;
    private String programId;

    public EGVPaymentInstrumentV2() {
        super(PaymentInstrumentType.EGV);
    }


    @Builder
    public EGVPaymentInstrumentV2(String cardNumber, String programId) {
        super(PaymentInstrumentType.EGV);
        this.cardNumber = cardNumber;
        this.programId = programId;
    }

}
