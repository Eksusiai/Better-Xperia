package com.sonymobile.calendar;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import com.sonymobile.calendar.alerts.AlertReceiver;
import com.sonymobile.calendar.alerts.AlertWork;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes2.dex */
public class DeleteEventHelper {
    public static final int DELETE_ALL = 1;
    public static final int DELETE_ALL_FOLLOWING = 1;
    public static final int DELETE_SELECTED = 0;
    private static final String[] EVENT_PROJECTION = {"_id", LunarContract.EventsColumns.TITLE, "allDay", LunarContract.EventsColumns.CALENDAR_ID, "rrule", LunarContract.EventsColumns.DTSTART, "_sync_id", LunarContract.EventsColumns.EVENT_TIMEZONE, "original_id", LunarContract.EventsColumns.HAS_ATTENDEE_DATA, LunarContract.EventsColumns.ORGANIZER};
    private static boolean sIsEventDeleted;
    private AlertDialog mAlertDialog;
    private int mAttendeeNumber;
    private Runnable mCallback;
    private final ContentResolver mContentResolver;
    private Cursor mCursor;
    private boolean mDeleteEventComplete;
    private long mEndMillis;
    private int mEventIndexId;
    private int mEventIndexRrule;
    private boolean mExitWhenDone;
    private onDeleteCallback mListener;
    private final AppCompatActivity mParent;
    private long mStartMillis;
    private String mSyncId;
    private int mWhichDelete;
    private DialogInterface.OnClickListener mDeleteEasOrganizerDialogListener = new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.DeleteEventHelper.1
        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            DialogInterface.OnClickListener deleteRepeatingDialogListener;
            DeleteEventHelper.this.mCursor.moveToFirst();
            String string = DeleteEventHelper.this.mCursor.getString(DeleteEventHelper.this.mEventIndexRrule);
            AlertDialog.Builder view = new AlertDialog.Builder(DeleteEventHelper.this.mParent, R.style.AlertDialogTheme).setTitle(R.string.popup_title_cancellation).setView(((LayoutInflater) DeleteEventHelper.this.mParent.getSystemService("layout_inflater")).inflate(R.layout.custom_view, (ViewGroup) null));
            if (string == null) {
                deleteRepeatingDialogListener = DeleteEventHelper.this.new DeleteNormalDialogListener(false);
            } else {
                deleteRepeatingDialogListener = DeleteEventHelper.this.new DeleteRepeatingDialogListener(false);
            }
            DeleteEventHelper.this.mAlertDialog = view.setPositiveButton(R.string.button_send, deleteRepeatingDialogListener).show();
        }
    };
    private DialogInterface.OnClickListener mDeleteListListener = new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.DeleteEventHelper.2
        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            DeleteEventHelper.this.mWhichDelete = i;
            DeleteEventHelper.this.mAlertDialog.getButton(-1).setEnabled(true);
        }
    };
    private DialogInterface.OnClickListener mDeleteAllEventsListener = new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.DeleteEventHelper.3
        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            try {
                DeleteEventHelper.this.mContentResolver.delete(CalendarContract.Events.CONTENT_URI, "account_type!= 'com.sonyericsson.facebook.account'", null);
                if (LunarAvailabilityManager.isLunarAvailable(DeleteEventHelper.this.mParent)) {
                    DeleteEventHelper.this.mContentResolver.delete(LunarContract.Events.CONTENT_URI, "account_type!= 'com.sonyericsson.facebook.account'", null);
                }
                DeleteEventHelper deleteEventHelper = DeleteEventHelper.this;
                deleteEventHelper.cleanNotifications(deleteEventHelper.mParent);
                Toast.makeText(DeleteEventHelper.this.mParent, R.string.toast_delete_all_success, 0).show();
            } catch (Exception unused) {
                Toast.makeText(DeleteEventHelper.this.mParent, R.string.toast_delete_all_fail, 0).show();
            }
        }
    };

    public interface onDeleteCallback {
        void onRepeatingAllDeleteConfirmed();

        void onSingleDeleteConfirmed();
    }

    public DeleteEventHelper(Context context, boolean z) {
        AppCompatActivity activityFromWrapper = Utils.getActivityFromWrapper(context);
        this.mParent = activityFromWrapper;
        this.mContentResolver = activityFromWrapper.getContentResolver();
        this.mExitWhenDone = z;
        this.mCursor = null;
    }

    public boolean isDeleteEventComplete() {
        return this.mDeleteEventComplete;
    }

    public void setExitWhenDone(boolean z) {
        this.mExitWhenDone = z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendCancellationIfNecessary() {
        int i = this.mWhichDelete;
        if (this.mSyncId == null && i >= 0) {
            i++;
        }
        if (i != 1 || this.mCursor.getLong(5) == this.mStartMillis) {
            int i2 = this.mCursor.getInt(this.mEventIndexId);
            Cursor cursorQuery = this.mParent.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, new String[]{"account_type", "account_name", "ownerAccount"}, "_id=?", new String[]{String.valueOf(this.mCursor.getInt(this.mCursor.getColumnIndexOrThrow(LunarContract.EventsColumns.CALENDAR_ID)))}, null);
            if (cursorQuery == null || cursorQuery.getCount() == 0) {
                Utils.closeCursor(cursorQuery);
                return;
            }
            cursorQuery.moveToFirst();
            try {
                String string = cursorQuery.getString(0);
                String string2 = cursorQuery.getString(1);
                Utils.closeCursor(cursorQuery);
                if (this.mAttendeeNumber <= 1 || !Utils.isExchangeAccountType(string)) {
                    return;
                }
                Cursor cursorQuery2 = this.mParent.getContentResolver().query(CalendarContract.Attendees.CONTENT_URI, new String[]{LunarContract.AttendeesColumns.ATTENDEE_RELATIONSHIP, LunarContract.AttendeesColumns.ATTENDEE_EMAIL}, "event_id=?", new String[]{String.valueOf(i2)}, null);
                if (cursorQuery2 == null || cursorQuery2.getCount() <= 1) {
                    Utils.closeCursor(cursorQuery2);
                    return;
                }
                cursorQuery2.moveToFirst();
                do {
                    if (string2 != null) {
                        try {
                            if (cursorQuery2.getString(1) != null && cursorQuery2.getString(1).equalsIgnoreCase(string2)) {
                                cursorQuery2.getInt(0);
                            }
                        } catch (Throwable th) {
                            Utils.closeCursor(cursorQuery2);
                            throw th;
                        }
                    }
                } while (cursorQuery2.moveToNext());
                Utils.closeCursor(cursorQuery2);
            } catch (Throwable th2) {
                Utils.closeCursor(cursorQuery);
                throw th2;
            }
        }
    }

    private class DeleteNormalDialogListener implements DialogInterface.OnClickListener {
        private boolean mIsLunarEvent;

        DeleteNormalDialogListener(boolean z) {
            this.mIsLunarEvent = z;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            if (DeleteEventHelper.this.mCursor == null || DeleteEventHelper.this.mCursor.getCount() == 0) {
                return;
            }
            DeleteEventHelper.this.mCursor.moveToFirst();
            long j = DeleteEventHelper.this.mCursor.getInt(DeleteEventHelper.this.mEventIndexId);
            int columnIndex = DeleteEventHelper.this.mCursor.getColumnIndex("original_id");
            Uri uriWithAppendedId = ContentUris.withAppendedId(this.mIsLunarEvent ? LunarContract.Events.CONTENT_URI : CalendarContract.Events.CONTENT_URI, j);
            String string = DeleteEventHelper.this.mCursor.getString(columnIndex);
            if (!this.mIsLunarEvent) {
                DeleteEventHelper.this.sendCancellationIfNecessary();
            }
            if (string == null) {
                DeleteEventHelper.this.mContentResolver.delete(uriWithAppendedId, null, null);
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(LunarContract.EventsColumns.STATUS, (Integer) 2);
                DeleteEventHelper.this.mContentResolver.update(uriWithAppendedId, contentValues, null, null);
            }
            if (DeleteEventHelper.this.mCallback != null) {
                DeleteEventHelper.this.mCallback.run();
            }
            DeleteEventHelper deleteEventHelper = DeleteEventHelper.this;
            deleteEventHelper.cleanNotifications(deleteEventHelper.mParent);
            DeleteEventHelper.this.dismissEventInfoFragment();
            if (DeleteEventHelper.this.mExitWhenDone) {
                DeleteEventHelper.this.mDeleteEventComplete = true;
                DeleteEventHelper.this.mParent.finish();
            }
            if (DeleteEventHelper.this.mListener != null) {
                DeleteEventHelper.this.mListener.onSingleDeleteConfirmed();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissEventInfoFragment() {
        FragmentManager supportFragmentManager = this.mParent.getSupportFragmentManager();
        EventInfoFragment eventInfoFragment = (EventInfoFragment) supportFragmentManager.findFragmentByTag(EventInfoFragment.TAG);
        if (eventInfoFragment != null) {
            eventInfoFragment.getActivity().finish();
        }
        EventInfoFragment eventInfoFragment2 = (EventInfoFragment) supportFragmentManager.findFragmentByTag(EventInfoFragment.TAG_DIALOG);
        if (eventInfoFragment2 != null) {
            eventInfoFragment2.getActivity().finish();
        }
    }

    private class DeleteRepeatingDialogListener implements DialogInterface.OnClickListener {
        private boolean mIsLunarEvent;

        DeleteRepeatingDialogListener(boolean z) {
            this.mIsLunarEvent = z;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            if (DeleteEventHelper.this.mCursor == null || DeleteEventHelper.this.mCursor.getCount() == 0) {
                return;
            }
            if (DeleteEventHelper.this.mWhichDelete != -1) {
                DeleteEventHelper.this.mCursor.moveToFirst();
                DeleteEventHelper deleteEventHelper = DeleteEventHelper.this;
                deleteEventHelper.deleteRepeatingEvent(deleteEventHelper.mWhichDelete, this.mIsLunarEvent);
                if (DeleteEventHelper.this.mWhichDelete != 0) {
                    DeleteEventHelper.this.sendCancellationIfNecessary();
                }
            }
            DeleteEventHelper.this.dismissEventInfoFragment();
        }
    }

    public void delete(long j, long j2, long j3, int i, boolean z) {
        Uri uri;
        if (z) {
            uri = LunarContract.Events.CONTENT_URI;
        } else {
            uri = CalendarContract.Events.CONTENT_URI;
        }
        Cursor cursorManagedQuery = this.mParent.managedQuery(ContentUris.withAppendedId(uri, j3), EVENT_PROJECTION, null, null, null);
        if (cursorManagedQuery == null || cursorManagedQuery.getCount() < 1) {
            return;
        }
        cursorManagedQuery.moveToFirst();
        delete(j, j2, cursorManagedQuery, i, z);
        setEventDeleted();
    }

    public void delete(long j, long j2, long j3, int i, Runnable runnable, boolean z) {
        delete(j, j2, j3, i, z);
        this.mCallback = runnable;
    }

    public void delete(long j, long j2, Cursor cursor, int i, boolean z) {
        CharSequence[] charSequenceArr;
        char c;
        AlertDialog alertDialogCreate;
        this.mWhichDelete = i;
        this.mStartMillis = j;
        this.mEndMillis = j2;
        this.mCursor = cursor;
        this.mEventIndexId = cursor.getColumnIndexOrThrow("_id");
        this.mEventIndexRrule = this.mCursor.getColumnIndexOrThrow("rrule");
        this.mSyncId = this.mCursor.getString(this.mCursor.getColumnIndexOrThrow("_sync_id"));
        String string = this.mCursor.getString(this.mEventIndexRrule);
        String[] strArr = {"account_type", "ownerAccount"};
        String[] strArr2 = {String.valueOf(this.mCursor.getInt(this.mCursor.getColumnIndexOrThrow(LunarContract.EventsColumns.CALENDAR_ID)))};
        String string2 = this.mCursor.getString(this.mCursor.getColumnIndexOrThrow(LunarContract.EventsColumns.ORGANIZER));
        Cursor cursorQuery = this.mParent.getContentResolver().query(z ? LunarContract.Calendars.CONTENT_URI : CalendarContract.Calendars.CONTENT_URI, strArr, "_id=?", strArr2, null);
        if (cursorQuery == null || cursorQuery.getCount() == 0) {
            Utils.closeCursor(cursorQuery);
            return;
        }
        cursorQuery.moveToFirst();
        try {
            String string3 = cursorQuery.getString(0);
            String string4 = cursorQuery.getString(1);
            Utils.closeCursor(cursorQuery);
            Cursor cursorQuery2 = this.mParent.getContentResolver().query(z ? LunarContract.Attendees.CONTENT_URI : CalendarContract.Attendees.CONTENT_URI, new String[]{"_id"}, "event_id=?", new String[]{String.valueOf(this.mCursor.getInt(this.mEventIndexId))}, null);
            this.mAttendeeNumber = cursorQuery2.getCount();
            cursorQuery2.close();
            TextView textView = (TextView) LayoutInflater.from(this.mParent).inflate(R.layout.alert_dialog_body, (ViewGroup) null);
            textView.setText(this.mParent.getResources().getString(R.string.popup_title_delete_event));
            if (string == null) {
                if (this.mAttendeeNumber > 1 && Utils.isExchangeAccountType(string3) && string4.equalsIgnoreCase(string2)) {
                    this.mAlertDialog = new AlertDialog.Builder(this.mParent, R.style.AlertDialogTheme).setView(textView).setPositiveButton(R.string.delete_label, this.mDeleteEasOrganizerDialogListener).setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) null).show();
                    return;
                } else {
                    this.mAlertDialog = new AlertDialog.Builder(this.mParent, R.style.AlertDialogTheme).setView(textView).setPositiveButton(R.string.delete_label, new DeleteNormalDialogListener(z)).setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) null).show();
                    return;
                }
            }
            String[] stringArray = this.mParent.getResources().getStringArray(R.array.change_response_labels);
            if (this.mSyncId == null) {
                charSequenceArr = new CharSequence[1];
                c = 0;
            } else {
                charSequenceArr = new CharSequence[2];
                charSequenceArr[0] = stringArray[0];
                c = 1;
            }
            charSequenceArr[c] = stringArray[1];
            if (this.mAttendeeNumber > 1 && Utils.isExchangeAccountType(string3) && string4.equalsIgnoreCase(string2)) {
                alertDialogCreate = new AlertDialog.Builder(this.mParent, R.style.AlertDialogTheme).setTitle(R.string.popup_title_delete_event).setSingleChoiceItems(charSequenceArr, i, this.mDeleteListListener).setPositiveButton(R.string.delete_label, this.mDeleteEasOrganizerDialogListener).setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) null).create();
            } else {
                alertDialogCreate = new AlertDialog.Builder(this.mParent, R.style.AlertDialogTheme).setTitle(R.string.popup_title_delete_event).setSingleChoiceItems(charSequenceArr, i, this.mDeleteListListener).setPositiveButton(R.string.delete_label, new DeleteRepeatingDialogListener(z)).setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) null).create();
            }
            this.mAlertDialog = alertDialogCreate;
            alertDialogCreate.getListView().setFooterDividersEnabled(false);
            this.mAlertDialog.show();
            if (i == -1) {
                alertDialogCreate.getButton(-1).setEnabled(false);
            }
        } catch (Throwable th) {
            Utils.closeCursor(cursorQuery);
            throw th;
        }
    }

    public void deleteAllEvents() {
        int count;
        Cursor cursorQuery = this.mContentResolver.query(CalendarContract.Events.CONTENT_URI, null, "deleted != 1", null, null);
        Cursor cursorQuery2 = LunarAvailabilityManager.isLunarAvailable(this.mParent) ? this.mContentResolver.query(LunarContract.Events.CONTENT_URI, null, "deleted != 1", null, null) : null;
        if (cursorQuery == null) {
            count = 0;
        } else {
            try {
                count = cursorQuery.getCount();
            } finally {
                Utils.closeCursor(cursorQuery);
                Utils.closeCursor(cursorQuery2);
            }
        }
        if (count + (cursorQuery2 == null ? 0 : cursorQuery2.getCount()) > 0) {
            this.mAlertDialog = new AlertDialog.Builder(this.mParent, R.style.AlertDialogTheme).setTitle(R.string.delete_title).setMessage(R.string.confirm_delete_all_events).setPositiveButton(R.string.delete_label, this.mDeleteAllEventsListener).setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) null).show();
        } else {
            Toast.makeText(this.mParent, R.string.toast_no_events, 0).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteRepeatingEvent(int i, boolean z) {
        Uri uri;
        int columnIndexOrThrow = this.mCursor.getColumnIndexOrThrow(LunarContract.EventsColumns.DTSTART);
        int columnIndexOrThrow2 = this.mCursor.getColumnIndexOrThrow("allDay");
        int columnIndexOrThrow3 = this.mCursor.getColumnIndexOrThrow(LunarContract.EventsColumns.TITLE);
        int columnIndexOrThrow4 = this.mCursor.getColumnIndexOrThrow(LunarContract.EventsColumns.EVENT_TIMEZONE);
        int columnIndexOrThrow5 = this.mCursor.getColumnIndexOrThrow(LunarContract.EventsColumns.CALENDAR_ID);
        boolean z2 = this.mCursor.getInt(columnIndexOrThrow2) != 0;
        this.mCursor.getLong(columnIndexOrThrow);
        long j = this.mCursor.getInt(this.mEventIndexId);
        int i2 = this.mSyncId == null ? i + 1 : i;
        if (i2 == 0) {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(LunarContract.EventsColumns.TITLE, this.mCursor.getString(columnIndexOrThrow3));
                String string = this.mCursor.getString(columnIndexOrThrow4);
                int i3 = this.mCursor.getInt(columnIndexOrThrow5);
                contentValues.put(LunarContract.EventsColumns.EVENT_TIMEZONE, string);
                contentValues.put("allDay", Integer.valueOf(z2 ? 1 : 0));
                contentValues.put(LunarContract.EventsColumns.CALENDAR_ID, Integer.valueOf(i3));
                contentValues.put(LunarContract.EventsColumns.DTSTART, Long.valueOf(this.mStartMillis));
                contentValues.put(LunarContract.EventsColumns.DTEND, Long.valueOf(this.mEndMillis));
                contentValues.put("original_sync_id", this.mSyncId);
                contentValues.put("originalInstanceTime", Long.valueOf(this.mStartMillis));
                contentValues.put(LunarContract.EventsColumns.STATUS, (Integer) 2);
                this.mContentResolver.insert(z ? LunarContract.Events.CONTENT_URI : CalendarContract.Events.CONTENT_URI, contentValues);
                onDeleteCallback ondeletecallback = this.mListener;
                if (ondeletecallback != null) {
                    ondeletecallback.onSingleDeleteConfirmed();
                }
                cleanNotifications(this.mParent);
                AppCompatActivity appCompatActivity = this.mParent;
                Toast.makeText(appCompatActivity, appCompatActivity.getResources().getString(R.string.toast_delete_success), 1).show();
            } catch (RuntimeException unused) {
                AppCompatActivity appCompatActivity2 = this.mParent;
                Toast.makeText(appCompatActivity2, appCompatActivity2.getResources().getString(R.string.toast_delete_fail), 1).show();
            }
        } else if (i2 == 1) {
            try {
                if (z) {
                    uri = LunarContract.Events.CONTENT_URI;
                } else {
                    uri = CalendarContract.Events.CONTENT_URI;
                }
                this.mContentResolver.delete(ContentUris.withAppendedId(uri, j), null, null);
                onDeleteCallback ondeletecallback2 = this.mListener;
                if (ondeletecallback2 != null) {
                    ondeletecallback2.onRepeatingAllDeleteConfirmed();
                }
                cleanNotifications(this.mParent);
                AppCompatActivity appCompatActivity3 = this.mParent;
                Toast.makeText(appCompatActivity3, appCompatActivity3.getResources().getString(R.string.toast_delete_all_success), 1).show();
            } catch (RuntimeException unused2) {
                AppCompatActivity appCompatActivity4 = this.mParent;
                Toast.makeText(appCompatActivity4, appCompatActivity4.getResources().getString(R.string.toast_delete_all_fail), 1).show();
            }
        }
        Runnable runnable = this.mCallback;
        if (runnable != null) {
            runnable.run();
        }
        if (this.mExitWhenDone) {
            this.mDeleteEventComplete = true;
            this.mParent.finish();
        }
    }

    public void dismissAlertDialog() {
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog != null) {
            alertDialog.setOnDismissListener(onDismissListener);
        }
    }

    public void cleanNotifications(Context context) {
        Intent intent = new Intent(LunarContract.ACTION_EVENT_REMINDER);
        Intent intent2 = new Intent();
        intent2.setClass(context, AlertWork.class);
        intent2.putExtras(intent);
        intent2.putExtra("action", intent.getAction());
        AlertReceiver.beginStartingService(context, intent2);
    }

    public void updateCursor(Cursor cursor) {
        this.mCursor = cursor;
    }

    public void setOnDeleteListener(onDeleteCallback ondeletecallback) {
        this.mListener = ondeletecallback;
    }

    public boolean isDialogShowing() {
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog != null) {
            return alertDialog.isShowing();
        }
        return false;
    }

    public static boolean isEventDeleted() {
        return sIsEventDeleted;
    }

    public static void resetEventDeleted() {
        sIsEventDeleted = false;
    }

    public static void setEventDeleted() {
        sIsEventDeleted = true;
    }
}
