package com.sonyericsson.calendar.util;

import android.util.MonthDisplayHelper;

/* JADX INFO: loaded from: classes.dex */
public class DayOfMonthCursor extends MonthDisplayHelper {
    private int mColumn;
    private boolean mIsR2L;
    private int mRow;

    public DayOfMonthCursor(int i, int i2, int i3, int i4, boolean z) {
        super(i, i2, i4);
        this.mIsR2L = z;
        this.mRow = getRowOf(i3);
        this.mColumn = getColumnOf(i3);
    }

    public int getSelectedRow() {
        return this.mRow;
    }

    public int getSelectedColumn() {
        return this.mColumn;
    }

    public void setSelectedRowColumn(int i, int i2) {
        this.mRow = i;
        this.mColumn = i2;
    }

    public int getSelectedDayOfMonth() {
        return getDayAt(this.mRow, this.mColumn);
    }

    public int getSelectedMonthOffset() {
        if (isWithinCurrentMonth(this.mRow, this.mColumn)) {
            return 0;
        }
        return this.mRow == 0 ? -1 : 1;
    }

    public void setSelectedDayOfMonth(int i) {
        this.mRow = getRowOf(i);
        this.mColumn = getColumnOf(i);
    }

    public boolean isSelected(int i, int i2) {
        return this.mRow == i && this.mColumn == i2;
    }

    public boolean isSelectedWeek(int i) {
        return this.mRow == i;
    }

    public boolean up() {
        if (isWithinCurrentMonth(this.mRow - 1, this.mColumn)) {
            this.mRow--;
            return false;
        }
        previousMonth();
        this.mRow = 5;
        while (!isWithinCurrentMonth(this.mRow, this.mColumn)) {
            this.mRow--;
        }
        return true;
    }

    public boolean down() {
        if (isWithinCurrentMonth(this.mRow + 1, this.mColumn)) {
            this.mRow++;
            return false;
        }
        nextMonth();
        this.mRow = 0;
        while (!isWithinCurrentMonth(this.mRow, this.mColumn)) {
            this.mRow++;
        }
        return true;
    }

    public boolean left() {
        int i = this.mColumn;
        if (i == 0) {
            this.mRow = this.mIsR2L ? this.mRow + 1 : this.mRow - 1;
            this.mColumn = 6;
        } else {
            this.mColumn = i - 1;
        }
        if (isWithinCurrentMonth(this.mRow, this.mColumn)) {
            return false;
        }
        if (this.mIsR2L) {
            nextMonth();
            this.mRow = 0;
            this.mColumn = 6;
            while (!isWithinCurrentMonth(this.mRow, this.mColumn)) {
                this.mColumn--;
            }
        } else {
            previousMonth();
            int numberOfDaysInMonth = getNumberOfDaysInMonth();
            this.mRow = getRowOf(numberOfDaysInMonth);
            this.mColumn = getColumnOf(numberOfDaysInMonth);
        }
        return true;
    }

    public boolean right() {
        int i = this.mColumn;
        if (i == 6) {
            this.mRow = this.mIsR2L ? this.mRow - 1 : this.mRow + 1;
            this.mColumn = 0;
        } else {
            this.mColumn = i + 1;
        }
        if (isWithinCurrentMonth(this.mRow, this.mColumn)) {
            return false;
        }
        if (this.mIsR2L) {
            previousMonth();
            int numberOfDaysInMonth = getNumberOfDaysInMonth();
            this.mRow = getRowOf(numberOfDaysInMonth);
            this.mColumn = getColumnOf(numberOfDaysInMonth);
        } else {
            nextMonth();
            this.mRow = 0;
            this.mColumn = 0;
            while (!isWithinCurrentMonth(this.mRow, this.mColumn)) {
                this.mColumn++;
            }
        }
        return true;
    }

    @Override // android.util.MonthDisplayHelper
    public int getDayAt(int i, int i2) {
        if (this.mIsR2L) {
            return getDayAtMirrored(i, i2);
        }
        return super.getDayAt(i, i2);
    }

    @Override // android.util.MonthDisplayHelper
    public boolean isWithinCurrentMonth(int i, int i2) {
        if (this.mIsR2L) {
            return isWithinCurrentMonthMirrored(i, i2);
        }
        return super.isWithinCurrentMonth(i, i2);
    }

    @Override // android.util.MonthDisplayHelper
    public int getColumnOf(int i) {
        int columnOf = super.getColumnOf(i);
        return this.mIsR2L ? 6 - columnOf : columnOf;
    }

    private int getDayAtMirrored(int i, int i2) {
        int offset = getOffset();
        previousMonth();
        int numberOfDaysInMonth = getNumberOfDaysInMonth();
        nextMonth();
        int numberOfDaysInMonth2 = getNumberOfDaysInMonth();
        if (i == 0 && i2 > 6 - offset) {
            return numberOfDaysInMonth - (i2 - (7 - offset));
        }
        int i3 = (i * 7) - (i2 - (7 - offset));
        return i3 > numberOfDaysInMonth2 ? i3 - numberOfDaysInMonth2 : i3;
    }

    private boolean isWithinCurrentMonthMirrored(int i, int i2) {
        int offset = getOffset();
        int numberOfDaysInMonth = getNumberOfDaysInMonth();
        if (i < 0 || i2 < 0 || i > 5 || i2 > 6) {
            return false;
        }
        return (i != 0 || i2 <= 6 - offset) && (i * 7) - (i2 - (7 - offset)) <= numberOfDaysInMonth;
    }
}
