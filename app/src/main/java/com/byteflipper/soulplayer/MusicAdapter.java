package com.byteflipper.soulplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private List<MusicRepository.Song> songs;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public MusicAdapter(Context context, List<MusicRepository.Song> songs, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.songs = songs != null ? songs : new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_item, parent, false); // R.layout.music_item - ваш макет элемента списка
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicRepository.Song song = songs.get(position);
        holder.titleTextView.setText(song.title);
        holder.artistTextView.setText(song.artistName);

        Bitmap albumArt = null;
        try {
            albumArt = getAlbumArt(song.data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (albumArt != null) {
            holder.albumArtImageView.setImageBitmap(albumArt);
        } else {
            // Установите изображение по умолчанию, если обложка не найдена
            holder.albumArtImageView.setImageResource(R.mipmap.ic_launcher);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void updateSongs(List<MusicRepository.Song> newSongs) {
        songs = newSongs;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView artistTextView;
        public ImageView albumArtImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            artistTextView = itemView.findViewById(R.id.artistTextView);
            albumArtImageView = itemView.findViewById(R.id.albumArtImageView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(MusicRepository.Song song);
    }

    // Метод для получения обложки альбома
    private Bitmap getAlbumArt(String path) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(path);
            byte[] art = retriever.getEmbeddedPicture();
            if (art != null) {
                return BitmapFactory.decodeByteArray(art, 0, art.length);
            }
        } catch (IllegalArgumentException e) {
            Log.e("MusicAdapter", "File does not exist or is not accessible: " + path, e);
        } finally {
            retriever.release();
        }
        return null;
    }
}