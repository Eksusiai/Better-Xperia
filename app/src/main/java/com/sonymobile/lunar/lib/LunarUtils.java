package com.sonymobile.lunar.lib;

import android.util.Log;
import com.google.common.base.Ascii;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public final class LunarUtils {
    private static final byte DZ_None = 0;
    private static final String EMPTY = "";
    private static final byte JQ_None = 0;
    public static final int MAX_LUNAR_YEAR = 2099;
    public static final int MIN_LUNAR_YEAR = 1901;
    private static final String TAG = "LunarUtils";
    private static final byte TG_None = 0;
    private static final int TOTAL_LUNAR_JIERI = 11;
    private static final int TOTAL_SOLAR_JIERI = 23;
    private static final int TOTAL_WEEK_JIERI = 3;
    private static final HashMap<Animal, String> sAnimalStringsMap;
    private static final byte[] sDaysOfMonth = {Ascii.US, Ascii.FS, Ascii.US, Ascii.RS, Ascii.US, Ascii.RS, Ascii.US, Ascii.US, Ascii.RS, Ascii.US, Ascii.RS, Ascii.US};
    private static final String[] sDiZhiStrings;
    private static final String[] sJieQiStrings;
    private static final LunarCalendarData[] sLunarCalendarData;
    public static final String[] sLunarDayStrings;
    private static final LunarFestival[] sLunarFestivalTable;
    private static final HashMap<LunarJieRi, String> sLunarJieRiStringsMap;
    public static final String[] sLunarMonthStrings;
    public static final String[] sLunarYearStrings;
    public static final String[] sLunarYearStringsMedium;
    private static final SolarFestival[] sSolarFestivalTable;
    private static final HashMap<SolarJieRi, String> sSolarJieRiStringsMap;
    private static final short[][] sSolarTermDataTable;
    private static final String[] sTianGanStrings;
    private static final WeekFestival[] sWeekFestivalTable;
    private static final HashMap<WeekJieRi, String> sWeekJieRiStringsMap;

    private enum Animal {
        SX_Rat,
        SX_Ox,
        SX_Tiger,
        SX_Rabbit,
        SX_Dragon,
        SX_Snake,
        SX_Horse,
        SX_Sheep,
        SX_Monkey,
        SX_Rooster,
        SX_Dog,
        SX_Pig,
        SX_None
    }

    private enum LunarJieRi {
        LJR_CHUNJIE,
        LJR_YUANXIAO,
        LJR_LONGTAITOU,
        LJR_DUANWU,
        LJR_QIXI,
        LJR_ZHONGYUAN,
        LJR_ZHONGQIU,
        LJR_CHONGYANG,
        LJR_LABA,
        LJR_XIAONIAN,
        LJR_CHUXI,
        LJR_NONE
    }

    private enum SolarJieRi {
        SJR_NEWYEARSDAY,
        SJR_VALENTINEDAY,
        SJR_WOMENDAY,
        SJR_ZHISHU,
        SJR_FOOLDAY,
        SJR_EARTHDAY,
        SJR_LABORDAY,
        SJR_YOUTHDAY,
        SJR_NOTOBACODAY,
        SJR_CHILDRENDAY,
        SJR_EYESDAY,
        SJR_JIANDANG,
        SJR_JIANJUN,
        SJR_JAPANSURRENDER,
        SJR_TEACHERDAY,
        SJR_JIUYIBA,
        SJR_NATIONALDAY,
        SJR_HALLOWEEN,
        SJR_SINGLEDAY,
        SJR_AIDSDAY,
        SJR_NANJINGDATUSHA,
        SJR_CHRISTMASEVE,
        SJR_CHRISTMAS,
        SJR_NONE
    }

    private enum WeekJieRi {
        WJR_MONTHERDAY,
        WJR_FATHERDAY,
        WJR_THANKSGIVINGDAY,
        WJR_NONE
    }

    static {
        HashMap<Animal, String> map = new HashMap<>();
        sAnimalStringsMap = map;
        HashMap<LunarJieRi, String> map2 = new HashMap<>();
        sLunarJieRiStringsMap = map2;
        HashMap<SolarJieRi, String> map3 = new HashMap<>();
        sSolarJieRiStringsMap = map3;
        HashMap<WeekJieRi, String> map4 = new HashMap<>();
        sWeekJieRiStringsMap = map4;
        sLunarFestivalTable = new LunarFestival[]{new LunarFestival(LunarJieRi.LJR_CHUNJIE, 1, 1), new LunarFestival(LunarJieRi.LJR_YUANXIAO, 1, 15), new LunarFestival(LunarJieRi.LJR_LONGTAITOU, 2, 2), new LunarFestival(LunarJieRi.LJR_DUANWU, 5, 5), new LunarFestival(LunarJieRi.LJR_QIXI, 7, 7), new LunarFestival(LunarJieRi.LJR_ZHONGYUAN, 7, 15), new LunarFestival(LunarJieRi.LJR_ZHONGQIU, 8, 15), new LunarFestival(LunarJieRi.LJR_CHONGYANG, 9, 9), new LunarFestival(LunarJieRi.LJR_LABA, 12, 8), new LunarFestival(LunarJieRi.LJR_XIAONIAN, 12, 23)};
        sSolarFestivalTable = new SolarFestival[]{new SolarFestival(SolarJieRi.SJR_NEWYEARSDAY, 1, 1), new SolarFestival(SolarJieRi.SJR_VALENTINEDAY, 2, 14), new SolarFestival(SolarJieRi.SJR_WOMENDAY, 3, 8), new SolarFestival(SolarJieRi.SJR_ZHISHU, 3, 12), new SolarFestival(SolarJieRi.SJR_FOOLDAY, 4, 1), new SolarFestival(SolarJieRi.SJR_EARTHDAY, 4, 22), new SolarFestival(SolarJieRi.SJR_LABORDAY, 5, 1), new SolarFestival(SolarJieRi.SJR_YOUTHDAY, 5, 4), new SolarFestival(SolarJieRi.SJR_NOTOBACODAY, 5, 31), new SolarFestival(SolarJieRi.SJR_CHILDRENDAY, 6, 1), new SolarFestival(SolarJieRi.SJR_EYESDAY, 6, 6), new SolarFestival(SolarJieRi.SJR_JIANDANG, 7, 1), new SolarFestival(SolarJieRi.SJR_JIANJUN, 8, 1), new SolarFestival(SolarJieRi.SJR_JAPANSURRENDER, 8, 15), new SolarFestival(SolarJieRi.SJR_TEACHERDAY, 9, 10), new SolarFestival(SolarJieRi.SJR_JIUYIBA, 9, 18), new SolarFestival(SolarJieRi.SJR_NATIONALDAY, 10, 1), new SolarFestival(SolarJieRi.SJR_HALLOWEEN, 10, 31), new SolarFestival(SolarJieRi.SJR_SINGLEDAY, 11, 11), new SolarFestival(SolarJieRi.SJR_AIDSDAY, 12, 1), new SolarFestival(SolarJieRi.SJR_NANJINGDATUSHA, 12, 13), new SolarFestival(SolarJieRi.SJR_CHRISTMASEVE, 12, 24), new SolarFestival(SolarJieRi.SJR_CHRISTMAS, 12, 25)};
        sWeekFestivalTable = new WeekFestival[]{new WeekFestival(WeekJieRi.WJR_MONTHERDAY, 5, 2, 1), new WeekFestival(WeekJieRi.WJR_FATHERDAY, 6, 3, 1), new WeekFestival(WeekJieRi.WJR_THANKSGIVINGDAY, 11, 4, 5)};
        sTianGanStrings = new String[]{"", "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
        sDiZhiStrings = new String[]{"", "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
        sJieQiStrings = new String[]{"", "小寒", "大寒", "立春", "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑", "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"};
        sLunarMonthStrings = new String[]{"闰", "正月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "冬月", "腊月"};
        sLunarDayStrings = new String[]{"", "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"};
        sLunarYearStrings = new String[]{"辛丑牛年(1901)", "壬寅虎年(1902)", "癸卯兔年(1903)", "甲辰龙年(1904)", "乙巳蛇年(1905)", "丙午马年(1906)", "丁未羊年(1907)", "戊申猴年(1908)", "己酉鸡年(1909)", "庚戌狗年(1910)", "辛亥猪年(1911)", "壬子鼠年(1912)", "癸丑牛年(1913)", "甲寅虎年(1914)", "乙卯兔年(1915)", "丙辰龙年(1916)", "丁巳蛇年(1917)", "戊午马年(1918)", "己未羊年(1919)", "庚申猴年(1920)", "辛酉鸡年(1921)", "壬戌狗年(1922)", "癸亥猪年(1923)", "甲子鼠年(1924)", "乙丑牛年(1925)", "丙寅虎年(1926)", "丁卯兔年(1927)", "戊辰龙年(1928)", "己巳蛇年(1929)", "庚午马年(1930)", "辛未羊年(1931)", "壬申猴年(1932)", "癸酉鸡年(1933)", "甲戌狗年(1934)", "乙亥猪年(1935)", "丙子鼠年(1936)", "丁丑牛年(1937)", "戊寅虎年(1938)", "己卯兔年(1939)", "庚辰龙年(1940)", "辛巳蛇年(1941)", "壬午马年(1942)", "癸未羊年(1943)", "甲申猴年(1944)", "乙酉鸡年(1945)", "丙戌狗年(1946)", "丁亥猪年(1947)", "戊子鼠年(1948)", "己丑牛年(1949)", "庚寅虎年(1950)", "辛卯兔年(1951)", "壬辰龙年(1952)", "癸巳蛇年(1953)", "甲午马年(1954)", "乙未羊年(1955)", "丙申猴年(1956)", "丁酉鸡年(1957)", "戊戌狗年(1958)", "己亥猪年(1959)", "庚子鼠年(1960)", "辛丑牛年(1961)", "壬寅虎年(1962)", "癸卯兔年(1963)", "甲辰龙年(1964)", "乙巳蛇年(1965)", "丙午马年(1966)", "丁未羊年(1967)", "戊申猴年(1968)", "己酉鸡年(1969)", "庚戌狗年(1970)", "辛亥猪年(1971)", "壬子鼠年(1972)", "癸丑牛年(1973)", "甲寅虎年(1974)", "乙卯兔年(1975)", "丙辰龙年(1976)", "丁巳蛇年(1977)", "戊午马年(1978)", "己未羊年(1979)", "庚申猴年(1980)", "辛酉鸡年(1981)", "壬戌狗年(1982)", "癸亥猪年(1983)", "甲子鼠年(1984)", "乙丑牛年(1985)", "丙寅虎年(1986)", "丁卯兔年(1987)", "戊辰龙年(1988)", "己巳蛇年(1989)", "庚午马年(1990)", "辛未羊年(1991)", "壬申猴年(1992)", "癸酉鸡年(1993)", "甲戌狗年(1994)", "乙亥猪年(1995)", "丙子鼠年(1996)", "丁丑牛年(1997)", "戊寅虎年(1998)", "己卯兔年(1999)", "庚辰龙年(2000)", "辛巳蛇年(2001)", "壬午马年(2002)", "癸未羊年(2003)", "甲申猴年(2004)", "乙酉鸡年(2005)", "丙戌狗年(2006)", "丁亥猪年(2007)", "戊子鼠年(2008)", "己丑牛年(2009)", "庚寅虎年(2010)", "辛卯兔年(2011)", "壬辰龙年(2012)", "癸巳蛇年(2013)", "甲午马年(2014)", "乙未羊年(2015)", "丙申猴年(2016)", "丁酉鸡年(2017)", "戊戌狗年(2018)", "己亥猪年(2019)", "庚子鼠年(2020)", "辛丑牛年(2021)", "壬寅虎年(2022)", "癸卯兔年(2023)", "甲辰龙年(2024)", "乙巳蛇年(2025)", "丙午马年(2026)", "丁未羊年(2027)", "戊申猴年(2028)", "己酉鸡年(2029)", "庚戌狗年(2030)", "辛亥猪年(2031)", "壬子鼠年(2032)", "癸丑牛年(2033)", "甲寅虎年(2034)", "乙卯兔年(2035)", "丙辰龙年(2036)", "丁巳蛇年(2037)", "戊午马年(2038)", "己未羊年(2039)", "庚申猴年(2040)", "辛酉鸡年(2041)", "壬戌狗年(2042)", "癸亥猪年(2043)", "甲子鼠年(2044)", "乙丑牛年(2045)", "丙寅虎年(2046)", "丁卯兔年(2047)", "戊辰龙年(2048)", "己巳蛇年(2049)", "庚午马年(2050)", "辛未羊年(2051)", "壬申猴年(2052)", "癸酉鸡年(2053)", "甲戌狗年(2054)", "乙亥猪年(2055)", "丙子鼠年(2056)", "丁丑牛年(2057)", "戊寅虎年(2058)", "己卯兔年(2059)", "庚辰龙年(2060)", "辛巳蛇年(2061)", "壬午马年(2062)", "癸未羊年(2063)", "甲申猴年(2064)", "乙酉鸡年(2065)", "丙戌狗年(2066)", "丁亥猪年(2067)", "戊子鼠年(2068)", "己丑牛年(2069)", "庚寅虎年(2070)", "辛卯兔年(2071)", "壬辰龙年(2072)", "癸巳蛇年(2073)", "甲午马年(2074)", "乙未羊年(2075)", "丙申猴年(2076)", "丁酉鸡年(2077)", "戊戌狗年(2078)", "己亥猪年(2079)", "庚子鼠年(2080)", "辛丑牛年(2081)", "壬寅虎年(2082)", "癸卯兔年(2083)", "甲辰龙年(2084)", "乙巳蛇年(2085)", "丙午马年(2086)", "丁未羊年(2087)", "戊申猴年(2088)", "己酉鸡年(2089)", "庚戌狗年(2090)", "辛亥猪年(2091)", "壬子鼠年(2092)", "癸丑牛年(2093)", "甲寅虎年(2094)", "乙卯兔年(2095)", "丙辰龙年(2096)", "丁巳蛇年(2097)", "戊午马年(2098)", "己未羊年(2099)"};
        sLunarYearStringsMedium = new String[]{"1901年", "1902年", "1903年", "1904年", "1905年", "1906年", "1907年", "1908年", "1909年", "1910年", "1911年", "1912年", "1913年", "1914年", "1915年", "1916年", "1917年", "1918年", "1919年", "1920年", "1921年", "1922年", "1923年", "1924年", "1925年", "1926年", "1927年", "1928年", "1929年", "1930年", "1931年", "1932年", "1933年", "1934年", "1935年", "1936年", "1937年", "1938年", "1939年", "1940年", "1941年", "1942年", "1943年", "1944年", "1945年", "1946年", "1947年", "1948年", "1949年", "1950年", "1951年", "1952年", "1953年", "1954年", "1955年", "1956年", "1957年", "1958年", "1959年", "1960年", "1961年", "1962年", "1963年", "1964年", "1965年", "1966年", "1967年", "1968年", "1969年", "1970年", "1971年", "1972年", "1973年", "1974年", "1975年", "1976年", "1977年", "1978年", "1979年", "1980年", "1981年", "1982年", "1983年", "1984年", "1985年", "1986年", "1987年", "1988年", "1989年", "1990年", "1991年", "1992年", "1993年", "1994年", "1995年", "1996年", "1997年", "1998年", "1999年", "2000年", "2001年", "2002年", "2003年", "2004年", "2005年", "2006年", "2007年", "2008年", "2009年", "2010年", "2011年", "2012年", "2013年", "2014年", "2015年", "2016年", "2017年", "2018年", "2019年", "2020年", "2021年", "2022年", "2023年", "2024年", "2025年", "2026年", "2027年", "2028年", "2029年", "2030年", "2031年", "2032年", "2033年", "2034年", "2035年", "2036年", "2037年", "2038年", "2039年", "2040年", "2041年", "2042年", "2043年", "2044年", "2045年", "2046年", "2047年", "2048年", "2049年", "2050年", "2051年", "2052年", "2053年", "2054年", "2055年", "2056年", "2057年", "2058年", "2059年", "2060年", "2061年", "2062年", "2063年", "2064年", "2065年", "2066年", "2067年", "2068年", "2069年", "2070年", "2071年", "2072年", "2073年", "2074年", "2075年", "2076年", "2077年", "2078年", "2079年", "2080年", "2081年", "2082年", "2083年", "2084年", "2085年", "2086年", "2087年", "2088年", "2089年", "2090年", "2091年", "2092年", "2093年", "2094年", "2095年", "2096年", "2097年", "2098年", "2099年"};
        map.put(Animal.SX_Rat, "鼠");
        map.put(Animal.SX_Ox, "牛");
        map.put(Animal.SX_Tiger, "虎");
        map.put(Animal.SX_Rabbit, "兔");
        map.put(Animal.SX_Dragon, "龙");
        map.put(Animal.SX_Snake, "蛇");
        map.put(Animal.SX_Horse, "马");
        map.put(Animal.SX_Sheep, "羊");
        map.put(Animal.SX_Monkey, "猴");
        map.put(Animal.SX_Rooster, "鸡");
        map.put(Animal.SX_Dog, "狗");
        map.put(Animal.SX_Pig, "猪");
        map.put(Animal.SX_None, "");
        map2.put(LunarJieRi.LJR_CHUNJIE, "春节");
        map2.put(LunarJieRi.LJR_YUANXIAO, "元宵节");
        map2.put(LunarJieRi.LJR_LONGTAITOU, "龙抬头");
        map2.put(LunarJieRi.LJR_DUANWU, "端午节");
        map2.put(LunarJieRi.LJR_QIXI, "七夕");
        map2.put(LunarJieRi.LJR_ZHONGYUAN, "中元节");
        map2.put(LunarJieRi.LJR_ZHONGQIU, "中秋节");
        map2.put(LunarJieRi.LJR_CHONGYANG, "重阳节");
        map2.put(LunarJieRi.LJR_LABA, "腊八");
        map2.put(LunarJieRi.LJR_XIAONIAN, "小年");
        map2.put(LunarJieRi.LJR_CHUXI, "除夕");
        map2.put(LunarJieRi.LJR_NONE, "");
        map3.put(SolarJieRi.SJR_NEWYEARSDAY, "元旦");
        map3.put(SolarJieRi.SJR_VALENTINEDAY, "情人节");
        map3.put(SolarJieRi.SJR_WOMENDAY, "妇女节");
        map3.put(SolarJieRi.SJR_ZHISHU, "植树节");
        map3.put(SolarJieRi.SJR_FOOLDAY, "愚人节");
        map3.put(SolarJieRi.SJR_EARTHDAY, "地球日");
        map3.put(SolarJieRi.SJR_LABORDAY, "劳动节");
        map3.put(SolarJieRi.SJR_YOUTHDAY, "青年节");
        map3.put(SolarJieRi.SJR_NOTOBACODAY, "无烟日");
        map3.put(SolarJieRi.SJR_CHILDRENDAY, "儿童节");
        map3.put(SolarJieRi.SJR_EYESDAY, "爱眼日");
        map3.put(SolarJieRi.SJR_JIANDANG, "建党日");
        map3.put(SolarJieRi.SJR_JIANJUN, "建军节");
        map3.put(SolarJieRi.SJR_JAPANSURRENDER, "日本投降日");
        map3.put(SolarJieRi.SJR_TEACHERDAY, "教师节");
        map3.put(SolarJieRi.SJR_JIUYIBA, "九一八");
        map3.put(SolarJieRi.SJR_NATIONALDAY, "国庆节");
        map3.put(SolarJieRi.SJR_HALLOWEEN, "万圣节");
        map3.put(SolarJieRi.SJR_SINGLEDAY, "光棍节");
        map3.put(SolarJieRi.SJR_AIDSDAY, "艾滋病日");
        map3.put(SolarJieRi.SJR_NANJINGDATUSHA, "南京大屠杀纪念日");
        map3.put(SolarJieRi.SJR_CHRISTMASEVE, "平安夜");
        map3.put(SolarJieRi.SJR_CHRISTMAS, "圣诞节");
        map3.put(SolarJieRi.SJR_NONE, "");
        map4.put(WeekJieRi.WJR_MONTHERDAY, "母亲节");
        map4.put(WeekJieRi.WJR_FATHERDAY, "父亲节");
        map4.put(WeekJieRi.WJR_THANKSGIVINGDAY, "感恩节");
        map4.put(WeekJieRi.WJR_NONE, "");
        sLunarCalendarData = new LunarCalendarData[]{new LunarCalendarData(8, 2, 50, false, 1198), new LunarCalendarData(9, 3, 39, false, 2647), new LunarCalendarData(10, 4, 29, false, 21837), new LunarCalendarData(1, 5, 47, false, 3366), new LunarCalendarData(2, 6, 35, false, 3477), new LunarCalendarData(3, 7, 25, true, 18005), new LunarCalendarData(4, 8, 44, false, 1386), new LunarCalendarData(5, 9, 33, false, 2477), new LunarCalendarData(6, 10, 22, false, 9565), new LunarCalendarData(7, 11, 41, false, 1198), new LunarCalendarData(8, 12, 30, false, 27227), new LunarCalendarData(9, 1, 49, false, 2637), new LunarCalendarData(10, 2, 37, false, 3365), new LunarCalendarData(1, 3, 26, true, 23845), new LunarCalendarData(2, 4, 45, false, 2900), new LunarCalendarData(3, 5, 34, false, 3434), new LunarCalendarData(4, 6, 23, false, 10970), new LunarCalendarData(5, 7, 42, false, 2395), new LunarCalendarData(6, 8, 32, true, 29847), new LunarCalendarData(7, 9, 51, false, 1175), new LunarCalendarData(8, 10, 39, false, 2635), new LunarCalendarData(9, 11, 28, false, 23371), new LunarCalendarData(10, 12, 47, false, 1701), new LunarCalendarData(1, 1, 36, false, 1748), new LunarCalendarData(2, 2, 24, true, 19125), new LunarCalendarData(3, 3, 44, false, 694), new LunarCalendarData(4, 4, 33, false, 2391), new LunarCalendarData(5, 5, 23, false, 9519), new LunarCalendarData(6, 6, 41, false, 1175), new LunarCalendarData(7, 7, 30, false, 26198), new LunarCalendarData(8, 8, 48, false, 3402), new LunarCalendarData(9, 9, 37, false, 3749), new LunarCalendarData(10, 10, 26, true, 22185), new LunarCalendarData(1, 11, 45, false, 1453), new LunarCalendarData(2, 12, 35, false, 694), new LunarCalendarData(3, 1, 24, true, 14446), new LunarCalendarData(4, 2, 42, false, 2350), new LunarCalendarData(5, 3, 31, true, 31885), new LunarCalendarData(6, 4, 50, false, 3221), new LunarCalendarData(7, 5, 39, false, 3402), new LunarCalendarData(8, 6, 27, true, 28042), new LunarCalendarData(9, 7, 46, false, 2901), new LunarCalendarData(10, 8, 36, false, 1386), new LunarCalendarData(1, 9, 25, true, 19035), new LunarCalendarData(2, 10, 44, false, 605), new LunarCalendarData(3, 11, 33, false, 2349), new LunarCalendarData(4, 12, 22, false, 11563), new LunarCalendarData(5, 1, 41, false, 2709), new LunarCalendarData(6, 2, 29, false, 31573), new LunarCalendarData(7, 3, 48, false, 1738), new LunarCalendarData(8, 4, 37, false, 2901), new LunarCalendarData(9, 5, 27, true, 21813), new LunarCalendarData(10, 6, 45, false, 1242), new LunarCalendarData(1, 7, 34, false, 2651), new LunarCalendarData(2, 8, 24, true, 13399), new LunarCalendarData(3, 9, 43, false, 1323), new LunarCalendarData(4, 10, 31, false, 35482), new LunarCalendarData(5, 11, 49, false, 3733), new LunarCalendarData(6, 12, 39, false, 1706), new LunarCalendarData(7, 1, 28, false, 27370), new LunarCalendarData(8, 2, 46, false, 2741), new LunarCalendarData(9, 3, 36, false, 1206), new LunarCalendarData(10, 4, 25, false, 19118), new LunarCalendarData(1, 5, 44, false, 2647), new LunarCalendarData(2, 6, 33, false, 1318), new LunarCalendarData(3, 7, 21, false, 16166), new LunarCalendarData(4, 8, 40, false, 3477), new LunarCalendarData(5, 9, 30, false, 30133), new LunarCalendarData(6, 10, 48, false, 1386), new LunarCalendarData(7, 11, 37, false, 2413), new LunarCalendarData(8, 12, 27, false, 21725), new LunarCalendarData(9, 1, 46, false, 1197), new LunarCalendarData(10, 2, 34, false, 2637), new LunarCalendarData(1, 3, 23, false, 19789), new LunarCalendarData(2, 4, 42, false, 3365), new LunarCalendarData(3, 5, 31, false, 36181), new LunarCalendarData(4, 6, 49, false, 2900), new LunarCalendarData(5, 7, 38, false, 2922), new LunarCalendarData(6, 8, 28, true, 26970), new LunarCalendarData(7, 9, 47, false, 2395), new LunarCalendarData(8, 10, 36, false, 1179), new LunarCalendarData(9, 11, 25, false, 19095), new LunarCalendarData(10, 12, 44, false, 2635), new LunarCalendarData(1, 1, 33, false, 43815), new LunarCalendarData(2, 2, 51, false, 1701), new LunarCalendarData(3, 3, 40, false, 1748), new LunarCalendarData(4, 4, 29, false, 27380), new LunarCalendarData(5, 5, 48, false, 2742), new LunarCalendarData(6, 6, 37, false, 2391), new LunarCalendarData(7, 7, 27, false, 21679), new LunarCalendarData(8, 8, 46, false, 1175), new LunarCalendarData(9, 9, 35, false, 1611), new LunarCalendarData(10, 10, 23, false, 14154), new LunarCalendarData(1, 11, 41, false, 3749), new LunarCalendarData(2, 12, 31, false, 34485), new LunarCalendarData(3, 1, 50, false, 1452), new LunarCalendarData(4, 2, 38, false, 2742), new LunarCalendarData(5, 3, 28, false, 22893), new LunarCalendarData(6, 4, 47, false, 2350), new LunarCalendarData(7, 5, 36, false, 3222), new LunarCalendarData(8, 6, 24, false, 19861), new LunarCalendarData(9, 7, 43, false, 3402), new LunarCalendarData(10, 8, 32, false, 3493), new LunarCalendarData(1, 9, 22, false, 10069), new LunarCalendarData(2, 10, 40, false, 1386), new LunarCalendarData(3, 11, 29, false, 31419), new LunarCalendarData(4, 12, 49, false, 605), new LunarCalendarData(5, 1, 38, false, 2349), new LunarCalendarData(6, 2, 26, false, 23723), new LunarCalendarData(7, 3, 45, false, 2709), new LunarCalendarData(8, 4, 34, false, 2890), new LunarCalendarData(9, 5, 23, false, 19370), new LunarCalendarData(10, 6, 41, false, 2773), new LunarCalendarData(1, 7, 31, false, 38237), new LunarCalendarData(2, 8, 50, false, 1210), new LunarCalendarData(3, 9, 39, false, 2651), new LunarCalendarData(4, 10, 28, true, 25879), new LunarCalendarData(5, 11, 47, false, 1323), new LunarCalendarData(6, 12, 36, false, 2707), new LunarCalendarData(7, 1, 25, false, 18325), new LunarCalendarData(8, 2, 43, false, 1706), new LunarCalendarData(9, 3, 32, false, 2773), new LunarCalendarData(10, 4, 22, false, 9653), new LunarCalendarData(1, 5, 41, false, 1206), new LunarCalendarData(2, 6, 29, false, 27246), new LunarCalendarData(3, 7, 48, false, 2638), new LunarCalendarData(4, 8, 37, false, 3366), new LunarCalendarData(5, 9, 26, false, 24230), new LunarCalendarData(6, 10, 44, false, 3411), new LunarCalendarData(7, 11, 34, false, 1450), new LunarCalendarData(8, 12, 23, false, 14186), new LunarCalendarData(9, 1, 42, false, 2413), new LunarCalendarData(10, 2, 31, false, 46255), new LunarCalendarData(1, 3, 50, false, 1197), new LunarCalendarData(2, 4, 39, false, 2637), new LunarCalendarData(3, 5, 28, true, 27915), new LunarCalendarData(4, 6, 46, false, 3365), new LunarCalendarData(5, 7, 35, false, 3410), new LunarCalendarData(6, 8, 24, false, 24020), new LunarCalendarData(7, 9, 43, false, 2906), new LunarCalendarData(8, 10, 32, false, 1389), new LunarCalendarData(9, 11, 22, false, 9563), new LunarCalendarData(10, 12, 41, false, 1179), new LunarCalendarData(1, 1, 30, false, 31319), new LunarCalendarData(2, 2, 48, false, 2635), new LunarCalendarData(3, 3, 37, false, 2725), new LunarCalendarData(4, 4, 26, true, 23333), new LunarCalendarData(5, 5, 45, false, 1746), new LunarCalendarData(6, 6, 33, false, 2778), new LunarCalendarData(7, 7, 23, true, 13494), new LunarCalendarData(8, 8, 42, false, 2359), new LunarCalendarData(9, 9, 32, false, 33951), new LunarCalendarData(10, 10, 50, false, 1175), new LunarCalendarData(1, 11, 39, false, 1611), new LunarCalendarData(2, 12, 28, true, 26250), new LunarCalendarData(3, 1, 46, false, 3749), new LunarCalendarData(4, 2, 35, false, 1714), new LunarCalendarData(5, 3, 24, true, 19052), new LunarCalendarData(6, 4, 43, false, 2734), new LunarCalendarData(7, 5, 33, false, 2350), new LunarCalendarData(8, 6, 21, false, 15662), new LunarCalendarData(9, 7, 40, false, 3222), new LunarCalendarData(10, 8, 29, false, 32085), new LunarCalendarData(1, 9, 48, false, 3402), new LunarCalendarData(2, 10, 36, false, 3493), new LunarCalendarData(3, 11, 26, false, 21973), new LunarCalendarData(4, 12, 45, false, 1386), new LunarCalendarData(5, 1, 34, false, 2669), new LunarCalendarData(6, 2, 23, false, 17757), new LunarCalendarData(7, 3, 42, false, 1325), new LunarCalendarData(8, 4, 31, false, 35483), new LunarCalendarData(9, 5, 50, false, 2709), new LunarCalendarData(10, 6, 38, false, 2890), new LunarCalendarData(1, 7, 27, false, 27498), new LunarCalendarData(2, 8, 46, false, 2773), new LunarCalendarData(3, 9, 36, false, 1370), new LunarCalendarData(4, 10, 24, false, 19130), new LunarCalendarData(5, 11, 43, false, 2651), new LunarCalendarData(6, 12, 33, false, 1323), new LunarCalendarData(7, 1, 22, false, 15143), new LunarCalendarData(8, 2, 40, false, 1683), new LunarCalendarData(9, 3, 29, false, 30515), new LunarCalendarData(10, 4, 48, false, 1706), new LunarCalendarData(1, 5, 37, false, 2773), new LunarCalendarData(2, 6, 26, true, 21685), new LunarCalendarData(3, 7, 45, false, 1206), new LunarCalendarData(4, 8, 34, false, 2647), new LunarCalendarData(5, 9, 24, false, 17742), new LunarCalendarData(6, 10, 41, false, 3366), new LunarCalendarData(7, 11, 30, false, 36502), new LunarCalendarData(8, 12, 49, false, 3410), new LunarCalendarData(9, 1, 38, false, 3498), new LunarCalendarData(10, 2, 27, true, 26026), new LunarCalendarData(1, 3, 46, false, 1389), new LunarCalendarData(2, 4, 36, false, 1198), new LunarCalendarData(3, 5, 25, false, 19101), new LunarCalendarData(4, 6, 43, false, 2637), new LunarCalendarData(5, 7, 32, false, 3349), new LunarCalendarData(6, 8, 21, false, 12069)};
        sSolarTermDataTable = new short[][]{new short[]{86, 52, 86, 85, 102, 102, 120, 136, 136, 137, 120, 104}, new short[]{86, 53, 86, 86, 102, 103, 136, 136, 136, 137, 120, 120}, new short[]{86, 69, 103, 86, 103, 103, 136, 137, 137, 137, 120, 120}, new short[]{87, 69, 86, 69, 86, 102, 119, 120, 120, 137, 120, 103}, new short[]{86, 52, 86, 85, 102, 102, 120, 136, 136, 137, 120, 104}, new short[]{86, 53, 86, 86, 102, 102, 136, 136, 136, 137, 120, 120}, new short[]{86, 69, 103, 86, 103, 103, 136, 137, 137, 137, 120, 120}, new short[]{87, 69, 86, 69, 86, 102, 119, 120, 120, 137, 120, 103}, new short[]{86, 52, 86, 85, 102, 102, 120, 136, 136, 137, 120, 104}, new short[]{86, 53, 86, 86, 102, 102, 136, 136, 136, 137, 120, 120}, new short[]{86, 69, 103, 86, 103, 103, 136, 137, 137, 137, 120, 120}, new short[]{87, 69, 86, 69, 86, 102, 119, 120, 120, 137, 104, 103}, new short[]{70, 52, 86, 85, 102, 102, 120, 136, 120, 137, 120, 104}, new short[]{86, 52, 86, 85, 102, 102, 136, 136, 136, 137, 120, 120}, new short[]{86, 69, 102, 86, 102, 103, 136, 136, 137, 137, 120, 120}, new short[]{86, 69, 86, 69, 86, 102, 119, 120, 120, 136, 104, 103}, new short[]{70, 52, 86, 85, 86, 102, 120, 136, 120, 137, 120, 104}, new short[]{86, 52, 86, 85, 102, 102, 136, 136, 136, 137, 120, 104}, new short[]{86, 69, 102, 86, 102, 103, 136, 136, 137, 137, 120, 120}, new short[]{86, 69, 86, 69, 86, 102, 119, 120, 120, 136, 104, 103}, new short[]{70, 52, 86, 69, 86, 102, 120, 136, 120, 137, 120, 103}, new short[]{86, 52, 86, 85, 102, 102, 136, 136, 136, 137, 120, 104}, new short[]{86, 53, 86, 86, 102, 103, 136, 136, 137, 137, 120, 120}, new short[]{86, 69, 86, 69, 86, 102, 119, 120, 120, 136, 104, 103}, new short[]{70, 52, 86, 69, 86, 102, 120, 136, 120, 137, 120, 103}, new short[]{86, 52, 86, 85, 102, 102, 120, 136, 136, 137, 120, 104}, new short[]{86, 53, 86, 86, 102, 103, 136, 136, 136, 137, 120, 120}, new short[]{86, 69, 86, 69, 86, 86, 119, 120, 120, 120, 103, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 120, 120, 137, 120, 103}, new short[]{86, 52, 86, 85, 102, 102, 120, 136, 136, 137, 120, 104}, new short[]{86, 53, 86, 86, 102, 103, 136, 136, 136, 137, 120, 120}, new short[]{86, 69, 86, 69, 86, 86, 119, 120, 120, 120, 103, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 120, 120, 137, 120, 103}, new short[]{86, 52, 86, 85, 102, 102, 120, 136, 136, 137, 120, 104}, new short[]{86, 53, 86, 86, 102, 102, 136, 136, 136, 137, 120, 120}, new short[]{86, 69, 86, 69, 86, 86, 119, 120, 120, 120, 103, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 120, 120, 137, 120, 103}, new short[]{86, 52, 86, 85, 102, 102, 120, 136, 136, 137, 120, 104}, new short[]{86, 53, 86, 86, 102, 102, 136, 136, 136, 137, 120, 120}, new short[]{86, 69, 86, 69, 86, 86, 119, 120, 120, 120, 103, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 120, 120, 137, 120, 103}, new short[]{86, 52, 86, 85, 102, 102, 120, 136, 136, 137, 120, 104}, new short[]{86, 53, 86, 86, 102, 102, 136, 136, 136, 137, 120, 120}, new short[]{86, 69, 86, 69, 85, 86, 119, 120, 120, 120, 103, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 120, 120, 136, 104, 103}, new short[]{70, 52, 86, 85, 102, 102, 120, 136, 120, 137, 120, 104}, new short[]{86, 52, 86, 85, 102, 102, 136, 136, 136, 137, 120, 120}, new short[]{86, 69, 85, 69, 85, 86, 119, 119, 120, 120, 103, 103}, new short[]{69, 52, 86, 69, 86, 102, 119, 120, 120, 136, 104, 103}, new short[]{70, 52, 86, 69, 86, 102, 120, 136, 120, 137, 120, 104}, new short[]{86, 52, 86, 85, 102, 102, 136, 136, 136, 137, 120, 120}, new short[]{86, 69, 85, 69, 85, 86, 119, 119, 120, 120, 103, 103}, new short[]{69, 52, 86, 69, 86, 102, 119, 120, 120, 136, 104, 103}, new short[]{70, 52, 86, 69, 86, 102, 120, 136, 120, 137, 120, 103}, new short[]{86, 52, 86, 85, 102, 102, 120, 136, 136, 137, 120, 104}, new short[]{86, 69, 69, 69, 85, 86, 119, 119, 120, 120, 103, 103}, new short[]{69, 52, 86, 69, 86, 102, 119, 120, 120, 136, 104, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 120, 120, 137, 120, 103}, new short[]{86, 52, 86, 85, 102, 102, 120, 136, 136, 137, 120, 104}, new short[]{86, 53, 69, 69, 85, 86, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 86, 86, 119, 120, 120, 120, 103, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 120, 120, 137, 120, 103}, new short[]{86, 52, 86, 85, 102, 102, 120, 136, 136, 137, 120, 104}, new short[]{86, 53, 69, 69, 85, 86, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 86, 86, 119, 120, 120, 120, 103, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 120, 120, 137, 120, 103}, new short[]{86, 52, 86, 85, 102, 102, 120, 136, 136, 137, 120, 104}, new short[]{86, 53, 69, 69, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 86, 86, 119, 120, 120, 120, 103, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 120, 120, 137, 120, 103}, new short[]{86, 52, 86, 85, 102, 102, 120, 136, 136, 137, 120, 104}, new short[]{86, 53, 69, 69, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 85, 86, 119, 120, 120, 120, 103, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 120, 120, 137, 120, 103}, new short[]{86, 52, 86, 85, 102, 102, 120, 136, 120, 137, 120, 104}, new short[]{86, 53, 69, 68, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 85, 86, 119, 119, 120, 120, 103, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 120, 120, 136, 120, 103}, new short[]{102, 52, 86, 85, 86, 102, 120, 136, 120, 137, 120, 104}, new short[]{86, 53, 69, 68, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 85, 86, 119, 119, 120, 120, 103, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 120, 120, 136, 104, 103}, new short[]{70, 52, 86, 69, 86, 102, 120, 136, 120, 137, 120, 104}, new short[]{86, 52, 69, 68, 85, 85, 103, 119, 119, 120, 103, 103}, new short[]{69, 52, 85, 69, 85, 86, 119, 119, 120, 120, 103, 103}, new short[]{69, 52, 86, 69, 86, 102, 119, 120, 120, 136, 104, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 136, 120, 137, 120, 103}, new short[]{86, 52, 69, 68, 85, 85, 103, 119, 119, 120, 103, 87}, new short[]{69, 52, 69, 69, 85, 86, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 86, 86, 119, 120, 120, 136, 104, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 120, 120, 137, 120, 103}, new short[]{86, 52, 69, 68, 85, 85, 103, 119, 119, 120, 103, 87}, new short[]{69, 36, 69, 69, 85, 86, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 86, 86, 119, 120, 120, 120, 103, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 120, 120, 137, 120, 103}, new short[]{86, 52, 69, 68, 85, 85, 103, 119, 119, 120, 103, 87}, new short[]{69, 36, 69, 69, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 86, 86, 119, 120, 120, 120, 103, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 120, 120, 137, 120, 103}, new short[]{86, 52, 69, 68, 85, 85, 103, 119, 119, 120, 103, 87}, new short[]{69, 36, 69, 69, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 86, 86, 119, 120, 120, 120, 103, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 120, 120, 137, 120, 103}, new short[]{86, 52, 69, 68, 85, 85, 103, 119, 119, 120, 103, 87}, new short[]{69, 36, 69, 69, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 85, 86, 119, 119, 120, 120, 103, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 120, 120, 137, 120, 103}, new short[]{86, 52, 69, 68, 85, 85, 103, 119, 103, 120, 103, 87}, new short[]{69, 36, 69, 68, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 85, 86, 119, 119, 120, 120, 103, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 120, 120, 136, 120, 103}, new short[]{86, 52, 69, 68, 69, 85, 103, 119, 103, 120, 103, 87}, new short[]{69, 36, 69, 68, 85, 85, 103, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 85, 86, 119, 119, 120, 120, 103, 103}, new short[]{70, 52, 86, 69, 86, 102, 119, 120, 120, 136, 104, 103}, new short[]{70, 52, 69, 52, 69, 85, 103, 119, 103, 120, 103, 87}, new short[]{69, 35, 69, 68, 85, 85, 103, 119, 119, 120, 103, 103}, new short[]{69, 52, 85, 69, 85, 86, 119, 119, 120, 120, 103, 103}, new short[]{69, 52, 86, 69, 86, 86, 119, 120, 120, 136, 104, 103}, new short[]{70, 52, 69, 52, 69, 85, 102, 103, 103, 120, 103, 87}, new short[]{69, 35, 69, 68, 85, 85, 103, 119, 119, 120, 103, 87}, new short[]{69, 52, 69, 69, 85, 86, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 86, 86, 119, 120, 120, 136, 104, 103}, new short[]{70, 52, 69, 52, 69, 85, 102, 103, 103, 120, 103, 86}, new short[]{69, 35, 69, 68, 85, 85, 103, 119, 119, 120, 103, 87}, new short[]{69, 36, 69, 69, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 86, 86, 119, 120, 120, 120, 103, 103}, new short[]{70, 52, 69, 52, 69, 85, 102, 103, 103, 120, 103, 86}, new short[]{69, 35, 69, 68, 85, 85, 103, 119, 119, 120, 103, 87}, new short[]{69, 36, 69, 69, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 86, 86, 119, 120, 120, 120, 103, 103}, new short[]{70, 52, 69, 52, 69, 85, 102, 103, 103, 120, 103, 86}, new short[]{69, 35, 69, 68, 85, 85, 103, 119, 119, 120, 103, 87}, new short[]{69, 36, 69, 69, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 85, 86, 119, 119, 120, 120, 103, 103}, new short[]{70, 52, 69, 52, 69, 85, 102, 103, 103, 120, 103, 86}, new short[]{69, 35, 69, 68, 85, 85, 103, 119, 119, 120, 103, 87}, new short[]{69, 36, 69, 69, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 85, 86, 119, 119, 120, 120, 103, 103}, new short[]{70, 52, 69, 52, 69, 85, 102, 103, 103, 120, 103, 86}, new short[]{69, 35, 69, 68, 69, 85, 103, 119, 103, 120, 103, 87}, new short[]{69, 36, 69, 68, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 85, 86, 119, 119, 120, 120, 103, 103}, new short[]{70, 52, 69, 52, 69, 85, 102, 103, 103, 119, 103, 86}, new short[]{69, 35, 69, 52, 69, 85, 103, 119, 103, 120, 103, 87}, new short[]{69, 36, 69, 68, 85, 85, 103, 119, 119, 120, 103, 103}, new short[]{69, 52, 86, 69, 85, 86, 119, 119, 120, 120, 103, 103}, new short[]{70, 52, 69, 52, 69, 69, 102, 103, 103, 119, 87, 86}, new short[]{53, 35, 69, 52, 69, 85, 102, 103, 103, 120, 103, 87}, new short[]{69, 35, 69, 68, 85, 85, 103, 119, 119, 120, 103, 103}, new short[]{69, 52, 85, 69, 85, 86, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 69, 52, 69, 69, 102, 103, 103, 119, 87, 86}, new short[]{53, 35, 69, 52, 69, 85, 102, 103, 103, 120, 103, 87}, new short[]{69, 35, 69, 68, 85, 85, 103, 119, 119, 120, 103, 103}, new short[]{69, 52, 69, 69, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 69, 52, 69, 69, 102, 103, 103, 119, 87, 86}, new short[]{53, 35, 69, 52, 69, 85, 102, 103, 103, 120, 103, 86}, new short[]{69, 35, 69, 68, 85, 85, 103, 119, 119, 120, 103, 87}, new short[]{69, 52, 69, 69, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 69, 52, 69, 69, 102, 103, 103, 103, 86, 86}, new short[]{53, 35, 69, 52, 69, 85, 102, 103, 103, 120, 103, 86}, new short[]{69, 35, 69, 68, 85, 85, 103, 119, 119, 120, 103, 87}, new short[]{69, 36, 69, 69, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 69, 52, 69, 69, 102, 103, 103, 103, 86, 86}, new short[]{53, 35, 69, 52, 69, 85, 102, 103, 103, 120, 103, 86}, new short[]{69, 35, 69, 68, 85, 85, 103, 119, 119, 120, 103, 87}, new short[]{69, 36, 69, 69, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 69, 52, 68, 69, 102, 102, 103, 103, 86, 86}, new short[]{53, 35, 69, 52, 69, 85, 102, 103, 103, 120, 103, 86}, new short[]{69, 35, 69, 68, 69, 85, 103, 119, 103, 120, 103, 87}, new short[]{69, 36, 85, 69, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 52, 69, 52, 68, 69, 102, 102, 103, 103, 86, 86}, new short[]{53, 35, 69, 52, 69, 85, 102, 103, 103, 119, 103, 86}, new short[]{69, 35, 69, 68, 69, 85, 103, 119, 103, 120, 103, 87}, new short[]{69, 36, 69, 68, 85, 85, 103, 119, 119, 120, 103, 103}, new short[]{69, 52, 69, 52, 68, 69, 102, 102, 103, 103, 86, 86}, new short[]{53, 35, 69, 52, 69, 85, 102, 103, 103, 119, 103, 86}, new short[]{69, 35, 69, 52, 69, 85, 102, 119, 103, 120, 103, 87}, new short[]{69, 36, 69, 68, 85, 85, 103, 119, 119, 120, 103, 103}, new short[]{69, 52, 69, 52, 68, 69, 102, 102, 103, 103, 86, 86}, new short[]{53, 35, 69, 52, 85, 69, 102, 103, 103, 119, 87, 86}, new short[]{69, 35, 69, 52, 69, 85, 102, 103, 103, 120, 103, 87}, new short[]{69, 36, 69, 68, 85, 85, 103, 119, 119, 120, 103, 103}, new short[]{69, 52, 68, 52, 68, 69, 102, 102, 102, 103, 86, 86}, new short[]{52, 35, 69, 52, 69, 69, 102, 103, 103, 119, 87, 86}, new short[]{53, 35, 69, 52, 69, 85, 102, 103, 103, 120, 103, 87}, new short[]{69, 35, 69, 68, 85, 85, 103, 119, 119, 120, 103, 103}, new short[]{69, 52, 52, 52, 68, 68, 102, 102, 102, 103, 86, 86}, new short[]{52, 35, 69, 52, 69, 69, 102, 103, 103, 119, 87, 86}, new short[]{53, 35, 69, 52, 69, 85, 102, 103, 103, 120, 103, 86}, new short[]{69, 35, 69, 68, 85, 85, 103, 119, 119, 120, 103, 87}, new short[]{52, 35, 69, 52, 69, 69, 102, 103, 103, 103, 86, 86}, new short[]{69, 35, 69, 68, 85, 85, 103, 119, 119, 120, 103, 87}, new short[]{69, 36, 52, 52, 68, 68, 102, 102, 102, 103, 86, 86}, new short[]{52, 35, 69, 52, 69, 69, 102, 102, 103, 103, 86, 86}, new short[]{53, 35, 69, 52, 69, 85, 102, 103, 103, 120, 103, 86}, new short[]{69, 35, 69, 68, 85, 85, 103, 119, 119, 120, 103, 87}, new short[]{69, 36, 69, 69, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 36, 69, 69, 85, 85, 119, 119, 119, 120, 103, 103}, new short[]{69, 36, 69, 69, 85, 85, 119, 119, 119, 120, 103, 103}};
    }

    public static final class LunarDate {
        public byte mDay;
        byte mDiZhi;
        public boolean mIsLeap;
        public byte mMonth;
        byte mTianGan;
        public int mYear;

        public boolean isBeforeMin() {
            return getComparisonValue(this) < 19010101;
        }

        public boolean isAfterMax() {
            return getComparisonValue(this) > 20991120;
        }

        private int getComparisonValue(LunarDate lunarDate) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(lunarDate.mYear));
            if (lunarDate.mMonth < 10) {
                sb.append("0");
            }
            sb.append(String.valueOf((int) lunarDate.mMonth));
            if (lunarDate.mDay < 10) {
                sb.append("0");
            }
            sb.append(String.valueOf((int) lunarDate.mDay));
            return Integer.parseInt(sb.toString());
        }

        public void setToMin() {
            this.mTianGan = (byte) 8;
            this.mDiZhi = (byte) 2;
            this.mIsLeap = false;
            this.mYear = LunarUtils.MIN_LUNAR_YEAR;
            this.mMonth = (byte) 1;
            this.mDay = (byte) 1;
        }

        public void setToMax() {
            this.mTianGan = (byte) 6;
            this.mDiZhi = (byte) 8;
            this.mIsLeap = false;
            this.mYear = LunarUtils.MAX_LUNAR_YEAR;
            this.mMonth = Ascii.VT;
            this.mDay = Ascii.DC4;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("The lunarDate info is: ").append("mYear = ").append(this.mYear).append(", mMonth = ").append((int) this.mMonth).append(", mDay = ").append((int) this.mDay).append(", mIsLeap = ").append(this.mIsLeap).append(", mTianGan = ").append((int) this.mTianGan).append(", mDiZhi = ").append((int) this.mDiZhi);
            return sb.toString();
        }
    }

    public static final class LunarFestival {
        private LunarJieRi mJieRi;
        private byte mLunarDay;
        private byte mLunarMonth;

        LunarFestival(LunarJieRi lunarJieRi, int i, int i2) {
            this.mJieRi = lunarJieRi;
            this.mLunarMonth = (byte) i;
            this.mLunarDay = (byte) i2;
        }
    }

    public static final class SolarFestival {
        private SolarJieRi mJieRi;
        private byte mSolarDay;
        private byte mSolarMonth;

        SolarFestival(SolarJieRi solarJieRi, int i, int i2) {
            this.mJieRi = solarJieRi;
            this.mSolarMonth = (byte) i;
            this.mSolarDay = (byte) i2;
        }
    }

    public static final class WeekFestival {
        private WeekJieRi mJieRi;
        private byte mSolarMonth;
        private byte mSolarWeekDay;
        private byte mSolarWeekNum;

        WeekFestival(WeekJieRi weekJieRi, int i, int i2, int i3) {
            this.mJieRi = weekJieRi;
            this.mSolarMonth = (byte) i;
            this.mSolarWeekDay = (byte) i3;
            this.mSolarWeekNum = (byte) i2;
        }
    }

    private static final class LunarCalendarData {
        private char mDaysOfMonths;
        private byte mDiZhi;
        private boolean mExtendInLeapMonth;
        private byte mTianGan;
        private byte mYearStartDay;

        LunarCalendarData(int i, int i2, int i3, boolean z, int i4) {
            this.mTianGan = (byte) i;
            this.mDiZhi = (byte) i2;
            this.mYearStartDay = (byte) i3;
            this.mExtendInLeapMonth = z;
            this.mDaysOfMonths = (char) i4;
        }
    }

    public static LunarDate convertSolarDateToLunarDate(Date date) {
        if (!isValid(date)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Log.e(TAG, "The solarDate is inValid: Year = " + calendar.get(1) + ", Month = " + calendar.get(2) + ", Day = " + calendar.get(2));
            return null;
        }
        return getLunarDateFromTable(date, null);
    }

    private static LunarDate getLunarDateFromTable(Date date, String str) {
        boolean z;
        byte b;
        Calendar calendar = Calendar.getInstance();
        if (str != null) {
            calendar = Calendar.getInstance(TimeZone.getTimeZone(str));
        }
        calendar.setTime(date);
        int i = calendar.get(2) + 1;
        long j = 0;
        for (int i2 = 1; i2 < i; i2++) {
            if (i2 == 2) {
                b = isLeapYear(calendar.get(1)) ? Ascii.GS : Ascii.FS;
            } else {
                b = sDaysOfMonth[i2 - 1];
            }
            j += (long) b;
        }
        long j2 = j + ((long) calendar.get(5)) + 1;
        LunarDate lunarDate = new LunarDate();
        LunarCalendarData[] lunarCalendarDataArr = sLunarCalendarData;
        LunarCalendarData lunarCalendarData = lunarCalendarDataArr[calendar.get(1) - 1901];
        if (j2 <= lunarCalendarData.mYearStartDay) {
            long daysOfLunarMonth = ((long) lunarCalendarData.mYearStartDay) - j2;
            LunarCalendarData lunarCalendarData2 = lunarCalendarDataArr[(calendar.get(1) - 1901) - 1];
            lunarDate.mTianGan = lunarCalendarData2.mTianGan;
            lunarDate.mDiZhi = lunarCalendarData2.mDiZhi;
            lunarDate.mYear = calendar.get(1) - 1;
            byte b2 = (byte) (lunarCalendarData2.mDaysOfMonths >> '\f');
            if (getDaysOfLunarMonth(calendar.get(1) - 1, 12) > daysOfLunarMonth) {
                lunarDate.mIsLeap = false;
                lunarDate.mMonth = Ascii.FF;
                lunarDate.mDay = (byte) (((long) getDaysOfLunarMonth(calendar.get(1) - 1, 12)) - daysOfLunarMonth);
                z = true;
            } else {
                daysOfLunarMonth -= (long) getDaysOfLunarMonth(calendar.get(1) - 1, 12);
                z = false;
            }
            if (!z) {
                if (b2 == 11) {
                    if (getDaysOfLeapMonth(calendar.get(1) - 1) > daysOfLunarMonth) {
                        lunarDate.mIsLeap = true;
                        lunarDate.mMonth = Ascii.VT;
                        lunarDate.mDay = (byte) (((long) getDaysOfLeapMonth(calendar.get(1) - 1)) - daysOfLunarMonth);
                    }
                } else if (getDaysOfLunarMonth(calendar.get(1) - 1, 11) > daysOfLunarMonth) {
                    lunarDate.mIsLeap = false;
                    lunarDate.mMonth = Ascii.VT;
                    lunarDate.mDay = (byte) (((long) getDaysOfLunarMonth(calendar.get(1) - 1, 11)) - daysOfLunarMonth);
                }
            }
        } else {
            long j3 = j2 - ((long) lunarCalendarData.mYearStartDay);
            lunarDate.mTianGan = lunarCalendarData.mTianGan;
            lunarDate.mDiZhi = lunarCalendarData.mDiZhi;
            lunarDate.mYear = calendar.get(1);
            byte b3 = (byte) (lunarCalendarData.mDaysOfMonths >> '\f');
            int daysOfLunarMonth2 = getDaysOfLunarMonth(calendar.get(1), 1);
            byte b4 = 1;
            boolean z2 = false;
            while (!z2) {
                long j4 = daysOfLunarMonth2;
                if (j3 <= j4) {
                    lunarDate.mMonth = b4;
                    lunarDate.mDay = (byte) j3;
                    lunarDate.mIsLeap = false;
                    z2 = true;
                } else {
                    j3 -= j4;
                    if (b3 == b4) {
                        long daysOfLeapMonth = getDaysOfLeapMonth(calendar.get(1));
                        if (j3 > daysOfLeapMonth) {
                            j3 -= daysOfLeapMonth;
                        } else {
                            lunarDate.mMonth = b4;
                            lunarDate.mDay = (byte) j3;
                            lunarDate.mIsLeap = true;
                            z2 = true;
                        }
                    }
                    if (!z2) {
                        b4 = (byte) (b4 + 1);
                        daysOfLunarMonth2 = getDaysOfLunarMonth(calendar.get(1), b4);
                    }
                }
            }
        }
        return lunarDate;
    }

    private static boolean isLeapYear(int i) {
        return i % 4 == 0 && (i % 100 != 0 || i % 400 == 0);
    }

    public static int getDaysOfLunarMonth(int i, int i2) {
        int i3;
        byte b;
        char c = sLunarCalendarData[i - 1901].mDaysOfMonths;
        switch (i2) {
            case 1:
                i3 = (c & 2048) >> 11;
                b = (byte) i3;
                break;
            case 2:
                i3 = (c & 1024) >> 10;
                b = (byte) i3;
                break;
            case 3:
                i3 = (c & 512) >> 9;
                b = (byte) i3;
                break;
            case 4:
                i3 = (c & 256) >> 8;
                b = (byte) i3;
                break;
            case 5:
                i3 = (c & 128) >> 7;
                b = (byte) i3;
                break;
            case 6:
                i3 = (c & '@') >> 6;
                b = (byte) i3;
                break;
            case 7:
                i3 = (c & ' ') >> 5;
                b = (byte) i3;
                break;
            case 8:
                i3 = (c & 16) >> 4;
                b = (byte) i3;
                break;
            case 9:
                i3 = (c & '\b') >> 3;
                b = (byte) i3;
                break;
            case 10:
                i3 = (c & 4) >> 2;
                b = (byte) i3;
                break;
            case 11:
                i3 = (c & 2) >> 1;
                b = (byte) i3;
                break;
            case 12:
                i3 = c & 1;
                b = (byte) i3;
                break;
            default:
                b = 0;
                break;
        }
        return b + Ascii.GS;
    }

    public static int getDaysOfLeapMonth(int i) {
        return sLunarCalendarData[i + (-1901)].mExtendInLeapMonth ? 30 : 29;
    }

    public static void getMatchedSolarDates(LunarDate lunarDate, ArrayList<Date> arrayList) {
        byte requestType = getRequestType(lunarDate);
        short s = 1;
        if (requestType == 1) {
            s = 60;
        } else if (requestType == 2) {
            s = 10;
        } else if (requestType == 3) {
            s = 12;
        } else if (requestType != 4) {
            s = 0;
        }
        getSolarDatesList(lunarDate, arrayList, s);
    }

    private static byte getRequestType(LunarDate lunarDate) {
        byte b = (lunarDate.mTianGan == 0 || lunarDate.mDiZhi == 0) ? (byte) 0 : (byte) 1;
        if (lunarDate.mTianGan != 0 && lunarDate.mDiZhi == 0) {
            b = 2;
        }
        if (lunarDate.mTianGan == 0 && lunarDate.mDiZhi != 0) {
            b = 3;
        }
        if (lunarDate.mTianGan == 0 && lunarDate.mDiZhi == 0) {
            return (byte) 4;
        }
        return b;
    }

    public static void getSolarDatesList(LunarDate lunarDate, ArrayList<Date> arrayList, short s) {
        int i = MIN_LUNAR_YEAR;
        while (!compareLunarDate(lunarDate, i, s)) {
            i++;
            if (i > 2099) {
                return;
            }
        }
        arrayList.clear();
        while (i <= 2099) {
            if (lunarDate.mIsLeap) {
                if (lunarDate.mDay <= getDaysOfLeapMonth(i) && lunarDate.mMonth == (sLunarCalendarData[i - 1901].mDaysOfMonths >> '\f')) {
                    arrayList.add(getSolarDateFromLunarDate(lunarDate, i));
                }
            } else if (lunarDate.mDay <= getDaysOfLunarMonth(i, lunarDate.mMonth)) {
                arrayList.add(getSolarDateFromLunarDate(lunarDate, i));
            }
            i += s;
        }
    }

    private static boolean compareLunarDate(LunarDate lunarDate, int i, short s) {
        LunarCalendarData lunarCalendarData = sLunarCalendarData[i - 1901];
        if (s == 60) {
            return lunarDate.mTianGan == lunarCalendarData.mTianGan && lunarDate.mDiZhi == lunarCalendarData.mDiZhi;
        }
        if (s == 10) {
            return lunarDate.mTianGan == lunarCalendarData.mTianGan;
        }
        if (s == 12) {
            return lunarDate.mDiZhi == lunarCalendarData.mDiZhi;
        }
        return s == 1;
    }

    private static Date getSolarDateFromLunarDate(LunarDate lunarDate, int i) {
        LunarCalendarData lunarCalendarData = sLunarCalendarData[i - 1901];
        int daysOfLunarMonth = 0;
        int i2 = 1;
        while (i2 < lunarDate.mMonth) {
            daysOfLunarMonth += getDaysOfLunarMonth(i, i2);
            if ((lunarCalendarData.mDaysOfMonths >> '\f') == i2) {
                daysOfLunarMonth += getDaysOfLeapMonth(i);
            }
            i2++;
        }
        if (lunarDate.mIsLeap) {
            daysOfLunarMonth += getDaysOfLunarMonth(i, i2);
        }
        return getDateFromDays(daysOfLunarMonth + lunarDate.mDay + (lunarCalendarData.mYearStartDay - 1), i);
    }

    private static Date getDateFromDays(int i, int i2) {
        byte b;
        loop0: while (true) {
            b = 1;
            do {
                if (b == 2) {
                    if (i <= (isLeapYear(i2) ? 29 : 28)) {
                        break loop0;
                    }
                    i -= isLeapYear(i2) ? 29 : 28;
                    b = (byte) (b + 1);
                } else {
                    byte[] bArr = sDaysOfMonth;
                    int i3 = b - 1;
                    if (i <= bArr[i3]) {
                        break loop0;
                    }
                    i -= bArr[i3];
                    b = (byte) (b + 1);
                }
            } while (b <= 12);
            i2++;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(i2, b - 1, i);
        return calendar.getTime();
    }

    private static byte getSolarTermFromSolarDate(Date date) {
        short s;
        if (!isValid(date)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Log.e(TAG, "The solarDate is inValid: Year = " + calendar.get(1) + ", Month = " + calendar.get(2) + ", Day = " + calendar.get(2));
            return (byte) 0;
        }
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date);
        short s2 = sSolarTermDataTable[calendar2.get(1) - 1901][calendar2.get(2)];
        byte b = (byte) ((calendar2.get(2) * 2) + 1);
        if (calendar2.get(5) <= 15) {
            s = (short) (s2 & 15);
        } else {
            s = (short) (((short) (((short) (s2 & 240)) >> 4)) + 16);
            b = (byte) (b + 1);
        }
        if (s == calendar2.get(5)) {
            return b;
        }
        return (byte) 0;
    }

    private static LunarJieRi getLunarFestivalFromSolarDate(Date date) {
        if (!isValid(date)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Log.e(TAG, "The solarDate is inValid: Year = " + calendar.get(1) + ", Month = " + calendar.get(2) + ", Day = " + calendar.get(2));
            return LunarJieRi.LJR_NONE;
        }
        LunarDate lunarDateConvertSolarDateToLunarDate = convertSolarDateToLunarDate(date);
        if (lunarDateConvertSolarDateToLunarDate != null) {
            if (lunarDateConvertSolarDateToLunarDate.mMonth == 12 && lunarDateConvertSolarDateToLunarDate.mDay == getDaysOfLunarMonth(lunarDateConvertSolarDateToLunarDate.mYear, lunarDateConvertSolarDateToLunarDate.mMonth)) {
                return LunarJieRi.LJR_CHUXI;
            }
            for (byte b = 0; b < 10; b = (byte) (b + 1)) {
                byte b2 = lunarDateConvertSolarDateToLunarDate.mMonth;
                LunarFestival[] lunarFestivalArr = sLunarFestivalTable;
                if (b2 == lunarFestivalArr[b].mLunarMonth && lunarDateConvertSolarDateToLunarDate.mDay == lunarFestivalArr[b].mLunarDay && !lunarDateConvertSolarDateToLunarDate.mIsLeap) {
                    return lunarFestivalArr[b].mJieRi;
                }
            }
        }
        return LunarJieRi.LJR_NONE;
    }

    private static SolarJieRi getSolarFestivalFromSolarDate(Date date) {
        if (!isValid(date)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Log.e(TAG, "The solarDate is inValid: Year = " + calendar.get(1) + ", Month = " + calendar.get(2) + ", Day = " + calendar.get(2));
            return SolarJieRi.SJR_NONE;
        }
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date);
        int i = calendar2.get(2) + 1;
        int i2 = calendar2.get(5);
        for (byte b = 0; b < 23; b = (byte) (b + 1)) {
            SolarFestival[] solarFestivalArr = sSolarFestivalTable;
            if (i == solarFestivalArr[b].mSolarMonth && i2 == solarFestivalArr[b].mSolarDay) {
                return solarFestivalArr[b].mJieRi;
            }
        }
        return SolarJieRi.SJR_NONE;
    }

    private static WeekJieRi getWeekFestivalFromSolarDate(Date date) {
        if (!isValid(date)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Log.e(TAG, "The solarDate is inValid: Year = " + calendar.get(1) + ", Month = " + calendar.get(2) + ", Day = " + calendar.get(2));
            return WeekJieRi.WJR_NONE;
        }
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date);
        int i = calendar2.get(2) + 1;
        int i2 = calendar2.get(7);
        int i3 = calendar2.get(8);
        for (byte b = 0; b < 3; b = (byte) (b + 1)) {
            WeekFestival[] weekFestivalArr = sWeekFestivalTable;
            if (i == weekFestivalArr[b].mSolarMonth && i2 == weekFestivalArr[b].mSolarWeekDay && i3 == weekFestivalArr[b].mSolarWeekNum) {
                return weekFestivalArr[b].mJieRi;
            }
        }
        return WeekJieRi.WJR_NONE;
    }

    private static Animal getAnimalFromLunarDate(LunarDate lunarDate) {
        Animal animal = Animal.SX_None;
        if (lunarDate == null) {
            return animal;
        }
        switch (lunarDate.mDiZhi) {
            case 1:
                return Animal.SX_Rat;
            case 2:
                return Animal.SX_Ox;
            case 3:
                return Animal.SX_Tiger;
            case 4:
                return Animal.SX_Rabbit;
            case 5:
                return Animal.SX_Dragon;
            case 6:
                return Animal.SX_Snake;
            case 7:
                return Animal.SX_Horse;
            case 8:
                return Animal.SX_Sheep;
            case 9:
                return Animal.SX_Monkey;
            case 10:
                return Animal.SX_Rooster;
            case 11:
                return Animal.SX_Dog;
            case 12:
                return Animal.SX_Pig;
            default:
                return animal;
        }
    }

    public static String getLunarYearTianGanString(LunarDate lunarDate) {
        return lunarDate.mTianGan != 0 ? sTianGanStrings[lunarDate.mTianGan] : "";
    }

    public static String getLunarYearDiZhiString(LunarDate lunarDate) {
        return lunarDate.mDiZhi != 0 ? sDiZhiStrings[lunarDate.mDiZhi] : "";
    }

    public static String getSolarTermString(Date date) {
        byte solarTermFromSolarDate = getSolarTermFromSolarDate(date);
        return solarTermFromSolarDate != 0 ? sJieQiStrings[solarTermFromSolarDate] : "";
    }

    public static String getLunarMonthString(LunarDate lunarDate) {
        return (lunarDate.mIsLeap ? sLunarMonthStrings[0] : "") + sLunarMonthStrings[lunarDate.mMonth];
    }

    public static String getLunarDayString(LunarDate lunarDate) {
        return lunarDate.mDay > (lunarDate.mIsLeap ? getDaysOfLeapMonth(lunarDate.mYear) : getDaysOfLunarMonth(lunarDate.mYear, lunarDate.mMonth)) ? "" : sLunarDayStrings[lunarDate.mDay];
    }

    public static String getSolarFestivalString(Date date) {
        SolarJieRi solarFestivalFromSolarDate = getSolarFestivalFromSolarDate(date);
        return solarFestivalFromSolarDate != SolarJieRi.SJR_NONE ? sSolarJieRiStringsMap.get(solarFestivalFromSolarDate) : "";
    }

    public static String getLunarFestivalString(Date date) {
        LunarJieRi lunarFestivalFromSolarDate = getLunarFestivalFromSolarDate(date);
        return lunarFestivalFromSolarDate != LunarJieRi.LJR_NONE ? sLunarJieRiStringsMap.get(lunarFestivalFromSolarDate) : "";
    }

    public static String getWeekFestivalString(Date date) {
        WeekJieRi weekFestivalFromSolarDate = getWeekFestivalFromSolarDate(date);
        return weekFestivalFromSolarDate != WeekJieRi.WJR_NONE ? sWeekJieRiStringsMap.get(weekFestivalFromSolarDate) : "";
    }

    public static String getAnimalString(LunarDate lunarDate) {
        Animal animalFromLunarDate = getAnimalFromLunarDate(lunarDate);
        return animalFromLunarDate != Animal.SX_None ? sAnimalStringsMap.get(animalFromLunarDate) : "";
    }

    public static ArrayList<String> getCalendarMonthGridString(Date date) {
        ArrayList<String> arrayList = new ArrayList<>();
        LunarDate lunarDateConvertSolarDateToLunarDate = convertSolarDateToLunarDate(date);
        if (lunarDateConvertSolarDateToLunarDate == null) {
            return arrayList;
        }
        if (getLunarFestivalFromSolarDate(date) != LunarJieRi.LJR_NONE) {
            arrayList.add(getLunarFestivalString(date));
        }
        if (getSolarFestivalFromSolarDate(date) != SolarJieRi.SJR_NONE) {
            arrayList.add(getSolarFestivalString(date));
        }
        if (getWeekFestivalFromSolarDate(date) != WeekJieRi.WJR_NONE) {
            arrayList.add(getWeekFestivalString(date));
        }
        if (getSolarTermFromSolarDate(date) != 0) {
            arrayList.add(getSolarTermString(date));
        }
        if (lunarDateConvertSolarDateToLunarDate.mDay == 1) {
            arrayList.add(getLunarMonthString(lunarDateConvertSolarDateToLunarDate));
        }
        if (arrayList.size() == 0) {
            arrayList.add(getLunarDayString(lunarDateConvertSolarDateToLunarDate));
        }
        return arrayList;
    }

    public static boolean isValid(LunarDate lunarDate) {
        int daysOfLunarMonth;
        if (lunarDate.mYear >= 1901 && lunarDate.mYear <= 2099) {
            if (lunarDate.mYear == 2099) {
                if (lunarDate.mMonth > 11) {
                    return false;
                }
                if (lunarDate.mMonth == 11 && lunarDate.mDay > 20) {
                    return false;
                }
            }
            if (lunarDate.mMonth >= 1 && lunarDate.mMonth <= 12 && ((!lunarDate.mIsLeap || lunarDate.mMonth == getLeapMonth(lunarDate.mYear)) && lunarDate.mDay >= 1)) {
                byte b = lunarDate.mDay;
                if (lunarDate.mIsLeap) {
                    daysOfLunarMonth = getDaysOfLeapMonth(lunarDate.mYear);
                } else {
                    daysOfLunarMonth = getDaysOfLunarMonth(lunarDate.mYear, lunarDate.mMonth);
                }
                if (b <= daysOfLunarMonth) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isValid(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(MIN_LUNAR_YEAR, 1, 19, 0, 0, 0);
        long timeInMillis = calendar.getTimeInMillis();
        calendar.set(2100, 0, 1, 0, 0, 0);
        long timeInMillis2 = calendar.getTimeInMillis();
        calendar.setTime(date);
        long timeInMillis3 = calendar.getTimeInMillis();
        return timeInMillis3 >= timeInMillis && timeInMillis3 <= timeInMillis2;
    }

    public static boolean isValid(Date date, String str) {
        Calendar calendar = Calendar.getInstance();
        if (str != null) {
            calendar = Calendar.getInstance(TimeZone.getTimeZone(str));
        }
        Calendar calendar2 = calendar;
        calendar2.set(MIN_LUNAR_YEAR, 1, 19, 0, 0, 0);
        long timeInMillis = calendar.getTimeInMillis();
        calendar2.set(2100, 0, 1, 0, 0, 0);
        long timeInMillis2 = calendar.getTimeInMillis();
        calendar.setTime(date);
        long timeInMillis3 = calendar.getTimeInMillis();
        return timeInMillis3 >= timeInMillis && timeInMillis3 <= timeInMillis2;
    }

    public static Date convertLunarDateToSolarDate(LunarDate lunarDate) {
        if (!isValid(lunarDate)) {
            Log.e(TAG, "The lunarDate is inValid. " + lunarDate.toString());
            return null;
        }
        int daysOfLunarMonth = 0;
        LunarCalendarData lunarCalendarData = sLunarCalendarData[lunarDate.mYear - 1901];
        int i = 1;
        while (i < lunarDate.mMonth) {
            daysOfLunarMonth += getDaysOfLunarMonth(lunarDate.mYear, i);
            if ((lunarCalendarData.mDaysOfMonths >> '\f') == i) {
                daysOfLunarMonth += getDaysOfLeapMonth(lunarDate.mYear);
            }
            i++;
        }
        if (lunarDate.mIsLeap) {
            daysOfLunarMonth += getDaysOfLunarMonth(lunarDate.mYear, i);
        }
        return getDateFromDays(daysOfLunarMonth + lunarDate.mDay + (lunarCalendarData.mYearStartDay - 1), lunarDate.mYear);
    }

    private static int getCyclicalMonth(Date date) {
        if (!isValid(date)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Log.e(TAG, "The solarDate is inValid: Year = " + calendar.get(1) + ", Month = " + calendar.get(2) + ", Day = " + calendar.get(2));
            return -1;
        }
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date);
        if (calendar2.get(5) < ((short) (sSolarTermDataTable[calendar2.get(1) - 1901][calendar2.get(2)] & 15))) {
            return ((((calendar2.get(1) - 1901) * 12) + calendar2.get(2)) + 24) % 60;
        }
        return ((((calendar2.get(1) - 1901) * 12) + calendar2.get(2)) + 25) % 60;
    }

    private static long UTC(int i, int i2, int i3, int i4, int i5, int i6) {
        long timeInMillis;
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        synchronized (calendar) {
            calendar.clear();
            calendar.set(i, i2, i3, i4, i5, i6);
            timeInMillis = calendar.getTimeInMillis();
        }
        return timeInMillis;
    }

    private static int getCyclicalDay(Date date) {
        if (!isValid(date)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Log.e(TAG, "The solarDate is inValid: Year = " + calendar.get(1) + ", Month = " + calendar.get(2) + ", Day = " + calendar.get(2));
            return -1;
        }
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date);
        return ((int) (((UTC(calendar2.get(1), calendar2.get(2), calendar2.get(5), 0, 0, 0) / 86400000) + 25567) + 10)) % 60;
    }

    public static String getMonthTianGanString(Date date) {
        return sTianGanStrings[(getCyclicalMonth(date) % 10) + 1];
    }

    public static String getMonthDiZhiString(Date date) {
        return sDiZhiStrings[(getCyclicalMonth(date) % 12) + 1];
    }

    public static String getDayTianGanString(Date date) {
        return sTianGanStrings[(getCyclicalDay(date) % 10) + 1];
    }

    public static String getDayDiZhiString(Date date) {
        return sDiZhiStrings[(getCyclicalDay(date) % 12) + 1];
    }

    public static String getMonthCyclicalString(Date date) {
        return getMonthTianGanString(date) + getMonthDiZhiString(date);
    }

    public static String getDayCyclicalString(Date date) {
        return getDayTianGanString(date) + getDayDiZhiString(date);
    }

    public static int getLeapMonth(int i) {
        return sLunarCalendarData[i - 1901].mDaysOfMonths >> '\f';
    }

    public static String getLunarYearInfo(Date date) {
        LunarDate lunarDateConvertSolarDateToLunarDate = convertSolarDateToLunarDate(date);
        return lunarDateConvertSolarDateToLunarDate != null ? getLunarYearTianGanString(lunarDateConvertSolarDateToLunarDate) + getLunarYearDiZhiString(lunarDateConvertSolarDateToLunarDate) + getAnimalString(lunarDateConvertSolarDateToLunarDate) + "年" : "";
    }

    public static LunarDate convertSolarDateToLunarDate(Date date, String str) {
        if (!isValid(date, str)) {
            Calendar calendar = Calendar.getInstance();
            if (str != null) {
                calendar = Calendar.getInstance(TimeZone.getTimeZone(str));
            }
            calendar.setTime(date);
            Log.e(TAG, "The solarDate is inValid: Year = " + calendar.get(1) + ", Month = " + calendar.get(2) + ", Day = " + calendar.get(2));
            return null;
        }
        return getLunarDateFromTable(date, str);
    }
}
