package mobile.wnext.pushupsdiary.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Wery7 on 4/1/2015.
 */
@DatabaseTable
public class TrainingSet {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "training_log_id", canBeNull = false)
    private TrainingLog trainingLog;
    @DatabaseField(canBeNull = false)
    private int sequence;
    @DatabaseField(canBeNull = false)
    private int count;
    @DatabaseField(canBeNull = false)
    private long time;
    @DatabaseField(canBeNull = false)
    private java.util.Date startDate;
    @DatabaseField
    private java.util.Date endDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TrainingLog getTrainingLog() {
        return trainingLog;
    }

    public int getSequence() {
        return sequence;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public static class Builder {
        private final TrainingLog mTrainingLog;
        private final int mSequence;


        private Date mStartDate = new Date();
        private Date mEndDate = new Date();
        private int mCount = 0;
        private int mTime = 0;
        public Builder(TrainingLog trainingLog, int sequence) {
            mTrainingLog = trainingLog;
            mSequence = sequence;
        }
        public Builder startDate(Date val) {
            mStartDate = val;
            return this;
        }
        public Builder endDate(Date val) {
            mEndDate = val;
            return this;
        }
        public Builder count(int val) {
            mCount = val;
            return this;
        }
        public Builder time(int val) {
            mTime = val;
            return this;
        }
        public TrainingSet build() {
            return new TrainingSet(this);
        }
    }

    private TrainingSet(Builder builder) {
        startDate = builder.mStartDate;
        endDate = builder.mEndDate;
        count = builder.mCount;
        time = builder.mTime;
        trainingLog = builder.mTrainingLog;
        sequence = builder.mSequence;
    }
    public TrainingSet() {}
}
