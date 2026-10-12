package com.vedicinsights.agent.worker;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.vedicinsights.agent.R;
import com.vedicinsights.agent.ui.home.HomeActivity;

public class NotificationHelper {
    private static final String CHANNEL_ID = "daily_vedic_briefing";
    private static final int NOTIFICATION_ID = 108;

    public static void sendNotification(Context context, String title, String body) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException("Notification permission has not been granted.");
        }
        NotificationManager manager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) {
            throw new IllegalStateException("Notification service is unavailable.");
        }

        NotificationChannel channel = new NotificationChannel(
            CHANNEL_ID,
            "Daily Vedic Insights",
            NotificationManager.IMPORTANCE_HIGH
        );
        manager.createNotificationChannel(channel);

        Intent openBriefing = new Intent(context, HomeActivity.class)
            .setAction(Intent.ACTION_VIEW)
            .setData(Uri.parse("vedicinsights://briefing"))
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            openBriefing,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true);
        manager.notify(NOTIFICATION_ID, notification.build());
    }
}
