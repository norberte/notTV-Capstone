package spring.view;

public class Video {
    public int id;
    public String title;
    public String url;
    public NotTVUser author;

    public Video(int id, String title, String url, NotTVUser author) {
        this.id = id;
	this.title = title;
	this.url = url;
        this.author = author;
    }
}
