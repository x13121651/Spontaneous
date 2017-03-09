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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.R.attr.radius;
import static android.app.Activity.RESULT_OK;


public class Home extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>, LocationListener {
    MapView mapView;
    GoogleMap googleMap;
    protected GoogleApiClient mGoogleApiClient;
    protected android.location.Location mLastLocation;
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;

    double latitude;
    double longitude;
    LatLng latLng;
    Marker currLocationMarker;
    LocationRequest mLocationRequest;

    String address;
    String city;
    String state;
    String country;
    String postalCode;
    String knownName;
    List<Address> addresses = null;
    ArrayList<LatLng> latlngs = null;
    Geocoder geocoder;
    MarkerOptions markerOptions;
    MarkerOptions xmarkerOptions;

    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    private static final float GEOFENCE_RADIUS = 500.0f; // in meters

    int PLACE_PICKER_REQUEST = 1;
    TextView pickaPlace;
    private static final String TAG = Home.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildGoogleApiClient();

        // setup geocoder to get address from lat long
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        //   PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.autocomplete_fragment);


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home, container, false);


//
//  if (ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(), new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
//                    PERMISSION_ACCESS_FINE_LOCATION);
//        }
//
        mapView = (MapView) rootView.findViewById(R.id.mapView2);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
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


        return rootView;
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                GetPlaceFromPicker(data);
            }
        }
    }

    private void GetPlaceFromPicker(Intent data)
    {
        Place placePicked = PlacePicker.getPlace(getActivity(),data);

        String placeNameTextView = placePicked.getName().toString();
        String placeAddressTextView = placePicked.getAddress().toString();
        String placePhoneNumberTextView = placePicked.getPhoneNumber().toString();

        String toastMsg = "\n\n" + placeAddressTextView
                + "\n\n" + placePhoneNumberTextView;

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


    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
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


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        this.googleMap = googleMap;

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            this.googleMap.setMyLocationEnabled(true); // false to disable
            this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
            this.googleMap.getUiSettings().setMapToolbarEnabled (true);

        } else {
            this.googleMap.setMyLocationEnabled(false); // false to disable
            this.googleMap.getUiSettings().setZoomGesturesEnabled(false);
            this.googleMap.getUiSettings().setMapToolbarEnabled (false);
        }


//        LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//
//        this.googleMap.addMarker(
//                new MarkerOptions().position(currentLocation).title("Party Night").snippet(address + "\n" + city + "\n" + state + "\n" + postalCode + "\n" + country).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//
//        );

        this.googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick(" + latLng + ")");
                markerForGeofence(latLng);
            }

        });
        // Set a listener for info window events.
        this.googleMap.setOnInfoWindowClickListener(this);

        this.googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context context = getContext(); //or getActivity(), YourActivity.this, etc.

                // Getting view from the layout file info_window_layout
                View v = View.inflate(context,R.layout.windowlayout,null);



//                LinearLayout info = new LinearLayout(context);
//                info.setOrientation(LinearLayout.VERTICAL);
//
 //            TextView title = new TextView(context);
                TextView title = (TextView) v.findViewById(R.id.tvTitle);

                title.setTextColor(Color.WHITE);
               title.setGravity(Gravity.CENTER);
               title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

  //     TextView snippet = new TextView(context);
                TextView snippet = (TextView) v.findViewById(R.id.tvAddress);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());


//                info.addView(title);
//                info.addView(snippet);

//                return info;
                     return v;
            }
        });


//        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,16));
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(currentLocation)      // Sets the center of the map to Mountain View
//                .zoom(17)                   // Sets the zoom
//                .bearing(0)                // Sets the orientation of the camera to east
//                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
//                .build();                   // Creates a CameraPosition from the builder
//        this.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Log.i(MainActivity.class.getSimpleName(), "Connected to Google Play Services!");

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();

                try {

                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    city = addresses.get(0).getLocality();
                    state = addresses.get(0).getAdminArea();
                    country = addresses.get(0).getCountryName();
                    postalCode = addresses.get(0).getPostalCode();
                    knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getContext(), "location detected", Toast.LENGTH_LONG).show();
                //place marker at current position
                //mGoogleMap.clear();
                latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                // markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.launcher));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                markerOptions.snippet(address + "\n" + city + "\n" + state + "\n" + postalCode + "\n" + country);
                currLocationMarker = googleMap.addMarker(markerOptions);

                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentLocation)      // Sets the center of the map to Mountain View
                        .zoom(16)                   // Sets the zoom
                        .bearing(0)                // Sets the orientation of the camera to east
                        .tilt(60)                   // Sets the tilt of the camera to 60 degrees
                        .build();                   // Creates a CameraPosition from the builder
                this.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                latlngs = new ArrayList<>();
                latlngs.add(new LatLng(53.3418983,-6.4294135));
                latlngs.add(new LatLng(53.3471163,-6.4228665));
                latlngs.add(new LatLng(53.3471163,-6.4228665));
                latlngs.add(new LatLng(53.3379756,-6.4301617));
                latlngs.add(new LatLng(53.3392099,-6.430388));

                latlngs.add(new LatLng(53.3486573,-6.2436192));
                latlngs.add(new LatLng(53.3486573,-6.2436192));
                latlngs.add(new LatLng(53.3486573,-6.2436192));
                latlngs.add(new LatLng(53.3491757,-6.2446767));
                latlngs.add(new LatLng(53.3492192,-6.2453573));


                for (LatLng latLng : latlngs) {
                    markerOptions.position(latLng);
                    try {
                        createGeofence(latLng, radius);
                        addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    city = addresses.get(0).getLocality();
                    state = addresses.get(0).getAdminArea();
                    country = addresses.get(0).getCountryName();
                    postalCode = addresses.get(0).getPostalCode();
                    knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                    markerOptions.title("Some Event");
                    markerOptions.snippet(address + "\n" + city + "\n" + state + "\n" + postalCode + "\n" + country);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    googleMap.addMarker(markerOptions);

                }

            } else {
                Toast.makeText(getContext(), "no location detected", Toast.LENGTH_LONG).show();
            }

        }

       // update location at 3/5 second intervals
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(180000); //180 seconds
        mLocationRequest.setFastestInterval(3000); //30 seconds

        //save some battery
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter
       LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
    }

