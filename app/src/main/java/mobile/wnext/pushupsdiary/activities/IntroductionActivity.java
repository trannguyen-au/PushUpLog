package mobile.wnext.pushupsdiary.activities;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import mobile.wnext.pushupsdiary.OnIntroActionListener;
import mobile.wnext.pushupsdiary.R;
import mobile.wnext.pushupsdiary.adapters.IntroductionScreenPagerAdapter;

public class IntroductionActivity extends ActionBarActivity {

    ViewPager mViewPager;
    private ActionBar actionBar;
    IntroductionScreenPagerAdapter mPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction_view_pager);
        initializeUI();
    }

    private void initializeUI() {
        mViewPager = (ViewPager) findViewById(R.id.introductionViewPager);
        mPagerAdapter = new IntroductionScreenPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.setOnIntroActionListener(new OnIntroActionListener() {
            @Override
            public void nextButtonClicked() {
                if(mViewPager.getCurrentItem() < mViewPager.getAdapter().getCount()-1) { // not the last item
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1,true);
                }
                else {
                    IntroductionActivity.this.finish();
                }
            }
        });
        mViewPager.setAdapter(mPagerAdapter);
        // hide the action bar
        actionBar = getSupportActionBar();
        actionBar.hide();
    }
}
