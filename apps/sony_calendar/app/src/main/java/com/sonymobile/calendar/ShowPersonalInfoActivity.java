package com.sonymobile.calendar;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/* JADX INFO: loaded from: classes2.dex */
public class ShowPersonalInfoActivity extends AppCompatActivity {
    PrivacyPolicyDialog privacyPolicyDialog;

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_show_personal_info);
        showDialog(this);
    }

    private void showDialog(Context context) {
        PrivacyPolicyDialog privacyPolicyDialog = new PrivacyPolicyDialog(context, R.style.DialogNoBackground);
        this.privacyPolicyDialog = privacyPolicyDialog;
        privacyPolicyDialog.setAgreeClickListener(new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.ShowPersonalInfoActivity.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                ShowPersonalInfoActivity.this.privacyPolicyDialog.dismiss();
            }
        });
        this.privacyPolicyDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.sonymobile.calendar.ShowPersonalInfoActivity.2
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                ShowPersonalInfoActivity.this.finish();
            }
        });
        this.privacyPolicyDialog.setCanceledOnTouchOutside(false);
        this.privacyPolicyDialog.show();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        PrivacyPolicyDialog privacyPolicyDialog = this.privacyPolicyDialog;
        if (privacyPolicyDialog == null || !privacyPolicyDialog.isShowing()) {
            return;
        }
        this.privacyPolicyDialog.dismiss();
    }
}
