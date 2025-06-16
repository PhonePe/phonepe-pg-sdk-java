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
package com.phonepe.sdk.pg.common.http;

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phonepe.sdk.pg.common.exception.ClientError;
import com.phonepe.sdk.pg.common.exception.ExceptionMapper;
import com.phonepe.sdk.pg.common.exception.PhonePeException;
import com.phonepe.sdk.pg.common.exception.ServerError;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Builder
@Setter
@AllArgsConstructor
@Slf4j
public class HttpCommand<T, R> {

    private final OkHttpClient client;
    @NotNull private final String hostURL;
    @NotNull private String url;
    private List<HttpHeaderPair> headers;
    private final ObjectMapper objectMapper;
    private final TypeReference<T> responseTypeReference;
    private final R requestData;
    private final String encodingType;
    @NotBlank private final HttpMethodType methodName;
    private Map<String, String> queryParams;

    /**
     * Returns the request body containing the serialized data.
     *
     * @return the request body as a RequestBody object
     */
    @SneakyThrows
    private RequestBody prepareRequestBody() {
        if (Objects.equals(encodingType, APPLICATION_JSON)) {
            return RequestBody.create(
                    objectMapper.writeValueAsBytes(requestData), MediaType.parse(encodingType));
        }

        if (Objects.equals(encodingType, APPLICATION_FORM_URLENCODED)) {
            return (RequestBody) requestData;
        }
        return (RequestBody) requestData;
    }

    /**
     * Constructs an HTTP URL by appending the provided URL to the base host URL.
     *
     * @param url the URL to append to the base host URL
     * @return the constructed HttpUrl object
     * @throws IllegalArgumentException if the base host URL or the provided URL is invalid
     */
    private HttpUrl prepareHttpURL(String url) {
        HttpUrl.Builder urlBuilder =
                Objects.requireNonNull(HttpUrl.parse(String.format("%s%s", this.hostURL, url)))
                        .newBuilder();
        if (!Objects.isNull(queryParams)) {
            for (Map.Entry<String, String> param : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }
        return HttpUrl.get(urlBuilder.build().toString());
    }

    /**
     * Executes the HTTP request and returns the response.
     *
     * @return <T> the response object
     */
    @SneakyThrows
    public T execute() {
        log.info("Calling {} : {}{}", methodName, hostURL, url);
        final HttpUrl httpUrl = prepareHttpURL(this.url);
        final Request httpRequest = prepareRequest(httpUrl);
        final Response response = client.newCall(httpRequest).execute();
        return handleResponse(response);
    }

    @SneakyThrows
    public T handleResponse(Response response) {
        int responseCode = response.code();
        final byte[] responseBody =
                Objects.nonNull(response.body()) ? response.body().bytes() : null;

        if (responseCode >= 200 && responseCode <= 299) {
            return objectMapper.readValue(responseBody, responseTypeReference);
        }
        try {
            PhonePeResponse phonePeResponse =
                    objectMapper.readValue(responseBody, PhonePeResponse.class);
            if (ExceptionMapper.codeToException.containsKey(responseCode)) {
                ExceptionMapper.prepareCodeToException(
                        responseCode, response.message(), phonePeResponse);
            } else if (responseCode >= 400 && responseCode <= 499) {
                throw new ClientError(responseCode, response.message(), phonePeResponse);
            } else if (responseCode >= 500 && responseCode <= 599) {
                throw new ServerError(responseCode, response.message(), phonePeResponse);
            }
            throw new PhonePeException(responseCode, response.message(), phonePeResponse);
        } catch (JsonProcessingException jsonProcessingException) {
            throw new PhonePeException(responseCode, response.message());
        }
    }

    /**
     * Constructs an HTTP request based on the provided HTTP URL, method name, and headers.
     *
     * @param httpUrl the HTTP URL for the request
     * @return the constructed Request object
     * @throws PhonePeException if the method name is not supported
     */
    public Request prepareRequest(final HttpUrl httpUrl) {
        if (methodName == HttpMethodType.POST) {
            final Request.Builder requestBuilder =
                    new Request.Builder().url(httpUrl).post(prepareRequestBody());
            if (!Objects.isNull(headers)) {
                for (HttpHeaderPair httpHeader : headers) {
                    requestBuilder.header(httpHeader.getKey(), httpHeader.getValue());
                }
            }
            return requestBuilder.build();
        } else if (methodName == HttpMethodType.GET) {
            final Request.Builder requestBuilder = new Request.Builder().url(httpUrl).get();
            if (!Objects.isNull(headers)) {
                for (HttpHeaderPair httpHeader : headers) {
                    requestBuilder.header(httpHeader.getKey(), httpHeader.getValue());
                }
            }
            return requestBuilder.build();
        } else {
            throw new PhonePeException(405, "Method Not Supported");
        }
    }
}
