package com.studentlms.ui.reminders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.studentlms.R;
import com.studentlms.data.models.Reminder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminders = new ArrayList<>();
    private OnReminderActionListener listener;

    public interface OnReminderActionListener {
        void onDeleteReminder(Reminder reminder);
    }

    public void setOnReminderActionListener(OnReminderActionListener listener) {
        this.listener = listener;
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.bind(reminder);
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    class ReminderViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView typeText;
        private final TextView descriptionText;
        private final TextView dateTimeText;
        private final ImageView typeIcon;
        private final Chip recurringBadge;
        private final MaterialButton deleteButton;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.reminder_title);
            typeText = itemView.findViewById(R.id.reminder_type);
            descriptionText = itemView.findViewById(R.id.reminder_description);
            dateTimeText = itemView.findViewById(R.id.reminder_datetime);
            typeIcon = itemView.findViewById(R.id.type_icon);
            recurringBadge = itemView.findViewById(R.id.recurring_badge);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(Reminder reminder) {
            titleText.setText(reminder.getTitle());
            typeText.setText(formatReminderType(reminder.getType()));
            descriptionText.setText(reminder.getDescription());

            // Format date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault());
            dateTimeText.setText(dateFormat.format(new Date(reminder.getDateTime())));

            // Set type icon
            int iconRes = getIconForType(reminder.getType());
            typeIcon.setImageResource(iconRes);

            // Show/hide recurring badge
            recurringBadge.setVisibility(reminder.isRecurring() ? View.VISIBLE : View.GONE);

            // Delete button
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteReminder(reminder);
                }
            });
        }

        private String formatReminderType(String type) {
            switch (type) {
                case "STUDY_SESSION":
                    return "Study Session";
                case "ASSIGNMENT":
                    return "Assignment";
                case "CUSTOM":
                default:
                    return "Custom";
            }
        }

        private int getIconForType(String type) {
            switch (type) {
                case "STUDY_SESSION":
                    return R.drawable.ic_dashboard;
                case "ASSIGNMENT":
                    return R.drawable.ic_assignment;
                case "CUSTOM":
                default:
                    return R.drawable.ic_notifications;
            }
        }
    }
}
