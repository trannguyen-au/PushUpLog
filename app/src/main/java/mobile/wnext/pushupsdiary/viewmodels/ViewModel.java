package mobile.wnext.pushupsdiary.viewmodels;

import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import mobile.wnext.pushupsdiary.PushUpsDiaryApplication;

/**
 * Created by Nnguyen on 15/01/2015.
 */
public abstract class ViewModel {
    public static PushUpsDiaryApplication application;

    protected Activity activity;
    protected ActionBar mActionBar;
    protected Resources mResources;

    public ViewModel(Activity activity) {
        this.activity = activity;
        if(application==null) {
            application = (PushUpsDiaryApplication) activity.getApplication();
        }

        if(activity.getClass() == ActionBarActivity.class) {
            mActionBar = ((ActionBarActivity)activity).getSupportActionBar();
        }

        mResources = activity.getResources();
    }

    public void startActivity(Class<? extends Activity> activityClass) {
        startActivity(activityClass, null);
    }

    public void startActivity(Class<? extends Activity> activityClass, Bundle extra) {
        Intent intent = new Intent(activity.getApplicationContext(),activityClass);
        if(extra!=null) {
            intent.putExtras(extra);
        }
        activity.startActivity(intent);
    }

    public void startActivityForResult(Class<? extends  Activity> activityClass, int requestCode, Bundle options) {
        Intent intent = new Intent(activity.getApplicationContext(),activityClass);
        activity.startActivityForResult(intent, requestCode, options);
    }

}
