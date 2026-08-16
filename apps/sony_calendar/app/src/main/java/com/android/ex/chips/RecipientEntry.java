package com.android.ex.chips;

import android.net.Uri;
import android.text.util.Rfc822Token;
import android.text.util.Rfc822Tokenizer;

/* JADX INFO: loaded from: classes.dex */
public class RecipientEntry {
    public static final int ENTRY_TYPE_PERSON = 0;
    public static final int ENTRY_TYPE_SIZE = 1;
    static final int GENERATED_CONTACT = -2;
    static final int INVALID_CONTACT = -1;
    public static final int INVALID_DESTINATION_TYPE = -1;
    private final long mContactId;
    private final long mDataId;
    private final String mDestination;
    private final String mDestinationLabel;
    private final int mDestinationType;
    private final Long mDirectoryId;
    private final String mDisplayName;
    private final int mEntryType;
    private boolean mIsFirstLevel;
    private boolean mIsValid;
    private final String mLookupKey;
    private final Uri mPhotoThumbnailUri;
    private byte[] mPhotoBytes = null;
    private final boolean mIsDivider = false;

    public static boolean isCreatedRecipient(long j) {
        return j == -1 || j == -2;
    }

    private static String pickDisplayName(int i, String str, String str2) {
        return i > 20 ? str : str2;
    }

    protected RecipientEntry(int i, String str, String str2, int i2, String str3, long j, Long l, long j2, Uri uri, boolean z, boolean z2, String str4) {
        this.mEntryType = i;
        this.mIsFirstLevel = z;
        this.mDisplayName = str;
        this.mDestination = str2;
        this.mDestinationType = i2;
        this.mDestinationLabel = str3;
        this.mContactId = j;
        this.mDirectoryId = l;
        this.mDataId = j2;
        this.mPhotoThumbnailUri = uri;
        this.mIsValid = z2;
        this.mLookupKey = str4;
    }

    public boolean isValid() {
        return this.mIsValid;
    }

    public static RecipientEntry constructFakeEntry(String str, boolean z) {
        Rfc822Token[] rfc822TokenArr = Rfc822Tokenizer.tokenize(str);
        String address = rfc822TokenArr.length > 0 ? rfc822TokenArr[0].getAddress() : str;
        return new RecipientEntry(0, address, address, -1, null, -1L, null, -1L, null, true, z, null);
    }

    public static RecipientEntry constructFakePhoneEntry(String str, boolean z) {
        return new RecipientEntry(0, str, str, -1, null, -1L, null, -1L, null, true, z, null);
    }

    public static RecipientEntry constructGeneratedEntry(String str, String str2, boolean z) {
        return new RecipientEntry(0, str, str2, -1, null, -2L, null, -2L, null, true, z, null);
    }

    public static RecipientEntry constructTopLevelEntry(String str, int i, String str2, int i2, String str3, long j, Long l, long j2, Uri uri, boolean z, String str4) {
        return new RecipientEntry(0, pickDisplayName(i, str, str2), str2, i2, str3, j, l, j2, uri, true, z, str4);
    }

    public static RecipientEntry constructTopLevelEntry(String str, int i, String str2, int i2, String str3, long j, Long l, long j2, String str4, boolean z, String str5) {
        return new RecipientEntry(0, pickDisplayName(i, str, str2), str2, i2, str3, j, l, j2, str4 != null ? Uri.parse(str4) : null, true, z, str5);
    }

    public static RecipientEntry constructSecondLevelEntry(String str, int i, String str2, int i2, String str3, long j, Long l, long j2, String str4, boolean z, String str5) {
        return new RecipientEntry(0, pickDisplayName(i, str, str2), str2, i2, str3, j, l, j2, str4 != null ? Uri.parse(str4) : null, false, z, str5);
    }

    public int getEntryType() {
        return this.mEntryType;
    }

    public String getDisplayName() {
        return this.mDisplayName;
    }

    public String getDestination() {
        return this.mDestination;
    }

    public int getDestinationType() {
        return this.mDestinationType;
    }

    public String getDestinationLabel() {
        return this.mDestinationLabel;
    }

    public long getContactId() {
        return this.mContactId;
    }

    public Long getDirectoryId() {
        return this.mDirectoryId;
    }

    public long getDataId() {
        return this.mDataId;
    }

    public boolean isFirstLevel() {
        return this.mIsFirstLevel;
    }

    public Uri getPhotoThumbnailUri() {
        return this.mPhotoThumbnailUri;
    }

    public synchronized void setPhotoBytes(byte[] bArr) {
        this.mPhotoBytes = bArr;
    }

    public synchronized byte[] getPhotoBytes() {
        return this.mPhotoBytes;
    }

    public boolean isSeparator() {
        return this.mIsDivider;
    }

    public boolean isSelectable() {
        return this.mEntryType == 0;
    }

    public String getLookupKey() {
        return this.mLookupKey;
    }

    public String toString() {
        return this.mDisplayName + " <" + this.mDestination + ">, isValid=" + this.mIsValid;
    }

    public boolean isSamePerson(RecipientEntry recipientEntry) {
        return recipientEntry != null && this.mContactId == recipientEntry.mContactId;
    }
}
