package com.sonymobile.calendar;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;
import androidx.fragment.app.FragmentTransaction;
import com.sonyericsson.calendar.util.EmailIntentUtil;
import com.sonymobile.calendar.alerts.DismissAlarmsService;
import com.sonymobile.calendar.linkedin.LinkedInUtils;
import com.sonymobile.calendar.permissions.PermissionHandlerActivity;
import com.sonymobile.calendar.permissions.PermissionItem;
import com.sonymobile.calendar.tablet.TabletEventInfoActivity;
import com.sonymobile.calendar.tasks.alerts.TasksDismissAlarmsService;
import com.sonymobile.calendar.transitions.SlideUpRevealTransition;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.emailcommon.service.IEmailServiceVersionApi;

/* JADX INFO: loaded from: classes2.dex */
public class EventInfoActivity extends PermissionHandlerActivity {
    public static final String TAG = "EventInfoActivity";
    private EventInfoFragment mEventInfoFragment;
    private ServiceConnection mServiceConnection;
    private IEmailServiceVersionApi mServiceVersionApi;
    private RectF mTouchRegion;

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        bindToSomcExchangeService();
        if (Utils.isTabletDevice(this) && !(this instanceof TabletEventInfoActivity)) {
            Intent intent = getIntent();
            intent.setClass(getApplicationContext(), TabletEventInfoActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        Window window = getWindow();
        window.requestFeature(12);
        setContentView(R.layout.event_info_activity);
        if (getIntent().getBooleanExtra(UiUtils.SLIDE_UP_ANIMATION, false)) {
            UiUtils.setEnterTransition(window, new SlideUpRevealTransition(this));
        }
        UiUtils.setAllowEnterTransitionOverlap(window, true);
        if (getIntent().hasExtra(LaunchActivity.ARG_FROM_NOTIFICATION)) {
            handleNotification(getIntent().getIntExtra(LaunchActivity.ARG_FROM_NOTIFICATION, 0), getIntent());
        }
        if (getIntent().getData() != null) {
            this.mEventInfoFragment = EventInfoFragment.newInstance(getIntent(), Utils.isTabletDevice(this));
            if (isEssentialPermissionsGranted()) {
                initFragment();
                return;
            }
            return;
        }
        Toast.makeText(getApplicationContext(), R.string.view_event_details_failed_toast_txt, 0).show();
        setResult(0);
        finish();
    }

    private void handleNotification(int i, Intent intent) {
        ComponentName component = intent.getComponent();
        intent.setAction(null);
        if (i == 1) {
            intent.setClass(this, DismissAlarmsService.class);
        } else if (i == 2) {
            intent.setClass(this, TasksDismissAlarmsService.class);
        } else {
            Log.d(TAG, "handleNotification: not support notification type");
            return;
        }
        startService(intent);
        intent.setComponent(component);
    }

    private void bindToSomcExchangeService() {
        this.mServiceConnection = new ServiceConnection() { // from class: com.sonymobile.calendar.EventInfoActivity.1
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                EventInfoActivity.this.mServiceVersionApi = IEmailServiceVersionApi.Stub.asInterface(iBinder);
                try {
                    try {
                        EventInfoActivity.this.mEventInfoFragment.setSomcExchangeApiVersion(EventInfoActivity.this.mServiceVersionApi.getApiVersion());
                        Log.d(EventInfoActivity.TAG, "SOMC Email Service version API::" + EventInfoActivity.this.mServiceVersionApi.getApiVersion());
                    } catch (Exception e) {
                        Log.e(EventInfoActivity.TAG, "Exception when binding to Exchange service:", e);
                    }
                } finally {
                    EventInfoActivity.this.unbindService(this);
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                EventInfoActivity.this.mServiceVersionApi = null;
            }
        };
        bindService(EmailIntentUtil.createSomcExchangeServiceIntent(), this.mServiceConnection, 1);
    }

    private void initFragment() {
        FragmentTransaction fragmentTransactionBeginTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransactionBeginTransaction.replace(R.id.event_info_activity_container, this.mEventInfoFragment);
        fragmentTransactionBeginTransaction.commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        if (Utils.isTabletDevice(this)) {
            View viewFindViewById = findViewById(R.id.event_info_activity_container);
            this.mTouchRegion = new RectF(viewFindViewById.getLeft(), viewFindViewById.getTop(), viewFindViewById.getRight(), viewFindViewById.getBottom());
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        EventInfoFragment eventInfoFragment = this.mEventInfoFragment;
        if (eventInfoFragment != null) {
            return eventInfoFragment.dispatchPopulateAccessibilityEvent(accessibilityEvent);
        }
        return super.dispatchPopulateAccessibilityEvent(accessibilityEvent);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        EventInfoFragment eventInfoFragment = this.mEventInfoFragment;
        if (eventInfoFragment == null || !eventInfoFragment.onKeyDown(i, keyEvent)) {
            return super.onKeyDown(i, keyEvent);
        }
        return true;
    }

    public void restoreResponse() {
        EventInfoFragment eventInfoFragment = this.mEventInfoFragment;
        if (eventInfoFragment != null) {
            eventInfoFragment.restoreResponse();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        finish();
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent motionEvent) {
        RectF rectF;
        if (motionEvent.getAction() == 0 && (rectF = this.mTouchRegion) != null && !rectF.contains(motionEvent.getX(), motionEvent.getY())) {
            finish();
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (LinkedInUtils.isLinkedInEnabled(this)) {
            LinkedInUtils.handleSyncWithLinkedInResult(this, i, i2, intent);
            this.mEventInfoFragment.handleSyncWithLinkedInView();
        }
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public PermissionItem[] getRequiredPermission() {
        return new PermissionItem[]{new PermissionItem("android.permission.READ_CALENDAR", null, null, R.drawable.ic_calendar_event, true)};
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public void onRequestPermissionResult(String[] strArr, int[] iArr) {
        initFragment();
    }
}
