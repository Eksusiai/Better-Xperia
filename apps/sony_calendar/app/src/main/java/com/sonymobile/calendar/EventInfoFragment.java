package com.sonymobile.calendar;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TaskStackBuilder;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import com.sonyericsson.calendar.util.CalendarColorService;
import com.sonyericsson.calendar.util.CalendarConstants;
import com.sonyericsson.calendar.util.CalendarInstanceService;
import com.sonyericsson.calendar.util.EmailIntentUtil;
import com.sonyericsson.calendar.util.EventDataContainer;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonyericsson.calendar.util.EventRecurrence;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonyericsson.calendar.util.VCalendarParser;
import com.sonymobile.calendar.design.SnackBar;
import com.sonymobile.calendar.editevent.EditEventActivity;
import com.sonymobile.calendar.editevent.EditEventView;
import com.sonymobile.calendar.editevent.TabletEditEventActivity;
import com.sonymobile.calendar.linkedin.LinkedInUtils;
import com.sonymobile.calendar.linkedin.ui.SyncWithLinkedInView;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.calendar.provider.SomcCalendarContract;
import com.sonymobile.calendar.tablet.TabletEventInfoActivity;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.lunar.lib.LunarContract;
import com.sonymobile.lunar.lib.LunarUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class EventInfoFragment extends Fragment implements EventAttendeesExpandableLayout.OnReplyClickedListener, SnackBar.ISnackBarCallback, LoaderManager.LoaderCallbacks<Cursor> {
    public static final int ACTION_TYPE_FORWARD = 3;
    public static final int ACTION_TYPE_REPLY = 1;
    public static final int ACTION_TYPE_REPLY_ALL = 2;
    private static final int ATTENDEES_INDEX_EMAIL = 2;
    private static final int ATTENDEES_INDEX_ID = 0;
    private static final int ATTENDEES_INDEX_NAME = 1;
    private static final int ATTENDEES_INDEX_RELATIONSHIP = 3;
    private static final int ATTENDEES_INDEX_STATUS = 4;
    private static final int ATTENDEES_INDEX_TYPE = 5;
    private static final String[] ATTENDEES_PROJECTION;
    private static final String ATTENDEES_SORT_ORDER = "attendeeName ASC, attendeeEmail ASC";
    private static final String ATTENDEES_WHERE = "event_id=?";
    private static final int ATTENDEE_ID_NONE = -1;
    private static final int ATTENDEE_LOADER_ID = 4;
    private static final int ATTENDEE_NO_RESPONSE = -1;
    private static final String BUNDLE_KEY_DELETE_DIALOG_VISIBLE = "key_delete_dialog_visible";
    private static final String BUNDLE_KEY_END_MILLIS = "key_end_millis";
    private static final String BUNDLE_KEY_EVENT_INSTANCE_ID = "key_event_instance_id";
    private static final String BUNDLE_KEY_EVENT_URI = "key_event_uri";
    private static final String BUNDLE_KEY_EXCHANGE_API_VERSION = "somcExchangeApiVersion";
    private static final String BUNDLE_KEY_IS_DIALOG = "key_fragment_is_dialog";
    private static final String BUNDLE_KEY_IS_LUNAR_EVENT = "key_is_lunar_event";
    private static final String BUNDLE_KEY_START_MILLIS = "key_start_millis";
    static final int CALENDARS_INDEX_DISPLAY_NAME = 1;
    static final int CALENDARS_INDEX_OWNER_ACCOUNT = 2;
    static final int CALENDARS_INDEX_OWNER_CAN_RESPOND = 3;
    static final int CALENDARS_INDEX_SYNC_ACCOUNT_TYPE = 4;
    static final String[] CALENDARS_PROJECTION;
    static final String CALENDARS_WHERE = "_id=?";
    private static final int CALENDAR_LOADER_ID = 1;
    public static final String CREATE_EXCEPTION_ATTENDEE_ID_FORWARD = "create_exception_attendee_id_forward";
    public static final String CREATE_EXCEPTION_EVENT_ID_FORWARD = "create_exception_event_id_forward";
    public static final String CREATE_EXCEPTION_FORWARD = "create_exception_forward";
    private static final int EAS_ACCIUNT_ID = 0;
    private static final String EAS_ACCOUNT_EMAIL_ADDRESS_WHERE = "emailAddress=?";
    private static final String[] EAS_ACCOUNT_PROJECTION;
    private static final String EMAIL_ADDRESS = "emailAddress";
    private static final int EMAIL_PROVIDER_LOADER_ID = 5;
    public static final float EVENT_DESCRIPTION_MAX_SCALE_FACTOR = 1.015f;
    public static final float EVENT_DESCRIPTION_MIN_SCALE_FACTOR = 0.985f;
    private static final String EVENT_DESCRIPTION_TEXT_SIZE = "event_description_text_size";
    private static final int EVENT_LOADER_ID = 0;
    private static final String[] EVENT_PROJECTION;
    private static final int EXTENDED_PROPERTIES_LOADER_ID = 2;
    private static final int EXTENDED_PROP_MEETING_CANCELED = 7;
    private static final String EXTENDED_PROP_MEETING_STATUS = "meeting_status";
    private static final String[] EXTENDED_PROP_PROJECTION;
    private static final String EXTENDED_PROP_SELECTION = "event_id=? AND name=?";
    private static final String EXTRA_ACCOUNT_ID = "account_id";
    public static final String FORWARD_EVENT_ATTEND_STATUS = "attend_status";
    public static final String FORWARD_EVENT_DESCRIPTION = "description";
    public static final String FORWARD_EVENT_END_MILLIS = "end_millis";
    public static final String FORWARD_EVENT_ID = "eventId";
    public static final String FORWARD_EVENT_OWNER_ACCOUNT = "owner_account";
    public static final String FORWARD_EVENT_START_MILLIS = "start_millis";
    public static final String FORWARD_EVENT_SUBJECT = "subject";
    public static final String FORWARD_EVENT_WHICH = "forward_which";
    public static final float MAX_DESCRIPTION_TEXT_SCALE_FACTOR = 3.0f;
    private static final int MENU_GROUP_DELETE = 1;
    public static final String ORGANIZER_EMAIL = "ORGANIZER_EMAIL";
    private static final String PERIOD_SPACE = ". ";
    private static final String RECORD_ID = "_id";
    private static final int REMINDERS_INDEX_MINUTES = 1;
    private static final int REMINDERS_LOADER_ID = 3;
    private static final String[] REMINDERS_PROJECTION;
    private static final String REMINDERS_SORT = "minutes";
    private static final String REMINDERS_WHERE = "event_id=? AND (method=1 OR method=0)";
    private static final int REPLY_WITH_COMMENT_MIN_VERSION = 3;
    public static final String TAG = "EventInfoFragment";
    private static final String TAG_ATTENDEES = "ATTENDEES";
    public static final String TAG_DIALOG = "EventInfoFragmentDialog";
    static final int UPDATE_ALL = 1;
    static final int UPDATE_SINGLE = 0;
    private static final Formatter mFormatter;
    private static final StringBuilder mStringBuilder;
    private long mAccountId;
    private AppCompatActivity mActivity;
    private int mAttendStatusOld;
    private Uri mAttendeesUri;
    private String mCalendarAccountName;
    private String mCalendarAccountType;
    private String mCalendarDisplayName;
    private String mCalendarOwnerAccount;
    private String mCalendarSyncAccountType;
    private Uri mCalendarsUri;
    private boolean mCanModifyCalendar;
    private boolean mCanModifyEvent;
    private float mCurrentDescriptionScaleFactor;
    private DeleteEventHelper mDeleteEventHelper;
    private TextView mDescriptionTextView;
    private EditResponseHelper mEditResponseHelper;
    private long mEndMillis;
    private EventAttendeesExpandableLayout mEventAttendeesContainer;
    private int mEventColor;
    private Drawable mEventColorDrawable;
    private EventDataContainer mEventData;
    private LinearLayout mEventInfoContainer;
    private long mEventInstanceId;
    private EventLocationWithMap mEventLocation;
    private LinearLayout mEventResponseContainer;
    private TextView mEventTitleText;
    private Uri mEventsUri;
    private Uri mExtendedPropertiesUri;
    private long mForwardAttendeeId;
    private long mForwardEventId;
    private boolean mIsExceptionToRepetitive;
    private boolean mIsOrganizer;
    private String mLastComment;
    private String mLocationAddress;
    private int mNumOfAttendees;
    private boolean mOrganizerCanRespond;
    private int mOriginalAttendeeResponse;
    private ArrayList<Integer> mRemindersInEvent;
    private Uri mRemindersUri;
    private Button mResponseAccept;
    private Button mResponseDecline;
    private Button mResponseMaybe;
    private View mRootView;
    private ScaleGestureDetector mScaleDetector;
    private ScrollView mScrollView;
    private CustomShareActionProvider mShareActionProvider;
    private SnackBar mSnackBar;
    private long mStartMillis;
    private String mSyncId;
    private Toolbar mToolbar;
    private Uri mUri;
    private StringBuilder mOrganizerEmail = new StringBuilder();
    private final ArrayList<String> mEmailList = new ArrayList<>();
    private long mCalendarOwnerAttendeeId = -1;
    private String mOrganizer = "";
    private boolean mChanged = false;
    private boolean mIsExchangeAccount = true;
    private boolean mIsLocalAccount = false;
    private boolean mIsCreateExceptionForward = false;
    private boolean mIsEventCanceled = false;
    private int mAttendStatus = 0;
    private int mWhichItem = -1;
    private int mForwardWhich = -1;
    private final int FORWARD_REQUEST_CODE = 1;
    private final int EDIT_REQUEST_CODE = 2;
    private Runnable mUpdateTZ = null;
    private boolean mIsLunarEvent = false;
    private boolean mOpenEditEventActivity = false;
    private boolean mIsDialog = false;
    public boolean mIsStopped = true;
    private boolean mDismissOnResume = false;
    private boolean mDeleteDialogVisible = false;
    private int mSomcExchangeApiVersion = -1;
    private final DialogInterface.OnClickListener mListListener = new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.EventInfoFragment.8
        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            EventInfoFragment.this.mForwardWhich = i;
            EventInfoFragment.this.doForward();
            dialogInterface.dismiss();
        }
    };
    private final OnColorPickedListener onColorPickedListener = new OnColorPickedListener() { // from class: com.sonymobile.calendar.EventInfoFragment.11
        @Override // com.sonymobile.calendar.OnColorPickedListener
        public void onColorPicked(int i, boolean z) {
            CalendarInstanceService.getInstance().updateEventColor(EventInfoFragment.this.mActivity, i, EventInfoFragment.this.mEventData.id, EventInfoFragment.this.mIsLunarEvent, EventInfoFragment.this.getAccountName(), EventInfoFragment.this.getAccountType());
            if (i != 0) {
                EventInfoFragment.this.updateEventColorIndicator(i);
            } else {
                CalendarColorService.getInstance().requestColor(EventInfoFragment.this.mActivity, new IAsyncServiceResultHandler() { // from class: com.sonymobile.calendar.EventInfoFragment.11.1
                    @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
                    public void onResult(Object obj, Object obj2) {
                        EventInfoFragment.this.updateEventColorIndicator(((Integer) obj).intValue());
                    }
                }, 0, EventInfoFragment.this.mEventData.calendarId, true, EventInfoFragment.this.mIsLunarEvent);
            }
        }
    };
    private final Runnable onDeleteRunnable = new Runnable() { // from class: com.sonymobile.calendar.EventInfoFragment.12
        @Override // java.lang.Runnable
        public void run() {
            if (EventInfoFragment.this.mIsStopped) {
                EventInfoFragment.this.mDismissOnResume = true;
                return;
            }
            if (EventInfoFragment.this.isVisible()) {
                EventInfoFragment.this.mActivity.finish();
            }
            if (((AgendaFragment) EventInfoFragment.this.getFragmentManager().findFragmentById(R.id.agendaGridFragment)) != null) {
                Utils.setAgendaSelectedEventInstanceId(0L);
                Utils.setAgendaSelectedBegin(0L);
            }
        }
    };

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    static /* synthetic */ float access$1432(EventInfoFragment eventInfoFragment, float f) {
        float f2 = eventInfoFragment.mCurrentDescriptionScaleFactor * f;
        eventInfoFragment.mCurrentDescriptionScaleFactor = f2;
        return f2;
    }

    static {
        StringBuilder sb = new StringBuilder(50);
        mStringBuilder = sb;
        mFormatter = new Formatter(sb, Locale.getDefault());
        EVENT_PROJECTION = new String[]{RECORD_ID, LunarContract.EventsColumns.TITLE, "rrule", "allDay", LunarContract.EventsColumns.CALENDAR_ID, LunarContract.EventsColumns.DTSTART, "_sync_id", LunarContract.EventsColumns.EVENT_TIMEZONE, "description", LunarContract.EventsColumns.EVENT_LOCATION, LunarContract.EventsColumns.HAS_ALARM, LunarContract.CalendarColumns.CALENDAR_ACCESS_LEVEL, LunarContract.CalendarColumns.CALENDAR_COLOR, LunarContract.EventsColumns.HAS_ATTENDEE_DATA, LunarContract.EventsColumns.GUESTS_CAN_MODIFY, LunarContract.EventsColumns.ORGANIZER, "account_type", LunarContract.EventsColumns.EVENT_COLOR, LunarContract.EventsColumns.DTSTART, LunarContract.EventsColumns.DTEND};
        ATTENDEES_PROJECTION = new String[]{RECORD_ID, LunarContract.AttendeesColumns.ATTENDEE_NAME, LunarContract.AttendeesColumns.ATTENDEE_EMAIL, LunarContract.AttendeesColumns.ATTENDEE_RELATIONSHIP, LunarContract.AttendeesColumns.ATTENDEE_STATUS, LunarContract.AttendeesColumns.ATTENDEE_TYPE};
        EXTENDED_PROP_PROJECTION = new String[]{"value"};
        CALENDARS_PROJECTION = new String[]{RECORD_ID, "calendar_displayName", "ownerAccount", "canOrganizerRespond", "account_type", "account_name"};
        REMINDERS_PROJECTION = new String[]{RECORD_ID, "minutes"};
        EAS_ACCOUNT_PROJECTION = new String[]{RECORD_ID};
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (AppCompatActivity) activity;
    }

    public static EventInfoFragment newInstance(Intent intent, boolean z) {
        Uri data = intent.getData();
        return newInstance(data, intent.getLongExtra(RECORD_ID, -1L), intent.getLongExtra(LunarContract.EXTRA_EVENT_BEGIN_TIME, 0L), intent.getLongExtra(LunarContract.EXTRA_EVENT_END_TIME, 0L), z, data.getAuthority().equalsIgnoreCase(LunarContract.AUTHORITY));
    }

    public static EventInfoFragment newInstance(Uri uri, long j, long j2, long j3, boolean z, boolean z2) {
        EventInfoFragment eventInfoFragment = new EventInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_KEY_EVENT_URI, uri);
        bundle.putLong(BUNDLE_KEY_START_MILLIS, j2);
        bundle.putLong(BUNDLE_KEY_END_MILLIS, j3);
        bundle.putBoolean(BUNDLE_KEY_IS_DIALOG, z);
        bundle.putBoolean(BUNDLE_KEY_IS_LUNAR_EVENT, z2);
        bundle.putLong(BUNDLE_KEY_EVENT_INSTANCE_ID, j);
        eventInfoFragment.setArguments(bundle);
        return eventInfoFragment;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (bundle != null) {
            onRestoreInstanceState(bundle);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        this.mUri = null;
        if (bundle != null) {
            this.mUri = (Uri) bundle.getParcelable(BUNDLE_KEY_EVENT_URI);
            this.mSomcExchangeApiVersion = bundle.getInt(BUNDLE_KEY_EXCHANGE_API_VERSION, -1);
        }
        if (this.mUri == null) {
            this.mUri = (Uri) arguments.getParcelable(BUNDLE_KEY_EVENT_URI);
        }
        this.mStartMillis = arguments.getLong(BUNDLE_KEY_START_MILLIS, 0L);
        this.mEndMillis = arguments.getLong(BUNDLE_KEY_END_MILLIS, 0L);
        this.mIsLunarEvent = arguments.getBoolean(BUNDLE_KEY_IS_LUNAR_EVENT);
        this.mIsDialog = arguments.getBoolean(BUNDLE_KEY_IS_DIALOG);
        this.mEventInstanceId = arguments.getLong(BUNDLE_KEY_EVENT_INSTANCE_ID);
        updateUris();
        if (!LunarAvailabilityManager.isLunarAvailable(this.mActivity) && this.mIsLunarEvent) {
            this.mActivity.finish();
            return;
        }
        if (Utils.isTabletDevice(this.mActivity)) {
            Utils.setAgendaSelectedEventInstanceId(this.mEventInstanceId);
            Utils.setAgendaSelectedBegin(this.mStartMillis);
            Utils.setAgendSelectedIsLunarEvent(this.mIsLunarEvent);
        }
        this.mEditResponseHelper = new EditResponseHelper(this.mActivity);
        Fragment fragmentFindFragmentByTag = getFragmentManager().findFragmentByTag(ColorPickerDialogBase.TAG);
        if (fragmentFindFragmentByTag != null) {
            ((ColorPickerDialogBase) fragmentFindFragmentByTag).setOnColorPickedListener(this.onColorPickedListener);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mRootView = layoutInflater.inflate(R.layout.event_info_fragment, viewGroup, false);
        init();
        return this.mRootView;
    }

    private void initLayout() {
        this.mEventTitleText = (TextView) this.mRootView.findViewById(R.id.event_info_description_title);
        this.mDescriptionTextView = (TextView) this.mRootView.findViewById(R.id.description);
        this.mScrollView = (ScrollView) this.mRootView.findViewById(R.id.event_info_scroll_view);
        EventAttendeesExpandableLayout eventAttendeesExpandableLayout = (EventAttendeesExpandableLayout) this.mRootView.findViewById(R.id.event_attendees_expandable);
        this.mEventAttendeesContainer = eventAttendeesExpandableLayout;
        eventAttendeesExpandableLayout.setOnReplyClickedListener(this);
        this.mEventInfoContainer = (LinearLayout) this.mRootView.findViewById(R.id.event_info_container);
        this.mEventResponseContainer = (LinearLayout) this.mRootView.findViewById(R.id.event_response_container);
        this.mEventLocation = (EventLocationWithMap) this.mRootView.findViewById(R.id.event_location_with_map);
    }

    private void initCalendarData(Cursor cursor) {
        this.mCalendarOwnerAccount = "";
        boolean z = false;
        if (cursor != null) {
            cursor.moveToFirst();
            this.mCalendarOwnerAccount = cursor.getString(2);
            this.mOrganizerCanRespond = cursor.getInt(3) != 0;
            this.mEventData.ownerAccount = cursor.getString(2);
            String string = cursor.getString(4);
            this.mCalendarSyncAccountType = string;
            if (string != null) {
                this.mIsLocalAccount = string.equals("LOCAL");
            }
            this.mCalendarDisplayName = cursor.getString(1);
            this.mCalendarAccountName = cursor.getString(cursor.getColumnIndex("account_name"));
            this.mCalendarAccountType = cursor.getString(cursor.getColumnIndex("account_type"));
        }
        String str = this.mEventData.eventOrganizer;
        String str2 = this.mCalendarOwnerAccount;
        if ((str2 != null && !str2.isEmpty()) || (str != null && !str.isEmpty())) {
            this.mIsOrganizer = this.mCalendarOwnerAccount.equalsIgnoreCase(str);
        }
        this.mOrganizer = str;
        this.mEventAttendeesContainer.setReplyOrganizerButton(this.mIsOrganizer ? "" : getResources().getString(R.string.event_organizer));
        if (this.mCanModifyCalendar && (this.mIsOrganizer || this.mEventData.guestCanModify != 0)) {
            z = true;
        }
        this.mCanModifyEvent = z;
        if (TextUtils.isEmpty(this.mCalendarDisplayName) || this.mCalendarDisplayName.trim().length() == 0) {
            setTextCommon(R.id.calendars, this.mCalendarDisplayName);
        } else {
            setTextCommon(R.id.calendars, Utils.getCalendarDisplayName(this.mActivity, this.mCalendarSyncAccountType, this.mCalendarDisplayName));
        }
        setupToolbarMenu();
    }

    private void initScaleDetector() {
        this.mScaleDetector = new ScaleGestureDetector(this.mActivity, new ScaleListener());
        this.mScrollView.setOnTouchListener(new View.OnTouchListener() { // from class: com.sonymobile.calendar.EventInfoFragment.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                EventInfoFragment.this.mScaleDetector.onTouchEvent(motionEvent);
                return false;
            }
        });
    }

    private void initAccountType(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            this.mAccountId = cursor.getLong(0);
        }
        if (Utils.isExchangeAccountType(this.mEventData.syncAccountType) || this.mAccountId >= 1) {
            return;
        }
        this.mIsExchangeAccount = false;
        setupToolbarMenu();
    }

    private static String[] makeSelectionArgument(Object... objArr) {
        int length = objArr.length;
        String[] strArr = new String[length];
        for (int i = 0; i < length; i++) {
            strArr[i] = objArr[i].toString();
        }
        return strArr;
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        EventDataContainer eventDataContainer = this.mEventData;
        long j = eventDataContainer == null ? -1L : eventDataContainer.id;
        if (i == 0) {
            return new CursorLoader(this.mActivity, this.mUri, EVENT_PROJECTION, null, null, null);
        }
        if (i == 1) {
            EventDataContainer eventDataContainer2 = this.mEventData;
            return new CursorLoader(this.mActivity, this.mCalendarsUri, CALENDARS_PROJECTION, CALENDARS_WHERE, makeSelectionArgument(Integer.valueOf(eventDataContainer2 == null ? -1 : eventDataContainer2.calendarId)), null);
        }
        if (i == 2) {
            return new CursorLoader(this.mActivity, this.mExtendedPropertiesUri, EXTENDED_PROP_PROJECTION, EXTENDED_PROP_SELECTION, makeSelectionArgument(Long.valueOf(j), EXTENDED_PROP_MEETING_STATUS), null);
        }
        if (i == 3) {
            return new CursorLoader(this.mActivity, this.mRemindersUri, REMINDERS_PROJECTION, REMINDERS_WHERE, makeSelectionArgument(Long.valueOf(j)), "minutes");
        }
        if (i == 4) {
            return new CursorLoader(this.mActivity, this.mAttendeesUri, ATTENDEES_PROJECTION, ATTENDEES_WHERE, makeSelectionArgument(Long.valueOf(j)), ATTENDEES_SORT_ORDER);
        }
        if (i == 5) {
            return new CursorLoader(this.mActivity, Uri.parse("content://" + EmailIntentUtil.getAvailableEmailComponent(this.mActivity).getPackageName() + ".provider/account"), EAS_ACCOUNT_PROJECTION, EAS_ACCOUNT_EMAIL_ADDRESS_WHERE, new String[]{this.mCalendarOwnerAccount}, null);
        }
        Log.e(TAG, "Unexpected id in onCreateLoader()");
        return null;
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int id = loader.getId();
        if (id == 0) {
            if (cursor == null || cursor.getCount() == 0) {
                return;
            }
            initEventDataContainer(cursor);
            initEventDependentLoaders();
            return;
        }
        if (id == 1) {
            initCalendarData(cursor);
            initCalendarsDependentLoaders();
            return;
        }
        if (id == 2) {
            checkIfEventIsCanceled(cursor);
            return;
        }
        if (id == 3) {
            initReminders(cursor);
            return;
        }
        if (id == 4) {
            initAttendeesData(cursor);
            this.mEventAttendeesContainer.updateContactPictures();
            updateResponse();
            setupToolbarMenu();
            return;
        }
        if (id == 5) {
            initAccountType(cursor);
            setupToolbarMenu();
        } else {
            Log.e(TAG, "Unexpected id in onLoadFinished()");
        }
    }

    private void initEventDependentLoaders() {
        getLoaderManager().restartLoader(1, null, this);
        getLoaderManager().restartLoader(2, null, this);
        if (this.mEventData.hasAlarm != 0) {
            getLoaderManager().restartLoader(3, null, this);
        }
    }

    private void initCalendarsDependentLoaders() {
        getLoaderManager().initLoader(4, null, this);
        if (EmailIntentUtil.isSomcEmailAvailableAndPermissionGranted(this.mActivity)) {
            getLoaderManager().initLoader(5, null, this);
        } else {
            this.mIsExchangeAccount = false;
            setupToolbarMenu();
        }
    }

    private void initEventDataContainer(Cursor cursor) {
        cursor.moveToFirst();
        EventDataContainer eventDataContainer = new EventDataContainer(cursor, this.mIsLunarEvent);
        this.mEventData = eventDataContainer;
        this.mEventColor = eventDataContainer.eventColor != 0 ? this.mEventData.eventColor : this.mEventData.color;
        this.mCanModifyCalendar = this.mEventData.visibility >= 500;
        modifyIndicator();
        updateView();
        this.mRootView.setVisibility(0);
    }

    private boolean isRepetitive() {
        return this.mEventData.rrule != null;
    }

    private boolean haveAttendeeData() {
        return this.mEventData.hasAttendeeData != 0;
    }

    private void initAttendeesData(Cursor cursor) {
        boolean z;
        boolean z2;
        this.mOriginalAttendeeResponse = -1;
        this.mCalendarOwnerAttendeeId = -1L;
        this.mNumOfAttendees = 0;
        ArrayList<EventAttendeesExpandableLayout.Attendee> arrayList = new ArrayList<>();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        this.mOrganizerEmail = new StringBuilder();
        if (cursor != null) {
            this.mNumOfAttendees = cursor.getCount();
            if (cursor.moveToFirst()) {
                EventAttendeesExpandableLayout.Attendee attendee = null;
                do {
                    int i = cursor.getInt(4);
                    String string = cursor.getString(1);
                    String string2 = cursor.getString(2);
                    boolean z3 = !TextUtils.isEmpty(string);
                    boolean z4 = !TextUtils.isEmpty(string2);
                    if (cursor.getInt(3) == 2) {
                        if (z3) {
                            this.mOrganizer = string;
                            this.mOrganizerEmail.append('\"').append(string).append("\" ");
                        }
                        if (z4) {
                            if (!z3) {
                                this.mOrganizer = string2;
                            }
                            this.mOrganizerEmail.append('<').append(string2).append(">");
                        }
                        z = true;
                    } else {
                        z = false;
                    }
                    if (!TextUtils.isEmpty(this.mCalendarOwnerAccount)) {
                        StringBuilder sb = new StringBuilder();
                        if (z3 && !string.equalsIgnoreCase(this.mCalendarOwnerAccount)) {
                            sb.append('\"').append(string).append("\" ");
                        }
                        if (z4 && !string2.equalsIgnoreCase(this.mCalendarOwnerAccount)) {
                            sb.append('<').append(string2).append("> ");
                            ArrayList<String> arrayList4 = this.mEmailList;
                            if (arrayList4 != null) {
                                arrayList4.add(sb.toString());
                            }
                        }
                    }
                    if (this.mCalendarOwnerAttendeeId == -1 && this.mCalendarOwnerAccount.equalsIgnoreCase(string2)) {
                        this.mCalendarOwnerAttendeeId = cursor.getInt(0);
                        int i2 = cursor.getInt(4);
                        this.mOriginalAttendeeResponse = i2;
                        if (this.mAttendStatus == 0) {
                            this.mAttendStatus = i2;
                            this.mAttendStatusOld = i2;
                        }
                        if (!this.mIsExceptionToRepetitive) {
                            attendee = new EventAttendeesExpandableLayout.Attendee(getResources().getString(R.string.event_self_attendee), null, 1, z, true, i);
                        }
                        z2 = false;
                    } else {
                        int i3 = cursor.getInt(5);
                        EventAttendeesExpandableLayout.Attendee attendee2 = new EventAttendeesExpandableLayout.Attendee(string, string2, i3, z, false, i);
                        if (i3 != 1) {
                            z2 = false;
                            arrayList3.add(attendee2);
                        } else if (z) {
                            z2 = false;
                            arrayList2.add(0, attendee2);
                        } else {
                            z2 = false;
                            arrayList2.add(attendee2);
                        }
                    }
                } while (cursor.moveToNext());
                cursor.moveToFirst();
                if (attendee != null) {
                    arrayList.add(attendee);
                }
                Iterator it = arrayList2.iterator();
                while (it.hasNext()) {
                    arrayList.add((EventAttendeesExpandableLayout.Attendee) it.next());
                }
                Iterator it2 = arrayList3.iterator();
                while (it2.hasNext()) {
                    arrayList.add((EventAttendeesExpandableLayout.Attendee) it2.next());
                }
            }
        }
        this.mEventAttendeesContainer.setAttendeesArrayList(arrayList);
    }

    private void init() {
        initLayout();
        initFormAnimation(this.mEventInfoContainer);
        getLoaderManager().initLoader(0, null, this);
        if (this.mEventData == null) {
            this.mRootView.setVisibility(8);
        }
        ArrayList<String> arrayList = this.mEmailList;
        if (arrayList != null && arrayList.size() != 0) {
            this.mEmailList.clear();
        }
        this.mIsExceptionToRepetitive = false;
        initResponse();
        initToolbar();
        initScaleDetector();
        this.mDeleteEventHelper = new DeleteEventHelper(this.mActivity, !Utils.isTabletDevice(this.mActivity));
        setEventDescriptionTextSize();
        this.mSnackBar = SnackBar.make(this.mActivity).action(R.string.meeting_undo_description, this);
    }

    public void handleSyncWithLinkedInView() {
        SyncWithLinkedInView syncWithLinkedInView = (SyncWithLinkedInView) this.mRootView.findViewById(R.id.connect_with_linkedin_in_meeting_details);
        if (LinkedInUtils.shouldShowConnectViewInMeetingDetails(this.mActivity)) {
            syncWithLinkedInView.setType(1);
            syncWithLinkedInView.setVisibility(0);
        } else {
            syncWithLinkedInView.setVisibility(8);
        }
    }

    private void checkIfEventIsCanceled(Cursor cursor) {
        int columnIndex;
        String string;
        if (cursor == null || !cursor.moveToFirst() || (columnIndex = cursor.getColumnIndex("value")) == -1 || (string = cursor.getString(columnIndex)) == null) {
            return;
        }
        try {
            if (Integer.parseInt(string) == 7) {
                this.mIsEventCanceled = true;
            }
        } catch (NumberFormatException e) {
            Log.e("value", e.getMessage());
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) this.mRootView.findViewById(R.id.toolbar);
        this.mToolbar = toolbar;
        toolbar.inflateMenu(R.menu.event_info_title_bar);
        setupToolbarMenu();
        initToolbarNavigationButton();
    }

    private void setupToolbarMenu() {
        Menu menu = this.mToolbar.getMenu();
        boolean z = this.mNumOfAttendees > 1;
        if (!EmailIntentUtil.isSomcEmailAvailableAndPermissionGranted(this.mActivity) || !this.mIsExchangeAccount || !z || (this.mCanModifyEvent && !this.mIsOrganizer)) {
            menu.findItem(R.id.forward).setVisible(false);
        } else {
            menu.findItem(R.id.forward).setVisible(true);
        }
        this.mEventAttendeesContainer.showReplyButtons(z);
        menu.findItem(R.id.edit).setVisible(this.mCanModifyEvent);
        Drawable icon = menu.findItem(R.id.set_event_color).getIcon();
        this.mEventColorDrawable = icon;
        icon.setColorFilter(UiUtils.getDisplayColorFromColor(this.mEventColor), PorterDuff.Mode.MULTIPLY);
        CustomShareActionProvider customShareActionProvider = (CustomShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.share_menu));
        this.mShareActionProvider = customShareActionProvider;
        customShareActionProvider.setShareIntent(createShareIntent(null, CalendarConstants.VCAL_MIME_TYPE));
        this.mShareActionProvider.setOnShareTargetSelectedListener(new ShareActionProvider.OnShareTargetSelectedListener() { // from class: com.sonymobile.calendar.EventInfoFragment.2
            @Override // androidx.appcompat.widget.ShareActionProvider.OnShareTargetSelectedListener
            public boolean onShareTargetSelected(ShareActionProvider shareActionProvider, Intent intent) {
                if (!EventInfoFragment.this.mShareActionProvider.getParseSuccessfull()) {
                    Toast.makeText(EventInfoFragment.this.mActivity, EventInfoFragment.this.mActivity.getString(R.string.operation_failed), 0).show();
                }
                return false;
            }
        });
        menu.setGroupVisible(1, this.mCanModifyCalendar);
        menu.setGroupEnabled(1, this.mCanModifyCalendar);
        this.mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() { // from class: com.sonymobile.calendar.EventInfoFragment.3
            @Override // androidx.appcompat.widget.Toolbar.OnMenuItemClickListener
            public boolean onMenuItemClick(MenuItem menuItem) {
                return EventInfoFragment.this.onToolbarMenuItemClicked(menuItem);
            }
        });
    }

    private void initToolbarNavigationButton() {
        if (this.mIsDialog || !Utils.isTabletDevice(this.mActivity)) {
            this.mToolbar.setNavigationIcon(R.drawable.ic_close);
            this.mToolbar.setNavigationContentDescription(R.string.accessibility_close);
            this.mToolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.EventInfoFragment.4
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    EventInfoFragment.this.mActivity.finish();
                }
            });
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:20:0x0045  */
    /* JADX WARN: Code duplicated, block: B:22:0x004b  */
    /* JADX WARN: Code duplicated, block: B:23:0x004f  */
    public boolean onToolbarMenuItemClicked(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId != 1) {
            if (itemId != 2) {
                if (itemId == 3) {
                    if (isRepetitive()) {
                        doForwardRepeatEvent();
                    } else {
                        doForward();
                    }
                } else {
                    if (itemId == android.R.id.home) {
                        Intent parentActivityIntent = NavUtils.getParentActivityIntent(this.mActivity);
                        if (NavUtils.shouldUpRecreateTask(this.mActivity, parentActivityIntent)) {
                            TaskStackBuilder.create(this.mActivity).addNextIntentWithParentStack(parentActivityIntent).startActivities();
                        } else if (parentActivityIntent != null) {
                            NavUtils.navigateUpTo(this.mActivity, parentActivityIntent);
                        }
                        return true;
                    }
                    if (itemId == R.id.delete) {
                        doDelete();
                    } else if (itemId == R.id.edit) {
                        doEdit();
                    } else if (itemId == R.id.forward) {
                        if (isRepetitive()) {
                            doForwardRepeatEvent();
                        } else {
                            doForward();
                        }
                    } else if (itemId == R.id.set_event_color) {
                        showEventColorPicker();
                    } else if (itemId == R.id.share_menu) {
                        sendVCalFile();
                    }
                }
            } else {
                doReply(2);
            }
        } else {
            doReply(1);
        }
        return true;
    }

    private void updateUris() {
        if (this.mIsLunarEvent) {
            this.mCalendarsUri = LunarContract.Calendars.CONTENT_URI;
            this.mAttendeesUri = LunarContract.Attendees.CONTENT_URI;
            this.mRemindersUri = LunarContract.Reminders.CONTENT_URI;
            this.mEventsUri = LunarContract.Events.CONTENT_URI;
            this.mExtendedPropertiesUri = LunarContract.ExtendedProperties.CONTENT_URI;
            return;
        }
        this.mCalendarsUri = CalendarContract.Calendars.CONTENT_URI;
        this.mAttendeesUri = CalendarContract.Attendees.CONTENT_URI;
        this.mRemindersUri = CalendarContract.Reminders.CONTENT_URI;
        this.mEventsUri = CalendarContract.Events.CONTENT_URI;
        this.mExtendedPropertiesUri = CalendarContract.ExtendedProperties.CONTENT_URI;
    }

    @Override // com.sonymobile.calendar.design.SnackBar.ISnackBarCallback
    public void actionCallBack() {
        this.mAttendStatus = this.mOriginalAttendeeResponse;
        updateCurrentResponseVisibility();
        this.mEventResponseContainer.setVisibility(0);
    }

    @Override // com.sonymobile.calendar.design.SnackBar.ISnackBarCallback
    public void onHideCallback() {
        this.mEventResponseContainer.setVisibility(0);
        saveResponse();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onResponseContainerClicked(int i) {
        if (this.mOriginalAttendeeResponse == i) {
            return;
        }
        if (isRepetitive() && !this.mIsCreateExceptionForward) {
            this.mSyncId = this.mEventData.syncId;
            this.mEditResponseHelper.setOnClickListener(new OnRecurrenceDialogClickListener(i));
            this.mEditResponseHelper.showSelectOccurrencesDialog(this.mWhichItem, this.mSyncId);
        } else if (canReplyWithComment()) {
            openCommentDialog(i);
        } else {
            processStatusSelection(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processStatusSelection(int i) {
        int i2;
        this.mAttendStatusOld = this.mAttendStatus;
        this.mAttendStatus = i;
        if (i == 1) {
            i2 = R.string.meeting_accepted_description;
        } else if (i == 2) {
            i2 = R.string.meeting_canceled_description;
        } else {
            if (i != 4) {
                Log.e(TAG_ATTENDEES, "No status");
                return;
            }
            i2 = R.string.meeting_tentative_description;
        }
        this.mEventResponseContainer.setVisibility(8);
        this.mSnackBar.message(i2).show();
    }

    @Override // com.sonymobile.calendar.EventAttendeesExpandableLayout.OnReplyClickedListener
    public void onReplyButtonClicked(int i) {
        if (i == 1) {
            doReply(1);
        } else if (i == 2) {
            doReply(2);
        }
    }

    private void saveResponse() {
        if (saveResponse(this.mActivity.getContentResolver())) {
            Toast.makeText(this.mActivity.getApplication(), R.string.saving_event, 0).show();
            updateCurrentResponseVisibility();
        }
    }

    private class OnCommentDialogClickListener implements DialogInterface.OnClickListener {
        private int mStatus;

        public OnCommentDialogClickListener(int i) {
            this.mStatus = i;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            String string = ((EditText) ((AlertDialog) dialogInterface).findViewById(R.id.input_comment)).getText().toString();
            if (!TextUtils.isEmpty(string)) {
                EventInfoFragment.this.mLastComment = string;
            }
            EventInfoFragment.this.processStatusSelection(this.mStatus);
            EventInfoFragment.this.updateCurrentResponseVisibility();
        }
    }

    private class OnRecurrenceDialogClickListener implements DialogInterface.OnClickListener {
        private final int mStatus;

        public OnRecurrenceDialogClickListener(int i) {
            this.mStatus = i;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            EventInfoFragment.this.mEditResponseHelper.closeDialog();
            if (EventInfoFragment.this.canReplyWithComment()) {
                EventInfoFragment.this.openCommentDialog(this.mStatus);
            } else {
                EventInfoFragment.this.processStatusSelection(this.mStatus);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean canReplyWithComment() {
        return this.mCalendarAccountType.equals(EmailIntentUtil.SOMC_EXCHANGE_PACKAGE) && this.mSomcExchangeApiVersion >= 3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openCommentDialog(int i) {
        this.mEditResponseHelper.setOnClickListener(new OnCommentDialogClickListener(i));
        this.mEditResponseHelper.showInputCommentDialog(getTitleForStatus(i));
    }

    private String getTitleForStatus(int i) {
        String[] stringArray = getResources().getStringArray(R.array.accepted_items);
        if (i == 1) {
            return stringArray[0];
        }
        if (i == 2) {
            return stringArray[2];
        }
        if (i != 4) {
            return null;
        }
        return stringArray[1];
    }

    private void initFormAnimation(ViewGroup viewGroup) {
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(4);
        layoutTransition.setDuration(100L);
        viewGroup.setLayoutTransition(layoutTransition);
    }

    private void initResponse() {
        this.mResponseAccept = (Button) this.mRootView.findViewById(R.id.response_accept);
        this.mResponseMaybe = (Button) this.mRootView.findViewById(R.id.response_maybe);
        this.mResponseDecline = (Button) this.mRootView.findViewById(R.id.response_decline);
        this.mResponseAccept.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.EventInfoFragment.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EventInfoFragment.this.onResponseContainerClicked(1);
            }
        });
        this.mResponseMaybe.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.EventInfoFragment.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EventInfoFragment.this.onResponseContainerClicked(4);
            }
        });
        this.mResponseDecline.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.EventInfoFragment.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EventInfoFragment.this.onResponseContainerClicked(2);
            }
        });
        updateCurrentResponseVisibility();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCurrentResponseVisibility() {
        if (isAdded()) {
            Context baseContext = this.mActivity.getBaseContext();
            Drawable drawable = ContextCompat.getDrawable(baseContext, R.drawable.ic_invite_accept);
            Drawable drawable2 = ContextCompat.getDrawable(baseContext, R.drawable.ic_invite_maybe);
            Drawable drawable3 = ContextCompat.getDrawable(baseContext, R.drawable.ic_invite_decline);
            int color = getColor(baseContext, R.color.event_response_accept);
            int color2 = getColor(baseContext, R.color.event_response_maybe);
            int color3 = getColor(baseContext, R.color.event_response_decline);
            int color4 = getColor(baseContext, R.color.transparent_dark_gray);
            int i = this.mAttendStatus;
            if (i == 1) {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                drawable2.setColorFilter(color4, PorterDuff.Mode.SRC_ATOP);
                drawable3.setColorFilter(color4, PorterDuff.Mode.SRC_ATOP);
                setResponseTextColor(color, color4, color4);
                setResponseButtonText(getString(R.string.accept_label), getString(R.string.decline_button_string));
            } else if (i == 2) {
                drawable.setColorFilter(color4, PorterDuff.Mode.SRC_ATOP);
                drawable2.setColorFilter(color4, PorterDuff.Mode.SRC_ATOP);
                drawable3.setColorFilter(color3, PorterDuff.Mode.SRC_ATOP);
                setResponseTextColor(color4, color4, color3);
                setResponseButtonText(getString(R.string.accept_button_string), getString(R.string.decline_label));
            } else if (i == 4) {
                drawable.setColorFilter(color4, PorterDuff.Mode.SRC_ATOP);
                drawable2.setColorFilter(color2, PorterDuff.Mode.SRC_ATOP);
                drawable3.setColorFilter(color4, PorterDuff.Mode.SRC_ATOP);
                setResponseTextColor(color4, color2, color4);
                setResponseButtonText(getString(R.string.accept_button_string), getString(R.string.decline_button_string));
            } else {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                drawable2.setColorFilter(color2, PorterDuff.Mode.SRC_ATOP);
                drawable3.setColorFilter(color3, PorterDuff.Mode.SRC_ATOP);
                setResponseTextColor(color4, color4, color4);
                setResponseButtonText(getString(R.string.accept_button_string), getString(R.string.decline_button_string));
            }
            setResponseDrawables(drawable, drawable3, drawable2);
        }
    }

    private int getColor(Context context, int i) {
        return ContextCompat.getColor(context, i);
    }

    private void setResponseDrawables(Drawable drawable, Drawable drawable2, Drawable drawable3) {
        this.mResponseAccept.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, drawable, (Drawable) null, (Drawable) null);
        this.mResponseDecline.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, drawable2, (Drawable) null, (Drawable) null);
        this.mResponseMaybe.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, drawable3, (Drawable) null, (Drawable) null);
    }

    private void setResponseButtonText(String str, String str2) {
        this.mResponseAccept.setText(str);
        this.mResponseDecline.setText(str2);
    }

    private void setResponseTextColor(int i, int i2, int i3) {
        this.mResponseAccept.setTextColor(i);
        this.mResponseMaybe.setTextColor(i2);
        this.mResponseDecline.setTextColor(i3);
    }

    public void restoreResponse() {
        this.mAttendStatus = this.mAttendStatusOld;
        updateResponse();
    }

    public void initReminders(Cursor cursor) {
        this.mRemindersInEvent = new ArrayList<>();
        if (this.mEventData.hasAlarm != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                this.mRemindersInEvent.add(Integer.valueOf(cursor.getInt(1)));
                cursor.moveToNext();
            }
        }
        ((EventReminderExpandableLayout) this.mRootView.findViewById(R.id.event_reminder_expandable)).setReminders(this.mRemindersInEvent);
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mEventAttendeesContainer.registerReceiver(this.mActivity);
        this.mIsStopped = false;
        if (this.mDismissOnResume && this.mRootView.getHandler() != null) {
            this.mRootView.getHandler().post(this.onDeleteRunnable);
        }
        if (this.mEventData == null) {
            this.mRootView.setVisibility(8);
        } else {
            this.mRootView.setVisibility(0);
        }
        this.mEventAttendeesContainer.updateContactPictures();
        updateResponse();
        updateView();
        if (this.mDeleteDialogVisible) {
            this.mDeleteEventHelper.delete(this.mStartMillis, this.mEndMillis, this.mEventData.id, -1, this.onDeleteRunnable, this.mIsLunarEvent);
            this.mDeleteEventHelper.setOnDismissListener(createDeleteOnDismissListener());
        }
        this.mEventAttendeesContainer.registerReceiver(this.mActivity);
        if (LinkedInUtils.isLinkedInEnabled(this.mActivity)) {
            handleSyncWithLinkedInView();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.mIsStopped = true;
        if (this.mDeleteDialogVisible) {
            this.mDeleteEventHelper.dismissAlertDialog();
        }
        GeneralPreferences.saveEventDescriptionScaleFactor(this.mActivity, this.mCurrentDescriptionScaleFactor);
        this.mEventAttendeesContainer.unregisterReceiver(this.mActivity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Intent createShareIntent(Uri uri, String str) {
        Intent intent = new Intent("android.intent.action.SEND");
        if (uri != null) {
            intent.setFlags(1);
            intent.putExtra("android.intent.extra.STREAM", uri);
        }
        if (str != null) {
            intent.setType(str);
        }
        return intent;
    }

    private void sendVCalFile() {
        ArrayList<EventInfo> arrayList = new ArrayList<>();
        arrayList.add(this.mEventData);
        VCalendarParser vCalendarParser = VCalendarParser.getInstance();
        vCalendarParser.writeToCalFile(arrayList, new VCalendarParserHandler(vCalendarParser), this.mActivity);
    }

    private class VCalendarParserHandler implements IAsyncServiceResultHandler {
        final VCalendarParser mParser;

        public VCalendarParserHandler(VCalendarParser vCalendarParser) {
            this.mParser = vCalendarParser;
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            boolean zBooleanValue = ((Boolean) obj).booleanValue();
            if (zBooleanValue) {
                EventInfoFragment.this.mShareActionProvider.setShareIntent(EventInfoFragment.this.createShareIntent(this.mParser.getFileUri(), CalendarConstants.VCAL_MIME_TYPE));
                EventInfoFragment.this.mShareActionProvider.setParseSuccessfull(zBooleanValue);
            }
        }
    }

    private void setEventDescriptionTextSize() {
        float eventDescriptionScaleFactor = GeneralPreferences.getEventDescriptionScaleFactor(this.mActivity, 1.0f);
        this.mCurrentDescriptionScaleFactor = eventDescriptionScaleFactor;
        TextView textView = this.mDescriptionTextView;
        textView.setTextSize(0, eventDescriptionScaleFactor * textView.getTextSize());
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private ScaleListener() {
        }

        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float f;
            float textSize;
            int iCompare = Float.compare(scaleGestureDetector.getScaleFactor(), 1.0f);
            if (iCompare == 0) {
                return true;
            }
            if (iCompare > 0) {
                f = 1.015f;
                EventInfoFragment.access$1432(EventInfoFragment.this, 1.015f);
                textSize = EventInfoFragment.this.mDescriptionTextView.getTextSize();
            } else {
                f = 0.985f;
                EventInfoFragment.access$1432(EventInfoFragment.this, 0.985f);
                textSize = EventInfoFragment.this.mDescriptionTextView.getTextSize();
            }
            float f2 = textSize * f;
            if (Float.compare(EventInfoFragment.this.mCurrentDescriptionScaleFactor, 1.0f) < 0) {
                EventInfoFragment.this.mCurrentDescriptionScaleFactor = 1.0f;
                return true;
            }
            if (EventInfoFragment.this.mCurrentDescriptionScaleFactor > 3.0f) {
                EventInfoFragment.this.mCurrentDescriptionScaleFactor = 3.0f;
                return true;
            }
            EventInfoFragment.this.mDescriptionTextView.setTextSize(0, f2);
            return true;
        }
    }

    private void doForwardRepeatEvent() {
        String str = this.mEventData.syncId;
        int whichEvents = this.mEditResponseHelper.getWhichEvents();
        if (str != null && str.length() > 0 && whichEvents == -1) {
            new AlertDialog.Builder(this.mActivity, R.style.AlertDialogTheme).setTitle(R.string.forward_event_title).setSingleChoiceItems(getResources().getStringArray(R.array.change_response_labels), -1, this.mListListener).setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) null).show().getButton(-1).setEnabled(false);
        } else {
            this.mForwardWhich = whichEvents;
            doForward();
        }
    }

    private boolean saveResponse(ContentResolver contentResolver) {
        if (this.mEventData == null || this.mAttendStatus <= 0 || this.mDeleteEventHelper.isDeleteEventComplete() || this.mCalendarOwnerAttendeeId == -1) {
            return false;
        }
        this.mOriginalAttendeeResponse = this.mAttendStatus;
        if (!isRepetitive()) {
            updateResponse(contentResolver, this.mEventData.id, this.mCalendarOwnerAttendeeId, this.mAttendStatus);
            return true;
        }
        int whichEvents = this.mEditResponseHelper.getWhichEvents();
        if (this.mForwardWhich == 0 && this.mIsCreateExceptionForward) {
            updateResponse(contentResolver, this.mForwardEventId, this.mForwardAttendeeId, this.mAttendStatus);
            return true;
        }
        if (this.mSyncId == null && whichEvents != -1) {
            whichEvents++;
        }
        if (whichEvents == -1) {
            return false;
        }
        if (whichEvents == 0) {
            createExceptionResponse(contentResolver, this.mEventData.id, this.mCalendarOwnerAttendeeId, this.mAttendStatus);
            return true;
        }
        if (whichEvents == 1) {
            updateResponse(contentResolver, this.mEventData.id, this.mCalendarOwnerAttendeeId, this.mAttendStatus);
            return true;
        }
        Log.e(TAG, "Unexpected choice for updating invitation response");
        return false;
    }

    private void updateResponse(ContentResolver contentResolver, long j, long j2, int i) {
        ContentValues contentValues = new ContentValues();
        if (!TextUtils.isEmpty(this.mCalendarOwnerAccount)) {
            contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_EMAIL, this.mCalendarOwnerAccount);
        }
        contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_STATUS, Integer.valueOf(i));
        contentValues.put("event_id", Long.valueOf(j));
        new AsyncQueryHandler(contentResolver) { // from class: com.sonymobile.calendar.EventInfoFragment.9
        }.startUpdate(0, null, ContentUris.withAppendedId(this.mAttendeesUri, j2), contentValues, null, null);
        insertReplyComment(j);
        Utils.scheduleSync(1, this.mCalendarOwnerAccount, this.mCalendarSyncAccountType, true, true);
        this.mActivity.onBackPressed();
    }

    private void createExceptionResponse(ContentResolver contentResolver, long j, long j2, int i) {
        ContentValues contentValues = new ContentValues();
        boolean z = this.mEventData.hasAlarm != 0;
        contentValues.put("originalInstanceTime", Long.valueOf(this.mStartMillis));
        contentValues.put(LunarContract.EventsColumns.SELF_ATTENDEE_STATUS, Integer.valueOf(i));
        contentValues.put(LunarContract.EventsColumns.STATUS, (Integer) 1);
        Uri.Builder builderBuildUpon = CalendarContract.Events.CONTENT_EXCEPTION_URI.buildUpon();
        ContentUris.appendId(builderBuildUpon, j);
        Uri uriInsert = contentResolver.insert(builderBuildUpon.build(), contentValues);
        if (uriInsert == null) {
            Log.e(TAG, "Error, there was a remote exception in content provider");
            return;
        }
        long j3 = Long.parseLong(uriInsert.getLastPathSegment());
        AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(contentResolver) { // from class: com.sonymobile.calendar.EventInfoFragment.10
        };
        if (z) {
            for (Integer num : this.mRemindersInEvent) {
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("event_id", Long.valueOf(j3));
                contentValues2.put("minutes", num);
                asyncQueryHandler.startInsert(0, null, this.mRemindersUri, contentValues2);
            }
        }
        this.mUri = ContentUris.withAppendedId(this.mEventsUri, j3);
        insertReplyComment(j3);
        refreshFragmentInfo();
    }

    private void insertReplyComment(long j) {
        String str = this.mLastComment;
        if (str == null || str.isEmpty()) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("event_id", Long.valueOf(j));
        contentValues.put(SomcCalendarContract.EventComments.COLUMN_EVENT_COMMENT, this.mLastComment);
        contentValues.put(SomcCalendarContract.EventComments.COLUMN_EVENT_DIRTY, (Boolean) true);
        this.mActivity.getContentResolver().insert(SomcCalendarContract.EventComments.CONTENT_URI, contentValues);
    }

    private void refreshFragmentInfo() {
        this.mIsExceptionToRepetitive = true;
        getLoaderManager().restartLoader(0, null, this);
        initResponse();
        Utils.scheduleSync(1, this.mCalendarOwnerAccount, this.mCalendarSyncAccountType, true, true);
    }

    private void doEdit() {
        Intent intent = new Intent("android.intent.action.EDIT", ContentUris.withAppendedId(this.mEventsUri, this.mEventData.id));
        intent.putExtra(LunarContract.EXTRA_EVENT_BEGIN_TIME, this.mStartMillis);
        intent.putExtra(LunarContract.EXTRA_EVENT_END_TIME, this.mEndMillis);
        intent.putExtra(ORGANIZER_EMAIL, this.mOrganizerEmail.toString());
        intent.setClass(this.mActivity, EditEventActivity.class);
        if (Utils.isTabletDevice(this.mActivity)) {
            intent.setClass(this.mActivity, TabletEditEventActivity.class);
        } else {
            intent.setClass(this.mActivity, EditEventActivity.class);
            this.mOpenEditEventActivity = true;
        }
        startActivityForResult(intent, 2);
        if (!Utils.isTabletDevice(this.mActivity) || (this.mActivity instanceof TabletEventInfoActivity)) {
            this.mActivity.finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doForward() {
        Intent intent = new Intent("android.intent.action.SEND");
        String string = this.mEventData.title;
        Resources resources = getResources();
        if (string == null || string.length() == 0) {
            string = resources.getString(R.string.no_title_label);
        }
        intent.putExtra(FORWARD_EVENT_ID, this.mEventData.id);
        intent.putExtra(FORWARD_EVENT_START_MILLIS, this.mStartMillis);
        intent.putExtra(FORWARD_EVENT_END_MILLIS, this.mEndMillis);
        intent.putExtra(FORWARD_EVENT_ATTEND_STATUS, this.mAttendStatus);
        intent.putExtra(FORWARD_EVENT_WHICH, this.mForwardWhich);
        intent.putExtra(FORWARD_EVENT_OWNER_ACCOUNT, this.mCalendarOwnerAccount);
        intent.putExtra("subject", resources.getString(R.string.clr_strings_subject_forward_txt) + string);
        intent.putExtra("description", this.mDescriptionTextView.getText().toString());
        intent.setClass(this.mActivity, ForwardActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1 && i2 == -1) {
            if (this.mForwardWhich != 0) {
                return;
            }
            this.mIsCreateExceptionForward = intent.getBooleanExtra(CREATE_EXCEPTION_FORWARD, false);
            this.mForwardEventId = intent.getLongExtra(CREATE_EXCEPTION_EVENT_ID_FORWARD, 0L);
            this.mForwardAttendeeId = intent.getLongExtra(CREATE_EXCEPTION_ATTENDEE_ID_FORWARD, 0L);
            saveResponse();
            return;
        }
        if (i == 2 && i2 == -1) {
            Uri uri = (Uri) intent.getParcelableExtra(EditEventView.NEW_EVENT_ID_EXTRA);
            AgendaFragment agendaFragment = (AgendaFragment) getFragmentManager().findFragmentById(R.id.agendaGridFragment);
            if (uri == null || agendaFragment == null) {
                return;
            }
            long id = ContentUris.parseId(this.mUri);
            this.mUri = uri;
            agendaFragment.getAgendaList().updateEvent(id, ContentUris.parseId(uri));
        }
    }

    private void doReply(int i) {
        String[] strArr;
        if (i == 1) {
            strArr = new String[]{this.mOrganizerEmail.toString()};
        } else if (i == 2) {
            ArrayList<String> arrayList = this.mEmailList;
            strArr = (String[]) arrayList.toArray(new String[arrayList.size()]);
        } else {
            strArr = null;
        }
        Intent genericSendEmailIntent = EmailIntentUtil.getGenericSendEmailIntent(strArr, getResources().getString(R.string.clr_strings_subject_reply_txt) + ((Object) this.mEventTitleText.getText()), this.mDescriptionTextView.getText());
        if (Utils.isIntentRecipientAvailable(this.mActivity, genericSendEmailIntent)) {
            startActivity(genericSendEmailIntent);
        } else {
            Toast.makeText(this.mActivity, R.string.quick_response_email_failed, 1).show();
        }
    }

    private void doDelete() {
        if (Utils.isTabletDevice(this.mActivity)) {
            AgendaFragment agendaFragment = (AgendaFragment) getFragmentManager().findFragmentById(R.id.agendaGridFragment);
            if (agendaFragment != null) {
                agendaFragment.getAgendaList().deleteEvent(this.mEventData.id);
                return;
            }
            this.mDeleteDialogVisible = true;
            this.mDeleteEventHelper.delete(this.mStartMillis, this.mEndMillis, this.mEventData.id, -1, this.onDeleteRunnable, this.mIsLunarEvent);
            this.mDeleteEventHelper.setOnDismissListener(createDeleteOnDismissListener());
            return;
        }
        this.mDeleteEventHelper.delete(this.mStartMillis, this.mEndMillis, this.mEventData.id, -1, this.mIsLunarEvent);
    }

    private void updateView() {
        boolean z;
        if (this.mEventData == null) {
            this.mRootView.setVisibility(8);
            return;
        }
        this.mRootView.setVisibility(0);
        String string = this.mEventData.title;
        Resources resources = getResources();
        if (string == null || string.length() == 0) {
            string = resources.getString(R.string.no_title_label);
        }
        boolean z2 = this.mEventData.allDay != 0;
        this.mLocationAddress = this.mEventData.eventLocation;
        String strTrim = this.mEventData.description;
        String str = this.mEventData.rrule;
        String str2 = this.mEventData.eventTimezone;
        this.mEventTitleText.setText(string);
        String timeZone = Utils.getTimeZone(this.mActivity, this.mUpdateTZ);
        if (z2) {
            setTextCommon(R.id.event_info_description_time, resources.getString(R.string.event_info_event_all_day_txt));
            if (this.mStartMillis == 0) {
                Time time = new SafeTime(timeZone);
                time.set(this.mEventData.startDayMillis);
                this.mStartMillis = time.toMillis(true);
                this.mEndMillis = Utils.getEndOfDayTime(time).toMillis(true);
            }
            long j = this.mStartMillis;
            setDate(R.id.event_info_description_date, j, j);
        } else {
            Time time2 = new SafeTime(timeZone);
            time2.set(this.mStartMillis);
            Time time3 = new SafeTime(timeZone);
            time3.set(this.mEndMillis);
            if (!Utils.areDatesEqual(time2, time3)) {
                this.mRootView.findViewById(R.id.event_info_description_date).setVisibility(8);
                z = true;
            } else {
                setDate(R.id.event_info_description_date, this.mStartMillis, this.mEndMillis);
                z = false;
            }
            setTime(R.id.event_info_description_time, this.mStartMillis, this.mEndMillis, z);
        }
        if (z2) {
            timeZone = "UTC";
        }
        if (str2 != null) {
            TimeZone timeZone2 = TimeZone.getTimeZone(timeZone);
            if (timeZone2 != null && !timeZone2.getID().equals("GMT")) {
                timeZone = timeZone2.getDisplayName() + "  (" + Utils.getTimezoneDisplayName(timeZone2) + ")";
            }
            setTextCommon(R.id.calendar_timezone, timeZone);
        } else {
            setVisibilityCommon(R.id.timezone_group, 8);
        }
        String string2 = resources.getString(R.string.does_not_repeat);
        if (str != null) {
            EventRecurrence eventRecurrence = new EventRecurrence();
            eventRecurrence.parse(str);
            Time time4 = new SafeTime(Utils.getTimeZone(this.mActivity, this.mUpdateTZ));
            if (z2) {
                time4.timezone = "UTC";
            }
            time4.set(this.mStartMillis);
            if (z2) {
                time4.second = 0;
                time4.minute = 0;
                time4.hour = 0;
            }
            eventRecurrence.setStartDate(time4);
            string2 = EventRecurrenceFormatter.getRepeatString(this.mActivity, eventRecurrence);
        }
        if (string2 != null) {
            setTextCommon(R.id.repeats, string2);
        }
        if (strTrim != null) {
            strTrim = strTrim.trim();
        }
        LinearLayout linearLayout = (LinearLayout) this.mRootView.findViewById(R.id.description_container);
        if (TextUtils.isEmpty(strTrim)) {
            linearLayout.setVisibility(8);
        } else {
            linearLayout.setVisibility(0);
            this.mDescriptionTextView.setText(strTrim);
            Linkify.addLinks(this.mDescriptionTextView, 15);
        }
        this.mEventLocation.setLocation(this.mLocationAddress, getChildFragmentManager());
        if (this.mIsLunarEvent) {
            setVisibilityCommon(R.id.lunar_group, 0);
            setLunarDate(R.id.lunar_date, this.mStartMillis, this.mEndMillis, z2);
        }
    }

    void updateResponse() {
        boolean z;
        if (!this.mCanModifyCalendar || ((haveAttendeeData() && this.mIsOrganizer && this.mNumOfAttendees <= 1) || ((this.mIsOrganizer && !this.mOrganizerCanRespond) || this.mIsLocalAccount || this.mIsEventCanceled))) {
            this.mEventResponseContainer.setVisibility(8);
            z = false;
        } else {
            z = true;
        }
        if (this.mNumOfAttendees <= 1) {
            this.mEventAttendeesContainer.showView(false);
        }
        if (z) {
            this.mEventResponseContainer.setVisibility(0);
            this.mEventInfoContainer.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.event_info_bottom_padding));
            updateCurrentResponseVisibility();
        }
    }

    private void setTextCommon(int i, CharSequence charSequence) {
        TextView textView = (TextView) this.mRootView.findViewById(i);
        if (textView == null || charSequence == null) {
            return;
        }
        textView.setText(charSequence.toString().trim());
    }

    private void setVisibilityCommon(int i, int i2) {
        View viewFindViewById = this.mRootView.findViewById(i);
        if (viewFindViewById != null) {
            viewFindViewById.setVisibility(i2);
        }
    }

    private void setDate(int i, long j, long j2) {
        String timeZone = Utils.getTimeZone(this.mActivity, this.mUpdateTZ);
        TextView textView = (TextView) this.mRootView.findViewById(i);
        mStringBuilder.setLength(0);
        textView.setText(DateUtils.formatDateRange(this.mActivity, mFormatter, j, j2, 22, timeZone).toString());
    }

    private void setTime(int i, long j, long j2, boolean z) {
        String timeZone = Utils.getTimeZone(this.mActivity, this.mUpdateTZ);
        TextView textView = (TextView) this.mRootView.findViewById(i);
        int i2 = DateFormat.is24HourFormat(this.mActivity) ? 129 : 1;
        if (z) {
            i2 |= 524288;
        }
        mStringBuilder.setLength(0);
        textView.setText(DateUtils.formatDateRange(this.mActivity, mFormatter, j, j2, i2, timeZone).toString());
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        EventLocationWithMap eventLocationWithMap = this.mEventLocation;
        if (eventLocationWithMap != null) {
            eventLocationWithMap.stopGeoLocationTask();
        }
        bundle.putBoolean(BUNDLE_KEY_DELETE_DIALOG_VISIBLE, this.mDeleteDialogVisible);
        bundle.putFloat(EVENT_DESCRIPTION_TEXT_SIZE, this.mDescriptionTextView.getTextSize());
        bundle.putParcelable(BUNDLE_KEY_EVENT_URI, this.mUri);
        bundle.putInt(BUNDLE_KEY_EXCHANGE_API_VERSION, this.mSomcExchangeApiVersion);
    }

    private void onRestoreInstanceState(Bundle bundle) {
        this.mDeleteDialogVisible = bundle.getBoolean(BUNDLE_KEY_DELETE_DIALOG_VISIBLE, false);
        this.mDescriptionTextView.setTextSize(0, bundle.getFloat(EVENT_DESCRIPTION_TEXT_SIZE));
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        if (this.mChanged) {
            Toast.makeText(this.mActivity, R.string.saving_event, 0).show();
        }
        DeleteEventHelper deleteEventHelper = this.mDeleteEventHelper;
        if (deleteEventHelper != null) {
            deleteEventHelper.dismissAlertDialog();
        }
        Drawable drawable = this.mEventColorDrawable;
        if (drawable != null && !this.mOpenEditEventActivity) {
            drawable.setColorFilter(0, PorterDuff.Mode.MULTIPLY);
        }
        EventLocationWithMap eventLocationWithMap = this.mEventLocation;
        if (eventLocationWithMap != null) {
            eventLocationWithMap.stopGeoLocationTask();
        }
        super.onDestroy();
    }

    private void addFieldToAccessibilityEvent(List<CharSequence> list, int i) {
        TextView textView = (TextView) this.mRootView.findViewById(i);
        if (textView != null) {
            CharSequence text = textView.getText();
            if (TextUtils.isEmpty(text)) {
                return;
            }
            String strTrim = text.toString().trim();
            if (strTrim.length() > 0) {
                list.add(strTrim);
                list.add(PERIOD_SPACE);
            }
        }
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        List<CharSequence> text = accessibilityEvent.getText();
        addFieldToAccessibilityEvent(text, R.id.event_info_description_title);
        addFieldToAccessibilityEvent(text, R.id.event_info_description_time);
        addFieldToAccessibilityEvent(text, R.id.event_info_description_date);
        return true;
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 67) {
            return false;
        }
        doDelete();
        return true;
    }

    public void setSomcExchangeApiVersion(int i) {
        this.mSomcExchangeApiVersion = i;
    }

    private void showEventColorPicker() {
        new CalendarColorQuery(this.mActivity, this.onColorPickedListener, this.mEventColor, true).startLoader(getAccountName(), getAccountType());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateEventColorIndicator(int i) {
        this.mEventColor = i;
        modifyIndicator();
    }

    private void modifyIndicator() {
        ((ImageView) this.mRootView.findViewById(R.id.event_info_calendar_indicator)).getDrawable().setColorFilter(UiUtils.getDisplayColorFromColor(this.mEventColor), PorterDuff.Mode.MULTIPLY);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getAccountName() {
        return this.mCalendarAccountName;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getAccountType() {
        return this.mCalendarAccountType;
    }

    private void setLunarDate(int i, long j, long j2, boolean z) {
        TextView textView = (TextView) this.mRootView.findViewById(i);
        String timeZone = Utils.getTimeZone(this.mActivity, this.mUpdateTZ);
        TimeZone timeZone2 = TimeZone.getTimeZone(timeZone);
        Calendar calendar = Calendar.getInstance(timeZone2);
        calendar.setTimeInMillis(j);
        Calendar calendar2 = Calendar.getInstance(timeZone2);
        if (z) {
            j2 -= 86400000;
        }
        calendar2.setTimeInMillis(j2);
        Time time = new SafeTime(timeZone);
        time.set(j);
        Time time2 = new SafeTime(timeZone);
        time2.set(j2);
        int julianDay = Time.getJulianDay(j, time.gmtoff);
        int julianDay2 = Time.getJulianDay(j2, time2.gmtoff);
        if (j != j2 && (time2.hour | time2.minute | time2.second) == 0 && julianDay2 - julianDay <= 1) {
            calendar2.add(5, -1);
        }
        LunarUtils.LunarDate lunarDateConvertSolarDateToLunarDate = LunarUtils.convertSolarDateToLunarDate(calendar.getTime(), timeZone);
        String str = LunarUtils.sLunarYearStrings[lunarDateConvertSolarDateToLunarDate.mYear - 1901];
        String lunarMonthString = LunarUtils.getLunarMonthString(lunarDateConvertSolarDateToLunarDate);
        String lunarDayString = LunarUtils.getLunarDayString(lunarDateConvertSolarDateToLunarDate);
        LunarUtils.LunarDate lunarDateConvertSolarDateToLunarDate2 = LunarUtils.convertSolarDateToLunarDate(calendar2.getTime(), timeZone);
        String str2 = LunarUtils.sLunarYearStrings[lunarDateConvertSolarDateToLunarDate2.mYear - 1901];
        String lunarMonthString2 = LunarUtils.getLunarMonthString(lunarDateConvertSolarDateToLunarDate2);
        String lunarDayString2 = LunarUtils.getLunarDayString(lunarDateConvertSolarDateToLunarDate2);
        StringBuilder sb = new StringBuilder();
        if (!str.equalsIgnoreCase(str2)) {
            sb.append(str).append(lunarMonthString).append(lunarDayString).append(" - ").append(str2).append(lunarMonthString2).append(lunarDayString2);
        } else {
            sb.append(str);
            if (!lunarMonthString.equalsIgnoreCase(lunarMonthString2)) {
                sb.append(lunarMonthString).append(lunarDayString).append(" - ").append(lunarMonthString2).append(lunarDayString2);
            } else {
                sb.append(lunarMonthString);
                if (!lunarDayString.equalsIgnoreCase(lunarDayString2)) {
                    sb.append(lunarDayString).append(" - ").append(lunarDayString2);
                } else {
                    sb.append(lunarDayString);
                }
            }
        }
        textView.setText(sb.toString());
    }

    private DialogInterface.OnDismissListener createDeleteOnDismissListener() {
        return new DialogInterface.OnDismissListener() { // from class: com.sonymobile.calendar.EventInfoFragment.13
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                if (EventInfoFragment.this.mIsStopped) {
                    return;
                }
                EventInfoFragment.this.mDeleteDialogVisible = false;
            }
        };
    }
}
