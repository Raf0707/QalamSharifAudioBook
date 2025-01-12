package raf.console.qalamsharifaudio.logic;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

import raf.console.qalamsharifaudio.MusicPlayer;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

public class PlaybackService extends MediaSessionService {

    private MediaSession mediaSession = null;
    private ExoPlayer player;
    private MusicPlayer musicPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        musicPlayer = MusicPlayer.getInstance(this);
        player = new ExoPlayer.Builder(this).build();

        mediaSession = new MediaSession.Builder(this, player)
                .setCallback(new MediaSession.Callback() {
                    @OptIn(markerClass = UnstableApi.class)
                    @NonNull
                    @Override
                    public ListenableFuture<MediaSession.MediaItemsWithStartPosition> onPlaybackResumption(@NonNull MediaSession session, @NonNull MediaSession.ControllerInfo controller) {
                        SettableFuture<MediaSession.MediaItemsWithStartPosition> future = SettableFuture.create();
                        future.set(restorePlaylist());
                        return future;
                    }
                })
                .build();

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    stopSelf();
                }
            }
        });
    }

    @Nullable
    @Override
    public MediaSession onGetSession(@NonNull MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            switch (action) {
                case "PLAY":
                    String source = intent.getStringExtra("source");
                    String title = intent.getStringExtra("title");
                    String artist = intent.getStringExtra("artist");
                    play(source, title, artist);
                    break;
                case "PAUSE":
                    pause();
                    break;
                case "STOP":
                    stop();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    private void play(String source, String title, String artist) {
        musicPlayer.play(source);

        MediaItem mediaItem = new MediaItem.Builder()
                .setUri(source)
                .setMediaMetadata(new MediaMetadata.Builder()
                        .setTitle(title)
                        .setArtist(artist)
                        .build())
                .build();
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    private void pause() {
        musicPlayer.pause();
        player.pause();
    }

    private void stop() {
        musicPlayer.stop();
        player.stop();
        stopSelf();
    }

    @Override
    public void onDestroy() {
        musicPlayer.release();
        player.release();
        mediaSession.release();
        super.onDestroy();
    }

    // Метод для восстановления плейлиста (реализуйте свою логику)
    @OptIn(markerClass = UnstableApi.class)
    private MediaSession.MediaItemsWithStartPosition restorePlaylist() {
        // ...
        return null;
    }
}