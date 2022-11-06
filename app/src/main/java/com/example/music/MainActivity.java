package com.example.music;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import android.annotation.SuppressLint;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    ImageView play, prev, next, imageView;
    TextView songTitle;
    SeekBar mSeekBarTime;
    static MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    int currentIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        play = findViewById(R.id.play);
        prev = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        songTitle = findViewById(R.id.songName);
        imageView = findViewById(R.id.music_icon);
        mSeekBarTime = findViewById(R.id.mSeekBarTime);

        final ArrayList<Integer> songs = new ArrayList<>();
        songs.add(R.raw.file_example);
        songs.add(R.raw.piano);
        songs.add(R.raw.success);

        mMediaPlayer = MediaPlayer.create(getApplicationContext(),
                songs.get(currentIndex));

        play.setOnClickListener(v -> {
            mSeekBarTime.setMax(mMediaPlayer.getDuration());
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                play.setImageResource(R.drawable.play);
            } else {
                mMediaPlayer.start();
                play.setImageResource(R.drawable.pause);
            }

            songNames();
        });
        next.setOnClickListener(v -> {
            if (mMediaPlayer != null) {
                play.setImageResource(R.drawable.pause);
            }
            if (currentIndex < songs.size() - 1) {
                currentIndex++;
            } else {
                currentIndex = 0;
            }
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer = MediaPlayer.create(getApplicationContext(),
                    songs.get(currentIndex));
            mMediaPlayer.start();
            songNames();
        });
        prev.setOnClickListener(v -> {
            if (mMediaPlayer != null) {
                play.setImageResource(R.drawable.pause);
            }
            if (currentIndex > 0) {
                currentIndex--;
            } else {
                currentIndex = songs.size() - 1;
            }
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer = MediaPlayer.create(getApplicationContext(),
                    songs.get(currentIndex));
            mMediaPlayer.start();
            songNames();

        });
    }
    private void songNames() {
        if (currentIndex == 0) {
            songTitle.setText("Impact moderato - Kevin Macleod");
            imageView.setImageResource(R.drawable.impact_moderato);
        }
        if (currentIndex == 1) {
            songTitle.setText("Whispering - Fils");
            imageView.setImageResource(R.drawable.whispering);
        }
        if (currentIndex == 2) {
            songTitle.setText("Success - AShamaluves ");
            imageView.setImageResource(R.drawable.success);
        }

        mMediaPlayer.setOnPreparedListener(mp -> {
            mSeekBarTime.setMax(mMediaPlayer.getDuration());
            mMediaPlayer.start();
        });
        mSeekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean
                    fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo(progress);
                    mSeekBarTime.setProgress(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        new Thread(() -> {
            while (mMediaPlayer != null) {
                try {
                    if (mMediaPlayer.isPlaying()) {
                        Message message = new Message();
                        message.what = mMediaPlayer.getCurrentPosition();
                        handler.sendMessage(message);
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @SuppressLint("Handler Leak") Handler handler = new Handler () {
        @Override
        public void handleMessage (Message msg) {
            mSeekBarTime.setProgress(msg.what);
        }
    };
}