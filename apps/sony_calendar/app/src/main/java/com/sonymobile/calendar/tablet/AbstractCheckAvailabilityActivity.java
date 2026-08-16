package com.sonymobile.calendar.tablet;
import com.sonymobile.calendar.SafeTime;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.sonyericsson.calendar.util.CalendarConnectivityManager;
import com.sonymobile.calendar.CalendarScrollView;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.permissions.PermissionHandlerActivity;
import com.sonymobile.calendar.permissions.PermissionItem;
import com.sonymobile.calendar.utils.PermissionUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: loaded from: classes2.dex */
public abstract class AbstractCheckAvailabilityActivity extends PermissionHandlerActivity {
    public static final String AVAILABILITY_CHANGED_TIME_HOUR = "changedTimeHour";
    public static final String AVAILABILITY_NEED_CHANGED_HOUR = "needChangeHour";
    protected static final char AVAILABILITY_STATUS_BUSY = '2';
    protected static final char AVAILABILITY_STATUS_FREE = '0';
    protected static final char AVAILABILITY_STATUS_NO_INFO = '4';
    protected static final char AVAILABILITY_STATUS_OOF = '3';
    protected static final char AVAILABILITY_STATUS_TENTATIVE = '1';
    protected static final int CELL_COLUMNS_COUNT = 24;
    protected static final int DIALOG_FAIL = 2;
    protected static final int DIALOG_FAIL_NO_NETWORK = 4;
    protected static final int DIALOG_NOTE = 1;
    protected static final int DIALOG_PROGRESS = 3;
    protected static final int EAS_QUERY_STATUS = 1;
    public static final String EVENT_AVAILABILITY_ACCOUNT_NAME = "availabilityAccountName";
    public static final String EVENT_AVAILABILITY_ALL_DAY = "availabilityAllDay";
    public static final String EVENT_AVAILABILITY_ATTENDEES_EMAIL = "availabilityAttendeesEmail";
    public static final String EVENT_AVAILABILITY_ATTENDEES_NAME = "availabilityAttendeesName";
    public static final String EVENT_AVAILABILITY_ATTENDEES_OPTIONAL_EMAIL = "availabilityAttendeesOptionalEmail";
    public static final String EVENT_AVAILABILITY_ATTENDEES_OPTIONAL_NAME = "availabilityAttendeesOptionalName";
    public static final String EVENT_AVAILABILITY_END_TIME = "availabilityEndTime";
    public static final String EVENT_AVAILABILITY_START_TIME = "availabilityStartTime";
    public static final String EVENT_AVAILABILITY_TIME_ZONE = "availabilityTimeZone";
    protected static final String EXCHANGE_GAL_AUTHORITY_AVAILABILITY_URI = "content://com.sonymobile.exchange.directory.provider2/contacts/availability/";
    protected static final String EXCHANGE_PARAMETER_ACCOUNT_NAME = "account_name";
    protected static final String EXCHANGE_PARAMETER_END_TIME = "end_time";
    protected static final String EXCHANGE_PARAMETER_START_TIME = "start_time";
    protected static final String EXCHANGE_STATUS_SUCCESS = "1";
    protected static final int FAILED_DIALOG_DISMISS = 4;
    protected static final int FAILED_DIALOG_SHOW = 3;
    protected static final int FAILED_DIALOG_SHOW_NO_NETWORK = 5;
    protected static final String NEW_EXCHANGE_AVAILABILITY_URI = "content://com.sonymobile.exchange.directory.provider/contacts/availability/";
    protected static final String OLD_EXCHANGE_AVAILABILITY_URI = "content://com.android.exchange.directory.provider/contacts/availability/";
    protected static final int PROJECTION_INDEX_DISPLAY_NAME = 2;
    protected static final int PROJECTION_INDEX_EMAIL_ADDRESS = 3;
    protected static final int PROJECTION_INDEX_MERGEDFREEBUSY = 4;
    protected static final int PROJECTION_INDEX_STATUS = 1;
    protected static final int PROJECTION_INDEX_TO = 0;
    protected static final int STATUS_DIALOG_DISMISS = 2;
    protected static final String TAG = "CheckAvailabilityActivity";
    protected static final int TIMEOUT = 15000;
    protected String mAccountName;
    protected char[] mAllAttendeeStatus;
    protected Bitmap mAllStatusBmp;
    protected LinearLayout mAttendees;
    protected int mCellHeight;
    protected int mCellWidth;
    protected Context mContext;
    protected TextView mDateTitle;
    protected float mDownPosX;
    protected float mDownPosY;
    protected AlertDialog mFailedDialog;
    protected HorizontalScrollView mHSV;
    protected int mHalfCellWidth;
    protected int mHalfCellWidthOffset;
    protected boolean mIsAllDay;
    protected int mLineWidth;
    protected boolean mMoveOccured;
    protected float mMoveThresholdDP;
    protected AlertDialog mNoteDialog;
    protected int mOffsetHour;
    protected int mOptionalCount;
    protected String[] mOptionalEmails;
    protected String[] mOptionalNames;
    protected int mOrientation;
    protected int mRequiredCount;
    protected String[] mRequiredEmails;
    protected String[] mRequiredNames;
    protected Resources mResources;
    protected CalendarScrollView mSVAttendee;
    protected CalendarScrollView mSVStatus;
    protected ScrollView mSVTime;
    protected int mSelectedLineWidth;
    protected int mSelectedTime;
    protected ImageView mShadowVerticalIV;
    protected int mSplitWidth;
    protected HashMap mStatus;
    protected Bitmap mStatusBmp;
    protected ProgressDialog mStatusDialog;
    protected LinearLayout mTimeContainer;
    protected String mTimeZone;
    protected int mTitleCellHeight;
    protected Toast mToast;
    protected static final String PROJECTION_AVAILABILITY_TO = "To";
    protected static final String PROJECTION_AVAILABILITY_STATUS = "Status";
    protected static final String PROJECTION_AVAILABILITY_DISPLAY_NAME = "DisplayName";
    protected static final String PROJECTION_AVAILABILITY_EMAIL_ADDRESS = "EmailAddress";
    protected static final String PROJECTION_AVAILABILITY_MERGEDFREEBUSY = "MergedFreeBusy";
    private static final String[] AVAILABILITY_PROJECTION = {PROJECTION_AVAILABILITY_TO, PROJECTION_AVAILABILITY_STATUS, PROJECTION_AVAILABILITY_DISPLAY_NAME, PROJECTION_AVAILABILITY_EMAIL_ADDRESS, PROJECTION_AVAILABILITY_MERGEDFREEBUSY};
    private static final String LOG_FILE = File.separator + "log.txt";
    protected Time mCurrentTime = new SafeTime();
    protected Time mStartTime = new SafeTime();
    protected Time mEndTime = new SafeTime();
    protected TimeOut mTimeOut = new TimeOut();
    protected Object mLock = new Object();
    protected boolean mNeedChangeHour = false;
    protected Point mScreenRect = new Point();
    protected Paint mRecPainter = new Paint();
    protected boolean mIsTimeOut = false;
    protected boolean mIsNeedScrollToSelectedTime = false;
    protected boolean mIsGotStatus = false;
    protected boolean mIsR2L = false;
    protected Handler mHandler = new Handler() { // from class: com.sonymobile.calendar.tablet.AbstractCheckAvailabilityActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        AbstractCheckAvailabilityActivity.this.showDialogs(2);
                    } else if (i != 4) {
                        if (i == 5) {
                            AbstractCheckAvailabilityActivity.this.showDialogs(4);
                        }
                    } else if (AbstractCheckAvailabilityActivity.this.mFailedDialog != null) {
                        AbstractCheckAvailabilityActivity.this.mFailedDialog.dismiss();
                    }
                } else if (AbstractCheckAvailabilityActivity.this.mStatusDialog != null) {
                    AbstractCheckAvailabilityActivity.this.mStatusDialog.dismiss();
                }
            } else if (AbstractCheckAvailabilityActivity.this.mIsTimeOut) {
                AbstractCheckAvailabilityActivity.this.mStatus = null;
                AbstractCheckAvailabilityActivity.this.mAllAttendeeStatus = null;
                return;
            } else if (AbstractCheckAvailabilityActivity.this.mIsGotStatus) {
                AbstractCheckAvailabilityActivity abstractCheckAvailabilityActivity = AbstractCheckAvailabilityActivity.this;
                abstractCheckAvailabilityActivity.drawAllAttendeeStatus(abstractCheckAvailabilityActivity.mSelectedTime);
                AbstractCheckAvailabilityActivity abstractCheckAvailabilityActivity2 = AbstractCheckAvailabilityActivity.this;
                abstractCheckAvailabilityActivity2.drawStatus(abstractCheckAvailabilityActivity2.mSelectedTime);
                if (AbstractCheckAvailabilityActivity.this.mStatusDialog != null) {
                    AbstractCheckAvailabilityActivity.this.mStatusDialog.dismiss();
                }
            }
            if (AbstractCheckAvailabilityActivity.this.mIsNeedScrollToSelectedTime) {
                AbstractCheckAvailabilityActivity.this.mHSV.scrollBy(AbstractCheckAvailabilityActivity.this.mOffsetHour, 0);
            } else {
                AbstractCheckAvailabilityActivity.this.mIsNeedScrollToSelectedTime = true;
            }
            super.handleMessage(message);
        }
    };
    protected Runnable statusLoader = new Runnable() { // from class: com.sonymobile.calendar.tablet.AbstractCheckAvailabilityActivity.2
        @Override // java.lang.Runnable
        public void run() {
            String[] strArr = new String[AbstractCheckAvailabilityActivity.this.mRequiredCount + AbstractCheckAvailabilityActivity.this.mOptionalCount];
            if (AbstractCheckAvailabilityActivity.this.mRequiredCount != 0) {
                System.arraycopy(AbstractCheckAvailabilityActivity.this.mRequiredEmails, 0, strArr, 0, AbstractCheckAvailabilityActivity.this.mRequiredCount);
            }
            if (AbstractCheckAvailabilityActivity.this.mOptionalCount != 0) {
                System.arraycopy(AbstractCheckAvailabilityActivity.this.mOptionalEmails, 0, strArr, AbstractCheckAvailabilityActivity.this.mRequiredCount, AbstractCheckAvailabilityActivity.this.mOptionalCount);
            }
            AbstractCheckAvailabilityActivity abstractCheckAvailabilityActivity = AbstractCheckAvailabilityActivity.this;
            abstractCheckAvailabilityActivity.mStatus = abstractCheckAvailabilityActivity.queryAttendeesStatus(abstractCheckAvailabilityActivity.mContext.getContentResolver(), AbstractCheckAvailabilityActivity.this.mAccountName, strArr, AbstractCheckAvailabilityActivity.this.mStartTime.format2445(), AbstractCheckAvailabilityActivity.this.mEndTime.format2445());
            synchronized (AbstractCheckAvailabilityActivity.this.mLock) {
                AbstractCheckAvailabilityActivity abstractCheckAvailabilityActivity2 = AbstractCheckAvailabilityActivity.this;
                abstractCheckAvailabilityActivity2.mAllAttendeeStatus = abstractCheckAvailabilityActivity2.getAllAttendeesStatus(abstractCheckAvailabilityActivity2.mStatus);
            }
            if (AbstractCheckAvailabilityActivity.this.mStatus != null) {
                synchronized (AbstractCheckAvailabilityActivity.this.mLock) {
                    AbstractCheckAvailabilityActivity.this.mIsGotStatus = true;
                }
            }
            AbstractCheckAvailabilityActivity.this.mHandler.sendEmptyMessage(1);
        }
    };
    protected View.OnTouchListener mOnTouchListener = new View.OnTouchListener() { // from class: com.sonymobile.calendar.tablet.AbstractCheckAvailabilityActivity.6
        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            try {
                int action = motionEvent.getAction();
                if (action == 0) {
                    AbstractCheckAvailabilityActivity.this.mMoveOccured = false;
                    AbstractCheckAvailabilityActivity.this.mDownPosX = motionEvent.getX();
                    AbstractCheckAvailabilityActivity.this.mDownPosY = motionEvent.getY();
                } else if (action != 1) {
                    if (action == 2 && (Math.abs(motionEvent.getX() - AbstractCheckAvailabilityActivity.this.mDownPosX) > AbstractCheckAvailabilityActivity.this.mMoveThresholdDP || Math.abs(motionEvent.getY() - AbstractCheckAvailabilityActivity.this.mDownPosY) > AbstractCheckAvailabilityActivity.this.mMoveThresholdDP)) {
                        AbstractCheckAvailabilityActivity.this.mMoveOccured = true;
                    }
                } else if (!AbstractCheckAvailabilityActivity.this.mMoveOccured && AbstractCheckAvailabilityActivity.this.mIsGotStatus && motionEvent.getY() <= AbstractCheckAvailabilityActivity.this.mCellHeight * (AbstractCheckAvailabilityActivity.this.mRequiredCount + AbstractCheckAvailabilityActivity.this.mOptionalCount)) {
                    AbstractCheckAvailabilityActivity.this.changeTime(motionEvent);
                }
            } catch (IllegalArgumentException unused) {
            }
            return false;
        }
    };

    protected abstract void drawAllAttendeeStatus(int i);

    protected abstract void drawStatus(int i);

    protected abstract void drawTime(int i);

    protected abstract void sendResult();

    protected abstract void showDialogProgres();

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r5v12 */
    /* JADX WARN: Type inference failed for: r5v14 */
    /* JADX WARN: Type inference failed for: r5v19 */
    /* JADX WARN: Type inference failed for: r5v21 */
    /* JADX WARN: Type inference failed for: r5v22 */
    /* JADX WARN: Type inference failed for: r5v23 */
    /* JADX WARN: Type inference failed for: r5v24 */
    /* JADX WARN: Type inference failed for: r5v6 */
    /* JADX WARN: Type inference failed for: r5v7 */
    /* JADX WARN: Type inference failed for: r5v8 */
    /* JADX WARN: Type inference failed for: r5v9, types: [java.io.OutputStream] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:22:0x0065 -> B:30:0x006c). Please report as a decompilation issue!!! */
    public static void setLog(String str, Activity activity) {
        File file = new File(activity.getApplicationInfo().dataDir + LOG_FILE);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            try {
                byte[] bytes = (str + "\n").getBytes();
                int length = bytes.length;
                for (byte b : bytes) {
                    fileOutputStream.write(b);
                }
                fileOutputStream.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e2) {
                        Log.e(TAG, e2.getMessage());
                    }
                }
            }
        } catch (IOException e4) {
            Log.e(TAG, e4.getMessage());
        }
    }

    protected void loadFromDatabase() {
        Thread thread = new Thread(this.statusLoader);
        thread.setPriority(1);
        thread.start();
        this.mHandler.postDelayed(this.mTimeOut, 15000L);
    }

    public HashMap queryAttendeesStatus(ContentResolver contentResolver, String str, String[] strArr, String str2, String str3) {
        String availabilityUri = getAvailabilityUri(OLD_EXCHANGE_AVAILABILITY_URI, str, str2, str3);
        Uri uri = Uri.parse(availabilityUri.toString());
        setLog("CheckAvailability queryAttendeesStatus: whole URI is " + availabilityUri.toString(), this);
        for (String str4 : strArr) {
            setLog("CheckAvailability queryAttendeesStatus: selectionArgs is " + str4, this);
        }
        if (!CalendarConnectivityManager.isNetworkAvailable(this)) {
            this.mHandler.sendEmptyMessage(2);
            this.mHandler.sendEmptyMessage(5);
            this.mHandler.removeCallbacks(this.mTimeOut);
            return null;
        }
        String[] strArr2 = AVAILABILITY_PROJECTION;
        Cursor cursorQuery = contentResolver.query(uri, strArr2, null, strArr, null);
        if (cursorQuery == null) {
            cursorQuery = contentResolver.query(Uri.parse(getAvailabilityUri(NEW_EXCHANGE_AVAILABILITY_URI, str, str2, str3).toString()), strArr2, null, strArr, null);
        }
        if (cursorQuery == null) {
            cursorQuery = contentResolver.query(Uri.parse(getAvailabilityUri(EXCHANGE_GAL_AUTHORITY_AVAILABILITY_URI, str, str2, str3).toString()), strArr2, null, strArr, null);
        }
        if (cursorQuery == null) {
            if (!this.mIsTimeOut) {
                this.mHandler.sendEmptyMessage(2);
                this.mHandler.sendEmptyMessage(3);
                this.mHandler.removeCallbacks(this.mTimeOut);
            }
            return null;
        }
        HashMap map = new HashMap();
        while (cursorQuery.moveToNext()) {
            try {
                try {
                    if (EXCHANGE_STATUS_SUCCESS.equals(cursorQuery.getString(1))) {
                        map.put(cursorQuery.getString(0), cursorQuery.getString(4));
                    }
                } catch (Exception e) {
                    setLog("CheckAvailability queryAttendeesStatus: cursor Exception: " + e.getMessage(), this);
                    Log.e(TAG, "CheckAvailability queryAttendeesStatus: cursor error: " + e.getMessage());
                }
            } catch (Throwable th) {
                cursorQuery.close();
                throw th;
            }
        }
        cursorQuery.close();
        return map;
    }

    private String getAvailabilityUri(String str, String str2, String str3, String str4) {
        StringBuilder sb = new StringBuilder();
        sb.append(str).append("?").append("account_name").append("=").append(str2).append("&").append(EXCHANGE_PARAMETER_START_TIME).append("=").append(str3).append("&").append(EXCHANGE_PARAMETER_END_TIME).append("=").append(str4);
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public char[] getAllAttendeesStatus(HashMap map) {
        if (map == null || map.size() == 0) {
            return null;
        }
        Iterator it = map.values().iterator();
        char[] cArr = new char[48];
        for (int i = 0; i < 48; i++) {
            cArr[i] = AVAILABILITY_STATUS_FREE;
        }
        while (it.hasNext()) {
            char[] cArr2 = new char[48];
            ((String) it.next()).getChars(0, 48, cArr2, 0);
            for (int i2 = 0; i2 < 48; i2++) {
                if (cArr2[i2] != '4') {
                    cArr[i2] = (char) Math.max((int) cArr[i2], (int) cArr2[i2]);
                }
            }
        }
        setLog("CheckAvailability getAllAttendeesStatus: All Attendee status is " + new String(cArr), this);
        return cArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    class TimeOut implements Runnable {
        private TimeOut() {
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (AbstractCheckAvailabilityActivity.this.mLock) {
                if (!AbstractCheckAvailabilityActivity.this.mIsGotStatus) {
                    AbstractCheckAvailabilityActivity.this.mIsTimeOut = true;
                    AbstractCheckAvailabilityActivity.this.mHandler.sendEmptyMessage(2);
                    AbstractCheckAvailabilityActivity.this.mHandler.removeMessages(1);
                    AbstractCheckAvailabilityActivity.this.mHandler.sendEmptyMessage(3);
                }
            }
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        sendResult();
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            sendResult();
        } else if (itemId == R.id.item_id_note) {
            showDialogs(1);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.availability_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected void showDialogs(int i) {
        if (i == 1) {
            AlertDialog alertDialogCreate = new AlertDialog.Builder(this, R.style.AlertDialogTheme).setTitle(this.mResources.getString(R.string.popup_title_availability_info)).setView((ScrollView) ((LayoutInflater) getApplicationContext().getSystemService("layout_inflater")).inflate(R.layout.dialog_note_view, (ViewGroup) null)).setPositiveButton(this.mResources.getString(R.string.clr_strings_button_title_ok_txt), new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.tablet.AbstractCheckAvailabilityActivity.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    dialogInterface.dismiss();
                }
            }).create();
            this.mNoteDialog = alertDialogCreate;
            alertDialogCreate.getWindow().addFlags(65792);
            this.mNoteDialog.show();
            return;
        }
        if (i == 2) {
            AlertDialog alertDialogCreate2 = new AlertDialog.Builder(this, R.style.AlertDialogTheme).setTitle(this.mResources.getString(R.string.availability_loading_failed_title_label)).setMessage(this.mResources.getString(R.string.availability_loading_failed_body_label)).setPositiveButton(this.mResources.getString(R.string.clr_strings_button_title_ok_txt), new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.tablet.AbstractCheckAvailabilityActivity.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    dialogInterface.dismiss();
                }
            }).create();
            this.mFailedDialog = alertDialogCreate2;
            alertDialogCreate2.show();
        } else if (i == 3) {
            showDialogProgres();
        } else {
            if (i != 4) {
                return;
            }
            AlertDialog alertDialogCreate3 = new AlertDialog.Builder(this, R.style.AlertDialogTheme).setTitle(this.mResources.getString(R.string.availability_loading_failed_title_label)).setMessage(this.mResources.getString(R.string.availability_loading_failed_no_network_body_label)).setPositiveButton(this.mResources.getString(R.string.clr_strings_button_title_ok_txt), new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.tablet.AbstractCheckAvailabilityActivity.5
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    dialogInterface.dismiss();
                }
            }).create();
            this.mFailedDialog = alertDialogCreate3;
            alertDialogCreate3.show();
        }
    }

    protected void changeTime(MotionEvent motionEvent) {
        String strValueOf;
        String strValueOf2;
        int i = this.mSelectedTime;
        int x = ((int) motionEvent.getX()) / this.mCellWidth;
        this.mSelectedTime = x;
        if (x < 0) {
            this.mSelectedTime = 0;
        }
        if (this.mSelectedTime > 23) {
            this.mSelectedTime = 23;
        }
        if (i == this.mSelectedTime) {
            return;
        }
        setLog("CheckAvailability changeTime: mSelectedTime is " + this.mSelectedTime, this);
        if (this.mIsR2L) {
            this.mSelectedTime = 23 - this.mSelectedTime;
        }
        drawTime(this.mSelectedTime);
        drawAllAttendeeStatus(this.mSelectedTime);
        this.mNeedChangeHour = true;
        int i2 = this.mSelectedTime;
        if (i2 < 10) {
            strValueOf = "0" + this.mSelectedTime;
            int i3 = this.mSelectedTime;
            if (i3 == 9) {
                strValueOf2 = String.valueOf(i3 + 1);
            } else {
                strValueOf2 = "0" + (this.mSelectedTime + 1);
            }
        } else {
            strValueOf = String.valueOf(i2);
            strValueOf2 = String.valueOf(this.mSelectedTime + 1);
        }
        showToast(String.format(getString(R.string.toast_time_update), strValueOf, "00", strValueOf2, "00"));
    }

    protected void showToast(String str) {
        Toast toast = this.mToast;
        if (toast == null) {
            this.mToast = Toast.makeText(this.mContext, str, 0);
        } else {
            toast.setText(str);
        }
        this.mToast.show();
    }

    protected void drawCell(Canvas canvas, char c, int i, int i2, int i3, int i4) {
        this.mRecPainter.setStyle(Paint.Style.FILL);
        switch (c) {
            case '1':
                this.mRecPainter.setColor(ContextCompat.getColor(this.mContext, R.color.tablet_new_event_availability_tent));
                break;
            case '2':
                this.mRecPainter.setColor(ContextCompat.getColor(this.mContext, R.color.tablet_new_event_availability_busy));
                break;
            case '3':
                this.mRecPainter.setColor(ContextCompat.getColor(this.mContext, R.color.tablet_new_event_availability_oof));
                break;
            case '4':
                this.mRecPainter.setColor(ContextCompat.getColor(this.mContext, R.color.tablet_new_event_availability_noin));
                break;
            default:
                return;
        }
        canvas.drawRect(i, i2, i3, i4, this.mRecPainter);
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public PermissionItem[] getRequiredPermission() {
        return new PermissionItem[]{PermissionUtils.getEssentialCalendarPermissionItem(this), new PermissionItem("android.permission.READ_CONTACTS", null, null, R.drawable.ic_contact, true)};
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public void onRequestPermissionResult(String[] strArr, int[] iArr) {
        loadFromDatabase();
    }
}
