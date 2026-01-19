package com.studentlms.ui.subjects;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.studentlms.R;
import com.studentlms.data.models.Subject;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying subjects in grid layout
 */
public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private List<Subject> subjects = new ArrayList<>();
    private OnSubjectClickListener listener;

    public interface OnSubjectClickListener {
        void onSubjectClick(Subject subject);
    }

    public void setOnSubjectClickListener(OnSubjectClickListener listener) {
        this.listener = listener;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects != null ? subjects : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subject_card, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.bind(subject);
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    class SubjectViewHolder extends RecyclerView.ViewHolder {
        private final TextView subjectCode;
        private final TextView subjectName;
        private final TextView semesterBadge;
        private final TextView contentCount;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectCode = itemView.findViewById(R.id.text_subject_code);
            subjectName = itemView.findViewById(R.id.text_subject_name);
            semesterBadge = itemView.findViewById(R.id.text_semester_badge);
            contentCount = itemView.findViewById(R.id.text_content_count);
        }

        public void bind(Subject subject) {
            subjectCode.setText(subject.getSubjectCode());
            subjectName.setText(subject.getName());
            semesterBadge.setText("Semester - " + subject.getSemester());
            contentCount.setText("Contents: " + subject.getContentCount());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSubjectClick(subject);
                }
            });
        }
    }
}
