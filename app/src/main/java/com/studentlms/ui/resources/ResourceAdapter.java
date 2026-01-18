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
        private final Chip typeBadge;
        private final MaterialButton openButton;
        private final MaterialButton deleteButton;

        public ResourceViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.resource_title);
            dateText = itemView.findViewById(R.id.resource_date);
            typeIcon = itemView.findViewById(R.id.resource_type_icon);
            typeBadge = itemView.findViewById(R.id.type_badge);
            openButton = itemView.findViewById(R.id.btn_open);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(Resource resource) {
            titleText.setText(resource.getTitle());

            // Format date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            dateText.setText("Added: " + dateFormat.format(new Date(resource.getAddedDate())));

            // Set type icon and badge
            int iconRes = getIconForType(resource.getType());
            typeIcon.setImageResource(iconRes);
            typeBadge.setText(resource.getType());

            // Open button
            openButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOpenResource(resource);
                }
            });

            // Delete button
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteResource(resource);
                }
            });
        }

        private int getIconForType(String type) {
            switch (type) {
                case "PDF":
                    return R.drawable.ic_pdf;
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
