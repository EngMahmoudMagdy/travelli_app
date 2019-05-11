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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magdy.travelli.Adapters.TourRecyclerAdapter;
import com.magdy.travelli.Data.Constants;
import com.magdy.travelli.Data.Tour;
import com.magdy.travelli.R;
import com.magdy.travelli.TourInfoListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.magdy.travelli.helpers.StaticMembers.TOURS;


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
        swipeRefreshLayout.setRefreshing(true);
        FirebaseDatabase.getInstance().getReference(TOURS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tours.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Tour tour = snapshot.getValue(Tour.class);
                    if (tour != null) {
                        tour.setKey(snapshot.getKey());
                        tours.add(tour);
                    }
                }
                adapter.notifyDataSetChanged();
                shimmer.stopShimmerAnimation();
                shimmer.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                shimmer.stopShimmerAnimation();
                shimmer.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    void addTour(final int i) {
        if (!tours.isEmpty() && i < tours.size()) {
            final Tour tour = tours.get(i);
            final DatabaseReference dr = FirebaseDatabase.getInstance().getReference(TOURS).push();
            dr.setValue(tour).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        tour.setKey(dr.getKey());
                        addTour(i + 1);
                    }
                }
            });
        }
    }

    @Override
    public void setSelected(Tour tour) {
        Intent i = new Intent(getContext(), TourDetailActivity.class);
        i.putExtra(Constants.TOUR, tour);
        startActivity(i);
        //Toast.makeText(getContext(),tour.getName()+"\n"+tour.getDetials(),Toast.LENGTH_SHORT).show();
    }
}