/////////geofencing//////////////////////////////////////////////////////////////////////////////////////////////



//////// Create a Geofence
    private Geofence createGeofence(LatLng latLng, float radius ) {
        Log.d(TAG, "createGeofence");
        return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion( latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration( GEO_DURATION )
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
    }

////////// Create a Geofence Request
    private GeofencingRequest createGeofenceRequest(Geofence geofence ) {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofence( geofence )
                .build();
    }

/////////// create pending intent
    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;
    Intent intent;
    private PendingIntent createGeofencePendingIntent() {
        Log.d(TAG, "createGeofencePendingIntent");
        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;

        intent = new Intent(getContext(), GeofenceTrasitionService.class);
        return PendingIntent.getService(
                getContext(), GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        if (checkPermission())
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(this);
    }


    @Override
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);
        if ( status.isSuccess() ) {
            drawGeofence();
            Toast.makeText(getContext(), status.toString(), Toast.LENGTH_SHORT).show();
        } else {
            // inform about fail
            Toast.makeText(getContext(), status.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    // Draw Geofence circle on GoogleMap
    private Circle geoFenceLimits;
    private void drawGeofence() {
        Log.d(TAG, "drawGeofence()");

        if ( geoFenceLimits != null )
            geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()
                .center(markerOptions.getPosition())
                .strokeColor(Color.argb(50, 170,170,70))
                .fillColor( Color.argb(100, 300,300,150) )
                .radius( GEOFENCE_RADIUS );
        geoFenceLimits = googleMap.addCircle(circleOptions);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch ( item.getItemId() ) {
//            case R.id.geofence: {
//                startGeofence();
//                return true;
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private Marker geoFenceMarker;
    // Create a marker for the geofence creation
    private void markerForGeofence(LatLng latLng) {
        Log.i(TAG, "markerForGeofence("+latLng+")");
        String title = latLng.latitude + ", " + latLng.longitude;
        // Define marker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title(title);
        if ( googleMap!=null ) {
            // Remove last geoFenceMarker
            if (geoFenceMarker != null)
                geoFenceMarker.remove();
            geoFenceMarker = googleMap.addMarker(markerOptions);
        }
    }


    // Start Geofence creation process
    private void startGeofence() {
        Log.i(TAG, "startGeofence()");
        if( xmarkerOptions != null ) {
            Geofence geofence = createGeofence( markerOptions.getPosition(), GEOFENCE_RADIUS );
            GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
            addGeofence( geofenceRequest );
        } else {
            Log.e(TAG, "Geofence marker is null");
        }
    }


    GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);


    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Toast.makeText(getContext(), "Connection Failed....", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Toast.makeText(getContext(), "Connection Suspended....", Toast.LENGTH_LONG).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(getContext(), "Need your location!", Toast.LENGTH_LONG).show();
                }

                break;
        }
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
       //googleMap.clear();
        if (currLocationMarker != null) {
            // remove the old marker and add then new one
            currLocationMarker.remove();
        }
        //latLng = new LatLng(location.getLatitude(), location.getLongitude());
        latitude = mLastLocation.getLatitude();
        longitude = mLastLocation.getLongitude();
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        city = addresses.get(0).getLocality();
        state = addresses.get(0).getAdminArea();
        country = addresses.get(0).getCountryName();
        postalCode = addresses.get(0).getPostalCode();
        knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("You are here!");
        markerOptions.snippet(address + "\n" + city + "\n" + state + "\n" + postalCode + "\n" + country);

       if (currLocationMarker != null) {
           // remove the old marker and add then new one
           currLocationMarker.remove();
       }
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        currLocationMarker = googleMap.addMarker(markerOptions);


        //zoom to current position:
        LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLocation)      // Sets the center of the map to Mountain View
                .zoom(16)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(60)                   // Sets the tilt of the camera to 60 degrees
                .build();                   // Creates a CameraPosition from the builder
        this.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //If you only need one location, unregister the listener
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }


}