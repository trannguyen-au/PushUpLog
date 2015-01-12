package mobile.wnext.pushupsdiary.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Wery7 on 4/1/2015.
 */
@DatabaseTable
public class TrainingLog {
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField
    public java.util.Date dateTimeStart;
    @DatabaseField(canBeNull = false)
    public int totalCount;
    @DatabaseField(canBeNull = false)
    public int totalTime;
    @ForeignCollectionField
    public ForeignCollection<TrainingSet> trainingSets;
}
