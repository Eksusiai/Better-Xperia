package com.android.ex.chips;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/* JADX INFO: loaded from: classes.dex */
class SingleRecipientArrayAdapter extends ArrayAdapter<RecipientEntry> {
    private final StateListDrawable mDeleteDrawable;
    private final DropdownChipLayouter mDropdownChipLayouter;

    public SingleRecipientArrayAdapter(Context context, RecipientEntry recipientEntry, DropdownChipLayouter dropdownChipLayouter) {
        this(context, recipientEntry, dropdownChipLayouter, null);
    }

    public SingleRecipientArrayAdapter(Context context, RecipientEntry recipientEntry, DropdownChipLayouter dropdownChipLayouter, StateListDrawable stateListDrawable) {
        super(context, dropdownChipLayouter.getAlternateItemLayoutResId(DropdownChipLayouter.AdapterType.SINGLE_RECIPIENT), new RecipientEntry[]{recipientEntry});
        this.mDropdownChipLayouter = dropdownChipLayouter;
        this.mDeleteDrawable = stateListDrawable;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        return this.mDropdownChipLayouter.bindView(view, viewGroup, getItem(i), i, DropdownChipLayouter.AdapterType.SINGLE_RECIPIENT, null, this.mDeleteDrawable);
    }
}
