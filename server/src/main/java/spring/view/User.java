package spring.view;

public class User {
    int userID;
    String username;
    String userProfileURL;
    String thumbnailURL;
    
    public User(int userID, String username, String userProfileURL, String thumbnailURL) {
        super();
        this.userID = userID;
        this.username = username;
        this.userProfileURL = userProfileURL;
        this.thumbnailURL = thumbnailURL;
    }
    
    public int getUserID() {
        return userID;
    }
    
    public void setUserID(int userID) {
        this.userID = userID;
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
