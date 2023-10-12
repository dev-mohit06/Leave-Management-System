package com.example.leave_management_system.adapters.users.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.leave_management_system.backend.students.Leave_Details;
import com.example.leave_management_system.R;
import com.example.leave_management_system.models.users.model.LeaveHistroyUserModel;

import java.util.ArrayList;

public class LeaveHistroyUserManager extends RecyclerView.Adapter<LeaveHistroyUserManager.ViewHolder> {

    private final Context context;
    private ArrayList<LeaveHistroyUserModel> leaves;

    public LeaveHistroyUserManager(Context context,ArrayList<LeaveHistroyUserModel> leaves) {
        this.context = context;
        this.leaves = leaves;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leave_histroy_users_layout,parent,false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String Status = "",ColorCode = "";
        if (leaves.get(position).leave_status == 1){
            Status = "Approved";
            ColorCode = "#35C759";
        }else  if (leaves.get(position).leave_status == 0){
            Status = "Pending";
            ColorCode = "#EFC56F";
        }else {
            Status = "Denied";
            ColorCode = "#CCD12E2E";
        }
        holder.leaveStatus.setText(Status);
        holder.leaveStatus.setTextColor(Color.parseColor(ColorCode));
        holder.leaveType.setText(leaves.get(position).leave_type);
        holder.leaveDurtaion.setText(leaves.get(position).leave_duration);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,Leave_Details.class);
                intent.putExtra("leave_id",Integer.parseInt(leaves.get(position).leave_id));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return leaves.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView leaveType,leaveDurtaion,leaveStatus;

        LinearLayout container;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            leaveType = itemView.findViewById(R.id.leave_type_leave_histroy);
            leaveDurtaion = itemView.findViewById(R.id.leave_duration_leave_histroy);
            leaveStatus  = itemView.findViewById(R.id.leave_status_leave_histroy);
            container = itemView.findViewById(R.id.leave_histroy_container);
        }
    }
}
