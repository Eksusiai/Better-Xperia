package com.sonymobile.calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.fragment.app.DialogFragment;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonymobile.calendar.utils.UiUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class PopupEventContextMenu extends DialogFragment implements AdapterView.OnItemClickListener {
    private Context context;
    private EventInfo event;
    private CalendarEventNavigator eventNavigator;
    private ArrayList<String> options;
    private AdapterView.OnItemClickListener previewClickListener;

    public PopupEventContextMenu(Context context, EventInfo eventInfo, CalendarEventNavigator calendarEventNavigator, AdapterView.OnItemClickListener onItemClickListener) {
        this.context = context;
        this.event = eventInfo;
        this.eventNavigator = calendarEventNavigator;
        this.previewClickListener = onItemClickListener;
        setRetainInstance(true);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        String string = this.event.title;
        if (this.event.title == null || this.event.title.length() < 1) {
            string = getResources().getString(R.string.no_title_label);
        }
        builder.setTitle(string);
        builder.setView(initOptionsList());
        return builder.create();
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        if (this.options.get(i).equals(this.context.getResources().getString(R.string.event_view))) {
            view.setEnabled(false);
            this.eventNavigator.goToEventDetails(this.event, UiUtils.makeZoomAnimationOnViewBundle(view));
        } else if (this.options.get(i).equals(this.context.getResources().getString(R.string.event_edit))) {
            this.eventNavigator.goToEditEvent(this.event);
        } else if (this.options.get(i).equals(this.context.getResources().getString(R.string.event_delete))) {
            this.eventNavigator.deleteEvent(this.event);
        } else {
            this.eventNavigator.goToCreateEvent(this.event.begin, this.event.allDay == 1);
        }
        this.previewClickListener.onItemClick(adapterView, view, i, j);
        dismiss();
    }

    private View initOptionsList() {
        ArrayList<String> arrayList = new ArrayList<>();
        this.options = arrayList;
        arrayList.add(this.context.getResources().getString(R.string.event_view));
        int i = this.event.accessLevel;
        if (i == 2) {
            this.options.add(this.context.getResources().getString(R.string.event_edit));
        }
        if (i >= 1) {
            this.options.add(this.context.getResources().getString(R.string.event_delete));
        }
        this.options.add(this.context.getResources().getString(R.string.event_create));
        ArrayAdapter arrayAdapter = new ArrayAdapter(this.context, android.R.layout.simple_list_item_1, this.options);
        ListView listView = new ListView(this.context);
        listView.setAdapter((ListAdapter) arrayAdapter);
        listView.setOnItemClickListener(this);
        return listView;
    }
}
