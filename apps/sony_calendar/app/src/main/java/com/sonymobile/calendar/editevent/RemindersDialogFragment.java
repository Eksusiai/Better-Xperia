package com.sonymobile.calendar.editevent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import com.sonymobile.calendar.R;
import java.util.ArrayList;
import java.util.TreeSet;

/* JADX INFO: loaded from: classes2.dex */
public class RemindersDialogFragment extends DialogFragment {
    private static final int MAX_REMINDERS = 5;
    private static final String SELECTABLE_LABELS = "selectableLabels";
    private static final String TAG = "RemindersDialogFragment";
    private DialogCallback mDialogCallback;
    private ListView mListView;
    private Toast mMaxRemindersToast;

    public interface DialogCallback {
        void onDialogPositiveClick(ArrayList<Integer> arrayList);
    }

    public static RemindersDialogFragment newInstance(TreeSet<Integer> treeSet, ArrayList<String> arrayList) {
        RemindersDialogFragment remindersDialogFragment = new RemindersDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList("keyReminderIndices", new ArrayList<>(treeSet));
        bundle.putStringArray(SELECTABLE_LABELS, (String[]) arrayList.toArray(new String[0]));
        remindersDialogFragment.setArguments(bundle);
        return remindersDialogFragment;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle arguments = getArguments();
        final ArrayList<Integer> integerArrayList = arguments.getIntegerArrayList("keyReminderIndices");
        String[] stringArray = arguments.getStringArray(SELECTABLE_LABELS);
        final boolean[] zArr = new boolean[stringArray.length];
        for (int i = 0; i < integerArrayList.size(); i++) {
            zArr[integerArrayList.get(i).intValue()] = true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setTitle(R.string.reminders_label);
        builder.setMultiChoiceItems(stringArray, zArr, new DialogInterface.OnMultiChoiceClickListener() { // from class: com.sonymobile.calendar.editevent.RemindersDialogFragment.1
            @Override // android.content.DialogInterface.OnMultiChoiceClickListener
            public void onClick(DialogInterface dialogInterface, int i2, boolean z) {
                if (z) {
                    if (integerArrayList.size() == 5) {
                        zArr[i2] = false;
                        RemindersDialogFragment.this.mListView.setItemChecked(i2, false);
                        if (RemindersDialogFragment.this.mMaxRemindersToast != null) {
                            RemindersDialogFragment.this.mMaxRemindersToast.cancel();
                        }
                        RemindersDialogFragment remindersDialogFragment = RemindersDialogFragment.this;
                        remindersDialogFragment.mMaxRemindersToast = Toast.makeText(remindersDialogFragment.getActivity(), R.string.maximum_number_reminders_selected, 0);
                        RemindersDialogFragment.this.mMaxRemindersToast.show();
                        return;
                    }
                    integerArrayList.add(Integer.valueOf(i2));
                    return;
                }
                if (integerArrayList.contains(Integer.valueOf(i2))) {
                    integerArrayList.remove(Integer.valueOf(i2));
                    if (integerArrayList.size() <= 5) {
                        zArr[i2] = false;
                        RemindersDialogFragment.this.mListView.setItemChecked(i2, false);
                    }
                }
            }
        });
        builder.setPositiveButton(R.string.clr_strings_button_title_ok_txt, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.editevent.RemindersDialogFragment.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                RemindersDialogFragment.this.mDialogCallback.onDialogPositiveClick(integerArrayList);
                RemindersDialogFragment.this.dismiss();
            }
        });
        builder.setNegativeButton(R.string.discard_label, (DialogInterface.OnClickListener) null);
        AlertDialog alertDialogCreate = builder.create();
        this.mListView = alertDialogCreate.getListView();
        return alertDialogCreate;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mDialogCallback = (DialogCallback) activity;
        } catch (ClassCastException e) {
            Log.e(TAG, "ClassCastException, must implement DialogCallback", e);
        }
    }
}
