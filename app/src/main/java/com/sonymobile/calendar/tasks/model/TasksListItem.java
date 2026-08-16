package com.sonymobile.calendar.tasks.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/* JADX INFO: loaded from: classes2.dex */
public class TasksListItem implements Parcelable {
    public static final int COMPLETED = 1;
    public static final Parcelable.Creator<TasksListItem> CREATOR = new Parcelable.Creator<TasksListItem>() { // from class: com.sonymobile.calendar.tasks.model.TasksListItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TasksListItem createFromParcel(Parcel parcel) {
            return new TasksListItem(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TasksListItem[] newArray(int i) {
            return new TasksListItem[i];
        }
    };
    public static final int NOT_COMPLETED = 0;
    public static final int NOT_DELETED = 0;
    public static final int PRIORITY_HIGH = 2;
    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_NORMAL = 1;
    public int accountColor;
    public int accountId;
    public String accountName;
    public String accountType;
    public int completed;
    public long dateComplete;
    public int deleted;
    public long dueDate;
    public boolean hasReminder;
    public int priority;
    public long reminderDate;
    public int reminderMethod;
    public String taskDescription;
    public long taskId;
    public String taskName;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TasksListItem tasksListItem = (TasksListItem) obj;
        return this.accountId == tasksListItem.accountId && this.dueDate == tasksListItem.dueDate && this.priority == tasksListItem.priority && this.reminderDate == tasksListItem.reminderDate && (TextUtils.isEmpty(tasksListItem.taskDescription) ? "" : tasksListItem.taskDescription).equals(this.taskDescription) && (TextUtils.isEmpty(tasksListItem.taskName) ? "" : tasksListItem.taskName).equals(this.taskName);
    }

    public int hashCode() {
        int iHashCode = ((((((this.accountId * 31) + this.priority) * 31) + this.taskName.hashCode()) * 31) + this.taskDescription.hashCode()) * 31;
        long j = this.dueDate;
        int i = (iHashCode + ((int) (j ^ (j >>> 32)))) * 31;
        long j2 = this.reminderDate;
        return i + ((int) (j2 ^ (j2 >>> 32)));
    }

    public TasksListItem() {
        this.taskId = -1L;
        this.accountId = -1;
        this.accountColor = -1;
        this.priority = -1;
        this.dueDate = -1L;
    }

    protected TasksListItem(Parcel parcel) {
        this.taskId = -1L;
        this.accountId = -1;
        this.accountColor = -1;
        this.priority = -1;
        this.dueDate = -1L;
        this.taskId = parcel.readLong();
        this.accountId = parcel.readInt();
        this.accountColor = parcel.readInt();
        this.accountType = parcel.readString();
        this.accountName = parcel.readString();
        this.priority = parcel.readInt();
        this.taskName = parcel.readString();
        this.taskDescription = parcel.readString();
        this.dueDate = parcel.readLong();
        this.hasReminder = parcel.readByte() != 0;
        this.reminderDate = parcel.readLong();
        this.reminderMethod = parcel.readInt();
        this.completed = parcel.readInt();
        this.dateComplete = parcel.readLong();
        this.deleted = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.taskId);
        parcel.writeInt(this.accountId);
        parcel.writeInt(this.accountColor);
        parcel.writeString(this.accountType);
        parcel.writeString(this.accountName);
        parcel.writeInt(this.priority);
        parcel.writeString(this.taskName);
        parcel.writeString(this.taskDescription);
        parcel.writeLong(this.dueDate);
        parcel.writeByte(this.hasReminder ? (byte) 1 : (byte) 0);
        parcel.writeLong(this.reminderDate);
        parcel.writeInt(this.reminderMethod);
        parcel.writeInt(this.completed);
        parcel.writeLong(this.dateComplete);
        parcel.writeInt(this.deleted);
    }
}
