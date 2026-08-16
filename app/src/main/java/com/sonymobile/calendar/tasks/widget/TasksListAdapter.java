package com.sonymobile.calendar.tasks.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.sonyericsson.calendar.util.CalendarColorService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.KeyboardListener;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.tasks.TasksDatabase;
import com.sonymobile.calendar.tasks.model.TasksListData;
import com.sonymobile.calendar.tasks.model.TasksListItem;
import com.sonymobile.calendar.utils.UiUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/* JADX INFO: loaded from: classes2.dex */
public class TasksListAdapter extends BaseExpandableListAdapter implements KeyboardListener {
    private Context context;
    private LayoutInflater inflater;
    ExpandableListView listView;
    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener;
    private long mSelectedTaskId;
    private Set<Integer> expandedGroups = new HashSet();
    private View.OnClickListener mOnClickListener = new View.OnClickListener() { // from class: com.sonymobile.calendar.tasks.widget.TasksListAdapter.1
        /* JADX WARN: Type inference failed for: r5v4, types: [com.sonymobile.calendar.tasks.widget.TasksListAdapter$1$1] */
        /* JADX WARN: Type inference failed for: r5v5, types: [com.sonymobile.calendar.tasks.widget.TasksListAdapter$1$2] */
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            final long jLongValue = ((Long) view.getTag(R.id.tag_task_id)).longValue();
            int iIntValue = ((Integer) view.getTag(R.id.tag_complete_status)).intValue();
            if (iIntValue == 0) {
                new AsyncTask<Void, Void, Integer>() { // from class: com.sonymobile.calendar.tasks.widget.TasksListAdapter.1.1
                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public Integer doInBackground(Void... voidArr) {
                        return Integer.valueOf(TasksDatabase.completeTask(TasksListAdapter.this.context, jLongValue, true));
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            } else {
                if (iIntValue != 1) {
                    return;
                }
                new AsyncTask<Void, Void, Integer>() { // from class: com.sonymobile.calendar.tasks.widget.TasksListAdapter.1.2
                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public Integer doInBackground(Void... voidArr) {
                        return Integer.valueOf(TasksDatabase.completeTask(TasksListAdapter.this.context, jLongValue, false));
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            }
        }
    };
    private List<CharSequence> groups = new ArrayList();
    private Vector<Vector<TasksListItem>> items = new Vector<>();

    @Override // android.widget.ExpandableListAdapter
    public long getGroupId(int i) {
        return i;
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean hasStableIds() {
        return false;
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    @Override // com.sonymobile.calendar.KeyboardListener
    public void onEscapePressed() {
    }

    public TasksListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override // android.widget.ExpandableListAdapter
    public View getGroupView(int i, boolean z, View view, ViewGroup viewGroup) {
        GroupViewHolder groupViewHolder;
        if (view == null) {
            view = this.inflater.inflate(R.layout.tasks_list_groups, viewGroup, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.groupName = (TextView) view.findViewById(R.id.groups);
            groupViewHolder.expandCollapsImage = (ImageView) view.findViewById(R.id.expand_collapse_group);
            view.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) view.getTag();
        }
        groupViewHolder.groupName.setText(this.groups.get(i));
        groupViewHolder.groupName.setTextColor(this.context.getResources().getColor(R.color.calendar_primary_text_color));
        if (z) {
            groupViewHolder.expandCollapsImage.setImageResource(R.drawable.semc_list_expandable_arrow_up);
        } else {
            groupViewHolder.expandCollapsImage.setImageResource(R.drawable.semc_list_expandable_arrow_down);
        }
        return view;
    }

    @Override // android.widget.ExpandableListAdapter
    public View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup) {
        ChildViewHolder childViewHolder;
        String string;
        this.listView = (ExpandableListView) viewGroup;
        if (view == null) {
            view = this.inflater.inflate(R.layout.tasks_list_childs, viewGroup, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.rootView = (ViewGroup) view.findViewById(R.id.task_item_root);
            childViewHolder.colorChip = (ImageView) view.findViewById(R.id.color_chip);
            childViewHolder.taskName = (TextView) view.findViewById(R.id.task_name);
            childViewHolder.subject = (TextView) view.findViewById(R.id.task_subject);
            childViewHolder.actionIcon = (CheckBox) view.findViewById(R.id.action_icon);
            childViewHolder.dueDate = (TextView) view.findViewById(R.id.due_date);
            childViewHolder.priorityIcon = (ImageView) view.findViewById(R.id.priority_icon);
            childViewHolder.taskItemView = view.findViewById(R.id.task_item);
            if (this.mCheckedChangeListener != null) {
                childViewHolder.actionIcon.setOnCheckedChangeListener(this.mCheckedChangeListener);
            }
            view.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) view.getTag();
        }
        TasksListItem tasksListItem = (TasksListItem) getChild(i, i2);
        if (Utils.isTabletDevice(this.context) && this.mSelectedTaskId == tasksListItem.taskId) {
            childViewHolder.taskItemView.setBackgroundColor(UiUtils.getPrimaryColor(this.context));
        } else if (tasksListItem.completed == 1) {
            childViewHolder.taskItemView.setBackgroundResource(R.drawable.task_completed_navigation_selector);
        } else {
            childViewHolder.taskItemView.setBackgroundResource(R.drawable.agenda_task_navigation_selector);
        }
        CalendarColorService.getInstance().requestColorByTaskAccountId(this.context, new ColorServiceResultHandler(childViewHolder.colorChip.getDrawable()), 1, tasksListItem.accountId, true, false);
        TextView textView = childViewHolder.taskName;
        if (TextUtils.isEmpty(tasksListItem.taskName)) {
            string = this.context.getString(R.string.no_title_label);
        } else {
            string = tasksListItem.taskName;
        }
        textView.setText(string);
        if (TextUtils.isEmpty(tasksListItem.taskDescription)) {
            childViewHolder.subject.setVisibility(8);
        } else {
            tasksListItem.taskDescription = tasksListItem.taskDescription.trim();
            childViewHolder.subject.setVisibility(0);
            childViewHolder.subject.setText(tasksListItem.taskDescription);
            childViewHolder.subject.setTextColor(ContextCompat.getColor(this.context, R.color.calendar_secondary_text_color));
        }
        childViewHolder.actionIcon.setTag(R.id.tag_task_id, Long.valueOf(tasksListItem.taskId));
        childViewHolder.actionIcon.setTag(R.id.tag_complete_status, Integer.valueOf(tasksListItem.completed));
        childViewHolder.actionIcon.setOnClickListener(this.mOnClickListener);
        long j = tasksListItem.dueDate;
        if (j != 0) {
            String dateTime = DateUtils.formatDateTime(this.context, j, 65536);
            childViewHolder.dueDate.setVisibility(0);
            childViewHolder.dueDate.setText(dateTime);
        } else {
            childViewHolder.dueDate.setVisibility(8);
        }
        if (tasksListItem.completed == 1) {
            drawCompleteTasks(childViewHolder, tasksListItem);
        } else if (tasksListItem.completed == 0) {
            drawUnCompleteTasks(childViewHolder, tasksListItem);
        }
        if (Utils.isTabletDevice(this.context) && this.mSelectedTaskId == tasksListItem.taskId) {
            drawMarkedTask(childViewHolder, tasksListItem);
        }
        return view;
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.mCheckedChangeListener = onCheckedChangeListener;
    }

    @Override // android.widget.ExpandableListAdapter
    public int getGroupCount() {
        return this.groups.size();
    }

    public long getTopMostTaskId() {
        if (this.groups.size() <= 0 || this.items.size() <= 0 || this.items.get(0).size() <= 0 || this.listView == null) {
            return -1L;
        }
        return this.items.get(0).get(0).taskId;
    }

    public int getTopMostTaskIndex() {
        ExpandableListView expandableListView;
        if (this.groups.size() > 0 && this.items.size() > 0 && this.items.get(0).size() > 0 && (expandableListView = this.listView) != null) {
            try {
                int flatListPosition = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(0, 0));
                this.listView.setItemChecked(flatListPosition, true);
                return flatListPosition;
            } catch (NullPointerException unused) {
            }
        }
        return -1;
    }

    public int getIndexForId(long j) {
        for (int i = 0; i < this.items.size(); i++) {
            for (int i2 = 0; i2 < this.items.get(i).size(); i2++) {
                if (this.items.get(i).get(i2).taskId == j) {
                    try {
                        return this.listView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(i, i2));
                    } catch (NullPointerException unused) {
                        return -1;
                    }
                }
            }
        }
        return -1;
    }

    public boolean isTaskPresent(long j) {
        for (int i = 0; i < this.items.size(); i++) {
            for (int i2 = 0; i2 < this.items.get(i).size(); i2++) {
                if (this.items.get(i).get(i2).taskId == j) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getGroupPositionForId(long j) {
        for (int i = 0; i < this.items.size(); i++) {
            for (int i2 = 0; i2 < this.items.get(i).size(); i2++) {
                if (this.items.get(i).get(i2).taskId == j) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void setGroupIsExpanded(boolean z, Integer num) {
        if (z) {
            this.expandedGroups.add(num);
        } else {
            this.expandedGroups.remove(num);
        }
    }

    public boolean getGroupIsExpandedForId(long j) {
        return this.expandedGroups.contains(Integer.valueOf(getGroupPositionForId(j)));
    }

    @Override // android.widget.ExpandableListAdapter
    public int getChildrenCount(int i) {
        return this.items.get(i).size();
    }

    @Override // android.widget.ExpandableListAdapter
    public Object getGroup(int i) {
        return this.groups.get(i);
    }

    @Override // android.widget.ExpandableListAdapter
    public Object getChild(int i, int i2) {
        return this.items.get(i).get(i2);
    }

    @Override // android.widget.ExpandableListAdapter
    public long getChildId(int i, int i2) {
        return this.items.get(i).get(i2).taskId;
    }

    public void updateItems(Vector<Vector<TasksListItem>> vector, List<CharSequence> list) {
        this.items = vector;
        this.groups = list;
    }

    private void drawMarkedTask(ChildViewHolder childViewHolder, TasksListItem tasksListItem) {
        childViewHolder.taskName.setTextColor(-1);
        if (tasksListItem.completed == 1) {
            childViewHolder.taskName.setPaintFlags(childViewHolder.taskName.getPaintFlags() | 16);
        } else if (tasksListItem.completed == 0) {
            childViewHolder.taskName.setPaintFlags(childViewHolder.taskName.getPaintFlags() & (-17));
        }
        childViewHolder.subject.setTextColor(-1);
        childViewHolder.dueDate.setTextColor(-1);
        childViewHolder.priorityIcon.setBackgroundResource(TasksListData.getPriorityIconCompleted(this.context, tasksListItem.priority));
    }

    private void drawCompleteTasks(ChildViewHolder childViewHolder, TasksListItem tasksListItem) {
        childViewHolder.rootView.setBackgroundColor(ContextCompat.getColor(this.context, R.color.completed_task_background_color));
        childViewHolder.actionIcon.setChecked(true);
        childViewHolder.taskName.setTextColor(ContextCompat.getColor(this.context, R.color.calendar_disabled_text_color));
        childViewHolder.taskName.setPaintFlags(childViewHolder.taskName.getPaintFlags() | 16);
        childViewHolder.subject.setTextColor(ContextCompat.getColor(this.context, R.color.calendar_disabled_text_color));
        childViewHolder.dueDate.setTextColor(ContextCompat.getColor(this.context, R.color.calendar_disabled_text_color));
        childViewHolder.priorityIcon.setBackgroundResource(TasksListData.getPriorityIconCompleted(this.context, tasksListItem.priority));
    }

    private void drawUnCompleteTasks(ChildViewHolder childViewHolder, TasksListItem tasksListItem) {
        childViewHolder.actionIcon.setChecked(false);
        if (tasksListItem.taskName.length() > 0) {
            childViewHolder.taskName.setTextColor(ContextCompat.getColor(this.context, R.color.calendar_primary_text_color));
        } else {
            childViewHolder.taskName.setTextColor(ContextCompat.getColor(this.context, R.color.calendar_secondary_text_color));
        }
        childViewHolder.taskName.setPaintFlags(childViewHolder.taskName.getPaintFlags() & (-17));
        childViewHolder.dueDate.setTextColor(ContextCompat.getColor(this.context, R.color.calendar_primary_text_color));
        childViewHolder.priorityIcon.setBackgroundResource(TasksListData.getPriorityIcon(this.context, tasksListItem.priority));
    }

    @Override // com.sonymobile.calendar.KeyboardListener
    public void onSpacePressed() {
        View selectedView;
        CheckBox checkBox;
        ExpandableListView expandableListView = this.listView;
        if (expandableListView == null || expandableListView.getSelectedItemPosition() == -1 || (selectedView = this.listView.getSelectedView()) == null || (checkBox = (CheckBox) selectedView.findViewById(R.id.action_icon)) == null) {
            return;
        }
        checkBox.performClick();
    }

    private static class GroupViewHolder {
        ImageView expandCollapsImage;
        TextView groupName;

        private GroupViewHolder() {
        }
    }

    private static class ChildViewHolder {
        CheckBox actionIcon;
        ImageView colorChip;
        TextView dueDate;
        ImageView priorityIcon;
        ViewGroup rootView;
        TextView subject;
        View taskItemView;
        TextView taskName;

        private ChildViewHolder() {
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

    public void setSelectedTaskId(int i, int i2) {
        this.mSelectedTaskId = ((TasksListItem) getChild(i, i2)).taskId;
    }
}
