package me.ronanlafford.spontaneous;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static me.ronanlafford.spontaneous.R.id.editTextPassword;
import static me.ronanlafford.spontaneous.R.id.editTextUsername;

public class LoginActivity extends AppCompatActivity {

    // url to php file
    private static final String LOGIN_URL = "http://52.209.112.163/login.php";

    //initialise Keys
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    //declare views
    private EditText etUsername;
    private EditText etPassword;
    private ProgressBar pb;

    SharedPreferences sharedpreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //sharedpreferences
        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        //initialise variables
        etUsername = (EditText) findViewById(editTextUsername);
        etPassword = (EditText) findViewById(editTextPassword);

        Button buttonToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        Button buttonLogin = (Button) findViewById(R.id.buttonLogin);

        // add click to button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb = (ProgressBar) findViewById(R.id.progressBar2);
                pb.setVisibility(ProgressBar.VISIBLE);
                registerUser();
            }
        });

        // add click to button
        buttonToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

    }

    // Check for permission to access Location
    private boolean checkPermission() {
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    //registerUser method when the button is clicked
    private void registerUser() {
        checkPermission();
        //get string values of variables
        final String username = etUsername.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();


        // create string request (POST)
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        etUsername.setText("");
                        etPassword.setText("");
                            //toast to show userid from response
                        Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();

                        if (response.equals("Login Failed")) {

//                            Snackbar.make(findViewById(R.id.loginRoot), "Login Successful", Snackbar.LENGTH_LONG)
//                                    .setDuration(5000).show();
//                            pb.setVisibility(ProgressBar.INVISIBLE);
//
//                            Intent i = new Intent(LoginActivity.this, TabActivity.class);
//                            startActivity(i);

                            Snackbar.make(findViewById(R.id.loginRoot), "Login Failed", Snackbar.LENGTH_LONG)
                                    .setDuration(5000).show();
                            pb.setVisibility(ProgressBar.INVISIBLE);

                        } else {

//                            Snackbar.make(findViewById(R.id.loginRoot), "Login Failed", Snackbar.LENGTH_LONG)
//                                    .setDuration(5000).show();
//                            pb.setVisibility(ProgressBar.INVISIBLE);

                            Snackbar.make(findViewById(R.id.loginRoot), "Login Successful", Snackbar.LENGTH_LONG)
                                    .setDuration(5000).show();
                            pb.setVisibility(ProgressBar.INVISIBLE);

                            //shared perferences to store user id
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("username", username);
                            editor.putString("user_id", response);
                            editor.apply();

//                            Intent i = new Intent(LoginActivity.this, TabActivity.class);
//                            i.putExtra("username",username);
//                            startActivity(i);

                            Intent i = new Intent(LoginActivity.this, TabActivity.class);
                            startActivity(i);

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(findViewById(R.id.loginRoot), error.toString(), Snackbar.LENGTH_LONG)
                                .setDuration(5000).show();
                        pb.setVisibility(ProgressBar.INVISIBLE);
                    }

                }) {

            //passing the parameters to the php file using a Map for Key Value pairs
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_USERNAME, username);
                params.put(KEY_PASSWORD, password);
                return params;
            }

        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, null);
    }

    public void onBackPressed() {
        //  super.onBackPressed();
        moveTaskToBack(true);

    }
}
