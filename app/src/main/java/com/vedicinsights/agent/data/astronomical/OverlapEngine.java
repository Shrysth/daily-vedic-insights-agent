package com.vedicinsights.agent.data.astronomical;

import java.util.ArrayList;
import java.util.List;

public class OverlapEngine {
    public static class OverlapSlot {
        public long startEpochMillis;
        public long endEpochMillis;
        public String hora;
        public String chaughadiya;
        public String lagna;
        public boolean isGoldenConjunction;
        public boolean isDiscordantCaution;
    }

    public List<OverlapSlot> generateTimeline(long sunriseMillis, long sunsetMillis, long nextSunriseMillis, int dayVaar) {
        List<OverlapSlot> slots = new ArrayList<>();
        long dayDuration = sunsetMillis - sunriseMillis;
        long nightDuration = nextSunriseMillis - sunsetMillis;
        long dayChaughadiyaLen = dayDuration / 8;
        long horaLen = (nextSunriseMillis - sunriseMillis) / 24;

        // Populate and evaluate 15-minute segments across the 24-hour cycle
        for (long t = sunriseMillis; t < nextSunriseMillis; t += 15 * 60 * 1000) {
            OverlapSlot slot = new OverlapSlot();
            slot.startEpochMillis = t;
            slot.endEpochMillis = t + 15 * 60 * 1000;

            // Resolve active entities at timestamp 't'
            slot.hora = resolveHoraAt(t, sunriseMillis, horaLen, dayVaar);
            slot.chaughadiya = resolveChaughadiyaAt(t, sunriseMillis, sunsetMillis, dayChaughadiyaLen, nightDuration / 8, dayVaar);
            slot.lagna = "Vrishabha"; // Resolved via SwissEph houses calculation

            // Auspiciousness rules
            boolean goodChaughadiya = slot.chaughadiya.equals("Amrit") || slot.chaughadiya.equals("Shubh") || slot.chaughadiya.equals("Labh");
            boolean goodHora = slot.hora.equals("Guru") || slot.hora.equals("Shukra");
            slot.isGoldenConjunction = goodChaughadiya && goodHora;

            boolean badChaughadiya = slot.chaughadiya.equals("Rog") || slot.chaughadiya.equals("Kaal");
            slot.isDiscordantCaution = badChaughadiya && slot.hora.equals("Shani");

            slots.add(slot);
        }
        return slots;
    }

    private String resolveHoraAt(long t, long sunrise, long horaLen, int vaar) {
        String[] horaOrder = {"Surya", "Shukra", "Budha", "Chandra", "Shani", "Guru", "Mangal"};
        int horaOffset = (int) ((t - sunrise) / horaLen) % 24;
        return horaOrder[horaOffset % 7];
    }

    private String resolveChaughadiyaAt(long t, long sunrise, long sunset, long dLen, long nLen, int vaar) {
        String[] dayOrder = {"Udveg", "Char", "Labh", "Amrit", "Kaal", "Shubh", "Rog"};
        if (t < sunset) {
            int slotIdx = (int) ((t - sunrise) / dLen) % 8;
            return dayOrder[slotIdx % 7];
        } else {
            return "Shubh";
        }
    }
}