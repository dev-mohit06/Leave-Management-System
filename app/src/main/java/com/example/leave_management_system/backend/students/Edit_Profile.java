package com.example.leave_management_system.backend.students;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.leave_management_system.databinding.EditProfileUsersBinding;
import com.example.leave_management_system.models.BarrerModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Edit_Profile extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 1;
    private final String API_URL = BarrerModel.API_BASE_URL + "user/modify-user";
    private final String BEARER_TOKEN = BarrerModel.BEARER_TOKEN;
    EditProfileUsersBinding binding;

    SharedPreferences session;
    SharedPreferences.Editor session_editor;
    private Bitmap image;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = EditProfileUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //fetching the data from the SharedPreferences
        session = getSharedPreferences("session", Context.MODE_PRIVATE);
        session_editor = session.edit();

        binding.fullname.setText(session.getString("fullname", "fullname"));
        binding.enrollment.setText(session.getString("enrollment", "your enrollment"));

        //back button
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //load image in circular image view using glide it's help to fetch and load the image from the api
        Glide
                .with(Edit_Profile.this)
                .load(session.getString("profile_picture", "https://api.smartmohit.com/images/users/deafult.jpg"))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        binding.profileLoader.setVisibility(View.INVISIBLE);
                        Toast.makeText(Edit_Profile.this, "Failed to image loder load", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        binding.profileLoader.setVisibility(View.INVISIBLE);
                        binding.profilePicture.setImageDrawable(resource);
                        return false;
                    }
                })
                .into(binding.profilePicture);

        /**
         * TODO: the given setps is to get file form the device and send to the api
         * 1. fetch the image form the local device here we are using browseImages() function to open gallery.
         * 2. then we are overriding inbuilt method onActivityResult in this fetch the image form Uri and place in imageview and encode in Base64.
         * 3. encodedImage() is store the base64 encoded image to an encodedImage variable.
         * */
        binding.profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseImages();
            }
        });
        binding.camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseImages();
            }
        });


        //modify the changes using api
        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updateUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        binding.updateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updateUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void browseImages() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //without image cropping
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
//            Uri filepath = data.getData();
//            try {
//                InputStream inputStream = getContentResolver().openInputStream(filepath);
//                image = BitmapFactory.decodeStream(inputStream);
//                binding.profilePicture.setImageBitmap(image);
//
//                encodedImage();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    //with image cropping
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri filePath = data.getData();
            if (filePath != null) {
                // Start the image cropping activity
                startImageCropper(filePath);
            } else {
                Toast.makeText(this, "Failed to retrieve image", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            // Handle the result from the image cropping activity
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri croppedImageUri = result.getUri();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(croppedImageUri);
                    image = BitmapFactory.decodeStream(inputStream);
                    binding.profilePicture.setImageBitmap(image);
                    encodedImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Image cropping failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startImageCropper(Uri sourceUri) {
        CropImage.activity(sourceUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1) // You can set the desired aspect ratio
                .setAllowRotation(true)
                .start(this);
    }

    private void encodedImage() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);

        byte[] bytesOfImage = byteArrayOutputStream.toByteArray();
        encodedImage = android.util.Base64.encodeToString(bytesOfImage, Base64.DEFAULT);
    }

    private void showLoader() {
        binding.updateText.setVisibility(View.INVISIBLE);
        binding.loader.setVisibility(View.VISIBLE);
        binding.update.setClickable(false);
        binding.back.setVisibility(View.INVISIBLE);
    }

    public void hideLoader() {
        binding.loader.setVisibility(View.INVISIBLE);
        binding.updateText.setVisibility(View.VISIBLE);
        binding.update.setClickable(true);
        binding.back.setVisibility(View.VISIBLE);
    }

    private void updateUser() throws JSONException {
        AlertDialog.Builder message = new AlertDialog.Builder(Edit_Profile.this);

        String oldPassword = binding.oldPassword.getText().toString();
        String newPassword = binding.newPassword.getText().toString();
        String conPassword = binding.confirmPassword.getText().toString(); // Corrected field name


        int count = 0;

        JSONObject userdata = new JSONObject();
        userdata.put("user_id", "1");

        if (!oldPassword.equals("") && (newPassword.equals("") || conPassword.equals(""))) {
            message.setTitle("All fields required");
            message.setMessage("You can't leave any of these password fields empty");
            message.show();
            count++;
        }

        if (!newPassword.equals(conPassword) && count == 0) {
            message.setTitle("Confirm password mismatch");
            message.setMessage("Password cannot match confirm password");
            message.show();
            count++;
        }

        if (count == 0) {
            if (encodedImage == null && (oldPassword.equals("") && newPassword.equals("") && conPassword.equals(""))) {
                finish();
            } else {
                showLoader();
                RequestQueue queue = Volley.newRequestQueue(Edit_Profile.this);
                StringRequest request = new StringRequest(Request.Method.POST, API_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parse the JSON string into a JSON object
                            JSONObject jsonResponce = new JSONObject(response);

                            if (!jsonResponce.isNull("message")) {
                                message.setTitle("Incorrect password");
                                message.setMessage("Invalid password please enter the correct password");
                                message.show();
                                hideLoader();
                            } else {
                                JSONObject data = jsonResponce.getJSONObject("data");
                                session_editor.remove("profile_picture");
                                session_editor.putString("profile_picture", data.getString("profile_picture"));
                                session_editor.apply();
                                hideLoader();
                                finish();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError){
                            Toast.makeText(Edit_Profile.this, "Slow internet detect!!", Toast.LENGTH_SHORT).show();
                            message.setTitle("Slow internet detect!!");
                            message.setMessage("After logout changes might will reflect");
                            message.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    hideLoader();
                                    finish();
                                }
                            });
                            message.show();
                        }
                    }
                }) {
                    @NonNull
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();

                        map.put("user_id", session.getString("id", "1"));

                        if (encodedImage == null) {
                            map.put("old_password", oldPassword);
                            map.put("new_password", newPassword);
                        } else {
                            if (newPassword.equals("")) {
                                map.put("profile_picture", encodedImage);
                            } else {
                                map.put("old_password", oldPassword);
                                map.put("new_password", newPassword);
                                map.put("profile_picture", encodedImage);
                            }
                        }

                        return map;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + BEARER_TOKEN);
                        return headers;
                    }
                };

                queue.add(request);
            }
        }
    }
}