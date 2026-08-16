package com.sonymobile.calendar;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Base64;
import android.util.Log;
import com.sonymobile.calendar.permissions.PermissionHandlerActivity;
import com.sonymobile.calendar.permissions.PermissionItem;
import com.sonymobile.calendar.utils.PermissionUtils;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes2.dex */
public class GoogleCalendarUriIntentFilter extends PermissionHandlerActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ACTION_RESPOND = "RESPOND";
    private static final int EVENT_INDEX_END = 2;
    private static final int EVENT_INDEX_ID = 0;
    private static final int EVENT_INDEX_START = 1;
    private static final String[] EVENT_PROJECTION = {"_id", LunarContract.EventsColumns.DTSTART, LunarContract.EventsColumns.DTEND, "_sync_id"};
    private static final String QUERY_PARAMETER_ACTION = "action";
    private static final String QUERY_PARAMETER_EID = "eid";
    private static final String QUERY_PARAMETER_RESPONSE_STATUS = "rst";
    private static final String TAG = "GoogleCalendarUriIntentFilter";
    private String mSelection;
    private Uri mUri;

    @Override // android.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private String extractEid(Uri uri) {
        try {
            String queryParameter = uri.getQueryParameter(QUERY_PARAMETER_EID);
            if (queryParameter == null) {
                return null;
            }
            byte[] bArrDecode = Base64.decode(queryParameter, 0);
            for (int i = 0; i < bArrDecode.length; i++) {
                if (bArrDecode[i] == 32) {
                    return new String(bArrDecode, 0, i);
                }
            }
            return null;
        } catch (IllegalArgumentException unused) {
            Log.e(TAG, "Could not decode EID from URI");
            return null;
        }
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        String strExtractEid;
        super.onCreate(bundle);
        Uri data = getIntent().getData();
        this.mUri = data;
        if (data != null && (strExtractEid = extractEid(data)) != null && isEssentialPermissionsGranted()) {
            this.mSelection = "_sync_id LIKE \"%" + strExtractEid + "\"";
            initLoader();
        } else {
            finish();
        }
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public PermissionItem[] getRequiredPermission() {
        return new PermissionItem[]{PermissionUtils.getEssentialCalendarPermissionItem(this)};
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public void onRequestPermissionResult(String[] strArr, int[] iArr) {
        initLoader();
    }

    private void initLoader() {
        getLoaderManager().initLoader(0, null, this);
    }

    @Override // android.app.LoaderManager.LoaderCallbacks
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, CalendarContract.Events.CONTENT_URI, EVENT_PROJECTION, this.mSelection, null, null);
    }

    @Override // android.app.LoaderManager.LoaderCallbacks
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int i = cursor.getInt(0);
            long j = cursor.getLong(1);
            long j2 = cursor.getLong(2);
            int attendeeStatus = getAttendeeStatus();
            Intent intent = new Intent("android.intent.action.VIEW", ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, i));
            intent.putExtra(LunarContract.EXTRA_EVENT_BEGIN_TIME, j);
            intent.putExtra(LunarContract.EXTRA_EVENT_END_TIME, j2);
            if (attendeeStatus != 0) {
                intent.putExtra(LunarContract.AttendeesColumns.ATTENDEE_STATUS, attendeeStatus);
            }
            startActivity(intent);
        } else {
            try {
                startNextMatchingActivity(getIntent());
            } catch (ActivityNotFoundException unused) {
                Log.e(TAG, "No browser installed");
            }
        }
        finish();
    }

    private int getAttendeeStatus() {
        if (!ACTION_RESPOND.equals(this.mUri.getQueryParameter(QUERY_PARAMETER_ACTION))) {
            return 0;
        }
        try {
            int i = Integer.parseInt(this.mUri.getQueryParameter(QUERY_PARAMETER_RESPONSE_STATUS));
            if (i == 1) {
                return 1;
            }
            if (i != 2) {
                return i != 3 ? 0 : 4;
            }
            return 2;
        } catch (NumberFormatException unused) {
            Log.i(TAG, "Invalid attendee status, using default status none...");
            return 0;
        }
    }
}
