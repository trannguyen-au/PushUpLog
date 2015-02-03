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
import java.util.Calendar;
import java.util.List;

import mobile.wnext.pushupsdiary.Constants;
import mobile.wnext.pushupsdiary.OnConfirmDialogEvent;
import mobile.wnext.pushupsdiary.R;
import mobile.wnext.pushupsdiary.Utils;
import mobile.wnext.pushupsdiary.activities.SummaryActivity;
import mobile.wnext.pushupsdiary.activities.fragments.AdFragment;
import mobile.wnext.pushupsdiary.activities.fragments.CongratulationDialogFragment;
import mobile.wnext.pushupsdiary.activities.fragments.CorrectCountDialogFragment;
import mobile.wnext.pushupsdiary.activities.fragments.SimpleMessageFragment;
import mobile.wnext.pushupsdiary.models.PracticeLog;
import mobile.wnext.pushupsdiary.models.PracticeLogHelper;

/**
 * Created by Nnguyen on 27/01/2015.
 */
public class PracticeViewModel extends ViewModel implements View.OnClickListener, SensorEventListener {
    // class variables / constants
    private static final Object lockObject = new Object(); // lock object
    private static final String CONGRATULATION_DIALOG_TAG = "CongratulationDialog";
    private static final String CORRECT_COUNT_DIALOG_TAG = "CorrectCountDialog";

    private static final int BREAK_BEST_RECORD = 0x1;
    private static final int BREAK_LAST_RECORD = 0x2;
    private static final int BREAK_SPEED_LIMIT = 0x4;

    // layout elements
    TextView tvPracticeBest, tvPracticeLast, tvPracticeSpeed, tvPracticeAvgPerDay;
    TextView tvPushUpCount, tvPushUpTimer;
    Button btnComplete;
    ImageButton btnSound, btnVibrate;
    LinearLayout btnMainRound;

    // reference variables
    Sensor proximitySensor;
    SensorManager sensorManager;

    // counter variables
    int mCurrentCount;
    long mCurrentTime;
    CountDownTimer mPushUpTimer;

    // performance summary
    int bestCount;
    int lastCount;
    int speedPerMinute;

    // flags, control
    boolean isMute = false, isVibrate = true;
    boolean coolingDown = false;
    boolean isCompleted = false;
    boolean isQuitConfirm = false;

    // database model
    PracticeLog mPracticeLog;

    // hardware variables
    MediaPlayer player;
    Vibrator vibrator;

    // advertisement / message
    private AdFragment mAdFragment;
    private SimpleMessageFragment mMessageFragment;

    FragmentManager fragmentManager;

    // others
    Animation bouncingAnimation;
    SharedPreferences mSharedPreferences;

