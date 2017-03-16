package me.ronanlafford.spontaneous;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class Create extends Fragment {

    // url to php file
    private static final String CREATE_URL = "http://52.209.112.163/createEvent.php";

    //initialise Keys
    public static final String KEY_TITLE = "title";
    public static final String KEY_TIME = "time";
    public static final String KEY_DATE = "date";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_DESCRIPTION = "description";

    CollapsingToolbarLayout collapsingToolbarLayout;

    EditText etTitle;
    EditText etTime;
    EditText etDate;
    EditText etAddress;
    EditText etDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create, container, false);


        //initialise variables
         etTitle = (EditText) rootView.findViewById(R.id.etTitle);
         etTime = (EditText) rootView.findViewById(R.id.etTime);
         etDate = (EditText) rootView.findViewById(R.id.etDate);
         etAddress = (EditText) rootView.findViewById(R.id.etAddress);
         etDescription = (EditText) rootView.findViewById(R.id.etDescription);

        Button addEventButton = (Button) rootView.findViewById(R.id.addEvent);


        // add click to button
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
            }
        });

        return rootView;
    }



    //ADD EVENT method when the button is clicked
    private void addEvent() {

        //get string values of variables
        final String title = etTitle.getText().toString();
        final String time = etTime.getText().toString();
        final String date = etDate.getText().toString();
        final String address = etAddress.getText().toString();
        final String description = etDescription.getText().toString();


        // create string request (POST)
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CREATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // set action button color
                        Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();

                        etTitle.setText("");
                        etTime.setText("");
                        etDate.setText("");
                        etAddress.setText("");
                        etDescription.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            //passing the parameters to the php file using a Map for Key Value pairs
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_TITLE, title);
                params.put(KEY_TIME, time);
                params.put(KEY_DATE, date);
                params.put(KEY_ADDRESS, address);
                params.put(KEY_DESCRIPTION, description);
                return params;
            }

        };

        // Create request queue and add the stringRequest to it
        // RequestQueue requestQueue = Volley.newRequestQueue(this);
        // requestQueue.add(stringRequest);

        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest, null);


    }
}