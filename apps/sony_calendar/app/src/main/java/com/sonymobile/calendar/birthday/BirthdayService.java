package com.sonymobile.calendar.birthday;
import com.sonymobile.calendar.SafeTime;

import android.app.Activity;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.text.format.Time;
import android.util.Pair;
import com.sonyericsson.calendar.util.CustomizeConfig;
import com.sonyericsson.calendar.util.DaySpan;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.BirthdayMap;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

/* JADX INFO: loaded from: classes2.dex */
public enum BirthdayService {
    INSTANCE;

    private boolean contactsSetting = true;
    private BirthdayMap mCache = new BirthdayMap();
    private Map<DaySpan, Queue<OnReadyHandler>> handlers = new HashMap();

    protected interface OnReadyHandler {
        void onReady(ArrayList<ContactBirthday> arrayList, int i);
    }

    BirthdayService() {
    }

    public static boolean getDefaultEnabledStatus(Context context) {
        return !CustomizeConfig.getInstance().getShowDataUsage(context);
    }

    public void clearCache() {
        this.mCache.clear();
    }

    public void requestBirthdays(Activity activity, DaySpan daySpan, final IAsyncServiceResultHandler iAsyncServiceResultHandler) {
        OnReadyHandler onReadyHandler = new OnReadyHandler() { // from class: com.sonymobile.calendar.birthday.BirthdayService.1
            @Override // com.sonymobile.calendar.birthday.BirthdayService.OnReadyHandler
            public void onReady(ArrayList<ContactBirthday> arrayList, int i) {
                iAsyncServiceResultHandler.onResult(arrayList, Integer.valueOf(i));
            }
        };
        if (!isBirthdaysEnabled(activity) || isLoadingDaySpan(daySpan, onReadyHandler)) {
            return;
        }
        LinkedList linkedList = new LinkedList();
        linkedList.add(onReadyHandler);
        this.handlers.put(daySpan, linkedList);
        loadDataAsync(daySpan, activity);
    }

    private boolean isLoadingDaySpan(DaySpan daySpan, OnReadyHandler onReadyHandler) {
        for (Map.Entry<DaySpan, Queue<OnReadyHandler>> entry : this.handlers.entrySet()) {
            if (daySpan.contains(entry.getKey())) {
                entry.getValue().add(onReadyHandler);
                return true;
            }
        }
        return false;
    }

    private boolean isBirthdaysEnabled(Context context) {
        boolean z = GeneralPreferences.getSharedPreferences(context).getBoolean(GeneralPreferences.KEY_BIRTHDAYS, getDefaultEnabledStatus(context));
        boolean z2 = GeneralPreferences.getSharedPreferences(context).getBoolean(BirthdayPreferences.KEY_BIRTHDAY_CONTACTS, true);
        if (!z || !valuesAreUnchanged(z2)) {
            this.mCache.clear();
        }
        this.contactsSetting = z2;
        return z && z2;
    }

    private boolean valuesAreUnchanged(boolean z) {
        return z == this.contactsSetting;
    }

    private void loadDataAsync(DaySpan daySpan, Activity activity) {
        new LoadDataAsyncTask(activity, daySpan);
    }

    public ArrayList<ContactBirthday> getBirthdays(int i, int i2) {
        ArrayList<ContactBirthday> arrayList = this.mCache.get(new Pair(Integer.valueOf(i), Integer.valueOf(i2)));
        return arrayList == null ? BirthdayMap.getNewBirthdayList() : arrayList;
    }

    private class LoadDataAsyncTask extends ContactBirthdayLoaderBase {
        private DaySpan span;

        public LoadDataAsyncTask(Activity activity, DaySpan daySpan) {
            super(activity);
            this.span = daySpan;
            activity.getLoaderManager().restartLoader(new Random().nextInt(100), null, this);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sonymobile.calendar.birthday.ContactBirthdayLoaderBase, android.app.LoaderManager.LoaderCallbacks
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            String string;
            if (cursor == null) {
                BirthdayService.this.handlers.remove(this.span);
                return;
            }
            int columnIndexOrThrow = cursor.getColumnIndexOrThrow("display_name");
            int columnIndexOrThrow2 = cursor.getColumnIndexOrThrow("data1");
            int columnIndexOrThrow3 = cursor.getColumnIndexOrThrow("contact_id");
            int columnIndexOrThrow4 = cursor.getColumnIndexOrThrow("mimetype");
            int columnIndexOrThrow5 = cursor.getColumnIndexOrThrow("data3");
            ArrayList<ContactBirthday> arrayList = new ArrayList<>();
            cursor.moveToPosition(-1);
            int age = 0;
            while (cursor.moveToNext()) {
                if (cursor.getString(columnIndexOrThrow4).contains(ContactBirthdayLoaderBinder.BIRTHDAY_MIMETYPE_IDENTIFIER)) {
                    string = cursor.getString(columnIndexOrThrow5);
                } else {
                    string = cursor.getString(columnIndexOrThrow2);
                }
                ContactBirthday contactBirthday = new ContactBirthday(cursor.getString(columnIndexOrThrow), string);
                contactBirthday.contactId = String.valueOf(cursor.getInt(columnIndexOrThrow3));
                Time time = new SafeTime(this.span.timezone);
                time.set(contactBirthday.monthDay, contactBirthday.month, contactBirthday.year);
                time.normalize(false);
                if (contactBirthday.isBirthdayWithinSpan(this.span) && !arrayList.contains(contactBirthday)) {
                    age = contactBirthday.getAge(time);
                    BirthdayService.this.addBirthday(contactBirthday);
                    arrayList.add(contactBirthday);
                }
            }
            Queue queue = (Queue) BirthdayService.this.handlers.remove(this.span);
            if (queue != null) {
                while (!queue.isEmpty()) {
                    ((OnReadyHandler) queue.remove()).onReady(arrayList, age);
                }
            }
            Utils.closeCursor(cursor);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addBirthday(ContactBirthday contactBirthday) {
        Pair pair = new Pair(Integer.valueOf(contactBirthday.month), Integer.valueOf(contactBirthday.monthDay));
        ArrayList<ContactBirthday> arrayList = this.mCache.get(pair);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        if (arrayList.contains(contactBirthday)) {
            return;
        }
        arrayList.add(contactBirthday);
        this.mCache.put(pair, arrayList);
    }

    public void stop() {
        this.handlers.clear();
    }
}
