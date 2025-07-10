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
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;
import com.phonepe.sdk.pg.common.tokenhandler.OAuthResponse;
import com.phonepe.sdk.pg.common.tokenhandler.TokenService;
import com.phonepe.sdk.pg.payments.v2.CustomCheckoutClient;
import com.phonepe.sdk.pg.payments.v2.customcheckout.CustomCheckoutConstants;
import okhttp3.FormBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

class SingletonCustomCheckoutTest extends BaseSetup {

    FormBody formBody =
            new FormBody.Builder()
                    .add("client_id", clientId)
                    .add("client_secret", clientSecret)
                    .add("grant_type", "client_credentials")
                    .add("client_version", String.valueOf(clientVersion))
                    .build();

    @Test
    void testSingletonViaGetInstance() {
        CustomCheckoutClient customCheckoutClient1 =
                CustomCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);
        CustomCheckoutClient customCheckoutClient2 =
                CustomCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);

        Assertions.assertEquals(customCheckoutClient1, customCheckoutClient2);
    }

    @Test
    void testSingletonWithDiffParameters() {
        CustomCheckoutClient customCheckoutClient1 =
                CustomCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);
        PhonePeException phonePeException =
                assertThrows(
                        PhonePeException.class,
                        () ->
                                CustomCheckoutClient.getInstance(
                                        "clientId2", "clientSecret2", 1, Env.TEST));
        Assertions.assertEquals(
                phonePeException.getMessage(),
                "Cannot re-initialize CustomCheckoutClient. Please utilize the existing Client"
                        + " object with required credentials");
    }

    @Test
    void testMultipleClientSingleAuthCall() {
        wireMockServer.resetRequests();
        TokenService.setOAuthResponse(null);
        String redirectUrl = "https://redirectUrl.com";
        PgPaymentRequest pgPaymentRequest =
                PgPaymentRequest.UpiQrRequestBuilder()
                        .merchantOrderId("MerchantOrderId")
                        .amount(100)
                        .build();

        PgPaymentResponse pgPaymentResponse =
                PgPaymentResponse.builder()
                        .orderId(String.valueOf(java.time.Instant.now().getEpochSecond()))
                        .state("PENDING")
                        .expireAt(java.time.Instant.now().getEpochSecond())
                        .qrData("qrData")
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

        CustomCheckoutClient customCheckoutClient1 =
                CustomCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);
        CustomCheckoutClient customCheckoutClient2 =
                CustomCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);
        CustomCheckoutClient customCheckoutClient3 =
                CustomCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);
        CustomCheckoutClient customCheckoutClient4 =
                CustomCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);

        final String url = CustomCheckoutConstants.PAY_API;
        addStubForPostRequest(
                url,
                getHeaders(),
                pgPaymentRequest,
                HttpStatus.SC_OK,
                Maps.newHashMap(),
                pgPaymentResponse);
        addStubForFormDataPostRequest(
                authUrl,
                getAuthHeaders(),
                formBody,
                HttpStatus.SC_OK,
                Maps.newHashMap(),
                oAuthResponse);
        PgPaymentResponse actual = customCheckoutClient1.pay(pgPaymentRequest);
        actual = customCheckoutClient2.pay(pgPaymentRequest);
        actual = customCheckoutClient3.pay(pgPaymentRequest);
        actual = customCheckoutClient4.pay(pgPaymentRequest);

        wireMockServer.verify(1, postRequestedFor(urlPathMatching(authUrl)));
    }
}
