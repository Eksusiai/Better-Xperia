package com.sonymobile.calendar.agendapager;

import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager.widget.ViewPager;
import com.sonymobile.calendar.AgendaPager;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.SplitScreenAgendaFragment;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.agendamonth.SplitScreenFragment;

/* JADX INFO: loaded from: classes2.dex */
public class SplitScreenAgendaViewPager extends Fragment {
    private AgendaSwipePageCallback mAgendaSwipePageCallback;
    private AgendaSelectedPageCallback mGoToTimeCallBack;
    private AgendaPager mPager;
    private AgendaPagerAdapter mPagerAdapter;
    private Fragment mParent;

    public interface AgendaSelectedPageCallback {
        void goToMonthDay(Time time);
    }

    public interface AgendaSwipePageCallback {
        void agendaListIsInSwipe(boolean z);
    }

    public static SplitScreenAgendaViewPager newInstance() {
        return new SplitScreenAgendaViewPager();
    }

    public boolean isVisibleListAtTop() {
        SplitScreenAgendaFragment splitScreenAgendaFragment = (SplitScreenAgendaFragment) this.mPagerAdapter.instantiateItem((ViewGroup) this.mPager, this.mPager.getCurrentItem());
        if (splitScreenAgendaFragment != null) {
            return splitScreenAgendaFragment.isListAtTop();
        }
        return false;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.view_pager, (ViewGroup) null);
        this.mPager = (AgendaPager) viewInflate.findViewById(R.id.agenda_pager);
        int julianDay = Time.getJulianDay(System.currentTimeMillis(), 0L);
        AgendaPagerAdapter agendaPagerAdapter = new AgendaPagerAdapter(getChildFragmentManager(), this.mPager);
        this.mPagerAdapter = agendaPagerAdapter;
        agendaPagerAdapter.setParent(this.mParent);
        this.mPager.setAdapter(this.mPagerAdapter);
        this.mPager.setOnPageChangeListener(new AgendaOnPageChangeListener());
        this.mPager.setPageTransformer(false, new FadePageTransformer());
        this.mPagerAdapter.goTo(julianDay);
        LifecycleOwner lifecycleOwner = this.mParent;
        this.mGoToTimeCallBack = (AgendaSelectedPageCallback) lifecycleOwner;
        this.mAgendaSwipePageCallback = (AgendaSwipePageCallback) lifecycleOwner;
        this.mAgendaSwipePageCallback = (AgendaSwipePageCallback) lifecycleOwner;
        return viewInflate;
    }

    private static class FadePageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        private FadePageTransformer() {
        }

        @Override // androidx.viewpager.widget.ViewPager.PageTransformer
        public void transformPage(View view, float f) {
            if (f < -1.0f) {
                view.setAlpha(0.0f);
                return;
            }
            if (f <= 0.0f) {
                view.setAlpha(1.0f);
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
            } else {
                if (f <= 1.0f) {
                    view.setAlpha(1.0f - f);
                    float fAbs = ((1.0f - Math.abs(f)) * 0.25f) + MIN_SCALE;
                    view.setScaleX(fAbs);
                    view.setScaleY(fAbs);
                    return;
                }
                view.setAlpha(0.0f);
            }
        }
    }

    public void updatePager() {
        this.mPagerAdapter.notifyDataSetChanged();
    }

    public void setSwipeEnabled(boolean z) {
        this.mPager.setSwipeEnabled(z);
    }

    private class AgendaOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private Time mCurrentTime;

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
        }

        private AgendaOnPageChangeListener() {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
            if (i == 0) {
                if (SplitScreenAgendaViewPager.this.mGoToTimeCallBack != null) {
                    SplitScreenAgendaViewPager.this.mAgendaSwipePageCallback.agendaListIsInSwipe(false);
                    return;
                }
                return;
            }
            SplitScreenAgendaViewPager.this.mAgendaSwipePageCallback.agendaListIsInSwipe(true);
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
            if (f < 1.0f) {
                Time time = this.mCurrentTime;
                if ((time == null || !Utils.areDatesEqual(time, SplitScreenAgendaViewPager.this.mPagerAdapter.getCurrentTime())) && SplitScreenAgendaViewPager.this.mGoToTimeCallBack != null) {
                    this.mCurrentTime = SplitScreenAgendaViewPager.this.mPagerAdapter.getCurrentTime();
                    SplitScreenAgendaViewPager.this.mGoToTimeCallBack.goToMonthDay(this.mCurrentTime);
                }
            }
        }
    }

    public void hideAgendaList() {
        this.mPagerAdapter.hideAgendaList();
    }

    public SplitScreenAgendaFragment getFragment() {
        return this.mPagerAdapter.getFragment();
    }

    public void setParent(Fragment fragment) {
        this.mParent = fragment;
    }

    public void goTo(Time time) {
        FragmentActivity activity = getActivity();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        this.mPagerAdapter.goTo(Time.getJulianDay(time.toMillis(true), time.gmtoff));
    }

    public boolean isMonthGridMoving() {
        Fragment fragment = this.mParent;
        if (fragment == null || !(fragment instanceof SplitScreenFragment)) {
            return false;
        }
        return ((SplitScreenFragment) fragment).isMonthGridMoving();
    }
}
