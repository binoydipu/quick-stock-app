package com.binoydipu.quickstock.helper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.auth.AuthUser;

import java.util.ArrayList;

public class StaffListAdapter extends RecyclerView.Adapter<StaffListAdapter.StaffListViewHolder> {

    private final Context context;
    private ArrayList<AuthUser> staffList, filteredStaffList;

    public StaffListAdapter(Context context, ArrayList<AuthUser> staffList) {
        this.context = context;
        this.staffList = staffList;
        this.filteredStaffList = new ArrayList<>(staffList);
    }

    @NonNull
    @Override
    public StaffListAdapter.StaffListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_model, parent, false);
        return new StaffListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffListAdapter.StaffListViewHolder holder, int position) {
        AuthUser user = filteredStaffList.get(position);
        holder.userName.setText(String.format("Name: %s", user.getUserName()));
        holder.staffId.setText(String.format("Staff ID: %s", user.getStaffId()));
        holder.userEmail.setText(String.format("Email: %s", user.getUserEmail()));
        holder.mobileNo.setText(String.format("Mobile: %s", user.getMobileNo()));
    }

    @Override
    public int getItemCount() {
        return filteredStaffList.size();
    }

    public void filterProducts(String searchText) {
        filteredStaffList.clear();
        if (searchText.isEmpty()) {
            filteredStaffList.addAll(staffList);
        } else {
            searchText = searchText.toLowerCase();
            for (AuthUser user : staffList) {
                if (user.getUserName().toLowerCase().contains(searchText)) {
                    filteredStaffList.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class StaffListViewHolder extends RecyclerView.ViewHolder {
        TextView userName, staffId, userEmail, mobileNo;

        public StaffListViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name_tv);
            staffId = itemView.findViewById(R.id.staff_id_tv);
            userEmail = itemView.findViewById(R.id.user_email_tv);
            mobileNo = itemView.findViewById(R.id.user_mobile_tv);
        }
    }
}
