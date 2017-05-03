package me.ronanlafford.spontaneous;


import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class Home extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnPoiClickListener, ResultCallback<Status> {

    // mapview
    private MapView mapView;

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;

    // for location
    protected android.location.Location mLastLocation;
    protected android.location.Location updatedLocation;

    //for latitude and longitude co-ordinates
    private LatLng latLng;
    private double latitude;
    private double longitude;
    private double newLatitude;
    private double newLongitude;

    //for geocoding
    private Geocoder geocoder;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String knownName;

    //arrays for addresses, co-ordinates and geofences
    private List<Address> addresses = null;
    private ArrayList<LatLng> latlngs = null;
    protected ArrayList<Geofence> mGeofenceList;


    private final String LOG_TAG = "Ronan's Test Maps";
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private MarkerOptions markerOptions;
    private Marker currentLocationMarker;

    // placepicker
    int PLACE_PICKER_REQUEST = 1;
    TextView pickaPlace;

    //to center user location
    ImageButton myLocation;

    private static final String TAG = Home.class.getSimpleName();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //   PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Empty list for storing geofences
        mGeofenceList = new ArrayList<>();

        // method to build api client
        buildGoogleApiClient();

        // setup geocoder to get address from lat long
        geocoder = new Geocoder(getContext(), Locale.getDefault());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home, container, false);

        //////Map
        mapView = (MapView) rootView.findViewById(R.id.mapView2);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        //////placepicker
        pickaPlace = (TextView) rootView.findViewById(R.id.pickView);

        pickaPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        ////// Re-center user Location
        myLocation = (ImageButton) rootView.findViewById(R.id.myLocationIcon);

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //zoom camera to current location
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentLocation)      // Sets the center of the map to current location
                        .zoom(16)                   // Sets the zoom
                        .bearing(0)                // Sets the orientation of the camera to east
                        .tilt(60)                   // Sets the tilt of the camera to 60 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return rootView;
    }


    //////Placepicker result
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                GetPlaceFromPicker(data);
            }
        }
    }

    /////Placepicker get name and number and display it in a dialog box
    private void GetPlaceFromPicker(Intent data) {
        Place placePicked = PlacePicker.getPlace(getActivity(), data);
        String placeNameTextView = placePicked.getName().toString();
        String placeAddressTextView = String.valueOf(placePicked.getAddress());
        String placePhoneNumberTextView = placePicked.getPhoneNumber().toString();
        String placePriceTextView = String.valueOf(placePicked.getPriceLevel());
        String placeRatingTextView = String.valueOf(placePicked.getRating());
        String placeURLTextView = placePicked.getWebsiteUri().toString();

        String toastMsg =
                  "\n" + "Address: " + placeAddressTextView
                + "\n\n" + "Phone: " + placePhoneNumberTextView
                + "\n\n" + "Price Level: " + placePriceTextView
                + "\n\n" + "Rating: " + placeRatingTextView
                + "\n\n" + "URL: " + placeURLTextView;

        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(placeNameTextView);
        alertDialog.setMessage(toastMsg);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    // Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(getActivity(), 0, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }


    //setup the map
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        // googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap = googleMap;
        checkPermission();
        mMap.setMyLocationEnabled(false); // false to disable
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnPoiClickListener(this);

        // Get the geofences used. Geofence data is hard coded in this sample.
        populateGeofenceList();

        // add click map to place marker
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick(" + latLng + ")");
                markerForGeofence(latLng);
            }

        });


        // Set a click listener for info window events.
        mMap.setOnInfoWindowClickListener(this);

        //Infowindow style and details
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Context context = getContext();

                // Getting view from the layout file info_window_layout
                View v = View.inflate(context, R.layout.windowlayout, null);

                //the info window title
                TextView title = (TextView) v.findViewById(R.id.tvTitle);
                title.setTextColor(Color.WHITE);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                // the info window text
                TextView snippet = (TextView) v.findViewById(R.id.tvAddress);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                return v;
            }
        });

    }


    // the app lifecycle methods
    @Override
    public void onStart() {
        if (!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        //check permission to access fine location
        checkPermission();


        // make the location request
        mLocationRequest = LocationRequest.create();
        //set the power
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
////////////// // mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //set the intervals
        mLocationRequest.setInterval(180000); //180 seconds
        mLocationRequest.setFastestInterval(30000); //30 seconds
       // mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter


        // start requesting location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


        //if connected then try add the geofences
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(getContext(), getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // pending intent to trigger when entering or exiting the geofence
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }

        // get last known location of user
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            try {
                // reverse geocode the lat lang of current location
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // return only 1 location result
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

            } catch (IOException e) {
                e.printStackTrace();
            }

         //   Toast.makeText(getContext(), " getting current location !", Toast.LENGTH_LONG).show();


            //place marker at current position
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            ///Marker options for displaying on map
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            markerOptions.snippet(address + "\n" + city + "\n" + state + "\n" + postalCode + "\n" + country);
            currentLocationMarker = mMap.addMarker(markerOptions);

            //zoom camera to current location
            LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentLocation)      // Sets the center of the map to current location
                    .zoom(16)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(60)                   // Sets the tilt of the camera to 60 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        }

    }

    ////////////////////////  ok above this /////////////////////////////


    /////Geofence status result
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
          //  Toast.makeText(getContext(), "Geofences Added", Toast.LENGTH_SHORT).show();
        } else {
            String errorMessage = GeofenceErrorMessages.getErrorString(getContext(), status.getStatusCode());
            Log.e(LOG_TAG, errorMessage);
        }
    }


    // Create a marker for the geofence creation
    private Marker geoFenceMarker;

    ////////////////////////////////////////////////////////////this is not really needed
    private void markerForGeofence(LatLng latLng) {
        Log.i(TAG, "Where map was touched:(" + latLng + ")");
        // Define marker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("Location Clicked:")
                .snippet(address);

        if (mMap != null) {
            // Remove last geoFenceMarker
            if (geoFenceMarker != null)
                geoFenceMarker.remove();
            geoFenceMarker = mMap.addMarker(markerOptions);
        }
    }

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(LOG_TAG, "checkPermission()");
        // Ask for permission for location access if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Toast.makeText(getContext(), "Connection Failed....", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // call connect() to attempt to re-establish the connection.
        Toast.makeText(getContext(), "Connection Suspended....", Toast.LENGTH_LONG).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent i = new Intent(getContext(), ViewEventDetailsActivity.class);
        startActivity(i);
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        //place marker at current position
        if (mLastLocation  != null) {
            currentLocationMarker.remove();
        }
        if (updatedLocation  != null) {
            currentLocationMarker.remove();
        }

       // updatedLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        checkPermission();
        newLatitude = mLastLocation.getLatitude();
        newLongitude = mLastLocation.getLongitude();

        try {
            // reverse geocode the lat lang of current location
            addresses = geocoder.getFromLocation(newLatitude, newLongitude, 1); // return only 1 location result
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

        } catch (IOException e) {
            e.printStackTrace();
        }

      //  Toast.makeText(getContext(), "location changed - position update in progress!", Toast.LENGTH_LONG).show();

        //place marker at current position
        latLng = new LatLng(newLatitude, newLongitude);

        ///Marker options for displaying on map
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("updated Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        markerOptions.snippet(address + "\n" + city + "\n" + state + "\n" + postalCode + "\n" + country);
        currentLocationMarker = mMap.addMarker(markerOptions);

    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

   // locations to rest geofences
    public static final HashMap<String, LatLng> LOCAL_EVENTS = new HashMap<String, LatLng>();

    static {  // Dublin Events
        LOCAL_EVENTS.put("A", new LatLng(53.3418983, -6.4294135));
        LOCAL_EVENTS.put("B", new LatLng(53.3418982, -6.4294135));
        LOCAL_EVENTS.put("C", new LatLng(53.3471164, -6.4228665));
        LOCAL_EVENTS.put("D", new LatLng(53.3471165, -6.4228665));
        LOCAL_EVENTS.put("E", new LatLng(53.3379756, -6.4301617));
        LOCAL_EVENTS.put("F", new LatLng(53.3392099, -6.430388));
        LOCAL_EVENTS.put("G", new LatLng(53.3486574, -6.2436192));
        LOCAL_EVENTS.put("H", new LatLng(53.3486575, -6.2436192));
        LOCAL_EVENTS.put("I", new LatLng(53.3486573, -6.2436192));
        LOCAL_EVENTS.put("J", new LatLng(53.3491757, -6.2446767));
        LOCAL_EVENTS.put("K", new LatLng(53.3479700, -6.4235800));
    }


    // Point of interest click
    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {
        Toast.makeText(getContext(),
                pointOfInterest.name
                + "\nPlace ID:" + pointOfInterest.placeId
                + "\nLatitude:" + pointOfInterest.latLng.latitude
                + "\nLongitude:" + pointOfInterest.latLng.longitude,
                Toast.LENGTH_LONG).show();
    }

    //to loop through the list of locations and add to geofence list
    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : LOCAL_EVENTS.entrySet()) {

            try {

                LatLng eventValue = new LatLng(entry.getValue().latitude, entry.getValue().longitude);

                addresses = geocoder.getFromLocation(entry.getValue().latitude, entry.getValue().longitude, 1);
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

               // Toast.makeText(getContext(), "Address found: " + address, Toast.LENGTH_SHORT).show();

                //places a marker for each location
                MarkerOptions eventMarker = new MarkerOptions().position(eventValue);

              //  Toast.makeText(getContext(), "eventmarker added", Toast.LENGTH_SHORT).show();
                eventMarker.title("test");
                eventMarker.snippet(address + "\n" + city + "\n" + state + "\n" + postalCode + "\n" + country);
                eventMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                mMap.addMarker(eventMarker);


//                // Instantiates a new CircleOptions object and defines the center and radius by the marker placed
                CircleOptions circleOptions = new CircleOptions()
                        .center(eventMarker.getPosition())
                        .radius(100) // In meters
                        .strokeWidth(5)
                        .strokeColor(Color.argb(75,255, 87, 34));


                // Get back the mutable Circle
                Circle circle = mMap.addCircle(circleOptions);

                mGeofenceList.add(new Geofence.Builder()
                        .setRequestId(entry.getKey())
                        .setCircularRegion(eventValue.latitude, eventValue.longitude, Constants.GEOFENCE_RADIUS_IN_METERS)
                        .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build()

                );

              //  Log.i(TAG, "Preparing to send notification...: " + mGeofenceList.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(getContext(), GeofenceTransitionsIntentService.class);
      //  Toast.makeText(getContext(),intent.toString(), Toast.LENGTH_LONG).show();
        return PendingIntent.getService(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }





}