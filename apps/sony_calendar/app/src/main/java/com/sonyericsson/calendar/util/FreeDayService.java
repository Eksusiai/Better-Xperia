package com.sonyericsson.calendar.util;
import com.sonymobile.calendar.SafeTime;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Xml;
import androidx.core.content.ContextCompat;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TimeZone;
import java.util.TreeMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class FreeDayService {
    private static final String ATTR_ENDDATE = "endDate";
    private static final String ATTR_STARTDATE = "startDate";
    private static final String FOLDER_BASE_PATH = "national_holidays/calendar-";
    public static final int REQUEST_HOLIDAY_NAME = 1;
    public static final int REQUEST_IS_FREE_DAY = 2;
    public static final int REQUEST_LOAD = 0;
    private static final String TAG_HOLIDAY = "holiday";
    private static FreeDayService instance;
    private Map<Integer, ArrayList<Holiday>> holidays;
    private Map<Integer, ArrayList<Holiday>> holidaysCopy;
    private String[] nationalities;
    private String[] weekendDays;
    private Queue<OnReadyHandler> handlers = new LinkedList();
    private boolean isDataBeingLoaded = false;
    private boolean isLunarAvailable = false;

    protected interface OnReadyHandler {
        void onReady(boolean z);
    }

    private FreeDayService() {
    }

    public static FreeDayService getInstance() {
        if (instance == null) {
            instance = new FreeDayService();
        }
        return instance;
    }

    public int getHolidayColor(Context context) {
        if (context == null) {
            return 0;
        }
        return ContextCompat.getColor(context, R.color.free_day_color);
    }

    public void requestLoad(Context context, final IAsyncServiceResultHandler iAsyncServiceResultHandler, final int i, boolean z) {
        if (isDataLoadedCopy() && !z && iAsyncServiceResultHandler != null) {
            iAsyncServiceResultHandler.onResult(null, Integer.valueOf(i));
        }
        loadDataAsync(context, new OnReadyHandler() { // from class: com.sonyericsson.calendar.util.FreeDayService.1
            @Override // com.sonyericsson.calendar.util.FreeDayService.OnReadyHandler
            public void onReady(boolean z2) {
                FreeDayService.this.holidaysCopy = new TreeMap(FreeDayService.this.holidays);
                IAsyncServiceResultHandler iAsyncServiceResultHandler2 = iAsyncServiceResultHandler;
                if (iAsyncServiceResultHandler2 != null) {
                    iAsyncServiceResultHandler2.onResult(Boolean.valueOf(z2), Integer.valueOf(i));
                }
            }
        });
    }

    public void requestHolidayName(Context context, final int i, final IAsyncServiceResultHandler iAsyncServiceResultHandler, final int i2) {
        if (isDataLoaded() && isDataLoadedCopy() && !isLunarAvailabilityChanged(context)) {
            iAsyncServiceResultHandler.onResult(getHolidayName(i), Integer.valueOf(i2));
        } else {
            loadDataAsync(context, new OnReadyHandler() { // from class: com.sonyericsson.calendar.util.FreeDayService.2
                @Override // com.sonyericsson.calendar.util.FreeDayService.OnReadyHandler
                public void onReady(boolean z) {
                    FreeDayService.this.holidaysCopy = new TreeMap(FreeDayService.this.holidays);
                    iAsyncServiceResultHandler.onResult(FreeDayService.this.getHolidayName(i), Integer.valueOf(i2));
                }
            });
        }
    }

    public void requestHolidayName(Context context, int i, int i2, int i3, IAsyncServiceResultHandler iAsyncServiceResultHandler, int i4) {
        Time time = new SafeTime();
        time.set(i3, i2, i);
        long jNormalize = time.normalize(true);
        requestHolidayName(context, Time.getJulianDay(jNormalize, TimeZone.getDefault().getOffset(jNormalize) / 1000), iAsyncServiceResultHandler, i4);
    }

    public void requestIsFreeDay(Context context, final int i, final IAsyncServiceResultHandler iAsyncServiceResultHandler, final int i2) {
        if (isDataLoaded()) {
            iAsyncServiceResultHandler.onResult(Boolean.valueOf(isFreeDay(i)), Integer.valueOf(i2));
        } else {
            loadDataAsync(context, new OnReadyHandler() { // from class: com.sonyericsson.calendar.util.FreeDayService.3
                @Override // com.sonyericsson.calendar.util.FreeDayService.OnReadyHandler
                public void onReady(boolean z) {
                    iAsyncServiceResultHandler.onResult(Boolean.valueOf(FreeDayService.this.isFreeDay(i)), Integer.valueOf(i2));
                }
            });
        }
    }

    public void requestIsFreeDay(Context context, int i, int i2, int i3, IAsyncServiceResultHandler iAsyncServiceResultHandler, int i4) {
        Time time = new SafeTime();
        time.set(i3, i2, i);
        long jNormalize = time.normalize(true);
        requestIsFreeDay(context, Time.getJulianDay(jNormalize, TimeZone.getDefault().getOffset(jNormalize) / 1000), iAsyncServiceResultHandler, i4);
    }

    public boolean isFreeDay(int i, int i2, int i3) {
        Time time = new SafeTime(Time.getCurrentTimezone());
        time.set(i3, i2, i);
        long millis = time.toMillis(true);
        return isFreeDay(Time.getJulianDay(millis, TimeZone.getDefault().getOffset(millis) / 1000));
    }

    public boolean isFreeDay(int i) {
        if (!isDataLoadedCopy()) {
            return false;
        }
        Time time = new SafeTime();
        time.setJulianDay(i);
        return isWeekend(time.weekDay) || isNationalHoliday(i);
    }

    public boolean isWeekend(int i) {
        if (this.weekendDays == null) {
            return false;
        }
        String strValueOf = String.valueOf(i);
        for (String str : this.weekendDays) {
            if (str.equals(strValueOf)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDataLoaded() {
        return (this.holidays == null || this.weekendDays == null) ? false : true;
    }

    public boolean isDataLoadedCopy() {
        return (this.holidaysCopy == null || this.weekendDays == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getHolidayName(int i) {
        String str = "";
        StringBuilder sb = new StringBuilder("");
        Map<Integer, ArrayList<Holiday>> map = this.holidaysCopy;
        if (map != null) {
            for (Map.Entry<Integer, ArrayList<Holiday>> entry : map.entrySet()) {
                if (i < entry.getKey().intValue()) {
                    break;
                }
                for (Holiday holiday : entry.getValue()) {
                    if (holiday.contains(i)) {
                        sb.append(str).append(holiday.description);
                        str = ", ";
                    }
                }
            }
        }
        return sb.toString();
    }

    public ArrayList<Holiday> getHolidayNameArray(int i) {
        ArrayList<Holiday> arrayList = new ArrayList<>();
        Map<Integer, ArrayList<Holiday>> map = this.holidaysCopy;
        if (map != null) {
            for (Map.Entry<Integer, ArrayList<Holiday>> entry : map.entrySet()) {
                if (i < entry.getKey().intValue()) {
                    break;
                }
                for (Holiday holiday : entry.getValue()) {
                    if (holiday.contains(i)) {
                        arrayList.add(holiday);
                    }
                }
            }
        }
        return arrayList;
    }

    private boolean isNationalHoliday(int i) {
        Map<Integer, ArrayList<Holiday>> map = this.holidaysCopy;
        if (map != null) {
            for (Map.Entry<Integer, ArrayList<Holiday>> entry : map.entrySet()) {
                if (i < entry.getKey().intValue()) {
                    return false;
                }
                Iterator<Holiday> it = entry.getValue().iterator();
                while (it.hasNext()) {
                    if (it.next().contains(i)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean loadWeekendDays(Context context) {
        String[] sharedPreference = Utils.getSharedPreference(context, GeneralPreferences.KEY_WEEKEND_DAYS, context.getResources().getStringArray(R.array.preferences_weekend_default));
        if (!hasWeekendDaysChanged(sharedPreference)) {
            return false;
        }
        this.weekendDays = sharedPreference;
        return true;
    }

    private boolean hasWeekendDaysChanged(String[] strArr) {
        String[] strArr2 = this.weekendDays;
        if (strArr2 == null || strArr2.length != strArr.length) {
            return true;
        }
        int i = 0;
        while (true) {
            String[] strArr3 = this.weekendDays;
            if (i >= strArr3.length) {
                return false;
            }
            if (!strArr3[i].equals(strArr[i])) {
                return true;
            }
            i++;
        }
    }

    private void loadDataAsync(Context context, OnReadyHandler onReadyHandler) {
        if (onReadyHandler != null) {
            this.handlers.add(onReadyHandler);
        }
        if (this.isDataBeingLoaded) {
            return;
        }
        this.isDataBeingLoaded = true;
        new LoadDataAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
    }

    private class LoadDataAsyncTask extends AsyncTask<Context, Void, Boolean> {
        private LoadDataAsyncTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Context... contextArr) {
            return Boolean.valueOf(loadHolidays(contextArr[0]));
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean bool) {
            FreeDayService.this.isDataBeingLoaded = false;
            while (!FreeDayService.this.handlers.isEmpty()) {
                ((OnReadyHandler) FreeDayService.this.handlers.remove()).onReady(bool.booleanValue());
            }
        }

        private boolean loadHolidays(Context context) {
            String[] sharedPreference = Utils.getSharedPreference(context, GeneralPreferences.KEY_NATIONAL_HOLIDAYS, new String[0]);
            if (!hasNationalitiesChanged(sharedPreference) && !FreeDayService.this.isLunarAvailabilityChanged(context)) {
                return false;
            }
            FreeDayService.this.nationalities = sharedPreference;
            FreeDayService.this.isLunarAvailable = LunarAvailabilityManager.isLunarAvailable(context);
            FreeDayService.this.holidays = new TreeMap();
            for (String str : FreeDayService.this.nationalities) {
                if (!FreeDayService.this.isLunarAvailable || !str.equals("zh-rCN")) {
                    loadHolidays(context, str);
                }
            }
            return true;
        }

        private boolean hasNationalitiesChanged(String[] strArr) {
            if (FreeDayService.this.nationalities == null || FreeDayService.this.nationalities.length != strArr.length) {
                return true;
            }
            for (int i = 0; i < FreeDayService.this.nationalities.length; i++) {
                if (!FreeDayService.this.nationalities[i].equals(strArr[i])) {
                    return true;
                }
            }
            return false;
        }

        private void loadHolidays(Context context, String str) {
            try {
                InputStream inputStreamOpen = context.getAssets().open(FreeDayService.FOLDER_BASE_PATH + str + ".xml");
                InputStreamReader inputStreamReader = new InputStreamReader(inputStreamOpen);
                XmlPullParser xmlPullParserNewPullParser = Xml.newPullParser();
                xmlPullParserNewPullParser.setInput(inputStreamReader);
                String str2 = "";
                boolean z = false;
                String attributeValue = "";
                for (int eventType = xmlPullParserNewPullParser.getEventType(); eventType != 1; eventType = xmlPullParserNewPullParser.next()) {
                    if (eventType != 2) {
                        if (eventType == 3) {
                            z = false;
                        } else if (eventType == 4 && z && !xmlPullParserNewPullParser.getText().isEmpty()) {
                            int day = parseDay(str2);
                            Holiday holiday = new Holiday(xmlPullParserNewPullParser.getText(), day, parseDay(attributeValue));
                            if (FreeDayService.this.holidays.containsKey(Integer.valueOf(day))) {
                                ((ArrayList) FreeDayService.this.holidays.get(Integer.valueOf(day))).add(holiday);
                            } else {
                                ArrayList arrayList = new ArrayList();
                                arrayList.add(holiday);
                                FreeDayService.this.holidays.put(Integer.valueOf(day), arrayList);
                            }
                        }
                    } else if (xmlPullParserNewPullParser.getName().equals(FreeDayService.TAG_HOLIDAY)) {
                        String attributeValue2 = xmlPullParserNewPullParser.getAttributeValue(null, FreeDayService.ATTR_STARTDATE);
                        attributeValue = xmlPullParserNewPullParser.getAttributeValue(null, FreeDayService.ATTR_ENDDATE);
                        z = true;
                        str2 = attributeValue2;
                    }
                }
                inputStreamOpen.close();
                inputStreamReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            } catch (XmlPullParserException e3) {
                e3.printStackTrace();
            }
        }

        private int parseDay(String str) {
            String[] strArrSplit = str.split(CalendarConstants.HYPHEN);
            Time time = new SafeTime();
            time.set(Integer.parseInt(strArrSplit[2]), Integer.parseInt(strArrSplit[1]) - 1, Integer.parseInt(strArrSplit[0]));
            long millis = time.toMillis(true);
            return Time.getJulianDay(millis, TimeZone.getDefault().getOffset(millis) / 1000);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isLunarAvailabilityChanged(Context context) {
        return (context == null || this.isLunarAvailable == LunarAvailabilityManager.isLunarAvailable(context)) ? false : true;
    }
}
