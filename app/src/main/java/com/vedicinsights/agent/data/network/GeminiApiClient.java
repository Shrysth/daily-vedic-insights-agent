package com.vedicinsights.agent.data.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import java.util.concurrent.TimeUnit;

public class GeminiApiClient {
    public interface GeminiService {
        @POST("v1beta/models/gemini-1.5-flash:generateContent")
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
    }
    public static class Candidate {
        public Contents content;
    }
}