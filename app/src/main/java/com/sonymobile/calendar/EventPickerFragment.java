package com.sonymobile.calendar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.sonyericsson.calendar.util.CalendarConstants;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonyericsson.calendar.util.VCalendarParser;
import com.sonymobile.calendar.utils.UiUtils;
import java.util.ArrayList;
import java.util.HashSet;

/* JADX INFO: loaded from: classes2.dex */
public class EventPickerFragment extends Fragment {
    public static final String TAG = "EventPickerFragment";
    private View buttonBar;
    private Button cancelButton;
    private Button doneButton;
    private View eventPickerView;
    private ImageView handleArrow;
    private TextView handleTextView;
    private AgendaListView mAgendaListView;
    private View mEventPickerAgendaLayout;
    protected String mQuery;
    private VCalendarParser parser;
    private ArrayList<EventInfo> selectedEvents = new ArrayList<>();
    private AgendaAdapter selectedEventsAdapter;
    private SlidingDrawer slidingDrawer;

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.parser = VCalendarParser.getInstance();
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.event_picker_activity, (ViewGroup) null);
        this.eventPickerView = viewInflate;
        AgendaListView agendaListView = (AgendaListView) viewInflate.findViewById(R.id.agenda_listview);
        this.mAgendaListView = agendaListView;
        agendaListView.setIsInEventPickerFragment(this);
        this.mAgendaListView.initEventLoader(this);
        this.mEventPickerAgendaLayout = this.eventPickerView.findViewById(R.id.event_picker_agenda_layout);
        initSearchView((SearchView) this.eventPickerView.findViewById(R.id.search_view));
        initSelectedEventsList();
        initButtons();
        Toolbar toolbar = (Toolbar) this.eventPickerView.findViewById(R.id.toolbar);
        UiUtils.setViewBackgroundToPrimaryColor(getContext(), toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        return this.eventPickerView;
    }

    private void initSelectedEventsList() {
        SlidingDrawer slidingDrawer = (SlidingDrawer) this.eventPickerView.findViewById(R.id.slidingdrawer);
        this.slidingDrawer = slidingDrawer;
        slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener());
        this.slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener());
        UiUtils.setViewBackgroundToPrimaryColor(getContext(), this.eventPickerView.findViewById(R.id.handle));
        this.handleArrow = (ImageView) this.slidingDrawer.findViewById(R.id.handle_arrow);
        this.handleTextView = (TextView) this.slidingDrawer.findViewById(R.id.selected_events);
        ListView listView = (ListView) this.slidingDrawer.findViewById(R.id.selected_events_list);
        AgendaAdapter agendaAdapter = new AgendaAdapter(getActivity(), R.layout.agenda_item_deselectable, this.selectedEvents, false);
        this.selectedEventsAdapter = agendaAdapter;
        listView.setAdapter((ListAdapter) agendaAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.sonymobile.calendar.EventPickerFragment.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                EventPickerFragment.this.eventClicked(EventPickerFragment.this.selectedEventsAdapter.getItem(i));
                EventPickerFragment.this.selectedEventsAdapter.notifyDataSetChanged();
            }
        });
    }

    private class OnDrawerOpenListener implements SlidingDrawer.OnDrawerOpenListener {
        private OnDrawerOpenListener() {
        }

        @Override // android.widget.SlidingDrawer.OnDrawerOpenListener
        public void onDrawerOpened() {
            EventPickerFragment.this.handleArrow.setImageResource(R.drawable.arrow_down);
        }
    }

    private class OnDrawerCloseListener implements SlidingDrawer.OnDrawerCloseListener {
        private OnDrawerCloseListener() {
        }

        @Override // android.widget.SlidingDrawer.OnDrawerCloseListener
        public void onDrawerClosed() {
            EventPickerFragment.this.handleArrow.setImageResource(R.drawable.arrow_up);
            if (EventPickerFragment.this.selectedEvents.isEmpty()) {
                EventPickerFragment.this.buttonBar.setVisibility(8);
                EventPickerFragment.this.slidingDrawer.setVisibility(8);
                EventPickerFragment.this.setAgendaMargin(8);
            }
        }
    }

    private void initButtons() {
        View viewFindViewById = this.eventPickerView.findViewById(R.id.buttons);
        this.buttonBar = viewFindViewById;
        viewFindViewById.setVisibility(8);
        Button button = (Button) this.eventPickerView.findViewById(R.id.btn_done);
        this.doneButton = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.EventPickerFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EventPickerFragment.this.writeCalendarFile();
            }
        });
        Button button2 = (Button) this.eventPickerView.findViewById(R.id.btn_discard);
        this.cancelButton = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.EventPickerFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EventPickerFragment.this.getActivity().setResult(0);
                EventPickerFragment.this.getActivity().finish();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeCalendarFile() {
        this.parser.writeToCalFile(getUniqueEvents(this.selectedEvents), new VCalendarParserHandler(), getActivity());
    }

    @Override // androidx.fragment.app.Fragment
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
    }

    protected ArrayList<EventInfo> getUniqueEvents(ArrayList<EventInfo> arrayList) {
        ArrayList<EventInfo> arrayList2 = new ArrayList<>();
        HashSet hashSet = new HashSet(arrayList.size());
        for (EventInfo eventInfo : arrayList) {
            if (hashSet.add(Long.valueOf(eventInfo.id))) {
                arrayList2.add(eventInfo);
            }
        }
        return arrayList2;
    }

    private class VCalendarParserHandler implements IAsyncServiceResultHandler {
        public VCalendarParserHandler() {
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            if (((Boolean) obj).booleanValue()) {
                Uri fileUri = EventPickerFragment.this.parser.getFileUri();
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType(CalendarConstants.VCAL_MIME_TYPE);
                intent.setFlags(1);
                intent.setData(fileUri);
                EventPickerFragment.this.getActivity().setResult(-1, intent);
                EventPickerFragment.this.getActivity().finish();
                return;
            }
            Toast.makeText(EventPickerFragment.this.getActivity(), EventPickerFragment.this.getString(R.string.operation_failed), 0).show();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mAgendaListView.onResume();
        performLoad();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performLoad() {
        Time time = new SafeTime(Utils.getTimeZone(getActivity(), null));
        time.set(System.currentTimeMillis());
        Utils.setDisplayTime(Long.valueOf(time.toMillis(true)));
        this.mAgendaListView.goTo(time, this.mQuery, false);
    }

    private void initSearchView(final SearchView searchView) {
        searchView.setIconified(false);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { // from class: com.sonymobile.calendar.EventPickerFragment.4
            @Override // android.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextSubmit(String str) {
                return false;
            }

            @Override // android.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextChange(String str) {
                if (!searchView.hasFocus()) {
                    return false;
                }
                EventPickerFragment eventPickerFragment = EventPickerFragment.this;
                if (str.length() <= 0) {
                    str = null;
                }
                eventPickerFragment.mQuery = str;
                EventPickerFragment.this.performLoad();
                return false;
            }
        });
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mAgendaListView.onPause();
    }

    public boolean eventClicked(EventInfo eventInfo) {
        boolean z;
        if (this.selectedEvents.contains(eventInfo)) {
            this.selectedEvents.remove(eventInfo);
            z = false;
        } else {
            this.selectedEvents.add(eventInfo);
            z = true;
        }
        int i = this.selectedEvents.size() > 0 ? 0 : 8;
        this.doneButton.setEnabled(i == 0);
        if (!this.slidingDrawer.isOpened()) {
            this.buttonBar.setVisibility(i);
            this.slidingDrawer.setVisibility(i);
            setAgendaMargin(i);
        } else {
            ((AgendaAdapter) this.mAgendaListView.getAdapter()).notifyDataSetChanged();
        }
        updateHandleText(this.selectedEvents.size());
        this.selectedEventsAdapter.notifyDataSetChanged();
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAgendaMargin(int i) {
        int dimension = i == 0 ? (int) getResources().getDimension(R.dimen.sliding_drawer_handle_height) : 0;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -1);
        layoutParams.setMargins(0, 0, 0, dimension);
        this.mEventPickerAgendaLayout.setLayoutParams(layoutParams);
    }

    private void updateHandleText(int i) {
        this.handleTextView.setText(getResources().getString(R.string.vCal_export_selected_events, Integer.valueOf(i)));
    }

    public boolean isEventSelected(EventInfo eventInfo) {
        return this.selectedEvents.contains(eventInfo);
    }
}
