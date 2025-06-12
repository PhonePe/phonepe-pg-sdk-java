package com.phonepe.sdk.pg.common.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class HttpHeaderPair {

    private String key;
    private String value;
}
