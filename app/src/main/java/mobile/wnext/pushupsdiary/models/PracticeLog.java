package mobile.wnext.pushupsdiary.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.List;

/**
 * Created by Wery7 on 4/1/2015.
 */
@DatabaseTable
public class PracticeLog {
    public static final String TABLE_NAME = "practicelog";

    public static final String COL_ID = "id";
    public static final String COL_LOG_DATE = "logDate";
    public static final String COL_COUNT_PUSH_UPS = "countPushUps";
    public static final String COL_TOTAL_TIME = "countTime";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false)
    private java.util.Date logDate;
    @DatabaseField(canBeNull = false)
    private int countPushUps;
    @DatabaseField
    private long countTime; // time in millisecond



    public static List<PracticeLog> GetAll() {
        //ConnectionSource connectionSource = new AndroidConnectionSource()
        return null;
        //OpenHelperManager
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public int getCountPushUps() {
        return countPushUps;
    }

    public void setCountPushUps(int countPushUps) {
        this.countPushUps = countPushUps;
    }

    public long getCountTime() {
        return countTime;
    }

    public void setCountTime(long countTime) {
        this.countTime = countTime;
    }
}
