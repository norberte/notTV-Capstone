package spring.view;

import java.io.File;

public class VideoForm {
	File thumbnail;
    String title;
    String description;
    int version;
    String filetype;
    String license;
    String downloadurl;
    String thumbnailurl;
    String tags; // TEMPORARY
    int userid;

    
    /**
     * @return the thumbnail img
     */
    public File getThumbnail() {
		return thumbnail;
	}

    /**
     * @param thumbnail: thumbnail img file to set
     */
	public void setThumbnail(File thumbnail) {
		this.thumbnail = thumbnail;
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
     * @return the tags
     */
    public String getTags() {
	return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(String tags) {
	this.tags = tags;
    }

    /**
     * @return the userid
     */
    public int getUserid() {
	return userid;
    }

    /**
     * @param userid the userid to set
     */
    public void setUserid(int userid) {
	this.userid = userid;
    }
}
