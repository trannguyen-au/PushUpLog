package mobile.wnext.pushupsdiary.activities.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import mobile.wnext.pushupsdiary.R;
import mobile.wnext.pushupsdiary.OnIntroActionListener;

/**
 * Created by Nnguyen on 29/01/2015.
 */
public class IntroPage1Fragment extends Fragment {
    View mView;
    Button btnNext;
    TextView tvMessage;

    OnIntroActionListener mOnIntroActionListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_introduction_page_1, container, false);
        btnNext = (Button) mView.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnIntroActionListener!=null)
                    mOnIntroActionListener.nextButtonClicked();
            }
        });

        tvMessage = (TextView) mView.findViewById(R.id.tvMessage);

        // check if the proximity sensor is available
        SensorManager sensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if(sensor==null) {
            tvMessage.setText(getResources().getString(R.string.introduction_message_no_proximity_sensor));
        }
        return mView;
    }

    public void setOnIntroActionListener(OnIntroActionListener onIntroActionListener) {
        mOnIntroActionListener = onIntroActionListener;
    }
}
