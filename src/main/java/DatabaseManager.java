import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/social_media_database";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root123";

    private Connection connection;

    public DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public User login(String username, String password) {
        String query = "SELECT user_UUID FROM user WHERE username = ? AND password = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }
            return getUser(rs.getString("user_UUID"));
        } catch (SQLException e) {
            return null;
        }

    }

    public User createUser(String username, String password) {
        Random rand = new Random();
        String userUUID = java.util.UUID.randomUUID().toString();
        String query = "INSERT INTO user (user_UUID, username, password, bio, pfp) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // set values for placeholders
            stmt.setString(1, userUUID);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.setString(4, String.format("Hello, my name is %s", username));
            stmt.setString(5, String.valueOf(rand.nextInt(10)));

            if(stmt.executeUpdate() > 0) {
                return getUser(userUUID);
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public Post createPost(String creatorUUID, String content) {
        String postUUID = java.util.UUID.randomUUID().toString();
        String query = "INSERT INTO post (creator_UUID, post_UUID, content) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // set values for placeholders
            stmt.setString(1, creatorUUID);
            stmt.setString(2, postUUID);
            stmt.setString(3, content);

            if (stmt.executeUpdate() > 0) {
                return getPost(postUUID);
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public Comment createComment(String creatorUUID, String postUUID, String content) {
        String commentUUID = java.util.UUID.randomUUID().toString();
        String query = "INSERT INTO comment (creator_UUID, post_UUID, comment_UUID, content) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // set values for placeholders
            stmt.setString(1, creatorUUID);
            stmt.setString(2, postUUID);
            stmt.setString(3, commentUUID);
            stmt.setString(4, content);

            if (stmt.executeUpdate() > 0) {
                return getComment(commentUUID);
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }

    }

    public boolean editComment(String commentUUID, String newContent) {
        String query = "UPDATE comment SET content = ? WHERE comment_UUID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newContent);
            stmt.setString(2, commentUUID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean editPost(String postUUID, String newContent, boolean enabled) {
        String query = "UPDATE post SET content = ?, comments_enabled = ? WHERE post_UUID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newContent);
            stmt.setBoolean(2, enabled);
            stmt.setString(3, postUUID);
            boolean commentsEnabled = enableDisableComments(postUUID, enabled);
            return stmt.executeUpdate() > 0 || commentsEnabled;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean editUser(String userUUID, String newUsername, String newBio, int newPfp) {
        String query = "UPDATE user SET username = ?, bio = ?, pfp = ? WHERE user_UUID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newUsername);
            stmt.setString(2, newBio);
            stmt.setString(3, String.valueOf(newPfp));
            stmt.setString(4, userUUID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean addPostLikeDislike(String userUUID, String postUUID, int reactionType) {
        String query = "INSERT INTO post_likes_dislikes VALUES(?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userUUID);
            stmt.setString(2, postUUID);
            stmt.setString(3, String.valueOf(reactionType));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean removePostLikeDislike(String userUUID, String postUUID) {
        String query = "DELETE FROM post_likes_dislikes WHERE user_UUID = ? AND post_UUID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userUUID);
            stmt.setString(2, postUUID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean addCommentLikeDislike(String userUUID, String commentUUID, int reactionType) {
        String query = "INSERT INTO comment_likes_dislikes VALUES(?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userUUID);
            stmt.setString(2, commentUUID);
            stmt.setString(3, String.valueOf(reactionType));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean removeCommentLikeDislike(String userUUID, String commentUUID) {
        String query = "DELETE FROM comment_likes_dislikes WHERE user_UUID = ? AND comment_UUID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userUUID);
            stmt.setString(2, commentUUID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deletePost(String postUUID) {
        String query = "DELETE FROM post WHERE post_UUID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, postUUID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteComment(String commentUUID) {
        String query = "DELETE FROM comment WHERE comment_UUID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, commentUUID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean hidePost(String userUUID, String postUUID) {
        String query = "INSERT INTO hidden_posts (user_UUID, post_UUID) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userUUID);
            stmt.setString(2, postUUID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean unhidePost(String userUUID, String postUUID) {
        String query = "DELETE FROM hidden_posts WHERE user_UUID = ? AND post_UUID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userUUID);
            stmt.setString(2, postUUID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean enableDisableComments(String postUUID, boolean enable) {
        String query = "UPDATE post SET comments_enabled = ? WHERE post_UUID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, enable);
            stmt.setString(2, postUUID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public ArrayList<Post> getHiddenPosts(String userUUID) {
        ArrayList<Post> output = new ArrayList<>();
        String query = "SELECT * FROM post WHERE post_UUID IN (SELECT post_UUID FROM hidden_posts WHERE user_UUID = ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userUUID);

            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    output.add(getPost(rs.getString("post_UUID")));
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return output;
    }

    public ArrayList<User> searchByUsername (String username) {
        ArrayList<User> output = new ArrayList<>();
        String query = "SELECT user_UUID FROM user WHERE LOWER(username) LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + username + "%");

            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    output.add(getUser(rs.getString("user_UUID")));
                }
            }
        } catch (SQLException e) {
            return new ArrayList<>();
        }
        return output;
    }

    public ArrayList<Post> getUserPosts(String userUUID) {
        ArrayList<Post> output = new ArrayList<>();
        String query = "SELECT post_UUID FROM post WHERE creator_UUID = ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userUUID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                output.add(getPost(rs.getString("post_UUID")));
            }
        } catch (SQLException e) {
            return new ArrayList<>();
        }
        return output;
    }

    public ArrayList<Comment> getPostComments(String postUUID) {
        String query = "SELECT comment_UUID FROM comment WHERE post_UUID = ?";
        ArrayList<Comment> output = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, postUUID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                output.add(getComment(rs.getString("comment_UUID")));
            }
        } catch (SQLException e) {
            return new ArrayList<>();
        }
        return output;
    }

    public ArrayList<User> getInbox(String receiverUUID) {
        String query = "SELECT sender_UUID FROM inbox WHERE receiver_UUID = ?";
        ArrayList<User> output = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, receiverUUID);

            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    output.add(getUser(rs.getString("sender_UUID")));
                }
            }
        } catch (SQLException e) {
            return new ArrayList<>();
        }
        return output;
    }

    // returns 1 if liked, -1 if disliked, 0 if neither
    public int inPostLikedDislikedUsers(String userUUID, String postUUID) {
        String query = "SELECT reaction_type FROM post_likes_dislikes " +
                "WHERE user_UUID = ? AND post_UUID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userUUID);
            stmt.setString(2, postUUID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("reaction_type");
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    // returns 1 if liked, -1 if disliked, 0 if neither
    public int inCommentLikedDislikedUsers(String userUUID, String commentUUID) {
        String query = "SELECT reaction_type FROM comment_likes_dislikes " +
                "WHERE user_UUID = ? AND comment_UUID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userUUID);
            stmt.setString(2, commentUUID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("reaction_type");
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    public User getUser(String userUUID) {
        String query = "SELECT * FROM user WHERE user_UUID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userUUID);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                return null;
            }

            String username = rs.getString("username");
            String password = rs.getString("password");
            String bio = rs.getString("bio");
            int pfp = rs.getInt("pfp");
            int likes = rs.getInt("likes");

            return new User(userUUID, username, password, bio, pfp, likes);
        } catch (SQLException e) {
             return null;
        }
    }

    public Post getPost(String postUUID) {
        String query = "SELECT * FROM post WHERE post_UUID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, postUUID);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                return null;
            }

            String creatorUUID = rs.getString("creator_UUID");
            boolean commentsEnabled = rs.getBoolean("comments_enabled");
            int likes = rs.getInt("likes");
            int dislikes = rs.getInt("dislikes");
            String content = rs.getString("content");

            return new Post(creatorUUID, postUUID, commentsEnabled, likes, dislikes, content);
        } catch (SQLException e) {
            return null;
        }
    }

    public Comment getComment(String commentUUID) {
        String query = "SELECT * FROM comment WHERE comment_UUID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, commentUUID);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                return null;
            }

            String creatorUUID = rs.getString("creator_UUID");
            String postUUID = rs.getString("post_UUID");
            int likes = rs.getInt("likes");
            int dislikes = rs.getInt("dislikes");
            String content = rs.getString("content");

            return new Comment(creatorUUID, postUUID, commentUUID, likes, dislikes, content);
        } catch (SQLException e) {
            return null;
        }

    }

    public boolean sendFriendRequest(String senderUUID, String receiverUUID) {
        String query = "INSERT INTO inbox (receiver_UUID, sender_UUID) VALUES(?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // set values for placeholders
            stmt.setString(1, receiverUUID);
            stmt.setString(2, senderUUID);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean unsendFriendRequest(String senderUUID, String receiverUUID) {
        String query = "DELETE FROM inbox WHERE sender_UUID = ? AND receiver_UUID = ?";

        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, senderUUID);
            stmt.setString(2, receiverUUID);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean removeFriend(String userUUID, String otherUUID) {
        String query = "DELETE FROM relationship WHERE ((user_UUID = ? AND other_UUID = ?) " +
                "OR (user_UUID = ? AND other_UUID = ?)) AND type = 1";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userUUID);
            stmt.setString(2, otherUUID);
            stmt.setString(3, otherUUID);
            stmt.setString(4, userUUID);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean acceptFriendRequest(String senderUUID, String receiverUUID) {
        removeInboxRequest(senderUUID, receiverUUID);
        removeInboxRequest(receiverUUID, senderUUID);

        String query = "INSERT INTO relationship VALUES(?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, senderUUID);
            stmt.setString(2, receiverUUID);
            stmt.setString(3, String.valueOf(1));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // 2 functions -> ignore friend request and remove from both user's inboxes when accepting friend request
    public boolean removeInboxRequest(String senderUUID, String receiverUUID) {
        String query = "DELETE FROM inbox WHERE receiver_UUID = ? AND sender_UUID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, receiverUUID);
            stmt.setString(2, senderUUID);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // blocks are single directional
    public boolean blockUser(String userUUID, String blockUUID) {
        removeFriend(userUUID, blockUUID);
        String query = "INSERT INTO relationship VALUES(?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userUUID);
            stmt.setString(2, blockUUID);
            stmt.setString(3, String.valueOf(-1));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean unblockUser(String userUUID, String blockUUID) {
        String query = "DELETE FROM relationship WHERE user_UUID = ? AND other_UUID = ? AND type = -1";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userUUID);
            stmt.setString(2, blockUUID);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // friendships are bidirectional
    public ArrayList<User> getFriendList(String userUUID) {
        String query = "SELECT CASE WHEN user_UUID = ? THEN other_UUID ELSE user_UUID " +
                "END AS friend_UUID FROM relationship WHERE (user_UUID = ? OR other_UUID = ?)" +
                "AND type = 1";

        ArrayList<User> output = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userUUID);
            stmt.setString(2, userUUID);
            stmt.setString(3, userUUID);

            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    output.add(getUser(rs.getString("friend_UUID")));
                }
            }
        } catch (SQLException e) {
            return new ArrayList<>();
        }
        return output;
    }

    public ArrayList<User> getBlockedList(String userUUID) {
        String query = "SELECT other_UUID FROM relationship WHERE user_UUID = ? AND type = -1";
        ArrayList<User> output = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userUUID);

            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    output.add(getUser(rs.getString("other_UUID")));
                }
            }
        } catch (SQLException e) {
            return new ArrayList<>();
        }
        return output;
    }

    public ArrayList<Post> genFeed(String userUUID) {
        ArrayList<Post> output = new ArrayList<>();
        ArrayList<Post> hiddenPosts = getHiddenPosts(userUUID);
        for (User friend : getFriendList(userUUID)) {
            for (Post post : getUserPosts(friend.getUserUUID())) {
                if (!hiddenPosts.contains(post)) {
                    output.add(post);
                }
            }
        }
        return output;
    }

    public Connection getConnection() {
        return connection;
    }
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
