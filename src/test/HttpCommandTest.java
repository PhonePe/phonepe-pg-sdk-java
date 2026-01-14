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
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.phonepe.sdk.pg.common.exception.PhonePeException;
import com.phonepe.sdk.pg.common.http.HttpCommand;
import com.phonepe.sdk.pg.common.http.HttpMethodType;
import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

public class HttpCommandTest extends BaseSetup {

    @Test
    public void testHttpCommand200() {
        HttpCommand<Integer, String> httpCommand =
                HttpCommand.<Integer, String>builder()
                        .hostURL("http://localhost:30419")
                        .client(okHttpClient)
                        .objectMapper(mapper)
                        .responseTypeReference(new TypeReference<Integer>() {})
                        .url("/testing")
                        .methodName(HttpMethodType.GET)
                        .build();
        addStubForGetRequest(
                "/testing",
                ImmutableMap.of(),
                ImmutableMap.of(),
                HttpStatus.SC_OK,
                ImmutableMap.of(),
                (int) 200);
        Integer actual = httpCommand.execute();
        Assertions.assertEquals(200, actual);
    }

    @Test
    void testIfPhonePeResponseNull() {
        HttpCommand<Integer, String> httpCommand =
                HttpCommand.<Integer, String>builder()
                        .hostURL("http://localhost:30419")
                        .client(okHttpClient)
                        .objectMapper(mapper)
                        .responseTypeReference(new TypeReference<Integer>() {})
                        .url("/testing")
                        .methodName(HttpMethodType.GET)
                        .build();
        PhonePeResponse phonePeResponse = PhonePeResponse.<Map<String, String>>builder().build();
        addStubForGetRequest(
                "/testing",
                ImmutableMap.of(),
                ImmutableMap.of(),
                HttpStatus.SC_NOT_FOUND,
                ImmutableMap.of(),
                phonePeResponse);
        final PhonePeException phonePeException =
                assertThrows(PhonePeException.class, () -> httpCommand.execute());
        Assertions.assertEquals(404, phonePeException.getHttpStatusCode());
        Assertions.assertEquals("Not Found", phonePeException.getMessage());
    }

    @Test
    public void testHttpCommand204() throws JsonProcessingException {
        String sampleJson = "{\"state\":\"PENDING\"}";
        RequestBody requestBody = RequestBody.create(
                sampleJson.getBytes(), MediaType.parse(APPLICATION_JSON));
        HttpCommand<Void, RequestBody> httpCommand =
                HttpCommand.<Void, RequestBody>builder()
                        .hostURL("http://localhost:30419")
                        .client(okHttpClient)
                        .requestData(requestBody)
                        .objectMapper(mapper)
                        .responseTypeReference(new TypeReference<>() {})
                        .url("/testing")
                        .methodName(HttpMethodType.POST)
                        .build();
        addStubForPostRequest("/testing", sampleJson, HttpStatus.SC_NO_CONTENT, "");
        Void result = httpCommand.execute();
        Assertions.assertNull(result);
    }
}
