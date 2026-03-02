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
package customCheckoutTests;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.google.common.collect.ImmutableMap;
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.constants.Headers;
import com.phonepe.sdk.pg.common.tokenhandler.OAuthResponse;
import com.phonepe.sdk.pg.common.tokenhandler.TokenConstants;
import com.phonepe.sdk.pg.payments.v2.CustomCheckoutClient;
import java.util.Map;
import okhttp3.FormBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for CustomCheckout tests. Sets up a WireMock server, stubs the OAuth token
 * endpoint, and instantiates {@link CustomCheckoutClient} ready for use in tests.
 */
abstract class CustomCheckoutBaseSetup {

    protected static final int WIREMOCK_PORT = 30419;

    protected final ObjectMapper mapper = new ObjectMapper();

    private static final WireMockConfiguration wireMockConfiguration =
            new WireMockConfiguration().port(WIREMOCK_PORT);
    protected static final WireMockServer wireMockServer =
            new WireMockServer(wireMockConfiguration);

    protected CustomCheckoutClient customCheckoutClient;

    protected final String clientId = "CLIENTID";
    protected final String clientSecret = "CLIENTSECRET";
    protected final Integer clientVersion = 1;
    protected final Env env = Env.TEST;
    protected final String merchantOrderId = "Merchant_CC_001";
    protected final long amount = 100L;

    private final FormBody oauthFormBody =
            new FormBody.Builder()
                    .add("client_id", clientId)
                    .add("client_secret", clientSecret)
                    .add("grant_type", "client_credentials")
                    .add("client_version", String.valueOf(clientVersion))
                    .build();

    @BeforeEach
    void setUp() {
        wireMockServer.start();
        stubOAuthToken();
        this.customCheckoutClient =
                CustomCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);
    }

    @AfterEach
    void tearDown() {
        Assertions.assertEquals(0, wireMockServer.findAllUnmatchedRequests().size());
        wireMockServer.stop();
    }

    // ── OAuth stub ────────────────────────────────────────────────────────

    private void stubOAuthToken() {
        OAuthResponse oAuthResponse =
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

        String requestBody = formDataToString(oauthFormBody);
        addStubForPostRequest(
                TokenConstants.OAUTH_GET_TOKEN,
                getAuthHeaders(),
                requestBody,
                200,
                ImmutableMap.of(),
                oAuthResponse);
    }

    // ── Header helpers ────────────────────────────────────────────────────

    protected Map<String, String> getHeaders() {
        return ImmutableMap.<String, String>builder()
                .put(Headers.CONTENT_TYPE, APPLICATION_JSON)
                .put(Headers.SOURCE, Headers.INTEGRATION)
                .put(Headers.SOURCE_VERSION, Headers.API_VERSION)
                .put(Headers.SOURCE_PLATFORM, Headers.SDK_TYPE)
                .put(Headers.SOURCE_PLATFORM_VERSION, Headers.SDK_VERSION)
                .put(Headers.OAUTH_AUTHORIZATION, "O-Bearer accessToken")
                .build();
    }

    protected Map<String, String> getAuthHeaders() {
        return ImmutableMap.<String, String>builder()
                .put("Content-Type", "application/x-www-form-urlencoded")
                .put("accept", "application/json")
                .build();
    }

    // ── WireMock stub helpers ─────────────────────────────────────────────

    protected void addStubForGetRequest(
            final String urlPath, final int status, final Object response) {
        addStubForGetRequest(
                urlPath, ImmutableMap.of(), ImmutableMap.of(), status, ImmutableMap.of(), response);
    }

    protected void addStubForGetRequest(
            final String urlPath,
            final Map<String, String> queryParams,
            final Map<String, String> requestHeaders,
            final int status,
            final Map<String, String> responseHeaders,
            final Object response) {
        final MappingBuilder mappingBuilder = WireMock.get(WireMock.urlPathEqualTo(urlPath));
        requestHeaders.forEach(
                (key, value) -> mappingBuilder.withHeader(key, WireMock.containing(value)));
        queryParams.forEach(
                (key, value) -> mappingBuilder.withQueryParam(key, WireMock.equalTo(value)));

        wireMockServer.stubFor(
                mappingBuilder.willReturn(buildResponse(status, responseHeaders, response)));
    }

    protected void addStubForPostRequest(
            final String urlPath, final Object request, final int status, final Object response) {
        addStubForPostRequest(
                urlPath, ImmutableMap.of(), request, status, ImmutableMap.of(), response);
    }

    protected void addStubForPostRequest(
            final String urlPath,
            final Map<String, String> requestHeaders,
            final Object request,
            final int status,
            final Map<String, String> responseHeaders,
            final Object response) {
        try {
            String body =
                    request instanceof String
                            ? (String) request
                            : mapper.writeValueAsString(request);

            final MappingBuilder mappingBuilder =
                    WireMock.post(WireMock.urlPathEqualTo(urlPath))
                            .withRequestBody(WireMock.equalTo(body));

            requestHeaders.forEach(
                    (key, value) -> mappingBuilder.withHeader(key, WireMock.containing(value)));

            wireMockServer.stubFor(
                    mappingBuilder.willReturn(buildResponse(status, responseHeaders, response)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseDefinitionBuilder buildResponse(
            int status, Map<String, String> responseHeaders, Object response) {
        try {
            ResponseDefinitionBuilder builder =
                    WireMock.aResponse()
                            .withStatus(status)
                            .withBody(
                                    response instanceof String
                                            ? (String) response
                                            : mapper.writeValueAsString(response));
            responseHeaders.forEach(builder::withHeader);
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String formDataToString(FormBody formBody) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < formBody.size(); i++) {
            if (i > 0) sb.append("&");
            sb.append(formBody.encodedName(i)).append("=").append(formBody.encodedValue(i));
        }
        return sb.toString();
    }
}
