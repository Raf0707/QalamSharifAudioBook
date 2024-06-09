package com.byteflipper.soulplayer.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.byteflipper.soulplayer.FullPlayer;
import com.byteflipper.soulplayer.logic.PlaybackService;
import com.byteflipper.soulplayer.ui.adapters.MusicAdapter;
import com.byteflipper.soulplayer.MusicRepository;
import com.byteflipper.soulplayer.PlayerViewModel;
import com.byteflipper.soulplayer.databinding.FragmentFirstBinding;

import java.util.List;

public class FirstFragment extends Fragment implements MusicAdapter.OnItemClickListener {
    private FragmentFirstBinding binding;
    private MusicAdapter adapter;
    private PlayerViewModel playerViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);

        binding.recview.setLayoutManager(new LinearLayoutManager(requireContext()));

        MusicRepository musicRepository = new MusicRepository();
        List<MusicRepository.Track> allSongs = MusicRepository.getTracks(requireContext());
        adapter = new MusicAdapter(requireContext(), allSongs, this);
        binding.recview.setAdapter(adapter);

        binding.play.setOnClickListener(v -> {
            Intent playIntent = new Intent(getContext(), PlaybackService.class);
            playIntent.setAction("PLAY");
            playIntent.putExtra("source", String.valueOf(binding.link.getText()));
            requireContext().startService(playIntent);

            FullPlayer nowPlayingFragment = new FullPlayer();
            nowPlayingFragment.show(getParentFragmentManager(), "NowPlayingFragment");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(MusicRepository.Track song) {
        playerViewModel.currentSong.setValue(song);
        Intent serviceIntent = new Intent(getContext(), PlaybackService.class);
        serviceIntent.setAction("PLAY");
        serviceIntent.putExtra("source", song.data);
        serviceIntent.putExtra("title", song.title); // Необязательно
        serviceIntent.putExtra("artist", song.artist); // Необязательно
        requireActivity().startService(serviceIntent);

        FullPlayer nowPlayingFragment = new FullPlayer();
        nowPlayingFragment.show(getParentFragmentManager(), "NowPlayingFragment");
    }
}