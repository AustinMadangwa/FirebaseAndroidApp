package com.example.firebaseapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.firebaseapp.Data.User;
import com.example.firebaseapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class AddPostActivity extends AppCompatActivity {

    MaterialToolbar customAppBar;
    TextInputEditText postTitleET, postAuthorET, postBodyET;
    MaterialButton btnChooseImage, btnUploadUserData;
    ImageView imagePreviewIV;
    String date,time;

    //The firebase database references
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    User user;

    //Firebase Storage attributes
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    //URI
    Uri filePathURI;
    //Image Code
    int IMAGE_REQUEST_CODE = 777;
    //ProgressDialog
    ProgressDialog progressDialog;
    //Some static data
    String STORAGE_PATH = "PostImages/";
    String DATABASE_PATH = "Users";

    //SharedPreferences for darkMode
    SharedPreferences sharedPreferencesDarkMode;
    SharedPreferences.Editor sharedPreferencesEditorDarkMode;
    boolean isNightModeOn;


    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

        //CustomAppBar
        this.customAppBar = findViewById(R.id.customAppBar);
        setSupportActionBar(this.customAppBar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add New Post");
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize the components
        this.btnChooseImage = findViewById(R.id.btnChooseImage);
        this.btnUploadUserData = findViewById(R.id.btnUploadUserData);
        this.postTitleET = findViewById(R.id.postTitleET);
        this.postBodyET = findViewById(R.id.postBodyET);
        this.postAuthorET = findViewById(R.id.postAuthorET);
        this.imagePreviewIV = findViewById(R.id.imagePreviewIV);
        this.imagePreviewIV.setVisibility(View.GONE);

        //Handle the database configurations
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.databaseReference = firebaseDatabase.getReference().child(DATABASE_PATH);
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.storageReference = this.firebaseStorage.getReference();
        this.progressDialog = new ProgressDialog(this);
        this.user = new User();

        //the button that uploads the data to the Firebase Realtime database
        this.btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Please Select an Image"), IMAGE_REQUEST_CODE);
        });

        //Upload the post to Firebase
        this.btnUploadUserData.setOnClickListener(v -> {
            //calling the method to upload the user post
            uploadImageFileTOFirebaseStorage();
        });

        //handle the database configurations
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.databaseReference = this.firebaseDatabase.getReference().child("Users");


        //handle the darkMode
        this.sharedPreferencesDarkMode = getSharedPreferences("DarkMode", 0);
        this.sharedPreferencesEditorDarkMode = this.sharedPreferencesDarkMode.edit();
        this.isNightModeOn = this.sharedPreferencesDarkMode.getBoolean("nightMode", false);

    }

    //The menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_post_menu, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }


    //A method to get the selected image file extension from File Path URI
    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        //Returning the file extension
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //Uploading the image to Firebase Storage
    public void uploadImageFileTOFirebaseStorage() {
        //Getting the image name from the EditText and store into a string variable
        String body = Objects.requireNonNull(postBodyET.getText()).toString().trim();
        String author = Objects.requireNonNull(postAuthorET.getText()).toString().trim();
        String title = Objects.requireNonNull(postTitleET.getText()).toString().trim();        //check if the filePathURI is empty
        if (this.filePathURI != null && !title.isEmpty() && !author.isEmpty() && !body.isEmpty()) {

            //Setting the progressDialog Title and showing it
            this.progressDialog.setTitle("New Post");
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.progressDialog.setMessage("Uploading post data...");
            this.progressDialog.show();

            //Creating second Storage Reference
            StorageReference storageReference1 = this.storageReference.child(STORAGE_PATH + System.currentTimeMillis() + "." + getFileExtension(filePathURI));

            //Adding addOnSuccessListener to second StorageReference
            storageReference1.putFile(filePathURI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Hiding the progressDialog
                            progressDialog.dismiss();
                            //Showing a toast message for a successful image upload
                            Toast.makeText(AddPostActivity.this, "Post uploaded successfully", Toast.LENGTH_SHORT).show();

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            //noinspection StatementWithEmptyBody
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();
                            String imageLink = null;
                            if (downloadUrl != null) {
                                imageLink = downloadUrl.toString();
                            }

                            getCurrentDateAndTime();

                            User imageUploadInfo = new User(title, author, date, time, body,imageLink);
                            //getting the image upload ID.
                            String imageUploadID = databaseReference.push().getKey();
                            assert imageUploadID != null;
                            databaseReference.child(imageUploadID).setValue(imageUploadInfo);
                        }
                    })
                    //On Failure to upload
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Hiding the progressDialog
                            progressDialog.dismiss();
                            //Showing exception error message
                            Toast.makeText(AddPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    //On Progress Change Listener
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            //Setting the progressDialog Title and showing it
                            progressDialog.setTitle("New Post");
                        }
                    });

        } else {
            Toast.makeText(this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
        }
    }

    //For Uploading the image and setting it to the ImageView for previewing
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            this.filePathURI = data.getData();

            try {
                //getting selected image into Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathURI);

                //Set the selected image into the imageView
                this.imagePreviewIV.setClipToOutline(true);
                this.imagePreviewIV.setImageBitmap(bitmap);
                this.imagePreviewIV.setVisibility(View.VISIBLE);

                //Change the button text
                this.btnChooseImage.setText(R.string.btn_image_selected);
            } catch (IOException exception) {
                Toast.makeText(this, "Error selecting the image!", Toast.LENGTH_SHORT).show();
            }
        }
    }



    //ToDo: create two global variables for date and time
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getCurrentDateAndTime(){
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");
        String formattedDate = myDateObj.format(myFormatObj);
        String[] splitStr = formattedDate.split("\\s+");
        this.date = splitStr[0];
        this.time = splitStr[1];
    }


}