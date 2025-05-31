package com.phonepe.sdk.pg.common.models.request.instruments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_NULL)
public class PhoneNumberCollectPaymentDetails extends CollectPaymentDetails {

    private String phoneNumber;

    @Builder
    public PhoneNumberCollectPaymentDetails(String phoneNumber) {
        super(CollectPaymentDetailsType.PHONE_NUMBER);
        this.phoneNumber = phoneNumber;
    }
}
