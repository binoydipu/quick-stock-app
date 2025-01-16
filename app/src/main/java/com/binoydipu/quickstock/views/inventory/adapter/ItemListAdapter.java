package com.binoydipu.quickstock.views.inventory.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.auth.AuthUser;
import com.binoydipu.quickstock.services.cloud.ItemModel;
import com.binoydipu.quickstock.utilities.format.NumberFormater;
import com.binoydipu.quickstock.views.inventory.ItemDetailsActivity;

import java.util.ArrayList;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemListViewHolder>{
    private Context context;
    private ArrayList<ItemModel> itemModels, filteredItemModels;

    public ItemListAdapter(Context context, ArrayList<ItemModel> itemModels) {
        this.context = context;
        this.itemModels = itemModels;
        filteredItemModels = new ArrayList<>(itemModels);
    }

    @NonNull
    @Override
    public ItemListAdapter.ItemListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_model, parent, false);
        return new ItemListAdapter.ItemListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListAdapter.ItemListViewHolder holder, int position) {
        ItemModel itemModel = filteredItemModels.get(position);
        holder.itemName.setText(itemModel.getItemName());
        String purchasePrice = NumberFormater.formatPrice(itemModel.getPurchasePrice());
        String salePrice = NumberFormater.formatPrice(itemModel.getSalePrice());
        String stockSize = String.valueOf(itemModel.getStockQuantity());
        holder.purchasePrice.setText(purchasePrice);
        holder.salePrice.setText(salePrice);
        holder.stockSize.setText(stockSize);
        if(itemModel.getStockQuantity() < 0) {
            holder.stockSize.setTextColor(context.getResources().getColor(R.color.red, context.getTheme()));
        }
    }

    @Override
    public int getItemCount() {
        return filteredItemModels.size();
    }

    public void filterItems(String searchText) {
        ArrayList<ItemModel> tempList = new ArrayList<>();
        if (searchText.isEmpty()) {
            tempList.addAll(itemModels);  // Add all items if search text is empty
        } else {
            searchText = searchText.toLowerCase();
            for (ItemModel item : itemModels) {
                if (item.getItemName().toLowerCase().contains(searchText)) {
                    tempList.add(item);
                }
            }
        }
        filteredItemModels.clear();
        filteredItemModels.addAll(tempList);
        notifyDataSetChanged();
    }

    public class ItemListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemName, purchasePrice, salePrice, stockSize;
        public ItemListViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemName = itemView.findViewById(R.id.product_name_tv);
            purchasePrice = itemView.findViewById(R.id.purchase_price_tv);
            salePrice = itemView.findViewById(R.id.sale_price_tv);
            stockSize = itemView.findViewById(R.id.stock_size_tv);
        }

        @Override
        public void onClick(View v) {
            int position = this.getAdapterPosition();
            Intent intent = new Intent(context, ItemDetailsActivity.class);
            intent.putExtra("itemName", filteredItemModels.get(position).getItemName());
            context.startActivity(intent);
        }
    }
}
