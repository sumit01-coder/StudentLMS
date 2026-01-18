package com.studentlms.ui.studyplan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.chip.Chip;
import com.studentlms.R;
import com.studentlms.data.models.StudySession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudySessionAdapter extends RecyclerView.Adapter<StudySessionAdapter.SessionViewHolder> {

    private List<StudySession> sessions = new ArrayList<>();
    private OnSessionActionListener listener;

    public interface OnSessionActionListener {
        void onToggleComplete(StudySession session);

        void onDeleteSession(StudySession session);
    }

    public void setOnSessionActionListener(OnSessionActionListener listener) {
        this.listener = listener;
    }

    public void setSessions(List<StudySession> sessions) {
        this.sessions = sessions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_study_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        StudySession session = sessions.get(position);
        holder.bind(session);
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    class SessionViewHolder extends RecyclerView.ViewHolder {
        private final TextView subjectText;
        private final TextView timeText;
        private final TextView notesText;
        private final MaterialCheckBox completeCheckbox;
        private final Chip durationBadge;
        private final MaterialButton deleteButton;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectText = itemView.findViewById(R.id.session_subject);
            timeText = itemView.findViewById(R.id.session_time);
            notesText = itemView.findViewById(R.id.session_notes);
            completeCheckbox = itemView.findViewById(R.id.checkbox_complete);
            durationBadge = itemView.findViewById(R.id.duration_badge);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(StudySession session) {
            // Display subject Name
            subjectText.setText(session.getSubjectName());

            // Format time
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            String startTime = timeFormat.format(new Date(session.getStartTime()));
            String endTime = timeFormat.format(new Date(session.getEndTime()));
            timeText.setText(startTime + " - " + endTime);

            // Calculate duration
            long durationMillis = session.getEndTime() - session.getStartTime();
            long hours = durationMillis / (1000 * 60 * 60);
            long minutes = (durationMillis % (1000 * 60 * 60)) / (1000 * 60);
            String durationText;
            if (hours > 0) {
                durationText = hours + "h";
                if (minutes > 0) {
                    durationText += " " + minutes + "m";
                }
            } else {
                durationText = minutes + "m";
            }
            durationBadge.setText(durationText);

            // Show/hide notes
            if (session.getNotes() != null && !session.getNotes().isEmpty()) {
                notesText.setText(session.getNotes());
                notesText.setVisibility(View.VISIBLE);
            } else {
                notesText.setVisibility(View.GONE);
            }

            // Completion checkbox
            completeCheckbox.setChecked(session.isCompleted());
            completeCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onToggleComplete(session);
                }
            });

            // Delete button
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteSession(session);
                }
            });
        }
    }
}
