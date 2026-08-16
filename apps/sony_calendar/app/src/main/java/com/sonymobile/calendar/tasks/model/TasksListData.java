package com.sonymobile.calendar.tasks.model;

import android.content.Context;
import com.sonymobile.calendar.R;

/* JADX INFO: loaded from: classes2.dex */
public class TasksListData {
    private static CharSequence[] groupsDate;
    private static CharSequence[] groupsPriority;
    private static int[] priorityIcons;
    private static int[] priorityIconsComplete;

    public static void resetStringsAfterLocaleChange() {
        groupsDate = null;
        groupsPriority = null;
    }

    public static CharSequence[] getGroupsPriority(Context context) {
        if (groupsPriority == null) {
            CharSequence[] charSequenceArr = new CharSequence[3];
            groupsPriority = charSequenceArr;
            charSequenceArr[0] = context.getText(R.string.task_list_group_title_high_txt);
            groupsPriority[1] = context.getText(R.string.task_list_group_title_normal_txt);
            groupsPriority[2] = context.getText(R.string.task_list_group_title_low_txt);
        }
        return (CharSequence[]) groupsPriority.clone();
    }

    public static CharSequence[] getGroupsDate(Context context) {
        if (groupsDate == null) {
            CharSequence[] charSequenceArr = new CharSequence[13];
            groupsDate = charSequenceArr;
            charSequenceArr[0] = context.getText(R.string.task_list_group_title_past_txt);
            groupsDate[1] = context.getText(R.string.task_list_group_title_over_due_txt);
            groupsDate[2] = context.getText(R.string.task_list_group_title_today_txt);
            groupsDate[3] = context.getText(R.string.task_list_group_title_tomorrow_txt);
            groupsDate[4] = context.getText(R.string.task_list_group_title_monday_txt);
            groupsDate[5] = context.getText(R.string.task_list_group_title_tuesday_txt);
            groupsDate[6] = context.getText(R.string.task_list_group_title_wednesday_txt);
            groupsDate[7] = context.getText(R.string.task_list_group_title_thursday_txt);
            groupsDate[8] = context.getText(R.string.task_list_group_title_friday_txt);
            groupsDate[9] = context.getText(R.string.task_list_group_title_saturday_txt);
            groupsDate[10] = context.getText(R.string.task_list_group_title_sunday_txt);
            groupsDate[11] = context.getText(R.string.task_list_group_title_later_txt);
            groupsDate[12] = context.getText(R.string.task_list_group_title_no_due_date_txt);
        }
        return (CharSequence[]) groupsDate.clone();
    }

    public static int getPriorityIcon(Context context, int i) {
        if (priorityIcons == null) {
            priorityIcons = new int[]{R.drawable.email_maillist_priority_low, 0, R.drawable.email_maillist_priority_high};
        }
        return priorityIcons[i];
    }

    public static int getPriorityIconCompleted(Context context, int i) {
        if (priorityIconsComplete == null) {
            priorityIconsComplete = new int[]{R.drawable.email_maillist_priority_low_complete, 0, R.drawable.email_maillist_priority_high_complete};
        }
        return priorityIconsComplete[i];
    }
}
