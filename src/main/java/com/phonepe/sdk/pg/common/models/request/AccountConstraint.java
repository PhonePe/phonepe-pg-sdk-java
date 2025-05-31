package com.phonepe.sdk.pg.common.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_NULL)
public class AccountConstraint extends InstrumentConstraint {

    private String accountNumber;
    private String ifsc;

    @Builder
    public AccountConstraint(String accountNumber, String ifsc) {
        super(PaymentInstrumentType.ACCOUNT);
        this.accountNumber = accountNumber;
        this.ifsc = ifsc;
    }
}
