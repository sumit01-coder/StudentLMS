package com.studentlms.ui.subjects;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.studentlms.data.dao.SubjectDao;
import com.studentlms.data.database.AppDatabase;
import com.studentlms.data.models.Subject;

import com.studentlms.utils.CredentialManager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel for Subjects screen
 */
public class SubjectsViewModel extends AndroidViewModel {
    private static final String TAG = "SubjectsViewModel";

    private final SubjectDao subjectDao;
    private final LiveData<List<Subject>> allSubjects;
    private final MutableLiveData<Integer> filterSemester = new MutableLiveData<>(0); // 0 = All
    private final MutableLiveData<Boolean> isSyncing = new MutableLiveData<>(false);
    private final MutableLiveData<String> syncError = new MutableLiveData<>();
    private final ExecutorService executorService;
    private final com.studentlms.services.lms.ERPPortalConnector erpConnector;
    private final CredentialManager credentialManager;

    public SubjectsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        subjectDao = database.subjectDao();
        allSubjects = subjectDao.getAllSubjects();
        executorService = Executors.newSingleThreadExecutor();
        erpConnector = new com.studentlms.services.lms.ERPPortalConnector();
        credentialManager = new CredentialManager(application);

        // Load current semester from preferences
        android.content.SharedPreferences prefs = application.getSharedPreferences("StudentLMSPrefs",
                android.content.Context.MODE_PRIVATE);
        int currentSemester = prefs.getInt("current_semester", 6);
        filterSemester.setValue(currentSemester);
    }

    public LiveData<List<Subject>> getFilteredSubjects() {
        return Transformations.switchMap(filterSemester, semester -> {
            if (semester == 0) {
                return allSubjects;
            } else {
                return subjectDao.getSubjectsBySemester(semester);
            }
        });
    }

    public LiveData<Boolean> getIsSyncing() {
        return isSyncing;
    }

    public LiveData<String> getSyncError() {
        return syncError;
    }

    public void setFilterSemester(int semester) {
        filterSemester.setValue(semester);
    }

    /**
     * Sync subjects from ERP portal
     */
    public void syncSubjectsFromERP() {
        isSyncing.setValue(true);
        syncError.setValue(null);

        executorService.execute(() -> {
            try {
                // Check network connectivity first
                if (!isNetworkAvailable()) {
                    syncError.postValue("No internet connection. Please check your WiFi or mobile data.");
                    isSyncing.postValue(false);
                    return;
                }

                // Get ERP credentials
                String username = credentialManager.getUsername();
                String password = null;
                try {
                    password = credentialManager.getPassword();
                } catch (Exception e) {
                    syncError.postValue("Failed to decrypt password");
                    isSyncing.postValue(false);
                    return;
                }

                if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                    syncError.postValue("ERP credentials not found. Please login in Profile.");
                    isSyncing.postValue(false);
                    return;
                }

                // Login to ERP
                boolean loginSuccess = erpConnector.login(username, password);
                if (!loginSuccess) {
                    syncError.postValue("ERP login failed. Check your credentials.");
                    isSyncing.postValue(false);
                    return;
                }

                // Fetch subjects from ERP dashboard
                List<Subject> scrapedSubjects = erpConnector.fetchSubjects();

                if (scrapedSubjects.isEmpty()) {
                    Log.w(TAG, "No subjects found from ERP");
                    syncError.postValue("No subjects found. Check your ERP credentials or try again later.");
                }

                // Save to database
                for (Subject subject : scrapedSubjects) {
                    subjectDao.insert(subject);
                }

                Log.i(TAG, "Synced " + scrapedSubjects.size() + " subjects");
                isSyncing.postValue(false);

            } catch (Exception e) {
                Log.e(TAG, "Error syncing subjects", e);
                syncError.postValue("Failed to sync subjects: " + e.getMessage());
                isSyncing.postValue(false);
            }
        });
    }

    public void insertSubject(Subject subject) {
        executorService.execute(() -> subjectDao.insert(subject));
    }

    public void deleteSubject(Subject subject) {
        executorService.execute(() -> subjectDao.delete(subject));
    }

    /**
     * Check if network connectivity is available
     */
    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplication()
                    .getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error checking network", e);
            return false;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
