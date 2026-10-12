package com.vedicinsights.agent.data.astronomical;

import java.util.ArrayList;
import java.util.List;

public class OverlapEngine {
    private static final long SEGMENT_MILLIS = 15 * 60 * 1000L;
    private static final String[] PLANETARY_HOUR_ORDER = {
        "Shani", "Guru", "Mangal", "Surya", "Shukra", "Budha", "Chandra"
    };
    private static final String[] CHOUGHADIYA_ORDER = {
        "Udveg", "Char", "Labh", "Amrit", "Kaal", "Shubh", "Rog"
    };
    private static final int[] DAY_CHAUGHADIYA_START = {0, 0, 3, 6, 2, 5, 1, 4};
    private static final int[] NIGHT_CHAUGHADIYA_START = {0, 5, 3, 1, 6, 4, 2, 0};
    private static final int[] WEEKDAY_HORA_START = {-1, 3, 6, 2, 5, 1, 4, 0};
    private static final String[] SIGNS = {
        "Mesha", "Vrishabha", "Mithuna", "Karka", "Simha", "Kanya",
        "Tula", "Vrishchika", "Dhanu", "Makara", "Kumbha", "Meena"
    };
    public static class OverlapSlot {
        public long startEpochMillis;
        public long endEpochMillis;
        public String hora;
        public String chaughadiya;
        public String lagna;
        public boolean isGoldenConjunction;
        public boolean isDiscordantCaution;
    }

    public List<OverlapSlot> generateTimeline(
        long sunriseMillis,
        long sunsetMillis,
        long nextSunriseMillis,
        int dayVaar,
        double latitude,
        double longitude
    ) {
        if (dayVaar < 1 || dayVaar > 7 || sunsetMillis <= sunriseMillis
            || nextSunriseMillis <= sunsetMillis) {
            throw new IllegalArgumentException("Invalid day boundaries or weekday.");
        }
        if (!Double.isFinite(latitude) || !Double.isFinite(longitude)
            || latitude < -90.0 || latitude > 90.0
            || longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Latitude and longitude are outside valid ranges.");
        }

        List<OverlapSlot> slots = new ArrayList<>();
        long dayDuration = sunsetMillis - sunriseMillis;
        long nightDuration = nextSunriseMillis - sunsetMillis;
        for (long time = sunriseMillis; time < nextSunriseMillis; time += SEGMENT_MILLIS) {
            OverlapSlot slot = new OverlapSlot();
            slot.startEpochMillis = time;
            slot.endEpochMillis = Math.min(time + SEGMENT_MILLIS, nextSunriseMillis);
            slot.hora = resolveHoraAt(time, sunriseMillis, sunsetMillis, dayDuration, nightDuration, dayVaar);
            slot.chaughadiya = resolveChaughadiyaAt(
                time, sunriseMillis, sunsetMillis, dayDuration, nightDuration, dayVaar
            );
            slot.lagna = resolveLagnaAt(time, latitude, longitude);

            boolean goodChaughadiya = slot.chaughadiya.equals("Amrit")
                || slot.chaughadiya.equals("Shubh") || slot.chaughadiya.equals("Labh");
            boolean goodHora = slot.hora.equals("Guru") || slot.hora.equals("Shukra");
            slot.isGoldenConjunction = goodChaughadiya && goodHora;
            boolean badChaughadiya = slot.chaughadiya.equals("Rog") || slot.chaughadiya.equals("Kaal");
            slot.isDiscordantCaution = badChaughadiya && slot.hora.equals("Shani");
            slots.add(slot);
        }
        return slots;
    }

    private String resolveHoraAt(
        long time, long sunrise, long sunset, long dayDuration, long nightDuration, int vaar
    ) {
        int hoursFromSunrise;
        if (time < sunset) {
            hoursFromSunrise = (int) ((time - sunrise) / (dayDuration / 12));
        } else {
            hoursFromSunrise = 12 + (int) ((time - sunset) / (nightDuration / 12));
        }
        int planetIndex = (WEEKDAY_HORA_START[vaar] + hoursFromSunrise) % PLANETARY_HOUR_ORDER.length;
        return PLANETARY_HOUR_ORDER[planetIndex];
    }

    private String resolveChaughadiyaAt(
        long time, long sunrise, long sunset, long dayDuration, long nightDuration, int vaar
    ) {
        boolean daytime = time < sunset;
        long segmentLength = daytime ? dayDuration / 8 : nightDuration / 8;
        long segmentStart = daytime ? sunrise : sunset;
        int segmentIndex = (int) Math.min(7, (time - segmentStart) / segmentLength);
        int firstChaughadiya = daytime
            ? DAY_CHAUGHADIYA_START[vaar]
            : NIGHT_CHAUGHADIYA_START[vaar];
        return CHOUGHADIYA_ORDER[(firstChaughadiya + segmentIndex) % CHOUGHADIYA_ORDER.length];
    }

    private String resolveLagnaAt(long timeMillis, double latitude, double longitude) {
        double ascendant = SiderealAstronomy.ascendantLongitude(
            SiderealAstronomy.fromEpochMillis(timeMillis), latitude, longitude
        );
        int signIndex = (int) (ascendant / 30.0);
        return SIGNS[Math.min(signIndex, SIGNS.length - 1)];
    }
}
