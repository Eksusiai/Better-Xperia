package com.sonymobile.gagtmhelper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes2.dex */
public class GaGtmUtils {
    private static final int GA_DEFAULT_DISPATCH_PERIOD = 1800;
    private static final String GTM_BUFFERED_EVENTS_FILENAME = "GTM_buffered_events";
    private static final int GTM_BUFFERED_EVENTS_VERSION = 0;
    private static final String GTM_KEY_DEVICE_BUILD_ID = "gagtm-deviceBuildId";
    private static final String GTM_KEY_DEVICE_BUILD_MODEL = "gagtm-deviceBuildModel";
    private static final String GTM_KEY_DEVICE_BUILD_TYPE = "gagtm-deviceBuildType";
    private static final String GTM_KEY_DEVICE_CUSTOMER_ID = "gagtm-deviceCustomerId";
    private static final String GTM_KEY_DEVICE_CUSTOMIZATION = "gagtm-deviceCustomization";
    private static final String GTM_KEY_DEVICE_CUSTOMIZATION_REVISION = "gagtm-deviceCustomizationRevision";
    private static final String GTM_KEY_DEVICE_NETWORK_MCC = "gagtm-deviceNetworkMcc";
    private static final String GTM_KEY_DEVICE_NETWORK_MNC = "gagtm-deviceNetworkMnc";
    private static final String GTM_KEY_DEVICE_SIM_MCC = "gagtm-deviceSimMcc";
    private static final String GTM_KEY_DEVICE_SIM_MNC = "gagtm-deviceSimMnc";
    private static final String GTM_KEY_EVENT = "event";
    private static final String GTM_KEY_EVENT_ACTION = "gagtm-eventAction";
    private static final String GTM_KEY_EVENT_CATEGORY = "gagtm-eventCategory";
    private static final String GTM_KEY_EVENT_LABEL = "gagtm-eventLabel";
    private static final String GTM_KEY_EVENT_VALUE = "gagtm-eventValue";
    private static final String GTM_KEY_EXCEPTION_DESCRIPTION = "gagtm-exceptionDescription";
    private static final String GTM_KEY_GA_DISPATCH_PERIOD = "gagtm-dispatchPeriod";
    private static final String GTM_KEY_GA_EXCEPTION_DEEP_MODE = "gagtm-exceptionDeepMode";
    private static final String GTM_KEY_GA_EXCEPTION_HASH_LIST = "gagtm-exceptionHashList";
    private static final String GTM_KEY_GA_EXCEPTION_MAX_REPORTED_ROWS = "gagtm-exceptionMaxReportedRows";
    private static final String GTM_KEY_GA_EXCEPTION_MAX_TRAVERSED_ROWS = "gagtm-exceptionMaxTraversedRows";
    private static final String GTM_KEY_GA_EXCEPTION_PACKAGE_NAMES = "gagtm-exceptionPackageNames";
    private static final String GTM_KEY_GA_FORCE_LOCAL_DISPATCH = "gagtm-forceLocalDispatch";
    private static final String GTM_KEY_SCREEN_NAME = "gagtm-screenName";
    private static final String GTM_KEY_TIMING_CATEGORY = "gagtm-timingCategory";
    private static final String GTM_KEY_TIMING_LABEL = "gagtm-timingLabel";
    private static final String GTM_KEY_TIMING_VALUE = "gagtm-timingValue";
    private static final String GTM_KEY_TIMING_VAR = "gagtm-timingVar";
    private static final String LOG_TAG = "GaGtmHelper";
    private static final int MAX_BUFFERED_PENDING_EVENTS = 50;
    private static final String SYSTEM_BUILD_CUSTOMER_ID = "ro.somc.customerid";
    private static final String SYSTEM_BUILD_CUSTOMIZATION = "ro.semc.version.cust";
    private static final String SYSTEM_BUILD_CUSTOMIZATION_REVISION = "ro.semc.version.cust_revision";
    private static volatile GaGtmUtils sInstance;
    private static final Object sLock = new Object();
    private Context mContext;
    private GaGtmSubscriber mGaGtmSubscriber;
    private TagManager mTagManager;
    private String mContainerId = null;
    private int mDefaultContainerResourceId = -1;
    private int mContainerLoadingTimeout = 2;
    private OnContainerLoadedListener mContainerLoadedCallback = null;
    private boolean mInitCalled = false;
    private boolean mContainerLoaded = false;
    private ContainerHolder mContainerHolder = null;
    private LinkedList<Map<String, Object>> mPendingEvents = new LinkedList<>();
    private Thread.UncaughtExceptionHandler mDefaultHandler = null;

