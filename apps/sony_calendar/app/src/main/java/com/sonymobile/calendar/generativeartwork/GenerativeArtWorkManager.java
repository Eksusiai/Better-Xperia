package com.sonymobile.calendar.generativeartwork;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.content.ContextCompat;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.utils.PermissionUtils;
import com.sonymobile.generativeartwork.GenerativeArtWork;
import com.sonymobile.generativeartwork.helper.OutputSymbols;
import com.sonymobile.generativeartwork.helper.SymbolsUtils;
import com.sonymobile.generativeartwork.layers.ArtisticLayer;
import com.sonymobile.generativeartwork.layers.BackgroundLayer;
import com.sonymobile.generativeartwork.layers.LayerType;
import com.sonymobile.generativeartwork.settings.CirclePatterSetupW17_2;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class GenerativeArtWorkManager {
    private static final int COLUMN_PHOTO_ID = 0;
    private static final int COLUMN_PRESENCE = 1;
    private static final int MINIMUM_NAME_LENGTH = 1;
    private static final int PHOTO_COLUMN = 0;
    private static final String TAG = "GenerativeArtWorkManage";
    private static volatile GenerativeArtWorkManager sInstance;
    private ArtisticLayer mArtisticLayer;
    private int mAttendeePhotoHeight;
    private int mAttendeePhotoWidth;
    private BackgroundLayer mBackgroundLayer;
    private Context mContext;
    private Bitmap mDefaultEmptyBadge;
    private Map<String, OutputSymbols> mGawAttendeeMap;
    private GenerativeArtWork mGenerativeArtWork;
    private boolean mIsGawEnabled;
    private static final HashMap<String, Integer> mStoreColor = new HashMap<>();
    private static final HashMap<String, Bitmap> mBitmap = new HashMap<>();
    static final String[] PROJECTION_PHOTO_ID_PRESENCE = {"photo_id", "contact_presence"};
    static final String[] PHOTO_PROJECTION = {"data15"};
    private static final CursorGetter<byte[]> BLOB_GETTER = new CursorGetter() { // from class: com.sonymobile.calendar.generativeartwork.GenerativeArtWorkManager$$ExternalSyntheticLambda0
        @Override // com.sonymobile.calendar.generativeartwork.GenerativeArtWorkManager.CursorGetter
        public final Object get(Cursor cursor, int i) {
            return cursor.getBlob(i);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    interface CursorGetter<T> {
        T get(Cursor cursor, int i);
    }

    private GenerativeArtWorkManager(Context context) {
        this.mContext = context;
        this.mAttendeePhotoWidth = context.getResources().getDimensionPixelSize(R.dimen.attendee_gaw_width);
        this.mAttendeePhotoHeight = context.getResources().getDimensionPixelSize(R.dimen.attendee_gaw_height);
        boolean zIsSonyPhone = isSonyPhone();
        this.mIsGawEnabled = zIsSonyPhone;
        if (zIsSonyPhone) {
            initializeGaw();
        } else {
            initializeDefaultEmptyBadge();
        }
    }

    private boolean isSonyPhone() {
        for (String str : this.mContext.getPackageManager().getSystemSharedLibraryNames()) {
            if (str.equals("com.sony.device")) {
                return true;
            }
        }
        return false;
    }

    private void initializeGaw() {
        GenerativeArtWork generativeArtWork = new GenerativeArtWork();
        this.mGenerativeArtWork = generativeArtWork;
        generativeArtWork.initLibrary(this.mContext, 8, 8, 8, 8, 0, 0, this.mAttendeePhotoWidth, this.mAttendeePhotoHeight);
        this.mBackgroundLayer = (BackgroundLayer) this.mGenerativeArtWork.addLayer(LayerType.BACKGROUND);
        ArtisticLayer artisticLayer = (ArtisticLayer) this.mGenerativeArtWork.addLayer(LayerType.ARTISTIC);
        this.mArtisticLayer = artisticLayer;
        if (artisticLayer != null) {
            artisticLayer.registerColorChangeListener(this.mBackgroundLayer);
        }
        this.mGenerativeArtWork.setSettings(new CirclePatterSetupW17_2(this.mContext));
        this.mGawAttendeeMap = new HashMap();
    }

    private void initializeDefaultEmptyBadge() {
        Drawable drawable = ContextCompat.getDrawable(this.mContext, R.drawable.contact_badge);
        this.mDefaultEmptyBadge = Bitmap.createBitmap(this.mAttendeePhotoWidth, this.mAttendeePhotoHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.mDefaultEmptyBadge);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
    }

    private OutputSymbols getGawCharsForAttendee(String str, String str2) {
        String str3 = TextUtils.isEmpty(str2) ? str : str2;
        if (!this.mGawAttendeeMap.containsKey(str3)) {
            addOutputSymbolsToMap(str, str2, resolveGawChars(str, str2));
        }
        return this.mGawAttendeeMap.get(str3);
    }

    private void addOutputSymbolsToMap(String str, String str2, OutputSymbols outputSymbols) {
        if (!TextUtils.isEmpty(str2)) {
            this.mGawAttendeeMap.put(str2, outputSymbols);
        } else {
            if (TextUtils.isEmpty(str)) {
                return;
            }
            this.mGawAttendeeMap.put(str, outputSymbols);
        }
    }

    private OutputSymbols resolveGawChars(String str, String str2) {
        return SymbolsUtils.getSymbolsFromFields(str, null, str2);
    }

    private static <T> T getFirstRowColumn(Context context, Uri uri, String[] strArr, String str, String[] strArr2, String str2, int i, T t, CursorGetter<T> cursorGetter) {
        Cursor cursorQuery;
        if (context != null && (cursorQuery = context.getContentResolver().query(uri, strArr, str, strArr2, str2)) != null) {
            try {
                if (cursorQuery.moveToFirst()) {
                    return cursorGetter.get(cursorQuery, i);
                }
            } finally {
                cursorQuery.close();
            }
        }
        return t;
    }

    private static byte[] getFirstRowBlob(Context context, Uri uri, String[] strArr, String str, String[] strArr2, String str2, int i, byte[] bArr) {
        return (byte[]) getFirstRowColumn(context, uri, strArr, str, strArr2, str2, i, bArr, BLOB_GETTER);
    }

    private static Bitmap readContactsDb(Context context, String str) {
        byte[] firstRowBlob;
        Log.d(TAG, "readContactsDb start: " + str);
        if (!PermissionUtils.isReadContactsGranted(context)) {
            return null;
        }
        Cursor cursorQuery = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.CommonDataKinds.Email.CONTENT_LOOKUP_URI, Uri.encode(str)), PROJECTION_PHOTO_ID_PRESENCE, null, null, null);
        if (cursorQuery == null) {
            return null;
        }
        try {
            if (cursorQuery.moveToFirst()) {
                long j = cursorQuery.getLong(0);
                cursorQuery.getInt(1);
                cursorQuery.close();
                if (j == -1 || (firstRowBlob = getFirstRowBlob(context, ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, j), PHOTO_PROJECTION, null, null, null, 0, null)) == null) {
                    return null;
                }
                try {
                    return BitmapFactory.decodeByteArray(firstRowBlob, 0, firstRowBlob.length, null);
                } catch (OutOfMemoryError e) {
                    Log.e(TAG, "Decoding bitmap failed with " + e.getMessage());
                    return null;
                }
            }
            cursorQuery.close();
            return null;
        } catch (Throwable th) {
            cursorQuery.close();
            throw th;
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable, Context context) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(this.mAttendeePhotoWidth, this.mAttendeePhotoHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmapCreateBitmap;
    }

    public Bitmap renderGawPhoto(String str, String str2) {
        if (this.mIsGawEnabled) {
            if (!(str != null && str.length() >= 1)) {
                str = str2;
            }
            Bitmap contactsDb = readContactsDb(this.mContext, str2);
            if (contactsDb != null) {
                return contactsDb;
            }
            HashMap<String, Integer> map = mStoreColor;
            if (!map.containsKey(str)) {
                map.put(str, Integer.valueOf(((int) (Math.random() * 1.6777215E7d)) | (-16777216)));
            }
            HashMap<String, Bitmap> map2 = mBitmap;
            if (map2.get(str) != null) {
                return map2.get(str);
            }
            char cCharAt = str.charAt(0);
            LetterTileDrawable letterTileDrawable = new LetterTileDrawable(this.mContext);
            letterTileDrawable.setLetter(Character.valueOf(Character.toUpperCase(cCharAt)));
            letterTileDrawable.setColor(map.get(str).intValue());
            Bitmap bitmapDrawableToBitmap = drawableToBitmap(letterTileDrawable, this.mContext);
            map2.put(str, bitmapDrawableToBitmap);
            return bitmapDrawableToBitmap;
        }
        return this.mDefaultEmptyBadge;
    }

    public static GenerativeArtWorkManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (GenerativeArtWorkManager.class) {
                if (sInstance == null) {
                    sInstance = new GenerativeArtWorkManager(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }
}
