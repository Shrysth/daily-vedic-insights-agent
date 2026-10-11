# daily-vedic-insights-agent

Daily Vedic Insights Agent (Jyotish Darpana)
Daily Vedic Insights Agent is a privacy-first, autonomous background automation agent built specifically for Android 14 (Motorola Moto G35 5G). It synthesizes 100% offline traditional Vedic astrology (Panchang) mathematical calculations, local Android Calendar events, and personal user goals to deliver actionable, AI-driven daily briefings using the Google AI Studio (Gemini) API.

Core Features
Offline Astronomical Engine: Uses a bundled Java port of the Swiss Ephemeris (swisseph) to calculate exact planetary degrees, Tithi, Nakshatra, Paksha, and Purnimanta Lunar Months natively on the device without network requests.

24-Hour Jyotish Timeline Matrix: Computes and visually renders overlapping auspicious and inauspicious time windows (Hora, Chaughadiya, Lagna, Rahu Kaal, Abhijit Muhurat).

Sankalpa Event Creator (Vedic Engine): A multi-parameter subset matching engine allowing users to schedule recurring events based on cosmic geometry (e.g., trigger an alert only when Shukla Chaturdashi coincides with Rohini Nakshatra and Guru Hora).

Privacy-Preserving AI Synthesis: Your precise GPS coordinates and raw astrological math never leave the device. Only a formatted string of calculated calendar and astrological data is sent to the LLM for contextual daily advice.

Exact Background Awakening: Utilizes Android 14 AlarmManager and WorkManager to silently wake up, compute math, fetch calendar events, and deliver a high-priority native notification before you wake up (e.g., 06:00 AM).

Tech Stack
Language: Java 17

Target SDK: Android 14 (API Level 34)

Architecture: MVVM + Clean Architecture (UI ➔ Domain ➔ Data)

Local Storage: Room Database (SQLite), EncryptedSharedPreferences

Networking: Retrofit2, OkHttp3, Google AI Studio (Gemini Flash)

Astrology Engine: Swiss Ephemeris (swisseph-2.01.00-01.jar)

Background Processing: AlarmManager (Exact Alarms), WorkManager, BroadcastReceiver

Cloud Development Setup (Zero-Local Install)
This project is configured to be developed and compiled 100% in the cloud using GitHub Codespaces and GitHub Actions, bypassing the need for a local Android Studio installation.

Launch Codespace: Open the repository in GitHub Codespaces. The custom .devcontainer automatically installs Java 17 and the Android SDK Command-line Tools.

Edit Code: Write Java and XML directly in the browser-based VS Code environment.

Compile & Release: Commit and push your code to the main branch.

Download APK: The GitHub Actions workflow .github/workflows/build-apk.yml will automatically assemble the debug APK. Download it from the "Actions" tab and install it directly on your Moto G35 5G.
