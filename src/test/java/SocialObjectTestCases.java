import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SocialObjectTestCases {
    private User user;
    private Post post;
    private Comment comment;

    @BeforeEach
    public void setUp() {
        // set up a test user, post, and comment for each test ase
        user = new User("123-456", "testUser", "password", "bio", 1, 0);
        post = new Post("123-456", "345-678", true, 4, 2, "msg");
        comment = new Comment("123-456", "345-678","567-890", 1, -2, "msg");
    }

    @Test
    public void testUserConstructor() {
        // tests that user is initialized and that all fields are correctly set
        assertEquals("123-456", user.getUserUUID());
        assertEquals("testUser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("bio", user.getBio());
        assertEquals(1, user.getPfp());
        assertEquals(0, user.getLikes());
    }

    @Test
    public void testUserEquals() {
        // tests equals method for user
        User user1 = new User("456", "testUser", "password", "bio", 1, 0);
        User user2 = new User("123-456", "testUser", "password", "bio", 1, 0);
        assertNotEquals(user1, user);
        assertEquals(user, user2);
        assertNotEquals(user, post);
    }

    @Test
    public void testPostConstructor() {
        // test that the post constructor properly initializes everything
        assertEquals("123-456", post.getCreatorUUID());
        assertEquals("345-678", post.getPostUUID());
        assertTrue(post.isCommentEnabled());
        assertEquals(4, post.getLikes());
        assertEquals(2, post.getDislikes());
        assertEquals("msg", post.getContent());
    }

    @Test
    public void testPostEquals() {
        // test the equals method for post
        Post post1 = new Post("123-456", "345", true, 4, 2, "msg");
        Post post2 = new Post("123-456", "345-678", true, 4, 2, "msg");
        assertEquals(post, post2);
        assertNotEquals(post, post1);
        assertNotEquals(post, user);
    }

    @Test
    public void testCommentConstructor() {
        // test that the comment constructor properly initializes everything
        assertEquals("123-456", comment.getCreator_UUID());
        assertEquals("345-678", comment.getPost_UUID());
        assertEquals("567-890", comment.getComment_UUID());
        assertEquals("msg", comment.getContent());
        assertEquals(1, comment.getLikes());
        assertEquals(-2, comment.getDislikes());
    }

    @Test
    public void testCommentEquals() {
        // tests the comment equals method
        Comment comment1 = new Comment("123-456", "345-678","567", 1, -2, "msg");
        Comment comment2 = new Comment("123-456", "345-678","567-890", 1, -2, "msg");
        assertEquals(comment, comment2);
        assertNotEquals(comment1, comment2);
        assertNotEquals(comment2, user);
        assertNotEquals(post, comment);
    }
}

