import java.io.Serializable;

public class User implements Serializable {
    private String userUUID;
    private String username;
    private String password;
    private String bio;
    private int pfp;
    private int likes;

    public User(String userUUID, String username, String password, String bio, int pfp, int likes) {
        this.userUUID = userUUID;
        this.username = username;
        this.password = password;
        this.bio = bio;
        this.pfp = pfp;
        this.likes = likes;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getBio() {
        return bio;
    }

    public int getPfp() {
        return pfp;
    }

    public int getLikes() {
        return likes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userUUID.equals(user.userUUID);
    }

    @Override
    public String toString() {
        return "User1{" +
                "userUUID='" + userUUID + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", bio='" + bio + '\'' +
                ", pfp=" + pfp +
                ", likes=" + likes +
                '}';
    }
}
