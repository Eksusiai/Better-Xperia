package com.sonymobile.calendar.provider.database;

/* JADX INFO: loaded from: classes2.dex */
final class DatabaseConstants {
    static final String CREATE_INDEX_EVENT_ID = "CREATE INDEX index_event_id ON event_comments(event_id)";
    static final String CREATE_TABLE_EVENT_COMMENTS = "CREATE TABLE event_comments (_id INTEGER PRIMARY KEY, event_id INTEGER NOT NULL, event_comment TEXT, event_dirty INTEGER )";
    static final String DATABASE_NAME = "SomcCalendarProvider.db";
    static final int DATABASE_VERSION = 1;
    private static final String INDEX_EVENT_ID = "index_event_id";

    private DatabaseConstants() {
    }
}
