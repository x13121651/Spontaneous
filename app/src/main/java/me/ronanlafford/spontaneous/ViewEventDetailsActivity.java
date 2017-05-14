package me.ronanlafford.spontaneous;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class ViewEventDetailsActivity extends AppCompatActivity {
CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView expandedImage;
    TextView viewTitle;
    TextView viewDate;
    TextView viewTime;
    TextView viewAddress;
    TextView viewLatLng;
    TextView viewDescription;
    Button join;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#ffffff"));
        //collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setExpandedTitleColor(Color.parseColor("#ffffff"));



        expandedImage = (ImageView) findViewById(R.id.expandedImage);
        viewTitle = (TextView) findViewById(R.id.viewTitle);
        viewDate = (TextView) findViewById(R.id.viewDate);
        viewTime = (TextView) findViewById(R.id.viewTime);
        viewAddress = (TextView) findViewById(R.id.viewAddress);
        viewLatLng = (TextView) findViewById(R.id.viewLatLng);
        viewDescription = (TextView) findViewById(R.id.viewDescription);

        join = (Button) findViewById(R.id.btnJoin);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "You have joined this event!", Toast.LENGTH_SHORT).show();

            }
        });

        String title = getIntent().getExtras().getString("TitleKey");
        String image = getIntent().getExtras().getString("ImageKey");
        String date = getIntent().getExtras().getString("DateKey");
        String time = getIntent().getExtras().getString("TimeKey");
        String address = getIntent().getExtras().getString("AddressKey");
        String latlng = getIntent().getExtras().getString("LatLngKey");
        String description = getIntent().getExtras().getString("DescriptionKey");




        viewTitle.setText(title);
        viewDate.setText(date);
        viewTime.setText(time);
        viewAddress.setText(address);
        viewDescription.setText(description);
        viewLatLng.setText(latlng);

        Picasso.with(getApplicationContext())
                .load(image)
                .placeholder(R.drawable.addphoto)
                .resize(400, 400)
                .into(expandedImage);


    }





}
