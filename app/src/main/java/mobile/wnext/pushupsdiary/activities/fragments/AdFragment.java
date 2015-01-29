package mobile.wnext.pushupsdiary.activities.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import mobile.wnext.pushupsdiary.Constants;
import mobile.wnext.pushupsdiary.R;

/**
 * Created by Nnguyen on 23/01/2015.
 */
public class AdFragment extends Fragment {
    AdView mAdView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ad, container, false);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        mAdView = (AdView) getView().findViewById(R.id.adView);
        AdRequest adRequest = null;
        if(Constants.ADS_SHOWING_MODE == Constants.ADS_MODE_DEBUG) {
            adRequest = new AdRequest.Builder()
                    .addTestDevice("F8DC72B4D101702C78B8BD905A34FAE9")
                    .build();
        }
        else if(Constants.ADS_SHOWING_MODE == Constants.ADS_MODE_RELEASE) {
            adRequest = new AdRequest.Builder()
                    .build();
        }
        if(adRequest!=null) {
            mAdView.loadAd(adRequest);
        }
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
