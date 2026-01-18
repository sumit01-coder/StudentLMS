package com.studentlms.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.studentlms.data.dao.LMSAssignmentDao;
import com.studentlms.data.database.AppDatabase;
import com.studentlms.data.models.LMSAssignment;
import com.studentlms.services.lms.ERPPortalConnector;
import com.studentlms.utils.CredentialManager;
import com.studentlms.utils.NotificationHelper;

import java.util.List;

public class ERPSyncWorker extends Worker {

    private static final String TAG = "ERPSyncWorker";

    public ERPSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Starting ERP sync...");

        try {
            // Get credentials
            CredentialManager credentialManager = new CredentialManager(getApplicationContext());

            if (!credentialManager.hasCredentials()) {
                Log.w(TAG, "No ERP credentials saved");
                return Result.success();
            }

            String username = credentialManager.getUsername();
            String password = credentialManager.getPassword();

            // Connect to ERP
            ERPPortalConnector connector = new ERPPortalConnector();
            boolean loginSuccess = connector.login(username, password);

            if (!loginSuccess) {
                Log.e(TAG, "ERP login failed");
                return Result.retry();
            }

            // Fetch assignments
            // Fetch assignments and student name
            ERPPortalConnector.DashboardData dashboardData = connector.fetchAssignments();
            List<LMSAssignment> assignments = dashboardData.assignments;
            String studentName = dashboardData.studentName;

            // Save student name if found
            if (studentName != null && !studentName.isEmpty()) {
                credentialManager.saveStudentName(studentName);
                Log.d(TAG, "Saved student name: " + studentName);
            }

            // Save to database
            AppDatabase database = AppDatabase.getInstance(getApplicationContext());
            LMSAssignmentDao assignmentDao = database.lmsAssignmentDao();

            int newAssignmentsCount = 0;
            String lastNewAssignmentTitle = "";

            for (LMSAssignment assignment : assignments) {
                // Check if assignment already exists
                List<LMSAssignment> existing = assignmentDao.getAssignmentsBySubjectSync(
                        assignment.getCourseName());

                boolean exists = false;
                for (LMSAssignment ex : existing) {
                    if (ex.getTitle().equals(assignment.getTitle())) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    assignmentDao.insert(assignment);
                    Log.d(TAG, "Added new assignment: " + assignment.getTitle());
                    newAssignmentsCount++;
                    lastNewAssignmentTitle = assignment.getTitle();
                }
            }

            // Trigger Notification if new assignments found
            if (newAssignmentsCount > 0) {
                String message = newAssignmentsCount == 1 ? "New assignment: " + lastNewAssignmentTitle
                        : "You have " + newAssignmentsCount + " new assignments pending.";

                NotificationHelper.showNewAssignmentNotification(getApplicationContext(), "New ERP Assignment",
                        message);
            }

            // Update last sync time
            getApplicationContext().getSharedPreferences("StudentLMSPrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putLong("last_erp_sync", System.currentTimeMillis())
                    .apply();

            Log.d(TAG, "ERP sync completed successfully. Synced " + assignments.size() + " assignments");
            return Result.success();

        } catch (Exception e) {
            Log.e(TAG, "ERP sync error: " + e.getMessage(), e);
            return Result.retry();
        }
    }
}
