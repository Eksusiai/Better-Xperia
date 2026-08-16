package com.sonymobile.calendar.tasks.settings;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.sonyericsson.calendar.util.AsyncServiceBase;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.tasks.model.TaskAccount;
import com.sonymobile.provider.TasksContract;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class TaskAccountManager extends AsyncServiceBase {
    private static final String ACCOUNT_ORDER = "account_type ASC";
    private static final int COLUMN_INDEX_ACCOUNT_ID = 0;
    private static final int COLUMN_INDEX_ACCOUNT_NAME = 1;
    private static final int COLUMN_INDEX_ACCOUNT_TYPE = 2;
    private static final int COLUMN_INDEX_ACCOUNT_VISIBLE = 3;
    static final String[] ACCOUNT_PROJECTION = {"_id", "account_name", "account_type", "visible", TasksContract.TaskListsColumns.TASKLIST_COLOR};
    private static volatile TaskAccountManager mTaskAccountManager = null;

    @Override // com.sonyericsson.calendar.util.AsyncServiceBase
    protected boolean handleResultData(Cursor cursor) {
        return false;
    }

    private TaskAccountManager() {
    }

    public static synchronized TaskAccountManager getInstance() {
        if (mTaskAccountManager == null) {
            mTaskAccountManager = new TaskAccountManager();
        }
        return mTaskAccountManager;
    }

    public ArrayList<TaskAccount> getAccountLists(Context context) {
        ArrayList<TaskAccount> arrayList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursorQuery = null;
        try {
            cursorQuery = contentResolver.query(TasksContract.TaskLists.CONTENT_URI, ACCOUNT_PROJECTION, null, null, ACCOUNT_ORDER);
            if (cursorQuery != null) {
                int columnIndexOrThrow = cursorQuery.getColumnIndexOrThrow(TasksContract.TaskListsColumns.TASKLIST_COLOR);
                while (cursorQuery.moveToNext()) {
                    TaskAccount taskAccount = new TaskAccount();
                    taskAccount.id = cursorQuery.getInt(0);
                    taskAccount.name = cursorQuery.getString(1);
                    taskAccount.type = cursorQuery.getString(2);
                    taskAccount.visibility = cursorQuery.getInt(3);
                    taskAccount.accountColor = cursorQuery.getInt(columnIndexOrThrow);
                    arrayList.add(taskAccount);
                }
            }
            return arrayList;
        } finally {
            Utils.closeCursor(cursorQuery);
        }
    }

    public void updateVisibility(Context context, boolean z, long j) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("visible", Integer.valueOf(z ? 1 : 0));
        performAsyncUpdate(context, null, ContentUris.withAppendedId(TasksContract.TaskLists.CONTENT_URI, j), contentValues, null, null, 0L);
    }
}
