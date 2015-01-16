package mobile.wnext.pushupsdiary.models;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Nnguyen on 12/01/2015.
 */
public class TrainingLogHelper extends TableHelper {

    // the DAO object we use to access the SimpleData table
    private Dao<TrainingLog, Integer> simpleDao = null;
    private RuntimeExceptionDao<TrainingLog, Integer> simpleRuntimeDao = null;

    private OrmLiteSqliteOpenHelper dbHelper;

    public static TrainingLogHelper getInstance(OrmLiteSqliteOpenHelper dbHelper) {
        if(singleton == null) singleton = new TrainingLogHelper(dbHelper);
        return singleton;
    }
    private static TrainingLogHelper singleton;
    private TrainingLogHelper(OrmLiteSqliteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(this.getClass().getName(), "onCreate");
            TableUtils.createTable(connectionSource, TrainingLog.class);
        } catch (SQLException e) {
            Log.e(this.getClass().getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(TrainingLogHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, TrainingLog.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(this.getClass().getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public TrainingLog findBestRecord() throws SQLException{
        Dao<TrainingLog, Integer> dao = getDao();
        QueryBuilder<TrainingLog, Integer> queryBuilder = dao.queryBuilder();
        queryBuilder.orderBy(TrainingLog.COL_TOTAL_COUNT, false);
        queryBuilder.limit(1L);
        return queryBuilder.queryForFirst();
    }

    public TrainingLog findLastRecord() throws SQLException {
        Dao<TrainingLog, Integer> dao = getDao();
        QueryBuilder<TrainingLog, Integer> queryBuilder = dao.queryBuilder();
        queryBuilder.orderBy(TrainingLog.COL_DATE_TIME_START, false);
        queryBuilder.limit(1L);
        return queryBuilder.queryForFirst();
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
     * value.
     */
    public Dao<TrainingLog, Integer> getDao() throws SQLException {
        if (simpleDao == null) {
            simpleDao = dbHelper.getDao(TrainingLog.class);
        }
        return simpleDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<TrainingLog, Integer> getSimpleDataDao() {
        if (simpleRuntimeDao == null) {
            simpleRuntimeDao = dbHelper.getRuntimeExceptionDao(TrainingLog.class);
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
