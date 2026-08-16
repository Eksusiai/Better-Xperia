package com.sonymobile.calendar.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.legacy.widget.Space;
import com.sonymobile.calendar.R;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes2.dex */
public class TextInputLayout extends LinearLayout {
    private static final int ANIMATION_DURATION = 200;
    private static final int INVALID_MAX_LENGTH = -1;
    private static Method sSetConstantStateMethod;
    private static boolean sSetConstantStateMethodFetched;
    private ValueAnimator mAnimator;
    private final CollapsingTextHelper mCollapsingTextHelper;
    private boolean mCounterEnabled;
    private int mCounterMaxLength;
    private int mCounterOverflowTextAppearance;
    private boolean mCounterOverflowed;
    private int mCounterTextAppearance;
    private TextView mCounterView;
    private ColorStateList mDefaultTextColor;
    private EditText mEditText;
    private boolean mErrorEnabled;
    private boolean mErrorShown;
    private int mErrorTextAppearance;
    private TextView mErrorView;
    private ColorStateList mFocusedTextColor;
    private boolean mHasReconstructedEditTextBackground;
    private CharSequence mHint;
    private boolean mHintAnimationEnabled;
    private boolean mHintEnabled;
    private LinearLayout mIndicatorArea;
    private int mIndicatorsAdded;
    private Paint mTmpPaint;

    public TextInputLayout(Context context) {
        this(context, null);
    }

