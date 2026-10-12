package com.vedicinsights.agent.data.network;

import android.content.Context;

public final class BriefingPrivacySettings {
    private static final String PREFERENCES = "briefing_privacy";
    private static final String INCLUDE_PERSONAL_DETAILS = "include_personal_details_with_ai";

    private BriefingPrivacySettings() { }

    public static boolean includePersonalDetailsWithAi(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .getBoolean(INCLUDE_PERSONAL_DETAILS, false);
    }

    public static void setIncludePersonalDetailsWithAi(Context context, boolean includeDetails) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(INCLUDE_PERSONAL_DETAILS, includeDetails)
            .apply();
    }
}
