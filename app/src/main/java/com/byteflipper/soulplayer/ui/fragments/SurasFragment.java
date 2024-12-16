package com.byteflipper.soulplayer.ui.fragments;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.byteflipper.soulplayer.FullPlayer;
import com.byteflipper.soulplayer.MusicPlayer;
import com.byteflipper.soulplayer.PlayerViewModel;
import com.byteflipper.soulplayer.R;
import com.byteflipper.soulplayer.databinding.FragmentSurasBinding;
import com.byteflipper.soulplayer.ui.adapters.SurasAdapter;
import com.byteflipper.soulplayer.utils.RecyclerItemClickListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class SurasFragment extends Fragment {

    FragmentSurasBinding b;
    private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentSurasBinding.inflate(getLayoutInflater());

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

        List<String> surahList = Arrays.asList(sures);
        List<String> translationList = Arrays.asList(suresTranslate);


        SurasAdapter adapter = new SurasAdapter(surahList, translationList);
        b.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        b.recyclerView.setAdapter(adapter);

        FrameLayout bottomSheet = b.getRoot().findViewById(R.id.quranMP3Player);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setPeekHeight((int) getResources().getDisplayMetrics().density * 80);

        FullPlayer fullPlayer = new FullPlayer();
        MusicPlayer musicPlayer = new MusicPlayer();
        PlayerViewModel playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);

        b.recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(),
                b.recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // Определяем имя файла
                        int surahIndex = position + 1;  // Индекс начинается с 1
                        String surahFileName = String.format("%03d.mp3", surahIndex);

                        // Инициализация BottomSheet
                        //FullPlayer fullPlayer = new FullPlayer();
                        Bundle args = new Bundle();
                        args.putString("SURAH_FILE_NAME", surahFileName);
                        fullPlayer.setArguments(args);

                        // Загрузка и проигрывание файла
                        /*try {
                            AssetFileDescriptor afd = requireContext().getAssets().openFd("quran/" + surahFileName);
                            musicPlayer.play(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                            afd.close();
                        } catch (IOException e) {
                            Log.e("FullPlayer", "Ошибка загрузки файла суры: " + surahFileName, e);
                        }*/

                        // Передаем данные в уже открытый FullPlayer, если он существует
                        /*if (getFragmentManager() != null) {
                            FullPlayer fullPlayer = (FullPlayer) getFragmentManager().findFragmentByTag("FullPlayer");
                            if (fullPlayer != null) {
                                // Устанавливаем новый файл для проигрывания
                                fullPlayer.updateFile(surahFileName);

                                // Запускаем воспроизведение
                                fullPlayer.playFile(surahFileName);
                            }
                        }*/

                        // Открытие BottomSheet
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // Обработка долгого нажатия, если требуется
                    }
                }
        ));

        return b.getRoot();
    }

    private void showFullPlayer() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}