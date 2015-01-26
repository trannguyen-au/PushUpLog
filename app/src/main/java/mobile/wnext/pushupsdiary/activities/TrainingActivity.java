package mobile.wnext.pushupsdiary.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import mobile.wnext.pushupsdiary.Constants;
import mobile.wnext.pushupsdiary.R;
import mobile.wnext.pushupsdiary.viewmodels.TrainingViewModel;

public class TrainingActivity extends ActionBarActivity {

    TrainingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewModel = new TrainingViewModel(this);

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
    public void onResume(){
        viewModel.resume();
        super.onResume();
    }

    @Override
    public void onPause(){
        viewModel.pause();
        super.onPause();
    }

    @Override
    public void onDestroy(){
        viewModel.stopAndDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_training, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(viewModel.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
