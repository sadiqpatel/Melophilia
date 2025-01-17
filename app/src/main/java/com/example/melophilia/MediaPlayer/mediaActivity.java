package com.example.melophilia.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.melophilia.Admin.adminHome;
import com.example.melophilia.Home.homeActivity;
import com.example.melophilia.R;
import com.example.melophilia.utils.noInternet;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class mediaActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_rewind, iv_pause, iv_play, iv_forward, iv_songImg;
    private MediaPlayer mediaPlayer;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();
    ;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    private TextView tv_startTime, tv_endTime, tv_songName;
    public static int oneTimeOnly = 0;
    Uri uri;
    ProgressDialog progressDialog;
    Toolbar mActionBarToolbar;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();
        iv_songImg = findViewById(R.id.songImg);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String myUri = intent.getStringExtra("uri");
        Log.d("mediaActivity","uri"+myUri);
        String songImg = intent.getStringExtra("imguri");
        Glide
                .with(getApplicationContext())
                .load(songImg)
                .centerCrop()
                .placeholder(R.drawable.demo)
                .into(iv_songImg);
        mActionBarToolbar = (Toolbar) findViewById(R.id.confirm_order_toolbar_layout);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(title);

        iv_rewind = findViewById(R.id.iv_rewind);
        iv_pause = findViewById(R.id.iv_pause);
        iv_play = findViewById(R.id.iv_play);
        iv_forward = findViewById(R.id.iv_forward);

        iv_rewind.setOnClickListener(this);
        iv_pause.setOnClickListener(this);
        iv_play.setOnClickListener(this);
        iv_forward.setOnClickListener(this);

        tv_startTime = (TextView) findViewById(R.id.tv_startTime);
        tv_endTime = (TextView) findViewById(R.id.tv_endTime);
        tv_songName = (TextView) findViewById(R.id.tv_songName);


        seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setClickable(false);


        uri = Uri.parse(myUri);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.d("mediaActivity", "ads" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                progressDialog.dismiss();
                play();

            }
        });

        seekbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    Log.d("mediaActivity", "Moved , process data, Moved to :" + seekbar.getProgress());
                    seekbar.setProgress(seekbar.getProgress());
                    mediaPlayer.seekTo(seekbar.getProgress());
                    return false;
                }
                Log.d("mediaActivity", "Touched , Progress :" + seekbar.getProgress());
                return true;
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(seekbar.getProgress() == finalTime){
                    iv_play.setVisibility(View.VISIBLE);
                    iv_pause.setVisibility(View.GONE);
                    seekBar.setProgress(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View view) {
        if (view == iv_rewind) {
            int temp = (int) startTime;

            if ((temp - backwardTime) > 0) {
                startTime = startTime - backwardTime;
                mediaPlayer.seekTo((int) startTime);
                Toast.makeText(getApplicationContext(), "You have rewind backward 5 seconds", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Cannot rewind backward 5 seconds", Toast.LENGTH_SHORT).show();
            }
        } else if (view == iv_pause) {
            Toast.makeText(getApplicationContext(), "Pausing sound", Toast.LENGTH_SHORT).show();
            mediaPlayer.pause();
            iv_pause.setVisibility(View.GONE);
            iv_play.setVisibility(View.VISIBLE);

        } else if (view == iv_play) {
          play();
        } else if (view == iv_forward) {
            int temp = (int) startTime;

            if ((temp + forwardTime) <= finalTime) {
                startTime = startTime + forwardTime;
                mediaPlayer.seekTo((int) startTime);
                Toast.makeText(getApplicationContext(), "You have forwarded 5 seconds", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Cannot forward 5 seconds", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                mediaPlayer.stop();
                Intent intent = new Intent(mediaActivity.this, homeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        @SuppressLint("DefaultLocale")
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            tv_startTime.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            seekbar.setProgress((int) startTime);
            myHandler.postDelayed(this, 100);

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediaPlayer.stop();
        finish();
    }
    public void play(){
        if (!(noInternet.isInternetAvailable(getApplicationContext()))) //returns true if internet available
        {
            Toast.makeText(getApplicationContext(), "Check your internet Connection", Toast.LENGTH_SHORT).show();
        } else {


            Toast.makeText(getApplicationContext(), "Playing sound", Toast.LENGTH_SHORT).show();
            mediaPlayer.start();
            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            seekbar.setMax((int) finalTime);

          /*  if (oneTimeOnly == 0) {
                seekbar.setMax((int) finalTime);
                oneTimeOnly = 1;
            }*/
            tv_endTime.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                    finalTime)))
            );

            tv_startTime.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                    startTime)))
            );

            seekbar.setProgress((int) startTime);
            myHandler.postDelayed(UpdateSongTime, 100);
            iv_play.setVisibility(View.GONE);
            iv_pause.setVisibility(View.VISIBLE);
           if(seekbar.getProgress() == finalTime){
               iv_play.setVisibility(View.VISIBLE);
               iv_pause.setVisibility(View.GONE);
           }
        }
    }
}
