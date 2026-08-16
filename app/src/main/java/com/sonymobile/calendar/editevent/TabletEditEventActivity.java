package com.sonymobile.calendar.editevent;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.sonymobile.calendar.OnColorSetListener;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.tablet.OnEditEventTimeChangedListener;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class TabletEditEventActivity extends EditEventActivity {
    private TabletEditEventDayFragment mDayFragment;

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        this.editEventView.init(intent, null, this);
    }

    @Override // com.sonymobile.calendar.editevent.EditEventActivity
    protected void init(Bundle bundle) {
        Intent intent = getIntent();
        initDayFragment();
        initEventView(intent, bundle);
        initToolbar();
    }

    private void initToolbar() {
        this.mToolbar = (Toolbar) findViewById(R.id.edit_event_toolbar);
        this.mToolbar.setBackgroundResource(R.color.toolbar_background_color);
        initToolbarMenu();
        initToolbarNavigationButton();
    }

    private void initDayFragment() {
        if (Utils.isTabletDevice(this)) {
            if (UiUtils.isPortrait(getApplicationContext())) {
                findViewById(R.id.day_view_container).setVisibility(8);
                return;
            }
            findViewById(R.id.day_view_container).setVisibility(0);
            TabletEditEventDayFragment tabletEditEventDayFragment = (TabletEditEventDayFragment) getSupportFragmentManager().findFragmentById(R.id.day_view);
            this.mDayFragment = tabletEditEventDayFragment;
            if (tabletEditEventDayFragment == null) {
                this.mDayFragment = (TabletEditEventDayFragment) Fragment.instantiate(this, TabletEditEventDayFragment.class.getName());
                FragmentTransaction fragmentTransactionBeginTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransactionBeginTransaction.add(R.id.day_view, this.mDayFragment);
                fragmentTransactionBeginTransaction.commit();
            }
            this.mDayFragment.setOnEditEventTimeChangedListener(new OnEditEventTimeChangedListener() { // from class: com.sonymobile.calendar.editevent.TabletEditEventActivity.1
                @Override // com.sonymobile.calendar.tablet.OnEditEventTimeChangedListener
                public void onTimeChanged(long j, long j2, boolean z, long j3, boolean z2) {
                    TabletEditEventActivity.this.updateEditEventView(j, j2, z);
                }
            });
        }
    }

    private void initEventView(Intent intent, Bundle bundle) {
        this.editEventView.init(intent, bundle, this);
        this.editEventView.setOnEditEventTimeChangedListener(new OnEditEventTimeChangedListener() { // from class: com.sonymobile.calendar.editevent.TabletEditEventActivity.2
            @Override // com.sonymobile.calendar.tablet.OnEditEventTimeChangedListener
            public void onTimeChanged(long j, long j2, boolean z, long j3, boolean z2) {
                if (TabletEditEventActivity.this.mDayFragment != null) {
                    TabletEditEventActivity.this.updateDayViewEvent(j, j2, j3, z, z2);
                }
            }
        });
        this.editEventView.setOnColorSetListener(new OnColorSetListener() { // from class: com.sonymobile.calendar.editevent.TabletEditEventActivity.3
            @Override // com.sonymobile.calendar.OnColorSetListener
            public void onColorSet(int i) {
                if (!UiUtils.isLandscape(TabletEditEventActivity.this.getApplicationContext()) || TabletEditEventActivity.this.mDayFragment == null) {
                    return;
                }
                TabletEditEventActivity.this.mDayFragment.updateEventColor(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDayViewEvent(long j, long j2, long j3, boolean z, boolean z2) {
        if (UiUtils.isLandscape(getApplicationContext())) {
            this.mDayFragment.updateSelectecTime(j, j2, j3, z, z2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateEditEventView(long j, long j2, boolean z) {
        this.editEventView.updateTime(j, j2, z);
    }
}
