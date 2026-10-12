package com.vedicinsights.agent.data.astronomical;

import java.time.LocalDate;
import java.time.ZoneId;
import io.github.cosinekitty.astronomy.Astronomy;
import io.github.cosinekitty.astronomy.Body;
import io.github.cosinekitty.astronomy.Direction;
import io.github.cosinekitty.astronomy.Observer;
import io.github.cosinekitty.astronomy.Time;

public class AstronomicalTimeCalculator {
    public static class SolarDay {
        public final long sunriseMillis;
        public final long sunsetMillis;

        SolarDay(long sunriseMillis, long sunsetMillis) {
            this.sunriseMillis = sunriseMillis;
            this.sunsetMillis = sunsetMillis;
        }
    }

    public SolarDay getSolarDay(LocalDate date, ZoneId zone, double latitude, double longitude) {
        if (!Double.isFinite(latitude) || !Double.isFinite(longitude)
            || latitude < -90.0 || latitude > 90.0
            || longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Latitude and longitude are outside valid ranges.");
        }

        long localMidnightMillis = date.atStartOfDay(zone).toInstant().toEpochMilli();
        long nextMidnightMillis = date.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli();
        double localDayLength = (nextMidnightMillis - localMidnightMillis) / 86400000.0;
        Time localMidnight = SiderealAstronomy.fromEpochMillis(localMidnightMillis);
        Observer observer = new Observer(latitude, longitude, 0.0);
        Time sunrise = Astronomy.searchRiseSet(
            Body.Sun, observer, Direction.Rise, localMidnight, localDayLength
        );
        if (sunrise == null) {
            throw new IllegalStateException("Could not calculate sunrise for " + date + ".");
        }
        Time sunset = Astronomy.searchRiseSet(
            Body.Sun, observer, Direction.Set, localMidnight, localDayLength
        );
        if (sunset == null) {
            throw new IllegalStateException("Could not calculate sunset for " + date + ".");
        }
        long sunriseMillis = sunrise.toMillisecondsSince1970();
        long sunsetMillis = sunset.toMillisecondsSince1970();
        if (sunsetMillis <= sunriseMillis) {
            throw new IllegalStateException("Calculated sunset does not follow sunrise for " + date + ".");
        }
        return new SolarDay(sunriseMillis, sunsetMillis);
    }
}
