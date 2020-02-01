package com.example.melophilia.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.melophilia.Adapter.adminSongAdapter;
import com.example.melophilia.Authentication.loginActivity;
import com.example.melophilia.CustomItemClickListener;
import com.example.melophilia.R;
import com.example.melophilia.Model.audioModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class adminHome extends AppCompatActivity {
    private final static String TAG = adminHome.class.getName();
    //All variables and object declarations here.
    public static final int PICK_CHOOSE_RESULT = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ = 1;
    Toolbar mActionBarToolbar;
    Uri filePath;
    ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    FloatingActionButton floatingActionButton;
    String tvSongName;
    TextView tv_songName, tv_song;
    public ArrayList<audioModel> audioModels = new ArrayList<>();
    public RecyclerView rv_songList;
    public adminSongAdapter adminSongAdapter;
    FirebaseAuth mAuth;

    String songKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mAuth = FirebaseAuth.getInstance();
        //Adding firebase reference.
        mDatabase = FirebaseDatabase.getInstance().getReference("Audio");

        //Adding Toolbar
        mActionBarToolbar = (Toolbar) findViewById(R.id.confirm_order_toolbar_layout);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.admin));

        //Adding progressDialog & linking other components with there xml id using findviewbyid.
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Songs");
        floatingActionButton = findViewById(R.id.floating_action_button);
        tv_song = findViewById(R.id.tv_songs);
        rv_songList = findViewById(R.id.rv_songList);
        if (!(mAuth.getCurrentUser().getEmail().equals("admin12345@gmail.com"))) {
            floatingActionButton.setVisibility(View.GONE);
            tv_song.setText("Songs Playlist");
            getSupportActionBar().setTitle(getResources().getString(R.string.user));
        }
        //Adding recyclerview and connecting it to a layout manager.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv_songList.setLayoutManager(layoutManager);

        //Passing data to adapter constructor and setting the adapter.
        adminSongAdapter = new adminSongAdapter(getApplicationContext(), audioModels, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                String data = audioModels.get(position).getSongKey();
                String songId = audioModels.get(position).getSongId();
                confirmDeleteDialog(data,songId);

            }
        });
        rv_songList.setAdapter(adminSongAdapter);

        //Retrieving song list
        getData();

        //On click of floating plus button.
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
    }

  /*  private void getFilePath(String data) {
        String fileName="";
        for(int i = data.length()-1 ;i>=0;i--){
            if(data.charAt(i) == '/'){
                break  ;
            }
            else{
                fileName = data.charAt(i)+fileName;
            }
        }
        Toast.makeText(this, fileName, Toast.LENGTH_SHORT).show();
        delete_storage(fileName);

    }*/


    //Adding menu to the toolbar with log out option.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    //Handling menu item click event in this method.
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

    //Handling the click of mobile backbutton.
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);

    }

    //Method created
    public void getData() {
        progressDialog.show();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                audioModels.clear();
                Log.d(TAG, "sdsff" + dataSnapshot);
                if (dataSnapshot.getValue() == null) {
                    progressDialog.dismiss();
                }
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.d(TAG, "sdsff" + dataSnapshot1);
                    audioModel audio = dataSnapshot1.getValue(audioModel.class);
                    audioModels.add(audio);
                    progressDialog.dismiss();
                }
                adminSongAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    public void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
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
            // Permission has already been granted
            chooseFile();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CHOOSE_RESULT && resultCode == RESULT_OK && data != null) {
            filePath = data.getData();
            getFileName(filePath);
            tvSongName = getFileName(filePath);
            tv_songName.setText(tvSongName);
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }

        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void delete_storage(String filename, final String songId) {
        // Create a reference to the file to delete
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("AudioFiles").child("songs");
        StorageReference audioRef = storageRef.child(filename);

// Delete the file
        audioRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Toast.makeText(adminHome.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                deleteDataRealtimeDatabase(songId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d("onFailure", "delete" + exception.getLocalizedMessage());
                Toast.makeText(adminHome.this, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void deleteDataRealtimeDatabase(String songId) {
        progressDialog.dismiss();
        mDatabase.child(songId).removeValue();
    }

    public void uploadFile_storage(final String songTitle, final String songWriter) {
        Log.d(TAG, "uploadFile Storage");
        Log.d(TAG, songTitle);
        Log.d(TAG, songWriter);
        progressDialog.setMessage("Uploading file");

        progressDialog.show();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("AudioFiles").child("songs");

        songKey = mDatabase.push().getKey();
        final StorageReference audioRef = storageRef.child(songKey);
        audioRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Song uploaded Successfully;

                pushToDatabase(audioRef, songTitle, songWriter);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("adminhome123", "adminhome123" + e.getLocalizedMessage());
                    }
                });


    }

    private void pushToDatabase(StorageReference ref, final String songTitle, final String songWriter) {
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("music", "song" + uri);
                String AudioURL = uri.toString();
                uploadFile_rd(AudioURL, songTitle, songWriter, songKey);
                progressDialog.dismiss();
                Toast.makeText(adminHome.this, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("adminhome123", "adminhome123" + e.getLocalizedMessage());

                    }
                });
    }


    private void uploadFile_rd(String audioURL, String songTitle, String songWriter, String songKey) {

        String mSongId = mDatabase.push().getKey();
        audioModel audio = new audioModel(audioURL, songTitle, songWriter, mSongId, songKey);
        mDatabase.child(mSongId).setValue(audio);
    }

    public void openDialog() {
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.fragment_dialog_add_songs, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final EditText et_writerName, et_songTitle;

        Button bt_chooseSong = dialogView.findViewById(R.id.bt_chooseSong);
        bt_chooseSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePermission();
            }
        });
        tv_songName = dialogView.findViewById(R.id.tv_songName);
        TextView bt_cancel, bt_upload;
        bt_cancel = dialogView.findViewById(R.id.bt_cancel);
        bt_upload = dialogView.findViewById(R.id.bt_upload);
        et_writerName = dialogView.findViewById(R.id.et_songWriter);
        et_songTitle = dialogView.findViewById(R.id.et_song_Title);


        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        bt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String songWriter = et_writerName.getText().toString().trim();
                String songTitle = et_songTitle.getText().toString().trim();
                String songName = tv_songName.getText().toString();
                if (songWriter.isEmpty()) {
                    et_writerName.setError(getResources().getString(R.string.empty_email));
                } else if (songTitle.isEmpty()) {
                    et_songTitle.setError(getResources().getString(R.string.empty_password));
                } else if (songName.isEmpty()) {
                    Toast.makeText(adminHome.this, "Choose Song", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile_storage(songTitle, songWriter);
                    alertDialog.dismiss();
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    public void confirmDeleteDialog(final String data,final String songId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to delete this song");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressDialog.setMessage("Deleting Song");
                progressDialog.show();
                delete_storage(data, songId);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}