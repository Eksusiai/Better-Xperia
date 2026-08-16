package com.sonymobile.calendar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.sonymobile.calendar.permissions.PermissionHandlerActivity;
import com.sonymobile.calendar.permissions.PermissionItem;

/* JADX INFO: loaded from: classes2.dex */
public class EventPickerActivity extends PermissionHandlerActivity {
    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initFragmentIfNeeded();
    }

    private void initFragmentIfNeeded() {
        if (isEssentialPermissionsGranted()) {
            FragmentTransaction fragmentTransactionBeginTransaction = getSupportFragmentManager().beginTransaction();
            Fragment fragmentInstantiate = Fragment.instantiate(this, EventPickerFragment.class.getName());
            fragmentTransactionBeginTransaction.setTransition(4099);
            fragmentTransactionBeginTransaction.replace(android.R.id.content, fragmentInstantiate, EventPickerFragment.TAG);
            fragmentTransactionBeginTransaction.commit();
        }
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public PermissionItem[] getRequiredPermission() {
        return new PermissionItem[]{new PermissionItem("android.permission.READ_CALENDAR", null, null, R.drawable.ic_calendar_event, true)};
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public void onRequestPermissionResult(String[] strArr, int[] iArr) {
        initFragmentIfNeeded();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        Utils.setActionBarOptionsHomeAsUp(this, R.string.app_label);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
