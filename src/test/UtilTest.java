import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.CommonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilTest {

    @Test
    void testSha256SuccessFor5Parameters() {
        String clientId = "";
        String clientSecret = "";
        Integer clientVersion = 1;
        String expected = "364112ec2ed6825c04df3ba71d9276c7542b28972f491fc4eb5a8e8c1a73c717";
        Assertions.assertEquals(expected, CommonUtils.calculateSha256(clientId, clientSecret, clientVersion,
                Env.TEST, false));
    }

    @Test
    void testSha256SuccessFor2Parameters() {
        String userId = "";
        String password = "";
        String expected = "e7ac0786668e0ff0f02b62bd04f45ff636fd82db63b1104601c975dc005f3a67";
        Assertions.assertEquals(expected, CommonUtils.calculateSha256(userId, password));
    }

}
