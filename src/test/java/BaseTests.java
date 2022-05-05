import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Parameters;
import retrofit2.Response;
import utils.ApiClientUtils;
import utils.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class BaseTests {
    ApiClientUtils apiClientUtils = new ApiClientUtils();
    public final String token = Constants.TOKEN.value;

    @AfterSuite
    @Parameters(value = {"email"})
    public void cleanUp(String email) throws IOException {
        Runtime.getRuntime().exec("mvn.cmd io.qameta.allure:allure-maven:serve");
        Response<List<Map<String, String>>> response = apiClientUtils.getApiClient().getUsers(token).execute();
        Assert.assertNotNull(response.body(), "Response body is empty");
        String userId;
        if (response.body().get(0).get("email").equals(email)) {
            userId = response.body().get(0).get("id");
            apiClientUtils.getApiClient().deleteUser(token, userId).execute();
        }
        if (response.body().get(0).get("email").equals("ssargsyan@testing.org")) {
            userId = response.body().get(0).get("id");
            apiClientUtils.getApiClient().deleteUser(token, userId).execute();
        }
    }
}