    public interface OnContainerLoadedListener {
        void onContainerLoaded(boolean z);
    }

    public boolean init(Context context, String str, int i, boolean z, int i2, OnContainerLoadedListener onContainerLoadedListener) {
        synchronized (sLock) {
            if (this.mInitCalled) {
                if (GaGtmLog.isEnabled()) {
                    Log.d(LOG_TAG, "Ignoring call to init, already called.");
                }
                return false;
            }
            this.mInitCalled = true;
            if (GaGtmLog.isEnabled()) {
                Log.d(LOG_TAG, "setContainerId=" + str + " defaultContainerResourceId=" + i + " containerLoadingTimeout=" + i2);
                if (onContainerLoadedListener != null) {
                    Log.d(LOG_TAG, "callback" + onContainerLoadedListener.toString());
                }
            }
            this.mContext = context.getApplicationContext();
            this.mContainerId = str;
            this.mDefaultContainerResourceId = i;
            this.mContainerLoadingTimeout = i2;
            this.mContainerLoadedCallback = onContainerLoadedListener;
            this.mTagManager = TagManager.getInstance(context);
            ensureContainerLoadedLocked();
            if (z) {
                GaGtmSubscriber gaGtmSubscriber = new GaGtmSubscriber(context);
                this.mGaGtmSubscriber = gaGtmSubscriber;
                gaGtmSubscriber.subscribeGaSettingChanges();
            }
            return true;
        }
    }

    public boolean init(Context context, String str, int i, boolean z) {
        return init(context, str, i, z, this.mContainerLoadingTimeout, this.mContainerLoadedCallback);
    }

