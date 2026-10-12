package com.vedicinsights.agent.domain.usecase;

import com.vedicinsights.agent.data.astronomical.OverlapEngine.OverlapSlot;
import com.vedicinsights.agent.data.astronomical.PanchangCalculator.FullPanchang;
import com.vedicinsights.agent.data.local.entity.VedicEventEntity;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class EvaluateVedicEventsUseCase {
    public List<VedicEventEntity> evaluate(
        FullPanchang panchang,
        OverlapSlot currentSlot,
        List<VedicEventEntity> savedEvents,
        long nowMillis,
        ZoneId zone
    ) {
        List<VedicEventEntity> matches = new ArrayList<>();
        LocalDate today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate();

        for (VedicEventEntity event : savedEvents) {
            if (!event.isActive || !matchesFrequency(event, today, zone)) {
                continue;
            }
            if (!"ALL".equals(event.matchMode) && !"ANY".equals(event.matchMode)) {
                throw new IllegalStateException("Unknown event match mode: " + event.matchMode);
            }

            boolean[] criteria = {
                event.targetTithiIndex == null || event.targetTithiIndex == panchang.tithiIndex,
                event.targetPaksha == null || event.targetPaksha.equalsIgnoreCase(panchang.paksha),
                event.targetNakshatraIndex == null
                    || event.targetNakshatraIndex == panchang.nakshatraIndex,
                event.targetPada == null || event.targetPada == panchang.nakshatraPada,
                event.targetPurnimantaMonth == null
                    || event.targetPurnimantaMonth == panchang.purnimantaMonth,
                event.targetAdhikaMasa == null || event.targetAdhikaMasa == panchang.adhikaMasa,
                event.targetHora == null || event.targetHora.equalsIgnoreCase(currentSlot.hora),
                event.targetChaughadiya == null
                    || event.targetChaughadiya.equalsIgnoreCase(currentSlot.chaughadiya),
                event.targetLagna == null || event.targetLagna.equalsIgnoreCase(currentSlot.lagna)
            };
            boolean hasCriteria = false;
            boolean allMatch = true;
            boolean anyMatch = false;
            for (int index = 0; index < criteria.length; index++) {
                if (hasCriterion(event, index)) {
                    hasCriteria = true;
                    allMatch &= criteria[index];
                    anyMatch |= criteria[index];
                }
            }
            if (!hasCriteria) {
                continue;
            }
            if ("ALL".equals(event.matchMode) && allMatch
                || "ANY".equals(event.matchMode) && anyMatch) {
                matches.add(event);
            }
        }
        return matches;
    }

    private boolean matchesFrequency(VedicEventEntity event, LocalDate today, ZoneId zone) {
        if (event.frequencyDays < 1) {
            throw new IllegalStateException("Event frequency must be at least one day.");
        }
        if (event.createdAtEpochMillis <= 0L) {
            return true;
        }
        LocalDate created = Instant.ofEpochMilli(event.createdAtEpochMillis).atZone(zone).toLocalDate();
        long daysSinceCreation = today.toEpochDay() - created.toEpochDay();
        return daysSinceCreation >= 0 && daysSinceCreation % event.frequencyDays == 0;
    }

    private boolean hasCriterion(VedicEventEntity event, int index) {
        switch (index) {
            case 0: return event.targetTithiIndex != null;
            case 1: return event.targetPaksha != null;
            case 2: return event.targetNakshatraIndex != null;
            case 3: return event.targetPada != null;
            case 4: return event.targetPurnimantaMonth != null;
            case 5: return event.targetAdhikaMasa != null;
            case 6: return event.targetHora != null;
            case 7: return event.targetChaughadiya != null;
            case 8: return event.targetLagna != null;
            default: throw new IllegalArgumentException("Unknown event criterion: " + index);
        }
    }
}
