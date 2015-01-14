package mobile.wnext.pushupsdiary.viewmodels;

import android.app.Activity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import mobile.wnext.pushupsdiary.R;

/**
 * Created by Nnguyen on 13/01/2015.
 */
public class SummaryViewModel {

    // context reference
    Activity context;

    // view properties
    LineChart chart;

    public SummaryViewModel(Activity context) {
        this.context = context;
        initializeUI();
        loadDataForChart();
    }

    private void initializeUI() {
        chart = (LineChart) context.findViewById(R.id.chart);
    }

    public void loadDataForChart() {
        ArrayList<String> names = new ArrayList<>();
        names.add("Q1");
        names.add("Q2");
        names.add("Q3");
        names.add("Q4");

        ArrayList<LineDataSet> dataSet = new ArrayList<>();

        ArrayList<Entry> yData = new ArrayList<>();
        Entry entry1 = new Entry(100,0);
        Entry entry2 = new Entry(120,1);
        yData.add(entry1);
        yData.add(entry2);

        LineDataSet set1 = new LineDataSet(yData,"Label Set1");
        dataSet.add(set1);

        LineData data = new LineData(names,dataSet);
        chart.setData(data);
    }
}
