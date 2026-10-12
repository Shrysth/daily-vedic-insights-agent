package com.vedicinsights.agent.worker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    private static final String PREFERENCES = "daily_schedule";
    private static final String HAS_SCHEDULE = "has_schedule";
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final int REQUEST_CODE = 108;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent == null ? null : intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)
            || Intent.ACTION_TIME_CHANGED.equals(action)
            || Intent.ACTION_TIMEZONE_CHANGED.equals(action)
            || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {
            restoreSchedule(context);
            return;
        }

        rescheduleNextRun(context);
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(DailyPipelineWorker.class).build();
        WorkManager.getInstance(context).enqueueUniqueWork(
            "daily-vedic-briefing",
            ExistingWorkPolicy.KEEP,
            workRequest
        );
    }

    public static void scheduleDailyAlarm(Context context, int hour, int minute) {
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Awakening time is invalid.");
        }
        boolean stored = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(HAS_SCHEDULE, true)
            .putInt(HOUR, hour)
            .putInt(MINUTE, minute)
            .commit();
        if (!stored) {
            throw new IllegalStateException("Could not persist the daily awakening time.");
        }
        scheduleNextRun(context, hour, minute);
    }

    public static int[] getSavedTime(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return new int[] {preferences.getInt(HOUR, 6), preferences.getInt(MINUTE, 0)};
    }

    public static boolean hasSavedSchedule(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .getBoolean(HAS_SCHEDULE, false);
    }

    private static void restoreSchedule(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if (!preferences.getBoolean(HAS_SCHEDULE, false)) {
            Log.i(TAG, "No daily awakening time has been configured; no alarm restored.");
            return;
        }
        scheduleNextRun(context, preferences.getInt(HOUR, 6), preferences.getInt(MINUTE, 0));
    }

    private static void rescheduleNextRun(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if (preferences.getBoolean(HAS_SCHEDULE, false)) {
            scheduleNextRun(context, preferences.getInt(HOUR, 6), preferences.getInt(MINUTE, 0));
        }
    }

    private static void scheduleNextRun(Context context, int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            throw new IllegalStateException("Alarm service is unavailable.");
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        Calendar next = Calendar.getInstance();
        next.set(Calendar.HOUR_OF_DAY, hour);
        next.set(Calendar.MINUTE, minute);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        if (next.getTimeInMillis() <= System.currentTimeMillis()) {
            next.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                next.getTimeInMillis(),
                pendingIntent
            );
        } else {
            Log.w(TAG, "Exact alarm permission is unavailable; scheduling an inexact alarm.");
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                next.getTimeInMillis(),
                pendingIntent
            );
        }
    }
}
