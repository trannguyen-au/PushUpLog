package mobile.wnext.pushupsdiary.activities.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import mobile.wnext.pushupsdiary.Constants;
import mobile.wnext.pushupsdiary.R;

/**
 * Created by Nnguyen on 23/01/2015.
 */
public class SimpleMessageFragment extends Fragment {
    TextView tvMessage;

    String message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if(args!=null && args.containsKey(Constants.MESSAGE_PARAM)) {
            message = args.getString(Constants.MESSAGE_PARAM);
        }

        View view = inflater.inflate(R.layout.fragment_message, container, false);
        tvMessage = (TextView)view.findViewById(R.id.tvMessage);

        if(message!=null && !message.equals("")) {
            tvMessage.setText(message);
        }

        return view;
    }

    public void setMessage(String message) {
        if(tvMessage!=null)
            tvMessage.setText(message);
    }

    public void updateMessage(Bundle args) {
        if(args==null)
            args = getArguments();
        if(args!=null && args.containsKey(Constants.MESSAGE_PARAM)) {
            message = args.getString(Constants.MESSAGE_PARAM);
        }
        if(tvMessage!=null)
            tvMessage.setText(message);
    }
}
