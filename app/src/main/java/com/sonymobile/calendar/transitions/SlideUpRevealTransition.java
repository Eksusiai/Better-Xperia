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
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import com.sonymobile.calendar.R;

/* JADX INFO: loaded from: classes2.dex */
public class SlideUpRevealTransition extends Visibility {
    private static final int FADE_ANIMATION_DURATION = 400;
    private static final float INTERPOLATOR_FACTOR = 1.0f;
    private static final int SLIDE_ANIMATION_DURATION = 350;
    private static final float TRANSLATION_FACTOR = 0.2f;
    private Context mContext;

    public SlideUpRevealTransition(Context context) {
        this.mContext = context;
    }

    @Override // android.transition.Visibility
    public Animator onAppear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
        return createSlideUpAnimation(viewGroup, ((Activity) this.mContext).findViewById(R.id.event_info_activity_container));
    }

    private Animator createSlideUpAnimation(ViewGroup viewGroup, final View view) {
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(view, "translationY", view.getHeight() * TRANSLATION_FACTOR, 0.0f);
        ObjectAnimator objectAnimatorOfFloat2 = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f);
        objectAnimatorOfFloat2.setDuration(400L);
        objectAnimatorOfFloat.setDuration(350L);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new DecelerateInterpolator(1.0f));
        animatorSet.playTogether(objectAnimatorOfFloat2, objectAnimatorOfFloat);
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.sonymobile.calendar.transitions.SlideUpRevealTransition.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                view.setVisibility(0);
            }
        });
        return animatorSet;
    }
}
