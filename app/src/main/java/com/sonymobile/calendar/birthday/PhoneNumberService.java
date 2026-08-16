package com.sonymobile.calendar.birthday;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.PhoneNumber;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/* JADX INFO: loaded from: classes2.dex */
public class PhoneNumberService {
    private static PhoneNumberService instance;
    private Queue<OnReadyHandler> handlers = new LinkedList();
    private HashMap<String, String> primaryNumberCache = new HashMap<>();
    private HashMap<String, ArrayList<PhoneNumber>> allNumbersCache = new HashMap<>();

    protected interface OnReadyHandler {
        void onReady();
    }

    private PhoneNumberService() {
    }

    public static PhoneNumberService getInstance() {
        if (instance == null) {
            instance = new PhoneNumberService();
        }
        return instance;
    }

    public void requestLoad(Context context, ArrayList<ContactBirthday> arrayList, final IAsyncServiceResultHandler iAsyncServiceResultHandler) {
        loadDataAsync(arrayList, new OnReadyHandler() { // from class: com.sonymobile.calendar.birthday.PhoneNumberService.1
            @Override // com.sonymobile.calendar.birthday.PhoneNumberService.OnReadyHandler
            public void onReady() {
                iAsyncServiceResultHandler.onResult(null, 0);
            }
        }, context);
    }

    public String getPrimaryNumber(ContactBirthday contactBirthday) {
        String str = this.primaryNumberCache.get(contactBirthday.contactId);
        return str == null ? "" : str;
    }

    public ArrayList<PhoneNumber> getAllNumbers(ContactBirthday contactBirthday) {
        ArrayList<PhoneNumber> arrayList = this.allNumbersCache.get(contactBirthday.contactId);
        return arrayList == null ? new ArrayList<>() : arrayList;
    }

    private void loadDataAsync(ArrayList<ContactBirthday> arrayList, OnReadyHandler onReadyHandler, Context context) {
        if (onReadyHandler != null) {
            this.handlers.add(onReadyHandler);
        }
        new LoadDataAsyncTask(context, arrayList);
    }

    private class LoadDataAsyncTask implements LoaderManager.LoaderCallbacks<Cursor> {
        private Context context;
        private String selection;
        private String[] selectionArgs;
        private final Uri QUERY_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        private String[] projection = {"data2", "data1", "is_primary", "contact_id"};

        @Override // android.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<Cursor> loader) {
        }

        public LoadDataAsyncTask(Context context, ArrayList<ContactBirthday> arrayList) {
            this.selectionArgs = new String[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++) {
                this.selectionArgs[i] = arrayList.get(i).contactId;
            }
            this.selection = "contact_id IN (" + TextUtils.join(", ", this.selectionArgs) + ")";
            this.context = context;
            if (arrayList.size() > 0) {
                ((Activity) context).getLoaderManager().restartLoader(Integer.parseInt(arrayList.get(0).contactId), null, this);
            }
        }

        @Override // android.app.LoaderManager.LoaderCallbacks
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            return new CursorLoader(this.context, this.QUERY_URI, this.projection, this.selection, null, null);
        }

        @Override // android.app.LoaderManager.LoaderCallbacks
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            ArrayList arrayList = new ArrayList();
            cursor.moveToPosition(-1);
            int columnIndex = cursor.getColumnIndex("data2");
            int columnIndex2 = cursor.getColumnIndex("data1");
            int columnIndex3 = cursor.getColumnIndex("is_primary");
            int columnIndex4 = cursor.getColumnIndex("contact_id");
            String str = null;
            while (cursor.moveToNext()) {
                String str2 = (String) ContactsContract.CommonDataKinds.Phone.getTypeLabel(this.context.getResources(), cursor.getInt(columnIndex), this.context.getResources().getString(R.string.customType));
                String string = cursor.getString(columnIndex2);
                int i = cursor.getInt(columnIndex3);
                String string2 = cursor.getString(columnIndex4);
                if (str != null && !str.equals(string2)) {
                    PhoneNumberService.this.allNumbersCache.put(str, new ArrayList(arrayList));
                    arrayList.clear();
                }
                arrayList.add(new PhoneNumber(str2, string));
                if (i != 0) {
                    PhoneNumberService.this.primaryNumberCache.put(string2, string);
                }
                str = string2;
            }
            PhoneNumberService.this.allNumbersCache.put(str, arrayList);
            while (!PhoneNumberService.this.handlers.isEmpty()) {
                ((OnReadyHandler) PhoneNumberService.this.handlers.remove()).onReady();
            }
            Utils.closeCursor(cursor);
        }
    }
}
