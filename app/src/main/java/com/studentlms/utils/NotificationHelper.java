package com.studentlms.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.studentlms.R;
import com.studentlms.MainActivity;

import java.util.Random;

public class NotificationHelper {

    private static final String CHANNEL_ID = "lms_updates_channel";
    private static final String CHANNEL_NAME = "LMS Updates";
    private static final String CHANNEL_DESC = "Notifications for new assignments and updates";

    public static void showNewAssignmentNotification(Context context, String title, String message) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) // Ensure this icon exists, fallback to launcher
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Use a unique ID for each notification to show multiple
        int notificationId = new Random().nextInt(10000);
        try {
            notificationManager.notify(notificationId, builder.build());
        } catch (SecurityException e) {
            // Handle permission denial on Android 13+
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESC);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
