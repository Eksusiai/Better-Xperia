package com.sonymobile.calendar.transitions;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import com.sonymobile.calendar.R;

/* JADX INFO: loaded from: classes2.dex */
public class EditCircularRevealTransition extends Visibility {
    private static final int REVEAL_ANIMATION_DURATION = 400;
    private Context mContext;
    private View mFabView;
    private View mRootView;
    private TransitionListener mTransitionListener;

    public interface TransitionListener {
        void onTransitionEnd();

        void onTransitionStart();
    }

    public EditCircularRevealTransition(TransitionListener transitionListener, View view, Context context) {
        this.mTransitionListener = transitionListener;
        this.mRootView = view;
        this.mFabView = view.findViewById(R.id.addEventButton);
        this.mContext = context;
    }

    @Override // android.transition.Visibility
    public Animator onAppear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
        final View viewFindViewById = ((Activity) this.mContext).findViewById(R.id.edit_event_toolbar);
        viewFindViewById.setAlpha(0.0f);
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(viewFindViewById, "alpha", 0.0f, 1.0f);
        objectAnimatorOfFloat.setDuration(400L);
        Animator animatorCreateCircularReveal = ViewAnimationUtils.createCircularReveal(this.mRootView, (int) (this.mRootView.getX() + this.mRootView.getWidth()), (int) (this.mRootView.getY() + this.mRootView.getHeight()), 0.0f, (float) Math.sqrt(Math.pow(this.mRootView.getWidth(), 2.0d) + Math.pow(this.mRootView.getHeight(), 2.0d)));
        animatorCreateCircularReveal.setInterpolator(new AccelerateInterpolator());
        animatorCreateCircularReveal.setDuration(400L);
        ObjectAnimator objectAnimatorOfFloat2 = ObjectAnimator.ofFloat(this.mFabView, "scaleX", 1.0f, 0.0f);
        ObjectAnimator objectAnimatorOfFloat3 = ObjectAnimator.ofFloat(this.mFabView, "scaleY", 1.0f, 0.0f);
        objectAnimatorOfFloat2.setDuration(400L);
        objectAnimatorOfFloat3.setDuration(400L);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.playTogether(animatorCreateCircularReveal, objectAnimatorOfFloat2, objectAnimatorOfFloat3, objectAnimatorOfFloat);
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.sonymobile.calendar.transitions.EditCircularRevealTransition.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                EditCircularRevealTransition.this.mFabView.setVisibility(0);
                if (EditCircularRevealTransition.this.mTransitionListener != null) {
                    EditCircularRevealTransition.this.mTransitionListener.onTransitionStart();
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                EditCircularRevealTransition.this.mFabView.setVisibility(4);
                viewFindViewById.setAlpha(1.0f);
                if (EditCircularRevealTransition.this.mTransitionListener != null) {
                    EditCircularRevealTransition.this.mTransitionListener.onTransitionEnd();
                }
            }
        });
        return animatorSet;
    }
}
