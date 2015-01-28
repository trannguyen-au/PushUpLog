package mobile.wnext.pushupsdiary.models;

import java.util.Date;

/**
 * Created by Nnguyen on 20/01/2015.
 */
public class TrainingLogChartSummary {
    private Date dateTimeStart;
    private int totalCount;
    private int totalTime;
    public double getRate() {
        return ((double)totalCount) / ((double) totalTime);
    }

    public Date getDateTimeStart() {
        return dateTimeStart;
    }

    public void setDateTimeStart(Date dateTimeStart) {
        this.dateTimeStart = dateTimeStart;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public void addCount(int count) {
        totalCount += count;
    }

    public void addTime(int time) {
        totalTime += time;
    }

    public TrainingLogChartSummary() {
        totalCount = 0;
        totalTime = 0;
    }
}
