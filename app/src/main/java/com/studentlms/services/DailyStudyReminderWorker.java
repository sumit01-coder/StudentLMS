package com.studentlms.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.studentlms.data.dao.StudySessionDao;
import com.studentlms.data.database.AppDatabase;
import com.studentlms.data.models.StudySession;
import com.studentlms.utils.NotificationHelper;

import java.util.Calendar;
import java.util.List;

public class DailyStudyReminderWorker extends Worker {

    private static final String TAG = "DailyStudyReminder";

    public DailyStudyReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Running daily study check...");

        try {
            AppDatabase database = AppDatabase.getInstance(getApplicationContext());
            StudySessionDao sessionDao = database.studySessionDao();

            // Check if user studied today
            Calendar todayStart = Calendar.getInstance();
            todayStart.set(Calendar.HOUR_OF_DAY, 0);
            todayStart.set(Calendar.MINUTE, 0);
            todayStart.set(Calendar.SECOND, 0);
            todayStart.set(Calendar.MILLISECOND, 0);

            Calendar todayEnd = Calendar.getInstance();
            todayEnd.set(Calendar.HOUR_OF_DAY, 23);
            todayEnd.set(Calendar.MINUTE, 59);
            todayEnd.set(Calendar.SECOND, 59);

            // Get today's sessions (upcoming + completed)
            List<StudySession> todaySessions = sessionDao.getSessionsInRangeSync(
                    todayStart.getTimeInMillis(),
                    todayEnd.getTimeInMillis());

            boolean studiedToday = false;
            if (todaySessions != null) {
                for (StudySession session : todaySessions) {
                    if (session.isCompleted()) {
                        studiedToday = true;
                        break;
                    }
                }
            }

            if (!studiedToday) {
                // Calculate study streak (simplified - just check last 7 days)
                int streak = calculateStreak(sessionDao);

                String title = "Time to Study! 📚";
                String message;

                if (streak > 0) {
                    message = "Don't break your " + streak + "-day study streak! Start a quick session now.";
                } else {
                    message = "You haven't studied today. Even 15 minutes can make a difference!";
                }

                NotificationHelper.showDailyMotivation(
                        getApplicationContext(),
                        title,
                        message);

                Log.d(TAG, "Sent daily study reminder (streak: " + streak + ")");
            } else {
                Log.d(TAG, "User has already studied today - no reminder needed");
            }

            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error checking daily study", e);
            return Result.failure();
        }
    }

    /**
     * Calculate current study streak
     * 
     * @return number of consecutive days with completed sessions
     */
    private int calculateStreak(StudySessionDao sessionDao) {
        int streak = 0;
        Calendar checkDay = Calendar.getInstance();
        checkDay.add(Calendar.DAY_OF_YEAR, -1); // Start from yesterday

        for (int i = 0; i < 7; i++) { // Check last 7 days
            Calendar dayStart = (Calendar) checkDay.clone();
            dayStart.set(Calendar.HOUR_OF_DAY, 0);
            dayStart.set(Calendar.MINUTE, 0);

            Calendar dayEnd = (Calendar) checkDay.clone();
            dayEnd.set(Calendar.HOUR_OF_DAY, 23);
            dayEnd.set(Calendar.MINUTE, 59);

            List<StudySession> sessions = sessionDao.getSessionsInRangeSync(
                    dayStart.getTimeInMillis(),
                    dayEnd.getTimeInMillis());

            boolean hasCompletedSession = false;
            if (sessions != null) {
                for (StudySession session : sessions) {
                    if (session.isCompleted()) {
                        hasCompletedSession = true;
                        break;
                    }
                }
            }

            if (hasCompletedSession) {
                streak++;
                checkDay.add(Calendar.DAY_OF_YEAR, -1); // Check previous day
            } else {
                break; // Streak broken
            }
        }

        return streak;
    }
}
