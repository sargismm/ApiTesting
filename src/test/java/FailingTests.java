import io.qameta.allure.Description;
import models.Post;
import models.User;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;
import retrofit2.Response;
import utils.ApiClientUtils;
import utils.Constants;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FailingTests {
    ApiClientUtils apiClientUtils = new ApiClientUtils();
    private static final Logger logger = Logger.getLogger(FailingTests.class);
    private final String token = Constants.TOKEN.value;
    private User user = new User();

    @BeforeClass
    public void before() {
        logger.info("\n==============================================================" +
                "\nFAILING TESTS" +
                "\n==============================================================");
    }

    /**
     * Example of a test that depends on another test.
     * In this case user is not created, thus it is impossible to get user ID.
     * To avoid wasting time and resources, it is skipped.
     * @throws IOException
     */

    @Test(priority = 1, enabled = false)
    @Description("Broken test: creating user")
    public void brokenTestCreateUser() throws IOException {
        logger.info("Creating a user with null user");
        user = null;
        try {
            apiClientUtils.getApiClient().createUser(token, user).execute();
        } catch (Exception e) {
            logger.error(e);
            String errorLog = Arrays.toString(e.getStackTrace());
            String result = errorLog.substring(1, errorLog.length() - 1);
            AllureListener.saveTextLog(result);
            throw e;
        }
        logger.info("Negative test finished");
    }

    @Test(priority = 2, dependsOnMethods = "brokenTestCreateUser", enabled = false)
    @Description("This test will get skipped because user was not created")
    public void skippedGetUser() {
        logger.warn("SKIPPED TEST");
    }

    @Test(priority = 3, enabled = false)
    @Description("Getting a post for a user that does not exist")
    public void getPostForInvalidUser() throws IOException {
        logger.info("Getting a post for a user that does not exist");
        Response<List<Post>> response = apiClientUtils.getApiClient().getPost(token, "invalidId").execute();
        Assert.assertNotNull(response.errorBody(), "Error: Body is null");
        Assert.assertEquals(response.code(), 404, "Error: Invalid response code, could not get the posts");

        logger.info("Test finished");
    }
}
