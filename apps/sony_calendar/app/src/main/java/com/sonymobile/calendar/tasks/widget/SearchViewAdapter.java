package com.sonymobile.calendar.tasks.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.sonyericsson.calendar.util.CalendarColorService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.tasks.TasksDatabase;
import com.sonymobile.calendar.tasks.activity.TasksEditActivity;
import com.sonymobile.calendar.tasks.alerts.TasksAlertUtils;
import com.sonymobile.calendar.tasks.alerts.TasksAlertWork;
import com.sonymobile.calendar.tasks.model.TasksListData;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.provider.TasksContract;

/* JADX INFO: loaded from: classes2.dex */
public class SearchViewAdapter extends ResourceCursorAdapter implements View.OnCreateContextMenuListener {
    private static final int CONTEXT_MENU_ID_DELETE_TASKS = 0;
    private static final int CONTEXT_MENU_ID_DISMISS_TASKS = 6;
    private boolean hasOnlyOneReminderTask;
    private View.OnLongClickListener longClickListener;
    private Activity mActivity;
    private View.OnClickListener showTaskListener;
    private View.OnClickListener switchTaskStatusListener;
    private boolean wasStartedFromNotification;

    public SearchViewAdapter(Activity activity, int i, Cursor cursor) {
        super(activity, i, cursor, 2);
        this.showTaskListener = new View.OnClickListener() { // from class: com.sonymobile.calendar.tasks.widget.SearchViewAdapter.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                long taskID = SearchViewAdapter.this.getTaskID(((Integer) view.getTag(R.id.tag_position)).intValue());
                if (taskID >= 0) {
                    Intent intent = new Intent(SearchViewAdapter.this.mActivity, (Class<?>) TasksEditActivity.class);
                    intent.putExtra(TasksEditActivity.TASK_ID, taskID);
                    SearchViewAdapter.this.mActivity.startActivityForResult(intent, 0);
                }
            }
        };
        this.switchTaskStatusListener = new View.OnClickListener() { // from class: com.sonymobile.calendar.tasks.widget.SearchViewAdapter.4
            /* JADX WARN: Type inference failed for: r5v8, types: [com.sonymobile.calendar.tasks.widget.SearchViewAdapter$4$1] */
            /* JADX WARN: Type inference failed for: r5v9, types: [com.sonymobile.calendar.tasks.widget.SearchViewAdapter$4$2] */
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                final long taskID = SearchViewAdapter.this.getTaskID(((Integer) view.getTag(R.id.tag_position)).intValue());
                if (taskID >= 0) {
                    int i2 = SearchViewAdapter.this.getCursor().getInt(SearchViewAdapter.this.getCursor().getColumnIndex("complete"));
                    if (i2 == 0) {
                        new AsyncTask<Void, Void, Integer>() { // from class: com.sonymobile.calendar.tasks.widget.SearchViewAdapter.4.1
                            /* JADX INFO: Access modifiers changed from: protected */
                            @Override // android.os.AsyncTask
                            public Integer doInBackground(Void... voidArr) {
                                return Integer.valueOf(TasksDatabase.completeTask(SearchViewAdapter.this.mActivity, taskID, true));
                            }
                        }.execute(new Void[0]);
                    } else {
                        if (i2 != 1) {
                            return;
                        }
                        new AsyncTask<Void, Void, Integer>() { // from class: com.sonymobile.calendar.tasks.widget.SearchViewAdapter.4.2
                            /* JADX INFO: Access modifiers changed from: protected */
                            @Override // android.os.AsyncTask
                            public Integer doInBackground(Void... voidArr) {
                                return Integer.valueOf(TasksDatabase.completeTask(SearchViewAdapter.this.mActivity, taskID, false));
                            }
                        }.execute(new Void[0]);
                    }
                }
            }
        };
        this.longClickListener = new View.OnLongClickListener() { // from class: com.sonymobile.calendar.tasks.widget.SearchViewAdapter.5
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View view) {
                SearchViewAdapter searchViewAdapter = SearchViewAdapter.this;
                searchViewAdapter.showCheckDialog(searchViewAdapter.getTaskID(((Integer) view.getTag(R.id.tag_position)).intValue()));
                return true;
            }
        };
        this.mActivity = activity;
        this.wasStartedFromNotification = TasksAlertUtils.wasStartedFromNotification(activity);
    }

    @Override // android.widget.ResourceCursorAdapter, android.widget.CursorAdapter
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View viewNewView = super.newView(context, cursor, viewGroup);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.taskItem = (ViewGroup) viewNewView.findViewById(R.id.task_item);
        if (this.wasStartedFromNotification) {
            viewHolder.taskItem.setOnCreateContextMenuListener(this);
            viewHolder.taskItem.setOnClickListener(this.showTaskListener);
        } else {
            viewHolder.taskItem.setOnClickListener(this.showTaskListener);
            viewHolder.taskItem.setOnLongClickListener(this.longClickListener);
        }
        viewHolder.colorChip = (ImageView) viewNewView.findViewById(R.id.color_chip);
        viewHolder.taskName = (TextView) viewNewView.findViewById(R.id.task_name);
        viewHolder.subject = (TextView) viewNewView.findViewById(R.id.task_subject);
        viewHolder.actionIcon = (CheckBox) viewNewView.findViewById(R.id.action_icon);
        viewHolder.actionIcon.setOnClickListener(this.switchTaskStatusListener);
        viewHolder.dueDate = (TextView) viewNewView.findViewById(R.id.due_date);
        viewHolder.priorityIcon = (ImageView) viewNewView.findViewById(R.id.priority_icon);
        viewNewView.setTag(viewHolder);
        return viewNewView;
    }

    @Override // android.widget.CursorAdapter
    public void bindView(View view, Context context, Cursor cursor) {
        String string;
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int columnIndexOrThrow = cursor.getColumnIndexOrThrow("_id");
        int columnIndexOrThrow2 = cursor.getColumnIndexOrThrow("subject");
        int columnIndexOrThrow3 = cursor.getColumnIndexOrThrow(TasksContract.TasksColumns.BODY_DATA);
        int columnIndexOrThrow4 = cursor.getColumnIndexOrThrow(TasksContract.TasksColumns.DUE_DATE);
        int columnIndexOrThrow5 = cursor.getColumnIndexOrThrow("complete");
        String string2 = cursor.getString(columnIndexOrThrow2);
        if (!TextUtils.isEmpty(string2)) {
            viewHolder.taskName.setText(string2);
        } else {
            viewHolder.taskName.setText(this.mActivity.getString(R.string.task_edit_task_name_view_hint_txt));
        }
        viewHolder.taskItem.setTag(R.id.tag_position, Integer.valueOf(cursor.getPosition()));
        CalendarColorService.getInstance().requestColorByTaskAccountId(context, new ColorServiceResultHandler(viewHolder.colorChip.getDrawable()), 1, cursor.getInt(columnIndexOrThrow), true, false);
        TextView textView = viewHolder.taskName;
        if (TextUtils.isEmpty(cursor.getString(columnIndexOrThrow2))) {
            string = context.getString(R.string.no_title_label);
        } else {
            string = cursor.getString(columnIndexOrThrow2);
        }
        textView.setText(string);
        if (cursor.getString(columnIndexOrThrow3).isEmpty()) {
            viewHolder.subject.setVisibility(8);
        } else {
            viewHolder.subject.setVisibility(0);
            viewHolder.subject.setText(cursor.getString(columnIndexOrThrow3));
        }
        viewHolder.actionIcon.setTag(R.id.tag_position, Integer.valueOf(cursor.getPosition()));
        long j = cursor.getLong(columnIndexOrThrow4);
        if (j != 0) {
            String dateTime = DateUtils.formatDateTime(context, j, 65536);
            viewHolder.dueDate.setVisibility(0);
            viewHolder.dueDate.setText(dateTime);
        } else {
            viewHolder.dueDate.setVisibility(8);
        }
        if (cursor.getInt(columnIndexOrThrow5) == 1) {
            drawCompleteTasks(viewHolder, cursor, context);
        } else if (cursor.getInt(columnIndexOrThrow5) == 0) {
            drawUnCompleteTasks(viewHolder, cursor, context);
        }
    }

    private void drawCompleteTasks(ViewHolder viewHolder, Cursor cursor, Context context) {
        viewHolder.actionIcon.setChecked(true);
        int columnIndexOrThrow = cursor.getColumnIndexOrThrow(TasksContract.TasksColumns.IMPORTANCE);
        viewHolder.taskName.setTextColor(ContextCompat.getColor(context, R.color.calendar_secondary_text_color));
        viewHolder.taskName.setPaintFlags(viewHolder.taskName.getPaintFlags() | 16);
        viewHolder.dueDate.setTextColor(ContextCompat.getColor(context, R.color.calendar_secondary_text_color));
        viewHolder.priorityIcon.setBackgroundResource(TasksListData.getPriorityIconCompleted(context, cursor.getInt(columnIndexOrThrow)));
    }

    private void drawUnCompleteTasks(ViewHolder viewHolder, Cursor cursor, Context context) {
        viewHolder.actionIcon.setChecked(false);
        int columnIndexOrThrow = cursor.getColumnIndexOrThrow(TasksContract.TasksColumns.IMPORTANCE);
        if (cursor.getString(cursor.getColumnIndexOrThrow("subject")).length() > 0) {
            viewHolder.taskName.setTextColor(ContextCompat.getColor(context, R.color.calendar_primary_text_color));
        } else {
            viewHolder.taskName.setTextColor(ContextCompat.getColor(context, R.color.calendar_secondary_text_color));
        }
        viewHolder.taskName.setPaintFlags(viewHolder.taskName.getPaintFlags() & (-17));
        viewHolder.dueDate.setTextColor(ContextCompat.getColor(context, R.color.calendar_primary_text_color));
        viewHolder.priorityIcon.setBackgroundResource(TasksListData.getPriorityIcon(context, cursor.getInt(columnIndexOrThrow)));
    }

    @Override // android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.add(0, 0, 0, R.string.task_delete_confirm_dialog_btn_delete_txt).setActionView(view);
        contextMenu.add(0, 6, 0, R.string.task_dismiss_button_text).setActionView(view);
    }

    /* JADX WARN: Type inference failed for: r8v4, types: [com.sonymobile.calendar.tasks.widget.SearchViewAdapter$1] */
    public boolean onContextItemSelected(MenuItem menuItem) {
        final long taskID = getTaskID(((Integer) menuItem.getActionView().getTag(R.id.tag_position)).intValue());
        int itemId = menuItem.getItemId();
        if (itemId == 0) {
            if (taskID != -1) {
                showCheckDialog(taskID);
            }
            return true;
        }
        if (itemId != 6) {
            return false;
        }
        if (taskID != -1) {
            new AsyncTask<Void, Void, Integer>() { // from class: com.sonymobile.calendar.tasks.widget.SearchViewAdapter.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public Integer doInBackground(Void... voidArr) {
                    return Integer.valueOf(TasksDatabase.dismissTask(SearchViewAdapter.this.mActivity, taskID));
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(Integer num) {
                    if (num.intValue() > 0) {
                        TasksAlertWork.updateAlertNotification(SearchViewAdapter.this.mActivity);
                        if (SearchViewAdapter.this.hasOnlyOneReminderTask) {
                            SearchViewAdapter.this.mActivity.finish();
                        }
                    }
                }
            }.execute(new Void[0]);
        }
        return true;
    }

    public void setHasOnlyOneReminderTask(boolean z) {
        this.hasOnlyOneReminderTask = z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showCheckDialog(final long j) {
        new AlertDialog.Builder(this.mActivity, R.style.AlertDialogTheme).setTitle(R.string.task_delete_confirm_dialog_title_txt).setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.task_delete_confirm_dialog_btn_delete_txt, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.tasks.widget.SearchViewAdapter.2
            /* JADX WARN: Type inference failed for: r1v1, types: [com.sonymobile.calendar.tasks.widget.SearchViewAdapter$2$1] */
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                new AsyncTask<Void, Void, Integer>() { // from class: com.sonymobile.calendar.tasks.widget.SearchViewAdapter.2.1
                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public Integer doInBackground(Void... voidArr) {
                        return Integer.valueOf(TasksDatabase.deleteTask(SearchViewAdapter.this.mActivity, j));
                    }
                }.execute(new Void[0]);
            }
        }).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getTaskID(int i) {
        if (getCursor() == null || !getCursor().moveToPosition(i)) {
            return -1L;
        }
        return getCursor().getLong(getCursor().getColumnIndex(this.wasStartedFromNotification ? "task_id" : "_id"));
    }

    private static class ViewHolder {
        CheckBox actionIcon;
        ImageView colorChip;
        TextView dueDate;
        ImageView priorityIcon;
        TextView subject;
        ViewGroup taskItem;
        TextView taskName;

        private ViewHolder() {
        }
    }

    private static class ColorServiceResultHandler implements IAsyncServiceResultHandler {
        private Drawable drawable;

        public ColorServiceResultHandler(Drawable drawable) {
            this.drawable = drawable;
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            if (((Integer) obj2).intValue() == 1) {
                this.drawable.setColorFilter(UiUtils.getDisplayColorFromColor(((Integer) obj).intValue()), PorterDuff.Mode.MULTIPLY);
            }
        }
    }
}
