package me.ronanlafford.spontaneous;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static me.ronanlafford.spontaneous.R.id.imageView3;

public class RatingActivity extends AppCompatActivity {
    ImageButton btnRateImage;
    RatingBar ratingbar2;
    EditText editText;
    EditText editText2;
    Button addRating;
    ImageView rateImage;
    ProgressBar progressBar3;
    String eventid;
    Button buttonCamera;
    Bitmap imageBitmap;
    Bitmap bitmap;

    SharedPreferences settings;

    //initialise Keys for table
    public static final String KEY_REVIEW = "review";
    public static final String KEY_TITLE = "title";
    public static final String KEY_RATING = "rating";
    public static final String KEY_EVENTID = "eventid";
    public static final String KEY_USERID = "userid";

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private final int PICK_IMAGE_REQUEST = 1;

    private final String RATE_URL = "http://52.209.112.163/createRating.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

       btnRateImage = (ImageButton) findViewById(R.id.imageButton6);
        buttonCamera = (Button)findViewById(R.id.buttonCamera);
        ratingbar2 = (RatingBar) findViewById(R.id.ratingBar2);
        editText = (EditText)findViewById(R.id.editText);
        editText2 = (EditText)findViewById(R.id.editText2);
        rateImage = (ImageView) findViewById(imageView3);
        addRating = (Button) findViewById(R.id.button2);
        progressBar3 = (ProgressBar) findViewById(R.id.progressBar3);
        // hide the progressbar just before the request
        progressBar3.setVisibility(View.INVISIBLE);
        //picasso gets the image from the saved url
        Picasso.with(getApplicationContext())
                .load(R.drawable.addphoto)
                .placeholder(R.drawable.addphoto)
                .resize(200, 200)
                .into(rateImage);

        addRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start the progressbar just before the request
//                progressBar3.setVisibility(View.VISIBLE);
//                addRatings();
                Toast.makeText(RatingActivity.this, "Rating has been added", Toast.LENGTH_SHORT).show();
                editText.setText("");
                editText2.setText("");
                ratingbar2.setRating(0);
                Picasso.with(getApplicationContext()).load(R.drawable.addphoto).into(rateImage);
            }
        });


        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });


        //button to choose image
        btnRateImage = (ImageButton) findViewById(R.id.imageButton6);
        btnRateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

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
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                rateImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if(data.getExtras()!=null) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                rateImage.setImageBitmap(imageBitmap);
            }else{

            }
        }

    }


    //ADD EVENT method when the button is clicked
    private void addRatings() {

        //get string values of variables
        final String title = editText.getText().toString();
        final String review = editText2.getText().toString();
        final String rating = String.valueOf(ratingbar2.getNumStars());

        settings = getSharedPreferences("Preferences", 0);
        final String userId = settings.getString("response", "");

                                //eventid
        final String eventId = "3";

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getApplicationContext(), "Uploading...", "Please wait...", false, false);

        // create string request (POST)
        StringRequest stringRequest = new StringRequest(Request.Method.POST, RATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Disimissing the progress dialog
                        loading.dismiss();

                        progressBar3.setVisibility(View.GONE);
                        // set action button color
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();

                        editText.setText("");
                        editText2.setText("");
                        ratingbar2.setRating(0);
                        //picasso gets the image from the saved url
                        Picasso.with(getApplicationContext())
                                .load(R.drawable.addphoto)
                                .placeholder(R.drawable.addphoto)
                                .resize(400, 400)
                                .into(rateImage);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            //passing the parameters to the php file using a Map for Key Value pairs
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Hashmap for event values
                Map<String, String> params = new HashMap<>();
                params.put(KEY_TITLE, title);
                params.put(KEY_REVIEW, review);
                params.put(KEY_RATING, rating);
                params.put(KEY_EVENTID, eventId);
                params.put(KEY_USERID, userId);

                return params;
            }

        };

        //  stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, null);
    }




}
