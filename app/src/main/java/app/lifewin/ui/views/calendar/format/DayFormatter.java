package app.lifewin.ui.views.calendar.format;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;

import app.lifewin.ui.views.calendar.CalendarDay;

/**
 * Supply labels for a given day. Default implementation is to format using a {@linkplain SimpleDateFormat}
 */
public interface DayFormatter {

    /**
     * Format a given day into a string
     *
     * @param day the day
     * @return a label for the day
     */
    @NonNull
    String format(@NonNull CalendarDay day);

    /**
     */
    public static final DayFormatter DEFAULT = new DateFormatDayFormatter();
}
