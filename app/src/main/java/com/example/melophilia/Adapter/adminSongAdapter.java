package com.example.melophilia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.melophilia.CustomItemClickListener;
import com.example.melophilia.MediaPlayer.mediaActivity;
import com.example.melophilia.Model.audioModel;
import com.example.melophilia.R;
import com.example.melophilia.utils.noInternet;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class adminSongAdapter extends RecyclerView.Adapter<adminSongAdapter.ViewHolder> implements Filterable {
    public Context context;
    public ArrayList<audioModel> audioModels;
    public ArrayList<audioModel> audioModelsFiltered;
    CustomItemClickListener listener;

    public adminSongAdapter(Context context, ArrayList<audioModel> audioModels, CustomItemClickListener listener) {
        this.context = context;
        this.audioModels = audioModels;
        this.listener = listener;
        audioModelsFiltered = audioModels;
        Log.d("adminhome123", "cons");


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_song_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Glide
                .with(context)
                .load(audioModelsFiltered.get(position).getSongImg())
                .centerCrop()
                .placeholder(R.drawable.demo)
                .into(holder.iv_songImage);
        holder.tv_songTitle.setText(audioModelsFiltered.get(position).songTitle);
        holder.tv_songWriter.setText(audioModelsFiltered.get(position).songWriter);
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(holder.iv_delete, holder.getPosition());

            }
        });
        holder.rl_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(noInternet.isInternetAvailable(context))) //returns true if internet available
                {
                    Toast.makeText(context, "Check your internet Connection", Toast.LENGTH_SHORT).show();
                } else {

                    Intent intent = new Intent(context, mediaActivity.class);
                    intent.putExtra("uri", audioModelsFiltered.get(position).getSongUri());
                    intent.putExtra("title", audioModelsFiltered.get(position).getSongTitle());
                    intent.putExtra("imguri", audioModelsFiltered.get(position).getSongImg());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("artist",audioModelsFiltered.get(position).getSongWriter());
                    intent.putExtra("image",R.drawable.t1);
                    intent.putExtra("audio", (Serializable) audioModelsFiltered);

                    context.startActivity(intent);
                    listener.onItemPlay(audioModelsFiltered.get(position));

                }
            }
        });
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    audioModelsFiltered = audioModels;
                } else {
                    ArrayList<audioModel> filteredList = new ArrayList<>();
                    for (audioModel row : audioModels) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getSongTitle().toLowerCase().contains(charString.toLowerCase()) || row.getSongWriter().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    audioModelsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = audioModelsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                audioModelsFiltered = (ArrayList<audioModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        Log.d("adminhome123", "count" + audioModels.size());
        return audioModelsFiltered.size();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_songImage, iv_delete, iv_arrow;
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
            if (!(mAuth.getCurrentUser().getEmail().equals("admin12345@gmail.com"))) {
                iv_delete.setVisibility(View.INVISIBLE);
            }
        }
    }
}
