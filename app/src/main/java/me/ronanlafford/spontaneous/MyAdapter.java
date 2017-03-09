package me.ronanlafford.spontaneous;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    //declare arraylist
    public ArrayList<String> eventList;

    //constructor
    public MyAdapter (ArrayList<String> eventList){
        this.eventList= eventList;
    }

    //inflate the list item (listItem.xml) within the parent Viewholder
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);

        // Get the cardView reference from RecyclerView current item
        final CardView cardView = (CardView) view.findViewById(R.id.cardView);

        // Set a click listener for the current item of RecyclerView
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Context context = view.getContext();
                    Intent i = new Intent(context, ViewEventDetailsActivity.class);
                   context.startActivity(i);

            }
        });

        return new ViewHolder(view);
    }

    //Bind the text from the list item position to the viewholder position
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
       // holder.myTextView.setText(eventList.get(position));
    }

    // get size of list
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    //subclass of viewholder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //declare textview
        private TextView title;


        public ViewHolder(View itemView) {
            super(itemView);
            //initialise the itemview with the textview in it




        }
    }
}


