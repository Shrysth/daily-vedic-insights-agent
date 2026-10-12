package com.vedicinsights.agent.ui.home;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.vedicinsights.agent.R;
import com.vedicinsights.agent.data.astronomical.PanchangCalculator;
import com.vedicinsights.agent.ui.home.view.MoonSphereView;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MoonSphereView moonView = findViewById(R.id.heroMoonCanvas);
        TextView tithiBadge = findViewById(R.id.textTithiBadge);

        // Fetch live calculation instantly from the Moshier engine
        PanchangCalculator calc = new PanchangCalculator();
        double julianDay = (System.currentTimeMillis() / 86400000.0) + 2440587.5;
        PanchangCalculator.FullPanchang panchang = calc.compute(julianDay);

        // Update UI
        moonView.setMoonData(panchang.moonIllumination, panchang.paksha);
        tithiBadge.setText(panchang.paksha + " Tithi: " + panchang.tithiIndex);
        
        // Note: TimelineMatrixView logic would follow here, utilizing OverlapEngine
    }
}