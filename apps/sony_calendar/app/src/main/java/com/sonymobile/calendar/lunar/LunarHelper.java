package com.sonymobile.calendar.lunar;

import androidx.core.view.PointerIconCompat;
import com.google.common.base.Objects;
import com.sonymobile.lunar.lib.LunarUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class LunarHelper {
    private static List<LunarMonthDate> sKeys;
    private static Map<LunarMonthDate, Integer> sLunarMonthMap;

    public static class LunarMonthDate {
        private boolean isLeap;
        private int lunarMonth;
        private int lunarYear;

        LunarMonthDate(int i, int i2, boolean z) {
            this.lunarYear = i;
            this.lunarMonth = i2;
            this.isLeap = z;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof LunarMonthDate)) {
                return false;
            }
            LunarMonthDate lunarMonthDate = (LunarMonthDate) obj;
            return this.lunarYear == lunarMonthDate.lunarYear && this.lunarMonth == lunarMonthDate.lunarMonth && this.isLeap == lunarMonthDate.isLeap;
        }

        public String toString() {
            return "lunarYear is " + this.lunarYear + "lunarMonth is " + this.lunarMonth + "isLeap is " + this.isLeap;
        }

        public int compare(LunarMonthDate lunarMonthDate) {
            return (((this.lunarYear - lunarMonthDate.lunarYear) * 12) + this.lunarMonth) - lunarMonthDate.lunarMonth;
        }

        public int hashCode() {
            return Objects.hashCode(Integer.valueOf(this.lunarYear), Integer.valueOf(this.lunarMonth), Boolean.valueOf(this.isLeap));
        }

        public int getYear() {
            return this.lunarYear;
        }

        public int getMonth() {
            return this.lunarMonth;
        }

        public boolean getIsLeap() {
            return this.isLeap;
        }
    }

    static {
        HashMap map = new HashMap();
        sLunarMonthMap = map;
        map.put(new LunarMonthDate(LunarUtils.MIN_LUNAR_YEAR, 1, false), 1);
        sLunarMonthMap.put(new LunarMonthDate(1903, 3, true), 28);
        sLunarMonthMap.put(new LunarMonthDate(1906, 4, true), 66);
        sLunarMonthMap.put(new LunarMonthDate(1909, 2, true), 99);
        sLunarMonthMap.put(new LunarMonthDate(1911, 6, true), 128);
        sLunarMonthMap.put(new LunarMonthDate(1914, 5, true), 164);
        sLunarMonthMap.put(new LunarMonthDate(1917, 2, true), 198);
        sLunarMonthMap.put(new LunarMonthDate(1919, 7, true), 228);
        sLunarMonthMap.put(new LunarMonthDate(1922, 5, true), 263);
        sLunarMonthMap.put(new LunarMonthDate(1925, 4, true), 299);
        sLunarMonthMap.put(new LunarMonthDate(1928, 2, true), 334);
        sLunarMonthMap.put(new LunarMonthDate(1930, 6, true), 363);
        sLunarMonthMap.put(new LunarMonthDate(1933, 5, true), 399);
        sLunarMonthMap.put(new LunarMonthDate(1936, 3, true), 434);
        sLunarMonthMap.put(new LunarMonthDate(1938, 7, true), 463);
        sLunarMonthMap.put(new LunarMonthDate(1941, 6, true), 499);
        sLunarMonthMap.put(new LunarMonthDate(1944, 4, true), 534);
        sLunarMonthMap.put(new LunarMonthDate(1947, 2, true), 569);
        sLunarMonthMap.put(new LunarMonthDate(1949, 7, true), 599);
        sLunarMonthMap.put(new LunarMonthDate(1952, 5, true), 634);
        sLunarMonthMap.put(new LunarMonthDate(1955, 3, true), 669);
        sLunarMonthMap.put(new LunarMonthDate(1957, 8, true), 699);
        sLunarMonthMap.put(new LunarMonthDate(1960, 6, true), 734);
        sLunarMonthMap.put(new LunarMonthDate(1963, 4, true), 769);
        sLunarMonthMap.put(new LunarMonthDate(1966, 3, true), 805);
        sLunarMonthMap.put(new LunarMonthDate(1968, 7, true), 834);
        sLunarMonthMap.put(new LunarMonthDate(1971, 5, true), 869);
        sLunarMonthMap.put(new LunarMonthDate(1974, 4, true), 905);
        sLunarMonthMap.put(new LunarMonthDate(1976, 8, true), 934);
        sLunarMonthMap.put(new LunarMonthDate(1979, 6, true), 969);
        sLunarMonthMap.put(new LunarMonthDate(1982, 4, true), Integer.valueOf(PointerIconCompat.TYPE_WAIT));
        sLunarMonthMap.put(new LunarMonthDate(1984, 10, true), 1035);
        sLunarMonthMap.put(new LunarMonthDate(1987, 6, true), 1068);
        sLunarMonthMap.put(new LunarMonthDate(1990, 5, true), 1104);
        sLunarMonthMap.put(new LunarMonthDate(1993, 3, true), 1139);
        sLunarMonthMap.put(new LunarMonthDate(1995, 8, true), 1169);
        sLunarMonthMap.put(new LunarMonthDate(1998, 5, true), 1203);
        sLunarMonthMap.put(new LunarMonthDate(2001, 4, true), 1239);
        sLunarMonthMap.put(new LunarMonthDate(2004, 2, true), 1274);
        sLunarMonthMap.put(new LunarMonthDate(2006, 7, true), 1304);
        sLunarMonthMap.put(new LunarMonthDate(2009, 5, true), 1339);
        sLunarMonthMap.put(new LunarMonthDate(2012, 4, true), 1375);
        sLunarMonthMap.put(new LunarMonthDate(2014, 9, true), 1405);
        sLunarMonthMap.put(new LunarMonthDate(2017, 6, true), 1439);
        sLunarMonthMap.put(new LunarMonthDate(2020, 4, true), 1474);
        sLunarMonthMap.put(new LunarMonthDate(2023, 2, true), 1509);
        sLunarMonthMap.put(new LunarMonthDate(2025, 6, true), 1538);
        sLunarMonthMap.put(new LunarMonthDate(2028, 5, true), 1574);
        sLunarMonthMap.put(new LunarMonthDate(2031, 3, true), 1609);
        sLunarMonthMap.put(new LunarMonthDate(2033, 11, true), 1642);
        sLunarMonthMap.put(new LunarMonthDate(2036, 6, true), 1674);
        sLunarMonthMap.put(new LunarMonthDate(2039, 5, true), 1710);
        sLunarMonthMap.put(new LunarMonthDate(2042, 2, true), 1744);
        sLunarMonthMap.put(new LunarMonthDate(2044, 7, true), 1774);
        sLunarMonthMap.put(new LunarMonthDate(2047, 5, true), 1809);
        sLunarMonthMap.put(new LunarMonthDate(2050, 3, true), 1844);
        sLunarMonthMap.put(new LunarMonthDate(2052, 8, true), 1874);
        sLunarMonthMap.put(new LunarMonthDate(2055, 6, true), 1909);
        sLunarMonthMap.put(new LunarMonthDate(2058, 4, true), 1944);
        sLunarMonthMap.put(new LunarMonthDate(2061, 3, true), 1980);
        sLunarMonthMap.put(new LunarMonthDate(2063, 7, true), 2009);
        sLunarMonthMap.put(new LunarMonthDate(2066, 5, true), 2044);
        sLunarMonthMap.put(new LunarMonthDate(2069, 4, true), 2080);
        sLunarMonthMap.put(new LunarMonthDate(2071, 8, true), 2109);
        sLunarMonthMap.put(new LunarMonthDate(2074, 6, true), 2144);
        sLunarMonthMap.put(new LunarMonthDate(2077, 4, true), 2179);
        sLunarMonthMap.put(new LunarMonthDate(2080, 3, true), 2215);
        sLunarMonthMap.put(new LunarMonthDate(2082, 7, true), 2244);
        sLunarMonthMap.put(new LunarMonthDate(2085, 5, true), 2279);
        sLunarMonthMap.put(new LunarMonthDate(2088, 4, true), 2315);
        sLunarMonthMap.put(new LunarMonthDate(2090, 8, true), 2344);
        sLunarMonthMap.put(new LunarMonthDate(2093, 6, true), 2379);
        sLunarMonthMap.put(new LunarMonthDate(2096, 4, true), 2414);
        sLunarMonthMap.put(new LunarMonthDate(LunarUtils.MAX_LUNAR_YEAR, 2, true), 2449);
        sLunarMonthMap.put(new LunarMonthDate(LunarUtils.MAX_LUNAR_YEAR, 11, false), 2458);
        ArrayList arrayList = new ArrayList(sLunarMonthMap.keySet());
        sKeys = arrayList;
        Collections.sort(arrayList, new Comparator<LunarMonthDate>() { // from class: com.sonymobile.calendar.lunar.LunarHelper.1
            @Override // java.util.Comparator
            public int compare(LunarMonthDate lunarMonthDate, LunarMonthDate lunarMonthDate2) {
                int i = lunarMonthDate.lunarYear - lunarMonthDate2.lunarYear;
                return i != 0 ? i : lunarMonthDate.lunarMonth - lunarMonthDate2.lunarMonth;
            }
        });
    }

    public static LunarMonthDate getLunarMonthFromPosition(int i, int i2) {
        int iIntValue;
        int i3;
        Calendar calendar = Calendar.getInstance();
        int i4 = 0;
        calendar.set(i, 0, 1);
        LunarUtils.LunarDate lunarDateConvertSolarDateToLunarDate = LunarUtils.convertSolarDateToLunarDate(calendar.getTime());
        LunarMonthDate lunarMonthDate = new LunarMonthDate(lunarDateConvertSolarDateToLunarDate.mYear, lunarDateConvertSolarDateToLunarDate.mMonth, lunarDateConvertSolarDateToLunarDate.mIsLeap);
        if (!sLunarMonthMap.containsKey(lunarMonthDate)) {
            int i5 = 0;
            while (true) {
                if (i5 >= sKeys.size()) {
                    iIntValue = 0;
                    i3 = 0;
                    break;
                }
                if (lunarMonthDate.compare(sKeys.get(i5)) <= 0) {
                    int i6 = i5 - 1;
                    LunarMonthDate lunarMonthDate2 = sKeys.get(i6);
                    int iIntValue2 = ((sLunarMonthMap.get(sKeys.get(i6)).intValue() + ((lunarMonthDate.lunarYear - lunarMonthDate2.lunarYear) * 12)) + lunarMonthDate.lunarMonth) - lunarMonthDate2.lunarMonth;
                    i3 = i6;
                    iIntValue = iIntValue2;
                    break;
                }
                i5++;
            }
        } else {
            iIntValue = sLunarMonthMap.get(lunarMonthDate).intValue();
            i3 = 0;
        }
        int i7 = i2 + iIntValue;
        boolean z = false;
        while (i3 < sKeys.size()) {
            int iIntValue3 = sLunarMonthMap.get(sKeys.get(i3)).intValue();
            if (i7 < iIntValue3) {
                i4 = i3 - 1;
                break;
            }
            if (i7 == iIntValue3) {
                z = sKeys.get(i3).isLeap;
                i4 = i3;
            }
            i3++;
        }
        LunarMonthDate lunarMonthDate3 = sKeys.get(i4);
        int iIntValue4 = i7 - sLunarMonthMap.get(sKeys.get(i4)).intValue();
        int i8 = lunarMonthDate3.lunarYear + (iIntValue4 / 12);
        int i9 = lunarMonthDate3.lunarMonth + (iIntValue4 % 12);
        if (i9 > 12) {
            i8++;
            i9 -= 12;
        }
        return new LunarMonthDate(i8, i9, z);
    }

    public static int getPositionFromDate(int i, int i2, int i3, boolean z) {
        int iIntValue;
        Calendar calendar = Calendar.getInstance();
        int iIntValue2 = 0;
        calendar.set(i, 0, 1);
        LunarUtils.LunarDate lunarDateConvertSolarDateToLunarDate = LunarUtils.convertSolarDateToLunarDate(calendar.getTime());
        LunarMonthDate lunarMonthDate = new LunarMonthDate(lunarDateConvertSolarDateToLunarDate.mYear, lunarDateConvertSolarDateToLunarDate.mMonth, lunarDateConvertSolarDateToLunarDate.mIsLeap);
        LunarMonthDate lunarMonthDate2 = new LunarMonthDate(i2, i3, z);
        if (!sLunarMonthMap.containsKey(lunarMonthDate)) {
            int i4 = 0;
            while (true) {
                if (i4 >= sKeys.size()) {
                    iIntValue = 0;
                    break;
                }
                if (lunarMonthDate.compare(sKeys.get(i4)) <= 0) {
                    int i5 = i4 - 1;
                    LunarMonthDate lunarMonthDate3 = sKeys.get(i5);
                    iIntValue = ((sLunarMonthMap.get(sKeys.get(i5)).intValue() + ((lunarMonthDate.lunarYear - lunarMonthDate3.lunarYear) * 12)) + lunarMonthDate.lunarMonth) - lunarMonthDate3.lunarMonth;
                    break;
                }
                i4++;
            }
        } else {
            iIntValue = sLunarMonthMap.get(lunarMonthDate).intValue();
        }
        if (sLunarMonthMap.containsKey(lunarMonthDate2)) {
            iIntValue2 = sLunarMonthMap.get(lunarMonthDate2).intValue();
        } else {
            for (int i6 = 0; i6 < sKeys.size(); i6++) {
                if (lunarMonthDate2.compare(sKeys.get(i6)) <= 0) {
                    int i7 = i6 - 1;
                    LunarMonthDate lunarMonthDate4 = sKeys.get(i7);
                    iIntValue2 = ((sLunarMonthMap.get(sKeys.get(i7)).intValue() + ((lunarMonthDate2.lunarYear - lunarMonthDate4.lunarYear) * 12)) + lunarMonthDate2.lunarMonth) - lunarMonthDate4.lunarMonth;
                    break;
                }
            }
        }
        return iIntValue2 - iIntValue;
    }
}
