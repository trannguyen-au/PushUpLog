package mobile.wnext.pushupsdiary.models;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class PracticeLogHelper extends TableHelper {

    // the DAO object we use to access the SimpleData table
    private Dao<PracticeLog, Integer> simpleDao = null;
    private RuntimeExceptionDao<PracticeLog, Integer> simpleRuntimeDao = null;

    private OrmLiteSqliteOpenHelper dbHelper;

    public static PracticeLogHelper getInstance(OrmLiteSqliteOpenHelper dbHelper) {
        if(singleton == null) singleton = new PracticeLogHelper(dbHelper);
        return singleton;
    }
    private static PracticeLogHelper singleton;
    private PracticeLogHelper(OrmLiteSqliteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(PracticeLogHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, PracticeLog.class);
        } catch (SQLException e) {
            Log.e(PracticeLogHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(PracticeLogHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, PracticeLog.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(PracticeLogHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
     * value.
     */
    public Dao<PracticeLog, Integer> getDao() throws SQLException {
        if (simpleDao == null) {
            simpleDao = dbHelper.getDao(PracticeLog.class);
        }
        return simpleDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<PracticeLog, Integer> getSimpleDataDao() {
        if (simpleRuntimeDao == null) {
            simpleRuntimeDao = dbHelper.getRuntimeExceptionDao(PracticeLog.class);
        }
        return simpleRuntimeDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */

    public void close() {
        simpleDao = null;
        simpleRuntimeDao = null;
    }


}