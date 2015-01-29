package mobile.wnext.pushupsdiary.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import mobile.wnext.pushupsdiary.activities.SummaryActivity;
import mobile.wnext.pushupsdiary.activities.fragments.MonthlySummaryFragment;
import mobile.wnext.pushupsdiary.activities.fragments.WeeklySummaryFragment;
import mobile.wnext.pushupsdiary.activities.fragments.YearlySummaryFragment;

/**
 * Created by Nnguyen on 16/01/2015.
 */
public class SummaryTabPagerAdapter extends FragmentStatePagerAdapter {

    List<Fragment> mFragments;

    public SummaryTabPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        mFragments = new ArrayList<>();
        mFragments.add(0,new WeeklySummaryFragment());
        mFragments.add(1,new MonthlySummaryFragment());
        mFragments.add(2,new YearlySummaryFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
