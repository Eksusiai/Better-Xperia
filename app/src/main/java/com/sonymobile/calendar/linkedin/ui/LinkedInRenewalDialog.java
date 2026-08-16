package com.sonymobile.calendar.linkedin.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.linkedin.LinkedInUtils;
import com.sonymobile.calendar.linkedin.backend.LinkedInSyncActivity;

/* JADX INFO: loaded from: classes2.dex */
public class LinkedInRenewalDialog extends DialogFragment {
    private static final int SYNC_LINKEDIN = 1001;
    public static final String TAG = "LinkedInSignOutDialogFragment";

    public static LinkedInRenewalDialog newInstance() {
        return new LinkedInRenewalDialog();
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setTitle(R.string.linkedin_renewal_dialog_titel);
        builder.setMessage(R.string.linkedin_renewal_dialog_text);
        builder.setPositiveButton(R.string.clr_strings_button_title_ok_txt, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.linkedin.ui.LinkedInRenewalDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                LinkedInRenewalDialog.this.getNewTokenAndDissmiss();
            }
        });
        return builder.create();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        String string;
        if (i == 1001 && i2 == -1) {
            if (intent.getBooleanExtra(LinkedInSyncActivity.LINKEDIN_SYNC_SUCCESS_PARAMETER, false)) {
                string = getResources().getString(R.string.linkedin_sync_success);
                LinkedInUtils.closeSyncWithLinkedIn(getActivity(), 2);
            } else {
                string = getResources().getString(R.string.linkedin_sync_unsuccess);
            }
            Toast.makeText(getActivity(), string, 1).show();
        }
    }

    public void getNewTokenAndDissmiss() {
        startActivityForResult(new Intent(getActivity(), (Class<?>) LinkedInSyncActivity.class), 1001);
        dismiss();
    }
}
