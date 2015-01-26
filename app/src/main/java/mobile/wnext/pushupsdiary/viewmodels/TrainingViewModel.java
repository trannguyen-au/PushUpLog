package mobile.wnext.pushupsdiary.viewmodels;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.Date;

import mobile.wnext.pushupsdiary.Constants;
import mobile.wnext.pushupsdiary.R;
import mobile.wnext.pushupsdiary.Utils;
import mobile.wnext.pushupsdiary.activities.SummaryActivity;
import mobile.wnext.pushupsdiary.activities.fragments.AdFragment;
import mobile.wnext.pushupsdiary.activities.fragments.ConfirmDialogFragment;
import mobile.wnext.pushupsdiary.activities.fragments.CorrectCountDialogFragment;
import mobile.wnext.pushupsdiary.activities.fragments.SimpleMessageFragment;
import mobile.wnext.pushupsdiary.models.TrainingLog;
import mobile.wnext.pushupsdiary.models.TrainingSet;

/**
 * Created by Wery7 on 7/1/2015.
 */
public class TrainingViewModel extends ViewModel
        implements View.OnClickListener, SensorEventListener
{
    // constants
    private static final int COOL_DOWN_PERIOD = 350;
    private static final Object countLock = new Object(); // count lock for multi-threading control
    private static final int PROXIMITY_MAX_NEAR_DISTANCE = 3;
    private static final int DEFAULT_RESTING_PERIOD = 60000; // 60 seconds
    private static final int QUIT_CHANGE_OF_MIND_PERIOD = 4000; //4 seconds

    // reference variables
    Sensor proximitySensor;
    SensorManager sensorManager;

    // properties & flags
    boolean coolingDown = false;
    boolean isCompleted = false;

    // resting variables
    boolean isResting = false;
    int restingTimeElapse = DEFAULT_RESTING_PERIOD;
    CountDownTimer mRestingTimer;

    // view variables
    LinearLayout mainButtonLayout;
    TextView tvPushCounter, tvTimer;
    TextView tvBRSet1,tvBRSet2,tvBRSet3,tvBRSet4;
    TextView tvLRSet1,tvLRSet2,tvLRSet3,tvLRSet4;
    TextView tvCurrentSet1,tvCurrentSet2,tvCurrentSet3,tvCurrentSet4;
    Button btnDoneCurrentSet, btnCompleteTraining;

    // push up count & training variables
    CountDownTimer mTrainingTimer;
    Date startTime;
    int currentCount;
    long currentTime;
    int currentSetIndex;

    // model database
    TrainingLog mTrainingLog;
    TrainingSet mCurrentTrainingSet;

    private AdFragment mAdFragment;
    private SimpleMessageFragment mMessageFragment;

    FragmentManager fragmentManager;
    Animation bouncingAnimation;

    public TrainingViewModel(Activity context) {
        super(context);
        currentCount = 0;
        bouncingAnimation = AnimationUtils.loadAnimation(activity.getApplicationContext(),
                R.anim.my_first_animate);

        initializeUI();
        initializeSensor();

        loadLastRecordValues();
        loadBestRecordValues();

        // create the data model
        mTrainingLog = new TrainingLog.Builder()
                .count(0)
                .time(0)
                .build();

        mAdFragment = new AdFragment();
        mMessageFragment = new SimpleMessageFragment();

        // attach ad fragment by code
        fragmentManager = ((ActionBarActivity) context).getSupportFragmentManager();

        updateMessageFragment(mAdFragment, Constants.MESSAGE_FRAGMENT_TAG);

    }

    private void updateMessageFragment(Fragment fragment, String tag) {
        if(fragmentManager.findFragmentByTag(tag)==null) {
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_message, fragment, tag)
                    .commit();
        }
        else {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_message, fragment, tag)
                    .commit();
        }
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

    private void initializeUI() {
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
    }

    private void loadBestRecordValues() {
        // reset all values
        tvBRSet1.setText("0");
        tvBRSet2.setText("0");
        tvBRSet3.setText("0");
        tvBRSet4.setText("0");

        // get the data from sqlite database
        try {
            TrainingLog bestLogRecord = application.getDbHelper().getTrainingLogHelper().findBestRecord();
            if(bestLogRecord!=null) {
                // find all the sets
                int count = 0;
                for (TrainingSet data : bestLogRecord.getTrainingSets()) { // using foreign collection reference
                    switch (count) {
                        case 0:
                            tvBRSet1.setText(String.valueOf(data.getCount()));
                            break;
                        case 1:
                            tvBRSet2.setText(String.valueOf(data.getCount()));
                            break;
                        case 2:
                            tvBRSet3.setText(String.valueOf(data.getCount()));
                            break;
                        case 3:
                            tvBRSet4.setText(String.valueOf(data.getCount()));
                            break;
                    }
                    count++;
                }
            }
        }
        catch (SQLException sqle) {
            Toast.makeText(activity, "Cannot access database for best record", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadLastRecordValues() {
        tvLRSet1.setText("0");
        tvLRSet2.setText("0");
        tvLRSet3.setText("0");
        tvLRSet4.setText("0");
        try {
            TrainingLog bestLogRecord = application.getDbHelper().getTrainingLogHelper().findLastRecord();
            if(bestLogRecord!=null) {
                // find all the sets
                int count = 0;
                for (TrainingSet data : bestLogRecord.getTrainingSets()) { // using foreign collection reference
                    switch (count) {
                        case 0:
                            tvLRSet1.setText(String.valueOf(data.getCount()));
                            break;
                        case 1:
                            tvLRSet2.setText(String.valueOf(data.getCount()));
                            break;
                        case 2:
                            tvLRSet3.setText(String.valueOf(data.getCount()));
                            break;
                        case 3:
                            tvLRSet4.setText(String.valueOf(data.getCount()));
                            break;
                    }
                    count++;
                }
            }
        }
        catch (SQLException sqle) {
            Toast.makeText(activity, "Cannot access database for last record", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetCounterValues() {
        tvPushCounter.setText("0");
        tvTimer.setText("00:00:000");
        currentCount = 0;
        currentTime = 0;
    }

    private void resetAllCounterValues() {
        resetCounterValues();
        tvCurrentSet1.setText("0/00:00");
        tvCurrentSet2.setText("0/00:00");
        tvCurrentSet3.setText("0/00:00");
        tvCurrentSet4.setText("0/00:00");
        currentSetIndex = 0;
    }


    @Override
    public void onClick(View view) {
        if(view == mainButtonLayout) {
            countOne();
        }
        else if(view == btnDoneCurrentSet) {
            if(mTrainingTimer !=null) {
                mTrainingTimer.cancel();
                mTrainingTimer = null;
            }

            // offer a popup to correct the number
            if(mCurrentTrainingSet!=null && currentTime!=0 && currentCount != 0) {

                CorrectCountDialogFragment dialogFragment = new CorrectCountDialogFragment();
                Bundle args = new Bundle();
                args.putInt(Constants.COUNT_PARAM, currentCount);
                dialogFragment.setArguments(args);
                dialogFragment.setEventListener(new CorrectCountDialogFragment.CorrectCountDialogEventListener() {
                    @Override
                    public void changeCount(int newCount) {
                        currentCount = newCount;
                    }

                    @Override
                    public void dialogClosing() {
                        saveCurrentRecord();
                        resetCounterValues();
                        startResting();
                    }
                });
                dialogFragment.show(activity.getFragmentManager(),"CorrectCountDialog");
            }
            else {
                Toast.makeText(activity, activity.getResources().getString(R.string.please_start_training_first), Toast.LENGTH_SHORT).show();
            }
        }
        else if(view == btnCompleteTraining) {
            // show result activity
            startActivity(SummaryActivity.class);
        }
    }

    private void startResting() {
        if(!isResting) {
            isResting = true;
            restingTimeElapse = DEFAULT_RESTING_PERIOD;
            mRestingTimer = new CountDownTimer(DEFAULT_RESTING_PERIOD,1000) {
                @Override
                public void onTick(long l) {
                    restingTimeElapse -= 1000;
                    updateRestingTimeDisplay(restingTimeElapse);
                }

                @Override
                public void onFinish() {
                    isResting = false;
                    resetCounterValues();
                }
            }.start();
        }
        else {
            Toast.makeText(activity,"Currently resting mode",Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRestingTimeDisplay(int restingTimeElapse) {
        tvPushCounter.setText(String.valueOf(restingTimeElapse/1000));
        tvTimer.setText("Resting...");
    }

    private void saveCurrentRecord() {
        if(currentSetIndex>=0 && currentSetIndex < 4) {
            logTrainingSet();
        }

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
                isCompleted = true;
                break;
        }

        if(!isCompleted) {
            currentSetIndex++;
            Log.i(Constants.TAG, "Current set index is " + currentSetIndex);
        }
        else {
            // display complete message
            Toast.makeText(activity,"Well done! This training is completed.", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void logTrainingSet() {
        try {
            mTrainingLog.setTotalCount(mTrainingLog.getTotalCount() + currentCount);
            mTrainingLog.setTotalTime(mTrainingLog.getTotalTime() + (int) currentTime);
            application.getDbHelper().getTrainingLogHelper().getDao().createOrUpdate(mTrainingLog);

            mCurrentTrainingSet.setCount(currentCount);
            mCurrentTrainingSet.setTime((int) currentTime);
            mCurrentTrainingSet.setEndDate(new Date());
            application.getDbHelper().getTrainingSetHelper().getDao().createOrUpdate(mCurrentTrainingSet);
        }
        catch (SQLException sqle) {
            Log.e(Constants.TAG,"logTrainingSet", sqle);
        }
    }

    private String getDisplayTimeShort(long currentTime) {
        return Utils.getDisplayTime(currentTime).substring(0,5);
    }

    private void countOne() {
        synchronized (countLock) {
            if(isCompleted) {
                Toast.makeText(activity,"This set is completed", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            else if(isResting) {
                // display cancel resting dialog
                ConfirmDialogFragment dialogFragment = new ConfirmDialogFragment();
                Bundle args = new Bundle();
                args.putString(Constants.TITLE_PARAM, "Resting..");
                args.putString(Constants.MESSAGE_PARAM, "Cancel resting?");
                dialogFragment.setArguments(args);
                dialogFragment.setEventListener(new ConfirmDialogFragment.OnConfirmDialogEvent() {
                    @Override
                    public void yes() {
                        mRestingTimer.cancel();
                        isResting = false;
                        resetCounterValues();
                    }

                    @Override
                    public void no() {
                        // do nothing
                    }
                });
                dialogFragment.show(activity.getFragmentManager(), "ConfirmCancelRestingDialog");
                return;
            }

            if(currentCount==2) { // congrat fragment
                updateMessageFragment(mMessageFragment, Constants.MESSAGE_FRAGMENT_TAG);
            }

            if(currentCount == 4) { // change back to ad fragment
                updateMessageFragment(mAdFragment, Constants.MESSAGE_FRAGMENT_TAG);
            }

            if (currentCount == 0) {
                // start timer
                // TODO: find another mean to calculate time
                startTime = new Date();
                if(currentSetIndex==0) {
                    mTrainingLog.setDateTimeStart(startTime);
                }
                mCurrentTrainingSet = new TrainingSet.Builder(mTrainingLog, currentSetIndex)
                        .count(0)
                        .time(0)
                        .startDate(startTime)
                        .build();
                mTrainingTimer = new CountDownTimer(999999, 25) {
                    @Override
                    public void onTick(long l) {
                        currentTime = (new Date()).getTime() - startTime.getTime();
                        mCurrentTrainingSet.setTime(currentTime);
                        tvTimer.setText(Utils.getDisplayTime(currentTime));
                    }

                    @Override
                    public void onFinish() {
                        saveCurrentRecord();
                        //Toast.makeText(activity,"Timer complete",Toast.LENGTH_SHORT).show();
                    }
                }.start();
            }
            if (!coolingDown) {
                currentCount++;
                mCurrentTrainingSet.setCount(currentCount);
                tvPushCounter.setText(String.valueOf(currentCount));
                coolingDown = true;

                // start a little animation on the button
                mainButtonLayout.startAnimation(bouncingAnimation);

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
            //Log.i(Constants.TAG, "Sensor value: " + sensorEvent.values[0] + ", accuracy:" + sensorEvent.accuracy);
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

    boolean isQuitConfirm = false;

    public boolean onBackPressed() {
        if(!isQuitConfirm && mCurrentTrainingSet!=null) {
            Toast.makeText(activity.getApplicationContext(),"Are you sure to quit training? " +
                    "Press back again to confirm."
                    ,Toast.LENGTH_LONG)
                    .show();
            isQuitConfirm = true;
            // giving 5 seconds to change of mind
            new CountDownTimer(QUIT_CHANGE_OF_MIND_PERIOD,QUIT_CHANGE_OF_MIND_PERIOD) {
                @Override
                public void onTick(long l) {}

                @Override
                public void onFinish() {
                    isQuitConfirm = false;
                }
            }.start();
            return false;
        }
        else {
            return true;
        }
    }
}
