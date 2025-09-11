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
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.constants.Headers;
import com.phonepe.sdk.pg.common.tokenhandler.OAuthResponse;
import com.phonepe.sdk.pg.payments.v2.StandardCheckoutClient;
import com.phonepe.sdk.pg.payments.v2.models.request.StandardCheckoutPayRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.StandardCheckoutPayResponse;
import com.phonepe.sdk.pg.payments.v2.standardcheckout.StandardCheckoutConstants;
import java.util.Map;
import okhttp3.FormBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

public class SingletonStandardCheckoutTest extends BaseSetup {

    FormBody formBody =
            new FormBody.Builder()
                    .add("client_id", clientId)
                    .add("client_secret", clientSecret)
                    .add("grant_type", "client_credentials")
                    .add("client_version", String.valueOf(clientVersion))
                    .build();

    @Test
    void testSingletonViaGetInstance() {
        StandardCheckoutClient standardCheckoutClient1 =
                StandardCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);
        StandardCheckoutClient standardCheckoutClient2 =
                StandardCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);

        Assertions.assertEquals(standardCheckoutClient1, standardCheckoutClient2);
    }

    @Test
    void testSingletonWithDiffParameters() {
        StandardCheckoutClient standardCheckoutClient1 =
                StandardCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);
        StandardCheckoutClient standardCheckoutClient2 =
                StandardCheckoutClient.getInstance("clientId2", "clientSecret2", 1, Env.TEST);
        Assertions.assertNotEquals(standardCheckoutClient1, standardCheckoutClient2);
        Assertions.assertNotNull(standardCheckoutClient1);
        Assertions.assertNotNull(standardCheckoutClient2);
    }

    @Test
    void testMultipleSameClientSingleAuthCall() {
        wireMockServer.resetRequests();
        standardCheckoutClient.getTokenService().setOAuthResponse(null);
        String redirectUrl = "https://redirectUrl.com";
        StandardCheckoutPayRequest standardCheckoutPayRequest =
                StandardCheckoutPayRequest.builder()
                        .merchantOrderId("merchantOrderId")
                        .amount(100)
                        .redirectUrl(redirectUrl)
                        .build();

        StandardCheckoutPayResponse standardCheckoutResponse =
                StandardCheckoutPayResponse.builder()
                        .orderId(String.valueOf(java.time.Instant.now().getEpochSecond()))
                        .state("PENDING")
                        .expireAt(java.time.Instant.now().getEpochSecond())
                        .redirectUrl("https://google.com")
                        .build();

        long currentTime = java.time.Instant.now().getEpochSecond();
        OAuthResponse oAuthResponse =
                OAuthResponse.builder()
                        .accessToken("accessToken")
                        .encryptedAccessToken("encryptedAccessToken")
                        .expiresAt(currentTime + 200)
                        .expiresIn(453543)
                        .issuedAt(currentTime)
                        .refreshToken("refreshToken")
                        .tokenType("O-Bearer")
                        .sessionExpiresAt(currentTime + 200)
                        .build();

        StandardCheckoutClient standardCheckoutClient1 =
                StandardCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);
        StandardCheckoutClient standardCheckoutClient2 =
                StandardCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);
        StandardCheckoutClient standardCheckoutClient3 =
                StandardCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);
        StandardCheckoutClient standardCheckoutClient4 =
                StandardCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);

        final String url = StandardCheckoutConstants.PAY_API;
        addStubForPostRequest(
                url,
                getHeaders(),
                standardCheckoutPayRequest,
                HttpStatus.SC_OK,
                Maps.newHashMap(),
                standardCheckoutResponse);
        addStubForFormDataPostRequest(
                authUrl,
                getAuthHeaders(),
                formBody,
                HttpStatus.SC_OK,
                Maps.newHashMap(),
                oAuthResponse);
        StandardCheckoutPayResponse actual =
                standardCheckoutClient1.pay(standardCheckoutPayRequest);
        actual = standardCheckoutClient2.pay(standardCheckoutPayRequest);
        actual = standardCheckoutClient3.pay(standardCheckoutPayRequest);
        actual = standardCheckoutClient4.pay(standardCheckoutPayRequest);

        wireMockServer.verify(1, postRequestedFor(urlPathMatching(authUrl)));
    }

    @Test
    void testMultipleDifferentClientSingleAuthCall() {
        String clientId = "clientId_for_auth1";
        String clientSecret = "clientSecret_for_auth1";
        String clientId1 = "clientId_for_auth2";
        String clientSecret1 = "clientSecret_for_auth2";
        int clientVersion = 1;

        FormBody mockFormBody =
                new FormBody.Builder()
                        .add("client_id", clientId)
                        .add("client_secret", clientSecret)
                        .add("grant_type", "client_credentials")
                        .add("client_version", String.valueOf(clientVersion))
                        .build();

        FormBody mockFormBody1 =
                new FormBody.Builder()
                        .add("client_id", clientId1)
                        .add("client_secret", clientSecret1)
                        .add("grant_type", "client_credentials")
                        .add("client_version", String.valueOf(clientVersion))
                        .build();

        wireMockServer.resetRequests();
        String redirectUrl = "https://redirectUrl.com";
        StandardCheckoutPayRequest standardCheckoutPayRequest =
                StandardCheckoutPayRequest.builder()
                        .merchantOrderId("merchantOrderId")
                        .amount(100)
                        .redirectUrl(redirectUrl)
                        .build();

        StandardCheckoutPayResponse standardCheckoutResponse =
                StandardCheckoutPayResponse.builder()
                        .orderId(String.valueOf(java.time.Instant.now().getEpochSecond()))
                        .state("PENDING")
                        .expireAt(java.time.Instant.now().getEpochSecond())
                        .redirectUrl("https://google.com")
                        .build();

        long currentTime = java.time.Instant.now().getEpochSecond();
        OAuthResponse oAuthResponse =
                OAuthResponse.builder()
                        .accessToken("accessToken")
                        .encryptedAccessToken("encryptedAccessToken")
                        .expiresAt(currentTime + 200)
                        .expiresIn(453543)
                        .issuedAt(currentTime)
                        .refreshToken("refreshToken")
                        .tokenType("O-Bearer")
                        .sessionExpiresAt(currentTime + 200)
                        .build();

        StandardCheckoutClient standardCheckoutClient1 =
                StandardCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);
        StandardCheckoutClient standardCheckoutClient2 =
                StandardCheckoutClient.getInstance(clientId1, clientSecret1, clientVersion, env);

        final String url = StandardCheckoutConstants.PAY_API;
        addStubForPostRequest(
                url,
                getHeaders(),
                standardCheckoutPayRequest,
                HttpStatus.SC_OK,
                Maps.newHashMap(),
                standardCheckoutResponse);
        addStubForFormDataPostRequest(
                authUrl,
                getAuthHeaders(),
                mockFormBody,
                HttpStatus.SC_OK,
                Maps.newHashMap(),
                oAuthResponse);
        addStubForFormDataPostRequest(
                authUrl,
                getAuthHeaders(),
                mockFormBody1,
                HttpStatus.SC_OK,
                Maps.newHashMap(),
                oAuthResponse);
        standardCheckoutClient1.pay(standardCheckoutPayRequest);
        standardCheckoutClient2.pay(standardCheckoutPayRequest);

        wireMockServer.verify(2, postRequestedFor(urlPathMatching(authUrl)));
    }

    public Map<String, String> getHeaders() {
        return ImmutableMap.<String, String>builder()
                .put(Headers.CONTENT_TYPE, APPLICATION_JSON)
                .put(Headers.SOURCE, Headers.INTEGRATION)
                .put(Headers.SOURCE_VERSION, Headers.API_VERSION)
                .put(Headers.SOURCE_PLATFORM, Headers.SDK_TYPE)
                .put(Headers.SOURCE_PLATFORM_VERSION, Headers.SDK_VERSION)
                .put(Headers.OAUTH_AUTHORIZATION, "O-Bearer accessToken")
                .build();
    }

    @Test
    void testSingletonWithDifferentEnvironments() {
        StandardCheckoutClient standardCheckoutClientProd =
                StandardCheckoutClient.getInstance(clientId, clientSecret, clientVersion, Env.PRODUCTION);
        StandardCheckoutClient standardCheckoutClientSandbox =
                StandardCheckoutClient.getInstance(clientId, clientSecret, clientVersion, Env.SANDBOX);

        Assertions.assertNotEquals(standardCheckoutClientProd, standardCheckoutClientSandbox);

        StandardCheckoutClient standardCheckoutClientProd2 =
                StandardCheckoutClient.getInstance(clientId, clientSecret, clientVersion, Env.PRODUCTION);
        Assertions.assertEquals(standardCheckoutClientProd, standardCheckoutClientProd2);

        StandardCheckoutClient standardCheckoutClientSandbox2 =
                StandardCheckoutClient.getInstance(clientId, clientSecret, clientVersion, Env.SANDBOX);
        Assertions.assertEquals(standardCheckoutClientSandbox, standardCheckoutClientSandbox2);
    }
}
