package com.byteflipper.soulplayer.ui.fragments.about_app;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.byteflipper.soulplayer.R;
import com.byteflipper.soulplayer.databinding.FragmentAppAboutBinding;
import com.byteflipper.soulplayer.logic.viewmodel.PlayerViewModelGlobal;
import com.byteflipper.soulplayer.utils.CustomTabUtil;
import com.google.android.material.snackbar.Snackbar;



public class AppAboutFragment extends Fragment {

    private FragmentAppAboutBinding binding;
    private int iconId;
    public static String selectTheme = "system";

    private PlayerViewModelGlobal playerViewModelGlobal;

    private SharedPreferences sPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            selectTheme = savedInstanceState.getString("theme");
            //iconId = savedInstanceState.getInt("iconTheme");
            loadTheme(selectTheme);
            //binding.themesBtn.setIcon(getResources().getDrawable(iconId));
            Log.d("onCreate", "load " + selectTheme);
        }

        playerViewModelGlobal = new ViewModelProvider(requireActivity())
                .get(PlayerViewModelGlobal.class);
    }

    @SuppressLint("IntentReset")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAppAboutBinding
                .inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.appVersionBtn.setText("Версия: 1.0.0.0");

        binding.appVersionBtn.setOnLongClickListener(v -> {
            addOnClick(v, getString(R.string.version_copied),
                    ClipData.newPlainText(
                            getString(R.string.getContext),
                            "Версия: 1.0.0.0"));
            return true;
        });

        binding.sourceCodeBtn.setOnLongClickListener(v -> {
            addOnClick(v, getString(R.string.link_to_source_copied),
                    ClipData.newPlainText(getString(R.string.getContext),
                            "https://github.com/Raf0707/QuranTajweed"));
            return true;
        });

        binding.donateBtn.setOnLongClickListener(v -> {
            addOnClick(v, "donate link copied",
                    ClipData.newPlainText(getString(R.string.getContext),
                            "https://www.donationalerts.com/r/raf0707"));
            return true;
        });

        binding.rafailBtn.setOnLongClickListener(v -> {
            addOnClick(v, getString(R.string.raf_git_copylink),
                    ClipData.newPlainText(getString(R.string.getContext),
                            getString(R.string.rafail_url)));
            return true;
        });

        binding.mailRafBtn.setOnLongClickListener(v -> {
            addOnClick(v, getString(R.string.my_email_copylink),
                    ClipData.newPlainText(getString(R.string.getContext),
                            getString(R.string.mail_raf)));
            return true;
        });

        binding.rateBtn.setOnLongClickListener(v -> {
            addOnClick(v, "RuStore link rate copied",
                    ClipData.newPlainText("https://apps.rustore.ru/app/raf.tabiin.qurantajweed",
                            "https://apps.rustore.ru/app/raf.tabiin.qurantajweed"));
            return true;
        });

        binding.vkGroupBtn.setOnLongClickListener(v -> {
            addOnClick(v, getString(R.string.vk_tabiin_coyplink),
                    ClipData.newPlainText(getString(R.string.getContext),
                            getString(R.string.tabiin)));
            return true;
        });

        binding.tgGroupBtn.setOnLongClickListener(v -> {
            addOnClick(v, getString(R.string.tg_tabiin_coyplink),
                    ClipData.newPlainText(getString(R.string.getContext),
                            getString(R.string.tgLink)));
            return true;
        });

        binding.otherAppsBtn.setOnLongClickListener(v -> {
            addOnClick(v, "Tabiin's Apps article link copied",
                    ClipData.newPlainText(getString(R.string.getContext),
                            "https://apps.rustore.ru/developer/ZPBnoCoBczpBFPZK0munW8NSpRTEayCj"));
            return true;
        });

        binding.sourceCodeBtn.setOnClickListener(v -> new CustomTabUtil()
                .openCustomTab(getActivity(),
                        "https://github.com/Raf0707/QuranTajweed",
                        R.color.md_dark_theme_background));

        binding.websiteBtn.setOnClickListener(v -> new CustomTabUtil()
                .openCustomTab(getActivity(),
                        "https://azan.ru/",
                        R.color.md_dark_theme_background));

        binding.websiteBtn.setOnLongClickListener(v -> {
            addOnClick(v, "Azan.ru site's link copied",
                    ClipData.newPlainText(getString(R.string.getContext),
                            "https://azan.ru/"));
            return true;
        });

        binding.downloadQuranBtn.setOnClickListener(v -> new CustomTabUtil()
                .openCustomTab(getActivity(),
                        "https://azan.ru/audio/view/Koran-karim-235",
                        R.color.md_dark_theme_background));

        binding.downloadQuranBtn.setOnLongClickListener(v -> {
            addOnClick(v, "Download Quran's audio link copied",
                    ClipData.newPlainText(getString(R.string.getContext),
                            "https://azan.ru/audio/view/Koran-karim-235"));
            return true;
        });

        binding.tafsirBtn.setOnClickListener(v -> new CustomTabUtil()
                .openCustomTab(getActivity(),
                        "https://azan.ru/tafsir",
                        R.color.md_dark_theme_background));

        binding.tafsirBtn.setOnLongClickListener(v -> {
            addOnClick(v, "Quran's Tafsir link copied",
                    ClipData.newPlainText(getString(R.string.getContext),
                            "https://azan.ru/tafsir"));
            return true;
        });

        binding.byteFlipperGitBtn.setOnClickListener(v -> new CustomTabUtil()
                .openCustomTab(getActivity(),
                        "https://github.com/ByteFlipper-58",
                        R.color.md_dark_theme_background));

        binding.byteFlipperGitBtn.setOnLongClickListener(v -> {
            addOnClick(v, "Quran's Tafsir link copied",
                    ClipData.newPlainText(getString(R.string.getContext),
                            "https://github.com/ByteFlipper-58"));
            return true;
        });

        binding.byteFlipperWebSiteBtn.setOnClickListener(v -> new CustomTabUtil()
                .openCustomTab(getActivity(),
                        "https://byteflipper.web.app/",
                        R.color.md_dark_theme_background));

        binding.byteFlipperWebSiteBtn.setOnLongClickListener(v -> {
            addOnClick(v, "Quran's Tafsir link copied",
                    ClipData.newPlainText(getString(R.string.getContext),
                            "https://byteflipper.web.app/"));
            return true;
        });


        binding.rafailBtn.setOnClickListener(v -> new CustomTabUtil()
                .openCustomTab(getActivity(),
                        getString(R.string.rafail_url),
                        R.color.md_dark_theme_background));


        binding.mailRafBtn.setOnClickListener(v -> {
            final Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setData(Uri.parse(getString(R.string.mailto)))
                    .setType(getString(R.string.text_plain))
                    .putExtra(Intent.EXTRA_EMAIL,
                            new String[]{getString(R.string.mail_raf)})
                    .putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
                    .putExtra(Intent.EXTRA_TEXT,
                            "Версия: 1.0.0.0");

            emailIntent.setType(getString(R.string.text_plain));
            // setType("message/rfc822")

            try {
                startActivity(Intent.createChooser(emailIntent,
                        getString(R.string.email_client)));

            } catch (ActivityNotFoundException ex) {
                Toast.makeText(getActivity(),
                        R.string.no_email_client, Toast.LENGTH_SHORT).show();
            }
        });



        binding.rateBtn.setOnClickListener(v -> new CustomTabUtil()
            .openCustomTab(getActivity(),
                    "https://apps.rustore.ru/app/raf.tabiin.qurantajweed",
                    R.color.md_dark_theme_background));


        binding.vkGroupBtn.setOnClickListener(v -> new CustomTabUtil()
                .openCustomTab(getActivity(),
                        getString(R.string.tabiin),
                        R.color.md_dark_theme_background));

        binding.otherAppsBtn.setOnClickListener(v -> {
            String url = "https://apps.rustore.ru/developer/ZPBnoCoBczpBFPZK0munW8NSpRTEayCj";
            Uri uri = Uri.parse(url);

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.chrome"); // замените на пакет вашего предпочитаемого браузера, если это не Chrome

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // Если браузер не найден, откройте ссылку в стандартном браузере
                intent.setPackage(null);
                startActivity(intent);
            }
        });

        binding.shareAppBtn.setOnClickListener(v -> {
            String appLink = "https://www.rustore.ru/catalog/app/raf.tabiin.qurantajweed";
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Поделитесь этим приложением");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Скачайте приложение Коран с Таджвидом по этой ссылке: " + appLink);

            try {
                v.getContext().startActivity(Intent.createChooser(shareIntent, "Поделиться приложением через"));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(v.getContext(), "Ошибка при попытке поделиться", Toast.LENGTH_SHORT).show();
            }
        });


        binding.shareAppBtn.setOnLongClickListener(v -> {
            addOnClick(v, "https://www.rustore.ru/catalog/app/raf.tabiin.qurantajweed",
                    ClipData.newPlainText(getString(R.string.getContext),
                            "https://www.rustore.ru/catalog/app/raf.tabiin.qurantajweed"));
            return true;
        });

        binding.donateBtn.setOnClickListener(v -> new CustomTabUtil().openCustomTab(getActivity(),
                "https://www.donationalerts.com/r/raf0707", R.color.md_dark_theme_secondary));

        binding.tgGroupBtn.setOnClickListener(v -> new CustomTabUtil()
                .openCustomTab(getActivity(), getString(R.string.tgLink),
                        R.color.md_dark_theme_secondary));

        binding.tgGroupBtn.setOnLongClickListener(v -> {
            addOnClick(v, getString(R.string.tg_tabiin_coyplink),
                    ClipData.newPlainText(getString(R.string.getContext),
                            getString(R.string.tgLink)));
            return true;
        });

        binding.tgBlogBtn.setOnClickListener(v -> new CustomTabUtil()
                .openCustomTab(getActivity(), getString(R.string.tgLinkBlog),
                        R.color.md_dark_theme_secondary));

        binding.tgBlogBtn.setOnLongClickListener(v -> {
            addOnClick(v, getString(R.string.tgLinkBlog),
                    ClipData.newPlainText(getString(R.string.tgLinkBlog),
                            getString(R.string.tgLinkBlog)));
            return true;
        });
    }

    public void addOnClick(View view, String text, ClipData clipData) {
        ClipboardManager clipboardManager = (ClipboardManager)
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE);

        clipboardManager.setPrimaryClip(clipData);
        Snackbar.make(requireView(), text, Snackbar.LENGTH_LONG).show();
    }
    public void saveTheme(String selectTheme) {
        Bundle tranBundle = new Bundle();
        FragmentManager fragmentManager  = getFragmentManager();
        AppAboutFragment appAboutFragment = new AppAboutFragment();
        tranBundle.putString("thm", selectTheme);
        appAboutFragment.setArguments(tranBundle);
    }
    public void loadTheme(String selectTheme) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String selectThm = bundle.getString("thm");
            selectTheme = selectThm;
            if (selectTheme.equals("system")) {
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }

                //saveTheme(selectTheme);
                requireActivity().recreate();

            } else if (selectTheme.equals("dark")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                requireActivity().recreate();

            } else if (selectTheme.equals("light")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                requireActivity().recreate();

            }
        }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("theme", selectTheme);
        Log.d("onSaveInstanceState", "save " + selectTheme);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        Log.d("onViewStateRestored", "restore " + selectTheme);
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        saveTheme(selectTheme);
        Log.d("onDestroy", "save " + selectTheme);
        super.onDestroy();
    }


}