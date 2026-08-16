package com.sonyericsson.calendar.util;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.utils.PermissionUtils;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes.dex */
public class EmailIntentUtil {
    private static final String ACCOUNT_URI_STRING = "content://EMAIL_PACKAGE_NAME_SENTINEL.provider/account";
    public static final int ACTION_FORWARD = 0;
    private static final int ATTENDEE_INDEX_ATTENDEE_EMAIL = 0;
    private static final int ATTENDEE_INDEX_ATTENDEE_NAME = 1;
    private static final int ATTENDEE_INDEX_ATTENDEE_TYPE = 3;
    private static final String ATTENDEE_WHERE = "event_id=?";
    private static final int CALENDARS_INDEX_ACCOUNT_NAME = 1;
    private static final String CALENDARS_WHERE = "_id=?";
    public static final String CALENDAR_ACTION = "com.sonymobile.email.intent.action.SEND_CALENDAR_EVENT";
    public static final String CALENDAR_EXTRA = "com.sonymobile.email.intent.extra.";
    public static final String CALENDAR_EXTRA_ATTACHMENT_NAME = "com.sonymobile.email.intent.extra.ATTACHMENT_NAME";
    public static final String CALENDAR_EXTRA_HANDLE_TYPE = "com.sonymobile.email.intent.extra.HANDLE_TYPE";
    private static final int EAS_ACCOUNT_INDEX_ID = 0;
    private static final String EAS_ACCOUNT_WHERE = "emailAddress = ?";
    private static final String EMAIL_COMPOSE_CLASS_DOT_NAME = ".activity.MessageComposeCalendar";
    private static final String EMAIL_CONTENT_TYPE = "message/rfc822";
    private static final String EMAIL_PACKAGE_NAME_SENTINEL = "EMAIL_PACKAGE_NAME_SENTINEL";
    private static final String EMAIL_URI_SCHEME = "mailto:";
    private static final int EVENT_INDEX_CALENDAR_ID = 3;
    private static final String EVENT_WHERE = "_id=?";
    private static final String EXTRA_ACCOUNT_ID = "account_id";
    private static final String NEW_EMAIL_PACKAGE = "com.sonymobile.email";
    private static final String OLD_EMAIL_PACKAGE = "com.android.email";
    public static final String SOMC_EXCHANGE_PACKAGE = "com.sonymobile.exchange";
    private static final String SOMC_EXCHANGE_SERVICE_ACTION = "com.sonymobile.exchange.EXCHANGE_VERSION_API";
    private static final String SOMC_EXCHANGE_SERVICE_NAME = "com.sonymobile.exchange.service.EasService";
    public static final int WHICH_ONLY_THIS_INSTANCE = 0;
    private long mAccountId;
    private Cursor mAttendeeCursor;
    private Cursor mCalendarCursor;
    private ContentResolver mContentResolver;
    private Context mContext;
    private Cursor mEventCursor;
    private long mEventId;
    private ICalendarGenerator mICalCreator;
    private final String[] EVENT_PROJECTION = {LunarContract.EventsColumns.TITLE, "rrule", "allDay", LunarContract.EventsColumns.CALENDAR_ID, LunarContract.EventsColumns.DTSTART, "description", LunarContract.EventsColumns.EVENT_LOCATION, LunarContract.EventsColumns.ORGANIZER, LunarContract.EventsColumns.DURATION, "original_sync_id", "sync_data2", "originalInstanceTime", LunarContract.EventsColumns.DTEND, "sync_data4"};
    private final String[] CALENDARS_PROJECTION = {"account_type", "account_name"};
    private final String[] ATTENDEE_PROJECTION = {LunarContract.AttendeesColumns.ATTENDEE_EMAIL, LunarContract.AttendeesColumns.ATTENDEE_NAME, LunarContract.AttendeesColumns.ATTENDEE_RELATIONSHIP, LunarContract.AttendeesColumns.ATTENDEE_TYPE};
    private final String[] EAS_ACCOUNT_PROJECTION = {"_id"};

    public EmailIntentUtil(long j, Context context) {
        this.mContentResolver = context.getContentResolver();
        this.mEventId = j;
        this.mContext = context;
        init(context);
    }

    private static final Uri getAccountUri(Context context) {
        ComponentName availableEmailComponent = getAvailableEmailComponent(context);
        if (availableEmailComponent != null) {
            return Uri.parse(ACCOUNT_URI_STRING.replace(EMAIL_PACKAGE_NAME_SENTINEL, availableEmailComponent.getPackageName()));
        }
        return null;
    }

    public static boolean isSomcEmailAvailableAndPermissionGranted(Context context) {
        return getAvailableEmailComponent(context) != null && PermissionUtils.isSendSomcEmailGranted(context);
    }

    public static ComponentName getAvailableEmailComponent(Context context) {
        ComponentName[] componentNameArr = {new ComponentName(OLD_EMAIL_PACKAGE, "com.android.email.activity.MessageComposeCalendar"), new ComponentName(NEW_EMAIL_PACKAGE, "com.sonymobile.email.activity.MessageComposeCalendar")};
        Intent intent = new Intent();
        for (int i = 0; i < 2; i++) {
            ComponentName componentName = componentNameArr[i];
            intent.setComponent(componentName);
            if (Utils.isIntentRecipientAvailable(context, intent)) {
                return componentName;
            }
        }
        return null;
    }

    public static Intent getGenericSendEmailIntent(String[] strArr, CharSequence charSequence, CharSequence charSequence2) {
        Intent intent = new Intent("android.intent.action.SENDTO");
        intent.setType(EMAIL_CONTENT_TYPE);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra("android.intent.extra.EMAIL", strArr);
        if (charSequence != null) {
            intent.putExtra("android.intent.extra.SUBJECT", charSequence);
        }
        if (charSequence2 != null) {
            intent.putExtra("android.intent.extra.TEXT", charSequence2);
        }
        return intent;
    }

