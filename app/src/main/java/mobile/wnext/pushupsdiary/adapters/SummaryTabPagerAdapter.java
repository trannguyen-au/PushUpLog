package mobile.wnext.pushupsdiary.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import mobile.wnext.pushupsdiary.activities.SummaryActivity;
import mobile.wnext.pushupsdiary.activities.fragments.MonthlySummaryFragment;
import mobile.wnext.pushupsdiary.activities.fragments.WeeklySummaryFragment;
import mobile.wnext.pushupsdiary.activities.fragments.YearlySummaryFragment;

/**
 * Created by Nnguyen on 16/01/2015.
 */
public class SummaryTabPagerAdapter extends FragmentStatePagerAdapter {

    MonthlySummaryFragment mMonthlySummaryFragment;
    WeeklySummaryFragment mWeeklySummaryFragment;
    YearlySummaryFragment mYearlySummaryFragment;

    public SummaryTabPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        mMonthlySummaryFragment = new MonthlySummaryFragment();
        mWeeklySummaryFragment = new WeeklySummaryFragment();
        mYearlySummaryFragment = new YearlySummaryFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mWeeklySummaryFragment;
            case 1:
                return mMonthlySummaryFragment;
            case 2:
                return mYearlySummaryFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