    public TextInputLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TextInputLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet);
        CollapsingTextHelper collapsingTextHelper = new CollapsingTextHelper(this);
        this.mCollapsingTextHelper = collapsingTextHelper;
        setOrientation(1);
        setWillNotDraw(false);
        setAddStatesFromChildren(true);
        collapsingTextHelper.setTextSizeInterpolator(new FastOutSlowInInterpolator());
        collapsingTextHelper.setPositionInterpolator(new AccelerateInterpolator());
        collapsingTextHelper.setCollapsedTextGravity(8388659);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.TextInputLayout, i, R.style.Widget_Design_TextInputLayout);
        this.mHintEnabled = typedArrayObtainStyledAttributes.getBoolean(21, true);
        setHint(typedArrayObtainStyledAttributes.getText(1));
        this.mHintAnimationEnabled = typedArrayObtainStyledAttributes.getBoolean(20, true);
        if (typedArrayObtainStyledAttributes.hasValue(0)) {
            ColorStateList colorStateList = typedArrayObtainStyledAttributes.getColorStateList(0);
            this.mFocusedTextColor = colorStateList;
            this.mDefaultTextColor = colorStateList;
        }
        if (typedArrayObtainStyledAttributes.getResourceId(22, -1) != -1) {
            setHintTextAppearance(typedArrayObtainStyledAttributes.getResourceId(22, 0));
        }
        this.mErrorTextAppearance = typedArrayObtainStyledAttributes.getResourceId(16, 0);
        boolean z = typedArrayObtainStyledAttributes.getBoolean(15, false);
        boolean z2 = typedArrayObtainStyledAttributes.getBoolean(11, false);
        setCounterMaxLength(typedArrayObtainStyledAttributes.getInt(12, -1));
        this.mCounterTextAppearance = typedArrayObtainStyledAttributes.getResourceId(14, 0);
        this.mCounterOverflowTextAppearance = typedArrayObtainStyledAttributes.getResourceId(13, 0);
        typedArrayObtainStyledAttributes.recycle();
        setErrorEnabled(z);
        setCounterEnabled(z2);
        if (ViewCompat.getImportantForAccessibility(this) == 0) {
            ViewCompat.setImportantForAccessibility(this, 1);
        }
        ViewCompat.setAccessibilityDelegate(this, new TextInputAccessibilityDelegate());
    }

    @Override // android.view.ViewGroup
    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        if (view instanceof EditText) {
            setEditText((EditText) view);
            super.addView(view, 0, updateEditTextMargin(layoutParams));
        } else {
            super.addView(view, i, layoutParams);
        }
    }

    public void setTypeface(Typeface typeface) {
        this.mCollapsingTextHelper.setTypefaces(typeface);
    }

    public Typeface getTypeface() {
        return this.mCollapsingTextHelper.getCollapsedTypeface();
    }

    private void setEditText(EditText editText) {
        if (this.mEditText != null) {
            throw new IllegalArgumentException("We already have an EditText, can only have one");
        }
        this.mEditText = editText;
        this.mCollapsingTextHelper.setTypefaces(editText.getTypeface());
        this.mCollapsingTextHelper.setExpandedTextSize(this.mEditText.getTextSize());
        this.mCollapsingTextHelper.setExpandedTextGravity(this.mEditText.getGravity());
        this.mEditText.addTextChangedListener(new TextWatcher() { // from class: com.sonymobile.calendar.utils.TextInputLayout.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                TextInputLayout.this.updateLabelState(true);
                if (TextInputLayout.this.mCounterEnabled) {
                    TextInputLayout.this.updateCounter(editable.length());
                }
            }
        });
        if (this.mDefaultTextColor == null) {
            this.mDefaultTextColor = this.mEditText.getHintTextColors();
        }
        if (this.mHintEnabled && TextUtils.isEmpty(this.mHint)) {
            setHint(this.mEditText.getHint());
            this.mEditText.setHint((CharSequence) null);
        }
        if (this.mCounterView != null) {
            updateCounter(this.mEditText.getText().length());
        }
        if (this.mIndicatorArea != null) {
            adjustIndicatorPadding();
        }
        updateLabelState(false);
    }

    private LinearLayout.LayoutParams updateEditTextMargin(ViewGroup.LayoutParams layoutParams) {
        LinearLayout.LayoutParams layoutParams2 = layoutParams instanceof LinearLayout.LayoutParams ? (LinearLayout.LayoutParams) layoutParams : new LinearLayout.LayoutParams(layoutParams);
        if (this.mHintEnabled) {
            if (this.mTmpPaint == null) {
                this.mTmpPaint = new Paint();
            }
            this.mTmpPaint.setTypeface(this.mCollapsingTextHelper.getCollapsedTypeface());
            this.mTmpPaint.setTextSize(this.mCollapsingTextHelper.getCollapsedTextSize());
            layoutParams2.topMargin = (int) (-this.mTmpPaint.ascent());
        } else {
            layoutParams2.topMargin = 0;
        }
        return layoutParams2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLabelState(boolean z) {
        ColorStateList colorStateList;
        TextView textView;
        TextView textView2;
        EditText editText = this.mEditText;
        boolean z2 = (editText == null || TextUtils.isEmpty(editText.getText())) ? false : true;
        boolean zArrayContains = arrayContains(getDrawableState(), android.R.attr.state_focused);
        boolean zIsEmpty = true ^ TextUtils.isEmpty(getError());
        ColorStateList colorStateList2 = this.mDefaultTextColor;
        if (colorStateList2 != null) {
            this.mCollapsingTextHelper.setExpandedTextColor(colorStateList2.getDefaultColor());
        }
        if (this.mCounterOverflowed && (textView2 = this.mCounterView) != null) {
            this.mCollapsingTextHelper.setCollapsedTextColor(textView2.getCurrentTextColor());
        } else if (zIsEmpty && (textView = this.mErrorView) != null) {
            this.mCollapsingTextHelper.setCollapsedTextColor(textView.getCurrentTextColor());
        } else if (zArrayContains && (colorStateList = this.mFocusedTextColor) != null) {
            this.mCollapsingTextHelper.setCollapsedTextColor(colorStateList.getDefaultColor());
        } else {
            ColorStateList colorStateList3 = this.mDefaultTextColor;
            if (colorStateList3 != null) {
                this.mCollapsingTextHelper.setCollapsedTextColor(colorStateList3.getDefaultColor());
            }
        }
        if (z2 || zArrayContains || zIsEmpty) {
            collapseHint(z);
        } else {
            expandHint(z);
        }
    }

    public EditText getEditText() {
        return this.mEditText;
    }

    public void setHint(CharSequence charSequence) {
        if (this.mHintEnabled) {
            setHintInternal(charSequence);
            sendAccessibilityEvent(2048);
        }
    }

    private void setHintInternal(CharSequence charSequence) {
        this.mHint = charSequence;
        this.mCollapsingTextHelper.setText(charSequence);
    }

    public CharSequence getHint() {
        if (this.mHintEnabled) {
            return this.mHint;
        }
        return null;
    }

    public void setHintEnabled(boolean z) {
        if (z != this.mHintEnabled) {
            this.mHintEnabled = z;
            CharSequence hint = this.mEditText.getHint();
            if (!this.mHintEnabled) {
                if (!TextUtils.isEmpty(this.mHint) && TextUtils.isEmpty(hint)) {
                    this.mEditText.setHint(this.mHint);
                }
                setHintInternal(null);
            } else if (!TextUtils.isEmpty(hint)) {
                if (TextUtils.isEmpty(this.mHint)) {
                    setHint(hint);
                }
                this.mEditText.setHint((CharSequence) null);
            }
            EditText editText = this.mEditText;
            if (editText != null) {
                this.mEditText.setLayoutParams(updateEditTextMargin(editText.getLayoutParams()));
            }
        }
    }

    public boolean isHintEnabled() {
        return this.mHintEnabled;
    }

    public void setHintTextAppearance(int i) {
        this.mCollapsingTextHelper.setCollapsedTextAppearance(i);
        this.mFocusedTextColor = ColorStateList.valueOf(this.mCollapsingTextHelper.getCollapsedTextColor());
        if (this.mEditText != null) {
            updateLabelState(false);
            this.mEditText.setLayoutParams(updateEditTextMargin(this.mEditText.getLayoutParams()));
            this.mEditText.requestLayout();
        }
    }

    private void addIndicator(TextView textView, int i) {
        if (this.mIndicatorArea == null) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            this.mIndicatorArea = linearLayout;
            linearLayout.setOrientation(0);
            addView(this.mIndicatorArea, -1, -2);
            this.mIndicatorArea.addView(new Space(getContext()), new LinearLayout.LayoutParams(0, 0, 1.0f));
            if (this.mEditText != null) {
                adjustIndicatorPadding();
            }
        }
        this.mIndicatorArea.setVisibility(0);
        this.mIndicatorArea.addView(textView, i);
        this.mIndicatorsAdded++;
    }

    private void adjustIndicatorPadding() {
        ViewCompat.setPaddingRelative(this.mIndicatorArea, ViewCompat.getPaddingStart(this.mEditText), 0, ViewCompat.getPaddingEnd(this.mEditText), this.mEditText.getPaddingBottom());
    }

    private void removeIndicator(TextView textView) {
        LinearLayout linearLayout = this.mIndicatorArea;
        if (linearLayout != null) {
            linearLayout.removeView(textView);
            int i = this.mIndicatorsAdded - 1;
            this.mIndicatorsAdded = i;
            if (i == 0) {
                this.mIndicatorArea.setVisibility(8);
            }
        }
    }

    public void setErrorEnabled(boolean z) {
        if (this.mErrorEnabled != z) {
            TextView textView = this.mErrorView;
            if (textView != null) {
                ViewCompat.animate(textView).cancel();
            }
            if (z) {
                TextView textView2 = new TextView(getContext());
                this.mErrorView = textView2;
                textView2.setTextAppearance(getContext(), this.mErrorTextAppearance);
                this.mErrorView.setVisibility(4);
                ViewCompat.setAccessibilityLiveRegion(this.mErrorView, 1);
                addIndicator(this.mErrorView, 0);
            } else {
                this.mErrorShown = false;
                updateEditTextBackground();
                removeIndicator(this.mErrorView);
                this.mErrorView = null;
            }
            this.mErrorEnabled = z;
        }
    }

    public boolean isErrorEnabled() {
        return this.mErrorEnabled;
    }

    /* JADX WARN: Code duplicated, block: B:20:0x0042  */
    /* JADX WARN: Code duplicated, block: B:22:0x004c  */
    public void setError(CharSequence charSequence) {
        if (!this.mErrorEnabled) {
            if (TextUtils.isEmpty(charSequence)) {
                return;
            } else {
                setErrorEnabled(true);
            }
        }
        boolean z = false;
        if (!TextUtils.isEmpty(charSequence)) {
            if (TextUtils.equals(charSequence, this.mErrorView.getText())) {
                if (!this.mErrorView.isShown() || ViewCompat.getAlpha(this.mErrorView) < 1.0f) {
                }
                if (z) {
                    if (ViewCompat.getAlpha(this.mErrorView) == 1.0f) {
                        ViewCompat.setAlpha(this.mErrorView, 0.0f);
                    }
                    ViewCompat.animate(this.mErrorView).alpha(1.0f).setDuration(200L).setInterpolator(new LinearOutSlowInInterpolator()).setListener(new ViewPropertyAnimatorListenerAdapter() { // from class: com.sonymobile.calendar.utils.TextInputLayout.2
                        @Override // androidx.core.view.ViewPropertyAnimatorListenerAdapter, androidx.core.view.ViewPropertyAnimatorListener
                        public void onAnimationStart(View view) {
                            view.setVisibility(0);
                        }
                    }).start();
                }
                this.mErrorShown = true;
                updateEditTextBackground();
                updateLabelState(true);
                return;
            }
            this.mErrorView.setText(charSequence);
            z = true;
            if (z) {
                if (ViewCompat.getAlpha(this.mErrorView) == 1.0f) {
                    ViewCompat.setAlpha(this.mErrorView, 0.0f);
                }
                ViewCompat.animate(this.mErrorView).alpha(1.0f).setDuration(200L).setInterpolator(new LinearOutSlowInInterpolator()).setListener(new ViewPropertyAnimatorListenerAdapter() { // from class: com.sonymobile.calendar.utils.TextInputLayout.2
                    @Override // androidx.core.view.ViewPropertyAnimatorListenerAdapter, androidx.core.view.ViewPropertyAnimatorListener
                    public void onAnimationStart(View view) {
                        view.setVisibility(0);
                    }
                }).start();
            }
            this.mErrorShown = true;
            updateEditTextBackground();
            updateLabelState(true);
            return;
        }
        if (this.mErrorView.getVisibility() == 0) {
            ViewCompat.animate(this.mErrorView).alpha(0.0f).setDuration(200L).setInterpolator(new FastOutLinearInInterpolator()).setListener(new ViewPropertyAnimatorListenerAdapter() { // from class: com.sonymobile.calendar.utils.TextInputLayout.3
                @Override // androidx.core.view.ViewPropertyAnimatorListenerAdapter, androidx.core.view.ViewPropertyAnimatorListener
                public void onAnimationEnd(View view) {
                    view.setVisibility(4);
                    TextInputLayout.this.updateLabelState(true);
                }
            }).start();
            this.mErrorShown = false;
            updateEditTextBackground();
        }
    }

    public void setCounterEnabled(boolean z) {
        if (this.mCounterEnabled != z) {
            if (z) {
                TextView textView = new TextView(getContext());
                this.mCounterView = textView;
                textView.setMaxLines(1);
                this.mCounterView.setTextAppearance(getContext(), this.mCounterTextAppearance);
                ViewCompat.setAccessibilityLiveRegion(this.mCounterView, 1);
                addIndicator(this.mCounterView, -1);
                EditText editText = this.mEditText;
                if (editText == null) {
                    updateCounter(0);
                } else {
                    updateCounter(editText.getText().length());
                }
            } else {
                removeIndicator(this.mCounterView);
                this.mCounterView = null;
            }
            this.mCounterEnabled = z;
        }
    }

    public boolean isCounterEnabled() {
        return this.mCounterEnabled;
    }

    public void setCounterMaxLength(int i) {
        if (this.mCounterMaxLength != i) {
            if (i > 0) {
                this.mCounterMaxLength = i;
            } else {
                this.mCounterMaxLength = -1;
            }
            if (this.mCounterEnabled) {
                EditText editText = this.mEditText;
                updateCounter(editText == null ? 0 : editText.getText().length());
            }
        }
    }

    public int getCounterMaxLength() {
        return this.mCounterMaxLength;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCounter(int i) {
        boolean z = this.mCounterOverflowed;
        int i2 = this.mCounterMaxLength;
        if (i2 == -1) {
            this.mCounterView.setText(String.valueOf(i));
            this.mCounterOverflowed = false;
        } else {
            boolean z2 = i > i2;
            this.mCounterOverflowed = z2;
            if (z != z2) {
                this.mCounterView.setTextAppearance(getContext(), this.mCounterOverflowed ? this.mCounterOverflowTextAppearance : this.mCounterTextAppearance);
            }
            this.mCounterView.setText(getContext().getString(R.string.character_counter_pattern, Integer.valueOf(i), Integer.valueOf(this.mCounterMaxLength)));
        }
        if (this.mEditText == null || z == this.mCounterOverflowed) {
            return;
        }
        updateLabelState(false);
        updateEditTextBackground();
    }

    private void updateEditTextBackground() {
        ensureBackgroundDrawableStateWorkaround();
        Drawable background = this.mEditText.getBackground();
        if (background == null) {
            return;
        }
        if (this.mErrorShown && this.mErrorView != null) {
            background.setColorFilter(new PorterDuffColorFilter(this.mErrorView.getCurrentTextColor(), PorterDuff.Mode.SRC_IN));
        } else if (this.mCounterOverflowed && this.mCounterView != null) {
            background.setColorFilter(new PorterDuffColorFilter(this.mCounterView.getCurrentTextColor(), PorterDuff.Mode.SRC_IN));
        } else {
            background.clearColorFilter();
            this.mEditText.refreshDrawableState();
        }
    }

    private void ensureBackgroundDrawableStateWorkaround() {
        Drawable background = this.mEditText.getBackground();
        if (background == null || this.mHasReconstructedEditTextBackground) {
            return;
        }
        Drawable drawableNewDrawable = background.getConstantState().newDrawable();
        if (background instanceof DrawableContainer) {
            this.mHasReconstructedEditTextBackground = setContainerConstantState((DrawableContainer) background, drawableNewDrawable.getConstantState());
        }
        if (this.mHasReconstructedEditTextBackground) {
            return;
        }
        this.mEditText.setBackgroundDrawable(drawableNewDrawable);
        this.mHasReconstructedEditTextBackground = true;
    }

    private static boolean setContainerConstantState(DrawableContainer drawableContainer, Drawable.ConstantState constantState) {
        if (!sSetConstantStateMethodFetched) {
            try {
                Method declaredMethod = DrawableContainer.class.getDeclaredMethod("setConstantState", DrawableContainer.DrawableContainerState.class);
                sSetConstantStateMethod = declaredMethod;
                declaredMethod.setAccessible(true);
            } catch (NoSuchMethodException unused) {
            }
            sSetConstantStateMethodFetched = true;
        }
        Method method = sSetConstantStateMethod;
        if (method != null) {
            try {
                method.invoke(drawableContainer, constantState);
                return true;
            } catch (Exception unused2) {
            }
        }
        return false;
    }

    public CharSequence getError() {
        TextView textView;
        if (this.mErrorEnabled && (textView = this.mErrorView) != null && textView.getVisibility() == 0) {
            return this.mErrorView.getText();
        }
        return null;
    }

    public boolean isHintAnimationEnabled() {
        return this.mHintAnimationEnabled;
    }

    public void setHintAnimationEnabled(boolean z) {
        this.mHintAnimationEnabled = z;
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.mHintEnabled) {
            this.mCollapsingTextHelper.draw(canvas);
        }
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        EditText editText;
        super.onLayout(z, i, i2, i3, i4);
        if (!this.mHintEnabled || (editText = this.mEditText) == null) {
            return;
        }
        int left = editText.getLeft() + this.mEditText.getCompoundPaddingLeft();
        int right = this.mEditText.getRight() - this.mEditText.getCompoundPaddingRight();
        this.mCollapsingTextHelper.setExpandedBounds(left, this.mEditText.getTop() + this.mEditText.getCompoundPaddingTop(), right, this.mEditText.getBottom() - this.mEditText.getCompoundPaddingBottom());
        this.mCollapsingTextHelper.setCollapsedBounds(left, getPaddingTop(), right, (i4 - i2) - getPaddingBottom());
        this.mCollapsingTextHelper.recalculate();
    }

    @Override // android.view.View
    public void refreshDrawableState() {
        super.refreshDrawableState();
        updateLabelState(ViewCompat.isLaidOut(this));
    }

    private void collapseHint(boolean z) {
        ValueAnimator valueAnimator = this.mAnimator;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.mAnimator.cancel();
        }
        if (z && this.mHintAnimationEnabled) {
            animateToExpansionFraction(1.0f);
        } else {
            this.mCollapsingTextHelper.setExpansionFraction(1.0f);
        }
    }

    private void expandHint(boolean z) {
        ValueAnimator valueAnimator = this.mAnimator;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.mAnimator.cancel();
        }
        if (z && this.mHintAnimationEnabled) {
            animateToExpansionFraction(0.0f);
        } else {
            this.mCollapsingTextHelper.setExpansionFraction(0.0f);
        }
    }

    private void animateToExpansionFraction(float f) {
        if (this.mCollapsingTextHelper.getExpansionFraction() == f) {
            return;
        }
        if (this.mAnimator == null) {
            ValueAnimator valueAnimator = new ValueAnimator();
            this.mAnimator = valueAnimator;
            valueAnimator.setInterpolator(new LinearOutSlowInInterpolator());
            this.mAnimator.setDuration(200L);
            this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.sonymobile.calendar.utils.TextInputLayout.4
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    TextInputLayout.this.mCollapsingTextHelper.setExpansionFraction(((Float) valueAnimator2.getAnimatedValue()).floatValue());
                }
            });
        }
        this.mAnimator.setFloatValues(this.mCollapsingTextHelper.getExpansionFraction(), f);
        this.mAnimator.start();
    }

    private int getThemeAttrColor(int i) {
        TypedValue typedValue = new TypedValue();
        if (getContext().getTheme().resolveAttribute(i, typedValue, true)) {
            return typedValue.data;
        }
        return -65281;
    }

    private class TextInputAccessibilityDelegate extends AccessibilityDelegateCompat {
        private TextInputAccessibilityDelegate() {
        }

        @Override // androidx.core.view.AccessibilityDelegateCompat
        public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            accessibilityEvent.setClassName("TextInputLayout");
        }

        @Override // androidx.core.view.AccessibilityDelegateCompat
        public void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            super.onPopulateAccessibilityEvent(view, accessibilityEvent);
            CharSequence text = TextInputLayout.this.mCollapsingTextHelper.getText();
            if (TextUtils.isEmpty(text)) {
                return;
            }
            accessibilityEvent.getText().add(text);
        }

        @Override // androidx.core.view.AccessibilityDelegateCompat
        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
            accessibilityNodeInfoCompat.setClassName("TextInputLayout");
            CharSequence text = TextInputLayout.this.mCollapsingTextHelper.getText();
            if (!TextUtils.isEmpty(text)) {
                accessibilityNodeInfoCompat.setText(text);
            }
            if (TextInputLayout.this.mEditText != null) {
                accessibilityNodeInfoCompat.setLabelFor(TextInputLayout.this.mEditText);
            }
            CharSequence text2 = TextInputLayout.this.mErrorView != null ? TextInputLayout.this.mErrorView.getText() : null;
            if (TextUtils.isEmpty(text2)) {
                return;
            }
            accessibilityNodeInfoCompat.setContentInvalid(true);
            accessibilityNodeInfoCompat.setError(text2);
        }
    }

    private static boolean arrayContains(int[] iArr, int i) {
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }
}
