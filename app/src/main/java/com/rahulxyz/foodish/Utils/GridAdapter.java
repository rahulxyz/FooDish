package com.rahulxyz.foodish.Utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;
import com.rahulxyz.foodish.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raul_Will on 10/20/2017.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridViewHolder> {

    private Context mContext;
    private ArrayList<FoodItem> mMenuList;
    private GridItemClickListener mOnClickListener;

    private StorageReference mStorageRef;

    public GridAdapter(Context c,
                       ArrayList<FoodItem> list,
                       StorageReference ref,
                       GridItemClickListener clickListener) {
        mContext = c;
        mMenuList = list;
        mStorageRef = ref;
        mOnClickListener = clickListener;
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.grid_item, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GridViewHolder holder, int position) {

        final FoodItem foodItem = mMenuList.get(position);
        holder.name.setText(foodItem.getName().toString());
        holder.price.setText(mContext.getString(R.string.indianCurrency) + foodItem.getPrice().toString());
        holder.quantity.setText(foodItem.getQuantity().toString());

        if (foodItem.getImageUrl() != null && !foodItem.getImageUrl().isEmpty()) {
            Picasso.with(mContext)
                    .load(foodItem.getImageUrl())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.thumbnail, new Callback() {
                        @Override
                        public void onSuccess() {
                            //no problem
                        }

                        @Override
                        public void onError() {
                            // Try again online if cache failed
                            Picasso.with(mContext).load(foodItem.getImageUrl()).into(holder.thumbnail);
                        }
                    });
        } else {
            holder.thumbnail.setImageResource(R.drawable.noimage);
        }

        if (foodItem.getScrimToggle())
            holder.scrim.setVisibility(View.VISIBLE);
        else
            holder.scrim.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (payloads.isEmpty()) {
            // Perform a full update
            onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        if (mMenuList == null)
            return 0;
        else
            return mMenuList.size();
    }

    public interface GridItemClickListener {
        void onCardClick(View root, int index);

        void onLongCardClick(View root, int index);
    }

    class GridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView name, price, quantity;
        ImageView thumbnail;
        View scrim;

        public GridViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            quantity = itemView.findViewById(R.id.quantity);
            scrim = itemView.findViewById(R.id.scrim);
        }

        @Override
        public void onClick(View view) {
            int index = getAdapterPosition();
            mOnClickListener.onCardClick(view, index);
        }


        @Override
        public boolean onLongClick(View view) {
            int index = getAdapterPosition();
            mOnClickListener.onLongCardClick(view, index);
            return true;
        }
    }
}
