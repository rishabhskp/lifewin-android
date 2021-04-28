package app.lifewin.model.app;


public class TaskBean {
    private String title;
    private int points;
    private String id;
    private String date;
    private long timeInMillis;
    private long createdTimeInMillis;

    public TaskBean(){}
    public TaskBean(String title,int points){
        this.title=title;
        this.points=points;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTimeInMillies() {
        return timeInMillis;
    }

    public void setTimeInMillies(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public long getCreatedTimeInMillis() {
        return createdTimeInMillis;
    }

    public void setCreatedTimeInMillis(long createdTimeInMillis) {
        this.createdTimeInMillis = createdTimeInMillis;
    }
}
