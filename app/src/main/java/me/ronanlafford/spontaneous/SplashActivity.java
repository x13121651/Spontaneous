package me.ronanlafford.spontaneous;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Ronan on 07/12/2016.
 */

//The splash screen that is the launcher to move to the main activity after the app has fully loaded.
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(SplashActivity.this, NavActivity.class);
        startActivity(intent);
        finish();
    }
}