package com.sonymobile.calendar.tasks.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.appcompat.widget.AppCompatImageView;

/* JADX INFO: loaded from: classes2.dex */
public class DontPressWithParentImageView extends AppCompatImageView {
    public DontPressWithParentImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.view.View
    public void setPressed(boolean z) {
        if (z && ((View) getParent()).isPressed()) {
            return;
        }
        super.setPressed(z);
    }
}
