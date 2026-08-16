package com.sonymobile.calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/* JADX INFO: loaded from: classes2.dex */
public class PrivacyPolicyDialog extends AlertDialog implements View.OnClickListener {
    private Button agreeButton;
    private DialogInterface.OnClickListener agreeClickListener;
    private String content;
    private TextView dialogContent;
    private TextView dialogSubTitle;
    private TextView dialogTitle;
    private DialogInterface.OnClickListener disagreeClickListener;
    private String subTitle;
    private String title;

    protected PrivacyPolicyDialog(Context context) {
        super(context);
    }

    protected PrivacyPolicyDialog(Context context, int i) {
        super(context, i);
    }

    public void setPrivacyTitle(String str) {
        this.title = str;
    }

    public void setPrivacySubTitle(String str) {
        this.subTitle = str;
    }

    public void setPrivacyContent(String str) {
        this.content = str;
    }

    public void setAgreeClickListener(DialogInterface.OnClickListener onClickListener) {
        this.agreeClickListener = onClickListener;
    }

    public void setDisagreeClickListener(DialogInterface.OnClickListener onClickListener) {
        this.disagreeClickListener = onClickListener;
    }

    @Override // android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dialog_privacy_policy_layout);
        this.dialogTitle = (TextView) findViewById(R.id.dialog_privacy_title);
        this.dialogSubTitle = (TextView) findViewById(R.id.dialog_privacy_sub_title);
        this.dialogContent = (TextView) findViewById(R.id.dialog_privacy_content);
        Button button = (Button) findViewById(R.id.dialog_agree_button);
        this.agreeButton = button;
        button.setOnClickListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        DialogInterface.OnClickListener onClickListener;
        if (view.getId() == R.id.dialog_agree_button && (onClickListener = this.agreeClickListener) != null) {
            onClickListener.onClick(this, -1);
        }
    }
}
