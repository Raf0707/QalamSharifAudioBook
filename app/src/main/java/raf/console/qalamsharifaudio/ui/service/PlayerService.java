package raf.console.qalamsharifaudio.ui.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import raf.console.qalamsharifaudio.R;

public class PlayerService extends Service {

    private ExoPlayer exoPlayer;
    private MediaSessionCompat mediaSession;

    @Override
    public void onCreate() {
        super.onCreate();
        // Инициализация ExoPlayer
        exoPlayer = new ExoPlayer.Builder(this).build();

        // Инициализация MediaSession
        mediaSession = new MediaSessionCompat(this, "PlayerService");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setCallback(new MediaSessionCallback());
        mediaSession.setActive(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Обработка команд (play, pause, stop и т.д.)
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "PLAY":
                    exoPlayer.play();
                    break;
                case "PAUSE":
                    exoPlayer.pause();
                    break;
                case "STOP":
                    stopSelf();
                    break;
            }
        }
        // Показываем уведомление
        showNotification();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
        if (mediaSession != null) {
            mediaSession.release();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void showNotification() {
        // Создаем действия для уведомления (play/pause, stop и т.д.)
        Intent playIntent = new Intent(this, PlayerService.class);
        playIntent.setAction("PLAY");
        PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent pauseIntent = new Intent(this, PlayerService.class);
        pauseIntent.setAction("PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent stopIntent = new Intent(this, PlayerService.class);
        stopIntent.setAction("STOP");
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        MediaItem currentMediaItem = exoPlayer.getCurrentMediaItem();
        if (currentMediaItem != null) {
            String trackTitle = currentMediaItem.mediaMetadata.title.toString();


            // Создаем уведомление
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "player_channel")
                    .setSmallIcon(R.drawable.quran_karim)
                    .setContentTitle(trackTitle)
                    .setContentText("Когда читается Коран, то слушайте его и храните молчание, — быть может, вас помилуют \n Коран, 7:204")
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.album_art))
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(mediaSession.getSessionToken())
                            .setShowActionsInCompactView(0, 1))
                    .addAction(R.drawable.play_arrow_24px, "Play", playPendingIntent)
                    .addAction(R.drawable.pause_24px, "Pause", pausePendingIntent)
                    .addAction(R.drawable.stop_play_fill, "Stop", stopPendingIntent)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            // Запускаем уведомление
            startForeground(1, builder.build());
        }
    }

    public void showNotification(String trackTitle) {
        // Создаем действия для уведомления (play/pause, stop и т.д.)
        Intent playIntent = new Intent(this, PlayerService.class);
        playIntent.setAction("PLAY");
        PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent pauseIntent = new Intent(this, PlayerService.class);
        pauseIntent.setAction("PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent stopIntent = new Intent(this, PlayerService.class);
        stopIntent.setAction("STOP");
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        // Создаем уведомление
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "player_channel")
                .setSmallIcon(R.drawable.quran_karim)
                .setContentTitle(trackTitle) // Используем переданное название трека
                .setContentText("Сейчас играет")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.album_art))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1))
                .addAction(R.drawable.play_arrow_24px, "Play", playPendingIntent)
                .addAction(R.drawable.pause_24px, "Pause", pausePendingIntent)
                .addAction(R.drawable.stop_play_fill, "Stop", stopPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // Запускаем уведомление
        startForeground(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "player_channel",
                    "Player Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Player Service Channel");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            exoPlayer.play();
            showNotification();
        }

        @Override
        public void onPause() {
            exoPlayer.pause();
            showNotification();
        }

        @Override
        public void onStop() {
            exoPlayer.stop();
            stopSelf();
        }
    }
}
