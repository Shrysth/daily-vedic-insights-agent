package com.vedicinsights.agent.data.astronomical;

import io.github.cosinekitty.astronomy.Aberration;
import io.github.cosinekitty.astronomy.Astronomy;
import io.github.cosinekitty.astronomy.Body;
import io.github.cosinekitty.astronomy.Ecliptic;
import io.github.cosinekitty.astronomy.Spherical;
import io.github.cosinekitty.astronomy.Time;

final class SiderealAstronomy {
    private static final double MILLIS_PER_DAY = 86400000.0;
    private static final double LAHIRI_AT_J2000 = 23.857092;
    private static final double LAHIRI_CENTURY_RATE = 1.396971278;
    private static final double LAHIRI_CENTURY_SQUARED_RATE = 0.0003086;

    private SiderealAstronomy() {}

    static Time fromJulianDay(double julianDayUt) {
        long epochMillis = Math.round((julianDayUt - 2440587.5) * MILLIS_PER_DAY);
        return Time.fromMillisecondsSince1970(epochMillis);
    }

    static Time fromEpochMillis(long epochMillis) {
        return Time.fromMillisecondsSince1970(epochMillis);
    }

    static double lahiriAyanamsa(Time time) {
        double centuriesSinceJ2000 = time.getUt() / 36525.0;
        return LAHIRI_AT_J2000
            + LAHIRI_CENTURY_RATE * centuriesSinceJ2000
            + LAHIRI_CENTURY_SQUARED_RATE * centuriesSinceJ2000 * centuriesSinceJ2000;
    }

    static double tropicalSunLongitude(Time time) {
        Ecliptic sun = Astronomy.equatorialToEcliptic(
            Astronomy.geoVector(Body.Sun, time, Aberration.Corrected)
        );
        return normalize(sun.getElon());
    }

    static double tropicalMoonLongitude(Time time) {
        Spherical moon = Astronomy.eclipticGeoMoon(time);
        return normalize(moon.getLon());
    }

    static double ascendantLongitude(Time time, double latitude, double longitude) {
        double siderealHours = Astronomy.siderealTime(time);
        double epsilon = Math.toRadians(time.nutationEps());
        double phi = Math.toRadians(latitude);
        double theta = Math.toRadians(normalize(siderealHours * 15.0 + longitude));
        double y = -Math.cos(theta);
        double x = Math.sin(phi) * Math.sin(epsilon)
            + Math.cos(phi) * Math.cos(epsilon) * Math.sin(theta);
        double tropicalAscendant = Math.atan2(y, x);

        double rightAscension = Math.atan2(
            Math.cos(epsilon) * Math.sin(tropicalAscendant),
            Math.cos(tropicalAscendant)
        );
        double hourAngle = normalizeRadians(theta - rightAscension);
        if (hourAngle >= Math.PI) {
            hourAngle -= 2.0 * Math.PI;
        }
        if (hourAngle >= 0.0) {
            tropicalAscendant += Math.PI;
        }

        return normalize(Math.toDegrees(tropicalAscendant) - lahiriAyanamsa(time));
    }

    static double normalize(double degrees) {
        return ((degrees % 360.0) + 360.0) % 360.0;
    }

    private static double normalizeRadians(double radians) {
        double fullCircle = 2.0 * Math.PI;
        return ((radians % fullCircle) + fullCircle) % fullCircle;
    }
}
