package mobile.wnext.pushupsdiary.activities.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import mobile.wnext.pushupsdiary.R;

/**
 * Created by Nnguyen on 16/01/2015.
 */
public class WeeklySummaryFragment extends Fragment {

    View mView;
    LineChart chart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_weekly_summary, container, false);
        initializeUI();
        loadDataForChart();
        return mView;
    }

    private void initializeUI() {
        chart = (LineChart) mView.findViewById(R.id.chart);
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
