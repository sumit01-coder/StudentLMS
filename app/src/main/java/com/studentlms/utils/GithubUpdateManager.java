package com.studentlms.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GithubUpdateManager {

    private static final String TAG = "GithubUpdateManager";
    private static final String LATEST_RELEASE_URL = "https://api.github.com/repos/sumit01-coder/StudentLMS/releases/latest";

    public interface UpdateCheckCallback {
        void onUpdateFound(ReleaseInfo releaseInfo);

        void onNoUpdateFound(String currentVersion, String latestVersion);

        void onError(String error);
    }

    public static class ReleaseInfo {
        public String tagName;
        public String name;
        public String body;
        public String downloadUrl;

        public ReleaseInfo(String tagName, String name, String body, String downloadUrl) {
            this.tagName = tagName;
            this.name = name;
            this.body = body;
            this.downloadUrl = downloadUrl;
        }
    }

    public static void checkForUpdates(String currentVersionVal, UpdateCheckCallback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(LATEST_RELEASE_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Update check failed", e);
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(response, currentVersionVal, callback);
            }
        });
    }

    public static ReleaseInfo checkForUpdatesSync(String currentVersionVal) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(LATEST_RELEASE_URL)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return parseResponse(response, currentVersionVal);
        }
    }

    private static void handleResponse(Response response, String currentVersionVal, UpdateCheckCallback callback) {
        try {
            Log.d(TAG, "Checking updates against: " + LATEST_RELEASE_URL);
            if (!response.isSuccessful()) {
                Log.e(TAG, "GitHub API failed with code: " + response.code());
                callback.onError("Request failed: " + response.code());
                return;
            }
            ReleaseInfo info = parseResponse(response, currentVersionVal);
            if (info != null) {
                Log.d(TAG, "Update available: " + info.tagName);
                callback.onUpdateFound(info);
            } else {
                Log.d(TAG, "No update found or same version.");
                // We might not know latest if parse returns null for same version,
                // so we pass currentVersionVal as latest for onNoUpdateFound
                callback.onNoUpdateFound(currentVersionVal, currentVersionVal);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling update response", e);
            callback.onError(e.getMessage());
        }
    }

    private static ReleaseInfo parseResponse(Response response, String currentVersionVal) throws IOException {
        if (response.body() == null) {
            Log.e(TAG, "Response body is null");
            return null;
        }

        String json = response.body().string();
        // Log.v(TAG, "JSON Response: " + json); // Uncomment for full JSON debugging

        JsonObject release = new Gson().fromJson(json, JsonObject.class);

        String tagName = release.get("tag_name").getAsString();
        Log.d(TAG, "Latest Release Tag: " + tagName);

        String name = release.has("name") && !release.get("name").isJsonNull()
                ? release.get("name").getAsString()
                : tagName;
        String body = release.has("body") && !release.get("body").isJsonNull()
                ? release.get("body").getAsString()
                : "No release notes.";

        // Find APK asset
        String apkUrl = null;
        if (release.has("assets")) {
            JsonArray assets = release.getAsJsonArray("assets");
            Log.d(TAG, "Found " + assets.size() + " assets.");
            for (int i = 0; i < assets.size(); i++) {
                JsonObject asset = assets.get(i).getAsJsonObject();
                String assetName = asset.get("name").getAsString();
                Log.d(TAG, "Asset: " + assetName);
                if (assetName.endsWith(".apk")) {
                    apkUrl = asset.get("browser_download_url").getAsString();
                    Log.d(TAG, "Found APK URL: " + apkUrl);
                    break;
                }
            }
        } else {
            Log.w(TAG, "No assets found in release.");
        }

        String normalizedCurrent = currentVersionVal.startsWith("v") ? currentVersionVal : "v" + currentVersionVal;
        String normalizedLatest = tagName.startsWith("v") ? tagName : "v" + tagName;

        Log.d(TAG, "Comparing Current: " + normalizedCurrent + " vs Latest: " + normalizedLatest);

        if (!normalizedCurrent.equals(normalizedLatest) && apkUrl != null) {
            return new ReleaseInfo(tagName, name, body, apkUrl);
        }
        return null;
    }
}
