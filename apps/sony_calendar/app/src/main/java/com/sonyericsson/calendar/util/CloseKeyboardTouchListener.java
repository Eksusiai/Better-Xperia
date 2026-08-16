package com.sonyericsson.calendar.util;

import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import com.sonymobile.calendar.Utils;

/* JADX INFO: loaded from: classes.dex */
public class CloseKeyboardTouchListener implements View.OnTouchListener {
    private Window mWindow;

    public CloseKeyboardTouchListener(Window window) {
        this.mWindow = window;
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view == null || this.mWindow == null) {
            return false;
        }
        Utils.hideKeyboard(view.getContext(), this.mWindow.getCurrentFocus());
        return false;
    }
}
