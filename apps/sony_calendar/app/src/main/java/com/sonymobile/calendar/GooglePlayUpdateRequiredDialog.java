package com.sonymobile.calendar;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;

/* JADX INFO: loaded from: classes2.dex */
public class GooglePlayUpdateRequiredDialog extends DialogFragment {
    private static final String GOOGLE_PLAY_STATUS = "keyGooglePlayStatus";
    public static final String TAG = "GooglePlayUpdateRequiredDialog";

    public static GooglePlayUpdateRequiredDialog newInstance(int i) {
        GooglePlayUpdateRequiredDialog googlePlayUpdateRequiredDialog = new GooglePlayUpdateRequiredDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(GOOGLE_PLAY_STATUS, i);
        googlePlayUpdateRequiredDialog.setArguments(bundle);
        return googlePlayUpdateRequiredDialog;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return GooglePlayServicesUtil.getErrorDialog(getArguments().getInt(GOOGLE_PLAY_STATUS), getActivity(), 0);
    }
}
