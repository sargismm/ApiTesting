import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.qameta.allure.Description;
import models.Comment;
import models.Post;
import models.Todos;
import models.User;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;
import retrofit2.Response;
import utils.ApiClientUtils;
import utils.Constants;
import java.io.IOException;
import java.util.Map;

public class NegativeTests extends BaseTests {
    private static final Logger logger = Logger.getLogger(FailingTests.class);
    ApiClientUtils apiClientUtils = new ApiClientUtils();
    private String userId;
    private Todos todo;
    private final String token = Constants.TOKEN.value;
    private final User user = new User();
    private final Post post = new Post();
    private final Comment comment = new Comment();



    @BeforeClass
    public void before() throws IOException {
        logger.info("\n==============================================================" +
                "\nNEGATIVE TESTS" +
                "\n==============================================================");
        logger.info("Creating a user");
        user.setName("Sargis Sargsyan");
        user.setEmail("ssargsyan@testing.org");
        user.setStatus("inactive");
        user.setGender("male");
        Response<User> responseUser = apiClientUtils.getApiClient().createUser(token, user).execute();
        Assert.assertNotNull(responseUser.body(), "Error: Body is null");
        Assert.assertEquals(responseUser.code(), 201, "Error: Invalid response code, user is not created");
        logger.info("Getting user ID");
        userId = responseUser.body().getId();
    }

    @AfterClass
    private void removeUser() throws IOException {
        apiClientUtils.getApiClient().deleteUser(token, userId).execute();
    }

    @Test(priority = 1)
    @Description("Creating a new user with invalid body")
    public void negCreateUserWithMissingBody() throws IOException {
        logger.info("Setting user info");
        user.setEmail("");
        logger.info("Creating a user with missing email");
        Response<User> response = apiClientUtils.getApiClient().createUser(token, user).execute();
        Assert.assertNotNull(response.errorBody(), "Error: Error Body is null");
        Assert.assertEquals(response.code(), 422, "Error: Invalid response code");
        Assert.assertEquals(response.message(), "Unprocessable Entity", "Error: Invalid response message");
        String errorBody = response.errorBody().string();
        JsonArray errorBodyJson = new JsonParser().parse(errorBody).getAsJsonArray();
        Assert.assertEquals(errorBodyJson.get(0).getAsJsonObject().get("field").toString().replace("\"", ""), "email",
                "Invalid field name in error");
        Assert.assertEquals(errorBodyJson.get(0).getAsJsonObject().get("message").toString().replace("\"", ""), "can't be blank",
                "Invalid message in error");
        logger.info("Test finished");
    }

    @Test(priority = 2)
    @Description("Creating a new user without authorization")
    public void negCreateUserWithoutAuth() throws IOException {
        logger.info("Setting user info");
        user.setEmail("ssargsyan@testing.org");
        logger.info("Creating a user without authorization");
        Response<User> response = apiClientUtils.getApiClient().createUser("", user).execute();
        Assert.assertNotNull(response.errorBody(), "Error: Error Body is null");
        Assert.assertEquals(response.code(), 401, "Error: Invalid response code");
        Assert.assertEquals(response.message(), "Unauthorized", "Error: Invalid response message");
        String errorBody = response.errorBody().string();
        JsonObject errorBodyJson = new JsonParser().parse(errorBody).getAsJsonObject();
        Assert.assertEquals(errorBodyJson.get("message").toString().replace("\"", ""), "Authentication failed",
                "Invalid message in error");
        logger.info("Test finished");
    }

    @Test(priority = 3)
    @Description("Creating a user that already exists")
    public void negCreateUserThatAlreadyExists() throws IOException {
        logger.info("Setting user info");
        logger.info("Creating a user that already exists");
        Response<User> response = apiClientUtils.getApiClient().createUser(token, user).execute();
        Assert.assertNotNull(response.errorBody(), "Error: Error Body is null");
        Assert.assertEquals(response.code(), 422, "Error: Invalid response code");
        Assert.assertEquals(response.message(), "Unprocessable Entity", "Error: Invalid response message");
        String errorBody = response.errorBody().string();
        JsonArray errorBodyJson = new JsonParser().parse(errorBody).getAsJsonArray();
        Assert.assertEquals(errorBodyJson.get(0).getAsJsonObject().get("field").toString().replace("\"", ""), "email",
                "Invalid field name in error");
        Assert.assertEquals(errorBodyJson.get(0).getAsJsonObject().get("message").toString().replace("\"", ""), "has already been taken");
        logger.info("Test finished");
    }

