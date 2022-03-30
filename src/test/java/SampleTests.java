import io.qameta.allure.Description;
import models.User;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;
import retrofit2.Response;
import utils.ApiClientUtils;
import utils.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Listeners(AllureListener.class)
public class SampleTests extends BaseTests {
    ApiClientUtils apiClientUtils = new ApiClientUtils();
    private static final Logger logger = Logger.getLogger(SampleTests.class);
    private String userId;
    private final String token = Constants.TOKEN.value;

    @Test(priority = 1)
    @Description("Getting all the users to verify that API is working")
    public void getUsers() throws IOException {
        Response<List<Map<String, String>>> response = apiClientUtils.getApiClient().getUsers(token).execute();
        Assert.assertNotNull(response.body());
        Assert.assertEquals(response.code(), 200);
    }

    @Parameters(value = {"name", "email", "gender", "status"})
    @Test(priority = 2)
    public void createUser(String name, String email, String gender, String status) throws IOException {
        User user = new User();
        user.setEmail(email);
        user.setGender(gender);
        user.setName(name);
        user.setStatus(status);
        Response<Map<String, String>> response = apiClientUtils.getApiClient().createUser(token, user).execute();
        Assert.assertNotNull(response.body());
        Assert.assertEquals(response.code(), 201);
        userId = response.body().get("id");
    }

    @Test(priority = 3)
    public void getUserById() throws IOException {
        Response<Map<String, String>> response = apiClientUtils.getApiClient().getUserDetail(token, userId).execute();
        Assert.assertNotNull(response.body());
        Assert.assertEquals(response.code(), 200);
    }

    @Test(priority = 4)
    public void deleteUser() throws IOException {
        Response<Void> response = apiClientUtils.getApiClient().deleteUser(token, userId).execute();
        Assert.assertEquals(response.code(), 204);
    }
}
