package com.sonymobile.provider;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Entity;
import android.content.EntityIterator;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.BaseColumns;

/* JADX INFO: loaded from: classes2.dex */
public final class TasksEntity implements BaseColumns, TasksContract.SyncColumns, TasksContract.TasksColumns {
    public static final Uri CONTENT_URI = Uri.parse("content://com.sonymobile.tasks.provider/task_entities");

    private TasksEntity() {
    }

    public static EntityIterator newEntityIterator(Cursor cursor, ContentResolver contentResolver) {
        return new EntityIteratorImpl(cursor, contentResolver);
    }

    public static EntityIterator newEntityIterator(Cursor cursor, ContentProviderClient contentProviderClient) {
        return new EntityIteratorImpl(cursor, contentProviderClient);
    }

    private static class EntityIteratorImpl extends CursorEntityIterator {
        private static final int COLUMN_ID = 0;
        private static final int COLUMN_METHOD = 2;
        private static final int COLUMN_NAME = 1;
        private static final int COLUMN_REMINDER_TIME = 0;
        private static final int COLUMN_UTC_REMINDER_TIME = 1;
        private static final int COLUMN_VALUE = 2;
        private static final String WHERE_TASK_ID = "task_id=?";
        private final ContentProviderClient mProvider;
        private final ContentResolver mResolver;
        private static final String[] REMINDERS_PROJECTION = {TasksContract.RemindersColumns.REMINDER_TIME, TasksContract.RemindersColumns.UTC_REMINDER_TIME, "method"};
        private static final String[] EXTENDED_PROJECTION = {"_id", "name", "value"};

        public EntityIteratorImpl(Cursor cursor, ContentResolver contentResolver) {
            super(cursor);
            this.mResolver = contentResolver;
            this.mProvider = null;
        }

        public EntityIteratorImpl(Cursor cursor, ContentProviderClient contentProviderClient) {
            super(cursor);
            this.mResolver = null;
            this.mProvider = contentProviderClient;
        }

