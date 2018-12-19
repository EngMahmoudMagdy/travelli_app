package com.magdy.travelli.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.magdy.travelli.Data.Place;
import com.magdy.travelli.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {
    private List<Place> placeList;
    public Context context;
    public PlaceAdapter(List<Place> placeLst, Context ctx){
        placeList = placeLst;
        context = ctx;
    }

    @NonNull
    @Override
    public PlaceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item_for_place_tab, parent, false);
        return new ViewHolder(view);
    }
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = placeList.get(position);
        holder.title.setText(place.getName());
        holder.description.setText(place.getDescription());
        Picasso.with(context).load(place.getImageLink()).into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView description;
        AppCompatImageView photo;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.place_title);
            description =  view.findViewById(R.id.place_description);
            photo =  itemView.findViewById(R.id.place_photo);
        }
    }

}
