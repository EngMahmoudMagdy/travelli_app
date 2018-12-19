package com.magdy.travelli.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.magdy.travelli.Data.Tour;
import com.magdy.travelli.GridInfoListener;
import com.magdy.travelli.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;


public class GridRecyclerAdapter extends RecyclerView.Adapter<GridRecyclerAdapter.SimpleViewHolder> {

    private List<Tour> tours;
    private Context context;

    private GridInfoListener fListener;

    public GridRecyclerAdapter(Context context, List<Tour> tours, GridInfoListener fl) {
        this.tours = tours;
        this.context = context;
        fListener = fl;

    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.tour_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, int position) {
        final Tour tour= tours.get(position);
        Picasso.with(context).load(tour.getImageLink()).into(holder.headImage);
        /*Picasso.with(context).load(place.getImageLink()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                holder.headImage.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });*/
        holder.name.setText(tour.getName());
        holder.desc.setText(tour.getDetials());
        holder.to.setText(tour.getTo());
        holder.from.setText(tour.getFrom());
        holder.price.setText(String.format(Locale.getDefault(),"%d",tour.getPrice()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fListener.setSelected(tour);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tours.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ImageView headImage;
        TextView name,price,to,from,desc;
        SimpleViewHolder(View itemView) {
            super(itemView);
            headImage = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            from = itemView.findViewById(R.id.from);
            to = itemView.findViewById(R.id.to);
            desc = itemView.findViewById(R.id.desc);

        }
    }
}
