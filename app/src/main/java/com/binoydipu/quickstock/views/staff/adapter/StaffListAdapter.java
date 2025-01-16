package com.binoydipu.quickstock.views.staff.adapter;

import static com.binoydipu.quickstock.constants.ConstantValues.ADMIN_EMAIL;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.auth.AuthUser;
import com.binoydipu.quickstock.services.cloud.FirebaseCloudStorage;

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
        if(user.isStaffVerified() || user.getUserEmail().equals(ADMIN_EMAIL)) {
            holder.ivVerified.setVisibility(View.VISIBLE);
            holder.ivAccept.setVisibility(View.GONE);
            holder.ivDecline.setVisibility(View.GONE);
        } else {
            holder.ivAccept.setVisibility(View.VISIBLE);
            holder.ivDecline.setVisibility(View.VISIBLE);
            holder.ivVerified.setVisibility(View.GONE);
        }
        holder.ivAccept.setOnClickListener(v -> onAccepted(position));
        holder.ivDecline.setOnClickListener(v -> onDecline(position));
        holder.itemView.setOnLongClickListener(v -> {
            if(!user.getUserEmail().equals(ADMIN_EMAIL)) {
                onDeleteStaff(position);
            } else {
                Toast.makeText(context, "Admin Info Can't Be Deleted", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void onDeleteStaff(int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Staff?")
                .setMessage("Are you sure you want to delete this staff?")
                .setIcon(R.drawable.quick_stock)
                .setPositiveButton("Yes", (dialog, which) -> {
                    AuthUser user = filteredStaffList.get(position);
                    FirebaseCloudStorage.getInstance().deleteUserByUserId(user.getUserId(), isItemDeleted -> {
                        if (isItemDeleted) {
                            Toast.makeText(context, "Staff Deleted", Toast.LENGTH_SHORT).show();
                            staffList.remove(position);
                            filterStaffs("");
                        }
                    });
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void onDecline(int position) {
        new AlertDialog.Builder(context)
                .setTitle("Decline Staff?")
                .setMessage("Are you sure you want to decline this staff? Staff data be deleted!")
                .setIcon(R.drawable.quick_stock)
                .setPositiveButton("Yes", (dialog, which) -> {
                    AuthUser user = filteredStaffList.get(position);
                    FirebaseCloudStorage.getInstance().deleteUserByUserId(user.getUserId(), isItemDeleted -> {
                        if(isItemDeleted) {
                            Toast.makeText(context, "Declined", Toast.LENGTH_SHORT).show();
                            staffList = FirebaseCloudStorage.getInstance().getAllUsers(context, isReceived -> {
                                if(isReceived) filterStaffs("");
                            });
                        }
                    });
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void onAccepted(int position) {
        new AlertDialog.Builder(context)
                .setTitle("Accept Staff?")
                .setMessage("Are you sure you want to accept this staff? Staff will be authorized!")
                .setIcon(R.drawable.quick_stock)
                .setPositiveButton("Yes", (dialog, which) -> {
                    AuthUser user = filteredStaffList.get(position);
                    FirebaseCloudStorage.getInstance().updateUser(user.getUserId(), user.getUserName(),
                            user.getStaffId(), user.getUserEmail(), user.getMobileNo(), true,
                            true, isUserUpdated -> {
                        if(isUserUpdated) {
                            Toast.makeText(context, "Accepted", Toast.LENGTH_SHORT).show();
                            staffList = FirebaseCloudStorage.getInstance().getAllUsers(context, isReceived -> {
                                if(isReceived) filterStaffs("");
                            });
                        }
                    });
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public int getItemCount() {
        return filteredStaffList.size();
    }

    public void filterStaffs(String searchText) {
        ArrayList<AuthUser> tempList = new ArrayList<>();
        if (searchText.isEmpty()) {
            tempList.addAll(staffList);
        } else {
            searchText = searchText.toLowerCase();
            for (AuthUser user : staffList) {
                if (user.getUserName().toLowerCase().contains(searchText)) {
                    tempList.add(user);
                }
            }
        }
        filteredStaffList.clear();
        filteredStaffList.addAll(tempList);
        notifyDataSetChanged();
    }

    public static class StaffListViewHolder extends RecyclerView.ViewHolder {
        TextView userName, staffId, userEmail, mobileNo;
        ImageView ivVerified, ivAccept, ivDecline;

        public StaffListViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name_tv);
            staffId = itemView.findViewById(R.id.staff_id_tv);
            userEmail = itemView.findViewById(R.id.user_email_tv);
            mobileNo = itemView.findViewById(R.id.user_mobile_tv);
            ivVerified = itemView.findViewById(R.id.verified_user_icon);
            ivAccept = itemView.findViewById(R.id.accept_user_icon);
            ivDecline = itemView.findViewById(R.id.decline_user_icon);
        }
    }
}
