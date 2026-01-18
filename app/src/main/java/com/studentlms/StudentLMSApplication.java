package com.studentlms;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.studentlms.services.LMSSyncWorker;

import java.util.concurrent.TimeUnit;

public class StudentLMSApplication extends Application {

    public static final String CHANNEL_REMINDERS = "reminders_channel";
    public static final String CHANNEL_ASSIGNMENTS = "assignments_channel";
    public static final String CHANNEL_INSIGHTS = "insights_channel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
        scheduleLMSSync();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);

            // Reminders Channel
            NotificationChannel remindersChannel = new NotificationChannel(
                    CHANNEL_REMINDERS,
                    "Study Reminders",
                    NotificationManager.IMPORTANCE_HIGH);
            remindersChannel.setDescription("Notifications for study sessions and deadlines");
            manager.createNotificationChannel(remindersChannel);

            // Assignments Channel
            NotificationChannel assignmentsChannel = new NotificationChannel(
                    CHANNEL_ASSIGNMENTS,
                    "Assignment Deadlines",
                    NotificationManager.IMPORTANCE_HIGH);
            assignmentsChannel.setDescription("Notifications for assignment due dates");
            manager.createNotificationChannel(assignmentsChannel);

            // Insights Channel
            NotificationChannel insightsChannel = new NotificationChannel(
                    CHANNEL_INSIGHTS,
                    "Daily Insights",
                    NotificationManager.IMPORTANCE_DEFAULT);
            insightsChannel.setDescription("AI-powered study insights and recommendations");
            manager.createNotificationChannel(insightsChannel);
        }
    }

    private void scheduleLMSSync() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest syncWorkRequest = new PeriodicWorkRequest.Builder(
                LMSSyncWorker.class,
                4, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "lms_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                syncWorkRequest);
    }
}
