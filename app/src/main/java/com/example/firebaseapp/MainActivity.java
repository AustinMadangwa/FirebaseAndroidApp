package com.example.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.firebaseapp.Activities.AddPostActivity;
import com.example.firebaseapp.Adapters.DisplayDataAdapter;
import com.example.firebaseapp.Data.User;
import com.example.firebaseapp.UserAuthentication.LoginActivity;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Objects;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    MaterialToolbar mainCustomAppBar;
    DrawerLayout drawerLayoutMain;
    TextView userEmailTv;
    NavigationView navigationViewMain;
    String userEmail;

    //SharedPreferences for darkMode
    SharedPreferences sharedPreferencesDarkMode;
    SharedPreferences.Editor sharedPreferencesEditorDarkMode;
    boolean isNightModeOn;

    //Firebase Authentication attributes
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    //Firebase Database
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseRecyclerOptions<User> options;

    //The RecyclerView Adapter
    RecyclerView recyclerView;
    DisplayDataAdapter adapter;

    //header view controls
    View headerView;


    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //CustomAppBar
        this.mainCustomAppBar = findViewById(R.id.mainCustomAppBar);
        setSupportActionBar(this.mainCustomAppBar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("User Posts");

        //The Drawer layout
        this.drawerLayoutMain = findViewById(R.id.drawerLayoutMain);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, this.drawerLayoutMain, this.mainCustomAppBar, R.string.nav_open, R.string.nav_close);
        toggle.setDrawerSlideAnimationEnabled(true);
        this.drawerLayoutMain.addDrawerListener(toggle);
        toggle.syncState();


        //The navigation drawer
        this.navigationViewMain = findViewById(R.id.navigationViewMain);
        this.navigationViewMain.setNavigationItemSelectedListener(this);


        //handle the database configurations
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.databaseReference = this.firebaseDatabase.getReference().child("Users");
        this.options = new FirebaseRecyclerOptions.Builder<User>().setQuery(this.databaseReference, User.class).build();
        this.firebaseAuth = FirebaseAuth.getInstance();


        //handle the recyclerView adapter
        this.recyclerView = findViewById(R.id.DisplayDataRV);
        this.recyclerView.hasFixedSize();
        this.recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        this.adapter = new DisplayDataAdapter(this.options, this);
        this.recyclerView.setAdapter(this.adapter);

        //handle the darkMode
        this.sharedPreferencesDarkMode = getSharedPreferences("DarkMode", 0);
        this.sharedPreferencesEditorDarkMode = this.sharedPreferencesDarkMode.edit();
        this.isNightModeOn = this.sharedPreferencesDarkMode.getBoolean("nightMode", false);
        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    //The menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.darkmode_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Menu clicks
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            //dark mode
            case R.id.menuDarkMode:
                sharedPreferencesEditorDarkMode.putBoolean("nightMode", true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                sharedPreferencesEditorDarkMode.apply();
                break;

            //light mode
            case R.id.menuLightMode:
                sharedPreferencesEditorDarkMode.putBoolean("nightMode", false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                sharedPreferencesEditorDarkMode.apply();
                break;

            case R.id.addUser:
                Intent intent = new Intent(this, AddPostActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    //Get the current user
    @Override
    protected void onStart() {
        super.onStart();
        this.adapter.startListening();
        this.firebaseUser = this.firebaseAuth.getCurrentUser();
        if (this.firebaseUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            this.userEmail = this.firebaseUser.getEmail();
            this.headerView = this.navigationViewMain.getHeaderView(0);
            StringTokenizer st = new StringTokenizer(userEmail, "@");
            String s2 = st.nextToken();
            this.userEmailTv = headerView.findViewById(R.id.userEmailAvatar);
            this.userEmailTv.setText(s2);
        }
    }

    //ToDo: create the logout option
    //SignOut the Current User
    public void signOutTheUserFromFirebase(FirebaseAuth myAuth) {
        myAuth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    //Handle the drawer layout clicks
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Menu menu = this.navigationViewMain.getMenu();
        MenuItem darkThemeMenuItem = menu.findItem(R.id.nav_drawer_dark_theme);
        switch (item.getItemId()) {
            //Refresh the view
            case R.id.nav_drawer_add_new_post:
                Intent intent = new Intent(this,AddPostActivity.class);
                startActivity(intent);
                break;

            //handle the dark theme
            case R.id.nav_drawer_dark_theme:
                if (isNightModeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    this.sharedPreferencesEditorDarkMode.putBoolean("nightMode",false);
                    darkThemeMenuItem.setTitle("Dark Theme");
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    this.sharedPreferencesEditorDarkMode.putBoolean("nightMode",true);
                    darkThemeMenuItem.setTitle("Light Theme");
                }
                this.sharedPreferencesEditorDarkMode.apply();
                this.navigationViewMain.setNavigationItemSelectedListener(this);
                break;

            //logout the user
            case R.id.nav_drawer_logout:
                signOutTheUserFromFirebase(this.firebaseAuth);
                break;
        }
        this.drawerLayoutMain.closeDrawer(GravityCompat.START);
        return false;
    }

    //handle the back button press when the drawer layout is open
    @Override
    public void onBackPressed() {
        if (this.drawerLayoutMain.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayoutMain.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        this.adapter.stopListening();
    }
}


