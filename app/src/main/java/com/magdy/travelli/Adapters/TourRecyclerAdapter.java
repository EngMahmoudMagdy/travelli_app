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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.magdy.travelli.Data.Tour;
import com.magdy.travelli.R;
import com.magdy.travelli.TourInfoListener;
import com.magdy.travelli.helpers.StaticMembers;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TourRecyclerAdapter extends RecyclerView.Adapter<TourRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Tour> list;
    private TourInfoListener listener;
    private DatabaseReference dbRef;

    public TourRecyclerAdapter(Context context, List<Tour> tours, TourInfoListener listener) {
        this.context = context;
        this.list = tours;
        this.listener = listener;
        dbRef = FirebaseDatabase.getInstance().getReference(StaticMembers.TOURS);
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
                dbRef.child(tour.getKey()).child(StaticMembers.FAV).setValue(true);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                dbRef.child(tour.getKey()).child(StaticMembers.FAV).setValue(false);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.setSelected(tour);
            }
        });
        holder.ratingBar.setRating(tour.getRate());
        holder.reviewers.setText(String.format(Locale.getDefault(), "(%d)", tour.getReviewers()));
        holder.fromto.setText(String.format(Locale.getDefault(), context.getString(R.string.from_to),
                StaticMembers.changeDateFromIsoToView(tour.getFrom()),
                StaticMembers.changeDateFromIsoToView(tour.getTo())));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        AppCompatImageView image;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.noOfReviews)
        TextView reviewers;
        @BindView(R.id.from_to)
        TextView fromto;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.ratingBar)
        AppCompatRatingBar ratingBar;
        @BindView(R.id.wishList)
        LikeButton wishList;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
