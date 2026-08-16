package com.sonymobile.calendar.holiday;

import android.content.Context;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class ChinaHolidaysManager {
    public static final String FILE_VERSION_JSON_NAME = "version.json";
    private static ChinaHolidaysManager sChinaHolidaysManager;
    ArrayList<ChinaHoliday> mChinaHolidayList = new ArrayList<>();
    private Context mContext;

    private ChinaHolidaysManager(Context context) {
        this.mContext = null;
        this.mContext = context;
    }

    public static ChinaHolidaysManager getInstance(Context context) {
        if (sChinaHolidaysManager == null) {
            sChinaHolidaysManager = new ChinaHolidaysManager(context);
        }
        return sChinaHolidaysManager;
    }

    /* JADX WARN: Code duplicated, block: B:35:0x00b9 A[Catch: IOException -> 0x00cf, TRY_ENTER, TryCatch #5 {IOException -> 0x00cf, blocks: (B:15:0x0096, B:35:0x00b9, B:37:0x00be, B:44:0x00cb, B:48:0x00d3), top: B:64:0x0034 }] */
    /* JADX WARN: Code duplicated, block: B:37:0x00be A[Catch: IOException -> 0x00cf, TRY_LEAVE, TryCatch #5 {IOException -> 0x00cf, blocks: (B:15:0x0096, B:35:0x00b9, B:37:0x00be, B:44:0x00cb, B:48:0x00d3), top: B:64:0x0034 }] */
    /* JADX WARN: Code duplicated, block: B:44:0x00cb A[Catch: IOException -> 0x00cf, TRY_ENTER, TryCatch #5 {IOException -> 0x00cf, blocks: (B:15:0x0096, B:35:0x00b9, B:37:0x00be, B:44:0x00cb, B:48:0x00d3), top: B:64:0x0034 }] */
    /* JADX WARN: Code duplicated, block: B:48:0x00d3 A[Catch: IOException -> 0x00cf, TRY_LEAVE, TryCatch #5 {IOException -> 0x00cf, blocks: (B:15:0x0096, B:35:0x00b9, B:37:0x00be, B:44:0x00cb, B:48:0x00d3), top: B:64:0x0034 }] */
    /* JADX WARN: Code duplicated, block: B:57:0x00e6 A[Catch: IOException -> 0x00e2, TRY_LEAVE, TryCatch #3 {IOException -> 0x00e2, blocks: (B:53:0x00de, B:57:0x00e6), top: B:62:0x00de }] */
    /* JADX WARN: Code duplicated, block: B:73:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:74:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v1, types: [java.io.File] */
    /* JADX WARN: Type inference failed for: r2v11 */
    /* JADX WARN: Type inference failed for: r2v2, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r2v6 */
    public void setup() {
        this.mChinaHolidayList.clear();
        File file = new File(this.mContext.getFilesDir().getAbsolutePath() + "/" + FILE_VERSION_JSON_NAME);
        if (!file.exists()) {
            return;
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            try {
                StringBuilder sb = new StringBuilder();
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    sb.append(line);
                }
                JSONObject jSONObject = new JSONObject(sb.toString());
                jSONObject.getString("version");
                JSONArray jSONArray = jSONObject.getJSONArray("jsons");
                int length = jSONArray.length();
                for (int i = 0; i < length; i++) {
                    buildHolidayData(this.mContext.getFilesDir().getAbsolutePath() + "/" + jSONArray.optString(i));
                }
            } finally {
                bufferedReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    /* JADX WARN: Code duplicated, block: B:64:0x00fa A[Catch: IOException -> 0x00f6, TRY_LEAVE, TryCatch #4 {IOException -> 0x00f6, blocks: (B:60:0x00f2, B:64:0x00fa), top: B:69:0x00f2 }] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0, types: [java.io.File] */
    /* JADX WARN: Type inference failed for: r0v1, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r0v12 */
    /* JADX WARN: Type inference failed for: r0v4 */
    private void buildHolidayData(String str) {
        File file = new File(str);
        if (!file.exists()) {
            return;
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            try {
                StringBuilder sb = new StringBuilder();
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    sb.append(line);
                }
                JSONObject jSONObject = new JSONObject(sb.toString());
                jSONObject.getString("version");
                JSONArray jSONArray = jSONObject.getJSONArray("chinaholidays");
                int length = jSONArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                    String string = jSONObject2.getString("name");
                    String string2 = jSONObject2.getString("description");
                    String string3 = jSONObject2.getString("type");
                    JSONArray jSONArray2 = jSONObject2.getJSONArray("holidays");
                    ChinaHoliday chinaHoliday = new ChinaHoliday();
                    chinaHoliday.setName(string);
                    chinaHoliday.setDescription(string2);
                    chinaHoliday.setType(string3);
                    int length2 = jSONArray2.length();
                    if (length2 > 0) {
                        for (int i2 = 0; i2 < length2; i2++) {
                            chinaHoliday.addHoliday(jSONArray2.getString(i2));
                        }
                    }
                    JSONArray jSONArray3 = jSONObject2.getJSONArray("workdays");
                    int length3 = jSONArray3.length();
                    if (length3 > 0) {
                        for (int i3 = 0; i3 < length3; i3++) {
                            chinaHoliday.addWorkday(jSONArray3.getString(i3));
                        }
                    }
                    this.mChinaHolidayList.add(chinaHoliday);
                }
            } finally {
                bufferedReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    public int getHolidayType(Calendar calendar) {
        String str = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        ArrayList<ChinaHoliday> arrayList = this.mChinaHolidayList;
        if (arrayList == null || arrayList.size() < 1) {
            return -1;
        }
        int size = this.mChinaHolidayList.size();
        int iCheckHolidayOrWorkday = -1;
        for (int i = 0; i < size; i++) {
            iCheckHolidayOrWorkday = this.mChinaHolidayList.get(i).checkHolidayOrWorkday(str);
            if (iCheckHolidayOrWorkday != -1) {
                return iCheckHolidayOrWorkday;
            }
        }
        return iCheckHolidayOrWorkday;
    }

    public String getHolidaySpecial(Calendar calendar) {
        String str = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        ArrayList<ChinaHoliday> arrayList = this.mChinaHolidayList;
        if (arrayList == null || arrayList.size() < 1) {
            return null;
        }
        int size = this.mChinaHolidayList.size();
        for (int i = 0; i < size; i++) {
            ChinaHoliday chinaHoliday = this.mChinaHolidayList.get(i);
            String holidaySpecial = chinaHoliday.getHolidaySpecial(str);
            if (!TextUtils.isEmpty(holidaySpecial) && "special".equals(chinaHoliday.getType())) {
                return holidaySpecial;
            }
        }
        return "";
    }
}
