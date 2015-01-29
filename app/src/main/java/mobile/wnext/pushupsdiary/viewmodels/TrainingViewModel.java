package mobile.wnext.pushupsdiary.viewmodels;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.Date;

import mobile.wnext.pushupsdiary.Constants;
import mobile.wnext.pushupsdiary.OnConfirmDialogEvent;
import mobile.wnext.pushupsdiary.R;
import mobile.wnext.pushupsdiary.Utils;
import mobile.wnext.pushupsdiary.activities.SummaryActivity;
import mobile.wnext.pushupsdiary.activities.fragments.AdFragment;
import mobile.wnext.pushupsdiary.activities.fragments.ConfirmDialogFragment;
import mobile.wnext.pushupsdiary.activities.fragments.CongratulationDialogFragment;
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
    private static final Object countLock = new Object(); // count lock for multi-threading control
    private static final String CONFIRM_CANCEL_RESTING_DIALOG_TAG = "ConfirmCancelRestingDialog";
    private static final String CORRECT_COUNT_DIALOG_TAG = "CorrectCountDialog";
    private static final String CONGRATULATION_DIALOG_TAG = "CongratulationDialog";

    // reference variables
    Sensor proximitySensor;
    SensorManager sensorManager;

    // properties & flags
    boolean coolingDown = false;
    boolean isCompleted = false;
    boolean isQuitConfirm = false;

    boolean isMute = false;
    boolean isVibrate = true;

    // resting variables
    boolean isResting = false;
    int restingTimeElapse = Constants.DEFAULT_RESTING_PERIOD;
    CountDownTimer mRestingTimer;

    // view variables
    LinearLayout mainButtonLayout;
    TextView tvPushCounter, tvTimer;
    TextView tvBRSet1,tvBRSet2,tvBRSet3,tvBRSet4;
    TextView tvLRSet1,tvLRSet2,tvLRSet3,tvLRSet4;
    TextView tvCurrentSet1,tvCurrentSet2,tvCurrentSet3,tvCurrentSet4;
    Button btnDoneCurrentSet, btnCompleteTraining;
    ImageButton btnSound, btnVibration;

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
    SharedPreferences mSharedPreferences;;

    // hardware variables
    MediaPlayer player;
    Vibrator vibrator;

    // others
    int bestRecord = 0;

    public TrainingViewModel(Activity context) {
        super(context);
        currentCount = 0;
        bouncingAnimation = AnimationUtils.loadAnimation(activity,
                R.anim.button_bouncing_anim);
        mSharedPreferences = activity.getSharedPreferences(Constants.PREF_APP_PRIVATE, Context.MODE_PRIVATE);
        initializeUI();
        initializeSensorAndHardware();
        loadPreference();

        loadLastRecordValues();
        loadBestRecordValues();

        // create the data model
        mTrainingLog = new TrainingLog.Builder()
                .count(0)
                .time(0)
                .build();

        fragmentManager = ((ActionBarActivity) context).getSupportFragmentManager();
        mMessageFragment = new SimpleMessageFragment();

        if(Constants.ADS_SHOWING_MODE == Constants.ADS_MODE_RELEASE ||
                Constants.ADS_SHOWING_MODE == Constants.ADS_MODE_DEBUG) {
            mAdFragment = new AdFragment();
            updateMessageFragment(mAdFragment, Constants.MESSAGE_FRAGMENT_TAG);
        }
    }

    private void loadPreference() {
        isMute = mSharedPreferences.getBoolean(Constants.PREF_IS_MUTE_TRAINING,false);
        isVibrate = mSharedPreferences.getBoolean(Constants.PREF_IS_VIBRATE_TRAINING,true);

        btnVibration.setImageDrawable(mResources.getDrawable(isVibrate?R.drawable.vibrate_on:R.drawable.vibrate_off));
        btnSound.setImageDrawable(mResources.getDrawable(isMute?R.drawable.sound_off:R.drawable.sound_on));
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
        if(proximitySensor!=null);
            sensorManager.unregisterListener(this, proximitySensor);
    }
    public void resume(){
        Log.i(Constants.TAG,"Training view is resume");
        coolingDown = false; // continue the counter
        if(proximitySensor!=null);
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        if(!vibrator.hasVibrator()) {
            btnVibration.setVisibility(View.GONE);
            isVibrate = false;
        }
    }
    public void stopAndDestroy(){
        Log.i(Constants.TAG,"Training view is destroyed");
        // release resources
        if(player!=null) {
            player.release();
        }
    }

    private void initializeSensorAndHardware() {
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // audio & vibration setting
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        vibrator = (Vibrator) activity.getSystemService(Activity.VIBRATOR_SERVICE);
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

        btnSound = (ImageButton) activity.findViewById(R.id.btnSound);
        btnSound.setOnClickListener(this);
        btnVibration = (ImageButton) activity.findViewById(R.id.btnVibration);
        btnVibration.setOnClickListener(this);

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
                    if(bestRecord<data.getCount()) bestRecord = data.getCount();
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
            Log.e(Constants.TAG, "Cannot access database for best record",sqle);
        }

        Log.i(Constants.TAG, "Best record: "+String.valueOf(bestRecord));
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
        tvTimer.setText("0:00:000");
        currentCount = 0;
        currentTime = 0;
    }

    private void resetAllCounterValues() {
        resetCounterValues();
        tvCurrentSet1.setText("0/0:00");
        tvCurrentSet2.setText("0/0:00");
        tvCurrentSet3.setText("0/0:00");
        tvCurrentSet4.setText("0/0:00");
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
                dialogFragment.show(activity.getFragmentManager(), CORRECT_COUNT_DIALOG_TAG);
            }
            else {
                Toast.makeText(activity, mResources.getString(R.string.please_start_training_first), Toast.LENGTH_SHORT).show();
            }
        }
        else if(view == btnCompleteTraining) {
            finalStepBeforeComplete();
        }
        else if(view == btnSound) {
            toggleSoundConfig();
        }
        else if(view == btnVibration) {
            toggleVibrationConfig();
        }
    }

    private void toggleVibrationConfig() {
        isVibrate = !isVibrate;
        btnVibration.setImageDrawable(mResources.getDrawable(isVibrate?R.drawable.vibrate_on:R.drawable.vibrate_off));

        // save to the preference
        mSharedPreferences.edit()
                .putBoolean(Constants.PREF_IS_VIBRATE_TRAINING, isVibrate)
                .apply();
    }

    private void toggleSoundConfig() {
        isMute = !isMute;
        btnSound.setImageDrawable(mResources.getDrawable(isMute?R.drawable.sound_off:R.drawable.sound_on));

        // save to the preference
        mSharedPreferences.edit()
                .putBoolean(Constants.PREF_IS_MUTE_TRAINING, isMute)
                .apply();
    }

    private void finalStepBeforeComplete() {
        // check if the record is break!
        if(isBreakRecord()) {
            // show congratulation dialog
            CongratulationDialogFragment dialogFragment = new CongratulationDialogFragment();
            Bundle args = new Bundle();
            String message = String.format(mResources.getString(R.string.congrat_message_break_best_record),mTrainingLog.getTotalCount());
            args.putString(Constants.MESSAGE_PARAM, message);
            dialogFragment.setArguments(args);
            dialogFragment.setEventListener(new OnConfirmDialogEvent() {
                @Override
                public void yes() {
                    finalStep();
                }

                @Override
                public void no() {
                    // do nothing
                }
            });
            dialogFragment.show(activity.getFragmentManager(), CONGRATULATION_DIALOG_TAG);
        }
        else {
            finalStep();
        }
    }

    private void finalStep() {
        Bundle showAdExtra = new Bundle();
        showAdExtra.putBoolean(Constants.SHOW_ADS_PARAM, true);
        startActivity(SummaryActivity.class,showAdExtra);
    }

    private boolean isBreakRecord() {
        // TODO: Check if break record
        return false;
    }

    private void startResting() {
        if(!isResting) {
            isResting = true;
            restingTimeElapse = Constants.DEFAULT_RESTING_PERIOD;
            mRestingTimer = new CountDownTimer(Constants.DEFAULT_RESTING_PERIOD, Constants.ONE_SECOND) {
                @Override
                public void onTick(long l) {
                    restingTimeElapse -= Constants.ONE_SECOND;
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
            Toast.makeText(activity,"Resting mode is already started",Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRestingTimeDisplay(int restingTimeElapse) {
        tvPushCounter.setText(String.valueOf(restingTimeElapse/Constants.ONE_SECOND));
        tvTimer.setText(mResources.getString(R.string.resting));
    }

    private void saveCurrentRecord() {
        if(currentSetIndex>=0 && currentSetIndex < 4) {
            logTrainingSet();
        }

        switch (currentSetIndex) {
            case 0:
                tvCurrentSet1.setText(String.valueOf(currentCount)+"/"+getDisplayTimeShort(currentTime));
                tvCurrentSet1.setTextColor(mResources.getColor(R.color.label_value_recorded));
                break;
            case 1:
                tvCurrentSet2.setText(String.valueOf(currentCount)+"/"+getDisplayTimeShort(currentTime));
                tvCurrentSet2.setTextColor(mResources.getColor(R.color.label_value_recorded));
                break;
            case 2:
                tvCurrentSet3.setText(String.valueOf(currentCount)+"/"+getDisplayTimeShort(currentTime));
                tvCurrentSet3.setTextColor(mResources.getColor(R.color.label_value_recorded));
                break;
            case 3:
                tvCurrentSet4.setText(String.valueOf(currentCount)+"/"+getDisplayTimeShort(currentTime));
                tvCurrentSet4.setTextColor(mResources.getColor(R.color.label_value_recorded));
                isCompleted = true;
                break;
        }

        if(!isCompleted) {
            currentSetIndex++;
        }
        else {
            // display complete message
            Toast.makeText(activity, mResources.getString(R.string.well_done_you_have_complete_this_training_session), Toast.LENGTH_SHORT)
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
        return Utils.getDisplayTime(currentTime).substring(0,4);
    }

    private void countOne() {
        synchronized (countLock) {
            if(isCompleted) {
                Toast.makeText(activity,mResources.getString(R.string.training_session_completed), Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            else if(isResting) {
                // display cancel resting dialog
                ConfirmDialogFragment dialogFragment = new ConfirmDialogFragment();
                Bundle args = new Bundle();
                args.putString(Constants.TITLE_PARAM, mResources.getString(R.string.resting));
                args.putString(Constants.MESSAGE_PARAM, mResources.getString(R.string.cancel_resting_question));
                dialogFragment.setArguments(args);
                dialogFragment.setEventListener(new OnConfirmDialogEvent() {
                    @Override
                    public void yes() {
                        mRestingTimer.cancel();
                        isResting = false;
                        resetCounterValues();
                    }

                    @Override
                    public void no() {
                        // do nothing, simply close the dialog as default behavior
                    }
                });
                dialogFragment.show(activity.getFragmentManager(), CONFIRM_CANCEL_RESTING_DIALOG_TAG);
                return;
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
                mTrainingTimer = new CountDownTimer(Constants.ONE_HOUR, 25) {
                    @Override
                    public void onTick(long elapseTime) {
                        if(!isCompleted && !isResting) {
                            //currentTime = (new Date()).getTime() - startTime.getTime();
                            currentTime = Constants.ONE_HOUR - elapseTime;
                            tvTimer.setText(Utils.getDisplayTime(currentTime));
                        }
                    }

                    @Override
                    public void onFinish() {
                        // in the event that the clock run for too long, save the record data upon the clock stop
                        saveCurrentRecord();
                    }
                }.start(); // start the timer clock
            }
            if (!coolingDown) {
                currentCount++;
                mCurrentTrainingSet.setCount(currentCount);
                mCurrentTrainingSet.setTime(currentTime);
                tvPushCounter.setText(String.valueOf(currentCount));

                // if user did more push up than the offset or his own best record
                boolean isNoBestRecord = Constants.NO_ADS_PUSH_UP_COUNT > bestRecord;
                if(isNoBestRecord && currentCount == Constants.NO_ADS_PUSH_UP_COUNT) {
                    Bundle argsMessage = new Bundle();

                    String message = String.format(mResources.getString(R.string.congrat_more_than_offset_push_up),
                            Constants.NO_ADS_PUSH_UP_COUNT);
                    if(mAdFragment!=null) {
                        message += " "+mResources.getString(R.string.congrat_advertisement_is_cleared);
                    }

                    argsMessage.putString(Constants.MESSAGE_PARAM, message);
                    if(!mMessageFragment.isAdded()) {
                        mMessageFragment.setArguments(argsMessage);
                    }
                    else {
                        mMessageFragment.updateMessage(argsMessage);
                    }
                    updateMessageFragment(mMessageFragment, Constants.MESSAGE_FRAGMENT_TAG);
                }
                else if(!isNoBestRecord && currentCount == bestRecord+1) {
                    Bundle argsMessage = new Bundle();

                    String message = mResources.getString(R.string.congrat_beat_best_record);
                    if(mAdFragment!=null) {
                        message += " "+mResources.getString(R.string.congrat_advertisement_is_cleared);
                    }

                    argsMessage.putString(Constants.MESSAGE_PARAM, message);
                    if(!mMessageFragment.isAdded()) {
                        mMessageFragment.setArguments(argsMessage);
                    }
                    else {
                        mMessageFragment.updateMessage(argsMessage);
                    }
                    updateMessageFragment(mMessageFragment, Constants.MESSAGE_FRAGMENT_TAG);
                }

                // play sound and/or vibrate
                if(!isMute) {
                    if(player==null) {
                        player = MediaPlayer.create(activity, R.raw.smw_kick);
                        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                            @Override
                            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                                mediaPlayer.release();
                                player = MediaPlayer.create(activity, R.raw.smw_kick);
                                return true;
                            }
                        });
                    }

                    if(player.isPlaying()) {
                        player.stop();
                        player.seekTo(0);
                    }

                    player.start();
                }
                if(isVibrate && vibrator.hasVibrator()) {
                    vibrator.vibrate(Constants.COOL_DOWN_PERIOD);
                }

                coolingDown = true;

                // start a little animation on the button
                mainButtonLayout.startAnimation(bouncingAnimation);

                // start cooling down for a specified period to prevent accidental pressed which causing
                // the count to be counted as 2
                new CountDownTimer(Constants.COOL_DOWN_PERIOD,Constants.COOL_DOWN_PERIOD) {
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
            if (sensorEvent.values[0] <= Constants.PROXIMITY_MAX_NEAR_DISTANCE) {
                // only count when the value changed to near
                countOne();
            } else {
                //Toast.makeText(activity, "Far", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // ignore this event from the proximity sensor
    }



    public boolean onBackPressed() {
        if(!isQuitConfirm && mCurrentTrainingSet!=null) {
            Toast.makeText(activity,"Are you sure to quit training? " +
                    "Press back again to confirm."
                    ,Toast.LENGTH_LONG)
                    .show();
            isQuitConfirm = true;
            // giving 5 seconds to change of mind
            new CountDownTimer(Constants.QUIT_CHANGE_OF_MIND_PERIOD,Constants.QUIT_CHANGE_OF_MIND_PERIOD) {
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
