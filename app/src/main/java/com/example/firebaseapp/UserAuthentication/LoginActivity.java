package com.example.firebaseapp.UserAuthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.firebaseapp.MainActivity;
import com.example.firebaseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.StringTokenizer;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText loginEmailET, loginPasswordET;
    MaterialButton btnLogin;
    TextView tvRegisterHere;

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
        this.setContentView(R.layout.activity_login);
        //make the notificationBar transparent
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        //handle view components
        this.loginEmailET = findViewById(R.id.loginEmailET);
        this.loginPasswordET = findViewById(R.id.loginPasswordET);
        this.tvRegisterHere = findViewById(R.id.tvRegisterHere);
        this.btnLogin = findViewById(R.id.btnLogin);

        //handle Firebase Authentication process
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

        //handle the login button click
        this.btnLogin.setOnClickListener(v->{
            loginUserToFirebase();
        });

        //handle the registerHere button click
        this.tvRegisterHere.setOnClickListener(v->{
            Intent intent = new Intent(this,RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loginUserToFirebase() {
        String email = Objects.requireNonNull(this.loginEmailET.getText()).toString().trim();
        String password = Objects.requireNonNull(this.loginPasswordET.getText()).toString().trim();

        if (email.isEmpty()){
            this.loginEmailET.setError("Email cannot be empty!");
            this.loginEmailET.requestFocus();
        }else if (password.isEmpty()){
            this.loginPasswordET.setError("Password cannot be empty!");
            this.loginPasswordET.requestFocus();
        }else{
            this.firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "User login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(LoginActivity.this, "Login error!", Toast.LENGTH_SHORT).show();
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