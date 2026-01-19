package com.studentlms.ui.resources;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.studentlms.R;
import com.studentlms.data.models.Resource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ResourceViewHolder> {

    private List<Resource> resources = new ArrayList<>();
    private OnResourceActionListener listener;

    public interface OnResourceActionListener {
        void onOpenResource(Resource resource);

        void onDeleteResource(Resource resource);

        void onFavoriteToggle(Resource resource);
    }

    public void setOnResourceActionListener(OnResourceActionListener listener) {
        this.listener = listener;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resource, parent, false);
        return new ResourceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResourceViewHolder holder, int position) {
        Resource resource = resources.get(position);
        holder.bind(resource);
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }

    class ResourceViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView dateText;
        private final ImageView typeIcon;
        private final ImageView favoriteIndicator;
        private final Chip typeBadge;
        private final Chip subjectBadge;
        private final MaterialButton openButton;
        private final MaterialButton shareButton;
        private final MaterialButton favoriteButton;

        public ResourceViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.resource_title);
            dateText = itemView.findViewById(R.id.resource_date);
            typeIcon = itemView.findViewById(R.id.resource_type_icon);
            favoriteIndicator = itemView.findViewById(R.id.favorite_indicator);
            typeBadge = itemView.findViewById(R.id.type_badge);
            subjectBadge = itemView.findViewById(R.id.subject_badge);
            openButton = itemView.findViewById(R.id.btn_open);
            shareButton = itemView.findViewById(R.id.btn_share);
            favoriteButton = itemView.findViewById(R.id.btn_favorite);
        }

        public void bind(Resource resource) {
            titleText.setText(resource.getTitle());

            // Format date (relative time)
            String timeAgo = getRelativeTimeAgo(resource.getAddedDate());
            dateText.setText("Added " + timeAgo);

            // Set type icon (color-coded)
            int iconRes = getColoredIconForType(resource.getType());
            typeIcon.setImageResource(iconRes);
            typeBadge.setText(resource.getType());

            // Set subject badge
            if (resource.getSubjectName() != null && !resource.getSubjectName().isEmpty()) {
                subjectBadge.setText(resource.getSubjectName());
                subjectBadge.setVisibility(View.VISIBLE);
            } else {
                subjectBadge.setVisibility(View.GONE);
            }

            // Update favorite UI
            if (resource.isFavorite()) {
                favoriteIndicator.setVisibility(View.VISIBLE);
                favoriteButton.setIconResource(R.drawable.ic_star);
                favoriteButton.setText("Unfavorite");
            } else {
                favoriteIndicator.setVisibility(View.GONE);
                favoriteButton.setIconResource(R.drawable.ic_star_outline);
                favoriteButton.setText("Favorite");
            }

            // Open button
            openButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOpenResource(resource);
                }
            });

            // Share button
            shareButton.setOnClickListener(v -> {
                shareResource(resource);
            });

            // Favorite button
            favoriteButton.setOnClickListener(v -> {
                resource.setFavorite(!resource.isFavorite());
                notifyItemChanged(getAdapterPosition());
                if (listener != null) {
                    listener.onFavoriteToggle(resource);
                }
            });

        }

        private String getRelativeTimeAgo(long timestamp) {
            long now = System.currentTimeMillis();
            long diff = now - timestamp;

            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = minutes / (60 * 24); // Corrected calculation for days
            long weeks = days / 7;
            long months = days / 30;

            if (seconds < 60) {
                return "just now";
            } else if (minutes < 60) {
                return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
            } else if (hours < 24) {
                return hours + (hours == 1 ? " hour ago" : " hours ago");
            } else if (days < 7) {
                return days + (days == 1 ? " day ago" : " days ago");
            } else if (weeks < 4) {
                return weeks + (weeks == 1 ? " week ago" : " weeks ago");
            } else if (months < 12) {
                return months + (months == 1 ? " month ago" : " months ago");
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                return dateFormat.format(new Date(timestamp));
            }
        }

        private void shareResource(Resource resource) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, resource.getTitle());

            String shareText = resource.getTitle();
            if (resource.getSubjectName() != null && !resource.getSubjectName().isEmpty()) {
                shareText += "\n" + resource.getSubjectName();
            }
            shareText += "\n\n" + resource.getUrlOrPath();

            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            Intent chooser = Intent.createChooser(shareIntent, "Share resource via");
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            itemView.getContext().startActivity(chooser);
        }

        private int getColoredIconForType(String type) {
            switch (type) {
                case "PDF":
                    return R.drawable.ic_pdf_colored;
                case "VIDEO":
                    return R.drawable.ic_video;
                case "LINK":
                    return R.drawable.ic_link;
                case "NOTE":
                    return R.drawable.ic_note;
                default:
                    return R.drawable.ic_resource;
            }
        }
    }
}
