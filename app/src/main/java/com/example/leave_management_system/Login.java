package com.example.leave_management_system;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Typeface;
import android.graphics.fonts.FontFamily;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import com.example.leave_management_system.databinding.ActivityLoginBinding;

import org.w3c.dom.Text;

public class Login extends AppCompatActivity {

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

                if (passwordToggleCount == 0){
                    binding.passwordToggle.setImageResource(R.drawable.eye_open);
                    binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordToggleCount++;
                }else{
                    binding.passwordToggle.setImageResource(R.drawable.eye_close);
                    binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordToggleCount--;
                }

                Typeface fontFamily = ResourcesCompat.getFont(Login.this,R.font.urbanist_medium);
                binding.password.setTypeface(fontFamily);
                binding.password.setSelection(selectionStart, selectionEnd);
            }
        });
    }
}