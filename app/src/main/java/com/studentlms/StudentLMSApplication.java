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
                scheduleAssignmentReminders();
                scheduleDailyStudyReminder();
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

        /**
         * Schedule assignment reminder worker to check every 6 hours
         */
        private void scheduleAssignmentReminders() {
                PeriodicWorkRequest assignmentReminderWork = new PeriodicWorkRequest.Builder(
                                com.studentlms.services.AssignmentReminderWorker.class,
                                6, TimeUnit.HOURS) // Check every 6 hours
                                .build();

                WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                                "assignment_reminders",
                                ExistingPeriodicWorkPolicy.KEEP,
                                assignmentReminderWork);
        }

        /**
         * Schedule daily study reminder to run once per day at 7 PM
         */
        private void scheduleDailyStudyReminder() {
                // Calculate initial delay to reach 7 PM today or tomorrow
                java.util.Calendar now = java.util.Calendar.getInstance();
                java.util.Calendar targetTime = (java.util.Calendar) now.clone();
                targetTime.set(java.util.Calendar.HOUR_OF_DAY, 19); // 7 PM
                targetTime.set(java.util.Calendar.MINUTE, 0);
                targetTime.set(java.util.Calendar.SECOND, 0);

                // If 7 PM has passed today, schedule for tomorrow
                if (targetTime.before(now)) {
                        targetTime.add(java.util.Calendar.DAY_OF_YEAR, 1);
                }

                long initialDelay = targetTime.getTimeInMillis() - now.getTimeInMillis();

                PeriodicWorkRequest dailyReminderWork = new PeriodicWorkRequest.Builder(
                                com.studentlms.services.DailyStudyReminderWorker.class,
                                1, TimeUnit.DAYS) // Once per day
                                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                                .build();

                WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                                "daily_study_reminder",
                                ExistingPeriodicWorkPolicy.KEEP,
                                dailyReminderWork);
        }
}