        @Override // com.sonymobile.provider.TasksEntity.CursorEntityIterator
        public Entity getEntityAndIncrementCursor(Cursor cursor) throws RemoteException {
            Cursor cursorQuery;
            Cursor cursorQuery2;
            long j = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", Long.valueOf(j));
            DatabaseUtils.cursorIntToContentValuesIfPresent(cursor, contentValues, TasksContract.TasksColumns.TASKLIST_ID);
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, "subject");
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, TasksContract.TasksColumns.BODY_DATA);
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, TasksContract.TasksColumns.BODY_TYPE);
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, TasksContract.TasksColumns.CATEGORIES);
            DatabaseUtils.cursorIntToContentValuesIfPresent(cursor, contentValues, "complete");
            DatabaseUtils.cursorIntToContentValuesIfPresent(cursor, contentValues, TasksContract.TasksColumns.IMPORTANCE);
            DatabaseUtils.cursorIntToContentValuesIfPresent(cursor, contentValues, TasksContract.TasksColumns.SENSITIVITY);
            DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, contentValues, TasksContract.TasksColumns.ORDINAL_DATE);
            DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, contentValues, TasksContract.TasksColumns.SUB_ORDINAL_DATE);
            DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, contentValues, TasksContract.TasksColumns.DATE_COMPLETED);
            DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, contentValues, TasksContract.TasksColumns.START_DATE);
            DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, contentValues, TasksContract.TasksColumns.UTC_START_DATE);
            DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, contentValues, TasksContract.TasksColumns.DUE_DATE);
            DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, contentValues, TasksContract.TasksColumns.UTC_DUE_DATE);
            DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, contentValues, TasksContract.TasksColumns.HAS_REMINDER);
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, "rrule");
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, "rdate");
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, "exrule");
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, "exdate");
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, "original_sync_id");
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, "original_id");
            DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, contentValues, "originalInstanceTime");
            DatabaseUtils.cursorIntToContentValuesIfPresent(cursor, contentValues, "originalAllDay");
            DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, contentValues, "lastDate");
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, "_sync_id");
            DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, contentValues, "dirty");
            DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, contentValues, "lastSynced");
            DatabaseUtils.cursorIntToContentValuesIfPresent(cursor, contentValues, "deleted");
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, "sync_data1");
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, "sync_data2");
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, "sync_data3");
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, "sync_data4");
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, "sync_data5");
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, "sync_data6");
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, "sync_data7");
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, "sync_data8");
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, "sync_data9");
            DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, contentValues, "sync_data10");
            Entity entity = new Entity(contentValues);
            ContentResolver contentResolver = this.mResolver;
            if (contentResolver != null) {
                cursorQuery = contentResolver.query(TasksContract.Reminders.CONTENT_URI, REMINDERS_PROJECTION, WHERE_TASK_ID, new String[]{Long.toString(j)}, null);
            } else {
                cursorQuery = this.mProvider.query(TasksContract.Reminders.CONTENT_URI, REMINDERS_PROJECTION, WHERE_TASK_ID, new String[]{Long.toString(j)}, null);
            }
            while (cursorQuery.moveToNext()) {
                try {
                    ContentValues contentValues2 = new ContentValues();
                    contentValues2.put(TasksContract.RemindersColumns.REMINDER_TIME, Long.valueOf(cursorQuery.getLong(0)));
                    contentValues2.put(TasksContract.RemindersColumns.UTC_REMINDER_TIME, Long.valueOf(cursorQuery.getLong(1)));
                    contentValues2.put("method", Integer.valueOf(cursorQuery.getInt(2)));
                    entity.addSubValue(TasksContract.Reminders.CONTENT_URI, contentValues2);
                } catch (Throwable th) {
                    cursorQuery.close();
                    throw th;
                }
            }
            cursorQuery.close();
            ContentResolver contentResolver2 = this.mResolver;
            if (contentResolver2 != null) {
                cursorQuery2 = contentResolver2.query(TasksContract.ExtendedProperties.CONTENT_URI, EXTENDED_PROJECTION, WHERE_TASK_ID, new String[]{Long.toString(j)}, null);
            } else {
                cursorQuery2 = this.mProvider.query(TasksContract.ExtendedProperties.CONTENT_URI, EXTENDED_PROJECTION, WHERE_TASK_ID, new String[]{Long.toString(j)}, null);
            }
            while (cursorQuery2.moveToNext()) {
                try {
                    ContentValues contentValues3 = new ContentValues();
                    contentValues3.put("_id", cursorQuery2.getString(0));
                    contentValues3.put("name", cursorQuery2.getString(1));
                    contentValues3.put("value", cursorQuery2.getString(2));
                    entity.addSubValue(TasksContract.ExtendedProperties.CONTENT_URI, contentValues3);
                } catch (Throwable th2) {
                    cursorQuery2.close();
                    throw th2;
                }
            }
            cursorQuery2.close();
            cursor.moveToNext();
            return entity;
        }
    }

    private static abstract class CursorEntityIterator implements EntityIterator {
        private final Cursor mCursor;
        private boolean mIsClosed = false;

        public abstract Entity getEntityAndIncrementCursor(Cursor cursor) throws RemoteException;

        public CursorEntityIterator(Cursor cursor) {
            this.mCursor = cursor;
            cursor.moveToFirst();
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            if (this.mIsClosed) {
                throw new IllegalStateException("calling hasNext() when the iterator is closed");
            }
            return !this.mCursor.isAfterLast();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Entity next() {
            if (this.mIsClosed) {
                throw new IllegalStateException("calling next() when the iterator is closed");
            }
            if (!hasNext()) {
                throw new IllegalStateException("you may only call next() if hasNext() is true");
            }
            try {
                return getEntityAndIncrementCursor(this.mCursor);
            } catch (RemoteException e) {
                throw new RuntimeException("caught a remote exception, this process will die soon", e);
            }
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException("remove not supported by EntityIterators");
        }

        @Override // android.content.EntityIterator
        public final void reset() {
            if (this.mIsClosed) {
                throw new IllegalStateException("calling reset() when the iterator is closed");
            }
            this.mCursor.moveToFirst();
        }

        @Override // android.content.EntityIterator
        public final void close() {
            if (this.mIsClosed) {
                throw new IllegalStateException("closing when already closed");
            }
            this.mIsClosed = true;
            this.mCursor.close();
        }
    }
}
