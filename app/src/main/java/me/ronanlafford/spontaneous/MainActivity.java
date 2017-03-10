package me.ronanlafford.spontaneous;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;

import static java.lang.Boolean.FALSE;

public class MainActivity extends AppCompatActivity {

    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    protected TextView tvLatLong;
    protected Location mLastLocation;

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





}
