plugins { 
    id("com.android.application") 
} android { 
    namespace = "com.vedicinsights.agent" 
    compileSdk = 34 
    defaultConfig { 
        applicationId = "com.vedicinsights.agent" 
        minSdk = 34 
        targetSdk = 34 
        versionCode = 1 
        versionName = "1.0.0" 
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" 
        } 
        buildTypes { 
            release { 
                isMinifyEnabled = false 
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), 
                "proguard-rules.pro") 
                } 
            } 
            compileOptions { 
                sourceCompatibility = JavaVersion.VERSION\_17 
                targetCompatibility = JavaVersion.VERSION\_17 
            } 
            buildFeatures { viewBinding = true } } dependencies { 
                // AndroidX &amp; Material 3 
                implementation("androidx.appcompat:appcompat:1.6.1") 
                implementation("com.google.android.material:material:1.11.0") 
                implementation("androidx.constraintlayout:constraintlayout:2.1.4") 
                implementation("androidx.recyclerview:recyclerview:1.3.2") 
                // Android Architecture Components &amp; WorkManager 
                implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0") 
                implementation("androidx.lifecycle:lifecycle-livedata:2.7.0") 
                implementation("androidx.work:work-runtime:2.9.0") 
                // Room SQLite Database 
                val roomVersion = "2.6.1" 
                implementation("androidx.room:room-runtime:$roomVersion") 
                annotationProcessor("androidx.room:room-compiler:$roomVersion") 
                // Networking (Gemini API Call) &amp; JSON 
                implementation("com.squareup.retrofit2:retrofit:2.9.0") 
                implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") 
                // Location Service 
                implementation("com.google.android.gms:play-services-location:21.1.0") 
                // Offline Astronomy: Swiss Ephemeris Java Port from Maven Central 
                implementation("de.th-heilbronn.swisseph:swisseph:2.01.00-01") 
                }