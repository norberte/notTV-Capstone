package spring.view;

public class Video {
    public int id;
    public String title;
    public String thumbnail;
    public String url;
    public NotTVUser user;

    public Video(int id, String title, String url, NotTVUser user) {
        this.id = id;
	this.title = title;
	this.url = url;
        this.user = user;
    }
}
