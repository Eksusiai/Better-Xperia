package com.sonymobile.calendar.birthday;
import com.sonymobile.calendar.SafeTime;

import android.app.Activity;
import android.content.Loader;
import android.database.Cursor;
import android.text.format.Time;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import com.sonyericsson.calendar.util.DaySpan;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class ContactBirthdayLoaderBinder extends ContactBirthdayLoaderBase {
    public static final String BIRTHDAY_MIMETYPE_IDENTIFIER = "birthday";
    private Activity activity;
    private BirthdayAdapter birthdayAdapter;
    private ArrayList<ContactBirthday> birthdayList;
    private Time birthdayTime;

    public ContactBirthdayLoaderBinder(Activity activity, ListView listView, Time time) {
        super(activity);
        this.activity = activity;
        this.birthdayTime = new SafeTime(time);
        this.birthdayList = new ArrayList<>();
        BirthdayAdapter birthdayAdapter = new BirthdayAdapter(activity, R.layout.birthday_item, this.birthdayList);
        this.birthdayAdapter = birthdayAdapter;
        listView.setAdapter((ListAdapter) birthdayAdapter);
        activity.getLoaderManager().restartLoader(1, null, this);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.sonymobile.calendar.birthday.ContactBirthdayLoaderBase, android.app.LoaderManager.LoaderCallbacks
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        String string;
        this.birthdayList.clear();
        int columnIndexOrThrow = cursor.getColumnIndexOrThrow("display_name");
        int columnIndexOrThrow2 = cursor.getColumnIndexOrThrow("data1");
        int columnIndexOrThrow3 = cursor.getColumnIndexOrThrow("contact_id");
        int columnIndexOrThrow4 = cursor.getColumnIndexOrThrow("has_phone_number");
        int columnIndexOrThrow5 = cursor.getColumnIndexOrThrow("mimetype");
        int columnIndexOrThrow6 = cursor.getColumnIndexOrThrow("data3");
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            if (cursor.getString(columnIndexOrThrow5).contains(BIRTHDAY_MIMETYPE_IDENTIFIER)) {
                string = cursor.getString(columnIndexOrThrow6);
            } else {
                string = cursor.getString(columnIndexOrThrow2);
            }
            ContactBirthday contactBirthday = new ContactBirthday(cursor.getString(columnIndexOrThrow), string);
            contactBirthday.contactId = String.valueOf(cursor.getInt(columnIndexOrThrow3));
            boolean z = false;
            contactBirthday.hasPhoneNumber = cursor.getInt(columnIndexOrThrow4) == 1;
            Activity activity = this.activity;
            Time time = this.birthdayTime;
            if (contactBirthday.isBirthdayWithinSpan(new DaySpan(activity, time, time))) {
                for (ContactBirthday contactBirthday2 : this.birthdayList) {
                    if (contactBirthday2.contactId.equals(contactBirthday.contactId)) {
                        contactBirthday2.mergeContact(contactBirthday);
                        z = true;
                    }
                }
                if (!z) {
                    Time time2 = new SafeTime(Utils.getTimeZone(this.activity, null));
                    time2.set(Utils.getDisplayTime());
                    if (contactBirthday.getAge(time2) >= 0) {
                        this.birthdayList.add(contactBirthday);
                    }
                }
            }
        }
        this.birthdayAdapter.notifyDataSetChanged(this.birthdayList);
    }

    public void refreshAdapter() {
        this.birthdayAdapter.notifyDataSetChanged();
    }

    public void setDialogListener(Fragment fragment) {
        this.birthdayAdapter.setDialogListener(fragment);
    }
}
