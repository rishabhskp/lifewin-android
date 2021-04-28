package app.lifewin.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import app.lifewin.preferences.MySharedPreference;

/**
 * Created by Vivek on 9/27/2015.
 */
public class AppUtils {

    public static void getWeekFirstDate(Context mContext) {
        //TODO fetch weekly points.
        String firstDay = MySharedPreference.getInstance(mContext).getFirstDay();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String todayDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
        if (firstDay.equalsIgnoreCase(todayDay)) {
            //TODO Week First Day...
            MySharedPreference.getInstance(mContext).setWeekStartingTime(getCurrentDateInMillies());
        } else {
            Calendar cal = Calendar.getInstance();

            int weekDay = 0;
            if (firstDay.equalsIgnoreCase("Saturday")) {
                weekDay = Calendar.SATURDAY;
            } else if (firstDay.equalsIgnoreCase("Sunday")) {
                weekDay = Calendar.SUNDAY;
            } else if (firstDay.equalsIgnoreCase("Monday")) {
                weekDay = Calendar.MONDAY;
            }

            while (cal.get(Calendar.DAY_OF_WEEK) != weekDay)
                cal.add(Calendar.DAY_OF_WEEK, -1);
            MySharedPreference.getInstance(mContext).setWeekStartingTime(cal.getTimeInMillis());

        }

    }

    public static long getCurrentDateInMillies() {
        long currentDateInMillies = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String date = sdf.format(new Date());
        try {
            SimpleDateFormat sdfM = new SimpleDateFormat("MM/dd/yyyy");
            Date dateM = sdfM.parse(date);
            currentDateInMillies = dateM.getTime();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            currentDateInMillies = System.currentTimeMillis();
        }
        return currentDateInMillies;
    }



    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String date = sdf.format(new Date());
        return date;
    }


    public static String convertMillisToDate(long time) {
        Date mDate = new Date();
        mDate.setTime(time);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String date = sdf.format(mDate);
        return date;
    }

    public static long convertDateInMillis(@NonNull String mdate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date date = sdf.parse(mdate);
            return date.getTime();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }

}
