package com.sonymobile.calendar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import androidx.fragment.app.DialogFragment;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public abstract class ColorPickerDialogBase extends DialogFragment implements DialogInterface.OnClickListener {
    public static final String COLOR_ARRAY = "colors";
    protected static final int DEFAULT_WIDTH_SIZE = -1;
    private static final int GRID_PADDING = 10;
    public static final String IS_ACCOUNT_SINCABLE = "isAccountSyncable";
    public static final String IS_CALENDAR_PALETTE = "isCalendarColorPalette";
    public static final String SELECTED_COLOR = "selectedColor";
    public static final String TAG = "colorPickerTag";
    protected String[] mAccessibilityColors;
    private ColorGridAdapter mAdapter;
    protected int[] mColors;
    private boolean mIsAccountSyncable;
    private OnColorPickedListener mListener;
    private int specifiedWidthResource = -1;

    @Override // androidx.fragment.app.DialogFragment
    public abstract Dialog onCreateDialog(Bundle bundle);

    public static ColorPickerDialogBase newInstance(boolean z, int[] iArr, int i, boolean z2) {
        ColorPickerDialogBase calendarColorPickerDialog;
        if (z) {
            calendarColorPickerDialog = new EventColorPickerDialog();
        } else {
            calendarColorPickerDialog = new CalendarColorPickerDialog();
        }
        Bundle bundle = new Bundle();
        bundle.putIntArray(COLOR_ARRAY, iArr);
        bundle.putBoolean(IS_CALENDAR_PALETTE, true);
        bundle.putInt(SELECTED_COLOR, i);
        bundle.putBoolean(IS_ACCOUNT_SINCABLE, z2);
        calendarColorPickerDialog.setArguments(bundle);
        return calendarColorPickerDialog;
    }

    public void setOnColorPickedListener(OnColorPickedListener onColorPickedListener) {
        this.mListener = onColorPickedListener;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        OnColorPickedListener onColorPickedListener = this.mListener;
        if (onColorPickedListener != null) {
            onColorPickedListener.onColorPicked(0, this.mIsAccountSyncable);
        }
    }

    protected View initColorPalette(int[] iArr, String[] strArr, int i, int i2) {
        int i3 = getArguments() != null ? getArguments().getInt(SELECTED_COLOR, -1) : -1;
        this.mIsAccountSyncable = getArguments() == null ? false : getArguments().getBoolean(IS_ACCOUNT_SINCABLE);
        this.mAdapter = new ColorGridAdapter(getActivity(), iArr, strArr, i3);
        this.specifiedWidthResource = i2;
        GridView gridView = new GridView(getActivity());
        gridView.setLayoutParams(new AbsListView.LayoutParams(-2, -2));
        gridView.setNumColumns(i);
        int i4 = (int) (getActivity().getResources().getDisplayMetrics().density * 10.0f);
        gridView.setPadding(i4, i4, i4, (int) (i4 * 1.5f));
        gridView.setVerticalSpacing(i4);
        gridView.setGravity(17);
        gridView.setAdapter((ListAdapter) this.mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.sonymobile.calendar.ColorPickerDialogBase.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i5, long j) {
                if (ColorPickerDialogBase.this.mListener != null) {
                    ColorPickerDialogBase.this.mListener.onColorPicked(ColorPickerDialogBase.this.mAdapter.getColor(i5), ColorPickerDialogBase.this.mIsAccountSyncable);
                }
                ColorPickerDialogBase.this.dismiss();
            }
        });
        return gridView;
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.specifiedWidthResource != -1) {
            WindowManager.LayoutParams attributes = getDialog().getWindow().getAttributes();
            attributes.width = (int) getResources().getDimension(this.specifiedWidthResource);
            getDialog().getWindow().setAttributes(attributes);
        }
    }

    protected String[] sortAccessibilityColors(int[] iArr) {
        String[] strArr = new String[iArr.length];
        String[] stringArray = getActivity().getResources().getStringArray(R.array.accessibility_calendar_color_picker_colors);
        int[] intArray = getActivity().getResources().getIntArray(R.array.google_calendar_colors);
        SparseArray sparseArray = new SparseArray();
        int iMin = Math.min(stringArray.length, intArray.length);
        for (int i = 0; i < iMin; i++) {
            sparseArray.put(UiUtils.getDisplayColorFromColor(intArray[i]), stringArray[i]);
        }
        for (int i2 = 0; i2 < iArr.length; i2++) {
            String str = (String) sparseArray.get(iArr[i2]);
            if (str == null) {
                str = "";
            }
            strArr[i2] = str;
        }
        return strArr;
    }
}
