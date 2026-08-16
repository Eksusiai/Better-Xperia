package com.sonymobile.calendar.jobs;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import com.sonymobile.calendar.alerts.AlertReceiver;
import com.sonymobile.calendar.tasks.alerts.TasksAlertReceiver;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes2.dex */
public class ProviderChangeJobService extends JobService {
    private static final String APPLICATION_PACKAGE = "com.sonymobile.calendar";
    private static final String COMPONENT_CLASS = "com.sonymobile.calendar.jobs.ProviderChangeJobService";
    private static final String TAG = "ProviderChangeJobService";
    private static final String WIDGET_UPDATE_RECEIVER_CLASS = "com.sonymobile.calendar.widget.CalendarAppWidgetService$CalendarFactory";
    private static final String CALENDAR_ALERT_RECEIVER_CLASS = AlertReceiver.class.getCanonicalName();
    private static final String TASK_ALERT_RECEIVER_CLASS = TasksAlertReceiver.class.getCanonicalName();

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public static void scheduleJob(Context context, int i) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService("jobscheduler");
        JobInfo.Builder builder = new JobInfo.Builder(i, new ComponentName("com.sonymobile.calendar", COMPONENT_CLASS));
        builder.addTriggerContentUri(new JobInfo.TriggerContentUri(CalendarContract.CONTENT_URI, 1));
        builder.addTriggerContentUri(new JobInfo.TriggerContentUri(LunarContract.CONTENT_URI, 1));
        jobScheduler.schedule(builder.build());
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        int jobId = jobParameters.getJobId();
        if (3 != jobId && 4 != jobId && 5 != jobId) {
            Log.d(TAG, "not for us: " + jobParameters.getJobId());
            return false;
        }
        if (jobParameters.getTriggeredContentAuthorities() != null) {
            for (Uri uri : jobParameters.getTriggeredContentUris()) {
                Intent intent = new Intent("android.intent.action.PROVIDER_CHANGED");
                intent.setData(uri);
                if (jobId == 3) {
                    intent.setClassName("com.sonymobile.calendar", CALENDAR_ALERT_RECEIVER_CLASS);
                } else if (jobId == 4) {
                    intent.setClassName("com.sonymobile.calendar", TASK_ALERT_RECEIVER_CLASS);
                } else if (jobId == 5) {
                    intent.setClassName("com.sonymobile.calendar", WIDGET_UPDATE_RECEIVER_CLASS);
                }
                sendBroadcast(intent);
            }
        }
        scheduleJob(this, jobId);
        return false;
    }
}
