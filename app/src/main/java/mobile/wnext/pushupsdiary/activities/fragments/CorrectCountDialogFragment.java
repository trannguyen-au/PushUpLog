package mobile.wnext.pushupsdiary.activities.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import mobile.wnext.pushupsdiary.Constants;
import mobile.wnext.pushupsdiary.R;

/**
 * Created by Nnguyen on 19/01/2015.
 */
public class CorrectCountDialogFragment extends DialogFragment
implements View.OnClickListener{


    View mMainView;
    Button btnMinus, btnPlus;
    TextView tvCount;
    int mCurrentCount = 0;


    // required to passed in the current count as an argument in bundle format
    public CorrectCountDialogFragment() {
        super();
        Bundle args = getArguments();
        if(args!=null && args.containsKey(Constants.COUNT_PARAM)) {
            mCurrentCount = args.getInt(Constants.COUNT_PARAM);
        }
        else {
            throw new IllegalArgumentException("Count parameter is required");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if(mMainView == null ) {
            mMainView = inflater.inflate(R.layout.dialog_correct_count, null, false);
            btnMinus = (Button)mMainView.findViewById(R.id.btnMinus);
            btnPlus = (Button)mMainView.findViewById(R.id.btnPlus);
            tvCount = (TextView)mMainView.findViewById(R.id.tvCount);

            btnMinus.setOnClickListener(this);
            btnPlus.setOnClickListener(this);
            tvCount.setText(mCurrentCount);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
        .setView(mMainView);
        return builder.create();
    }

    @Override
    public void onClick(View view) {
        if(view == btnPlus) {
            mCurrentCount++;
            tvCount.setText(mCurrentCount);
        }
        else if(view == btnMinus) {
            mCurrentCount--;
            tvCount.setText(mCurrentCount);
        }
    }
}
