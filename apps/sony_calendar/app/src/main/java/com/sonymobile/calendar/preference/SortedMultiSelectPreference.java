package com.sonymobile.calendar.preference;

import android.content.Context;
import android.util.AttributeSet;
import com.sonymobile.calendar.CustomMultiSelectListPreference;
import java.util.ArrayList;
import java.util.Collections;

/* JADX INFO: loaded from: classes2.dex */
public class SortedMultiSelectPreference extends CustomMultiSelectListPreference {
    public SortedMultiSelectPreference(Context context) {
        super(context, null);
    }

    public SortedMultiSelectPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        sortLabelsAndValues();
    }

    private void sortLabelsAndValues() {
        CharSequence[] entries = getEntries();
        CharSequence[] entryValues = getEntryValues();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < entries.length; i++) {
            arrayList.add(new LabelValuePair(entries[i], entryValues[i]));
        }
        Collections.sort(arrayList);
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            entries[i2] = ((LabelValuePair) arrayList.get(i2)).getLabel();
            entryValues[i2] = ((LabelValuePair) arrayList.get(i2)).getValue();
        }
        setEntries(entries);
        setEntryValues(entryValues);
    }

    public void show() {
        onClick();
    }

    private static class LabelValuePair implements Comparable<LabelValuePair> {
        private CharSequence label;
        private CharSequence value;

        public LabelValuePair(CharSequence charSequence, CharSequence charSequence2) {
            this.label = charSequence;
            this.value = charSequence2;
        }

        @Override // java.lang.Comparable
        public int compareTo(LabelValuePair labelValuePair) {
            return this.label.toString().compareTo(labelValuePair.label.toString());
        }

        public CharSequence getLabel() {
            return this.label;
        }

        public CharSequence getValue() {
            return this.value;
        }
    }
}
