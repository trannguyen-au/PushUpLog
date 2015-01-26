package mobile.wnext.pushupsdiary.activities;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import mobile.wnext.pushupsdiary.Constants;
import mobile.wnext.pushupsdiary.R;

public class IntroductionActivity extends ActionBarActivity {

    Button btnGotIt;
    CheckBox chkDontShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        btnGotIt = (Button) findViewById(R.id.btnNext);
        chkDontShow = (CheckBox)findViewById(R.id.chkDontShow);
        btnGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chkDontShow.isChecked()) {
                    setResult(RESULT_CANCELED);
                }
                else {
                    setResult(RESULT_OK);
                }
                finish();
            }
        });
    }

    private void applyPreferenceDoNotShowAgain(boolean isDoNotShow) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREF_DO_NOT_SHOW_INTRODUCTION, MODE_PRIVATE);
        if(sharedPreferences.contains(Constants.PREF_DO_NOT_SHOW_INTRODUCTION)) {
            sharedPreferences.edit()
                    .putBoolean(Constants.PREF_DO_NOT_SHOW_INTRODUCTION,isDoNotShow)
                    .apply();
        }
        else {
            sharedPreferences.edit()
                    .putBoolean(Constants.PREF_DO_NOT_SHOW_INTRODUCTION,isDoNotShow)
                    .apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_introduction, menu);
        return true;
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

        return super.onOptionsItemSelected(item);
    }
}
