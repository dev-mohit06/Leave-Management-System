package com.example.leave_management_system.backend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.leave_management_system.R;
import com.example.leave_management_system.databinding.ActivityLoginBinding;
import com.example.leave_management_system.models.BarrerModel;
import com.example.leave_management_system.backend.students.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private final String API_URL = "https://api.smartmohit.com/api/v1/authorization/login";
    private final String BEARER_TOKEN = BarrerModel.BEARER_TOKEN;
    ActivityLoginBinding binding;
    private int passwordToggleCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.passwordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectionStart = binding.password.getSelectionStart();
                int selectionEnd = binding.password.getSelectionEnd();

                if (passwordToggleCount == 0) {
                    binding.passwordToggle.setImageResource(R.drawable.eye_open);
                    binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordToggleCount++;
                } else {
                    binding.passwordToggle.setImageResource(R.drawable.eye_close);
                    binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordToggleCount--;
                }

                Typeface fontFamily = ResourcesCompat.getFont(Login.this, R.font.urbanist_medium);
                binding.password.setTypeface(fontFamily);
                binding.password.setSelection(selectionStart, selectionEnd);
            }
        });

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }

        });
        binding.loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        if (session.getBoolean("remember_me", false)) {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }
    }

    protected void login() {
        AlertDialog.Builder messageBuilder = new AlertDialog.Builder(Login.this);

        showLoader();

        int count = 0;

        if (binding.email.getText().toString().equals("")) {
            messageBuilder.setTitle("Email required");
            messageBuilder.setMessage("Enter your email to continue.");
            messageBuilder.show();
            hideLoader();
            count++;
        }

        if (!validator(binding.email) && count == 0) {
            messageBuilder.setTitle("Invalid Email");
            messageBuilder.setMessage("Enter the valid email address.");
            messageBuilder.show();
            hideLoader();
            count++;
        }

        if (binding.password.getText().toString().equals("") && count == 0) {
            messageBuilder.setTitle("Password required");
            messageBuilder.setMessage("Enter your password to continue.");
            messageBuilder.show();
            hideLoader();
            count++;
        }

        if (count == 0) {

            RequestQueue queue = Volley.newRequestQueue(Login.this);

            JSONObject credential = new JSONObject();
            try {
                credential.put("email", binding.email.getText().toString());
                credential.put("password", binding.password.getText().toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, API_URL, credential,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hideLoader();
                            try {
                                if (response.isNull("message")) {
                                    //fetching json objects form the requests
                                    JSONObject user = response.getJSONObject("data");


                                    //get the current user role
                                    String role = user.getString("role");

                                    SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
                                    SharedPreferences.Editor session_editor = session.edit();

                                    session_editor.clear();
                                    session_editor.putBoolean("remember_me", binding.RememberMe.isChecked());

                                    //putting general data for all the user to avoid code reception
                                    session_editor.putString("email", user.getString("email"));
                                    session_editor.putString("mobile_number", user.getString("mobile_number"));
                                    session_editor.putString("profile_picture", user.getString("profile_picture"));
                                    session_editor.putString("role", user.getString("role"));

                                    if (role.equals("student")) {
                                        //puting the current user data in session
                                        session_editor.putString("id", user.getString("id"));
                                        session_editor.putString("enrollment", user.getString("enrollment"));
                                        session_editor.putString("fullname", user.getString("fullname"));
                                        session_editor.putString("roll_number", user.getString("roll_number"));
                                        session_editor.putString("class_id", user.getString("class_id"));


                                        //create session helper for student.
                                        SharedPreferences session_helper = getSharedPreferences("session_helper", MODE_PRIVATE);
                                        SharedPreferences.Editor session_helper_editor = session_helper.edit();
                                        session_helper_editor.clear();

                                        //fetching faculty data for student because it's need in profile page and many places.
                                        JSONObject faculty = response.getJSONObject("faculty_data");

                                        //puting the current class councillor record in session_helper
                                        session_helper_editor.putString("faculty_id", faculty.getString("id"));
                                        session_helper_editor.putString("faculty_name", faculty.getString("faculty_name"));
                                        session_helper_editor.putString("faculty_email", faculty.getString("email"));
                                        session_helper_editor.putString("mobile_number", faculty.getString("mobile_number"));

                                        session_helper_editor.apply();
                                    } else {
                                        session_editor.putString("faculty_name", user.getString("faculty_name"));
                                    }
                                    session_editor.apply();

                                    if (role.equals("student")) {
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                        finish();
                                    } else if (role.equals("cc")) {
                                        //cc
                                    } else {
                                        //hod
                                    }
                                } else {
                                    if (response.getString("is_valid").equals("0") && response.getString("message").equals("Invalid Password")){
                                        messageBuilder.setTitle(response.getString("message"));
                                        messageBuilder.setMessage("Please enter the correct password");
                                        messageBuilder.show();
                                    }else{
                                        messageBuilder.setTitle(response.getString("m   essage"));
                                        messageBuilder.setMessage("Please enter the existing data");
                                        messageBuilder.show();
                                    }
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideLoader();
                            Toast.makeText(Login.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            // Handle the error
//                            Toast.makeText(Login.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + BEARER_TOKEN);
                    return headers;
                }
            };

            queue.add(loginRequest);
        }

    }

    protected boolean validator(EditText email) {
        return Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches();
    }

    private void showLoader() {
        binding.loginText.setVisibility(View.INVISIBLE);
        binding.loader.setVisibility(View.VISIBLE);
        binding.login.setClickable(false);
    }

    public void hideLoader() {
        binding.loader.setVisibility(View.INVISIBLE);
        binding.loginText.setVisibility(View.VISIBLE);
        binding.login.setClickable(true);
    }
}