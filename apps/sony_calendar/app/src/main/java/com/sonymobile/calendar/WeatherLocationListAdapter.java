package com.sonymobile.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.sonymobile.accuweather.WeatherLocation;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class WeatherLocationListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<WeatherLocation> mLocations;

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return null;
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return 0L;
    }

    public WeatherLocationListAdapter(ArrayList<WeatherLocation> arrayList, Context context) {
        this.mLocations = arrayList;
        this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mLocations.size();
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.mInflater.inflate(R.layout.weather_location_list_item, viewGroup, false);
        }
        ((TextView) view.findViewById(R.id.listText)).setText(this.mLocations.get(i).toString());
        return view;
    }
}
