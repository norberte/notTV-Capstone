package springbackend;

import java.io.File;

public class VideoForm {
    private File videoFile;
    private File thumbnailFile;
    private String title;
    private String description;
    private int version;
    private String fileType;
    private String userName;
    private int author;
    private String language;
    private String city;
    private String country;
    private String license;
    private String trackerURL;
    private String thumbnailURL;
    private String contentRating;

    // TO DO: add category variables


    // no-arg constructor
    public VideoForm() {
        super();
    }

    // 2-arg constructor
    public VideoForm(File video, File thumbnail) {
        super();
        this.videoFile = video;
        this.thumbnailFile = thumbnail;
    }

    // 2-arg constructor
    public VideoForm(File video, String title) {
        super();
        this.videoFile = video;
        this.title = title;
    }

    // all argument constructor
    public VideoForm(File videoFile, File thumbnailFile, String title, String description, int version, String fileType,
            String userName, int author, String language, String city, String country, String license,
            String trackerURL, String thumbnailURL, String contentRating) {
        super();
        this.videoFile = videoFile;
        this.thumbnailFile = thumbnailFile;
        this.title = title;
        this.description = description;
        this.version = version;
        this.fileType = fileType;
        this.userName = userName;
        this.author = author;
        this.language = language;
        this.city = city;
        this.country = country;
        this.license = license;
        this.trackerURL = trackerURL;
        this.thumbnailURL = thumbnailURL;
        this.contentRating = contentRating;
    }

    public File getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(File videoFile) {
        this.videoFile = videoFile;
    }

    public File getThumbnailFile() {
        return thumbnailFile;
    }

    public void setThumbnailFile(File thumbnailFile) {
        this.thumbnailFile = thumbnailFile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getAuthor() {
        return author;
    }

    public void setAuthor(int author) {
        this.author = author;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getTrackerURL() {
        return trackerURL;
    }

    public void setTrackerURL(String trackerURL) {
        this.trackerURL = trackerURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getContentRating() {
        return contentRating;
    }

    public void setContentRating(String contentRating) {
        this.contentRating = contentRating;
    }

    @Override
    public String toString() {
        return "VideoForm [title=" + title + ", description=" + description + ", version=" + version + ", fileType="
                + fileType + ", author=" + author + ", language=" + language + ", city=" + city + ", country=" + country
                + ", license=" + license + ", trackerURL=" + trackerURL + ", thumbnailURL=" + thumbnailURL
                + ", contentRating=" + contentRating + ", userName =" + userName +  "]";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /* temporary code describing the connection of fields from HTML form to VideoForm object
     * 
     * <form action="#" th:action="@{/videoSubmission}" th:object="${form}" method="post">
    	 for title add this --> th:field="*{title}"
         for description add this --> th:field="*{description}"
         for version add this --> th:field="*{version}"
         for fileType add this --> th:field="*{fileType}"
         for language add this --> th:field="*{language}"
         for city add this --> th:field="*{city}"
         for country add this --> th:field="*{country}"
         for license add this --> th:field="*{license}"
         for tags add this --> th:field="*{tags}"
    </form>
     * 
     */
}
