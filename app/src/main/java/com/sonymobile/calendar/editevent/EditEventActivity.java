package com.sonymobile.calendar.editevent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.sonymobile.calendar.OnColorSetListener;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.permissions.PermissionHandlerActivity;
import com.sonymobile.calendar.permissions.PermissionItem;
import com.sonymobile.calendar.tablet.OnEditEventTimeChangedListener;
import com.sonymobile.calendar.transitions.EditCircularRevealTransition;
import com.sonymobile.calendar.utils.PermissionUtils;
import com.sonymobile.calendar.utils.UiUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class EditEventActivity extends PermissionHandlerActivity implements RemindersDialogFragment.DialogCallback {
    protected EditEventView editEventView;
    private TabletEditEventDayFragment mDayFragment;
    protected TextView mSaveSendText;
    private Bundle mSavedInstanceState;
    protected Toolbar mToolbar;

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Window window = getWindow();
        window.requestFeature(12);
        if (isNewEvent()) {
            setContentView(R.layout.create_event_activity);
            this.editEventView = (CreateEventView) findViewById(R.id.create_event_view);
            onCreateForCreateEventActivity(window);
        } else {
            setContentView(R.layout.edit_event_activity);
            this.editEventView = (EditEventView) findViewById(R.id.edit_event_view);
        }
        if (isEssentialPermissionsGranted()) {
            initViewIfNeeded(bundle);
        } else {
            this.mSavedInstanceState = bundle;
        }
        this.editEventView.forceLoad(getSupportLoaderManager());
    }

    private void initViewIfNeeded(Bundle bundle) {
        init(bundle);
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public PermissionItem[] getRequiredPermission() {
        return new PermissionItem[]{PermissionUtils.getEssentialCalendarPermissionItem(this), new PermissionItem("android.permission.READ_CONTACTS", null, null, R.drawable.ic_contact, false)};
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public void onRequestPermissionResult(String[] strArr, int[] iArr) {
        initViewIfNeeded(this.mSavedInstanceState);
    }

    private void onCreateForCreateEventActivity(Window window) {
        this.editEventView.setTransitionDone(false);
        findViewById(R.id.addEventButton).setVisibility(8);
        UiUtils.setEnterTransition(window, new EditCircularRevealTransition(this.editEventView, findViewById(R.id.event_view_container), this));
    }

    protected void init(Bundle bundle) {
        if (Utils.isTabletDevice(this)) {
            initDayFragment();
            initEventView();
        }
        this.editEventView.init(getIntent(), bundle, this);
        initToolbar();
    }

    private void initToolbar() {
        if (this.mToolbar != null) {
            return;
        }
        if (Utils.isTabletDevice(this)) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.edit_event_toolbar);
            this.mToolbar = toolbar;
            toolbar.setBackgroundResource(R.color.toolbar_background_color);
        } else {
            Toolbar toolbar2 = (Toolbar) this.editEventView.findViewById(R.id.edit_event_toolbar);
            this.mToolbar = toolbar2;
            toolbar2.setBackgroundResource(R.color.toolbar_background_color);
        }
        initToolbarMenu();
        initToolbarNavigationButton();
    }

    protected void initToolbarMenu() {
        View viewInflate = getLayoutInflater().inflate(R.layout.toolbar_custom_layout, (ViewGroup) null);
        TextView textView = (TextView) viewInflate.findViewById(R.id.toolbar_custom_view);
        this.mSaveSendText = textView;
        this.editEventView.setSaveSendTextView(textView);
        this.mToolbar.addView(viewInflate);
        viewInflate.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.editevent.EditEventActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (EditEventActivity.this.editEventView.save()) {
                    Utils.hideKeyboard(EditEventActivity.this.getApplicationContext(), EditEventActivity.this.editEventView.getFocusedChild());
                    EditEventActivity.this.finish();
                }
            }
        });
    }

    protected void initToolbarNavigationButton() {
        this.mToolbar.setNavigationIcon(R.drawable.ic_delete_reminder);
        this.mToolbar.setNavigationContentDescription(R.string.accessibility_close);
        this.mToolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.editevent.EditEventActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EditEventActivity editEventActivity = EditEventActivity.this;
                Utils.hideKeyboard(editEventActivity, editEventActivity.editEventView.getFocusedChild());
                EditEventActivity.this.finish();
            }
        });
    }

    protected boolean isNewEvent() {
        Intent intent = getIntent();
        return !"android.intent.action.EDIT".equals(intent.getAction()) || intent.getData() == null;
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        if (isEssentialPermissionsGranted()) {
            this.editEventView.onResume();
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        super.onStop();
        if (isEssentialPermissionsGranted()) {
            this.editEventView.onStop();
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        if (isEssentialPermissionsGranted()) {
            this.editEventView.onDestroy();
        }
        super.onDestroy();
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.editEventView == null || !isEssentialPermissionsGranted()) {
            return;
        }
        this.editEventView.onSaveInstanceState(bundle);
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        if (this.editEventView == null || !isEssentialPermissionsGranted()) {
            return;
        }
        this.editEventView.onRestoreInstanceState(bundle);
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (isEssentialPermissionsGranted()) {
            this.editEventView.onActivityResult(i, i2, intent);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        if (this.editEventView == null || !isEssentialPermissionsGranted()) {
            return;
        }
        this.editEventView.exitOnBackPress();
    }

    @Override // com.sonymobile.calendar.editevent.RemindersDialogFragment.DialogCallback
    public void onDialogPositiveClick(ArrayList arrayList) {
        if (this.editEventView == null || !isEssentialPermissionsGranted()) {
            return;
        }
        EditEventView.setSelectedReminders(arrayList);
        this.editEventView.updateRemindersLabel();
    }

    private void initDayFragment() {
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
        this.mDayFragment.setOnEditEventTimeChangedListener(new OnEditEventTimeChangedListener() { // from class: com.sonymobile.calendar.editevent.EditEventActivity.3
            @Override // com.sonymobile.calendar.tablet.OnEditEventTimeChangedListener
            public void onTimeChanged(long j, long j2, boolean z, long j3, boolean z2) {
                EditEventActivity.this.editEventView.updateTime(j, j2, z);
            }
        });
    }

    private void initEventView() {
        this.editEventView.setOnEditEventTimeChangedListener(new OnEditEventTimeChangedListener() { // from class: com.sonymobile.calendar.editevent.EditEventActivity.4
            @Override // com.sonymobile.calendar.tablet.OnEditEventTimeChangedListener
            public void onTimeChanged(long j, long j2, boolean z, long j3, boolean z2) {
                if (EditEventActivity.this.mDayFragment != null) {
                    EditEventActivity.this.updateDayViewEvent(j, j2, j3, z, z2);
                }
            }
        });
        this.editEventView.setOnColorSetListener(new OnColorSetListener() { // from class: com.sonymobile.calendar.editevent.EditEventActivity.5
            @Override // com.sonymobile.calendar.OnColorSetListener
            public void onColorSet(int i) {
                if (!UiUtils.isLandscape(EditEventActivity.this.getApplicationContext()) || EditEventActivity.this.mDayFragment == null) {
                    return;
                }
                EditEventActivity.this.mDayFragment.updateEventColor(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDayViewEvent(long j, long j2, long j3, boolean z, boolean z2) {
        if (UiUtils.isLandscape(getApplicationContext())) {
            this.mDayFragment.updateSelectecTime(j, j2, j3, z, z2);
        }
    }
}
