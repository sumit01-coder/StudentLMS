package com.studentlms;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.appbar.MaterialToolbar;
import com.studentlms.ui.dashboard.DashboardFragment;
import com.studentlms.ui.profile.ProfileFragment;
import com.studentlms.ui.reminders.RemindersFragment;
import com.studentlms.ui.resources.ResourcesFragment;
import com.studentlms.ui.studyplan.StudyPlanFragment;
import com.studentlms.ui.ai.AIChatFragment;
import com.studentlms.ui.ai.AIChatFragment;
import com.studentlms.utils.AppLockManager;
import com.studentlms.utils.ThemeManager;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import java.util.concurrent.TimeUnit;
import com.studentlms.services.UpdateCheckWorker;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigation;
    private boolean isAuthenticated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply saved theme before setting content view
        ThemeManager.init(this);

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Set up toolbar
        setSupportActionBar(toolbar);

        // Set up bottom navigation listener
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            String title = "";

            int itemId = item.getItemId();
            if (itemId == R.id.navigation_dashboard) {
                selectedFragment = new DashboardFragment();
                title = getString(R.string.nav_dashboard);
            } else if (itemId == R.id.navigation_study_plan) {
                selectedFragment = new StudyPlanFragment();
                title = getString(R.string.nav_study_plan);
            } else if (itemId == R.id.navigation_resources) {
                selectedFragment = new ResourcesFragment();
                title = getString(R.string.nav_resources);
            } else if (itemId == R.id.navigation_ai_chat) {
                selectedFragment = new AIChatFragment();
                title = getString(R.string.nav_ai_chat);
            } else if (itemId == R.id.navigation_reminders) {
                selectedFragment = new RemindersFragment();
                title = getString(R.string.nav_reminders);
            }

            if (selectedFragment != null) {
                toolbar.setTitle(title);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Load default fragment
        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.navigation_dashboard);
        }

        scheduleUpdateCheck();
    }

    private void scheduleUpdateCheck() {
        PeriodicWorkRequest updateRequest = new PeriodicWorkRequest.Builder(
                UpdateCheckWorker.class,
                15, TimeUnit.MINUTES) // Changed from 12 hours to 15 minutes for testing
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "update_check",
                ExistingPeriodicWorkPolicy.KEEP,
                updateRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            // Open Profile fragment
            toolbar.setTitle(getString(R.string.nav_profile));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, new ProfileFragment())
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if app lock is enabled and we need to authenticate
        if (AppLockManager.shouldShowLock(this) && !isAuthenticated) {
            showBiometricLock();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Record when app was paused
        AppLockManager.recordPauseTime(this);
        isAuthenticated = false;
    }

    private void showBiometricLock() {
        AppLockManager.showBiometricPrompt(this, new AppLockManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationSuccess() {
                isAuthenticated = true;
            }

            @Override
            public void onAuthenticationError(String error) {
                // If authentication fails, close the app
                finishAffinity();
            }

            @Override
            public void onAuthenticationFailed() {
                // User can retry
            }

            @Override
            public void onAuthenticationCancelled() {
                // If user cancels, close the app
                finishAffinity();
            }
        });
    }
}
