package com.example.leave_management_system.backend.students;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.leave_management_system.R;
import com.example.leave_management_system.databinding.NewLeaveUsersBinding;
import com.example.leave_management_system.models.BarrerModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class New_Leave extends AppCompatActivity {

    public ArrayList<String> leaveOptions;
    public ArrayList<String> reasonOptions;


    NewLeaveUsersBinding binding;

    String startingDate,endingDate,details,leaveType,leaveReason;

    private final String API_URL = BarrerModel.API_BASE_URL + "student/apply-for-leave";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = NewLeaveUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        leaveOptions = new ArrayList<>();
        leaveOptions.add(getString(R.string.select_leave_type));
        leaveOptions.add("Marriage Leave");
        leaveOptions.add("Medical Leave");
        leaveOptions.add("Outdoor Leave");
        leaveOptions.add("Other Leave");
        ArrayAdapter leaveTypeAdapter = new ArrayAdapter(New_Leave.this, android.R.layout.simple_spinner_dropdown_item,leaveOptions);
        binding.leaveType.setAdapter(leaveTypeAdapter);
        binding.leaveType.setSelected(false);

        binding.startingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int date = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(New_Leave.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                        binding.startingDate.setText(selectedDate);
                    }
                },year,month,date);

                datePickerDialog.show();
            }
        });
        binding.endingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int date = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(New_Leave.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                        binding.endingDate.setText(selectedDate);
                    }
                },year,month,date);

                datePickerDialog.show();
            }
        });

        reasonOptions = new ArrayList<>();
        reasonOptions.add(getString(R.string.select_reason));
        reasonOptions.add("Social");
        reasonOptions.add("Personal");
        reasonOptions.add("Medical");
        reasonOptions.add("Office work");
        reasonOptions.add("Part in event");



        ArrayAdapter reasonAdapter = new ArrayAdapter(New_Leave.this, android.R.layout.simple_spinner_dropdown_item,reasonOptions);
        binding.reason.setAdapter(reasonAdapter);
        binding.reason.setSelected(false);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    apply();
                }
            }
        });

        binding.applyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    apply();
                }
            }
        });
    }

    private boolean validate(){
        startingDate = binding.startingDate.getText().toString();
        endingDate = binding.endingDate.getText().toString();
        details = binding.details.getText().toString();
        leaveType = binding.leaveType.getSelectedItem().toString();
        leaveReason = binding.reason.getSelectedItem().toString();

        AlertDialog.Builder message = new AlertDialog.Builder(New_Leave.this);

        if (startingDate.equals("") || endingDate.equals("") || details.equals("") || leaveType.equals(getString(R.string.select_leave_type)) || leaveReason.equals(getString(R.string.select_reason))){
            message.setTitle("All the fields require");
            message.setMessage("you can't leave any of the fields");
            message.show();
            return false;
        }else {
            return true;
        }
    }

    private void showLoader() {
        binding.applyText.setVisibility(View.INVISIBLE);
        binding.loader.setVisibility(View.VISIBLE);
        binding.apply.setClickable(false);
        binding.back.setVisibility(View.INVISIBLE);
    }

    public void hideLoader() {
        binding.loader.setVisibility(View.INVISIBLE);
        binding.applyText.setVisibility(View.VISIBLE);
        binding.apply.setClickable(true);
        binding.back.setVisibility(View.VISIBLE);
    }

    private void apply(){

        showLoader();

        RequestQueue queue = Volley.newRequestQueue(New_Leave.this);

        StringRequest request = new StringRequest(Request.Method.POST, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    if (!jsonResponse.isNull("message")){
                        hideLoader();
                        AlertDialog.Builder message = new AlertDialog.Builder(New_Leave.this);
                        message.setTitle("Leave applied successfully");
                        message.setMessage("your leave applied successfully wait for it's approval");
                        message.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        message.show();
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(New_Leave.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                hideLoader();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap<String,String>();

                SharedPreferences session = getSharedPreferences("session",MODE_PRIVATE);
                SharedPreferences session_helper = getSharedPreferences("session_helper",MODE_PRIVATE);

                data.put("student_id",session.getString("id","1"));
                data.put("faculty_id",session_helper.getString("faculty_id","1"));
                data.put("leave_type",leaveType.toLowerCase());
                data.put("starting_date",startingDate.toLowerCase());
                data.put("ending_date",endingDate.toLowerCase());
                data.put("reason",leaveReason.toLowerCase());
                data.put("details",details);
                return data;
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
}