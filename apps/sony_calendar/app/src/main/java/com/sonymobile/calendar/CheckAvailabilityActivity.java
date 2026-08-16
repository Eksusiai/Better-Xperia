package com.sonymobile.calendar;

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
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.sonymobile.calendar.tablet.AbstractCheckAvailabilityActivity;
import com.sonymobile.calendar.utils.UiUtils;
import java.util.Formatter;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class CheckAvailabilityActivity extends AbstractCheckAvailabilityActivity {
    private static final int CELL_COLUMNS_LANDSCAPE = 10;
    private static final int CELL_COLUMNS_PORTRAIT = 5;
    private static final int CELL_HEIGHT = 26;
    private static final float CELL_WIDTH_SCALE = 0.8f;
    private static final int LANDSCAPE_END_MOVE_HOUR = 20;
    private static final int LANDSCAPE_MAX_MOVE_HOUR = 14;
    private static final int LANDSCAPE_START_MOVE_HOUR = 5;
    private static final int LINE_WIDTH = 1;
    private static final int PORTRAIT_END_MOVE_HOUR = 22;
    private static final int PORTRAIT_MAX_MOVE_HOUR = 20;
    private static final int PORTRAIT_START_MOVE_HOUR = 2;
    private static final int SELECTED_LINE_WIDTH = 2;
    private static final int SHADOW_LINE_WIDTH = 7;
    private static final int SPLIT_WIDTH = 126;
    private static final int TITLE_CELL_HEIGHT = 22;

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
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        this.mTimeContainer.removeAllViews();
        this.mAttendees.removeAllViews();
        this.mHandler.removeCallbacksAndMessages(null);
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

    @Override // com.sonymobile.calendar.tablet.AbstractCheckAvailabilityActivity, android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        getWindowManager().getDefaultDisplay().getSize(this.mScreenRect);
        this.mOrientation = this.mResources.getConfiguration().orientation;
        this.mIsR2L = CalendarApplication.isR2L(this.mResources);
        if (this.mOrientation == 2) {
            this.mCellWidth = ((this.mScreenRect.x - this.mSplitWidth) - this.mLineWidth) / 10;
        } else {
            this.mCellWidth = ((this.mScreenRect.x - this.mSplitWidth) - this.mLineWidth) / 5;
        }
        this.mCellWidth = (int) (this.mCellWidth * CELL_WIDTH_SCALE);
        this.mHalfCellWidth = this.mCellWidth / 2;
        this.mHalfCellWidthOffset = this.mCellWidth % 2;
        drawTime(this.mSelectedTime);
        drawAllAttendeeStatus(this.mSelectedTime);
        drawStatus(this.mSelectedTime);
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
            this.mCurrentTime.set(longExtra);
            this.mCurrentTime.hour = 0;
            this.mCurrentTime.minute = 0;
            this.mCurrentTime.second = 0;
        } else {
            this.mCurrentTime.set(longExtra);
        }
        this.mCurrentTime.timezone = this.mTimeZone;
        getWindowManager().getDefaultDisplay().getSize(this.mScreenRect);
        this.mOrientation = this.mResources.getConfiguration().orientation;
        this.mMoveThresholdDP = this.mResources.getDisplayMetrics().density * 20.0f;
        this.mSplitWidth = Utils.dp2px(this, 126.0f);
        this.mLineWidth = Utils.dp2px(this, 1.0f);
        this.mTitleCellHeight = Utils.dp2px(this, 22.0f);
        this.mCellHeight = Utils.dp2px(this, 26.0f);
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
        this.mDateTitle.setTypeface(Typeface.DEFAULT);
        StringBuilder sb = new StringBuilder(50);
        sb.setLength(0);
        this.mDateTitle.setText(DateUtils.formatDateRange(this, new Formatter(sb, Locale.getDefault()), this.mCurrentTime.toMillis(true), this.mCurrentTime.toMillis(true), 65554, this.mCurrentTime.timezone).toString());
        if (this.mOrientation == 2) {
            this.mCellWidth = ((this.mScreenRect.x - this.mSplitWidth) - this.mLineWidth) / 10;
        } else {
            this.mCellWidth = ((this.mScreenRect.x - this.mSplitWidth) - this.mLineWidth) / 5;
        }
        this.mCellWidth = (int) (this.mCellWidth * CELL_WIDTH_SCALE);
        this.mHalfCellWidth = this.mCellWidth / 2;
        this.mHalfCellWidthOffset = this.mCellWidth % 2;
        this.mSelectedTime = this.mCurrentTime.hour;
        this.mSVStatus.name = 1;
        this.mSVAttendee.name = 2;
        BindSVManager.setCalendarSVA(this.mSVStatus);
        BindSVManager.setCalendarSVB(this.mSVAttendee);
        this.mSVStatus.setOnTouchListener(this.mOnTouchListener);
        this.mSVTime.setOnTouchListener(this.mOnTouchListener);
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
            if (i > 5 && i < 20) {
                this.mOffsetHour = this.mCellWidth * (i - 5);
                return;
            } else if (i >= 20) {
                this.mOffsetHour = this.mCellWidth * 14;
                return;
            } else {
                this.mOffsetHour = 0;
                return;
            }
        }
        if (i > 2 && i < 22) {
            this.mOffsetHour = this.mCellWidth * (i - 2);
        } else if (i >= 22) {
            this.mOffsetHour = this.mCellWidth * 20;
        } else {
            this.mOffsetHour = 0;
        }
    }

    @Override // com.sonymobile.calendar.tablet.AbstractCheckAvailabilityActivity
    protected void drawTime(int i) {
        LayoutInflater layoutInflater = getLayoutInflater();
        this.mTimeContainer = (LinearLayout) findViewById(R.id.timeContainer);
        this.mTimeContainer.removeAllViews();
        for (int i2 = 0; i2 < 24; i2++) {
            View viewInflate = layoutInflater.inflate(R.layout.hour_item, (ViewGroup) null);
            this.mTimeContainer.addView(viewInflate, this.mCellWidth, Utils.dp2px(this, 27.0f));
            TextView textView = (TextView) viewInflate.findViewById(R.id.hourLable);
            textView.setTypeface(Typeface.DEFAULT);
            if (i2 < 10) {
                textView.setText("0" + i2);
            } else {
                textView.setText(String.valueOf(i2));
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
        this.mAttendees.addView(layoutInflater.inflate(R.layout.attendee_item, (ViewGroup) null), this.mSplitWidth, this.mSelectedLineWidth);
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
        this.mAllStatusBmp = Bitmap.createBitmap(this.mCellWidth * 24, this.mTitleCellHeight + 2, Bitmap.Config.ARGB_8888);
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
        canvas.drawBitmap(bitmapDecodeResource, (Rect) null, new Rect(0, this.mTitleCellHeight, this.mCellWidth * 24, this.mTitleCellHeight + this.mLineWidth), (Paint) null);
        Rect rect = new Rect(this.mCellWidth * i, 0, (this.mCellWidth * i) + this.mSelectedLineWidth, this.mTitleCellHeight + this.mLineWidth);
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
        for (int i = 0; i < 48; i++) {
            int i2 = this.mTitleCellHeight;
            if (i % 2 != 0) {
                int i3 = i / 2;
                int i4 = i3 + 1;
                drawCell(canvas, cArr[i], this.mHalfCellWidthOffset + (this.mCellWidth * i3) + this.mHalfCellWidth, 1, this.mCellWidth * i4, i2);
                canvas.drawBitmap(bitmapDecodeResource, (Rect) null, new Rect(this.mCellWidth * i4, 1, (this.mCellWidth * i4) + this.mLineWidth, i2), (Paint) null);
            } else if (i != 0) {
                if (cArr[i] == cArr[i - 1]) {
                    int i5 = i / 2;
                    drawCell(canvas, cArr[i], this.mCellWidth * i5, 1, (this.mCellWidth * i5) + this.mHalfCellWidth + this.mHalfCellWidthOffset, i2);
                } else {
                    int i6 = i / 2;
                    drawCell(canvas, cArr[i], this.mLineWidth + (this.mCellWidth * i6), 1, (this.mCellWidth * i6) + this.mHalfCellWidth + this.mHalfCellWidthOffset, i2);
                }
            } else {
                drawCell(canvas, cArr[i], 0, 1, (this.mCellWidth * (i / 2)) + this.mHalfCellWidth + this.mHalfCellWidthOffset, i2);
            }
        }
        bitmapDecodeResource.recycle();
    }

    @Override // com.sonymobile.calendar.tablet.AbstractCheckAvailabilityActivity
    protected void drawStatus(int i) {
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
        addStatus(true, i2, canvas, this.mRequiredCount, this.mRequiredEmails);
        addStatus(false, i2, canvas, this.mOptionalCount, this.mOptionalEmails);
        canvas.drawRect(new Rect(this.mCellWidth * i, this.mStatusBmp.getHeight() - this.mSelectedLineWidth, (this.mCellWidth * (i + 1)) + this.mSelectedLineWidth, this.mStatusBmp.getHeight()), this.mRecPainter);
        ((ImageView) findViewById(R.id.status)).setImageBitmap(this.mStatusBmp);
    }

    private void addAttendeeView(boolean z, LayoutInflater layoutInflater, int i, String[] strArr, String[] strArr2) {
        String str;
        for (int i2 = 0; i2 < i; i2++) {
            View viewInflate = layoutInflater.inflate(R.layout.attendee_item, (ViewGroup) null);
            this.mAttendees.addView(viewInflate, this.mSplitWidth, this.mCellHeight);
            TextView textView = (TextView) viewInflate.findViewById(R.id.displayName);
            ImageView imageView = (ImageView) viewInflate.findViewById(R.id.attendType);
            if (this.mResources.getString(R.string.organizer_availability_label).equals(strArr[i2])) {
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                imageView.setVisibility(8);
            } else {
                textView.setTypeface(Typeface.DEFAULT);
            }
            if (!z) {
                imageView.setImageResource(R.drawable.ic_availability_optional);
            }
            if (strArr[i2] == null || "".equals(strArr[i2])) {
                str = strArr2[i2];
            } else {
                str = strArr[i2];
            }
            textView.setText(str);
        }
    }

    private void addStatus(boolean z, int i, Canvas canvas, int i2, String[] strArr) {
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
                    cArr2[i9] = '4';
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
    protected void changeTime(MotionEvent motionEvent) {
        super.changeTime(motionEvent);
        drawStatus(this.mSelectedTime);
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
        setResult(-1, intent);
        finish();
    }

    @Override // com.sonymobile.calendar.tablet.AbstractCheckAvailabilityActivity
    protected void showDialogProgres() {
        ProgressDialog progressDialog = this.mStatusDialog;
        this.mStatusDialog = ProgressDialog.show(this, null, this.mResources.getString(R.string.availability_loading_label), true, true, new DialogInterface.OnCancelListener() { // from class: com.sonymobile.calendar.CheckAvailabilityActivity.1
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
                CheckAvailabilityActivity.this.mIsTimeOut = true;
                CheckAvailabilityActivity.this.mHandler.sendEmptyMessage(2);
                CheckAvailabilityActivity.this.mHandler.removeMessages(1);
            }
        });
    }
}
