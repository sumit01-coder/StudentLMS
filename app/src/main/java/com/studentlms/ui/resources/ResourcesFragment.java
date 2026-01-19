package com.studentlms.ui.resources;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.studentlms.R;
import com.studentlms.data.models.Resource;

public class ResourcesFragment extends Fragment {

    private ResourcesViewModel viewModel;
    private ResourceAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private FloatingActionButton fabAdd;
    private TextInputEditText searchInput;
    private ChipGroup filterChipGroup;
    private Spinner semesterSpinner;
    private String currentFilter = "ALL";
    private int currentSemester = 6; // Default semester
    private ActivityResultLauncher<String> filePickerLauncher;
    private TextInputEditText currentUrlInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resources, container, false);

        setupFilePicker();
        initViews(view);
        setupViewModel();
        setupRecyclerView();
        setupListeners();

        return view;
    }

    private void setupFilePicker() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null && currentUrlInput != null) {
                        currentUrlInput.setText(uri.toString());
                    }
                });
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.resources_recycler);
        emptyState = view.findViewById(R.id.empty_state);
        fabAdd = view.findViewById(R.id.fab_add_resource);
        searchInput = view.findViewById(R.id.search_input);
        filterChipGroup = view.findViewById(R.id.filter_chip_group);
        semesterSpinner = view.findViewById(R.id.spinner_semester);

        // Load saved semester from SharedPreferences
        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("StudentLMSPrefs",
                android.content.Context.MODE_PRIVATE);
        currentSemester = prefs.getInt("current_semester", 6);

        // Set default selection (index 0 = All, 1-8 = Semesters 1-8)
        semesterSpinner.setSelection(currentSemester);

        // Check for subject filter arguments
        if (getArguments() != null) {
            String subjectCode = getArguments().getString("filter_subject");
            if (subjectCode != null && !subjectCode.isEmpty()) {
                // Pre-fill search with subject code to filter
                if (searchInput != null) {
                    searchInput.setText(subjectCode);
                }
                // Also optionally set semester
                int semester = getArguments().getInt("filter_semester", 0);
                if (semester > 0 && semesterSpinner != null) {
                    // Assuming spinner has items 1-8 at indices 0-7, or similar mapping
                    // We'll leave it for now or implement exact mapping if needed
                }

                // Handle content URL
                String contentUrl = getArguments().getString("content_url");
                if (contentUrl != null && !contentUrl.isEmpty()) {
                    // Trigger native resource sync
                    int filterSemester = getArguments().getInt("filter_semester", 0);
                    viewModel.syncResources(contentUrl, subjectCode, filterSemester);

                    MaterialButton openCourseBtn = view.findViewById(R.id.btn_open_course);
                    if (openCourseBtn != null) {
                        openCourseBtn.setVisibility(View.VISIBLE);
                        openCourseBtn.setOnClickListener(v -> {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contentUrl));
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "Cannot open link: " + contentUrl, Toast.LENGTH_LONG)
                                        .show();
                            }
                        });

                        // Debugging: Show URL
                        // Toast.makeText(getContext(), "URL Found: " + contentUrl,
                        // Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ResourcesViewModel.class);

        viewModel.getFilteredResources().observe(getViewLifecycleOwner(), resources -> {
            adapter.setResources(resources);

            // Show/hide empty state
            if (resources == null || resources.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
            }
        });

        // Observe sync status
        viewModel.getIsSyncing().observe(getViewLifecycleOwner(), isSyncing -> {
            View progressBar = getView().findViewById(R.id.progress_sync);
            if (progressBar != null) {
                progressBar.setVisibility(isSyncing ? View.VISIBLE : View.GONE);
            }
            // If syncing, hide empty state even if empty
            if (isSyncing) {
                emptyState.setVisibility(View.GONE);
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new ResourceAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnResourceActionListener(new ResourceAdapter.OnResourceActionListener() {
            @Override
            public void onOpenResource(Resource resource) {
                openResource(resource);
            }

            @Override
            public void onDeleteResource(Resource resource) {
                viewModel.deleteResource(resource);
            }

            @Override
            public void onFavoriteToggle(Resource resource) {
                // Update resource in database
                viewModel.updateResource(resource);
                Toast.makeText(requireContext(),
                        resource.isFavorite() ? "Added to favorites" : "Removed from favorites",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        fabAdd.setOnClickListener(v -> showAddResourceDialog());

        // Search functionality
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    viewModel.searchResources(query).observe(getViewLifecycleOwner(), resources -> {
                        adapter.setResources(resources);
                    });
                } else {
                    viewModel.setFilterType(currentFilter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Filter chips
        filterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int chipId = checkedIds.get(0);
                if (chipId == R.id.chip_all) {
                    currentFilter = "ALL";
                } else if (chipId == R.id.chip_pdf) {
                    currentFilter = "PDF";
                } else if (chipId == R.id.chip_video) {
                    currentFilter = "VIDEO";
                } else if (chipId == R.id.chip_link) {
                    currentFilter = "LINK";
                } else if (chipId == R.id.chip_note) {
                    currentFilter = "NOTE";
                }
                viewModel.setFilterType(currentFilter);
            }
        });

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
    }

    private void openResource(Resource resource) {
        String urlOrPath = resource.getUrlOrPath();
        String type = resource.getType();

        if (urlOrPath == null || urlOrPath.isEmpty()) {
            Toast.makeText(getContext(), "No URL or path specified", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            if (type.equals("PDF")) {
                intent.setDataAndType(Uri.parse(urlOrPath), "application/pdf");
            } else if (type.equals("VIDEO")) {
                intent.setDataAndType(Uri.parse(urlOrPath), "video/*");
            } else {
                intent.setData(Uri.parse(urlOrPath));
            }

            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Unable to open resource: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddResourceDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_resource, null);

        TextInputEditText inputTitle = dialogView.findViewById(R.id.input_title);
        AutoCompleteTextView inputType = dialogView.findViewById(R.id.input_type);
        TextInputEditText inputUrl = dialogView.findViewById(R.id.input_url);
        TextInputEditText inputSubjectId = dialogView.findViewById(R.id.input_subject_id);
        AutoCompleteTextView inputSemester = dialogView.findViewById(R.id.input_semester);
        MaterialButton btnBrowseFile = dialogView.findViewById(R.id.btn_browse_file);

        // Store reference to URL input for file picker callback
        currentUrlInput = inputUrl;

        // Browse file button
        btnBrowseFile.setOnClickListener(v -> {
            filePickerLauncher.launch("*/*"); // Accept all file types
        });

        // Setup type dropdown
        String[] types = { "LINK", "PDF", "VIDEO", "NOTE" };
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, types);
        inputType.setAdapter(typeAdapter);
        inputType.setText(types[0], false);

        // Setup semester dropdown
        String[] semesters = getResources().getStringArray(R.array.semester_options);
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, semesters);
        inputSemester.setAdapter(semesterAdapter);
        // Pre-fill with current semester (index matches currentSemester)
        if (currentSemester >= 0 && currentSemester < semesters.length) {
            inputSemester.setText(semesters[currentSemester], false);
        }

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
            String type = inputType.getText().toString();
            String url = inputUrl.getText() != null ? inputUrl.getText().toString().trim() : "";
            String subjectIdText = inputSubjectId.getText() != null ? inputSubjectId.getText().toString().trim() : "0";
            String semesterText = inputSemester.getText().toString();

            if (title.isEmpty()) {
                inputTitle.setError("Title is required");
                return;
            }

            if (url.isEmpty()) {
                inputUrl.setError("URL or path is required");
                return;
            }

            int subjectId = 0;
            try {
                subjectId = Integer.parseInt(subjectIdText);
            } catch (NumberFormatException e) {
                // Use default 0
            }

            // Parse semester from selected text ("Semester 6" -> 6, "All Semesters" -> 0)
            int semester = currentSemester; // Default to current
            if (semesterText.startsWith("Semester ")) {
                try {
                    semester = Integer.parseInt(semesterText.substring(9).trim());
                } catch (NumberFormatException e) {
                    semester = currentSemester;
                }
            } else if (semesterText.equals("All Semesters")) {
                semester = 0;
            }

            Resource resource = new Resource(title, type, url, subjectId, semester, System.currentTimeMillis());

            viewModel.insertResource(resource);
            dialog.dismiss();
        });
    }
}
