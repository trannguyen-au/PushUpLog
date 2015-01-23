package mobile.wnext.pushupsdiary.activities.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mobile.wnext.pushupsdiary.R;
import mobile.wnext.pushupsdiary.viewmodels.YearlySummaryViewModel;

/**
 * Created by Nnguyen on 16/01/2015.
 */
public class YearlySummaryFragment extends Fragment {
    YearlySummaryViewModel mViewModel;
    View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_yearly_summary, container, false);
        mViewModel = new YearlySummaryViewModel(getActivity(), mView);
        return mView;
    }
}
