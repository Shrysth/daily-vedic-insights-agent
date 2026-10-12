package com.vedicinsights.agent.domain.usecase;

import static org.junit.Assert.assertEquals;

import com.vedicinsights.agent.data.astronomical.OverlapEngine.OverlapSlot;
import com.vedicinsights.agent.data.astronomical.PanchangCalculator.FullPanchang;
import com.vedicinsights.agent.data.local.entity.VedicEventEntity;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.Test;

public class EvaluateVedicEventsUseCaseTest {
    private final EvaluateVedicEventsUseCase evaluator = new EvaluateVedicEventsUseCase();
    private final FullPanchang panchang = panchang();
    private final OverlapSlot slot = slot();
    private final ZoneId utc = ZoneId.of("UTC");

    @Test
    public void allModeRequiresEverySelectedCondition() {
        VedicEventEntity event = event("ALL");
        event.targetTithiIndex = 11;
        event.targetHora = "Shukra";

        assertEquals(0, evaluator.evaluate(panchang, slot, List.of(event), 1_000L, utc).size());
        event.targetHora = "Guru";
        assertEquals(1, evaluator.evaluate(panchang, slot, List.of(event), 1_000L, utc).size());
    }

    @Test
    public void anyModeAcceptsOneMatchingSelectedCondition() {
        VedicEventEntity event = event("ANY");
        event.targetTithiIndex = 11;
        event.targetHora = "Shukra";

        assertEquals(1, evaluator.evaluate(panchang, slot, List.of(event), 1_000L, utc).size());
    }

    @Test
    public void adhikaMasaIsAnEvaluatedNullableCriterion() {
        VedicEventEntity event = event("ALL");
        event.targetAdhikaMasa = true;
        assertEquals(0, evaluator.evaluate(panchang, slot, List.of(event), 1_000L, utc).size());

        panchang.adhikaMasa = true;
        assertEquals(1, evaluator.evaluate(panchang, slot, List.of(event), 1_000L, utc).size());
    }

    @Test
    public void frequencyCycleUsesLocalCalendarDaysFromCreation() {
        VedicEventEntity event = event("ALL");
        event.targetTithiIndex = 11;
        event.frequencyDays = 7;
        event.createdAtEpochMillis = Instant.parse("2024-01-01T12:00:00Z").toEpochMilli();

        long dayAfter = Instant.parse("2024-01-02T12:00:00Z").toEpochMilli();
        long weekAfter = Instant.parse("2024-01-08T12:00:00Z").toEpochMilli();
        assertEquals(0, evaluator.evaluate(panchang, slot, List.of(event), dayAfter, utc).size());
        assertEquals(1, evaluator.evaluate(panchang, slot, List.of(event), weekAfter, utc).size());
    }

    @Test
    public void eventWithoutSelectedCriteriaNeverMatches() {
        assertEquals(
            0,
            evaluator.evaluate(panchang, slot, List.of(event("ALL")), 1_000L, utc).size()
        );
    }

    private static VedicEventEntity event(String matchMode) {
        VedicEventEntity event = new VedicEventEntity();
        event.title = "Test event";
        event.matchMode = matchMode;
        event.frequencyDays = 1;
        return event;
    }

    private static FullPanchang panchang() {
        FullPanchang panchang = new FullPanchang();
        panchang.tithiIndex = 11;
        panchang.paksha = "Shukla";
        panchang.nakshatraIndex = 4;
        panchang.nakshatraPada = 2;
        panchang.purnimantaMonth = 3;
        return panchang;
    }

    private static OverlapSlot slot() {
        OverlapSlot slot = new OverlapSlot();
        slot.hora = "Guru";
        slot.chaughadiya = "Shubh";
        slot.lagna = "Vrishabha";
        return slot;
    }
}
