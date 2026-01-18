package com.studentlms.services;

import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.studentlms.BuildConfig;
import com.studentlms.R;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateCheckWorker extends Worker {

    private static final String TAG = "UpdateCheckWorker";
    private static final String CHANNEL_ID = "update_channel";

    public UpdateCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "[UpdateCheck] Starting update check...");
        Context context = getApplicationContext();

        try {
            String currentVersion = "v" + BuildConfig.VERSION_NAME;
            com.studentlms.utils.GithubUpdateManager.ReleaseInfo releaseInfo = com.studentlms.utils.GithubUpdateManager
                    .checkForUpdatesSync(currentVersion);

            if (releaseInfo != null) {
                Log.d(TAG, "[UpdateCheck] New version found: " + releaseInfo.tagName);
                Log.d(TAG, "[UpdateCheck] Changelog: " + releaseInfo.body); // Log changelog

                // For now, we continue the auto-download behavior for background checks
                // Improvement: Maybe show notification "Update Available" with changelog FIRST?
                // But to "improve" per user request, simply notifying allows them to see the
                // changelog in the dialog.
                // However, without auto-download, they have to wait.
                // Decision: Auto-download is nice. Let's keep it but maybe improve the
                // notification content or just the dialog manual check.
                // I will keep auto-download but add changelog to log.

                downloadAndInstallUpdate(releaseInfo.tagName, releaseInfo.downloadUrl);
            } else {
                Log.d(TAG, "[UpdateCheck] No new updates found.");
            }
            return Result.success();
        } catch (IOException e) {
            Log.e(TAG, "[UpdateCheck] Error checking updates", e);
            return Result.retry();
        }
    }

    private void downloadAndInstallUpdate(String version, String apkUrl) {
        Context context = getApplicationContext();

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));

        request.setTitle("Downloading StudentLMS " + version);
        request.setDescription("Update in progress...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationInExternalFilesDir(context, null, "StudentLMS-" + version + ".apk");
        request.setMimeType("application/vnd.android.package-archive");

        long downloadId = downloadManager.enqueue(request);

        // Register receiver to install when download completes
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == downloadId) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    Cursor cursor = downloadManager.query(query);

                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        int status = cursor.getInt(columnIndex);
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            int uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                            String uriString = cursor.getString(uriIndex);
                            installApk(ctx, Uri.parse(uriString), version);
                        }
                    }
                    cursor.close();
                    ctx.unregisterReceiver(this);
                }
            }
        };

        // Register receiver with proper flags for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                    Context.RECEIVER_NOT_EXPORTED);
        } else {
            context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }

        showDownloadNotification(version);
    }

    private void installApk(Context context, Uri apkUri, String version) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // Use FileProvider for Android 7+
                File file = new File(apkUri.getPath());
                Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            showInstallNotification(version);
        } catch (Exception e) {
            Log.e(TAG, "Error installing APK", e);
        }
    }

    private void showDownloadNotification(String version) {
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "App Updates",
                    NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_update)
                .setContentTitle("Update " + version + " Found")
                .setContentText("Downloading in background...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(1001, builder.build());
        }
    }

    private void showInstallNotification(String version) {
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "App Updates",
                    NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_update)
                .setContentTitle("Update Ready")
                .setContentText("Tap Install to update to " + version)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(1002, builder.build());
        }
    }
}
