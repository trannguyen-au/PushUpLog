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
import mobile.wnext.pushupsdiary.models.TrainingLogChartSummary;
import mobile.wnext.utils.DateUtils;

/**
 * Created by Nnguyen on 16/01/2015.
 */
public class MonthlySummaryViewModel implements View.OnClickListener {
    private static int ANIMATE_X = 700;
    private static int ANIMATE_Y = 1000;

    ArrayList<String> names;

    // view variables
    LineChart mChartRate;
    BarChart mChart;
    Button btnPrevious, btnNext;
    TextView tvDateDisplay;

    View mView;
    Activity mActivity;
    Resources mResources;
    List<TrainingLogChartSummary> mChartMonthlyData = null;
    Date fromDate, toDate;
    Calendar currentSelectedMonth;

    public MonthlySummaryViewModel(Activity activity, View view) {
        mView = view;
        mActivity = activity;
        mResources = activity.getResources();
        currentSelectedMonth = Calendar.getInstance();
        names = calculateAllDaysOfMonth(currentSelectedMonth);
        initializeUI();

        changeCurrentSelectedMonth(0); // load data for chart
    }

    private ArrayList<String> calculateAllDaysOfMonth(Calendar calendar) {
        int maxDate = calendar.getActualMaximum(Calendar.DATE);
        ArrayList<String> result = new ArrayList<>();
        for (int i=1;i<=maxDate;i++) {
            result.add(String.valueOf(i));
        }
        return result;
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

    private void changeCurrentSelectedMonth(int monthValue) {
        currentSelectedMonth.add(Calendar.MONTH, monthValue);
        calculateDateOfMonth();
        loadDataForChart();
        tvDateDisplay.setText(DateUtils.DMYFormat(fromDate, "/") + " - " + DateUtils.DMYFormat(toDate, "/"));
    }

    private void calculateDateOfMonth() {
        Calendar tmpCalendar = (Calendar) currentSelectedMonth.clone();
        tmpCalendar.set(Calendar.DATE, 1);
        fromDate = tmpCalendar.getTime();

        tmpCalendar.set(Calendar.DATE, (tmpCalendar.getActualMaximum(Calendar.DATE)));
        toDate = tmpCalendar.getTime();
    }

    public void loadDataForChart() {
        // load data from database
        mChartMonthlyData = loadMonthlyData();

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
        mChart.animateXY(ANIMATE_X,ANIMATE_Y);
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
        mChartRate.animateXY(ANIMATE_X,ANIMATE_Y);
        mChartRate.invalidate(); // refresh the drawing
    }

    private List<TrainingLogChartSummary> loadMonthlyData() {
        try {
            PushUpsDiaryApplication application = (PushUpsDiaryApplication) mActivity.getApplication();
            List<TrainingLogChartSummary> queryDataTraining = application.getDbHelper().getTrainingLogHelper().findRecords(fromDate, toDate);
            List<TrainingLogChartSummary> queryDataPractice = application.getDbHelper().getPracticeLogHelper().findRecords(fromDate, toDate);

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

            return queryDataTraining;
        }
        catch (SQLException sqle) {
            Log.e(Constants.TAG, "Exception at loadMonthlyData", sqle);
            return null;
        }
    }

    private LineDataSet loadPushUpsLineData() {
        ArrayList<Entry> yData = new ArrayList<>();

        for (TrainingLogChartSummary trainingLog : mChartMonthlyData) {
            try {
                yData.add(new Entry(trainingLog.getTotalCount(),
                        DateUtils.DayDifferent(
                                fromDate,
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

        for (TrainingLogChartSummary trainingLog : mChartMonthlyData) {
            try {
                yData.add(new BarEntry(trainingLog.getTotalCount(),
                        DateUtils.DayDifferent(
                                fromDate,
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

        for (TrainingLogChartSummary trainingLog : mChartMonthlyData) {
            try {

                int totalCount = trainingLog.getTotalCount();
                int totalTime = trainingLog.getTotalTime();
                int rate = (totalCount * 60000) / (totalTime);

                yData.add(new Entry(rate,
                        DateUtils.DayDifferent(
                                fromDate,
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
            changeCurrentSelectedMonth(1);
        }
        else if(view == btnPrevious) {
            changeCurrentSelectedMonth(-1);
        }
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
