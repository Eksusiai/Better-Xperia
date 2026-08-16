package com.sonymobile.calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import com.sonyericsson.calendar.util.CalendarConstants;
import com.sonyericsson.calendar.util.CalendarInstanceService;
import com.sonyericsson.calendar.util.CalendarParser;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonyericsson.calendar.util.ICalendarParser;
import com.sonyericsson.calendar.util.VCalendarParser;
import java.util.ArrayList;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarAccountPickerDialog extends DialogFragment {
    private static final String ICS_FILE_EXTENSION = "ics";
    public static final String TAG = "calendarAccount_picker";
    private static final String VCS_FILE_EXTENSION = "vcs";
    private CalendarParser parser;

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setTitle(getActivity().getResources().getString(R.string.vCal_add_event_title));
        builder.setView(initPickerView());
        return builder.create();
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        getActivity().finish();
    }

    private ListView initPickerView() {
        Map<Long, CalendarItem> calendars = CalendarInstanceService.getInstance().getCalendars();
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(calendars.values());
        ListView listView = new ListView(getActivity());
        final CalendarAccountPickerAdapter calendarAccountPickerAdapter = new CalendarAccountPickerAdapter(getActivity(), R.layout.calendar_account_item, arrayList);
        listView.setAdapter((ListAdapter) calendarAccountPickerAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.sonymobile.calendar.CalendarAccountPickerDialog.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                String strSubstring;
                FragmentActivity activity = CalendarAccountPickerDialog.this.getActivity();
                CalendarAccountPickerDialog.this.dismiss();
                MyProgressDialog myProgressDialog = new MyProgressDialog();
                myProgressDialog.show(CalendarAccountPickerDialog.this.getFragmentManager(), "MyProgressDialog");
                CalendarItem item = calendarAccountPickerAdapter.getItem(i);
                Intent intent = CalendarAccountPickerDialog.this.getActivity().getIntent();
                Uri data = intent.getData();
                if (data != null) {
                    String lastPathSegment = data.getLastPathSegment();
                    strSubstring = lastPathSegment.substring(lastPathSegment.lastIndexOf(".") + 1);
                } else {
                    strSubstring = null;
                }
                String type = intent.getType();
                if (CalendarAccountPickerDialog.VCS_FILE_EXTENSION.equalsIgnoreCase(strSubstring) || CalendarConstants.VCAL_MIME_TYPE.equalsIgnoreCase(type)) {
                    CalendarAccountPickerDialog.this.parser = VCalendarParser.getInstance();
                } else if (CalendarAccountPickerDialog.ICS_FILE_EXTENSION.equalsIgnoreCase(strSubstring) || CalendarConstants.ICAL_MIME_TYPE.equalsIgnoreCase(type)) {
                    CalendarAccountPickerDialog.this.parser = ICalendarParser.getInstance();
                } else {
                    Toast.makeText(CalendarAccountPickerDialog.this.getActivity(), R.string.file_open_failed_toast_txt, 0).show();
                    myProgressDialog.dismiss();
                    activity.finish();
                    return;
                }
                CalendarAccountPickerDialog.this.parser.parseUri(CalendarAccountPickerDialog.this.getActivity(), data, item.calendarId, CalendarAccountPickerDialog.this.new VCalendarParserHandler(myProgressDialog, activity));
            }
        });
        return listView;
    }

    private class VCalendarParserHandler implements IAsyncServiceResultHandler {
        private static final int PLURAL = 10;
        Activity mActivity;
        MyProgressDialog mDialog;
        private String mFailure;
        private String mSuccess;

        public VCalendarParserHandler(MyProgressDialog myProgressDialog, Activity activity) {
            this.mActivity = activity;
            this.mDialog = myProgressDialog;
            this.mSuccess = activity.getResources().getQuantityString(R.plurals.vCal_toast_add_success, 10);
            this.mFailure = CalendarAccountPickerDialog.this.getString(R.string.operation_failed);
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            if (obj != null && this.mDialog.isAdded()) {
                Toast.makeText(this.mActivity, ((Boolean) obj).booleanValue() ? this.mSuccess : this.mFailure, 0).show();
                this.mDialog.dismiss();
                this.mActivity.finish();
            }
        }
    }

    public static class MyProgressDialog extends DialogFragment {
        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle(R.string.vCal_progress_title);
            progressDialog.setIndeterminate(true);
            return progressDialog;
        }
    }
}
