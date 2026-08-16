package com.sonymobile.calendar.utils;
import com.sonymobile.calendar.SafeTime;

import android.R;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.transition.Visibility;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonymobile.calendar.CalendarApplication;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.WeekNavigatorDayView;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import java.util.ArrayList;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class UiUtils {
    private static final int[] ACTIONBAR_STYLE = {R.attr.actionBarStyle};
    private static final int[] BACKGROUND = {R.attr.background};
    private static final float INTENSITY_ADJUST = 1.0f;
    private static final float SATURATION_ADJUST = 1.0f;
    public static final String SLIDE_UP_ANIMATION = "slideUpAnimation";
    private static final String TAG = "UiUtils";
    private static final boolean THEME_COLOR = false;
    private static final float TODAY_MARKER_STROKE_WIDTH = 1.0f;

    public static int getPrimaryColor(Context context) {
        return -1;
    }

    public static boolean isPhonePortrait(Context context) {
        return (Utils.isTabletDevice(context) || context.getResources().getConfiguration().orientation == 2) ? false : true;
    }

    public static boolean isPhoneLandscape(Context context) {
        return !Utils.isTabletDevice(context) && context.getResources().getConfiguration().orientation == 2;
    }

    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == 2;
    }

    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == 1;
    }

    public static void drawEventDot(int i, Canvas canvas, Paint paint, float f, float f2, int i2) {
        ArrayList<EventInfo> eventsForDay = EventUtils.getEventsForDay(i);
        ArrayList<EventInfo> allDayEventsForDay = EventUtils.getAllDayEventsForDay(i);
        if (eventsForDay.isEmpty() && allDayEventsForDay.isEmpty()) {
            return;
        }
        paint.setColor(i2);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(f, f2, 5.0f, paint);
    }

    public static boolean shouldUseOneLetterForDay(Resources resources) {
        Locale locale = resources.getConfiguration().locale;
        return (locale.toString().startsWith("fa") || locale.toString().startsWith("iw") || locale.toString().startsWith("ar") || locale.toString().startsWith("zh") || locale.toString().startsWith("ko") || locale.toString().startsWith("te") || locale.toString().startsWith("ja")) ? false : true;
    }

    public static int getEventDotColor(Context context, Time time, boolean z, boolean z2) {
        int color;
        int primaryTextColor;
        if (FreeDayService.getInstance().isFreeDay(time.year, time.month, time.monthDay)) {
            color = FreeDayService.getInstance().getHolidayColor(context);
        } else {
            color = ContextCompat.getColor(context, com.sonymobile.calendar.R.color.day_number_color);
        }
        if (z) {
            if (FreeDayService.getInstance().isFreeDay(time.year, time.month, time.monthDay)) {
                primaryTextColor = FreeDayService.getInstance().getHolidayColor(context);
            } else {
                primaryTextColor = getPrimaryTextColor(context);
            }
            color = primaryTextColor;
        }
        return z2 ? ContextCompat.getColor(context, com.sonymobile.calendar.R.color.day_number_white) : color;
    }

    public static int getDateBoxBackgroundColor(Context context, Time time) {
        if (FreeDayService.getInstance().isFreeDay(time.year, time.month, time.monthDay)) {
            return FreeDayService.getInstance().getHolidayColor(context);
        }
        return getPrimaryTextColor(context);
    }

    public static int getTodayMarkerColor(Context context, Time time) {
        if (FreeDayService.getInstance().isFreeDay(time.year, time.month, time.monthDay)) {
            return FreeDayService.getInstance().getHolidayColor(context);
        }
        return getPrimaryTextColor(context);
    }

    public static int getDateBoxForegroundColor(Context context, Time time, boolean z, boolean z2, boolean z3) {
        if (z) {
            if (FreeDayService.getInstance().isFreeDay(time.year, time.month, time.monthDay)) {
                return ContextCompat.getColor(context, com.sonymobile.calendar.R.color.free_day_disabled_color);
            }
            return ContextCompat.getColor(context, com.sonymobile.calendar.R.color.day_disabled_color);
        }
        if (z2) {
            return ContextCompat.getColor(context, com.sonymobile.calendar.R.color.calendar_grid_area_background);
        }
        if (FreeDayService.getInstance().isFreeDay(time.year, time.month, time.monthDay)) {
            return ContextCompat.getColor(context, com.sonymobile.calendar.R.color.free_day_color);
        }
        if (z3) {
            return getPrimaryTextColor(context);
        }
        return ContextCompat.getColor(context, com.sonymobile.calendar.R.color.day_number_color);
    }

    public static void drawMarkerWeekDayNavigation(Context context, Canvas canvas, WeekNavigatorDayView weekNavigatorDayView, boolean z, boolean z2, int i) {
        int width;
        int width2;
        int width3;
        int width4;
        int i2;
        Paint paint = new Paint();
        paint.setColor(getDateBoxBackgroundColor(context, weekNavigatorDayView.getDay()));
        float todayMarkerStrokeWidth = getTodayMarkerStrokeWidth(context);
        float dimension = context.getResources().getDimension(com.sonymobile.calendar.R.dimen.day_week_view_portrait_circle_radius);
        if (LunarAvailabilityManager.isLunarAvailable(context)) {
            dimension = context.getResources().getDimension(com.sonymobile.calendar.R.dimen.month_view_phone_lunar_circle_radius);
        }
        if (z2) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(todayMarkerStrokeWidth);
        } else {
            paint.setAlpha(i);
        }
        paint.setAntiAlias(true);
        if (z) {
            if (isLandscape(context)) {
                if (CalendarApplication.isR2L(context.getResources())) {
                    width3 = (weekNavigatorDayView.getWidth() / 2) + (weekNavigatorDayView.getWidth() / 4);
                    width4 = weekNavigatorDayView.getWidth() / 16;
                    i2 = width3 + width4;
                } else {
                    width = (weekNavigatorDayView.getWidth() / 2) - (weekNavigatorDayView.getWidth() / 4);
                    width2 = weekNavigatorDayView.getWidth() / 16;
                    i2 = width - width2;
                }
            } else if (CalendarApplication.isR2L(context.getResources())) {
                width3 = weekNavigatorDayView.getWidth() / 2;
                width4 = weekNavigatorDayView.getWidth() / 4;
                i2 = width3 + width4;
            } else {
                width = weekNavigatorDayView.getWidth() / 2;
                width2 = weekNavigatorDayView.getWidth() / 4;
                i2 = width - width2;
            }
            int right = weekNavigatorDayView.getRight() - i2;
            int boxTop = (int) (weekNavigatorDayView.getBoxTop() + dimension + (context.getResources().getDimension(com.sonymobile.calendar.R.dimen.month_view_tablet_day_number_padding_top) / 2.0f));
            if (z2) {
                dimension -= todayMarkerStrokeWidth / 2.0f;
            }
            canvas.drawCircle(right, boxTop, dimension, paint);
            return;
        }
        boolean zIsLunarAvailable = LunarAvailabilityManager.isLunarAvailable(context);
        if (isLandscape(context)) {
            float f = dimension / 1.5f;
            if (z2) {
                f -= todayMarkerStrokeWidth / 2.0f;
            }
            int left = weekNavigatorDayView.getLeft();
            int right2 = left + ((int) ((weekNavigatorDayView.getRight() - left) * context.getResources().getFraction(com.sonymobile.calendar.R.fraction.navigation_number_position_week, 1, 1)));
            int boxTop2 = weekNavigatorDayView.getBoxTop();
            int boxTop3 = boxTop2 - (boxTop2 / 5);
            if (zIsLunarAvailable) {
                boxTop3 = weekNavigatorDayView.getBoxTop() - 5;
            }
            canvas.drawCircle(right2, boxTop3, f, paint);
            return;
        }
        if (z2) {
            dimension -= todayMarkerStrokeWidth / 2.0f;
        }
        int left2 = weekNavigatorDayView.getLeft() - 6;
        int right3 = left2 + (((weekNavigatorDayView.getRight() - 1) - left2) / 2) + 4;
        int boxHeight = (weekNavigatorDayView.getBoxHeight() / 2) - 12;
        if (isSub320dpScreen(context)) {
            boxHeight -= 5;
        }
        int boxTop4 = weekNavigatorDayView.getBoxTop() + 3;
        if (zIsLunarAvailable) {
            boxTop4 = weekNavigatorDayView.getBoxTop() - 4;
        }
        canvas.drawCircle(right3, boxTop4 + boxHeight, dimension, paint);
    }

    public static float getTodayMarkerStrokeWidth(Context context) {
        return TypedValue.applyDimension(1, 1.0f, context.getResources().getDisplayMetrics());
    }

    public static void showWeekNumberInWeekDayView(Context context, TextView textView) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(GeneralPreferences.KEY_SHOW_WEEK_NUMBER, false)) {
            Time time = new SafeTime();
            time.set(Utils.getDisplayTime());
            time.normalize(false);
            Integer numValueOf = Integer.valueOf(Utils.getWeekNumberOfDay(context, time));
            textView.setTextColor(ContextCompat.getColor(context, com.sonymobile.calendar.R.color.day_number_color));
            textView.setText(numValueOf.toString());
            return;
        }
        textView.setText("");
    }

    public static int getAccentColor(Context context) {
        TypedArray typedArrayObtainStyledAttributes = context.getApplicationContext().getTheme().obtainStyledAttributes(new int[]{R.attr.colorAccent});
        int color = typedArrayObtainStyledAttributes.getColor(0, ContextCompat.getColor(context, com.sonymobile.calendar.R.color.local_accent_color_light));
        typedArrayObtainStyledAttributes.recycle();
        return color;
    }

    public static int getPrimaryTextColor(Context context) {
        TypedArray typedArrayObtainStyledAttributes = context.getTheme().obtainStyledAttributes(new int[]{R.attr.textColorPrimary});
        int color = typedArrayObtainStyledAttributes.getColor(0, ContextCompat.getColor(context, com.sonymobile.calendar.R.color.black));
        typedArrayObtainStyledAttributes.recycle();
        return color;
    }

    public static int getSecondaryTextColor(Context context) {
        TypedArray typedArrayObtainStyledAttributes = context.getTheme().obtainStyledAttributes(new int[]{R.attr.textColorSecondary});
        int color = typedArrayObtainStyledAttributes.getColor(0, ContextCompat.getColor(context, com.sonymobile.calendar.R.color.black));
        typedArrayObtainStyledAttributes.recycle();
        return color;
    }

    public static void setNavigationBar(Activity activity) {
        activity.getWindow().setBackgroundDrawable(activity.getResources().getDrawable(com.sonymobile.calendar.R.drawable.backgroundColor));
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | 16);
    }

    public static Bundle makeZoomAnimationOnViewBundle(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap drawingCache = view.getDrawingCache();
        if (drawingCache == null) {
            return null;
        }
        return ActivityOptions.makeThumbnailScaleUpAnimation(view, drawingCache, 0, 0).toBundle();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmapCreateBitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmapCreateBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmapCreateBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmapCreateBitmap;
    }

    public static int getDisplayColorFromColor(int i) {
        return getDisplayColorFromColor(i, 1.0f);
    }

    public static int getDisplayColorFromColor(int i, float f) {
        float[] fArr = new float[3];
        Color.colorToHSV(i, fArr);
        fArr[1] = Math.min(fArr[1] * f, 1.0f);
        fArr[2] = fArr[2] * 1.0f;
        return Color.HSVToColor(fArr);
    }

    public static Drawable getActionBarBackground(Context context) {
        if (context == null) {
            throw new IllegalArgumentException();
        }
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(ACTIONBAR_STYLE);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(0, 0);
        typedArrayObtainStyledAttributes.recycle();
        if (resourceId == 0) {
            return null;
        }
        TypedArray typedArrayObtainStyledAttributes2 = context.obtainStyledAttributes(resourceId, BACKGROUND);
        Drawable drawable = typedArrayObtainStyledAttributes2.getDrawable(0);
        typedArrayObtainStyledAttributes2.recycle();
        return drawable;
    }

    public static boolean isSub320dpScreen(Context context) {
        Activity activity = context instanceof Activity ? (Activity) context : null;
        if (activity == null || !activity.isInMultiWindowMode()) {
            return context.getResources().getBoolean(com.sonymobile.calendar.R.bool.is_sub320dp_screen);
        }
        return true;
    }

    public static void setEnterTransition(Window window, Visibility visibility) {
        if (Utils.isBuildVersionLollipopOrPlus()) {
            window.setEnterTransition(visibility);
        }
    }

    public static void setAllowEnterTransitionOverlap(Window window, boolean z) {
        if (Utils.isBuildVersionLollipopOrPlus()) {
            window.setAllowEnterTransitionOverlap(z);
        }
    }

    public static void setViewBackgroundToAccentColor(Context context, View view) {
        setViewBackgroundColor(view, getAccentColor(context));
    }

    public static void setViewBackgroundToPrimaryColor(Context context, View view) {
        view.setBackgroundColor(getPrimaryColor(context));
    }

    public static void setViewBackgroundColor(View view, int i) {
        int[] iArr = {com.sonymobile.calendar.R.id.shape1, com.sonymobile.calendar.R.id.shape2};
        Drawable background = view.getBackground();
        if (Utils.isBuildVersionLollipopOrPlus()) {
            resolveDrawableAndColorRipple(background, i);
        } else {
            traverseLayerDrawableAndColor(background, iArr, i);
        }
    }

    private static void resolveDrawableAndColorRipple(Drawable drawable, int i) {
        if (drawable instanceof RippleDrawable) {
            RippleDrawable rippleDrawable = (RippleDrawable) drawable;
            int numberOfLayers = rippleDrawable.getNumberOfLayers();
            for (int i2 = 0; i2 < numberOfLayers; i2++) {
                resolveDrawableAndColor(rippleDrawable.getDrawable(i2), i);
            }
            return;
        }
        if (drawable instanceof GradientDrawable) {
            ((GradientDrawable) drawable).setColor(i);
            return;
        }
        if (drawable instanceof StateListDrawable) {
            Drawable current = drawable.getCurrent();
            if (current instanceof RippleDrawable) {
                resolveDrawableAndColorRipple(current, i);
            } else {
                ((GradientDrawable) current.getCurrent()).setColor(i);
            }
        }
    }

    private static void resolveDrawableAndColor(Drawable drawable, int i) {
        if (drawable instanceof GradientDrawable) {
            ((GradientDrawable) drawable).setColor(i);
        } else if (drawable instanceof StateListDrawable) {
            ((GradientDrawable) drawable.getCurrent().getCurrent()).setColor(i);
        }
    }

    private static void traverseLayerDrawableAndColor(Drawable drawable, int[] iArr, int i) {
        for (int i2 : iArr) {
            Integer numValueOf = Integer.valueOf(i2);
            if (drawable instanceof LayerDrawable) {
                drawable = ((LayerDrawable) drawable).findDrawableByLayerId(numValueOf.intValue());
            }
            resolveDrawableAndColor(drawable, i);
        }
    }
}
