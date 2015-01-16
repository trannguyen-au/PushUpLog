package mobile.wnext.pushupsdiary.activities.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mobile.wnext.pushupsdiary.R;

/**
 * Created by Nnguyen on 16/01/2015.
 */
public class YearlySummaryFragment extends Fragment {

    View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_yearly_summary, container, false);
        initializeUI();
        return mView;
    }

    private void initializeUI() {
        
    }
}
