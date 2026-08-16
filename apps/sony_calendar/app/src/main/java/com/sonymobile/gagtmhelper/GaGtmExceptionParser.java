package com.sonymobile.gagtmhelper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Base64;
import android.util.Log;
import com.sonyericsson.calendar.util.CalendarConstants;
import com.sonyericsson.calendar.util.RecurrenceRuleParser;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class GaGtmExceptionParser {
    private static final String DEEPMODE_EVENT_STRING = "EVENT";
    private static final String DEEPMODE_EXCEPTION_STRING = "EXCEPTION";
    private static final String HASH_VERSION = "0";
    private static final String LOG_TAG = "GaGtmHelper";
    private static final int MAX_CAUSE_DEPTH = 2;
    private static final int MAX_MESSAGE_LENGTH = 120;
    private static final int MAX_REPORTED_ROWS_DEFAULT = 10;
    private static final int MAX_TRAVERSED_ROWS_DEFAULT = 20;
    private static volatile Thread.UncaughtExceptionHandler mDefaultHandler;
    private static volatile List<String> mEnabledPackageNames = new LinkedList();
    private static volatile int mMaxReportedRows = 10;
    private static volatile int mMaxTraversedRows = 20;
    private static volatile List<String> mDeepCrashHashList = new LinkedList();
    private static volatile DeepMode mDeepMode = DeepMode.DEEPMODE_EXCEPTION;
    private static volatile String mAppVersion = "";
    private static Context mContext = null;
    private static long mStartTime = System.nanoTime();
    private static final Object mLock = new Object();

    public enum DeepMode {
        DEEPMODE_EVENT,
        DEEPMODE_EXCEPTION
    }

    private static byte[] intToByteArray(int i) {
        return new byte[]{(byte) ((i >> 24) & 255), (byte) ((i >> 16) & 255), (byte) ((i >> 8) & 255), (byte) (i & 255)};
    }

    private GaGtmExceptionParser() {
    }

    public static void setEnabledPackageNames(String str) {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "setEnabledPackageNames " + str);
        }
        synchronized (mLock) {
            mEnabledPackageNames = new LinkedList(Arrays.asList(str.split(RecurrenceRuleParser.VALUE_SEPARATOR)));
            ListIterator<String> listIterator = mEnabledPackageNames.listIterator();
            while (listIterator.hasNext()) {
                String strReplaceAll = listIterator.next().replaceAll(" ", "");
                if (strReplaceAll.isEmpty()) {
                    listIterator.remove();
                } else {
                    listIterator.set(strReplaceAll);
                }
            }
            if (GaGtmLog.isEnabled()) {
                for (int i = 0; i < mEnabledPackageNames.size(); i++) {
                    Log.d(LOG_TAG, "[" + String.valueOf(i) + "] '" + mEnabledPackageNames.get(i) + "'");
                }
            }
        }
    }

    public static void setMaxReportedRows(int i) {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "setMaxReportedRows:" + i);
        }
        if (i == 0) {
            i = 10;
            if (GaGtmLog.isEnabled()) {
                Log.d(LOG_TAG, "value was 0, setMaxReportedRows:10");
            }
        }
        synchronized (mLock) {
            mMaxReportedRows = i;
        }
    }

    public static void setMaxTraversedRows(int i) {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "setMaxTraversedRows:" + i);
        }
        if (i == 0) {
            i = 20;
            if (GaGtmLog.isEnabled()) {
                Log.d(LOG_TAG, "value was 0, setMaxReportedRows:20");
            }
        }
        synchronized (mLock) {
            mMaxTraversedRows = i;
        }
    }

    public static void setDeepMode(String str) {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "setDeepMode " + str);
        }
        synchronized (mLock) {
            if (str.equals(DEEPMODE_EVENT_STRING)) {
                if (GaGtmLog.isEnabled()) {
                    Log.d(LOG_TAG, "DEEPMODE_EVENT active");
                }
                mDeepMode = DeepMode.DEEPMODE_EVENT;
            } else if (str.equals(DEEPMODE_EXCEPTION_STRING)) {
                mDeepMode = DeepMode.DEEPMODE_EXCEPTION;
                if (GaGtmLog.isEnabled()) {
                    Log.d(LOG_TAG, "DEEPMODE_EXCEPTION active");
                }
            }
        }
    }

    public static void setDeepCrashHashList(String str) {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "setDeepCrashHashList " + str);
        }
        synchronized (mLock) {
            mDeepCrashHashList = new LinkedList(Arrays.asList(str.split(RecurrenceRuleParser.VALUE_SEPARATOR)));
            ListIterator<String> listIterator = mDeepCrashHashList.listIterator();
            while (listIterator.hasNext()) {
                String strReplaceAll = listIterator.next().replaceAll(" ", "");
                if (strReplaceAll.isEmpty()) {
                    listIterator.remove();
                } else {
                    listIterator.set(strReplaceAll);
                }
            }
            if (GaGtmLog.isEnabled()) {
                for (int i = 0; i < mDeepCrashHashList.size(); i++) {
                    Log.d(LOG_TAG, "[" + String.valueOf(i) + "] '" + mDeepCrashHashList.get(i) + "'");
                }
            }
        }
    }

    public static void enableExceptionParsing(Context context) throws IllegalStateException {
        synchronized (mLock) {
            mContext = context;
            if (mEnabledPackageNames.size() == 0 && GaGtmLog.isEnabled()) {
                Log.d(LOG_TAG, "setEnabledPackageNames is empty!");
            }
            if (mDefaultHandler == null) {
                mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            } else if (GaGtmLog.isEnabled()) {
                Log.d(LOG_TAG, "enableExceptionParsing was called twice but it should normally not be done!");
            }
            try {
                mAppVersion = context.getPackageManager().getPackageInfo(context.getApplicationContext().getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() { // from class: com.sonymobile.gagtmhelper.GaGtmExceptionParser.1
            @Override // java.lang.Thread.UncaughtExceptionHandler
            public void uncaughtException(Thread thread, Throwable th) {
                try {
                    GaGtmExceptionParser.generateCrash(thread, th);
                    GaGtmUtils.getInstance().serializeQueueToFile();
                    synchronized (GaGtmExceptionParser.mLock) {
                        if (GaGtmExceptionParser.mDefaultHandler != null) {
                            GaGtmExceptionParser.mDefaultHandler.uncaughtException(thread, th);
                        }
                    }
                } catch (Throwable th2) {
                    if (GaGtmLog.isEnabled()) {
                        Log.e(GaGtmExceptionParser.LOG_TAG, "internal exception : " + th2.getMessage());
                    }
                }
            }
        });
    }

    public static void generateCrash(Thread thread, Throwable th) {
        DeepMode deepMode;
        try {
            if (GaGtmLog.isEnabled()) {
                Log.d(LOG_TAG, "GenerateCrash");
            }
            if (thread != null && th != null) {
                String stackTraceHash = getStackTraceHash(thread, th);
                if (GaGtmLog.isEnabled()) {
                    Log.d(LOG_TAG, "exception has hash:" + stackTraceHash);
                }
                generateNormalCrash(thread, th, stackTraceHash);
                if (isDeepCrash(stackTraceHash)) {
                    if (GaGtmLog.isEnabled()) {
                        Log.d(LOG_TAG, "Generate deep crash");
                    }
                    DeepMode deepMode2 = DeepMode.DEEPMODE_EVENT;
                    synchronized (mLock) {
                        deepMode = mDeepMode;
                    }
                    if (deepMode == DeepMode.DEEPMODE_EVENT) {
                        generateDeepCrashEventMode(th, stackTraceHash);
                        return;
                    } else {
                        if (deepMode == DeepMode.DEEPMODE_EXCEPTION) {
                            generateDeepCrashExceptionMode(th, stackTraceHash);
                            return;
                        }
                        return;
                    }
                }
                if (GaGtmLog.isEnabled()) {
                    Log.d(LOG_TAG, "no deep crash is to be generated");
                    return;
                }
                return;
            }
            if (GaGtmLog.isEnabled()) {
                Log.d(LOG_TAG, "Thread or throwable was null");
            }
        } catch (Throwable th2) {
            if (GaGtmLog.isEnabled()) {
                Log.e(LOG_TAG, "internal exception : " + th2.getMessage());
            }
        }
    }

    private static void generateNormalCrash(Thread thread, Throwable th, String str) {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "generateNormalCrash");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("H:").append(str);
        sb.append(" T:").append(replaceAllNumbers(thread.getName())).append(" ").append((CharSequence) createThrowableDescription(th, 0));
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "uncaughtException: " + ((Object) sb));
            Log.d(LOG_TAG, "about to push exception");
        }
        GaGtmUtils.getInstance().pushException(sb.toString());
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "done pushing");
        }
    }

    private static StringBuilder createThrowableDescription(Throwable th, int i) {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "createThrowableDescription");
        }
        StringBuilder sb = new StringBuilder();
        if (i <= 2) {
            sb = createThrowableDescriptionShort(th);
            Throwable cause = th.getCause();
            if (cause != null) {
                sb.append(" Cause: ");
                sb.append((CharSequence) createThrowableDescription(cause, i + 1));
            } else {
                sb.append((CharSequence) createThrowableDescriptionStackTrace(th));
            }
        } else {
            sb.append("... ").append((CharSequence) createThrowableDescriptionStackTrace(th));
        }
        return sb;
    }

    private static StringBuilder createThrowableDescriptionShort(Throwable th) {
        StringBuilder sb = new StringBuilder(th.getClass().getSimpleName());
        String message = th.getMessage();
        if (message != null) {
            sb.append("(").append(message.substring(0, Math.min(message.length(), 120))).append(")");
        }
        return sb;
    }

    private static StringBuilder createThrowableDescriptionStackTrace(Throwable th) {
        int i;
        int i2;
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTrace = th.getStackTrace();
        Object obj = mLock;
        synchronized (obj) {
            i = mMaxReportedRows;
        }
        synchronized (obj) {
            i2 = mMaxTraversedRows;
        }
        if (stackTrace != null) {
            int length = stackTrace.length;
            sb.append(" S:").append(String.valueOf(length));
            boolean z = false;
            boolean z2 = false;
            int i3 = 0;
            for (int i4 = 0; !z2 && i4 < i2 && i4 < length; i4++) {
                StackTraceElement stackTraceElement = stackTrace[i4];
                if (stackTraceElement != null && packageNameIsEnabled(stackTraceElement.getClassName())) {
                    if (GaGtmLog.isEnabled()) {
                        Log.d(LOG_TAG, "firstInterestingRow=" + i4);
                    }
                    i3 = i4;
                    z2 = true;
                }
            }
            int i5 = 0;
            while (!z && i3 < i2 && i5 < i && i3 < length) {
                if (stackTrace[i3] != null) {
                    sb.append((CharSequence) stackElementString(stackTrace, i3));
                    i5++;
                    i3++;
                } else {
                    z = true;
                }
            }
        } else {
            sb.append(" Stack trace was null");
        }
        return sb;
    }

    private static StringBuilder stackElementString(StackTraceElement[] stackTraceElementArr, int i) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement stackTraceElement = stackTraceElementArr[i];
        sb.append(" ").append(String.format(Locale.US, "%02d", Integer.valueOf(i))).append(CalendarConstants.COLON).append(stackTraceElement.getFileName().replace(".java", "")).append(CalendarConstants.COLON).append(stackTraceElement.getClassName()).append(".").append(stackTraceElement.getMethodName()).append(CalendarConstants.COLON).append(stackTraceElement.getLineNumber());
        return sb;
    }

    private static boolean packageNameIsEnabled(String str) {
        synchronized (mLock) {
            Iterator<String> it = mEnabledPackageNames.iterator();
            while (it.hasNext()) {
                if (str.startsWith(it.next())) {
                    return true;
                }
            }
            return false;
        }
    }

    private static boolean isDeepCrash(String str) {
        boolean zContains;
        synchronized (mLock) {
            zContains = mDeepCrashHashList.contains(str);
            if (GaGtmLog.isEnabled()) {
                Log.d(LOG_TAG, "isDeepCrash for " + str + " is " + String.valueOf(zContains));
            }
        }
        return zContains;
    }

    private static String getStackTraceHash(Thread thread, Throwable th) {
        String rawStackTraceHashString = getRawStackTraceHashString(thread, th);
        int iHashCode = rawStackTraceHashString.hashCode();
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "hashString:" + rawStackTraceHashString);
        }
        String strReplaceAll = (HASH_VERSION + Base64.encodeToString(intToByteArray(iHashCode), 0)).replaceAll("\n", "");
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "hashString base64:" + strReplaceAll);
        }
        return strReplaceAll;
    }

    private static String getRawStackTraceHashString(Thread thread, Throwable th) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTrace = th.getStackTrace();
        synchronized (mLock) {
            sb.append(mAppVersion);
        }
        sb.append(replaceAllNumbers(thread.getName()));
        Throwable cause = th.getCause();
        if (cause != null) {
            sb.append(cause);
        }
        boolean z = false;
        int length = stackTrace != null ? stackTrace.length : 0;
        sb.append(String.valueOf(length));
        int i = 0;
        while (!z && i < 20 && i < length) {
            StackTraceElement stackTraceElement = th.getStackTrace()[i];
            if (stackTraceElement != null) {
                sb.append(String.valueOf(i)).append(stackTraceElement.getFileName()).append(stackTraceElement.getClassName()).append(stackTraceElement.getMethodName()).append(stackTraceElement.getLineNumber());
                i++;
            } else {
                z = true;
            }
        }
        if (GaGtmLog.isEnabled()) {
            Log.e(LOG_TAG, "stackTraceHashString: " + ((Object) sb));
        }
        return sb.toString();
    }

    private static String replaceAllNumbers(String str) {
        int length = str.length();
        String str2 = str;
        for (int i = 0; i < length; i++) {
            if (Character.isDigit(str2.charAt(i))) {
                str2 = str2.substring(0, i) + CalendarConstants.VCALENDAR_END_MARKER + str2.substring(i + 1, length);
            }
            if (str2.length() == 0) {
                str2 = str;
            }
        }
        return str2;
    }

    private static void generateDeepCrashEventMode(Throwable th, String str) {
        int i;
        int i2;
        int length;
        StackTraceElement[] stackTrace = th.getStackTrace();
        synchronized (mLock) {
            i = mMaxTraversedRows;
        }
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "generateDeepCrashEventmode hash:" + str);
        }
        boolean z = false;
        if (stackTrace != null) {
            length = stackTrace.length;
            i2 = 0;
        } else {
            i2 = 0;
            length = 0;
        }
        while (!z && i2 < i && i2 < length) {
            if (th.getStackTrace()[i2] != null) {
                StringBuilder sbStackElementString = stackElementString(stackTrace, i2);
                if (GaGtmLog.isEnabled()) {
                    Log.d(LOG_TAG, "[" + String.valueOf(i2) + "] " + sbStackElementString.toString());
                }
                GaGtmUtils.getInstance().pushDeepEvent("deepCrash", str, sbStackElementString.toString(), 0L);
                i2++;
            } else {
                z = true;
            }
        }
    }

    private static void generateDeepCrashExceptionMode(Throwable th, String str) {
        int i;
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTrace = th.getStackTrace();
        synchronized (mLock) {
            i = mMaxTraversedRows;
        }
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "generateDeepCrashExceptionMode hash:" + str);
        }
        boolean z = false;
        int length = stackTrace != null ? stackTrace.length : 0;
        sb.append("deepCrash H:").append(str);
        int i2 = 0;
        while (!z && i2 < i && i2 < length) {
            if (th.getStackTrace()[i2] != null) {
                sb.append((CharSequence) stackElementString(stackTrace, i2));
                i2++;
            } else {
                z = true;
            }
        }
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "deep exception description:" + sb.toString());
        }
        GaGtmUtils.getInstance().pushDeepException(sb.toString());
    }
}
