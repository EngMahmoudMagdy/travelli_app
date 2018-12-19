package com.magdy.travelli.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.magdy.travelli.Data.Constants;
import com.magdy.travelli.Data.Tour;
import com.magdy.travelli.R;
import com.magdy.travelli.TourInfoListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.rmiri.skeleton.Master.AdapterSkeleton;
import io.rmiri.skeleton.Master.IsCanSetAdapterListener;
import io.rmiri.skeleton.SkeletonGroup;


public class TourRecyclerAdapter extends AdapterSkeleton<Tour,TourRecyclerAdapter.ViewHolder> {
    TourInfoListener listener;
    private RequestQueue queue;
    SharedPreferences preferences ;

    public TourRecyclerAdapter (final Context context , final  ArrayList<Tour> tours, final  RecyclerView recyclerView , final  IsCanSetAdapterListener isCanSetAdapterListener, final TourInfoListener listener)
    {
        this.context = context ;
        this.items = tours ;
        this.isCanSetAdapterListener = isCanSetAdapterListener ;
        measureHeightRecyclerViewAndItem(recyclerView,R.layout.item_tour_horizontal);
        this.listener = listener;
        preferences = context.getSharedPreferences(context.getString(R.string.user_data),0);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tour_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if (skeletonConfig.isSkeletonIsOn()) {
            return;
        } else {
            holder.skeletonGroup.setShowSkeleton(false);
            holder.skeletonGroup.finishAnimation();
        }
        final Tour tour = items.get(position);
        Picasso.with(context).load(tour.getImageLink()).into(holder.image);
        holder.name.setText(tour.getName());
        holder.description.setText(tour.getDetials());
        holder.price.setText(String.format(Locale.getDefault(),"%d EGP.",tour.getPrice()));
        holder.wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"New wishlist item"+tour.getName(),Toast.LENGTH_SHORT).show();
                if(tour.isFav()){
                   // removefromfavourite(tour.getId()) ;
                    holder.wishlist.getDrawable().setColorFilter(ContextCompat.getColor(context, R.color.gray), PorterDuff.Mode.SRC_IN);
                }
                else {
                    //  ``
                    // ]addtofavourite(tour.getId());
                    holder.wishlist.getDrawable().setColorFilter(ContextCompat.getColor(context, R.color.red), PorterDuff.Mode.SRC_IN);
                }
              //if(isfav)
              //
              //  holder.wishlist.getDrawable().setColorFilter(ContextCompat.getColor(context, R.color.gray), PorterDuff.Mode.SRC_IN);
                //else
                //addtofavourite()
                //  holder.wishlist.getDrawable().setColorFilter(ContextCompat.getColor(context, R.color.red), PorterDuff.Mode.SRC_IN);


            }
        });
        holder.ratingBar.setRating(tour.getRate());
        holder.reviewers.setText(String.format(Locale.getDefault(),"(%d)",tour.getReviewers()));
        holder.fromto.setText(String.format(Locale.US,"from %s",tour.getFrom()));
        holder.more.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                listener.setSelected(tour);
            }
        });

        if (tour.isFav()) {
            holder.wishlist.setImageResource(R.drawable.ic_favorite);
            holder.wishlist.getDrawable().setColorFilter(ContextCompat.getColor(context, R.color.red), PorterDuff.Mode.SRC_IN);
        } else {
            holder.wishlist.setImageResource(R.drawable.ic_favorite);
            holder.wishlist.getDrawable().setColorFilter(ContextCompat.getColor(context, R.color.gray), PorterDuff.Mode.SRC_IN);
        }

    }
    private void addtofavourite(final String id)
    {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.BASE+"add_fav.php",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("tour_id", id);
                params.put("user_id", preferences.getString(context.getString(R.string.user_id),"-1"));
                return params;
            }
        };
        queue.add(postRequest);
    }
    private void removefromfavourite(final String id){
        StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.BASE+"remove_fav.php",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("tour_id", id);
                params.put("user_id", preferences.getString(context.getString(R.string.user_id),"-1"));
                return params;
            }
        };
        queue.add(postRequest);
    }
    class ViewHolder extends RecyclerView.ViewHolder {
            private SkeletonGroup skeletonGroup;
            private AppCompatImageView image;
            private TextView name, description , reviewers , fromto , price ;
            private AppCompatRatingBar ratingBar ;
            private AppCompatImageButton wishlist ;
             private RippleView more;
            ViewHolder(View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.image);
                skeletonGroup = itemView.findViewById(R.id.skeletonGroup);
                name =itemView.findViewById(R.id.name);
                ratingBar= itemView.findViewById(R.id.ratebar);
                reviewers=itemView.findViewById(R.id.numofreviews);
                description = itemView.findViewById(R.id.description);
                wishlist =itemView.findViewById(R.id.wishlist_btn);
                price = itemView.findViewById(R.id.price);
                fromto = itemView.findViewById(R.id.from_to);
                more = itemView.findViewById(R.id.more);
            }
        }
}
