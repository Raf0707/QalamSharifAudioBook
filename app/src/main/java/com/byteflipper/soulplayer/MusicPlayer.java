package com.byteflipper.soulplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;

public class MusicPlayer {
    private Context context;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private SeekBar seekBar;
    private TextView currentTimeTextView;
    private TextView totalTimeTextView;
    private TextView songTitleTextView;
    private TextView artistTextView;
    private TextView albumTextView;
    private ImageView coverImageView;
    private MaterialButton playButton;
    private boolean isPaused = false;
    private boolean isLooping = false;

    public MusicPlayer(Context context, SeekBar seekBar, TextView currentTimeTextView,
                       TextView totalTimeTextView, TextView songTitleTextView, TextView artistTextView,
                       TextView albumTextView, ImageView coverImageView, MaterialButton playButton) {
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
        handler = new Handler();
        setupSeekBar();
        setupMediaPlayer();
    }

    // Метод для настройки SeekBar
    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    updateCurrentTime(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
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

    // Метод для проигрывания аудиофайла по пути (локальному или по URL)
    public void play(String source) {
        Log.d("MusicPlayer", "Playing: " + source); // Добавим лог для отладки
        try {
            mediaPlayer.reset();

            // Проверка, локальный ли файл или URL
            if (source.startsWith("http") || source.startsWith("https")) {
                // Проигрывание по URL
                mediaPlayer.setDataSource(source);
            } else {
                // Проигрывание локального файла
                mediaPlayer.setDataSource(source);
            }

            mediaPlayer.prepare();
            mediaPlayer.start();
            startUpdatingProgress();
            updateTotalTime(mediaPlayer.getDuration());
            isPaused = false;

            // Получение информации о треке с помощью MediaMetadataRetriever
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(source);

            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            byte[] coverArt = retriever.getEmbeddedPicture();

            retriever.release();

            // Установка информации о треке
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
            e.printStackTrace();
            Toast.makeText(context, "Failed to play the audio", Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для остановки проигрывания
    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        stopUpdatingProgress();
        seekBar.setProgress(0);
        updateCurrentTime(0);
        isPaused = false;
    }

    // Метод для паузы
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPaused = true;
        }
    }

    // Метод для возобновления
    public void resume() {
        if (mediaPlayer != null && isPaused) {
            mediaPlayer.start();
            isPaused = false;
            startUpdatingProgress();
        }
    }

    // Метод для получения текущего прогресса воспроизведения
    private int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    // Метод для получения общей продолжительности аудиофайла
    private int getDuration() {
        return mediaPlayer.getDuration();
    }

    // Метод для обновления текущего времени воспроизведения
    private void updateCurrentTime(int progress) {
        int minutes = progress / 60000;
        int seconds = (progress % 60000) / 1000;
        currentTimeTextView.setText(String.format("%02d:%02d", minutes, seconds));
    }

    // Метод для обновления общей продолжительности аудиофайла
    private void updateTotalTime(int duration) {
        int minutes = duration / 60000;
        int seconds = (duration % 60000) / 1000;
        totalTimeTextView.setText(String.format("%02d:%02d", minutes, seconds));
    }

    // Метод для запуска обновления прогресса воспроизведения
    private void startUpdatingProgress() {
        handler.postDelayed(updateProgressTask, 1000); // Обновлять каждую секунду
    }

    // Метод для остановки обновления прогресса воспроизведения
    private void stopUpdatingProgress() {
        handler.removeCallbacks(updateProgressTask);
    }

    // Задача для обновления прогресса воспроизведения
    private Runnable updateProgressTask = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentPosition = getCurrentPosition();
                int duration = getDuration();
                seekBar.setMax(duration);
                seekBar.setProgress(currentPosition);
                updateCurrentTime(currentPosition);
                handler.postDelayed(this, 1000); // Повторно запускаем через 1 секунду
            }
        }
    };

    // Метод для изменения громкости
    public void setVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    // Метод для получения текущей громкости
    public float getVolume() {
        if (mediaPlayer != null) {
            //return mediaPlayer.getVolume()[0];
        }
        return 0.0f;
    }

    // Метод для проверки, приостановлено ли воспроизведение
    public boolean isPaused() {
        return isPaused;
    }

    // Метод для проверки, идёт ли воспроизведение
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    // Метод для освобождения ресурсов и остановки
    public void release() {
        if (mediaPlayer != null) {
            stopUpdatingProgress(); // Остановка обновления прогресса
            mediaPlayer.stop(); // Остановка воспроизведения
            mediaPlayer.release(); // Освобождение ресурсов
            mediaPlayer = null; // Обнуление ссылки на MediaPlayer

            // Обнуление информации о треке
            songTitleTextView.setText("Название песни");
            artistTextView.setText("Исполнитель");
            albumTextView.setText("Альбом");
            coverImageView.setImageResource(R.mipmap.ic_launcher);

            // Обнуление прогресса
            seekBar.setProgress(0);
            updateCurrentTime(0);
            updateTotalTime(0);

            isPaused = false;
        }
    }

    public void setLooping(boolean looping) {
        isLooping = looping;
    }

    public boolean isLooping() {
        return isLooping;
    }
}