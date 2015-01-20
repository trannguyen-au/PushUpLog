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
import mobile.wnext.pushupsdiary.viewmodels.WeeklySummaryViewModel;

/**
 * Created by Nnguyen on 16/01/2015.
 */
public class WeeklySummaryFragment extends Fragment {
    WeeklySummaryViewModel mViewModel;
    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_weekly_summary, container, false);
        mViewModel = new WeeklySummaryViewModel(getActivity(), mView);
        return mView;
    }
}
