package com.studentlms.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.studentlms.services.ERPSyncWorker;
import com.studentlms.services.LMSSyncWorker;

import java.util.concurrent.TimeUnit;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Reschedule LMS sync after device reboot
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            PeriodicWorkRequest syncWorkRequest = new PeriodicWorkRequest.Builder(
                    LMSSyncWorker.class,
                    4, TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .build();

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    "lms_sync",
                    ExistingPeriodicWorkPolicy.KEEP,
                    syncWorkRequest);

            // Check if Auto-Sync is enabled for ERP
            boolean autoSyncEnabled = context.getSharedPreferences("StudentLMSPrefs", Context.MODE_PRIVATE)
                    .getBoolean("auto_sync", true);

            if (autoSyncEnabled) {
                PeriodicWorkRequest erpSyncRequest = new PeriodicWorkRequest.Builder(
                        ERPSyncWorker.class,
                        6, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .build();

                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                        "erp_sync",
                        ExistingPeriodicWorkPolicy.KEEP,
                        erpSyncRequest);
            }
        }
    }
}
