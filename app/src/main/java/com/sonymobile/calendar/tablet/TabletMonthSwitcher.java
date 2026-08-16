package com.sonymobile.calendar.tablet;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.format.Time;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import androidx.core.content.ContextCompat;
import com.sonymobile.calendar.CalendarApplication;
import com.sonymobile.calendar.MonthFragment;
import com.sonymobile.calendar.OnDateSwitcherClickedListener;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class TabletMonthSwitcher {
    private boolean isR2L;
    private Animation mInAnimationFuture;
    private Animation mInAnimationPast;
    private Animation mOutAnimationFuture;
    private Animation mOutAnimationPast;
    private ViewSwitcher navigationViewSwitcher;
    private TextView nextButton;
    private View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.sonymobile.calendar.tablet.TabletMonthSwitcher.2
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.next_year_button) {
                if (TabletMonthSwitcher.this.isR2L) {
                    TabletMonthSwitcher tabletMonthSwitcher = TabletMonthSwitcher.this;
                    tabletMonthSwitcher.animateNavigationViewTransition(tabletMonthSwitcher.mOutAnimationPast, TabletMonthSwitcher.this.mInAnimationPast);
                } else {
                    TabletMonthSwitcher tabletMonthSwitcher2 = TabletMonthSwitcher.this;
                    tabletMonthSwitcher2.animateNavigationViewTransition(tabletMonthSwitcher2.mOutAnimationFuture, TabletMonthSwitcher.this.mInAnimationFuture);
                }
                TabletMonthSwitcher.this.onDateSwitcherClickedListener.onNextButtonClicked();
                return;
            }
            if (id != R.id.previous_year_button) {
                return;
            }
            if (TabletMonthSwitcher.this.isR2L) {
                TabletMonthSwitcher tabletMonthSwitcher3 = TabletMonthSwitcher.this;
                tabletMonthSwitcher3.animateNavigationViewTransition(tabletMonthSwitcher3.mOutAnimationFuture, TabletMonthSwitcher.this.mInAnimationFuture);
            } else {
                TabletMonthSwitcher tabletMonthSwitcher4 = TabletMonthSwitcher.this;
                tabletMonthSwitcher4.animateNavigationViewTransition(tabletMonthSwitcher4.mOutAnimationPast, TabletMonthSwitcher.this.mInAnimationPast);
            }
            TabletMonthSwitcher.this.onDateSwitcherClickedListener.onPreviousButtonClicked();
        }
    };
    private OnDateSwitcherClickedListener onDateSwitcherClickedListener;
    private TextView prevButton;
    private int themeColor;

    public TabletMonthSwitcher(MonthFragment monthFragment, View view) {
        this.isR2L = CalendarApplication.isR2L(monthFragment.getActivity().getResources());
        this.themeColor = UiUtils.getPrimaryColor(monthFragment.getActivity());
        initMonthNavigationSwitcher(monthFragment, view);
        initButtons(view);
        initAnimations(monthFragment.getActivity());
    }

    public void updateMonthSwitcher(Time time) {
        ((MonthsNavigationView) this.navigationViewSwitcher.getCurrentView()).updateNavigationView(time);
        this.prevButton.setText(String.valueOf(time.year - 1));
        this.nextButton.setText(String.valueOf(time.year + 1));
    }

    public void setOnDateSwitcherClickedListener(OnDateSwitcherClickedListener onDateSwitcherClickedListener) {
        this.onDateSwitcherClickedListener = onDateSwitcherClickedListener;
    }

    private void initMonthNavigationSwitcher(final MonthFragment monthFragment, View view) {
        ViewSwitcher.ViewFactory viewFactory = new ViewSwitcher.ViewFactory() { // from class: com.sonymobile.calendar.tablet.TabletMonthSwitcher.1
            @Override // android.widget.ViewSwitcher.ViewFactory
            public View makeView() {
                MonthsNavigationView monthsNavigationView = new MonthsNavigationView(monthFragment);
                monthsNavigationView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
                return monthsNavigationView;
            }
        };
        ViewSwitcher viewSwitcher = (ViewSwitcher) view.findViewById(R.id.navigation_switcher);
        this.navigationViewSwitcher = viewSwitcher;
        viewSwitcher.setFactory(viewFactory);
    }

    private void initButtons(View view) {
        this.prevButton = (TextView) view.findViewById(R.id.previous_year_button);
        this.nextButton = (TextView) view.findViewById(R.id.next_year_button);
        this.prevButton.setOnClickListener(this.onClickListener);
        this.nextButton.setOnClickListener(this.onClickListener);
        this.prevButton.setBackground(getYearButtonBackgroundStateList(view.getContext()));
        this.prevButton.setTextColor(getYearTextColorStateList(view.getContext()));
        this.nextButton.setBackground(getYearButtonBackgroundStateList(view.getContext()));
        this.nextButton.setTextColor(getYearTextColorStateList(view.getContext()));
    }

    private void initAnimations(Context context) {
        this.mInAnimationPast = AnimationUtils.loadAnimation(context, R.anim.slide_right_in);
        this.mOutAnimationPast = AnimationUtils.loadAnimation(context, R.anim.slide_right_out);
        this.mInAnimationFuture = AnimationUtils.loadAnimation(context, R.anim.slide_left_in);
        this.mOutAnimationFuture = AnimationUtils.loadAnimation(context, R.anim.slide_left_out);
    }

    private ColorStateList getYearTextColorStateList(Context context) {
        TypedArray typedArrayObtainStyledAttributes = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary});
        int color = typedArrayObtainStyledAttributes.getColor(0, ContextCompat.getColor(context, R.color.calendar_foreground));
        typedArrayObtainStyledAttributes.recycle();
        return new ColorStateList(new int[][]{new int[]{android.R.attr.state_pressed}, new int[0]}, new int[]{ContextCompat.getColor(context, R.color.calendar_grid_area_background), color});
    }

    private StateListDrawable getYearButtonBackgroundStateList(Context context) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, new ColorDrawable(this.themeColor));
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(this.themeColor));
        return stateListDrawable;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateNavigationViewTransition(Animation animation, Animation animation2) {
        this.navigationViewSwitcher.setOutAnimation(animation);
        this.navigationViewSwitcher.setInAnimation(animation2);
        this.navigationViewSwitcher.showNext();
    }
}
