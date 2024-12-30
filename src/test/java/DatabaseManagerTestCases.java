import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerTestCases {
    // in order to use test cases add the following users to database with the exact userUUIDs
    private DatabaseManager dm;
    private String testUserUUID = "ca7b9e1a-dee3-4bbb-820a-feb1509bc6dc";
    private String testUserUUID2 = "8923ddc1-5fbf-4978-a6e7-734882a2fcb8";
    private User user;
    private Post post;
    private Comment comment;

    @BeforeEach
    public void setUp() {
        // init DatabaseManager instance for each test case
        dm = new DatabaseManager();
        user = dm.getUser(testUserUUID);
        post = dm.createPost(testUserUUID, "test");
        comment = dm.createComment(testUserUUID, post.getPostUUID(), "test");
    }

    @Test
    public void testConstructor() {
        // makes sure the dbManager is properly initialized
        assertNotNull(dm);
        assertNotNull(dm.getConnection());
    }

    @Test
    public void getFindUserPostComment() {
        // tests queries for finding user, comments, posts
        // tests queries for searching by username
        // tests queries for finding user posts and post comments
        assertNotNull(dm.getUser(testUserUUID));
        assertNotNull(dm.getPost(post.getPostUUID()));
        assertNotNull(dm.getComment((comment.getComment_UUID())));
        assertNull(dm.getUser(post.getPostUUID()));
        assertNull(dm.getComment(user.getUserUUID()));
        assertNotNull(dm.getComment(comment.getComment_UUID()));
        assertNull(dm.getComment(post.getPostUUID()));
        assertTrue(dm.searchByUsername("jsadfkl;jsdalkfjsdal;k").isEmpty());
        assertFalse(dm.searchByUsername("user").isEmpty());
        assertFalse(dm.searchByUsername("firstUser").isEmpty());
        assertFalse(dm.getUserPosts(testUserUUID).isEmpty());
        assertFalse(dm.getPostComments(post.getPostUUID()).isEmpty());
    }

    @Test
    public void testLogin() {
        // test the login method with test user -> return null on failure, User on success
        assertNull(dm.login("blahblahblah", "asdjfklajsdflk;jasdklf;"));
        assertNotNull(dm.login("firstUser1", "1"));
    }

    @Test
    public void testCreateDeletePostComment() {
        // test post and comment creation with different edge cases
        assertNotNull(post);
        assertNotNull(comment);
        assertNull(dm.createPost(testUserUUID.substring(0, testUserUUID.length() - 2), "blah"));
        assertNull(dm.createComment(testUserUUID, post.getPostUUID().substring(0, post.getPostUUID().length() - 2), "blah"));
        assertNull(dm.createComment(testUserUUID.substring(0, testUserUUID.length() - 2), post.getPostUUID(), "blah"));

        // teste delete post and comment
        assertFalse(dm.deletePost(post.getCreatorUUID()));
        assertFalse(dm.deleteComment(comment.getPost_UUID()));
        assertFalse(dm.deleteComment(comment.getCreator_UUID()));
        assertTrue(dm.deleteComment(comment.getComment_UUID()));
        assertTrue(dm.deletePost(post.getPostUUID()));
        assertFalse(dm.deleteComment(comment.getComment_UUID()));
        assertFalse(dm.deletePost(post.getPostUUID()));
    }

    @Test
    public void testEditUserPostComment() {
        // test editing users, posts, and comments
        assertFalse(dm.editUser(testUserUUID.substring(0, 6), "blah", "blah", 1));
        assertTrue(dm.editUser(testUserUUID, "blah", "blah", 7));
        User editedUser = dm.getUser(testUserUUID);
        assertEquals("blah", editedUser.getUsername());
        assertEquals("blah", editedUser.getBio());
        assertEquals(7, editedUser.getPfp());
        assertEquals(user, editedUser);

        assertTrue(dm.editPost(post.getPostUUID(), "wow", false));
        Post editedPost = dm.getPost(post.getPostUUID());
        assertEquals(editedPost, post);
        assertEquals("wow", editedPost.getContent());
        assertFalse(editedPost.isCommentEnabled());
        assertTrue(dm.enableDisableComments(post.getPostUUID(), true));

        assertTrue(dm.editComment(comment.getComment_UUID(), "bruh"));
        Comment editedComment = dm.getComment(comment.getComment_UUID());
        assertEquals(editedComment, comment);
        assertEquals("bruh", editedComment.getContent());
        assertFalse(dm.editComment(post.getPostUUID(), "sdlkf"));

        dm.deletePost(post.getPostUUID());
        dm.editUser(user.getUserUUID(), user.getUsername(), user.getBio(), user.getPfp());
    }

    @Test
    public void testLikeDislikePostComment() {
        // tests adding likes and dislikes to pots and comments
        // tests queries that find out who was liked or disliked a post or comment
        assertTrue(dm.addPostLikeDislike(testUserUUID, post.getPostUUID(), 1));
        assertEquals(1, dm.getPost(post.getPostUUID()).getLikes());
        assertEquals(1, dm.inPostLikedDislikedUsers(testUserUUID, post.getPostUUID()));

        assertFalse(dm.addPostLikeDislike(testUserUUID, post.getPostUUID(), -1));
        assertTrue(dm.removePostLikeDislike(testUserUUID, post.getPostUUID()));
        assertEquals(0, dm.inPostLikedDislikedUsers(testUserUUID, post.getPostUUID()));
        assertTrue(dm.addPostLikeDislike(testUserUUID, post.getPostUUID(), -1));
        assertEquals(1, dm.getPost(post.getPostUUID()).getDislikes());
        assertEquals(0, dm.getPost(post.getPostUUID()).getLikes());
        assertEquals(-1, dm.inPostLikedDislikedUsers(testUserUUID, post.getPostUUID()));
        assertFalse(dm.addPostLikeDislike(testUserUUID, post.getCreatorUUID(), 1));

        assertTrue(dm.addCommentLikeDislike(testUserUUID, comment.getComment_UUID(), 1));
        assertEquals(1, dm.getComment(comment.getComment_UUID()).getLikes());
        assertEquals(1, dm.inCommentLikedDislikedUsers(testUserUUID, comment.getComment_UUID()));

        assertFalse(dm.addCommentLikeDislike(testUserUUID, comment.getComment_UUID(), -1));
        assertTrue(dm.removeCommentLikeDislike(testUserUUID, comment.getComment_UUID()));
        assertEquals(0, dm.inCommentLikedDislikedUsers(testUserUUID, comment.getComment_UUID()));
        assertTrue(dm.addCommentLikeDislike(testUserUUID, comment.getComment_UUID(), -1));
        assertEquals(-1, dm.inCommentLikedDislikedUsers(testUserUUID, comment.getComment_UUID()));
        assertEquals(0, dm.getComment(comment.getComment_UUID()).getLikes());
        assertEquals(1, dm.getComment(comment.getComment_UUID()).getDislikes());

        assertFalse(dm.addCommentLikeDislike(testUserUUID, comment.getPost_UUID(), -1));
        dm.deletePost(post.getPostUUID());
    }

    @Test
    public void testFriendBlock() {
        // tests sending friend requests
        // tests accepting and ignoring friend requests
        // tests friending and blocking
        User user2 = dm.getUser(testUserUUID2);

        assertFalse(dm.sendFriendRequest(testUserUUID, user2.getUsername()));
        assertTrue(dm.sendFriendRequest(testUserUUID, user2.getUserUUID()));
        assertTrue(dm.getInbox(user2.getUserUUID()).contains(user));
        assertFalse(dm.getInbox(user.getUserUUID()).contains(user2));

        assertTrue(dm.unsendFriendRequest(testUserUUID, testUserUUID2));
        assertFalse(dm.unsendFriendRequest(testUserUUID, testUserUUID2));
        assertFalse(dm.unsendFriendRequest(testUserUUID2, testUserUUID));

        dm.sendFriendRequest(testUserUUID, testUserUUID2);
        assertTrue(dm.acceptFriendRequest(testUserUUID2, testUserUUID));
        assertTrue(dm.getFriendList(testUserUUID2).contains(user));
        assertTrue(dm.getFriendList(testUserUUID).contains(user2));

        assertTrue(dm.removeFriend(testUserUUID2, testUserUUID));
        assertFalse(dm.removeFriend(testUserUUID2, testUserUUID));
        assertFalse(dm.getFriendList(testUserUUID2).contains(user));
        assertFalse(dm.getFriendList(testUserUUID).contains(user2));

        dm.sendFriendRequest(testUserUUID, testUserUUID2);
        assertTrue(dm.removeInboxRequest(testUserUUID, testUserUUID2));
        assertFalse(dm.removeInboxRequest(testUserUUID, testUserUUID2));
        assertFalse(dm.removeInboxRequest(testUserUUID, testUserUUID));

        dm.sendFriendRequest(testUserUUID, testUserUUID2);
        dm.acceptFriendRequest(testUserUUID2, testUserUUID);
        assertTrue(dm.blockUser(testUserUUID2, testUserUUID));
        assertFalse(dm.getFriendList(testUserUUID2).contains(user));
        assertFalse(dm.getFriendList(testUserUUID).contains(user2));
        assertTrue(dm.getBlockedList(testUserUUID2).contains(user));

        assertTrue(dm.unblockUser(testUserUUID2, testUserUUID));
        assertFalse(dm.getBlockedList(testUserUUID2).contains(user));
    }

    @Test
    public void testHidePost() {
        // tests hiding posts
        assertTrue(dm.hidePost(testUserUUID, post.getPostUUID()));
        assertTrue(dm.getHiddenPosts(testUserUUID).contains(post));
        assertTrue(dm.unhidePost(testUserUUID, post.getPostUUID()));
        assertFalse(dm.getHiddenPosts(testUserUUID).contains(post));

        dm.deletePost(post.getPostUUID());
    }
}