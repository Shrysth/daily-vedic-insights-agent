package com.vedicinsights.agent.domain.usecase;

import com.vedicinsights.agent.data.astronomical.PanchangCalculator.FullPanchang;
import com.vedicinsights.agent.data.local.entity.VedicEventEntity;
import java.util.ArrayList;
import java.util.List;

public class EvaluateVedicEventsUseCase {

    public List<VedicEventEntity> evaluate(FullPanchang today, List<VedicEventEntity> savedEvents) {
        List<VedicEventEntity> matches = new ArrayList<>();

        for (VedicEventEntity e : savedEvents) {
            if (!e.isActive) continue;

            // 1. Tithi check (if set)
            if (e.targetTithiIndex != null && e.targetTithiIndex != today.tithiIndex) continue;

            // 2. Paksha check (if set)
            if (e.targetPaksha != null && !e.targetPaksha.equalsIgnoreCase(today.paksha)) continue;

            // 3. Nakshatra check (if set)
            if (e.targetNakshatraIndex != null && e.targetNakshatraIndex != today.nakshatraIndex) continue;

            // 4. Pada check (if set)
            if (e.targetPada != null && e.targetPada != today.nakshatraPada) continue;

            // 5. Purnimanta Month check (if set)
            if (e.targetPurnimantaMonth != null && e.targetPurnimantaMonth != today.purnimantaMonth) continue;

            // Match confirmed: All defined parameters satisfied
            matches.add(e);
        }
        return matches;
    }
}