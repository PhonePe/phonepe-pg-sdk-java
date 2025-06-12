package com.phonepe.sdk.pg.common.models.request.instruments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class BillingAddress {

    private String line1;
    private String line2;
    private String city;
    private String state;
    private String zip;
    private String country;
}
