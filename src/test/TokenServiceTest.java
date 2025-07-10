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
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.phonepe.sdk.pg.common.constants.Headers;
import com.phonepe.sdk.pg.common.exception.PhonePeException;
import com.phonepe.sdk.pg.common.exception.UnauthorizedAccess;
import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;
import com.phonepe.sdk.pg.common.tokenhandler.OAuthResponse;
import com.phonepe.sdk.pg.common.tokenhandler.TokenConstants;
import com.phonepe.sdk.pg.common.tokenhandler.TokenService;
import com.phonepe.sdk.pg.payments.v2.models.request.StandardCheckoutPayRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.StandardCheckoutPayResponse;
import com.phonepe.sdk.pg.payments.v2.standardcheckout.StandardCheckoutConstants;
import java.util.Collections;
import java.util.Map;
import okhttp3.FormBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

class TokenServiceTest extends BaseSetup {

    protected String formDataToString(FormBody formBody) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < formBody.size(); i++) {
            if (i > 0) {
                builder.append("&");
            }
            builder.append(formBody.encodedName(i)).append("=").append(formBody.encodedValue(i));
        }
        return builder.toString();
    }

    FormBody formBody =
            new FormBody.Builder()
                    .add("client_id", clientId)
                    .add("client_secret", clientSecret)
                    .add("grant_type", "client_credentials")
                    .add("client_version", String.valueOf(clientVersion))
                    .build();

    TokenService spyTokenService = spy(tokenService);

    @Test
    void testFailFirstFetch() throws JsonProcessingException, InterruptedException {
        PhonePeResponse phonePeResponse =
                PhonePeResponse.builder()
                        .code("INVALID_CLIENT")
                        .errorCode("errorCode")
                        .message("Bad Request")
                        .data(
                                Collections.singletonMap(
                                        "errorDescription", "Client Authentication Failure"))
                        .build();
        TokenService.setOAuthResponse(null);
        wireMockServer.stubFor(
                post(urlPathMatching(authUrl))
                        .willReturn(
                                aResponse()
                                        .withStatus(400)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                objectMapper.writeValueAsString(phonePeResponse))));

        final PhonePeException phonePeException =
                assertThrows(
                        PhonePeException.class,
                        standardCheckoutClient.getTokenService()::getAuthToken);

        Assertions.assertEquals(400, phonePeException.getHttpStatusCode());
        Assertions.assertEquals("Bad Request", phonePeException.getMessage());
    }

    @Test
    void testOAuthFetchToken() throws JsonProcessingException {
        wireMockServer.stubFor(
                post(urlPathMatching(authUrl))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(objectMapper.writeValueAsString(oAuthResponse))));
        String actual = standardCheckoutClient.getTokenService().getAuthToken();
        Assertions.assertEquals("O-Bearer accessToken", actual);
    }

    @Test
    void testTokenRefresh() {
        TokenService.setOAuthResponse(null);
        OAuthResponse oAuthResponse =
                OAuthResponse.builder()
                        .accessToken("accessToken")
                        .encryptedAccessToken("encryptedAccessToken")
                        .expiresAt(170963)
                        .expiresIn(0)
                        .issuedAt(0)
                        .refreshToken("refreshToken")
                        .tokenType("O-Bearer")
                        .sessionExpiresAt(1709630316)
                        .build();

        doReturn(oAuthResponse).when(spyTokenService).fetchTokenFromPhonePe();
        String setToken = spyTokenService.getAuthToken(); // sets the token
        String secondCall =
                spyTokenService.getAuthToken(); // notices token is invalid and fetched new token
        Assertions.assertEquals("O-Bearer accessToken", secondCall);
        verify(spyTokenService, times(2)).fetchTokenFromPhonePe();
    }

    @Test
    void testTokenUseCached() {
        TokenService.setOAuthResponse(null);
        OAuthResponse oAuthResponse =
                OAuthResponse.builder()
                        .accessToken("accessToken")
                        .encryptedAccessToken("encryptedAccessToken")
                        .expiresAt(2147483647)
                        .expiresIn(2147483647)
                        .issuedAt(1709630316)
                        .refreshToken("refreshToken")
                        .tokenType("O-Bearer")
                        .sessionExpiresAt(2147483647)
                        .build();

        doReturn(oAuthResponse).when(spyTokenService).fetchTokenFromPhonePe();

        String setToken = spyTokenService.getAuthToken();
        String noRefresh =
                spyTokenService.getAuthToken(); // tries to fetch new token but token is valid

        Assertions.assertEquals("O-Bearer accessToken", noRefresh);
        verify(spyTokenService, times(1)).fetchTokenFromPhonePe();
    }

    @Test
    void testTokenCachedUseWithCurrentTime() {
        TokenService.setOAuthResponse(null);
        long currentTime = java.time.Instant.now().getEpochSecond();
        long twoSecMoreCur = currentTime + 2;
        OAuthResponse oAuthResponse =
                OAuthResponse.builder()
                        .accessToken("accessToken")
                        .encryptedAccessToken("encryptedAccessToken")
                        .expiresAt(twoSecMoreCur)
                        .expiresIn(200)
                        .issuedAt(currentTime)
                        .refreshToken("refreshToken")
                        .tokenType("O-Bearer")
                        .sessionExpiresAt(1709630316)
                        .build();

        doReturn(oAuthResponse).when(spyTokenService).fetchTokenFromPhonePe();

        doReturn(currentTime).when(spyTokenService).getCurrentTime();

        String setToken = spyTokenService.getAuthToken(); // sets Token
        setToken = spyTokenService.getAuthToken(); // tries to fetch but token is valid
        setToken = spyTokenService.getAuthToken(); // tries to fetch but token is valid

        Assertions.assertEquals("O-Bearer accessToken", setToken);
        verify(spyTokenService, times(1)).fetchTokenFromPhonePe();

        doReturn(currentTime + 1).when(spyTokenService).getCurrentTime();

        setToken =
                spyTokenService.getAuthToken(); // tries to fetch new token as token is invalid now
        setToken =
                spyTokenService.getAuthToken(); // tries to fetch new token as token is invalid now
        setToken =
                spyTokenService.getAuthToken(); // tries to fetch new token as token is invalid now
        setToken =
                spyTokenService.getAuthToken(); // tries to fetch new token as token is invalid now

        verify(spyTokenService, times(5)).fetchTokenFromPhonePe();
    }

    @Test
    void testUseCachedThenInValidSoFetch() throws InterruptedException, JsonProcessingException {
        TokenService.setOAuthResponse(null);
        long currentTime = java.time.Instant.now().getEpochSecond();
        long twoSecMoreCur = currentTime + 2;
        long fourSecMoreCur = currentTime + 4;
        OAuthResponse oAuthResponse =
                OAuthResponse.builder()
                        .accessToken("accessToken")
                        .encryptedAccessToken("encryptedAccessToken")
                        .expiresAt(twoSecMoreCur)
                        .expiresIn(200)
                        .issuedAt(currentTime)
                        .refreshToken("refreshToken")
                        .tokenType("O-Bearer")
                        .sessionExpiresAt(1709630316)
                        .build();

        doReturn(oAuthResponse).when(spyTokenService).fetchTokenFromPhonePe();
        doReturn(currentTime).when(spyTokenService).getCurrentTime();

        String setToken = spyTokenService.getAuthToken();
        setToken = spyTokenService.getAuthToken();
        setToken = spyTokenService.getAuthToken();
        verify(spyTokenService, times(1)).fetchTokenFromPhonePe();

        oAuthResponse =
                OAuthResponse.builder()
                        .accessToken("accessToken")
                        .encryptedAccessToken("encryptedAccessToken")
                        .expiresAt(fourSecMoreCur)
                        .expiresIn(200)
                        .issuedAt(currentTime)
                        .refreshToken("refreshToken")
                        .tokenType("O-Bearer")
                        .sessionExpiresAt(1709630316)
                        .build();

        doReturn(oAuthResponse).when(spyTokenService).fetchTokenFromPhonePe();
        doReturn(currentTime + 1).when(spyTokenService).getCurrentTime();

        setToken = spyTokenService.getAuthToken(); // fetched new token
        setToken = spyTokenService.getAuthToken(); // uses old token
        setToken = spyTokenService.getAuthToken(); // uses old token

        verify(spyTokenService, times(2)).fetchTokenFromPhonePe();
    }

    @Test
    void testWhenUnauthorizedTokenForPay() {
        wireMockServer.resetRequests();
        TokenService.setOAuthResponse(null);
        final String url = StandardCheckoutConstants.PAY_API;

        StandardCheckoutPayRequest standardCheckoutPayRequest =
                StandardCheckoutPayRequest.builder()
                        .merchantOrderId("merchantOrderId")
                        .amount(1)
                        .build();

        PhonePeResponse phonePeResponse =
                PhonePeResponse.builder()
                        .code("Unauthorized")
                        .message("message")
                        .data(Collections.singletonMap("a", "b"))
                        .build();

        final String authUrl = TokenConstants.OAUTH_GET_TOKEN;

        addStubForFormDataPostRequest(
                authUrl,
                getAuthHeaders(),
                formBody,
                HttpStatus.SC_OK,
                Maps.newHashMap(),
                oAuthResponse);

        addStubForPostRequest(
                url,
                getHeaders(),
                standardCheckoutPayRequest,
                HttpStatus.SC_UNAUTHORIZED,
                Maps.newHashMap(),
                phonePeResponse);

        final UnauthorizedAccess unauthorizedAccess =
                assertThrows(
                        UnauthorizedAccess.class,
                        () -> standardCheckoutClient.pay(standardCheckoutPayRequest));

        Assertions.assertEquals(401, unauthorizedAccess.getHttpStatusCode());
        Assertions.assertEquals("Unauthorized", unauthorizedAccess.getCode());
        Assertions.assertEquals(
                "O-Bearer accessToken",
                standardCheckoutClient.getTokenService().formatCachedToken());

        wireMockServer.verify(exactly(2), postRequestedFor(urlPathMatching(authUrl)));

        StandardCheckoutPayResponse standardCheckoutResponse =
                StandardCheckoutPayResponse.builder()
                        .orderId(String.valueOf(java.time.Instant.now().getEpochSecond()))
                        .state("PENDING")
                        .expireAt(java.time.Instant.now().getEpochSecond())
                        .redirectUrl("https://google.com")
                        .build();

        addStubForPostRequest(
                url,
                getHeaders(),
                standardCheckoutPayRequest,
                HttpStatus.SC_OK,
                Maps.newHashMap(),
                standardCheckoutResponse);

        StandardCheckoutPayResponse actual = standardCheckoutClient.pay(standardCheckoutPayRequest);
        Assertions.assertEquals(standardCheckoutResponse, actual);
        Assertions.assertEquals(
                "O-Bearer accessToken",
                standardCheckoutClient.getTokenService().formatCachedToken());
    }

    @Test
    void testWhenUnauthorizedTokenForOrder() throws JsonProcessingException {
        wireMockServer.resetRequests();

        TokenService.setOAuthResponse(null);
        String url = String.format(StandardCheckoutConstants.ORDER_STATUS_API, merchantOrderId);

        PhonePeResponse phonePeResponse =
                PhonePeResponse.<Map<String, String>>builder()
                        .code("Unauthorized")
                        .message("message")
                        .data(Collections.singletonMap("a", "b"))
                        .build();

        addStubForFormDataPostRequest(
                authUrl,
                getAuthHeaders(),
                formBody,
                HttpStatus.SC_OK,
                Maps.newHashMap(),
                oAuthResponse);

        addStubForGetRequest(
                url,
                ImmutableMap.of(StandardCheckoutConstants.ORDER_DETAILS, "false"),
                getHeaders(),
                HttpStatus.SC_UNAUTHORIZED,
                ImmutableMap.of(),
                phonePeResponse);

        final UnauthorizedAccess unauthorizedAccess =
                assertThrows(
                        UnauthorizedAccess.class,
                        () -> standardCheckoutClient.getOrderStatus(merchantOrderId, false));

        Assertions.assertEquals(401, unauthorizedAccess.getHttpStatusCode());
        Assertions.assertEquals("Unauthorized", unauthorizedAccess.getCode());
        Assertions.assertEquals(
                "O-Bearer accessToken",
                standardCheckoutClient.getTokenService().formatCachedToken());

        wireMockServer.verify(2, postRequestedFor(urlPathMatching(authUrl)));

        OrderStatusResponse orderStatusResponse =
                OrderStatusResponse.builder().orderId("Order123").state("PENDING").build();

        addStubForGetRequest(
                url,
                ImmutableMap.of(StandardCheckoutConstants.ORDER_DETAILS, "false"),
                getHeaders(),
                HttpStatus.SC_OK,
                ImmutableMap.of(),
                orderStatusResponse);

        OrderStatusResponse actual = standardCheckoutClient.getOrderStatus(merchantOrderId, false);
        Assertions.assertEquals(orderStatusResponse, actual);
        Assertions.assertEquals(
                "O-Bearer accessToken",
                standardCheckoutClient.getTokenService().formatCachedToken());
    }

    @Test
    void testFirstFetchWorksSecondFetchFailsSendsBackOldToken() throws JsonProcessingException {
        wireMockServer.resetRequests();
        TokenService.setOAuthResponse(null);
        String requestBody = formDataToString(formBody);
        wireMockServer.stubFor(
                post(urlPathMatching(authUrl))
                        .withRequestBody(equalTo(requestBody))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(objectMapper.writeValueAsString(oAuthResponse))));

        String setToken = standardCheckoutClient.getTokenService().getAuthToken();

        wireMockServer.verify(
                1,
                postRequestedFor(urlPathMatching(authUrl)).withRequestBody(equalTo(requestBody)));

        PhonePeResponse phonePeResponse =
                PhonePeResponse.<Map<String, String>>builder()
                        .code("Service Unavailable")
                        .message("message")
                        .data(Collections.singletonMap("a", "b"))
                        .build();

        wireMockServer.stubFor(
                post(urlPathMatching(authUrl))
                        .willReturn(
                                aResponse()
                                        .withStatus(400)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                objectMapper.writeValueAsString(phonePeResponse))));

        String shouldReceiveOldToken1 = standardCheckoutClient.getTokenService().getAuthToken();
        String shouldReceiveOldToken2 = standardCheckoutClient.getTokenService().getAuthToken();
        String shouldReceiveOldToken3 = standardCheckoutClient.getTokenService().getAuthToken();

        Assertions.assertEquals("O-Bearer accessToken", setToken);
        Assertions.assertEquals("O-Bearer accessToken", shouldReceiveOldToken1);
        Assertions.assertEquals("O-Bearer accessToken", shouldReceiveOldToken2);
        Assertions.assertEquals("O-Bearer accessToken", shouldReceiveOldToken3);
    }

    public Map<String, String> getNewHeaders() {
        return ImmutableMap.<String, String>builder()
                .put(Headers.CONTENT_TYPE, APPLICATION_JSON)
                .put(Headers.SOURCE, Headers.INTEGRATION)
                .put(Headers.SOURCE_VERSION, Headers.API_VERSION)
                .put(Headers.SOURCE_PLATFORM, Headers.SDK_TYPE)
                .put(Headers.SOURCE_PLATFORM_VERSION, Headers.SDK_VERSION)
                .put(Headers.OAUTH_AUTHORIZATION, "O-Bearer accessToken")
                .build();
    }
}
