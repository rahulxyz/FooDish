package com.rahulxyz.foodish.Utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rahulxyz.foodish.R;

import java.util.ArrayList;

/**
 * Created by raul_Will on 10/23/2017.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    private Context mContext;
    private ArrayList<FoodItem> mOrder;


    public ListAdapter(Context c, ArrayList<FoodItem> list) {
        mContext = c;
        mOrder = list;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        FoodItem foodItem = mOrder.get(position);
        holder.name.setText(foodItem.getName());
        Integer quantity = foodItem.getQuantity();
        Integer price = foodItem.getPrice();
        Integer total = price * quantity;
        holder.quantity.setText(quantity.toString());
        holder.price.setText(total.toString());
    }

    @Override
    public int getItemCount() {
        if (mOrder != null)
            return mOrder.size();
        else
            return 0;
    }

    class ListViewHolder extends RecyclerView.ViewHolder {
        TextView name, quantity, price;

        public ListViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_list);
            quantity = itemView.findViewById(R.id.quantity_list);
            price = itemView.findViewById(R.id.price_list);
        }
    }
}
