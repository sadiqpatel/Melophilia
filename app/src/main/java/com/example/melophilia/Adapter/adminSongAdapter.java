package com.example.melophilia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melophilia.CustomItemClickListener;
import com.example.melophilia.MediaPlayer.mediaActivity;
import com.example.melophilia.Model.audioModel;
import com.example.melophilia.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class adminSongAdapter extends RecyclerView.Adapter<adminSongAdapter.ViewHolder> {
    public Context context;
    public ArrayList<audioModel> audioModels;
    CustomItemClickListener listener;
    public adminSongAdapter(Context context, ArrayList<audioModel> audioModels,  CustomItemClickListener listener) {
        this.context = context;
        this.audioModels = audioModels;
        this.listener = listener;
        Log.d("adminhome123","cons");


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_song_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.tv_songTitle.setText(audioModels.get(position).songTitle);
        holder.tv_songWriter.setText(audioModels.get(position).songWriter);
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(holder.iv_delete, holder.getPosition());

            }
        });
        holder.rl_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, mediaActivity.class);
                intent.putExtra("uri",audioModels.get(position).getSongUri());
                intent.putExtra("title",audioModels.get(position).getSongTitle());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("adminhome123","count"+audioModels.size());
        return audioModels.size();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_songImage, iv_delete,iv_arrow;
        TextView tv_songTitle, tv_songWriter;
        FirebaseAuth mAuth;
        RelativeLayout rl_song;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mAuth = FirebaseAuth.getInstance();
            rl_song = itemView.findViewById(R.id.rl_song);
            iv_songImage = itemView.findViewById(R.id.iv_songImg);
            tv_songTitle = itemView.findViewById(R.id.tv_songTitle);
            tv_songWriter = itemView.findViewById(R.id.tv_songWriter);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            iv_arrow = itemView.findViewById(R.id.iv_arrow);
            if(!(mAuth.getCurrentUser().getEmail().equals("admin12345@gmail.com"))){
                iv_delete.setVisibility(View.INVISIBLE);
            }
        }
    }
}
