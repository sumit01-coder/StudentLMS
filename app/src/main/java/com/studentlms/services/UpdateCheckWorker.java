package com.studentlms.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.studentlms.BuildConfig;
import com.studentlms.R;

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
        Log.d(TAG, "Checking for updates...");

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/repos/sumit01-coder/StudentLMS/releases/latest")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String json = response.body().string();
                JsonObject release = new Gson().fromJson(json, JsonObject.class);
                String tagName = release.get("tag_name").getAsString(); // e.g., "v1.1.4"
                String htmlUrl = release.get("html_url").getAsString();
                String body = release.has("body") ? release.get("body").getAsString() : "New features available!";

                String currentVersion = "v" + BuildConfig.VERSION_NAME;

                if (!tagName.equals(currentVersion)) {
                    showUpdateNotification(tagName, htmlUrl, body);
                }
                return Result.success();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error checking updates", e);
            return Result.retry();
        }

        return Result.failure();
    }

    private void showUpdateNotification(String newVersion, String url, String releaseNotes) {
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // Create channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "App Updates",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for new app updates");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Intent to open the release URL
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications) // Ensure this exists, or use ic_update if preferred
                .setContentTitle("Update Available: " + newVersion)
                .setContentText("Tap to download. "
                        + (releaseNotes.length() > 50 ? releaseNotes.substring(0, 50) + "..." : releaseNotes))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (notificationManager != null) {
            notificationManager.notify(1001, builder.build());
        }
    }
}
