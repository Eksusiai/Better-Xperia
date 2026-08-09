/*
 * Copyright (c) 2014, The Linux Foundation. All rights reserved.
 * Pure Offline Implementation without Qualcomm ContentProvider Dependency.
 */

package com.android.calendar;

import android.content.Context;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class LunarUtils {
    private static final String TAG = "LunarUtils";

    // 格式化常量（保持原版兼容）
    public static final int FORMAT_LUNAR_LONG = 0x00001;
    public static final int FORMAT_LUNAR_SHORT = 0x00002;
    public static final int FORMAT_ONE_FESTIVAL = 0x00004;
    public static final int FORMAT_MULTI_FESTIVAL = 0x00008;
    public static final int FORMAT_ANIMAL = 0x00010;

    // 天干地支与生肖表
    private static final String[] GAN = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    private static final String[] ZHI = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    private static final String[] ANIMALS = {"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};

    // 农历名称
    private static final String[] LUNAR_DAY_STR = {
            "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
            "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
            "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"
    };

    private static final String[] LUNAR_MONTH_STR = {
            "正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "冬", "腊"
    };

    // 1900-2100 农历核心数据编码
    private static final long[] LUNAR_INFO = new long[]{
            0x04bd8L, 0x04ae0L, 0x0a570L, 0x054d5L, 0x0d260L, 0x0d950L, 0x16554L, 0x056a0L, 0x09ad0L, 0x055d2L,
            0x04ae0L, 0x0a5b6L, 0x0a4d0L, 0x0d250L, 0x1d255L, 0x0b540L, 0x0d6a0L, 0x0ada2L, 0x095b0L, 0x14977L,
            0x049b0L, 0x0a4b0L, 0x0b4b5L, 0x06a50L, 0x06d40L, 0x1ab54L, 0x02b60L, 0x09570L, 0x052f2L, 0x04970L,
            0x06566L, 0x0d4a0L, 0x0ea50L, 0x06e95L, 0x05ad0L, 0x02b60L, 0x186e3L, 0x092e0L, 0x1c8d7L, 0x0c950L,
            0x0d4a0L, 0x0d8a6L, 0x0b550L, 0x056a0L, 0x1a5b4L, 0x025d0L, 0x092d0L, 0x0d2b2L, 0x0a950L, 0x0b557L,
            0x06ca0L, 0x0b550L, 0x15355L, 0x04da0L, 0x0a5d0L, 0x14573L, 0x052d0L, 0x0a9a8L, 0x0e950L, 0x06aa0L,
            0x0aea6L, 0x0ab50L, 0x04b60L, 0x0aae4L, 0x0a570L, 0x05260L, 0x0f263L, 0x0d950L, 0x05b57L, 0x056a0L,
            0x096d0L, 0x04dd5L, 0x04ad0L, 0x0a4d0L, 0x0d4d4L, 0x0d250L, 0x0d558L, 0x0b540L, 0x0b6a0L, 0x195a6L,
            0x095b0L, 0x049b0L, 0x0a974L, 0x0a4b0L, 0x0b27aL, 0x06a50L, 0x06d40L, 0x0af46L, 0x0ab60L, 0x09570L,
            0x04af5L, 0x04970L, 0x064b0L, 0x074a3L, 0x0ea50L, 0x06b58L, 0x055c0L, 0x0ab60L, 0x096d5L, 0x092e0L,
            0x0c960L, 0x0d954L, 0x0d4a0L, 0x0da50L, 0x07552L, 0x056a0L, 0x0abb7L, 0x025d0L, 0x092d0L, 0x0caf5L,
            0x0a950L, 0x0b4a0L, 0x0baa4L, 0x0ad50L, 0x055d9L, 0x04ba0L, 0x0a5b0L, 0x15176L, 0x052b0L, 0x0a930L,
            0x07954L, 0x06aa0L, 0x0ad50L, 0x05b52L, 0x04b60L, 0x0a6e6L, 0x0a4e0L, 0x0d260L, 0x0ea65L, 0x0d530L,
            0x05aa0L, 0x076a3L, 0x096d0L, 0x04afbL, 0x04ad0L, 0x0a4d0L, 0x1d0b6L, 0x0d250L, 0x0d520L, 0x0dd45L,
            0x0b5a0L, 0x056d0L, 0x055b2L, 0x049b0L, 0x0a577L, 0x0a4b0L, 0x0aa50L, 0x1b255L, 0x06d20L, 0x0ada0L
    };

    /**
     * 判断当前系统语言是否需要显示农历
     */
    public static boolean showLunar(Context context) {
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage().toLowerCase();
        String country = locale.getCountry().toLowerCase();
        return ("zh".equals(language) && ("cn".equals(country) || "tw".equals(country) || "hk".equals(country)));
    }

    public static void clearInfo() {
        // 空实现，兼容旧代码调用
    }

    /**
     * 计算并获取天干地支年份 (例如 "丙午[马]年")
     */
    public static String getGanZhiYear(int lunarYear) {
        int ganIdx = (lunarYear - 4) % 10;
        if (ganIdx < 0) ganIdx += 10;
        int zhiIdx = (lunarYear - 4) % 12;
        if (zhiIdx < 0) zhiIdx += 12;
        return GAN[ganIdx] + ZHI[zhiIdx] + "[" + ANIMALS[zhiIdx] + "]年";
    }

    /**
     * 原版兼容接口：由算法直接计算农历信息并返回
     */
    public static String get(Context context, int year, int month, int day, int format,
                             boolean showLunarBeforeFestival, ArrayList<String> result) {

        LunarDate date = calcLunar(year, month, day);
        if (date == null) return "";

        StringBuilder sb = new StringBuilder();

        // 1. 拼接农历日期 / 月份
        if ((format & FORMAT_LUNAR_SHORT) == FORMAT_LUNAR_SHORT
                || (format & FORMAT_LUNAR_LONG) == FORMAT_LUNAR_LONG) {
            String label = (date.day == 1) ? (LUNAR_MONTH_STR[date.month - 1] + "月") : LUNAR_DAY_STR[date.day - 1];
            if ((format & FORMAT_LUNAR_LONG) == FORMAT_LUNAR_LONG) {
                label = getGanZhiYear(date.year) + " " + LUNAR_MONTH_STR[date.month - 1] + "月" + LUNAR_DAY_STR[date.day - 1];
            }
            sb.append(label);
            if (result != null) result.add(label);
        }

        // 2. 拼接生肖/干支
        if ((format & FORMAT_ANIMAL) == FORMAT_ANIMAL) {
            String animal = getGanZhiYear(date.year);
            if (sb.length() > 0) sb.append(" ");
            sb.append(animal);
            if (result != null) result.add(animal);
        }

        return sb.toString();
    }

    // 内部农历算法实现
    private static LunarDate calcLunar(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);

        Calendar baseCal = Calendar.getInstance();
        baseCal.set(1900, 0, 31);

        long offset = (cal.getTimeInMillis() - baseCal.getTimeInMillis()) / (24 * 60 * 60 * 1000L);

        int lunarYear = 1900;
        int daysInYear;
        for (int i = 1900; i < 2100 && offset > 0; i++) {
            daysInYear = getLunarYearDays(i);
            offset -= daysInYear;
            lunarYear++;
        }
        if (offset < 0) {
            offset += getLunarYearDays(lunarYear - 1);
            lunarYear--;
        }

        int leapMonth = getLeapMonth(lunarYear);
        boolean isLeap = false;
        int lunarMonth = 1;
        int daysInMonth = 0;

        for (int i = 1; i < 13 && offset >= 0; i++) {
            if (leapMonth > 0 && i == (leapMonth + 1) && !isLeap) {
                --i;
                isLeap = true;
                daysInMonth = getLeapDays(lunarYear);
            } else {
                daysInMonth = getLunarMonthDays(lunarYear, i);
            }

            if (isLeap && i == (leapMonth + 1)) {
                isLeap = false;
            }

            offset -= daysInMonth;
            if (!isLeap) {
                lunarMonth++;
            }
        }

        if (offset < 0) {
            offset += daysInMonth;
            if (!isLeap) {
                lunarMonth--;
            }
        }

        int lunarDay = (int) offset + 1;
        return new LunarDate(lunarYear, lunarMonth, lunarDay, isLeap);
    }

    private static int getLunarYearDays(int y) {
        int sum = 348;
        for (int i = 0x8000; i > 0x8; i >>= 1) {
            sum += ((LUNAR_INFO[y - 1900] & i) != 0) ? 1 : 0;
        }
        return sum + getLeapDays(y);
    }

    private static int getLeapMonth(int y) {
        return (int) (LUNAR_INFO[y - 1900] & 0xf);
    }

    private static int getLeapDays(int y) {
        if (getLeapMonth(y) != 0) {
            return ((LUNAR_INFO[y - 1900] & 0x10000L) != 0) ? 30 : 29;
        }
        return 0;
    }

    private static int getLunarMonthDays(int y, int m) {
        return ((LUNAR_INFO[y - 1900] & (0x10000L >> m)) != 0) ? 30 : 29;
    }

    private static class LunarDate {
        int year;
        int month;
        int day;
        boolean isLeap;

        LunarDate(int year, int month, int day, boolean isLeap) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.isLeap = isLeap;
        }
    }
    // =========================================================================
    // 补全 Loader 兼容类（继承 AsyncTaskLoader 自动提供监听器注册/反注册支持）
    // =========================================================================
    public static class LunarInfoLoader extends android.content.AsyncTaskLoader<Void> {
        public LunarInfoLoader(Context context) {
            super(context);
        }
        public void load(int year, int month, int day) {}
        public void load(int year, int month) {}
        public void load(int from_year, int from_month, int from_day, int to_year, int to_month, int to_day) {}

        @Override
        public Void loadInBackground() {
            return null;
        }
    }
}
