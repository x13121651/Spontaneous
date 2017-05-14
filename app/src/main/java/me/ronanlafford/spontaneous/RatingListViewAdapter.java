package me.ronanlafford.spontaneous;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;
import java.util.Random;
                                                        //<string>
public class RatingListViewAdapter extends ArrayAdapter<String> {

    private RatingListActivity activity;
    private List<String> friendList;
       // ArrayList<Event> ratingList;


    public RatingListViewAdapter(RatingListActivity context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.friendList = objects;

    }

    @Override
    public int getCount() {
        return friendList.size();
      //  return ratingList.size();
    }

    @Override
    public String getItem(int position) {

       return friendList.get(position);
      // return ratingList.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.rating_item, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }

        holder.friendName.setText(getItem(position));

        //get first letter of each String item
        String firstLetter = String.valueOf(getItem(position).charAt(0));

        Random r = new Random();
        int i1 = r.nextInt(5 - 1) + 1;
        holder.ratingBar.setRating(i1);

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getColor(getItem(position));
        //int color = generator.getRandomColor();

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(firstLetter, color); // radius in px

        holder.imageView.setImageDrawable(drawable);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), RatingActivity.class);
                activity.startActivity(i);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        private ImageView imageView;
        private TextView friendName;
        private RatingBar ratingBar;

        public ViewHolder(View v) {
            imageView = (ImageView) v.findViewById(R.id.image_view);
            friendName = (TextView) v.findViewById(R.id.text);
            ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
        }
    }
}