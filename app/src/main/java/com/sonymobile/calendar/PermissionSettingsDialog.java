package com.sonymobile.calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

/* JADX INFO: loaded from: classes2.dex */
public class PermissionSettingsDialog extends DialogFragment {
    private static final String PACKAGE_SCHEME = "package:";
    private static final String PERMISSION_KEY = "permissionKey";
    public static final String TAG = "com.sonymobile.calendar.PermissionSettingsDialog";

    public static PermissionSettingsDialog newInstance(String[] strArr) {
        PermissionSettingsDialog permissionSettingsDialog = new PermissionSettingsDialog();
        Bundle bundle = new Bundle();
        bundle.putStringArray(PERMISSION_KEY, strArr);
        permissionSettingsDialog.setArguments(bundle);
        return permissionSettingsDialog;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String strBuildMessage = buildMessage(getArguments().getStringArray(PERMISSION_KEY));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setTitle(R.string.permissions_description_dialog_title).setMessage(strBuildMessage).setPositiveButton(R.string.hint_item_continue, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.PermissionSettingsDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setFlags(268435456);
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.parse("package:" + PermissionSettingsDialog.this.getActivity().getPackageName()));
                PermissionSettingsDialog.this.getActivity().startActivity(intent);
            }
        }).setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) null);
        AlertDialog alertDialogCreate = builder.create();
        alertDialogCreate.setCancelable(false);
        alertDialogCreate.setCanceledOnTouchOutside(false);
        return alertDialogCreate;
    }

    private String buildMessage(String[] strArr) {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.permissions_description_dialog_text));
        sb.append(System.lineSeparator());
        for (String str : strArr) {
            sb.append(PermissionsHelper.getPermissionMessage(str, getActivity().getPackageManager()));
        }
        sb.append(System.lineSeparator());
        sb.append(getString(R.string.essential_permission_message_not_granted_with_explanation));
        return sb.toString();
    }
}
