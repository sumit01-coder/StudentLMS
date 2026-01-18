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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study_plan, container, false);

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

        TextInputEditText inputSubjectId = dialogView.findViewById(R.id.input_subject_id);
        TextInputEditText inputNotes = dialogView.findViewById(R.id.input_notes);
        TextView selectedTimesText = dialogView.findViewById(R.id.selected_times_text);

        final Calendar startCal = Calendar.getInstance();
        final Calendar endCal = Calendar.getInstance();
        endCal.add(Calendar.HOUR_OF_DAY, 1); // Default 1 hour later

        final boolean[] startTimeSet = { false };
        final boolean[] endTimeSet = { false };

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());

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

        builder.setPositiveButton(null, null);
        builder.setNegativeButton(null, null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Setup button listeners
        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.btn_save).setOnClickListener(v -> {
            String subjectIdText = inputSubjectId.getText() != null ? inputSubjectId.getText().toString().trim() : "1";
            String notes = inputNotes.getText() != null ? inputNotes.getText().toString().trim() : "";

            int subjectId = 1;
            try {
                subjectId = Integer.parseInt(subjectIdText);
            } catch (NumberFormatException e) {
                inputSubjectId.setError("Invalid subject ID");
                return;
            }

            StudySession session = new StudySession(subjectId, startCal.getTimeInMillis(),
                    endCal.getTimeInMillis(), notes, false);

            viewModel.insertSession(session);
            dialog.dismiss();
        });
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
