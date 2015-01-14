package mobile.wnext.pushupsdiary.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Created by Nnguyen on 12/01/2015.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "PushUpsDiary";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        PracticeLogHelper.getInstance(this).onCreate(database,connectionSource);
        TrainingLogHelper.getInstance(this).onCreate(database,connectionSource);
        TrainingSetHelper.getInstance(this).onCreate(database,connectionSource);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        PracticeLogHelper.getInstance(this).onUpgrade(database,connectionSource,oldVersion,newVersion);
        TrainingLogHelper.getInstance(this).onUpgrade(database,connectionSource,oldVersion,newVersion);
        TrainingSetHelper.getInstance(this).onUpgrade(database,connectionSource,oldVersion,newVersion);
    }

    public PracticeLogHelper getPracticeLogHelper() {
        return PracticeLogHelper.getInstance(this);
    }
    public TrainingLogHelper getTrainingLogHelper() {
        return TrainingLogHelper.getInstance(this);
    }
    public TrainingSetHelper getTrainingSetHelper() {
        return TrainingSetHelper.getInstance(this);
    }
}
