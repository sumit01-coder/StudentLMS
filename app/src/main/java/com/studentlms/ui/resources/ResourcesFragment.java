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
    private String currentFilter = "ALL";
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

            Resource resource = new Resource(title, type, url, subjectId, System.currentTimeMillis());

            viewModel.insertResource(resource);
            dialog.dismiss();
        });
    }
}
