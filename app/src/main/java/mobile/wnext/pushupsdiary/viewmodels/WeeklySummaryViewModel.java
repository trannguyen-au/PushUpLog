package mobile.wnext.pushupsdiary.viewmodels;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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
import mobile.wnext.pushupsdiary.Utils;
import mobile.wnext.pushupsdiary.models.TrainingLogChartSummary;
import mobile.wnext.utils.ArrayUtils;
import mobile.wnext.utils.DateUtils;

/**
 * Created by Nnguyen on 16/01/2015.
 */
public class WeeklySummaryViewModel implements View.OnClickListener {
    ArrayList<String> names;

    // view variables
    LineChart mChartRate;
    BarChart mChart;
    Button btnPrevious, btnNext;
    TextView tvDateDisplay;

    View mView;
    Activity mActivity;
    Resources mResources;
    List<TrainingLogChartSummary> weeklyData = null;
    Date firstDayOfWeek,lastDayOfWeek;
    Calendar currentSelectedWeek;

    public WeeklySummaryViewModel(Activity activity, View view) {
        mView = view;
        mActivity = activity;
        mResources = activity.getResources();
        names = ArrayUtils.toList(mResources.getStringArray(R.array.day_of_week));
        initializeUI();
        currentSelectedWeek = Calendar.getInstance();
        changeCurrentSelectedWeek(0); // load data for chart
    }

    private void initializeUI() {
        btnPrevious = (Button) mView.findViewById(R.id.btnPrevious);
        btnNext = (Button) mView.findViewById(R.id.btnNext);
        tvDateDisplay = (TextView) mView.findViewById(R.id.tvDateDisplay);
        mChart = (BarChart) mView.findViewById(R.id.chart);
        mChartRate = (LineChart) mView.findViewById(R.id.chartRate);

        btnPrevious.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        setupChart();
    }

    private void setupChart() {
        // apply style for push ups chart
        applyDefaultChartStyle(mChart);
        applyDefaultChartStyle(mChartRate);

        // if enabled, the chart will always start at zero on the y-axis
        mChart.setDescription(mResources.getString(R.string.title_chart_push_ups_count));
        mChart.setNoDataTextDescription(mResources.getString(R.string.no_data_found));

        //////// Apply styles for push up rate chart
        mChartRate.setDescription(mResources.getString(R.string.title_chart_rate_per_minute));
        mChartRate.setNoDataTextDescription(mResources.getString(R.string.no_data_found));
    }

    private void calculateDateOfWeek() {
        Calendar tmpCalendar = (Calendar) currentSelectedWeek.clone();
        tmpCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        firstDayOfWeek = tmpCalendar.getTime();

        tmpCalendar.add(Calendar.DATE, 6);
        lastDayOfWeek = tmpCalendar.getTime();
    }

    public void loadDataForChart() {
        // load data from database
        weeklyData = loadWeeklyData();

        // render data to each chart
        loadPushUpsData();
        loadRateData();
    }

    private void loadPushUpsData() {
        BarDataSet barDataSet = loadPushUpsBarData();
        BarData barData = new BarData(names,barDataSet);

        if(mChart.getData()== null) {
            mChart.setData(barData);
        }
        else {
            mChart.getData().removeDataSet(0);
            mChart.getData().addDataSet(barDataSet);
        }
        mChart.animateXY(Constants.CHART_ANIMATE_X,Constants.CHART_ANIMATE_Y);
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
        mChartRate.animateXY(Constants.CHART_ANIMATE_X,Constants.CHART_ANIMATE_Y);
        mChartRate.invalidate(); // refresh the drawing
    }

