package com.studentlms.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class AppLockManager {

    private static final String PREFS_NAME = "StudentLMSPrefs";
    private static final String KEY_APP_LOCK = "app_lock";
    private static final String KEY_LAST_PAUSE_TIME = "last_pause_time";
    private static final long LOCK_TIMEOUT = 60000; // 1 minute in milliseconds

    public static boolean isAppLockEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_APP_LOCK, false);
    }

    public static void setAppLockEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_APP_LOCK, enabled).apply();
    }

    public static void recordPauseTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putLong(KEY_LAST_PAUSE_TIME, System.currentTimeMillis()).apply();
    }

    public static boolean shouldShowLock(Context context) {
        if (!isAppLockEnabled(context)) {
            return false;
        }

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long lastPauseTime = prefs.getLong(KEY_LAST_PAUSE_TIME, 0);
        long currentTime = System.currentTimeMillis();

        // Show lock if more than LOCK_TIMEOUT has passed
        return (currentTime - lastPauseTime) > LOCK_TIMEOUT;
    }

    public static boolean canAuthenticate(Context context) {
        BiometricManager biometricManager = BiometricManager.from(context);
        int canAuthenticate = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL);
        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS;
    }

    public static void showBiometricPrompt(FragmentActivity activity,
            AuthenticationCallback callback) {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock Student LMS")
                .setSubtitle("Authenticate to access your study materials")
                .setAllowedAuthenticators(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG |
                                BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();

        BiometricPrompt biometricPrompt = new BiometricPrompt(activity,
                ContextCompat.getMainExecutor(activity),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                                errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                            callback.onAuthenticationCancelled();
                        } else {
                            callback.onAuthenticationError(errString.toString());
                        }
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        callback.onAuthenticationSuccess();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        callback.onAuthenticationFailed();
                    }
                });

        biometricPrompt.authenticate(promptInfo);
    }

    public interface AuthenticationCallback {
        void onAuthenticationSuccess();

        void onAuthenticationError(String error);

        void onAuthenticationFailed();

        void onAuthenticationCancelled();
    }
}
