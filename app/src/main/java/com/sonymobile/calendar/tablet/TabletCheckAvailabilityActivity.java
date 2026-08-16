package com.sonymobile.calendar.tablet;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.sonymobile.calendar.BindSVManager;
import com.sonymobile.calendar.CalendarApplication;
import com.sonymobile.calendar.CalendarScrollView;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.utils.UiUtils;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class TabletCheckAvailabilityActivity extends AbstractCheckAvailabilityActivity {
    public static final String AVAILABILITY_CHANGED_TIME_DATE = "changedTimeDate";
    public static final String AVAILABILITY_NEED_CHANGED_TIME_DATE = "needChangeTimeDate";
    private static final int CELL_COLUMNS_LANDSCAPE = 9;
    private static final int CELL_COLUMNS_PORTRAIT = 6;
    private static final float CELL_HEIGHT = 47.4f;
    private static final int LANDSCAPE_END_MOVE_HOUR = 19;
    private static final int LANDSCAPE_MAX_MOVE_HOUR = 15;
    private static final int LANDSCAPE_START_MOVE_HOUR = 4;
    private static final int LEFT_BUTTON = -1;
    private static final int LINE_WIDTH = 1;
    private static final int PORTRAIT_END_MOVE_HOUR = 20;
    private static final int PORTRAIT_MAX_MOVE_HOUR = 18;
    private static final int PORTRAIT_START_MOVE_HOUR = 2;
    private static final int RIGHT_BUTTON = 1;
    private static final int SELECTED_LINE_WIDTH = 2;
    private static final int SHADOW_LINE_WIDTH = 7;
    private static final int TIME_CELL_HEIGHT = 31;
    private int mAllAttendeesCellHeight;
    private ImageButton mLeftButton;
    private boolean mNeedChangeTimeDate;
    private ImageButton mRightButton;

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.checkavailability_activity);
        this.mResources = getResources();
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle(this.mResources.getString(R.string.check_availability_label));
        UiUtils.setViewBackgroundToPrimaryColor(this, toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadData();
        init();
        if (isEssentialPermissionsGranted()) {
            loadFromDatabase();
        }
        this.mLeftButton = (ImageButton) findViewById(R.id.left_button);
        this.mRightButton = (ImageButton) findViewById(R.id.right_button);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        this.mTimeContainer.removeAllViews();
        this.mAttendees.removeAllViews();
        this.mHandler.removeCallbacksAndMessages(null);
        if (this.mNoteDialog != null) {
            this.mNoteDialog.dismiss();
            this.mNoteDialog = null;
        }
        if (this.mStatusDialog != null) {
            this.mStatusDialog.dismiss();
            this.mStatusDialog = null;
        }
        if (this.mFailedDialog != null) {
            this.mFailedDialog.dismiss();
            this.mFailedDialog = null;
        }
        if (this.mAllStatusBmp != null) {
            this.mAllStatusBmp.recycle();
        }
        if (this.mStatusBmp != null) {
            this.mStatusBmp.recycle();
        }
        if (this.mToast != null) {
            this.mToast.cancel();
            this.mToast = null;
        }
        super.onDestroy();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        getWindowManager().getDefaultDisplay().getSize(this.mScreenRect);
        this.mSplitWidth = this.mScreenRect.x / 4;
        this.mOrientation = this.mResources.getConfiguration().orientation;
        this.mIsR2L = CalendarApplication.isR2L(this.mResources);
        if (this.mOrientation == 2) {
            this.mCellWidth = (this.mScreenRect.x - this.mSplitWidth) / 9;
        } else {
            this.mCellWidth = (this.mScreenRect.x - this.mSplitWidth) / 6;
        }
        this.mHalfCellWidth = this.mCellWidth / 2;
        this.mHalfCellWidthOffset = this.mCellWidth % 2;
        drawTime(this.mSelectedTime);
        drawAllAttendeeStatus(this.mSelectedTime);
        drawStatus(this.mSelectedTime, '4');
        if (this.mIsR2L) {
            this.mShadowVerticalIV.setImageResource(R.drawable.divider_shadow_vertical_inverse);
        } else {
            this.mShadowVerticalIV.setImageResource(R.drawable.divider_shadow_vertical);
        }
    }

    private void loadData() {
        Intent intent = getIntent();
        long longExtra = intent.getLongExtra(AbstractCheckAvailabilityActivity.EVENT_AVAILABILITY_START_TIME, 0L);
        this.mTimeZone = intent.getStringExtra(AbstractCheckAvailabilityActivity.EVENT_AVAILABILITY_TIME_ZONE);
        this.mIsAllDay = intent.getBooleanExtra(AbstractCheckAvailabilityActivity.EVENT_AVAILABILITY_ALL_DAY, false);
        this.mAccountName = intent.getStringExtra(AbstractCheckAvailabilityActivity.EVENT_AVAILABILITY_ACCOUNT_NAME);
        this.mRequiredNames = intent.getStringArrayExtra(AbstractCheckAvailabilityActivity.EVENT_AVAILABILITY_ATTENDEES_NAME);
        this.mRequiredEmails = intent.getStringArrayExtra(AbstractCheckAvailabilityActivity.EVENT_AVAILABILITY_ATTENDEES_EMAIL);
        this.mOptionalNames = intent.getStringArrayExtra(AbstractCheckAvailabilityActivity.EVENT_AVAILABILITY_ATTENDEES_OPTIONAL_NAME);
        this.mOptionalEmails = intent.getStringArrayExtra(AbstractCheckAvailabilityActivity.EVENT_AVAILABILITY_ATTENDEES_OPTIONAL_EMAIL);
        if (this.mIsAllDay) {
            this.mCurrentTime.timezone = "UTC";
            this.mCurrentTime.set(longExtra);
            this.mCurrentTime.hour = 0;
            this.mCurrentTime.minute = 0;
            this.mCurrentTime.second = 0;
        } else {
            this.mCurrentTime.timezone = this.mTimeZone;
            this.mCurrentTime.set(longExtra);
        }
        getWindowManager().getDefaultDisplay().getSize(this.mScreenRect);
        this.mOrientation = this.mResources.getConfiguration().orientation;
        this.mMoveThresholdDP = this.mResources.getDisplayMetrics().density * 20.0f;
        this.mSplitWidth = this.mScreenRect.x / 4;
        this.mLineWidth = Utils.dp2px(this, 1.0f);
        this.mAllAttendeesCellHeight = Utils.dp2px(this, CELL_HEIGHT);
        this.mCellHeight = Utils.dp2px(this, CELL_HEIGHT);
        this.mSelectedLineWidth = Utils.dp2px(this, 2.0f);
        this.mIsR2L = CalendarApplication.isR2L(this.mResources);
    }

    private void init() {
        if ("".equals(this.mAccountName)) {
            return;
        }
        if (this.mRequiredEmails != null && this.mRequiredEmails.length > 0) {
            this.mRequiredCount = this.mRequiredEmails.length;
        } else {
            this.mRequiredCount = 0;
        }
        if (this.mOptionalEmails != null && this.mOptionalEmails.length > 0) {
            this.mOptionalCount = this.mOptionalEmails.length;
        } else {
            this.mOptionalCount = 0;
        }
        setLog("CheckAvailability init: mRequiredCount is " + this.mRequiredCount, this);
        setLog("CheckAvailability init: mOptionalCount is " + this.mOptionalCount, this);
        if (this.mRequiredCount == 0 && this.mOptionalCount == 0) {
            return;
        }
        showDialogs(3);
        this.mContext = this;
        this.mDateTitle = (TextView) findViewById(R.id.date);
        this.mHSV = (HorizontalScrollView) findViewById(R.id.hsv);
        this.mSVStatus = (CalendarScrollView) findViewById(R.id.svr);
        this.mSVAttendee = (CalendarScrollView) findViewById(R.id.svl);
        this.mSVTime = (ScrollView) findViewById(R.id.timeScrollView);
        this.mShadowVerticalIV = (ImageView) findViewById(R.id.divider_shadow_v);
        setStartAndEndTime();
        if (this.mOrientation == 2) {
            this.mCellWidth = (this.mScreenRect.x - this.mSplitWidth) / 9;
        } else {
            this.mCellWidth = (this.mScreenRect.x - this.mSplitWidth) / 6;
        }
        this.mHalfCellWidth = this.mCellWidth / 2;
        this.mHalfCellWidthOffset = this.mCellWidth % 2;
        this.mSelectedTime = this.mCurrentTime.hour;
        this.mSVStatus.name = 1;
        this.mSVAttendee.name = 2;
        BindSVManager.setCalendarSVA(this.mSVStatus);
        BindSVManager.setCalendarSVB(this.mSVAttendee);
        this.mSVStatus.setOnTouchListener(this.mOnTouchListener);
        this.mSVTime.setOnTouchListener(this.mOnTouchListener);
        drawTitle();
        drawTime(this.mSelectedTime);
        drawAttendee();
        if (this.mIsR2L) {
            this.mShadowVerticalIV.setImageResource(R.drawable.divider_shadow_vertical_inverse);
        } else {
            this.mShadowVerticalIV.setImageResource(R.drawable.divider_shadow_vertical);
        }
        computerScrollOffset(this.mSelectedTime);
        if (this.mIsNeedScrollToSelectedTime) {
            this.mHSV.scrollBy(this.mOffsetHour, 0);
        } else {
            this.mIsNeedScrollToSelectedTime = true;
        }
    }

    private void computerScrollOffset(int i) {
        if (this.mIsR2L) {
            i = 23 - i;
        }
        if (this.mOrientation == 2) {
            if (i > 4 && i < 19) {
                this.mOffsetHour = this.mCellWidth * (i - 4);
                return;
            } else if (i >= 19) {
                this.mOffsetHour = this.mCellWidth * 15;
                return;
            } else {
                this.mOffsetHour = 0;
                return;
            }
        }
        if (i > 2 && i < 20) {
            this.mOffsetHour = this.mCellWidth * (i - 2);
        } else if (i >= 20) {
            this.mOffsetHour = this.mCellWidth * 18;
        } else {
            this.mOffsetHour = 0;
        }
    }

    @Override // com.sonymobile.calendar.tablet.AbstractCheckAvailabilityActivity
    protected void changeTime(MotionEvent motionEvent) {
        super.changeTime(motionEvent);
        drawStatus(this.mSelectedTime, '4');
    }

    @Override // com.sonymobile.calendar.tablet.AbstractCheckAvailabilityActivity
    protected void drawTime(int i) {
        LayoutInflater layoutInflater = getLayoutInflater();
        this.mTimeContainer = (LinearLayout) findViewById(R.id.timeContainer);
        this.mTimeContainer.removeAllViews();
        for (int i2 = 0; i2 < 24; i2++) {
            View viewInflate = layoutInflater.inflate(R.layout.hour_item, (ViewGroup) null);
            this.mTimeContainer.addView(viewInflate, this.mCellWidth, Utils.dp2px(this, 32.0f));
            TextView textView = (TextView) viewInflate.findViewById(R.id.hourLable);
            textView.setTypeface(Typeface.DEFAULT);
            if (i2 < 10) {
                textView.setText("0" + i2 + ":00");
            } else {
                textView.setText(String.valueOf(i2) + ":00");
            }
            if (i2 == i) {
                ((ImageView) viewInflate.findViewById(R.id.selectedTopLine)).setVisibility(0);
                if (this.mIsR2L) {
                    if (i2 != 23) {
                        ((ImageView) viewInflate.findViewById(R.id.selectedYLine)).setVisibility(0);
                    }
                } else if (i2 != 0) {
                    ((ImageView) viewInflate.findViewById(R.id.selectedYLine)).setVisibility(0);
                }
            }
            if (i2 == 0 && !this.mIsR2L) {
                ((ImageView) viewInflate.findViewById(R.id.liney)).setVisibility(8);
            }
            if (i2 == i + 1) {
                ((ImageView) viewInflate.findViewById(R.id.selectedYLine)).setVisibility(0);
            }
        }
    }

    private void drawAttendee() {
        LayoutInflater layoutInflater = getLayoutInflater();
        this.mAttendees = (LinearLayout) findViewById(R.id.attendees);
        this.mAttendees.removeAllViews();
        addAttendeeView(true, layoutInflater, this.mRequiredCount, this.mRequiredNames, this.mRequiredEmails);
        addAttendeeView(false, layoutInflater, this.mOptionalCount, this.mOptionalNames, this.mOptionalEmails);
        this.mAttendees.addView(layoutInflater.inflate(R.layout.attendee_item, (ViewGroup) null), new ViewGroup.LayoutParams(-1, this.mSelectedLineWidth));
    }

    @Override // com.sonymobile.calendar.tablet.AbstractCheckAvailabilityActivity
    protected void drawAllAttendeeStatus(int i) {
        if (this.mAllAttendeeStatus == null || this.mAllAttendeeStatus.length < 1) {
            return;
        }
        setLog("CheckAvailability drawAllAttendeeStatus: mAllAttendeeStatus is " + new String(this.mAllAttendeeStatus), this);
        if (this.mAllStatusBmp != null) {
            this.mAllStatusBmp.recycle();
        }
        this.mAllStatusBmp = Bitmap.createBitmap(this.mCellWidth * 24, this.mAllAttendeesCellHeight + 1, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.mAllStatusBmp);
        Bitmap bitmapDecodeResource = BitmapFactory.decodeResource(this.mResources, R.drawable.divider_horizontal_bright_thick);
        if (this.mIsR2L) {
            i = 23 - i;
            char[] cArr = new char[48];
            for (int i2 = 0; i2 < 48; i2++) {
                cArr[i2] = this.mAllAttendeeStatus[47 - i2];
            }
            setLog("CheckAvailability drawAllAttendeeStatus: mAllAttendeeStatus(R2L) is " + new String(this.mAllAttendeeStatus), this);
            addAllStatus(canvas, cArr);
        } else {
            addAllStatus(canvas, this.mAllAttendeeStatus);
        }
        canvas.drawBitmap(bitmapDecodeResource, (Rect) null, new Rect(0, this.mAllAttendeesCellHeight, this.mCellWidth * 24, this.mAllAttendeesCellHeight + this.mLineWidth), (Paint) null);
        Rect rect = new Rect(this.mCellWidth * i, 0, (this.mCellWidth * i) + this.mSelectedLineWidth, this.mAllAttendeesCellHeight + this.mLineWidth);
        this.mRecPainter.setColor(ContextCompat.getColor(this.mContext, R.color.new_event_availability_blue));
        if (i != 0) {
            canvas.drawRect(rect, this.mRecPainter);
        }
        rect.left += this.mCellWidth;
        rect.right += this.mCellWidth;
        canvas.drawRect(rect, this.mRecPainter);
        ((ImageView) findViewById(R.id.allStatus)).setImageBitmap(this.mAllStatusBmp);
        bitmapDecodeResource.recycle();
    }

    private void addAllStatus(Canvas canvas, char[] cArr) {
        Bitmap bitmapDecodeResource = BitmapFactory.decodeResource(this.mResources, R.drawable.divider_availability_vertical_bright);
        if (this.mAllAttendeeStatus == null || this.mAllAttendeeStatus.length < 1) {
            return;
        }
        synchronized (this.mLock) {
            for (int i = 0; i < 48; i++) {
                int i2 = this.mAllAttendeesCellHeight;
                if (i % 2 != 0) {
                    drawCell(canvas, cArr[i], (this.mCellWidth * (i / 2)) + this.mHalfCellWidth + this.mHalfCellWidthOffset, 1, this.mCellWidth * ((i / 2) + 1), i2);
                    canvas.drawBitmap(bitmapDecodeResource, (Rect) null, new Rect(this.mCellWidth * ((i / 2) + 1), 1, (this.mCellWidth * ((i / 2) + 1)) + this.mLineWidth, i2), (Paint) null);
                } else if (i != 0) {
                    if (cArr[i] == cArr[i - 1]) {
                        drawCell(canvas, cArr[i], this.mCellWidth * (i / 2), 1, (this.mCellWidth * (i / 2)) + this.mHalfCellWidth + this.mHalfCellWidthOffset, i2);
                    } else {
                        drawCell(canvas, cArr[i], (this.mCellWidth * (i / 2)) + this.mLineWidth, 1, (this.mCellWidth * (i / 2)) + this.mHalfCellWidth + this.mHalfCellWidthOffset, i2);
                    }
                } else {
                    drawCell(canvas, cArr[i], 0, 1, (this.mCellWidth * (i / 2)) + this.mHalfCellWidth + this.mHalfCellWidthOffset, i2);
                }
            }
        }
        bitmapDecodeResource.recycle();
    }

    @Override // com.sonymobile.calendar.tablet.AbstractCheckAvailabilityActivity
    protected void drawStatus(int i) {
        drawStatus(this.mSelectedTime, '4');
    }

    private void drawStatus(int i, char c) {
        if (this.mStatus == null || this.mStatus.size() < 1) {
            return;
        }
        if (this.mStatusBmp != null) {
            this.mStatusBmp.recycle();
        }
        this.mStatusBmp = Bitmap.createBitmap(this.mCellWidth * 24, (this.mCellHeight * (this.mRequiredCount + this.mOptionalCount)) + this.mSelectedLineWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.mStatusBmp);
        if (this.mIsR2L) {
            i = 23 - i;
        }
        int i2 = i;
        addStatus(true, i2, canvas, this.mRequiredCount, this.mRequiredEmails, c);
        addStatus(false, i2, canvas, this.mOptionalCount, this.mOptionalEmails, c);
        canvas.drawRect(new Rect(this.mCellWidth * i, this.mStatusBmp.getHeight() - this.mSelectedLineWidth, (this.mCellWidth * (i + 1)) + this.mSelectedLineWidth, this.mStatusBmp.getHeight()), this.mRecPainter);
        ((ImageView) findViewById(R.id.status)).setImageBitmap(this.mStatusBmp);
    }

    private void addAttendeeView(boolean z, LayoutInflater layoutInflater, int i, String[] strArr, String[] strArr2) {
        int i2;
        String str;
        if (z) {
            View viewInflate = layoutInflater.inflate(R.layout.attendee_item, (ViewGroup) null);
            this.mAttendees.addView(viewInflate, new ViewGroup.LayoutParams(-1, -1));
            TextView textView = (TextView) viewInflate.findViewById(R.id.displayName);
            ImageView imageView = (ImageView) viewInflate.findViewById(R.id.attendType);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            imageView.setVisibility(8);
            textView.setText(getResources().getString(R.string.organizer_availability_label));
            i2 = 1;
        } else {
            i2 = 0;
        }
        while (i2 < i) {
            View viewInflate2 = layoutInflater.inflate(R.layout.attendee_item, (ViewGroup) null);
            this.mAttendees.addView(viewInflate2, new ViewGroup.LayoutParams(-1, -1));
            TextView textView2 = (TextView) viewInflate2.findViewById(R.id.displayName);
            ImageView imageView2 = (ImageView) viewInflate2.findViewById(R.id.attendType);
            textView2.setTypeface(Typeface.DEFAULT);
            if (!z) {
                imageView2.setImageResource(R.drawable.ic_availability_optional);
            }
            if (strArr[i2] == null || "".equals(strArr[i2])) {
                str = strArr2[i2];
            } else {
                str = strArr[i2];
            }
            textView2.setText(str);
            i2++;
        }
    }

    private void addStatus(boolean z, int i, Canvas canvas, int i2, String[] strArr, char c) {
        int i3;
        char[] cArr;
        int i4;
        int i5;
        int i6;
        Bitmap bitmapDecodeResource = BitmapFactory.decodeResource(this.mResources, R.drawable.divider_availability_horizontal_bright);
        Bitmap bitmapDecodeResource2 = BitmapFactory.decodeResource(this.mResources, R.drawable.divider_availability_vertical_bright);
        Bitmap bitmapDecodeResource3 = BitmapFactory.decodeResource(this.mResources, R.drawable.divider_shadow_horizontal);
        int i7 = 0;
        while (i7 < i2) {
            int i8 = 48;
            char[] cArr2 = new char[48];
            if (this.mStatus.get(strArr[i7]) == null || "".equals(strArr[i7].trim())) {
                for (int i9 = 0; i9 < 48; i9++) {
                    cArr2[i9] = c;
                }
            } else {
                ((String) this.mStatus.get(strArr[i7])).getChars(0, 48, cArr2, 0);
                setLog("CheckAvailability drawStatus: status is " + new String(cArr2), this);
                if (this.mIsR2L) {
                    char[] cArr3 = new char[48];
                    for (int i10 = 0; i10 < 48; i10++) {
                        cArr3[i10] = cArr2[47 - i10];
                    }
                    System.arraycopy(cArr3, 0, cArr2, 0, 48);
                    setLog("CheckAvailability drawStatus: status(R2L) is " + new String(cArr2), this);
                }
            }
            int i11 = this.mCellHeight * i7;
            int i12 = i7 + 1;
            int i13 = (this.mCellHeight * i12) - this.mLineWidth;
            if (!z) {
                i11 += this.mCellHeight * this.mRequiredCount;
                i13 += this.mCellHeight * this.mRequiredCount;
            }
            int i14 = i11;
            int i15 = i13;
            int i16 = 0;
            while (i16 < i8) {
                if (i16 % 2 == 0) {
                    if (i16 != 0) {
                        if (cArr2[i16] == cArr2[i16 - 1]) {
                            int i17 = i16 / 2;
                            i3 = i16;
                            i5 = i15;
                            i6 = i14;
                            cArr = cArr2;
                            drawCell(canvas, cArr2[i16], this.mCellWidth * i17, i14, (this.mCellWidth * i17) + this.mHalfCellWidth + this.mHalfCellWidthOffset, i5);
                        } else {
                            i3 = i16;
                            i5 = i15;
                            i6 = i14;
                            cArr = cArr2;
                            int i18 = i3 / 2;
                            drawCell(canvas, cArr[i3], (this.mCellWidth * i18) + this.mLineWidth, i6, (this.mCellWidth * i18) + this.mHalfCellWidth + this.mHalfCellWidthOffset, i5);
                        }
                    } else {
                        i3 = i16;
                        i5 = i15;
                        i6 = i14;
                        cArr = cArr2;
                        drawCell(canvas, cArr[i3], 0, i6, (this.mCellWidth * (i3 / 2)) + this.mHalfCellWidth + this.mHalfCellWidthOffset, i5);
                    }
                    i15 = i5;
                    i4 = i6;
                } else {
                    i3 = i16;
                    int i19 = i15;
                    int i20 = i14;
                    cArr = cArr2;
                    int i21 = i3 / 2;
                    int i22 = i21 + 1;
                    drawCell(canvas, cArr[i3], this.mHalfCellWidthOffset + (this.mCellWidth * i21) + this.mHalfCellWidth, i20, this.mCellWidth * i22, i19);
                    i15 = i19;
                    i4 = i20;
                    canvas.drawBitmap(bitmapDecodeResource2, (Rect) null, new Rect(this.mCellWidth * i22, i4, (this.mCellWidth * i22) + this.mLineWidth, i15), (Paint) null);
                }
                i14 = i4;
                cArr2 = cArr;
                i8 = 48;
                i16 = i3 + 1;
            }
            int i23 = i14;
            if (i7 == i2 - 1 && ((z && this.mOptionalCount == 0) || !z)) {
                canvas.drawBitmap(bitmapDecodeResource3, (Rect) null, new Rect(0, i15, this.mCellWidth * 24, i15 + 7), (Paint) null);
            } else {
                canvas.drawBitmap(bitmapDecodeResource, (Rect) null, new Rect(0, i15, this.mCellWidth * 24, this.mLineWidth + i15), (Paint) null);
            }
            Rect rect = new Rect(this.mCellWidth * i, i23 - this.mLineWidth, (this.mCellWidth * i) + this.mSelectedLineWidth, i15 + this.mLineWidth);
            this.mRecPainter.setColor(ContextCompat.getColor(this.mContext, R.color.new_event_availability_blue));
            if (i != 0) {
                canvas.drawRect(rect, this.mRecPainter);
            }
            rect.left += this.mCellWidth;
            rect.right += this.mCellWidth;
            canvas.drawRect(rect, this.mRecPainter);
            i7 = i12;
        }
        bitmapDecodeResource.recycle();
        bitmapDecodeResource2.recycle();
        bitmapDecodeResource3.recycle();
    }

    @Override // com.sonymobile.calendar.tablet.AbstractCheckAvailabilityActivity
    protected void sendResult() {
        Intent intent = new Intent();
        if (!this.mIsAllDay) {
            intent.putExtra(AbstractCheckAvailabilityActivity.AVAILABILITY_NEED_CHANGED_HOUR, this.mNeedChangeHour);
            intent.putExtra(AbstractCheckAvailabilityActivity.AVAILABILITY_CHANGED_TIME_HOUR, this.mSelectedTime);
        } else {
            intent.putExtra(AbstractCheckAvailabilityActivity.AVAILABILITY_NEED_CHANGED_HOUR, false);
        }
        intent.putExtra(AVAILABILITY_NEED_CHANGED_TIME_DATE, this.mNeedChangeTimeDate);
        intent.putExtra(AVAILABILITY_CHANGED_TIME_DATE, this.mCurrentTime.toMillis(true));
        setResult(-1, intent);
        finish();
    }

    @Override // com.sonymobile.calendar.tablet.AbstractCheckAvailabilityActivity
    protected void showDialogProgres() {
        ProgressDialog progressDialog = this.mStatusDialog;
        this.mStatusDialog = ProgressDialog.show(this, null, this.mResources.getString(R.string.availability_loading_label), true, true, new DialogInterface.OnCancelListener() { // from class: com.sonymobile.calendar.tablet.TabletCheckAvailabilityActivity.1
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
                TabletCheckAvailabilityActivity.this.mIsTimeOut = true;
                TabletCheckAvailabilityActivity.this.mHandler.sendEmptyMessage(2);
                TabletCheckAvailabilityActivity.this.mHandler.removeMessages(1);
            }
        });
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        this.mLeftButton.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.tablet.TabletCheckAvailabilityActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                TabletCheckAvailabilityActivity.this.doButtonClick(-1);
            }
        });
        this.mRightButton.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.tablet.TabletCheckAvailabilityActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                TabletCheckAvailabilityActivity.this.doButtonClick(1);
            }
        });
    }

    private void drawTitle() {
        this.mDateTitle.setTypeface(Typeface.DEFAULT);
        StringBuilder sb = new StringBuilder(50);
        sb.setLength(0);
        this.mDateTitle.setText(DateUtils.formatDateRange(this, new Formatter(sb, Locale.getDefault()), this.mCurrentTime.toMillis(true), this.mCurrentTime.toMillis(true), 22, this.mCurrentTime.timezone).toString().toUpperCase());
    }

    private void setStartAndEndTime() {
        this.mStartTime.timezone = this.mTimeZone;
        this.mStartTime.set(this.mCurrentTime.toMillis(true));
        this.mStartTime.hour = 0;
        this.mStartTime.minute = 0;
        this.mStartTime.second = 0;
        this.mStartTime.switchTimezone("UTC");
        this.mEndTime.timezone = this.mTimeZone;
        this.mEndTime.set(this.mCurrentTime.toMillis(true));
        this.mEndTime.hour = 23;
        this.mEndTime.minute = 59;
        this.mEndTime.second = 59;
        this.mEndTime.switchTimezone("UTC");
    }

    private void resetAllAttendeeStatus() {
        char[] cArr = new char[48];
        for (int i = 0; i < 48; i++) {
            cArr[i] = '0';
        }
        this.mAllAttendeeStatus = cArr;
    }

    private void resetStatus() {
        if (this.mStatus != null) {
            char[] cArr = new char[48];
            for (int i = 0; i < 48; i++) {
                cArr[i] = '0';
            }
            Iterator it = this.mStatus.entrySet().iterator();
            while (it.hasNext()) {
                this.mStatus.put(((Map.Entry) it.next()).getKey(), String.valueOf(cArr));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doButtonClick(int i) {
        if (this.mIsR2L) {
            i = -i;
        }
        this.mIsTimeOut = false;
        this.mIsGotStatus = false;
        resetStatus();
        resetAllAttendeeStatus();
        drawStatus(this.mSelectedTime, '0');
        drawAllAttendeeStatus(this.mSelectedTime);
        this.mCurrentTime.monthDay += i;
        this.mCurrentTime.normalize(true);
        this.mNeedChangeTimeDate = true;
        setStartAndEndTime();
        showDialogs(3);
        drawTitle();
        Thread thread = new Thread(this.statusLoader);
        thread.setPriority(1);
        thread.start();
        this.mHandler.postDelayed(this.mTimeOut, 15000L);
    }
}
