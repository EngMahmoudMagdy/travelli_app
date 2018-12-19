package com.magdy.travelli.UI;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.magdy.travelli.Adapters.PlaceAdapter;
import com.magdy.travelli.Data.Constants;
import com.magdy.travelli.Data.Place;
import com.magdy.travelli.Data.Tour;
import com.magdy.travelli.R;
import com.magdy.travelli.Services.StringRequestNew;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PlacesFragment extends Fragment {
    List<Place>places ;
    Tour tour ;
    RequestQueue queue ;
    PlaceAdapter adapter;
    public static PlacesFragment newInstance(Tour tour)
    {
        PlacesFragment fragment = new PlacesFragment();
        fragment.tour = tour ;
        return fragment ;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.place_tab_in_tour_details, container, false);
        queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.place_recycle_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        places = new ArrayList<>();
        adapter = new PlaceAdapter(places,getContext());
        recyclerView.setAdapter(adapter);
        if (savedInstanceState!=null)
            tour = (Tour) savedInstanceState.getSerializable(Constants.TOUR);
        downloadPlaces(tour.getId());
    }
    void downloadPlaces(String id)
    {
        Uri uri = Uri.parse(Constants.BASE+"/tour_places.php").buildUpon().appendQueryParameter("id",id).build();
        StringRequestNew stringRequest = new StringRequestNew(Request.Method.GET, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("data",response);
                        try {
                            JSONObject o = new JSONObject(response);
                            int suc = o.getInt("success");
                            if (suc==1) {
                                JSONArray arr = o.getJSONArray("places");
                                Place place;
                                places.clear();
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject obj = (JSONObject) arr.get(i);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd hh:mm a z", Locale.getDefault());
                                    place = new Place();
                                    place.setId(obj.getString("id"));
                                    place.setImageLink(obj.getString("image"));
                                    place.setName(obj.getString("name"));
                                    place.setDescription(obj.getString("description"));
                                    place.setLongt(obj.getDouble("longt"));
                                    place.setLat(obj.getDouble("lat"));
                                    places.add(place);
                                }
                            }
                            else Toast.makeText(getContext(), o.getString("message"), Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.TOUR,tour);
    }
}