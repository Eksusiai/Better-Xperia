package com.sonymobile.calendar.linkedin.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class LinkedInPeopleSearchParser extends LinkedInEmailLookupParser {
    private static final String PEOPLE = "people";

    public LinkedInPeopleSearchParser(JSONObject jSONObject) {
        super(jSONObject);
    }

    @Override // com.sonymobile.calendar.linkedin.model.LinkedInEmailLookupParser
    protected JSONArray getValuesArray() throws JSONException {
        if (!this.mRespons.has(PEOPLE)) {
            return null;
        }
        JSONObject jSONObject = this.mRespons.getJSONObject(PEOPLE);
        if (jSONObject.has("values")) {
            return jSONObject.getJSONArray("values");
        }
        return null;
    }
}
