package me.ronanlafford.spontaneous;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    static String image;
    static String title;
    static String date;
    static String time;
    static String address;
    static String latlng;
    static String latitude;
    static String longitude;
    static String description;


    Context context;


    //declare arraylist
    public static ArrayList<Event> eventList;

    //constructor
    public MyAdapter(Context context, ArrayList<Event> eventList) {
        this.eventList = eventList;
        this.context = context;
    }

    //inflate the list item (listItem.xml) within the parent Viewholder
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);

        // Get the cardView reference from RecyclerView current item
        final CardView cardView = (CardView) view.findViewById(R.id.cardView);

        // Set a click listener for the current item of RecyclerView
//        // the on click will send the data from the card to the details activity screen
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        return new ViewHolder(view);
    }

    // Clean all elements of the recycler
    public void clear() {
        eventList.clear();
        notifyDataSetChanged();
    }


// --Commented out by Inspection START (13/05/2017 18:39):
//    // Add a list of items
//    public void addAll(ArrayList<Event> eventList) {
//        eventList.addAll(eventList);
//        notifyDataSetChanged();
//    }
// --Commented out by Inspection STOP (13/05/2017 18:39)


    //Bind the text from the list item position to the viewholder position
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //get the event for each cardview
        Event event1 = eventList.get(position);

        //get data from the event to display
        title = event1.getTitle();
        date = event1.getDate();
        address = event1.getAddress();
        image = event1.getImageUri();


        // set the data into the view
        holder.tvTitle.setText(title);
        holder.tvLocation.setText(address);
        holder.tvDate.setText(date);

        // load the image into the view
        Log.i("ImageUri: ", image);
        //picasso gets the image from the saved url
        Picasso.with(context)
                .load(image)
                .placeholder(R.drawable.addphoto)
                .resize(400, 400)
                .into(holder.cardImage);

    }

    // get size of list
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    //subclass of viewholder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //declare textview
        private TextView tvTitle;
        private TextView tvLocation;
        private TextView tvDate;
        private ImageView cardImage;


        public ViewHolder(View itemView) {
            super(itemView);
            //initialise the itemview with the textview in it
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // get position
                    int pos = getAdapterPosition();

                    // check if item still exists
                    if (pos != RecyclerView.NO_POSITION) {
                        Context context = v.getContext();

                        // get the event position
                        Event clickedEvent = eventList.get(pos);

                        // get data from the event
                        title = clickedEvent.getTitle();
                        date = clickedEvent.getDate();
                        time = clickedEvent.getTime();
                        address = clickedEvent.getAddress();
                        latitude = clickedEvent.getLatitude();
                        latlng = latitude + " : " + longitude;
                        longitude = clickedEvent.getLongitude();
                        description = clickedEvent.getDescription();
                        image = clickedEvent.getImageUri();

                        Log.i("ImageUri: ", image);
                        //picasso gets the image from the saved url
                        Picasso.with(context)
                                .load(image)
                                .placeholder(R.drawable.addphoto)
                                .resize(400, 400)
                                .centerCrop()
                                .into(cardImage);

                        // pass the data from the view into the details activity
                        Intent i = new Intent(context, ViewEventDetailsActivity.class);
                        i.putExtra("TitleKey", title);
                        i.putExtra("ImageKey", image);
                        i.putExtra("DateKey", date);
                        i.putExtra("TimeKey", time);
                        i.putExtra("AddressKey", address);
                        i.putExtra("LatLngKey", latlng);
                        i.putExtra("DescriptionKey", description);
                        context.startActivity(i);

                    }
                }
            });


            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            cardImage = (ImageView) itemView.findViewById(R.id.cardImage);


        }
    }
}


