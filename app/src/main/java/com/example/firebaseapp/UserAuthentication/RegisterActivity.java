package com.example.firebaseapp.UserAuthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.firebaseapp.MainActivity;
import com.example.firebaseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText registerEmailET, registerPasswordET;
    MaterialButton btnRegister;
    TextView tvLoginHere;

    FirebaseAuth firebaseAuth;

    //SharedPreferences for darkMode
    SharedPreferences sharedPreferencesDarkMode;
    SharedPreferences.Editor sharedPreferencesEditorDarkMode;
    boolean isNightModeOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //making notification bar transparent
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_register);
        //make the notificationBar transparent
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        //handle the view components
        this.registerEmailET = findViewById(R.id.registerEmailET);
        this.registerPasswordET = findViewById(R.id.registerPasswordET);
        this.btnRegister = findViewById(R.id.btnRegister);
        this.tvLoginHere = findViewById(R.id.tvLoginHere);

        //handle the Firebase Authentication configuration
        this.firebaseAuth = FirebaseAuth.getInstance();

        //handle the darkMode
        this.sharedPreferencesDarkMode = getSharedPreferences("DarkMode", 0);
        this.sharedPreferencesEditorDarkMode = this.sharedPreferencesDarkMode.edit();
        this.isNightModeOn = this.sharedPreferencesDarkMode.getBoolean("nightMode", false);
        if (isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        //handle the register button click
        this.btnRegister.setOnClickListener(v->{
            registerUserToFirebase();
        });

        //handle the Login here button click
        this.tvLoginHere.setOnClickListener(v->{
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUserToFirebase() {
        String email = Objects.requireNonNull(this.registerEmailET.getText()).toString().trim();
        String password = Objects.requireNonNull(this.registerPasswordET.getText()).toString().trim();

        if (email.isEmpty()){
            this.registerEmailET.setError("Email cannot be empty!");
            this.registerEmailET.requestFocus();
        }else if (password.isEmpty()){
            this.registerPasswordET.setError("Password cannot be empty!");
            this.registerPasswordET.requestFocus();
        }else{
            this.firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "User registration successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(RegisterActivity.this, "Registration error!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //Get the current user
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = this.firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}