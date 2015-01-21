package mobile.wnext.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import mobile.wnext.pushupsdiary.Constants;

/**
 * Created by Nnguyen on 20/01/2015.
 */
public class DateUtils {

    private static final String TAG = "WN-Utilities";

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public static int DayDifferent(Date from, Date to) throws ParseException {
        try {
            from = dateOnly(from);
            to = dateOnly(to);
            long second = (to.getTime() - from.getTime())/1000;
            return (int) (second / 86400);
        }
        catch (ParseException ex) {
            Log.e(TAG,"Can't strip time component from date "+sdf.format(from)+" and "+sdf.format(to));
            throw ex;
        }
    }

    public static String format(Date date, String format) {
        sdf.applyPattern(format);
        return sdf.format(date);
    }

    public static String DMYFormat(Date date, String separator) {
        return format(date, "dd"+separator+"MM"+separator+"yyyy");
    }

    public static String YMDFormat(Date date,String separator) {
        return format(date, "yyyy"+separator+"MM"+separator+"dd");
    }

    public static Date dateOnly(Date input) throws ParseException {
        return sdf.parse(sdf.format(input));
    }

    public static Date parseDate(String string, String dateFormat)  {
        try {
            sdf.applyPattern(dateFormat);
            return sdf.parse(string);
        }
        catch (ParseException ex) {
            Log.e(TAG,"parseDate: "+string+" format: "+dateFormat);
            return null;
        }
    }


}
