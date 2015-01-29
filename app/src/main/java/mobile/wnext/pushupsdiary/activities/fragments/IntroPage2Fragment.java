package mobile.wnext.pushupsdiary.activities.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import mobile.wnext.pushupsdiary.Constants;
import mobile.wnext.pushupsdiary.R;
import mobile.wnext.pushupsdiary.OnIntroActionListener;

/**
 * Created by Nnguyen on 29/01/2015.
 */
public class IntroPage2Fragment extends Fragment {
    View mView;
    Button btnGotIt;
    CheckBox chkDontShow;
    SharedPreferences sharedPreferences;
    OnIntroActionListener mOnIntroActionListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_introduction_page_2, container, false);
        btnGotIt = (Button) mView.findViewById(R.id.btnNext);
        chkDontShow = (CheckBox)mView.findViewById(R.id.chkDontShow);
        sharedPreferences = getActivity().getSharedPreferences(Constants.PREF_APP_PRIVATE, Context.MODE_PRIVATE);

        chkDontShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean doNotShow) {
                sharedPreferences.edit()
                        .putBoolean(Constants.PREF_DO_NOT_SHOW_INTRODUCTION, doNotShow)
                        .apply();
            }
        });

        btnGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnIntroActionListener !=null)
                    mOnIntroActionListener.nextButtonClicked();
            }
        });
        return mView;
    }

    public void setOnIntroActionListener(OnIntroActionListener onIntroActionListener) {
        mOnIntroActionListener = onIntroActionListener;
    }
}
