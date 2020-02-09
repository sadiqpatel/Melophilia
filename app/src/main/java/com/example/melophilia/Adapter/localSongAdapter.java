package com.example.melophilia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melophilia.MediaPlayer.mediaActivity;
import com.example.melophilia.Model.audioModel;
import com.example.melophilia.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class localSongAdapter extends RecyclerView.Adapter<localSongAdapter.ViewHolder> {
    Context context;
    ArrayList<audioModel> audioModels;

    public localSongAdapter(Context context, ArrayList<audioModel> audioModels) {
        this.context = context;
        this.audioModels = audioModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_song_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
        holder.name.setText(audioModels.get(position).getSongTitle());
        holder.rl_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, mediaActivity.class);
                intent.putExtra("title", audioModels.get(position).getSongTitle());
                intent.putExtra("audioModels", audioModels);
                intent.putExtra("uri",audioModels.get(position).getSongUri());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return audioModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,url;
        LinearLayout l;
        RelativeLayout rl_song;
        ImageView  iv_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rl_song = itemView.findViewById(R.id.rl_song);
            name = itemView.findViewById(R.id.tv_songTitle);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            iv_delete.setVisibility(View.GONE);

        }
    }
}
