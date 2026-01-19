package com.studentlms.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.studentlms.data.dao.StudySessionDao;
import com.studentlms.data.database.AppDatabase;
import com.studentlms.data.models.StudySession;
import com.studentlms.utils.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StudySessionReminderWorker extends Worker {

    private static final String TAG = "StudySessionReminder";
    public static final String KEY_SESSION_ID = "session_id";
    public static final String KEY_SUBJECT_NAME = "subject_name";
    public static final String KEY_START_TIME = "start_time";

    public StudySessionReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Study session reminder triggered");

        try {
            // Get session details from input data
            int sessionId = getInputData().getInt(KEY_SESSION_ID, -1);
            String subjectName = getInputData().getString(KEY_SUBJECT_NAME);
            long startTime = getInputData().getLong(KEY_START_TIME, 0);

            if (sessionId == -1 || subjectName == null) {
                Log.e(TAG, "Invalid session data");
                return Result.failure();
            }

            // Verify session still exists and hasn't been deleted
            AppDatabase database = AppDatabase.getInstance(getApplicationContext());
            StudySessionDao sessionDao = database.studySessionDao();

            // Query session by ID to confirm it still exists
            // If deleted, this notification is no longer needed
            // For now, we'll just send the notification

            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            String formattedTime = timeFormat.format(new Date(startTime));

            String title = "Study Session Starting Soon";
            String message = subjectName + " starts at " + formattedTime;

            NotificationHelper.showStudySessionReminder(
                    getApplicationContext(),
                    title,
                    message,
                    sessionId);

            Log.d(TAG, "Sent reminder for session: " + subjectName);
            return Result.success();

        } catch (Exception e) {
            Log.e(TAG, "Error sending study session reminder", e);
            return Result.failure();
        }
    }
}
