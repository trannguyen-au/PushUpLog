package mobile.wnext.pushupsdiary.activities.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import mobile.wnext.pushupsdiary.Constants;
import mobile.wnext.pushupsdiary.OnConfirmDialogEvent;
import mobile.wnext.pushupsdiary.R;

/**
 * Created by Nnguyen on 23/01/2015.
 */
public class ConfirmDialogFragment extends DialogFragment {

    private OnConfirmDialogEvent eventListener;
    private String mTitle, mMessage;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        if(args!=null && args.containsKey(Constants.TITLE_PARAM) && args.containsKey(Constants.MESSAGE_PARAM)) {
            mTitle = args.getString(Constants.TITLE_PARAM);
            mMessage = args.getString(Constants.MESSAGE_PARAM);
        }
        else {
            throw new IllegalArgumentException("Title and message parameters are required");
        }

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle)
                .setMessage(mMessage)
                .setPositiveButton(getActivity().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(eventListener!=null) eventListener.yes();
                    }
                })
                .setNegativeButton(getActivity().getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(eventListener!=null) eventListener.no();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setEventListener(OnConfirmDialogEvent eventListener) {
        this.eventListener = eventListener;
    }
}