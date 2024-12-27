import java.io.Serializable;

public class Post implements Serializable {
    private String postUUID;
    private String creatorUUID;
    private boolean commentsEnabled;
    private int likes;
    private int dislikes;
    private String content;

    public Post(String creatorUUID, String postUUID, boolean commentsEnabled, int likes, int dislikes, String content) {
        this.postUUID = postUUID;
        this.creatorUUID = creatorUUID;
        this.commentsEnabled = commentsEnabled;
        this.likes = likes;
        this.dislikes = dislikes;
        this.content = content;
    }

    public String getPostUUID() {
        return postUUID;
    }

    public String getCreatorUUID() {
        return creatorUUID;
    }

    public boolean isCommentEnabled() {
        return commentsEnabled;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return this.postUUID.equals(post.postUUID);
    }

    @Override
    public String toString() {
        return "Post1{" +
                "postUUID='" + postUUID + '\'' +
                ", creatorUUID='" + creatorUUID + '\'' +
                ", commentEnabled=" + commentsEnabled +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", content='" + content + '\'' +
                '}';
    }
}
