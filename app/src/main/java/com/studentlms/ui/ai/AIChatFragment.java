package com.studentlms.ui.ai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.studentlms.R;

public class AIChatFragment extends Fragment {

    private AIChatViewModel viewModel;
    private ChatMessageAdapter adapter;
    private RecyclerView messagesRecycler;
    private LinearLayout emptyState;
    private TextInputEditText inputMessage;
    private MaterialButton btnSend;
    private ProgressBar loadingIndicator;
    private Chip chipQuiz;
    private Chip chipExplain;
    private Chip chipSummarize;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_chat, container, false);

        initViews(view);
        setupViewModel();
        setupRecyclerView();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        messagesRecycler = view.findViewById(R.id.messages_recycler);
        emptyState = view.findViewById(R.id.empty_state);
        inputMessage = view.findViewById(R.id.input_message);
        btnSend = view.findViewById(R.id.btn_send);
        loadingIndicator = view.findViewById(R.id.loading_indicator);
        chipQuiz = view.findViewById(R.id.chip_quiz);
        chipExplain = view.findViewById(R.id.chip_explain);
        chipSummarize = view.findViewById(R.id.chip_summarize);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(AIChatViewModel.class);

        // Observe messages
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.setMessages(messages);

            if (messages == null || messages.isEmpty()) {
                emptyState.setVisibility(View.VISIBLE);
                messagesRecycler.setVisibility(View.GONE);
            } else {
                emptyState.setVisibility(View.GONE);
                messagesRecycler.setVisibility(View.VISIBLE);
                // Scroll to bottom
                messagesRecycler.scrollToPosition(messages.size() - 1);
            }
        });

        // Observe loading state
        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnSend.setEnabled(!isLoading);
        });

        // Observe errors
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new ChatMessageAdapter();
        messagesRecycler.setAdapter(adapter);
    }

    private void setupListeners() {
        btnSend.setOnClickListener(v -> {
            String message = inputMessage.getText() != null ? inputMessage.getText().toString().trim() : "";

            if (!message.isEmpty()) {
                viewModel.sendMessage(message);
                inputMessage.setText("");
            }
        });

        chipQuiz.setOnClickListener(v -> {
            String topic = inputMessage.getText() != null ? inputMessage.getText().toString().trim() : "";

            if (topic.isEmpty()) {
                Toast.makeText(getContext(), "Enter a topic for the quiz",
                        Toast.LENGTH_SHORT).show();
            } else {
                viewModel.generateQuiz(topic);
                inputMessage.setText("");
            }
        });

        chipExplain.setOnClickListener(v -> {
            String concept = inputMessage.getText() != null ? inputMessage.getText().toString().trim() : "";

            if (concept.isEmpty()) {
                Toast.makeText(getContext(), "Enter a concept to explain",
                        Toast.LENGTH_SHORT).show();
            } else {
                viewModel.explainConcept(concept);
                inputMessage.setText("");
            }
        });

        chipSummarize.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Summarize feature - coming soon!",
                    Toast.LENGTH_SHORT).show();
        });
    }
}
