package com.studentlms.services.ai;

import android.content.Context;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiAIService {

    private static final String API_KEY = "AIzaSyATCog4fbNhXc3D8AgY2xmcLidK_NmF_EA";
    private static final String MODEL_NAME = "gemini-pro";

    private final GenerativeModelFutures model;
    private final Executor executor;

    public GeminiAIService(Context context) {
        GenerativeModel gm = new GenerativeModel(MODEL_NAME, API_KEY);
        model = GenerativeModelFutures.from(gm);
        executor = Executors.newSingleThreadExecutor();
    }

    public void sendMessage(String userMessage, AIResponseCallback callback) {
        // Build educational prompt
        String educationalPrompt = buildEducationalPrompt(userMessage);

        Content content = new Content.Builder()
                .addText(educationalPrompt)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String aiResponse = result.getText();
                callback.onSuccess(aiResponse);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onError(t.getMessage());
            }
        }, executor);
    }

    private String buildEducationalPrompt(String userMessage) {
        return "You are a helpful AI tutor for students. " +
                "Provide clear, educational explanations. " +
                "Use simple language and examples when explaining complex topics. " +
                "If the question is about homework, guide the student to understand the concept rather than just giving the answer. "
                +
                "\n\nStudent's question: " + userMessage;
    }

    public void generateQuiz(String topic, AIResponseCallback callback) {
        String quizPrompt = "Generate 5 multiple-choice questions about: " + topic +
                ". Format each question with 4 options (A, B, C, D) and indicate the correct answer. " +
                "Make questions educational and at high school/college level.";

        sendMessage(quizPrompt, callback);
    }

    public void explainConcept(String concept, AIResponseCallback callback) {
        String explainPrompt = "Explain this concept in simple terms with real-world examples: " + concept +
                ". Break it down step by step for a student to understand.";

        sendMessage(explainPrompt, callback);
    }

    public void summarizeText(String text, AIResponseCallback callback) {
        String summarizePrompt = "Summarize the following text in bullet points, " +
                "highlighting the key concepts: " + text;

        sendMessage(summarizePrompt, callback);
    }

    public interface AIResponseCallback {
        void onSuccess(String response);

        void onError(String error);
    }
}