    @Test(priority = 4)
    @Description("Getting a user with invalid id")
    public void negGetUserWithInvalidId() throws IOException {
        logger.info("Getting user by invalid ID");
        Response<Map<String, String>> response = apiClientUtils.getApiClient().getUserDetail(token, "invalidId").execute();
        Assert.assertNotNull(response.errorBody(), "Error: Error Body is null");
        Assert.assertEquals(response.code(), 404, "Error: Invalid response code");
        String errorBody = response.errorBody().string();
        JsonObject errorBodyJson = new JsonParser().parse(errorBody).getAsJsonObject();
        Assert.assertEquals(errorBodyJson.get("message").toString().replace("\"", ""), "Resource not found",
                "Invalid message in error");
        logger.info("Test finished");
    }

    @Test(priority = 5)
    @Description("Updating a user with invalid body")
    public void negUpdateUserWithInvalidEmail() throws IOException {
        logger.info("Updating user by invalid email");
        user.setEmail("invalid email");
        Response<User> response = apiClientUtils.getApiClient().updateUser(token, userId, user).execute();
        Assert.assertNotNull(response.errorBody(), "Error: Error Body is null");
        Assert.assertEquals(response.code(), 422, "Error: Invalid response code");
        String errorBody = response.errorBody().string();
        JsonArray errorBodyJson = new JsonParser().parse(errorBody).getAsJsonArray();
        Assert.assertEquals(errorBodyJson.get(0).getAsJsonObject().get("field").toString().replace("\"", ""), "email",
                "Invalid field name in error");
        Assert.assertEquals(errorBodyJson.get(0).getAsJsonObject().get("message").toString().replace("\"", ""), "is invalid",
                "Invalid message in error");
        logger.info("Test finished");
    }

    @Test(priority = 6)
    @Description("Removing a user that does not exist")
    public void negDeleteUserWithInvalidId() throws IOException {
        logger.info("Deleting user by invalid ID");
        Response<Void> response = apiClientUtils.getApiClient().deleteUser(token, "invalidId").execute();
        Assert.assertNotNull(response.errorBody(), "Error: Error Body is null");
        Assert.assertEquals(response.code(), 404, "Error: Invalid response code");
        String errorBody = response.errorBody().string();
        JsonObject errorBodyJson = new JsonParser().parse(errorBody).getAsJsonObject();
        Assert.assertEquals(errorBodyJson.get("message").toString().replace("\"", ""), "Resource not found",
                "Invalid message in error");
        logger.info("Test finished");
    }

    @Test(priority = 7)
    @Description("Creating a post for a user with invalid id")
    public void negCreatePostWithInvalidId() throws IOException {
        logger.info("Creating a post for user with invalid id");
        post.setTitle("Title of the post");
        post.setBody("Body of the post");
        Response<Post> responsePost = apiClientUtils.getApiClient().createPost(token, "invalidId", post).execute();
        Assert.assertNotNull(responsePost.errorBody(), "Error: Error Body is null");
        Assert.assertEquals(responsePost.code(), 422, "Error: Invalid response code, post is not created");
        String errorBody = responsePost.errorBody().string();
        JsonArray errorBodyJson = new JsonParser().parse(errorBody).getAsJsonArray();
        Assert.assertEquals(errorBodyJson.get(0).getAsJsonObject().get("field").toString().replace("\"", ""), "user",
                "Invalid field name in error");
        Assert.assertEquals(errorBodyJson.get(0).getAsJsonObject().get("message").toString().replace("\"", ""), "must exist",
                "Invalid message in error");
        logger.info("Test finished");
    }

