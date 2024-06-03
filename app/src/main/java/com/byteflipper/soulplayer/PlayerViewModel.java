package com.byteflipper.soulplayer;

import android.app.Application;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.byteflipper.soulplayer.logic.PlaybackService;

public class PlayerViewModel extends AndroidViewModel {
    public MutableLiveData<MusicRepository.Track> currentSong = new MutableLiveData<MusicRepository.Track>();
    public MutableLiveData<Integer> songDuration = new MutableLiveData<>();

    public PlayerViewModel(@NonNull Application application) {
        super(application);
    }

    public void play(String songPath) {
        Application application = getApplication();
        Intent intent = new Intent(application, PlaybackService.class);
        intent.setAction("PLAY");
        intent.putExtra("SONG_PATH", songPath);
        application.startService(intent);
    }

    public void pause() {
        Application application = getApplication();
        Intent intent = new Intent(application, PlaybackService.class);
        intent.setAction("PAUSE");
        application.startService(intent);
    }

    public void stop() {
        Application application = getApplication();
        Intent intent = new Intent(application, PlaybackService.class);
        intent.setAction("STOP");
        application.startService(intent);
    }

    public void updateSongDuration(int duration) {
        songDuration.setValue(duration);
    }
}