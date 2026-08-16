package com.sonymobile.calendar.agendapager;
import com.sonymobile.calendar.SafeTime;

import android.text.format.Time;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.sonymobile.calendar.CalendarApplication;
import com.sonymobile.calendar.SplitScreenAgendaFragment;

/* JADX INFO: loaded from: classes2.dex */
public class AgendaPagerAdapter extends FragmentStatePagerAdapter {
    private static final int MAX = 120;
    private static final int SCROLL_ZONE = 30;
    private ViewPager mPager;
    private Fragment mParent;
    private Fragment mSplitScreenAgendaFragment;
    private int middleDay;

    private int getFirst() {
        return 60;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getCount() {
        return 120;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getItemPosition(Object obj) {
        return -2;
    }

    public AgendaPagerAdapter(FragmentManager fragmentManager, ViewPager viewPager) {
        super(fragmentManager);
        this.mPager = viewPager;
        viewPager.setAdapter(this);
    }

    @Override // androidx.fragment.app.FragmentStatePagerAdapter
    public Fragment getItem(int i) {
        SplitScreenAgendaFragment splitScreenAgendaFragmentNewInstance = SplitScreenAgendaFragment.newInstance(positionToDay(i));
        this.mSplitScreenAgendaFragment = splitScreenAgendaFragmentNewInstance;
        splitScreenAgendaFragmentNewInstance.setParent(this.mParent);
        ((SplitScreenAgendaFragment) this.mSplitScreenAgendaFragment).setPagerAdapter(this);
        return this.mSplitScreenAgendaFragment;
    }

    public void hideAgendaList() {
        ((SplitScreenAgendaFragment) this.mSplitScreenAgendaFragment).hideAgendaList();
    }

    public SplitScreenAgendaFragment getFragment() {
        return (SplitScreenAgendaFragment) this.mSplitScreenAgendaFragment;
    }

    public void setParent(Fragment fragment) {
        this.mParent = fragment;
    }

    public void goTo(int i) {
        if (i == this.mPager.getCurrentItem()) {
            return;
        }
        if (Math.abs(i - this.middleDay) > 30) {
            this.middleDay = i;
            this.mPager.setCurrentItem(getFirst());
            notifyDataSetChanged();
            return;
        }
        this.mPager.setCurrentItem(dayToPosition(i));
    }

    public int getCurrentJulianDay() {
        return positionToDay(this.mPager.getCurrentItem());
    }

    public Time getCurrentTime() {
        Time time = new SafeTime();
        time.setJulianDay(getCurrentJulianDay());
        return time;
    }

    private int positionToDay(int i) {
        if (CalendarApplication.isR2L(this.mParent.getResources())) {
            return (this.middleDay - i) + getFirst();
        }
        return (this.middleDay + i) - getFirst();
    }

    public void moveToNext() {
        ViewPager viewPager = this.mPager;
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    public void moveToPrevious() {
        ViewPager viewPager = this.mPager;
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }

    private int dayToPosition(int i) {
        if (CalendarApplication.isR2L(this.mParent.getResources())) {
            return (getFirst() - i) + this.middleDay;
        }
        return (getFirst() + i) - this.middleDay;
    }
}
