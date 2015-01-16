package mobile.wnext.pushupsdiary.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Wery7 on 4/1/2015.
 */
@DatabaseTable
public class TrainingLog {

    public static final String COL_ID = "id";
    public static final String COL_DATE_TIME_START = "dateTimeStart";
    public static final String COL_TOTAL_COUNT = "totalCount";
    public static final String COL_TOTAL_TIME = "totalTime";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private java.util.Date dateTimeStart;
    @DatabaseField(canBeNull = false)
    private int totalCount;
    @DatabaseField(canBeNull = false)
    private int totalTime;
    @ForeignCollectionField
    private ForeignCollection<TrainingSet> trainingSets;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public ForeignCollection<TrainingSet> getTrainingSets() {
        return trainingSets;
    }

    public static class Builder {
        private Date mDate = new Date();
        private int mCount = 0;
        private int mTime = 0;
        public Builder() {}
        public Builder startDate(Date val) {
            mDate = val;
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
        public TrainingLog build() {
            return new TrainingLog(this);
        }
    }

    private TrainingLog(Builder builder) {
        dateTimeStart = builder.mDate;
        totalCount = builder.mCount;
        totalTime = builder.mTime;
    }
    public TrainingLog(){}
}
