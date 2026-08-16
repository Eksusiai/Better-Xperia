package com.sonymobile.calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.common.base.Strings;
import com.sonyericsson.calendar.util.CalendarInstanceService;
import com.sonymobile.calendar.utils.UiUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class DrawerAdapter extends BaseAdapter {
    private static final int CALENDAR_VIEW_TYPE = 0;
    private static final int DIVIDER_VIEW_TYPE = 1;
    private static final int ITEM_VIEW_TYPE = 2;
    private static final int VIEW_COUNT = 4;
    private static final int ZERO_HEIGHT_VIEW_TYPE = 3;
    private Context mContext;
    private Time mDate;
    private LayoutInflater mInflater;
    private ArrayList<DrawerItem> mItems;
    private int mSelectedPosition = -1;

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return 0L;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        return 4;
    }

    private static class ViewHolder {
        public CheckBox checkBox;
        public ImageView indicator;
        public TextView subTitle;
        public TextView title;

        private ViewHolder() {
        }
    }

    public DrawerAdapter(Context context, ArrayList<DrawerItem> arrayList) {
        this.mItems = arrayList;
        this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.mContext = context;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mItems.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return this.mItems.get(i);
    }

    public DrawerItem.DrawerItemType getItemType(int i) {
        return this.mItems.get(i).getItemType();
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int i) {
        DrawerItem.DrawerItemType itemType = getItemType(i);
        if (itemType == DrawerItem.DrawerItemType.CALENDAR) {
            return 0;
        }
        if (itemType == DrawerItem.DrawerItemType.DIVIDER) {
            return 1;
        }
        return (itemType != DrawerItem.DrawerItemType.AGENDA || Utils.isTabletDevice(this.mContext)) ? 2 : 3;
    }

    public void updateDate(Time time) {
        this.mDate = time;
    }

    public void setSelectedItem(int i) {
        this.mSelectedPosition = i;
        notifyDataSetChanged();
    }

    public int getSelectedItemPosition() {
        return this.mSelectedPosition;
    }

    public DrawerItem.DrawerItemType getSelectedDrawerItemType() {
        int i = this.mSelectedPosition;
        if (i != -1) {
            return this.mItems.get(i).getItemType();
        }
        return DrawerItem.DrawerItemType.MONTH;
    }

    public void setCalendar(DrawerItemCheckable drawerItemCheckable) {
        if (!this.mItems.contains(drawerItemCheckable)) {
            this.mItems.add(drawerItemCheckable);
        } else {
            this.mItems.set(this.mItems.indexOf(drawerItemCheckable), drawerItemCheckable);
        }
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        View currentFocus = ((Activity) viewGroup.getContext()).getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
        int itemViewType = getItemViewType(i);
        if (itemViewType == 0) {
            return getViewCalendar(i, view, viewGroup);
        }
        if (itemViewType == 1) {
            return getViewDivider(i, view, viewGroup);
        }
        if (itemViewType == 2) {
            return getViewItem(i, view, viewGroup);
        }
        return getZeroHeightViewType(i, view, viewGroup);
    }

    private View getZeroHeightViewType(int i, View view, ViewGroup viewGroup) {
        return view == null ? this.mInflater.inflate(R.layout.zero_height_view, viewGroup, false) : view;
    }

    private View getViewCalendar(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = this.mInflater.inflate(R.layout.drawer_list_item_checkbox, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.indicator = (ImageView) view.findViewById(R.id.color_indicator);
            viewHolder.title = (TextView) view.findViewById(R.id.drawer_text);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            viewHolder.subTitle = (TextView) view.findViewById(R.id.drawer_subtext);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final DrawerItemCheckable drawerItemCheckable = (DrawerItemCheckable) this.mItems.get(i);
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.sonymobile.calendar.DrawerAdapter.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (drawerItemCheckable.isChecked() != z) {
                    drawerItemCheckable.setChecked(z);
                    CalendarInstanceService.getInstance().updateVisibility(DrawerAdapter.this.mContext, z, drawerItemCheckable.getId(), drawerItemCheckable.getAccountType());
                }
            }
        });
        viewHolder.checkBox.setChecked(drawerItemCheckable.isChecked());
        viewHolder.indicator.getDrawable().mutate().setColorFilter(UiUtils.getDisplayColorFromColor(drawerItemCheckable.getColor()), PorterDuff.Mode.MULTIPLY);
        viewHolder.indicator.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.DrawerAdapter.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                new CalendarColorQuery((AppCompatActivity) DrawerAdapter.this.mContext, new OnColorPickedListener() { // from class: com.sonymobile.calendar.DrawerAdapter.2.1
                    @Override // com.sonymobile.calendar.OnColorPickedListener
                    public void onColorPicked(int i2, boolean z) {
                        if (z) {
                            CalendarInstanceService.getInstance().updateCalendarColorByKey(DrawerAdapter.this.mContext, i2, false, drawerItemCheckable.getId());
                        } else {
                            CalendarInstanceService.getInstance().updateCalendarColor(DrawerAdapter.this.mContext, i2, false, drawerItemCheckable.getSubtitle(), drawerItemCheckable.getAccountType(), drawerItemCheckable.getId());
                        }
                    }
                }, drawerItemCheckable.getColor(), false).startLoader(drawerItemCheckable);
            }
        });
        viewHolder.title.setText(drawerItemCheckable.getTitle());
        if (drawerItemCheckable.getSubtitle() != null && !drawerItemCheckable.getSubtitle().isEmpty()) {
            viewHolder.subTitle.setText(drawerItemCheckable.getSubtitle());
            viewHolder.subTitle.setVisibility(0);
        } else {
            viewHolder.subTitle.setVisibility(8);
        }
        return view;
    }

    private View getViewDivider(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = this.mInflater.inflate(R.layout.drawer_list_item_header, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.indicator = (ImageView) view.findViewById(R.id.color_indicator);
            viewHolder.title = (TextView) view.findViewById(R.id.drawer_text);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        DrawerItem drawerItem = this.mItems.get(i);
        int secondaryTextColor = UiUtils.getSecondaryTextColor(this.mContext);
        viewHolder.indicator.setBackgroundColor(secondaryTextColor);
        viewHolder.title.setText(drawerItem.getTitle());
        viewHolder.title.setTextColor(secondaryTextColor);
        return view;
    }

    private View getViewItem(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = this.mInflater.inflate(R.layout.drawer_list_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.indicator = (ImageView) view.findViewById(R.id.color_indicator);
            viewHolder.title = (TextView) view.findViewById(R.id.drawer_text);
            viewHolder.subTitle = (TextView) view.findViewById(R.id.drawer_subtext);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        DrawerItem drawerItem = this.mItems.get(i);
        viewHolder.title.setText(drawerItem.getTitle());
        if (this.mSelectedPosition == i || (!Utils.isTabletDevice(this.mContext) && drawerItem.getItemType() == DrawerItem.DrawerItemType.MONTH && getSelectedDrawerItemType() == DrawerItem.DrawerItemType.AGENDA)) {
            int primaryTextColor = UiUtils.getPrimaryTextColor(this.mContext);
            viewHolder.title.setTextColor(primaryTextColor);
            viewHolder.subTitle.setTextColor(primaryTextColor);
            viewHolder.indicator.setVisibility(0);
        } else {
            viewHolder.indicator.setVisibility(4);
            int primaryTextColor2 = UiUtils.getPrimaryTextColor(this.mContext);
            viewHolder.title.setTextColor(primaryTextColor2);
            viewHolder.subTitle.setTextColor(primaryTextColor2);
        }
        drawerItem.setSubtitle(drawerItem.formatDrawerString(this.mContext, this.mDate));
        if (!Strings.isNullOrEmpty(drawerItem.getSubtitle())) {
            viewHolder.subTitle.setText(drawerItem.getSubtitle());
            viewHolder.subTitle.setVisibility(0);
        } else {
            viewHolder.subTitle.setVisibility(8);
        }
        return view;
    }

    public void updateItems(ArrayList<DrawerItem> arrayList) {
        this.mItems = arrayList;
    }
}
