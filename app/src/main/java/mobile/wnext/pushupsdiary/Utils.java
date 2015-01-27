package mobile.wnext.pushupsdiary;

/**
 * Created by Nnguyen on 15/01/2015.
 */
public class Utils {
    public static String getDisplayTime(long currentTime) {
        return getDisplayTime(currentTime, "%01d:%02d:%03d");
    }

    public static String getDisplayTime(long currentTime, String customFormat) {
        long seconds = currentTime / 1000;
        long ticks = currentTime % 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format(customFormat,minutes,seconds,ticks);
    }

    public static String getHourAndMinuteTime(long timeSpan) {
        long minutes = timeSpan / 60000;
        long hours = minutes / 60;
        long remainingMinutes = minutes %60;

        return String.format("%dh%02dm",hours, minutes);
    }
}
