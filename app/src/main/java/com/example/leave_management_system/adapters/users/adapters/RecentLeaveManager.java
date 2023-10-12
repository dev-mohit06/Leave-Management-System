package com.example.leave_management_system.adapters.users.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.leave_management_system.backend.students.Leave_Details;
import com.example.leave_management_system.models.users.model.RecentLeaveModel;
import com.example.leave_management_system.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class RecentLeaveManager extends RecyclerView.Adapter<RecentLeaveManager.ViewHolder> {

    private Context context;
    private ArrayList<RecentLeaveModel> leaveData;

    public RecentLeaveManager(Context context, ArrayList<RecentLeaveModel> leaveData){
        this.context = context;
        this.leaveData = leaveData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_leave_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.leaveTitle.setText(leaveData.get(position).leaveTitle);
        holder.leaveDuration.setText(leaveData.get(position).leaveDuration);

        int currentLeaveStatus = leaveData.get(position).leaveStatus;
        if (currentLeaveStatus == 1){
            holder.leaveStatus.setImageResource(R.drawable.success_indicator);
        }else if (currentLeaveStatus == 0){
            holder.leaveStatus.setImageResource(R.drawable.pending_indicator);
        }else{
            holder.leaveStatus.setImageResource(R.drawable.denied_indicator);
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,Leave_Details.class);
                intent.putExtra("leave_id",leaveData.get(position).leaveId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return leaveData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView leaveTitle,leaveDuration;
        ImageView leaveStatus;

        MaterialCardView container;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            leaveTitle = itemView.findViewById(R.id.LeaveTitle);
            leaveDuration = itemView.findViewById(R.id.LeaveDuration);
            leaveStatus = itemView.findViewById(R.id.LeaveStatus);

            container = itemView.findViewById(R.id.recent_leave_container);
        }
    }

}
