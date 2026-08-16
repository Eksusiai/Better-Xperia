package com.android.ex.chips;

import android.content.res.Resources;
import android.net.Uri;
import android.provider.ContactsContract;

/* JADX INFO: loaded from: classes.dex */
class Queries {
    public static final Query PHONE = new Query(new String[]{"display_name", "data1", "data2", "data3", "contact_id", "_id", "photo_thumb_uri", "display_name_source", "lookup", "mimetype"}, ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, ContactsContract.CommonDataKinds.Phone.CONTENT_URI) { // from class: com.android.ex.chips.Queries.1
        @Override // com.android.ex.chips.Queries.Query
        public CharSequence getTypeLabel(Resources resources, int i, CharSequence charSequence) {
            return ContactsContract.CommonDataKinds.Phone.getTypeLabel(resources, i, charSequence);
        }
    };
    public static final Query EMAIL = new Query(new String[]{"display_name", "data1", "data2", "data3", "contact_id", "_id", "photo_thumb_uri", "display_name_source", "lookup", "mimetype"}, ContactsContract.CommonDataKinds.Email.CONTENT_FILTER_URI, ContactsContract.CommonDataKinds.Email.CONTENT_URI) { // from class: com.android.ex.chips.Queries.2
        @Override // com.android.ex.chips.Queries.Query
        public CharSequence getTypeLabel(Resources resources, int i, CharSequence charSequence) {
            return ContactsContract.CommonDataKinds.Email.getTypeLabel(resources, i, charSequence);
        }
    };

    Queries() {
    }

    static abstract class Query {
        public static final int CONTACT_ID = 4;
        public static final int DATA_ID = 5;
        public static final int DESTINATION = 1;
        public static final int DESTINATION_LABEL = 3;
        public static final int DESTINATION_TYPE = 2;
        public static final int DISPLAY_NAME_SOURCE = 7;
        public static final int LOOKUP_KEY = 8;
        public static final int MIME_TYPE = 9;
        public static final int NAME = 0;
        public static final int PHOTO_THUMBNAIL_URI = 6;
        private final Uri mContentFilterUri;
        private final Uri mContentUri;
        private final String[] mProjection;

        public abstract CharSequence getTypeLabel(Resources resources, int i, CharSequence charSequence);

        public Query(String[] strArr, Uri uri, Uri uri2) {
            this.mProjection = strArr;
            this.mContentFilterUri = uri;
            this.mContentUri = uri2;
        }

        public String[] getProjection() {
            return this.mProjection;
        }

        public Uri getContentFilterUri() {
            return this.mContentFilterUri;
        }

        public Uri getContentUri() {
            return this.mContentUri;
        }
    }
}
