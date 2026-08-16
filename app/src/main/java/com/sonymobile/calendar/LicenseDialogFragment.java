package com.sonymobile.calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes2.dex */
public class LicenseDialogFragment extends DialogFragment {
    private static final String APACHE_LINK = "http://www.apache.org/licenses/";
    private static final String MESSAGE = "message";
    private static final String SCHEME = "http://";
    public static final String TAG = "LicenseDialogFragment";
    private static final String TITLE = "title";

    public static LicenseDialogFragment newInstance(String str, String str2) {
        LicenseDialogFragment licenseDialogFragment = new LicenseDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", str);
        bundle.putString("message", str2);
        licenseDialogFragment.setArguments(bundle);
        return licenseDialogFragment;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        FragmentActivity activity = getActivity();
        String string = getArguments().getString("title");
        String string2 = getArguments().getString("message");
        View viewInflate = ((LayoutInflater) activity.getSystemService("layout_inflater")).inflate(R.layout.license_dialog_fragment_message, (ViewGroup) null, false);
        TextView textView = (TextView) viewInflate.findViewById(R.id.license_message);
        textView.setText(string2);
        Linkify.addLinks(textView, Pattern.compile(APACHE_LINK), SCHEME);
        return new AlertDialog.Builder(activity, R.style.AlertDialogTheme).setTitle(string).setView(viewInflate).setPositiveButton(R.string.clr_strings_button_title_ok_txt, (DialogInterface.OnClickListener) null).create();
    }
}
