package com.sonymobile.calendar.linkedin;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.util.GaUtils;
import android.widget.Toast;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISessionManager;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.linkedin.backend.LinkedInCommunicationService;
import com.sonymobile.calendar.linkedin.backend.LinkedInSyncActivity;
import com.sonymobile.calendar.linkedin.model.LinkedInContact;
import com.sonymobile.calendar.linkedin.model.LinkedInImageCache;

/* JADX INFO: loaded from: classes2.dex */
public class LinkedInUtils {
    private static final int HIDE_SYNC_VIEW = 0;
    public static final String KEY_SHOW_CONNECT_IN_LISTVIEW = "preferences_show_connect_in_listview";
    public static final String KEY_SHOW_CONNECT_IN_MEETING_DETAILS = "preferences_show_connect_in_meeting_details";
    public static final String LINKEDIN_COMPANY = "linkedInCompany";
    public static final String LINKEDIN_EMAIL = "linkedInEmail";
    public static final String LINKEDIN_EMAIL_FOR_STORE = "linkedInEmailForStore";
    public static final String LINKEDIN_FIRST_NAME = "linkedInFirstName";
    public static final String LINKEDIN_LAST_NAME = "linkedInLastName";
    private static final String LINKEDIN_MARKET_LINK = "market://details?id=com.linkedin.android&referrer=sony_xperia";
    public static final String LINKEDIN_PACKAGE = "com.linkedin.android";
    public static final String LINKEDIN_POSITION = "linkedInPosition";
    public static final String LINKEDIN_TITLE = "linkedInTitle";
    private static final int SHOW_SYNC_VIEW = 1;
    private static final int SYNC_LINKEDIN = 1001;
    public static final int TYPE_CONNECT_FROM_AGENDA_LIST = 0;
    public static final int TYPE_CONNECT_FROM_BOTH = 2;
    public static final int TYPE_CONNECT_FROM_MEETING_DETAILS = 1;

    public enum LinkedInActivationEntrypoint {
        WELCOME_SCREEN,
        EVENT,
        AGENDA,
        SETTINGS,
        HINT_AND_PROMOTE,
        UNKNOWN_ENTRYPOINT
    }

    public static void closeSyncWithLinkedIn(Context context, int i) {
        if (i == 0) {
            disableAgendaListEntryPoint(context);
            return;
        }
        if (i == 1) {
            disableEventInfoEntryPoint(context);
        } else {
            if (i != 2) {
                return;
            }
            disableAgendaListEntryPoint(context);
            disableEventInfoEntryPoint(context);
        }
    }

    private static void disableAgendaListEntryPoint(Context context) {
        SharedPreferences sharedPreferences = GeneralPreferences.getSharedPreferences(context);
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        if (sharedPreferences.getInt(KEY_SHOW_CONNECT_IN_LISTVIEW, 1) == 1) {
            editorEdit.putInt(KEY_SHOW_CONNECT_IN_LISTVIEW, 0);
            editorEdit.commit();
        }
    }

    private static void disableEventInfoEntryPoint(Context context) {
        SharedPreferences sharedPreferences = GeneralPreferences.getSharedPreferences(context);
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        if (sharedPreferences.getInt(KEY_SHOW_CONNECT_IN_MEETING_DETAILS, 1) == 1) {
            editorEdit.putInt(KEY_SHOW_CONNECT_IN_MEETING_DETAILS, 0);
            editorEdit.commit();
        }
    }

    public static boolean shouldShowConnectViewInListView(Context context) {
        return GeneralPreferences.getSharedPreferences(context).getInt(KEY_SHOW_CONNECT_IN_LISTVIEW, 1) == 1;
    }

    public static boolean shouldShowConnectViewInMeetingDetails(Context context) {
        return GeneralPreferences.getSharedPreferences(context).getInt(KEY_SHOW_CONNECT_IN_MEETING_DETAILS, 1) == 1;
    }

    public static void deactivateLinkedInSync(Context context) {
        LISessionManager.getInstance(context).clearSession();
        LinkedInImageCache.killCache();
    }

    public static void sendConnectionRequest(Activity activity, LinkedInContact linkedInContact) {
        Resources resources = activity.getResources();
        Intent intent = new Intent(activity, (Class<?>) LinkedInCommunicationService.class);
        intent.setAction(LinkedInCommunicationService.ACTION_CONTACT_INVITE);
        intent.putExtra(LinkedInCommunicationService.LINKEDIN_ID_PARAMETER, linkedInContact.getLinkedinId());
        intent.putExtra(LinkedInCommunicationService.SUBJECT_PARAMETER, resources.getString(R.string.linkedin_add_to_your_network_subject));
        intent.putExtra(LinkedInCommunicationService.BODY_PARAMETER, resources.getString(R.string.linkedin_add_to_your_network_body));
        activity.startService(intent);
    }

    public static boolean isLinkedInSessionValid(Context context) {
        return LISessionManager.getInstance(context).getSession().isValid();
    }

