package mobile.wnext.pushupsdiary;

import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mobile.wnext.pushupsdiary.models.DatabaseHelper;

/**
 * Created by Nnguyen on 13/01/2015.
 */
public class PushUpsDiaryApplication extends Application {
    DatabaseHelper dbHelper;

    public DatabaseHelper getDbHelper(){
        return dbHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DatabaseHelper(this);
    }

    @Override
    public void onTerminate() {
        if(dbHelper.isOpen()) dbHelper.close();
        dbHelper = null;
        super.onTerminate();
    }

    public void checkIfTrialExpired() {
        try {
            long firstInstalledTime = getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(getApplicationContext().getPackageName(), 0)
                    .firstInstallTime;

            Calendar installedDate = Calendar.getInstance();
            installedDate.setTimeInMillis(firstInstalledTime);

            Calendar currentDate = Calendar.getInstance();
            long usingPeriod = currentDate.getTimeInMillis() - installedDate.getTimeInMillis();

            long expiredPeriod = Constants.AD_FREE_PERIOD * Constants.ONE_DAY;
            if(usingPeriod < expiredPeriod) Constants.ADS_SHOWING_MODE = Constants.ADS_MODE_DISABLED;
            else Constants.ADS_SHOWING_MODE = Constants.ADS_MODE_RELEASE;

            Log.i(Constants.TAG, "Using period: "+usingPeriod);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
}
