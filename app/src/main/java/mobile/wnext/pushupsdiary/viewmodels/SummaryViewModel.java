package mobile.wnext.pushupsdiary.viewmodels;

import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import mobile.wnext.pushupsdiary.R;
import mobile.wnext.pushupsdiary.adapters.SummaryTabPagerAdapter;

/**
 * Created by Nnguyen on 13/01/2015.
 */
public class SummaryViewModel extends ViewModel {

    // view properties

    SummaryTabPagerAdapter mSummaryTabPagerAdapter;
    ViewPager mViewPager;
    private ActionBar actionBar;

    public SummaryViewModel(FragmentActivity context) {
        super(context);
        initializeUI();
    }

    private void initializeUI() {
        actionBar = ((ActionBarActivity)activity).getSupportActionBar();
        mSummaryTabPagerAdapter = new SummaryTabPagerAdapter(((FragmentActivity)activity).getSupportFragmentManager());
        mViewPager = (ViewPager)activity.findViewById(R.id.summaryPager);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if(actionBar!=null)
                    actionBar.setSelectedNavigationItem(position);
            }
        });
        mViewPager.setAdapter(mSummaryTabPagerAdapter);

        // enable tabs on action bar
        if(actionBar!=null) {

            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            ActionBar.TabListener tabListener = new ActionBar.TabListener() {
                @Override
                public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
                    mViewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

                }
            };

            // add new tabs
            actionBar.addTab(actionBar.newTab().setText("Weekly").setTabListener(tabListener));
            actionBar.addTab(actionBar.newTab().setText("Monthly").setTabListener(tabListener));
            actionBar.addTab(actionBar.newTab().setText("Yearly").setTabListener(tabListener));
        }


    }


}
