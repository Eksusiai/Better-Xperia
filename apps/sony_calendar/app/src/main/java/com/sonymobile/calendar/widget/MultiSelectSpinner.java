package com.sonymobile.calendar.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatSpinner;

/* JADX INFO: loaded from: classes2.dex */
public class MultiSelectSpinner extends AppCompatSpinner {
    public boolean isSpinnerInitialised;

    public MultiSelectSpinner(Context context) {
        super(context);
        this.isSpinnerInitialised = false;
    }

    public MultiSelectSpinner(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.isSpinnerInitialised = false;
    }

    public MultiSelectSpinner(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.isSpinnerInitialised = false;
    }

    @Override // android.widget.AbsSpinner, android.widget.AdapterView
    public void setSelection(int i) {
        boolean z = i == getSelectedItemPosition();
        super.setSelection(i);
        this.isSpinnerInitialised = true;
        if (!z || getOnItemSelectedListener() == null) {
            return;
        }
        getOnItemSelectedListener().onItemSelected(this, getSelectedView(), i, getSelectedItemId());
    }

    @Override // android.widget.AbsSpinner
    public void setSelection(int i, boolean z) {
        boolean z2 = i == getSelectedItemPosition();
        super.setSelection(i, z);
        this.isSpinnerInitialised = true;
        if (!z2 || getOnItemSelectedListener() == null) {
            return;
        }
        getOnItemSelectedListener().onItemSelected(this, getSelectedView(), i, getSelectedItemId());
    }

    public void setSelectionFirstTime(int i) {
        super.setSelection(i);
    }

    @Override // androidx.appcompat.widget.AppCompatSpinner, android.widget.Spinner, android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
