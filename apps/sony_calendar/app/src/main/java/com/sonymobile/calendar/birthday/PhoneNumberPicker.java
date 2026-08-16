package com.sonymobile.calendar.birthday;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.fragment.app.DialogFragment;
import com.sonymobile.calendar.PhoneNumber;
import com.sonymobile.calendar.R;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class PhoneNumberPicker extends DialogFragment {
    private static final String INTERACTION_TYPE = "interaction_type";
    private static final String NAME = "name";
    private static final String PHONE_NUMBERS = "phone_numbers";
    public static final String TAG = "phoneNumber_picker";
    private static String phoneNumberRegularExpression = "[^\\d\\+\\*#]";
    private OnPhoneNumberPickedListener onPhoneNumberClickedListener;
    private ArrayList<String> phoneNumberStrings;

    public static PhoneNumberPicker newInstance(ArrayList<PhoneNumber> arrayList, String str, String str2) {
        PhoneNumberPicker phoneNumberPicker = new PhoneNumberPicker();
        Bundle bundle = new Bundle();
        bundle.putString(INTERACTION_TYPE, str);
        bundle.putString("name", str2);
        bundle.putParcelableArrayList(PHONE_NUMBERS, arrayList);
        phoneNumberPicker.setArguments(bundle);
        return phoneNumberPicker;
    }

    public void setOnPhoneNumberClickedListener(OnPhoneNumberPickedListener onPhoneNumberPickedListener) {
        this.onPhoneNumberClickedListener = onPhoneNumberPickedListener;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setTitle(getArguments().getString("name"));
        builder.setView(initPickerView());
        return builder.create();
    }

    private ListView initPickerView() {
        this.phoneNumberStrings = new ArrayList<>();
        ArrayList<PhoneNumber> parcelableArrayList = getArguments().getParcelableArrayList(PHONE_NUMBERS);
        if (parcelableArrayList != null) {
            for (PhoneNumber phoneNumber : parcelableArrayList) {
                this.phoneNumberStrings.add(phoneNumber.number + " (" + phoneNumber.type + ")");
            }
        }
        ListView listView = new ListView(getActivity());
        listView.setAdapter((ListAdapter) new ArrayAdapter(getActivity(), R.layout.number_picker_item, android.R.id.text1, this.phoneNumberStrings));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.sonymobile.calendar.birthday.PhoneNumberPicker.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                PhoneNumberPicker.this.dismiss();
                if (PhoneNumberPicker.this.onPhoneNumberClickedListener != null) {
                    PhoneNumberPicker.this.onPhoneNumberClickedListener.onNumberPicked(((String) PhoneNumberPicker.this.phoneNumberStrings.get(i)).replaceAll(PhoneNumberPicker.phoneNumberRegularExpression, ""), PhoneNumberPicker.this.getArguments().getString(PhoneNumberPicker.INTERACTION_TYPE));
                }
            }
        });
        return listView;
    }
}
