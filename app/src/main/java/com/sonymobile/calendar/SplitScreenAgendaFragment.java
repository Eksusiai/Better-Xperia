package com.sonymobile.calendar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import androidx.core.os.BuildCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.google.common.primitives.Ints;
import com.sonyericsson.calendar.util.DaySpan;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.agendamonth.SplitScreenFragment;
import com.sonymobile.calendar.agendapager.AgendaPagerAdapter;
import com.sonymobile.calendar.agendapager.SplitScreenAgendaViewPager;
import com.sonymobile.calendar.birthday.BirthdayActivity;
import com.sonymobile.calendar.birthday.BirthdayPreferences;
import com.sonymobile.calendar.birthday.BirthdayService;
import com.sonymobile.calendar.birthday.ContactBirthday;
import com.sonymobile.calendar.birthday.ContactPhotoService;
import com.sonymobile.calendar.linkedin.LinkedInUtils;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.calendar.tablet.TabletMonthControllerFragment;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.calendar.widget.WhiteHeader;
import com.sonymobile.lunar.lib.LunarContract;
import com.sonymobile.lunar.lib.LunarUtils;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class SplitScreenAgendaFragment extends Fragment implements Navigator, AgendaListView.AgendaListNavigatorCallback {
    private static final boolean DEBUG = true;
    private static String INIT_DAY = "day";
    private static final String TAG = "AgendaFragment";
    private AgendaPagerAdapter agendaPagerAdapter;
    private View birthdayHeader;
    private Time lateExecute;
    private AgendaListView mAgendaListView;
    private View mConnectWithLinkedInFooterView;
    private ContentResolver mContentResolver;
    private View mHintPromoHeaderView;
    private Fragment mParent;
    private Time mTime;
    private WhiteHeader mWhiteHeader;
    private boolean birthdaysEnabled = false;
    private boolean isBeingAnimated = false;
    private int NO_BIRTHDAYS = 0;
    private int SINGLE_BIRTHDAY = 1;
    private Runnable mUpdateTZ = new Runnable() { // from class: com.sonymobile.calendar.SplitScreenAgendaFragment.1
        @Override // java.lang.Runnable
        public void run() {
            long millis = SplitScreenAgendaFragment.this.mTime.toMillis(true);
            SplitScreenAgendaFragment.this.mTime = new SafeTime(Utils.getTimeZone(SplitScreenAgendaFragment.this.getActivity(), this));
            SplitScreenAgendaFragment.this.mTime.set(millis);
        }
    };
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() { // from class: com.sonymobile.calendar.SplitScreenAgendaFragment.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.TIME_SET") || action.equals("android.intent.action.DATE_CHANGED") || action.equals("android.intent.action.TIMEZONE_CHANGED")) {
                ListAdapter adapter = SplitScreenAgendaFragment.this.mAgendaListView.getAdapter();
                if (adapter instanceof AgendaAdapter) {
                    ((AgendaAdapter) adapter).notifyDataSetChanged();
                }
            }
        }
    };
    private final ContentObserver mObserver = new ContentObserver(new Handler()) { // from class: com.sonymobile.calendar.SplitScreenAgendaFragment.3
        @Override // android.database.ContentObserver
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            SplitScreenAgendaFragment.this.mAgendaListView.onCalendarsChanged();
        }
    };

    @Override // com.sonymobile.calendar.Navigator
    public CharSequence getDateString() {
        return "";
    }

    @Override // com.sonymobile.calendar.Navigator
    public long getSelectedTimeInMillis() {
        return 0L;
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goTo(Time time, boolean z) {
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToNext(float f) {
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToPrevious(float f) {
    }

    @Override // com.sonymobile.calendar.Navigator
    public void updateActionBar(Time time) {
    }

    public static SplitScreenAgendaFragment newInstance(int i) {
        SplitScreenAgendaFragment splitScreenAgendaFragment = new SplitScreenAgendaFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(INIT_DAY, i);
        splitScreenAgendaFragment.setArguments(bundle);
        return splitScreenAgendaFragment;
    }

    public void setPagerAdapter(AgendaPagerAdapter agendaPagerAdapter) {
        this.agendaPagerAdapter = agendaPagerAdapter;
    }

    @Override // com.sonymobile.calendar.AgendaListView.AgendaListNavigatorCallback
    public void goToNext() {
        this.agendaPagerAdapter.moveToNext();
    }

    @Override // com.sonymobile.calendar.AgendaListView.AgendaListNavigatorCallback
    public void moveToPrevious() {
        this.agendaPagerAdapter.moveToPrevious();
    }

    public boolean isListAtTop() {
        AgendaListView agendaListView = this.mAgendaListView;
        if (agendaListView == null) {
            return false;
        }
        return agendaListView.isListAtTop();
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContentResolver = getActivity().getContentResolver();
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.splitscreen_agenda_fragment, (ViewGroup) null);
        initAgendaListView(viewInflate);
        initWhiteHeader(viewInflate);
        updateAgendaAndHeadersOnM();
        return viewInflate;
    }

    private void initHintAndPromoView() {
        View view;
        if (HintPromoteUtils.shouldShowHintAndPromoInListView(getActivity())) {
            if (this.mAgendaListView.getFooterViewsCount() <= 0 || this.mHintPromoHeaderView == null) {
                View viewInflate = ((LayoutInflater) getActivity().getApplicationContext().getSystemService("layout_inflater")).inflate(R.layout.hint_and_promo_item, (ViewGroup) null, false);
                this.mHintPromoHeaderView = viewInflate;
                viewInflate.findViewById(R.id.text_continue).setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.SplitScreenAgendaFragment.4
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        SplitScreenAgendaFragment.this.startActivity(new Intent(SplitScreenAgendaFragment.this.getActivity(), (Class<?>) HintPromoteActivity.class));
                    }
                });
                this.mHintPromoHeaderView.findViewById(R.id.text_skip).setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.SplitScreenAgendaFragment.5
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        HintPromoteUtils.removeHintAndPromoteFromListView(SplitScreenAgendaFragment.this.getActivity());
                        SplitScreenAgendaFragment.this.mAgendaListView.removeFooterView(SplitScreenAgendaFragment.this.mHintPromoHeaderView);
                        SplitScreenAgendaFragment.this.initConnectWithLinkedInView();
                        if (SplitScreenAgendaFragment.this.mParent instanceof SplitScreenFragment) {
                            ((SplitScreenFragment) SplitScreenAgendaFragment.this.mParent).updateAgendaPager();
                        }
                        if (SplitScreenAgendaFragment.this.mParent instanceof TabletMonthControllerFragment) {
                            ((TabletMonthControllerFragment) SplitScreenAgendaFragment.this.mParent).updateAgendaPager();
                        }
                    }
                });
                ((TextView) this.mHintPromoHeaderView.findViewById(R.id.hint_caption)).setTextColor(UiUtils.getPrimaryTextColor(getActivity()));
                this.mAgendaListView.addFooterView(this.mHintPromoHeaderView, null, false);
                return;
            }
            return;
        }
        if (this.mAgendaListView.getFooterViewsCount() <= 0 || (view = this.mHintPromoHeaderView) == null) {
            return;
        }
        this.mAgendaListView.removeFooterView(view);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initConnectWithLinkedInView() {
        View view;
        if (LinkedInUtils.shouldShowConnectViewInListView(getActivity())) {
            if (this.mAgendaListView.getFooterViewsCount() <= 0 || this.mConnectWithLinkedInFooterView == null) {
                View viewInflate = ((LayoutInflater) getActivity().getApplicationContext().getSystemService("layout_inflater")).inflate(R.layout.sync_with_linkedin, (ViewGroup) this.mAgendaListView, false);
                this.mConnectWithLinkedInFooterView = viewInflate;
                ((ImageButton) viewInflate.findViewById(R.id.close_linkedin_icon)).setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.SplitScreenAgendaFragment.6
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        LinkedInUtils.closeSyncWithLinkedIn(SplitScreenAgendaFragment.this.getActivity(), 0);
                        SplitScreenAgendaFragment.this.mConnectWithLinkedInFooterView.setVisibility(8);
                        if (SplitScreenAgendaFragment.this.mParent instanceof SplitScreenFragment) {
                            ((SplitScreenFragment) SplitScreenAgendaFragment.this.mParent).updateAgendaPager();
                        }
                        if (SplitScreenAgendaFragment.this.mParent instanceof TabletMonthControllerFragment) {
                            ((TabletMonthControllerFragment) SplitScreenAgendaFragment.this.mParent).updateAgendaPager();
                        }
                    }
                });
                ((Button) this.mConnectWithLinkedInFooterView.findViewById(R.id.connect_with_linkedin)).setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.SplitScreenAgendaFragment.7
                    private static final long CLICK_DELAY = 500;
                    private long mLastClick;

                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        long jCurrentTimeMillis = System.currentTimeMillis();
                        if (Math.abs(this.mLastClick - jCurrentTimeMillis) < CLICK_DELAY) {
                            return;
                        }
                        this.mLastClick = jCurrentTimeMillis;
                        LinkedInUtils.startSyncWithLinkedInActivity(SplitScreenAgendaFragment.this.getActivity(), LinkedInUtils.LinkedInActivationEntrypoint.AGENDA);
                    }
                });
                this.mConnectWithLinkedInFooterView.findViewById(R.id.sync_with_linkedin_hidden_view).setVisibility(0);
                ((ImageView) this.mConnectWithLinkedInFooterView.findViewById(R.id.sync_with_linkedin_devider)).setVisibility(0);
                ((TextView) this.mConnectWithLinkedInFooterView.findViewById(R.id.learn_about_participants)).setTextColor(UiUtils.getSecondaryTextColor(getActivity()));
                this.mAgendaListView.addFooterView(this.mConnectWithLinkedInFooterView);
                return;
            }
            return;
        }
        if (this.mAgendaListView.getFooterViewsCount() <= 0 || (view = this.mConnectWithLinkedInFooterView) == null) {
            return;
        }
        this.mAgendaListView.removeFooterView(view);
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mTime = new SafeTime();
        this.birthdaysEnabled = isBirthdaysEnabled();
        this.mUpdateTZ.run();
        updateAgendaAndHeaders();
        Log.v(TAG, "OnResume to " + this.mTime.toString());
        this.mAgendaListView.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.DATE_CHANGED");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        getActivity().registerReceiver(this.mIntentReceiver, intentFilter, 2);
        this.mContentResolver.registerContentObserver(CalendarContract.Events.CONTENT_URI, true, this.mObserver);
        if (LunarAvailabilityManager.isLunarAvailable(getActivity())) {
            this.mContentResolver.registerContentObserver(LunarContract.Events.CONTENT_URI, true, this.mObserver);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
    }

    private void updateAgendaAndHeaders() {
        this.mTime = new SafeTime();
        this.birthdaysEnabled = isBirthdaysEnabled();
        this.mUpdateTZ.run();
        int i = getArguments().getInt(INIT_DAY, 0);
        Time time = new SafeTime(Utils.getTimeZone(getActivity(), null));
        time.setJulianDay(i);
        updateAgendaList(time);
        updateWhiteHeader(time);
    }

    private void updateAgendaAndHeadersOnM() {
        if (BuildCompat.isAtLeastN() && getActivity().isInMultiWindowMode()) {
            updateAgendaAndHeaders();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.mIntentReceiver);
        this.mContentResolver.unregisterContentObserver(this.mObserver);
        this.mAgendaListView.onPause();
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToToday() {
        Time time = new SafeTime(Utils.getTimeZone(getActivity(), this.mUpdateTZ));
        time.set(System.currentTimeMillis());
        Utils.setDisplayTime(Long.valueOf(time.toMillis(false)));
        goTo(time, true);
    }

    public void onTransitionAnimationStart(int i) {
        if (this.mAgendaListView.getCount() - this.mAgendaListView.getHeaderViewsCount() != 0) {
            AgendaListView agendaListView = this.mAgendaListView;
            if (agendaListView.getChildAt(agendaListView.getHeaderViewsCount()) == null) {
                return;
            }
            AgendaListView agendaListView2 = this.mAgendaListView;
            View childAt = agendaListView2.getChildAt(agendaListView2.getHeaderViewsCount());
            int count = this.mAgendaListView.getCount() - this.mAgendaListView.getHeaderViewsCount();
            View view = this.birthdayHeader;
            int height = (childAt.getHeight() * count) + ((int) (view != null && view.findViewById(R.id.birthdayItemContainer).getVisibility() == 0 ? getResources().getDimension(R.dimen.birthday_item_height) : 0.0f));
            AgendaListView agendaListView3 = this.mAgendaListView;
            agendaListView3.measure(View.MeasureSpec.makeMeasureSpec(agendaListView3.getWidth(), Ints.MAX_POWER_OF_TWO), View.MeasureSpec.makeMeasureSpec(height, Ints.MAX_POWER_OF_TWO));
            AgendaListView agendaListView4 = this.mAgendaListView;
            agendaListView4.layout(agendaListView4.getLeft(), this.mAgendaListView.getTop(), this.mAgendaListView.getLeft() + this.mAgendaListView.getMeasuredWidth(), this.mAgendaListView.getTop() + this.mAgendaListView.getMeasuredHeight());
            this.mAgendaListView.setBlockLayout(true);
            this.isBeingAnimated = true;
        }
    }

    public void onTransitionAnimationCompleted() {
        AgendaListView agendaListView = this.mAgendaListView;
        if (agendaListView != null) {
            agendaListView.setBlockLayout(false);
            this.isBeingAnimated = false;
        }
    }

    public boolean isTransitionAnimationOngoing() {
        return this.isBeingAnimated;
    }

    public void updateAgendaList(Time time) {
        if (this.mAgendaListView == null) {
            return;
        }
        if (time == null || this.mTime == null || (time.year == this.mTime.year && time.month == this.mTime.month && time.monthDay == this.mTime.monthDay)) {
            this.mAgendaListView.setVisibility(0);
            return;
        }
        this.mAgendaListView.setHideDeclinedEvents(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getBoolean(GeneralPreferences.KEY_HIDE_DECLINED, true));
        this.mTime = new SafeTime(time);
        this.mAgendaListView.goToDay(time, null);
        this.mAgendaListView.setVisibility(0);
        updateBirthdayHeader(time);
    }

    private void updateWhiteHeader(Time time) {
        if (this.mWhiteHeader == null) {
            return;
        }
        Time time2 = new SafeTime(time);
        this.mTime = time2;
        this.mWhiteHeader.setDate(time2, ViewType.MONTH);
        this.mWhiteHeader.invalidate();
    }

    public void hideAgendaList() {
        AgendaListView agendaListView = this.mAgendaListView;
        if (agendaListView == null) {
            return;
        }
        agendaListView.setVisibility(8);
    }

    public void setParent(Fragment fragment) {
        this.mParent = fragment;
    }

    private void initAgendaListView(View view) {
        AgendaListView agendaListView = (AgendaListView) view.findViewById(R.id.splitscreen_agenda);
        this.mAgendaListView = agendaListView;
        agendaListView.initEventLoader(this);
        this.mAgendaListView.setIsInMonthView();
        this.mAgendaListView.setAgendaListNavigator(this);
        initBirthdayHeader();
    }

    private void initWhiteHeader(View view) {
        this.mWhiteHeader = (WhiteHeader) view.findViewById(R.id.white_header);
        if (Utils.isTabletDevice(getActivity()) || (UiUtils.isLandscape(getActivity()) && UiUtils.isSub320dpScreen(getActivity()))) {
            this.mWhiteHeader.setVisibility(8);
        } else {
            this.mWhiteHeader.showWeather(true);
            this.mWhiteHeader.showHolidays(true);
        }
    }

    private void initBirthdayHeader() {
        View viewInflate = ((LayoutInflater) getActivity().getSystemService("layout_inflater")).inflate(R.layout.birthday_header, (ViewGroup) null);
        this.birthdayHeader = viewInflate;
        viewInflate.setOnKeyListener(new View.OnKeyListener() { // from class: com.sonymobile.calendar.SplitScreenAgendaFragment.8
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i != 20 || keyEvent.getAction() != 0) {
                    return false;
                }
                SplitScreenAgendaFragment.this.mAgendaListView.setSelection(0);
                return false;
            }
        });
        this.birthdayHeader.findViewById(R.id.birthdayItemContainer).setVisibility(8);
        this.mAgendaListView.addHeaderView(this.birthdayHeader);
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Time time = this.lateExecute;
        if (time == null || !this.birthdaysEnabled) {
            return;
        }
        updateBirthdayHeader(time);
        this.lateExecute = null;
    }

    private boolean isBirthdaysEnabled() {
        return Boolean.valueOf(GeneralPreferences.getSharedPreferences(getActivity()).getBoolean(GeneralPreferences.KEY_BIRTHDAYS, BirthdayService.getDefaultEnabledStatus(getActivity()))).booleanValue() && Boolean.valueOf(GeneralPreferences.getSharedPreferences(getActivity()).getBoolean(BirthdayPreferences.KEY_BIRTHDAY_CONTACTS, true)).booleanValue();
    }

    private void setBirthdayVisibility(int i) {
        View view = this.birthdayHeader;
        if (view != null) {
            view.findViewById(R.id.birthdayItemContainer).setVisibility(i);
        }
    }

    private void updateBirthdayHeader(Time time) {
        if (!Utils.isReadContactsEnabled(getActivity())) {
            setBirthdayVisibility(8);
            return;
        }
        setBirthdayVisibility(8);
        if (this.birthdayHeader == null || getActivity() == null || !this.birthdaysEnabled) {
            this.lateExecute = time;
            return;
        }
        View viewFindViewById = this.birthdayHeader.findViewById(R.id.doublePhotoFrame);
        View viewFindViewById2 = this.birthdayHeader.findViewById(R.id.quadPhotoFrame);
        TextView textView = (TextView) this.birthdayHeader.findViewById(R.id.birthdayAge);
        viewFindViewById.setVisibility(4);
        viewFindViewById2.setVisibility(8);
        textView.setVisibility(8);
        BirthdayService.INSTANCE.requestBirthdays(getActivity(), new DaySpan(time, time), new TodayBirthdayServiceResultHandler(this.birthdayHeader, getActivity(), time));
    }

    public void onEventsDisplayed() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            initHintAndPromoView();
            if (!LinkedInUtils.isLinkedInEnabled(activity) || HintPromoteUtils.shouldShowHintAndPromoInListView(activity)) {
                return;
            }
            initConnectWithLinkedInView();
        }
    }

    private class TodayBirthdayServiceResultHandler implements IAsyncServiceResultHandler {
        private Activity activity;
        private Time selectedTime;
        private View view;

        public TodayBirthdayServiceResultHandler(View view, Activity activity, Time time) {
            this.view = view;
            this.activity = activity;
            this.selectedTime = time;
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            ArrayList<ContactBirthday> arrayList = (ArrayList) obj;
            if (arrayList.size() < 1) {
                SplitScreenAgendaFragment.this.birthdayHeader.setFocusable(false);
                SplitScreenAgendaFragment.this.birthdayHeader.findViewById(R.id.birthdayItemContainer).setVisibility(8);
                return;
            }
            TextView textView = (TextView) this.view.findViewById(R.id.birthdayName);
            TextView textView2 = (TextView) this.view.findViewById(R.id.birthdayAge);
            ArrayList arrayList2 = new ArrayList();
            int i = 0;
            for (ContactBirthday contactBirthday : arrayList) {
                if (contactBirthday.getAge(this.selectedTime) >= 0) {
                    SplitScreenAgendaFragment.this.birthdayHeader.findViewById(R.id.birthdayItemContainer).setVisibility(0);
                    arrayList2.add(contactBirthday);
                    i++;
                }
            }
            if (i == SplitScreenAgendaFragment.this.NO_BIRTHDAYS) {
                SplitScreenAgendaFragment.this.birthdayHeader.setFocusable(false);
                SplitScreenAgendaFragment.this.birthdayHeader.findViewById(R.id.birthdayItemContainer).setVisibility(8);
                return;
            }
            if (i == SplitScreenAgendaFragment.this.SINGLE_BIRTHDAY) {
                textView.setText(((ContactBirthday) arrayList2.get(0)).name);
                textView2.setText("(" + ((ContactBirthday) arrayList2.get(0)).getAge(this.selectedTime) + ")");
                textView2.setVisibility(0);
            } else {
                textView.setText(String.format(this.activity.getResources().getString(R.string.multiple_birthdays_label), Integer.valueOf(i)));
            }
            ContactPhotoService.getInstance().requestContactPhotos(this.activity, arrayList, new ContactPhotoResultHandler(arrayList2));
            SplitScreenAgendaFragment.this.birthdayHeader.findViewById(R.id.birthdayItemContainer).setVisibility(0);
            SplitScreenAgendaFragment.this.birthdayHeader.setFocusable(true);
            SplitScreenAgendaFragment.this.birthdayHeader.findViewById(R.id.bottom_divider).setAlpha(0.3f);
            SplitScreenAgendaFragment.this.mAgendaListView.scrollToBirthdayHeader();
            SplitScreenAgendaFragment.this.birthdayHeader.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.SplitScreenAgendaFragment.TodayBirthdayServiceResultHandler.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setClassName(TodayBirthdayServiceResultHandler.this.activity, BirthdayActivity.class.getName());
                    intent.setFlags(537001984);
                    intent.putExtra(BirthdayActivity.DISPLAYED_TIME, TodayBirthdayServiceResultHandler.this.selectedTime.toMillis(false));
                    SplitScreenAgendaFragment.this.startActivity(intent);
                }
            });
        }

        private class ContactPhotoResultHandler implements IAsyncServiceResultHandler {
            ArrayList<ContactBirthday> birthdays;

            public ContactPhotoResultHandler(ArrayList<ContactBirthday> arrayList) {
                this.birthdays = arrayList;
            }

            @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
            public void onResult(Object obj, Object obj2) {
                fetchPhotos();
            }

            private void fetchPhotos() {
                int size = this.birthdays.size();
                if (size < 3) {
                    setDoublePhotos(size);
                } else {
                    setQuadPhotos(size);
                }
            }

            private void setDoublePhotos(int i) {
                ImageView imageView = (ImageView) TodayBirthdayServiceResultHandler.this.view.findViewById(R.id.doublePhotoSlot1);
                ((View) imageView.getParent()).setVisibility(0);
                setPhoto(imageView, ContactPhotoService.getInstance().getPhoto(SplitScreenAgendaFragment.this.getActivity(), this.birthdays.get(0).contactId));
                if (i > 1) {
                    setPhoto((ImageView) TodayBirthdayServiceResultHandler.this.view.findViewById(R.id.doublePhotoSlot2), ContactPhotoService.getInstance().getPhoto(SplitScreenAgendaFragment.this.getActivity(), this.birthdays.get(1).contactId));
                }
            }

            private void setQuadPhotos(int i) {
                ImageView imageView = (ImageView) TodayBirthdayServiceResultHandler.this.view.findViewById(R.id.quadPhotoSlot1);
                ImageView imageView2 = (ImageView) TodayBirthdayServiceResultHandler.this.view.findViewById(R.id.quadPhotoSlot2);
                ImageView imageView3 = (ImageView) TodayBirthdayServiceResultHandler.this.view.findViewById(R.id.quadPhotoSlot3);
                ((View) imageView.getParent()).setVisibility(0);
                setPhoto(imageView, ContactPhotoService.getInstance().getPhoto(SplitScreenAgendaFragment.this.getActivity(), this.birthdays.get(0).contactId));
                setPhoto(imageView2, ContactPhotoService.getInstance().getPhoto(SplitScreenAgendaFragment.this.getActivity(), this.birthdays.get(1).contactId));
                setPhoto(imageView3, ContactPhotoService.getInstance().getPhoto(SplitScreenAgendaFragment.this.getActivity(), this.birthdays.get(2).contactId));
                if (i > 3) {
                    ImageView imageView4 = (ImageView) TodayBirthdayServiceResultHandler.this.view.findViewById(R.id.quadPhotoSlot4);
                    if (i == 4) {
                        setPhoto(imageView4, ContactPhotoService.getInstance().getPhoto(SplitScreenAgendaFragment.this.getActivity(), this.birthdays.get(3).contactId));
                    } else {
                        imageView4.setVisibility(0);
                        imageView4.setImageResource(R.drawable.ic_more_small);
                    }
                }
            }

            private void setPhoto(ImageView imageView, Bitmap bitmap) {
                imageView.setVisibility(0);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public static String getLunarDate(Date date) {
        StringBuilder sb = new StringBuilder(new SimpleDateFormat("d cccc", Locale.getDefault()).format((java.util.Date) date));
        LunarUtils.LunarDate lunarDateConvertSolarDateToLunarDate = LunarUtils.convertSolarDateToLunarDate(date);
        sb.append(" | ").append(LunarUtils.getLunarMonthString(lunarDateConvertSolarDateToLunarDate)).append(LunarUtils.getLunarDayString(lunarDateConvertSolarDateToLunarDate));
        return sb.toString();
    }

    public boolean isMonthGridMoving() {
        if (getParentFragment() != null) {
            return ((SplitScreenAgendaViewPager) getParentFragment()).isMonthGridMoving();
        }
        return false;
    }
}
