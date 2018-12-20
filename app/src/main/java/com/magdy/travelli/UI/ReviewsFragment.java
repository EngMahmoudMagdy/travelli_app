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

import com.magdy.travelli.Adapters.ReviewsAdapter;
import com.magdy.travelli.Data.Constants;
import com.magdy.travelli.Data.Review;
import com.magdy.travelli.Data.Tour;
import com.magdy.travelli.R;

import java.util.List;

public class ReviewsFragment extends Fragment {
    List<Review> reviews;
    Tour tour;

    public static ReviewsFragment newInstance(Tour tour) {
        ReviewsFragment fragment = new ReviewsFragment();
        fragment.tour = tour;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reviews_tab, container, false);


        RecyclerView recyclerView = view.findViewById(R.id.reviews_scrollableview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        ReviewsAdapter adapter = new ReviewsAdapter(getContext(), reviews);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null)
            tour = (Tour) savedInstanceState.getSerializable(Constants.TOUR);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.TOUR, tour);
    }
}