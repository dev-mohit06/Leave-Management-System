package com.example.leave_management_system.backend.students;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.leave_management_system.databinding.LeaveDetailsUsersBinding;
import com.example.leave_management_system.models.BarrerModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Leave_Details extends AppCompatActivity {

    LeaveDetailsUsersBinding binding;

    private final String API_URL = BarrerModel.API_BASE_URL + "student/get-leaves";
    private final String API_DELETE_URL = BarrerModel.API_BASE_URL + "student/remove-leave";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LeaveDetailsUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showLoader();

        SharedPreferences session = getSharedPreferences("session",MODE_PRIVATE);
        Intent prevActivityData = getIntent();

        int leave_id = prevActivityData.getIntExtra("leave_id",1);
        RequestQueue queue = Volley.newRequestQueue(Leave_Details.this);

        StringRequest request = new StringRequest(Request.Method.POST, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoader();
                try {
                    JSONObject rawData = new JSONObject(response);
                    JSONObject leaveData = rawData.getJSONObject("data");

                    binding.leaveType.setText(leaveData.getString("leave_type"));
                    binding.from.setText(leaveData.getString("starting_date"));
                    binding.to.setText(leaveData.getString("ending_date"));

                    String leaveStatus = leaveData.getString("leave_status");

                    if (leaveStatus.equals("1")){
                        binding.status.setText("approved");
                    }
                    else if (leaveStatus.equals("0")){
                        binding.status.setText("pending");
                    }
                    else if (leaveStatus.equals("-1")){
                        binding.status.setText("denied");
                    }

                    binding.reason.setText(leaveData.getString("reason"));
                    binding.leaveDetails.setText(leaveData.getString("details"));

                    if(leaveData.getString("faculty_message").equals("null") && leaveData.getString("leave_status").equals("0")){
                        binding.cancleText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                binding.cancleText.setVisibility(View.INVISIBLE);
                                binding.cancelLoader.setVisibility(View.VISIBLE);
                                RequestQueue queue1 = Volley.newRequestQueue(Leave_Details.this);

                                StringRequest request1 = new StringRequest(Request.Method.POST, API_DELETE_URL, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        binding.cancleText.setVisibility(View.VISIBLE);
                                        binding.cancelLoader.setVisibility(View.INVISIBLE);
                                        finish();
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
                                        map.put("leave_id",Integer.toString(leave_id));
                                        return map;
                                    }

                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String, String> headers = new HashMap<>();
                                        headers.put("Authorization", "Bearer " + BarrerModel.BEARER_TOKEN);
                                        return headers;
                                    }
                                };

                                queue1.add(request1);
                            }
                        });
                    }else{
                        if(leaveData.getString("faculty_message").equals("null")){
                            binding.councilorMessage.setText("There is no message from your councillor");
                        }else{
                            binding.councilorMessage.setText(leaveData.getString("faculty_message"));
                        }
                        binding.cancel.setVisibility(View.INVISIBLE);
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
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

                map.put("getSingleLeave",Integer.toString(leave_id));
                map.put("student_id",session.getString("id","1"));

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

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showLoader() {
        binding.loader.setVisibility(View.VISIBLE);
        binding.leaveDataContainer.setVisibility(View.INVISIBLE);
    }

    public void hideLoader() {
        binding.loader.setVisibility(View.INVISIBLE);
        binding.leaveDataContainer.setVisibility(View.VISIBLE);
    }
}