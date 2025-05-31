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

    protected BaseClient(String clientId, String clientSecret, Integer clientVersion, Env env,
            boolean shouldPublishEvents) {
        this.okHttpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.env = env;
        this.credentialConfig = CredentialConfig.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientVersion(clientVersion)
                .build();
        this.shouldPublishEvents = shouldPublishEvents;
        this.eventPublisherFactory = new EventPublisherFactory(this.getObjectMapper(), this.getOkHttpClient(),
                env.getEventsHostUrl());
        this.eventPublisher = this.eventPublisherFactory.getEventPublisher(shouldPublishEvents);
        this.tokenService = new TokenService(this.okHttpClient, this.objectMapper, credentialConfig, this.env,
                this.eventPublisher);
        this.eventPublisher.startPublishingEvents(tokenService::getAuthToken);
    }

    @SneakyThrows
    protected <T, R> T requestViaAuthRefresh(HttpMethodType methodName, R requestData,
            String url, Map<String, String> queryParams, TypeReference<T> responseTypeReference,
            List<HttpHeaderPair> headers) {
        List<HttpHeaderPair> httpHeaders = new ArrayList<>(headers);
        HttpCommand<T, R> httpCommand = HttpCommand.<T, R>builder()
                .client(this.okHttpClient)
                .objectMapper(this.objectMapper)
                .responseTypeReference(responseTypeReference)
                .methodName(methodName)
                .headers(addAuthHeader(httpHeaders))
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

    @SneakyThrows
    protected List<HttpHeaderPair> addAuthHeader(List<HttpHeaderPair> headers) {
        headers.add(HttpHeaderPair.builder()
                .key(Headers.OAUTH_AUTHORIZATION)
                .value(tokenService.getAuthToken())
                .build());

        return headers;
    }

}
