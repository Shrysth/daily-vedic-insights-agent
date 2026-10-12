package com.vedicinsights.agent.data.calendar;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import androidx.core.content.ContextCompat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeviceCalendarRepository {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault());

    public static boolean hasCalendarPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
            == PackageManager.PERMISSION_GRANTED;
    }

    public static List<String> getTodayEvents(Context context) {
        if (!hasCalendarPermission(context)) {
            throw new SecurityException("Calendar permission has not been granted.");
        }

        ZoneId zone = ZoneId.systemDefault();
        long beginMillis = LocalDate.now(zone).atStartOfDay(zone).toInstant().toEpochMilli();
        long endMillis = LocalDate.now(zone).plusDays(1).atStartOfDay(zone)
            .toInstant().toEpochMilli() - 1L;

        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, beginMillis);
        ContentUris.appendId(builder, endMillis);
        String[] projection = {
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END
        };

        List<String> events = new ArrayList<>();
        try (Cursor cursor = context.getContentResolver().query(
            builder.build(),
            projection,
            null,
            null,
            CalendarContract.Instances.BEGIN + " ASC"
        )) {
            if (cursor == null) {
                throw new IllegalStateException("The calendar provider returned no query result.");
            }
            int titleColumn = cursor.getColumnIndexOrThrow(CalendarContract.Instances.TITLE);
            int beginColumn = cursor.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN);
            int endColumn = cursor.getColumnIndexOrThrow(CalendarContract.Instances.END);
            while (cursor.moveToNext()) {
                String title = cursor.getString(titleColumn);
                long startMillis = cursor.getLong(beginColumn);
                long finishMillis = cursor.getLong(endColumn);
                String formattedStart = TIME_FORMAT.format(
                    java.time.Instant.ofEpochMilli(startMillis).atZone(zone)
                );
                String formattedEnd = TIME_FORMAT.format(
                    java.time.Instant.ofEpochMilli(finishMillis).atZone(zone)
                );
                events.add((title == null || title.trim().isEmpty() ? "Untitled event" : title.trim())
                    + " (" + formattedStart + "–" + formattedEnd + ")");
            }
        }
        return events;
    }
}
