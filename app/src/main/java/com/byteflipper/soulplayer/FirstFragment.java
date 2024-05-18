package com.byteflipper.soulplayer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.byteflipper.soulplayer.databinding.FragmentFirstBinding;

import java.util.List;

public class FirstFragment extends Fragment {
    private FragmentFirstBinding binding;
    private MusicPlayer player;
    MusicAdapter adapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        player = new MusicPlayer(getContext(), binding.seekBar, binding.currentTimeTextView, binding.totalTimeTextView,
                binding.songTitleTextView, binding.artistTextView, binding.albumTextView, binding.coverImageView, binding.playButton);

        binding.playButton.setOnClickListener(v -> {
            if (player.isPlaying()) {
                player.pause();
                binding.playButton.setIconResource(R.drawable.play_arrow_24px);
            } else if (player.isPaused()) {
                player.resume();
                binding.playButton.setIconResource(R.drawable.pause_24px);
            } else {
                player.play(binding.link.getText().toString());
                binding.playButton.setIconResource(R.drawable.pause_24px);
            }
        });

        binding.playButton.setOnLongClickListener(v -> {
            // Освободите ресурсы и создайте новый MusicPlayer
            if (player != null) {
                player.release();
                player = new MusicPlayer(getContext(), binding.seekBar, binding.currentTimeTextView, binding.totalTimeTextView,
                        binding.songTitleTextView, binding.artistTextView, binding.albumTextView, binding.coverImageView, binding.playButton);
            }
            binding.playButton.setIconResource(R.drawable.play_arrow_24px);
            return true;
        });

        binding.repeatButton.setOnClickListener(v -> {
            player.setLooping(!player.isLooping());
            binding.repeatButton.setIconResource(player.isLooping() ? R.drawable.repeat_one_24px : R.drawable.repeat_24px);
        });

        binding.recview.setLayoutManager(new LinearLayoutManager(requireContext())); // Выбор менеджера компоновки

        MusicRepository musicRepository = new MusicRepository(requireContext());
        List<MusicRepository.Song> allSongs = musicRepository.getSongs();
        adapter = new MusicAdapter(requireContext(), allSongs, track -> {
            player.play(track.data);
        });
        binding.recview.setAdapter(adapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (player != null) {
            player.release();
        }
        binding = null;
    }

}