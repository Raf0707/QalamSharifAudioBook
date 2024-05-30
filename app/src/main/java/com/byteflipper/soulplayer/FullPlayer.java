package com.byteflipper.soulplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.byteflipper.soulplayer.databinding.FragmentNowPlayingBinding;
import com.byteflipper.soulplayer.databinding.FullPlayerBinding;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.IOException;

public class FullPlayer extends DialogFragment {
    private FullPlayerBinding binding;
    private MusicPlayer musicPlayer;
    private PlayerViewModel playerViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.BaseSoulPlayerTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FullPlayerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        musicPlayer = playerViewModel.getPlayerInstance(
                requireContext(),
                binding.sliderVert,
                binding.position,
                binding.duration,
                binding.fullSongName,
                binding.fullSongArtist,
                binding.fullAlbumName,
                binding.fullSheetCover,
                binding.sheetMidButton
        );

        musicPlayer.attachLifecycleOwner(getViewLifecycleOwner()); // Присоединяем плеер к жизненному циклу

        playerViewModel.currentSong.observe(getViewLifecycleOwner(), song -> {
            binding.fullAlbumName.setText(song.title);
            binding.fullSongArtist.setText(song.artistName);
            binding.fullAlbumName.setText(song.albumName);
            //binding.seekBarr.setMax((int) song.duration);

            try {
                Bitmap albumArt = getAlbumArt(song.data);
                if (albumArt != null) {
                    binding.fullSheetCover.setImageBitmap(albumArt);
                } else {
                    binding.fullSheetCover.setImageResource(R.mipmap.ic_launcher);
                }
            } catch (IOException e) {
                Log.e("NowPlayingFragment", "Ошибка загрузки обложки", e);
                binding.fullSheetCover.setImageResource(R.mipmap.ic_launcher);
            }

            // Воспроизведение при выборе песни
            musicPlayer.play(song.data);
        });

        binding.sheetMidButton.setOnClickListener(v -> {
            if (musicPlayer.isPlaying()) {
                musicPlayer.pause();
                binding.sheetMidButton.setIconResource(R.drawable.play_arrow_24px);
            } else {
                musicPlayer.resume();
                binding.sheetMidButton.setIconResource(R.drawable.pause_24px);
            }
        });

        binding.sheetNextSong.setOnClickListener(v -> {
            // TODO: Реализовать переход к следующей песне
        });

        binding.sheetPreviousSong.setOnClickListener(v -> {
            // TODO: Реализовать переход к предыдущей песне
        });

        binding.sheetLoop.setOnClickListener(v -> {
            musicPlayer.setLooping(!musicPlayer.isLooping());
            updateShuffleButtonIcon();
        });
    }

    private void updateShuffleButtonIcon() {
        if (musicPlayer.isLooping()) {
            binding.sheetRandom.setIconResource(R.drawable.shuffle_on_24px);
        } else {
            binding.sheetRandom.setIconResource(R.drawable.shuffle_24px);
        }
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
            Log.e("NowPlayingFragment", "Ошибка получения обложки", e);
        } finally {
            retriever.release();
        }
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}