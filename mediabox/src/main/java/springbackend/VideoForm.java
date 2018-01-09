package springbackend;

import javax.validation.constraints.Size;

public class VideoForm {
	@Size(min=2, max=50)
	private String title;
	@Size(max=500)
	private String description;
	private int version;
	@Size(max=4)
	private String fileType;
	private int author;
	@Size(max=25)
	private String language;
	@Size(max=25)
	private String city;
	@Size(max=25)
	private String country;
	@Size(max=25)
	private String license;
	@Size(max=500)
	private String tags;
	@Size(max=100)
	private String trackerFilePath;
	
	// 1-arg constructor
	public VideoForm(String title) {
		super();
		this.title = title;
	}
	
	// no-arg constructor
	public VideoForm() {
		super();
	}

	// all argument constructor
	public VideoForm(String title, String description, int version, String fileType, int author,
			String language, String city, String country, String license, String tags, String trackerFilePath) {
		super();
		this.title = title;
		this.description = description;
		this.version = version;
		this.fileType = fileType;
		this.author = author;
		this.language = language;
		this.city = city;
		this.country = country;
		this.license = license;
		this.tags = tags;
		this.trackerFilePath = trackerFilePath;
	}
	
	public String getTrackerFilePath() {
		return trackerFilePath;
	}

	public void setTrackerFilePath(String trackerFilePath) {
		this.trackerFilePath = trackerFilePath;
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

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "Video [title=" + title + ", description=" + description + ", version=" + version
				+ ", fileType=" + fileType + ", author=" + author + ", language=" + language + ", city=" + city
				+ ", country=" + country + ", license=" + license + ", tags=" + tags + "]";
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
