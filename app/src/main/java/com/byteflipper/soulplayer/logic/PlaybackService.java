package com.byteflipper.soulplayer.logic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.byteflipper.soulplayer.MusicPlayer;
import com.byteflipper.soulplayer.R;
import com.byteflipper.soulplayer.ui.MainActivity;

public class PlaybackService extends Service {
    private MusicPlayer musicPlayer;
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        PlaybackService getService() {
            return PlaybackService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        musicPlayer = MusicPlayer.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if ("PLAY".equals(action)) {
            String songPath = intent.getStringExtra("SONG_PATH");
            if (songPath != null) {
                playMusic(songPath);
            }
        } else if ("PAUSE".equals(action)) {
            pauseMusic();
        } else if ("STOP".equals(action)) {
            stopMusic();
            stopSelf();
        }
        return START_STICKY;
    }

    private void playMusic(String songPath) {
        MusicPlayer.getInstance(this).play(songPath);
        startForeground(1, getNotification("Playing"));
    }

    private void pauseMusic() {
        MusicPlayer.getInstance(this).pause();
        startForeground(1, getNotification("Paused"));
    }

    private void stopMusic() {
        MusicPlayer.getInstance(this).stop();
        stopForeground(true);
    }

    private Notification getNotification(String playbackStatus) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Создание канала уведомлений для Android 8.0 и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("MusicPlayerChannel",
                    "Music Player",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        return new NotificationCompat.Builder(this, "MusicPlayerChannel")
                .setContentTitle("Music Player")
                .setContentText(playbackStatus)
                .setSmallIcon(R.drawable.play_arrow_24px)
                .setContentIntent(pendingIntent)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (musicPlayer != null) {
            musicPlayer.release();
            musicPlayer = null;
        }
    }
}
