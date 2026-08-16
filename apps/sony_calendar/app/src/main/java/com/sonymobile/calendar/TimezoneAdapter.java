package com.sonymobile.calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.BidiFormatter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class TimezoneAdapter extends ArrayAdapter<TimezoneAdapter.TimezoneRow> {
    private static final String KEY_RECENT_TIMEZONES = "preferences_recent_timezones";
    private static final int MAX_RECENT_TIMEZONES = 3;
    private static final String RECENT_TIMEZONES_DELIMITER = ",";
    private static final String TAG = "TimezoneAdapter";
    private static LinkedHashMap<String, TimezoneRow> sTimezones;
    private Context mContext;
    private String mCurrentTimezone;
    private Date mDateTime;
    private boolean mShowingAll;
    private long mTime;

    public class TimezoneRow implements Comparable<TimezoneRow> {
        private final String mDisplayName;
        private String mGmtDisplayName;
        public final String mId;
        private final int mOffset;
        private final boolean mUseDaylightTime;

        public TimezoneRow(String str, String str2) {
            this.mId = str;
            this.mDisplayName = str2;
            TimeZone timeZone = TimeZone.getTimeZone(str);
            this.mUseDaylightTime = timeZone.useDaylightTime();
            this.mOffset = timeZone.getOffset(TimezoneAdapter.this.mTime);
        }

        public String toString() {
            if (this.mGmtDisplayName == null) {
                buildGmtDisplayName();
            }
            return this.mGmtDisplayName;
        }

        public void buildGmtDisplayName() {
            if (this.mGmtDisplayName != null) {
                return;
            }
            String str = this.mUseDaylightTime ? " ☀" : "";
            BidiFormatter bidiFormatter = BidiFormatter.getInstance();
            String strUnicodeWrap = "(" + Utils.getTimeZoneDisplayName(this.mId) + ")";
            if (bidiFormatter.isRtl(this.mDisplayName)) {
                strUnicodeWrap = bidiFormatter.unicodeWrap(strUnicodeWrap);
            }
            this.mGmtDisplayName = String.format("%1$s %2$s %3$s", strUnicodeWrap, this.mDisplayName, str);
        }

        public int hashCode() {
            String str = this.mDisplayName;
            int iHashCode = ((str == null ? 0 : str.hashCode()) + 31) * 31;
            String str2 = this.mId;
            return ((iHashCode + (str2 != null ? str2.hashCode() : 0)) * 31) + this.mOffset;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            TimezoneRow timezoneRow = (TimezoneRow) obj;
            String str = this.mDisplayName;
            if (str == null) {
                if (timezoneRow.mDisplayName != null) {
                    return false;
                }
            } else if (!str.equals(timezoneRow.mDisplayName)) {
                return false;
            }
            String str2 = this.mId;
            if (str2 == null) {
                if (timezoneRow.mId != null) {
                    return false;
                }
            } else if (!str2.equals(timezoneRow.mId)) {
                return false;
            }
            return this.mOffset == timezoneRow.mOffset;
        }

        @Override // java.lang.Comparable
        public int compareTo(TimezoneRow timezoneRow) {
            int i = this.mOffset;
            int i2 = timezoneRow.mOffset;
            if (i == i2) {
                return 0;
            }
            return i < i2 ? -1 : 1;
        }
    }

    public TimezoneAdapter(Context context, String str, long j) {
        super(context, R.layout.simple_spinner_item, android.R.id.text1);
        this.mShowingAll = false;
        this.mContext = context;
        this.mCurrentTimezone = str;
        this.mTime = j;
        this.mDateTime = new Date(this.mTime);
        this.mShowingAll = false;
        showInitialTimezones();
        showAllTimezones();
    }

    public int getRowById(String str) {
        TimezoneRow timezoneRow = sTimezones.get(str);
        if (timezoneRow == null) {
            return -1;
        }
        return getPosition(timezoneRow);
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2 = super.getView(i, view, viewGroup);
        view2.setTextDirection(2);
        view2.setTextAlignment(5);
        return view2;
    }

    public void showInitialTimezones() {
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet();
        if (!TextUtils.isEmpty(this.mCurrentTimezone)) {
            linkedHashSet.add(this.mCurrentTimezone);
        }
        linkedHashSet.add(TimeZone.getDefault().getID());
        String string = GeneralPreferences.getSharedPreferences(this.mContext).getString(KEY_RECENT_TIMEZONES, null);
        if (string != null) {
            for (String str : string.split(",")) {
                if (!TextUtils.isEmpty(str)) {
                    linkedHashSet.add(str);
                }
            }
        }
        clear();
        synchronized (TimezoneAdapter.class) {
            loadFromResources(this.mContext.getResources());
            TimeZone timeZone = TimeZone.getTimeZone("GMT");
            boolean z = false;
            for (String str2 : linkedHashSet) {
                if (!sTimezones.containsKey(str2)) {
                    TimeZone timeZone2 = TimeZone.getTimeZone(str2);
                    if (!z || !timeZone2.equals(timeZone)) {
                        if (timeZone2.equals(timeZone)) {
                            z = true;
                        }
                        sTimezones.put(str2, new TimezoneRow(str2, timeZone2.getDisplayName(timeZone2.inDaylightTime(this.mDateTime), 1, Locale.getDefault())));
                    }
                }
                add(sTimezones.get(str2));
            }
        }
        this.mShowingAll = false;
    }

    public void showAllTimezones() {
        ArrayList<TimezoneRow> arrayList = new ArrayList(sTimezones.values());
        Collections.sort(arrayList);
        clear();
        for (TimezoneRow timezoneRow : arrayList) {
            timezoneRow.buildGmtDisplayName();
            add(timezoneRow);
        }
        this.mShowingAll = true;
    }

    public void setCurrentTimezone(String str) {
        if (str == null || str.equals(this.mCurrentTimezone)) {
            return;
        }
        this.mCurrentTimezone = str;
        if (this.mShowingAll) {
            return;
        }
        showInitialTimezones();
    }

    public void saveRecentTimezone(String str) {
        ArrayList arrayList;
        SharedPreferences sharedPreferences = GeneralPreferences.getSharedPreferences(this.mContext);
        String string = sharedPreferences.getString(KEY_RECENT_TIMEZONES, null);
        if (string == null) {
            arrayList = new ArrayList(3);
        } else {
            arrayList = new ArrayList(Arrays.asList(string.split(",")));
        }
        while (arrayList.size() >= 3) {
            arrayList.remove(0);
        }
        arrayList.add(str);
        sharedPreferences.edit().putString(KEY_RECENT_TIMEZONES, Utils.join(arrayList, ",")).apply();
    }

    public CharSequence[][] getAllTimezones() {
        CharSequence[][] charSequenceArr = (CharSequence[][]) Array.newInstance((Class<?>) CharSequence.class, 2, sTimezones.size());
        ArrayList<String> arrayList = new ArrayList<>(sTimezones.keySet());
        int i = 0;
        for (TimezoneAdapter.TimezoneRow timezoneRow : new ArrayList<>(sTimezones.values())) {
            charSequenceArr[0][i] = (CharSequence) arrayList.get(i);
            charSequenceArr[1][i] = timezoneRow.toString();
            i++;
        }
        return charSequenceArr;
    }

    private void loadFromResources(Resources resources) {
        LinkedHashMap<String, TimezoneRow> linkedHashMap = sTimezones;
        if (linkedHashMap != null) {
            linkedHashMap.clear();
        }
        String[] stringArray = resources.getStringArray(R.array.timezone_values);
        String[] stringArray2 = resources.getStringArray(R.array.timezone_labels);
        int length = stringArray.length;
        sTimezones = new LinkedHashMap<>(length);
        if (stringArray.length != stringArray2.length) {
            Log.wtf(TAG, "ids length (" + stringArray.length + ") and labels length(" + stringArray2.length + ") should be equal but aren't.");
        }
        for (int i = 0; i < length; i++) {
            sTimezones.put(stringArray[i], new TimezoneRow(stringArray[i], stringArray2[i]));
        }
    }
}
