package com.byteflipper.soulplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;

public class MusicPlayer {

    public static MusicPlayer instance;
    private MediaPlayer mediaPlayer;
    private boolean isPaused = false;
    private boolean isLooping = false;
    private Context context;
    private OnPlaybackChangeListener onPlaybackChangeListener;

    private MusicPlayer(Context context) {
        this.context = context;
        mediaPlayer = new MediaPlayer();
        setupMediaPlayer();
    }

    public static MusicPlayer getInstance(Context context) {
        if (instance == null) {
            instance = new MusicPlayer(context);
        }
        return instance;
    }

    private void setupMediaPlayer() {
        mediaPlayer.setOnCompletionListener(mp -> {
            if (isLooping) {
                mp.seekTo(0);
                mp.start();
            } else {
                stop();
                if (onPlaybackChangeListener != null) {
                    onPlaybackChangeListener.onStopped();
                }
            }
        });

        mediaPlayer.setOnSeekCompleteListener(mp -> {
            if (onPlaybackChangeListener != null) {
                onPlaybackChangeListener.onProgressChanged(mediaPlayer.getCurrentPosition());
            }
        });

        // Добавьте обработчик onBufferingUpdateListener
        mediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
            if (onPlaybackChangeListener != null) {
                // Обновление progressPercentage, если необходимо (например, для отображения буферизации)
                onPlaybackChangeListener.onProgressChanged((int) (((float) percent / 100) * mediaPlayer.getDuration()));
            }
        });

        mediaPlayer.setOnInfoListener((mp, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                // Буферизация началась (если необходимо, покажите индикатор)
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // Буферизация завершена (если необходимо, скройте индикатор)
            }
            return false;
        });

        mediaPlayer.setOnPreparedListener((mp) -> {
            if (onPlaybackChangeListener != null) {
                onPlaybackChangeListener.onDurationChanged(mp.getDuration()); // Вызов onDurationChanged после подготовки MediaPlayer
            }
        });
    }

    public void setOnPlaybackChangeListener(OnPlaybackChangeListener listener) {
        this.onPlaybackChangeListener = listener;
    }

    public void play(String source) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(source);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPaused = false;
            if (onPlaybackChangeListener != null) {
                onPlaybackChangeListener.onStarted();
            }
        } catch (IOException e) {
            Log.e("MusicPlayer", "Error playing audio", e);
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            isPaused = false;
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPaused = true;
            if (onPlaybackChangeListener != null) {
                onPlaybackChangeListener.onPaused();
            }
        }
    }

    public void resume() {
        if (mediaPlayer != null && isPaused) {
            mediaPlayer.start();
            isPaused = false;
            if (onPlaybackChangeListener != null) {
                onPlaybackChangeListener.onResumed();
            }
        }
    }

    public void setLooping(boolean looping) {
        isLooping = looping;
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(looping);
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public boolean isCompleted() {
        return mediaPlayer != null && !mediaPlayer.isPlaying();
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    public int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    public int getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }

    public interface OnPlaybackChangeListener {
        void onStarted();

        void onPaused();

        void onResumed();

        void onStopped();

        void onProgressChanged(int progress);

        void onDurationChanged(int duration);
    }
}