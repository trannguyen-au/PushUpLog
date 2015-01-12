package mobile.wnext.pushupsdiary.viewmodels;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import mobile.wnext.pushupsdiary.Constants;
import mobile.wnext.pushupsdiary.R;

/**
 * Created by Wery7 on 7/1/2015.
 */
public class TrainingViewModel
        implements View.OnClickListener, SensorEventListener
{
    // constants
    private static final int COOL_DOWN_PERIOD = 350;
    private static final Object countLock = new Object(); // count lock for multi-threading control
    private static final int PROXIMITY_MAX_NEAR_DISTANCE = 3;

    // reference variables
    Activity activity;
    Sensor proximitySensor;
    SensorManager sensorManager;

    // properties
    private boolean coolingDown = false;

    // view variables
    LinearLayout mainButtonLayout;
    TextView tvPushCounter, tvTimer;
    TextView tvBRSet1,tvBRSet2,tvBRSet3,tvBRSet4;
    TextView tvLRSet1,tvLRSet2,tvLRSet3,tvLRSet4;
    TextView tvCurrentSet1,tvCurrentSet2,tvCurrentSet3,tvCurrentSet4;
    Button btnDoneCurrentSet, btnCompleteTraining;

    // model variables
    CountDownTimer timer;
    Date startTime;
    int currentCount;
    long currentTime;
    long seconds,ticks,minutes;
    int currentSetIndex;

    public TrainingViewModel(Activity activity) {
        currentCount = 0;
        this.activity = activity;
        initializeUI(activity);
        initializeSensor();
    }

    public void pause(){
        Log.i(Constants.TAG,"Training view is pause");
        coolingDown = true; // prevent the counter
        sensorManager.unregisterListener(this, proximitySensor);
    }
    public void resume(){
        Log.i(Constants.TAG,"Training view is resume");
        coolingDown = false; // continue the counter
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void stopAndDestroy(){
        Log.i(Constants.TAG,"Training view is destroyed");
        // release resources
    }

    private void initializeSensor() {
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    private void initializeUI(Activity activity) {
        mainButtonLayout = (LinearLayout)activity.findViewById(R.id.mainButtonLayout);
        mainButtonLayout.setOnClickListener(this);

        tvPushCounter = (TextView) activity.findViewById(R.id.tvPushCounter);
        tvTimer = (TextView) activity.findViewById(R.id.tvTimer);

        tvBRSet1 = (TextView) activity.findViewById(R.id.tvBRSet1);
        tvBRSet2 = (TextView) activity.findViewById(R.id.tvBRSet2);
        tvBRSet3 = (TextView) activity.findViewById(R.id.tvBRSet3);
        tvBRSet4 = (TextView) activity.findViewById(R.id.tvBRSet4);

        tvLRSet1 = (TextView) activity.findViewById(R.id.tvLRSet1);
        tvLRSet2 = (TextView) activity.findViewById(R.id.tvLRSet2);
        tvLRSet3 = (TextView) activity.findViewById(R.id.tvLRSet3);
        tvLRSet4 = (TextView) activity.findViewById(R.id.tvLRSet4);

        tvCurrentSet1 = (TextView) activity.findViewById(R.id.tvCurrentSet1);
        tvCurrentSet2 = (TextView) activity.findViewById(R.id.tvCurrentSet2);
        tvCurrentSet3 = (TextView) activity.findViewById(R.id.tvCurrentSet3);
        tvCurrentSet4 = (TextView) activity.findViewById(R.id.tvCurrentSet4);

        btnDoneCurrentSet = (Button) activity.findViewById(R.id.btnDoneCurrentSet);
        btnDoneCurrentSet.setOnClickListener(this);
        btnCompleteTraining = (Button) activity.findViewById(R.id.btnCompleteTraining);
        btnCompleteTraining.setOnClickListener(this);

        resetAllCounterValues();
        loadLastRecordValues();
        loadBestRecordValues();
    }

    private void loadBestRecordValues() {
        tvBRSet1.setText("0");
        tvBRSet2.setText("0");
        tvBRSet3.setText("0");
        tvBRSet4.setText("0");
    }

    private void loadLastRecordValues() {
        tvLRSet1.setText("0");
        tvLRSet2.setText("0");
        tvLRSet3.setText("0");
        tvLRSet4.setText("0");
    }

    private void resetCounterValues() {
        tvPushCounter.setText("0");
        tvTimer.setText("00:00");
        currentCount = 0;
        currentTime = 0;
    }

    private void resetAllCounterValues() {
        resetCounterValues();
        tvCurrentSet1.setText("0");
        tvCurrentSet2.setText("0");
        tvCurrentSet3.setText("0");
        tvCurrentSet4.setText("0");
        currentSetIndex = 0;
    }


    @Override
    public void onClick(View view) {
        if(view == mainButtonLayout) {
            countOne();
        }
        else if(view == btnDoneCurrentSet) {
            if(timer!=null) {
                timer.cancel();
                timer = null;
            }

            saveCurrentRecord();
            resetCounterValues();
        }
        else if(view == btnCompleteTraining) {
            // show result activity
        }
    }

    private void saveCurrentRecord() {
        switch (currentSetIndex) {
            case 0:
                tvCurrentSet1.setText(String.valueOf(currentCount)+"/"+getDisplayTimeShort(currentTime));
                tvCurrentSet1.setTextColor(activity.getResources().getColor(R.color.label_value_recorded));
                break;
            case 1:
                tvCurrentSet2.setText(String.valueOf(currentCount)+"/"+getDisplayTimeShort(currentTime));
                tvCurrentSet2.setTextColor(activity.getResources().getColor(R.color.label_value_recorded));
                break;
            case 2:
                tvCurrentSet3.setText(String.valueOf(currentCount)+"/"+getDisplayTimeShort(currentTime));
                tvCurrentSet3.setTextColor(activity.getResources().getColor(R.color.label_value_recorded));
                break;
            case 3:
                tvCurrentSet4.setText(String.valueOf(currentCount)+"/"+getDisplayTimeShort(currentTime));
                tvCurrentSet4.setTextColor(activity.getResources().getColor(R.color.label_value_recorded));
                break;
        }

        if(currentSetIndex < 4) {
            currentSetIndex++;
            Toast.makeText(activity,"Current set index is "+currentSetIndex, Toast.LENGTH_SHORT)
                    .show();
        }
        else {
            // display complete message
            Toast.makeText(activity,"This set is completed", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private String getDisplayTime(long currentTime) {
        seconds = currentTime / 1000;
        ticks = currentTime % 1000;
        minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d", minutes)+":"+
                String.format("%02d",seconds)+":"+
                String.format("%03d",ticks);
    }

    private String getDisplayTimeShort(long currentTime) {
        return getDisplayTime(currentTime).substring(0,5);
    }

    private void countOne() {
        synchronized (countLock) {
            if (currentCount == 0) {
                // start timer
                // TODO: find another mean to calculate exec
                startTime = new Date();
                timer = new CountDownTimer(999999, 50) {
                    @Override
                    public void onTick(long l) {
                        currentTime = (new Date()).getTime() - startTime.getTime();
                        tvTimer.setText(getDisplayTime(currentTime));
                    }

                    @Override
                    public void onFinish() {
                        //Toast.makeText(activity,"Timer complete",Toast.LENGTH_SHORT).show();
                    }
                }.start();
            }
            if (!coolingDown) {
                currentCount++;
                tvPushCounter.setText(String.valueOf(currentCount));
                coolingDown = true;

                // start cooling down for a specified period to prevent accidental pressed which causing
                // the count to be counted as 2
                new CountDownTimer(COOL_DOWN_PERIOD, COOL_DOWN_PERIOD) {
                    @Override
                    public void onTick(long l) {
                    }

                    @Override
                    public void onFinish() {
                        coolingDown = false;
                    }
                }.start();
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            Log.i(Constants.TAG, "Sensor value: " + sensorEvent.values[0] + ", accuracy:" + sensorEvent.accuracy);
            if (sensorEvent.values[0] <= PROXIMITY_MAX_NEAR_DISTANCE) {
                // only count when the value changed to near
                countOne();
            } else {
                //Toast.makeText(activity, "Far", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // ignore this event
    }
}
