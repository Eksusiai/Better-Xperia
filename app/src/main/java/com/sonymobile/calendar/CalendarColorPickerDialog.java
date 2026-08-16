package com.sonymobile.calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarColorPickerDialog extends ColorPickerDialogBase {
    static final String[] HEX_COLORS = {"#0a7f42", "#33b679", "#029ae4", "#3f5ca9", "#7885ca", "#636363", "#8d23a9", "#f5be25", "#f3501d", "#e57b72", "#d40000"};
    private static final int NUMBER_OF_COLUMNS = 6;

    @Override // com.sonymobile.calendar.ColorPickerDialogBase, androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        this.mColors = getArguments().getIntArray(ColorPickerDialogBase.COLOR_ARRAY);
        this.mAccessibilityColors = sortAccessibilityColors(this.mColors);
        setRetainInstance(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setTitle(R.string.calendar_color_picker_title);
        builder.setView(super.initColorPalette(this.mColors, this.mAccessibilityColors, 6, -1));
        return builder.create();
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    public static int[] getColorIntegers(String[] strArr) {
        int[] iArr = new int[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            iArr[i] = UiUtils.getDisplayColorFromColor(Color.parseColor(strArr[i]));
        }
        return iArr;
    }
}
