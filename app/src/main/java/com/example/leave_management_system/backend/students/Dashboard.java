package com.example.leave_management_system.backend.students;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.leave_management_system.adapters.users.adapters.RecentLeaveManager;
import com.example.leave_management_system.databinding.FragementDashboardUsersBinding;
import com.example.leave_management_system.models.BarrerModel;
import com.example.leave_management_system.models.users.model.RecentLeaveModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Dashboard extends Fragment {

    public final String API_URL = BarrerModel.API_BASE_URL + "student/get-leaves";
    private Context context;
    private FragementDashboardUsersBinding binding;
    private SharedPreferences session;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private ArrayList<RecentLeaveModel> leaveData;
    private ArrayList<RecentLeaveModel> cacheLeaveData;


    @Override
    public void onResume() {
        loadRecentLeaves();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragementDashboardUsersBinding.inflate(inflater, container, false);
        // Fetch the value from shared preferences
        session = context.getSharedPreferences("session", Context.MODE_PRIVATE);

        // Dashboard navigation
        binding.newLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, New_Leave.class));
            }
        });

        binding.manageLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Manage_Leaves_Users.class));
            }
        });

        binding.leaveHistroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Leave_Histroy_User.class));
            }
        });


        // Setting up the greeting message.
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH", Locale.getDefault());
        int hour = Integer.parseInt(hourFormat.format(currentTime));
        String greeting;
        if (hour >= 0 && hour < 12) {
            greeting = "Good Morning,";
        }
        else if (hour >= 12 && hour < 17) {
            greeting = "Good Afternoon,";
        }
        else {
            greeting = "Good Evening,";
        }
        binding.greetings.setText(greeting);
        binding.fullname.setText(session.getString("fullname", "fullname"));


        binding.recentLeave.setLayoutManager(new LinearLayoutManager(context));

        leaveData = new ArrayList<>();
        cacheLeaveData = new ArrayList<>();
        loadRecentLeaves();

        return binding.getRoot();
    }

    private void loadRecentLeaves(){
        binding.noItem.setVisibility(View.INVISIBLE);
        binding.noFoundText.setVisibility(View.INVISIBLE);
        showLoader();
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        StringRequest request = new StringRequest(Request.Method.POST, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    leaveData.clear();
                    cacheLeaveData.clear();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray leaveArray = jsonObject.getJSONArray("data");

                    if (!(leaveArray.length() <= 0)){
                        for (int i = 0; i < leaveArray.length(); i++) {
                            JSONObject data = leaveArray.getJSONObject(i);

                            int leave_id = data.getInt("id");
                            String leave_type = data.getString("leave_type");
                            String leave_duration = DateFormatterForRecentLeave.merger(data.getString("starting_date"), data.getString("ending_date"));
                            int leave_status = data.getInt("leave_status");

                            leaveData.add(new RecentLeaveModel(leave_id,leave_type,leave_duration,leave_status));
                        }
                        RecentLeaveManager adapter = new RecentLeaveManager(context,leaveData);
                        binding.recentLeave.setAdapter(adapter);
                        hideLoader();
                        binding.noItem.setVisibility(View.INVISIBLE);
                        binding.noFoundText.setVisibility(View.INVISIBLE);
                    }else{
                        binding.loader.setVisibility(View.INVISIBLE);
                        binding.recentLeave.setVisibility(View.INVISIBLE);
                        binding.noItem.setVisibility(View.VISIBLE);
                        binding.noFoundText.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing error
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

                map.put("student_id",session.getString("id","1"));
                map.put("getRecentLeaves","1");

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

    static class DateFormatterForRecentLeave {
        private static String getOnlyDate(String dateString) {

            String input = dateString; // Replace this with your input date string

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String from = "";

            try {
                Date date = inputFormat.parse(input);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd", Locale.US);
                from = outputFormat.format(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return from;
        }

        private static String getMonthNameFromDateString(String dateString) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date date = inputFormat.parse(dateString);
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM", Locale.US);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return "Invalid Date";
            }
        }

        private static String merger(String startDate, String endDate) {

            String from = getOnlyDate(startDate);
            String to = getOnlyDate(endDate);

            String month = getMonthNameFromDateString(startDate);
            String year = getYearFromDateString(startDate);

            return from + " - " + to + " " + month + " " + year;
        }

        private static String getYearFromDateString(String dateString) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date date = inputFormat.parse(dateString);
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy", Locale.US);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return "Invalid Date";
            }
        }
    }

    private void showLoader() {
        binding.loader.setVisibility(View.VISIBLE);
        binding.recentLeave.setVisibility(View.INVISIBLE);
    }

    public void hideLoader() {
        binding.loader.setVisibility(View.INVISIBLE);
        binding.recentLeave.setVisibility(View.VISIBLE);
    }
}
