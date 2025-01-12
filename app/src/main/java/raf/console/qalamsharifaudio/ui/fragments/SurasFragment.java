package raf.console.qalamsharifaudio.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import raf.console.qalamsharifaudio.R;
import raf.console.qalamsharifaudio.databinding.FragmentSurasBinding;
import raf.console.qalamsharifaudio.logic.viewmodel.PlayerViewModelGlobal;
import raf.console.qalamsharifaudio.ui.adapters.SurasAdapter;
import raf.console.qalamsharifaudio.ui.service.PlayerService;
import raf.console.qalamsharifaudio.utils.RecyclerItemClickListener;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;


import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class SurasFragment extends Fragment {

    private FragmentSurasBinding binding;
    private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;
    private ExoPlayer exoPlayer;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int currentPosition = 0; // Сохраняем позицию
    private String globalFileName = "asset:///quran/001.mp3";

    private OnSuraChangedListener suraChangedListener;

    private PlayerViewModelGlobal playerViewModelGlobal;



    private int currentPlaybackMode = 0; // 0 - play-stop, 1 - queue, 2 - repeat one

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerViewModelGlobal = new ViewModelProvider(requireActivity())
                .get(PlayerViewModelGlobal.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSurasBinding.inflate(getLayoutInflater());

        // Инициализация ExoPlayer
        exoPlayer = new ExoPlayer.Builder(requireContext()).build();
        //exoPlayer = playerViewModelGlobal.getExoPlayer();

        Intent serviceIntent = new Intent(requireContext(), PlayerService.class);
        requireContext().startService(serviceIntent);

        // Настройка BottomSheet
        FrameLayout bottomSheet = binding.getRoot().findViewById(R.id.quranMP3Player);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setPeekHeight((int) getResources().getDisplayMetrics().density * 80);

        // Загрузка списка сур
        String[] sures = getSuraNames();
        String[] suresTranslate = getSuraTranslations();

        List<String> surahList = Arrays.asList(sures);
        List<String> translationList = Arrays.asList(suresTranslate);

        SurasAdapter adapter = new SurasAdapter(surahList, translationList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        loadTrackTitle(binding.suraNameMini, sures);

        // Обработка кликов на RecyclerView
        binding.recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(),
                binding.recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // Определяем имя файла
                        int surahIndex = position + 1; // Индекс начинается с 1
                        String surahFileName = String.format("%03d.mp3", surahIndex);
                        globalFileName = surahFileName;

                        binding.suraNameMini.setText(sures[position]);
                        Log.d("SuraName", "Setting text: " + sures[position]);

                        // Проверяем, играет ли уже выбранная сура
                        if (isCurrentTrackN(surahFileName)) {
                            // Если выбранная сура уже играет, продолжаем воспроизведение
                            if (exoPlayer.isPlaying()) {
                                // Просто выдвигаем боттом-шит
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            } else {
                                // Если сура закончила воспроизводиться, начинаем её заново
                                playFile(surahFileName);
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }
                        } else {
                            // Если выбрана другая сура, заменяем текущий трек на новый
                            setNewMediaItem(surahFileName);
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // Обработка долгого нажатия, если требуется
                    }
                }
        ));

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // Обработка изменения состояния BottomSheet
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Перехват событий касания
                bottomSheet.setOnTouchListener((v, event) -> true); // Возвращаем true, чтобы событие не передавалось дальше
            }
        });

        // Обработка кнопки паузы/воспроизведения
        binding.sheetMidButton.setOnClickListener(v -> {
            if (exoPlayer.isPlaying()) {
                exoPlayer.pause();
            } else {
                exoPlayer.play();
                int duration = (int) exoPlayer.getDuration();
                binding.sliderVert.setValueTo(duration);
                updateTotalTime(duration);
            }
            updatePlayButtonIcon();
        });

        binding.sheetMode.setOnClickListener(v -> {
            //switchPlaybackMode();
            Snackbar.make(binding.getRoot(), "В следующих реализациях", Snackbar.LENGTH_SHORT)
                    .show();
        });

        binding.lyrics.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(), "В следующих реализациях", Snackbar.LENGTH_SHORT)
                    .show();
        });

        binding.sheetRandom.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(), "В следующих реализациях", Snackbar.LENGTH_SHORT)
                    .show();
        });

        binding.timer.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(), "В следующих реализациях", Snackbar.LENGTH_SHORT)
                    .show();
        });

        binding.playlist.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(), "В следующих реализациях", Snackbar.LENGTH_SHORT)
                    .show();
        });

        // Обработка слайдера
        binding.sliderVert.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                // Получаем текущую длительность аудиофайла
                long duration = exoPlayer.getDuration();

                // Проверяем, что значение слайдера не превышает длительность
                if (value > duration) {
                    // Устанавливаем значение слайдера на максимум
                    binding.sliderVert.setValue((float) duration);
                    // Перемещаем позицию воспроизведения на максимум
                    exoPlayer.seekTo(duration);
                    // Вызываем метод onComplete()
                    //onComplete();
                } else {
                    // Если значение корректно, перемещаем позицию воспроизведения
                    exoPlayer.seekTo((long) value);
                }
                updateCurrentTime((int) value);
            }
        });

        SurasFragment surasFragment = new SurasFragment();
        surasFragment.setOnSuraChangedListener(suraName -> {
            // Обновляем текст в suraNameMini
            binding.suraNameMini.setText(suraName);
        });
        // Обработка кнопки перемотки назад
        binding.sheetPreviousSong.setOnClickListener(v -> {
            String previousFileName;
            int currentIndex = getCurrentTrackIndex();

            if (currentIndex > 1) {
                // Если текущая сура не первая, переходим к предыдущей
                previousFileName = String.format("%03d.mp3", currentIndex - 1);
                setSuraNameInTextView(previousFileName);
            } else {
                // Если текущая сура первая, переходим к последней
                previousFileName = String.format("%03d.mp3", currentIndex + 113);
                setSuraNameInTextView(previousFileName);
            }

            playFile(previousFileName);
            globalFileName = previousFileName;
            updatePlayButtonIcon();
        });

