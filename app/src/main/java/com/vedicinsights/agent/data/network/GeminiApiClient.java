package com.vedicinsights.agent.data.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import java.util.concurrent.TimeUnit;

public class GeminiApiClient {
    private static final String MODEL = "gemini-2.5-flash";

    public interface GeminiService {
        @POST("v1beta/models/" + MODEL + ":generateContent")
        retrofit2.Call<GeminiResponse> generateBriefing(
            @Query("key") String apiKey,
            @Body GeminiRequest request
        );
    }

    public static GeminiService create() {
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

        return new Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiService.class);
    }

    public static class GeminiRequest {
        public Contents[] contents;
        public GeminiRequest(String promptText) {
            if (promptText == null || promptText.trim().isEmpty()) {
                throw new IllegalArgumentException("Briefing prompt cannot be empty.");
            }
            this.contents = new Contents[] { new Contents(promptText) };
        }
    }
    public static class Contents {
        public Part[] parts;
        public Contents(String text) { this.parts = new Part[]{ new Part(text) }; }
    }
    public static class Part {
        public String text;
        public Part(String text) { this.text = text; }
    }
    public static class GeminiResponse {
        public Candidate[] candidates;

        public String getBriefingText() {
            if (candidates == null || candidates.length == 0
                || candidates[0].content == null || candidates[0].content.parts == null) {
                throw new IllegalStateException("Gemini returned no briefing text.");
            }
            StringBuilder text = new StringBuilder();
            for (Part part : candidates[0].content.parts) {
                if (part.text != null && !part.text.trim().isEmpty()) {
                    if (text.length() > 0) {
                        text.append('\n');
                    }
                    text.append(part.text.trim());
                }
            }
            if (text.length() == 0) {
                throw new IllegalStateException("Gemini returned no briefing text.");
            }
            return text.toString();
        }
    }
    public static class Candidate {
        public Contents content;
    }
}