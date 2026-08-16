package com.sonymobile.calendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;

/* JADX INFO: loaded from: classes2.dex */
public class EditResponseHelper implements DialogInterface.OnClickListener {
    private AlertDialog mAlertDialog;
    private DialogInterface.OnClickListener mDialogListener;
    private final AppCompatActivity mParent;
    private final boolean mTabletMode;
    private int mWhichEvents = -1;
    private final DialogInterface.OnCancelListener mOnCancelListener = new DialogInterface.OnCancelListener() { // from class: com.sonymobile.calendar.EditResponseHelper.1
        @Override // android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialogInterface) {
            if (!EditResponseHelper.this.mTabletMode) {
                ((EventInfoActivity) EditResponseHelper.this.mParent).restoreResponse();
                return;
            }
            EventInfoFragment eventInfoFragment = (EventInfoFragment) EditResponseHelper.this.mParent.getSupportFragmentManager().findFragmentByTag(EventInfoFragment.TAG);
            if (eventInfoFragment != null) {
                eventInfoFragment.restoreResponse();
            }
        }
    };
    private final DialogInterface.OnClickListener mCancelListener = new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.EditResponseHelper.2
        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            EditResponseHelper.this.mWhichEvents = -1;
            if (!EditResponseHelper.this.mTabletMode) {
                ((EventInfoActivity) EditResponseHelper.this.mParent).restoreResponse();
                return;
            }
            EventInfoFragment eventInfoFragment = (EventInfoFragment) EditResponseHelper.this.mParent.getSupportFragmentManager().findFragmentByTag(EventInfoFragment.TAG);
            if (eventInfoFragment != null) {
                eventInfoFragment.restoreResponse();
            }
        }
    };
    private final DialogInterface.OnClickListener mListListener = new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.EditResponseHelper.3
        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            EditResponseHelper.this.mWhichEvents = i;
            EditResponseHelper.this.mAlertDialog.getButton(-1).setEnabled(true);
        }
    };

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
    }

    public EditResponseHelper(AppCompatActivity appCompatActivity) {
        this.mParent = appCompatActivity;
        this.mTabletMode = Utils.isTabletDevice(appCompatActivity);
    }

    public void setOnClickListener(DialogInterface.OnClickListener onClickListener) {
        this.mDialogListener = onClickListener;
    }

    public int getWhichEvents() {
        return this.mWhichEvents;
    }

    public void showSelectOccurrencesDialog(int i, String str) {
        if (this.mDialogListener == null) {
            this.mDialogListener = this;
        }
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog == null || !alertDialog.isShowing()) {
            CharSequence[] stringArray = this.mParent.getResources().getStringArray(R.array.change_response_labels);
            if (str == null) {
                stringArray = new CharSequence[]{stringArray[1]};
            }
            AlertDialog alertDialogShow = new AlertDialog.Builder(this.mParent, R.style.AlertDialogTheme).setTitle(R.string.change_response_title).setIcon(R.drawable.ic_dialog_alert_holo_light).setSingleChoiceItems(stringArray, i, this.mListListener).setPositiveButton(android.R.string.ok, this.mDialogListener).setNegativeButton(android.R.string.cancel, this.mCancelListener).setOnCancelListener(this.mOnCancelListener).show();
            this.mAlertDialog = alertDialogShow;
            if (i == -1) {
                alertDialogShow.getButton(-1).setEnabled(false);
            }
        }
    }

    public void showInputCommentDialog(String str) {
        if (this.mDialogListener == null) {
            this.mDialogListener = this;
        }
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog == null || !alertDialog.isShowing()) {
            AlertDialog alertDialogCreate = new AlertDialog.Builder(this.mParent, R.style.AlertDialogTheme).setTitle(str).setView(R.layout.input_comment_dialog).setPositiveButton(R.string.button_send, this.mDialogListener).setNegativeButton(android.R.string.cancel, this.mCancelListener).setOnCancelListener(this.mOnCancelListener).create();
            this.mAlertDialog = alertDialogCreate;
            alertDialogCreate.getWindow().setSoftInputMode(4);
            this.mAlertDialog.show();
        }
    }

    public void closeDialog() {
        this.mAlertDialog = null;
    }
}
