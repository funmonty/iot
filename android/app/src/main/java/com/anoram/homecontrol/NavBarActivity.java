package com.anoram.homecontrol;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class NavBarActivity extends AppCompatActivity{
    ImageView profImage, editIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_bar);
        getSupportActionBar().hide();


        //loading the default fragment
        if (savedInstanceState == null) {

            loadFragment(new HomeFragment());
        }

        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.home_nav:
                        Log.d("hello","hello");
                        loadFragment(new HomeFragment());
                        return true;
                    case R.id.device_nav:
                        loadFragment(new DeviceManagementFragment());
                        return true;
                    case R.id.user_nav:
                        loadFragment(new UserManagementFragment());
                        return true;
                }
                return false;
            }
        });


    }
    private void loadFragment(Fragment fragment) {
        //switching fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_containe, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
