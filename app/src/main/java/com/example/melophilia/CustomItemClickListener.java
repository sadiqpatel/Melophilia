package com.example.melophilia;

import android.view.View;

import com.example.melophilia.Model.audioModel;


public interface CustomItemClickListener {
    public void onItemClick(View v, int position);
    public void onItemPlay(audioModel audioModel);
}