    private GaGtmUtils() {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "GaGtmUtils constructor");
        }
    }

    /* JADX WARN: Code duplicated, block: B:81:0x00eb A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:84:0x00e6 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:98:? A[Catch: all -> 0x00ef, SYNTHETIC, TRY_ENTER, TryCatch #7 {, blocks: (B:4:0x0003, B:6:0x0009, B:7:0x0010, B:9:0x0018, B:11:0x001e, B:12:0x0025, B:14:0x0027, B:16:0x002e, B:19:0x003d, B:21:0x0043, B:35:0x009b, B:37:0x00a0, B:59:0x00df, B:65:0x00e6, B:67:0x00eb, B:68:0x00ee, B:56:0x00d7, B:58:0x00dc), top: B:83:0x0003 }] */
    public void serializeQueueToFile() {
        String str;
        synchronized (sLock) {
            if (GaGtmLog.isEnabled()) {
                Log.d(LOG_TAG, "serializeQueueToFile");
            }
            if (this.mPendingEvents.size() == 0) {
                if (GaGtmLog.isEnabled()) {
                    Log.d(LOG_TAG, "No buffered events to serialize to file.");
                }
                return;
            }
            try {
                str = this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException unused) {
                str = "";
            }
            if (GaGtmLog.isEnabled()) {
                Log.d(LOG_TAG, "Current appVersion=" + str);
            }
            ObjectOutputStream objectOutputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = this.mContext.openFileOutput(GTM_BUFFERED_EVENTS_FILENAME, 0);
                objectOutputStream = new ObjectOutputStream(fileOutputStream);
                if (GaGtmLog.isEnabled()) {
                    Log.d(LOG_TAG, "Write version");
                }
                objectOutputStream.writeInt(0);
                if (GaGtmLog.isEnabled()) {
                    Log.d(LOG_TAG, "Write app version");
                }
                objectOutputStream.writeObject(str);
                if (GaGtmLog.isEnabled()) {
                    Log.d(LOG_TAG, "Write pending events to file");
                }
                objectOutputStream.writeObject(this.mPendingEvents);
            } catch (IOException e) {
                if (GaGtmLog.isEnabled()) {
                    Log.d(LOG_TAG, "IOException=" + e.getMessage());
                }
            } finally {
                if (objectOutputStream != null) {
                    try {
                        objectOutputStream.close();
                    } catch (IOException unused) {
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException unused2) {
                    }
                }
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:146:0x00ea A[EXC_TOP_SPLITTER, PHI: r1 r2
  0x00ea: PHI (r1v6 java.util.LinkedList) = 
  (r1v14 java.util.LinkedList)
  (r1v16 java.util.LinkedList)
  (r1v18 java.util.LinkedList)
  (r1v20 java.util.LinkedList)
  (r1v8 java.util.LinkedList)
 binds: [B:68:0x0138, B:77:0x0165, B:86:0x0192, B:95:0x01c0, B:38:0x00e8] A[DONT_GENERATE, DONT_INLINE]
  0x00ea: PHI (r2v15 ??) = (r2v11 ??), (r2v12 ??), (r2v13 ??), (r2v14 ??), (r2v23 ??) binds: [B:68:0x0138, B:77:0x0165, B:86:0x0192, B:95:0x01c0, B:38:0x00e8] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v21 */
    /* JADX WARN: Type inference failed for: r1v3 */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v5, types: [java.io.ObjectInputStream] */
    /* JADX WARN: Type inference failed for: r2v1 */
    /* JADX WARN: Type inference failed for: r2v10, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r2v11 */
    /* JADX WARN: Type inference failed for: r2v12 */
    /* JADX WARN: Type inference failed for: r2v13 */
    /* JADX WARN: Type inference failed for: r2v14 */
    /* JADX WARN: Type inference failed for: r2v15, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r2v2 */
    /* JADX WARN: Type inference failed for: r2v23, types: [java.io.FileInputStream, java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r2v28 */
    /* JADX WARN: Type inference failed for: r2v29 */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r2v30 */
    /* JADX WARN: Type inference failed for: r2v31 */
    /* JADX WARN: Type inference failed for: r2v32 */
    /* JADX WARN: Type inference failed for: r2v4 */
    /* JADX WARN: Type inference failed for: r2v5 */
    /* JADX WARN: Type inference failed for: r2v6 */
    /* JADX WARN: Type inference failed for: r2v7 */
    /* JADX WARN: Type inference failed for: r2v8 */
    /* JADX WARN: Type inference failed for: r2v9 */
    void deserializeQueueFromFileAndPush() throws Throwable {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "deserializeQueueFromFile");
        }
        if (new File(this.mContext.getFilesDir(), GTM_BUFFERED_EVENTS_FILENAME).length() == 0) {
            if (GaGtmLog.isEnabled()) {
                Log.d(LOG_TAG, "File is empty skip");
            }
            return;
        }
        String str;
        try {
            str = this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException unused) {
            str = "";
        }
        if (GaGtmLog.isEnabled()) {
            String str2 = "Current appVersion=" + str;
            Log.d(LOG_TAG, str2);
        }
        LinkedList linkedList = null;
        try {
            FileInputStream fileInputStream = this.mContext.openFileInput(GTM_BUFFERED_EVENTS_FILENAME);
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                try {
                    int i = objectInputStream.readInt();
                    if (GaGtmLog.isEnabled()) {
                        Log.d(LOG_TAG, "Read version=" + i);
                    }
                    if (i == 0) {
                        String str3 = (String) objectInputStream.readObject();
                        if (GaGtmLog.isEnabled()) {
                            Log.d(LOG_TAG, "Read bufferedAppVersion=" + str3);
                        }
                        if (str.contentEquals(str3)) {
                            linkedList = (LinkedList) objectInputStream.readObject();
                        } else if (GaGtmLog.isEnabled()) {
                            Log.d(LOG_TAG, "Bad app version. Skip");
                        }
                    } else if (GaGtmLog.isEnabled()) {
                        Log.d(LOG_TAG, "Bad format version. Skip");
                    }
                    objectInputStream.close();
                } catch (FileNotFoundException e) {
                    if (GaGtmLog.isEnabled()) {
                        Log.d(LOG_TAG, "FileNotFoundException=" + e.getMessage());
                    }
                } catch (OptionalDataException e2) {
                    if (GaGtmLog.isEnabled()) {
                        Log.d(LOG_TAG, "OptionalDataException=" + e2.getMessage());
                    }
                } catch (IOException e3) {
                    if (GaGtmLog.isEnabled()) {
                        Log.d(LOG_TAG, "IOException=" + e3.getMessage());
                    }
                } catch (ClassNotFoundException e4) {
                    if (GaGtmLog.isEnabled()) {
                        Log.d(LOG_TAG, "ClassNotFoundException=" + e4.getMessage());
                    }
                } finally {
                    try {
                        fileInputStream.close();
                    } catch (IOException unused2) {
                    }
                }
            } catch (Throwable th) {
                try {
                    fileInputStream.close();
                } catch (IOException unused3) {
                }
                throw th;
            }
        } catch (IOException e5) {
            if (GaGtmLog.isEnabled()) {
                Log.d(LOG_TAG, "IOException=" + e5.getMessage());
            }
        }
        boolean zDeleteFile = this.mContext.deleteFile(GTM_BUFFERED_EVENTS_FILENAME);
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "Buffer file deleted=" + zDeleteFile);
        }
        if (linkedList == null) {
            return;
        }
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "Push buffered events");
        }
        DataLayer dataLayer = this.mTagManager.getDataLayer();
        synchronized (sLock) {
            while (true) {
                Map<String, Object> map = (Map) linkedList.poll();
                if (map != null) {
                    if (GaGtmLog.isEnabled()) {
                        Log.d(LOG_TAG, "item=" + map.toString());
                    }
                    dataLayer.push(map);
                }
            }
        }
    }

    public static GaGtmUtils getInstance() {
        if (sInstance == null) {
            synchronized (GaGtmUtils.class) {
                if (sInstance == null) {
                    sInstance = new GaGtmUtils();
                }
            }
        }
        return sInstance;
    }

    private void ensureContainerLoadedLocked() {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "ensureContainerLoaded");
        }
        this.mTagManager.loadContainerPreferFresh(this.mContainerId, this.mDefaultContainerResourceId).setResultCallback(new ResultCallback<ContainerHolder>() { // from class: com.sonymobile.gagtmhelper.GaGtmUtils.1
            /* JADX WARN: Code duplicated, block: B:23:0x007d  */
            /* JADX WARN: Code duplicated, block: B:25:0x0083  */
            /* JADX WARN: Code duplicated, block: B:28:? A[RETURN, SYNTHETIC] */
            @Override // com.google.android.gms.common.api.ResultCallback
            public void onResult(ContainerHolder containerHolder) {
                boolean z = false;
                if (GaGtmLog.isEnabled()) {
                    Log.d(GaGtmUtils.LOG_TAG, "onResult:");
                }
                if (containerHolder != null) {
                    if (containerHolder.getStatus().isSuccess()) {
                        z = true;
                        GaGtmUtils.this.mContainerHolder = containerHolder;
                        Container container = containerHolder.getContainer();
                        if (container != null && GaGtmLog.isEnabled()) {
                            Log.d(GaGtmUtils.LOG_TAG, "container is default = " + container.isDefault());
                        }
                        GaGtmUtils.this.pushInitDefaultsToDataLayer();
                        GaGtmUtils.this.setContainerDefaults();
                        new Thread(new Runnable() { // from class: com.sonymobile.gagtmhelper.GaGtmUtils.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                synchronized (GaGtmUtils.sLock) {
                                    GaGtmUtils.this.mContainerLoaded = true;
                                    try {
                                        GaGtmUtils.this.deserializeQueueFromFileAndPush();
                                    } catch (Throwable th) {
                                        if (GaGtmLog.isEnabled()) {
                                            Log.d(GaGtmUtils.LOG_TAG, "Error pushing buffered events: " + th.getMessage());
                                        }
                                    }
                                    GaGtmUtils.this.flushDataLayerQueueLocked();
                                }
                            }
                        }).start();
                    } else if (GaGtmLog.isEnabled()) {
                        Log.d(GaGtmUtils.LOG_TAG, "Error loading container");
                    }
                    if (GaGtmUtils.this.mContainerLoadedCallback != null) {
                        if (GaGtmLog.isEnabled()) {
                            Log.d(GaGtmUtils.LOG_TAG, "Calling callback");
                        }
                        GaGtmUtils.this.mContainerLoadedCallback.onContainerLoaded(z);
                    }
                }
                if (GaGtmLog.isEnabled()) {
                    Log.d(GaGtmUtils.LOG_TAG, "containerHolder was null");
                }
                z = false;
                if (GaGtmUtils.this.mContainerLoadedCallback != null) {
                    if (GaGtmLog.isEnabled()) {
                        Log.d(GaGtmUtils.LOG_TAG, "Calling callback");
                    }
                    GaGtmUtils.this.mContainerLoadedCallback.onContainerLoaded(z);
                }
            }
        }, this.mContainerLoadingTimeout, TimeUnit.SECONDS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void flushDataLayerQueueLocked() {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "flushDataLayerQueueLocked");
        }
        DataLayer dataLayer = this.mTagManager.getDataLayer();
        while (true) {
            Map<String, Object> mapPoll = this.mPendingEvents.poll();
            if (mapPoll == null) {
                return;
            }
            if (GaGtmLog.isEnabled()) {
                Log.d(LOG_TAG, "item=" + mapPoll.toString());
            }
            dataLayer.push(mapPoll);
        }
    }

    public ContainerHolder getContainerHolder() {
        ContainerHolder containerHolder;
        synchronized (sLock) {
            containerHolder = this.mContainerHolder;
        }
        return containerHolder;
    }

    public void pushAppView(String str) {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "pushAppView screenName=" + str);
        }
        pushToDataLayer(DataLayer.mapOf("event", "appView", GTM_KEY_SCREEN_NAME, str));
    }

    public void pushEvent(String str, String str2, String str3, long j) {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "pushEvent category=" + str + " action=" + str2 + " label=" + str3 + " value=" + j);
        }
        pushToDataLayer(DataLayer.mapOf("event", "event", GTM_KEY_EVENT_CATEGORY, str, GTM_KEY_EVENT_ACTION, str2, GTM_KEY_EVENT_LABEL, str3, GTM_KEY_EVENT_VALUE, String.valueOf(j)));
    }

    public void pushTiming(String str, long j, String str2, String str3) {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "pushTiming category=" + str + " value=" + String.valueOf(j) + " var=" + str2 + " label=" + str3);
        }
        pushToDataLayer(DataLayer.mapOf("event", "timing", GTM_KEY_TIMING_CATEGORY, str, GTM_KEY_TIMING_VALUE, String.valueOf(j), GTM_KEY_TIMING_VAR, str2, GTM_KEY_TIMING_LABEL, str3));
    }

    public void pushStartSession() {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "pushStartSession");
        }
        pushToDataLayer(DataLayer.mapOf("event", "startSession"));
    }

    public void pushEndSession() {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "pushEndSession");
        }
        pushToDataLayer(DataLayer.mapOf("event", "endSession"));
    }

    public void pushDeepEvent(String str, String str2, String str3, long j) {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "pushDeepEvent category=" + str + " action=" + str2 + " label=" + str3 + " value=" + String.valueOf(j));
        }
        pushToDataLayer(DataLayer.mapOf("event", "deepEvent", GTM_KEY_EVENT_CATEGORY, str, GTM_KEY_EVENT_ACTION, str2, GTM_KEY_EVENT_LABEL, str3, GTM_KEY_EVENT_VALUE, String.valueOf(j)));
    }

    public void pushException(String str) {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "pushException exceptionDescription=" + str);
        }
        pushToDataLayer(DataLayer.mapOf("event", "exception", GTM_KEY_EXCEPTION_DESCRIPTION, str));
    }

    public void pushDeepException(String str) {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "pushDeepException exceptionDescription=" + str);
        }
        pushToDataLayer(DataLayer.mapOf("event", "deepException", GTM_KEY_EXCEPTION_DESCRIPTION, str));
    }

    public void pushToDataLayer(Map<String, Object> map) {
        synchronized (sLock) {
            if (this.mContainerLoaded) {
                this.mTagManager.getDataLayer().push(map);
            } else {
                if (GaGtmLog.isEnabled()) {
                    Log.d(LOG_TAG, "Container is NOT loaded, add to queue");
                }
                if (this.mPendingEvents.size() < 50) {
                    this.mPendingEvents.add(map);
                } else if (GaGtmLog.isEnabled()) {
                    Log.w(LOG_TAG, "Max pending events reached. Dropping event " + map);
                }
            }
        }
    }

    public synchronized void pushInitDefaultsToDataLayer() {
        String str = "";
        String str2 = "";
        String str3 = "";
        String strSubstring = "";
        String strSubstring2 = "";
        String strSubstring3 = "";
        String strSubstring4 = "";
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "pushInitDefaultsToDataLayer");
        }
        try {
            str = SystemPropertiesProxy.get(this.mContext, SYSTEM_BUILD_CUSTOMIZATION);
            str2 = SystemPropertiesProxy.get(this.mContext, SYSTEM_BUILD_CUSTOMIZATION_REVISION);
            str3 = SystemPropertiesProxy.get(this.mContext, SYSTEM_BUILD_CUSTOMER_ID);
        } catch (Exception e) {
            if (GaGtmLog.isEnabled()) {
                Log.d(LOG_TAG, "SystemProperty exception:" + e.getMessage());
            }
        }
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        try {
            if (telephonyManager.getSimState() == 5) {
                String simOperator = telephonyManager.getSimOperator();
                if (simOperator.length() == 5 || simOperator.length() == 6) {
                    strSubstring2 = simOperator.substring(0, 3);
                    strSubstring = simOperator.substring(3);
                }
            } else if (GaGtmLog.isEnabled()) {
                Log.d(LOG_TAG, "SIM state is not ready");
            }
        } catch (Exception e2) {
            if (GaGtmLog.isEnabled()) {
                Log.d(LOG_TAG, "Unexpected exception reading SIM info:" + e2.getMessage());
            }
        }
        try {
            String networkOperator = telephonyManager.getNetworkOperator();
            if (networkOperator.length() == 5 || networkOperator.length() == 6) {
                strSubstring4 = networkOperator.substring(0, 3);
                strSubstring3 = networkOperator.substring(3);
            }
        } catch (Exception e3) {
            if (GaGtmLog.isEnabled()) {
                Log.d(LOG_TAG, "Unexpected exception reading network info:" + e3.getMessage());
            }
        }
        String str4 = Build.ID + " " + Build.VERSION.INCREMENTAL;
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "Pushing to data layer deviceBuildModel:" + Build.MODEL + ", deviceBuildId: " + str4 + ", deviceBuildType:" + Build.TYPE + ", deviceCustomization:" + str + ", deviceCustomizationRevision:" + str2 + ", deviceCustomerId: " + str3 + ", deviceSimMcc: " + strSubstring2 + ", deviceSimMnc: " + strSubstring + ", deviceNetworkMcc: " + strSubstring4 + ", deviceNetworkMnc: " + strSubstring3);
        }
        pushToDataLayer(DataLayer.mapOf(GTM_KEY_DEVICE_BUILD_MODEL, Build.MODEL, GTM_KEY_DEVICE_BUILD_ID, str4, GTM_KEY_DEVICE_BUILD_TYPE, Build.TYPE, GTM_KEY_DEVICE_CUSTOMIZATION, str, GTM_KEY_DEVICE_CUSTOMIZATION_REVISION, str2, GTM_KEY_DEVICE_CUSTOMER_ID, str3, GTM_KEY_DEVICE_SIM_MCC, strSubstring2, GTM_KEY_DEVICE_SIM_MNC, strSubstring, GTM_KEY_DEVICE_NETWORK_MCC, strSubstring4, GTM_KEY_DEVICE_NETWORK_MNC, strSubstring3));
    }

    public void setContainerDefaults() {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "setContainerDefaults");
        }
        ContainerHolder containerHolder = this.mContainerHolder;
        if (containerHolder == null) {
            if (GaGtmLog.isEnabled()) {
                Log.d(LOG_TAG, "container holder is null exiting");
                return;
            }
            return;
        }
        Container container = containerHolder.getContainer();
        if (container == null) {
            if (GaGtmLog.isEnabled()) {
                Log.d(LOG_TAG, "container is null exiting");
                return;
            }
            return;
        }
        int i = (int) container.getLong(GTM_KEY_GA_DISPATCH_PERIOD);
        if (i <= 0) {
            i = GA_DEFAULT_DISPATCH_PERIOD;
        }
        GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(this.mContext);
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "gaDispatchPeriod:" + i);
        }
        googleAnalytics.setLocalDispatchPeriod(i);
        boolean z = container.getLong(GTM_KEY_GA_FORCE_LOCAL_DISPATCH) == 1;
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "gaForceLocalDispatch:" + z);
        }
        if (z && GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "gaForceLocalDispatch will be ignored, consider removing it from your container");
        }
        GaGtmExceptionParser.setMaxReportedRows((int) container.getLong(GTM_KEY_GA_EXCEPTION_MAX_REPORTED_ROWS));
        GaGtmExceptionParser.setMaxTraversedRows((int) container.getLong(GTM_KEY_GA_EXCEPTION_MAX_TRAVERSED_ROWS));
        GaGtmExceptionParser.setEnabledPackageNames(container.getString(GTM_KEY_GA_EXCEPTION_PACKAGE_NAMES));
        GaGtmExceptionParser.setDeepCrashHashList(container.getString(GTM_KEY_GA_EXCEPTION_HASH_LIST));
        GaGtmExceptionParser.setDeepMode(container.getString(GTM_KEY_GA_EXCEPTION_DEEP_MODE));
    }
}
