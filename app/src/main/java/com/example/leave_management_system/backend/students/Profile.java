package com.example.leave_management_system.backend.students;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.leave_management_system.backend.Login;
import com.example.leave_management_system.databinding.FragmentProfileUsersBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class Profile extends Fragment {


    FragmentProfileUsersBinding binding;
    SharedPreferences session;
    SharedPreferences session_helper;

    @Override
    public void onResume() {
        super.onResume();

        // Update the profile picture using data binding
        Glide
                .with(this)
                .load(session.getString("profile_picture", "https://api.smartmohit.com/images/users/deafult.jpg"))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        binding.loader.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Failed to load profile picture", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        binding.loader.setVisibility(View.INVISIBLE);
                        binding.profilePicture.setImageDrawable(resource);
                        return false;
                    }
                })
                .into(binding.profilePicture);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileUsersBinding.inflate(inflater, container, false);
        //fetch the value form shared preferences
        session = getContext().getSharedPreferences("session", Context.MODE_PRIVATE);
        session_helper = getContext().getSharedPreferences("session_helper", Context.MODE_PRIVATE);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        JSONObject data = new JSONObject();
        try {
            data.put("class_id", session.getString("class_id", "class_id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        binding.fullname.setText(session.getString("fullname", "fullname"));
        binding.enrollment.setText(session.getString("enrollment", "enrollment"));
        Glide
                .with(getContext())
                .load(session.getString("profile_picture", "https://api.smartmohit.com/images/users/deafult.jpg"))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        binding.loader.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Failed to page load", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        binding.loader.setVisibility(View.INVISIBLE);
                        binding.profilePicture.setImageDrawable(resource);
                        return false;
                    }
                })
                .into(binding.profilePicture);

        binding.email.setText(session.getString("email", "your email"));
        binding.phoneNuber.setText(session.getString("mobile_number", "your phone number"));
        binding.councilorName.setText(session_helper.getString("faculty_name", "class councilor name"));
        binding.councilorPhoneNumber.setText(session_helper.getString("mobile_number", "class councilor mobile number"));
        binding.councilorEmail.setText(session_helper.getString("faculty_email", "class councilor phone number"));

        binding.councilorEmailNumberContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{session_helper.getString("faculty_email", "example@gmail.com")});
                Intent.createChooser(intent, "Select your mail application");
                startActivity(intent);
            }
        });

        binding.councilorPhoneNumberContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + session_helper.getString("mobile_number", "1234567890")));
                startActivity(intent);
            }
        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fetching the data
                SharedPreferences session = getContext().getSharedPreferences("session", Context.MODE_PRIVATE);
                SharedPreferences.Editor session_editor = session.edit();
                SharedPreferences session_helper = getContext().getSharedPreferences("session_helper", Context.MODE_PRIVATE);
                SharedPreferences.Editor session_helper_editor = session_helper.edit();

                //destroying the data
                session_editor.clear();
                session_helper_editor.clear();

                //commit the data
                session_editor.apply();
                session_helper_editor.apply();

                //redirect
                startActivity(new Intent(getContext(), Login.class));
                getActivity().finish();
            }
        });

        binding.editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Edit_Profile.class);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }
}