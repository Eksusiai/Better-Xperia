package com.sonymobile.calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import androidx.fragment.app.DialogFragment;

/* JADX INFO: loaded from: classes2.dex */
public class PermissionContactsDialog extends DialogFragment {
    private static final String PACKAGE_SCHEME = "package:";
    private static final String PERMISSION_KEY = "permissionKey";
    public static final String TAG = "com.sonymobile.calendar.PermissionContactsDialog";

    public static PermissionContactsDialog newInstance(String[] strArr) {
        PermissionContactsDialog permissionContactsDialog = new PermissionContactsDialog();
        Bundle bundle = new Bundle();
        bundle.putStringArray(PERMISSION_KEY, strArr);
        permissionContactsDialog.setArguments(bundle);
        return permissionContactsDialog;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String strBuildMessage = buildMessage(getArguments().getStringArray(PERMISSION_KEY));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setTitle(R.string.permissions_description_dialog_title).setMessage(strBuildMessage).setPositiveButton(R.string.hint_item_continue, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.PermissionContactsDialog.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setFlags(268435456);
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.parse("package:" + PermissionContactsDialog.this.getActivity().getPackageName()));
                PermissionContactsDialog.this.getActivity().startActivity(intent);
                PermissionContactsDialog.this.getActivity().finish();
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.PermissionContactsDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                PermissionContactsDialog.this.getActivity().finish();
            }
        });
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.sonymobile.calendar.PermissionContactsDialog.3
            @Override // android.content.DialogInterface.OnKeyListener
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return i == 4 && keyEvent.getAction() == 1 && !PermissionContactsDialog.this.getActivity().isFinishing();
            }
        });
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
