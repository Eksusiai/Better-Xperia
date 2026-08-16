package com.sonymobile.calendar.linkedin.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.linkedin.LinkedInUtils;

/* JADX INFO: loaded from: classes2.dex */
public class SyncWithLinkedInView extends RelativeLayout {
    private int mType;

    public SyncWithLinkedInView(final Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        LayoutInflater.from(context).inflate(R.layout.sync_with_linkedin, (ViewGroup) this, true);
        ImageButton imageButton = (ImageButton) findViewById(R.id.close_linkedin_icon);
        Button button = (Button) findViewById(R.id.connect_with_linkedin);
        imageButton.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.linkedin.ui.SyncWithLinkedInView.1
            private static final long CLICK_DELAY = 500;
            private long mLastClick;

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                long jCurrentTimeMillis = System.currentTimeMillis();
                if (Math.abs(this.mLastClick - jCurrentTimeMillis) < CLICK_DELAY) {
                    return;
                }
                this.mLastClick = jCurrentTimeMillis;
                LinkedInUtils.closeSyncWithLinkedIn(SyncWithLinkedInView.this.getContext(), SyncWithLinkedInView.this.mType);
                SyncWithLinkedInView.this.setVisibility(8);
            }
        });
        button.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.linkedin.ui.SyncWithLinkedInView.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                LinkedInUtils.startSyncWithLinkedInActivity((Activity) context, LinkedInUtils.LinkedInActivationEntrypoint.EVENT);
            }
        });
        setFocusable(true);
    }

    public void setType(int i) {
        this.mType = i;
    }
}
