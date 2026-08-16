package com.sonymobile.calendar;

import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import com.sonymobile.calendar.editevent.EditEventActivity;
import com.sonymobile.calendar.editevent.TabletEditEventActivity;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes2.dex */
public class FABFrameLayout extends FrameLayout {
    ImageButton mImageButton;

    public FABFrameLayout(final Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        View viewInflate = View.inflate(context, R.layout.fab, null);
        ImageButton imageButton = (ImageButton) viewInflate.findViewById(R.id.addEventButton);
        this.mImageButton = imageButton;
        UiUtils.setViewBackgroundToAccentColor(context, imageButton);
        this.mImageButton.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.FABFrameLayout.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Navigator navigator = (Navigator) ((AppCompatActivity) context).getSupportFragmentManager().findFragmentByTag(LaunchActivity.CURRENT_FRAGMENT);
                if (navigator == null) {
                    return;
                }
                long selectedTimeInMillis = navigator.getSelectedTimeInMillis();
                Time time = new SafeTime(Utils.getTimeZone(FABFrameLayout.this.getContext(), null));
                time.set(selectedTimeInMillis);
                long jCorrectSelectedDateTimeWithCurrent = Utils.correctSelectedDateTimeWithCurrent(time);
                long j = 3600000 + jCorrectSelectedDateTimeWithCurrent;
                Intent intent = new Intent("android.intent.action.EDIT");
                if (Utils.isTabletDevice(FABFrameLayout.this.getContext())) {
                    intent.setClassName(context, TabletEditEventActivity.class.getName());
                } else {
                    intent.setClassName(context, EditEventActivity.class.getName());
                }
                intent.putExtra(LunarContract.EXTRA_EVENT_BEGIN_TIME, jCorrectSelectedDateTimeWithCurrent);
                intent.putExtra(LunarContract.EXTRA_EVENT_END_TIME, j);
                intent.putExtra("allDay", false);
                Context context2 = context;
                context2.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation((AppCompatActivity) context2, new Pair[0]).toBundle());
            }
        });
        addView(viewInflate);
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mImageButton.setOnClickListener(onClickListener);
    }
}
