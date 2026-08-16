package com.sonymobile.calendar.birthday;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import com.sonymobile.calendar.CalendarSettingsActivity;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;

/* JADX INFO: loaded from: classes2.dex */
public class BirthdayPreferences extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, DialogInterface.OnClickListener {
    public static final String KEY_BIRTHDAY_CONTACTS = "preferences_birthday_contacts";
    protected Switch actionBarSwitch;
    private Toolbar activityToolbar;
    private SwitchPreference mContactsSwitchbox;
    private TextView mOnOffLabel;
    protected CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() { // from class: com.sonymobile.calendar.birthday.BirthdayPreferences.2
        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            boolean z2 = GeneralPreferences.getSharedPreferences(BirthdayPreferences.this.getActivity()).getBoolean(GeneralPreferences.KEY_BIRTHDAYS, true);
            if (z && !z2 && LunarAvailabilityManager.isLunarPluginExist(BirthdayPreferences.this.getActivity())) {
                BirthdayPreferences.this.createDialog();
            }
            if (z) {
                BirthdayPreferences.this.mContactsSwitchbox.setChecked(true);
            } else {
                BirthdayPreferences.this.mContactsSwitchbox.setChecked(false);
            }
            BirthdayPreferences.this.applyPreference(z);
            BirthdayPreferences.this.updateStatus(z);
        }
    };
    DialogInterface.OnCancelListener onCancelListener = new DialogInterface.OnCancelListener() { // from class: com.sonymobile.calendar.birthday.BirthdayPreferences.3
        @Override // android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialogInterface) {
            BirthdayPreferences.this.actionBarSwitch.setChecked(false);
        }
    };

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        addPreferencesFromResource(getResources().getBoolean(R.bool.tablet_mode) ? R.xml.tablet_birthday_preferences : R.xml.birthday_preferences);
        SwitchPreference switchPreference = (SwitchPreference) getPreferenceManager().findPreference(KEY_BIRTHDAY_CONTACTS);
        this.mContactsSwitchbox = switchPreference;
        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.sonymobile.calendar.birthday.BirthdayPreferences.1
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                if (Boolean.valueOf(obj.toString()).booleanValue()) {
                    return true;
                }
                BirthdayPreferences.this.actionBarSwitch.setChecked(false);
                return true;
            }
        });
        Utils.setActionBarOptionsHomeAsUp(getActivity());
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        updateActionBarSwitchState();
        return false;
    }

    private void updateActionBarSwitchState() {
        if (Boolean.valueOf(this.mContactsSwitchbox.isChecked()).booleanValue()) {
            return;
        }
        this.actionBarSwitch.setOnCheckedChangeListener(null);
        applyPreference(false);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        super.onDetach();
        updateActionBarSwitchState();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        setupSwitchLayout();
        boolean z = GeneralPreferences.getSharedPreferences(getActivity()).getBoolean(GeneralPreferences.KEY_BIRTHDAYS, true);
        this.actionBarSwitch.setChecked(z);
        applyPreference(z);
        updateStatus(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateStatus(boolean z) {
        getPreferenceManager().findPreference(KEY_BIRTHDAY_CONTACTS).setEnabled(z);
        if (Utils.isTabletDevice(getActivity())) {
            return;
        }
        if (z) {
            this.mOnOffLabel.setText(R.string.on_label);
        } else {
            this.mOnOffLabel.setText(R.string.off_label);
        }
    }

    protected void setupCustomActionbar(View view, ActionBar.LayoutParams layoutParams) {
        ActionBar supportActionBar = Utils.getSupportActionBar(getActivity());
        if (supportActionBar != null) {
            supportActionBar.setCustomView((View) null);
            supportActionBar.setTitle(R.string.preferences_display_birthdays);
            supportActionBar.setDisplayOptions(16, 16);
            if (Utils.isTabletDevice(getActivity())) {
                supportActionBar.setCustomView(view, layoutParams);
            }
            supportActionBar.setDisplayShowTitleEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void setupSwitchLayout() {
        ActionBar.LayoutParams layoutParams;
        if (Utils.isTabletDevice(getActivity())) {
            Switch r0 = new Switch(getActivity());
            this.actionBarSwitch = r0;
            r0.setFocusable(true);
            this.actionBarSwitch.setPadding(0, 0, getResources().getDimensionPixelSize(R.dimen.actionbar_switch_padding), 0);
            this.actionBarSwitch.setOnCheckedChangeListener(this.checkedChangeListener);
            layoutParams = new ActionBar.LayoutParams(-2, -2, 8388629);
        } else {
            View viewInflate = LayoutInflater.from(getActivity()).inflate(R.layout.preference_toolbar, this.activityToolbar);
            Switch r2 = (Switch) viewInflate.findViewById(R.id.pref_switch);
            this.actionBarSwitch = r2;
            r2.setFocusable(true);
            this.actionBarSwitch.setOnCheckedChangeListener(this.checkedChangeListener);
            this.mOnOffLabel = (TextView) viewInflate.findViewById(R.id.on_off_label);
            layoutParams = null;
        }
        setupCustomActionbar(this.actionBarSwitch, layoutParams);
    }

    protected void applyPreference(boolean z) {
        SharedPreferences.Editor editorEdit = GeneralPreferences.getSharedPreferences(getActivity()).edit();
        editorEdit.putBoolean(GeneralPreferences.KEY_BIRTHDAYS, z);
        editorEdit.apply();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setIconAttribute(android.R.attr.alertDialogIcon);
        builder.setMessage(getString(R.string.preferences_birthday_descrip));
        builder.setPositiveButton(getString(android.R.string.yes), this);
        builder.setNegativeButton(getString(android.R.string.no), this);
        AlertDialog alertDialogCreate = builder.create();
        alertDialogCreate.setOnCancelListener(this.onCancelListener);
        alertDialogCreate.show();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -2) {
            this.actionBarSwitch.setChecked(false);
            dialogInterface.dismiss();
        } else {
            if (i != -1) {
                return;
            }
            dialogInterface.dismiss();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity instanceof CalendarSettingsActivity) {
            CalendarSettingsActivity calendarSettingsActivity = (CalendarSettingsActivity) appCompatActivity;
            calendarSettingsActivity.updateToolbar(true);
            this.activityToolbar = calendarSettingsActivity.getToolbar();
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
    }
}
