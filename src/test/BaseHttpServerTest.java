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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import lombok.SneakyThrows;
import okhttp3.FormBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import testsupport.HttpTestServer;
import testsupport.HttpTestServer.ResponseSpec;

public abstract class BaseHttpServerTest {

    public static final int HTTP_PORT = 30419;

    public final ObjectMapper mapper = new ObjectMapper();

    protected final HttpTestServer httpServer = new HttpTestServer(HTTP_PORT);

    @SneakyThrows
    @BeforeEach
    public void startHttpServer() {
        httpServer.start();
    }

    @SneakyThrows
    @AfterEach
    public void stopHttpServer() {
        try {
            Assertions.assertEquals(0, httpServer.unmatchedRequests().size(),
                    () -> httpServer.unmatchedRequests().toString());
        } finally {
            httpServer.close();
        }
    }

    protected void addStubForGetRequest(
            final String urlPath, final int status, final Object response) {
        addStubForGetRequest(
                urlPath, ImmutableMap.of(), ImmutableMap.of(), status, ImmutableMap.of(), response);
    }

    @SneakyThrows
    protected void addStubForGetRequest(
            final String urlPath,
            final Map<String, String> queryParams,
            final Map<String, String> requestHeaders,
            final int status,
            final Map<String, String> responseHeaders,
            final Object response) {

        httpServer.stub("GET", urlPath, requestHeaders, null, queryParams,
                new ResponseSpec(status, responseHeaders, serialize(response)));
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
        addStubForPostRequest(
                urlPath,
                requestHeaders,
                request,
                status,
                responseHeaders,
                response,
                ImmutableMap.of());
    }

    @SneakyThrows
    protected void addStubForPostRequest(
            final String urlPath,
            final Map<String, String> requestHeaders,
            final Object request,
            final int status,
            final Map<String, String> responseHeaders,
            final Object response,
            final Map<String, String> queryParams) {
        httpServer.stub("POST", urlPath, requestHeaders, serialize(request), queryParams,
                new ResponseSpec(status, responseHeaders, serialize(response)));
    }

    @SneakyThrows
    private String serialize(Object value) {
        return value instanceof String ? (String) value : mapper.writeValueAsString(value);
    }

    protected void addStubForFormDataPostRequest(
            final String urlPath,
            final Map<String, String> requestHeaders,
            final Object request,
            final int status,
            final Map<String, String> responseHeaders,
            final Object response) {
        String requestBody = formDataToString((FormBody) request);
        addStubForPostRequest(
                urlPath,
                requestHeaders,
                requestBody,
                status,
                responseHeaders,
                response,
                ImmutableMap.of());
    }

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
}
