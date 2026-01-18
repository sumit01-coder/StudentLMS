package com.studentlms.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;

public class UpdateDownloader {

    private static final String TAG = "UpdateDownloader";

    public static void downloadAndInstall(Context context, String version, String apkUrl) {
        Toast.makeText(context, "Downloading update in background...", Toast.LENGTH_LONG).show();

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
                            installApk(ctx, Uri.parse(uriString));
                        }
                    }
                    cursor.close();
                    ctx.unregisterReceiver(this);
                }
            }
        };

        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private static void installApk(Context context, Uri apkUri) {
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

            Toast.makeText(context, "Download complete! Tap Install.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Error installing APK", e);
            Toast.makeText(context, "Error installing update", Toast.LENGTH_SHORT).show();
        }
    }
}
