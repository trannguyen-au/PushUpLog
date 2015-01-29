package mobile.wnext.pushupsdiary.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import mobile.wnext.pushupsdiary.OnIntroActionListener;
import mobile.wnext.pushupsdiary.activities.fragments.IntroPage1Fragment;
import mobile.wnext.pushupsdiary.activities.fragments.IntroPage2Fragment;

/**
 * Created by Nnguyen on 29/01/2015.
 */
public class IntroductionScreenPagerAdapter extends FragmentStatePagerAdapter {

    List<Fragment> mFragments;

    public IntroductionScreenPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        mFragments = new ArrayList<>();
        mFragments.add(0, new IntroPage1Fragment());
        mFragments.add(1, new IntroPage2Fragment());
    }

    public void setOnIntroActionListener(OnIntroActionListener listener) {
        ((IntroPage1Fragment)mFragments.get(0)).setOnIntroActionListener(listener);
        ((IntroPage2Fragment)mFragments.get(1)).setOnIntroActionListener(listener);
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
