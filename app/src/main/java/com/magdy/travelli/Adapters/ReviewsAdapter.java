package com.magdy.travelli.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;


import com.magdy.travelli.Data.Review;
import com.magdy.travelli.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Suleiman on 02/03/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsVh> {


    private List<Review> reviews = new ArrayList<>();

    private Context context;

    public ReviewsAdapter(Context context , List<Review> reviews) {
        this.reviews = reviews;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewsVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.reviews_item, parent, false);
        return new ReviewsAdapter.ReviewsVh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsVh holder, int position) {
        Review dessert = reviews.get(position);

        holder.mName.setText(dessert.getName());
        holder.mDescription.setText(dessert.getDescription());
        holder.mRating.setRating(Float.parseFloat(dessert.getRate()));
//        holder.mFirstLetter.setText(String.valueOf(dessert.getFirstLetter()));

    }

    @Override
    public int getItemCount() {
        return reviews == null ? 0 : reviews.size();
    }

    static class ReviewsVh extends RecyclerView.ViewHolder {

        private TextView mName;
        private TextView mDescription;
        private TextView mFirstLetter;
        private RatingBar mRating;

        ReviewsVh(View itemView) {
            super(itemView);
            mRating =  itemView.findViewById(R.id.user_rate) ;
            mName =  itemView.findViewById(R.id.txt_name);
            mDescription =  itemView.findViewById(R.id.txt_desc);
//            mFirstLetter = (TextView) itemView.findViewById(R.id.txt_firstletter);
        }
    }
}