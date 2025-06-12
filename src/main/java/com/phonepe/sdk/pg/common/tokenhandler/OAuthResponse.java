package com.phonepe.sdk.pg.common.tokenhandler;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class OAuthResponse {

    @JsonAlias("access_token")
    private String accessToken;

    @JsonAlias("encrypted_access_token")
    private String encryptedAccessToken;

    @JsonAlias("refresh_token")
    private String refreshToken;

    @JsonAlias("expires_in")
    private int expiresIn;

    @JsonAlias("issued_at")
    private long issuedAt;

    @JsonAlias("expires_at")
    private long expiresAt;

    @JsonAlias("session_expires_at")
    private long sessionExpiresAt;

    @JsonAlias("token_type")
    private String tokenType;
}
