package spring.view;

import java.io.File;

public class VideoForm {
    String title;
    String description;
    int version;
    String license;
    String downloadurl;
    File thumbnailFile;
    int[] tags; 
    int userid;

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
     * @return the thumbnailFile
     */
    public File getThumbnailFile() {
        return thumbnailFile;
    }

    /**
     * @param thumbnailFile the thumbnailFile to set
     */
    public void setThumbnailFile(File thumbnailFile) {
        this.thumbnailFile = thumbnailFile;
    }

    /**
     * @return the tags
     */
    public int[] getTags() {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(int[] tags) {
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
