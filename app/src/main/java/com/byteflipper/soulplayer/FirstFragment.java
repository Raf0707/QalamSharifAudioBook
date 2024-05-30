package com.byteflipper.soulplayer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.byteflipper.soulplayer.databinding.FragmentFirstBinding;

import java.util.List;

public class FirstFragment extends Fragment {
    private FragmentFirstBinding binding;
    private MusicAdapter adapter;
    private PlayerViewModel playerViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);

        binding.recview.setLayoutManager(new LinearLayoutManager(requireContext()));

        MusicRepository musicRepository = new MusicRepository(requireContext());
        List<MusicRepository.Song> allSongs = musicRepository.getSongs();
        adapter = new MusicAdapter(requireContext(), allSongs, track -> {
            playerViewModel.currentSong.setValue(track);
            playerViewModel.play(track.data);
            FullPlayer nowPlayingFragment = new FullPlayer();
            nowPlayingFragment.show(getParentFragmentManager(), "NowPlayingFragment");
        });
        binding.recview.setAdapter(adapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}