package com.studentlms.ui.studyplan;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.studentlms.data.dao.LMSAssignmentDao;
import com.studentlms.data.dao.StudySessionDao;
import com.studentlms.data.database.AppDatabase;
import com.studentlms.data.models.StudySession;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudyPlanViewModel extends AndroidViewModel {
    private final StudySessionDao studySessionDao;
    private final LiveData<List<StudySession>> allSessions;
    private final LiveData<List<StudySession>> upcomingSessions;
    private final ExecutorService executorService;

    private final LMSAssignmentDao lmsAssignmentDao; // New field

    public StudyPlanViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application); // Keep this for consistency or refactor as per
                                                                     // instruction
        studySessionDao = database.studySessionDao();
        lmsAssignmentDao = database.lmsAssignmentDao(); // Initialize new DAO
        allSessions = studySessionDao.getAllSessions();
        upcomingSessions = studySessionDao.getUpcomingSessions(System.currentTimeMillis());
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<String>> getDistinctCourseNames() {
        return lmsAssignmentDao.getDistinctCourseNames();
    }

    public LiveData<List<StudySession>> getAllSessions() {
        return allSessions;
    }

    public LiveData<List<StudySession>> getUpcomingSessions() {
        return upcomingSessions;
    }

    public LiveData<List<StudySession>> getSessionsInRange(long startTime, long endTime) {
        return studySessionDao.getSessionsInRange(startTime, endTime);
    }

    public void insertSession(StudySession session) {
        executorService.execute(() -> studySessionDao.insert(session));
    }

    public void updateSession(StudySession session) {
        executorService.execute(() -> studySessionDao.update(session));
    }

    public void deleteSession(StudySession session) {
        executorService.execute(() -> studySessionDao.delete(session));
    }

    public void toggleSessionComplete(StudySession session) {
        session.setCompleted(!session.isCompleted());
        updateSession(session);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
