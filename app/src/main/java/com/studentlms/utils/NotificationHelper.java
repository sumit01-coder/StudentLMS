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

    // Notification Channels
    private static final String CHANNEL_ASSIGNMENTS = "assignment_alerts";
    private static final String CHANNEL_STUDY = "study_reminders";
    private static final String CHANNEL_DAILY = "daily_motivation";
    private static final String CHANNEL_PROGRESS = "progress_updates";

    // Legacy channel (keep for backwards compatibility)
    private static final String CHANNEL_ID = "lms_updates_channel";
    private static final String CHANNEL_NAME = "LMS Updates";
    private static final String CHANNEL_DESC = "Notifications for new assignments and updates";

    /**
     * Show notification for new assignment from ERP sync
     */

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

    /**
     * Show study session reminder notification
     */
    public static void showStudySessionReminder(Context context, String title, String message, int sessionId) {
        createAllNotificationChannels(context);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("open_study_plan", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, sessionId, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_STUDY)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[] { 0, 250, 250, 250 });

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(1000 + sessionId, builder.build());
        } catch (SecurityException e) {
            // Handle permission denial
        }
    }

    /**
     * Show assignment alert with priority level
     * 
     * @param priority 1=LOW (7 days), 2=MEDIUM (3 days), 3=HIGH (1 day), 4=URGENT
     *                 (2 hours)
     */
    public static void showAssignmentAlert(Context context, String title, String message,
            int assignmentId, int priority) {
        createAllNotificationChannels(context);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, assignmentId, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        int notificationPriority = NotificationCompat.PRIORITY_DEFAULT;
        long[] vibrationPattern = null;

        switch (priority) {
            case 4: // URGENT
                notificationPriority = NotificationCompat.PRIORITY_MAX;
                vibrationPattern = new long[] { 0, 500, 200, 500 };
                break;
            case 3: // HIGH
                notificationPriority = NotificationCompat.PRIORITY_HIGH;
                vibrationPattern = new long[] { 0, 300, 200, 300 };
                break;
            case 2: // MEDIUM
                notificationPriority = NotificationCompat.PRIORITY_DEFAULT;
                break;
            case 1: // LOW
                notificationPriority = NotificationCompat.PRIORITY_LOW;
                break;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ASSIGNMENTS)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(notificationPriority)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (vibrationPattern != null) {
            builder.setVibrate(vibrationPattern);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(2000 + assignmentId, builder.build());
        } catch (SecurityException e) {
            // Handle permission denial
        }
    }

    /**
     * Create all notification channels
     */
    private static void createAllNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager == null)
                return;

            // Assignment Alerts - HIGH priority
            NotificationChannel assignmentChannel = new NotificationChannel(
                    CHANNEL_ASSIGNMENTS,
                    "Assignment Alerts",
                    NotificationManager.IMPORTANCE_HIGH);
            assignmentChannel.setDescription("Notifications for upcoming assignment deadlines");
            assignmentChannel.enableVibration(true);
            notificationManager.createNotificationChannel(assignmentChannel);

            // Study Reminders - DEFAULT priority
            NotificationChannel studyChannel = new NotificationChannel(
                    CHANNEL_STUDY,
                    "Study Session Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT);
            studyChannel.setDescription("Reminders for scheduled study sessions");
            studyChannel.enableVibration(true);
            notificationManager.createNotificationChannel(studyChannel);

            // Daily Motivation - LOW priority
            NotificationChannel dailyChannel = new NotificationChannel(
                    CHANNEL_DAILY,
                    "Daily Study Motivation",
                    NotificationManager.IMPORTANCE_LOW);
            dailyChannel.setDescription("Daily reminders and study streak updates");
            notificationManager.createNotificationChannel(dailyChannel);

            // Progress Updates - LOW priority
            NotificationChannel progressChannel = new NotificationChannel(
                    CHANNEL_PROGRESS,
                    "Progress Updates",
                    NotificationManager.IMPORTANCE_LOW);
            progressChannel.setDescription("Weekly summaries and progress reports");
            notificationManager.createNotificationChannel(progressChannel);
        }
    }

    /**
     * Show daily motivation/study reminder
     */
    public static void showDailyMotivation(Context context, String title, String message) {
        createAllNotificationChannels(context);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("open_study_plan", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 5000, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_DAILY)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(5001, builder.build());
        } catch (SecurityException e) {
            // Handle permission denial
        }
    }
}
