package com.studentlms.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.studentlms.data.dao.LMSAssignmentDao;
import com.studentlms.data.database.AppDatabase;
import com.studentlms.data.models.LMSAssignment;
import com.studentlms.utils.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssignmentReminderWorker extends Worker {

    private static final String TAG = "AssignmentReminder";
    private static final long REMINDER_WINDOW_MS = 24 * 60 * 60 * 1000; // 24 hours

    public AssignmentReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Checking for upcoming pending assignments...");

        try {
            AppDatabase database = AppDatabase.getInstance(getApplicationContext());
            LMSAssignmentDao assignmentDao = database.lmsAssignmentDao();

            long currentTime = System.currentTimeMillis();

            // Check assignments in different time windows for multi-tier alerts
            List<LMSAssignment> allPendingAssignments = assignmentDao.getPendingAssignmentsInRange(
                    currentTime, currentTime + (7 * 24 * 60 * 60 * 1000)); // 7 days

            if (allPendingAssignments != null && !allPendingAssignments.isEmpty()) {
                Log.d(TAG, "Found " + allPendingAssignments.size() + " pending assignments.");

                // Separate assignments by urgency
                for (LMSAssignment assignment : allPendingAssignments) {
                    long timeUntilDue = assignment.getDueDate() - currentTime;
                    int priority = determinePriority(timeUntilDue);

                    // Only send notification if it matches one of our key windows
                    if (shouldSendNotification(timeUntilDue)) {
                        String title = getPriorityTitle(priority);
                        String timeRemaining = formatTimeRemaining(timeUntilDue);
                        String message = assignment.getTitle() + " - " + assignment.getCourseName() +
                                "\nDue in " + timeRemaining;

                        NotificationHelper.showAssignmentAlert(
                                getApplicationContext(),
                                title,
                                message,
                                assignment.getId(),
                                priority);

                        Log.d(TAG, "Sent priority " + priority + " alert for: " + assignment.getTitle());
                    }
                }
            } else {
                Log.d(TAG, "No pending assignments in the next 7 days.");
            }

            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error checking assignments", e);
            return Result.failure();
        }
    }

    /**
     * Determine priority level based on time until due
     * 
     * @param timeUntilDue milliseconds until assignment is due
     * @return 1=LOW (7 days), 2=MEDIUM (3 days), 3=HIGH (1 day), 4=URGENT (2 hours)
     */
    private int determinePriority(long timeUntilDue) {
        long hours = timeUntilDue / (60 * 60 * 1000);

        if (hours <= 2) {
            return 4; // URGENT - 2 hours or less
        } else if (hours <= 24) {
            return 3; // HIGH - 1 day or less
        } else if (hours <= 72) {
            return 2; // MEDIUM - 3 days or less
        } else {
            return 1; // LOW - 7 days or less
        }
    }

    /**
     * Check if we should send notification for this time window
     * Only send at specific thresholds to avoid spam
     */
    private boolean shouldSendNotification(long timeUntilDue) {
        long hours = timeUntilDue / (60 * 60 * 1000);

        // Send at: 7 days, 3 days, 1 day, 2 hours
        // With some tolerance (±2 hours) since worker runs periodically
        return (hours >= 166 && hours <= 170) || // ~7 days
                (hours >= 70 && hours <= 74) || // ~3 days
                (hours >= 22 && hours <= 26) || // ~1 day
                (hours <= 2 && hours >= 1); // 1-2 hours
    }

    private String getPriorityTitle(int priority) {
        switch (priority) {
            case 4:
                return "⚠️ URGENT Assignment Due Soon!";
            case 3:
                return "⏰ Assignment Due Tomorrow";
            case 2:
                return "📝 Assignment Reminder";
            case 1:
                return "📌 Upcoming Assignment";
            default:
                return "Assignment Reminder";
        }
    }

    private String formatTimeRemaining(long diffMillis) {
        long hours = diffMillis / (60 * 60 * 1000);
        long minutes = (diffMillis % (60 * 60 * 1000)) / (60 * 1000);

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + "m";
        }
    }
}
