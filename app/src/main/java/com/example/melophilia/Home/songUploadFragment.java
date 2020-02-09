package com.example.melophilia.Home;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.melophilia.Adapter.adminSongAdapter;
import com.example.melophilia.Admin.adminHome;
import com.example.melophilia.CustomItemClickListener;
import com.example.melophilia.Model.audioModel;
import com.example.melophilia.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;

public class songUploadFragment extends Fragment {

    private final static String TAG = adminHome.class.getName();
    //All variables and object declarations here.
    public static final int PICK_SONG_RESULT = 1;
    public static final int PICK_IMAGE_RESULT = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ = 1;
    Toolbar mActionBarToolbar;
    Uri filePath, imagePath = null;
    ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    FloatingActionButton floatingActionButton;
    String tvSongName;
    TextView tv_songName, tv_song;
    public ArrayList<audioModel> audioModels = new ArrayList<>();
    public RecyclerView rv_songList;
    public com.example.melophilia.Adapter.adminSongAdapter adminSongAdapter;
    FirebaseAuth mAuth;
    ImageView iv_songImg;
    String songKey, ImageKey;
    String AudioURL, ImgURL = "";

    public songUploadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_upload, container, false);
        mAuth = FirebaseAuth.getInstance();
        //Adding firebase reference.
        mDatabase = FirebaseDatabase.getInstance().getReference("Audio");


        //Adding progressDialog & linking other components with there xml id using findviewbyid.
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Songs");
        progressDialog.setCancelable(false);

        floatingActionButton = view.findViewById(R.id.floating_action_button);
        tv_song = view.findViewById(R.id.tv_songs);
        rv_songList = view.findViewById(R.id.rv_songList);

        if (!(mAuth.getCurrentUser().getEmail().equals("admin12345@gmail.com"))) {
            floatingActionButton.setVisibility(View.GONE);

        }
        //Adding recyclerview and connecting it to a layout manager.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv_songList.setLayoutManager(layoutManager);

        //Passing data to localSongAdapter constructor and setting the localSongAdapter.
        adminSongAdapter = new adminSongAdapter(getContext(), audioModels, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                String data = audioModels.get(position).getSongKey();
                String songId = audioModels.get(position).getSongId();
                confirmDeleteDialog(data, songId);

            }

            @Override
            public void onItemPlay(audioModel audioModel) {
                
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

        return view;
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

    public void chooseSong() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_SONG_RESULT);
    }

    public void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_RESULT);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_SONG_RESULT && resultCode == RESULT_OK && data != null) {
            filePath = data.getData();
            getFileName(filePath);
            tvSongName = getFileName(filePath);
            tv_songName.setText(tvSongName);
        }
        if (requestCode == PICK_IMAGE_RESULT && resultCode == RESULT_OK && data != null) {
            imagePath = data.getData();
            iv_songImg.setImageURI(imagePath);
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
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
                Toast.makeText(getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                deleteDataRealtimeDatabase(songId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d("onFailure", "delete" + exception.getLocalizedMessage());
                Toast.makeText(getContext(), exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void deleteDataRealtimeDatabase(String songId) {
        progressDialog.dismiss();
        mDatabase.child(songId).removeValue();
    }

    public void uploadSong_storage(final String songTitle, final String songWriter) {
        Log.d(TAG, "uploadFile Storage");
        Log.d(TAG, songTitle);
        Log.d(TAG, songWriter);
        AudioURL = null;
        ImgURL = null;
        progressDialog.setMessage("Uploading file");

        progressDialog.show();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("AudioFiles").child("songs");

        songKey = mDatabase.push().getKey();
        final StorageReference audioRef = storageRef.child(songKey);

        Task upload_audio = audioRef.putFile(filePath);

        final StorageReference ImgRef = FirebaseStorage.getInstance().getReference("Image");

        Task upload_img = null;

        if (imagePath != null) {
            upload_img = ImgRef.putFile(imagePath);
        }

        Task all;
        if (imagePath != null)
            all = Tasks.whenAll(upload_audio, upload_img);
        else
            all = Tasks.whenAll(upload_audio);


        all.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {


                Task<Uri> audio_uri = audioRef.getDownloadUrl();


                audio_uri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        AudioURL = uri.toString();
                        Log.e("AAURIAD = ", "URI =" + AudioURL);

                    }
                });

                Task<Uri> img_uri = null;
                if (imagePath != null) {
                    img_uri = ImgRef.getDownloadUrl();

                    img_uri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ImgURL = uri.toString();
                            Log.e("AAURIIM = ", "URI =" + ImgURL);
                        }
                    });
                }
                Task all_uris;
                if (imagePath != null)
                    all_uris = Tasks.whenAll(audio_uri, img_uri);
                else
                    all_uris = Tasks.whenAll(audio_uri);

                Tasks.whenAll(all_uris).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Log.e("AAURI = ", "URI =" + AudioURL);
//                        Log.e("AAURI = ", "URI =" + ImgURL);

                        upload(ImgURL, AudioURL, songTitle, songWriter, songKey);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });


    }




    public void upload(String imgURL, String audioUrl, String songTitle, String songWriter, String songKey) {
        uploadFile_rd(audioUrl, imgURL, songTitle, songWriter, songKey);
        progressDialog.dismiss();
        Toast.makeText(getContext(), "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
    }

    private void uploadFile_rd(String audioURL, String imgRef, String songTitle, String songWriter, String songKey) {
        String mSongId = mDatabase.push().getKey();
        audioModel audio = new audioModel(audioURL, imgRef, songTitle, songWriter, mSongId, songKey);
        mDatabase.child(mSongId).setValue(audio);
    }

    public void openDialog() {
        final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dialog_add_songs, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        final EditText et_writerName, et_songTitle;
        Button bt_chooseImg = dialogView.findViewById(R.id.bt_chooseImg);
        bt_chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        Button bt_chooseSong = dialogView.findViewById(R.id.bt_chooseSong);
        bt_chooseSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseSong();
            }
        });
        tv_songName = dialogView.findViewById(R.id.tv_songName);
        TextView bt_cancel, bt_upload;
        bt_cancel = dialogView.findViewById(R.id.bt_cancel);
        bt_upload = dialogView.findViewById(R.id.bt_upload);
        et_writerName = dialogView.findViewById(R.id.et_songWriter);
        et_songTitle = dialogView.findViewById(R.id.et_song_Title);
        iv_songImg = dialogView.findViewById(R.id.iv_songImg);

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
                    Toast.makeText(getContext(), "Choose Song", Toast.LENGTH_SHORT).show();
                } else {
                    uploadSong_storage(songTitle, songWriter);
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

    public void confirmDeleteDialog(final String data, final String songId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
