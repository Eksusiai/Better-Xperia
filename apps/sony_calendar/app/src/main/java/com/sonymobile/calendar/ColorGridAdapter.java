package com.sonymobile.calendar;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class ColorGridAdapter extends BaseAdapter {
    private final String[] accessibilityColors;
    private final int[] colors;
    private Context context;
    private int selectedColor;

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public ColorGridAdapter(Context context, int[] iArr, String[] strArr, int i) {
        this.selectedColor = 0;
        this.context = context;
        this.colors = (int[]) iArr.clone();
        this.accessibilityColors = (String[]) strArr.clone();
        this.selectedColor = i;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.colors.length;
    }

    public int getColor(int i) {
        return this.colors[i];
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.color_grid_item, viewGroup, false);
        }
        int[] iArr = this.colors;
        int i2 = iArr[i];
        boolean z = this.selectedColor == iArr[i];
        ((ImageView) view.findViewById(R.id.color_item)).getDrawable().setColorFilter(UiUtils.getDisplayColorFromColor(i2), PorterDuff.Mode.MULTIPLY);
        view.findViewById(R.id.color_item_check_mark).setVisibility(z ? 0 : 8);
        view.setContentDescription(this.accessibilityColors[i]);
        return view;
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return Integer.valueOf(this.colors[i]);
    }
}
