package com.sonymobile.generativeartwork.settings;

import android.graphics.Color;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public final class ColorSettings {
    private final ArrayList<Data> mData = new ArrayList<>();

    public enum Component {
        Hue,
        Saturation,
        Value,
        Brightness,
        Opacity
    }

    public enum Model {
        HSB,
        HSV
    }

    public enum Op {
        Offset,
        Multiply,
        Set
    }

    private static class Data {
        final Component mCmp;
        final Model mModel;
        final Op mOp;
        final float mValue;

        public Data(Model model, Component component, Op op, float f) {
            this.mModel = model;
            this.mOp = op;
            this.mCmp = component;
            this.mValue = f;
        }
    }

    public void add(Model model, Component component, Op op, float f) {
        this.mData.add(new Data(model, component, op, f));
    }

    public float[] transform(int i, float[] fArr) {
        if (fArr == null) {
            fArr = new float[4];
        }
        if (fArr.length < 4) {
            throw new RuntimeException("4 components required for rgba");
        }
        fArr[3] = 0.0f;
        fArr[2] = 0.0f;
        fArr[1] = 0.0f;
        fArr[0] = 0.0f;
        fArr[component2index(Component.Opacity)] = 1.0f;
        for (Data data : this.mData) {
            int i2 = AnonymousClass1.$SwitchMap$com$sonymobile$generativeartwork$settings$ColorSettings$Model[data.mModel.ordinal()];
            if (i2 == 1 || i2 == 2) {
                Color.colorToHSV(i, fArr);
                int iComponent2index = component2index(data.mCmp);
                int i3 = AnonymousClass1.$SwitchMap$com$sonymobile$generativeartwork$settings$ColorSettings$Op[data.mOp.ordinal()];
                if (i3 == 1) {
                    fArr[iComponent2index] = data.mValue;
                } else if (i3 == 2) {
                    fArr[iComponent2index] = fArr[iComponent2index] * (data.mValue + 1.0f);
                } else if (i3 == 3) {
                    fArr[iComponent2index] = fArr[iComponent2index] * data.mValue;
                }
                if (AnonymousClass1.$SwitchMap$com$sonymobile$generativeartwork$settings$ColorSettings$Component[data.mCmp.ordinal()] == 1) {
                    fArr[iComponent2index] = (fArr[iComponent2index] + 360.0f) % 360.0f;
                }
                i = Color.HSVToColor(fArr);
            }
        }
        convertIntToFloat(i, fArr);
        return fArr;
    }

    /* JADX INFO: renamed from: com.sonymobile.generativeartwork.settings.ColorSettings$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$sonymobile$generativeartwork$settings$ColorSettings$Component;
        static final /* synthetic */ int[] $SwitchMap$com$sonymobile$generativeartwork$settings$ColorSettings$Model;
        static final /* synthetic */ int[] $SwitchMap$com$sonymobile$generativeartwork$settings$ColorSettings$Op;

        static {
            int[] iArr = new int[Model.values().length];
            $SwitchMap$com$sonymobile$generativeartwork$settings$ColorSettings$Model = iArr;
            try {
                iArr[Model.HSB.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$settings$ColorSettings$Model[Model.HSV.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            int[] iArr2 = new int[Component.values().length];
            $SwitchMap$com$sonymobile$generativeartwork$settings$ColorSettings$Component = iArr2;
            try {
                iArr2[Component.Hue.ordinal()] = 1;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$settings$ColorSettings$Component[Component.Saturation.ordinal()] = 2;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$settings$ColorSettings$Component[Component.Value.ordinal()] = 3;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$settings$ColorSettings$Component[Component.Brightness.ordinal()] = 4;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$settings$ColorSettings$Component[Component.Opacity.ordinal()] = 5;
            } catch (NoSuchFieldError unused7) {
            }
            int[] iArr3 = new int[Op.values().length];
            $SwitchMap$com$sonymobile$generativeartwork$settings$ColorSettings$Op = iArr3;
            try {
                iArr3[Op.Set.ordinal()] = 1;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$settings$ColorSettings$Op[Op.Offset.ordinal()] = 2;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$settings$ColorSettings$Op[Op.Multiply.ordinal()] = 3;
            } catch (NoSuchFieldError unused10) {
            }
        }
    }

    private static int component2index(Component component) {
        int i = AnonymousClass1.$SwitchMap$com$sonymobile$generativeartwork$settings$ColorSettings$Component[component.ordinal()];
        if (i == 1) {
            return 0;
        }
        if (i == 2) {
            return 1;
        }
        if (i == 3 || i == 4) {
            return 2;
        }
        if (i == 5) {
            return 3;
        }
        throw new IllegalArgumentException("Invalid color settings");
    }

    private static void convertIntToFloat(int i, float[] fArr) {
        fArr[0] = ((i >> 16) & 255) / 255.0f;
        fArr[1] = ((i >> 8) & 255) / 255.0f;
        fArr[2] = ((i >> 0) & 255) / 255.0f;
    }
}
