package com.sonymobile.calendar;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.util.SparseIntArray;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import com.sonyericsson.calendar.util.CalendarInstanceService;
import com.sonyericsson.calendar.util.HsvColorComparator;
import java.util.ArrayList;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarColorQuery implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int COLORS_INDEX_COLOR = 0;
    private static final int COLORS_INDEX_COLOR_KEY = 1;
    private static final String[] COLORS_PROJECTION = {"color", "color_index"};
    private static final String COLORS_WHERE = "account_name=? AND account_type=? AND color_type=0";
    private static final String COLORS_WHERE_EVENT = "account_name=? AND account_type=? AND color_type=1";
    private String[] args;
    private Context context;
    private OnColorPickedListener mColorPickedListener;
    private int mDefaultColor;
    private boolean mIsEventColorPicker;
    private boolean mIsSyncable;
    private Uri uri = CalendarContract.Colors.CONTENT_URI;
    private boolean hasLoaded = false;
    private Handler handler = new Handler() { // from class: com.sonymobile.calendar.CalendarColorQuery.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.obj != null) {
                ColorPickerDialogBase colorPickerDialogBaseNewInstance = ColorPickerDialogBase.newInstance(CalendarColorQuery.this.mIsEventColorPicker, (int[]) message.obj, CalendarColorQuery.this.mDefaultColor, CalendarColorQuery.this.mIsSyncable);
                colorPickerDialogBaseNewInstance.setOnColorPickedListener(CalendarColorQuery.this.mColorPickedListener);
                colorPickerDialogBaseNewInstance.show(((AppCompatActivity) CalendarColorQuery.this.context).getSupportFragmentManager(), ColorPickerDialogBase.TAG);
            }
        }
    };

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public CalendarColorQuery(AppCompatActivity appCompatActivity, OnColorPickedListener onColorPickedListener, int i, boolean z) {
        this.context = appCompatActivity;
        this.mColorPickedListener = onColorPickedListener;
        this.mDefaultColor = i;
        this.mIsEventColorPicker = z;
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this.context, this.uri, COLORS_PROJECTION, this.mIsEventColorPicker ? COLORS_WHERE_EVENT : COLORS_WHERE, this.args, null);
    }

    public void startLoader(DrawerItemCheckable drawerItemCheckable) {
        startLoader(drawerItemCheckable.getAccountName(), drawerItemCheckable.getAccountType());
    }

    public void startLoader(String str, String str2) {
        this.args = new String[]{str, str2};
        ((AppCompatActivity) this.context).getSupportLoaderManager().restartLoader(1, null, this);
    }

    public void startLoader(String str, String str2, int i) {
        this.args = new String[]{str, str2};
        ((AppCompatActivity) this.context).getSupportLoaderManager().restartLoader(i, null, this);
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (this.hasLoaded) {
            return;
        }
        this.hasLoaded = true;
        sendMessage(getColorsToUse(cursor));
    }

    private int[] getColorsToUse(Cursor cursor) {
        int[] colorIntegers;
        ArrayList arrayList = new ArrayList();
        if (cursor.getCount() == 0) {
            if (this.mIsEventColorPicker) {
                colorIntegers = EventColorPickerDialog.getColorIntegers(EventColorPickerDialog.HEX_COLORS);
            } else {
                colorIntegers = CalendarColorPickerDialog.getColorIntegers(CalendarColorPickerDialog.HEX_COLORS);
            }
            for (int i : colorIntegers) {
                arrayList.add(Integer.valueOf(i));
            }
        } else {
            this.mIsSyncable = true;
            SparseIntArray sparseIntArray = new SparseIntArray();
            while (cursor.moveToNext()) {
                int i2 = cursor.getInt(1);
                int i3 = cursor.getInt(0);
                arrayList.add(Integer.valueOf(i3));
                sparseIntArray.put(i3, i2);
            }
            CalendarInstanceService.getInstance().updateColorKeyMap(sparseIntArray);
        }
        Integer[] numArr = (Integer[]) arrayList.toArray(new Integer[arrayList.size()]);
        Arrays.sort(numArr, new HsvColorComparator());
        int length = numArr.length;
        int[] iArr = new int[length];
        for (int i4 = 0; i4 < length; i4++) {
            iArr[i4] = numArr[i4].intValue();
        }
        return iArr;
    }

    private void sendMessage(int[] iArr) {
        Message messageObtain = Message.obtain();
        messageObtain.obj = iArr;
        this.handler.sendMessage(messageObtain);
    }
}
