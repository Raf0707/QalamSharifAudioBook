package raf.console.qalamsharifaudio.logic.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.media3.exoplayer.ExoPlayer;

public class PlayerViewModelGlobal extends ViewModel {
    private ExoPlayer exoPlayer;

    // Метод для инициализации ExoPlayer
    public void initializePlayer(Context context) {
        if (exoPlayer == null) {
            exoPlayer = new ExoPlayer.Builder(context).build();
        }
    }

    // Метод для освобождения ресурсов ExoPlayer
    public void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    // Метод для получения ExoPlayer
    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    @Override
    protected void onCleared() {
        releasePlayer(); // Освобождаем ресурсы при уничтожении ViewModel
    }
}