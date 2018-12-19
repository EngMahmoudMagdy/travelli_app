package com.magdy.travelli.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.magdy.travelli.Data.Tour;
import com.magdy.travelli.R;
import com.magdy.travelli.UI.TourDetailActivity;

import java.util.List;

/**
 * Created by engma on 10/4/2017.
 */

public class HorizontalRecyclerAdapter extends RecyclerView.Adapter<HorizontalRecyclerAdapter.Holder> {

    private List<Tour>tours ;
    private Context context ;
    public HorizontalRecyclerAdapter(Context context, List<Tour>tours)
    {
        this.tours = tours ;
        this.context = context ;
    }
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(this.context).inflate(R.layout.tour_item, parent, false);
        return new Holder(view);
    }
    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.title.setText(tours.get(position).getName());
        //holder.imageView.setImageResource(tours.get(position).getRes());
        holder.price.setText(tours.get(position).getPrice());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TourDetailActivity.class);
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return tours.size();
    }
    class Holder extends RecyclerView.ViewHolder
    {
        ImageView imageView ;
        TextView title , price ;
        Holder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
        }
    }
}
