package com.vedicinsights.agent.ui.events;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.vedicinsights.agent.R;
import com.vedicinsights.agent.data.local.AppDatabase;
import com.vedicinsights.agent.data.local.entity.VedicEventEntity;

public class CreateVedicEventActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_vedic_event);

        EditText titleInput = findViewById(R.id.inputEventTitle);
        Button saveButton = findViewById(R.id.btnSaveEvent);

        saveButton.setOnClickListener(v -> {
            VedicEventEntity newEvent = new VedicEventEntity();
            newEvent.title = titleInput.getText().toString();
            newEvent.category = "Sadhana / Pooja"; // Example hardcoded from chip selection
            newEvent.isActive = true;
            
            // Set dynamic nullable triggers from UI checkboxes
            newEvent.targetPaksha = "Shukla"; 
            newEvent.targetTithiIndex = 11; // E.g., Ekadashi

            // Run DB insert on background thread
            new Thread(() -> {
                AppDatabase.getInstance(getApplicationContext()).vedicEventDao().insertEvent(newEvent);
                runOnUiThread(this::finish);
            }).start();
        });
    }
}