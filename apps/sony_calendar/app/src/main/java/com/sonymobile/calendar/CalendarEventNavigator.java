package com.sonymobile.calendar;

import android.os.Bundle;
import com.sonyericsson.calendar.util.EventInfo;

/* JADX INFO: loaded from: classes2.dex */
public interface CalendarEventNavigator {
    void deleteEvent(EventInfo eventInfo);

    void goToBirthdays(long j);

    void goToCreateEvent(long j, boolean z);

    void goToEditEvent(EventInfo eventInfo);

    void goToEventDetails(EventInfo eventInfo, Bundle bundle);

    void showEventPreview(EventInfo[] eventInfoArr, float f, float f2);
}
