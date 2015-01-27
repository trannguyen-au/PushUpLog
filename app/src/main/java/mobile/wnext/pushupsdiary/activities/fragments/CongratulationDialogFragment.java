package mobile.wnext.pushupsdiary.activities.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import mobile.wnext.pushupsdiary.Constants;
import mobile.wnext.pushupsdiary.OnConfirmDialogEvent;
import mobile.wnext.pushupsdiary.R;

/**
 * Created by Nnguyen on 27/01/2015.
 */
public class CongratulationDialogFragment extends DialogFragment {

    OnConfirmDialogEvent eventListener;
    int newBestRecord;

    Dialog dialog;
    View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if(args!=null && args.containsKey(Constants.BEST_TOTAL_RECORD_PARAM)) {
            newBestRecord = args.getInt(Constants.BEST_TOTAL_RECORD_PARAM);
        }
        else {
            throw new IllegalArgumentException("Best new push ups record parameter is required");
        }

        // Create the AlertDialog object and return it
        if(dialog==null) {
            dialog = new Dialog(this.getActivity());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        if(view == null)
        {
            view = dialog.getLayoutInflater().inflate(
                    R.layout.fragment_congratulation_dialog_content,
                    null, false);
        }
        ImageView ivMainImage = (ImageView)view.findViewById(R.id.ivThumbUp);
        ivMainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eventListener != null) eventListener.yes();
                dismiss();
            }
        });

        TextView tvMessage = (TextView)view.findViewById(R.id.tvMessage);
        tvMessage.setText(String.format(getResources().getString(R.string.congratulation_message), newBestRecord));
        dialog.setContentView(view);

        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle arg) {
        super.onActivityCreated(arg);

        // allow custom dialog animation
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    public void setEventListener(OnConfirmDialogEvent onConfirmDialogEvent) {
        eventListener = onConfirmDialogEvent;
    }
}
