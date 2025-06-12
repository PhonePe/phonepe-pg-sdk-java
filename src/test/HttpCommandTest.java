import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.phonepe.sdk.pg.common.exception.PhonePeException;
import com.phonepe.sdk.pg.common.http.HttpCommand;
import com.phonepe.sdk.pg.common.http.HttpMethodType;
import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

public class HttpCommandTest extends BaseSetup {

    @Test
    public void testHttpCommand200() {
        HttpCommand<Integer, String> httpCommand = HttpCommand.<Integer, String>builder()
                .hostURL("http://localhost:30419")
                .client(okHttpClient)
                .objectMapper(mapper)
                .responseTypeReference(new TypeReference<Integer>() {
                })
                .url("/testing")
                .methodName(HttpMethodType.GET)
                .build();
        addStubForGetRequest("/testing", ImmutableMap.of(), ImmutableMap.of(), HttpStatus.SC_OK, ImmutableMap.of(),
                (int) 200);
        Integer actual = httpCommand.execute();
        Assertions.assertEquals(200, actual);
    }


    @Test
    void testIfPhonePeResponseNull() {
        HttpCommand<Integer, String> httpCommand = HttpCommand.<Integer, String>builder()
                .hostURL("http://localhost:30419")
                .client(okHttpClient)
                .objectMapper(mapper)
                .responseTypeReference(new TypeReference<Integer>() {
                })
                .url("/testing")
                .methodName(HttpMethodType.GET)
                .build();
        PhonePeResponse phonePeResponse = PhonePeResponse.<Map<String, String>>builder()
                .build();
        addStubForGetRequest("/testing", ImmutableMap.of(), ImmutableMap.of(), HttpStatus.SC_NOT_FOUND,
                ImmutableMap.of(),
                phonePeResponse);
        final PhonePeException phonePeException = assertThrows(PhonePeException.class,
                () -> httpCommand.execute());
        Assertions.assertEquals(404, phonePeException.getHttpStatusCode());
        Assertions.assertEquals("Not Found", phonePeException.getMessage());
    }


}
