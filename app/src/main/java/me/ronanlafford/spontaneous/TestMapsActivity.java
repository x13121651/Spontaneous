//package me.ronanlafford.spontaneous;
//
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.location.Location;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.app.TaskStackBuilder;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.ResultCallback;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.Geofence;
//import com.google.android.gms.location.GeofencingRequest;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.CameraPosition;
//import com.google.android.gms.maps.model.Circle;
//import com.google.android.gms.maps.model.CircleOptions;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.PointOfInterest;
//
//import java.util.ArrayList;
//import java.util.Map;
//
//import static me.ronanlafford.spontaneous.R.id.map;
//
//public class TestMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnPoiClickListener, ResultCallback<Status> {
//
//    private final String LOG_TAG = "Ronan's Test Maps";
//    protected ArrayList<Geofence> mGeofenceList;
//    private GoogleMap mMap;
//    private GoogleApiClient mGoogleApiClient;
//    private LocationRequest mLocationRequest;
//    private Marker currentLocationMarker;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test_maps);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(map);
//        mapFragment.getMapAsync(this);
//
//        // Empty list for storing geofences
//        mGeofenceList = new ArrayList<Geofence>();
//
//        // Get the geofences used. Geofence data is hard coded in this sample.
//        populateGeofenceList();
//
//        //API Client built in on create
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//    }
//
//        //When the map has fully loaded..
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        checkPermission();
//        mMap.setMyLocationEnabled(true); // false to disable
//        mMap.getUiSettings().setMapToolbarEnabled(true);
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.setOnPoiClickListener(this);
//
//        // add click map to place marker
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                Log.d(LOG_TAG, "onMapClick(" + latLng + ")");
//                Toast.makeText(TestMapsActivity.this, latLng.toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
//
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.connect();
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
////        java.util.List<PatternItem> pattern = Arrays.<PatternItem>asList(
////                new Dot());
//
//        // Add a marker for location and move the camera
//        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
//
//
//  ////////////////////////////////  Circle for geofence     ////////////////////////////////////////////////////////////
//
//        // Instantiates a new CircleOptions object and defines the center and radius
//        CircleOptions circleOptions = new CircleOptions()
//                .center(currentLocation)
//                .radius(100) // In meters
//                .strokeWidth(5)
//                .strokeColor(Color.BLUE);
//                //.strokePattern(pattern);
//
//        // Get back the mutable Circle
//        Circle circle = mMap.addCircle(circleOptions);
//
//
// /////////////////////////////////////////////////////////////////////////////////////////
//
//        if (currentLocationMarker != null) {
//            currentLocationMarker.remove();
//        }
//        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Me"));
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(currentLocation)      // Sets the center of the map to Current Location
//                .zoom(16)                   // Sets the zoom
//                .bearing(0)                // Sets the orientation of the camera to east
//                .tilt(60)                   // Sets the tilt of the camera to 60 degrees
//                .build();                   // Creates a CameraPosition from the builder
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//    }
//
//
//    /////Geofence status result
//    public void onResult(Status status) {
//        if (status.isSuccess()) {
//            Toast.makeText(this, "Geofences Addded", Toast.LENGTH_SHORT).show();
//        } else {
//            String errorMessage = GeofenceErrorMessages.getErrorString(this, status.getStatusCode());
//            Log.e(LOG_TAG, errorMessage);
//        }
//    }
//
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        //check permission to access fine location
//        checkPermission();
//        // make the location request
//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(30000);//update interval
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//
//        if (!mGoogleApiClient.isConnected()) {
//            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            LocationServices.GeofencingApi.addGeofences(
//                    mGoogleApiClient,
//                    // The GeofenceRequest object.
//                    getGeofencingRequest(),
//                    // A pending intent that that is reused when calling removeGeofences(). This
//                    // pending intent is used to generate an intent when a matched geofence
//                    // transition is observed.
//                    getGeofencePendingIntent()
//            ).setResultCallback(this); // Result processed in onResult().
//        } catch (SecurityException securityException) {
//            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
//        }
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        mGoogleApiClient.connect();
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Toast.makeText(this, "Connection has failed!", Toast.LENGTH_SHORT).show();
//    }
//
//
//    // Check for permission to access Fine Location
//    private boolean checkPermission() {
//        Log.d(LOG_TAG, "checkPermission()");
//        // Ask for permission if it wasn't granted yet
//        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED);
//    }
//
//    // Point of interest click
//    @Override
//    public void onPoiClick(PointOfInterest pointOfInterest) {
//        Toast.makeText(getApplicationContext(), "Clicked: " +
//                        pointOfInterest.name + "\nPlace ID:" + pointOfInterest.placeId +
//                        "\nLatitude:" + pointOfInterest.latLng.latitude +
//                        " Longitude:" + pointOfInterest.latLng.longitude +
//                        "Address:"    + pointOfInterest.toString(),
//                Toast.LENGTH_SHORT).show();
//    }
//
//    public void populateGeofenceList() {
//        for (Map.Entry<String, LatLng> entry : Constants.BAY_AREA_LANDMARKS.entrySet()) {
//            mGeofenceList.add(new Geofence.Builder()
//                    .setRequestId(entry.getKey())
//                    .setCircularRegion(entry.getValue().latitude, entry.getValue().longitude, Constants.GEOFENCE_RADIUS_IN_METERS)
//                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
//                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
//                    .build()
//            );
//        }
//    }
//
//    private GeofencingRequest getGeofencingRequest() {
//        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
//        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
//        builder.addGeofences(mGeofenceList);
//        return builder.build();
//    }
//
//    private PendingIntent getGeofencePendingIntent() {
//        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
//        return PendingIntent.getService(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
//    }
//
//
//
//    private void test_sendNotification(String notificationDetails) {
//        // Create an explicit content Intent that starts the main Activity.
//        Intent notificationIntent = new Intent(getApplicationContext(), TestMapsActivity.class);
//
//        // Construct a task stack.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//
//        // Add the main Activity to the task stack as the parent.
//        stackBuilder.addParentStack(TestMapsActivity.class);
//
//        // Push the content Intent onto the stack.
//        stackBuilder.addNextIntent(notificationIntent);
//
//        // Get a PendingIntent containing the entire back stack.
//        PendingIntent notificationPendingIntent =
//                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // Get a notification builder that's compatible with platform versions >= 4
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//
//        // Define the notification settings.
//        builder.setSmallIcon(R.drawable.ic_location_on_deep_orange_500_24dp)
//                // In a real app, you may want to use a library like Volley
//                // to decode the Bitmap.
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
//                        R.drawable.ic_map_white_24dp))
//                .setColor(Color.RED)
//                .setContentTitle(notificationDetails)
//                .setContentText(getString(R.string.geofence_transition_notification_text))
//                .setContentIntent(notificationPendingIntent);
//
//        // Dismiss notification once the user touches it.
//        builder.setAutoCancel(true);
//
//        // Get an instance of the Notification manager
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Issue the notification
//        mNotificationManager.notify(0, builder.build());
//    }
//
//}
//
