package com.example.firebaseapp.Activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.Objects;
import com.example.firebaseapp.R;
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.google.android.material.appbar.MaterialToolbar;


public class ViewPostActivity extends AppCompatActivity {

    MaterialToolbar customAppBarView;
    PorterShapeImageView postImageIV;
    TextView postTitleTV, postBodyTV;
    TextView postTime, postDate, postAuthor;
    SharedPreferences accessDarkModeData;
    SharedPreferences.Editor editorDarkModeData;
    boolean isTheDarkModeOn;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        //custom app bar
        //CustomAppBar
        this.customAppBarView = findViewById(R.id.customAppBarView);
        setSupportActionBar(this.customAppBarView);


        //The back arrow
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Post Details");

        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString("title");
        String author = bundle.getString("author");
        String image = bundle.getString("imageUrl");
        String time = bundle.getString("time");
        String body = bundle.getString("body");
        String date = bundle.getString("date");

        //get the components IDs
        this.postImageIV = findViewById(R.id.postImageIV);
        this.postTitleTV = findViewById(R.id.postTitleTV);
        this.postBodyTV = findViewById(R.id.postBodyTV);
        this.postAuthor = findViewById(R.id.postAuthor);
        this.postDate = findViewById(R.id.postDate);
        this.postTime = findViewById(R.id.postTime);

        //set the components
        this.postTitleTV.setText(title);
        this.postAuthor.setText(author);
        this.postTime.setText(time);
        this.postDate.setText(date);
        this.postBodyTV.setText(body);
        this.postImageIV.setClipToOutline(true);
        Glide.with(this).load(image).into(this.postImageIV);


        //Handle the DarkMode stuff
        this.accessDarkModeData = getSharedPreferences("DarkMode",0);
        this.editorDarkModeData = this.accessDarkModeData.edit();
        this.isTheDarkModeOn = this.accessDarkModeData.getBoolean("nightMode",false);
    }

    //adding the menu to the toolbar
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
                editorDarkModeData.putBoolean("nightMode", true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editorDarkModeData.apply();
                break;

            //light mode
            case R.id.menuLightMode:
                editorDarkModeData.putBoolean("nightMode", false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editorDarkModeData.apply();
                break;

            case R.id.addUser:
                Intent intent = new Intent(this, AddPostActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
