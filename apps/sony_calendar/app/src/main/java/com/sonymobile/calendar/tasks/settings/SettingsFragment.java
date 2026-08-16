package com.sonymobile.calendar.tasks.settings;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import androidx.legacy.app.FragmentCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.tasks.model.TaskAccount;
import com.sonymobile.provider.TasksContract;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, FragmentCompat.OnRequestPermissionsResultCallback {
    private PreferenceCategory mAccountPreference;

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return true;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.tasks_settings_preferences);
        this.mAccountPreference = (PreferenceCategory) getPreferenceScreen().findPreference(Settings.KEY_SYNC_ACCOUNT);
        updateAccountPreference();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateAccountPreference();
    }

    private void updateAccountPreference() {
        String string;
        this.mAccountPreference.removeAll();
        ArrayList<TaskAccount> accountLists = TaskAccountManager.getInstance().getAccountLists(getActivity());
        Intent intent = new Intent();
        intent.setAction("android.settings.SYNC_SETTINGS");
        intent.putExtra("authorities", new String[]{TasksContract.AUTHORITY});
        for (TaskAccount taskAccount : accountLists) {
            final AccountPreference accountPreference = new AccountPreference(getActivity(), null, taskAccount);
            String str = taskAccount.type;
            if (taskAccount.isLocal()) {
                if (getResources().getBoolean(R.bool.tablet_mode) || getResources().getBoolean(R.bool.tablet_with_phone_ui_mode)) {
                    string = getActivity().getResources().getString(R.string.task_default_account_tablet_txt);
                } else {
                    string = getActivity().getResources().getString(R.string.task_default_account_txt);
                }
            } else {
                string = taskAccount.name;
            }
            accountPreference.setTitle(new SpannableString(string));
            if (!str.equalsIgnoreCase("LOCAL")) {
                accountPreference.setIntent(intent);
            }
            accountPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.sonymobile.calendar.tasks.settings.SettingsFragment.1
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    accountPreference.mSwitchBox.setChecked(!accountPreference.mSwitchBox.isChecked());
                    return false;
                }
            });
            this.mAccountPreference.addPreference(accountPreference);
        }
    }

    public void onRequestPermissionResult(int i, String[] strArr, int[] iArr) {
        if (Build.VERSION.SDK_INT >= 23) {
            super.onRequestPermissionsResult(i, strArr, iArr);
        }
    }
}
