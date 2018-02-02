package spring.model;

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

    public VideoData(String title, String description, int userid,  String username) {
	this.title = title;
	this.description = description;
	this.userid = userid;
	this.username = username;
    }
    
    /**
     * @return the id
     */
    public int getId() {
	return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
	this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
	return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
	this.title = title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
	return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
	this.description = description;
    }

    /**
     * @return the version
     */
    public int getVersion() {
	return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(int version) {
	this.version = version;
    }

    /**
     * @return the filetype
     */
    public String getFiletype() {
	return filetype;
    }

    /**
     * @param filetype the filetype to set
     */
    public void setFiletype(String filetype) {
	this.filetype = filetype;
    }

    /**
     * @return the license
     */
    public String getLicense() {
	return license;
    }

    /**
     * @param license the license to set
     */
    public void setLicense(String license) {
	this.license = license;
    }

    /**
     * @return the downloadurl
     */
    public String getDownloadurl() {
	return downloadurl;
    }

    /**
     * @param downloadurl the downloadurl to set
     */
    public void setDownloadurl(String downloadurl) {
	this.downloadurl = downloadurl;
    }

    /**
     * @return the thumbnailurl
     */
    public String getThumbnailurl() {
	return thumbnailurl;
    }

    /**
     * @param thumbnailurl the thumbnailurl to set
     */
    public void setThumbnailurl(String thumbnailurl) {
	this.thumbnailurl = thumbnailurl;
    }

    /**
     * @return the userid
     */
    public int getUserid() {
	return userid;
    }
    
    public String getUsername(){
        return username;
    }

    /**
     * @param userid the userid to set
     */
    public void setUserid(int userid) {
	this.userid = userid;
    }
}

