package com.example.ucm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.ucm.Fragments.Analytics;
import com.example.ucm.Fragments.Home;

import me.ibrahimsn.lib.SmoothBottomBar;

public class HomePage extends AppCompatActivity {
    SmoothBottomBar smoothBottomBar;
    FrameLayout frameLayout;
    Fragment selectedFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        //
        frameLayout = findViewById(R.id.fragment_container);
        //
        selectedFragment =  new Home();;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit();

        smoothBottomBar = findViewById(R.id.bottom_navigation);
        smoothBottomBar.setOnItemSelectedListener(i -> {
            selectedFragment = new Home();
            switch (i) {
                case 0:
                    selectedFragment = new Home();
                    break;
                case 1:
                    selectedFragment = new Analytics();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();
        });
    }
}