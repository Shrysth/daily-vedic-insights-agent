package com.vedicinsights.agent.ui.settings;

import android.Manifest;
import android.app.AlarmManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.vedicinsights.agent.R;
import com.vedicinsights.agent.data.network.GeminiApiKeyStore;
import com.vedicinsights.agent.data.network.BriefingPrivacySettings;
import com.vedicinsights.agent.worker.AlarmReceiver;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private ActivityResultLauncher<String[]> permissionLauncher;
    private TextView timeView;
    private TextView apiKeyStatusView;
    private TextView settingsStatusView;
    private EditText apiKeyInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        timeView = findViewById(R.id.textAwakeningTime);
        apiKeyStatusView = findViewById(R.id.textApiKeyStatus);
        settingsStatusView = findViewById(R.id.textSettingsStatus);
        apiKeyInput = findViewById(R.id.inputGeminiApiKey);

        permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> refreshPermissionStatus()
        );

        int[] time = AlarmReceiver.getSavedTime(this);
        updateAwakeningTime(time[0], time[1]);
        refreshPermissionStatus();
        refreshApiKeyStatus();
        CheckBox sharePersonalDetailsWithAi = findViewById(R.id.checkIncludePersonalDetailsWithAi);
        sharePersonalDetailsWithAi.setChecked(BriefingPrivacySettings.includePersonalDetailsWithAi(this));
        updatePersonalDetailsSharingStatus(sharePersonalDetailsWithAi.isChecked());
        sharePersonalDetailsWithAi.setOnCheckedChangeListener((button, checked) -> {
            BriefingPrivacySettings.setIncludePersonalDetailsWithAi(this, checked);
            updatePersonalDetailsSharingStatus(checked);
        });

        Button timePickerButton = findViewById(R.id.btnEditAwakeningTime);
        timePickerButton.setOnClickListener(view -> {
            int[] savedTime = AlarmReceiver.getSavedTime(this);
            showTimePicker(savedTime[0], savedTime[1]);
        });
        findViewById(R.id.btnExactAlarmPermission).setOnClickListener(view -> requestExactAlarmPermission());
        findViewById(R.id.btnRequestPermissions).setOnClickListener(view -> requestRuntimePermissions());
        findViewById(R.id.btnSaveApiKey).setOnClickListener(view -> saveApiKey());
        findViewById(R.id.btnDeleteApiKey).setOnClickListener(view -> deleteApiKey());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (settingsStatusView != null) {
            refreshPermissionStatus();
        }
    }

    private void showTimePicker(int hour, int minute) {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText("Set Daily Awakening")
            .build();
        picker.addOnPositiveButtonClickListener(view -> {
            AlarmReceiver.scheduleDailyAlarm(this, picker.getHour(), picker.getMinute());
            updateAwakeningTime(picker.getHour(), picker.getMinute());
            Toast.makeText(this, "Daily briefing schedule saved.", Toast.LENGTH_SHORT).show();
        });
        picker.show(getSupportFragmentManager(), "AWAKENING_TIME");
    }

    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            settingsStatusView.setText("Exact alarm access is not required on this Android version.");
            return;
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            settingsStatusView.setText("Alarm service is unavailable.");
            return;
        }
        if (alarmManager.canScheduleExactAlarms()) {
            settingsStatusView.setText("Exact alarms are allowed.");
            return;
        }
        Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            .setData(Uri.fromParts("package", getPackageName(), null));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException | SecurityException exception) {
            settingsStatusView.setText("Could not open exact-alarm settings: " + exception.getMessage());
        }
    }

    private void requestRuntimePermissions() {
        permissionLauncher.launch(new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.POST_NOTIFICATIONS
        });
    }

    private void refreshPermissionStatus() {
        boolean location = androidx.core.content.ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            || androidx.core.content.ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED;
        boolean calendar = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_CALENDAR
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED;
        boolean notifications = Build.VERSION.SDK_INT < 33
            || ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        boolean exactAlarms = Build.VERSION.SDK_INT < Build.VERSION_CODES.S
            || alarmManager != null && alarmManager.canScheduleExactAlarms();
        settingsStatusView.setText(
            "Location: " + status(location) + " · Calendar: " + status(calendar)
                + " · Notifications: " + status(notifications)
                + " · Exact alarms: " + status(exactAlarms)
        );
    }

    private void refreshApiKeyStatus() {
        try {
            boolean configured = GeminiApiKeyStore.read(this) != null;
            apiKeyStatusView.setText(configured
                ? "A key is stored encrypted. Enter a new key to replace it."
                : "No key stored. AI briefings require a Gemini API key.");
        } catch (GeneralSecurityException | IOException | IllegalArgumentException exception) {
            apiKeyStatusView.setText("Could not read encrypted key: " + exception.getMessage());
        }
    }

    private void updatePersonalDetailsSharingStatus(boolean enabled) {
        TextView status = findViewById(R.id.textPersonalDetailsSharingStatus);
        status.setText(enabled
            ? "Calendar entries and matched event names will be included in Gemini requests."
            : "Off by default. Personal event details stay on-device unless you turn this on.");
    }

    private void saveApiKey() {
        try {
            GeminiApiKeyStore.save(this, apiKeyInput.getText().toString());
            apiKeyInput.setText("");
            refreshApiKeyStatus();
            Toast.makeText(this, "Encrypted API key saved.", Toast.LENGTH_SHORT).show();
        } catch (GeneralSecurityException | IOException | IllegalArgumentException exception) {
            apiKeyStatusView.setText("API key was not saved: " + exception.getMessage());
        }
    }

    private void deleteApiKey() {
        GeminiApiKeyStore.delete(this);
        apiKeyInput.setText("");
        refreshApiKeyStatus();
        Toast.makeText(this, "Saved API key removed.", Toast.LENGTH_SHORT).show();
    }

    private void updateAwakeningTime(int hour, int minute) {
        timeView.setText(String.format(
            Locale.getDefault(),
            "Next briefing: %02d:%02d",
            hour,
            minute
        ) + (AlarmReceiver.hasSavedSchedule(this) ? "" : " · not scheduled yet"));
    }

    private String status(boolean enabled) {
        return enabled ? "on" : "off";
    }
}
