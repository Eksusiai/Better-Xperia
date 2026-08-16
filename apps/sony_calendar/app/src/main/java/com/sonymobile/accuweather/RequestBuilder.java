package com.sonymobile.accuweather;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import com.sonyericsson.calendar.util.CalendarConstants;
import com.sonymobile.calendar.R;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class RequestBuilder {
    private static final String ALERTS_FIND = "alerts/v1/";
    private static final String API_KEY_PREFIX = "apikey=";
    private static final String AUTOCOMPLETE_FIND = "locations/v1/cities/autocomplete.json?q=";
    private static final String DETAILS = "&details=true";
    private static final String FORECAST_FIND = "forecasts/v1/daily/5day/";
    private static String HOST = null;
    private static final String HOURLY_FIND = "forecasts/v1/hourly/72hour/";
    private static final String JSON = ".json?";
    private static final String LANGUAGE = "&language=";
    private static final String LOCATIONS_FIND = "locations/v1/";
    private static final String LOCATION_FIND = "locations/v1/cities/search?q=";
    private static final String LOCATION_GEO_FIND = "locations/v1/cities/geoposition/search.json?q=";
    private static final String PROTOCOL = "https";
    private static final String TAG = "RequestBuilder";
    private static String URL_PREFIX = null;
    private static final String WEATHER_FIND = "currentconditions/v1/";

    static URL buildFindLocationRequestUrl(Context context, String str, boolean z, boolean z2) {
        String strReplace;
        Objects.requireNonNull(str, "searchTerm must not be null");
        int iIndexOf = str.indexOf(47);
        if (iIndexOf > 0) {
            str = str.substring(0, iIndexOf).trim();
        }
        try {
            strReplace = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException unused) {
            strReplace = str.replace(" ", "%20");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(z2 ? AUTOCOMPLETE_FIND : LOCATION_FIND).append(strReplace).append("&").append(API_KEY_PREFIX).append(new ApiKey(context));
        appendLanguageCode(sb, context.getResources(), z);
        return createUrl(sb);
    }

    private static URL createUrl(StringBuilder sb) {
        try {
            return new URL(PROTOCOL, HOST, sb.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Problem building URL request.", e);
            return null;
        }
    }

    static URL buildCurrentRequestUrl(Context context, String str, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append(WEATHER_FIND).append(removeOldPrefix(str)).append('?').append(API_KEY_PREFIX).append(new ApiKey(context)).append(DETAILS);
        appendLanguageCode(sb, context.getResources(), z);
        return createUrl(sb);
    }

    static URL buildForecastRequestUrl(Context context, String str, boolean z) {
        return buildRequestUrl(context, str, z, FORECAST_FIND, true);
    }

    private static URL buildRequestUrl(Context context, String str, boolean z, String str2, boolean z2) {
        StringBuilder sb = new StringBuilder();
        sb.append(str2).append(removeOldPrefix(str)).append('?').append(API_KEY_PREFIX).append(new ApiKey(context));
        if (z2) {
            sb.append(DETAILS);
        }
        appendLanguageCode(sb, context.getResources(), z);
        return createUrl(sb);
    }

    static URL buildLocationRequestUrl(Context context, double d, double d2, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append(LOCATION_GEO_FIND).append(d).append(',').append(d2).append('&').append(API_KEY_PREFIX).append(new ApiKey(context));
        appendLanguageCode(sb, context.getResources(), z);
        return createUrl(sb);
    }

    public static URL buildLocationRequestUrlFromId(Context context, String str, boolean z) {
        return buildRequestUrlFromId(context, str, z, LOCATION_FIND);
    }

    public static URL buildAlertsRequestUrlFromId(Context context, String str) {
        return buildRequestUrlFromId(context, str, false, ALERTS_FIND);
    }

    private static URL buildRequestUrlFromId(Context context, String str, boolean z, String str2) {
        String strRemoveOldPrefix = removeOldPrefix(str);
        StringBuilder sb = new StringBuilder();
        sb.append(str2).append(strRemoveOldPrefix).append(JSON).append(API_KEY_PREFIX).append(new ApiKey(context));
        appendLanguageCode(sb, context.getResources(), z);
        return createUrl(sb);
    }

    static URL buildLocationDetailsRequestUrlFromId(Context context, String str, boolean z) {
        String strRemoveOldPrefix = removeOldPrefix(str);
        StringBuilder sb = new StringBuilder();
        sb.append(LOCATIONS_FIND).append(strRemoveOldPrefix).append(JSON).append(API_KEY_PREFIX).append(new ApiKey(context));
        appendLanguageCode(sb, context.getResources(), z);
        return createUrl(sb);
    }

    private static String getDefaultSupportedLanguageCode(Resources resources) {
        Locale locale = Locale.getDefault();
        if (locale == null) {
            return null;
        }
        String language = locale.getLanguage();
        if (language.isEmpty()) {
            return null;
        }
        if ("iw".equals(language)) {
            language = "he";
        } else if ("in".equals(language)) {
            language = "id";
        } else if ("ji".equals(language)) {
            language = "yi";
        } else if ("tl".equals(language)) {
            language = "ph";
        } else if ("nb".equals(language)) {
            language = "no";
        }
        String country = locale.getCountry();
        if (!country.isEmpty()) {
            String str = language + CalendarConstants.HYPHEN + country.toLowerCase(Locale.US);
            for (String str2 : resources.getStringArray(R.array.accuweather_supported_lang_w_countries)) {
                if (str2.equals(str)) {
                    return str;
                }
            }
        }
        for (String str3 : resources.getStringArray(R.array.accuweather_supported_lang)) {
            if (str3.equals(language)) {
                return language;
            }
        }
        return null;
    }

    private static void appendLanguageCode(StringBuilder sb, Resources resources, boolean z) {
        String defaultSupportedLanguageCode;
        if (z || (defaultSupportedLanguageCode = getDefaultSupportedLanguageCode(resources)) == null) {
            return;
        }
        sb.append(LANGUAGE).append(defaultSupportedLanguageCode);
    }

    private static String removeOldPrefix(String str) {
        return (str == null || !str.toLowerCase(Locale.US).contains("cityid:")) ? str : str.substring(7);
    }

    public static void init(String str) {
        URL_PREFIX = str;
        HOST = URL_PREFIX + ".accuweather.com";
    }
}
