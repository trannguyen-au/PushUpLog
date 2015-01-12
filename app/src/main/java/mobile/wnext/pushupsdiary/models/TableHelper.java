package mobile.wnext.pushupsdiary.models;

import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.support.ConnectionSource;

/**
 * Created by Nnguyen on 12/01/2015.
 */
public abstract class TableHelper {
    //protected abstract class getTheClass();

    public abstract void onCreate(SQLiteDatabase db, ConnectionSource connectionSource);
    public abstract void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion);
}
