package com.sonymobile.calendar.linkedin.model;

import android.util.Log;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class LinkedInEmailLookupParser {
    private static final String DISTANCE = "distance";
    private static final String EMAIL_KEY = "email=";
    private static final String FIRST_NAME = "firstName";
    private static final String HEADLINE = "headline";
    private static final String ID = "id";
    private static final String INDUSTRY = "industry";
    private static final String KEY = "_key";
    private static final String LAST_NAME = "lastName";
    private static final String LOCATION = "location";
    private static final String NAME = "name";
    private static final String PICTURE_URL = "pictureUrl";
    private static final String TAG = "LinkedInEmailLookupParser";
    protected static final String VALUES = "values";
    protected JSONObject mRespons;

    public LinkedInEmailLookupParser(JSONObject jSONObject) {
        this.mRespons = jSONObject;
    }

    public ArrayList<LinkedInContact> parse() {
        ArrayList<LinkedInContact> arrayList = new ArrayList<>();
        try {
            JSONArray valuesArray = getValuesArray();
            if (valuesArray != null) {
                for (int i = 0; i < valuesArray.length(); i++) {
                    arrayList.add(parseOneContacts(valuesArray.getJSONObject(i)));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
        return arrayList;
    }

    protected JSONArray getValuesArray() throws JSONException {
        if (this.mRespons.has(VALUES)) {
            return this.mRespons.getJSONArray(VALUES);
        }
        return null;
    }

    private LinkedInContact parseOneContacts(JSONObject jSONObject) throws JSONException {
        LinkedInContact linkedInContact = new LinkedInContact();
        if (jSONObject.has(ID)) {
            linkedInContact.setLinkedinId(jSONObject.getString(ID));
        }
        if (jSONObject.has(FIRST_NAME)) {
            linkedInContact.setFirstName(jSONObject.getString(FIRST_NAME));
        }
        if (jSONObject.has(LAST_NAME)) {
            linkedInContact.setLastName(jSONObject.getString(LAST_NAME));
        }
        if (jSONObject.has(DISTANCE)) {
            linkedInContact.setConnectionLevel(jSONObject.getInt(DISTANCE));
        }
        if (jSONObject.has(PICTURE_URL)) {
            linkedInContact.setPhotoUrl(jSONObject.getString(PICTURE_URL));
        }
        if (jSONObject.has(KEY)) {
            linkedInContact.setEmail(jSONObject.getString(KEY).substring(6));
        }
        if (jSONObject.has(HEADLINE)) {
            linkedInContact.setmHeadline(jSONObject.getString(HEADLINE));
        }
        if (jSONObject.has(INDUSTRY)) {
            linkedInContact.setIndustry(jSONObject.getString(INDUSTRY));
        }
        if (jSONObject.has("location")) {
            JSONObject jSONObject2 = jSONObject.getJSONObject("location");
            if (jSONObject2.has("name")) {
                linkedInContact.setLocation(jSONObject2.getString("name"));
            }
        }
        return linkedInContact;
    }
}
