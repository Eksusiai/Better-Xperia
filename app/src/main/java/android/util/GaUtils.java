package android.util;

import android.content.Context;
import com.sonymobile.calendar.R;
import com.sonymobile.gagtmhelper.GaGtmExceptionParser;
import com.sonymobile.gagtmhelper.GaGtmUtils;

/* JADX INFO: loaded from: classes.dex */
public class GaUtils {
    public static final String LINKEDIN_ACTIVATION_CATEGORY = "Linkedin activation";
    public static final String LINKEDIN_ACTIVATION_FROM_AGENDA = "Linkedin activation from agenda view";
    public static final String LINKEDIN_ACTIVATION_FROM_EVENT = "Linkedin activation from event details";
    public static final String LINKEDIN_ACTIVATION_FROM_HINT_AND_PROMOTE = "Linkedin activation from hint and promote";
    public static final String LINKEDIN_ACTIVATION_FROM_SETTINGS = "Linkedin activation from settings";
    public static final String LINKEDIN_ACTIVATION_FROM_WELCOME_SCREEN = "Linkedin activation from welcome screen";
    public static final String LINKEDIN_APP_STORE_LAUNCHED = "Linkedin app store activity launched";
    private static final String LOG_TAG = "GaUtils";
    public static final String NOTIFICATION_CATEGORY = "Notification Event";
    public static final String NOTIFICATION_CREATED_TAG = "Notification created";
    public static final String STARTING_APP_CATEGORY = "App Launch Event";
    public static final String STARTING_FROM_CONTACTS = "App started from Contacts";
    public static final String STARTING_FROM_EMAIL = "App started from E-mail";
    public static final String STARTING_FROM_ICON = "App started from icon";
    public static final String STARTING_FROM_NOTIFICATION = "App started from event reminder";
    private static boolean setup;

    public static boolean setupAnalytics(Context context) {
        if (setup) {
            return false;
        }
        Context applicationContext = context.getApplicationContext();
        GaGtmExceptionParser.enableExceptionParsing(applicationContext);
        GaGtmUtils.getInstance().init(applicationContext, applicationContext.getString(R.string.gtm_container_key), R.raw.gtm_container, true, 2, new GaGtmUtils.OnContainerLoadedListener() { // from class: android.util.GaUtils.1
            @Override // com.sonymobile.gagtmhelper.GaGtmUtils.OnContainerLoadedListener
            public void onContainerLoaded(boolean z) {
                Log.d(GaUtils.LOG_TAG, "Container loaded success=" + z);
            }
        });
        setup = true;
        return true;
    }

    public static void sendView(Context context, String str) {
        GaGtmUtils.getInstance().pushAppView(str);
    }

    public static void sendEvent(String str, String str2) {
        GaGtmUtils.getInstance().pushEvent(str, str2, "", 0L);
    }
}
