package com.byteflipper.soulplayer;

import android.content.Context;
import android.widget.SeekBar;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.slider.Slider;
import com.google.android.material.textview.MaterialTextView;

public class PlayerViewModel extends ViewModel {
    private MusicPlayer musicPlayer;
    public MutableLiveData<MusicRepository.Song> currentSong = new MutableLiveData<>();

    public MusicPlayer getPlayerInstance(Context context, Slider seekBar, MaterialTextView currentTimeTextView,
                                         MaterialTextView totalTimeTextView, MaterialTextView songTitleTextView, MaterialTextView artistTextView,
                                         MaterialTextView albumTextView, ShapeableImageView coverImageView, MaterialButton playButton) {
        if (musicPlayer == null) {
            musicPlayer = MusicPlayer.getInstance(context, seekBar, currentTimeTextView, totalTimeTextView,
                    songTitleTextView, artistTextView, albumTextView, coverImageView, playButton);
        }
        return musicPlayer;
    }

    public void play(String songPath) {
        if (musicPlayer != null) {
            musicPlayer.play(songPath);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (musicPlayer != null) {
            musicPlayer.release();
        }
    }
}