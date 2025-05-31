package com.phonepe.sdk.pg.common.exception;

import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;

public class ExceptionMapper {

    public static Map<Integer, Class<?>> codeToException = Stream.of(
                    new AbstractMap.SimpleEntry<>(400, BadRequest.class),
                    new AbstractMap.SimpleEntry<>(401, UnauthorizedAccess.class),
                    new AbstractMap.SimpleEntry<>(403, ForbiddenAccess.class),
                    new AbstractMap.SimpleEntry<>(404, ResourceNotFound.class),
                    new AbstractMap.SimpleEntry<>(409, ResourceConflict.class),
                    new AbstractMap.SimpleEntry<>(410, ResourceGone.class),
                    new AbstractMap.SimpleEntry<>(417, ExpectationFailed.class),
                    new AbstractMap.SimpleEntry<>(422, ResourceInvalid.class),
                    new AbstractMap.SimpleEntry<>(429, TooManyRequest.class)
            )
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    @SneakyThrows
    public static void prepareCodeToException(int responseCode, String message,
            PhonePeResponse phonePeResponse) {

        Class<?> exceptionClass = codeToException.get(responseCode);
        throw (Throwable) exceptionClass.getDeclaredConstructor(Integer.class, String.class, PhonePeResponse.class)
                .newInstance(responseCode, message, phonePeResponse);

    }

}
