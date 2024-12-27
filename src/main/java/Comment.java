import java.io.Serializable;

public class Comment implements Serializable {
    private String creator_UUID;
    private String post_UUID;
    private String comment_UUID;
    private int likes;
    private int dislikes;
    private String content;

    public Comment(String creator_UUID, String post_UUID, String comment_UUID,
                   int likes, int dislikes, String content) {
        this.creator_UUID = creator_UUID;
        this.post_UUID = post_UUID;
        this.comment_UUID = comment_UUID;
        this.likes = likes;
        this.dislikes = dislikes;
        this.content = content;
    }public String getCreator_UUID() {
        return creator_UUID;
    }

    public String getPost_UUID() {
        return post_UUID;
    }

    public String getComment_UUID() {
        return comment_UUID;
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

    public void setCreator_UUID(String creator_UUID) {
        this.creator_UUID = creator_UUID;
    }

    public void setPost_UUID(String post_UUID) {
        this.post_UUID = post_UUID;
    }

    public void setComment_UUID(String comment_UUID) {
        this.comment_UUID = comment_UUID;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Comment1{" +
                "creator_UUID='" + creator_UUID + '\'' +
                ", post_UUID='" + post_UUID + '\'' +
                ", comment_UUID='" + comment_UUID + '\'' +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", content='" + content + '\'' +
                '}';
    }
}
