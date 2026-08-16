package com.sonymobile.generativeartwork.settings;

import android.content.Context;
import com.sonymobile.calendar.R;

/* JADX INFO: loaded from: classes2.dex */
public class SquarePatternSetupW17_2 extends LayerSettings {
    public SquarePatternSetupW17_2(Context context) {
        ColorSettings[] colorSettingsArr = {new ColorSettings(), new ColorSettings(), new ColorSettings()};
        colorSettingsArr[0].add(ColorSettings.Model.HSB, ColorSettings.Component.Saturation, ColorSettings.Op.Offset, 0.0f);
        colorSettingsArr[1].add(ColorSettings.Model.HSB, ColorSettings.Component.Brightness, ColorSettings.Op.Multiply, 0.9090909f);
        colorSettingsArr[2].add(ColorSettings.Model.HSB, ColorSettings.Component.Brightness, ColorSettings.Op.Multiply, 0.8333333f);
        add(LayerSettings.Item.GradientColorPoints, colorSettingsArr);
        add(LayerSettings.Item.GradientAngle, Float.valueOf(45.0f));
        add(LayerSettings.Item.GradientRanges, new float[]{0.0f, 0.5f, 1.0f});
        colorSettingsArr[0].add(ColorSettings.Model.HSB, ColorSettings.Component.Saturation, ColorSettings.Op.Multiply, 0.58823526f);
        colorSettingsArr[0].add(ColorSettings.Model.HSB, ColorSettings.Component.Brightness, ColorSettings.Op.Multiply, 1.4f);
        colorSettingsArr[0].add(ColorSettings.Model.HSB, ColorSettings.Component.Opacity, ColorSettings.Op.Set, 0.25f);
        ColorSettings[] colorSettingsArr2 = {new ColorSettings(), new ColorSettings()};
        colorSettingsArr2[1].add(ColorSettings.Model.HSB, ColorSettings.Component.Saturation, ColorSettings.Op.Multiply, 0.6666667f);
        colorSettingsArr2[1].add(ColorSettings.Model.HSB, ColorSettings.Component.Brightness, ColorSettings.Op.Multiply, 1.2f);
        colorSettingsArr2[1].add(ColorSettings.Model.HSB, ColorSettings.Component.Opacity, ColorSettings.Op.Set, 0.2f);
        add(LayerSettings.Item.LetterColorPoints, colorSettingsArr2);
        add(LayerSettings.Item.LetterAngle, Float.valueOf(20.0f));
        add(LayerSettings.Item.LetterXOffset, Float.valueOf(-0.3f));
        add(LayerSettings.Item.LetterYOffset, Float.valueOf(0.15f));
        add(LayerSettings.Item.LetterScale, Float.valueOf(0.7692308f));
        add(LayerSettings.Item.PaletteColors, getPalette(context));
    }

    private static int[] getPalette(Context context) {
        return new int[]{context.getResources().getColor(R.color.blue), context.getResources().getColor(R.color.purple), context.getResources().getColor(R.color.red), context.getResources().getColor(R.color.yellow), context.getResources().getColor(R.color.green), context.getResources().getColor(R.color.gray)};
    }
}
