package com.studentlms.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.studentlms.data.database.AppDatabase;
import com.studentlms.data.models.LMSAssignment;

import java.util.List;

public class DashboardViewModel extends AndroidViewModel {

    private final AppDatabase database;
    private final LiveData<List<LMSAssignment>> upcomingAssignments;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
        upcomingAssignments = database.lmsAssignmentDao()
                .getUpcomingAssignments(System.currentTimeMillis());
    }

    public LiveData<List<LMSAssignment>> getUpcomingAssignments() {
        return upcomingAssignments;
    }

    public void syncAssignments() {
        androidx.work.OneTimeWorkRequest syncRequest = new androidx.work.OneTimeWorkRequest.Builder(
                com.studentlms.services.ERPSyncWorker.class)
                .addTag("manual_sync")
                .build();
        androidx.work.WorkManager.getInstance(getApplication()).enqueue(syncRequest);
    }
}
