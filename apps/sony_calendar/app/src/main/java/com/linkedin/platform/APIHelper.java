package com.linkedin.platform;

import android.content.Context;
import android.text.TextUtils;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.internals.QueueManager;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class APIHelper {
    private static final String CONTENT_VALUE = "application/json";
    private static final String DATA = "responseData";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_LI_FORMAT = "x-li-format";
    private static final String HEADER_LI_FORMAT_VALUE = "json";
    private static final String HEADER_LI_VER = "x-li-msdk-ver";
    private static final String HEADER_SRC = "x-li-src";
    private static final String HEADER_SRC_VALUE = "msdk";
    private static final String HTTP_STATUS_CODE = "StatusCode";
    private static final String LOCATION_HEADER = "Location";
    private static final String TAG = "com.linkedin.platform.APIHelper";
    private static APIHelper apiHelper;

    public static APIHelper getInstance(Context context) {
        if (apiHelper == null) {
            apiHelper = new APIHelper();
        }
        return apiHelper;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Map<String, String> getLiHeaders(String str) {
        HashMap map = new HashMap();
        map.put("Content-Type", CONTENT_VALUE);
        map.put("Authorization", "Bearer " + str);
        map.put(HEADER_SRC, HEADER_SRC_VALUE);
        map.put(HEADER_LI_FORMAT, HEADER_LI_FORMAT_VALUE);
        map.put(HEADER_LI_VER, "1.0.0");
        return map;
    }

    private JsonObjectRequest buildRequest(final String str, int i, String str2, JSONObject jSONObject, final ApiListener apiListener) {
        return new JsonObjectRequest(i, str2, jSONObject, new Response.Listener<JSONObject>() { // from class: com.linkedin.platform.APIHelper.1
            @Override // com.android.volley.Response.Listener
            public void onResponse(JSONObject jSONObject2) {
                ApiListener apiListener2 = apiListener;
                if (apiListener2 != null) {
                    apiListener2.onApiSuccess(ApiResponse.buildApiResponse(jSONObject2));
                }
            }
        }, new Response.ErrorListener() { // from class: com.linkedin.platform.APIHelper.2
            @Override // com.android.volley.Response.ErrorListener
            public void onErrorResponse(VolleyError volleyError) {
                if (apiListener != null) {
                    apiListener.onApiError(LIApiError.buildLiApiError(volleyError));
                }
            }
        }) { // from class: com.linkedin.platform.APIHelper.3
            @Override // com.android.volley.toolbox.JsonObjectRequest, com.android.volley.toolbox.JsonRequest, com.android.volley.Request
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse networkResponse) {
                try {
                    JSONObject jSONObject2 = new JSONObject();
                    jSONObject2.put(APIHelper.HTTP_STATUS_CODE, networkResponse.statusCode);
                    String str3 = networkResponse.headers.get("Location");
                    if (!TextUtils.isEmpty(str3)) {
                        jSONObject2.put("Location", str3);
                    }
                    if (networkResponse.data != null && networkResponse.data.length != 0) {
                        jSONObject2.put(APIHelper.DATA, new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers)));
                    }
                    return Response.success(jSONObject2, HttpHeaderParser.parseCacheHeaders(networkResponse));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException e2) {
                    return Response.error(new ParseError(e2));
                }
            }

            @Override // com.android.volley.Request
            public Map<String, String> getHeaders() throws AuthFailureError {
                return APIHelper.this.getLiHeaders(str);
            }
        };
    }

    private void request(Context context, int i, String str, JSONObject jSONObject, ApiListener apiListener) {
        LISession session = LISessionManager.getInstance(context.getApplicationContext()).getSession();
        if (session.isValid()) {
            JsonObjectRequest jsonObjectRequestBuildRequest = buildRequest(session.getAccessToken().getValue(), i, str, jSONObject, apiListener);
            jsonObjectRequestBuildRequest.setTag(context == null ? TAG : context);
            QueueManager.getInstance(context).getRequestQueue().add(jsonObjectRequestBuildRequest);
        } else if (apiListener != null) {
            apiListener.onApiError(new LIApiError(LIApiError.ErrorType.accessTokenIsNotSet, "access toke is not set", null));
        }
    }

    public void getRequest(Context context, String str, ApiListener apiListener) {
        request(context, 0, str, null, apiListener);
    }

    public void postRequest(Context context, String str, JSONObject jSONObject, ApiListener apiListener) {
        request(context, 1, str, jSONObject, apiListener);
    }

    public void postRequest(Context context, String str, String str2, ApiListener apiListener) {
        JSONObject jSONObject;
        if (str2 != null) {
            try {
                jSONObject = new JSONObject(str2);
            } catch (JSONException e) {
                apiListener.onApiError(new LIApiError("Unable to convert body to json object " + e.toString(), e));
                return;
            }
        } else {
            jSONObject = null;
        }
        postRequest(context, str, jSONObject, apiListener);
    }

    public void putRequest(Context context, String str, JSONObject jSONObject, ApiListener apiListener) {
        request(context, 2, str, jSONObject, apiListener);
    }

    public void putRequest(Context context, String str, String str2, ApiListener apiListener) {
        JSONObject jSONObject;
        if (str2 != null) {
            try {
                jSONObject = new JSONObject(str2);
            } catch (JSONException e) {
                apiListener.onApiError(new LIApiError("Unable to convert body to json object " + e.toString(), e));
                return;
            }
        } else {
            jSONObject = null;
        }
        putRequest(context, str, jSONObject, apiListener);
    }

    public void deleteRequest(Context context, String str, ApiListener apiListener) {
        request(context, 3, str, null, apiListener);
    }

    public void cancelCalls(Context context) {
        QueueManager.getInstance(context).getRequestQueue().cancelAll(context);
    }
}
