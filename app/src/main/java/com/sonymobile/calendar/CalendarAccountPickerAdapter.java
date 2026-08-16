package com.sonymobile.calendar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarAccountPickerAdapter extends ArrayAdapter<CalendarItem> {
    private ArrayList<CalendarItem> mCalendars;
    private Context mContext;
    private int mLayoutResource;

    static class ViewHolder {
        TextView displayText;
        TextView typeText;

        ViewHolder() {
        }
    }

    public CalendarAccountPickerAdapter(Context context, int i, ArrayList<CalendarItem> arrayList) {
        super(context, i, arrayList);
        this.mCalendars = arrayList;
        this.mContext = context;
        this.mLayoutResource = i;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(this.mContext, this.mLayoutResource, null);
        }
        ViewHolder viewHolder = view.getTag() instanceof ViewHolder ? (ViewHolder) view.getTag() : null;
        CalendarItem calendarItem = this.mCalendars.get(i);
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.typeText = (TextView) view.findViewById(R.id.typeLabel);
            viewHolder.displayText = (TextView) view.findViewById(R.id.displayLabel);
            view.setTag(viewHolder);
        }
        viewHolder.displayText.setText(Utils.getCalendarDisplayName(this.mContext, calendarItem.accountType, calendarItem.displayName));
        viewHolder.typeText.setText(Utils.getDisplayNameForAccountType(this.mContext, calendarItem.accountType));
        return view;
    }
}
