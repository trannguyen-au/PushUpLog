package mobile.wnext.pushupsdiary.models;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mobile.wnext.pushupsdiary.Constants;

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

    public List<TrainingLogChartSummary> findRecords(Date from, Date to) throws SQLException {
        if(from.compareTo(to)!=-1)
            throw new IllegalArgumentException("'From' date must be after 'To' date");

        Dao<TrainingLog, Integer> dao = getDao();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String rawQuery = "select substr("+TrainingLog.COL_DATE_TIME_START+",0,11)" +
                ", sum("+TrainingLog.COL_TOTAL_COUNT+") as totalCount" +
                ", sum("+TrainingLog.COL_TOTAL_TIME+") as totalTime " +
                " from "+TrainingLog.TABLE_NAME+" " +
                " where "+TrainingLog.COL_DATE_TIME_START+" < Datetime('"+ sdf.format(to)+" 00:00:00') " +
                "   and "+TrainingLog.COL_DATE_TIME_START+" >= Datetime('"+sdf.format(from)+" 00:00:00') " +
                " group by substr("+TrainingLog.COL_DATE_TIME_START+",0,11)"; // group up to date value

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

        Dao<TrainingLog, Integer> dao = getDao();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String rawQuery = "select substr("+TrainingLog.COL_DATE_TIME_START+",0,11)" +
                ", sum("+TrainingLog.COL_TOTAL_COUNT+") as totalCount" +
                ", sum("+TrainingLog.COL_TOTAL_TIME+") as totalTime " +
                " from "+TrainingLog.TABLE_NAME+" " +
                " where "+TrainingLog.COL_DATE_TIME_START+" < Datetime('"+ (year+1)+"-01-01 00:00:00') " +
                "   and "+TrainingLog.COL_DATE_TIME_START+" >= Datetime('"+year+"-01-01 00:00:00') " +
                " group by substr("+TrainingLog.COL_DATE_TIME_START+",0,8)"; // group up to month value

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
