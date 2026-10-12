package com.vedicinsights.agent.data.astronomical;

import swisseph.SweConst;
import swisseph.SwissEph;

public class PanchangCalculator {
    private final SwissEph sw;

    public PanchangCalculator() {
        // Zero external asset files — pure Java Moshier analytical engine
        this.sw = new SwissEph();
        this.sw.swe_set_sid_mode(SweConst.SE_SIDM_LAHIRI, 0, 0);
    }

    public static class FullPanchang {
        public int tithiIndex; // 1 to 15
        public String paksha; // "Shukla" or "Krishna"
        public int nakshatraIndex; // 1 to 27
        public int nakshatraPada; // 1 to 4
        public int purnimantaMonth; // 1 to 12 (1 = Chaitra, ..., 12 = Phalguna)
        public double moonIllumination; // 0.0 to 1.0
        public int vaarIndex; // 1 = Ravivaar, ..., 7 = Shanivaar
        public double moonLongitude;
        public double sunLongitude;
    }

    public FullPanchang compute(double julianDayUt) {
        FullPanchang result = new FullPanchang();
        double[] sunPos = new double[6];
        double[] moonPos = new double[6];
        StringBuffer serr = new StringBuffer();

        // Moshier mode flag (SEFLG_MOSEPH)
        int flags = SweConst.SEFLG_MOSEPH | SweConst.SEFLG_SIDEREAL | SweConst.SEFLG_SPEED;
        sw.swe_calc_ut(julianDayUt, SweConst.SE_SUN, flags, sunPos, serr);
        sw.swe_calc_ut(julianDayUt, SweConst.SE_MOON, flags, moonPos, serr);

        result.sunLongitude = sunPos[0];
        result.moonLongitude = moonPos[0];

        // 1. Nakshatra & Pada
        double nakshatraSpan = 360.0 / 27.0;
        result.nakshatraIndex = (int) (result.moonLongitude / nakshatraSpan) + 1;
        double degreesWithinNakshatra = result.moonLongitude % nakshatraSpan;
        result.nakshatraPada = (int) (degreesWithinNakshatra / (nakshatraSpan / 4.0)) + 1;

        // 2. Tithi & Paksha
        double elongation = (result.moonLongitude - result.sunLongitude + 360.0) % 360.0;
        int rawTithi = (int) (elongation / 12.0) + 1;
        if (rawTithi <= 15) {
            result.paksha = "Shukla";
            result.tithiIndex = rawTithi;
        } else {
            result.paksha = "Krishna";
            result.tithiIndex = rawTithi - 15;
        }

        // 3. Moon Illumination Phase (0.00 to 1.00)
        result.moonIllumination = (1.0 - Math.cos(Math.toRadians(elongation))) / 2.0;

        // 4. Purnimanta Month Calculation
        int amantaBaseMonth = (int) (result.sunLongitude / 30.0) + 1;
        if (result.paksha.equals("Krishna")) {
            int nextMonth = amantaBaseMonth + 1;
            result.purnimantaMonth = (nextMonth > 12) ? 1 : nextMonth;
        } else {
            result.purnimantaMonth = amantaBaseMonth;
        }

        // 5. Vaar
        result.vaarIndex = ((int) Math.floor(julianDayUt + 1.5)) % 7 + 1;
        return result;
    }
}
