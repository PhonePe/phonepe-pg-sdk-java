/*
 *  Copyright (c) 2025 Original Author(s), PhonePe India Pvt. Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
