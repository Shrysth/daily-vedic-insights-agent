package com.vedicinsights.agent.data.astronomical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import org.junit.Test;

public class OverlapEngineTest {
    private static final long HOUR = 60 * 60 * 1000L;

    @Test
    public void weekdaySetsPlanetaryHoraAndEightDaySegments() {
        List<OverlapEngine.OverlapSlot> monday = new OverlapEngine().generateTimeline(
            0L, 12 * HOUR, 24 * HOUR, 2, 28.6139, 77.2090
        );
        List<OverlapEngine.OverlapSlot> sunday = new OverlapEngine().generateTimeline(
            0L, 12 * HOUR, 24 * HOUR, 1, 28.6139, 77.2090
        );

        assertEquals(96, monday.size());
        assertEquals("Chandra", monday.get(0).hora);
        assertEquals("Shani", monday.get(4).hora);
        assertEquals("Surya", sunday.get(0).hora);
        assertEquals("Amrit", monday.get(0).chaughadiya);
        assertEquals("Labh", monday.get(36).chaughadiya);
        assertEquals("Shukra", monday.get(48).hora);
        assertEquals("Amrit", monday.get(48).chaughadiya);
        assertEquals("Shubh", sunday.get(48).chaughadiya);
    }

    @Test
    public void lagnaIsCalculatedRatherThanFixed() {
        List<OverlapEngine.OverlapSlot> slots = new OverlapEngine().generateTimeline(
            1718924400000L,
            1718971200000L,
            1719010800000L,
            6,
            28.6139,
            77.2090
        );

        assertNotNull(slots.get(0).lagna);
        assertNotEquals(slots.get(0).lagna, slots.get(24).lagna);
    }
}
