package com.sonymobile.calendar.editevent;
import com.sonymobile.calendar.SafeTime;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.calendar.widget.CalendarAppWidgetProvider;
import com.sonymobile.lunar.lib.LunarContract;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class CreateEventView extends EditEventView {
    private static final String EAS_ACCOUNT_TYPE = "com.android.exchange";

    @Override // com.sonymobile.calendar.editevent.EditEventView
    protected boolean isBasicEventInfoModified() {
        return false;
    }

    @Override // com.sonymobile.calendar.editevent.EditEventView
    protected void setAvailButtonVisibility() {
    }

    @Override // com.sonymobile.calendar.editevent.EditEventView
    protected void showChangeAllSeriesOrSingleEventDialog() {
    }

    public CreateEventView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.sonymobile.calendar.editevent.EditEventView
    protected boolean initEventTimes(Intent intent) {
        long longExtra;
        boolean booleanExtra = intent.getBooleanExtra("allDay", false);
        this.mTimezone = booleanExtra ? "UTC" : Utils.getTimeZone(getContext(), null);
        if (intent.getBooleanExtra(CalendarAppWidgetProvider.KEY_CURRENT_TIME_INTRINSIC, false)) {
            longExtra = new SafeTime(this.mTimezone).toMillis(true);
        } else {
            longExtra = intent.getLongExtra(LunarContract.EXTRA_EVENT_BEGIN_TIME, 0L);
        }
        long longExtra2 = intent.getLongExtra(LunarContract.EXTRA_EVENT_END_TIME, 3600000 + longExtra);
        this.mStartTime = new SafeTime(this.mTimezone);
        this.mEndTime = new SafeTime(this.mTimezone);
        this.mStartTime.set(longExtra);
        this.mEndTime.set(longExtra2);
        this.mStartTime.allDay = booleanExtra;
        this.mStartTime.second = 0;
        this.mEndTime.second = 0;
        if (booleanExtra) {
            this.mStartTime.second = 0;
            this.mStartTime.minute = 0;
            this.mStartTime.hour = 0;
        }
        this.mStartTime.normalize(true);
        this.mEndTime.normalize(true);
        return true;
    }

    @Override // com.sonymobile.calendar.editevent.EditEventView
    protected String getSaveEventText() {
        return getResources().getString(R.string.creating_event);
    }

    @Override // com.sonymobile.calendar.editevent.EditEventView
    protected void initReminderControls() {
        super.initReminderControls();
        addReminder();
    }

    @Override // com.sonymobile.calendar.editevent.EditEventView
    protected void saveRruleAndReminders(ArrayList<ContentProviderOperation> arrayList, ContentValues contentValues, Uri uri, ArrayList<Integer> arrayList2, boolean z) {
        contentValues.put(LunarContract.EventsColumns.HAS_ATTENDEE_DATA, (Integer) 1);
        addRecurrenceRule(contentValues);
        if (this.mRrule != null) {
            contentValues.remove(LunarContract.EventsColumns.DTEND);
        }
        if (!this.mIsLunarEvent) {
            addUid(contentValues);
        }
        this.mNewEventIdIndex = arrayList.size();
        arrayList.add(ContentProviderOperation.newInsert(asSyncAdapter(this.mEventsUri, this.mOrganizerEmail, EAS_ACCOUNT_TYPE)).withValues(contentValues).build());
        if (this.mNewEventIdIndex >= 0) {
            saveRemindersWithBackRef(arrayList, this.mNewEventIdIndex, arrayList2, this.mOriginalMinutes, true, this.mIsLunarEvent, z);
        }
    }

    @Override // com.sonymobile.calendar.editevent.EditEventView
    protected void initCalendarSpinner() {
        this.mCalendarsSpinner = (Spinner) findViewById(R.id.calendars);
        this.mCalendarsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.sonymobile.calendar.editevent.CreateEventView.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (i < 0 || i >= CreateEventView.this.mCalendarsSpinner.getCount()) {
                    return;
                }
                int selectedItemPosition = CreateEventView.this.mCalendarsSpinner.getSelectedItemPosition();
                if (CreateEventView.this.mCalendarsCursor != null && CreateEventView.this.mCalendarsCursor.moveToPosition(selectedItemPosition)) {
                    if (Utils.isExchangeAccountType(CreateEventView.this.mCalendarsCursor.getString(4))) {
                        CreateEventView.this.mCheckAvailability.setVisibility(0);
                    } else {
                        CreateEventView.this.mCheckAvailability.setVisibility(8);
                    }
                }
                CreateEventView.this.mCalendarPosition = selectedItemPosition;
                CreateEventView.this.updateEditEventDayView();
                if (CreateEventView.this.eventColorButton != null && CreateEventView.this.eventColor == 0) {
                    CreateEventView.this.getCalendarColorForButton();
                }
                if (LunarAvailabilityManager.isLunarAvailable(CreateEventView.this.activity) && CreateEventView.this.mCalendarPosition == CreateEventView.this.mLocalCalendarPosition) {
                    CreateEventView.this.mIsLocalCalendarSelected = true;
                    CreateEventView.this.initButtonListeners(1);
                    return;
                }
                CreateEventView createEventView = CreateEventView.this;
                createEventView.mIsLocalCalendarSelected = createEventView.mCalendarPosition == CreateEventView.this.mLocalCalendarPosition;
                CreateEventView.this.mIsLunarEvent = false;
                CreateEventView.this.updateUris();
                CreateEventView createEventView2 = CreateEventView.this;
                createEventView2.setDate(createEventView2.mStartDateButton, CreateEventView.this.mStartTime.toMillis(true));
                CreateEventView createEventView3 = CreateEventView.this;
                createEventView3.setDate(createEventView3.mEndDateButton, CreateEventView.this.mEndTime.toMillis(true));
                CreateEventView.this.updateWarningText();
                CreateEventView.this.updateHomeTime();
                CreateEventView.this.initButtonListeners(2);
            }
        });
    }

    @Override // com.sonymobile.calendar.editevent.EditEventView
    protected void populateCalendars(int i) {
        super.populateCalendars(i);
        if (this.mCalendarsSpinner.getAdapter().getCount() < 2) {
            this.mCalendarsSpinner.setEnabled(false);
        }
    }
}
