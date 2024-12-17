package com.byteflipper.soulplayer.ui.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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


public class SurasFragment extends Fragment {

    FragmentSurasBinding binding;
    private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;

    private PlayerViewModel playerViewModel;
    private MusicPlayer musicPlayer;
    private final Handler handler = new Handler(Looper.getMainLooper());

    int currentPosition = 0;  // Сохраняем позицию

    public String globalFileName = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSurasBinding.inflate(getLayoutInflater());

        musicPlayer = new MusicPlayer();

        String[] sures = new String[] {
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

        String[] suresTranslate = new String[] {
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

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MusicPlayerPrefs", Context.MODE_PRIVATE);
        String trackPath = sharedPreferences.getString("lastTrack", null);  // Получаем путь последнего трека
        int pos = sharedPreferences.getInt("lastPosition", 0);
        loadTrackData(binding.fullAlbumName, sures);

        binding.sheetMidButton.setOnClickListener(v -> {
            if (musicPlayer.isPlaying()) {
                // Сохраняем текущую позицию при паузе
                musicPlayer.pause(trackPath, getContext()); // Приостановить воспроизведение
            } else {

                // Восстанавливаем позицию и продолжаем воспроизведение
                //musicPlayer.resumeFromCurrentPosition(); // Продолжаем воспроизведение с текущей позиции
                saveTrackData(globalFileName, musicPlayer.getCurrentPosition());
                musicPlayer.seekTo(musicPlayer.getCurrentPosition());
                musicPlayer.play(trackPath);
                musicPlayer.resume();
            }
            updatePlayButtonIcon(); // Обновление иконки кнопки воспроизведения
        });

        List<String> surahList = Arrays.asList(sures);
        List<String> translationList = Arrays.asList(suresTranslate);


        SurasAdapter adapter = new SurasAdapter(surahList, translationList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        FrameLayout bottomSheet = binding.getRoot().findViewById(R.id.quranMP3Player);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setPeekHeight((int) getResources().getDisplayMetrics().density * 80);

        binding.recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(),
                binding.recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // Определяем имя файла
                        int surahIndex = position + 1;  // Индекс начинается с 1
                        String surahFileName = String.format("%03d.mp3", surahIndex);
                        globalFileName = surahFileName;

                        binding.fullAlbumName.setText(sures[position]);

                        // Открытие BottomSheet
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);


                        // Загрузка и проигрывание файла

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

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MusicPlayerPrefs", Context.MODE_PRIVATE);
                        String trackPath = sharedPreferences.getString("lastTrack", null);  // Получаем путь последнего трека
                        int pos = sharedPreferences.getInt("lastPosition", 0);

                        // Обработчик для кнопки паузы/воспроизведения
                        binding.sheetMidButton.setOnClickListener(v -> {
                            if (globalFileName == "") {
                                globalFileName = trackPath;
                            }
                            Log.d("MusicPlayer", "Button clicked");
                            if (musicPlayer.isPlaying()) {
                                // Сохраняем текущую позицию при паузе
                                currentPosition = musicPlayer.getCurrentPosition();
                                Log.d("currentPosition", "currentPosition: " + currentPosition);
                                musicPlayer.pause(globalFileName, getContext()); // Приостановить воспроизведение
                            } else {
                                saveTrackData(globalFileName, musicPlayer.getCurrentPosition());
                                // Восстанавливаем позицию и продолжаем воспроизведение
                                Log.d("MusicPlayer", "Resuming at position: " + currentPosition);
                                musicPlayer.resumeFromCurrentPosition(); // Продолжаем воспроизведение с текущей позиции
                                musicPlayer.seekTo(musicPlayer.getCurrentPosition());
                                musicPlayer.resume();
                            }
                            updatePlayButtonIcon(); // Обновление иконки кнопки воспроизведения
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

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // Обработка долгого нажатия, если требуется
                    }
                }
        ));

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // В зависимости от состояния, выполняем необходимые действия
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        // BottomSheet открыт
                        Log.d("BottomSheet", "Открыт");
                        binding.slideDown.setIconResource(R.drawable.ic_expand_more);
                        binding.recyclerView.setVisibility(View.GONE);
                        if (globalFileName == "") {
                            globalFileName = trackPath;
                            currentPosition = pos;
                            musicPlayer.seekTo(pos);
                        }

                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        // BottomSheet свернут
                        Log.d("BottomSheet", "Свернут");
                        binding.slideDown.setIconResource(R.drawable.ic_expand_off);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        break;

