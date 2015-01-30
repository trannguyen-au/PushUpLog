package mobile.wnext.pushupsdiary;

/**
 * Created by Nnguyen on 12/01/2015.
 */
public class Constants {
    public static final String TAG = "PUSH_UPS_DIARY";
    public static final String COUNT_PARAM = "COUNT";
    public static final String TITLE_PARAM = "TITLE";
    public static final String MESSAGE_PARAM = "MESSAGE";
    public static final String DRAWABLE_ID_IMAGE_PARAM = "DRAWABLE_ID_IMAGE";
    public static final String BEST_TOTAL_RECORD_PARAM = "BEST_TOTAL_RECORD";
    public static final String SHOW_ADS_PARAM = "SHOW_ADS";

    public static final String MESSAGE_FRAGMENT_TAG = "MESSAGE_FRAGMENT";

    public static final String PREF_APP_PRIVATE = "mobile.wnext.pushupsdiary.PREF";
    public static final String PREF_DO_NOT_SHOW_INTRODUCTION = "mobile.wnext.pushupsdiary.PREF.DO_NOT_SHOW_INTRODUCTION";
    public static final String PREF_IS_MUTE_TRAINING = "mobile.wnext.pushupsdiary.PREF.IS_MUTE_TRAINING";
    public static final String PREF_IS_VIBRATE_TRAINING = "mobile.wnext.pushupsdiary.PREF.IS_VIBRATE_TRAINING";
    public static final String PREF_IS_MUTE_PRACTICE = "mobile.wnext.pushupsdiary.PREF.IS_MUTE_PRACTICE";
    public static final String PREF_IS_VIBRATE_PRACTICE = "mobile.wnext.pushupsdiary.PREF.IS_VIBRATE_PRACTICE";

    public static final int ONE_SECOND = 1000;
    public static final int ONE_MINUTE = 60000;
    public static final int ONE_HOUR = 3600000;
    public static final int ONE_DAY = 86400000;
    public static final int COOL_DOWN_PERIOD = 350;
    public static final int PROXIMITY_MAX_NEAR_DISTANCE = 3;
    public static final int DEFAULT_RESTING_PERIOD = 60000; // 60 seconds
    public static final int QUIT_CHANGE_OF_MIND_PERIOD = 4000; //4 seconds
    public static final int NO_ADS_PUSH_UP_COUNT = 30;

    //TODO: Change ADS SHOWING MODE to 1 (RELEASE) on release
    public static int ADS_SHOWING_MODE = 1;
    public static final int ADS_MODE_DEBUG = 0;
    public static final int ADS_MODE_RELEASE = 1;
    public static final int ADS_MODE_DISABLED = 2;

    public static final int CHART_ANIMATE_X = 700;
    public static final int CHART_ANIMATE_Y = 1000;

    //TODO: Change to false on release
    public static boolean IS_DEBUG = false;

    public static final int AD_FREE_PERIOD = 3; // day value
    public static final String PRO_PACKAGE_NAME = "mobile.wnext.pushupsdiarypro";


}
