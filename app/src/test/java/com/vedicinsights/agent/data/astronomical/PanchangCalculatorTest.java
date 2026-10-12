package com.vedicinsights.agent.data.astronomical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.Test;

public class PanchangCalculatorTest {
    private static final ZoneId DELHI_ZONE = ZoneId.of("Asia/Kolkata");
    private static final double DELHI_LATITUDE = 28.6139;
    private static final double DELHI_LONGITUDE = 77.2090;

    @Test
    public void solarDayUsesLocalDateAndReturnsOrderedSunriseAndSunset() {
        AstronomicalTimeCalculator.SolarDay solarDay = new AstronomicalTimeCalculator()
            .getSolarDay(LocalDate.of(2024, 6, 21), DELHI_ZONE, DELHI_LATITUDE, DELHI_LONGITUDE);
        LocalDate sunriseDate = Instant.ofEpochMilli(solarDay.sunriseMillis).atZone(DELHI_ZONE).toLocalDate();
        LocalDate sunsetDate = Instant.ofEpochMilli(solarDay.sunsetMillis).atZone(DELHI_ZONE).toLocalDate();

        assertEquals(LocalDate.of(2024, 6, 21), sunriseDate);
        assertEquals(sunriseDate, sunsetDate);
        assertTrue(solarDay.sunsetMillis > solarDay.sunriseMillis);
        assertTrue(
            Math.abs(solarDay.sunriseMillis - Instant.parse("2024-06-20T23:54:00Z").toEpochMilli())
                < 10 * 60 * 1000L
        );
        assertTrue(
            Math.abs(solarDay.sunsetMillis - Instant.parse("2024-06-21T13:51:00Z").toEpochMilli())
                < 10 * 60 * 1000L
        );
    }

    @Test
    public void vaarBeforeLocalSunriseBelongsToPreviousDay() {
        double beforeSunrise = Instant.parse("2024-06-20T23:00:00Z").toEpochMilli()
            / 86400000.0 + 2440587.5;
        PanchangCalculator.FullPanchang panchang = new PanchangCalculator().compute(
            beforeSunrise, DELHI_LATITUDE, DELHI_LONGITUDE, DELHI_ZONE
        );

        assertEquals(5, panchang.vaarIndex);
    }

    @Test
    public void purnimantaMonthUsesThePrecedingAmavasyaAndPaksha() {
        PanchangCalculator calculator = new PanchangCalculator();
        double shuklaDate = Instant.parse("2024-06-15T12:00:00Z").toEpochMilli()
            / 86400000.0 + 2440587.5;
        double krishnaDate = Instant.parse("2024-06-25T12:00:00Z").toEpochMilli()
            / 86400000.0 + 2440587.5;
        PanchangCalculator.FullPanchang shukla = calculator.compute(
            shuklaDate, DELHI_LATITUDE, DELHI_LONGITUDE, DELHI_ZONE
        );
        PanchangCalculator.FullPanchang krishna = calculator.compute(
            krishnaDate, DELHI_LATITUDE, DELHI_LONGITUDE, DELHI_ZONE
        );

        assertEquals("Shukla", shukla.paksha);
        assertEquals("Krishna", krishna.paksha);
        assertEquals(3, shukla.purnimantaMonth);
        assertEquals(4, krishna.purnimantaMonth);
        assertFalse(shukla.adhikaMasa);
    }

    @Test
    public void masaWithNoSolarIngressIsMarkedAdhika() {
        double adhikaShravanaDate = Instant.parse("2023-08-01T12:00:00Z").toEpochMilli()
            / 86400000.0 + 2440587.5;
        PanchangCalculator.FullPanchang panchang = new PanchangCalculator().compute(
            adhikaShravanaDate, DELHI_LATITUDE, DELHI_LONGITUDE, DELHI_ZONE
        );

        assertEquals(5, panchang.purnimantaMonth);
        assertTrue(panchang.adhikaMasa);
    }

    @Test
    public void panchangExposesSolarTimesAndValidIndexes() {
        double julianDay = Instant.parse("2024-06-21T12:00:00Z").toEpochMilli()
            / 86400000.0 + 2440587.5;
        PanchangCalculator.FullPanchang panchang = new PanchangCalculator().compute(
            julianDay, DELHI_LATITUDE, DELHI_LONGITUDE, DELHI_ZONE
        );

        assertTrue(panchang.tithiIndex >= 1 && panchang.tithiIndex <= 15);
        assertTrue(panchang.nakshatraIndex >= 1 && panchang.nakshatraIndex <= 27);
        assertTrue(panchang.nakshatraPada >= 1 && panchang.nakshatraPada <= 4);
        assertTrue(panchang.sunriseMillis < panchang.sunsetMillis);
        assertTrue(panchang.sunsetMillis < panchang.nextSunriseMillis);
        assertNotEquals(panchang.sunriseMillis, panchang.nextSunriseMillis);
    }
}
