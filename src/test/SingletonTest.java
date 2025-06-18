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
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.Maps;
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.exception.PhonePeException;
import com.phonepe.sdk.pg.common.tokenhandler.OAuthResponse;
import com.phonepe.sdk.pg.payments.v2.StandardCheckoutClient;
import com.phonepe.sdk.pg.payments.v2.models.request.StandardCheckoutPayRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.StandardCheckoutPayResponse;
import com.phonepe.sdk.pg.payments.v2.standardcheckout.StandardCheckoutConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

public class SingletonTest extends BaseSetupWithOAuth {

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
        PhonePeException phonePeException =
                assertThrows(
                        PhonePeException.class,
                        () ->
                                StandardCheckoutClient.getInstance(
                                        "clientId2", "clientSecret2", 1, Env.TEST));
        Assertions.assertEquals(
                phonePeException.getMessage(),
                "Cannot re-initialize StandardCheckoutClient. Please utilize the existing Client"
                        + " object with required credentials");
    }

    @Test
    void testMultipleClientSingleAuthCall() {
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
}
