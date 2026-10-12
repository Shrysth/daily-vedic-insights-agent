package com.vedicinsights.agent.data.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import java.util.function.Consumer;

public final class LocationRepository {
    private static final String PREFERENCES = "local_location";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    private LocationRepository() { }

    public static class Coordinates {
        public final double latitude;
        public final double longitude;

        Coordinates(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    public static boolean hasLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestCurrentLocation(
        Activity activity,
        Consumer<Coordinates> onLocation,
        Consumer<Exception> onFailure
    ) {
        if (!hasLocationPermission(activity)) {
            onFailure.accept(new SecurityException("Location permission has not been granted."));
            return;
        }

        try {
            CancellationTokenSource cancellationToken = new CancellationTokenSource();
            LocationServices.getFusedLocationProviderClient(activity)
                .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cancellationToken.getToken())
                .addOnSuccessListener(location -> {
                    if (location == null) {
                        onFailure.accept(new IllegalStateException(
                            "A location fix is unavailable. Enable device location and try again."
                        ));
                        return;
                    }
                    Coordinates coordinates = new Coordinates(location.getLatitude(), location.getLongitude());
                    save(activity, coordinates);
                    onLocation.accept(coordinates);
                })
                .addOnFailureListener(exception -> onFailure.accept(exception));
        } catch (SecurityException exception) {
            onFailure.accept(exception);
        }
    }

    public static Coordinates getSaved(Context context) {
        android.content.SharedPreferences preferences =
            context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if (!preferences.contains(LATITUDE) || !preferences.contains(LONGITUDE)) {
            throw new IllegalStateException(
                "No device location is saved. Open the app and grant location permission."
            );
        }
        return new Coordinates(
            Double.longBitsToDouble(preferences.getLong(LATITUDE, 0L)),
            Double.longBitsToDouble(preferences.getLong(LONGITUDE, 0L))
        );
    }

    private static void save(Context context, Coordinates coordinates) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putLong(LATITUDE, Double.doubleToRawLongBits(coordinates.latitude))
            .putLong(LONGITUDE, Double.doubleToRawLongBits(coordinates.longitude))
            .apply();
    }
}
