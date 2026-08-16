package com.sonymobile.calendar.tablet;

import android.view.View;
import androidx.appcompat.app.ActionBar;
import androidx.preference.SwitchPreference;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.birthday.BirthdayPreferences;

/* JADX INFO: loaded from: classes2.dex */
public class TabletBirthdayPreferences extends BirthdayPreferences {
    @Override // com.sonymobile.calendar.birthday.BirthdayPreferences
    protected void setupCustomActionbar(View view, ActionBar.LayoutParams layoutParams) {
        ActionBar supportActionBar = Utils.getSupportActionBar(getActivity());
        if (supportActionBar != null) {
            supportActionBar.setTitle(R.string.preferences_display_birthdays);
            supportActionBar.setDisplayOptions(16, 16);
            supportActionBar.setDisplayShowTitleEnabled(true);
            supportActionBar.setCustomView(view, layoutParams);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        if (!Boolean.valueOf(((SwitchPreference) getPreferenceManager().findPreference(BirthdayPreferences.KEY_BIRTHDAY_CONTACTS)).isChecked()).booleanValue()) {
            applyPreference(false);
        }
        ActionBar supportActionBar = Utils.getSupportActionBar(getActivity());
        if (supportActionBar != null) {
            supportActionBar.setDisplayOptions(4, 4);
            supportActionBar.setDisplayShowCustomEnabled(false);
        }
    }
}
