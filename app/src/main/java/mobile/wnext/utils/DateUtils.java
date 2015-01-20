package mobile.wnext.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import mobile.wnext.pushupsdiary.Constants;

/**
 * Created by Nnguyen on 20/01/2015.
 */
public class DateUtils {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public static int DayDifferent(Date from, Date to) {
        from = dateOnly(from);
        to = dateOnly(to);
        long second = (to.getTime() - from.getTime())/1000;
        return (int) (second / 86400);
    }

    public static Date dateOnly(Date input) {
        try {
            return sdf.parse(sdf.format(input));
        }
        catch (Exception ex) {
            Log.e("WN-Utilities","Can't strip time component from date "+sdf.format(input));
            return null;
        }
    }
}
