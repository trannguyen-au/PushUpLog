package mobile.wnext.pushupsdiary.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import mobile.wnext.pushupsdiary.R;


public class StartActivity extends ActionBarActivity implements View.OnClickListener {

    Button btnStartTraining, btnLog, btnPractice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        initializeUI();
    }

    private void initializeUI() {
        btnStartTraining = (Button) findViewById(R.id.btnStartTraining);
        btnLog = (Button) findViewById(R.id.btnLog);
        btnPractice = (Button) findViewById(R.id.btnPractice);

        btnStartTraining.setOnClickListener(this);
        btnLog.setOnClickListener(this);
        btnPractice.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
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

    @Override
    public void onClick(View view) {
        if(view == btnStartTraining) {
            Intent trainingIntent = new Intent(getApplicationContext(), TrainingActivity.class);
            startActivity(trainingIntent);
        }
        else if(view == btnPractice) {

        }
        else if(view == btnLog) {

        }
    }
}
