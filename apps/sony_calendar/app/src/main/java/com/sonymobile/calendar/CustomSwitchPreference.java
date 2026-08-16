package com.sonymobile.calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import androidx.preference.Preference;

/* JADX INFO: loaded from: classes2.dex */
public class CustomSwitchPreference extends Preference implements CompoundButton.OnCheckedChangeListener {
    protected SharedPreferences mPrefs;
    private String mTabletTitle;

    protected boolean getDefaultValue(Context context) {
        return true;
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
    }

    protected boolean shouldBeChecked() {
        return true;
    }

    public CustomSwitchPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, true, attributeSet);
    }

    public CustomSwitchPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, true, attributeSet);
    }

    public CustomSwitchPreference(Context context, boolean z) {
        super(context);
        init(context, z, null);
    }

    private void init(Context context, boolean z, AttributeSet attributeSet) {
        SharedPreferences sharedPreferences = GeneralPreferences.getSharedPreferences(context);
        this.mPrefs = sharedPreferences;
        applyPreference(sharedPreferences.getBoolean(getKey(), getDefaultValue(context)) && z);
        if (attributeSet != null) {
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.TabletPreferenceTitle);
            this.mTabletTitle = typedArrayObtainStyledAttributes.getString(0);
            typedArrayObtainStyledAttributes.recycle();
        }
    }

    public String getTabletTitle() {
        return this.mTabletTitle;
    }

    public void setTabletTitle(String str) {
        this.mTabletTitle = str;
    }

    protected void applyPreference(boolean z) {
        SharedPreferences.Editor editorEdit = this.mPrefs.edit();
        editorEdit.putBoolean(getKey(), z);
        editorEdit.apply();
    }
}
