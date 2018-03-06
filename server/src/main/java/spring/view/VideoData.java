package spring.view;

public class VideoData {
    int id;
    String title;
    String description;
    int version;
    String filetype;
    String license;
    String downloadurl;
    String thumbnailurl;
    int userid;
    String username;
    boolean subscribed;
    
    public VideoData(int id, String title, String description, int userid,  String username, boolean subscribed) {
        this.id = id;
	this.title = title;
	this.description = description;
	this.userid = userid;
	this.username = username;
	this.subscribed = subscribed;
    }
    
    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public int getVersion() {
        return version;
    }
    public String getFiletype() {
        return filetype;
    }
    public String getLicense() {
        return license;
    }
    public String getDownloadurl() {
        return downloadurl;
    }
    public String getThumbnailurl() {
        return thumbnailurl;
    }
    public int getUserid() {
        return userid;
    }
    public String getUsername(){
        return username;
    }
    public boolean getSubscribed(){
        return subscribed;
    }
    
}

