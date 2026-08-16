package com.sonymobile.calendar.design;

import android.R;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/* JADX INFO: loaded from: classes2.dex */
public final class SnackBar {
    private ViewGroup mRoot;
    private final SnackFrame mSnackFrame;

    public interface ISnackBarCallback {

        public enum CallbackType {
            ACTION_CALLBACK,
            ON_HIDE_CALLBACK
        }

        void actionCallBack();

        void onHideCallback();
    }

    private SnackBar(Activity activity) {
        this.mRoot = (ViewGroup) activity.findViewById(R.id.content);
        this.mSnackFrame = new SnackFrame(activity);
    }

    public static SnackBar make(Activity activity) {
        return new SnackBar(activity);
    }

    public SnackBar message(int i) {
        this.mSnackFrame.setMessage(i);
        return this;
    }

    public SnackBar action(int i, ISnackBarCallback iSnackBarCallback) {
        this.mSnackFrame.setAction(i, iSnackBarCallback);
        return this;
    }

    public void show() {
        this.mSnackFrame.showInternal(this.mRoot);
    }

    private static class SnackFrame extends FrameLayout {
        private static final int ANIMATION_DURATION = 300;
        private static final int SNACK_BAR_MESSAGE_DURATION = 3000;
        private boolean mActionClicked;
        private final Runnable mHideRunnable;
        private Animator mInAnimation;
        private Animator mOutAnimation;
        private TextView mSnackBarAction;
        private TextView mSnackBarMessage;
        private ISnackBarCallback mSnackCallback;

        public SnackFrame(Context context) {
            super(context);
            this.mActionClicked = false;
            this.mHideRunnable = new Runnable() { // from class: com.sonymobile.calendar.design.SnackBar.SnackFrame.1
                @Override // java.lang.Runnable
                public void run() {
                    SnackFrame.this.mOutAnimation.start();
                }
            };
            init(context);
        }

        public SnackFrame(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.mActionClicked = false;
            this.mHideRunnable = new Runnable() { // from class: com.sonymobile.calendar.design.SnackBar.SnackFrame.1
                @Override // java.lang.Runnable
                public void run() {
                    SnackFrame.this.mOutAnimation.start();
                }
            };
            init(context);
        }

        public SnackFrame(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
            this.mActionClicked = false;
            this.mHideRunnable = new Runnable() { // from class: com.sonymobile.calendar.design.SnackBar.SnackFrame.1
                @Override // java.lang.Runnable
                public void run() {
                    SnackFrame.this.mOutAnimation.start();
                }
            };
            init(context);
        }

        public SnackFrame(Context context, AttributeSet attributeSet, int i, int i2) {
            super(context, attributeSet, i, i2);
            this.mActionClicked = false;
            this.mHideRunnable = new Runnable() { // from class: com.sonymobile.calendar.design.SnackBar.SnackFrame.1
                @Override // java.lang.Runnable
                public void run() {
                    SnackFrame.this.mOutAnimation.start();
                }
            };
            init(context);
        }

        public void setMessage(int i) {
            this.mSnackBarMessage.setText(getResources().getString(i));
        }

        public void setAction(int i, ISnackBarCallback iSnackBarCallback) {
            this.mSnackBarAction.setText(getResources().getString(i));
            this.mSnackCallback = iSnackBarCallback;
        }

        private void init(Context context) {
            ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(com.sonymobile.calendar.R.layout.snackbar_frame, (ViewGroup) this, true);
            this.mSnackBarMessage = (TextView) findViewById(com.sonymobile.calendar.R.id.snackbar_text);
            TextView textView = (TextView) findViewById(com.sonymobile.calendar.R.id.snackbar_action);
            this.mSnackBarAction = textView;
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.design.SnackBar.SnackFrame.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (SnackFrame.this.mOutAnimation.isStarted()) {
                        return;
                    }
                    SnackFrame.this.mActionClicked = true;
                    SnackFrame.this.runCallBack(ISnackBarCallback.CallbackType.ACTION_CALLBACK);
                    SnackFrame snackFrame = SnackFrame.this;
                    snackFrame.removeCallbacks(snackFrame.mHideRunnable);
                    SnackFrame.this.mOutAnimation.start();
                }
            });
            Animator animatorLoadAnimator = AnimatorInflater.loadAnimator(context, com.sonymobile.calendar.R.animator.snackbar_slide_in);
            this.mInAnimation = animatorLoadAnimator;
            animatorLoadAnimator.setDuration(300L);
            this.mInAnimation.setTarget(this);
            Animator animatorLoadAnimator2 = AnimatorInflater.loadAnimator(context, com.sonymobile.calendar.R.animator.snackbar_slide_out);
            this.mOutAnimation = animatorLoadAnimator2;
            animatorLoadAnimator2.setDuration(300L);
            this.mOutAnimation.setTarget(this);
            this.mOutAnimation.addListener(new Animator.AnimatorListener() { // from class: com.sonymobile.calendar.design.SnackBar.SnackFrame.3
                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationRepeat(Animator animator) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    SnackFrame.this.mInAnimation.cancel();
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    SnackFrame.this.setVisibility(8);
                    if (!SnackFrame.this.mActionClicked) {
                        SnackFrame.this.runCallBack(ISnackBarCallback.CallbackType.ON_HIDE_CALLBACK);
                    }
                    SnackFrame.this.mActionClicked = false;
                }
            });
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            removeCallbacks(this.mHideRunnable);
        }

        public void showInternal(ViewGroup viewGroup) {
            this.mActionClicked = false;
            if (getParent() == null) {
                viewGroup.addView(this);
            }
            setVisibility(0);
            this.mInAnimation.start();
            postDelayed(this.mHideRunnable, 3000L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void runCallBack(ISnackBarCallback.CallbackType callbackType) {
            if (this.mSnackCallback != null) {
                if (ISnackBarCallback.CallbackType.ACTION_CALLBACK == callbackType) {
                    this.mSnackCallback.actionCallBack();
                } else if (ISnackBarCallback.CallbackType.ON_HIDE_CALLBACK == callbackType) {
                    this.mSnackCallback.onHideCallback();
                }
            }
        }
    }
}
