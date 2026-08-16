package com.sonymobile.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.Time;
import android.widget.ImageView;
import com.sonymobile.calendar.birthday.ContactBirthday;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class AllDayBirthdayView extends AllDayEventViewBase {
    private final ContactBirthday[] birthdays;
    private boolean isR2L;

    public AllDayBirthdayView(Context context, ContactBirthday[] contactBirthdayArr, Time time, boolean z, boolean z2, ViewIndex viewIndex) {
        super(context, time, z, viewIndex);
        this.birthdays = (ContactBirthday[]) contactBirthdayArr.clone();
        this.isR2L = z2;
        init(-1);
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase
    protected void initContentDescription() {
        StringBuilder sb = new StringBuilder();
        Resources resources = getContext().getResources();
        ContactBirthday[] contactBirthdayArr = this.birthdays;
        if (contactBirthdayArr.length == 1) {
            sb.append(contactBirthdayArr.length).append(resources.getString(R.string.accessibility_birthday_label));
        } else {
            sb.append(String.format(Locale.getDefault(), resources.getString(R.string.multiple_birthdays_label), Integer.valueOf(this.birthdays.length)));
        }
        setContentDescription(sb.toString());
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase
    protected String getTitleText() {
        if (this.showFullEventText && this.birthdays.length == 1) {
            StringBuilder sb = new StringBuilder();
            int age = this.birthdays[0].getAge(this.day);
            if (age >= 0) {
                if (this.isR2L) {
                    sb.append("(");
                    sb.append(age);
                    sb.append(") ");
                    sb.append(this.birthdays[0].name);
                } else {
                    sb.append(this.birthdays[0].name);
                    sb.append(" (");
                    sb.append(age);
                    sb.append(")");
                }
                initIcon();
            }
            return sb.toString();
        }
        return "" + this.birthdays.length;
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase
    protected Object getNavigationInfo() {
        return Long.valueOf(this.day.normalize(false));
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase
    protected void initIcon() {
        ((ImageView) findViewById(R.id.event_icon)).setBackgroundResource(R.drawable.ic_birthday);
    }
}
