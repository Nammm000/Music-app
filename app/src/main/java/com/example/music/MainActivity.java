package com.example.music;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Random;

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

    Field[] fields = R.raw.class.getFields();
    Random rand = new Random();
    int n = fields.length, currentIndex = rand.nextInt(n);
    ArrayList<Integer> prevIndex = new ArrayList<>();

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

        for(int i = 0; i < n; i++){
            try {
                songs.add(fields[i].getInt(fields[i]));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

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
            prevIndex.add(currentIndex);
            int temp = rand.nextInt(3);
            if (currentIndex != temp) {
                currentIndex = temp;

            } else {
                if (currentIndex == n-1) {
                    currentIndex = 0;
                } else {
                    currentIndex++;
                }
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
            if (prevIndex.isEmpty()) {
                currentIndex = rand.nextInt(n);
            }
            if (!prevIndex.isEmpty()) {
                int last = prevIndex.size() - 1;
                currentIndex = prevIndex.get(last);
                prevIndex.remove(last);
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
        try {
            int temp = fields[currentIndex].getInt(fields[currentIndex]);
            Uri mediaPath = Uri.parse("android.resource://" + getPackageName() + "/" + temp);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(this, mediaPath);
            String sponsorTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String sponsorArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            songTitle.setText(sponsorTitle +" - "+ sponsorArtist);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (currentIndex == 0) {
            imageView.setImageResource(R.drawable.impact_moderato);
        }
        if (currentIndex == 1) {
            imageView.setImageResource(R.drawable.whispering);
        }
        if (currentIndex == 2) {
            imageView.setImageResource(R.drawable.success);
        }
        if (currentIndex >= n) {
            imageView.setImageResource(R.drawable.music_note);
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