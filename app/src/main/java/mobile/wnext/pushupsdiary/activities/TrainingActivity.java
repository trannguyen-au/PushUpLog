package mobile.wnext.pushupsdiary.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import mobile.wnext.pushupsdiary.R;
import mobile.wnext.pushupsdiary.viewmodels.TrainingViewModel;

public class TrainingActivity extends ActionBarActivity {

    TrainingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        viewModel = new TrainingViewModel(this);
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
