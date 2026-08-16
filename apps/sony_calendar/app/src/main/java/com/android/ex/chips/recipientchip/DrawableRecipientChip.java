package com.android.ex.chips.recipientchip;

import android.graphics.Canvas;
import android.graphics.Rect;

/* JADX INFO: loaded from: classes.dex */
public interface DrawableRecipientChip extends BaseRecipientChip {
    void draw(Canvas canvas);

    Rect getBounds();
}
