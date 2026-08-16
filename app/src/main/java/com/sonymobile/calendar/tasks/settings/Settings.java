package com.sonymobile.calendar.tasks.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import com.sonymobile.calendar.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes2.dex */
public class Settings {
    private static final String KEY_DEFAULT_SETTINGS_VERSION = "pref_key_default_settings_version";
    public static final String KEY_DISPLAY_COMPLETED_TASK = "pref_key_display_completed_task";
    public static final String KEY_REMINDER_RINGTONE = "pref_key_select_ringtone";
    public static final String KEY_REMINDER_VIBRATE = "pref_key_vibrate";
    public static final String KEY_SUFFIX_VISIBILITY = "_visibility";
    public static final String KEY_SYNC_ACCOUNT = "pref_key_sync_account";
    public static final String KEY_SYNC_REMINDER = "pref_key_reminder";
    private static final int NO_VERSION = -1;
    private static final String TAG = "Settings";
    private static Settings sInstance;
    private Context mContext;
    private SharedPreferences mPrefs;

    private Settings(Context context) {
        this.mContext = null;
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        if (applicationContext == null) {
            this.mContext = context;
        }
    }

    public static synchronized Settings getInstance(Context context) {
        try {
            if (context == null) {
                Log.i(TAG, "context is null");
                return null;
            }
            if (sInstance == null) {
                Settings settings = new Settings(context);
                sInstance = settings;
                settings.init(false);
            }
            return sInstance;
        } catch (Throwable th) {
            throw th;
        }
    }

