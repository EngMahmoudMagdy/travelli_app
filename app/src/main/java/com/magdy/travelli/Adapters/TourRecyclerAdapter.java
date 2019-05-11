package com.magdy.travelli.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.magdy.travelli.Data.Tour;
import com.magdy.travelli.R;
import com.magdy.travelli.TourInfoListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;


public class TourRecyclerAdapter extends RecyclerView.Adapter<TourRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Tour> list;
    private TourInfoListener listener;

    public TourRecyclerAdapter(Context context, List<Tour> tours, TourInfoListener listener) {
        this.context = context;
        this.list = tours;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tour_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Tour tour = list.get(position);
        Picasso.with(context).load(tour.getImageLink()).into(holder.image);
        holder.name.setText(tour.getName());
        holder.description.setText(tour.getDetials());
        holder.price.setText(String.format(Locale.getDefault(), "%d EGP.", tour.getPrice()));
        holder.wishList.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

            }

            @Override
            public void unLiked(LikeButton likeButton) {

            }
        });
        holder.ratingBar.setRating(tour.getRate());
        holder.reviewers.setText(String.format(Locale.getDefault(), "(%d)", tour.getReviewers()));
        holder.fromto.setText(String.format(Locale.US, "from %s", tour.getFrom()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView image;
        private TextView name, description, reviewers, fromto, price;
        private AppCompatRatingBar ratingBar;
        private LikeButton wishList;

        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            reviewers = itemView.findViewById(R.id.noOfReviews);
            description = itemView.findViewById(R.id.description);
            wishList = itemView.findViewById(R.id.wishList);
            price = itemView.findViewById(R.id.price);
            fromto = itemView.findViewById(R.id.from_to);
        }
    }
}
