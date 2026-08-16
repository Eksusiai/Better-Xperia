package com.sonymobile.calendar.alerts;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import androidx.core.app.ActivityCompat;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.permissions.ListActivity;
import com.sonymobile.calendar.permissions.PermissionHandlerActivity;
import com.sonymobile.calendar.permissions.PermissionItem;
import com.sonymobile.calendar.utils.PermissionUtils;

/* JADX INFO: loaded from: classes2.dex */
public abstract class PermissionHandlerAlertsActivity extends ListActivity implements AdapterView.OnItemClickListener {
    private int mPosition;

    public abstract void onItemClick(int i);

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        if (PermissionUtils.isCalendarGranted(this)) {
            onItemClick(i);
        } else {
            this.mPosition = i;
            ActivityCompat.requestPermissions(this, new String[]{PermissionUtils.getEssentialCalendarPermissionItem(this).getPermission()}, 1002);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity, androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i != 1002) {
            return;
        }
        if (PermissionUtils.isCalendarGranted(this)) {
            onItemClick(this.mPosition);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.permissions_description_dialog_go_to_settings_text));
        sb.append(System.lineSeparator());
        PermissionItem essentialCalendarPermissionItem = PermissionUtils.getEssentialCalendarPermissionItem(this);
        if (!TextUtils.isEmpty(essentialCalendarPermissionItem.getLabel(this))) {
            sb.append(System.lineSeparator());
            sb.append(essentialCalendarPermissionItem.getLabel(this));
            sb.append(System.lineSeparator());
        }
        if (!TextUtils.isEmpty(essentialCalendarPermissionItem.getDescription(this))) {
            sb.append(essentialCalendarPermissionItem.getDescription(this));
            sb.append(System.lineSeparator());
            sb.append(System.lineSeparator());
            sb.append(getString(R.string.essential_permission_message_not_granted));
        }
        PermissionHandlerActivity.AppSettingsDialog.newInstance(sb.toString(), false).show(getFragmentManager(), PermissionHandlerActivity.AppSettingsDialog.TAG);
    }
}
