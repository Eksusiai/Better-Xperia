package com.sonymobile.calendar;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import java.util.Arrays;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes2.dex */
public class AsyncQueryServiceHelper extends JobService {
    private static final int START_QUERY_SERVICE = 0;
    private static final String TAG = "AsyncQuery";
    private static final PriorityQueue<OperationInfo> sWorkQueue = new PriorityQueue<>();
    protected Class<AsyncQueryService> mService = AsyncQueryService.class;
    private volatile ServiceHandler mServiceHandler;
    private volatile Looper mServiceLooper;

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    protected static class OperationInfo implements Delayed {
        public Object cookie;
        public long delayMillis;
        public Handler handler;
        private long mScheduledTimeMillis = 0;
        public int op;
        public String orderBy;
        public String[] projection;
        public ContentResolver resolver;
        public Object result;
        public String selection;
        public String[] selectionArgs;
        public int token;
        public Uri uri;
        public ContentValues values;

        protected OperationInfo() {
        }

        void calculateScheduledTime() {
            this.mScheduledTimeMillis = SystemClock.elapsedRealtime() + this.delayMillis;
        }

        @Override // java.util.concurrent.Delayed
        public long getDelay(TimeUnit timeUnit) {
            return timeUnit.convert(this.mScheduledTimeMillis - SystemClock.elapsedRealtime(), TimeUnit.MILLISECONDS);
        }

        @Override // java.lang.Comparable
        public int compareTo(Delayed delayed) {
            long j = this.mScheduledTimeMillis;
            long j2 = ((OperationInfo) delayed).mScheduledTimeMillis;
            if (j == j2) {
                return 0;
            }
            return j < j2 ? -1 : 1;
        }

        public String toString() {
            return "OperationInfo [\n\t token= " + this.token + ",\n\t op= " + AsyncQueryService.Operation.opToChar(this.op) + ",\n\t uri= " + this.uri + ",\n\t delayMillis= " + this.delayMillis + ",\n\t mScheduledTimeMillis= " + this.mScheduledTimeMillis + ",\n\t resolver= " + this.resolver + ",\n\t handler= " + this.handler + ",\n\t projection= " + Arrays.toString(this.projection) + ",\n\t selection= " + this.selection + ",\n\t selectionArgs= " + Arrays.toString(this.selectionArgs) + ",\n\t orderBy= " + this.orderBy + ",\n\t result= " + this.result + ",\n\t cookie= " + this.cookie + ",\n\t values= " + this.values + "\n]";
        }

        public boolean equivalent(AsyncQueryService.Operation operation) {
            return operation.token == this.token && operation.op == this.op;
        }
    }

    public static void queueOperation(Context context, OperationInfo operationInfo) {
        operationInfo.calculateScheduledTime();
        PriorityQueue<OperationInfo> priorityQueue = sWorkQueue;
        synchronized (priorityQueue) {
            priorityQueue.add(operationInfo);
            priorityQueue.notify();
        }
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService("jobscheduler");
        JobInfo.Builder builder = new JobInfo.Builder(0, new ComponentName(context, (Class<?>) AsyncQueryServiceHelper.class));
        builder.setOverrideDeadline(0L);
        jobScheduler.schedule(builder.build());
    }

    public static AsyncQueryService.Operation getLastCancelableOperation() {
        AsyncQueryService.Operation operation;
        PriorityQueue<OperationInfo> priorityQueue = sWorkQueue;
        synchronized (priorityQueue) {
            long j = Long.MIN_VALUE;
            operation = null;
            for (OperationInfo operationInfo : priorityQueue) {
                if (operationInfo.delayMillis > 0 && j < operationInfo.mScheduledTimeMillis) {
                    if (operation == null) {
                        operation = new AsyncQueryService.Operation();
                    }
                    operation.token = operationInfo.token;
                    operation.op = operationInfo.op;
                    operation.scheduledExecutionTime = operationInfo.mScheduledTimeMillis;
                    j = operationInfo.mScheduledTimeMillis;
                }
            }
        }
        return operation;
    }

