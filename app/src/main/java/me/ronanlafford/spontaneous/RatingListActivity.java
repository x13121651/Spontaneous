package me.ronanlafford.spontaneous;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class RatingListActivity extends AppCompatActivity {
    //private ArrayAdapter<Event>adapter;
    //private ArrayList<Event> ratingList;
    private ListView listView;
    private ArrayList<String> stringArrayList;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_list);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar) ;

        listView = (ListView) findViewById(R.id.list_item);

        setData();

         adapter = new RatingListViewAdapter(this, R.layout.rating_item, stringArrayList);
        listView.setAdapter(adapter);

    }



    private void setData() {
        stringArrayList = new ArrayList<>();
        stringArrayList.add("Johns Leaving Do");
        stringArrayList.add("Franks Birthday");
        stringArrayList.add("Concert 3 Arena");
        stringArrayList.add("Movie Night");
        stringArrayList.add("Party Fundraiser");
        stringArrayList.add("Volunteering");
        stringArrayList.add("Hillwalking");
        stringArrayList.add("Dance Class");
        stringArrayList.add("FootballMatch");
        stringArrayList.add("Sarah's 30th");
        stringArrayList.add("Swimming");
        stringArrayList.add("Reading Club");
    }
}
