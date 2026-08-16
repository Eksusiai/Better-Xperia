package com.sonymobile.calendar.lunar;

import android.database.Cursor;
import android.database.MatrixCursor;
import com.sonymobile.calendar.Utils;

/* JADX INFO: loaded from: classes2.dex */
public abstract class LuniSolarCursorManager {
    private boolean mIsLunarAvailable;
    private Cursor mLunarCursor;
    private Cursor mSolarCursor;

    public abstract MatrixCursor initMatrixCursor();

    public abstract boolean isSolarCursorProper(Cursor cursor, Cursor cursor2);

    public abstract void mergeCursor(MatrixCursor matrixCursor, Cursor cursor, boolean z);

    public LuniSolarCursorManager(Cursor cursor, Cursor cursor2, boolean z) {
        this.mSolarCursor = cursor;
        this.mLunarCursor = cursor2;
        this.mIsLunarAvailable = z;
    }

    private static boolean isValid(Cursor cursor) {
        return (cursor == null || cursor.isClosed()) ? false : true;
    }

    public MatrixCursor processCursor() {
        MatrixCursor matrixCursorInitMatrixCursor = initMatrixCursor();
        int count = isValid(this.mSolarCursor) ? this.mSolarCursor.getCount() : 0;
        int count2 = isValid(this.mLunarCursor) ? this.mLunarCursor.getCount() : 0;
        if (isValid(this.mSolarCursor)) {
            this.mSolarCursor.moveToFirst();
        }
        if (isValid(this.mLunarCursor)) {
            this.mLunarCursor.moveToFirst();
        }
        int i = 0;
        int i2 = 0;
        while (i < count && i2 < count2) {
            if (isSolarCursorProper(this.mSolarCursor, this.mLunarCursor)) {
                mergeCursor(matrixCursorInitMatrixCursor, this.mSolarCursor, false);
                i++;
                this.mSolarCursor.moveToNext();
            } else {
                mergeCursor(matrixCursorInitMatrixCursor, this.mLunarCursor, true);
                i2++;
                this.mLunarCursor.moveToNext();
            }
        }
        while (i < count) {
            mergeCursor(matrixCursorInitMatrixCursor, this.mSolarCursor, false);
            i++;
            this.mSolarCursor.moveToNext();
        }
        while (i2 < count2) {
            mergeCursor(matrixCursorInitMatrixCursor, this.mLunarCursor, true);
            i2++;
            this.mLunarCursor.moveToNext();
        }
        if (!this.mIsLunarAvailable) {
            Utils.closeCursor(this.mSolarCursor);
        }
        return matrixCursorInitMatrixCursor;
    }
}
