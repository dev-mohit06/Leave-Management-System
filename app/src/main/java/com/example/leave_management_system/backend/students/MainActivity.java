package com.example.leave_management_system.backend.students;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.leave_management_system.R;
import com.example.leave_management_system.adapters.NavManager;
import com.example.leave_management_system.databinding.MainUsersBinding;

public class MainActivity extends AppCompatActivity {

    MainUsersBinding binding;

    Fragment[] fragments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //create a fragments array to easy the logic of fragment adapter
        fragments = new Fragment[]{
                new Dashboard(),
                new Profile(),
                new About(),
        };

        //create an object of NavManager class it is a fragment manager because we are using bottom nav view
        NavManager navManager = new NavManager(getSupportFragmentManager(),fragments);
        binding.fragmentParent.setAdapter(navManager);

        //setting up with nav hovers
        binding.fragmentParent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                binding.nav.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        binding.nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.dashboard){
                binding.fragmentParent.setCurrentItem(0);
            }else if(id == R.id.profile){
                binding.fragmentParent.setCurrentItem(1);
            }else{
                binding.fragmentParent.setCurrentItem(2);
            }
            return  false;
        });
    }
}