import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.Maps;
import com.phonepe.sdk.pg.common.exception.BadRequest;
import com.phonepe.sdk.pg.common.exception.PhonePeException;
import com.phonepe.sdk.pg.payments.v2.models.request.StandardCheckoutPayRequest;
import com.phonepe.sdk.pg.payments.v2.standardcheckout.StandardCheckoutConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

public class ExceptionTest extends BaseSetupWithOAuth {

    @Test
    void testR999() {
        String json = "R999";
        final String url = StandardCheckoutConstants.PAY_API;
        StandardCheckoutPayRequest standardCheckoutPayRequest =
                StandardCheckoutPayRequest.builder()
                        .merchantOrderId(merchantOrderId)
                        .amount(amount)
                        .build();
        addStubForPostRequest(url, getHeaders(), standardCheckoutPayRequest, HttpStatus.SC_SERVICE_UNAVAILABLE,
                Maps.newHashMap(), json);

        PhonePeException phonePeException = assertThrows(PhonePeException.class,
                () -> standardCheckoutClient.pay(standardCheckoutPayRequest));
        Assertions.assertEquals(503, phonePeException.getHttpStatusCode());
        Assertions.assertEquals("Service Unavailable", phonePeException.getMessage());
    }

    @Test
    void testBadRequest() {
        String response = "{\"errorCode\":\"OIM000\",\"code\":\"INVALID_CLIENT\","
                + "\"message\":\"Bad Request: Invalid Client, trackingId: 2123d\","
                + "\"context\":{\"error_description\":\"Client authentication failure\"}}";
        final String url = StandardCheckoutConstants.PAY_API;
        StandardCheckoutPayRequest standardCheckoutPayRequest =
                StandardCheckoutPayRequest.builder()
                        .merchantOrderId(merchantOrderId)
                        .amount(amount)
                        .build();
        addStubForPostRequest(url, getHeaders(), standardCheckoutPayRequest, HttpStatus.SC_BAD_REQUEST,
                Maps.newHashMap(), response);

        BadRequest badRequest = assertThrows(BadRequest.class,
                () -> standardCheckoutClient.pay(standardCheckoutPayRequest));

        Assertions.assertEquals(400, badRequest.getHttpStatusCode());
        Assertions.assertEquals("Bad Request: Invalid Client, trackingId: 2123d", badRequest.getMessage());
    }
}
