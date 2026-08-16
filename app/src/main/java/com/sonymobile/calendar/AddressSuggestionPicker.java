package com.sonymobile.calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class AddressSuggestionPicker extends DialogFragment {
    private static final String BUNDLE_KEY_ADDRESSES = "addresses";
    public static final String TAG = "AddressSuggestionPicker";
    private List<Address> mAddresses;
    private OnAddressPickedListener mOnAddressPickedListener;

    public static AddressSuggestionPicker newInstance(List<Address> list) {
        AddressSuggestionPicker addressSuggestionPicker = new AddressSuggestionPicker();
        if (list instanceof ArrayList) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(BUNDLE_KEY_ADDRESSES, (ArrayList) list);
            addressSuggestionPicker.setArguments(bundle);
        }
        return addressSuggestionPicker;
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mAddresses = arguments.getParcelableArrayList(BUNDLE_KEY_ADDRESSES);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mOnAddressPickedListener = (OnAddressPickedListener) activity;
        } catch (ClassCastException unused) {
            throw new ClassCastException(activity.toString() + " must implement OnAdressPickedListener");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setTitle(R.string.did_you_mean_dialog_title);
        builder.setView(initPickerView());
        return builder.create();
    }

    private ListView initPickerView() {
        String[] strArr = new String[this.mAddresses.size()];
        for (int i = 0; i < this.mAddresses.size(); i++) {
            strArr[i] = addressToString(this.mAddresses.get(i));
        }
        ListView listView = new ListView(getActivity());
        listView.setAdapter((ListAdapter) new ArrayAdapter(getActivity(), R.layout.address_suggestion_item, android.R.id.text1, strArr));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.sonymobile.calendar.AddressSuggestionPicker.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i2, long j) {
                AddressSuggestionPicker.this.dismiss();
                AddressSuggestionPicker.this.mOnAddressPickedListener.onAddressPicked((Address) AddressSuggestionPicker.this.mAddresses.get(i2), ((TextView) view.findViewById(android.R.id.text1)).getText());
            }
        });
        return listView;
    }

    private String addressToString(Address address) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            processText(address.getAddressLine(i), arrayList);
        }
        StringBuilder sb = new StringBuilder("");
        if (arrayList.size() > 0) {
            sb = new StringBuilder(arrayList.get(0));
            for (int i2 = 1; i2 < arrayList.size(); i2++) {
                sb.append(", ").append(arrayList.get(i2));
            }
        }
        return sb.toString();
    }

    private void processText(String str, List<String> list) {
        if (str != null) {
            list.add(str);
        }
    }
}
