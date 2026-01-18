package com.studentlms.services.lms;

import android.util.Log;

import com.studentlms.data.models.LMSAssignment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ERPPortalConnector {

    public static class DashboardData {
        public String studentName;
        public List<LMSAssignment> assignments;

        public DashboardData(String studentName, List<LMSAssignment> assignments) {
            this.studentName = studentName;
            this.assignments = assignments;
        }
    }

    private static final String TAG = "ERPPortalConnector";
    private static final String BASE_URL = "https://erp.ppsu.ac.in";
    private static final String LOGIN_URL = BASE_URL + "/Login.aspx";
    private static final String LMS_DASHBOARD_URL = BASE_URL + "/StudentPanel/LMS/LMS_ContentStudentDashboard.aspx";

    private final OkHttpClient client;

    public ERPPortalConnector() {
        this.client = new OkHttpClient.Builder()
                .cookieJar(new InMemoryCookieJar())
                .followRedirects(true)
                .followSslRedirects(true)
                .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("User-Agent",
                                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                })
                .build();
    }

    private static class InMemoryCookieJar implements CookieJar {
        private final List<Cookie> cookieStore = new ArrayList<>();

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            for (Cookie newCookie : cookies) {
                for (int i = 0; i < cookieStore.size(); i++) {
                    if (cookieStore.get(i).name().equals(newCookie.name())) {
                        cookieStore.remove(i);
                        break;
                    }
                }
                cookieStore.add(newCookie);
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> validCookies = new ArrayList<>();
            for (Cookie cookie : cookieStore) {
                if (cookie.matches(url)) {
                    validCookies.add(cookie);
                }
            }
            return validCookies;
        }
    }

    public boolean login(String username, String password) {
        try {
            // First, get the login page to extract ViewState and other hidden fields
            Request initialRequest = new Request.Builder()
                    .url(LOGIN_URL)
                    .build();

            Response initialResponse = client.newCall(initialRequest).execute();
            String loginPageHtml = initialResponse.body().string();
            Document loginDoc = Jsoup.parse(loginPageHtml);

            // Extract hidden fields
            String viewState = getHiddenField(loginDoc, "__VIEWSTATE");
            String viewStateGenerator = getHiddenField(loginDoc, "__VIEWSTATEGENERATOR");
            String eventValidation = getHiddenField(loginDoc, "__EVENTVALIDATION");

            // Build login form data
            FormBody.Builder formBuilder = new FormBody.Builder()
                    .add("__VIEWSTATE", viewState != null ? viewState : "")
                    .add("__VIEWSTATEGENERATOR", viewStateGenerator != null ? viewStateGenerator : "")
                    .add("__EVENTVALIDATION", eventValidation != null ? eventValidation : "")
                    .add("txtUserName", username)
                    .add("txtPassword", password)
                    .add("btnLogin", "Login");

            // Explicitly set role to "Student" based on the provided HTML source
            // The HTML shows: <input id="rblRole_1" type="radio" name="rblRole"
            // value="Student" ... />
            formBuilder.add("rblRole", "Student");
            Log.d(TAG, "Explicitly set rblRole to Student");

            // Add other hidden fields found in the HTML if they are missing from previous
            // extraction
            // The HTML shows these empty fields:
            // <input type="hidden" name="__LASTFOCUS" id="__LASTFOCUS" value=""/>
            // <input type="hidden" name="__EVENTTARGET" id="__EVENTTARGET" value=""/>
            // <input type="hidden" name="__EVENTARGUMENT" id="__EVENTARGUMENT" value=""/>
            // <input type="hidden" name="__VIEWSTATEENCRYPTED" id="__VIEWSTATEENCRYPTED"
            // value=""/>

            // We'll add them just to be safe and mimic a real browser closely
            formBuilder.add("__LASTFOCUS", "");
            formBuilder.add("__EVENTTARGET", "");
            formBuilder.add("__EVENTARGUMENT", "");
            formBuilder.add("__VIEWSTATEENCRYPTED", "");

            FormBody finalBody = formBuilder.build();

            // Submit login
            Request loginRequest = new Request.Builder()
                    .url(LOGIN_URL)
                    .post(finalBody)
                    .build();

            Response loginResponse = client.newCall(loginRequest).execute();

            // Check if login successful.
            // 1. Check for final URL (redirected to defaults/dashboard)
            // 2. Check for auth cookies in our store?
            // A simple check is to verify we are NOT on the login page anymore.
            String finalUrl = loginResponse.request().url().toString();
            String responseBody = loginResponse.body().string();

            if (finalUrl.contains("Login.aspx") && responseBody.contains("id=\"form1\"")) {
                // We are likely still on login page. Check for error messages if needed.
                // Sometimes "Login failed" text might be present.
                Log.e(TAG, "Login failed - still on login page");
                return false;
            }

            Log.d(TAG, "Login successful (Redirected to: " + finalUrl + ")");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Login error: " + e.getMessage(), e);
            return false;
        }
    }

    public DashboardData fetchAssignments() {
        List<LMSAssignment> assignments = new ArrayList<>();
        String studentName = "";

        try {
            // Navigate to LMS dashboard
            // Cookies are handled automatically by CookieJar now
            Request dashboardRequest = new Request.Builder()
                    .url(LMS_DASHBOARD_URL)
                    .build();

            Response dashboardResponse = client.newCall(dashboardRequest).execute();
            if (!dashboardResponse.isSuccessful()) {
                throw new IOException("Dashboard fetch failed with code: " + dashboardResponse.code());
            }

            String dashboardHtml = dashboardResponse.body().string();
            Document dashboardDoc = Jsoup.parse(dashboardHtml);

            // Verify we are actually logged in (check for login form or title)
            if (dashboardDoc.title().contains("Login") || dashboardDoc.select("form.user-login-5").size() > 0) {
                throw new IOException("Session expired or login failed (redirected to login page)");
            }

            // Parse assignment table
            // Based on HTML source: <div
            // id="ctl00_cphPageContent_Div_PendingSubmissionList"> ... <table>
            Element pendingDiv = dashboardDoc.getElementById("ctl00_cphPageContent_Div_PendingSubmissionList");
            if (pendingDiv == null) {
                Log.w(TAG, "Pending submission div not found");
                return new DashboardData(studentName, assignments);
            }

            // Parse Student Name
            // Selector: span#ctl00_cphPageContent_ucStudentInfoCompact_lblStudentLCName
            Element nameElement = dashboardDoc
                    .getElementById("ctl00_cphPageContent_ucStudentInfoCompact_lblStudentLCName");
            if (nameElement != null) {
                studentName = nameElement.text().trim();
                Log.d(TAG, "Parsed student name: " + studentName);
            } else {
                // Fallback 1: Header username (ctl00_lblCurrentUsername)
                Element topUsername = dashboardDoc.getElementById("ctl00_lblCurrentUsername");
                if (topUsername != null) {
                    studentName = topUsername.text().trim();
                    Log.d(TAG, "Parsed student name from top label: " + studentName);
                } else {
                    // Fallback 2: Dynamic header name
                    Element headerNameElement = dashboardDoc.getElementById("ctl00_lblPageHeaderStudentName_XXXXX");
                    if (headerNameElement != null) {
                        studentName = headerNameElement.text().trim();
                        Log.d(TAG, "Parsed student name from header: " + studentName);
                    } else {
                        Log.w(TAG, "Student name element not found");
                    }
                }
            }

            Elements assignmentRows = pendingDiv.select("table tr");
            Log.d(TAG, "Dashboard HTML length: " + dashboardHtml.length());
            Log.d(TAG, "Page Title: " + dashboardDoc.title());
            Log.d(TAG, "Found " + assignmentRows.size() + " rows in table");

            for (Element row : assignmentRows) {
                // Header row has class "TRDark" or is inside "thead"
                if (row.hasClass("TRDark") || row.parent().tagName().equalsIgnoreCase("thead"))
                    continue; // Skip header row

                Elements cells = row.select("td");
                Log.d(TAG, "Processing row with " + cells.size() + " cells");

                // Based on screenshot:
                // Index 0: Sr (1)
                // Index 1: Subject (SECE3231...)
                // Index 2: Assignment (Practical - 1)
                // Index 3: Last Date (24-01-2026...)
                // Index 4: Updated On
                // Index 5: Action

                if (cells.size() >= 4) {
                    try {
                        String subjectName = cells.get(1).text().trim();
                        String title = cells.get(2).text().trim();
                        String deadlineStr = cells.get(3).text().trim();
                        // Status is not explicitly in a column, but the table itself is "PENDING
                        // SUBMISSION"
                        // We can assume these are all pending/not submitted.
                        // But wait, the previous code assumed status at index 3.
                        // The new table doesn't have a status column, it has "Last Date".
                        // The "Action" column has "Click here to Submit".

                        // Clean up deadline string "24-01-2026 04:30:00 PM (6 Days left)"
                        if (deadlineStr.contains("(")) {
                            deadlineStr = deadlineStr.substring(0, deadlineStr.indexOf("(")).trim();
                        }

                        Log.d(TAG,
                                "Row data: Subject=" + subjectName + ", Title=" + title + ", Deadline=" + deadlineStr);

                        LMSAssignment assignment = new LMSAssignment(
                                subjectName,
                                title,
                                "", // description
                                parseDateString(deadlineStr),
                                "ERP_PORTAL");

                        assignments.add(assignment);
                        Log.d(TAG, "Assignment added: " + title);

                    } catch (Exception e) {
                        Log.w(TAG, "Error parsing assignment row: " + e.getMessage());
                    }
                } else {
                    Log.d(TAG, "Row skipped (insufficient cells): " + row.text());
                }
            }

            Log.d(TAG, "Fetched " + assignments.size() + " pending assignments");
            return new DashboardData(studentName, assignments);

        } catch (Exception e) {
            Log.e(TAG, "Error fetching assignments: " + e.getMessage(), e);
            throw new RuntimeException("Failed to fetch assignments", e);
        }
    }

    private String getHiddenField(Document doc, String fieldName) {
        Element field = doc.selectFirst("input[name=" + fieldName + "]");
        return field != null ? field.attr("value") : "";
    }

    private long parseDateString(String dateStr) {
        try {
            // Try different date formats commonly used
            String[] formats = {
                    "dd-MM-yyyy hh:mm:ss a",
                    "dd/MM/yyyy",
                    "dd-MM-yyyy",
                    "yyyy-MM-dd",
                    "dd MMM yyyy"
            };

            for (String format : formats) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                    Date date = sdf.parse(dateStr);
                    if (date != null) {
                        return date.getTime();
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error parsing date: " + dateStr);
        }

        // Return current time + 7 days as fallback
        return System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000);
    }
}
