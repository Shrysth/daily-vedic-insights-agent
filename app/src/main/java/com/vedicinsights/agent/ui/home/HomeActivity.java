package com.vedicinsights.agent.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.vedicinsights.agent.R;
import com.vedicinsights.agent.data.astronomical.AstronomicalTimeCalculator;
import com.vedicinsights.agent.data.astronomical.OverlapEngine;
import com.vedicinsights.agent.data.astronomical.OverlapEngine.OverlapSlot;
import com.vedicinsights.agent.data.astronomical.PanchangCalculator;
import com.vedicinsights.agent.data.calendar.DeviceCalendarRepository;
import com.vedicinsights.agent.data.location.LocationRepository;
import com.vedicinsights.agent.data.local.AppDatabase;
import com.vedicinsights.agent.data.local.entity.VedicEventEntity;
import com.vedicinsights.agent.domain.usecase.EvaluateVedicEventsUseCase;
import com.vedicinsights.agent.ui.events.CreateVedicEventActivity;
import com.vedicinsights.agent.ui.home.view.MoonSphereView;
import com.vedicinsights.agent.ui.settings.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {
    private static final String PERMISSIONS_PREFERENCES = "runtime_permissions";
    private static final String HAS_REQUESTED_PERMISSIONS = "has_requested";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private ActivityResultLauncher<String[]> permissionLauncher;
    private boolean firstResume = true;
    private boolean permissionRequestPending;
    private TextView statusView;
    private TextView panchangSummaryView;
    private TextView sunriseSunsetView;
    private TextView anchorCardsView;
    private TextView occurrenceView;
    private TextView festivalBannerView;
    private TextView tithiBadgeView;
    private MoonSphereView moonView;
    private LinearLayout timelineContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        statusView = findViewById(R.id.textStatus);
        panchangSummaryView = findViewById(R.id.textPanchangSummary);
        sunriseSunsetView = findViewById(R.id.textSunriseSunset);
        anchorCardsView = findViewById(R.id.textAnchorCards);
        occurrenceView = findViewById(R.id.textNextOccurrences);
        festivalBannerView = findViewById(R.id.textFestivalBanner);
        tithiBadgeView = findViewById(R.id.textTithiBadge);
        moonView = findViewById(R.id.heroMoonCanvas);
        timelineContainer = findViewById(R.id.timelineContainer);

        findViewById(R.id.btnOpenEvents).setOnClickListener(view ->
            startActivity(new Intent(this, CreateVedicEventActivity.class)));
        findViewById(R.id.btnOpenSettings).setOnClickListener(view ->
            startActivity(new Intent(this, SettingsActivity.class)));
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_home);
        bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_events) {
                startActivity(new Intent(this, CreateVedicEventActivity.class));
            } else if (item.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            }
            return true;
        });

        permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                permissionRequestPending = false;
                refreshLocationAndPanchang();
            }
        );
        SharedPreferences permissionPreferences =
            getSharedPreferences(PERMISSIONS_PREFERENCES, MODE_PRIVATE);
        if (!LocationRepository.hasLocationPermission(this)
            && !permissionPreferences.getBoolean(HAS_REQUESTED_PERMISSIONS, false)) {
            permissionPreferences.edit().putBoolean(HAS_REQUESTED_PERMISSIONS, true).apply();
            requestRuntimePermissions();
        } else {
            refreshLocationAndPanchang();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firstResume) {
            firstResume = false;
        } else if (!permissionRequestPending) {
            refreshLocationAndPanchang();
        }
    }

    private void requestRuntimePermissions() {
        permissionRequestPending = true;
        permissionLauncher.launch(new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.POST_NOTIFICATIONS
        });
    }

    private void refreshLocationAndPanchang() {
        boolean locationAllowed = LocationRepository.hasLocationPermission(this);
        boolean calendarAllowed = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
            == PackageManager.PERMISSION_GRANTED;
        boolean notificationsAllowed = android.os.Build.VERSION.SDK_INT < 33
            || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
        String permissionStatus = "Location " + (locationAllowed ? "on" : "off")
            + " · Calendar " + (calendarAllowed ? "on" : "off")
            + " · Notifications " + (notificationsAllowed ? "on" : "off");

        if (!locationAllowed) {
            statusView.setText(permissionStatus + "\nGrant location permission to calculate local Panchang.");
            return;
        }
        statusView.setText(permissionStatus + "\nGetting current location on this device…");
        LocationRepository.requestCurrentLocation(
            this,
            coordinates -> runOnUiThread(() -> {
                statusView.setText(permissionStatus + "\nLocal calculations updated.");
                loadPanchang(coordinates);
            }),
            exception -> runOnUiThread(() -> statusView.setText(
                permissionStatus + "\nLocation unavailable: " + exception.getMessage()
            ))
        );
    }

    private void loadPanchang(LocationRepository.Coordinates coordinates) {
        long nowMillis = System.currentTimeMillis();
        ZoneId zone = ZoneId.systemDefault();
        executor.execute(() -> {
            try {
                double julianDay = nowMillis / 86400000.0 + 2440587.5;
                PanchangCalculator.FullPanchang panchang = new PanchangCalculator().compute(
                    julianDay,
                    coordinates.latitude,
                    coordinates.longitude,
                    zone
                );
                List<OverlapSlot> timeline = timelineForNow(panchang, coordinates, nowMillis, zone);
                List<VedicEventEntity> events =
                    AppDatabase.getInstance(getApplicationContext()).vedicEventDao().getAllActive();
                List<String> calendarEvents = DeviceCalendarRepository.hasCalendarPermission(
                    getApplicationContext()
                ) ? DeviceCalendarRepository.getTodayEvents(getApplicationContext()) : List.of();
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        renderPanchang(panchang, timeline, events, calendarEvents, nowMillis, zone);
                        occurrenceView.setText("Finding upcoming matches…");
                    }
                });
                String occurrences = findNextOccurrences(events, coordinates, zone, LocalDate.now(zone));
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        occurrenceView.setText(occurrences);
                    }
                });
            } catch (IllegalArgumentException | IllegalStateException | SecurityException exception) {
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        statusView.setText("Panchang calculation failed: " + exception.getMessage());
                    }
                });
            }
        });
    }

    private List<OverlapSlot> timelineForNow(
        PanchangCalculator.FullPanchang panchang,
        LocationRepository.Coordinates coordinates,
        long nowMillis,
        ZoneId zone
    ) {
        long sunrise = panchang.sunriseMillis;
        long sunset = panchang.sunsetMillis;
        long nextSunrise = panchang.nextSunriseMillis;
        if (nowMillis < sunrise) {
            LocalDate date = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate();
            AstronomicalTimeCalculator.SolarDay previousDay = new AstronomicalTimeCalculator()
                .getSolarDay(date.minusDays(1), zone, coordinates.latitude, coordinates.longitude);
            sunrise = previousDay.sunriseMillis;
            sunset = previousDay.sunsetMillis;
            nextSunrise = panchang.sunriseMillis;
        }
        return new OverlapEngine().generateTimeline(
            sunrise,
            sunset,
            nextSunrise,
            panchang.vaarIndex,
            coordinates.latitude,
            coordinates.longitude
        );
    }

    private void renderPanchang(
        PanchangCalculator.FullPanchang panchang,
        List<OverlapSlot> timeline,
        List<VedicEventEntity> events,
        List<String> calendarEvents,
        long nowMillis,
        ZoneId zone
    ) {
        LocalDate today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate();
        ((TextView) findViewById(R.id.textDate)).setText(
            DateTimeFormatter.ofPattern("EEEE · d MMMM", Locale.getDefault()).format(today)
        );
        String tithiName = getResources().getStringArray(R.array.tithi_options)[panchang.tithiIndex];
        String monthName = getResources().getStringArray(R.array.month_options)[panchang.purnimantaMonth];
        String pakshaTithi = panchang.paksha + " " + tithiName;
        tithiBadgeView.setText(pakshaTithi);
        moonView.setMoonData(panchang.moonIllumination, panchang.paksha);
        panchangSummaryView.setText(
            "Nakshatra " + getResources().getStringArray(R.array.nakshatra_options)[panchang.nakshatraIndex]
                + " · Pada " + panchang.nakshatraPada
                + " · " + monthName + (panchang.adhikaMasa ? " (Adhika)" : "")
                + " · Vaar " + getResources().getStringArray(R.array.weekday_names)[panchang.vaarIndex]
        );
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
        sunriseSunsetView.setText(
            "Sunrise " + timeFormat.format(Instant.ofEpochMilli(panchang.sunriseMillis).atZone(zone))
                + "  ·  Sunset " + timeFormat.format(Instant.ofEpochMilli(panchang.sunsetMillis).atZone(zone))
        );

        OverlapSlot currentSlot = findCurrentSlot(timeline, nowMillis);
        List<VedicEventEntity> matchingEvents = new EvaluateVedicEventsUseCase().evaluate(
            panchang,
            currentSlot,
            events,
            nowMillis,
            zone
        );
        StringBuilder matchingEventNames = new StringBuilder();
        for (VedicEventEntity event : matchingEvents) {
            if (matchingEventNames.length() > 0) {
                matchingEventNames.append(", ");
            }
            matchingEventNames.append(event.title == null || event.title.trim().isEmpty()
                ? "Untitled event" : event.title.trim());
        }
        anchorCardsView.setText(
            "Tithi\n" + pakshaTithi
                + "\n\nNakshatra\n" + getResources().getStringArray(R.array.nakshatra_options)[panchang.nakshatraIndex]
                + " · Pada " + panchang.nakshatraPada
                + "\n\nLunar month\n" + monthName + (panchang.adhikaMasa ? " · Adhika Masa" : "")
                + "\n\nCurrent anchors\n" + currentSlot.hora + " Hora · "
                + currentSlot.chaughadiya + " Chaughadiya · " + currentSlot.lagna + " Lagna"
                + "\n\nMatched events\n"
                + (matchingEventNames.length() == 0 ? "None at this time" : matchingEventNames)
        );
        if (!DeviceCalendarRepository.hasCalendarPermission(this)) {
            festivalBannerView.setText("Calendar access is off. Grant permission to show local observances.");
        } else if (calendarEvents.isEmpty()) {
            festivalBannerView.setText("No calendar observances are scheduled for today.");
        } else {
            festivalBannerView.setText(String.join("\n", calendarEvents));
        }
        drawTimeline(
            timeline,
            timeline.get(0).startEpochMillis,
            timeline.get(timeline.size() - 1).endEpochMillis,
            zone
        );
    }

    private void drawTimeline(List<OverlapSlot> slots, long sunrise, long nextSunrise, ZoneId zone) {
        timelineContainer.removeAllViews();
        long duration = nextSunrise - sunrise;
        for (int hour = 0; hour < 24; hour++) {
            long binStart = sunrise + duration * hour / 24;
            long binEnd = sunrise + duration * (hour + 1) / 24;
            OverlapSlot representative = null;
            for (OverlapSlot slot : slots) {
                if (slot.startEpochMillis >= binStart && slot.startEpochMillis < binEnd) {
                    representative = slot;
                    break;
                }
            }
            if (representative == null) {
                throw new IllegalStateException("A timeline hour has no calculated time segment.");
            }

            LinearLayout cell = new LinearLayout(this);
            cell.setOrientation(LinearLayout.VERTICAL);
            cell.setGravity(Gravity.CENTER);
            cell.setPadding(4, 8, 4, 8);
            cell.setBackgroundResource(R.drawable.bg_chip);
            int color = representative.isGoldenConjunction
                ? ContextCompat.getColor(this, R.color.status_good)
                : representative.isDiscordantCaution
                    ? ContextCompat.getColor(this, R.color.status_caution)
                    : ContextCompat.getColor(this, R.color.text_primary);
            cell.setBackgroundTintList(ColorStateList.valueOf(
                representative.isGoldenConjunction
                    ? ContextCompat.getColor(this, R.color.cosmic_surface_variant)
                    : ContextCompat.getColor(this, R.color.cosmic_surface)
            ));

            TextView clock = new TextView(this);
            clock.setText(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
                .format(Instant.ofEpochMilli(binStart).atZone(zone)));
            clock.setTextColor(color);
            clock.setTextSize(11);
            clock.setGravity(Gravity.CENTER);
            TextView condition = new TextView(this);
            condition.setText(
                representative.hora + "\n" + representative.chaughadiya + "\n" + representative.lagna
            );
            condition.setTextColor(color);
            condition.setTextSize(10);
            condition.setGravity(Gravity.CENTER);
            cell.addView(clock);
            cell.addView(condition);
            cell.setContentDescription(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
                .format(Instant.ofEpochMilli(binStart).atZone(zone))
                + ", " + representative.hora + " hora, " + representative.chaughadiya
                + " chaughadiya, " + representative.lagna + " lagna");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.timeline_cell_width),
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 6, 0);
            timelineContainer.addView(cell, params);
        }
    }

    private String findNextOccurrences(
        List<VedicEventEntity> events,
        LocationRepository.Coordinates coordinates,
        ZoneId zone,
        LocalDate startDate
    ) {
        if (events.isEmpty()) {
            return "Add a recurring event to simulate upcoming matches.";
        }
        List<String> occurrences = new ArrayList<>();
        EvaluateVedicEventsUseCase evaluator = new EvaluateVedicEventsUseCase();
        for (int dayOffset = 0; dayOffset < 90 && occurrences.size() < 3; dayOffset++) {
            LocalDate date = startDate.plusDays(dayOffset);
            AstronomicalTimeCalculator.SolarDay solarDay = new AstronomicalTimeCalculator()
                .getSolarDay(date, zone, coordinates.latitude, coordinates.longitude);
            AstronomicalTimeCalculator.SolarDay nextSolarDay = new AstronomicalTimeCalculator()
                .getSolarDay(date.plusDays(1), zone, coordinates.latitude, coordinates.longitude);
            long calculationTime = solarDay.sunriseMillis + 60_000L;
            PanchangCalculator.FullPanchang panchang = new PanchangCalculator().compute(
                calculationTime / 86400000.0 + 2440587.5,
                coordinates.latitude,
                coordinates.longitude,
                zone
            );
            List<OverlapSlot> slots = new OverlapEngine().generateTimeline(
                solarDay.sunriseMillis,
                solarDay.sunsetMillis,
                nextSolarDay.sunriseMillis,
                panchang.vaarIndex,
                coordinates.latitude,
                coordinates.longitude
            );
            for (VedicEventEntity event : events) {
                for (OverlapSlot slot : slots) {
                    if (!evaluator.evaluate(
                        panchang,
                        slot,
                        List.of(event),
                        slot.startEpochMillis,
                        zone
                    ).isEmpty()) {
                        String dateLabel = DateTimeFormatter.ofPattern("EEE, d MMM", Locale.getDefault())
                            .format(date);
                        String eventName = event.title == null || event.title.trim().isEmpty()
                            ? "Untitled event" : event.title.trim();
                        occurrences.add(dateLabel + " · " + eventName + " · " + slot.hora
                            + " " + slot.chaughadiya);
                        break;
                    }
                }
                if (occurrences.size() == 3) {
                    break;
                }
            }
        }
        if (occurrences.isEmpty()) {
            return "No matches found in the next 90 days.";
        }
        StringBuilder output = new StringBuilder();
        for (int index = 0; index < occurrences.size(); index++) {
            if (output.length() > 0) {
                output.append('\n');
            }
            output.append(index + 1).append(". ").append(occurrences.get(index));
        }
        return output.toString();
    }

    private OverlapSlot findCurrentSlot(List<OverlapSlot> slots, long nowMillis) {
        for (OverlapSlot slot : slots) {
            if (nowMillis >= slot.startEpochMillis && nowMillis < slot.endEpochMillis) {
                return slot;
            }
        }
        throw new IllegalStateException("The current time is outside the displayed timeline.");
    }

    @Override
    protected void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }
}
