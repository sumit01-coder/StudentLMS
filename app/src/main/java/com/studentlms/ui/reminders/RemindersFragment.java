package com.studentlms.ui.reminders;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.studentlms.R;
import com.studentlms.data.models.Reminder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RemindersFragment extends Fragment {

    private RemindersViewModel viewModel;
    private ReminderAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private FloatingActionButton fabAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        initViews(view);
        setupViewModel();
        setupRecyclerView();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.reminders_recycler);
        emptyState = view.findViewById(R.id.empty_state);
        fabAdd = view.findViewById(R.id.fab_add_reminder);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(RemindersViewModel.class);

        viewModel.getAllReminders().observe(getViewLifecycleOwner(), reminders -> {
            adapter.setReminders(reminders);

            // Show/hide empty state
            if (reminders == null || reminders.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new ReminderAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnReminderActionListener(reminder -> {
            // Delete reminder
            viewModel.deleteReminder(reminder);
        });
    }

    private void setupListeners() {
        fabAdd.setOnClickListener(v -> showAddReminderDialog());
    }

    private void showAddReminderDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_reminder, null);

        TextInputEditText inputTitle = dialogView.findViewById(R.id.input_title);
        TextInputEditText inputDescription = dialogView.findViewById(R.id.input_description);
        AutoCompleteTextView inputType = dialogView.findViewById(R.id.input_type);
        MaterialButton btnPickDate = dialogView.findViewById(R.id.btn_pick_date);
        MaterialButton btnPickTime = dialogView.findViewById(R.id.btn_pick_time);
        TextView selectedDateTimeText = dialogView.findViewById(R.id.selected_datetime_text);
        MaterialCheckBox checkboxRecurring = dialogView.findViewById(R.id.checkbox_recurring);

        // Setup type dropdown
        String[] types = { "Custom", "Study Session", "Assignment" };
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, types);
        inputType.setAdapter(typeAdapter);
        inputType.setText(types[0], false);

        // Date and time selection
        final Calendar selectedDateTime = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault());

        btnPickDate.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        selectedDateTime.set(Calendar.YEAR, year);
                        selectedDateTime.set(Calendar.MONTH, month);
                        selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        selectedDateTimeText.setText(dateFormat.format(selectedDateTime.getTime()));
                    },
                    selectedDateTime.get(Calendar.YEAR),
                    selectedDateTime.get(Calendar.MONTH),
                    selectedDateTime.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        btnPickTime.setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(requireContext(),
                    (view, hourOfDay, minute) -> {
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedDateTime.set(Calendar.MINUTE, minute);
                        selectedDateTimeText.setText(dateFormat.format(selectedDateTime.getTime()));
                    },
                    selectedDateTime.get(Calendar.HOUR_OF_DAY),
                    selectedDateTime.get(Calendar.MINUTE),
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
            String title = inputTitle.getText() != null ? inputTitle.getText().toString().trim() : "";
            String description = inputDescription.getText() != null ? inputDescription.getText().toString().trim() : "";
            String typeText = inputType.getText().toString();
            boolean isRecurring = checkboxRecurring.isChecked();

            if (title.isEmpty()) {
                inputTitle.setError("Title is required");
                return;
            }

            // Convert type text to type code
            String type = "CUSTOM";
            if (typeText.equals("Study Session")) {
                type = "STUDY_SESSION";
            } else if (typeText.equals("Assignment")) {
                type = "ASSIGNMENT";
            }

            Reminder reminder = new Reminder(title, description, selectedDateTime.getTimeInMillis(),
                    type, isRecurring, 0);

            viewModel.insertReminder(reminder);
            dialog.dismiss();
        });
    }
}
