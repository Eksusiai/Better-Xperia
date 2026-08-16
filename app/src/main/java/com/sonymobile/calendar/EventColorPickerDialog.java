package com.sonymobile.calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class EventColorPickerDialog extends ColorPickerDialogBase {
    static final String[] HEX_COLORS = {"#4f6778", "#1bb6bd", "#de6a93", "#eb4456", "#bd9459", "#b09609", "#736375", "#15c99d", "#869c27", "#6b939e", "#de7d4d", "#2290b8"};
    private static final int NUMBER_OF_COLUMNS = 4;

    @Override // com.sonymobile.calendar.ColorPickerDialogBase, androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        int i = getResources().getBoolean(R.bool.tablet_mode) ? R.dimen.color_dialog_width : -1;
        this.mColors = getArguments().getIntArray(ColorPickerDialogBase.COLOR_ARRAY);
        this.mAccessibilityColors = sortAccessibilityColors(this.mColors);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setTitle(R.string.event_color_picker_title);
        builder.setPositiveButton(R.string.event_color_picker_default_button, this);
        builder.setView(super.initColorPalette(this.mColors, this.mAccessibilityColors, 4, i));
        return builder.create();
    }

    public static int[] getColorIntegers(String[] strArr) {
        int[] iArr = new int[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            iArr[i] = UiUtils.getDisplayColorFromColor(Color.parseColor(strArr[i]));
        }
        return iArr;
    }
}
