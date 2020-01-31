package com.example.melophilia.Admin;


import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.melophilia.R;



public class dialog_addSongs extends DialogFragment {
    private Button bt_chooseSong;
    private EditText et_writerName, et_songTitle;
    TextView tv_songName;


    public dialog_addSongs() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialog_add_songs, container, false);

        et_writerName = view.findViewById(R.id.et_songWriter);
        et_songTitle = view.findViewById(R.id.et_song_Title);
        return view;
    }


}