    public static boolean isLinkedInSyncValid(Context context) {
        return isLinkedInEnabled(context) && isLinkedInSessionValid(context) && Utils.checkPackageAvailable(context, "com.linkedin.android");
    }

    public static boolean isLinkedInEnabled(Context context) {
        return context.getResources().getBoolean(R.bool.is_linkedin_enabled);
    }

    public static String getConnectionString(Context context, int i) {
        if (i == 1) {
            return context.getResources().getString(R.string.first_linkedin_connection);
        }
        if (i == 2) {
            return context.getResources().getString(R.string.second_linkedin_connection);
        }
        if (i != 3) {
            return null;
        }
        return context.getResources().getString(R.string.third_linkedin_connection);
    }

    public static boolean isTokenExpired(Context context) {
        AccessToken accessToken = LISessionManager.getInstance(context).getSession().getAccessToken();
        return accessToken != null && accessToken.isExpired();
    }

    public static void goToAppStore(Activity activity) {
        try {
            activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(LINKEDIN_MARKET_LINK)));
        } catch (ActivityNotFoundException unused) {
        }
    }

    public static void startSyncWithLinkedInActivity(Activity activity, LinkedInActivationEntrypoint linkedInActivationEntrypoint) {
        activity.startActivityForResult(new Intent(activity, (Class<?>) LinkedInSyncActivity.class), 1001);
        int i = AnonymousClass1.$SwitchMap$com$sonymobile$calendar$linkedin$LinkedInUtils$LinkedInActivationEntrypoint[linkedInActivationEntrypoint.ordinal()];
        if (i == 1) {
            GaUtils.sendEvent(GaUtils.LINKEDIN_ACTIVATION_CATEGORY, GaUtils.LINKEDIN_ACTIVATION_FROM_WELCOME_SCREEN);
            return;
        }
        if (i == 2) {
            GaUtils.sendEvent(GaUtils.LINKEDIN_ACTIVATION_CATEGORY, GaUtils.LINKEDIN_ACTIVATION_FROM_EVENT);
            return;
        }
        if (i == 3) {
            GaUtils.sendEvent(GaUtils.LINKEDIN_ACTIVATION_CATEGORY, GaUtils.LINKEDIN_ACTIVATION_FROM_AGENDA);
        } else if (i == 4) {
            GaUtils.sendEvent(GaUtils.LINKEDIN_ACTIVATION_CATEGORY, GaUtils.LINKEDIN_ACTIVATION_FROM_HINT_AND_PROMOTE);
        } else {
            if (i != 5) {
                return;
            }
            GaUtils.sendEvent(GaUtils.LINKEDIN_ACTIVATION_CATEGORY, GaUtils.LINKEDIN_ACTIVATION_FROM_SETTINGS);
        }
    }

    /* JADX INFO: renamed from: com.sonymobile.calendar.linkedin.LinkedInUtils$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$sonymobile$calendar$linkedin$LinkedInUtils$LinkedInActivationEntrypoint;

        static {
            int[] iArr = new int[LinkedInActivationEntrypoint.values().length];
            $SwitchMap$com$sonymobile$calendar$linkedin$LinkedInUtils$LinkedInActivationEntrypoint = iArr;
            try {
                iArr[LinkedInActivationEntrypoint.WELCOME_SCREEN.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$sonymobile$calendar$linkedin$LinkedInUtils$LinkedInActivationEntrypoint[LinkedInActivationEntrypoint.EVENT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$sonymobile$calendar$linkedin$LinkedInUtils$LinkedInActivationEntrypoint[LinkedInActivationEntrypoint.AGENDA.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$sonymobile$calendar$linkedin$LinkedInUtils$LinkedInActivationEntrypoint[LinkedInActivationEntrypoint.HINT_AND_PROMOTE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$sonymobile$calendar$linkedin$LinkedInUtils$LinkedInActivationEntrypoint[LinkedInActivationEntrypoint.SETTINGS.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    public static void handleSyncWithLinkedInResult(Activity activity, int i, int i2, Intent intent) {
        handleSyncWithLinkedInResultAndReturnStatus(activity, i, i2, intent);
    }

    public static boolean handleSyncWithLinkedInResultAndReturnStatus(Activity activity, int i, int i2, Intent intent) {
        String string;
        if (i != 1001 || i2 != -1) {
            return false;
        }
        Resources resources = activity.getResources();
        boolean booleanExtra = intent.getBooleanExtra(LinkedInSyncActivity.LINKEDIN_SYNC_SUCCESS_PARAMETER, false);
        if (booleanExtra) {
            string = resources.getString(R.string.linkedin_sync_success);
            closeSyncWithLinkedIn(activity, 2);
            getOwnLinkedInEmail(activity);
        } else {
            string = resources.getString(R.string.linkedin_sync_unsuccess);
        }
        Toast.makeText(activity, string, 1).show();
        return booleanExtra;
    }

    public static void getOwnLinkedInEmail(Activity activity) {
        Intent intent = new Intent(activity, (Class<?>) LinkedInCommunicationService.class);
        intent.setAction(LinkedInCommunicationService.ACTION_OWN_EMAIL);
        activity.startService(intent);
    }
}
