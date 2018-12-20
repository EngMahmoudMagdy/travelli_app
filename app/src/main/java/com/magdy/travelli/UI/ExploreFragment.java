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
                                m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon360_64));
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
        //downloadData();
        String response= "{\"places\":[{\"id\":\"2\",\"name\":\"Egyptian Museum\",\"longt\":\"31.2337\",\"lat\":\"30.0475\",\"description\":\"Known as one of the richest museums of ancient history in the World, the Egyptian Museum in Cairo has over 120,000 interesting historical items on display. Those include the items found in the Tomb of Tutankhamen, a number of ancient mummies, and plenty of other amazing items. The museum is also famous with its extended collections of ancient coins and papyrus.\",\"image\":\"https:\\/\\/lh6.googleusercontent.com\\/proxy\\/a6DBkKoQbGdDmpshpYmfbv0lLhuikAL9DnyC8ib59lvXniGvRPEiamVd20AI2I3zmHMcwuD-iFaRymK4tVFWwuETZkk2UCRfJ95wjK495aLLz8DwZo6sc-Fpr4oR-XghE2JsfTsP4o3foIzX0-let04-KdIhNM8=w221-h160-k-no\"},{\"id\":\"3\",\"name\":\"The hanged church\",\"longt\":\"31.2302\",\"lat\":\"30.0053\",\"description\":\"The Hanging Church\\r\\nKom Ghorab\\r\\nMisr Al Qadimah\\r\\nCairo Governorate\\r\\nEgypt\",\"image\":\"https:\\/\\/lh4.googleusercontent.com\\/proxy\\/lRRGBfPTHbGygeXOM06R56jgs42Ba4OYd0PIQWgamJ8fJiJbc2d65XMGAljVocyFCMRVicwx034UZcfhBLhLi8ola-MqIwk7rZuRFCWBHadSvALJ7Q-PswBoh2-xEgqfad_KbJLrVO6jWI-9htSGJPljPAlPEoc=w240-h160-k-no\"},{\"id\":\"4\",\"name\":\"Amr ibn al-As Mosque\",\"longt\":\"31.2331\",\"lat\":\"30.0101\",\"description\":\"Amr ibn al - Aas Mosque. Built in the city of Fustat, founded by Muslims in Egypt after opening. It was also called the Fath Mosque, the Old Mosque and the Taj Al-Jamaa. The Amr ibn al-Aas Mosque is located on the eastern side of the Nile at 31 31 59 East and at latitude 30 0 37 north. According to the Islamic network: The area of the mosque at the time of construction 50 cubits in 30 arms and has six doors\",\"image\":\"https:\\/\\/lh5.googleusercontent.com\\/proxy\\/jhU_ng159uTRujmGjtaXh3hwII7HMTikGK4NW2kX5B0xySxqqRmeyofSKzMH2IYCrZzWgVu5gToD7WjjPkIQHybIDiqaIPp6KE-SdJv5RLtwGAxmhLiK2DnaqgJMT92QEqXQJvbuXH9vhMVv9FPsNUJrE7EB_a8=w227-h145-k-no\"}],\"success\":1}";
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
                m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon360_64));
                markerMap.put(m,p.getId());
            }
            //Toast.makeText(getContext()         , "Places download completed!", Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            //Toast.makeText(getContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
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
