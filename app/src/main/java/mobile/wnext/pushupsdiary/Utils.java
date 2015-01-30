package mobile.wnext.pushupsdiary;

import java.util.List;

import mobile.wnext.pushupsdiary.models.TrainingLogChartSummary;

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

    public static String getTimeSpanDisplay(long timeSpan) {
        long minutes = timeSpan / Constants.ONE_MINUTE;
        long hours = minutes / 60;

        if(hours > 0) {
            // display hour and minutes
            return String.format("%dh%02dm",hours, minutes);
        }
        else if(minutes > 0) {
            // display minutes and second
            long seconds = (timeSpan % Constants.ONE_MINUTE)/1000;
            return String.format("%02dm%02ds", minutes, seconds);
        }
        else {
            // display seconds only
            long seconds = timeSpan / 1000;
            return String.format("%02ds", seconds);
        }
    }

    public static void sortByDate(List<TrainingLogChartSummary> data) {
        for (int i=0;i<data.size()-1;i++) {
            for (int j=i;j<data.size();j++) {
                if(data.get(i).getDateTimeStart().compareTo(data.get(j).getDateTimeStart()) > 0) {
                    // swap i to j
                    TrainingLogChartSummary tmp = data.get(i);
                    data.set(i, data.get(j));
                    data.set(j, tmp);
                }
            }
        }
    }
}
