package mobile.wnext.pushupsdiary.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

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

}