                    case BottomSheetBehavior.STATE_DRAGGING:
                        // BottomSheet в процессе перетаскивания
                        Log.d("BottomSheet", "Перетаскивание");
                        break;

                    case BottomSheetBehavior.STATE_SETTLING:
                        // BottomSheet в процессе анимации
                        Log.d("BottomSheet", "Анимация");
                        break;

                    case BottomSheetBehavior.STATE_HIDDEN:
                        binding.slideDown.setIconResource(R.drawable.ic_expand_off);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        Log.d("BottomSheet", "Скрыт");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Метод, который отслеживает положение BottomSheet во время перетаскивания
                // slideOffset от 0 до 1: 0 - свернуто, 1 - раскрыто

                // Меняем иконку в зависимости от slideOffset
                if (slideOffset > 0.5f) {
                    // Если BottomSheet ближе к раскрытому состоянию, показываем иконку вниз
                    binding.slideDown.setIconResource(R.drawable.ic_expand_more);
                    binding.recyclerView.setVisibility(View.GONE);
                } else {
                    // Если BottomSheet ближе к свернутому состоянию, показываем иконку вверх
                    binding.slideDown.setIconResource(R.drawable.ic_expand_off);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                }

                Log.d("BottomSheet", "Offset: " + slideOffset);
            }
        });

        binding.slideDown.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        binding.sheetMidButton.setOnClickListener(v -> {
            Log.d("MusicPlayer", "Button clicked");
            if (musicPlayer.isPlaying()) {
                // Сохраняем текущую позицию при паузе
                currentPosition = musicPlayer.getCurrentPosition();
                Log.d("currentPosition", "currentPosition: " + currentPosition);
                musicPlayer.pause(globalFileName, getContext()); // Приостановить воспроизведение
            } else {

                    // Восстанавливаем позицию и продолжаем воспроизведение
                    Log.d("MusicPlayer", "Resuming at position: " + currentPosition);
                    musicPlayer.resumeFromCurrentPosition(); // Продолжаем воспроизведение с текущей позиции

            }
            updatePlayButtonIcon(); // Обновление иконки кнопки воспроизведения
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MusicPlayerPrefs", Context.MODE_PRIVATE);
        String trackPath = sharedPreferences.getString("lastTrack", null);  // Получаем путь последнего трека
        int pos = sharedPreferences.getInt("lastPosition", 0);

        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        musicPlayer = new MusicPlayer();

        globalFileName = trackPath;
        musicPlayer.seekTo(musicPlayer.getCurrentPosition());
        // Получаем длительность трека
        int totalDuration = musicPlayer.getDuration();
        if (totalDuration > 0) {
            binding.sliderVert.setValueTo(totalDuration);  // Устанавливаем максимальное значение слайдера
            binding.sliderVert.setValue(pos);  // Устанавливаем слайдер на текущую позицию
            updateCurrentTime(pos);  // Обновляем текущее время
            updateTotalTime(totalDuration);  // Обновляем общее время
            // Загрузка и проигрывание файла
        }


        // Обновление прогресса при воспроизведении
        if (musicPlayer != null && musicPlayer.isPlaying()) {
            currentPosition = musicPlayer.getCurrentPosition();
            int totalDuration1 = musicPlayer.getDuration();
            if (totalDuration1 > 0) {
                binding.sliderVert.setValue(currentPosition);
                binding.sliderVert.setValueTo(totalDuration1);
                updateCurrentTime(currentPosition);
                updateTotalTime(totalDuration);
            }
        }
        startUpdatingProgress();

        // Обработчик для кнопки паузы/воспроизведения
        binding.sheetMidButton.setOnClickListener(v -> {
            if (musicPlayer.isPlaying()) {
                // Сохраняем текущую позицию при паузе
                musicPlayer.pause("/assets/quran/"+trackPath, getContext()); // Приостановить воспроизведение
            } else {

                // Восстанавливаем позицию и продолжаем воспроизведение
                //musicPlayer.resumeFromCurrentPosition(); // Продолжаем воспроизведение с текущей позиции
                if (trackPath != null) {
                    try {
                        AssetFileDescriptor afd = requireContext().getAssets().openFd("quran/" + trackPath);
                        musicPlayer.play(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        musicPlayer.seekTo(currentPosition);
                        afd.close();
                    } catch (IOException e) {
                        Log.e("FullPlayer", "Ошибка загрузки файла суры: " + trackPath, e);
                    }

                }
            }
            updatePlayButtonIcon(); // Обновление иконки кнопки воспроизведения
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
                saveTrackData(globalFileName, musicPlayer.getCurrentPosition());
            }

            @Override
            public void onResumed() {
                startUpdatingProgress();
            }

            @Override
            public void onStopped() {
                stopUpdatingProgress();
                saveTrackData(globalFileName, musicPlayer.getCurrentPosition());
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

    private void showFullPlayer() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void saveTrackData(String trackPath, int position) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MusicPlayerPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastTrack", trackPath); // Сохраняем путь к последнему треку
        editor.putInt("lastPosition", position);   // Сохраняем позицию в миллисекундах
        editor.apply();
    }

    public void loadTrackData(MusicPlayer musicPlayer, MaterialTextView statusTextView) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MusicPlayerPrefs", Context.MODE_PRIVATE);
        String trackPath = sharedPreferences.getString("lastTrack", null);  // Получаем путь последнего трека
        int position = sharedPreferences.getInt("lastPosition", 0);         // Получаем последнюю сохраненную позицию

        if (trackPath != null && position > 0) {
            musicPlayer.play(trackPath); // Загружаем трек
            musicPlayer.seekTo(position); // Устанавливаем позицию
        } else {
            // Если нет сохраненного трека, отображаем текст "Ничего не воспроизводится"
            // Устанавливаем значение позиции как 0
            Log.d("MusicPlayer", "Nothing is playing. Displaying default text and setting position to 0.");

            // Здесь вы можете обновить UI для отображения "Ничего не воспроизводится"
            // Например:
            statusTextView.setText("Ничего не воспроизводится");

            musicPlayer.seekTo(0); // Устанавливаем позицию на 0
        }
    }

    public void loadTrackData(MaterialTextView statusTextView, String[] sures) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MusicPlayerPrefs", Context.MODE_PRIVATE);
        String trackPath = sharedPreferences.getString("lastTrack", null);  // Получаем путь последнего трека
        int position = sharedPreferences.getInt("lastPosition", 0);         // Получаем последнюю сохраненную позицию

        // Проверка, если данных о треке нет, выводим сообщение
        if (trackPath != null && position > 0) {
            // Если путь трека существует и позиция больше 0, загружаем трек и воспроизводим с сохраненной позиции
            musicPlayer.play("/assets/quran/"+trackPath); // Загружаем трек
            musicPlayer.seekTo(position); // Устанавливаем позицию воспроизведения
            statusTextView.setText(getSuraNameFromFileName(trackPath, sures)); // Обновляем статус
        } else {
            // Если трек не найден, выводим сообщение "Ничего не воспроизводится"
            statusTextView.setText("Ничего не воспроизводится");
        }
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


}