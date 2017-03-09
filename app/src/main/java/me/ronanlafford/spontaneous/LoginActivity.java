package me.ronanlafford.spontaneous;

/**
 * Created by 15the on 30/01/2017.
 */


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

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


    //declare values
    private EditText etUsername;
    private EditText etPassword;

    private Button buttonLogin;
    private Button buttonToRegister;
    private ProgressBar pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialise variables
        etUsername = (EditText) findViewById(editTextUsername);
        etPassword = (EditText) findViewById(editTextPassword);

        buttonToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);

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

    //registerUser method when the button is clicked
    private void registerUser() {

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

                        if (response.equals("Success")) {
                            Snackbar.make(findViewById(R.id.loginRoot), "Login Successful", Snackbar.LENGTH_LONG)
                                    .setDuration(5000).show();
                            pb.setVisibility(ProgressBar.INVISIBLE);
                            Intent i = new Intent(LoginActivity.this, TabActivity.class);
                            startActivity(i);


                        } else {
                            Snackbar.make(findViewById(R.id.loginRoot), "Login Failed", Snackbar.LENGTH_LONG)
                                    .setDuration(5000).show();
                            pb.setVisibility(ProgressBar.INVISIBLE);
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

        // Create request queue and add the stringRequest to it
        // RequestQueue requestQueue = Volley.newRequestQueue(this);
        // requestQueue.add(stringRequest);

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, null);
    }


}
