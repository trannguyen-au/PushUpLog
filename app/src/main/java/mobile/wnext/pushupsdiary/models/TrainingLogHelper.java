package mobile.wnext.pushupsdiary.models;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Nnguyen on 12/01/2015.
 */
public class TrainingLogHelper extends TableHelper {


    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(PracticeLogHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, TrainingLog.class);
        } catch (SQLException e) {
            Log.e(PracticeLogHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }
}
