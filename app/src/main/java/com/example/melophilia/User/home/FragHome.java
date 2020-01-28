package com.example.melophilia.User.home;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.melophilia.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class LatestAlbum{
        public String link;
        public String name;
        public String artist;

        public LatestAlbum(){}

        public LatestAlbum(String link, String name, String artist) {
            this.name = name;
            this.link = link;
            this.artist = artist;
        }
    }

public class FragHome extends Fragment {
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    RecyclerView recyclerView;
    MediaPlayer mediaPlayer;
    List<LatestAlbum> list;
    public static  String link;
    public FragHome() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_home, container, false);
        list = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("songs");
        recyclerView = view.findViewById(R.id.fragHome_recyclerView);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                list.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    LatestAlbum Mod = dataSnapshot1.getValue(LatestAlbum.class);
                    list.add(Mod);
                }



                final HomeLatestAlbumAdapter adapterClass = new HomeLatestAlbumAdapter((Activity)getContext() , list);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                recyclerView.setAdapter(adapterClass);
                link = null;
                recyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LatestAlbum modelData = list.get(v.getScrollX());
                        link = modelData.link;
                        if (mediaPlayer.isPlaying()){

                            mediaPlayer.stop();
                            mediaPlayer.reset();
                        }

                        playMusic(link);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getDetails());
            }
        });
    }

    private void playMusic(String url){

        firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReferenceFromUrl(url);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    // Download url of file
                    String url = uri.toString();
                    mediaPlayer.setDataSource(url);
                    // wait for media player to get prepare
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            try {
                                mediaPlayer.start();
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    mediaPlayer.prepareAsync();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("TAG", e.getMessage());
                    }
                });


    }

}

class HomeLatestAlbumAdapter extends RecyclerView.Adapter<HomeLatestAlbumAdapter.MyViewHolder>{

    private Activity context;
    private List<LatestAlbum> latestAlbums;
    String databaseReference;

    public HomeLatestAlbumAdapter(Activity context, List<LatestAlbum> latestAlbums) {
        this.context = context;
        this.latestAlbums = latestAlbums;
    }

    @NonNull
    @Override
    public HomeLatestAlbumAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.items_latest_albums, parent, false);
       return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeLatestAlbumAdapter.MyViewHolder holder, int position) {
        holder.textTitle.setText(latestAlbums.get(position).name);
        holder.textAuthor.setText("("+latestAlbums.get(position).artist+")");
    }

    @Override
    public int getItemCount() {
        return latestAlbums.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textAuthor;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.latestAlbum_txtTitle);
            textAuthor = itemView.findViewById(R.id.latestAlbum_txtAuthor);
        }
    }
}

