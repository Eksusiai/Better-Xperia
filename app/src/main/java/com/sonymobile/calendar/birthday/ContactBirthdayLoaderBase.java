package com.sonymobile.calendar.birthday;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import com.sonymobile.calendar.GeneralPreferences;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public abstract class ContactBirthdayLoaderBase implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String CONTACTS_SELECTION = "mimetype= ? AND data2=3";
    private Context mContext;
    private String selection;
    private String[] selectionArguments;
    private static final String[] PROJECTION = {"display_name", "mimetype", "data1", "data3", "contact_id", "has_phone_number"};
    private static final String[] CONTACTS_ARGUMENTS = {"vnd.android.cursor.item/contact_event"};
    public static final Uri QUERY_URI = ContactsContract.Data.CONTENT_URI;

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.app.LoaderManager.LoaderCallbacks
    public abstract void onLoadFinished(Loader<Cursor> loader, Cursor cursor);

    @Override // android.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public ContactBirthdayLoaderBase(Context context) {
        this.mContext = context.getApplicationContext();
        Boolean boolValueOf = Boolean.valueOf(GeneralPreferences.getSharedPreferences(context).getBoolean(GeneralPreferences.KEY_BIRTHDAYS, BirthdayService.getDefaultEnabledStatus(context)));
        Boolean boolValueOf2 = Boolean.valueOf(GeneralPreferences.getSharedPreferences(context).getBoolean(BirthdayPreferences.KEY_BIRTHDAY_CONTACTS, true));
        if (boolValueOf.booleanValue() && boolValueOf2.booleanValue()) {
            this.selection = CONTACTS_SELECTION;
            this.selectionArguments = CONTACTS_ARGUMENTS;
        }
    }

    @Override // android.app.LoaderManager.LoaderCallbacks
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this.mContext, QUERY_URI, PROJECTION, this.selection, this.selectionArguments, null);
    }

    public static String[] getProjection() {
        String[] strArr = PROJECTION;
        return (String[]) Arrays.copyOf(strArr, strArr.length);
    }

    public static String[] getContactsArguments() {
        String[] strArr = CONTACTS_ARGUMENTS;
        return (String[]) Arrays.copyOf(strArr, strArr.length);
    }
}
