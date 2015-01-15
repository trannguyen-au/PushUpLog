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
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false)
    private java.util.Date logDate;
    @DatabaseField(canBeNull = false)
    private int countPushUps;
    @DatabaseField
    private int countTime; // time in seconds



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

    public int getCountTime() {
        return countTime;
    }

    public void setCountTime(int countTime) {
        this.countTime = countTime;
    }
}
