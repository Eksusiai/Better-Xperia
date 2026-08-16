package com.sonymobile.calendar;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.util.Rfc822Token;
import android.text.util.Rfc822Tokenizer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.RecipientEditTextView;
import com.sonyericsson.calendar.util.CalendarConstants;
import com.sonyericsson.calendar.util.EmailIntentUtil;
import com.sonyericsson.calendar.util.RecurrenceRuleParser;
import com.sonymobile.calendar.permissions.PermissionHandlerActivity;
import com.sonymobile.calendar.permissions.PermissionItem;
import com.sonymobile.common.Rfc822InputFilter;
import com.sonymobile.common.Rfc822Validator;
import com.sonymobile.lunar.lib.LunarContract;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

/* JADX INFO: loaded from: classes2.dex */
public class ForwardActivity extends PermissionHandlerActivity {
    private static final String ACTION_PICK = "com.sonyericsson.android.socialphonebook.PICK";
    private static final int ATTENDEES_INDEX_EMAIL = 2;
    private static final int ATTENDEES_INDEX_ID = 0;
    private static final int ATTENDEES_INDEX_NAME = 1;
    private static final int ATTENDEES_INDEX_RELATIONSHIP = 3;
    private static final int ATTENDEES_INDEX_STATUS = 4;
    private static final int ATTENDEES_INDEX_TYPE = 5;
    private static final String ATTENDEES_SORT_ORDER = "attendeeName ASC, attendeeEmail ASC";
    private static final String ATTENDEES_WHERE = "event_id=?";
    private static final int EVENT_INDEX_ACCESS_LEVEL = 11;
    private static final int EVENT_INDEX_ALL_DAY = 3;
    private static final int EVENT_INDEX_CALENDAR_ID = 4;
    private static final int EVENT_INDEX_CAN_INVITE_OTHERS = 15;
    private static final int EVENT_INDEX_COLOR = 12;
    private static final int EVENT_INDEX_DESCRIPTION = 8;
    private static final int EVENT_INDEX_EVENT_LOCATION = 9;
    private static final int EVENT_INDEX_EVENT_TIMEZONE = 7;
    private static final int EVENT_INDEX_GUESTS_CAN_MODIFY = 14;
    private static final int EVENT_INDEX_HAS_ALARM = 10;
    private static final int EVENT_INDEX_HAS_ATTENDEE_DATA = 13;
    private static final int EVENT_INDEX_ID = 0;
    private static final int EVENT_INDEX_ORGANIZER = 16;
    private static final int EVENT_INDEX_RRULE = 2;
    private static final int EVENT_INDEX_SYNC_ACCOUNT_TYPE = 18;
    private static final int EVENT_INDEX_SYNC_ID = 6;
    private static final int EVENT_INDEX_TITLE = 1;
    private static final int REMINDERS_INDEX_MINUTES = 1;
    private static final String REMINDERS_SORT = "minutes";
    private static final String REMINDERS_WHERE = "event_id=? AND (method=1 OR method=0)";
    private static final int REQUEST_CODE_PHONEBOOK_ACTIVITY_FOR_REQUIRED = 2;
    private TextView mAccountTextView;
    private int mAttendStatus;
    private RecipientEditTextView mAttendeesList;
    private EditText mDescriptionTextView;
    private Rfc822Validator mEmailValidator;
    private long mEndMillis;
    private long mEventId;
    private int mForwardWhich = -1;
    private String mOwnerAccount;
    private long mStartMillis;
    private EditText mSubjectTextView;
    private static final String[] EVENT_PROJECTION = {"_id", LunarContract.EventsColumns.TITLE, "rrule", "allDay", LunarContract.EventsColumns.CALENDAR_ID, LunarContract.EventsColumns.DTSTART, "_sync_id", LunarContract.EventsColumns.EVENT_TIMEZONE, "description", LunarContract.EventsColumns.EVENT_LOCATION, LunarContract.EventsColumns.HAS_ALARM, LunarContract.CalendarColumns.CALENDAR_ACCESS_LEVEL, LunarContract.CalendarColumns.CALENDAR_COLOR, LunarContract.EventsColumns.HAS_ATTENDEE_DATA, LunarContract.EventsColumns.GUESTS_CAN_MODIFY, LunarContract.EventsColumns.GUESTS_CAN_INVITE_OTHERS, LunarContract.EventsColumns.ORGANIZER, "original_id", "account_type"};
    private static final String[] ATTENDEES_PROJECTION = {"_id", LunarContract.AttendeesColumns.ATTENDEE_NAME, LunarContract.AttendeesColumns.ATTENDEE_EMAIL, LunarContract.AttendeesColumns.ATTENDEE_RELATIONSHIP, LunarContract.AttendeesColumns.ATTENDEE_STATUS, LunarContract.AttendeesColumns.ATTENDEE_TYPE};
    private static final String[] REMINDERS_PROJECTION = {"_id", "minutes"};
    private static InputFilter[] sRecipientFilters = {new Rfc822InputFilter()};

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public void onRequestPermissionResult(String[] strArr, int[] iArr) {
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.forward_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setBackgroundResource(R.color.toolbar_background_color);
        toolbar.setTitle(R.string.clr_strings_event_forward_action_txt);
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        this.mEventId = intent.getLongExtra(EventInfoFragment.FORWARD_EVENT_ID, this.mEventId);
        this.mStartMillis = intent.getLongExtra(EventInfoFragment.FORWARD_EVENT_START_MILLIS, this.mStartMillis);
        this.mEndMillis = intent.getLongExtra(EventInfoFragment.FORWARD_EVENT_END_MILLIS, this.mEndMillis);
        this.mForwardWhich = intent.getIntExtra(EventInfoFragment.FORWARD_EVENT_WHICH, this.mForwardWhich);
        this.mAttendStatus = intent.getIntExtra(EventInfoFragment.FORWARD_EVENT_ATTEND_STATUS, this.mAttendStatus);
        this.mOwnerAccount = intent.getStringExtra(EventInfoFragment.FORWARD_EVENT_OWNER_ACCOUNT);
        String stringExtra = intent.getStringExtra("subject");
        String stringExtra2 = intent.getStringExtra("description");
        this.mAccountTextView = (TextView) findViewById(R.id.account_email);
        this.mSubjectTextView = (EditText) findViewById(R.id.subject);
        this.mDescriptionTextView = (EditText) findViewById(R.id.description);
        this.mAccountTextView.setText(this.mOwnerAccount);
        this.mSubjectTextView.setText(stringExtra);
        this.mDescriptionTextView.setText(CalendarConstants.CRLF + stringExtra2);
        String strExtractDomain = extractDomain(this.mOwnerAccount);
        if (strExtractDomain != null) {
            this.mEmailValidator = new Rfc822Validator(strExtractDomain);
        }
        this.mAttendeesList = initMultiAutoCompleteTextView(R.id.attendees_list);
        getSupportActionBar().setDisplayOptions(10);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        new AlertDialog.Builder(this, R.style.AlertDialogTheme).setTitle(R.string.popup_title_delete_email).setMessage(R.string.popup_label_delete_email).setNegativeButton(R.string.discard_label, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.clr_strings_button_title_ok_txt, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.ForwardActivity.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                ForwardActivity.this.finish();
            }
        }).create().show();
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (intent != null && i == 2) {
            setContactsData(i, intent);
        }
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public PermissionItem[] getRequiredPermission() {
        return new PermissionItem[]{new PermissionItem("android.permission.READ_CALENDAR", null, null, R.drawable.ic_calendar_event, true), new PermissionItem("android.permission.READ_CONTACTS", null, null, R.drawable.ic_contact, false)};
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forward_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            onBackPressed();
        } else if (itemId == R.id.action_send) {
            doSend();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void doSend() {
        LinkedHashSet<Rfc822Token> addressesFromList = getAddressesFromList(this.mAttendeesList);
        if (!isAddressAllValid()) {
            this.mAttendeesList.setError(null);
            Toast.makeText(this, getString(R.string.message_compose_error_invalid_email), 1).show();
            return;
        }
        if (addressesFromList.size() < 1) {
            Toast.makeText(this, getString(R.string.message_compose_error_no_recipients), 1).show();
            return;
        }
        String[] strArr = new String[addressesFromList.size()];
        int i = 0;
        for (Rfc822Token rfc822Token : addressesFromList) {
            String name = rfc822Token.getName();
            String address = rfc822Token.getAddress();
            if (name == null) {
                name = address;
            }
            strArr[i] = name + "/" + address;
            i++;
        }
        EmailIntentUtil emailIntentUtil = new EmailIntentUtil(this.mEventId, getApplicationContext());
        startActivity(emailIntentUtil.createForwardIntent(this, this.mForwardWhich, this.mStartMillis, strArr, this.mDescriptionTextView.getText().toString(), this.mSubjectTextView.getText().toString()));
        emailIntentUtil.clear();
        addAttendees(strArr);
        if (this.mForwardWhich == 0) {
            Intent intent = new Intent();
            long j = 0;
            Cursor cursorQuery = getContentResolver().query(CalendarContract.Attendees.CONTENT_URI, ATTENDEES_PROJECTION, ATTENDEES_WHERE, new String[]{Long.toString(this.mEventId)}, ATTENDEES_SORT_ORDER);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.getCount() > 1) {
                        cursorQuery.moveToFirst();
                        do {
                            if (cursorQuery.getString(2).equalsIgnoreCase(this.mOwnerAccount)) {
                                j = cursorQuery.getInt(0);
                            }
                        } while (cursorQuery.moveToNext());
                    }
                } catch (Throwable th) {
                    Utils.closeCursor(cursorQuery);
                    throw th;
                }
            }
            Utils.closeCursor(cursorQuery);
            intent.putExtra(EventInfoFragment.CREATE_EXCEPTION_FORWARD, true);
            intent.putExtra(EventInfoFragment.CREATE_EXCEPTION_EVENT_ID_FORWARD, this.mEventId);
            intent.putExtra(EventInfoFragment.CREATE_EXCEPTION_ATTENDEE_ID_FORWARD, j);
            setResult(-1, intent);
        }
        finish();
    }

    private void addAttendees(String[] strArr) {
        boolean z;
        if (this.mForwardWhich == 0) {
            this.mEventId = createExceptionForward();
        }
        Uri uri = CalendarContract.Attendees.CONTENT_URI;
        Cursor cursorQuery = getContentResolver().query(uri, ATTENDEES_PROJECTION, ATTENDEES_WHERE, new String[]{Long.toString(this.mEventId)}, ATTENDEES_SORT_ORDER);
        ContentValues contentValues = new ContentValues();
        try {
            for (String str : strArr) {
                int iIndexOf = str.indexOf(47);
                String strSubstring = str.substring(0, iIndexOf);
                String strSubstring2 = str.substring(iIndexOf + 1);
                if (TextUtils.isEmpty(strSubstring)) {
                    strSubstring = strSubstring2;
                }
                if (cursorQuery == null || cursorQuery.getCount() <= 1) {
                    z = false;
                    break;
                }
                cursorQuery.moveToFirst();
                while (true) {
                    if (!cursorQuery.getString(2).equalsIgnoreCase(strSubstring2)) {
                        if (!cursorQuery.moveToNext()) {
                            z = false;
                            break;
                        }
                    } else {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    contentValues.clear();
                    contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_NAME, strSubstring);
                    contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_EMAIL, strSubstring2);
                    contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_RELATIONSHIP, (Integer) 1);
                    contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_TYPE, (Integer) 1);
                    contentValues.put("event_id", Long.valueOf(this.mEventId));
                    getContentResolver().insert(uri, contentValues);
                }
            }
        } finally {
            Utils.closeCursor(cursorQuery);
        }
    }

    private int createExceptionForward() {
        Uri uriWithAppendedId = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, this.mEventId);
        ContentResolver contentResolver = getContentResolver();
        Cursor cursorQuery = contentResolver.query(uriWithAppendedId, EVENT_PROJECTION, null, null, null);
        if (cursorQuery == null) {
            return 0;
        }
        if (!cursorQuery.moveToFirst()) {
            cursorQuery.close();
            return 0;
        }
        String[] strArr = {Long.toString(this.mEventId)};
        Cursor cursorQuery2 = getContentResolver().query(CalendarContract.Attendees.CONTENT_URI, ATTENDEES_PROJECTION, ATTENDEES_WHERE, strArr, ATTENDEES_SORT_ORDER);
        Cursor cursorQuery3 = contentResolver.query(CalendarContract.Reminders.CONTENT_URI, REMINDERS_PROJECTION, REMINDERS_WHERE, strArr, "minutes");
        try {
            ContentValues contentValues = new ContentValues();
            String string = cursorQuery.getString(1);
            String string2 = cursorQuery.getString(7);
            int i = cursorQuery.getInt(4);
            boolean z = cursorQuery.getInt(3) != 0;
            String string3 = cursorQuery.getString(6);
            String string4 = cursorQuery.getString(8);
            String string5 = cursorQuery.getString(9);
            int i2 = cursorQuery.getInt(14);
            String string6 = cursorQuery.getString(16);
            int i3 = cursorQuery.getInt(13);
            int i4 = cursorQuery.getInt(10);
            contentValues.put(LunarContract.EventsColumns.TITLE, string);
            contentValues.put(LunarContract.EventsColumns.EVENT_TIMEZONE, string2);
            contentValues.put("allDay", Integer.valueOf(z ? 1 : 0));
            contentValues.put(LunarContract.EventsColumns.CALENDAR_ID, Integer.valueOf(i));
            contentValues.put(LunarContract.EventsColumns.DTSTART, Long.valueOf(this.mStartMillis));
            contentValues.put(LunarContract.EventsColumns.DTEND, Long.valueOf(this.mEndMillis));
            contentValues.put("original_sync_id", string3);
            contentValues.put("originalInstanceTime", Long.valueOf(this.mStartMillis));
            contentValues.put("originalAllDay", Integer.valueOf(z ? 1 : 0));
            contentValues.put(LunarContract.EventsColumns.STATUS, (Integer) 1);
            contentValues.put(LunarContract.EventsColumns.SELF_ATTENDEE_STATUS, Integer.valueOf(this.mAttendStatus));
            contentValues.put(LunarContract.EventsColumns.ORGANIZER, string6);
            contentValues.put("description", string4);
            contentValues.put(LunarContract.EventsColumns.EVENT_LOCATION, string5);
            contentValues.put(LunarContract.EventsColumns.HAS_ATTENDEE_DATA, Integer.valueOf(i3));
            contentValues.put(LunarContract.EventsColumns.GUESTS_CAN_MODIFY, Integer.valueOf(i2));
            contentValues.put(LunarContract.EventsColumns.HAS_ALARM, Integer.valueOf(i4));
            int i5 = Integer.parseInt(contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues).getLastPathSegment());
            if (cursorQuery2 != null && cursorQuery2.getCount() > 1) {
                cursorQuery2.moveToFirst();
                do {
                    contentValues.clear();
                    if (!cursorQuery2.getString(2).equalsIgnoreCase(this.mOwnerAccount)) {
                        contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_STATUS, Integer.valueOf(cursorQuery2.getInt(4)));
                        contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_NAME, cursorQuery2.getString(1));
                        contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_EMAIL, cursorQuery2.getString(2));
                        contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_RELATIONSHIP, Integer.valueOf(cursorQuery2.getInt(3)));
                        contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_TYPE, Integer.valueOf(cursorQuery2.getInt(5)));
                        contentValues.put("event_id", Integer.valueOf(i5));
                        contentResolver.insert(CalendarContract.Attendees.CONTENT_URI, contentValues);
                    }
                } while (cursorQuery2.moveToNext());
            }
            if (i4 != 0 && cursorQuery3 != null && cursorQuery3.getCount() > 0) {
                cursorQuery3.moveToFirst();
                do {
                    contentValues.clear();
                    contentValues.put("event_id", Integer.valueOf(i5));
                    contentValues.put("minutes", Integer.valueOf(cursorQuery3.getInt(1)));
                    contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, contentValues);
                } while (cursorQuery3.moveToNext());
            }
            return i5;
        } finally {
            Utils.closeCursor(cursorQuery);
            Utils.closeCursor(cursorQuery2);
            Utils.closeCursor(cursorQuery3);
        }
    }

    boolean isAddressAllValid() {
        String string = this.mAttendeesList.getText().toString();
        if (string == null || string.length() <= 0) {
            return true;
        }
        for (Rfc822Token rfc822Token : Rfc822Tokenizer.tokenize(string.replaceAll("\\\\", "\\\\\\\\"))) {
            String address = rfc822Token.getAddress();
            if (!TextUtils.isEmpty(address) && !isValidAddress(address)) {
                return false;
            }
        }
        return true;
    }

    static boolean isValidAddress(String str) {
        int length = str.length();
        int iIndexOf = str.indexOf(64);
        int iLastIndexOf = str.lastIndexOf(64);
        int i = iLastIndexOf + 1;
        int iIndexOf2 = str.indexOf(46, i);
        int iLastIndexOf2 = str.lastIndexOf(46);
        return iIndexOf > 0 && iIndexOf == iLastIndexOf && i < iIndexOf2 && iIndexOf2 <= iLastIndexOf2 && iLastIndexOf2 < length - 1;
    }

    private void setContactsData(int i, Intent intent) {
        RecipientEditTextView recipientEditTextView = i == 2 ? this.mAttendeesList : null;
        if (recipientEditTextView != null) {
            new PickContactTask(intent, recipientEditTextView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
        }
    }

    private class PickContactTask extends AsyncTask<Void, Void, ArrayList<String>> {
        private Intent mData;
        private MultiAutoCompleteTextView mTextView;

        public PickContactTask(Intent intent, MultiAutoCompleteTextView multiAutoCompleteTextView) {
            this.mData = intent;
            this.mTextView = multiAutoCompleteTextView;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public ArrayList<String> doInBackground(Void... voidArr) {
            ArrayList parcelableArrayListExtra = this.mData.getParcelableArrayListExtra("email_uris");
            ArrayList<String> arrayList = new ArrayList<>();
            if (parcelableArrayListExtra != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("_id IN ( ");
                for (int i = 0; i < parcelableArrayListExtra.size(); i++) {
                    if (parcelableArrayListExtra.get(i) instanceof Uri) {
                        sb.append(((Uri) parcelableArrayListExtra.get(i)).getLastPathSegment());
                        sb.append(RecurrenceRuleParser.VALUE_SEPARATOR);
                    } else if (parcelableArrayListExtra.get(i) instanceof CharSequence) {
                        CharSequence charSequence = (CharSequence) parcelableArrayListExtra.get(i);
                        if (!TextUtils.isEmpty(charSequence)) {
                            arrayList.add(charSequence.toString() + ", ");
                        }
                    }
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append(")");
                Cursor cursorQuery = ForwardActivity.this.getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{"data1"}, sb.toString(), null, null);
                if (cursorQuery != null) {
                    while (cursorQuery.moveToNext()) {
                        arrayList.add(cursorQuery.getString(0) + ", ");
                    }
                    cursorQuery.close();
                }
            }
            return arrayList;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onCancelled(ArrayList<String> arrayList) {
            super.onCancelled(arrayList);
            this.mTextView = null;
            this.mData = null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(ArrayList<String> arrayList) {
            if (arrayList == null) {
                return;
            }
            this.mTextView.requestFocus();
            Editable text = this.mTextView.getText();
            if (!TextUtils.isEmpty(text)) {
                String strTrim = text.toString().trim();
                if (strTrim.charAt(strTrim.length() - 1) != ',') {
                    this.mTextView.append(", ");
                }
            }
            Iterator<String> it = arrayList.iterator();
            while (it.hasNext()) {
                this.mTextView.append(it.next());
            }
        }
    }

    private RecipientEditTextView initMultiAutoCompleteTextView(int i) {
        RecipientEditTextView recipientEditTextView = (RecipientEditTextView) findViewById(i);
        recipientEditTextView.setAdapter(new BaseRecipientAdapter(this));
        recipientEditTextView.setTokenizer(new Rfc822Tokenizer());
        recipientEditTextView.setValidator(this.mEmailValidator);
        recipientEditTextView.setFilters(sRecipientFilters);
        return recipientEditTextView;
    }

    private static String extractDomain(String str) {
        int i;
        int iLastIndexOf = str.lastIndexOf(64);
        if (iLastIndexOf == -1 || (i = iLastIndexOf + 1) >= str.length()) {
            return null;
        }
        return str.substring(i);
    }

    private LinkedHashSet<Rfc822Token> getAddressesFromList(RecipientEditTextView recipientEditTextView) {
        recipientEditTextView.clearComposingText();
        LinkedHashSet<Rfc822Token> linkedHashSet = new LinkedHashSet<>();
        Rfc822Tokenizer.tokenize(recipientEditTextView.getText(), linkedHashSet);
        Iterator<Rfc822Token> it = linkedHashSet.iterator();
        while (it.hasNext()) {
            Rfc822Token next = it.next();
            if (!this.mEmailValidator.isValid(next.getAddress())) {
                Log.w("ForwordActivity", "Dropping invalid attendee email address: " + next);
                it.remove();
            }
        }
        return linkedHashSet;
    }
}
