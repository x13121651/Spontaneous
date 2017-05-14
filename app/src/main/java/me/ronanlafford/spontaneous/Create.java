package me.ronanlafford.spontaneous;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static me.ronanlafford.spontaneous.MyAdapter.address;


public class Create extends Fragment {

    // url to php file
    // private static final String CREATE_URL = "http://52.209.112.163/createEvent.php";
    private static final String CREATE_URL = "http://52.209.112.163/getFull.php";

    //initialise Keys for table
    public static final String KEY_TITLE = "title";
    public static final String KEY_TIME = "time";
    public static final String KEY_DATE = "date";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_NAME = "name";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";


    private EditText etTitle;
    private EditText etTime;
    private EditText etDate;
    private EditText etAddress;
    private EditText etDescription;
    private TextView textView3;
    private ImageView imageView;
    private String streetAddress;
    private String latitude;
    private String longitude;
    private Button btnCamera;

    private int mYear, mMonth, mDay, mHour, mMinute;

    private Bitmap bitmap;
    Bitmap imageBitmap;

    private final int PICK_IMAGE_REQUEST = 1;

    // private String UPLOAD_URL = "http://52.209.112.163/uploadImage.php";
    private final String UPLOAD_URL = "http://52.209.112.163/getFull.php";
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create, container, false);


        //initialise variables
        etTitle = (EditText) rootView.findViewById(R.id.etTitle);
        etTime = (EditText) rootView.findViewById(R.id.etTime);
        etDate = (EditText) rootView.findViewById(R.id.etDate);
        etAddress = (EditText) rootView.findViewById(R.id.etAddress);
        etDescription = (EditText) rootView.findViewById(R.id.etDescription);
        imageView = (ImageView) rootView.findViewById(R.id.imageViewCreate);
        textView3 = (TextView) rootView.findViewById(R.id.textView3);
        btnCamera = (Button)rootView.findViewById(R.id.btnCamera);

//        //button to upload image
//        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.uploadImage);
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // uploadImage();
//                getLocationFromAddress(streetAddress);
//            }
//        });


        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });


        //button to choose image
        ImageButton chooseImage = (ImageButton) rootView.findViewById(R.id.addPhoto);
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        //datepicker button
        Button btnDP = (Button) rootView.findViewById(R.id.btnDP);
        //timepicker button
        Button btnTP = (Button) rootView.findViewById(R.id.btnTP);

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
                getLocationFromAddress(address);
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

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getContext(), "Uploading...", "Please wait...", false, false);

        // create string request (POST)
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CREATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        // set action button color
                        Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();

                        etTitle.setText("");
                        etTime.setText("");
                        etDate.setText("");
                        etAddress.setText("");
                        etDescription.setText("");
                        imageView.setImageResource(R.drawable.addphoto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            //passing the parameters to the php file using a Map for Key Value pairs
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Getting Image Name as a date and time
                String name = DateFormat.getDateTimeInstance().format(new Date());

                //Hashmap for event values
                Map<String, String> params = new HashMap<>();
                params.put(KEY_TITLE, title);
                params.put(KEY_TIME, time);
                params.put(KEY_DATE, date);
                params.put(KEY_ADDRESS, address);
                params.put(KEY_DESCRIPTION, description);
                params.put(KEY_IMAGE, image);
                params.put(KEY_LATITUDE, latitude);
                params.put(KEY_LONGITUDE, longitude);

                return params;
            }

        };

      //  stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest, null);
    }


    // geocoder to get latitude and longitude for pin location
    public void getLocationFromAddress(String streetAddress) {
        streetAddress = etAddress.getText().toString();
        Geocoder coder = new Geocoder(getContext());
        try {
            ArrayList<Address> adressesCreated = (ArrayList<Address>) coder.getFromLocationName(streetAddress, 1);
            for (Address add : adressesCreated) {
                //if the arraylist is not empty get the LatLng
                Double lng = add.getLongitude();
                Double lat = add.getLatitude();
                LatLng geo = new LatLng(lat, lng);
                //display co-ordinates
                latitude = String.valueOf(lat);
                longitude = String.valueOf(lng);
                textView3.setText(latitude + " : " + longitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//////////////////////////////  upload image    ////////////////////////

    public String getStringImage(Bitmap bmp) {
        //converting image to base64 string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

//       String safeString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//        String encodedImage = safeString.replace('+','-').replace('/','_');
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
                        Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the error
                        Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                    //Converting Bitmap to String
                    String image = getStringImage(bitmap);


                //setting the Image Name as a date and time
                String name = DateFormat.getDateTimeInstance().format(new Date());

                //Creating parameters
                Map<String, String> params = new Hashtable<>();

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
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                if(data.getExtras()!=null) {
                    Bundle extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                    imageView.setImageBitmap(imageBitmap);
                }else{

                }
            }

        }

}