package com.sonymobile.generativeartwork.layers;

import android.util.Log;
import com.sonymobile.generativeartwork.GenerativeArtWork;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes2.dex */
public class LayerFactory {
    private static final String TAG = "com.sonymobile.generativeartwork.layers.LayerFactory";

    /* JADX INFO: renamed from: com.sonymobile.generativeartwork.layers.LayerFactory$1, reason: invalid class name */
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

    public static GenerativeLayer buildLayer(LayerType layerType, GenerativeArtWork generativeArtWork) {
        int i = AnonymousClass1.$SwitchMap$com$sonymobile$generativeartwork$layers$LayerType[layerType.ordinal()];
        if (i == 1) {
            return new BackgroundLayer();
        }
        if (i == 2) {
            return new ArtisticLayer();
        }
        Log.e(TAG, "Uknown layer type");
        return null;
    }

    public static void initLayers(ArrayList<GenerativeLayer> arrayList, GenerativeArtWork generativeArtWork) {
        Iterator<GenerativeLayer> it = arrayList.iterator();
        while (it.hasNext()) {
            initLayer(it.next(), generativeArtWork);
        }
    }

    public static void initLayer(GenerativeLayer generativeLayer, GenerativeArtWork generativeArtWork) {
        int i = AnonymousClass1.$SwitchMap$com$sonymobile$generativeartwork$layers$LayerType[generativeLayer.getType().ordinal()];
        if (i == 1) {
            ((BackgroundLayer) generativeLayer).initLayer(generativeArtWork);
        } else {
            if (i != 2) {
                return;
            }
            ((ArtisticLayer) generativeLayer).initLayer(generativeArtWork);
        }
    }

    public static void releaseLayers(ArrayList<GenerativeLayer> arrayList) {
        Iterator<GenerativeLayer> it = arrayList.iterator();
        while (it.hasNext()) {
            releaseLayer(it.next());
        }
    }

    public static void releaseLayer(GenerativeLayer generativeLayer) {
        int i = AnonymousClass1.$SwitchMap$com$sonymobile$generativeartwork$layers$LayerType[generativeLayer.getType().ordinal()];
        if (i == 1) {
            ((BackgroundLayer) generativeLayer).releaseLayer();
        } else {
            if (i != 2) {
                return;
            }
            ((ArtisticLayer) generativeLayer).releaseLayer();
        }
    }
}
