package com.badboy.gpstracker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badboy.gpstracker.R;
import com.badboy.gpstracker.model.LocationObj;
import com.badboy.gpstracker.utils.Utils;

import java.text.ParseException;
import java.util.List;

/**
 * Created by Bad Boy on 1/26/2017.
 */

public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.ItemViewHolder> {
    private List<LocationObj> locationObjs;
    public static RecyclerViewItemClickListener itemClickListener;
    public TripListAdapter(List<LocationObj> locationObjs,RecyclerViewItemClickListener itemClickListener) {
        this.locationObjs = locationObjs;
        this.itemClickListener = itemClickListener;
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trip_list, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ItemViewHolder vh = new ItemViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        try {
            holder.tvTripDate.setText(Utils.getFormattedDate(locationObjs.get(position).getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tvTripPoints.setText(locationObjs.get(position).getPoints() + " Points");
    }

/*    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.tvTripdate.setText(locationObjs.get(position).getDate());
        holder.tvTripPoints.setText(locationObjs.get(position).getPoints());

    }*/

    @Override
    public int getItemCount() {
        return this.locationObjs.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTripDate,tvTripPoints;
        ImageView ivTripIcon,ivTripDeleteIcon;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvTripDate = (TextView) itemView.findViewById(R.id.tvTripDate);
            tvTripPoints = (TextView) itemView.findViewById(R.id.tvTripPoints);
            ivTripDeleteIcon = (ImageView) itemView.findViewById(R.id.ivTripDeleteIcon);
            ivTripDeleteIcon.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            itemClickListener.recyclerViewItemClicked(v,position);
        }
    }

    public interface RecyclerViewItemClickListener {
        public void recyclerViewItemClicked(View v, int position);
    }
}