    private void init(Context context) {
        Cursor cursorQuery = this.mContentResolver.query(CalendarContract.Events.CONTENT_URI, this.EVENT_PROJECTION, "_id=?", new String[]{String.valueOf(this.mEventId)}, null);
        this.mEventCursor = cursorQuery;
        cursorQuery.moveToFirst();
        Cursor cursorQuery2 = this.mContentResolver.query(CalendarContract.Calendars.CONTENT_URI, this.CALENDARS_PROJECTION, "_id=?", new String[]{String.valueOf(this.mEventCursor.getInt(3))}, null);
        this.mCalendarCursor = cursorQuery2;
        cursorQuery2.moveToFirst();
        this.mAttendeeCursor = this.mContentResolver.query(CalendarContract.Attendees.CONTENT_URI, this.ATTENDEE_PROJECTION, ATTENDEE_WHERE, new String[]{String.valueOf(this.mEventId)}, null);
        this.mCalendarCursor.moveToFirst();
        Uri accountUri = getAccountUri(context);
        Cursor cursorQuery3 = accountUri != null ? this.mContentResolver.query(accountUri, this.EAS_ACCOUNT_PROJECTION, EAS_ACCOUNT_WHERE, new String[]{this.mCalendarCursor.getString(1)}, null) : null;
        if (cursorQuery3 == null) {
            this.mAccountId = -1L;
            return;
        }
        cursorQuery3.moveToFirst();
        if (cursorQuery3.getCount() > 0) {
            this.mAccountId = cursorQuery3.getInt(0);
        } else {
            this.mAccountId = -1L;
        }
        this.mICalCreator = new ICalendarGenerator(this.mContext, this.mEventCursor, this.mCalendarCursor, this.mAttendeeCursor);
        cursorQuery3.close();
    }

    public void clear() {
        this.mEventCursor.close();
        this.mCalendarCursor.close();
        this.mAttendeeCursor.close();
    }

    public Intent createForwardIntent(Context context, int i, long j, String[] strArr, String str, String str2) {
        Intent intentCreateBasicIntent = createBasicIntent(getAvailableEmailComponent(context), i, j);
        this.mICalCreator.setmToList(strArr);
        addCalendarInformation(0, intentCreateBasicIntent);
        intentCreateBasicIntent.putExtra(CALENDAR_EXTRA_HANDLE_TYPE, "handle_forward");
        intentCreateBasicIntent.putExtra("android.intent.extra.SUBJECT", str2);
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        StringBuilder sb3 = new StringBuilder();
        this.mAttendeeCursor.moveToFirst();
        while (!this.mAttendeeCursor.isAfterLast()) {
            if (this.mAttendeeCursor.getInt(3) == 1) {
                String string = this.mAttendeeCursor.getString(1);
                if (!TextUtils.isEmpty(string)) {
                    sb2.append(string).append("; ");
                } else {
                    sb2.append("'").append(this.mAttendeeCursor.getString(0)).append("'").append("; ");
                }
            } else if (this.mAttendeeCursor.getInt(3) == 2) {
                String string2 = this.mAttendeeCursor.getString(1);
                if (!TextUtils.isEmpty(string2)) {
                    sb3.append(string2).append("; ");
                } else {
                    sb3.append(this.mAttendeeCursor.getString(0)).append("; ");
                }
            }
            this.mAttendeeCursor.moveToNext();
        }
        if (sb2.length() > 0) {
            sb.append(context.getString(R.string.message_compose_required_to_hint));
            sb.append(": ");
            sb.append((CharSequence) sb2);
            sb.append(CalendarConstants.CRLF);
        }
        if (sb3.length() > 0) {
            sb.append(context.getString(R.string.message_compose_optional_to_hint));
            sb.append(": ");
            sb.append(sb3.toString());
            sb.append(CalendarConstants.CRLF);
        }
        sb.append("--------------------\r\n");
        sb.append(str);
        Log.i("tagd", "description:\r\n" + sb.toString());
        intentCreateBasicIntent.putExtra("android.intent.extra.TEXT", sb.toString());
        intentCreateBasicIntent.putExtra("android.intent.extra.EMAIL", strArr);
        return intentCreateBasicIntent;
    }

    private Intent createBasicIntent(ComponentName componentName, int i, long j) {
        Intent intent = new Intent(CALENDAR_ACTION);
        intent.setComponent(componentName);
        intent.putExtra(EXTRA_ACCOUNT_ID, this.mAccountId);
        intent.putExtra("mime_type", CalendarConstants.ICAL_MIME_TYPE);
        this.mICalCreator.setWhich(i);
        this.mICalCreator.setDate(j);
        return intent;
    }

    private void addCalendarInformation(int i, Intent intent) {
        this.mICalCreator.setAction(i);
        this.mICalCreator.fillWritter();
        String string = this.mICalCreator.getWritter().toString();
        Log.i("tagd", "vCalendar:\r\n" + string);
        intent.putExtra("android.intent.extra.STREAM", string);
        intent.putExtra(CALENDAR_EXTRA_ATTACHMENT_NAME, "invite.ics");
    }

    public static Intent createSomcExchangeServiceIntent() {
        Intent intent = new Intent();
        intent.setAction(SOMC_EXCHANGE_SERVICE_ACTION);
        intent.setComponent(new ComponentName(SOMC_EXCHANGE_PACKAGE, SOMC_EXCHANGE_SERVICE_NAME));
        return intent;
    }
}
