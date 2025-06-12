package com.phonepe.sdk.pg.payments.v2.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class MerchantUrls {

    private String redirectUrl;

}
