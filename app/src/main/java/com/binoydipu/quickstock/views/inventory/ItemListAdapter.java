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

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemListViewHolder>{
    private Context context;
    private ArrayList<ItemModel> itemModels;

    public ItemListAdapter(Context context, ArrayList<ItemModel> itemModels) {
        this.context = context;
        this.itemModels = itemModels;
    }

    @NonNull
    @Override
    public ItemListAdapter.ItemListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_model, parent, false);
        return new ItemListAdapter.ItemListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListAdapter.ItemListViewHolder holder, int position) {
        ItemModel itemModel = itemModels.get(position);
        holder.itemName.setText(itemModel.getItemName());
        String purchasePrice = NumberFormater.formatPrice(itemModel.getPurchasePrice());
        String salePrice = NumberFormater.formatPrice(itemModel.getSalePrice());
        String stockSize = String.valueOf(itemModel.getStockQuantity());
        holder.purchasePrice.setText(purchasePrice);
        holder.salePrice.setText(salePrice);
        holder.stockSize.setText(stockSize);
        if(itemModel.getStockQuantity() < 0) {
            holder.stockSize.setBackgroundColor(context.getResources().getColor(R.color.red, context.getTheme()));
        }
    }

    @Override
    public int getItemCount() {
        return itemModels.size();
    }

    public class ItemListViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, purchasePrice, salePrice, stockSize;
        public ItemListViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.product_name_tv);
            purchasePrice = itemView.findViewById(R.id.purchase_price_tv);
            salePrice = itemView.findViewById(R.id.sale_price_tv);
            stockSize = itemView.findViewById(R.id.stock_size_tv);
        }
    }
}
