package com.example.leave_management_system.adapters.users.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.leave_management_system.backend.students.Leave_Details;
import com.example.leave_management_system.backend.students.Manage_Leaves_Users;
import com.example.leave_management_system.R;
import com.example.leave_management_system.models.BarrerModel;
import com.example.leave_management_system.models.users.model.ManageLeaveUserModel;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageLeaveUserManager extends RecyclerView.Adapter<ManageLeaveUserManager.ViewHolder> {

    private final Context context;
    private ArrayList<ManageLeaveUserModel> leaves;

    private Manage_Leaves_Users activity;

    private final String API_URL = "https://api.smartmohit.com/api/v1/student/remove-leave";

    public ManageLeaveUserManager(Context context, ArrayList<ManageLeaveUserModel> leaves,Manage_Leaves_Users activity){
        this.context = context;
        this.leaves = leaves;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.manage_leaves_users_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.leaveType.setText(leaves.get(position).leaveType);
        holder.leaveDuration.setText(leaves.get(position).leaveDuration);


        holder.leaveContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Leave_Details.class);
                intent.putExtra("leave_id",leaves.get(position).leave_id);
                context.startActivity(intent);
            }
        });

        holder.cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoader(holder.cancel_text,holder.loader);
                cancelLeave(String.valueOf(leaves.get(position).leave_id),holder.cancel_text,holder.loader,position);
            }
        });

        holder.cancel_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoader(holder.cancel_text,holder.loader);
                cancelLeave(String.valueOf(leaves.get(position).leave_id),holder.cancel_text,holder.loader,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return leaves.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView leaveType,leaveDuration,cancel_text;

        MaterialCardView leaveContainer,cancel_button;

        ProgressBar loader;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            leaveType = itemView.findViewById(R.id.leave_type_manage_user);
            leaveDuration = itemView.findViewById(R.id.leave_duration_manage_user);
            leaveContainer = itemView.findViewById(R.id.manage_leave_container);
            cancel_button = itemView.findViewById(R.id.manage_cancel);
            cancel_text = itemView.findViewById(R.id.manage_cancle_text);
            loader = itemView.findViewById(R.id.manage_cancel_loader);
        }
    }

    public void cancelLeave(String leave_id,TextView loaderText,ProgressBar loader, int position){
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hideLoader(loaderText,loader);
                leaves.remove(position);
                notifyItemRemoved(position);

                if (leaves.isEmpty()){
                    activity.loadLeaves();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> map = new HashMap<String,String>();
                map.put("leave_id",leave_id);
                return map;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + BarrerModel.BEARER_TOKEN);
                return headers;
            }
        };

        queue.add(request);
    }

    private void showLoader(TextView loaderText,ProgressBar loader) {
        loader.setVisibility(View.VISIBLE);
        loaderText.setVisibility(View.INVISIBLE);
    }

    public void hideLoader(TextView loaderText,ProgressBar loader) {
        loader.setVisibility(View.INVISIBLE);
        loaderText.setVisibility(View.VISIBLE);
    }
}