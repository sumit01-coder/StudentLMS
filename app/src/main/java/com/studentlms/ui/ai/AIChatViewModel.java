package com.studentlms.ui.ai;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.studentlms.data.dao.ChatMessageDao;
import com.studentlms.data.database.AppDatabase;
import com.studentlms.data.models.ChatMessage;
import com.studentlms.services.ai.GeminiAIService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AIChatViewModel extends AndroidViewModel {

    private final ChatMessageDao chatMessageDao;
    private final GeminiAIService aiService;
    private final ExecutorService executorService;
    private final String sessionId;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final LiveData<List<ChatMessage>> messages;

    public AIChatViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        chatMessageDao = database.chatMessageDao();
        aiService = new GeminiAIService(application);
        executorService = Executors.newSingleThreadExecutor();

        // Generate unique session ID for this conversation
        sessionId = UUID.randomUUID().toString();
        messages = chatMessageDao.getMessagesBySession(sessionId);
    }

    public LiveData<List<ChatMessage>> getMessages() {
        return messages;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void sendMessage(String messageText) {
        if (messageText == null || messageText.trim().isEmpty()) {
            return;
        }

        isLoading.postValue(true);

        // Save user message
        ChatMessage userMessage = new ChatMessage(
                messageText,
                true,
                System.currentTimeMillis(),
                sessionId);

        executorService.execute(() -> {
            chatMessageDao.insert(userMessage);

            // Get AI response
            aiService.sendMessage(messageText, new GeminiAIService.AIResponseCallback() {
                @Override
                public void onSuccess(String response) {
                    // Save AI response
                    ChatMessage aiMessage = new ChatMessage(
                            response,
                            false,
                            System.currentTimeMillis(),
                            sessionId);

                    executorService.execute(() -> {
                        chatMessageDao.insert(aiMessage);
                        isLoading.postValue(false);
                    });
                }

                @Override
                public void onError(String errorMsg) {
                    error.postValue(errorMsg);
                    isLoading.postValue(false);
                }
            });
        });
    }

    public void generateQuiz(String topic) {
        isLoading.postValue(true);

        aiService.generateQuiz(topic, new GeminiAIService.AIResponseCallback() {
            @Override
            public void onSuccess(String response) {
                // Save AI response
                ChatMessage aiMessage = new ChatMessage(
                        response,
                        false,
                        System.currentTimeMillis(),
                        sessionId);

                executorService.execute(() -> {
                    chatMessageDao.insert(aiMessage);
                    isLoading.postValue(false);
                });
            }

            @Override
            public void onError(String errorMsg) {
                error.postValue(errorMsg);
                isLoading.postValue(false);
            }
        });
    }

    public void explainConcept(String concept) {
        isLoading.postValue(true);

        aiService.explainConcept(concept, new GeminiAIService.AIResponseCallback() {
            @Override
            public void onSuccess(String response) {
                ChatMessage aiMessage = new ChatMessage(
                        response,
                        false,
                        System.currentTimeMillis(),
                        sessionId);

                executorService.execute(() -> {
                    chatMessageDao.insert(aiMessage);
                    isLoading.postValue(false);
                });
            }

            @Override
            public void onError(String errorMsg) {
                error.postValue(errorMsg);
                isLoading.postValue(false);
            }
        });
    }

    public void clearChat() {
        executorService.execute(() -> chatMessageDao.clearSession(sessionId));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
