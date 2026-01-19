package com.studentlms.ui.subjects;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.studentlms.R;
import com.studentlms.data.models.Subject;

/**
 * Fragment for displaying subjects filtered by semester
 */
public class SubjectsFragment extends Fragment {

    private SubjectsViewModel viewModel;
    private SubjectAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private Spinner semesterSpinner;
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton fabSync;
    private int currentSemester = 6;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subjects, container, false);

        initViews(view);
        setupViewModel();
        setupRecyclerView();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.subjects_recycler);
        emptyState = view.findViewById(R.id.empty_state);
        semesterSpinner = view.findViewById(R.id.spinner_semester);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        fabSync = view.findViewById(R.id.fab_sync);

        // Load saved semester from preferences
        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("StudentLMSPrefs",
                android.content.Context.MODE_PRIVATE);
        currentSemester = prefs.getInt("current_semester", 6);

        // Set default semester selection
        semesterSpinner.setSelection(currentSemester);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(SubjectsViewModel.class);

        // Observe filtered subjects
        viewModel.getFilteredSubjects().observe(getViewLifecycleOwner(), subjects -> {
            adapter.setSubjects(subjects);

            // Show/hide empty state
            if (subjects == null || subjects.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
            }
        });

        // Observe sync status
        viewModel.getIsSyncing().observe(getViewLifecycleOwner(), isSyncing -> {
            swipeRefresh.setRefreshing(isSyncing);
        });

        // Observe sync errors
        viewModel.getSyncError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new SubjectAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnSubjectClickListener(subject -> {
            // Navigate to Resources fragment with subject filter
            Bundle args = new Bundle();
            args.putString("filter_subject", subject.getSubjectCode());
            args.putString("filter_subject_name", subject.getName());
            args.putInt("filter_semester", subject.getSemester());
            args.putString("content_url", subject.getContentUrl());

            try {
                androidx.navigation.NavController navController = androidx.navigation.Navigation
                        .findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.navigation_resources, args);
            } catch (Exception e) {
                // Fallback: Show toast if navigation fails
                Toast.makeText(requireContext(),
                        "Selected: " + subject.getSubjectCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        // Semester spinner listener
        semesterSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                currentSemester = position;
                viewModel.setFilterSemester(position);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Pull to refresh
        swipeRefresh.setOnRefreshListener(() -> {
            viewModel.syncSubjectsFromERP();
        });

        // FAB sync button
        fabSync.setOnClickListener(v -> {
            Toast.makeText(requireContext(), R.string.syncing_subjects, Toast.LENGTH_SHORT).show();
            viewModel.syncSubjectsFromERP();
        });
    }
}
