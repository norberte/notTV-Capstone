package spring;

public class VideoData {

    private final long id;
    public final String title;
    
    public VideoData(long id, String title){
        this.id = id;
        this.title = title;
    }
    public long getId(){
        return id;
    }
    public String getTitle(){
        return title;
    }
}
