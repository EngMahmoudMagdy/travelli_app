package com.magdy.travelli.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.magdy.travelli.Adapters.TourRecyclerAdapter;
import com.magdy.travelli.Data.Constants;
import com.magdy.travelli.Data.Tour;
import com.magdy.travelli.R;
import com.magdy.travelli.Services.StringRequestNew;
import com.magdy.travelli.TourInfoListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import io.rmiri.skeleton.Master.IsCanSetAdapterListener;
import io.rmiri.skeleton.Master.SkeletonConfig;


/**
 * Created by engma on 10/4/2017.
 */

public class ToursFragment extends Fragment implements TourInfoListener{
    static ToursFragment newInstance()
    {
        ToursFragment f = new ToursFragment();
        f.setArguments(new Bundle());
        return f;
    }
    ArrayList<Tour>tours;
    TourRecyclerAdapter adapter ;
    RecyclerView tourRecycler;
    SwipeRefreshLayout swipeRefreshLayout;
    RequestQueue queue ;
    SkeletonConfig config ;
    String url =Constants.BASE+"view_tours.php";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_tours,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tours = new ArrayList<>();
        //adapter = new GridRecyclerAdapter(getContext(),tours,this);
        tourRecycler = view.findViewById(R.id.tours);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        tourRecycler.setLayoutManager(linearLayoutManager);
        tourRecycler.setHasFixedSize(true);
        adapter = new TourRecyclerAdapter(Objects.requireNonNull(getActivity()).getApplicationContext(), tours, tourRecycler, new IsCanSetAdapterListener() {
            @Override
            public void isCanSet() {
                tourRecycler.setAdapter(adapter);
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

                        downloadData();
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
                downloadData();
            }
        }, 1500);
    }

    void downloadData()
    {
        CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ALL ) );
        if (getContext()!=null) {
            queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
            StringRequestNew stringRequest = new StringRequestNew(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("data", response);
                            swipeRefreshLayout.setRefreshing(false);
                            config.setSkeletonIsOn(true);
                            try {
                                JSONObject o = new JSONObject(response);
                                JSONArray arr = o.getJSONArray("tours");
                                tours.clear();
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject obj = (JSONObject) arr.get(i);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd hh:mm a z", Locale.getDefault());
                                    Tour p = new Tour(obj.getString("id"),
                                            obj.getString("name"),
                                            obj.getString("description"),
                                            obj.getInt("price"),
                                            obj.getString("image"),
                                            dateFormat.format(obj.getDouble("from")),
                                            dateFormat.format(obj.getDouble("to")),
                                            obj.getString("agency"),
                                            Float.parseFloat(obj.getString("rate")),
                                            Integer.parseInt(obj.getString("reviewers"))
                                            , obj.getBoolean("fav"));
                                    tours.add(p);
                                }
                                adapter.addMoreDataAndSkeletonFinish((ArrayList<Tour>) tours);
                                //Toast.makeText(getContext(), "DB download completed!", Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                //Toast.makeText(getContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            MyApp.getInstance().addToRequestQueue(stringRequest);
        }
    }
    @Override
    public void setSelected(Tour tour) {
        Intent i = new Intent(getContext(),TourDetailActivity.class);
        i.putExtra(Constants.TOUR,tour);
        startActivity(i);
        //Toast.makeText(getContext(),tour.getName()+"\n"+tour.getDetials(),Toast.LENGTH_SHORT).show();
    }
}