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
            long windowEnd = currentTime + REMINDER_WINDOW_MS;

            List<LMSAssignment> pendingAssignments = assignmentDao.getPendingAssignmentsInRange(currentTime, windowEnd);

            if (pendingAssignments != null && !pendingAssignments.isEmpty()) {
                Log.d(TAG, "Found " + pendingAssignments.size() + " pending assignments due soon.");

                // Alert for the most urgent one
                LMSAssignment urgent = pendingAssignments.get(0);

                String timeRemaining = formatTimeRemaining(urgent.getDueDate() - currentTime);
                String message = "Due in " + timeRemaining + ": " + urgent.getTitle() + " (" + urgent.getCourseName()
                        + ")";

                NotificationHelper.showNewAssignmentNotification(
                        getApplicationContext(),
                        "Assignment Due Soon!",
                        message);

                // If multiple, maybe show a summary count too?
                // For now, focusing on the most urgent one is good to avoid spam.
            } else {
                Log.d(TAG, "No pending assignments due in the next 24 hours.");
            }

            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error checking assignments", e);
            return Result.failure();
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
