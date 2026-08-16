package com.sonymobile.calendar.permissions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.utils.UiUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public abstract class PermissionHandlerActivity extends AppCompatActivity {
    public static final String IS_ESSENTIAL_PERMISSION_GRANTED = "isEssentialPermissionGrantedKey";
    public static final int MY_PERMISSIONS_REQUEST_CODE = 1002;
    public static final String PACKAGE_SCHEME = "package:";
    private boolean mIsEssentialPermissionsGranted;
    private final Map<String, PermissionItem> mPermissionItemMap = new HashMap();
    private boolean mShouldRequestPermission = false;

    public abstract PermissionItem[] getRequiredPermission();

    public abstract void onRequestPermissionResult(String[] strArr, int[] iArr);

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        populatePermissionMap();
        if (bundle != null) {
            boolean z = bundle.getBoolean(IS_ESSENTIAL_PERMISSION_GRANTED);
            if (!this.mIsEssentialPermissionsGranted && z) {
                this.mShouldRequestPermission = true;
                bundle = null;
            }
        } else {
            this.mShouldRequestPermission = true;
        }
        super.onCreate(bundle);
        UiUtils.setNavigationBar(this);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        if (isAppSettingsDialogShown() || !this.mShouldRequestPermission || this.mPermissionItemMap.isEmpty()) {
            return;
        }
        int size = this.mPermissionItemMap.size();
        PermissionItem[] permissionItemArr = (PermissionItem[]) this.mPermissionItemMap.values().toArray(new PermissionItem[size]);
        Arrays.sort(permissionItemArr);
        String[] strArr = new String[size];
        for (int i = 0; i < size; i++) {
            strArr[i] = permissionItemArr[i].getPermission();
        }
        ActivityCompat.requestPermissions(this, strArr, 1002);
    }

    private boolean isAppSettingsDialogShown() {
        Fragment fragmentFindFragmentByTag = getFragmentManager().findFragmentByTag(AppSettingsDialog.TAG);
        return fragmentFindFragmentByTag != null && fragmentFindFragmentByTag.isAdded();
    }

    private void populatePermissionMap() {
        this.mIsEssentialPermissionsGranted = true;
        for (PermissionItem permissionItem : getRequiredPermission()) {
            if (!permissionItem.isPermissionGranted(this)) {
                if (permissionItem.isEssential()) {
                    this.mIsEssentialPermissionsGranted = false;
                }
                this.mPermissionItemMap.put(permissionItem.getPermission(), permissionItem);
            }
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        this.mPermissionItemMap.clear();
        populatePermissionMap();
        this.mShouldRequestPermission = !this.mPermissionItemMap.isEmpty();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(IS_ESSENTIAL_PERMISSION_GRANTED, this.mIsEssentialPermissionsGranted);
    }

    public boolean isEssentialPermissionsGranted() {
        return this.mIsEssentialPermissionsGranted;
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity, androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i != 1002 || strArr.length <= 0) {
            return;
        }
        this.mShouldRequestPermission = false;
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.permissions_description_dialog_go_to_settings_text));
        sb.append(System.lineSeparator());
        this.mIsEssentialPermissionsGranted = true;
        for (String str : strArr) {
            PermissionItem permissionItem = this.mPermissionItemMap.get(str);
            if (permissionItem != null && permissionItem.isEssential() && !permissionItem.isPermissionGranted(this)) {
                this.mIsEssentialPermissionsGranted = false;
                if (!TextUtils.isEmpty(permissionItem.getLabel(this))) {
                    sb.append(System.lineSeparator());
                    sb.append(permissionItem.getLabel(this));
                    sb.append(System.lineSeparator());
                }
                if (!TextUtils.isEmpty(permissionItem.getDescription(this))) {
                    sb.append(permissionItem.getDescription(this));
                    sb.append(System.lineSeparator());
                }
            }
        }
        if (!this.mIsEssentialPermissionsGranted) {
            sb.append(System.lineSeparator());
            sb.append(getString(R.string.essential_permission_message_not_granted));
            showAppSettingsDialog(sb.toString());
            return;
        }
        onRequestPermissionResult(strArr, iArr);
    }

    private void showAppSettingsDialog(String str) {
        AppSettingsDialog.newInstance(str, true).show(getFragmentManager(), AppSettingsDialog.TAG);
    }

    public static class AppSettingsDialog extends DialogFragment {
        private static final String MESSAGE_KEY = "messageKey";
        private static final String SHOULD_FINISH_ACTIVITY_KEY = "shouldFinsishActivityKey";
        public static final String TAG = "com.sonymobile.calendar.permissions.PermissionHandlerActivity$AppSettingsDialog";

        public static AppSettingsDialog newInstance(String str, boolean z) {
            AppSettingsDialog appSettingsDialog = new AppSettingsDialog();
            Bundle bundle = new Bundle();
            bundle.putString(MESSAGE_KEY, str);
            bundle.putBoolean(SHOULD_FINISH_ACTIVITY_KEY, z);
            appSettingsDialog.setArguments(bundle);
            return appSettingsDialog;
        }

        @Override // android.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            Bundle arguments = getArguments();
            String string = arguments.getString(MESSAGE_KEY);
            final boolean z = arguments.getBoolean(SHOULD_FINISH_ACTIVITY_KEY);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
            builder.setTitle(R.string.permissions_description_settings_dialog_title);
            if (!TextUtils.isEmpty(string)) {
                builder.setMessage(string);
            }
            builder.setPositiveButton(R.string.go_to_app_info_button_text, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.permissions.PermissionHandlerActivity.AppSettingsDialog.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.parse(PermissionHandlerActivity.PACKAGE_SCHEME + AppSettingsDialog.this.getActivity().getPackageName()));
                    AppSettingsDialog.this.getActivity().startActivity(intent);
                    if (z) {
                        AppSettingsDialog.this.getActivity().finish();
                    }
                }
            });
            builder.setNegativeButton(R.string.discard_label, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.permissions.PermissionHandlerActivity.AppSettingsDialog.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (z) {
                        AppSettingsDialog.this.getActivity().finish();
                    }
                }
            });
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.sonymobile.calendar.permissions.PermissionHandlerActivity.AppSettingsDialog.3
                @Override // android.content.DialogInterface.OnKeyListener
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (i != 4 || keyEvent.getAction() != 1 || !z || AppSettingsDialog.this.getActivity().isFinishing()) {
                        return false;
                    }
                    AppSettingsDialog.this.getActivity().finish();
                    return true;
                }
            });
            AlertDialog alertDialogCreate = builder.create();
            alertDialogCreate.setCancelable(false);
            alertDialogCreate.setCanceledOnTouchOutside(false);
            return alertDialogCreate;
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        populatePermissionMap();
    }
}
