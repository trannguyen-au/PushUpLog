package mobile.wnext.pushupsdiary.activities.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import mobile.wnext.pushupsdiary.R;

/**
 * Created by Nnguyen on 23/01/2015.
 */
public class SimpleMessageFragment extends Fragment {
    TextView tvMessage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_message, container, false);
        tvMessage = (TextView)view.findViewById(R.id.tvMessage);
        return view;
    }

}
