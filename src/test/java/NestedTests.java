import io.qameta.allure.Description;
import models.Post;
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
import java.util.List;
import java.util.stream.Collectors;

public class NestedTests extends BaseTests {
    ApiClientUtils apiClientUtils = new ApiClientUtils();
    private static final Logger logger = Logger.getLogger(NestedTests.class);
    private final String token = Constants.TOKEN.value;
    private String userId;
    private int postId;
    private final User user = new User();
    private final Post post = new Post();

    @BeforeClass
    private void createUser() throws IOException {
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
    @Description("Creating a post")
    public void createPost() throws IOException {
        logger.info("Creating a post for user: " + userId);
        post.setTitle("Title of the post");
        post.setBody("Body of the post");
        Response<Post> responsePost = apiClientUtils.getApiClient().createPost(token, userId, post).execute();
        Assert.assertEquals(responsePost.code(), 201, "Error: Invalid response code, post is not created");
        Assert.assertNotNull(responsePost.body(), "Error: Post body is null");
        Assert.assertEquals(responsePost.body().getTitle(), post.getTitle(), "Post title is invalid");
        Assert.assertEquals(responsePost.body().getBody(), post.getBody(), "Post body is invalid");
        postId = responsePost.body().getId();
        logger.info("Test finished");
    }

    @Test(priority = 2)
    @Description("Getting the post")
    public void getPost() throws IOException {
        logger.info("Getting the post");
        Response<List<Post>> response = apiClientUtils.getApiClient().getPost(token, userId).execute();
        Assert.assertNotNull(response.body(), "Error: Body is null");
        Assert.assertEquals(response.code(), 200, "Error: Invalid response code, could not get the posts");
        List<Integer> idList = response.body().stream().map(Post::getId).collect(Collectors.toList());
        int index;
        for (index = 0; index < idList.size(); index++){
            if (postId == idList.get(index))
                break;
        }

        Assert.assertEquals(response.body().get(index).getTitle(), post.getTitle(), "Post title is invalid");
        Assert.assertEquals(response.body().get(index).getBody(), post.getBody(), "Post body is invalid");
        logger.info("Test finished");
    }
}
