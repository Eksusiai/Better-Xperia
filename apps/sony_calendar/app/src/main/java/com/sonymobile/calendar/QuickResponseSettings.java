package com.sonymobile.calendar;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.sonymobile.calendar.preference.CustomEditTextPreference;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public class QuickResponseSettings extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "QuickResponseSettings";
    private EditTextPreference[] mEditTextPrefs;
    private String[] mResponses;

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        setDivider(new ColorDrawable(0));
        setDividerHeight(0);
        Utils.setActionBarOptionsHomeAsUpTitleDisplayed(getActivity(), R.string.quick_response_settings);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PreferenceScreen preferenceScreenCreatePreferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());
        String[] quickResponses = Utils.getQuickResponses(getActivity());
        if (quickResponses != null) {
            int length = getResources().getStringArray(R.array.quick_response_defaults).length;
            this.mResponses = new String[length];
            this.mEditTextPrefs = new EditTextPreference[length];
            Arrays.sort(quickResponses);
            int i = 0;
            for (String str : quickResponses) {
                if (!str.trim().equals("")) {
                    CustomEditTextPreference customEditTextPreferenceCreateCustomEditPreference = createCustomEditPreference(str);
                    this.mResponses[i] = str;
                    this.mEditTextPrefs[i] = customEditTextPreferenceCreateCustomEditPreference;
                    preferenceScreenCreatePreferenceScreen.addPreference(customEditTextPreferenceCreateCustomEditPreference);
                    i++;
                }
            }
            while (i < length) {
                CustomEditTextPreference customEditTextPreferenceCreateCustomEditPreference2 = createCustomEditPreference("");
                this.mResponses[i] = "";
                this.mEditTextPrefs[i] = customEditTextPreferenceCreateCustomEditPreference2;
                preferenceScreenCreatePreferenceScreen.addPreference(customEditTextPreferenceCreateCustomEditPreference2);
                i++;
            }
        } else {
            Log.wtf(TAG, "No responses found");
        }
        setPreferenceScreen(preferenceScreenCreatePreferenceScreen);
    }

    private CustomEditTextPreference createCustomEditPreference(String str) {
        CustomEditTextPreference customEditTextPreference = new CustomEditTextPreference(getActivity());
        customEditTextPreference.setDialogTitle(R.string.quick_response_settings_edit_title);
        customEditTextPreference.setTitle(str);
        customEditTextPreference.setText(str);
        customEditTextPreference.setKey("key_" + str);
        customEditTextPreference.setOnPreferenceChangeListener(this);
        customEditTextPreference.setDialogLayoutResource(R.layout.preference_dialog_edittext);
        return customEditTextPreference;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int i = 0;
        while (true) {
            EditTextPreference[] editTextPreferenceArr = this.mEditTextPrefs;
            if (i >= editTextPreferenceArr.length) {
                return false;
            }
            if (editTextPreferenceArr[i].compareTo(preference) == 0) {
                if (this.mResponses[i].equals(obj)) {
                    return true;
                }
                String[] strArr = this.mResponses;
                strArr[i] = (String) obj;
                this.mEditTextPrefs[i].setTitle(strArr[i]);
                this.mEditTextPrefs[i].setText(this.mResponses[i]);
                Utils.setQuickResponses(getActivity(), this.mResponses);
                return true;
            }
            i++;
        }
    }
}
