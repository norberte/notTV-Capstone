package spring.view;

public class Video {
    public String title;
    public String thumbnail;
    public String url;
    public String author;
    public String authorUrl;

    public Video(String title, String thumbnail, String url, String author, String authorUrl) {
	this.title = title;
	this.thumbnail = thumbnail;
	this.url = url;
	this.author = author;
	this.authorUrl = authorUrl;
    }
}
