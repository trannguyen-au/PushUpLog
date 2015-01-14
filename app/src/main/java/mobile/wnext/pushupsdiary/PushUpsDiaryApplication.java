package mobile.wnext.pushupsdiary;

import android.app.Application;

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
}
