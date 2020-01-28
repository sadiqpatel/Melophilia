package com.example.melophilia.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.melophilia.Authentication.loginActivity;
import com.example.melophilia.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class adminHome extends AppCompatActivity implements View.OnClickListener {
    public static final int PICK_CHOOSE_RESULT = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ = 1;
    Toolbar mActionBarToolbar;
    Button bt_upload,bt_stop;
    Uri filePath;
    ProgressDialog progressDialog;
    MediaPlayer mediaplayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mediaplayer = new MediaPlayer();
        mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading file");
        bt_upload = findViewById(R.id.bt_upload);
        bt_stop = findViewById(R.id.bt_stop);
        bt_stop.setOnClickListener(this);
        bt_upload.setOnClickListener(this);
        mActionBarToolbar = (Toolbar) findViewById(R.id.confirm_order_toolbar_layout);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.admin));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.item_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(adminHome.this, loginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);

    }

    public void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType( "audio/*");
        startActivityForResult(intent, PICK_CHOOSE_RESULT);
    }
public void choosePermission() {
    // Here, thisActivity is the current activity
    if (ContextCompat.checkSelfPermission(adminHome.this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

        // Permission is not granted
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(adminHome.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(adminHome.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ);

            // MY_PERMISSIONS_REQUEST_READ is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    } else {
        chooseFile();
        // Permission has already been granted
    }
}

    @Override
    public void onClick(View view) {
        if (view == bt_upload) {
            choosePermission();

        }
        else if(view == bt_stop){


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_CHOOSE_RESULT && resultCode == RESULT_OK && data!=null){
            filePath = data.getData();
            Toast.makeText(this, filePath.toString(), Toast.LENGTH_SHORT).show();
            uploadFile();
            progressDialog.show();
        }
    }

    public void uploadFile(){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("AudioFiles").child("songs");
        StorageReference audioRef = storageRef.child(filePath.getLastPathSegment());
        audioRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Song uploaded Successfully;
                progressDialog.dismiss();
                Toast.makeText(adminHome.this, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
            }
        });
        audioRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("music","song"+uri);
                String AudioURL = uri.toString();
                try {
                    mediaplayer.setDataSource(AudioURL);
                    mediaplayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaplayer.start();


            }
        });


    }
}
