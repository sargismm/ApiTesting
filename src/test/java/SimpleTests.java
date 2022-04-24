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
public class SimpleTests extends BaseTests {

    ApiClientUtils apiClientUtils = new ApiClientUtils();
    private static final Logger logger = Logger.getLogger(SimpleTests.class);
    private String userId;
    private final String token = Constants.TOKEN.value;
    private final User user = new User();

    @Test(priority = 1)
    @Description("Getting all the users to verify that API is working")
    public void getUsers() throws IOException {
        logger.info("Getting all users list");
        Response<List<Map<String, String>>> response = apiClientUtils.getApiClient().getUsers(token).execute();
        Assert.assertNotNull(response.body(), "Error: Body is null");
        Assert.assertEquals(response.code(), 200, "Error: Invalid response code, users not found");
        logger.info("Test finished");
    }

    @Parameters(value = {"name", "email", "gender", "status"})
    @Test(priority = 2)
    @Description("Creating a new user")
    public void createUser(String name, String email, String gender, String status) throws IOException {
        logger.info("Setting user info");
        user.setEmail(email);
        user.setGender(gender);
        user.setName(name);
        user.setStatus(status);
        logger.info("Creating a user");
        Response<User> response = apiClientUtils.getApiClient().createUser(token, user).execute();
        Assert.assertNotNull(response.body(), "Error: Body is null");
        Assert.assertEquals(response.code(), 201, "Error: Invalid response code, user is not created");
        userId = response.body().getId();
        logger.info("Test finished");
    }

    @Test(priority = 3, dependsOnMethods = "createUser")
    @Description("Getting user by ID")
    public void getUserById() throws IOException {
        logger.info("Getting created user by ID");
        Response<Map<String, String>> response = apiClientUtils.getApiClient().getUserDetail(token, userId).execute();
        Assert.assertNotNull(response.body(), "Error: Body is null");
        Assert.assertEquals(response.code(), 200, "Error: Invalid response code, user id not found");
        logger.info("Test finished");
    }

    @Test(priority = 4, dependsOnMethods = "createUser")
    @Description("Updating user info")
    public void updateUser() throws IOException {
        logger.info("Setting user status to inactive");
        user.setStatus("inactive");
        Response<User> response = apiClientUtils.getApiClient().updateUser(token, userId, user).execute();
        Assert.assertNotNull(response.body(), "Error: Body is null");
        Assert.assertEquals(response.code(), 200, "Error: Invalid response code, user is not updated");
        Assert.assertEquals(response.body().getStatus(), "inactive", "Status is not updated");
        logger.info("Test finished");
    }

    @Test(priority = 5, dependsOnMethods = "createUser")
    @Description("Deleting the user by ID")
    public void deleteUser() throws IOException {
        logger.info("Removing the user");
        Response<Void> response = apiClientUtils.getApiClient().deleteUser(token, userId).execute();
        Assert.assertEquals(response.code(), 204, "Error: Invalid response code, user is not deleted");
        logger.info("Test finished");
    }
}
