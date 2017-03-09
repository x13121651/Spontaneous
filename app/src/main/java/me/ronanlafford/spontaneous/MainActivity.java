package me.ronanlafford.spontaneous;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import static java.lang.Boolean.FALSE;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */


    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    protected TextView tvLatLong;
    protected Location mLastLocation;
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar) ;
        getSupportActionBar().setDisplayShowTitleEnabled(FALSE);

        mLatitudeText = (TextView) findViewById((R.id.mLatitudeText));
        mLongitudeText = (TextView) findViewById((R.id.mLongitudeText));
        tvLatLong = (TextView) findViewById(R.id.tvLatLong);


        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "eu-west-1:fd9b2eef-d3ff-428f-87cf-59685d7f1050", // Identity Pool ID
                Regions.EU_WEST_1 // Region
        );




        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_ACCESS_FINE_LOCATION);
        }

        buildGoogleApiClient();


        Button buttonAddLoc = (Button) findViewById(R.id.btnGoAdd);
        buttonAddLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, TabActivity.class);
                startActivity(i);
            }
        });



        Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent j = new Intent(MainActivity.this, MapsActivity.class);
                j.putExtra("Long", String.valueOf(mLastLocation.getLongitude()));
                j.putExtra("Lat", String.valueOf(mLastLocation.getLatitude()));
                startActivity(j);
            }
        });


        Button buttonCreate = (Button) findViewById(R.id.button4);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, TabActivity.class);
                startActivity(i);
            }

        });


        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }

        });

        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
            }

        });


    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(MainActivity.class.getSimpleName(), "Connected to Google Play Services!");

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            tvLatLong.setText("Latitude: "+String.valueOf(mLastLocation.getLatitude()) + "\t" + "Longitude: " + String.valueOf(mLastLocation.getLongitude()));

        } else {
            Toast.makeText(this, "no location detected", Toast.LENGTH_LONG).show();
        }}
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Toast.makeText(this, "Connection Failed....", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Toast.makeText(this, "Connection Suspended....", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }



}
