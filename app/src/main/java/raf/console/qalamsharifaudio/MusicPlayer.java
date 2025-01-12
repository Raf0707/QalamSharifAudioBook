package raf.console.qalamsharifaudio;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;

import com.google.android.material.textview.MaterialTextView;

import java.io.FileDescriptor;
import java.io.IOException;

public class MusicPlayer {

    public static MusicPlayer instance;
    private MediaPlayer mediaPlayer;
    private boolean isPaused = false;
    private boolean isLooping = false;
    private Context context;
    private OnPlaybackChangeListener onPlaybackChangeListener;

    public MusicPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> onTrackCompleted());
        mediaPlayer.setOnPreparedListener(mp -> {
            if (onPlaybackChangeListener != null) {
                onPlaybackChangeListener.onDurationChanged(mp.getDuration());
                onPlaybackChangeListener.onProgressChanged(mp.getCurrentPosition());
            }
            mp.start();  // Начало проигрывания
            isPaused = false;
            if (onPlaybackChangeListener != null) {
                onPlaybackChangeListener.onStarted();
            }
        });
    }

    private MusicPlayer(Context context) {
        this.context = context;
        mediaPlayer = new MediaPlayer();
        setupMediaPlayer();
        loadTrackData();
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

        mediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
            if (onPlaybackChangeListener != null) {
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

        mediaPlayer.setOnPreparedListener(mp -> {
            if (onPlaybackChangeListener != null) {
                onPlaybackChangeListener.onDurationChanged(mp.getDuration());
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
            mediaPlayer.prepareAsync(); // Асинхронная подготовка
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                isPaused = false;
                if (onPlaybackChangeListener != null) {
                    onPlaybackChangeListener.onStarted();
                }
            });
        } catch (IOException e) {
            Log.e("MusicPlayer", "Error playing audio", e);
        }
    }

    public void play(FileDescriptor fileDescriptor, long startOffset, long length) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(fileDescriptor, startOffset, length);
            mediaPlayer.prepareAsync(); // Асинхронная подготовка
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                isPaused = false;
                if (onPlaybackChangeListener != null) {
                    onPlaybackChangeListener.onStarted();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prepare(String source) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(source);
            mediaPlayer.prepare();
            isPaused = true;
            if (onPlaybackChangeListener != null) {
                onPlaybackChangeListener.onDurationChanged(mediaPlayer.getDuration());
            }
        } catch (IOException e) {
            Log.e("MusicPlayer", "Error preparing audio", e);
        }
    }

    public void prepare(String source, int currentPosition) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(source);
            mediaPlayer.prepare();
            mediaPlayer.seekTo(currentPosition);
            isPaused = true;
            if (onPlaybackChangeListener != null) {
                onPlaybackChangeListener.onDurationChanged(mediaPlayer.getDuration());
                onPlaybackChangeListener.onProgressChanged(currentPosition);
            }
        } catch (IOException e) {
            Log.e("MusicPlayer", "Error preparing audio", e);
        }
    }

    public void prepare(FileDescriptor fileDescriptor, long startOffset, long length, int currentPosition) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(fileDescriptor, startOffset, length);
            mediaPlayer.prepare();
            mediaPlayer.seekTo(currentPosition);
            isPaused = true;
            if (onPlaybackChangeListener != null) {
                onPlaybackChangeListener.onDurationChanged(mediaPlayer.getDuration());
                onPlaybackChangeListener.onProgressChanged(currentPosition);
            }
        } catch (IOException e) {
            Log.e("MusicPlayer", "Error preparing audio", e);
        }
    }

    public void prepare(FileDescriptor fileDescriptor, long startOffset, long length) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(fileDescriptor, startOffset, length);
            mediaPlayer.prepare();
            isPaused = true;
            if (onPlaybackChangeListener != null) {
                onPlaybackChangeListener.onDurationChanged(mediaPlayer.getDuration());
            }
        } catch (IOException e) {
            Log.e("MusicPlayer", "Error preparing audio", e);
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            isPaused = false;
        }
    }

    private void onTrackCompleted() {
        if (onPlaybackChangeListener != null) {
            onPlaybackChangeListener.onCompleted();
        }
        resetPlayer();
    }

    private void resetPlayer() {
        mediaPlayer.seekTo(0);
        isPaused = true;
        if (onPlaybackChangeListener != null) {
            onPlaybackChangeListener.onProgressChanged(0);
        }
    }

    public void pause(String filePath, Context context) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            saveTrackData(filePath, getCurrentPosition(), context);
            isPaused = true;
            if (onPlaybackChangeListener != null) {
                onPlaybackChangeListener.onPaused();
            }
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
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(position);
        }
    }

    public void resumeFromCurrentPosition() {
        if (mediaPlayer != null && !isPaused && !mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(getCurrentPosition());
            mediaPlayer.start();
            isPaused = false;
            if (onPlaybackChangeListener != null) {
                onPlaybackChangeListener.onResumed();
            }
        }
    }

    public void saveTrackData(String trackPath, int position, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MusicPlayerPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastTrack", trackPath);
        editor.putInt("lastPosition", position);
        editor.apply();
    }

    public void loadTrackData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MusicPlayerPrefs", Context.MODE_PRIVATE);
        String trackPath = sharedPreferences.getString("lastTrack", "");
        int position = sharedPreferences.getInt("lastPosition", 0);

        if (trackPath != null && position > 0) {
            play(trackPath);
            seekTo(position);
        }
    }

    public void loadTrackData(MaterialTextView statusTextView) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MusicPlayerPrefs", Context.MODE_PRIVATE);
        String trackPath = sharedPreferences.getString("lastTrack", null);
        int position = sharedPreferences.getInt("lastPosition", 0);

        if (trackPath != null && position > 0) {
            play(trackPath);
            seekTo(position);
        } else {
            statusTextView.setText("Ничего не воспроизводится");
            seekTo(0);
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

        void onCompleted();

        void onProgressChanged(int progress);

        void onDurationChanged(int duration);
    }
}