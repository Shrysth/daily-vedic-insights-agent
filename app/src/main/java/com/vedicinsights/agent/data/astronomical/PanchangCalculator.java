package com.vedicinsights.agent.data.astronomical;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import io.github.cosinekitty.astronomy.Time;

public class PanchangCalculator {
    public static class FullPanchang {
        public int tithiIndex;
        public String paksha;
        public int nakshatraIndex;
        public int nakshatraPada;
        public int purnimantaMonth;
        public boolean adhikaMasa;
        public double moonIllumination;
        public int vaarIndex;
        public double moonLongitude;
        public double sunLongitude;
        public long sunriseMillis;
        public long sunsetMillis;
        public long nextSunriseMillis;
        public String hora;
        public String chaughadiya;
        public String lagna;
    }

    public FullPanchang compute(double julianDayUt, double latitude, double longitude) {
        return compute(julianDayUt, latitude, longitude, ZoneId.systemDefault());
    }

    public FullPanchang compute(double julianDayUt, double latitude, double longitude, ZoneId zone) {
        validateCoordinates(latitude, longitude);
        LocalDate localDate = Instant.ofEpochMilli(toEpochMillis(julianDayUt)).atZone(zone).toLocalDate();
        AstronomicalTimeCalculator timeCalculator = new AstronomicalTimeCalculator();
        AstronomicalTimeCalculator.SolarDay solarDay =
            timeCalculator.getSolarDay(localDate, zone, latitude, longitude);
        AstronomicalTimeCalculator.SolarDay nextSolarDay =
            timeCalculator.getSolarDay(localDate.plusDays(1), zone, latitude, longitude);

        FullPanchang result = new FullPanchang();
        Time time = SiderealAstronomy.fromJulianDay(julianDayUt);
        double ayanamsa = SiderealAstronomy.lahiriAyanamsa(time);
        double tropicalSunLongitude = SiderealAstronomy.tropicalSunLongitude(time);
        double tropicalMoonLongitude = SiderealAstronomy.tropicalMoonLongitude(time);
        result.sunLongitude = SiderealAstronomy.normalize(tropicalSunLongitude - ayanamsa);
        result.moonLongitude = SiderealAstronomy.normalize(tropicalMoonLongitude - ayanamsa);
        double elongation = SiderealAstronomy.normalize(tropicalMoonLongitude - tropicalSunLongitude);
        int rawTithi = Math.min(30, (int) (elongation / 12.0) + 1);
        result.paksha = rawTithi <= 15 ? "Shukla" : "Krishna";
        result.tithiIndex = rawTithi <= 15 ? rawTithi : rawTithi - 15;
        result.moonIllumination = (1.0 - Math.cos(Math.toRadians(elongation))) / 2.0;

        double previousNewMoon = findAdjacentNewMoon(julianDayUt, -1);
        double nextNewMoon = findAdjacentNewMoon(julianDayUt, 1);
        double previousNewMoonSun = siderealSunLongitude(previousNewMoon);
        double nextNewMoonSun = siderealSunLongitude(nextNewMoon);
        int previousSunSign = (int) (previousNewMoonSun / 30.0);
        int nextSunSign = (int) (nextNewMoonSun / 30.0);
        result.adhikaMasa = previousSunSign == nextSunSign;
        int amantaMonth = ((previousSunSign + 1) % 12) + 1;
        result.purnimantaMonth = result.paksha.equals("Krishna") && !result.adhikaMasa
            ? (amantaMonth % 12) + 1
            : amantaMonth;

        ZonedDateTime localMoment = Instant.ofEpochMilli(toEpochMillis(julianDayUt)).atZone(zone);
        LocalDate vaarDate = toEpochMillis(julianDayUt) < solarDay.sunriseMillis
            ? localMoment.toLocalDate().minusDays(1)
            : localMoment.toLocalDate();
        int javaDay = vaarDate.getDayOfWeek().getValue();
        result.vaarIndex = javaDay == 7 ? 1 : javaDay + 1;
        result.nakshatraIndex = Math.min(27, (int) (result.moonLongitude / (360.0 / 27.0)) + 1);
        double degreesWithinNakshatra = result.moonLongitude % (360.0 / 27.0);
        result.nakshatraPada = Math.min(4, (int) (degreesWithinNakshatra / (360.0 / 108.0)) + 1);

        result.sunriseMillis = solarDay.sunriseMillis;
        result.sunsetMillis = solarDay.sunsetMillis;
        result.nextSunriseMillis = nextSolarDay.sunriseMillis;
        return result;
    }

    private double siderealSunLongitude(double julianDayUt) {
        Time time = SiderealAstronomy.fromJulianDay(julianDayUt);
        return SiderealAstronomy.normalize(
            SiderealAstronomy.tropicalSunLongitude(time) - SiderealAstronomy.lahiriAyanamsa(time)
        );
    }

    private double findAdjacentNewMoon(double julianDayUt, int direction) {
        double firstDay = julianDayUt;
        double firstPhase = phaseAt(firstDay);
        for (int day = 1; day <= 35; day++) {
            double secondDay = julianDayUt + direction * day;
            double secondPhase = phaseAt(secondDay);
            boolean crossedNewMoon = direction > 0
                ? secondPhase < firstPhase && firstPhase - secondPhase > 180.0
                : secondPhase > firstPhase && secondPhase - firstPhase > 180.0;
            if (crossedNewMoon) {
                double left = Math.min(firstDay, secondDay);
                double right = Math.max(firstDay, secondDay);
                for (int iteration = 0; iteration < 48; iteration++) {
                    double middle = (left + right) / 2.0;
                    if (phaseAt(middle) > 180.0) {
                        left = middle;
                    } else {
                        right = middle;
                    }
                }
                return (left + right) / 2.0;
            }
            firstDay = secondDay;
            firstPhase = secondPhase;
        }
        throw new IllegalStateException("Could not find adjacent new moon.");
    }

    private double phaseAt(double julianDayUt) {
        Time time = SiderealAstronomy.fromJulianDay(julianDayUt);
        return SiderealAstronomy.normalize(
            SiderealAstronomy.tropicalMoonLongitude(time) - SiderealAstronomy.tropicalSunLongitude(time)
        );
    }

    private static long toEpochMillis(double julianDayUt) {
        return Math.round((julianDayUt - 2440587.5) * 86400000.0);
    }

    private static void validateCoordinates(double latitude, double longitude) {
        if (!Double.isFinite(latitude) || !Double.isFinite(longitude)
            || latitude < -90.0 || latitude > 90.0
            || longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Latitude and longitude are outside valid ranges.");
        }
    }
}
