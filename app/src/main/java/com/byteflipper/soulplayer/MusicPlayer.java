package com.byteflipper.soulplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.slider.Slider;
import com.google.android.material.textview.MaterialTextView;

import java.io.IOException;

public class MusicPlayer implements LifecycleObserver {

    private static MusicPlayer instance;

    private final Context context;
    private MediaPlayer mediaPlayer;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Slider seekBar;
    private MaterialTextView currentTimeTextView;
    private MaterialTextView totalTimeTextView;
    private MaterialTextView songTitleTextView;
    private MaterialTextView artistTextView;
    private MaterialTextView albumTextView;
    private ShapeableImageView coverImageView;
    private MaterialButton playButton;
    private boolean isPaused = false;
    private boolean isLooping = false;

    private MusicPlayer(Context context, Slider seekBar, MaterialTextView currentTimeTextView,
                        MaterialTextView totalTimeTextView, MaterialTextView songTitleTextView,
                        MaterialTextView artistTextView, MaterialTextView albumTextView,
                        ShapeableImageView coverImageView, MaterialButton playButton) {

        this.context = context;
        this.seekBar = seekBar;
        this.currentTimeTextView = currentTimeTextView;
        this.totalTimeTextView = totalTimeTextView;
        this.songTitleTextView = songTitleTextView;
        this.artistTextView = artistTextView;
        this.albumTextView = albumTextView;
        this.coverImageView = coverImageView;
        this.playButton = playButton;
        mediaPlayer = new MediaPlayer();

        setupSeekBar();
        setupMediaPlayer();
    }

    public static MusicPlayer getInstance(Context context, Slider seekBar,
                                          MaterialTextView currentTimeTextView,
                                          MaterialTextView totalTimeTextView,
                                          MaterialTextView songTitleTextView,
                                          MaterialTextView artistTextView,
                                          MaterialTextView albumTextView,
                                          ShapeableImageView coverImageView,
                                          MaterialButton playButton) {
        if (instance == null) {
            instance = new MusicPlayer(context, seekBar, currentTimeTextView, totalTimeTextView,
                    songTitleTextView, artistTextView, albumTextView, coverImageView, playButton);
        }
        return instance;
    }

    public void attachLifecycleOwner(LifecycleOwner owner) {
        owner.getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onActivityPaused() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            pause();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onActivityResumed() {
        if (mediaPlayer != null && isPaused) {
            resume();
        }
    }

    private void setupSeekBar() {
        seekBar.addOnChangeListener((slider, value, fromUser) -> { // изменен listener
            if (fromUser) {
                mediaPlayer.seekTo((int) value);
                updateCurrentTime((int) value);
            }
        });
    }

    private void setupMediaPlayer() {
        mediaPlayer.setOnCompletionListener(mp -> {
            if (isLooping) {
                mp.seekTo(0);
                mp.start();
            } else {
                stop();
                if (playButton != null) {
                    playButton.setIconResource(R.drawable.play_arrow_24px);
                }
            }
        });
    }

    public void play(String source) {
        Log.d("MusicPlayer", "Playing: " + source);
        try {
            mediaPlayer.reset();

            if (source.startsWith("http") || source.startsWith("https")) {
                mediaPlayer.setDataSource(source);
            } else {
                mediaPlayer.setDataSource(source);
            }

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                startUpdatingProgress();
                updateTotalTime(mediaPlayer.getDuration());
                isPaused = false;
                playButton.setIconResource(R.drawable.pause_24px);
                seekBar.setValueTo(mediaPlayer.getDuration());
            });

            mediaPlayer.prepareAsync();

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(source);

            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            byte[] coverArt = retriever.getEmbeddedPicture();

            retriever.release();

            songTitleTextView.setText(title != null ? title : "Неизвестное название");
            artistTextView.setText(artist != null ? artist : "Неизвестный исполнитель");
            albumTextView.setText(album != null ? album : "Неизвестный альбом");

            if (coverArt != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(coverArt, 0, coverArt.length);
                coverImageView.setImageBitmap(bitmap);
            } else {
                coverImageView.setImageResource(R.mipmap.ic_launcher);
            }

        } catch (IOException e) {
            Log.e("MusicPlayer", "Ошибка воспроизведения", e);
            Toast.makeText(context, "Ошибка воспроизведения", Toast.LENGTH_SHORT).show();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            stopUpdatingProgress();
            seekBar.setValue(0);
            updateCurrentTime(0);
            isPaused = false;
            playButton.setIconResource(R.drawable.play_arrow_24px);
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPaused = true;
            playButton.setIconResource(R.drawable.play_arrow_24px);
        }
    }

    public void resume() {
        if (mediaPlayer != null && isPaused) {
            mediaPlayer.start();
            isPaused = false;
            startUpdatingProgress();
            playButton.setIconResource(R.drawable.pause_24px);
        }
    }

    private int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    private int getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }

    private void updateCurrentTime(int progress) {
        int minutes = progress / 60000;
        int seconds = (progress % 60000) / 1000;
        currentTimeTextView.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void updateTotalTime(int duration) {
        int minutes = duration / 60000;
        int seconds = (duration % 60000) / 1000;
        totalTimeTextView.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void startUpdatingProgress() {
        handler.postDelayed(updateProgressTask, 1000);
    }

    private void stopUpdatingProgress() {
        handler.removeCallbacks(updateProgressTask);
    }

    private final Runnable updateProgressTask = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentPosition = getCurrentPosition();
                seekBar.setValue(currentPosition);
                updateCurrentTime(currentPosition);
                handler.postDelayed(this, 1000);
            }
        }
    };

    public void setVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    public float getVolume() {
        //return mediaPlayer != null ? mediaPlayer.getVolume() : 0.0f;
        return 0;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void release() {
        if (mediaPlayer != null) {
            stopUpdatingProgress();
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;

            isPaused = false;
        }
    }

    public void setLooping(boolean looping) {
        isLooping = looping;
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(looping);
        }
    }

    public boolean isLooping() {
        return isLooping;
    }
}