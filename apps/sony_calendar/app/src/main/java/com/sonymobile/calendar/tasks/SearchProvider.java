package com.sonymobile.calendar.tasks;

import android.app.Activity;
import android.text.TextUtils;
import androidx.loader.content.CursorLoader;
import com.sonymobile.provider.TasksContract;
import java.util.ArrayList;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes2.dex */
public class SearchProvider {
    private static final Pattern SQLITE_LIKE_ESCAPES = Pattern.compile("[#%_]");
    private static SearchProvider instance;
    private String mInputQueryString;
    private String[] mQueryArgs;
    private String mQueryString;

    private SearchProvider() {
    }

    public static SearchProvider getInstance() {
        if (instance == null) {
            instance = new SearchProvider();
        }
        return instance;
    }

    public CursorLoader configureLoader(Activity activity) {
        CursorLoader cursorLoader = new CursorLoader(activity);
        cursorLoader.setProjection(new String[]{"_id", "subject", TasksContract.TasksColumns.DUE_DATE, TasksContract.TasksColumns.IMPORTANCE, "complete", "deleted", TasksContract.TasksColumns.TASKLIST_ID, TasksContract.TasksColumns.UTC_DUE_DATE, TasksContract.TasksColumns.BODY_DATA, TasksContract.TaskListsColumns.TASKLIST_COLOR});
        cursorLoader.setUri(TasksContract.Tasks.CONTENT_URI);
        cursorLoader.setSortOrder("utc_due_date desc");
        if (hasActiveQuery()) {
            cursorLoader.setSelection(this.mQueryString);
            cursorLoader.setSelectionArgs(this.mQueryArgs);
        }
        return cursorLoader;
    }

    public boolean hasActiveQuery() {
        return !TextUtils.isEmpty(this.mQueryString);
    }

    public String getQueryString() {
        return this.mInputQueryString;
    }

    public void setQueryString(String str) {
        this.mInputQueryString = str;
        this.mQueryString = null;
        this.mQueryArgs = null;
        if (TextUtils.isEmpty(str)) {
            return;
        }
        String[] strArrSplit = str.split(" ");
        ArrayList arrayList = new ArrayList(strArrSplit.length);
        StringBuilder sb = new StringBuilder();
        sb.append("deleted =? ");
        sb.append(" AND ");
        sb.append("visible =? ");
        arrayList.add(Integer.toString(0));
        arrayList.add(Integer.toString(1));
        for (int i = 0; i < strArrSplit.length; i++) {
            if (!TextUtils.isEmpty(strArrSplit[i])) {
                sb.append(" AND ");
                sb.append("subject");
                sb.append(" LIKE ? ESCAPE '#'");
                arrayList.add('%' + SQLITE_LIKE_ESCAPES.matcher(strArrSplit[i]).replaceAll("#$0") + '%');
            }
        }
        if (arrayList.size() > 0) {
            this.mQueryString = sb.toString();
            this.mQueryArgs = (String[]) arrayList.toArray(new String[arrayList.size()]);
        }
    }
}
