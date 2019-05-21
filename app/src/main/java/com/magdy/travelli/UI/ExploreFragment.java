package com.magdy.travelli.UI;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magdy.travelli.Data.Place;
import com.magdy.travelli.R;
import com.magdy.travelli.helpers.StaticMembers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by engma on 10/3/2017.
 */

public class ExploreFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;
    List<Place> places;
    Map<Marker, String> markerMap;

    static ExploreFragment newInstance() {
        ExploreFragment fragment = new ExploreFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        places = new ArrayList<>();
        markerMap = new HashMap<>();
        FirebaseDatabase.getInstance().getReference(StaticMembers.PLACES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                markerMap.clear();
                double avgLat = 0, avgLong = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Place place = snapshot.getValue(Place.class);
                    if (place != null) {
                        LatLng temp = new LatLng(place.getLat(), place.getLongt());
                        avgLat += temp.latitude;
                        avgLong += temp.longitude;
                        Marker m = mMap.addMarker(new MarkerOptions().position(temp).title(place.getName()));
                        m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_pin));
                        markerMap.put(m, place.getId());
                    }
                }
                if (markerMap.size() > 0) {
                    avgLat /= markerMap.size();
                    avgLong /= markerMap.size();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(avgLat, avgLong), 15));
                    mMap.setOnMarkerClickListener(ExploreFragment.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_explore, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        String id = markerMap.get(marker);
        if (id != null) {
            Toast.makeText(getContext(), id, Toast.LENGTH_SHORT).show();
            marker.showInfoWindow();
            return true;
        }
        return false;
    }
}
