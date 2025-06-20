package com.example.argapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.argapp.Controllers.UserController;
import com.example.argapp.Fragments.Admin.AdminCouponsFragment;
import com.example.argapp.Fragments.Admin.AdminDashboardFragment;
import com.example.argapp.Fragments.Admin.AdminItemsFragment;
import com.example.argapp.Fragments.Admin.AdminOrdersFragment;
import com.example.argapp.Fragments.Admin.AdminReviewsFragment;
import com.example.argapp.Fragments.Admin.AdminUsersFragment;
import com.example.argapp.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        initViews();
        // Inside onCreate after initializing views
        NavController navController = Navigation.findNavController(this, R.id.admin_fragment_container);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
        setupNavigationDrawer();

        userController = new UserController();

        // Mặc định hiển thị Dashboard
        if (savedInstanceState == null) {
            loadFragment(new AdminDashboardFragment());
            navigationView.setCheckedItem(R.id.nav_admin_dashboard);
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        int itemId = item.getItemId();
        if (itemId == R.id.nav_admin_dashboard) {
            fragment = new AdminDashboardFragment();
        } else if (itemId == R.id.nav_admin_users) {
            fragment = new AdminUsersFragment();
        } else if (itemId == R.id.nav_admin_orders) {
            fragment = new AdminOrdersFragment();
        }else if (itemId == R.id.nav_admin_items) {
            fragment = new AdminItemsFragment();
        } else if (itemId == R.id.nav_admin_reviews) {
            fragment = new AdminReviewsFragment();
        } else if (itemId == R.id.nav_admin_coupons) {
            fragment = new AdminCouponsFragment();
        } else if (itemId == R.id.nav_admin_logout) {
            logout();
            return true;
        }

        if (fragment != null) {
            loadFragment(fragment);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.admin_fragment_container, fragment)
                .commit();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}