import io.qameta.allure.Description;
import models.User;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;
import utils.ApiClientUtils;
import utils.Constants;

import java.io.IOException;
import java.util.Arrays;

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

    @Test(priority = 1)
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

    @Test(priority = 2, dependsOnMethods = "brokenTestCreateUser")
    @Description("This test will get skipped because user was not created")
    public void skippedGetUser() {
        logger.warn("SKIPPED TEST");
    }
}
