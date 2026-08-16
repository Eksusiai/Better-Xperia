package com.linkedin.platform.internals;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.sonymobile.calendar.R;

/* JADX INFO: loaded from: classes.dex */
public class UpdateLinkedInAppDialogFragment extends DialogFragment {
    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.update_linkedin_app_message).setTitle(R.string.update_linkedin_app_title);
        builder.setPositiveButton(R.string.update_linkedin_app_download, new DialogInterface.OnClickListener() { // from class: com.linkedin.platform.internals.UpdateLinkedInAppDialogFragment.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    UpdateLinkedInAppDialogFragment.this.getActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.linkedin.android")));
                } catch (ActivityNotFoundException unused) {
                }
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(R.string.update_linkedin_app_cancel, new DialogInterface.OnClickListener() { // from class: com.linkedin.platform.internals.UpdateLinkedInAppDialogFragment.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return builder.create();
    }
}
