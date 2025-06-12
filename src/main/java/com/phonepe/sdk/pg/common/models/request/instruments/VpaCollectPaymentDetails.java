package com.phonepe.sdk.pg.common.models.request.instruments;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_NULL)
public class VpaCollectPaymentDetails extends CollectPaymentDetails {

    private String vpa;

    @Builder
    public VpaCollectPaymentDetails(String vpa) {
        super(CollectPaymentDetailsType.VPA);
        this.vpa = vpa;
    }
}
