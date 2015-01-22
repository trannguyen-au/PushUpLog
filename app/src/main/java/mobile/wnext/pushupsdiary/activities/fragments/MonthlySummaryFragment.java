package mobile.wnext.pushupsdiary.activities.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mobile.wnext.pushupsdiary.R;
import mobile.wnext.pushupsdiary.viewmodels.MonthlySummaryViewModel;
import mobile.wnext.pushupsdiary.viewmodels.WeeklySummaryViewModel;

/**
 * Created by Nnguyen on 16/01/2015.
 */
public class MonthlySummaryFragment extends Fragment {

    MonthlySummaryViewModel mViewModel;

    View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_weekly_summary, container, false);
        mViewModel = new MonthlySummaryViewModel(getActivity(), mView);
        return mView;
    }
}
