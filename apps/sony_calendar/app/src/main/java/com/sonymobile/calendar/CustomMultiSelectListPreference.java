package com.sonymobile.calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.PreferenceViewHolder;
import java.util.HashSet;

/* JADX INFO: loaded from: classes2.dex */
public class CustomMultiSelectListPreference extends MultiSelectListPreference {
    private static final String CHECKED_VALUES_LIST = "checkedValuesList";
    private static final String INSTANCE_STATE = "instanceState";
    private static final int MAX_LINE_SHOWN = 200;
    private CharSequence[] mEntries;
    private boolean[] mEntryChecked;
    private CharSequence[] mEntryValues;
    private boolean mIsRelaunched;
    private HashSet<String> mValues;

    public CustomMultiSelectListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mValues = new HashSet<>();
        this.mIsRelaunched = false;
    }

    public CustomMultiSelectListPreference(Context context) {
        super(context);
        this.mValues = new HashSet<>();
        this.mIsRelaunched = false;
    }

    protected void onAttachedToActivity() {
        this.mEntryValues = getEntryValues();
        this.mEntries = getEntries();
        this.mValues.clear();
        this.mValues.addAll(getValues());
    }

    @Override // androidx.preference.MultiSelectListPreference, androidx.preference.Preference
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putBooleanArray(CHECKED_VALUES_LIST, this.mEntryChecked);
        return bundle;
    }

    @Override // androidx.preference.MultiSelectListPreference, androidx.preference.Preference
    protected void onRestoreInstanceState(Parcelable parcelable) {
        Bundle bundle = (Bundle) parcelable;
        boolean[] booleanArray = bundle.getBooleanArray(CHECKED_VALUES_LIST);
        if (booleanArray != null) {
            this.mValues.clear();
            this.mIsRelaunched = true;
            for (int i = 0; i < booleanArray.length; i++) {
                if (booleanArray[i]) {
                    this.mValues.add(this.mEntryValues[i].toString());
                }
            }
        }
        super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
    }

    public void setCheckedItems() {
        this.mEntryValues = getEntryValues();
        this.mEntries = getEntries();
        if (!this.mIsRelaunched) {
            this.mValues.addAll(getValues());
        }
        int length = this.mEntries.length;
        this.mEntryChecked = new boolean[length];
        for (int i = 0; i < length; i++) {
            this.mEntryChecked[i] = this.mValues.contains(this.mEntryValues[i].toString());
        }
    }

    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        setCheckedItems();
        builder.setMultiChoiceItems(this.mEntries, this.mEntryChecked, new DialogInterface.OnMultiChoiceClickListener() { // from class: com.sonymobile.calendar.CustomMultiSelectListPreference.1
            @Override // android.content.DialogInterface.OnMultiChoiceClickListener
            public void onClick(DialogInterface dialogInterface, int i, boolean z) {
                CustomMultiSelectListPreference.this.mEntryChecked[i] = z;
                if (z) {
                    CustomMultiSelectListPreference.this.mValues.add(CustomMultiSelectListPreference.this.mEntryValues[i].toString());
                } else {
                    CustomMultiSelectListPreference.this.mValues.remove(CustomMultiSelectListPreference.this.mEntryValues[i].toString());
                }
            }
        });
    }

    protected void onDialogClosed(boolean z) {
        if (z) {
            callChangeListener(this.mValues);
            return;
        }
        this.mValues.clear();
        this.mValues.addAll(getValues());
        setCheckedItems();
    }

    @Override // androidx.preference.MultiSelectListPreference, androidx.preference.Preference
    protected Object onGetDefaultValue(TypedArray typedArray, int i) {
        if (typedArray.getTextArray(i) != null) {
            return super.onGetDefaultValue(typedArray, i);
        }
        return new HashSet();
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ((TextView) preferenceViewHolder.findViewById(android.R.id.summary)).setMaxLines(200);
    }
}
