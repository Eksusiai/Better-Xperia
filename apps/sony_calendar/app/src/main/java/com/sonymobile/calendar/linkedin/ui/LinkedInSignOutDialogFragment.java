package com.sonymobile.calendar.linkedin.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.linkedin.LinkedInUtils;

/* JADX INFO: loaded from: classes2.dex */
public class LinkedInSignOutDialogFragment extends DialogFragment {
    public static final String TAG = "LinkedInSignOutDialogFragment";
    private LinkedInSignOutListener mParent;

    public interface LinkedInSignOutListener {
        void onSignOut();
    }

    public static LinkedInSignOutDialogFragment newInstance() {
        return new LinkedInSignOutDialogFragment();
    }

    public void setLinkedInSignOutListener(LinkedInSignOutListener linkedInSignOutListener) {
        this.mParent = linkedInSignOutListener;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setTitle(R.string.linkedin_signout_title);
        builder.setMessage(R.string.linkedin_signout_message);
        builder.setPositiveButton(R.string.linkedin_signout_button, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.linkedin.ui.LinkedInSignOutDialogFragment.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                LinkedInUtils.deactivateLinkedInSync(LinkedInSignOutDialogFragment.this.getActivity());
                LinkedInSignOutDialogFragment.this.dismiss();
                if (LinkedInSignOutDialogFragment.this.mParent != null) {
                    LinkedInSignOutDialogFragment.this.mParent.onSignOut();
                }
            }
        });
        builder.setNegativeButton(R.string.clr_strings_button_title_cancel_txt, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.linkedin.ui.LinkedInSignOutDialogFragment.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                LinkedInSignOutDialogFragment.this.dismiss();
            }
        });
        return builder.create();
    }
}
