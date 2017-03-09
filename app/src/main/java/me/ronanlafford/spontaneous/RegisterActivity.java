package me.ronanlafford.spontaneous;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static android.support.design.widget.Snackbar.make;

public class RegisterActivity extends AppCompatActivity {

    // url to php file
    private static final String REGISTER_URL = "http://52.209.112.163/register.php";

    //initialise Keys
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";

    //declare values
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private Button buttonRegister;
    private Button buttonToLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initialise variables
        editTextUsername = (EditText) findViewById(R.id.regUsername);
        editTextPassword = (EditText) findViewById(R.id.regPassword);
        editTextEmail = (EditText) findViewById(R.id.regEmail);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // add click to button
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        // add click to button
        buttonToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });


    }

    //registerUser method when the button is clicked
    private void registerUser() {

        //get string values of variables
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();

        // create string request (POST)
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // set action button color
                        Snackbar.make(findViewById(R.id.registerRoot), response.toString(), Snackbar.LENGTH_LONG)
                                .setDuration(3000).show();

                        editTextUsername.setText("");
                        editTextPassword.setText("");
                        editTextEmail.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        make(findViewById(R.id.registerRoot), error.toString(), Snackbar.LENGTH_LONG)
                                .setDuration(3000).show();
                    }
                }) {

            //passing the parameters to the php file using a Map for Key Value pairs
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_USERNAME, username);
                params.put(KEY_PASSWORD, password);
                params.put(KEY_EMAIL, email);
                return params;
            }

        };

        // Create request queue and add the stringRequest to it
        // RequestQueue requestQueue = Volley.newRequestQueue(this);
        // requestQueue.add(stringRequest);

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, null);
    }


}
