package mobile.wnext.pushupsdiary.models;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import mobile.wnext.pushupsdiary.Constants;

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

    public PracticeLog findBestRecord() throws SQLException {
        Dao<PracticeLog, Integer> dao = getDao();
        QueryBuilder<PracticeLog, Integer> queryBuilder = dao.queryBuilder();
        queryBuilder.orderBy(PracticeLog.COL_COUNT_PUSH_UPS, false);
        queryBuilder.limit(1L);
        return queryBuilder.queryForFirst();
    }

    public PracticeLog findLastRecord() throws  SQLException {
        Dao<PracticeLog, Integer> dao = getDao();
        QueryBuilder<PracticeLog, Integer> queryBuilder = dao.queryBuilder();
        queryBuilder.orderBy(PracticeLog.COL_LOG_DATE, false);
        queryBuilder.limit(1L);
        return queryBuilder.queryForFirst();
    }

    public List<PracticeLog> queryAllGroupByDate() throws SQLException {
        Dao<PracticeLog, Integer> dao = getDao();
        String rawQuery = "select sum("+PracticeLog.COL_COUNT_PUSH_UPS+") as totalCount" +
                ", sum("+PracticeLog.COL_TOTAL_TIME+") as totalTime " +
                " from "+PracticeLog.TABLE_NAME+
                " group by substr("+PracticeLog.COL_LOG_DATE+",0,11)";
        GenericRawResults<String[]> rawResults = dao.queryRaw(rawQuery);
        List<String[]> results = rawResults.getResults();
        List<PracticeLog> finalResult = new ArrayList<>();
        for (int i=0;i<results.size();i++) {
            String[] resultArray = results.get(i);

            PracticeLog practiceLog = new PracticeLog();
            practiceLog.setCountPushUps(Integer.valueOf(resultArray[0]));
            practiceLog.setCountTime(Long.valueOf(resultArray[1]));
            finalResult.add(practiceLog);

        }
        return finalResult;
    }

    public List<TrainingLogChartSummary> findRecords(Date from, Date to) throws SQLException {
        if(from.compareTo(to)!=-1)
            throw new IllegalArgumentException("'From' date must be after 'To' date");

        Dao<PracticeLog, Integer> dao = getDao();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String rawQuery = "select substr("+PracticeLog.COL_LOG_DATE+",0,11)" +
                ", sum("+PracticeLog.COL_COUNT_PUSH_UPS+") as totalCount" +
                ", sum("+PracticeLog.COL_TOTAL_TIME+") as totalTime " +
                " from "+PracticeLog.TABLE_NAME+" " +
                " where "+PracticeLog.COL_LOG_DATE+" < Datetime('"+ sdf.format(to)+" 00:00:00') " +
                "   and "+PracticeLog.COL_LOG_DATE+" >= Datetime('"+sdf.format(from)+" 00:00:00') " +
                " group by substr("+PracticeLog.COL_LOG_DATE+",0,11)"; // group up to date value

        GenericRawResults<String[]> rawResults = dao.queryRaw(rawQuery);

        List<String[]> results = rawResults.getResults();
        List<TrainingLogChartSummary> finalResult = new ArrayList<>();
        for (int i=0;i<results.size();i++) {
            String[] resultArray = results.get(i);

            TrainingLogChartSummary chartSummary = new TrainingLogChartSummary();
            try{
                chartSummary.setDateTimeStart(sdf.parse(resultArray[0]));
            } catch (Exception ex) {}

            chartSummary.setTotalCount(Integer.valueOf(resultArray[1]));
            chartSummary.setTotalTime(Integer.valueOf(resultArray[2]));
            finalResult.add(chartSummary);
        }

        return finalResult;
    }

    public List<TrainingLogChartSummary> findYearlyRecords(int year) throws SQLException {
        if(year<1990 || year >9999)
            throw new IllegalArgumentException("'year' value is invalid");

        Dao<PracticeLog, Integer> dao = getDao();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String rawQuery = "select substr("+PracticeLog.COL_LOG_DATE+",0,11)" +
                ", sum("+PracticeLog.COL_COUNT_PUSH_UPS+") as totalCount" +
                ", sum("+PracticeLog.COL_TOTAL_TIME+") as totalTime " +
                " from "+PracticeLog.TABLE_NAME+" " +
                " where "+PracticeLog.COL_LOG_DATE+" < Datetime('"+ (year+1)+"-01-01 00:00:00') " +
                "   and "+PracticeLog.COL_LOG_DATE+" >= Datetime('"+year+"-01-01 00:00:00') " +
                " group by substr("+PracticeLog.COL_LOG_DATE+",0,8)"; // group up to month value

        GenericRawResults<String[]> rawResults = dao.queryRaw(rawQuery);

        List<String[]> results = rawResults.getResults();
        List<TrainingLogChartSummary> finalResult = new ArrayList<>();
        for (int i=0;i<results.size();i++) {
            String[] resultArray = results.get(i);

            TrainingLogChartSummary chartSummary = new TrainingLogChartSummary();
            try{
                chartSummary.setDateTimeStart(sdf.parse(resultArray[0]));
            } catch (Exception ex) {
                Log.e(Constants.TAG, "ERROR: Cannot read date value of "+resultArray[0]+" at: findYearlyRecords("+year+")", ex);
            }

            chartSummary.setTotalCount(Integer.valueOf(resultArray[1]));
            chartSummary.setTotalTime(Integer.valueOf(resultArray[2]));
            finalResult.add(chartSummary);
        }

        return finalResult;
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