    private List<TrainingLogChartSummary> loadWeeklyData() {
        try {
            PushUpsDiaryApplication application = (PushUpsDiaryApplication) mActivity.getApplication();
            List<TrainingLogChartSummary> queryDataTraining = application.getDbHelper().getTrainingLogHelper().findRecords(firstDayOfWeek, lastDayOfWeek);
            List<TrainingLogChartSummary> queryDataPractice = application.getDbHelper().getPracticeLogHelper().findRecords(firstDayOfWeek, lastDayOfWeek);

            for (int i=0;i<queryDataPractice.size();i++) {
                boolean isFound = false;
                TrainingLogChartSummary dataPractice = queryDataPractice.get(i);
                for (int j=0;j< queryDataTraining.size();j++) {
                    TrainingLogChartSummary dataTraining = queryDataTraining.get(j);
                    if(dataTraining.getDateTimeStart().compareTo(dataPractice.getDateTimeStart()) == 0) {
                        dataTraining.addCount(dataPractice.getTotalCount());
                        dataTraining.addTime(dataPractice.getTotalTime());
                        isFound = true;
                    }
                }

                if(!isFound) {
                    // add new data
                    queryDataTraining.add(dataPractice);
                }
            }

            // sort the data by datetime
            Utils.sortByDate(queryDataTraining);

            return queryDataTraining;
        }
        catch (SQLException sqle) {
            Log.e(Constants.TAG, "Exception at loadWeeklyData", sqle);
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

        LineDataSet result = new LineDataSet(yData,mResources.getString(R.string.push_ups));
        applyLineStyle(result);
        return result;
    }

    private BarDataSet loadPushUpsBarData() {
        ArrayList<BarEntry> yData = new ArrayList<>();

        for (TrainingLogChartSummary trainingLog : weeklyData) {
            try {
                yData.add(new BarEntry(trainingLog.getTotalCount(),
                        DateUtils.DayDifferent(
                                firstDayOfWeek,
                                trainingLog.getDateTimeStart())));
            }
            catch(ParseException pe) {
                Log.e(Constants.TAG, "Cannot calculate day different", pe);
            }
        }

        return new BarDataSet(yData,mResources.getString(R.string.push_ups));
    }

    private LineDataSet loadPushUpsRateLineData() {
        ArrayList<Entry> yData = new ArrayList<>();

        for (TrainingLogChartSummary trainingLog : weeklyData) {
            try {
                int ratePerMinute = (trainingLog.getTotalCount() * Constants.ONE_MINUTE) / (trainingLog.getTotalTime());

                yData.add(new Entry(ratePerMinute,
                        DateUtils.DayDifferent(
                                firstDayOfWeek,
                                trainingLog.getDateTimeStart())));
            }
            catch(ParseException pe) {
                Log.e(Constants.TAG, "Cannot calculate day different", pe);
            }
        }

        LineDataSet result = new LineDataSet(yData,mResources.getString(R.string.rate_per_minute));
        applyLineStyle(result);
        return result;
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
        tvDateDisplay.setText(DateUtils.DMYFormat(firstDayOfWeek, "/") + " - " + DateUtils.DMYFormat(lastDayOfWeek, "/"));
    }

    //// DEFAULT STYLING METHOD FOR CHART
    private void applyDefaultChartStyle(BarLineChartBase<?> chart) {
        chart.setStartAtZero(true);
        chart.setDrawBorder(false);
        chart.setDrawVerticalGrid(false);
        chart.setDrawHorizontalGrid(true);
        chart.setDrawGridBackground(false);
        chart.setGridColor(mResources.getColor(R.color.grid_line));
        chart.setGridWidth(getFloatResource(R.dimen.grid_width));

        // enable touch gestures
        chart.setTouchEnabled(false);

        // enable scaling and dragging
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setHighlightEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);
        chart.setBackgroundColor(mResources.getColor(R.color.chart_background));
        chart.setDrawYValues(true);
    }

    private void applyLineStyle(LineDataSet lineDataSet) {
        lineDataSet.setCircleSize(getFloatResource(R.dimen.circle_size));
        lineDataSet.setLineWidth(getFloatResource(R.dimen.line_width));
        lineDataSet.setDrawCubic(true);
        lineDataSet.setColor(Color.RED);
        lineDataSet.setCircleColor(mResources.getColor(R.color.red_circle));
    }

    private float getFloatResource(int dimenResourceId) {
        TypedValue outValue = new TypedValue();
        mResources.getValue(dimenResourceId, outValue, true);
        return outValue.getFloat();
    }
}
