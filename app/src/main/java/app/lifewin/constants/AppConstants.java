package app.lifewin.constants;

import android.os.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppConstants {

    public static final String CHILD_FOLDER_PROFILE_IMAGES = Environment.getExternalStorageDirectory() + "/.LifeWin";
    public final static String USER_SETTINGS = "user_settings";
    public static final String USERS_POINTS = "user_points";
    public static final String USERS_TASKS = "user_tasks";
    public static final String USERS_GOALS = "user_goals";
    /** This table is used for keeping the record of the goals completed days  */
    public static final String USERS_GOALS_STATUS = "user_goals_status";

    public static final String USERS_WIN_STREAK = "user_win_streak";
    public static int GOOGLE_PLUS_LOGIN_FLAG = 200;

    //Settings Table Fields
    public final static String POMODORO_METER = "pomodoro_meter";
    public final static String HOURLY_RATE = "hourly_rate";
    public final static String FIRST_DAY = "first_day";
    public final static String USER_ID = "user_id";

    public final static String[] STATUS_ARRAY=new String[]{"DONE","REMAINING","LATER"};
    public final static String[] GOAL_STATUS_ARRAY=new String[]{"PRESENT","FUTURE"};

    public final static long ONE_DAY_INTERVAL = 24 * 60 * 60 * 1000;

    public static HashMap<String,Object> mTaskHashMap=new HashMap<String,Object>();
    public static List<String> mTaskList=new ArrayList<String>();
    public static List<String> mGoalsList=new ArrayList<String>();

}
