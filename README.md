# Daily Vedic Insights

Daily Vedic Insights (Jyotish Darpana) is an Android app that combines on-device Panchang calculations, optional local-calendar context, recurring Vedic event matching, and an optional Gemini-generated daily briefing.

## Features

- Calculates sidereal Sun and Moon positions, tithi, paksha, nakshatra and pada, Purnimanta month, and Adhika Masa with Astronomy Engine and a Lahiri ayanamsa approximation.
- Calculates local sunrise and sunset from the device's current location. Weekday (vaar) follows the local sunrise boundary.
- Builds sunrise-based Hora and Chaughadiya periods and calculates Lagna across the daily timeline.
- Matches user-created events using selected Vedic conditions, with ALL/ANY matching and a daily, 7-, 14-, or 30-day repeat cycle.
- Shows a 24-hour muhurta matrix, local calendar entries, and the next three simulated event occurrences.
- Reschedules its daily WorkManager briefing alarm after delivery and restores the configured alarm after reboot, clock, timezone, or app-package changes.
- Stores the Gemini API key encrypted with Android Keystore. The key is not bundled in the app or sent anywhere except Google's Gemini API request.
- Keeps location, calendar data, and astronomical calculations on-device. The optional AI request includes formatted Panchang details. Calendar entries and matched personal event names are excluded unless you explicitly enable personal-detail sharing in Settings.

The calculations are intended for informational use and should not be treated as authoritative religious, medical, legal, or financial advice. Sunrise and lagna depend on accurate device location and timezone settings.

## First run

1. Install and open the app; grant location permission so local sunrise-based calculations can run.
2. Grant calendar and notification permissions if you want those optional features.
3. Open **Settings** and save a Gemini API key to enable AI briefings. Calendar entries and matched event names are not sent unless you explicitly turn on personal-detail sharing. Without an API key, the app still delivers a locally generated Panchang summary.
4. Choose a daily awakening time. Allow exact alarms in Android settings for the most precise schedule; Android may otherwise deliver an inexact alarm.
5. Create a recurring event and select at least one Vedic condition. ALL requires every selected condition; ANY requires at least one.

## Build

The project uses Java 17, Android Gradle Plugin 8.2.2, Gradle 8.5, and Android SDK 34.

```sh
./gradlew testDebugUnitTest assembleDebug
```

GitHub Actions installs the required Android SDK packages, caches Gradle dependencies, runs unit tests, and assembles a debug APK. The devcontainer installs the same SDK packages after creation.

## Third-party licensing

Astronomy Engine is distributed under the MIT License; see [third-party notices](THIRD_PARTY_NOTICES.md). The app applies an approximate Lahiri ayanamsa to its ecliptic coordinates. Panchang results are informational and may differ slightly from systems using other ephemeris models or ayanamsa implementations.

Space Grotesk, Manrope, and JetBrains Mono are bundled under the SIL Open Font License; their license texts are included in the app assets at `app/src/main/assets/licenses/`. See [third-party notices](THIRD_PARTY_NOTICES.md).
