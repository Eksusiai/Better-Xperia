package com.sonymobile.generativeartwork.settings;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class LayerSettings {
    private final HashMap<Item, Object> mMap = new HashMap<>();

    public enum Item {
        GradientColorPoints,
        GradientAngle,
        GradientRanges,
        LetterColorPoints,
        LetterAngle,
        LetterXOffset,
        LetterYOffset,
        LetterScale,
        PaletteColors
    }

    public LayerSettings() {
    }

    public LayerSettings(LayerSettings layerSettings) {
        for (Map.Entry<Item, Object> entry : layerSettings.mMap.entrySet()) {
            this.mMap.put(entry.getKey(), entry.getValue());
        }
    }

    public void add(Item item, Object obj) {
        this.mMap.put(item, obj);
    }

    public Object get(Item item) {
        return this.mMap.get(item);
    }

    public void validateKey(Item item) {
        if (this.mMap.containsKey(item)) {
            if (!validate(item, this.mMap.get(item))) {
                throw new IllegalArgumentException("The object of key: " + item + " is not acceptable for such key");
            }
            return;
        }
        throw new IllegalArgumentException("The object for key: " + item + " is not presented in the map");
    }

    public boolean containsKey(Item item) {
        return this.mMap.containsKey(item);
    }

    /* JADX INFO: renamed from: com.sonymobile.generativeartwork.settings.LayerSettings$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$sonymobile$generativeartwork$settings$LayerSettings$Item;

        static {
            int[] iArr = new int[Item.values().length];
            $SwitchMap$com$sonymobile$generativeartwork$settings$LayerSettings$Item = iArr;
            try {
                iArr[Item.LetterXOffset.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$settings$LayerSettings$Item[Item.LetterYOffset.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$settings$LayerSettings$Item[Item.LetterScale.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$settings$LayerSettings$Item[Item.GradientAngle.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$settings$LayerSettings$Item[Item.LetterAngle.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$settings$LayerSettings$Item[Item.GradientColorPoints.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$settings$LayerSettings$Item[Item.GradientRanges.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$settings$LayerSettings$Item[Item.LetterColorPoints.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$settings$LayerSettings$Item[Item.PaletteColors.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
        }
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:6:0x000e. Please report as an issue. */
    private boolean validate(Item item, Object obj) {
        Object f;
        int length;
        int i;
        int i2;
        int length2;
        if (obj == null) {
            return false;
        }
        int i3 = 3;
        switch (AnonymousClass1.$SwitchMap$com$sonymobile$generativeartwork$settings$LayerSettings$Item[item.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                f = new Float(0.0d);
                length2 = 0;
                i2 = 0;
                i = i2;
                break;
            case 6:
                f = new ColorSettings[3];
                length = ((ColorSettings[]) obj).length;
                i = 1;
                int i4 = length;
                i2 = i3;
                length2 = i4;
                break;
            case 7:
                f = new float[3];
                length = ((float[]) obj).length;
                i = 1;
                int i5 = length;
                i2 = i3;
                length2 = i5;
                break;
            case 8:
                i3 = 2;
                f = new ColorSettings[2];
                length = ((ColorSettings[]) obj).length;
                i = 1;
                int i6 = length;
                i2 = i3;
                length2 = i6;
                break;
            case 9:
                f = new int[1];
                length2 = ((int[]) obj).length;
                i2 = 1;
                i = i2;
                break;
            default:
                f = null;
                length2 = 0;
                i2 = 0;
                i = i2;
                break;
        }
        if (f == null || obj.getClass() != f.getClass()) {
            return false;
        }
        return i == 0 || length2 >= i2;
    }
}
