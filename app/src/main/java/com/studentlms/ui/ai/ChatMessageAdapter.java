package com.studentlms.ui.ai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.studentlms.R;
import com.studentlms.data.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder> {

    private List<ChatMessage> messages = new ArrayList<>();

    public void setMessages(List<ChatMessage> newMessages) {
        this.messages = newMessages != null ? newMessages : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        if (message.isUser()) {
            // Show user message
            holder.userMessageCard.setVisibility(View.VISIBLE);
            holder.aiMessageCard.setVisibility(View.GONE);
            holder.userMessageText.setText(message.getMessage());
        } else {
            // Show AI message
            holder.userMessageCard.setVisibility(View.GONE);
            holder.aiMessageCard.setVisibility(View.VISIBLE);
            holder.aiMessageText.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView userMessageCard;
        MaterialCardView aiMessageCard;
        TextView userMessageText;
        TextView aiMessageText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessageCard = itemView.findViewById(R.id.user_message_card);
            aiMessageCard = itemView.findViewById(R.id.ai_message_card);
            userMessageText = itemView.findViewById(R.id.user_message_text);
            aiMessageText = itemView.findViewById(R.id.ai_message_text);
        }
    }
}
