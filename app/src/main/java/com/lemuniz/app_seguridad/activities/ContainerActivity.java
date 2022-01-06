package com.lemuniz.app_seguridad.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.lemuniz.app_seguridad.R;
import com.lemuniz.app_seguridad.fragments.HomeFragment;
import com.lemuniz.app_seguridad.fragments.ProfileFragment;
import com.lemuniz.app_seguridad.providers.AuthProvider;

public class ContainerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        mDrawerLayout = findViewById(R.id.drawerLayout);
        mNavigationView = findViewById(R.id.navView);
        mToolbar = findViewById(R.id.toolbar);

        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new HomeFragment()).commit();
        setTitle("Home");

        //setup Toolbar
        setSupportActionBar(mToolbar);

        mNavigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        selectItemNav(item);
        return true;
    }

    private void selectItemNav(MenuItem item) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch (item.getItemId()){
            case R.id.nav_home:
                ft.replace(R.id.frameLayout, new HomeFragment()).commit();
                break;
            case R.id.nav_profile:
                ft.replace(R.id.frameLayout, new ProfileFragment()).commit();
                break;
        }
        setTitle(item.getTitle());
        mDrawerLayout.closeDrawers();
    }
}