package com.sonymobile.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class EventReminderExpandableLayout extends RelativeLayout {
    private View.OnClickListener expandReminders;
    private LinearLayout mEventReminderExpandableLayout;
    private RelativeLayout mRemainderHeader;
    private TextView mReminderHeaderTextView;
    private ImageView mShowReminderImageArrow;

    public EventReminderExpandableLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.expandReminders = new View.OnClickListener() { // from class: com.sonymobile.calendar.EventReminderExpandableLayout.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (EventReminderExpandableLayout.this.mEventReminderExpandableLayout.getVisibility() == 8) {
                    EventReminderExpandableLayout.this.mEventReminderExpandableLayout.setVisibility(0);
                    EventReminderExpandableLayout.this.mShowReminderImageArrow.setImageResource(R.drawable.ic_arrow_up);
                } else {
                    EventReminderExpandableLayout.this.mEventReminderExpandableLayout.setVisibility(8);
                    EventReminderExpandableLayout.this.mShowReminderImageArrow.setImageResource(R.drawable.ic_arrow_down);
                }
            }
        };
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.event_reminder_expandable_layout, (ViewGroup) this, true);
        this.mRemainderHeader = (RelativeLayout) findViewById(R.id.event_reminder_header);
        this.mReminderHeaderTextView = (TextView) findViewById(R.id.event_reminder_header_text);
        this.mShowReminderImageArrow = (ImageView) findViewById(R.id.expand_reminder_icon);
        this.mEventReminderExpandableLayout = (LinearLayout) findViewById(R.id.event_reminder_expandable_layout);
        this.mShowReminderImageArrow.setOnClickListener(this.expandReminders);
    }

    public void setReminders(ArrayList<Integer> arrayList) {
        this.mEventReminderExpandableLayout.removeAllViews();
        if (arrayList.size() != 0) {
            if (arrayList.size() != 1 || arrayList.get(0).intValue() != 0) {
                updateVisibility(arrayList.size());
                this.mReminderHeaderTextView.setText(Utils.constructReminderLabelOrCustom(getContext(), arrayList.get(0).intValue(), false));
                if (arrayList.size() > 1) {
                    LayoutInflater layoutInflaterFrom = LayoutInflater.from(getContext());
                    for (int i = 1; i < arrayList.size(); i++) {
                        TextView textView = (TextView) layoutInflaterFrom.inflate(R.layout.event_reminder_item, (ViewGroup) this.mEventReminderExpandableLayout, false);
                        textView.setText(Utils.constructReminderLabelOrCustom(getContext(), arrayList.get(i).intValue(), false));
                        this.mEventReminderExpandableLayout.addView(textView);
                    }
                    return;
                }
                return;
            }
        }
        setVisibility(8);
    }

    private void updateVisibility(int i) {
        setVisibility(0);
        if (i <= 1) {
            this.mShowReminderImageArrow.setVisibility(8);
        } else {
            this.mShowReminderImageArrow.setVisibility(0);
            this.mRemainderHeader.setOnClickListener(this.expandReminders);
        }
    }
}
