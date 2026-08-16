package com.sonymobile.calendar;

import android.os.Bundle;
import com.sonyericsson.calendar.util.CalendarInstanceService;
import com.sonyericsson.calendar.util.DatabaseUtils;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.permissions.PermissionHandlerActivity;
import com.sonymobile.calendar.permissions.PermissionItem;
import com.sonymobile.calendar.utils.PermissionUtils;

/* JADX INFO: loaded from: classes2.dex */
public class AddEventDialog extends PermissionHandlerActivity implements IAsyncServiceResultHandler {
    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (isEssentialPermissionsGranted()) {
            DatabaseUtils.createCalendarInDatabaseIfNeeded(getApplicationContext(), this);
        }
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public PermissionItem[] getRequiredPermission() {
        return new PermissionItem[]{PermissionUtils.getEssentialCalendarPermissionItem(this), new PermissionItem("android.permission.READ_CALENDAR", getString(R.string.calendar_permision_group_title), getString(R.string.calendar_permision_group_description), R.drawable.ic_calendar_event, true)};
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public void onRequestPermissionResult(String[] strArr, int[] iArr) {
        DatabaseUtils.createCalendarInDatabaseIfNeeded(getApplicationContext(), this);
    }

    @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
    public void onResult(Object obj, Object obj2) {
        CalendarInstanceService.getInstance().requestLoad(this, new IAsyncServiceResultHandler() { // from class: com.sonymobile.calendar.AddEventDialog.1
            @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
            public void onResult(Object obj3, Object obj4) {
                new CalendarAccountPickerDialog().show(AddEventDialog.this.getSupportFragmentManager(), CalendarAccountPickerDialog.TAG);
            }
        }, true);
    }
}
