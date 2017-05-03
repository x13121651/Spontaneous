package me.ronanlafford.spontaneous;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static me.ronanlafford.spontaneous.R.id.progressBar1;


public class List extends Fragment {
    ArrayList<Event> eventList = new ArrayList<>();
    ProgressBar progressBar;

    String GET_JSON_LIST_DATA_URL = "http://52.209.112.163/getAll.php";
    String JSON_TITLE = "title";
    String JSON_TIME = "time";
    String JSON_DATE = "date";
    String JSON_ADDRESS = "address";
    String JSON_DESCRIPTION = "description";
    private SwipeRefreshLayout swipeContainer;
    MyAdapter adapter;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.list, container, false);
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        final ProgressBar progressBar;
        progressBar = (ProgressBar) view.findViewById(progressBar1);

        //arraylist used to populate the cards


        // start the progressbar just before the request
        progressBar.setVisibility(View.VISIBLE);

        //recyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        //layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        //recyclerView Adapter
        RecyclerView.Adapter adapter = new MyAdapter(eventList);
        recyclerView.setAdapter(adapter);

        //volley request to get json data
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_JSON_LIST_DATA_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("json response", response.toString());
                        progressBar.setVisibility(View.GONE);

                        for (int i = 0; i < response.length(); i++) {

                            Event eventObject = new Event();

                            JSONObject json = null;
                            try {
                                json = response.getJSONObject(i);

                                eventObject.setTitle(json.getString(JSON_TITLE));

                                eventObject.setTime(json.getString(JSON_TIME));

                                eventObject.setDate(json.getString(JSON_DATE));

                                eventObject.setAddress(json.getString(JSON_ADDRESS));

                                eventObject.setDescription(json.getString(JSON_DESCRIPTION));

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                            eventList.add(eventObject);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("json response", error.toString());
                        Toast.makeText(getContext(), "There was an error: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                });


        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest, null);


        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(child);
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }

            GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        });


        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                eventList.clear();

                //volley request to get json data
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_JSON_LIST_DATA_URL, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                //recyclerView Adapter
                                RecyclerView.Adapter adapter = new MyAdapter(eventList);
                                recyclerView.setAdapter(adapter);
                                Log.d("json response", response.toString());
                                progressBar.setVisibility(View.GONE);

                                for (int i = 0; i < response.length(); i++) {

                                    Event eventObject = new Event();

                                    JSONObject json = null;
                                    try {
                                        json = response.getJSONObject(i);

                                        eventObject.setTitle(json.getString(JSON_TITLE));

                                        eventObject.setTime(json.getString(JSON_TIME));

                                        eventObject.setDate(json.getString(JSON_DATE));

                                        eventObject.setAddress(json.getString(JSON_ADDRESS));

                                        eventObject.setDescription(json.getString(JSON_DESCRIPTION));

                                    } catch (JSONException e) {

                                        e.printStackTrace();
                                    }
                                    eventList.add(eventObject);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("json response", error.toString());
                                Toast.makeText(getContext(), "There was an error: " + error.toString(), Toast.LENGTH_LONG).show();
                            }
                        });


                VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest, null);


                swipeContainer.setRefreshing(false);
            }

        });



// Inflate the layout for this fragment
        return view;
    }


}