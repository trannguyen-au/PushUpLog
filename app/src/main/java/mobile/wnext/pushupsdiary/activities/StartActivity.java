package mobile.wnext.pushupsdiary.activities;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import mobile.wnext.pushupsdiary.R;
import mobile.wnext.pushupsdiary.viewmodels.StartViewModel;


public class StartActivity extends ActionBarActivity {

    StartViewModel startViewModel;
    // display ads before exit
    private InterstitialAd interstitial;

    private void loadAdRequest() {
        // Create the interstitial.
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getString(R.string.ad_unit_id));
        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                exitApp();
            }
        });

        // Create ad request.
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        // Begin loading your interstitial.
        //interstitial.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {
        if(!displayInterstitial()){
            super.onBackPressed();
        }
    }

    // Invoke displayInterstitial() when you are ready to display an interstitial.
    public boolean displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
            return true;
        } else {
            return false;
        }
    }

    private void exitApp() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        loadAdRequest();
        startViewModel = new StartViewModel(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        startViewModel.refreshData();
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
}
