package com.sonymobile.calendar.preference;

import android.content.Context;
import android.widget.TextView;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceViewHolder;
import com.sonymobile.calendar.R;

/* JADX INFO: loaded from: classes2.dex */
public class CustomEditTextPreference extends EditTextPreference {
    private TextView mValueText;

    public CustomEditTextPreference(Context context) {
        super(context);
        setLayoutResource(R.layout.preference_with_value);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.findViewById(R.id.preference_title);
        this.mValueText = textView;
        if (textView != null) {
            textView.setText(getText());
        }
    }

    @Override // androidx.preference.EditTextPreference
    public void setText(String str) {
        super.setText(str);
        TextView textView = this.mValueText;
        if (textView != null) {
            textView.setText(getText());
        }
    }
}