    private void init(boolean z) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        this.mPrefs = defaultSharedPreferences;
        SharedPreferences.Editor editorEdit = defaultSharedPreferences.edit();
        boolean z2 = false;
        XmlResourceParser defaultSettingsResourceId = null;
        try {
            try {
                defaultSettingsResourceId = getDefaultSettingsResourceId();
                SettingsParser settingsParser = new SettingsParser(defaultSettingsResourceId, z);
                int settingsVersion = getSettingsVersion();
                int version = settingsParser.getVersion();
                if (settingsVersion != version || z) {
                    Log.v(TAG, "Applying default settings");
                    settingsParser.apply(this.mPrefs, editorEdit);
                    editorEdit.putInt(KEY_DEFAULT_SETTINGS_VERSION, version);
                    z2 = true;
                }
                if (defaultSettingsResourceId != null) {
                    defaultSettingsResourceId.close();
                }
            } catch (Resources.NotFoundException e) {
                Log.i(TAG, "Settings resource not found", e);
                if (defaultSettingsResourceId != null) {
                }
            } catch (IOException e2) {
                Log.e(TAG, "Applying default settings failed", e2);
                if (defaultSettingsResourceId != null) {
                }
            } catch (XmlPullParserException e3) {
                Log.e(TAG, "Applying default settings failed", e3);
                if (defaultSettingsResourceId != null) {
                }
            }
            if (z2) {
                editorEdit.apply();
                Log.v(TAG, "Settings committed");
            }
        } catch (Throwable th) {
            if (defaultSettingsResourceId != null) {
                defaultSettingsResourceId.close();
            }
            throw th;
        }
    }

    public String getString(String str, String str2) {
        SharedPreferences sharedPreferences;
        if (TextUtils.isEmpty(str) || (sharedPreferences = this.mPrefs) == null) {
            return str2;
        }
        try {
            return sharedPreferences.getString(str, str2);
        } catch (ClassCastException unused) {
            Log.w(TAG, "It is not a string setting item");
            return str2;
        }
    }

    public boolean getBoolean(String str, boolean z) {
        SharedPreferences sharedPreferences;
        if (TextUtils.isEmpty(str) || (sharedPreferences = this.mPrefs) == null) {
            return z;
        }
        try {
            return sharedPreferences.getBoolean(str, z);
        } catch (ClassCastException unused) {
            Log.w(TAG, "It is not a boolean setting item");
            return z;
        }
    }

    private synchronized int getSettingsVersion() {
        return this.mPrefs.getInt(KEY_DEFAULT_SETTINGS_VERSION, -1);
    }

    XmlResourceParser getDefaultSettingsResourceId() {
        return this.mContext.getResources().getXml(R.xml.default_settings);
    }

    private static class SettingsParser {
        private static final String ATTR_KEY = "key";
        private static final String ATTR_RUNTIME = "runtime";
        private static final String ATTR_VERSION = "version";
        private static final String SETTING_DISPLAY_COMPLETED_TASK = "display-completed-task";
        private static final String SETTING_DISPLAY_COMPLETED_TASK_VISIBILITY = "display-completed-task-visibility";
        private static final String SETTING_REMINDER_RINGTONE = "reminder-ringtone";
        private static final String SETTING_REMINDER_RINGTONE_VISIBILITY = "reminder-ringtone-visibility";
        private static final String SETTING_REMINDER_VIBRATION = "reminder-vibration";
        private static final String SETTING_REMINDER_VIBRATION_VISIBILITY = "reminder-vibration-visibility";
        private static final String TAG_SETTING = "setting";
        private static final String TAG_SETTINGS = "settings";
        private SharedPreferences.Editor mEditor;
        private boolean mIsMasterReset;
        private SharedPreferences mPrefs;
        private int mVersion = parseVersion();
        private XmlPullParser mXpp;

        public SettingsParser(XmlPullParser xmlPullParser, boolean z) throws XmlPullParserException, IOException {
            this.mIsMasterReset = false;
            this.mXpp = xmlPullParser;
            this.mIsMasterReset = z;
        }

        private int parseVersion() throws XmlPullParserException, IOException {
            int eventType = this.mXpp.getEventType();
            while (eventType != 1) {
                if (eventType == 2 && this.mXpp.getName().equals(TAG_SETTINGS)) {
                    return Integer.parseInt(this.mXpp.getAttributeValue(null, ATTR_VERSION));
                }
                eventType = this.mXpp.next();
            }
            return 0;
        }

        public int getVersion() {
            return this.mVersion;
        }

        public void apply(SharedPreferences sharedPreferences, SharedPreferences.Editor editor) throws XmlPullParserException, IOException {
            String text;
            this.mPrefs = sharedPreferences;
            this.mEditor = editor;
            int eventType = this.mXpp.getEventType();
            while (eventType != 1) {
                if (eventType == 2 && this.mXpp.getName().equals(TAG_SETTING)) {
                    String attributeValue = this.mXpp.getAttributeValue(null, "key");
                    String attributeValue2 = this.mXpp.getAttributeValue(null, ATTR_RUNTIME);
                    if (this.mXpp.next() == 4 && (text = this.mXpp.getText()) != null) {
                        try {
                            applySetting(attributeValue, text, Boolean.parseBoolean(attributeValue2));
                        } catch (IllegalArgumentException unused) {
                            Log.w(Settings.TAG, "Illegal value: " + attributeValue + " = " + text);
                        }
                    }
                }
                eventType = this.mXpp.next();
            }
        }

        private void applySetting(String str, String str2, boolean z) throws IllegalArgumentException {
            str.hashCode();
            switch (str) {
                case "display-completed-task-visibility":
                    putBoolean("pref_key_display_completed_task_visibility", str2, z);
                    break;
                case "reminder-ringtone":
                    putAndCommitString(Settings.KEY_REMINDER_RINGTONE, str2, z, this.mPrefs);
                    break;
                case "reminder-vibration-visibility":
                    putBoolean("pref_key_vibrate_visibility", str2, z);
                    break;
                case "reminder-vibration":
                    putBoolean(Settings.KEY_REMINDER_VIBRATE, str2, z);
                    break;
                case "reminder-ringtone-visibility":
                    putBoolean("pref_key_select_ringtone_visibility", str2, z);
                    break;
                case "display-completed-task":
                    putBoolean(Settings.KEY_DISPLAY_COMPLETED_TASK, str2, z);
                    break;
            }
        }

        private void putAndCommitString(String str, String str2, boolean z, SharedPreferences sharedPreferences) {
            if (z && this.mPrefs.contains(str) && !this.mIsMasterReset) {
                return;
            }
            SharedPreferences.Editor editorEdit = sharedPreferences.edit();
            editorEdit.putString(str, str2);
            editorEdit.apply();
        }

        private void putBoolean(String str, String str2, boolean z) throws IllegalArgumentException {
            boolean z2;
            if (z && this.mPrefs.contains(str) && !this.mIsMasterReset) {
                return;
            }
            if (str2.equalsIgnoreCase("true")) {
                z2 = true;
            } else {
                if (!str2.equalsIgnoreCase("false")) {
                    throw new IllegalArgumentException();
                }
                z2 = false;
            }
            this.mEditor.putBoolean(str, z2);
        }
    }
}
