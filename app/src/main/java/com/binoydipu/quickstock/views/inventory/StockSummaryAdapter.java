package com.binoydipu.quickstock.views.inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.cloud.ItemModel;
import com.binoydipu.quickstock.utilities.format.NumberFormater;

import java.util.ArrayList;

public class StockSummaryAdapter extends RecyclerView.Adapter<StockSummaryAdapter.StockSummaryViewHolder>{
    private Context context;
    private ArrayList<ItemModel> itemModels;

    public StockSummaryAdapter(Context context, ArrayList<ItemModel> itemModels) {
        this.context = context;
        this.itemModels = itemModels;
    }

    @NonNull
    @Override
    public StockSummaryAdapter.StockSummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stock_model, parent, false);
        return new StockSummaryAdapter.StockSummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockSummaryAdapter.StockSummaryViewHolder holder, int position) {
        ItemModel itemModel = itemModels.get(position);
        holder.itemName.setText(itemModel.getItemName());
        String stockValue = NumberFormater.formatPrice(itemModel.getPurchasePrice() * itemModel.getStockQuantity());
        String stockQuantity = String.valueOf(itemModel.getStockQuantity());
        holder.stockValue.setText(stockValue);
        holder.stockQuantity.setText(stockQuantity);
    }

    @Override
    public int getItemCount() {
        return itemModels.size();
    }

    public class StockSummaryViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, stockValue, stockQuantity;
        public StockSummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.product_name_tv);
            stockValue = itemView.findViewById(R.id.stock_value_tv);
            stockQuantity = itemView.findViewById(R.id.stock_quantity_tv);
        }
    }
}
