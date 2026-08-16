package com.sonymobile.generativeartwork.layers;

import com.sonymobile.generativeartwork.settings.ColorSettings;
import com.sonymobile.generativeartwork.settings.LayerSettings;
import java.lang.reflect.Array;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public final class LayerSetup {
    private static final int START_COLOR_ID = 0;

    public static void setSettings(ArrayList<GenerativeLayer> arrayList, LayerSettings layerSettings) {
        layerSettings.validateKey(LayerSettings.Item.PaletteColors);
        layerSettings.validateKey(LayerSettings.Item.GradientColorPoints);
        layerSettings.validateKey(LayerSettings.Item.LetterColorPoints);
        int[] iArr = (int[]) layerSettings.get(LayerSettings.Item.PaletteColors);
        init(arrayList, layerSettings);
        ColorSettings[] colorSettingsArr = (ColorSettings[]) layerSettings.get(LayerSettings.Item.GradientColorPoints);
        ColorSettings[] colorSettingsArr2 = (ColorSettings[]) layerSettings.get(LayerSettings.Item.LetterColorPoints);
        float[][][] fArr = (float[][][]) Array.newInstance((Class<?>) float.class, iArr.length, colorSettingsArr.length, 4);
        float[][][] fArr2 = (float[][][]) Array.newInstance((Class<?>) float.class, iArr.length, colorSettingsArr2.length, 4);
        for (int i = 0; i < iArr.length; i++) {
            for (int i2 = 0; i2 < colorSettingsArr.length; i2++) {
                colorSettingsArr[i2].transform(iArr[i], fArr[i][i2]);
            }
            for (int i3 = 0; i3 < colorSettingsArr2.length; i3++) {
                colorSettingsArr2[i3].transform(iArr[i], fArr2[i][i3]);
            }
        }
        for (GenerativeLayer generativeLayer : arrayList) {
            int i4 = AnonymousClass1.$SwitchMap$com$sonymobile$generativeartwork$layers$LayerType[generativeLayer.getType().ordinal()];
            if (i4 == 1) {
                ((BackgroundLayer) generativeLayer).setColorPalette(fArr);
            } else if (i4 == 2) {
                ((ArtisticLayer) generativeLayer).setColorPalette(fArr2);
            }
        }
    }

    /* JADX INFO: renamed from: com.sonymobile.generativeartwork.layers.LayerSetup$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$sonymobile$generativeartwork$layers$LayerType;

        static {
            int[] iArr = new int[LayerType.values().length];
            $SwitchMap$com$sonymobile$generativeartwork$layers$LayerType = iArr;
            try {
                iArr[LayerType.BACKGROUND.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$layers$LayerType[LayerType.ARTISTIC.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    private static void init(ArrayList<GenerativeLayer> arrayList, LayerSettings layerSettings) {
        layerSettings.validateKey(LayerSettings.Item.PaletteColors);
        int i = ((int[]) layerSettings.get(LayerSettings.Item.PaletteColors))[0];
        for (GenerativeLayer generativeLayer : arrayList) {
            int i2 = AnonymousClass1.$SwitchMap$com$sonymobile$generativeartwork$layers$LayerType[generativeLayer.getType().ordinal()];
            if (i2 == 1) {
                initBackground((BackgroundLayer) generativeLayer, i, layerSettings);
            } else if (i2 == 2) {
                initArtisticLayer((ArtisticLayer) generativeLayer, i, layerSettings);
            }
        }
    }

    private static void initBackground(BackgroundLayer backgroundLayer, int i, LayerSettings layerSettings) {
        layerSettings.validateKey(LayerSettings.Item.GradientColorPoints);
        layerSettings.validateKey(LayerSettings.Item.GradientRanges);
        layerSettings.validateKey(LayerSettings.Item.GradientAngle);
        ColorSettings[] colorSettingsArr = (ColorSettings[]) layerSettings.get(LayerSettings.Item.GradientColorPoints);
        float[][] fArr = (float[][]) Array.newInstance((Class<?>) float.class, colorSettingsArr.length, 4);
        for (int i2 = 0; i2 < colorSettingsArr.length; i2++) {
            colorSettingsArr[i2].transform(i, fArr[i2]);
        }
        backgroundLayer.setColor(fArr);
        backgroundLayer.setGradientRanges((float[]) layerSettings.get(LayerSettings.Item.GradientRanges));
        backgroundLayer.setGradientAngle(((Float) layerSettings.get(LayerSettings.Item.GradientAngle)).floatValue());
    }

    private static void initArtisticLayer(ArtisticLayer artisticLayer, int i, LayerSettings layerSettings) {
        layerSettings.validateKey(LayerSettings.Item.LetterColorPoints);
        layerSettings.validateKey(LayerSettings.Item.LetterAngle);
        layerSettings.validateKey(LayerSettings.Item.LetterXOffset);
        layerSettings.validateKey(LayerSettings.Item.LetterYOffset);
        layerSettings.validateKey(LayerSettings.Item.LetterScale);
        ColorSettings[] colorSettingsArr = (ColorSettings[]) layerSettings.get(LayerSettings.Item.LetterColorPoints);
        float[][] fArr = (float[][]) Array.newInstance((Class<?>) float.class, colorSettingsArr.length, 4);
        for (int i2 = 0; i2 < colorSettingsArr.length; i2++) {
            colorSettingsArr[i2].transform(i, fArr[i2]);
        }
        artisticLayer.setColor(fArr);
        artisticLayer.setArtTrasnformation(((Float) layerSettings.get(LayerSettings.Item.LetterAngle)).floatValue(), ((Float) layerSettings.get(LayerSettings.Item.LetterXOffset)).floatValue(), ((Float) layerSettings.get(LayerSettings.Item.LetterYOffset)).floatValue(), ((Float) layerSettings.get(LayerSettings.Item.LetterScale)).floatValue());
    }
}