    public PracticeViewModel(Activity activity) {
        super(activity);
        mCurrentCount = 0;
        bouncingAnimation = AnimationUtils.loadAnimation(activity,
                R.anim.button_bouncing_anim);
        mSharedPreferences = activity.getSharedPreferences(Constants.PREF_APP_PRIVATE, Context.MODE_PRIVATE);
        initializeUI();
        initializeSensorAndHardware();
        loadSummaryData();
        loadPreference();

        // create data model
        mPracticeLog = new PracticeLog();

        fragmentManager = ((ActionBarActivity) activity).getSupportFragmentManager();

        if(Constants.ADS_SHOWING_MODE == Constants.ADS_MODE_RELEASE ||
                Constants.ADS_SHOWING_MODE == Constants.ADS_MODE_DEBUG) {
            mAdFragment = new AdFragment();
            updateMessageFragment(mAdFragment, Constants.MESSAGE_FRAGMENT_TAG);
        }
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

    private void loadPreference() {
        isMute = mSharedPreferences.getBoolean(Constants.PREF_IS_MUTE_PRACTICE,false);
        isVibrate = mSharedPreferences.getBoolean(Constants.PREF_IS_VIBRATE_PRACTICE,true);

        btnVibrate.setImageDrawable(mResources.getDrawable(isVibrate?R.drawable.vibrate_on:R.drawable.vibrate_off));
        btnSound.setImageDrawable(mResources.getDrawable(isMute?R.drawable.sound_off:R.drawable.sound_on));
    }

    private void loadSummaryData() {
        // reset all figures:
        tvPracticeBest.setText("0");
        tvPracticeLast.setText("0");
        tvPracticeSpeed.setText("0/m");
        tvPracticeAvgPerDay.setText("0/d");

        // load summary data for the top figures
        try {
            PracticeLogHelper practiceLogHelper = application.getDbHelper().getPracticeLogHelper();
            PracticeLog bestRecord = practiceLogHelper.findBestRecord();
            PracticeLog lastRecord = practiceLogHelper.findLastRecord();
            List<PracticeLog> practiceLogList = practiceLogHelper.queryAllGroupByDate();
            int totalPushUpCount = 0;
            int tmpSpeed = 0;
            for (int i=0;i<practiceLogList.size();i++) {
                PracticeLog practiceLogRecord = practiceLogList.get(i);
                totalPushUpCount += practiceLogRecord.getCountPushUps();
                tmpSpeed = (practiceLogRecord.getCountPushUps() * Constants.ONE_MINUTE) / ((int)practiceLogRecord.getCountTime());
                if(tmpSpeed > speedPerMinute)
                    speedPerMinute = tmpSpeed;
            }
            int avgPerDay = 0;
            if(practiceLogList.size()>0) {
                avgPerDay = totalPushUpCount / practiceLogList.size();
            }
            if(bestRecord!=null) {
                bestCount = bestRecord.getCountPushUps();
                tvPracticeBest.setText(String.valueOf(bestCount));
            }
            if(lastRecord!=null) {
                lastCount = lastRecord.getCountPushUps();
                tvPracticeLast.setText(String.valueOf(lastCount));
            }
            tvPracticeSpeed.setText(String.valueOf(speedPerMinute)+"/m");
            tvPracticeAvgPerDay.setText(String.valueOf(avgPerDay+"/d"));

        }
        catch (SQLException sqle) {
            Log.e(Constants.TAG, "loadSummaryData for practice log", sqle);
            Toast.makeText(activity, "Could not load summary data for practice log.",Toast.LENGTH_SHORT).show();
        }
    }



    public void pause(){
        Log.i(Constants.TAG, "Training view is pause");
        coolingDown = true; // prevent the counter
        if(proximitySensor!=null)
            sensorManager.unregisterListener(this, proximitySensor);
    }
    public void resume(){
        Log.i(Constants.TAG,"Training view is resume");
        coolingDown = false; // continue the counter
        if(proximitySensor!=null)
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        if(!vibrator.hasVibrator()) {
            btnVibrate.setVisibility(View.GONE);
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
        tvPracticeBest = (TextView) activity.findViewById(R.id.tvPracticeBest);
        tvPracticeLast = (TextView) activity.findViewById(R.id.tvPracticeLast);
        tvPracticeSpeed = (TextView) activity.findViewById(R.id.tvPracticeSpeed);
        tvPracticeAvgPerDay = (TextView) activity.findViewById(R.id.tvPracticeAvgPerDay);
        tvPushUpCount = (TextView) activity.findViewById(R.id.tvPushUpCount);
        tvPushUpTimer = (TextView) activity.findViewById(R.id.tvPushUpTimer);

        btnMainRound = (LinearLayout) activity.findViewById(R.id.btnMainRound);
        btnMainRound.setOnClickListener(this);

        btnComplete = (Button) activity.findViewById(R.id.btnComplete);
        btnComplete.setOnClickListener(this);
        btnSound = (ImageButton) activity.findViewById(R.id.btnSound);
        btnSound.setOnClickListener(this);
        btnVibrate = (ImageButton) activity.findViewById(R.id.btnVibrate);
        btnVibrate.setOnClickListener(this);
        resetAllCounterValues();
    }

    private void resetAllCounterValues() {
        tvPushUpCount.setText("0");
        tvPushUpTimer.setText("00:00:000");
        mCurrentCount = 0;
        mCurrentTime = 0;
    }

    @Override
    public void onClick(View view) {
        if(view == btnMainRound) {
            countOne();
        }
        else if(view == btnComplete) {
            completePractice();
        }
        else if(view == btnSound) {
            toggleSoundConfig();
        }
        else if(view == btnVibrate) {
            toggleVibrationConfig();
        }
    }

    private void countOne() {
        synchronized (lockObject) {
            if(isCompleted) {
                Toast.makeText(activity, "This is completed",Toast.LENGTH_SHORT).show();
                return;
            }

            if(mCurrentCount ==0 ) {
                // start timer
                mPushUpTimer = new CountDownTimer(Constants.ONE_HOUR,25) {
                    @Override
                    public void onTick(long elapseTime) {
                        if(!isCompleted) {
                            mCurrentTime = Constants.ONE_HOUR - elapseTime;
                            tvPushUpTimer.setText(Utils.getDisplayTime(mCurrentTime));
                        }
                    }

                    @Override
                    public void onFinish() {
                         completePractice();
                    }
                }.start(); // start the timer clock;
            }

            if(!coolingDown) {
                mCurrentCount++;
                mPracticeLog.setCountPushUps(mCurrentCount);
                mPracticeLog.setCountTime((int)mCurrentTime);
                tvPushUpCount.setText(String.valueOf(mCurrentCount));

                // play sound and /or vibrate
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
                btnMainRound.startAnimation(bouncingAnimation);

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

    private void completePractice() {
        isCompleted = true;
        // offer a popup to correct the push up count if any missing during the session
        CorrectCountDialogFragment dialogFragment = new CorrectCountDialogFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.COUNT_PARAM, mCurrentCount);
        dialogFragment.setArguments(args);
        dialogFragment.setEventListener(new CorrectCountDialogFragment.CorrectCountDialogEventListener() {
            @Override
            public void changeCount(int newCount) {
                mCurrentCount = newCount;
            }

            @Override
            public void dialogClosing() {
                // save data to database
                savePracticeLog();
                congratsIfBreakRecord();
            }
        });
        dialogFragment.show(activity.getFragmentManager(), CORRECT_COUNT_DIALOG_TAG);
    }

    int totalDisplayingDialog = 0;

    private void congratsIfBreakRecord() {

        boolean isBreakBestRecord =  false, isBreakLastRecord = false, isBreakSpeedLimit = false;
        if(mCurrentCount > bestCount) isBreakBestRecord = true;
        else if(mCurrentCount > lastCount) isBreakLastRecord = true;

        int estimateSpeed = ((mCurrentCount * Constants.ONE_MINUTE) / (int)mCurrentTime);
        if(estimateSpeed > speedPerMinute) isBreakSpeedLimit = true;

        if(isBreakBestRecord || isBreakLastRecord || isBreakSpeedLimit) {
            String message = "";
            if(isBreakBestRecord) {
                message = String.format(mResources.getString(R.string.congrat_message_break_best_record),mPracticeLog.getCountPushUps());
                totalDisplayingDialog++;
                showCongratulationDialog(message);
            }
            else if(isBreakLastRecord) {
                message = String.format(mResources.getString(R.string.congrat_message_break_last_record));
                totalDisplayingDialog++;
                showCongratulationDialog(message);
            }

            if(isBreakSpeedLimit) {
                message = String.format(mResources.getString(R.string.congrat_message_new_power_limit),estimateSpeed);
                totalDisplayingDialog++;
                showCongratulationDialog(message);
            }
        }
        else {
            finalStep();
        }
    }

    private void finalStep() {
        // TODO: what to do in the final step of practice mode?
        // show result activity
        Bundle showAdExtra = new Bundle();
        showAdExtra.putBoolean(Constants.SHOW_ADS_PARAM, true);
        startActivity(SummaryActivity.class, showAdExtra);
    }

    private void showCongratulationDialog(String message) {
        CongratulationDialogFragment dialogFragment = new CongratulationDialogFragment();
        Bundle args = new Bundle();
        args.putString(Constants.MESSAGE_PARAM, message);
        dialogFragment.setArguments(args);
        dialogFragment.setEventListener(new OnConfirmDialogEvent() {
            @Override
            public void yes() {
                totalDisplayingDialog--;
                if(totalDisplayingDialog==0)
                    finalStep();
            }

            @Override
            public void no() {
                // do nothing
            }
        });
        dialogFragment.show(activity.getFragmentManager(), CONGRATULATION_DIALOG_TAG);
    }

    private void savePracticeLog() {
        try {
            // set the time that this practice log should be saved
            mPracticeLog.setLogDate(Calendar.getInstance().getTime());
            application.getDbHelper().getPracticeLogHelper().getDao().createOrUpdate(mPracticeLog);
        } catch (SQLException sqle) {
            Log.e(Constants.TAG, "save Practice Log", sqle);
        }
    }

    private void toggleVibrationConfig() {
        isVibrate = !isVibrate;

        btnVibrate.setImageDrawable(mResources.getDrawable(isVibrate?
                R.drawable.vibrate_on:
                R.drawable.vibrate_off));

        // save to the preference
        mSharedPreferences.edit()
                .putBoolean(Constants.PREF_IS_VIBRATE_PRACTICE, isVibrate)
                .apply();
    }

    private void toggleSoundConfig() {
        isMute = !isMute;

        btnSound.setImageDrawable(mResources.getDrawable(isMute?
                R.drawable.sound_off:
                R.drawable.sound_on));

        // save to the preference
        mSharedPreferences.edit()
                .putBoolean(Constants.PREF_IS_MUTE_PRACTICE, isMute)
                .apply();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
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
        if(!isQuitConfirm && mPracticeLog.getCountPushUps() > 0) {
            Toast.makeText(activity,"Back button is pressed accidentally? " +
                    "If not, press back again to quit free style practice."
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
