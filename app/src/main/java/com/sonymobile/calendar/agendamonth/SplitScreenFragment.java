package com.sonymobile.calendar.agendamonth;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.sonymobile.calendar.IMonthFragmentParent;
import com.sonymobile.calendar.LaunchActivity;
import com.sonymobile.calendar.MonthFragment;
import com.sonymobile.calendar.Navigator;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.agendapager.SplitScreenAgendaViewPager;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class SplitScreenFragment extends Fragment implements Navigator, SplitScreenLayout.Callback, IMonthFragmentParent, SplitScreenAgendaViewPager.AgendaSelectedPageCallback, SplitScreenAgendaViewPager.AgendaSwipePageCallback, View.OnTouchListener {
    private static final float MIN_SCROLL = 150.0f;
    private static final int TRANSITION_DURATION = 500;
    private float mAgendaModeSplitPosition;
    private GestureDetector mDetector;
    private MonthFragment mMonthFragment;
    private SplitScreenAgendaViewPager mSplitScreenAgendaViewPager;
    private SplitScreenLayout mSplitter;
    private boolean mIsAnimationEnabled = true;
    private boolean mIsMonthGridMoving = false;
    private boolean mIsTouchStartedFromMonthGrid = false;
    private float mScrollLength = 0.0f;
    private float mLengthWithoutScrolling = 0.0f;

    @Override // com.sonymobile.calendar.Navigator
    public CharSequence getDateString() {
        return "";
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToNext(float f) {
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToPrevious(float f) {
    }

    @Override // com.sonymobile.calendar.agendamonth.SplitScreenLayout.Callback
    public void onSplitStateChanged(int i) {
    }

    @Override // com.sonymobile.calendar.Navigator
    public void updateActionBar(Time time) {
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        MonthFragment monthFragment = (MonthFragment) Fragment.instantiate(getActivity(), MonthFragment.class.getName());
        this.mMonthFragment = monthFragment;
        monthFragment.setParent(this);
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.dimen.agenda_mode_split_position, typedValue, true);
        this.mAgendaModeSplitPosition = typedValue.getFloat();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        double d = displayMetrics.heightPixels / displayMetrics.widthPixels;
        if (d > 1.8d && d <= 2.0d) {
            this.mAgendaModeSplitPosition = 0.88f;
        } else if (d > 2.0d) {
            this.mAgendaModeSplitPosition = 0.89f;
        }
    }

    @Override // com.sonymobile.calendar.agendapager.SplitScreenAgendaViewPager.AgendaSwipePageCallback
    public void agendaListIsInSwipe(boolean z) {
        this.mIsAnimationEnabled = !z;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.splitscreen, viewGroup, false);
        SplitScreenLayout splitScreenLayout = (SplitScreenLayout) viewInflate.findViewById(R.id.splitter);
        this.mSplitter = splitScreenLayout;
        splitScreenLayout.setCallback(this);
        if (UiUtils.isSub320dpScreen(getContext())) {
            this.mMonthFragment.startInAgendaMode(true);
            this.mSplitter.reLayoutSplit(this.mAgendaModeSplitPosition);
        } else {
            this.mDetector = new GestureDetector(getActivity(), new MyGestureListener());
        }
        this.mDetector = new GestureDetector(getActivity(), new MyGestureListener());
        return viewInflate;
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!UiUtils.isSub320dpScreen(getContext())) {
            this.mDetector.onTouchEvent(motionEvent);
            if (motionEvent.getAction() == 1) {
                if (UiUtils.isPortrait(getActivity())) {
                    float f = this.mScrollLength;
                    if (f >= MIN_SCROLL) {
                        if (isGoingFromWeek()) {
                            animateToMonth();
                        } else {
                            completeTransitionAnimation();
                        }
                    } else if (f < 0.0f || f >= MIN_SCROLL) {
                        if (f > 0.0f || f <= -150.0f) {
                            if (f <= -150.0f) {
                                animateToAgenda();
                            }
                        } else if (!isGoingFromWeek()) {
                            animateToMonth();
                        } else {
                            animateToAgenda();
                        }
                    } else if (isGoingFromWeek()) {
                        animateToAgenda();
                    } else {
                        completeTransitionAnimation();
                    }
                }
                this.mIsTouchStartedFromMonthGrid = false;
                this.mIsMonthGridMoving = false;
                return true;
            }
        }
        return false;
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean mListStartsAtTop = false;

        MyGestureListener() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent motionEvent) {
            SplitScreenFragment.this.mScrollLength = 0.0f;
            SplitScreenFragment.this.mLengthWithoutScrolling = 0.0f;
            this.mListStartsAtTop = UiUtils.isPhonePortrait(SplitScreenFragment.this.getActivity()) ? SplitScreenFragment.this.mSplitScreenAgendaViewPager.isVisibleListAtTop() : false;
            if (SplitScreenFragment.this.mMonthFragment.getCalendarBottom() >= motionEvent.getY()) {
                SplitScreenFragment.this.mIsTouchStartedFromMonthGrid = true;
            }
            return true;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (!SplitScreenFragment.this.mIsAnimationEnabled || UiUtils.isLandscape(SplitScreenFragment.this.getActivity().getApplicationContext()) || motionEvent == null || motionEvent2 == null || SplitScreenFragment.this.mMonthFragment == null || SplitScreenFragment.this.mSplitScreenAgendaViewPager.getFragment() == null) {
                return false;
            }
            float x = motionEvent2.getX() - motionEvent.getX();
            float y = motionEvent2.getY() - motionEvent.getY();
            if (Math.abs(y) > Math.abs(x)) {
                boolean z = y > 0.0f;
                if (!z || (!this.mListStartsAtTop && !SplitScreenFragment.this.mIsTouchStartedFromMonthGrid)) {
                    if (SplitScreenFragment.this.mMonthFragment.isInAgendaView() || z) {
                        if (!z) {
                            this.mListStartsAtTop = false;
                        }
                        SplitScreenFragment.this.mScrollLength = 0.0f;
                        SplitScreenFragment.this.mLengthWithoutScrolling = y;
                    } else {
                        SplitScreenFragment.this.mIsMonthGridMoving = true;
                        SplitScreenFragment splitScreenFragment = SplitScreenFragment.this;
                        splitScreenFragment.mScrollLength = y - splitScreenFragment.mLengthWithoutScrolling;
                        SplitScreenFragment splitScreenFragment2 = SplitScreenFragment.this;
                        splitScreenFragment2.reLayoutSplit(splitScreenFragment2.mScrollLength);
                    }
                } else {
                    SplitScreenFragment.this.mIsMonthGridMoving = true;
                    SplitScreenFragment splitScreenFragment3 = SplitScreenFragment.this;
                    splitScreenFragment3.mScrollLength = y - splitScreenFragment3.mLengthWithoutScrolling;
                    SplitScreenFragment splitScreenFragment4 = SplitScreenFragment.this;
                    splitScreenFragment4.reLayoutSplit(splitScreenFragment4.mScrollLength);
                }
            }
            return true;
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        FragmentTransaction fragmentTransactionBeginTransaction = getChildFragmentManager().beginTransaction();
        SplitScreenAgendaViewPager splitScreenAgendaViewPagerNewInstance = SplitScreenAgendaViewPager.newInstance();
        this.mSplitScreenAgendaViewPager = splitScreenAgendaViewPagerNewInstance;
        splitScreenAgendaViewPagerNewInstance.setParent(this);
        fragmentTransactionBeginTransaction.replace(R.id.leftPane, this.mSplitScreenAgendaViewPager);
        fragmentTransactionBeginTransaction.commit();
        FragmentTransaction fragmentTransactionBeginTransaction2 = getChildFragmentManager().beginTransaction();
        fragmentTransactionBeginTransaction2.replace(R.id.rightPane, this.mMonthFragment);
        fragmentTransactionBeginTransaction2.commit();
        ((LaunchActivity) getActivity()).getActionBarController().onFragmentAttached(getClass().getName());
    }

    public void updateAgendaPager() {
        this.mSplitScreenAgendaViewPager.updatePager();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        getLoaderManager();
        super.onResume();
    }

    @Override // com.sonymobile.calendar.agendapager.SplitScreenAgendaViewPager.AgendaSelectedPageCallback
    public void goToMonthDay(Time time) {
        this.mMonthFragment.onCalendarClicked(time, true);
    }

    @Override // com.sonymobile.calendar.IMonthFragmentParent
    public void updateAgendaList(Time time) {
        SplitScreenAgendaViewPager splitScreenAgendaViewPager;
        if (!isAdded() || (splitScreenAgendaViewPager = this.mSplitScreenAgendaViewPager) == null) {
            return;
        }
        splitScreenAgendaViewPager.goTo(time);
    }

    @Override // com.sonymobile.calendar.IMonthFragmentParent
    public void hideAgendaList() {
        this.mSplitScreenAgendaViewPager.hideAgendaList();
    }

    public void reLayoutSplit() {
        this.mSplitter.reLayoutSplit();
    }

    public boolean isGoingFromWeek() {
        return this.mMonthFragment.isGoingFromWeek();
    }

    @Override // androidx.fragment.app.Fragment
    public void onMultiWindowModeChanged(boolean z) {
        SplitScreenLayout splitScreenLayout;
        super.onMultiWindowModeChanged(z);
        if (z || (splitScreenLayout = this.mSplitter) == null || !splitScreenLayout.isHorizontal()) {
            return;
        }
        this.mMonthFragment.showMonth();
    }

    public void reLayoutSplit(float f) {
        float defaultSplitPosition;
        int height;
        float defaultSplitPosition2;
        this.mSplitScreenAgendaViewPager.setSwipeEnabled(false);
        if (this.mMonthFragment.isInAgendaView()) {
            this.mMonthFragment.showMonth();
            defaultSplitPosition2 = this.mAgendaModeSplitPosition;
        } else {
            if (this.mMonthFragment.isGoingFromWeek()) {
                defaultSplitPosition = this.mAgendaModeSplitPosition;
                height = this.mSplitter.getHeight();
            } else {
                defaultSplitPosition = this.mSplitter.getDefaultSplitPosition();
                height = this.mSplitter.getHeight();
            }
            defaultSplitPosition2 = defaultSplitPosition - (f / height);
        }
        if (!this.mMonthFragment.isTransitionAnimationOngoing() && !this.mSplitScreenAgendaViewPager.getFragment().isTransitionAnimationOngoing()) {
            this.mMonthFragment.onTransitionAnimationStart((int) (this.mSplitter.getHeight() * (1.0f - this.mSplitter.getDefaultSplitPosition())), (int) (this.mSplitter.getHeight() * (1.0f - this.mAgendaModeSplitPosition)));
            this.mSplitScreenAgendaViewPager.getFragment().onTransitionAnimationStart((int) (this.mSplitter.getHeight() * this.mAgendaModeSplitPosition));
        }
        float f2 = this.mAgendaModeSplitPosition;
        if (defaultSplitPosition2 > f2) {
            defaultSplitPosition2 = f2;
        } else if (defaultSplitPosition2 < this.mSplitter.getDefaultSplitPosition()) {
            defaultSplitPosition2 = this.mSplitter.getDefaultSplitPosition();
        }
        this.mSplitter.reLayoutSplit(defaultSplitPosition2);
    }

    @Override // androidx.fragment.app.Fragment, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mSplitter.doConfigurationChanged(configuration);
        reLayoutSplit();
    }

    @Override // com.sonymobile.calendar.Navigator
    public long getSelectedTimeInMillis() {
        MonthFragment monthFragment = this.mMonthFragment;
        if (monthFragment != null) {
            return monthFragment.getSelectedTimeInMillis();
        }
        return 0L;
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goTo(Time time, boolean z) {
        MonthFragment monthFragment = this.mMonthFragment;
        if (monthFragment != null) {
            monthFragment.goTo(time, z);
        }
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToToday() {
        MonthFragment monthFragment = this.mMonthFragment;
        if (monthFragment != null) {
            monthFragment.goToToday();
        }
    }

    public void animateToAgenda() {
        this.mMonthFragment.onTransitionAnimationStart((int) (this.mSplitter.getHeight() * (1.0f - this.mSplitter.getDefaultSplitPosition())), (int) (this.mSplitter.getHeight() * (1.0f - this.mAgendaModeSplitPosition)));
        this.mSplitScreenAgendaViewPager.setSwipeEnabled(false);
        this.mSplitScreenAgendaViewPager.getFragment().onTransitionAnimationStart((int) (this.mSplitter.getHeight() * this.mAgendaModeSplitPosition));
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.mSplitter.getSplitPosition(), this.mAgendaModeSplitPosition);
        valueAnimatorOfFloat.setDuration(500L);
        valueAnimatorOfFloat.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.sonymobile.calendar.agendamonth.SplitScreenFragment.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                SplitScreenFragment.this.mSplitter.reLayoutSplit(((Float) valueAnimator.getAnimatedValue()).floatValue());
            }
        });
        valueAnimatorOfFloat.addListener(new Animator.AnimatorListener() { // from class: com.sonymobile.calendar.agendamonth.SplitScreenFragment.2
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (SplitScreenFragment.this.isActivityFinished()) {
                    return;
                }
                SplitScreenFragment.this.mMonthFragment.showWeek();
                SplitScreenFragment.this.mMonthFragment.onTransitionAnimationCompleted();
                SplitScreenFragment.this.mSplitScreenAgendaViewPager.getFragment().onTransitionAnimationCompleted();
                SplitScreenFragment.this.mSplitter.reLayoutSplit(SplitScreenFragment.this.mAgendaModeSplitPosition);
                SplitScreenFragment.this.mSplitScreenAgendaViewPager.setSwipeEnabled(true);
            }
        });
        valueAnimatorOfFloat.start();
    }

    public void animateToMonth() {
        this.mMonthFragment.onTransitionAnimationStart((int) (this.mSplitter.getHeight() * (1.0f - this.mSplitter.getDefaultSplitPosition())), (int) (this.mSplitter.getHeight() * (1.0f - this.mAgendaModeSplitPosition)));
        this.mSplitScreenAgendaViewPager.setSwipeEnabled(false);
        this.mSplitScreenAgendaViewPager.getFragment().onTransitionAnimationStart((int) (this.mSplitter.getHeight() * this.mAgendaModeSplitPosition));
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.mSplitter.getSplitPosition(), this.mSplitter.getDefaultSplitPosition());
        valueAnimatorOfFloat.setDuration(500L);
        valueAnimatorOfFloat.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.sonymobile.calendar.agendamonth.SplitScreenFragment.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                SplitScreenFragment.this.mSplitter.reLayoutSplit(((Float) valueAnimator.getAnimatedValue()).floatValue());
            }
        });
        valueAnimatorOfFloat.addListener(new Animator.AnimatorListener() { // from class: com.sonymobile.calendar.agendamonth.SplitScreenFragment.4
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (SplitScreenFragment.this.isActivityFinished()) {
                    return;
                }
                SplitScreenFragment.this.mMonthFragment.showMonth();
                SplitScreenFragment.this.mMonthFragment.onTransitionAnimationCompleted();
                SplitScreenFragment.this.mSplitScreenAgendaViewPager.getFragment().onTransitionAnimationCompleted();
                SplitScreenFragment.this.mSplitter.reLayoutSplit();
                SplitScreenFragment.this.mSplitScreenAgendaViewPager.setSwipeEnabled(true);
            }
        });
        valueAnimatorOfFloat.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isActivityFinished() {
        FragmentActivity activity = getActivity();
        return activity == null || activity.isFinishing();
    }

    public void completeTransitionAnimation() {
        MonthFragment monthFragment = this.mMonthFragment;
        if (monthFragment != null) {
            monthFragment.onTransitionAnimationCompleted();
        }
        if (this.mSplitScreenAgendaViewPager.getFragment() != null) {
            this.mSplitScreenAgendaViewPager.getFragment().onTransitionAnimationCompleted();
        }
        this.mSplitScreenAgendaViewPager.setSwipeEnabled(true);
    }

    public boolean isMonthGridMoving() {
        return this.mIsMonthGridMoving;
    }
}
