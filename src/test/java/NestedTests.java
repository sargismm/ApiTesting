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

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class NestedTests extends BaseTests {
    private static final Logger logger = Logger.getLogger(NestedTests.class);
    private String userId;
    private int postId;
    private int todoId;
    private final User user = new User();
    private final Post post = new Post();
    private final Comment comment = new Comment();
    private Todos todo;

    @BeforeClass
    private void before() throws IOException {
        logger.info("\n==============================================================" +
                "\nNESTED TESTS\n" +
                "==============================================================");
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

    @Test(priority = 2, dependsOnMethods = "createPost")
    @Description("Getting the post")
    public void getPost() throws IOException {
        logger.info("Getting the post");
        Response<List<Post>> response = apiClientUtils.getApiClient().getPost(token, userId).execute();
        Assert.assertNotNull(response.body(), "Error: Body is null");
        Assert.assertEquals(response.code(), 200, "Error: Invalid response code, could not get the posts");
        List<Integer> idList = response.body().stream().map(Post::getId).collect(Collectors.toList());
        int index;
        for (index = 0; index < idList.size(); index++) {
            if (postId == idList.get(index)) {
                post.setId(postId);
                break;
            }
        }

        Assert.assertEquals(response.body().get(index).getTitle(), post.getTitle(), "Post title is invalid");
        Assert.assertEquals(response.body().get(index).getBody(), post.getBody(), "Post body is invalid");
        logger.info("Test finished");
    }

    @Test(priority = 3)
    @Description("Creating a post comment")
    public void createComment() throws IOException {
        logger.info("Creating a post comment");
        comment.setPost_id(post.getId());
        comment.setName(user.getName());
        comment.setEmail(user.getEmail());
        comment.setBody("This is comment body");
        Response<Comment> response = apiClientUtils.getApiClient().createComment(token, comment).execute();
        Assert.assertEquals(response.code(), 201, "Error: Invalid response code, comment is not created");
        Assert.assertNotNull(response.body(), "Error: Comment body is null");
        Assert.assertEquals(response.body().getBody(), comment.getBody(), "Post comment is invalid");
    }

    @Test(priority = 4, dependsOnMethods = "createComment")
    @Description("Getting post comment")
    public void getComment() throws IOException {
        logger.info("Getting a post comment");
        Response<List<Comment>> response = apiClientUtils.getApiClient().getComment(token, post.getId()).execute();
        Assert.assertEquals(response.code(), 200, "Error: Invalid response code, comment does not exist");
        Assert.assertNotNull(response.body(), "Error: Comment body is null");

        List<Integer> idList = response.body().stream().map(Comment::getPost_id).collect(Collectors.toList());
        int index;
        for (index = 0; index < idList.size(); index++) {
            if (postId == idList.get(index)) {
                post.setId(postId);
                break;
            }
        }

        Assert.assertEquals(response.body().get(index).getName(), comment.getName(), "Error: Comment name does not match");
        Assert.assertEquals(response.body().get(index).getEmail(), comment.getEmail(), "Error: Comment email does not match");
    }

    @Test(priority = 5)
    @Description("Creating a user todo")
    public void createTodo() throws IOException {
        todo = new Todos("Todo title", "2022-12-31T00:00:00.000+05:30", "pending");
        Response<Todos> response = apiClientUtils.getApiClient().createUserTodo(token, userId, todo).execute();
        Assert.assertEquals(response.code(), 201, "Error: Invalid response code, todos is not created");
        Assert.assertNotNull(response.body(), "Error: Todos body is null");
        Assert.assertEquals(response.body().getTitle(), todo.getTitle(), "Todo title is invalid");
        todoId = response.body().getId();
    }

    @Test(priority = 6, dependsOnMethods = "createTodo")
    @Description("Getting a user todo")
    public void getTodo() throws IOException {
        logger.info("Getting a user todo");
        Response<List<Todos>> response = apiClientUtils.getApiClient().getTodo(token, userId).execute();
        Assert.assertEquals(response.code(), 200, "Error: Invalid response code, todo does not exist");
        Assert.assertNotNull(response.body(), "Error: Todo body is null");
        List<Integer> idList = response.body().stream().map(Todos::getId).collect(Collectors.toList());
        int index;
        for (index = 0; index < idList.size(); index++) {
            if (todoId == idList.get(index)) {
                todo.setId(todoId);
                break;
            }
        }
        Assert.assertEquals(response.body().get(index).getTitle(), todo.getTitle(), "Error: Todo title does not match");
        Assert.assertEquals(response.body().get(index).getStatus(), todo.getStatus(), "Error: Todo status does not match");
    }
}
