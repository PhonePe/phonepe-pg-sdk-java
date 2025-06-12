package com.phonepe.sdk.pg.common.configs;


import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CredentialConfig {

    @NotBlank
    private String clientId;

    @NotBlank
    private String clientSecret;

    @NotBlank
    private Integer clientVersion;
}
