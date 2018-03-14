package spring.view;

public class Video {
    public int id;
    public String title;
    public NotTVUser author;

    public Video(int id, String title, NotTVUser author) {
        this.id = id;
	this.title = title;
        this.author = author;
    }
}
