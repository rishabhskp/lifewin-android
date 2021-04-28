package app.lifewin.preferences;

import android.content.Context;
import android.content.SharedPreferences;


public class MySharedPreference {
    private static SharedPreferences.Editor editor;
    private static Context _context;
    private static SharedPreferences mPreferences;
    private static final String PREF_NAME = "LifeWin";
    private static MySharedPreference uniqInstance;
    private static final int PRIVATE_MODE = 0;

    /**
     * Private Constructor for not allowing other classes to instantiate this
     * class
     */
    private MySharedPreference() {

    }

    /**
     * @param context of the class calling this method
     * @return instance of this class This method is the global point of access
     * for getting the only one instance of this class
     */
    public static synchronized MySharedPreference getInstance(Context context) {
        if (uniqInstance == null) {
            _context = context;
            mPreferences = _context.getSharedPreferences(PREF_NAME,
                    PRIVATE_MODE);
            editor = mPreferences.edit();
            uniqInstance = new MySharedPreference();
        }
        return uniqInstance;
    }



    public static  MySharedPreference getInstance() {
        return uniqInstance;
    }
    public void setDeviceToken(String deviceToken) {
        editor.putString("device_token", deviceToken);
        editor.commit();
    }

    public String getDeviceToken() {
        return mPreferences.getString("device_token", "");
    }


    public boolean getLogin() {
        return mPreferences.getBoolean("is_login", false);
    }

    public void setLogin(boolean is_login) {
        editor.putBoolean("is_login", is_login);
        editor.commit();
    }

    public void setUserId(String value) {
        editor.putString("user_id", value);
        editor.commit();
    }

    public String getUserId() {
        return mPreferences.getString("user_id", "");
    }

    public void setUserProfileImage(String value) {
        editor.putString("user_image", value);
        editor.commit();
    }

    public String getUserProfileImageF() {
        return mPreferences.getString("image", "");
    }

    public void setUserProfileImageF(String value) {
        editor.putString("image", value);
        editor.commit();
    }

    public String getUserProfileImage() {
        return mPreferences.getString("user_image", "");
    }

    public String getName() {
        return mPreferences.getString("name", "");
    }

    public void setName(String value) {
        editor.putString("name", value);
        editor.commit();
    }

    public void setFirstDay(String firstDay) {
        editor.putString("first_day", firstDay);
        editor.commit();
    }

    public String getFirstDay() {
        return mPreferences.getString("first_day", "Monday");
    }

    public void setHourlyRate(String firstDay) {
        editor.putString("hourly_rate", firstDay);
        editor.commit();
    }

    public String getHourlyRate() {
        return mPreferences.getString("hourly_rate", "10");
    }


    public void setPomodoroMeter(String firstDay) {
        editor.putString("pomodoro_meter", firstDay);
        editor.commit();
    }

    public String getPomodoroMeter() {
        return mPreferences.getString("pomodoro_meter", "25m/5m");
    }


    public void setLastSyncDate(String date) {
        editor.putString("sync_date", date);
        editor.commit();
    }

    public String getLastSyncDate() {
        return mPreferences.getString("sync_date", "2000/01/01");
    }

    public void setPointsSyncDatabase(boolean isSync) {
        editor.putBoolean("points_sync_data", isSync);
        editor.commit();
    }

    public boolean getPointsSyncDatabase() {
        return mPreferences.getBoolean("points_sync_data", false);
    }


    public void setTaskSyncDatabase(boolean isSync) {
        editor.putBoolean("task_sync_data", isSync);
        editor.commit();
    }

    public boolean getTaskSyncDatabase() {
        return mPreferences.getBoolean("task_sync_data", false);
    }

    public void setGoalsSyncDatabase(boolean isSync) {
        editor.putBoolean("goal_sync_data", isSync);
        editor.commit();
    }

    public boolean getGoalsSyncDatabase() {
        return mPreferences.getBoolean("goal_sync_data", false);
    }

    public void setWinStreakSyncDatabase(boolean isSync) {
        editor.putBoolean("winstreak_sync_data", isSync);
        editor.commit();
    }

    public boolean getWinStreakSyncDatabase() {
        return mPreferences.getBoolean("winstreak_sync_data", false);
    }

    public void setCompletedTaskSyncDatabase(boolean isSync) {
        editor.putBoolean("completed_task_sync_data", isSync);
        editor.commit();
    }

    public boolean getCompletedTaskSyncDatabase() {
        return mPreferences.getBoolean("completed_task_sync_data", false);
    }

    public void setTodayPoints(int points) {
        editor.putInt("today_points", points);
        editor.commit();
    }

    public int getTodayPoints() {
        return mPreferences.getInt("today_points", 0);
    }


    public void setWeeklyPoints(int points) {
        editor.putInt("weekly_points", points);
        editor.commit();
    }

    public int getWeeklyPoints() {
        return mPreferences.getInt("weekly_points", 0);
    }

    public void resetAll() {
        editor.clear();
        editor.commit();
    }

    public void setWeekStartingTime(long timeInMillis) {
        editor.putLong("week_first_day_time", timeInMillis);
        editor.commit();
    }

    public long getWeekStartingTime() {
        return mPreferences.getLong("week_first_day_time", 0);
    }

}
