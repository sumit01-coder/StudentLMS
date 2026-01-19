package com.studentlms.ui.studyplan;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.studentlms.R;
import com.studentlms.data.models.StudySession;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudyPlanFragment extends Fragment {

    private StudyPlanViewModel viewModel;
    private StudySessionAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private FloatingActionButton fabAdd;
    private TextView completedCountText;
    private TextView totalHoursText;
    private java.util.concurrent.ExecutorService executorService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study_plan, container, false);

        executorService = java.util.concurrent.Executors.newSingleThreadExecutor();
        initViews(view);
        setupViewModel();
        setupRecyclerView();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.sessions_recycler);
        emptyState = view.findViewById(R.id.empty_state);
        fabAdd = view.findViewById(R.id.fab_add_session);
        completedCountText = view.findViewById(R.id.completed_count);
        totalHoursText = view.findViewById(R.id.total_hours);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(StudyPlanViewModel.class);

        viewModel.getAllSessions().observe(getViewLifecycleOwner(), sessions -> {
            adapter.setSessions(sessions);

            // Show/hide empty state
            if (sessions == null || sessions.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
            }

            // Update statistics
            updateStatistics(sessions);
        });
    }

    private void setupRecyclerView() {
        adapter = new StudySessionAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnSessionActionListener(new StudySessionAdapter.OnSessionActionListener() {
            @Override
            public void onToggleComplete(StudySession session) {
                viewModel.toggleSessionComplete(session);
            }

            @Override
            public void onDeleteSession(StudySession session) {
                viewModel.deleteSession(session);
            }
        });
    }

    private void setupListeners() {
        fabAdd.setOnClickListener(v -> showAddSessionDialog());
    }

    private void updateStatistics(List<StudySession> sessions) {
        int completedCount = 0;
        long totalMillis = 0;

        for (StudySession session : sessions) {
            if (session.isCompleted()) {
                completedCount++;
            }
            totalMillis += (session.getEndTime() - session.getStartTime());
        }

        completedCountText.setText(String.valueOf(completedCount));

        long totalHours = totalMillis / (1000 * 60 * 60);
        totalHoursText.setText(totalHours + "h");
    }

    private void showAddSessionDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_study_session, null);

        // UI References
        android.widget.Spinner spinnerSubject = dialogView.findViewById(R.id.spinner_subject);
        TextInputEditText inputNotes = dialogView.findViewById(R.id.input_notes);
        TextView selectedTimesText = dialogView.findViewById(R.id.selected_times_text);
        com.google.android.material.checkbox.MaterialCheckBox checkboxRemindMe = dialogView
                .findViewById(R.id.checkbox_remind_me);
        android.widget.Spinner spinnerReminderTime = dialogView.findViewById(R.id.spinner_reminder_time);
        TextView reminderTimeLabel = dialogView.findViewById(R.id.reminder_time_label);

        // Toggle reminder time spinner visibility
        checkboxRemindMe.setOnCheckedChangeListener((buttonView, isChecked) -> {
            spinnerReminderTime.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            reminderTimeLabel.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        final Calendar startCal = Calendar.getInstance();
        final Calendar endCal = Calendar.getInstance();
        endCal.add(Calendar.HOUR_OF_DAY, 1); // Default 1 hour later

        final boolean[] startTimeSet = { false };
        final boolean[] endTimeSet = { false };

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());

        // Setup Course Names Spinner
        android.widget.ArrayAdapter<String> spinnerAdapter = new android.widget.ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(spinnerAdapter);

        // Observe course names from DB
        viewModel.getDistinctCourseNames().observe(getViewLifecycleOwner(), courseNames -> {
            if (courseNames != null && !courseNames.isEmpty()) {
                spinnerAdapter.clear();
                spinnerAdapter.addAll(courseNames);
                spinnerAdapter.notifyDataSetChanged();
            } else {
                // Fallback if no courses found? Add a "General" option or similar
                spinnerAdapter.add("General Study");
            }
        });

        // Start time picker
        dialogView.findViewById(R.id.btn_pick_start_time).setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(requireContext(),
                    (view, hourOfDay, minute) -> {
                        startCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        startCal.set(Calendar.MINUTE, minute);
                        startTimeSet[0] = true;
                        updateSelectedTimesText(selectedTimesText, startCal, endCal,
                                startTimeSet[0], endTimeSet[0], timeFormat);
                    },
                    startCal.get(Calendar.HOUR_OF_DAY),
                    startCal.get(Calendar.MINUTE),
                    false);
            timePicker.show();
        });

        // End time picker
        dialogView.findViewById(R.id.btn_pick_end_time).setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(requireContext(),
                    (view, hourOfDay, minute) -> {
                        endCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        endCal.set(Calendar.MINUTE, minute);
                        endTimeSet[0] = true;
                        updateSelectedTimesText(selectedTimesText, startCal, endCal,
                                startTimeSet[0], endTimeSet[0], timeFormat);
                    },
                    endCal.get(Calendar.HOUR_OF_DAY),
                    endCal.get(Calendar.MINUTE),
                    false);
            timePicker.show();
        });

        // Show dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Setup button listeners
        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.btn_save).setOnClickListener(v -> {
            String selectedSubject = (String) spinnerSubject.getSelectedItem();
            if (selectedSubject == null || selectedSubject.isEmpty()) {
                selectedSubject = "General Study";
            }

            String notes = inputNotes.getText() != null ? inputNotes.getText().toString().trim() : "";

            StudySession session = new StudySession(selectedSubject, startCal.getTimeInMillis(),
                    endCal.getTimeInMillis(), notes, false);

            // Set reminder preferences
            boolean hasReminder = checkboxRemindMe.isChecked();
            session.setHasReminder(hasReminder);

            if (hasReminder) {
                // Parse reminder time from spinner (15, 30, or 60 minutes)
                int reminderMinutes = 15; // default
                int selectedPos = spinnerReminderTime.getSelectedItemPosition();
                switch (selectedPos) {
                    case 0:
                        reminderMinutes = 15;
                        break;
                    case 1:
                        reminderMinutes = 30;
                        break;
                    case 2:
                        reminderMinutes = 60;
                        break;
                }
                session.setReminderMinutesBefore(reminderMinutes);

                // Make variables effectively final for lambda
                final String finalSubject = selectedSubject;
                final long finalStartTime = startCal.getTimeInMillis();
                final int finalReminderMinutes = reminderMinutes;

                // Insert session first to get its ID
                executorService.execute(() -> {
                    long sessionId = viewModel.insertSessionAndGetId(session);
                    session.setId((int) sessionId);

                    // Schedule the reminder on the main thread
                    requireActivity().runOnUiThread(() -> {
                        scheduleStudySessionReminder(finalSubject, finalStartTime,
                                finalReminderMinutes, (int) sessionId);
                    });
                });
            } else {
                viewModel.insertSession(session);
            }

            dialog.dismiss();
        });
    }

    private void scheduleStudySessionReminder(String subject, long startTime, int reminderMinutesBefore,
            int sessionId) {
        long delay = startTime - System.currentTimeMillis() - (reminderMinutesBefore * 60 * 1000);

        if (delay > 0) {
            // Create input data for the worker
            androidx.work.Data inputData = new androidx.work.Data.Builder()
                    .putInt(com.studentlms.services.StudySessionReminderWorker.KEY_SESSION_ID, sessionId)
                    .putString(com.studentlms.services.StudySessionReminderWorker.KEY_SUBJECT_NAME, subject)
                    .putLong(com.studentlms.services.StudySessionReminderWorker.KEY_START_TIME, startTime)
                    .build();

            // Schedule one-time notification
            androidx.work.OneTimeWorkRequest reminderWork = new androidx.work.OneTimeWorkRequest.Builder(
                    com.studentlms.services.StudySessionReminderWorker.class)
                    .setInitialDelay(delay, java.util.concurrent.TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag("study_reminder_" + sessionId)
                    .build();

            androidx.work.WorkManager.getInstance(requireContext()).enqueue(reminderWork);

            android.util.Log.d("StudyPlan", "Scheduled reminder for " + subject + " at " +
                    new java.text.SimpleDateFormat("h:mm a")
                            .format(new java.util.Date(startTime - (reminderMinutesBefore * 60 * 1000))));
        }
    }

    private void updateSelectedTimesText(TextView textView, Calendar start, Calendar end,
            boolean startSet, boolean endSet, SimpleDateFormat format) {
        if (startSet && endSet) {
            textView.setText(format.format(start.getTime()) + " - " + format.format(end.getTime()));
        } else if (startSet) {
            textView.setText("Start: " + format.format(start.getTime()));
        } else if (endSet) {
            textView.setText("End: " + format.format(end.getTime()));
        }
    }
}
