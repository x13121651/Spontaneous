package me.ronanlafford.spontaneous;


public final class Constants {

    private Constants() {
    }

    public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;

    public static final float GEOFENCE_RADIUS_IN_METERS = 100; // 100 meters

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
//    public static final HashMap<String, LatLng> BAY_AREA_LANDMARKS = new HashMap<String, LatLng>();
//
//    static {
//        // San Francisco International Airport.
//        BAY_AREA_LANDMARKS.put("A", new LatLng(53.3418983, -6.4294135));
//        BAY_AREA_LANDMARKS.put("B", new LatLng(53.3418983, -6.4294135));
//        BAY_AREA_LANDMARKS.put("C", new LatLng(53.3471163, -6.4228665));
//        BAY_AREA_LANDMARKS.put("D", new LatLng(53.3471163, -6.4228665));
//        BAY_AREA_LANDMARKS.put("E", new LatLng(53.3379756, -6.4301617));
//        BAY_AREA_LANDMARKS.put("F", new LatLng(53.3392099, -6.430388));
//
//    }

}
