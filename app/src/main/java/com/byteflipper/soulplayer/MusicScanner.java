package com.byteflipper.soulplayer;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class MusicScanner {

    public static class MusicTrack {
        public String title;
        public String artist;
        public String path;

        public MusicTrack(String title, String artist, String path) {
            this.title = title;
            this.artist = artist;
            this.path = path;
        }
    }

    public List<MusicTrack> scanMusicFiles(Context context) {
        List<MusicTrack> musicTracks = new ArrayList<>();

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA
        };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                musicTracks.add(new MusicTrack(title, artist, path));
            }
            cursor.close();
        }

        return musicTracks;
    }
}