package me.ronanlafford.spontaneous;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class Create extends Fragment {

    // url to php file
    private static final String CREATE_URL = "http://52.209.112.163/createEvent.php";

    //initialise Keys for table
    public static final String KEY_TITLE = "title";
    public static final String KEY_TIME = "time";
    public static final String KEY_DATE = "date";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_DESCRIPTION = "description";



    EditText etTitle;
    EditText etTime;
    EditText etDate;
    EditText etAddress;
    EditText etDescription;

    Button btnTP;
    Button btnDP;
    private int mYear, mMonth, mDay, mHour, mMinute;

    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private String UPLOAD_URL = "http://52.209.112.163/uploadImage.php";

    //php variables
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";

    FloatingActionButton floatingActionButton;
    Button chooseImage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create, container, false);


        //initialise variables
        etTitle = (EditText) rootView.findViewById(R.id.etTitle);
        etTime = (EditText) rootView.findViewById(R.id.etTime);
        etDate = (EditText) rootView.findViewById(R.id.etDate);
        etAddress = (EditText) rootView.findViewById(R.id.etAddress);
        etDescription = (EditText) rootView.findViewById(R.id.etDescription);


        //button to upload image
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.uploadImage);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        //button to choose image
        chooseImage = (Button) rootView.findViewById(R.id.chooseImage);
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileChooser();
            }
        });

        //datepicker button
        btnDP = (Button) rootView.findViewById(R.id.btnDP);
        //timepicker button
        btnTP = (Button) rootView.findViewById(R.id.btnTP);

        //datepicker
        btnDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                etDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        //timepicker
        btnTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                etTime.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

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
//////////////////////////////  upload image    ////////////////////////

    public String getStringImage(Bitmap bmp) {
        //converting image to base64 string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage() {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getContext(), "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(getContext(), s.toString(), Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Getting Image Name
                String name = "test picture";  //editTextName.getText().toString().trim();

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, name);

                //returning parameters
                return params;
            }
        };

      // stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest, null);
    }

    //choose the image from the gallery.
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //show the image in the activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                ImageView imageView = (ImageView) getView().findViewById(R.id.imageViewCreate);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}