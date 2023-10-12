package com.example.leave_management_system.backend.students;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
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
import com.example.leave_management_system.adapters.users.adapters.LeaveHistroyUserManager;
import com.example.leave_management_system.databinding.LeaveHistroyUsersBinding;
import com.example.leave_management_system.models.BarrerModel;
import com.example.leave_management_system.models.users.model.LeaveHistroyUserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Leave_Histroy_User extends AppCompatActivity {

    LeaveHistroyUsersBinding binding;


    ArrayList<LeaveHistroyUserModel> leaves;

    SharedPreferences session;


    private final String API_URL = BarrerModel.API_BASE_URL + "student/get-leaves";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LeaveHistroyUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = getSharedPreferences("session",MODE_PRIVATE);

        leaves = new ArrayList<>();
        binding.leaveContainer.setLayoutManager(new LinearLayoutManager(Leave_Histroy_User.this));
        loadHistroy();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void loadHistroy(){
        showLoader();
        binding.noItem.setVisibility(View.INVISIBLE);
        binding.noFoundText.setVisibility(View.INVISIBLE);
        RequestQueue queue = Volley.newRequestQueue(Leave_Histroy_User.this);

        StringRequest request = new StringRequest(Request.Method.POST, API_URL, new Response.Listener<String>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    JSONArray leaveArray = jsonObject.getJSONArray("data");

                    if (!(leaveArray.length() <= 0)){
                        for (int i = 0; i < leaveArray.length(); i++) {
                            JSONObject data = leaveArray.getJSONObject(i);

                            int leave_id = data.getInt("id");
                            String leave_type = data.getString("leave_type");
                            String leave_status = data.getString("leave_status");
                            String starting_date = data.getString("starting_date").replace("-","/");
                            String ending_date = data.getString("ending_date").replace("-","/");
                            leaves.add(new LeaveHistroyUserModel(Integer.toString(leave_id),leave_type,starting_date + " - " + ending_date,Integer.parseInt(leave_status)));
                            binding.noItem.setVisibility(View.INVISIBLE);
                            binding.noFoundText.setVisibility(View.INVISIBLE);
                        }
                    }
                    else{
                        binding.leaveContainer.setVisibility(View.INVISIBLE);
                        hideLoader();
                        binding.noItem.setVisibility(View.VISIBLE);
                        binding.noFoundText.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                LeaveHistroyUserManager adapter = new LeaveHistroyUserManager(Leave_Histroy_User.this,leaves);
                binding.leaveContainer.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                hideLoader();
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
                map.put("student_id",session.getString("id","1"));
                map.put("getAllLeaves","1");
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

    public void showLoader() {
        binding.loader.setVisibility(View.VISIBLE);
        binding.leaveContainer.setVisibility(View.INVISIBLE);
    }

    public void hideLoader() {
        binding.loader.setVisibility(View.INVISIBLE);
        binding.leaveContainer.setVisibility(View.VISIBLE);
    }
}