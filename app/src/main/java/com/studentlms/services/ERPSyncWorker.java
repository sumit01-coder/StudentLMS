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

import java.util.ArrayList;
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

            // Check network connectivity first
            if (!isNetworkAvailable()) {
                Log.w(TAG, "No network connectivity - skipping ERP sync");
                return Result.success(); // Success to avoid retry spam when offline
            }

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
                boolean exists = false;

                // 1. Check by unique LMS ID (if generated)
                if (assignment.getLmsId() != null && !assignment.getLmsId().isEmpty()) {
                    if (assignmentDao.countByLmsId(assignment.getLmsId()) > 0) {
                        exists = true;
                    }
                }

                // 2. Fallback check by Title + Course Name (if ID missing or legacy)
                if (!exists) {
                    if (assignmentDao.countByTitleAndSubject(assignment.getTitle(), assignment.getCourseName()) > 0) {
                        exists = true;
                    }
                }

                if (!exists) {
                    assignmentDao.insert(assignment);
                    Log.d(TAG, "Added new assignment: " + assignment.getTitle());
                    newAssignmentsCount++;
                    lastNewAssignmentTitle = assignment.getTitle();
                } else {
                    Log.d(TAG, "Skipping duplicate assignment: " + assignment.getTitle());
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

        } catch (java.net.SocketTimeoutException e) {
            Log.w(TAG, "Network timeout - will retry later: " + e.getMessage());
            return Result.retry();
        } catch (java.io.IOException e) {
            Log.w(TAG, "Network error - will retry later: " + e.getMessage());
            return Result.retry();
        } catch (Exception e) {
            Log.e(TAG, "ERP sync error: " + e.getMessage(), e);
            return Result.retry();
        }
    }

    /**
     * Check if device has network connectivity
     */
    private boolean isNetworkAvailable() {
        android.net.ConnectivityManager connectivityManager = (android.net.ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            android.net.Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }

            android.net.NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);

            return capabilities != null && (capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            android.net.NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
    }
}
