package com.example.melophilia.Home;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.melophilia.Adapter.localSongAdapter;
import com.example.melophilia.Model.audioModel;
import com.example.melophilia.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class songLocalFragment extends Fragment {

    RecyclerView recyclerView;
    localSongAdapter songAdapter;
    public songLocalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_song_local, container, false);
        recyclerView = view. findViewById(R.id.rv_songList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        getAllAudioFromDevice(getContext());
        songAdapter = new localSongAdapter(getContext(),getAllAudioFromDevice(getContext()));
        recyclerView.setAdapter(songAdapter);
        return view;
    }
    public ArrayList<audioModel> getAllAudioFromDevice(final Context context) {

        final ArrayList<audioModel> tempAudioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,};
        Cursor c = context.getContentResolver().query(uri,
                projection,
                null,
                null,
                null);
        if (c != null) {
            while (c.moveToNext()) {

                audioModel audioModel = new audioModel();
                String path = c.getString(0);
                String album = c.getString(1);
                String artist = c.getString(2);

                String name = path.substring(path.lastIndexOf("/") + 1);

                audioModel.setSongTitle(name);
                /*audioModel.setaAlbum(album);
                audioModel.setaArtist(artist);*/
                audioModel.setSongUri(path);

                Log.d("name" + name, " Album :" + album);
                Log.d("Path :" + path, " Artist :" + artist);
                tempAudioList.add(audioModel);

            }
            c.close();
        }


        return tempAudioList;
    }


}
