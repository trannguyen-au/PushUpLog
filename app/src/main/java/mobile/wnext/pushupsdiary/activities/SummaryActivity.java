package mobile.wnext.pushupsdiary.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import mobile.wnext.pushupsdiary.Constants;
import mobile.wnext.pushupsdiary.R;
import mobile.wnext.pushupsdiary.viewmodels.SummaryViewModel;

public class SummaryActivity extends ActionBarActivity {

    SummaryViewModel summaryViewModel;
    // display ads before exit
    private InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        summaryViewModel = new SummaryViewModel(this);

        Intent callingIntent = getIntent();
        if(callingIntent!=null && callingIntent.getExtras()!=null && callingIntent.getExtras().containsKey(Constants.SHOW_ADS_PARAM)) {
            loadAdRequest();
        }
    }

    private void loadAdRequest() {
        // Create the interstitial.
        interstitial = new InterstitialAd(this);
        if(Constants.ADS_SHOWING_MODE == Constants.ADS_MODE_DEBUG) {
            interstitial.setAdUnitId(getString(R.string.ad_interstitial_test_unit_id));
        }
        else if(Constants.ADS_SHOWING_MODE == Constants.ADS_MODE_RELEASE) {
            interstitial.setAdUnitId(getString(R.string.ad_interstitial_unit_id));
        }
        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                SummaryActivity.this.finish();
            }
        });

        // Create ad request.
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        // Begin loading your interstitial.
        if(Constants.ADS_SHOWING_MODE != Constants.ADS_MODE_DISABLED) {
            interstitial.loadAd(adRequest);
        }
    }

    // Invoke displayInterstitial() when you are ready to display an interstitial.
    public boolean displayInterstitial() {
        if (interstitial!=null && interstitial.isLoaded()) {
            interstitial.show();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if(!displayInterstitial()){
            super.onBackPressed();
            summaryViewModel.returnToMain();
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
        else if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
