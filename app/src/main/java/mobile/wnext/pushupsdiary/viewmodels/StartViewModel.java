package mobile.wnext.pushupsdiary.viewmodels;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mobile.wnext.pushupsdiary.Constants;
import mobile.wnext.pushupsdiary.R;
import mobile.wnext.pushupsdiary.Utils;
import mobile.wnext.pushupsdiary.activities.PracticeActivity;
import mobile.wnext.pushupsdiary.activities.SummaryActivity;
import mobile.wnext.pushupsdiary.activities.TrainingActivity;
import mobile.wnext.pushupsdiary.models.PracticeLog;
import mobile.wnext.pushupsdiary.models.TrainingSet;
import mobile.wnext.utils.AndroidDatabaseManager;

/**
 * Created by Nnguyen on 15/01/2015.
 */
public class StartViewModel extends ViewModel implements View.OnClickListener {
    // view reference
    TextView tvBest, tvLast, tvTotal, tvTime;
    Button btnStartTraining, btnPractice, btnLog;
    ImageView ivLogo;
    Animation mAnimation;

    int easterEggCount = 5;

    public StartViewModel(Activity context) {
        super(context);
        initializeUI();
        mAnimation = AnimationUtils.loadAnimation(activity,
                R.anim.button_bouncing_anim);
    }

    private void loadSummaryData() {
        int totalPushUps = 0;
        int bestPushUps = 0;
        long totalTime = 0;
        int lastPushup = 0;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(0);
        Date lastPushupDate = c.getTime();
        try {
            List<PracticeLog> allPractice = application.getDbHelper().getPracticeLogHelper().getDao().queryForAll();
            if(allPractice!=null && allPractice.size()>0) {
                for (PracticeLog log : allPractice) {
                    totalPushUps += log.getCountPushUps();
                    totalTime += log.getCountTime();
                    if(bestPushUps<log.getCountPushUps()) {
                        bestPushUps = log.getCountPushUps();
                    }

                    if(lastPushupDate.before(log.getLogDate())) {
                        lastPushupDate = log.getLogDate();
                        lastPushup = log.getCountPushUps();
                    }
                }
            }

            List<TrainingSet> allSets = application.getDbHelper().getTrainingSetHelper().getDao().queryForAll();
            if(allSets!=null && allSets.size()>0) {
                for (TrainingSet log : allSets) {
                    totalPushUps += log.getCount();
                    totalTime += log.getTime();
                    if(bestPushUps<log.getCount()) {
                        bestPushUps = log.getCount();
                    }

                    if(lastPushupDate.before(log.getStartDate())) {
                        lastPushupDate = log.getStartDate();
                        lastPushup = log.getCount();
                    }
                }
            }

            tvTotal.setText(String.valueOf(totalPushUps));
            tvBest.setText(String.valueOf(bestPushUps));
            tvTime.setText(String.valueOf(Utils.getTimeSpanDisplay(totalTime)));
            tvLast.setText(String.valueOf(lastPushup));
        }
        catch (SQLException sqle) {
            Log.e(Constants.TAG, "db error",sqle);
        }
    }

    public void refreshData() {
        loadSummaryData();
    }

    private void initializeUI() {
        tvBest = (TextView) activity.findViewById(R.id.tvBest);
        tvLast= (TextView) activity.findViewById(R.id.tvLast);
        tvTime= (TextView) activity.findViewById(R.id.tvTime);
        tvTotal= (TextView) activity.findViewById(R.id.tvTotal);

        btnStartTraining = (Button) activity.findViewById(R.id.btnStartTraining);
        btnStartTraining.setOnClickListener(this);

        btnPractice= (Button) activity.findViewById(R.id.btnPractice);
        btnPractice.setOnClickListener(this);

        btnLog = (Button) activity.findViewById(R.id.btnLog);
        btnLog.setOnClickListener(this);

        // TODO: remove this debug tool on release
        ivLogo = (ImageView) activity.findViewById(R.id.ivLogo);
        ivLogo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == btnStartTraining) {
            startActivity(TrainingActivity.class);
        }
        else if(view == btnPractice) {
            startActivity(PracticeActivity.class);
        }
        else if(view == btnLog) {
            startActivity(SummaryActivity.class);
        }
        else if(view ==ivLogo) {
            if(Constants.IS_DEBUG) {
                startActivity(AndroidDatabaseManager.class);
            }
            else {
                /*easterEggCount--;
                if(easterEggCount <= 0) {
                    easterEggCount = 5;
                    new Runnable() {

                        @Override
                        public void run() {
                            ivLogo.startAnimation(mAnimation);
                        }
                    }.run();

                    tvBest.startAnimation(mAnimation);
                    tvLast.startAnimation(mAnimation);
                    tvTotal.startAnimation(mAnimation);
                    tvTime.startAnimation(mAnimation);
                }*/
            }
        }
    }
}
