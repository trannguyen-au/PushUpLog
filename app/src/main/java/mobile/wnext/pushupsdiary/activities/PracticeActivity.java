package mobile.wnext.pushupsdiary.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import mobile.wnext.pushupsdiary.Constants;
import mobile.wnext.pushupsdiary.R;
import mobile.wnext.pushupsdiary.viewmodels.PracticeViewModel;

public class PracticeActivity extends ActionBarActivity {
    PracticeViewModel mViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewModel = new PracticeViewModel(this);

        // start the introduction activity
        if(!isSkipIntro()) {
            Intent intentIntro = new Intent(getApplicationContext(), IntroductionActivity.class);
            startActivityForResult(intentIntro,0);
        }
    }

    private boolean isSkipIntro() {
        return getSharedPreferences(Constants.PREF_APP_PRIVATE, MODE_PRIVATE)
                .getBoolean(Constants.PREF_DO_NOT_SHOW_INTRODUCTION, false);
    }

    private void setSkipIntro(boolean isSkipIntro) {
        getSharedPreferences(Constants.PREF_APP_PRIVATE, MODE_PRIVATE)
                .edit()
                .putBoolean(Constants.PREF_DO_NOT_SHOW_INTRODUCTION, isSkipIntro)
                .apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0) {
            if(resultCode == RESULT_CANCELED) {
                setSkipIntro(true);
            }
        }
    }


    @Override
    public void onPause(){
        super.onPause();
        mViewModel.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.stopAndDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.resume();
    }

    @Override
    public void onBackPressed() {
        if(mViewModel.onBackPressed()) {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent aboutIntent = new Intent(getApplicationContext(),AboutActivity.class);
            startActivity(aboutIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
