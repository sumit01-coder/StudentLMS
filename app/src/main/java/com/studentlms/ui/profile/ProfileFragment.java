package com.studentlms.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.studentlms.R;
import com.studentlms.services.ERPSyncWorker;
import com.studentlms.utils.AppLockManager;
import com.studentlms.utils.CredentialManager;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import android.content.Intent;
import android.net.Uri;
import com.studentlms.BuildConfig;
import com.studentlms.utils.UpdateDownloader;
import com.google.gson.JsonArray;
import com.studentlms.utils.ThemeManager;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.chip.Chip;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private TextView studyStreakValue;
    private TextView totalStudyHours;
    private RecyclerView accountsRecycler;
    private LinearLayout emptyAccountsState;
    private MaterialButton btnAddAccount;
    private SwitchMaterial switchNotifications;
    private SwitchMaterial switchAutoSync;
    private SwitchMaterial switchAppLock;

    private SharedPreferences preferences;

    // ERP components
    private TextView erpStatusText;
    private MaterialButton btnConnectErp;
    private MaterialButton btnSyncErp;
    private MaterialButton btnCheckUpdates;
    private MaterialButton btnForceBackgroundCheck;
    private ChipGroup chipGroupTheme;
    private Chip chipThemeLight, chipThemeDark, chipThemeSystem;
    private CredentialManager credentialManager;
    private android.widget.FrameLayout btnNotificationsHeader;
    private View notificationBadge;

    @Nullable
    private void checkForUpdates() {
        Toast.makeText(getContext(), "Checking for updates...", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://api.github.com/repos/sumit01-coder/StudentLMS/releases/latest")
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String json = response.body().string();
                        JsonObject release = new Gson().fromJson(json, JsonObject.class);
                        String tagName = release.get("tag_name").getAsString();
                        JsonArray assets = release.has("assets") ? release.getAsJsonArray("assets") : new JsonArray();

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> handleUpdateResponse(tagName, assets));
                        }
                    } else {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> Toast
                                    .makeText(getContext(), "Failed to check updates", Toast.LENGTH_SHORT).show());
                        }
                    }
                }
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(
                            () -> Toast.makeText(getContext(), "Error checking updates", Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }

    private void handleUpdateResponse(String latestVersion, JsonArray assets) {
        // Assuming tagName format is "v1.1.3" and versionName is "1.1.3"
        String currentVersion = "v" + BuildConfig.VERSION_NAME;

        if (!latestVersion.equals(currentVersion)) {
            // Find APK download URL
            String apkUrl = null;
            for (int i = 0; i < assets.size(); i++) {
                JsonObject asset = assets.get(i).getAsJsonObject();
                String name = asset.get("name").getAsString();
                if (name.endsWith(".apk")) {
                    apkUrl = asset.get("browser_download_url").getAsString();
                    break;
                }
            }

            if (apkUrl != null) {
                String finalApkUrl = apkUrl;
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Update Available")
                        .setMessage("New version " + latestVersion + " is available.\nCurrent: " + currentVersion)
                        .setPositiveButton("Download", (dialog, which) -> {
                            UpdateDownloader.downloadAndInstall(requireContext(), latestVersion, finalApkUrl);
                        })
                        .setNegativeButton("Later", null)
                        .show();
            } else {
                Toast.makeText(getContext(), "Update info unavailable", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "You are using the latest version: " + currentVersion, Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        setupViewModel();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        studyStreakValue = view.findViewById(R.id.study_streak_value);
        totalStudyHours = view.findViewById(R.id.total_study_hours);
        accountsRecycler = view.findViewById(R.id.accounts_recycler);
        emptyAccountsState = view.findViewById(R.id.empty_accounts_state);
        btnAddAccount = view.findViewById(R.id.btn_add_account);
        switchNotifications = view.findViewById(R.id.switch_notifications);
        switchAutoSync = view.findViewById(R.id.switch_auto_sync);
        switchAppLock = view.findViewById(R.id.switch_app_lock);

        // Get shared preferences
        preferences = requireContext().getSharedPreferences("StudentLMSPrefs", Context.MODE_PRIVATE);

        // Load saved preferences
        loadPreferences();

        // Initialize ERP components
        erpStatusText = view.findViewById(R.id.erp_status_text);
        btnConnectErp = view.findViewById(R.id.btn_connect_erp);
        btnSyncErp = view.findViewById(R.id.btn_sync_erp);

        // Header Notification Icon
        btnNotificationsHeader = view.findViewById(R.id.btn_notifications);
        notificationBadge = view.findViewById(R.id.notification_badge);
        btnCheckUpdates = view.findViewById(R.id.btn_check_updates);
        btnForceBackgroundCheck = view.findViewById(R.id.btn_force_background_check);

        // Theme selector
        chipGroupTheme = view.findViewById(R.id.chip_group_theme);
        chipThemeLight = view.findViewById(R.id.chip_theme_light);
        chipThemeDark = view.findViewById(R.id.chip_theme_dark);
        chipThemeSystem = view.findViewById(R.id.chip_theme_system);

        credentialManager = new CredentialManager(requireContext());

        // Update ERP status only if components exist
        if (erpStatusText != null && btnConnectErp != null) {
            updateERPStatus();
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Observe connected accounts
        viewModel.getConnectedAccounts().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts == null || accounts.isEmpty()) {
                accountsRecycler.setVisibility(View.GONE);
                emptyAccountsState.setVisibility(View.VISIBLE);
            } else {
                accountsRecycler.setVisibility(View.VISIBLE);
                emptyAccountsState.setVisibility(View.GONE);
                // TODO: Set up adapter for LMS accounts
            }
        });

        // For now, set default statistics
        // In a real app, these would be calculated from database
        studyStreakValue.setText("0");
        totalStudyHours.setText("0h");
    }

    private void setupListeners() {
        btnAddAccount.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Add LMS Account - Coming in future update",
                    Toast.LENGTH_SHORT).show();
        });

        // ERP Portal
        btnConnectErp.setOnClickListener(v -> showERPLoginDialog());

        btnSyncErp.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Syncing assignments...", Toast.LENGTH_SHORT).show();
            // Trigger one-time sync
            androidx.work.Constraints constraints = new androidx.work.Constraints.Builder()
                    .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                    .build();

            WorkManager.getInstance(requireContext())
                    .enqueue(new androidx.work.OneTimeWorkRequest.Builder(ERPSyncWorker.class)
                            .setConstraints(constraints)
                            .setBackoffCriteria(
                                    androidx.work.BackoffPolicy.LINEAR,
                                    androidx.work.OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                    TimeUnit.MILLISECONDS)
                            .addTag("manual_sync")
                            .build());
        });
        if (btnNotificationsHeader != null) {
            btnNotificationsHeader.setOnClickListener(v -> {
                showWhatsNewDialog();
                // Hide badge after viewing
                if (notificationBadge != null) {
                    notificationBadge.setVisibility(View.GONE);
                }
            });
        }

        if (btnCheckUpdates != null) {
            btnCheckUpdates.setOnClickListener(v -> checkForUpdates());
        }

        if (btnForceBackgroundCheck != null) {
            btnForceBackgroundCheck.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Forcing background update check...", Toast.LENGTH_SHORT).show();
                androidx.work.OneTimeWorkRequest testRequest = new androidx.work.OneTimeWorkRequest.Builder(
                        com.studentlms.services.UpdateCheckWorker.class)
                        .build();
                androidx.work.WorkManager.getInstance(requireContext()).enqueue(testRequest);
                Toast.makeText(getContext(), "Check logcat for [UpdateCheck] logs", Toast.LENGTH_LONG).show();
            });
        }

        // Theme selector
        setupThemeSelector();

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Save notification preference
            Toast.makeText(getContext(), "Notifications " + (isChecked ? "enabled" : "disabled"),
                    Toast.LENGTH_SHORT).show();
        });

        switchAutoSync.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("auto_sync", isChecked).apply();

            if (isChecked) {
                scheduleERPSync();
                Toast.makeText(getContext(), "Auto-sync enabled (Every 6h)", Toast.LENGTH_SHORT).show();
            } else {
                WorkManager.getInstance(requireContext()).cancelUniqueWork("erp_sync");
                Toast.makeText(getContext(), "Auto-sync disabled", Toast.LENGTH_SHORT).show();
            }
        });

        // App Lock toggle
        switchAppLock.setOnCheckedChangeListener((buttonView, isChecked) ->

        {
            if (isChecked) {
                // Check if device supports biometric authentication
                if (!AppLockManager.canAuthenticate(requireContext())) {
                    switchAppLock.setChecked(false);
                    Toast.makeText(getContext(),
                            "Device authentication not available. Please set up fingerprint, face unlock, or PIN in device settings.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Verify with biometric before enabling
                AppLockManager.showBiometricPrompt(requireActivity(),
                        new AppLockManager.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationSuccess() {
                                AppLockManager.setAppLockEnabled(requireContext(), true);
                                Toast.makeText(getContext(), "App Lock enabled", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAuthenticationError(String error) {
                                switchAppLock.setChecked(false);
                                Toast.makeText(getContext(), "Authentication failed: " + error,
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                // User can retry
                            }

                            @Override
                            public void onAuthenticationCancelled() {
                                switchAppLock.setChecked(false);
                            }
                        });
            } else

            {
                AppLockManager.setAppLockEnabled(requireContext(), false);
                Toast.makeText(getContext(), "App Lock disabled", Toast.LENGTH_SHORT).show();
            }
        });

        // Theme selector
        themeChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int chipId = checkedIds.get(0);
                int themeMode;
                String themeName;

                if (chipId == R.id.chip_theme_light) {
                    themeMode = AppCompatDelegate.MODE_NIGHT_NO;
                    themeName = "light";
                } else if (chipId == R.id.chip_theme_dark) {
                    themeMode = AppCompatDelegate.MODE_NIGHT_YES;
                    themeName = "dark";
                } else {
                    themeMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                    themeName = "system";
                }

                // Save preference
                preferences.edit().putString("theme", themeName).apply();

                // Apply theme
                AppCompatDelegate.setDefaultNightMode(themeMode);
                Toast.makeText(getContext(), "Theme updated", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void loadPreferences() {
        // Load saved preferences
        boolean notifications = preferences.getBoolean("notifications", true);
        boolean autoSync = preferences.getBoolean("auto_sync", true);
        boolean appLock = preferences.getBoolean("app_lock", false);
        String theme = preferences.getString("theme", "system");

        switchNotifications.setChecked(notifications);
        switchAutoSync.setChecked(autoSync);
        switchAppLock.setChecked(appLock);

    }

    private void showERPLoginDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_erp_login, null);
        TextInputEditText inputUsername = dialogView.findViewById(R.id.input_erp_username);
        TextInputEditText inputPassword = dialogView.findViewById(R.id.input_erp_password);

        // Pre-fill if already saved
        if (credentialManager.hasCredentials()) {
            inputUsername.setText(credentialManager.getUsername());
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Connect to ERP Portal")
                .setView(dialogView)
                .setPositiveButton("Connect", (dialog, which) -> {
                    String username = inputUsername.getText() != null ? inputUsername.getText().toString() : "";
                    String password = inputPassword.getText() != null ? inputPassword.getText().toString() : "";

                    if (username.isEmpty() || password.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter credentials",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        credentialManager.saveCredentials(username, password);
                        Toast.makeText(getContext(), "ERP credentials saved!",
                                Toast.LENGTH_SHORT).show();

                        // Schedule periodic sync
                        scheduleERPSync();

                        // Update UI
                        updateERPStatus();

                        // Trigger first sync
                        WorkManager.getInstance(requireContext())
                                .enqueue(new androidx.work.OneTimeWorkRequest.Builder(ERPSyncWorker.class)
                                        .setConstraints(new androidx.work.Constraints.Builder()
                                                .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                                                .build())
                                        .addTag("manual_sync")
                                        .build());

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error saving credentials: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateERPStatus() {
        if (credentialManager.hasCredentials()) {
            String displayHeader = "Connected";
            String studentName = credentialManager.getStudentName();
            if (studentName != null && !studentName.isEmpty()) {
                displayHeader = "Connected as " + studentName;
            } else {
                displayHeader = "Connected as " + credentialManager.getUsername();
            }
            erpStatusText.setText(displayHeader);
            btnConnectErp.setText("Update Credentials");
            btnSyncErp.setVisibility(View.VISIBLE);

            // Show last sync time
            long lastSync = preferences.getLong("last_erp_sync", 0);
            if (lastSync > 0) {
                long hours = (System.currentTimeMillis() - lastSync) / (1000 * 60 * 60);
                String namePart = (studentName != null && !studentName.isEmpty()) ? studentName
                        : credentialManager.getUsername();
                erpStatusText.setText("Synced " + hours + "h ago • " + namePart);
            }
        } else {
            erpStatusText.setText("Not connected");
            btnConnectErp.setText("Connect to ERP");
            btnSyncErp.setVisibility(View.GONE);
        }
    }

    private void scheduleERPSync() {
        // Schedule periodic sync every 6 hours
        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest.Builder(
                ERPSyncWorker.class, 6, TimeUnit.HOURS)
                .setConstraints(new androidx.work.Constraints.Builder()
                        .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                        .build())
                .setBackoffCriteria(
                        androidx.work.BackoffPolicy.LINEAR,
                        androidx.work.PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(requireContext())
                .enqueueUniquePeriodicWork(
                        "erp_sync",
                        androidx.work.ExistingPeriodicWorkPolicy.REPLACE,
                        syncRequest);
    }

    private void showWhatsNewDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("What's New in StudentLMS")
                .setIcon(R.drawable.ic_notifications)
                .setMessage("We've updated the app with some exciting new features:\n\n" +
                        "• Auto Sync: Your assignments now sync automatically in the background.\n" +
                        "• Enhanced Security: Your credentials and name are now encrypted.\n" +
                        "• Brand New Look: Check out the new, modern app icon!\n" +
                        "• Notifications: Get alerted instantly for new assignments.")
                .setPositiveButton("Awesome!", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void setupThemeSelector() {
        // Load saved theme preference
        int currentTheme = ThemeManager.getThemeMode(requireContext());
        switch (currentTheme) {
            case ThemeManager.THEME_LIGHT:
                chipThemeLight.setChecked(true);
                break;
            case ThemeManager.THEME_DARK:
                chipThemeDark.setChecked(true);
                break;
            case ThemeManager.THEME_SYSTEM:
            default:
                chipThemeSystem.setChecked(true);
                break;
        }

        // Set up theme change listener
        chipGroupTheme.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);
                int themeMode;

                if (checkedId == chipThemeLight.getId()) {
                    themeMode = ThemeManager.THEME_LIGHT;
                } else if (checkedId == chipThemeDark.getId()) {
                    themeMode = ThemeManager.THEME_DARK;
                } else {
                    themeMode = ThemeManager.THEME_SYSTEM;
                }

                ThemeManager.saveThemeMode(requireContext(), themeMode);
            }
        });
    }
}
