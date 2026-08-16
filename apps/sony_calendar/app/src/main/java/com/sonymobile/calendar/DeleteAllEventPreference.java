package com.sonymobile.calendar;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;

/* JADX INFO: loaded from: classes2.dex */
public class DeleteAllEventPreference extends Preference {
    private DeleteEventHelper deleteEventHelper;
    private Context mContext;

    public DeleteAllEventPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    @Override // androidx.preference.Preference
    protected void onClick() {
        DeleteEventHelper deleteEventHelper = new DeleteEventHelper(this.mContext, false);
        this.deleteEventHelper = deleteEventHelper;
        deleteEventHelper.deleteAllEvents();
    }

    public void dismissAlertDialog() {
        DeleteEventHelper deleteEventHelper = this.deleteEventHelper;
        if (deleteEventHelper != null) {
            deleteEventHelper.dismissAlertDialog();
        }
    }

    public boolean isDialogShowing() {
        DeleteEventHelper deleteEventHelper = this.deleteEventHelper;
        if (deleteEventHelper != null) {
            return deleteEventHelper.isDialogShowing();
        }
        return false;
    }
}
