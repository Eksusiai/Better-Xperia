package com.sonymobile.calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.CalendarUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import com.sonymobile.accuweather.WeatherFactory;
import com.sonymobile.accuweather.WeatherInfo;

/* JADX INFO: loaded from: classes2.dex */
public class DialogWeatherInfo extends DialogFragment {
    private static final String KEY_WEATHER_INFO = "key_weather_info";
    private OnDismissListener listener;

    public interface OnDismissListener {
        void onDismiss();
    }

    public static DialogWeatherInfo newInstance(WeatherInfo weatherInfo) {
        DialogWeatherInfo dialogWeatherInfo = new DialogWeatherInfo();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_WEATHER_INFO, weatherInfo);
        dialogWeatherInfo.setArguments(bundle);
        return dialogWeatherInfo;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        View viewInflate = getActivity().getLayoutInflater().inflate(R.layout.dialog_weather, (ViewGroup) null);
        final SharedPreferences.Editor editorEdit = GeneralPreferences.getSharedPreferences(getActivity()).edit();
        WeatherFactory weatherFactory = CalendarApplication.getWeatherFactory();
        ((TextView) viewInflate.findViewById(R.id.textView)).setText(weatherFactory.getWeatherInformationDialogText());
        builder.setTitle(weatherFactory.getWeatherInformationDialogTitle());
        builder.setView(viewInflate);
        builder.setPositiveButton(getString(R.string.clr_strings_button_title_ok_txt), (DialogInterface.OnClickListener) null);
        ((CheckBox) viewInflate.findViewById(R.id.checkBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.sonymobile.calendar.DialogWeatherInfo.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                editorEdit.putBoolean(GeneralPreferences.KEY_WEATHER_HIDE_DIALOG, z);
                editorEdit.apply();
            }
        });
        return builder.create();
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(-1).setOnClickListener(new WeatherClickListener((WeatherInfo) getArguments().getParcelable(KEY_WEATHER_INFO), CalendarUtils.isCelsius(getActivity()), this));
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.listener = onDismissListener;
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        OnDismissListener onDismissListener = this.listener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
            this.listener = null;
        }
    }
}
