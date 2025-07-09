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
package com.phonepe.sdk.pg.common;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.configs.CredentialConfig;
import com.phonepe.sdk.pg.common.constants.Headers;
import com.phonepe.sdk.pg.common.events.publisher.EventPublisher;
import com.phonepe.sdk.pg.common.events.publisher.EventPublisherFactory;
import com.phonepe.sdk.pg.common.exception.UnauthorizedAccess;
import com.phonepe.sdk.pg.common.http.HttpCommand;
import com.phonepe.sdk.pg.common.http.HttpHeaderPair;
import com.phonepe.sdk.pg.common.http.HttpMethodType;
import com.phonepe.sdk.pg.common.tokenhandler.TokenService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;

import javax.validation.constraints.Max;

@Getter
public abstract class BaseClient {

    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;
    private final Env env;
    private TokenService tokenService;
    private CredentialConfig credentialConfig;
    protected EventPublisherFactory eventPublisherFactory;
    protected EventPublisher eventPublisher;
    private boolean shouldPublishEvents;
    protected List<HttpHeaderPair> headers;

    protected BaseClient(
            String clientId,
            String clientSecret,
            Integer clientVersion,
            Env env,
            boolean shouldPublishEvents) {
        this.okHttpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.headers = new List<HttpHeaderPair>();
        this.env = env;
        this.credentialConfig =
                CredentialConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .clientVersion(clientVersion)
                        .build();
        this.shouldPublishEvents = shouldPublishEvents;
        this.eventPublisherFactory =
                new EventPublisherFactory(
                        this.getObjectMapper(), this.getOkHttpClient(), env.getEventsHostUrl());
        this.eventPublisher = this.eventPublisherFactory.getEventPublisher(shouldPublishEvents);
        this.tokenService =
                new TokenService(
                        this.okHttpClient,
                        this.objectMapper,
                        credentialConfig,
                        this.env,
                        this.eventPublisher);
        this.eventPublisher.startPublishingEvents(tokenService::getAuthToken);
        this.headers = prepareHeaders();
    }

    @SneakyThrows
    protected <T, R> T requestViaAuthRefresh(
            HttpMethodType methodName,
            R requestData,
            String url,
            Map<String, String> queryParams,
            TypeReference<T> responseTypeReference,
            List<HttpHeaderPair> headers) {
        List<HttpHeaderPair> httpHeaders = new ArrayList<>(headers);
        HttpCommand<T, R> httpCommand =
                HttpCommand.<T, R>builder()
                        .client(this.okHttpClient)
                        .objectMapper(this.objectMapper)
                        .responseTypeReference(responseTypeReference)
                        .methodName(methodName)
                        .headers(this.headers)
                        .requestData(requestData)
                        .hostURL(this.env.getPgHostUrl())
                        .encodingType(APPLICATION_JSON)
                        .queryParams(queryParams)
                        .url(url)
                        .build();

        try {
            return httpCommand.execute();
        } catch (UnauthorizedAccess unauthorizedAccess) {
            tokenService.forceRefreshToken();
            throw unauthorizedAccess;
        }
    }

    protected void prepareHeaders() {
        headers.add(
                HttpHeaderPair.builder()
                        .key(Headers.OAUTH_AUTHORIZATION)
                        .value(tokenService.getAuthToken())
                        .build());
        headers.add(
                HttpHeaderPair.builder().key(Headers.CONTENT_TYPE).value(APPLICATION_JSON).build());
        headers.add(
                HttpHeaderPair.builder().key(Headers.SOURCE).value(Headers.INTEGRATION).build());
        headers.add(
                HttpHeaderPair.builder()
                        .key(Headers.SOURCE_VERSION)
                        .value(Headers.API_VERSION)
                        .build());
        headers.add(
                HttpHeaderPair.builder()
                        .key(Headers.SOURCE_PLATFORM)
                        .value(Headers.SDK_TYPE)
                        .build());
        headers.add(
                HttpHeaderPair.builder()
                        .key(Headers.SOURCE_PLATFORM_VERSION)
                        .value(Headers.SDK_VERSION)
                        .build());
    }
}
