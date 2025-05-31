package com.phonepe.sdk.pg.common.models.response.paymentinstruments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountPaymentInstrumentV2 extends PaymentInstrumentV2 {

    private String maskedAccountNumber;
    private String ifsc;
    private String accountHolderName;
    private String accountType;

    public AccountPaymentInstrumentV2() {
        super(PaymentInstrumentType.ACCOUNT);
    }


    @Builder
    public AccountPaymentInstrumentV2(String maskedAccountNumber, String ifsc, String accountHolderName,
            String accountType) {
        super(PaymentInstrumentType.ACCOUNT);
        this.maskedAccountNumber = maskedAccountNumber;
        this.ifsc = ifsc;
        this.accountHolderName = accountHolderName;
        this.accountType = accountType;
    }
}