// Обработка кнопки перемотки вперед
        binding.sheetNextSong.setOnClickListener(v -> {
            String nextFileName;
            int currentIndex = getCurrentTrackIndex();

            if (currentIndex < getTotalTracks() - 1) {
                // Если текущая сура не последняя, переходим к следующей
                nextFileName = String.format("%03d.mp3", currentIndex + 1);
                setSuraNameInTextView(nextFileName);
            } else {
                // Если текущая сура последняя, переходим к первой
                nextFileName = String.format("%03d.mp3", 1);
                setSuraNameInTextView(nextFileName);
            }

            playFile(nextFileName);
            globalFileName = nextFileName;
            updatePlayButtonIcon();
        });

        binding.rewindBack.setOnClickListener(v -> {
            long currentPosition = exoPlayer.getCurrentPosition();
            long newPosition = Math.max(0, currentPosition - 5000); // Перемотка на 10 секунд назад
            exoPlayer.seekTo(newPosition);

            // Если плеер на паузе, не начинаем воспроизведение
            if (!exoPlayer.isPlaying()) {
                updatePlayButtonIcon(); // Убедимся, что иконка не изменилась
            }
        });

        binding.rewindForward.setOnClickListener(v -> {
            long currentPosition = exoPlayer.getCurrentPosition();
            long newPosition = Math.min(exoPlayer.getDuration(), currentPosition + 5000); // Перемотка на 10 секунд вперед
            exoPlayer.seekTo(newPosition);

            // Если плеер на паузе, не начинаем воспроизведение
            if (!exoPlayer.isPlaying()) {
                updatePlayButtonIcon(); // Убедимся, что иконка не изменилась
            }
        });

        // Настройка BottomSheetBehavior
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        binding.slideDown.setIconResource(R.drawable.ic_expand_more);
                        binding.recyclerView.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        binding.slideDown.setIconResource(R.drawable.ic_expand_off);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        binding.slideDown.setIconResource(R.drawable.ic_expand_off);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset > 0.5f) {
                    binding.slideDown.setIconResource(R.drawable.ic_expand_more);
                    binding.recyclerView.setVisibility(View.GONE);
                } else {
                    binding.slideDown.setIconResource(R.drawable.ic_expand_off);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.slideDown.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                binding.recyclerView.setVisibility(View.VISIBLE);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                binding.recyclerView.setVisibility(View.GONE);
            }
        });

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                // Этот метод вызывается при переходе на новый трек
                if (mediaItem != null) {
                    // Обновляем название трека
                    binding.suraNameMini.setText(mediaItem.mediaMetadata.title);

                    // Обновляем прогресс и длительность
                    binding.sliderVert.setValue((int) exoPlayer.getCurrentPosition());
                    binding.sliderVert.setValueTo((int) exoPlayer.getDuration());
                    updateCurrentTime((int) exoPlayer.getCurrentPosition());
                    updateTotalTime((int) exoPlayer.getDuration());

                    // Устанавливаем иконку воспроизведения
                    //binding.sheetMidButton.setIconResource(R.drawable.pause_24px);
                    updatePlayButtonIcon();

                    // Запускаем анимацию обложки (если есть)
                    // showCurrentArtwork();

                    // Если плеер не воспроизводит, запускаем воспроизведение
                    if (!exoPlayer.isPlaying()) {
                        exoPlayer.play();
                    }

                }
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                // Этот метод вызывается при изменении состояния воспроизведения

                if (playbackState == ExoPlayer.STATE_ENDED) {
                    // Обнуляем сохраненное время
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SuraPlayerPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong("lastPlayedPosition", 0);
                    editor.putString("lastSuraFileName", null);
                    editor.apply();
                    updatePlayButtonIcon();
                }

                if (playbackState == ExoPlayer.STATE_READY) {
                    // Обновляем UI, когда плеер готов к воспроизведению
                    if (playbackState == ExoPlayer.STATE_READY) {
                        // Обновляем UI, когда плеер готов к воспроизведению
                        MediaItem currentMediaItem = exoPlayer.getCurrentMediaItem();
                        if (currentMediaItem != null) {
                            binding.suraNameMini.setText(currentMediaItem.mediaMetadata.title);
                        }

                        // Получаем текущую позицию и длительность
                        int currentPosition = (int) exoPlayer.getCurrentPosition();
                        int totalDuration = (int) exoPlayer.getDuration();

                        // Проверяем, что длительность больше 0
                        if (totalDuration > 0) {
                            // Проверяем, что текущая позиция не превышает длительность
                            if (currentPosition > totalDuration) {
                                currentPosition = totalDuration; // Устанавливаем на максимум
                            }

                            binding.sliderVert.setValue(currentPosition);
                            binding.sliderVert.setValueTo(totalDuration);
                            updateCurrentTime(currentPosition);
                            updateTotalTime(totalDuration);
                            updatePlayButtonIcon();
                        }
                    }

                } else {
                    // Если плеер не готов, устанавливаем иконку паузы
                    //binding.sheetMidButton.setIconResource(R.drawable.play_arrow_24px);
                }
                binding.suraNameMini.setText(setSuraNameInText(globalFileName));
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                // Этот метод вызывается при изменении состояния воспроизведения (воспроизведение/пауза)
                if (isPlaying) {
                    binding.sheetMidButton.setIconResource(R.drawable.pause_24px);
                    startUpdatingProgress(); // Запускаем обновление прогресса
                } else {
                    binding.sheetMidButton.setIconResource(R.drawable.play_arrow_24px);
                    stopUpdatingProgress(); // Останавливаем обновление прогресса
                }
            }
        });

        handler.post(updateProgressTask);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Восстанавливаем последний воспроизводимый трек

        if (savedInstanceState != null) {
            restoreLastPlayedSura();

            // Восстанавливаем текущий режим воспроизведения
            restorePlaybackMode();
        }


        // Обновляем UI
        //updatePlayButtonIcon();
        //binding.suraNameMini.setText(setSuraNameInText(globalFileName));

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                // Этот метод вызывается при переходе на новый трек
                if (mediaItem != null) {
                    // Обновляем название трека
                    binding.suraNameMini.setText(mediaItem.mediaMetadata.title);

                    // Обновляем прогресс и длительность
                    binding.sliderVert.setValue((int) exoPlayer.getCurrentPosition());
                    binding.sliderVert.setValueTo((int) exoPlayer.getDuration());
                    updateCurrentTime((int) exoPlayer.getCurrentPosition());
                    updateTotalTime((int) exoPlayer.getDuration());

                    // Устанавливаем иконку воспроизведения
                    //binding.sheetMidButton.setIconResource(R.drawable.pause_24px);
                    updatePlayButtonIcon();

                    // Запускаем анимацию обложки (если есть)
                    // showCurrentArtwork();

                    // Если плеер не воспроизводит, запускаем воспроизведение
                    if (!exoPlayer.isPlaying()) {
                        exoPlayer.play();
                    }
                }
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                // Этот метод вызывается при изменении состояния воспроизведения
                if (playbackState == ExoPlayer.STATE_READY) {
                    // Обновляем UI, когда плеер готов к воспроизведению
                    binding.suraNameMini.setText(Objects.requireNonNull(exoPlayer.getCurrentMediaItem()).mediaMetadata.title);
                    binding.sliderVert.setValue((int) exoPlayer.getCurrentPosition());
                    binding.sliderVert.setValueTo((int) exoPlayer.getDuration());
                    updateCurrentTime((int) exoPlayer.getCurrentPosition());
                    updateTotalTime((int) exoPlayer.getDuration());
                    //binding.sheetMidButton.setIconResource(R.drawable.pause_24px);
                    updatePlayButtonIcon();
                    // Запускаем анимацию обложки (если есть)
                    // showCurrentArtwork();

                } else {
                    // Если плеер не готов, устанавливаем иконку паузы
                    //binding.sheetMidButton.setIconResource(R.drawable.play_arrow_24px);
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                // Этот метод вызывается при изменении состояния воспроизведения (воспроизведение/пауза)
                if (isPlaying) {
                    binding.sheetMidButton.setIconResource(R.drawable.pause_24px);
                    startUpdatingProgress(); // Запускаем обновление прогресса
                } else {
                    binding.sheetMidButton.setIconResource(R.drawable.play_arrow_24px);
                    stopUpdatingProgress(); // Останавливаем обновление прогресса
                }
            }
        });

        // Обновление прогресса
        handler.post(updateProgressTask);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        exoPlayer.release();
        handler.removeCallbacks(updateProgressTask);
    }

    private void startUpdatingProgress() {
        handler.post(updateProgressTask);
    }

    private void stopUpdatingProgress() {
        handler.removeCallbacks(updateProgressTask);
    }

    private void playFile(String surahFileName) {
        try {
            // Создаем URI для ассета
            Uri assetUri = Uri.parse("asset:///quran/" + surahFileName);

            // Создаем MediaItem с использованием URI
            MediaItem mediaItem = MediaItem.fromUri(assetUri);

            exoPlayer.clearMediaItems();

            // Устанавливаем MediaItem в ExoPlayer
            exoPlayer.setMediaItem(mediaItem);

            // Подготавливаем плеер
            exoPlayer.prepare();

            // Начинаем воспроизведение
            exoPlayer.play();

            setSuraNameInTextView(surahFileName);

            binding.suraNameMini.setText(setSuraNameInText(globalFileName));

        } catch (Exception e) {
            Log.e("FullPlayer", "Ошибка загрузки файла суры: " + surahFileName, e);
        }
    }

    private boolean isCurrentTrack(String surahFileName) {
        // Получаем текущий путь трека
        String currentTrackPath = globalFileName; // Предполагаем, что globalFileName содержит текущий трек
        return currentTrackPath != null && currentTrackPath.equals(surahFileName);
    }

    private void updatePlayButtonIcon() {
        if (exoPlayer.isPlaying()) {
            binding.sheetMidButton.setIconResource(R.drawable.pause_24px);
        } else {
            binding.sheetMidButton.setIconResource(R.drawable.play_arrow_24px);
        }
        if (globalFileName != "") binding.suraNameMini.setText(setSuraNameInText(globalFileName));
        else {
            globalFileName = "asset:///quran/001.mp3";
            binding.suraNameMini.setText(setSuraNameInText(globalFileName));
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

    private void onComplete() {
        // Обнуляем сохраненную позицию
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SuraPlayerPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("lastPlayedPosition", 0);
        editor.apply();

        // Устанавливаем слайдер на начало
        exoPlayer.seekTo(0);
        binding.sliderVert.setValue(0);

        // Скрываем BottomSheet (если нужно)
        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // Обновляем UI
        //binding.suraNameMini.setText("");
        updateCurrentTime(0);
        updateTotalTime(0);

        // Переходим к следующей суре (если нужно)
        /*int currentIndex = getCurrentTrackIndex();
        if (currentIndex < getTotalTracks() - 1) {
            String nextFileName = String.format("%03d.mp3", currentIndex + 2);
            playFile(nextFileName);
        }*/
    }

    private void restoreLastPlayedSura() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SuraPlayerPrefs", Context.MODE_PRIVATE);

        // Получаем имя файла последней воспроизведенной суры
        String lastSuraFileName = sharedPreferences.getString("lastSuraFileName", null);

        // Получаем последнюю позицию воспроизведения
        long lastPlayedPosition = sharedPreferences.getLong("lastPlayedPosition", 0);

        if (lastSuraFileName != null) {
            // Устанавливаем BottomSheet в раскрытое состояние
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            binding.recyclerView.setVisibility(View.GONE);
            binding.slideDown.setIconResource(R.drawable.ic_expand_more);

            // Воспроизводим последнюю суру
            globalFileName = lastSuraFileName;
            playFile(lastSuraFileName);

            // Устанавливаем позицию воспроизведения
            exoPlayer.seekTo(lastPlayedPosition);

            // Устанавливаем паузу после восстановления
            exoPlayer.pause();

            binding.suraNameMini.setText(setSuraNameInText(globalFileName));
            loadTrackTitle(binding.suraNameMini, getSuraNames());

            // Обновляем UI
            int suraIndex = Integer.parseInt(lastSuraFileName.replace(".mp3", "")) - 1;
            binding.suraNameMini.setText(getSuraNames()[suraIndex]);

            setSuraNameInTextView(lastSuraFileName);

            // Убедимся, что длительность аудиофайла установлена
            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == ExoPlayer.STATE_READY) {
                        // Установим значение слайдера только после того, как длительность будет известна
                        long duration = exoPlayer.getDuration();
                        if (lastPlayedPosition >= 0 && lastPlayedPosition <= duration) {
                            binding.sliderVert.setValue((float) lastPlayedPosition);
                            binding.sliderVert.setValueTo((float) duration);
                            updateCurrentTime((int) lastPlayedPosition);
                            updateTotalTime((int) duration);
                        }

                        if (exoPlayer.isPlaying()) {
                            //exoPlayer.pause();
                            updatePlayButtonIcon(); // Обновляем иконку кнопки
                        }
                    }

                    if (playbackState == ExoPlayer.STATE_ENDED) {
                        // Вызываем метод onComplete(), когда воспроизведение завершено
                        onComplete();
                    }
                }
            });
        }
    }

    private final Runnable updateProgressTask = new Runnable() {
        @Override
        public void run() {
            if (exoPlayer != null && exoPlayer.isPlaying()) {
                int currentPosition = (int) exoPlayer.getCurrentPosition();
                int totalDuration = (int) exoPlayer.getDuration();

                // Проверяем, что длительность больше 0
                if (totalDuration > 0) {
                    // Проверяем, что текущая позиция не превышает длительность
                    if (currentPosition > totalDuration) {
                        currentPosition = totalDuration; // Устанавливаем на максимум
                    }

                    binding.sliderVert.setValue(currentPosition);
                    updateCurrentTime(currentPosition);
                }

                handler.postDelayed(this, 100); // Обновление каждые 100 мс
            }
        }
    };

    /*private int getCurrentTrackIndex() {
        // Логика для получения текущего индекса трека
        return Integer.parseInt(globalFileName.replace(".mp3", "")) - 1;
    }*/
    private int getCurrentTrackIndex() {
        MediaItem currentMediaItem = exoPlayer.getCurrentMediaItem();
        if (currentMediaItem != null) {
            Uri currentUri = currentMediaItem.localConfiguration.uri;
            if (currentUri != null) {
                String fileName = currentUri.getLastPathSegment();
                if (fileName != null && fileName.endsWith(".mp3")) {
                    try {
                        return Integer.parseInt(fileName.replace(".mp3", ""));
                    } catch (NumberFormatException e) {
                        Log.e("TrackSwitch", "Failed to parse track index from file name: " + fileName, e);
                    }
                }
            }
        }
        return -1;
    }


    private int getTotalTracks() {
        // Логика для получения общего количества треков
        return 114; // Пример для 114 сур
    }
    private String[] getSuraNames() {
        // Возвращает массив имен сур
        return new String[]{
                "Сура 1. Аль-Фатиха",
                "Сура 2. Аль-Бакара",
                "Сура 3. Али Имран",
                "Сура 4. Ан-Ниса",
                "Сура 5. Аль-Маида",
                "Сура 6. Аль-Ан'ам",
                "Сура 7. Аль-А'раф",
                "Сура 8. Аль-Анфаль",
                "Сура 9. Ат-Тауба",
                "Сура 10. Юнус",
                "Сура 11. Худ",
                "Сура 12. Йусуф",
                "Сура 13. Ар-Ра'д",
                "Сура 14. Ибрахим",
                "Сура 15. Аль-Хиджр",
                "Сура 16. Ан-Нахль",
                "Сура 17. Аль-Исра",
                "Сура 18. Аль-Кахф",
                "Сура 19. Марьям",
                "Сура 20. Та-Ха",
                "Сура 21. Аль-Анбия",
                "Сура 22. Аль-Хадж",
                "Сура 23. Аль-Му'минун",
                "Сура 24. Ан-Нур",
                "Сура 25. Аль-Фуркан",
                "Сура 26. Аш-Шуара",
                "Сура 27. Ан-Намль",
                "Сура 28. Аль-Касас",
                "Сура 29. Аль-Анкабут",
                "Сура 30. Ар-Рум",
                "Сура 31. Лукман",
                "Сура 32. Ас-Саджда",
                "Сура 33. Аль-Ахзаб",
                "Сура 34. Саба",
                "Сура 35. Фатыр",
                "Сура 36. Йа Син",
                "Сура 37. Ас-Саффат",
                "Сура 38. Сад",
                "Сура 39. Аз-Зумар",
                "Сура 40. Гафир",
                "Сура 41. Фуссылят",
                "Сура 42. Аш-Шура",
                "Сура 43. Аз-Зухруф",
                "Сура 44. Ад-Духан",
                "Сура 45. Аль-Джасия",
                "Сура 46. Аль-Ахкаф",
                "Сура 47. Мухаммад",
                "Сура 48. Аль-Фатх",
                "Сура 49. Аль-Худжурат",
                "Сура 50. Каф",
                "Сура 51. Аль-Дариат",
                "Сура 52. Ат-Тур",
                "Сура 53. Ан-Наджм",
                "Сура 54. Аль-Камар",
                "Сура 55. Ар-Рахман",
                "Сура 56. Аль-Вакы'а",
                "Сура 57. Аль-Хадид",
                "Сура 58. Аль-Муджадала",
                "Сура 59. Аль-Хашр",
                "Сура 60. Аль-Мумтахина",
                "Сура 61. Ас-Сафф",
                "Сура 62. Аль-Джуму'а",
                "Сура 63. Аль-Мунафикун",
                "Сура 64. Ат-Тагабун",
                "Сура 65. Ат-Талак",
                "Сура 66. Ат-Тахрим",
                "Сура 67. Аль-Мульк",
                "Сура 68. Аль-Калам",
                "Сура 69. Аль-Хакка",
                "Сура 70. Аль-Ма'аридж",
                "Сура 71. Нух",
                "Сура 72. Аль-Джинн",
                "Сура 73. Аль-Муззаммиль",
                "Сура 74. Аль-Муддассир",
                "Сура 75. Аль-Кыяма",
                "Сура 76. Аль-Инсан",
                "Сура 77. Аль-Мурсалят",
                "Сура 78. Ан-Наба",
                "Сура 79. Ан-Назиат",
                "Сура 80. Абаса",
                "Сура 81. Ат-Таквир",
                "Сура 82. Аль-Инфитар",
                "Сура 83. Аль-Мутаффифин",
                "Сура 84. Аль-Иншикак",
                "Сура 85. Аль-Бурудж",
                "Сура 86. Ат-Тарик",
                "Сура 87. Аль-А'ля",
                "Сура 88. Аль-Гашия",
                "Сура 89. Аль-Фаджр",
                "Сура 90. Аль-Баляд",
                "Сура 91. Аш-Шамс",
                "Сура 92. Аль-Ляйл",
                "Сура 93. Ад-Духа",
                "Сура 94. Аш-Шарх",
                "Сура 95. Ат-Тин",
                "Сура 96. Аль-'Аляк",
                "Сура 97. Аль-Кадр",
                "Сура 98. Аль-Баййина",
                "Сура 99. Аз-Зальзаля",
                "Сура 100. Аль-'Адият",
                "Сура 101. Аль-Кари'а",
                "Сура 102. Ат-Такасур",
                "Сура 103. Аль-'Аср",
                "Сура 104. Аль-Хумаза",
                "Сура 105. Аль-Филь",
                "Сура 106. Курайш",
                "Сура 107. Аль-Ма'ун",
                "Сура 108. Аль-Кяусар",
                "Сура 109. Аль-Кяфирун",
                "Сура 110. Ан-Наср",
                "Сура 111. Аль-Ляхаб",
                "Сура 112. Аль-Ихляс",
                "Сура 113. Аль-Фаляк",
                "Сура 114. Ан-Нас"
        };
    }

    private String[] getSuraTranslations() {
        // Возвращает массив переводов сур
        return new String[]{
                "Открывающая",
                "Корова",
                "Семейство Имрана",
                "Женщины",
                "Трапеза",
                "Скот",
                "Преграды",
                "Трофеи",
                "Покаяние",
                "Юнус (Иона)",
                "Худ",
                "Йусуф (Иосиф)",
                "Гром",
                "Ибрахим (Авраам)",
                "Каменное плато",
                "Пчелы",
                "Ночной перенос",
                "Пещера",
                "Марьям (Мария)",
                "Та-Ха",
                "Пророки",
                "Паломничество",
                "Верующие",
                "Свет",
                "Различение",
                "Поэты",
                "Муравьи",
                "Рассказы",
                "Паук",
                "Римляне",
                "Лукман",
                "Поклон",
                "Союзники",
                "Сабейцы",
                "Творец",
                "Йа Син",
                "Выстраивающиеся в ряды",
                "Буква Сад",
                "Толпы",
                "Прощающий",
                "Разъяснены",
                "Совет",
                "Украшения",
                "Дым",
                "Коленопреклоненные",
                "Дюны",
                "Мухаммад",
                "Победа",
                "Комнаты",
                "Буква Каф",
                "Рассеивающие",
                "Гора",
                "Звезда",
                "Луна",
                "Милостивый",
                "Неотвратимое событие",
                "Железо",
                "Препирающаяся",
                "Сбор",
                "Испытуемая",
                "Ряд",
                "Пятница",
                "Лицемеры",
                "Раскрытие самообмана",
                "Развод",
                "Запрещение",
                "Власть",
                "Письменная трость",
                "Неизбежное",
                "Ступени",
                "Нух (Ной)",
                "Джинны",
                "Закутавшийся",
                "Завернувшийся",
                "Воскресение",
                "Человек",
                "Посланные",
                "Весть",
                "Вырывающие",
                "Нахмурившийся",
                "Скручивание",
                "Раскалывание",
                "Обвешивающие",
                "Разверзнется",
                "Созвездия",
                "Ночной путник",
                "Высочайший",
                "Покрывающее",
                "Заря",
                "Город",
                "Солнце",
                "Ночь",
                "Утро",
                "Раскрытие",
                "Инжир",
                "Сгусток крови",
                "Ночь предопределения",
                "Ясное знамение",
                "Землетрясение",
                "Скачущие",
                "Великое бедствие",
                "Страсть к приумножению",
                "Предвечернее время",
                "Хулитель",
                "Слон",
                "Курайшиты",
                "Мелочь",
                "Изобилие",
                "Неверующие",
                "Победа",
                "Пальмовые волокна",
                "Искренность",
                "Рассвет",
                "Люди"
        };
    }

    // Метод для замены текущего трека на новый
    // Метод для замены текущего трека на новый
    private void setNewMediaItem(String surahFileName) {
        try {
            // Создаем URI для ассета
            Uri assetUri = Uri.parse("asset:///quran/" + surahFileName);
            Log.d("ExoPlayer", "Asset URI: " + assetUri.toString());

            // Создаем MediaItem с использованием URI
            MediaItem mediaItem = MediaItem.fromUri(assetUri);

            // Очищаем текущий плейлист
            exoPlayer.clearMediaItems();

            // Устанавливаем MediaItem в ExoPlayer
            exoPlayer.setMediaItem(mediaItem);

            // Подготавливаем плеер
            exoPlayer.prepare();

            // Начинаем воспроизведение
            if (exoPlayer.getPlaybackState() == Player.STATE_IDLE || exoPlayer.getPlaybackState() == Player.STATE_ENDED) {
                exoPlayer.prepare();
            }
            exoPlayer.play();
        } catch (Exception e) {
            Log.e("ExoPlayer", "Ошибка загрузки файла суры: " + surahFileName, e);
        }
    }

    private boolean isCurrentTrackN(String surahFileName) {
        MediaItem currentMediaItem = exoPlayer.getCurrentMediaItem();
        if (currentMediaItem != null) {
            Uri currentUri = currentMediaItem.localConfiguration.uri;
            return currentUri != null && currentUri.toString().endsWith(surahFileName);
        }
        return false;
    }

    private int getSuraIndexFromFileName(String fileName) {
        // Убираем расширение файла и преобразуем в индекс
        String suraNumber = fileName.replace(".mp3", "");
        return Integer.parseInt(suraNumber) - 1; // Индекс начинается с 0
    }

    private void setSuraNameInTextView(String fileName) {
        // Получаем индекс суры
        int suraIndex = getSuraIndexFromFileName(fileName);
        // Получаем название суры из массива sures
        String suraName = getSuraNames()[suraIndex];
        if (suraIndex >= 0 && suraIndex < 114) {
            binding.suraNameMini.setText(suraName);
        } else {
            // Обработка ошибки, если индекс выходит за пределы массива
            binding.suraNameMini.setText("Ошибка: неверный индекс");
        }


        // Устанавливаем название в TextView
        //binding.suraNameMini.setText(suraName);
    }

    private String setSuraNameInText(String fileName) {
        // Получаем индекс суры
        int suraIndex = getSuraIndexFromFileName(fileName);

        // Получаем название суры из массива sures
        String suraName = getSuraNames()[suraIndex];

        // Устанавливаем название в TextView
        return suraName;
    }

    @Override
    public void onPause() {
        super.onPause();
        saveLastPlayedSura();
    }

    private void saveLastPlayedSura() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SuraPlayerPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Сохраняем имя файла текущей суры
        editor.putString("lastSuraFileName", globalFileName);

        // Сохраняем текущую позицию воспроизведения
        editor.putLong("lastPlayedPosition", exoPlayer.getCurrentPosition());

        editor.apply();
    }

    public void setOnSuraChangedListener(OnSuraChangedListener listener) {
        this.suraChangedListener = listener;
    }

    /*private void switchPlaybackMode() {
        // Переключаем режим
        currentPlaybackMode = (currentPlaybackMode + 1) % 3;

        // Сохраняем текущий режим
        savePlaybackMode(currentPlaybackMode);

        // Текущая позиция воспроизведения
        long currentPosition = exoPlayer.getCurrentPosition();

        // Устанавливаем соответствующий режим
        switch (currentPlaybackMode) {
            case 0:
                setupPlayStopMode();
                break;
            case 1:
                setupQueueMode();
                break;
            case 2:
                setupRepeatOneMode();
                break;
        }

        // Обновляем иконку кнопки
        updateLoopButtonIcon();
    }

    private void setupPlayStopMode() {
        // Удаляем обработчик для автоматического переключения треков
        exoPlayer.removeListener(queueModeListener);

        // Сохраняем текущую позицию воспроизведения и состояние
        long currentPosition = exoPlayer.getCurrentPosition();
        boolean isPlaying = exoPlayer.isPlaying();

        // Очищаем плейлист и добавляем только текущий трек
        exoPlayer.clearMediaItems();
        String currentFileName = String.format("%03d.mp3", getCurrentTrackIndex() + 1);
        Uri assetUri = Uri.parse("asset:///quran/" + currentFileName);
        MediaItem mediaItem = MediaItem.fromUri(assetUri);
        exoPlayer.setMediaItem(mediaItem);

        // Подготавливаем плеер
        exoPlayer.prepare();

        // Восстанавливаем позицию воспроизведения
        exoPlayer.seekTo(currentPosition);

        // Если плеер был в состоянии воспроизведения, запускаем трек
        if (isPlaying) {
            exoPlayer.play();
        }

        // Устанавливаем обработчик для завершения трека
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    // Останавливаем плеер после завершения трека
                    exoPlayer.stop();
                }
            }
        });

        // Обновляем UI
        updatePlayButtonIcon();
    }*/



    /*private void setupQueueMode() {
        // Сохраняем текущую позицию и состояние воспроизведения
        long currentPosition = exoPlayer.getCurrentPosition();
        boolean isPlaying = exoPlayer.isPlaying();
        int currentMediaIndex = exoPlayer.getCurrentMediaItemIndex();

        // Удаляем обработчик для других режимов
        exoPlayer.removeListener(repeatOneListener);

        // Создаем список MediaItem для всех 114 сур, начиная с текущего трека
        List<MediaItem> mediaItems = new ArrayList<>();
        for (int i = getCurrentTrackIndex() + 1; i <= 114; i++) { // Треки начиная с текущей суры
            String fileName = String.format("%03d.mp3", i);
            Uri assetUri = Uri.parse("asset:///quran/" + fileName);
            MediaItem mediaItem = MediaItem.fromUri(assetUri);
            mediaItems.add(mediaItem);
        }

        // Очищаем текущий плейлист
        exoPlayer.clearMediaItems();

        // Добавляем треки в плейлист
        exoPlayer.addMediaItems(mediaItems);

        // Подготавливаем плеер
        exoPlayer.prepare();

        // Восстанавливаем позицию и текущий трек
        exoPlayer.seekTo(currentMediaIndex, currentPosition);

        // Если плеер был в состоянии воспроизведения, запускаем трек
        if (isPlaying) {
            exoPlayer.play();
        }

        // Устанавливаем обработчик для автоматического переключения треков
        exoPlayer.addListener(queueModeListener);

        // Обновляем UI
        updatePlayButtonIcon();
    }*/



    /*private void setupRepeatOneMode() {
        // Удаляем обработчик для автоматического переключения треков
        exoPlayer.removeListener(queueModeListener);

        // Сохраняем текущую позицию воспроизведения и состояние
        long currentPosition = exoPlayer.getCurrentPosition();
        boolean isPlaying = exoPlayer.isPlaying();

        // Очищаем плейлист и добавляем только текущий трек
        exoPlayer.clearMediaItems();
        String currentFileName = String.format("%03d.mp3", getCurrentTrackIndex() + 1);
        Uri assetUri = Uri.parse("asset:///quran/" + currentFileName);
        MediaItem mediaItem = MediaItem.fromUri(assetUri);
        exoPlayer.setMediaItem(mediaItem);

        // Подготавливаем плеер
        exoPlayer.prepare();

        // Восстанавливаем позицию воспроизведения
        exoPlayer.seekTo(currentPosition);

        // Если плеер был в состоянии воспроизведения, запускаем трек
        if (isPlaying) {
            exoPlayer.play();
        }

        // Устанавливаем обработчик для повторения текущего трека
        exoPlayer.addListener(repeatOneListener);

        // Обновляем UI
        updatePlayButtonIcon();
    }



    private final Player.Listener queueModeListener = new Player.Listener() {
        @Override
        public void onPlaybackStateChanged(int playbackState) {
            if (playbackState == ExoPlayer.STATE_ENDED) {
                // Переключаемся на следующий трек
                exoPlayer.seekToNext();
            }
        }

        @Override
        public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
            if (mediaItem != null) {
                // Получаем имя файла текущего трека
                String fileName = mediaItem.localConfiguration.uri.getLastPathSegment();

                // Обновляем globalFileName
                globalFileName = fileName;

                // Обновляем suraNameMini
                setSuraNameInTextView(fileName);
            }
        }
    };

    private final Player.Listener repeatOneListener = new Player.Listener() {
        @Override
        public void onPlaybackStateChanged(int playbackState) {
            if (playbackState == ExoPlayer.STATE_ENDED) {
                // Перематываем на начало текущего трека
                exoPlayer.seekTo(0);
                exoPlayer.play();
            }
        }
    };

    private void updateLoopButtonIcon() {
        switch (currentPlaybackMode) {
            case 0:
                binding.sheetMode.setIcon(getResources().getDrawable(R.drawable.play_stop));
                break;
            case 1:
                binding.sheetMode.setIcon(getResources().getDrawable(R.drawable.ic_repeat));
                break;
            case 2:
                binding.sheetMode.setIcon(getResources().getDrawable(R.drawable.ic_repeat_one));
                break;
        }
    }*/

    public void loadTrackTitle(MaterialTextView statusTextView, String[] sures) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MusicPlayerPrefs", Context.MODE_PRIVATE);
        String trackPath = sharedPreferences.getString("lastTrack", null);  // Получаем путь последнего трека
        int position = sharedPreferences.getInt("lastPosition", 0);         // Получаем последнюю сохраненную позицию

        // Проверка, если данных о треке нет, выводим сообщение
        if (trackPath != null && position > 0) {
            // Если путь трека существует и позиция больше 0, загружаем трек и воспроизводим с сохраненной позиции
            //musicPlayer.play("/assets/quran/"+trackPath); // Загружаем трек
            //musicPlayer.seekTo(position); // Устанавливаем позицию воспроизведения
            statusTextView.setText(getSuraNameFromFileName(trackPath, sures)); // Обновляем статус
        } else {
            // Если трек не найден, выводим сообщение "Ничего не воспроизводится"
            statusTextView.setText("Ничего не воспроизводится");
        }
    }

    private void savePlaybackMode(int mode) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SuraPlayerPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("playbackMode", mode);
        editor.apply();
    }

    private void restorePlaybackMode() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SuraPlayerPrefs", Context.MODE_PRIVATE);
        currentPlaybackMode = sharedPreferences.getInt("playbackMode", 0); // По умолчанию режим play-stop

        // Устанавливаем соответствующий режим
        /*switch (currentPlaybackMode) {
            case 0:
                setupPlayStopMode();
                break;
            case 1:
                setupQueueMode();
                break;
            case 2:
                setupRepeatOneMode();
                break;
        }

        // Обновляем иконку кнопки
        updateLoopButtonIcon();*/
    }

    private String getCurrentFileName() {
        MediaItem currentMediaItem = exoPlayer.getCurrentMediaItem();
        if (currentMediaItem != null) {
            return currentMediaItem.localConfiguration.uri.getLastPathSegment();
        }
        String myFile = globalFileName;
        return myFile; // Возвращаем последний известный файл
    }

    public static String getSuraNameFromFileName(String fileName, String[] sures) {
        // Убираем расширение файла .mp3 и извлекаем номер
        String numberStr = fileName.replace(".mp3", "");

        // Преобразуем строку в число
        try {
            int number = Integer.parseInt(numberStr);

            // Проверяем, что номер в пределах массива sures[]
            if (number > 0 && number <= sures.length) {
                // Возвращаем соответствующую сура из массива sures
                return sures[number - 1]; // уменьшаем на 1, т.к. индексация в массиве начинается с 0
            } else {
                return "Неверный номер суры"; // Если номер выходит за пределы массива
            }
        } catch (NumberFormatException e) {
            return "Неверный формат имени файла"; // Если строка не может быть преобразована в число
        }
    }

    public interface OnSuraChangedListener {
        void onSuraChanged(String suraName);
    }

}