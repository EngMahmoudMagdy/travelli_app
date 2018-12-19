package com.magdy.travelli.UI;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.magdy.travelli.Data.Constants;
import com.magdy.travelli.Data.Tour;
import com.magdy.travelli.R;

public class DetailsFragment extends Fragment {
    Tour tour ;
    public static DetailsFragment newInstance(Tour tour)
    {
        DetailsFragment fragment = new DetailsFragment();
        fragment.tour = tour ;
        return fragment ;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        return inflater.inflate(R.layout.details_tab, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState!=null)
            tour= (Tour) savedInstanceState.getSerializable(Constants.TOUR);
        TextView textView = view.findViewById(R.id.name);
        textView.setText(tour.getName());
        textView = view.findViewById(R.id.description);
        textView.setText(tour.getDetials());
        textView = view.findViewById(R.id.from);
        textView.setText(tour.getFrom());
        textView = view.findViewById(R.id.to);
        textView.setText(tour.getTo());
/*        textView = view.findViewById(R.id.max_person);
        textView.setText(tour.);*/
        textView = view.findViewById(R.id.agency);
        textView.setText(tour.getAgency());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.TOUR,tour);
    }
}