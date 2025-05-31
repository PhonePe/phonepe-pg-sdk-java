package com.phonepe.sdk.pg.common.models.request.instruments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Data;

@JsonInclude(Include.NON_NULL)
@Data
@Builder
public class Expiry {

    private String month;
    private String year;
}
