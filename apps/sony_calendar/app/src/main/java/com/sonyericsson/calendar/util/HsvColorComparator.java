package com.sonyericsson.calendar.util;

import android.graphics.Color;
import java.io.Serializable;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class HsvColorComparator implements Comparator<Integer>, Serializable {
    @Override // java.util.Comparator
    public int compare(Integer num, Integer num2) {
        float[] fArr = new float[3];
        Color.colorToHSV(num.intValue(), fArr);
        float f = fArr[0];
        float f2 = fArr[1];
        float f3 = fArr[2];
        float[] fArr2 = new float[3];
        Color.colorToHSV(num2.intValue(), fArr2);
        float f4 = fArr2[0];
        float f5 = fArr2[1];
        float f6 = fArr2[2];
        if (Float.compare(f4, f) != 0) {
            return Float.compare(f4, f);
        }
        if (Float.compare(f5, f2) != 0) {
            return Float.compare(f5, f2);
        }
        if (Float.compare(f6, f3) != 0) {
            return Float.compare(f6, f3);
        }
        return 0;
    }
}
