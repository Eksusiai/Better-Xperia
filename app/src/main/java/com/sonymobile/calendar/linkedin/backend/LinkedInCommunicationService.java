package com.sonymobile.calendar.linkedin.backend;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.sonyericsson.calendar.util.RecurrenceRuleParser;
import com.sonymobile.calendar.linkedin.model.LinkedInContact;
import com.sonymobile.calendar.linkedin.model.LinkedInEmailLookupParser;
import com.sonymobile.calendar.linkedin.model.LinkedInMySelf;
import com.sonymobile.calendar.linkedin.model.LinkedInPeopleSearchParser;
import com.sonymobile.provider.TasksContract;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class LinkedInCommunicationService extends Service {
    public static final String ACTION_CONTACT_INVITE = "linkedinContactInviteAction";
    public static final String ACTION_EMAIL_LOOKUP = "linkedinEmailLookupAction";
    public static final String ACTION_OWN_EMAIL = "linkedinOwnemailAction";
    public static final String ACTION_PEOPLE_SEARCH = "linkedinPeopleSearchAction";
    public static final String BODY_PARAMETER = "bodyParameter";
    private static final String COMPANY = "company-name=";
    public static final String COMPANY_PARAMETER = "companyParameter";
    private static final String CONTACT_INVITATION = "v1/people/~/mailbox";
    private static final String COUNT = "count=";
    private static final int COUNT_DEFAULT_VALUE = 25;
    public static final String COUNT_PARAMETER = "countParameter";
    private static final String EMAIL = "email=";
    public static final String EMAILS_PARAMETER = "emailsParameter";
    private static final String EMAIL_LOOKUP = "v1/people::(%s):(id,first-name,last-name,picture-url,headline,location:(name),industry,distance)";
    private static final String FIRST_NAME = "first-name=";
    public static final String FIRST_NAME_PARAMETER = "firstNameParameter";
    private static final String HOST = "https://api.linkedin.com/";
    private static final String LAST_NAME = "last-name=";
    public static final String LAST_NAME_PARAMETER = "lastNameParameter";
    public static final String LINKEDIN_CONTACTS_PARAMETER = "linkedinContactsParameter";
    public static final String LINKEDIN_ID_PARAMETER = "linkedinIdParameter";
    public static final String LINKEDIN_OWN_EMAIL_PARAMETER = "linkedinOwnemailParameter";
    public static final String MESSAGE_CONTACT_INVITE = "linkedinContactInviteMessage";
    public static final String MESSAGE_EMAIL_LOOKUP = "linkedinEmailLookupMessage";
    public static final String MESSAGE_OWN_EMAIL = "linkedinOwnemailMessage";
    public static final String MESSAGE_PEOPLE_SEARCH = "linkedinPeopleSearchMessage";
    private static final String OFFSET = "start=";
    public static final String OFFSET_PARAMETER = "offsetParameter";
    private static final String OWN_EMAIL_LOOKUP = "v1/people/~:(email-address)";
    private static final String PEOPLE_SEARCH = "v1/people-search:(people:(id,first-name,last-name,picture-url,headline,location:(name),industry,distance),num-results)?";
    private static final String POSITION = "keywords=";
    public static final String POSITION_PARAMETER = "positionParameter";
    public static final String REQUEST_SUCCESS_PARAMETER = "requestSuccessParameter";
    public static final String SUBJECT_PARAMETER = "subjectParameter";
    private static final String TAG = "LinkedInCommunicationService";
    private static final String TITLE = "title=";
    public static final String TITLE_PARAMETER = "titleParameter";

    private enum RequestType {
        PEOPLE_SEARCH_REQUEST,
        EMAIL_LOOKUP_REQUEST,
        CONTACT_INVITE_REQUEST,
        OWN_EMAIL_REQUEST
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("This service does not support binding");
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent == null) {
            return 2;
        }
        String action = intent.getAction();
        if (ACTION_PEOPLE_SEARCH.equals(action)) {
            searchPeople(intent.getStringExtra(FIRST_NAME_PARAMETER), intent.getStringExtra(LAST_NAME_PARAMETER), intent.getStringExtra(COMPANY_PARAMETER), intent.getStringExtra(TITLE_PARAMETER), intent.getStringExtra(POSITION_PARAMETER), intent.getIntExtra(OFFSET_PARAMETER, 0), intent.getIntExtra(COUNT_PARAMETER, 25));
        } else if (ACTION_EMAIL_LOOKUP.equals(action)) {
            ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra(EMAILS_PARAMETER);
            if (stringArrayListExtra != null) {
                emailLookup(stringArrayListExtra);
            }
        } else if (ACTION_CONTACT_INVITE.equals(action)) {
            inviteToConnect(intent.getStringExtra(LINKEDIN_ID_PARAMETER), intent.getStringExtra(SUBJECT_PARAMETER), intent.getStringExtra(BODY_PARAMETER));
        } else if (ACTION_OWN_EMAIL.equals(action)) {
            lookupOwnemail();
        }
        return 2;
    }

    private void lookupOwnemail() {
        request(HOST + OWN_EMAIL_LOOKUP, RequestType.OWN_EMAIL_REQUEST);
    }

    private void searchPeople(String str, String str2, String str3, String str4, String str5, int i, int i2) {
        request(makeSearchUrl(str, str2, str3, str4, str5, i, i2), RequestType.PEOPLE_SEARCH_REQUEST);
    }

    private void emailLookup(List<String> list) {
        request(makeEmailLookupUrl(list), RequestType.EMAIL_LOOKUP_REQUEST);
    }

    private void request(String str, RequestType requestType) {
        APIHelper.getInstance(getApplicationContext()).getRequest(this, str, new ApiRequestListener(requestType));
    }

    private void inviteToConnect(String str, String str2, String str3) {
        APIHelper.getInstance(getApplicationContext()).postRequest(this, makeInviteUrl(), makeInviteContactBody(str, str2, str3), new ApiRequestListener(RequestType.CONTACT_INVITE_REQUEST));
    }

    private String makeInviteUrl() {
        return HOST + CONTACT_INVITATION;
    }

    private JSONObject makeInviteContactBody(String str, String str2, String str3) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("recipients", makeRecipients(str));
            jSONObject.put("subject", str2);
            jSONObject.put(TasksContract.TasksColumns.BODY_DATA, str3);
            jSONObject.put("item-content", makeItemContent());
        } catch (JSONException e) {
            Log.e(TAG, "makeInviteContactBody() exception:" + e.toString());
        }
        return jSONObject;
    }

    private JSONObject makeRecipients(String str) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        jSONObject.put("values", jSONArray);
        JSONObject jSONObject2 = new JSONObject();
        jSONArray.put(jSONObject2);
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put("_path", "/people/" + str);
        jSONObject2.put("person", jSONObject3);
        return jSONObject;
    }

    private JSONObject makeItemContent() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("invitation-request", makeInvitationRequest());
        return jSONObject;
    }

    private JSONObject makeInvitationRequest() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("connect-type", "friend");
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put("name", "NAME_SEARCH");
        jSONObject2.put("value", LISessionManager.getInstance(this).getSession().getAccessToken().getValue());
        jSONObject.put("authorization", jSONObject2);
        return jSONObject;
    }

    private String makeEmailLookupUrl(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                sb.append(RecurrenceRuleParser.VALUE_SEPARATOR);
            }
            String str = list.get(i);
            sb.append(EMAIL);
            sb.append(Uri.encode(str));
        }
        return HOST + String.format(EMAIL_LOOKUP, sb.toString());
    }

    private String makeSearchUrl(String str, String str2, String str3, String str4, String str5, int i, int i2) {
        boolean z;
        StringBuilder sb = new StringBuilder();
        sb.append(HOST);
        sb.append(PEOPLE_SEARCH);
        boolean z2 = true;
        if (TextUtils.isEmpty(str2)) {
            z = false;
        } else {
            sb.append(LAST_NAME);
            sb.append(Uri.encode(str2));
            z = true;
        }
        if (!TextUtils.isEmpty(str)) {
            if (z) {
                sb.append("&");
            }
            sb.append(FIRST_NAME);
            sb.append(Uri.encode(str));
            z = true;
        }
        if (!TextUtils.isEmpty(str3)) {
            if (z) {
                sb.append("&");
            }
            sb.append(COMPANY);
            sb.append(Uri.encode(str3));
            z = true;
        }
        if (!TextUtils.isEmpty(str4)) {
            if (z) {
                sb.append("&");
            }
            sb.append(TITLE);
            sb.append(Uri.encode(str4));
            z = true;
        }
        if (TextUtils.isEmpty(str5)) {
            z2 = z;
        } else {
            if (z) {
                sb.append("&");
            }
            sb.append(POSITION);
            sb.append(Uri.encode(str5));
        }
        if (i < 0) {
            i = 0;
        }
        if (z2) {
            sb.append("&");
        }
        sb.append(OFFSET);
        sb.append(i);
        sb.append("&");
        sb.append(COUNT);
        sb.append(i2);
        return sb.toString();
    }

    private class ApiRequestListener implements ApiListener {
        private RequestType mType;

        public ApiRequestListener(RequestType requestType) {
            this.mType = requestType;
        }

        @Override // com.linkedin.platform.listeners.ApiListener
        public void onApiSuccess(ApiResponse apiResponse) {
            ArrayList<LinkedInContact> arrayList;
            JSONObject responseDataAsJson;
            if (this.mType == RequestType.CONTACT_INVITE_REQUEST || (responseDataAsJson = apiResponse.getResponseDataAsJson()) == null) {
                arrayList = null;
            } else {
                int i = AnonymousClass1.$SwitchMap$com$sonymobile$calendar$linkedin$backend$LinkedInCommunicationService$RequestType[this.mType.ordinal()];
                if (i == 1) {
                    arrayList = new LinkedInPeopleSearchParser(responseDataAsJson).parse();
                } else if (i != 2) {
                    if (i == 3) {
                        LinkedInMySelf.getInstance().setEmail(responseDataAsJson);
                    }
                    arrayList = null;
                } else {
                    arrayList = new LinkedInEmailLookupParser(responseDataAsJson).parse();
                }
            }
            LinkedInCommunicationService.this.broadcastContactsIntent(true, arrayList, this.mType);
        }

        @Override // com.linkedin.platform.listeners.ApiListener
        public void onApiError(LIApiError lIApiError) {
            Log.w(LinkedInCommunicationService.TAG, lIApiError.toString());
            LinkedInCommunicationService.this.broadcastContactsIntent(false, null, this.mType);
        }
    }

    /* JADX INFO: renamed from: com.sonymobile.calendar.linkedin.backend.LinkedInCommunicationService$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$sonymobile$calendar$linkedin$backend$LinkedInCommunicationService$RequestType;

        static {
            int[] iArr = new int[RequestType.values().length];
            $SwitchMap$com$sonymobile$calendar$linkedin$backend$LinkedInCommunicationService$RequestType = iArr;
            try {
                iArr[RequestType.PEOPLE_SEARCH_REQUEST.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$sonymobile$calendar$linkedin$backend$LinkedInCommunicationService$RequestType[RequestType.EMAIL_LOOKUP_REQUEST.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$sonymobile$calendar$linkedin$backend$LinkedInCommunicationService$RequestType[RequestType.OWN_EMAIL_REQUEST.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$sonymobile$calendar$linkedin$backend$LinkedInCommunicationService$RequestType[RequestType.CONTACT_INVITE_REQUEST.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broadcastContactsIntent(boolean z, ArrayList<LinkedInContact> arrayList, RequestType requestType) {
        Intent intent;
        int i = AnonymousClass1.$SwitchMap$com$sonymobile$calendar$linkedin$backend$LinkedInCommunicationService$RequestType[requestType.ordinal()];
        if (i == 1) {
            intent = new Intent(MESSAGE_PEOPLE_SEARCH);
            if (z) {
                intent.putParcelableArrayListExtra(LINKEDIN_CONTACTS_PARAMETER, arrayList);
            }
        } else if (i == 2) {
            intent = new Intent(MESSAGE_EMAIL_LOOKUP);
            if (z) {
                intent.putParcelableArrayListExtra(LINKEDIN_CONTACTS_PARAMETER, arrayList);
            }
        } else if (i == 3) {
            intent = new Intent(MESSAGE_OWN_EMAIL);
            if (z) {
                intent.putExtra(LINKEDIN_OWN_EMAIL_PARAMETER, LinkedInMySelf.getInstance().getEmail());
            }
        } else {
            intent = i != 4 ? new Intent() : new Intent(MESSAGE_CONTACT_INVITE);
        }
        intent.putExtra(REQUEST_SUCCESS_PARAMETER, z);
        sendBroadcast(intent);
    }
}
