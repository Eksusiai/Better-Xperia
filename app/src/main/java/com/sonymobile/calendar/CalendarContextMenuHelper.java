package com.sonymobile.calendar;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.MenuItem;
import com.sonyericsson.calendar.util.EventInfo;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarContextMenuHelper {
    public static void setTitle(Context context, ContextMenu contextMenu, EventInfo eventInfo) {
        contextMenu.setHeaderTitle((eventInfo.title == null || eventInfo.title.length() < 1) ? context.getResources().getString(R.string.no_title_label) : eventInfo.title);
    }

    public static void setTitle(Context context, ContextMenu contextMenu, long j, boolean z) {
        String str;
        if (z) {
            str = "";
        } else {
            str = DateFormat.is24HourFormat(context) ? "HH:mm " : "h:mma ";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str + "cccc");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(Utils.getTimeZone(context, null)));
        contextMenu.setHeaderTitle(simpleDateFormat.format(Long.valueOf(j)));
    }

    public static void addViewEvent(ContextMenu contextMenu, EventInfo eventInfo, MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
        MenuItem menuItemAdd = contextMenu.add(0, 4, 0, R.string.event_view);
        menuItemAdd.setOnMenuItemClickListener(onMenuItemClickListener);
        menuItemAdd.setIcon(android.R.drawable.ic_menu_info_details);
    }

    public static void addEditEvent(ContextMenu contextMenu, EventInfo eventInfo, MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
        if (eventInfo.accessLevel == 2) {
            MenuItem menuItemAdd = contextMenu.add(0, 6, 0, R.string.event_edit);
            menuItemAdd.setOnMenuItemClickListener(onMenuItemClickListener);
            menuItemAdd.setIcon(android.R.drawable.ic_menu_edit);
            menuItemAdd.setAlphabeticShortcut('e');
        }
    }

    public static void addDeleteEvent(ContextMenu contextMenu, EventInfo eventInfo, MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
        if (eventInfo.accessLevel >= 1) {
            MenuItem menuItemAdd = contextMenu.add(0, 7, 0, R.string.event_delete);
            menuItemAdd.setOnMenuItemClickListener(onMenuItemClickListener);
            menuItemAdd.setIcon(android.R.drawable.ic_menu_delete);
        }
    }

    public static void addCreateEventItem(ContextMenu contextMenu, MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
        MenuItem menuItemAdd = contextMenu.add(0, 5, 0, R.string.event_create);
        menuItemAdd.setOnMenuItemClickListener(onMenuItemClickListener);
        menuItemAdd.setIcon(android.R.drawable.ic_menu_add);
        menuItemAdd.setAlphabeticShortcut('n');
    }
}
