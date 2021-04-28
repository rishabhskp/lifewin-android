package app.lifewin.model.app;


import java.util.ArrayList;

public class GoalBean {
    private String id;
    private String title;
    private int days;
    private long startTime;
    private long endTime;
    private long timeInMillis;
    private long createdTimeMillis;
    private String userId;
    private String type;
    private boolean isCompleted;

    private ArrayList<Long> listOfCompletedGoalsDate;


    public GoalBean(){}

    public GoalBean(String title){
        this.title=title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public long getCreatedTimeMillis() {
        return createdTimeMillis;
    }

    public void setCreatedTimeMillis(long createdTimeMillis) {
        this.createdTimeMillis = createdTimeMillis;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public ArrayList<Long> getListOfCompletedGoalsDate() {
        return listOfCompletedGoalsDate;
    }

    public void setListOfCompletedGoalsDate(ArrayList<Long> listOfCompletedGoalsDate) {
        this.listOfCompletedGoalsDate = listOfCompletedGoalsDate;
    }
}
