package mobile.wnext.pushupsdiary.viewmodels;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.sql.SQLException;
import java.text.ParseException;
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
public class WeeklySummaryViewModel implements View.OnClickListener {

    private static final String LINE_LABEL_PUSH_UP = "Push ups";
    ArrayList<String> names = new ArrayList<String>(){{
        add("Mon");
        add("Tue");
        add("Wed");
        add("Thu");
        add("Fri");
        add("Sat");
        add("Sun");
    }}; // TODO: move this to strings resource instead

    // view variables
    LineChart mChart, mChartRate;
    Button btnPrevious, btnNext;
    TextView tvWeek;

    View mView;
    Activity mActivity;
    List<TrainingLogChartSummary> weeklyData = null;
    Date firstDayOfWeek,lastDayOfWeek;
    Calendar currentSelectedWeek;

    public WeeklySummaryViewModel(Activity activity, View view) {
        mView = view;
        mActivity = activity;
        initializeUI();
        currentSelectedWeek = Calendar.getInstance();
        changeCurrentSelectedWeek(0); // load data for chart
    }

    private void initializeUI() {
        btnPrevious = (Button) mView.findViewById(R.id.btnPrevious);
        btnNext = (Button) mView.findViewById(R.id.btnNext);
        tvWeek = (TextView) mView.findViewById(R.id.tvWeek);
        mChart = (LineChart) mView.findViewById(R.id.chart);
        mChartRate = (LineChart) mView.findViewById(R.id.chartRate);

        btnPrevious.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        setupChart();
    }

    private void setupChart() {
        // apply style for push ups chart
        // if enabled, the chart will always start at zero on the y-axis
        mChart.setStartAtZero(true);
        // disable the drawing of values into the chart
        mChart.setDrawYValues(false);

        mChart.setDrawBorder(false);
        mChart.setDescription("Weekly push ups report");
        mChart.setNoDataTextDescription("No data found for this week.");
        mChart.setDrawVerticalGrid(true);
        mChart.setDrawHorizontalGrid(true);
        mChart.setDrawGridBackground(true);
        mChart.setGridColor(Color.rgb(170,170,170));
        mChart.setGridWidth(1.25f);

        // enable touch gestures
        mChart.setTouchEnabled(false);

        // enable scaling and dragging
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setHighlightEnabled(false);


        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setBackgroundColor(Color.rgb(251, 245, 214));
        mChart.setDrawYValues(true);

        //////// Apply styles for push up rate chart
        mChartRate.setStartAtZero(true);
        // disable the drawing of values into the chart
        mChartRate.setDrawYValues(false);

        mChartRate.setDrawBorder(false);
        mChartRate.setDescription("Weekly push ups report");
        mChartRate.setNoDataTextDescription("No data found for this week.");
        mChartRate.setDrawVerticalGrid(true);
        mChartRate.setDrawHorizontalGrid(true);
        mChartRate.setDrawGridBackground(true);
        mChartRate.setGridColor(Color.rgb(170,170,170));
        mChartRate.setGridWidth(1.25f);

        // disable touch gestures
        mChartRate.setTouchEnabled(false);
        mChartRate.setDragEnabled(false);
        mChartRate.setScaleEnabled(false);
        mChartRate.setHighlightEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChartRate.setPinchZoom(false);
        mChartRate.setBackgroundColor(Color.rgb(251, 245, 214));
        mChartRate.setDrawYValues(true);

    }

    private void applyLineStyle(LineDataSet lineDataSet) {
        lineDataSet.setCircleSize(3f);
        lineDataSet.setLineWidth(1.75f);
        lineDataSet.setColor(Color.rgb(51,51,51));
        lineDataSet.setCircleColor(Color.rgb(71,71,71));
        lineDataSet.setHighLightColor(Color.rgb(81,81,81));
        lineDataSet.setDrawCubic(true);
        lineDataSet.setDrawFilled(true);
    }

    private void calculateDateOfWeek() {
        Calendar tmpCalendar = (Calendar) currentSelectedWeek.clone();
        tmpCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        firstDayOfWeek = tmpCalendar.getTime();

        tmpCalendar.add(Calendar.DATE, 7);
        lastDayOfWeek = tmpCalendar.getTime();
    }

    public void loadDataForChart() {
        // load data from database
        weeklyData = loadWeeklyData();

        loadPushupData();

        loadRateData();
    }

    private void loadPushupData() {
        LineDataSet newPushUpsLineData = loadPushUpsLineData();
        Log.i(Constants.TAG, "push up line count: "+newPushUpsLineData.getEntryCount());
        ArrayList<LineDataSet> dataSet = new ArrayList<>();
        dataSet.add(newPushUpsLineData);

        LineData data = new LineData(names,dataSet);

        if(mChart.getData()== null) {
            mChart.setData(data);
        }
        else {
            mChart.getData().removeDataSet(0);
            mChart.getData().addDataSet(newPushUpsLineData);
        }
        mChart.animateXY(700,1000);
        mChart.invalidate(); // refresh the drawing
    }

    private void loadRateData() {
        ArrayList<LineDataSet> dataSetRate = new ArrayList<>();
        dataSetRate.add(loadPushUpsRateLineData());

        LineData dataRate = new LineData(names,dataSetRate);

        if(mChartRate.getData()== null) {
            mChartRate.setData(dataRate);
        }
        else {
            mChartRate.getData().removeDataSet(0);
            mChartRate.getData().addDataSet(dataSetRate.get(0));
        }
        mChartRate.animateXY(700,1000);
        mChartRate.invalidate(); // refresh the drawing
    }

    public void refreshDataForChart() {

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
            try {
                yData.add(new Entry(trainingLog.getTotalCount(),
                        DateUtils.DayDifferent(
                                firstDayOfWeek,
                                trainingLog.getDateTimeStart())));
            }
            catch(ParseException pe) {
                Log.e(Constants.TAG, "Cannot calculate day different", pe);
            }
        }
        LineDataSet result = new LineDataSet(yData,"Push Ups");
        applyLineStyle(result);
        return result;
    }

    private LineDataSet loadPushUpsRateLineData() {
        ArrayList<Entry> yData = new ArrayList<>();

        for (TrainingLogChartSummary trainingLog : weeklyData) {
            try {

                int totalCount = trainingLog.getTotalCount();
                int totalTime = trainingLog.getTotalTime();
                int rate = (totalCount * 60000) / (totalTime);

                yData.add(new Entry(rate,
                        DateUtils.DayDifferent(
                                firstDayOfWeek,
                                trainingLog.getDateTimeStart())));
            }
            catch(ParseException pe) {
                Log.e(Constants.TAG, "Cannot calculate day different", pe);
            }
        }

        LineDataSet result = new LineDataSet(null,"Rate per minute");
        applyLineStyle(result);
        return result;
    }

    private LineDataSet loadPushUpsTimeLineData() {
        return null;
    }

    @Override
    public void onClick(View view) {
        if(view == btnNext) {
            changeCurrentSelectedWeek(7);
        }
        else if(view == btnPrevious) {
            changeCurrentSelectedWeek(-7);
        }
    }

    private void changeCurrentSelectedWeek(int dayValue) {
        currentSelectedWeek.add(Calendar.DATE,dayValue);
        calculateDateOfWeek();
        loadDataForChart();
        tvWeek.setText(DateUtils.DMYFormat(firstDayOfWeek,"/")+" - "+DateUtils.DMYFormat(lastDayOfWeek,"/"));
    }
}
