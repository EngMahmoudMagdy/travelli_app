package com.magdy.travelli.UI;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.magdy.travelli.Data.Constants;
import com.magdy.travelli.Data.Place;
import com.magdy.travelli.Data.Tour;
import com.magdy.travelli.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Created by engma on 10/3/2017.
 */

public class ExploreFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;
    List<Place> placeList ;
    private RequestQueue queue;
    Map<Marker,String> markerMap ;

    static ExploreFragment newInstance()
    {
        ExploreFragment fragment = new ExploreFragment();
        fragment.setArguments(new Bundle());
        return fragment ;
    }
    void downloadData()
    {
        queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.BASE+"view_places.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("data",response);
                        try {
                            JSONObject o = new JSONObject(response);
                            JSONArray arr = o.getJSONArray("places");
                            placeList.clear();
                            for(int i=0; i<arr.length();i++){
                                JSONObject obj = (JSONObject)arr.get(i);
                                Place p = new Place() ;
                                p.setId(obj.getString("id"));
                                p.setName(obj.getString("name"));
                                p.setDescription(obj.getString("description"));
                                p.setLongt(Double.parseDouble(obj.getString("longt")));
                                p.setLat(Double.parseDouble(obj.getString("lat")));
                                p.setImageLink(obj.getString("image"));
                                placeList.add(p);
                                LatLng temp = new LatLng(p.getLat(),p.getLongt());
                                Marker m = mMap.addMarker(new MarkerOptions().position(temp).title(p.getName()));
                                m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon360));
                                markerMap.put(m,p.getId());
                            }
                            //Toast.makeText(getContext()         , "Places download completed!", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            //Toast.makeText(getContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        placeList = new ArrayList<>();
        markerMap = new HashMap<>();
        downloadData();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(30.0447706,31.2348594), 15));
        mMap.setOnMarkerClickListener(this);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_explore,container,false);
        SupportMapFragment mapFragment = (SupportMapFragment) Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String id = markerMap.get(marker);
        if (id!=null) {
            Toast.makeText(getContext(), id, Toast.LENGTH_SHORT).show();
            marker.showInfoWindow();
            return true;
        }
        return false;
    }
}
