package com.binoydipu.quickstock.views.inventory.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.cloud.ItemModel;
import com.binoydipu.quickstock.utilities.format.NumberFormater;
import com.binoydipu.quickstock.views.inventory.ItemDetailsActivity;

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

    public class StockSummaryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemName, stockValue, stockQuantity;
        public StockSummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemName = itemView.findViewById(R.id.product_name_tv);
            stockValue = itemView.findViewById(R.id.stock_value_tv);
            stockQuantity = itemView.findViewById(R.id.stock_quantity_tv);
        }

        @Override
        public void onClick(View v) {
            int position = this.getAdapterPosition();
            ItemModel itemModel = itemModels.get(position);
            String itemName = itemModel.getItemName();
            String itemCode = itemModel.getItemCode();
            double purchasePrice = itemModel.getPurchasePrice();
            double salePrice = itemModel.getSalePrice();
            int stockQuantity = itemModel.getStockQuantity();
            long expireDateInMillis = itemModel.getExpireDateInMillis();

            Intent intent = new Intent(context, ItemDetailsActivity.class);
            intent.putExtra("itemName", itemName);
            intent.putExtra("itemCode", itemCode);
            intent.putExtra("purchasePrice", purchasePrice);
            intent.putExtra("salePrice", salePrice);
            intent.putExtra("stockQuantity", stockQuantity);
            intent.putExtra("expireDateInMillis", expireDateInMillis);
            context.startActivity(intent);
        }
    }
}
