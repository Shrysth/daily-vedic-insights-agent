package com.vedicinsights.agent.ui.events;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.vedicinsights.agent.R;
import com.vedicinsights.agent.data.local.AppDatabase;
import com.vedicinsights.agent.data.local.entity.VedicEventEntity;

public class CreateVedicEventActivity extends AppCompatActivity {
    private static final String TAG = "CreateVedicEvent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_vedic_event);
        findViewById(R.id.btnSaveEvent).setOnClickListener(view -> saveEvent());
    }

    private void saveEvent() {
        EditText titleInput = findViewById(R.id.inputEventTitle);
        String title = titleInput.getText().toString().trim();
        if (title.isEmpty()) {
            titleInput.setError("Enter an event title.");
            titleInput.requestFocus();
            return;
        }

        VedicEventEntity event = new VedicEventEntity();
        event.title = title;
        event.category = selectedString(R.id.spinnerEventCategory);
        event.notes = ((EditText) findViewById(R.id.inputEventNotes)).getText().toString().trim();
        event.targetTithiIndex = selectedIndex(R.id.spinnerTithi);
        event.targetPaksha = selectedStringOrNull(R.id.spinnerPaksha);
        event.targetNakshatraIndex = selectedIndex(R.id.spinnerNakshatra);
        event.targetPada = selectedIndex(R.id.spinnerPada);
        event.targetPurnimantaMonth = selectedIndex(R.id.spinnerMonth);
        int adhikaMasaPosition = ((Spinner) findViewById(R.id.spinnerAdhikaMasa)).getSelectedItemPosition();
        event.targetAdhikaMasa = adhikaMasaPosition == 0 ? null : adhikaMasaPosition == 2;
        event.targetHora = selectedStringOrNull(R.id.spinnerHora);
        event.targetChaughadiya = selectedStringOrNull(R.id.spinnerChaughadiya);
        event.targetLagna = selectedStringOrNull(R.id.spinnerLagna);
        event.matchMode = ((Spinner) findViewById(R.id.spinnerMatchMode)).getSelectedItemPosition() == 0
            ? "ALL" : "ANY";
        int frequencyPosition = ((Spinner) findViewById(R.id.spinnerFrequency)).getSelectedItemPosition();
        event.frequencyDays = getResources().getIntArray(R.array.frequency_days)[frequencyPosition];
        event.createdAtEpochMillis = System.currentTimeMillis();
        if (!hasAtLeastOneCondition(event)) {
            Toast.makeText(this, "Choose at least one Vedic condition.", Toast.LENGTH_LONG).show();
            return;
        }

        findViewById(R.id.btnSaveEvent).setEnabled(false);
        new Thread(() -> {
            try {
                AppDatabase.getInstance(getApplicationContext()).vedicEventDao().insertEvent(event);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Recurring event saved.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (RuntimeException exception) {
                Log.e(TAG, "Could not save recurring event.", exception);
                runOnUiThread(() -> {
                    findViewById(R.id.btnSaveEvent).setEnabled(true);
                    Toast.makeText(this, "Event was not saved: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private String selectedString(int spinnerId) {
        return ((Spinner) findViewById(spinnerId)).getSelectedItem().toString();
    }

    private String selectedStringOrNull(int spinnerId) {
        Spinner spinner = findViewById(spinnerId);
        return spinner.getSelectedItemPosition() == 0 ? null : spinner.getSelectedItem().toString();
    }

    private Integer selectedIndex(int spinnerId) {
        int position = ((Spinner) findViewById(spinnerId)).getSelectedItemPosition();
        return position == 0 ? null : position;
    }

    private boolean hasAtLeastOneCondition(VedicEventEntity event) {
        return event.targetTithiIndex != null
            || event.targetPaksha != null
            || event.targetNakshatraIndex != null
            || event.targetPada != null
            || event.targetPurnimantaMonth != null
            || event.targetAdhikaMasa != null
            || event.targetHora != null
            || event.targetChaughadiya != null
            || event.targetLagna != null;
    }
}