    @Test(priority = 8)
    @Description("Creating a post for a user with invalid body")
    public void negCreatePostWithInvalidBody() throws IOException {
        logger.info("Creating a post for user with invalid id");
        post.setTitle("Title of the post");
        post.setBody(null);
        Response<Post> responsePost = apiClientUtils.getApiClient().createPost(token, userId, post).execute();
        Assert.assertNotNull(responsePost.errorBody(), "Error: Error Body is null");
        Assert.assertEquals(responsePost.code(), 422, "Error: Invalid response code, post is not created");
        String errorBody = responsePost.errorBody().string();
        JsonArray errorBodyJson = new JsonParser().parse(errorBody).getAsJsonArray();
        Assert.assertEquals(errorBodyJson.get(0).getAsJsonObject().get("field").toString().replace("\"", ""), "body",
                "Invalid field name in error");
        Assert.assertEquals(errorBodyJson.get(0).getAsJsonObject().get("message").toString().replace("\"", ""), "can't be blank",
                "Invalid message in error");
        logger.info("Test finished");
    }

    @Test(priority = 9)
    @Description("Creating a post comment with invalid post id")
    public void negCreateCommentWithInvalidPostId() throws IOException {
        logger.info("Creating a post comment with invalid post id");
        comment.setPost_id(-1);
        comment.setName(user.getName());
        comment.setEmail(user.getEmail());
        comment.setBody("This is comment body");
        Response<Comment> response = apiClientUtils.getApiClient().createComment(token, comment).execute();
        Assert.assertNotNull(response.errorBody(), "Error: Comment body is null");
        Assert.assertEquals(response.code(), 422, "Error: Invalid response code, comment is not created");
        String errorBody = response.errorBody().string();
        JsonArray errorBodyJson = new JsonParser().parse(errorBody).getAsJsonArray();
        Assert.assertEquals(errorBodyJson.get(0).getAsJsonObject().get("field").toString().replace("\"", ""), "post",
                "Invalid field name in error");
        Assert.assertEquals(errorBodyJson.get(0).getAsJsonObject().get("message").toString().replace("\"", ""), "must exist",
                "Invalid message in error");
        logger.info("Test finished");
    }

    @Test(priority = 10)
    @Description("Creating a with invalid status")
    public void negCreateTodoWithInvalidStatus() throws IOException {
        todo = new Todos("Todo title", "2022-12-31T00:00:00.000+05:30", "invalidStatus");
        Response<Todos> response = apiClientUtils.getApiClient().createUserTodo(token, userId, todo).execute();
        Assert.assertEquals(response.code(), 422, "Error: Invalid response code, todos is not created");
        Assert.assertNotNull(response.errorBody(), "Error: Todos body is null");
        String errorBody = response.errorBody().string();
        JsonArray errorBodyJson = new JsonParser().parse(errorBody).getAsJsonArray();
        Assert.assertEquals(errorBodyJson.get(0).getAsJsonObject().get("field").toString().replace("\"", ""), "status",
                "Invalid field name in error");
        Assert.assertEquals(errorBodyJson.get(0).getAsJsonObject().get("message").toString().replace("\"", ""), "can't be blank",
                "Invalid message in error");
        logger.info("Test finished");
    }

    @Test(priority = 11)
    @Description("Creating a with invalid status")
    public void negCreateTodoWithInvalidUserId() throws IOException {
        todo = new Todos("Todo title", "2022-12-31T00:00:00.000+05:30", "pending");
        Response<Todos> response = apiClientUtils.getApiClient().createUserTodo(token, "invalidId", todo).execute();
        Assert.assertEquals(response.code(), 422, "Error: Invalid response code, todos is not created");
        Assert.assertNotNull(response.errorBody(), "Error: Todos body is null");
        String errorBody = response.errorBody().string();
        JsonArray errorBodyJson = new JsonParser().parse(errorBody).getAsJsonArray();
        Assert.assertEquals(errorBodyJson.get(0).getAsJsonObject().get("field").toString().replace("\"", ""), "user",
                "Invalid field name in error");
        Assert.assertEquals(errorBodyJson.get(0).getAsJsonObject().get("message").toString().replace("\"", ""), "must exist",
                "Invalid message in error");
        logger.info("Test finished");
    }

}