    public static int cancelOperation(int i) {
        int i2;
        PriorityQueue<OperationInfo> priorityQueue = sWorkQueue;
        synchronized (priorityQueue) {
            Iterator<OperationInfo> it = priorityQueue.iterator();
            i2 = 0;
            while (it.hasNext()) {
                if (it.next().token == i) {
                    it.remove();
                    i2++;
                }
            }
        }
        return i2;
    }

    void processMessage(Message message) {
        ContentResolver contentResolver;
        Cursor cursorQuery;
        OperationInfo operationInfoPeek;
        if (message.what != 0) {
            Log.w(TAG, "processMessage: wrong message");
            return;
        }
        synchronized (sWorkQueue) {
            while (true) {
                PriorityQueue<OperationInfo> priorityQueue = sWorkQueue;
                if (priorityQueue.size() != 0) {
                    if (priorityQueue.size() == 1 && (operationInfoPeek = priorityQueue.peek()) != null) {
                        long jElapsedRealtime = operationInfoPeek.mScheduledTimeMillis - SystemClock.elapsedRealtime();
                        if (jElapsedRealtime > 0) {
                            try {
                                priorityQueue.wait(jElapsedRealtime);
                            } catch (InterruptedException unused) {
                            }
                        }
                    }
                    OperationInfo operationInfoPoll = sWorkQueue.poll();
                    if (operationInfoPoll != null && (contentResolver = operationInfoPoll.resolver) != null) {
                        int i = operationInfoPoll.op;
                        if (i == 1) {
                            try {
                                cursorQuery = contentResolver.query(operationInfoPoll.uri, operationInfoPoll.projection, operationInfoPoll.selection, operationInfoPoll.selectionArgs, operationInfoPoll.orderBy);
                                if (cursorQuery != null) {
                                    cursorQuery.getCount();
                                }
                            } catch (Exception e) {
                                Log.w(TAG, e.toString());
                                cursorQuery = null;
                            }
                            operationInfoPoll.result = cursorQuery;
                        } else if (i == 2) {
                            operationInfoPoll.result = contentResolver.insert(operationInfoPoll.uri, operationInfoPoll.values);
                        } else if (i == 3) {
                            operationInfoPoll.result = Integer.valueOf(contentResolver.update(operationInfoPoll.uri, operationInfoPoll.values, operationInfoPoll.selection, operationInfoPoll.selectionArgs));
                        } else if (i == 4) {
                            operationInfoPoll.result = Integer.valueOf(contentResolver.delete(operationInfoPoll.uri, operationInfoPoll.selection, operationInfoPoll.selectionArgs));
                        }
                        Message messageObtainMessage = operationInfoPoll.handler.obtainMessage(operationInfoPoll.token);
                        messageObtainMessage.obj = operationInfoPoll;
                        messageObtainMessage.arg1 = operationInfoPoll.op;
                        messageObtainMessage.sendToTarget();
                    }
                } else {
                    try {
                        sWorkQueue.wait();
                    } catch (InterruptedException unused2) {
                    }
                }
            }
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        HandlerThread handlerThread = new HandlerThread("AsyncQueryService", 10);
        handlerThread.start();
        this.mServiceLooper = handlerThread.getLooper();
        if (this.mServiceLooper != null) {
            this.mServiceHandler = new ServiceHandler(this.mServiceLooper);
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        if (jobParameters.getJobId() != 0) {
            Log.d(TAG, "job id not for us: " + jobParameters.getJobId());
            return false;
        }
        Message messageObtainMessage = this.mServiceHandler.obtainMessage();
        messageObtainMessage.what = 0;
        messageObtainMessage.obj = jobParameters;
        this.mServiceHandler.sendMessage(messageObtainMessage);
        return true;
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            AsyncQueryServiceHelper.this.processMessage(message);
            AsyncQueryServiceHelper.this.jobFinished((JobParameters) message.obj, false);
        }
    }
}
