package com.vedicinsights.agent.data.astronomical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.github.cosinekitty.astronomy.Time;
import java.time.Instant;
import org.junit.Test;

public class SiderealAstronomyTest {
    @Test
    public void lahiriAyanamsaUsesTheJ2000ReferenceEpoch() {
        Time j2000 = Time.fromMillisecondsSince1970(946728000000L);

        assertEquals(23.857092, SiderealAstronomy.lahiriAyanamsa(j2000), 1.0e-9);
    }

    @Test
    public void siderealLongitudesAreNormalized() {
        Time summerSolstice = Time.fromMillisecondsSince1970(
            Instant.parse("2024-06-21T12:00:00Z").toEpochMilli()
        );

        double ayanamsa = SiderealAstronomy.lahiriAyanamsa(summerSolstice);
        double sunLongitude = SiderealAstronomy.normalize(
            SiderealAstronomy.tropicalSunLongitude(summerSolstice) - ayanamsa
        );
        double moonLongitude = SiderealAstronomy.normalize(
            SiderealAstronomy.tropicalMoonLongitude(summerSolstice) - ayanamsa
        );

        assertTrue(sunLongitude >= 0.0 && sunLongitude < 360.0);
        assertTrue(moonLongitude >= 0.0 && moonLongitude < 360.0);
    }
}
