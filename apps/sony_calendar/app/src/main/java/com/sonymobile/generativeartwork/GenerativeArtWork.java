package com.sonymobile.generativeartwork;

import android.content.Context;
import android.graphics.Bitmap;
import com.sonymobile.generativeartwork.layers.GenerativeLayer;
import com.sonymobile.generativeartwork.layers.LayerFactory;
import com.sonymobile.generativeartwork.layers.LayerSetup;
import com.sonymobile.generativeartwork.layers.LayerType;
import com.sonymobile.generativeartwork.render.OffscreenRenderer;
import com.sonymobile.generativeartwork.settings.LayerSettings;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class GenerativeArtWork implements GenerativeLayer.FBResultAccess {
    private static final int ALPHA_IDX = 3;
    private static final int BLUE_IDX = 2;
    public static final int BYTES_PER_PIXEL = 4;
    private static final int DEPTH_IDX = 4;
    private static final int GREEN_IDX = 1;
    public static final int MAX_HEIGHT = 1920;
    public static final int MAX_WIDTH = 1920;
    private static final int NUM_GL_PARAMS = 6;
    private static final int RED_IDX = 0;
    private static final int STENCIL_IDX = 5;
    private static final String TAG = "com.sonymobile.generativeartwork.GenerativeArtWork";
    public static final String VERSION = "1.0";
    private int mHeight;
    private int mWidth;
    private ArrayList<GenerativeLayer> mLayersList = new ArrayList<>();
    private Context mContext = null;
    private OffscreenRenderer mOfscreenRenderer = new OffscreenRenderer();
    private int[] mGLConfig = new int[6];
    private boolean mIsInitialized = false;
    private final Object mSyncObj = new Object();
    private final Object mInitSync = new Object();
    private final Object mReleaseSync = new Object();
    private Bitmap mResultingImg = null;
    private boolean mReleaseLibrary = false;
    private RenderThread mRenderThread = null;
    private boolean mRequestDone = true;
    private Request mCurrentRequest = Request.LAYER_ADDED;
    private GenerativeLayer mChangingLayer = null;
    private LayerSettings mLastSettings = null;

    public enum Request {
        RENDER,
        LAYER_ADDED,
        LAYER_REMOVED
    }

    public void initLibrary(Context context, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        if (i7 < 0 || i7 > 1920 || i8 < 0 || i8 > 1920) {
            throw new IllegalArgumentException("Wrong size parameters for library init");
        }
        if (this.mIsInitialized) {
            throw new IllegalStateException("The library is initialized already, you should release it first.");
        }
        this.mWidth = i7;
        this.mHeight = i8;
        this.mContext = context;
        int[] iArr = this.mGLConfig;
        iArr[0] = i;
        iArr[1] = i2;
        iArr[2] = i3;
        iArr[3] = i4;
        iArr[4] = i5;
        iArr[5] = i6;
        this.mReleaseLibrary = false;
        RenderThread renderThread = new RenderThread(this);
        this.mRenderThread = renderThread;
        renderThread.setPriority(10);
        this.mRenderThread.start();
        synchronized (this.mInitSync) {
            while (!this.mIsInitialized) {
                try {
                    this.mInitSync.wait();
                } catch (InterruptedException unused) {
                }
            }
        }
    }

    public void releaseLibrary() {
        synchronized (this.mSyncObj) {
            if (this.mIsInitialized) {
                this.mReleaseLibrary = true;
                this.mRequestDone = false;
                this.mResultingImg = null;
                this.mSyncObj.notifyAll();
                this.mIsInitialized = false;
            }
        }
    }

    public GenerativeLayer addLayer(LayerType layerType) {
        GenerativeLayer generativeLayer;
        if (layerType == null) {
            throw new IllegalArgumentException("LayerType is null!");
        }
        synchronized (this.mSyncObj) {
            generativeLayer = null;
            if (this.mIsInitialized && !this.mReleaseLibrary) {
                while (!this.mRequestDone) {
                    try {
                        this.mSyncObj.wait();
                    } catch (InterruptedException unused) {
                    }
                }
                this.mCurrentRequest = Request.LAYER_ADDED;
                GenerativeLayer generativeLayerBuildLayer = LayerFactory.buildLayer(layerType, this);
                if (generativeLayerBuildLayer != null) {
                    if (this.mLayersList.add(generativeLayerBuildLayer)) {
                        generativeLayerBuildLayer.setSize(this.mWidth, this.mHeight);
                        this.mChangingLayer = generativeLayerBuildLayer;
                    } else {
                        generativeLayerBuildLayer = null;
                    }
                }
                this.mRequestDone = false;
                this.mSyncObj.notifyAll();
                while (!this.mRequestDone) {
                    try {
                        this.mSyncObj.wait();
                    } catch (InterruptedException unused2) {
                    }
                }
                this.mChangingLayer = null;
                generativeLayer = generativeLayerBuildLayer;
            }
        }
        return generativeLayer;
    }

    public void removeLayer(GenerativeLayer generativeLayer) {
        if (generativeLayer == null) {
            throw new IllegalArgumentException("Layer is null!");
        }
        synchronized (this.mSyncObj) {
            if (this.mIsInitialized && !this.mReleaseLibrary) {
                while (!this.mRequestDone) {
                    try {
                        this.mSyncObj.wait();
                    } catch (InterruptedException unused) {
                    }
                }
                this.mCurrentRequest = Request.LAYER_REMOVED;
                int iIndexOf = this.mLayersList.indexOf(generativeLayer);
                if (iIndexOf != -1) {
                    this.mChangingLayer = this.mLayersList.remove(iIndexOf);
                    this.mRequestDone = false;
                    this.mSyncObj.notifyAll();
                    while (!this.mRequestDone) {
                        try {
                            this.mSyncObj.wait();
                        } catch (InterruptedException unused2) {
                        }
                    }
                    this.mChangingLayer = null;
                } else {
                    throw new IllegalArgumentException("Couldn't find such layer in the list");
                }
            }
        }
    }

    public void render(Bitmap bitmap) {
        if (bitmap == null) {
            throw new IllegalArgumentException("resultingImage is null!");
        }
        synchronized (this.mSyncObj) {
            if (this.mIsInitialized && !this.mReleaseLibrary) {
                while (!this.mRequestDone) {
                    try {
                        this.mSyncObj.wait();
                    } catch (InterruptedException unused) {
                    }
                }
                this.mCurrentRequest = Request.RENDER;
                this.mResultingImg = bitmap;
                this.mRequestDone = false;
                this.mSyncObj.notifyAll();
                while (!this.mRequestDone) {
                    try {
                        this.mSyncObj.wait();
                    } catch (InterruptedException unused2) {
                    }
                }
                this.mResultingImg = null;
            }
        }
    }

    public boolean checkSignature(Bitmap bitmap) {
        return this.mOfscreenRenderer.checkSignatureInfo(bitmap);
    }

    @Override // com.sonymobile.generativeartwork.layers.GenerativeLayer.FBResultAccess
    public int getFBResult() {
        return this.mOfscreenRenderer.getCurrentTexture();
    }

    public void setSettings(LayerSettings layerSettings) {
        if (layerSettings == null) {
            throw new IllegalArgumentException("settings is null!");
        }
        LayerSetup.setSettings(this.mLayersList, layerSettings);
        this.mLastSettings = layerSettings;
    }

    public LayerSettings getSettings() {
        return new LayerSettings(this.mLastSettings);
    }

    public Context getContext() {
        return this.mContext;
    }

    class RenderThread extends Thread {
        GenerativeArtWork mOwner;

        RenderThread(GenerativeArtWork generativeArtWork) {
            this.mOwner = generativeArtWork;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            GenerativeArtWork.this.mOfscreenRenderer.init(GenerativeArtWork.this.mGLConfig[0], GenerativeArtWork.this.mGLConfig[1], GenerativeArtWork.this.mGLConfig[2], GenerativeArtWork.this.mGLConfig[3], GenerativeArtWork.this.mGLConfig[4], GenerativeArtWork.this.mGLConfig[5], GenerativeArtWork.this.mWidth, GenerativeArtWork.this.mHeight);
            LayerFactory.initLayers(GenerativeArtWork.this.mLayersList, this.mOwner);
            synchronized (GenerativeArtWork.this.mInitSync) {
                GenerativeArtWork.this.mIsInitialized = true;
                GenerativeArtWork.this.mInitSync.notifyAll();
            }
            while (!GenerativeArtWork.this.mReleaseLibrary) {
                synchronized (GenerativeArtWork.this.mSyncObj) {
                    while (GenerativeArtWork.this.mRequestDone) {
                        try {
                            GenerativeArtWork.this.mSyncObj.wait();
                        } catch (InterruptedException unused) {
                        }
                    }
                }
                int i = AnonymousClass1.$SwitchMap$com$sonymobile$generativeartwork$GenerativeArtWork$Request[GenerativeArtWork.this.mCurrentRequest.ordinal()];
                if (i != 1) {
                    if (i != 2) {
                        if (i == 3 && GenerativeArtWork.this.mChangingLayer != null) {
                            LayerFactory.releaseLayer(GenerativeArtWork.this.mChangingLayer);
                        }
                    } else if (GenerativeArtWork.this.mChangingLayer != null) {
                        LayerFactory.initLayer(GenerativeArtWork.this.mChangingLayer, this.mOwner);
                    }
                } else if (GenerativeArtWork.this.mResultingImg != null) {
                    GenerativeArtWork.this.mOfscreenRenderer.render(GenerativeArtWork.this.mResultingImg, GenerativeArtWork.this.mLayersList, OffscreenRenderer.Mode.Stretch);
                }
                synchronized (GenerativeArtWork.this.mSyncObj) {
                    GenerativeArtWork.this.mRequestDone = true;
                    GenerativeArtWork.this.mSyncObj.notifyAll();
                }
            }
            LayerFactory.releaseLayers(GenerativeArtWork.this.mLayersList);
            GenerativeArtWork.this.mLayersList.clear();
            GenerativeArtWork.this.mOfscreenRenderer.release();
        }
    }

    /* JADX INFO: renamed from: com.sonymobile.generativeartwork.GenerativeArtWork$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$sonymobile$generativeartwork$GenerativeArtWork$Request;

        static {
            int[] iArr = new int[Request.values().length];
            $SwitchMap$com$sonymobile$generativeartwork$GenerativeArtWork$Request = iArr;
            try {
                iArr[Request.RENDER.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$GenerativeArtWork$Request[Request.LAYER_ADDED.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$GenerativeArtWork$Request[Request.LAYER_REMOVED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }
}
