package mobile.wnext.pushupsdiary.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import mobile.wnext.pushupsdiary.PushUpsDiaryApplication;

/**
 * Created by Nnguyen on 15/01/2015.
 */
public abstract class ViewModel {
    protected Activity activity;
    public static PushUpsDiaryApplication application;

    public ViewModel(Activity activity) {
        this.activity = activity;
        if(application==null) {
            application = (PushUpsDiaryApplication) activity.getApplication();
        }
    }

    public void startActivity(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(activity.getApplicationContext(),activityClass);
        activity.startActivity(intent);
    }
    public void startActivityForResult(Class<? extends  Activity> activityClass, int requestCode, Bundle options) {
        Intent intent = new Intent(activity.getApplicationContext(),activityClass);
        activity.startActivityForResult(intent, requestCode, options);
    }

}
