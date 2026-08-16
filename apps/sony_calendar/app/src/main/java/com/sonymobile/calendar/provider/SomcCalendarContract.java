package com.sonymobile.calendar.provider;

import android.net.Uri;
import android.provider.BaseColumns;
import java.util.HashMap;

/* JADX INFO: loaded from: classes2.dex */
public final class SomcCalendarContract {
    public static final String AUTHORITY = "com.sonymobile.calendar.provider";
    private static final Uri AUTHORITY_URI = Uri.parse("content://com.sonymobile.calendar.provider");
    private static final String ITEM_TYPE_BASE = "vnd.android.cursor.item/";

    private SomcCalendarContract() {
    }

    public static final class EventComments implements BaseColumns {
        public static final String COLUMN_EVENT_COMMENT = "event_comment";
        public static final String COLUMN_EVENT_DIRTY = "event_dirty";
        public static final String COLUMN_EVENT_ID = "event_id";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/event_comment";
        public static final String CONTENT_TYPE = "vnd.android.cursor.item/event_comment";
        public static final Uri CONTENT_URI;
        public static final String ID_PATH = "event_comments/#";
        public static final String PATH = "event_comments";
        public static final HashMap<String, String> PROJECTION;
        public static final String TABLE_EVENT_COMMENTS = "event_comments";

        private EventComments() {
        }

        static {
            HashMap<String, String> map = new HashMap<>();
            PROJECTION = map;
            map.put("_id", "_id");
            map.put("event_id", "event_id");
            map.put(COLUMN_EVENT_COMMENT, COLUMN_EVENT_COMMENT);
            map.put(COLUMN_EVENT_DIRTY, COLUMN_EVENT_DIRTY);
            CONTENT_URI = Uri.withAppendedPath(SomcCalendarContract.AUTHORITY_URI, "event_comments");
        }
    }
}
