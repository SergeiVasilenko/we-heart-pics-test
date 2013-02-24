package ru.sergeyvasilenko.weheartpics.interview.utils;

import android.content.Context;
import android.content.res.Resources;
import ru.sergeyvasilenko.weheartpics.interview.R;

/**
 * User: Serg
 * Date: 24.02.13
 * Time: 14:46
 */
public class Time2String {
    private final static long MINUTE = 60*1000;
    private final static long HOUR = 60*MINUTE;
    private final static long DAY = 24*HOUR;

    public static String getApproximateTimeString(Context context, long difference){
        if(difference < 0) throw new IllegalArgumentException("difference must be > 0");
        Resources res = context.getResources();

        StringBuilder timeString = new StringBuilder();

        if(difference < MINUTE) timeString.append(res.getString(R.string.time_less_than_1_m));
        else if(difference >= MINUTE && difference < HOUR ) {
            timeString.append(difference/MINUTE)
                    .append(" ")
                    .append(res.getString(R.string.time_minute));
        }
        else if(difference >= HOUR && difference < DAY) {
            timeString.append(difference/HOUR)
                    .append(" ")
                    .append(res.getString(R.string.time_hour));
        }
        else if(difference >= DAY) {
            timeString.append(difference/DAY)
                    .append(" ")
                    .append(res.getString(R.string.time_day));
        }
        timeString.append(" ")
                .append(res.getString(R.string.time_ago));
        return timeString.toString();
    }
}
