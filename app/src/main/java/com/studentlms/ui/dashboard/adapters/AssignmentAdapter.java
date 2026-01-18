package com.studentlms.ui.dashboard.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.studentlms.R;
import com.studentlms.data.models.LMSAssignment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private List<LMSAssignment> assignments = new ArrayList<>();

    public void setAssignments(List<LMSAssignment> assignments) {
        this.assignments = assignments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lms_assignment, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        LMSAssignment assignment = assignments.get(position);
        holder.bind(assignment);
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        private final TextView lmsBadge, courseName, title, description, countdown, urgencyIndicator;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            lmsBadge = itemView.findViewById(R.id.lms_badge);
            courseName = itemView.findViewById(R.id.course_name);
            title = itemView.findViewById(R.id.assignment_title);
            description = itemView.findViewById(R.id.assignment_description);
            countdown = itemView.findViewById(R.id.countdown_text);
            urgencyIndicator = itemView.findViewById(R.id.urgency_indicator);
        }

        public void bind(LMSAssignment assignment) {
            // Set LMS badge with color
            lmsBadge.setText(getBadgeText(assignment.getLmsType()));
            lmsBadge.setBackgroundColor(getBadgeColor(assignment.getLmsType()));

            courseName.setText(assignment.getCourseName());
            title.setText(assignment.getTitle());
            description.setText(assignment.getDescription());

            // Calculate time remaining
            long timeRemaining = assignment.getDueDate() - System.currentTimeMillis();
            long daysRemaining = TimeUnit.MILLISECONDS.toDays(timeRemaining);
            long hoursRemaining = TimeUnit.MILLISECONDS.toHours(timeRemaining);

            String countdownText;
            int urgencyColor;
            String urgencyText;

            if (timeRemaining < 0) {
                countdownText = "Overdue";
                urgencyColor = Color.parseColor("#f56565");
                urgencyText = "OVERDUE";
            } else if (hoursRemaining < 24) {
                countdownText = "Due in " + hoursRemaining + " hours";
                urgencyColor = Color.parseColor("#f56565");
                urgencyText = "URGENT";
            } else if (daysRemaining < 3) {
                countdownText = "Due in " + daysRemaining + " days";
                urgencyColor = Color.parseColor("#ed8936");
                urgencyText = "SOON";
            } else {
                countdownText = "Due in " + daysRemaining + " days";
                urgencyColor = Color.parseColor("#48bb78");
                urgencyText = "OK";
                urgencyIndicator.setVisibility(View.GONE);
            }

            countdown.setText(countdownText);
            if (urgencyIndicator.getVisibility() == View.VISIBLE) {
                urgencyIndicator.setText(urgencyText);
                urgencyIndicator.setBackgroundColor(urgencyColor);
            }
        }

        private String getBadgeText(String lmsType) {
            switch (lmsType) {
                case "GOOGLE_CLASSROOM":
                    return "Classroom";
                case "MOODLE":
                    return "Moodle";
                case "CANVAS":
                    return "Canvas";
                default:
                    return lmsType;
            }
        }

        private int getBadgeColor(String lmsType) {
            switch (lmsType) {
                case "GOOGLE_CLASSROOM":
                    return Color.parseColor("#0f9d58");
                case "MOODLE":
                    return Color.parseColor("#ff8800");
                case "CANVAS":
                    return Color.parseColor("#e13f2b");
                default:
                    return Color.parseColor("#667eea");
            }
        }
    }
}
