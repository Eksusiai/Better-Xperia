package com.sonymobile.calendar;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import androidx.appcompat.app.AppCompatActivity;
import com.sonyericsson.calendar.util.EventInfo;
import java.util.ArrayList;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public class EventPreviewPopup extends PopupWindow {
    private static final double MAX_ROW_COUNT = 3.5d;
    private AppCompatActivity activity;
    private PopupEventContextMenu contextMenu;
    private CalendarEventNavigator eventNavigator;
    private final int[] leftTop;
    private float mXStart;
    private final int[] widthHeight;
    private KeyboardListener mListener = new KeyboardListener() { // from class: com.sonymobile.calendar.EventPreviewPopup.2
        @Override // com.sonymobile.calendar.KeyboardListener
        public void onSpacePressed() {
        }

        @Override // com.sonymobile.calendar.KeyboardListener
        public void onEscapePressed() {
            if (EventPreviewPopup.this.isShowing()) {
                EventPreviewPopup.this.dismiss();
            }
        }
    };
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() { // from class: com.sonymobile.calendar.EventPreviewPopup.3
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (EventPreviewPopup.this.isShowing()) {
                EventPreviewPopup.this.dismiss();
            }
        }
    };

    public EventPreviewPopup(Context context, EventInfo[] eventInfoArr, int[] iArr, int[] iArr2, CalendarEventNavigator calendarEventNavigator) {
        this.activity = (AppCompatActivity) context;
        this.leftTop = (int[]) iArr.clone();
        this.widthHeight = (int[]) iArr2.clone();
        this.eventNavigator = calendarEventNavigator;
        initPopup(context, eventInfoArr);
        initListView(context, eventInfoArr);
    }

    public void show(View view, float f, float f2) {
        int height = (int) (f2 - (getHeight() / 2));
        int[] iArr = this.leftTop;
        int i = iArr[1];
        int i2 = iArr[1] + this.widthHeight[1];
        if (getHeight() + height > i2) {
            height -= (getHeight() + height) - i2;
        } else if (height < i) {
            height = i;
        }
        showAtLocation(view, 0, (int) this.mXStart, height);
    }

    @Override // android.widget.PopupWindow
    public void dismiss() {
        PopupEventContextMenu popupEventContextMenu = this.contextMenu;
        if (popupEventContextMenu != null && popupEventContextMenu.isVisible()) {
            this.contextMenu.dismiss();
        }
        super.dismiss();
    }

    private void initPopup(Context context, EventInfo[] eventInfoArr) {
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setClippingEnabled(false);
        setAnimationStyle(R.style.PopupZoomTransition);
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        int i = point.x;
        this.mXStart = 0.0f;
        if (Utils.isTabletDevice(this.activity) && context.getResources().getConfiguration().orientation == 2) {
            setWidth(this.widthHeight[0] + ((int) context.getResources().getDimension(R.dimen.event_preview_popup_side_border)));
        } else {
            setWidth(i);
        }
        setHeight((int) (((double) context.getResources().getDimension(R.dimen.agenda_item_height_popup)) * Math.min(MAX_ROW_COUNT, eventInfoArr.length)));
    }

    private void initListView(Context context, EventInfo[] eventInfoArr) {
        AgendaAdapter agendaAdapter = new AgendaAdapter(context, R.layout.agenda_item, new ArrayList(Arrays.asList(eventInfoArr)), true);
        agendaAdapter.setListIsInPreview();
        final AgendaListView agendaListView = (AgendaListView) ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.event_preview_popup, (ViewGroup) null);
        agendaListView.setIsInMonthView();
        agendaListView.setAdapterAndLoader(agendaAdapter, null);
        agendaListView.setKeyboardListener(this.mListener);
        agendaListView.setPreviewItemClickListener(this.itemClickListener);
        agendaListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: com.sonymobile.calendar.EventPreviewPopup.1
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                EventPreviewPopup.this.contextMenu = new PopupEventContextMenu(EventPreviewPopup.this.activity, (EventInfo) agendaListView.getItemAtPosition(i), EventPreviewPopup.this.eventNavigator, EventPreviewPopup.this.itemClickListener);
                EventPreviewPopup.this.contextMenu.show(EventPreviewPopup.this.activity.getSupportFragmentManager(), "contextmenu");
                return true;
            }
        });
        setContentView(agendaListView);
    }
}
