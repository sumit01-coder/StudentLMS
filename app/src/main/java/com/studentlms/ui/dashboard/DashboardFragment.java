package com.studentlms.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.studentlms.R;
import com.studentlms.ui.dashboard.adapters.AssignmentAdapter;
import com.studentlms.utils.CredentialManager;

import java.util.Calendar;

public class DashboardFragment extends Fragment {

    private TextView greetingText, streakText, aiRecommendation;
    private RecyclerView assignmentsRecycler;
    private Button syncButton, startStudyButton, addResourceButton;
    private DashboardViewModel viewModel;
    private AssignmentAdapter assignmentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        initViews(view);
        setupRecyclerView();
        setupViewModel();
        setupListeners();
        updateGreeting();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateGreeting();
    }

    private void initViews(View view) {
        greetingText = view.findViewById(R.id.greeting_text);
        streakText = view.findViewById(R.id.streak_text);
        aiRecommendation = view.findViewById(R.id.ai_recommendation);
        assignmentsRecycler = view.findViewById(R.id.assignments_recycler);
        syncButton = view.findViewById(R.id.sync_button);
        startStudyButton = view.findViewById(R.id.start_study_button);
        addResourceButton = view.findViewById(R.id.add_resource_button);
    }

    private void setupRecyclerView() {
        assignmentAdapter = new AssignmentAdapter();
        assignmentsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        assignmentsRecycler.setAdapter(assignmentAdapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // Observe assignments
        viewModel.getUpcomingAssignments().observe(getViewLifecycleOwner(), assignments -> {
            assignmentAdapter.setAssignments(assignments);
        });
    }

    private void setupListeners() {
        syncButton.setOnClickListener(v -> {
            // TODO: Trigger manual LMS sync
            viewModel.syncAssignments();
        });

        startStudyButton.setOnClickListener(v -> {
            // TODO: Navigate to study session screen
        });

        addResourceButton.setOnClickListener(v -> {
            // TODO: Navigate to add resource screen
        });
    }

    private void updateGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hourOfDay < 12) {
            greeting = "Good Morning";
        } else if (hourOfDay < 17) {
            greeting = "Good Afternoon";
        } else {
            greeting = "Good Evening";
        }

        // Add student name if available
        CredentialManager credentialManager = new CredentialManager(requireContext());
        String studentName = credentialManager.getStudentName();
        if (studentName != null && !studentName.isEmpty()) {
            // Extract first name for a friendlier greeting
            String[] parts = studentName.split(" ");
            String firstName = parts.length > 1 ? parts[1] : parts[0]; // Assuming format LASTNAME FIRSTNAME MIDDLE
            // Check if name is all caps, title case it if so
            if (firstName.equals(firstName.toUpperCase())) {
                firstName = firstName.charAt(0) + firstName.substring(1).toLowerCase();
            }
            greeting += ", " + firstName + "!";
        } else {
            greeting += "!";
        }

        greetingText.setText(greeting);
    }
}
