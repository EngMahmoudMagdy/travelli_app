package com.magdy.travelli.UI;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.magdy.travelli.Adapters.TourRecyclerAdapter;
import com.magdy.travelli.Data.Constants;
import com.magdy.travelli.Data.Tour;
import com.magdy.travelli.R;
import com.magdy.travelli.TourInfoListener;

import java.util.ArrayList;
import java.util.Objects;

import io.rmiri.skeleton.Master.IsCanSetAdapterListener;
import io.rmiri.skeleton.Master.SkeletonConfig;

public class Favourite extends DialogFragment implements TourInfoListener {
    ArrayList<Tour> favs;
    TourRecyclerAdapter adapter ;
    RecyclerView favRecycler;
    SwipeRefreshLayout swipeRefreshLayout;
    SkeletonConfig config ;
    static Favourite newInstance()
    {
        Favourite f = new Favourite();
        f.setArguments(new Bundle());
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light_DarkActionBar);

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fg_favourite, container, false);
        Toolbar actionBar = v.findViewById(R.id.toolbar);
        if (actionBar!=null) {
            actionBar.setTitle(getString(R.string.fav));
            actionBar.setNavigationOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getDialog().dismiss();
                }
            });
        }
        return v;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favs = new ArrayList<>();
        favRecycler = view.findViewById(R.id.tours);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        favRecycler.setLayoutManager(linearLayoutManager);
        favRecycler.setHasFixedSize(true);
        adapter = new TourRecyclerAdapter(Objects.requireNonNull(getActivity()).getApplicationContext(), favs, favRecycler, new IsCanSetAdapterListener() {
            @Override
            public void isCanSet() {
                favRecycler.setAdapter(adapter);
            }
        },this);
        config = adapter.getSkeletonConfig();
        swipeRefreshLayout =  view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                config.setSkeletonIsOn(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //--favourite data---downloadData();
                    }
                }, 1500);
            }
        });
        swipeRefreshLayout.setRefreshing(true);
        config.setSkeletonIsOn(true);
        adapter.notifyDataSetChanged();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //--favourite data--- downloadData();
            }
        }, 1500);
    }

    @Override
    public void setSelected(Tour tour) {
        Intent i = new Intent(getActivity().getApplicationContext(),TourDetailActivity.class);
        i.putExtra(Constants.TOUR,tour);
        startActivity(i);
    }
}

