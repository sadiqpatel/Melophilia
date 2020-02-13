package com.example.melophilia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.melophilia.Model.audioModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MostStreamedSongs extends AppCompatActivity {
    private ArrayList<audioModel> audioList;
    MostStremedSongAdapter mostStremedSongAdapter;
    ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_most_streamed_songs);

        mAuth = FirebaseAuth.getInstance();
        //Adding firebase reference.
        mDatabase = FirebaseDatabase.getInstance().getReference("Audio");

        progressDialog = new ProgressDialog(MostStreamedSongs.this);
        progressDialog.setMessage("Loading Songs");
        progressDialog.setCancelable(false);

        audioList = new ArrayList<>();
        RecyclerView mostStreamed_recyclerView = findViewById(R.id.mostStreamed_recyclerView);
        mostStreamed_recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mostStremedSongAdapter = new MostStremedSongAdapter(MostStreamedSongs.this, audioList);
        mostStreamed_recyclerView.setAdapter(mostStremedSongAdapter);

        getData();
    }

    public void getData() {
        progressDialog.show();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                audioList.clear();
                if (dataSnapshot.getValue() == null) {
                    progressDialog.dismiss();
                }
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    audioModel audio = dataSnapshot1.getValue(audioModel.class);
                    audioList.add(audio);
                    Collections.sort(audioList, new Comparator<audioModel>() {
                        @Override
                        public int compare(audioModel lhs, audioModel rhs) {
                            // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                            return lhs.getCount() > rhs.getCount() ? -1 : (lhs.count < rhs.count ) ? 1 : 0;
                        }
                    });
                    progressDialog.dismiss();
                }
                mostStremedSongAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }
}

class MostStremedSongAdapter extends RecyclerView.Adapter<MostStremedSongAdapter.ViewHolder>{

    private Context context;
    private ArrayList<audioModel> audioList;

    public MostStremedSongAdapter(Context context, ArrayList<audioModel> audioList) {
        this.context = context;
        this.audioList = audioList;
    }

    @NonNull
    @Override
    public MostStremedSongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view_count, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MostStremedSongAdapter.ViewHolder holder, int position) {
        holder.title.setText(audioList.get(position).getSongTitle());
        holder.count.setText(String.valueOf(audioList.get(position).getCount()));
        Glide
                .with(context)
                .load(audioList.get(position).getSongImg())
                .centerCrop()
                .placeholder(R.drawable.demo)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, count;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.txtTitle);
            count = itemView.findViewById(R.id.txtCount);
            imageView = itemView.findViewById(R.id.txtImg);
        }
    }
}
