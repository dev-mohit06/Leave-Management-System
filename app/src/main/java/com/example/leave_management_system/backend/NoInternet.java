package com.example.leave_management_system.backend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.leave_management_system.databinding.ActivityNoInternetBinding;

public class NoInternet extends AppCompatActivity {

    ActivityNoInternetBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoInternetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.reconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoInternet.this, Splash.class));
                finish();
            }
        });
    }
}