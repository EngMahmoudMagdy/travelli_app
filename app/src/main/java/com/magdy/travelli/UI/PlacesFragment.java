package com.magdy.travelli.UI;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.magdy.travelli.Adapters.PlaceAdapter;
import com.magdy.travelli.Data.Place;
import com.magdy.travelli.Data.Tour;
import com.magdy.travelli.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlacesFragment extends Fragment {
    List<Place> places;
    Tour tour;
    PlaceAdapter adapter;

    public static PlacesFragment newInstance(Tour tour) {
        PlacesFragment fragment = new PlacesFragment();
        fragment.tour = tour;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.place_tab_in_tour_details, container, false);
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
        adapter = new PlaceAdapter(places, getContext());
        recyclerView.setAdapter(adapter);

        //downloadPlaces(tour.getId());
        String response = "{\"places\":[{\"id\":\"2\",\"name\":\"Egyptian Museum\",\"longt\":\"31.2337\",\"lat\":\"30.0475\",\"description\":\"Known as one of the richest museums of ancient history in the World, the Egyptian Museum in Cairo has over 120,000 interesting historical items on display. Those include the items found in the Tomb of Tutankhamen, a number of ancient mummies, and plenty of other amazing items. The museum is also famous with its extended collections of ancient coins and papyrus.\",\"image\":\"https:\\/\\/lh6.googleusercontent.com\\/proxy\\/a6DBkKoQbGdDmpshpYmfbv0lLhuikAL9DnyC8ib59lvXniGvRPEiamVd20AI2I3zmHMcwuD-iFaRymK4tVFWwuETZkk2UCRfJ95wjK495aLLz8DwZo6sc-Fpr4oR-XghE2JsfTsP4o3foIzX0-let04-KdIhNM8=w221-h160-k-no\"},{\"id\":\"3\",\"name\":\"The hanged church\",\"longt\":\"31.2302\",\"lat\":\"30.0053\",\"description\":\"The Hanging Church\\r\\nKom Ghorab\\r\\nMisr Al Qadimah\\r\\nCairo Governorate\\r\\nEgypt\",\"image\":\"https:\\/\\/lh4.googleusercontent.com\\/proxy\\/lRRGBfPTHbGygeXOM06R56jgs42Ba4OYd0PIQWgamJ8fJiJbc2d65XMGAljVocyFCMRVicwx034UZcfhBLhLi8ola-MqIwk7rZuRFCWBHadSvALJ7Q-PswBoh2-xEgqfad_KbJLrVO6jWI-9htSGJPljPAlPEoc=w240-h160-k-no\"}],\"success\":1}";
        try {
            JSONObject o = new JSONObject(response);
            int suc = o.getInt("success");
            if (suc == 1) {
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
            } else Toast.makeText(getContext(), o.getString("message"), Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }


}