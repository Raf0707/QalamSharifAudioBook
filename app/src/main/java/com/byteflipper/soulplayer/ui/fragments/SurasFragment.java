package com.byteflipper.soulplayer.ui.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.SessionToken;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.byteflipper.soulplayer.MusicPlayer;
import com.byteflipper.soulplayer.PlayerViewModel;
import com.byteflipper.soulplayer.R;
import com.byteflipper.soulplayer.databinding.FragmentSurasBinding;
import com.byteflipper.soulplayer.logic.PlaybackService;
import com.byteflipper.soulplayer.ui.adapters.SurasAdapter;
import com.byteflipper.soulplayer.utils.RecyclerItemClickListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textview.MaterialTextView;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class SurasFragment extends Fragment {

    private FragmentSurasBinding binding;
    private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;
    private ExoPlayer exoPlayer;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int currentPosition = 0; // Сохраняем позицию
    private String globalFileName = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSurasBinding.inflate(getLayoutInflater());

        // Инициализация ExoPlayer
        exoPlayer = new ExoPlayer.Builder(requireContext()).build();

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

                        binding.fullAlbumName.setText(sures[position]);

                        // Открытие BottomSheet
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                        // Загрузка и проигрывание файла
                        playFile(surahFileName);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // Обработка долгого нажатия, если требуется
                    }
                }
        ));

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

        // Обработка слайдера
        binding.sliderVert.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                exoPlayer.seekTo((long) value);
                updateCurrentTime((int) value);
            }
        });

        // Обработка кнопок перемотки и переключения треков
        binding.sheetPreviousSong.setOnClickListener(v -> {
            int currentIndex = getCurrentTrackIndex();
            if (currentIndex > 0) {
                String previousFileName = String.format("%03d.mp3", currentIndex);
                playFile(previousFileName);
            }
        });

        binding.sheetNextSong.setOnClickListener(v -> {
            int currentIndex = getCurrentTrackIndex();
            if (currentIndex < getTotalTracks() - 1) {
                String nextFileName = String.format("%03d.mp3", currentIndex + 2);
                playFile(nextFileName);
            }
        });

        binding.rewindBack.setOnClickListener(v -> {
            long currentPosition = exoPlayer.getCurrentPosition();
            long newPosition = Math.max(0, currentPosition - 10000); // Перемотка на 10 секунд назад
            exoPlayer.seekTo(newPosition);

            // Если плеер на паузе, не начинаем воспроизведение
            if (!exoPlayer.isPlaying()) {
                updatePlayButtonIcon(); // Убедимся, что иконка не изменилась
            }
        });

        binding.rewindForward.setOnClickListener(v -> {
            long currentPosition = exoPlayer.getCurrentPosition();
            long newPosition = Math.min(exoPlayer.getDuration(), currentPosition + 10000); // Перемотка на 10 секунд вперед
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
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                // Этот метод вызывается при переходе на новый трек
                if (mediaItem != null) {
                    // Обновляем название трека
                    binding.fullAlbumName.setText(mediaItem.mediaMetadata.title);

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
                    binding.fullAlbumName.setText(Objects.requireNonNull(exoPlayer.getCurrentMediaItem()).mediaMetadata.title);
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

        handler.post(updateProgressTask);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                // Этот метод вызывается при переходе на новый трек
                if (mediaItem != null) {
                    // Обновляем название трека
                    binding.fullAlbumName.setText(mediaItem.mediaMetadata.title);

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
                    binding.fullAlbumName.setText(Objects.requireNonNull(exoPlayer.getCurrentMediaItem()).mediaMetadata.title);
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

            // Устанавливаем MediaItem в ExoPlayer
            exoPlayer.setMediaItem(mediaItem);

            // Подготавливаем плеер
            exoPlayer.prepare();

            // Начинаем воспроизведение
            exoPlayer.play();
        } catch (Exception e) {
            Log.e("FullPlayer", "Ошибка загрузки файла суры: " + surahFileName, e);
        }
    }

    private void updatePlayButtonIcon() {
        if (exoPlayer.isPlaying()) {
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

    private final Runnable updateProgressTask = new Runnable() {
        @Override
        public void run() {
            if (exoPlayer != null && exoPlayer.isPlaying()) {
                int currentPosition = (int) exoPlayer.getCurrentPosition();
                int totalDuration = (int) exoPlayer.getDuration();
                if (totalDuration > 0) {
                    binding.sliderVert.setValue(currentPosition);
                    updateCurrentTime(currentPosition);
                }
                handler.postDelayed(this, 100); // Обновление каждые 100 мс
            }
        }
    };

    private int getCurrentTrackIndex() {
        // Логика для получения текущего индекса трека
        return Integer.parseInt(globalFileName.replace(".mp3", "")) - 1;
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


}