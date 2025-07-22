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

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.configs.CredentialConfig;
import com.phonepe.sdk.pg.common.constants.Headers;
import com.phonepe.sdk.pg.common.events.models.BaseEvent;
import com.phonepe.sdk.pg.common.events.models.enums.EventType;
import com.phonepe.sdk.pg.common.events.publisher.EventPublisher;
import com.phonepe.sdk.pg.common.http.HttpCommand;
import com.phonepe.sdk.pg.common.http.HttpHeaderPair;
import com.phonepe.sdk.pg.common.http.HttpMethodType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;

@Slf4j
public class TokenService {

    private OkHttpClient okHttpClient;
    private ObjectMapper objectMapper;
    private CredentialConfig credentialConfig;
    private Env env;
    @Setter private OAuthResponse oAuthResponse;
    private EventPublisher eventPublisher;

    public TokenService(
            OkHttpClient okHttpClient,
            ObjectMapper objectMapper,
            CredentialConfig credentialConfig,
            Env env,
            EventPublisher eventPublisher) {
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
        this.credentialConfig = credentialConfig;
        this.env = env;
        this.eventPublisher = eventPublisher;
        this.eventPublisher.send(
                BaseEvent.buildInitClientEvent(EventType.TOKEN_SERVICE_INITIALIZED));
    }

    private List<HttpHeaderPair> prepareRequestHeaders() {
        return Arrays.asList(
                HttpHeaderPair.builder()
                        .key(Headers.CONTENT_TYPE)
                        .value(APPLICATION_FORM_URLENCODED)
                        .build(),
                HttpHeaderPair.builder().key(Headers.ACCEPT).value(APPLICATION_JSON).build());
    }

    public String formatCachedToken() {
        return oAuthResponse.getTokenType() + " " + oAuthResponse.getAccessToken();
    }

    public long getCurrentTime() {
        return java.time.Instant.now().getEpochSecond();
    }

    @SneakyThrows
    public synchronized String getAuthToken() {
        if (isCachedTokenValid()) {
            log.debug("Returning cached token");
            return formatCachedToken();
        }
        try {
            this.setOAuthResponse(fetchTokenFromPhonePe());
        } catch (Exception exception) {
            if (Objects.isNull(oAuthResponse)) {
                log.error(
                        "No cached token, error occurred while fetching new token {}",
                        exception.toString());
                throw exception;
            }
            log.info(
                    "Returning cached token, error occurred while fetching new token {}",
                    exception.toString());
            eventPublisher.send(
                    BaseEvent.buildOAuthEvent(
                            getCurrentTime(),
                            TokenConstants.OAUTH_GET_TOKEN,
                            EventType.OAUTH_FETCH_FAILED_USED_CACHED_TOKEN,
                            exception,
                            oAuthResponse.getIssuedAt(),
                            oAuthResponse.getExpiresAt()));
        }
        return formatCachedToken();
    }

    private synchronized boolean isCachedTokenValid() {
        if (Objects.isNull(oAuthResponse)) {
            return false;
        }
        long issuedAt = oAuthResponse.getIssuedAt();
        long expireAt = oAuthResponse.getExpiresAt();
        long currentTime = getCurrentTime();
        long reloadTime = issuedAt + (expireAt - issuedAt) / 2;
        return currentTime < reloadTime;
    }

    public void forceRefreshToken() {
        log.debug("Force Refreshing Token");
        this.setOAuthResponse(fetchTokenFromPhonePe());
    }

    @SneakyThrows
    public synchronized OAuthResponse fetchTokenFromPhonePe() {
        final FormBody formBody = prepareFormBody(this.credentialConfig);

        final String url = TokenConstants.OAUTH_GET_TOKEN;
        return HttpCommand.<OAuthResponse, FormBody>builder()
                .client(okHttpClient)
                .objectMapper(objectMapper)
                .responseTypeReference(new TypeReference<OAuthResponse>() {})
                .methodName(HttpMethodType.POST)
                .headers(prepareRequestHeaders())
                .requestData(formBody)
                .hostURL(this.env.getOAuthHostUrl())
                .url(url)
                .encodingType(APPLICATION_FORM_URLENCODED)
                .build()
                .execute();
    }

    private FormBody prepareFormBody(CredentialConfig credentialConfig) {
        return new FormBody.Builder()
                .add("client_id", credentialConfig.getClientId())
                .add("client_secret", credentialConfig.getClientSecret())
                .add("grant_type", TokenConstants.OAUTH_GRANT_TYPE)
                .add("client_version", String.valueOf(credentialConfig.getClientVersion()))
                .build();
    }
}
