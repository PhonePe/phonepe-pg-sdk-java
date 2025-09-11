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
import com.google.common.collect.Maps;
import com.phonepe.sdk.pg.common.tokenhandler.OAuthResponse;
import com.phonepe.sdk.pg.common.tokenhandler.TokenConstants;
import java.util.Map;
import okhttp3.FormBody;
import org.junit.jupiter.api.BeforeEach;
import wiremock.org.apache.http.HttpStatus;

// Cannot mock TokenService as it is getting created inside the StandardCheckoutClient
class BaseSetupWithOAuth extends BaseSetup {

    OAuthResponse oAuthResponse;

    FormBody formBody =
            new FormBody.Builder()
                    .add("client_id", clientId)
                    .add("client_secret", clientSecret)
                    .add("grant_type", "client_credentials")
                    .add("client_version", String.valueOf(clientVersion))
                    .build();

    @BeforeEach
    void oauthsetup() {
        oAuthResponse =
                OAuthResponse.builder()
                        .accessToken("accessToken")
                        .encryptedAccessToken("encryptedAccessToken")
                        .expiresAt(java.time.Instant.now().getEpochSecond() + 500)
                        .expiresIn(2432)
                        .issuedAt(java.time.Instant.now().getEpochSecond())
                        .refreshToken("refreshToken")
                        .tokenType("O-Bearer")
                        .sessionExpiresAt(234543534)
                        .build();

        Map<String, String> authHeaders = getAuthHeaders();
        final String authUrl = TokenConstants.OAUTH_GET_TOKEN;
        addStubForFormDataPostRequest(
                authUrl, authHeaders, formBody, HttpStatus.SC_OK, Maps.newHashMap(), oAuthResponse);
    }
}
