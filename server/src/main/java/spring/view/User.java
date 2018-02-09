package spring.view;

public class User {
    String username;
    String userProfileURL;
    String thumbnailURL;
    
    
    public User(String username, String userProfileURL, String thumbnailURL) {
        super();
        this.username = username;
        this.userProfileURL = userProfileURL;
        this.thumbnailURL = thumbnailURL;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getUserProfileURL() {
        return userProfileURL;
    }
    public void setUserProfileURL(String userProfileURL) {
        this.userProfileURL = userProfileURL;
    }
    public String getThumbnailURL() {
        return thumbnailURL;
    }
    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }
}
