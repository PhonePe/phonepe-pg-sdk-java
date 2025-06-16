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
package com.phonepe.sdk.pg.common.exception;

import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;

public class ExceptionMapper {

    public static Map<Integer, Class<?>> codeToException =
            Stream.of(
                            new AbstractMap.SimpleEntry<>(400, BadRequest.class),
                            new AbstractMap.SimpleEntry<>(401, UnauthorizedAccess.class),
                            new AbstractMap.SimpleEntry<>(403, ForbiddenAccess.class),
                            new AbstractMap.SimpleEntry<>(404, ResourceNotFound.class),
                            new AbstractMap.SimpleEntry<>(409, ResourceConflict.class),
                            new AbstractMap.SimpleEntry<>(410, ResourceGone.class),
                            new AbstractMap.SimpleEntry<>(417, ExpectationFailed.class),
                            new AbstractMap.SimpleEntry<>(422, ResourceInvalid.class),
                            new AbstractMap.SimpleEntry<>(429, TooManyRequest.class))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    @SneakyThrows
    public static void prepareCodeToException(
            int responseCode, String message, PhonePeResponse phonePeResponse) {

        Class<?> exceptionClass = codeToException.get(responseCode);
        throw (Throwable)
                exceptionClass
                        .getDeclaredConstructor(Integer.class, String.class, PhonePeResponse.class)
                        .newInstance(responseCode, message, phonePeResponse);
    }
}
