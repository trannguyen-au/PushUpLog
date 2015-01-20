package mobile.wnext.pushupsdiary.viewmodels;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mobile.wnext.pushupsdiary.Constants;
import mobile.wnext.pushupsdiary.PushUpsDiaryApplication;
import mobile.wnext.pushupsdiary.R;
import mobile.wnext.pushupsdiary.models.TrainingLog;
import mobile.wnext.pushupsdiary.models.TrainingLogChartSummary;
import mobile.wnext.utils.DateUtils;

/**
 * Created by Nnguyen on 16/01/2015.
 */
public class WeeklySummaryViewModel {

    LineChart mChart;
    View mView;
    Activity mActivity;
    List<TrainingLogChartSummary> weeklyData = null;
    Date firstDayOfWeek,lastDayOfWeek;

    public WeeklySummaryViewModel(Activity activity, View view) {
        mView = view;
        mActivity = activity;
        initializeUI();
        loadDataForChart();
    }

    private void initializeUI() {
        mChart = (LineChart) mView.findViewById(R.id.chart);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        firstDayOfWeek = calendar.getTime();

        calendar.add(Calendar.DATE, 7);
        lastDayOfWeek = calendar.getTime();
    }

    public void loadDataForChart() {

        // load data from database
        weeklyData = loadWeeklyData();

        // render the data on chart
        ArrayList<String> names = new ArrayList<String>(){{
            add("Mon");
            add("Tue");
            add("Wed");
            add("Thu");
            add("Fri");
            add("Sat");
            add("Sun");
        }}; // TODO: move this to strings resource instead

        ArrayList<LineDataSet> dataSet = new ArrayList<>();

        dataSet.add(loadPushUpsLineData());
        dataSet.add(loadPushUpsRateLineData());
        dataSet.add(loadPushUpsTimeLineData());

        LineData data = new LineData(names,dataSet);
        mChart.setData(data);
    }

    private List<TrainingLogChartSummary> loadWeeklyData() {
        try {
            PushUpsDiaryApplication application = (PushUpsDiaryApplication) mActivity.getApplication();
            List<TrainingLogChartSummary> queryData = application.getDbHelper().getTrainingLogHelper().findRecords(firstDayOfWeek, lastDayOfWeek);
            return queryData;
        }
        catch (SQLException sqle) {
            Log.e(Constants.TAG, "Execption at loadWeeklyData", sqle);
            return null;
        }
    }

    private LineDataSet loadPushUpsLineData() {
        ArrayList<Entry> yData = new ArrayList<>();

        for (TrainingLogChartSummary trainingLog : weeklyData) {
            yData.add(new Entry(trainingLog.getTotalCount(),
                    DateUtils.DayDifferent(
                            firstDayOfWeek,
                            trainingLog.getDateTimeStart())));
        }

        return new LineDataSet(yData,"Push Ups");
    }

    private LineDataSet loadPushUpsRateLineData() {
        return null;
    }

    private LineDataSet loadPushUpsTimeLineData() {
        return null;
    }
}
