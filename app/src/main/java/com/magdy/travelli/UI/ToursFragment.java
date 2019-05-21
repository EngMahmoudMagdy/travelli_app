package com.magdy.travelli.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magdy.travelli.Adapters.TourRecyclerAdapter;
import com.magdy.travelli.Data.Constants;
import com.magdy.travelli.Data.Tour;
import com.magdy.travelli.R;
import com.magdy.travelli.TourInfoListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.magdy.travelli.helpers.StaticMembers.FAV_TOURS;
import static com.magdy.travelli.helpers.StaticMembers.TOURS;
import static com.magdy.travelli.helpers.StaticMembers.USERS;


/**
 * Created by engma on 10/4/2017.
 */

public class ToursFragment extends Fragment implements TourInfoListener {
    static ToursFragment newInstance() {
        ToursFragment f = new ToursFragment();
        f.setArguments(new Bundle());
        return f;
    }

    ArrayList<Tour> tours;
    TourRecyclerAdapter adapter;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler)
    RecyclerView tourRecycler;
    @BindView(R.id.shimmer)
    ShimmerFrameLayout shimmer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_tours, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        tours = new ArrayList<>();
        adapter = new TourRecyclerAdapter(getContext(), tours, this);
        tourRecycler.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTours();
            }
        });
        swipeRefreshLayout.setRefreshing(true);
        adapter.notifyDataSetChanged();
        getTours();
    }

    void getTours() {
        shimmer.startShimmerAnimation();
        shimmer.setVisibility(View.VISIBLE);
        tourRecycler.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setRefreshing(true);
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot root) {
                tours.clear();
                List<String> fav = new ArrayList<>();
                DataSnapshot toursSnap = root.child(TOURS);
                if (FirebaseAuth.getInstance().getUid() != null) {
                    DataSnapshot user = root.child(USERS).child(FirebaseAuth.getInstance().getUid());
                    for (DataSnapshot snapshot : user.child(FAV_TOURS).getChildren()) {
                        fav.add(snapshot.getKey());
                    }
                }
                for (DataSnapshot snapshot : toursSnap.getChildren()) {
                    Tour tour = snapshot.getValue(Tour.class);
                    if (tour != null) {
                        tour.setKey(snapshot.getKey());
                        for (String favKey : fav) {
                            if (favKey.equals(tour.getKey()))
                                tour.setFav(true);
                        }
                        tours.add(tour);
                    }
                }
                adapter.notifyDataSetChanged();
                tourRecycler.setVisibility(View.VISIBLE);
                shimmer.stopShimmerAnimation();
                shimmer.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                shimmer.stopShimmerAnimation();
                shimmer.setVisibility(View.GONE);
                tourRecycler.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    @Override
    public void setSelected(Tour tour) {
        Intent i = new Intent(getContext(), TourDetailActivity.class);
        i.putExtra(Constants.TOUR, tour);
        startActivity(i);
    }
}
