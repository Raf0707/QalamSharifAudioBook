package com.byteflipper.soulplayer;

import android.content.ComponentName;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.session.SessionToken;

import com.byteflipper.soulplayer.databinding.FullPlayerBinding;
import com.byteflipper.soulplayer.logic.PlaybackService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;

public class FullPlayer extends BottomSheetDialogFragment {
    private FullPlayerBinding binding;
    private PlayerViewModel playerViewModel;
    private MusicPlayer musicPlayer;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private int currentPosition = 0;  // Сохраняем позицию

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        //setStyle(STYLE_NORMAL, com.google.android.material.R.style.Theme_Material3_DayNight_BottomSheetDialog);


        SessionToken sessionToken =
                new SessionToken(requireContext(), new ComponentName(requireContext(), PlaybackService.class));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FullPlayerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        musicPlayer = new MusicPlayer();

        // Загрузка и проигрывание файла
        String surahFileName = requireArguments().getString("SURAH_FILE_NAME", "");
        try {
            AssetFileDescriptor afd = requireContext().getAssets().openFd("quran/" + surahFileName);
            musicPlayer.play(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
        } catch (IOException e) {
            Log.e("FullPlayer", "Ошибка загрузки файла суры: " + surahFileName, e);
        }

        // Обновление прогресса при воспроизведении
        if (musicPlayer != null && musicPlayer.isPlaying()) {
            currentPosition = musicPlayer.getCurrentPosition();
            int totalDuration = musicPlayer.getDuration();
            if (totalDuration > 0) {
                binding.sliderVert.setValue(currentPosition);
                binding.sliderVert.setValueTo(totalDuration);
                updateCurrentTime(currentPosition);
                updateTotalTime(totalDuration);
            }
        }
        startUpdatingProgress();

        // Обработчик для кнопки паузы/воспроизведения
        binding.sheetMidButton.setOnClickListener(v -> {
            if (musicPlayer.isPlaying()) {
                // Сохраняем текущую позицию при паузе
                currentPosition = musicPlayer.getCurrentPosition();
                musicPlayer.pause();
            } else {
                if (musicPlayer.isCompleted()) {
                    // Если трек завершён, начинаем с начала
                    musicPlayer.seekTo(0);
                    musicPlayer.resume();
                } else {
                    // Восстанавливаем позицию и продолжаем воспроизведение
                    musicPlayer.seekTo(currentPosition);
                    musicPlayer.resume();
                }
            }
            updatePlayButtonIcon();
        });

        // Обработчик событий воспроизведения
        musicPlayer.setOnPlaybackChangeListener(new MusicPlayer.OnPlaybackChangeListener() {
            @Override
            public void onStarted() {
                startUpdatingProgress();
            }

            @Override
            public void onPaused() {
                stopUpdatingProgress();
            }

            @Override
            public void onResumed() {
                startUpdatingProgress();
            }

            @Override
            public void onStopped() {
                stopUpdatingProgress();
            }

            @Override
            public void onCompleted() {
                // Сбрасываем позицию на начало при завершении трека
                currentPosition = 0;
                musicPlayer.seekTo(0);
                updatePlayButtonIcon();
                binding.sliderVert.setValue(0);
                updateCurrentTime(0);
            }

            @Override
            public void onProgressChanged(int progress) {
                binding.sliderVert.setValue(progress);
                updateCurrentTime(progress);
            }

            @Override
            public void onDurationChanged(int duration) {
                binding.sliderVert.setValueTo(duration);
                playerViewModel.updateSongDuration(duration);
            }
        });

        binding.sliderVert.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                musicPlayer.seekTo((int) value);
            }
        });

        // Обновление данных песни
        playerViewModel.currentSong.observe(getViewLifecycleOwner(), song -> {
            if (song != null) {
                binding.fullSongName.setText(song.title);
                binding.fullSongArtist.setText(song.artist);
                binding.fullAlbumName.setText(song.album);

                try {
                    Bitmap albumArt = getAlbumArt(song.data);
                    if (albumArt != null) {
                        binding.fullSheetCover.setImageBitmap(albumArt);
                    } else {
                        binding.fullSheetCover.setImageResource(R.mipmap.ic_launcher);
                    }
                } catch (IOException e) {
                    Log.e("FullPlayer", "Ошибка загрузки обложки", e);
                    binding.fullSheetCover.setImageResource(R.drawable.quran_karim);
                }

                if (musicPlayer.isPlaying() || musicPlayer.isPaused()) {
                    musicPlayer.stop();
                }
                musicPlayer.play(song.data);
                updatePlayButtonIcon();
            }
        });

        playerViewModel.songDuration.observe(getViewLifecycleOwner(), duration -> {
            if (duration != null) {
                binding.sliderVert.setValueTo(duration);
            }
        });

        updatePlayButtonIcon();
    }

    public void resumePlayback() {
        // Восстанавливаем позицию и продолжаем воспроизведение
        musicPlayer.seekTo(currentPosition);
        musicPlayer.resume();
        updatePlayButtonIcon();
    }

    private Bitmap getAlbumArt(String path) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(path);
            byte[] art = retriever.getEmbeddedPicture();
            if (art != null) {
                return BitmapFactory.decodeByteArray(art, 0, art.length);
            }
        } catch (IllegalArgumentException e) {
            Log.e("FullPlayer", "Ошибка получения обложки", e);
        } finally {
            retriever.release();
        }
        return null;
    }

    private void updatePlayButtonIcon() {
        if (musicPlayer.isPlaying()) {
            binding.sheetMidButton.setIconResource(R.drawable.pause_24px);
        } else {
            binding.sheetMidButton.setIconResource(R.drawable.play_arrow_24px);
        }
    }

    private void updateCurrentTime(int currentPosition) {
        int minutes = currentPosition / 60000;
        int seconds = (currentPosition % 60000) / 1000;
        String currentTime = String.format("%02d:%02d", minutes, seconds);
        binding.position.setText(currentTime);
    }

    private void updateTotalTime(int duration) {
        int minutes = duration / 60000;
        int seconds = (duration % 60000) / 1000;
        String totalTime = String.format("%02d:%02d", minutes, seconds);
        binding.duration.setText(totalTime);
    }

    public void updateFile(String surahFileName) {
        try {
            AssetFileDescriptor afd = requireContext().getAssets().openFd("quran/" + surahFileName);
            musicPlayer.play(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
        } catch (IOException e) {
            Log.e("FullPlayer", "Ошибка загрузки файла суры: " + surahFileName, e);
        }
    }

    public void playFile(String surahFileName) {
        // Логика воспроизведения нового файла
        try {
            AssetFileDescriptor afd = requireContext().getAssets().openFd("quran/" + surahFileName);
            musicPlayer.play(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
        } catch (IOException e) {
            Log.e("FullPlayer", "Ошибка загрузки файла суры: " + surahFileName, e);
        }
        updatePlayButtonIcon();
        startUpdatingProgress();
    }

    private void startUpdatingProgress() {
        handler.post(updateProgressTask);
    }

    private void stopUpdatingProgress() {
        handler.removeCallbacks(updateProgressTask);
    }

    private final Runnable updateProgressTask = new Runnable() {
        @Override
        public void run() {
            if (musicPlayer != null && musicPlayer.isPlaying()) {
                int currentPosition = musicPlayer.getCurrentPosition();
                int totalDuration = musicPlayer.getDuration();
                if (totalDuration > 0) {
                    binding.sliderVert.setValue(currentPosition);
                    updateCurrentTime(currentPosition);
                    updateTotalTime(totalDuration);
                }
                handler.postDelayed(this, 100);
            }
        }
    };
}
