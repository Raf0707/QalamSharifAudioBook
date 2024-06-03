package com.byteflipper.soulplayer.logic.utils;

import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class LrclibParser {

    private static final String BASE_URL = "https://lrcl.in";
    private LrclibApi api;

    public LrclibParser() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(LrclibApi.class);
    }

    public interface LrclibApi {
        @GET("/api/get")
        Call<Lyrics> getLyrics(
                @Query("track_name") String trackName,
                @Query("artist_name") String artistName,
                @Query("album_name") String albumName,
                @Query("duration") int duration
        );

        @GET("/api/get-cached")
        Call<Lyrics> getCachedLyrics(
                @Query("track_name") String trackName,
                @Query("artist_name") String artistName,
                @Query("album_name") String albumName,
                @Query("duration") int duration
        );

        @GET("/api/get/{id}")
        Call<Lyrics> getLyricsById(@Path("id") int id);

        @GET("/api/search")
        Call<List<Lyrics>> searchLyrics(
                @Query("q") String query,
                @Query("track_name") String trackName,
                @Query("artist_name") String artistName,
                @Query("album_name") String albumName
        );

        @POST("/api/request-challenge")
        Call<ChallengeResponse> requestChallenge();

        @POST("/api/publish")
        Call<Void> publishLyrics(
                // ... (параметры для публикации текста)
        );
    }

    public static class Lyrics {
        public int id;
        public String trackName;
        public String artistName;
        public String albumName;
        public int duration;
        public boolean instrumental;
        public String plainLyrics;
        public String syncedLyrics;
    }

    public static class ChallengeResponse {
        public String prefix;
        public String target;
    }

    public void getLyrics(String trackName, String artistName, String albumName, int duration,
                          Callback<Lyrics> callback) {
        Call<Lyrics> call = api.getLyrics(trackName, artistName, albumName, duration);
        call.enqueue(callback);
    }

    public void getCachedLyrics(String trackName, String artistName, String albumName, int duration,
                                Callback<Lyrics> callback) {
        Call<Lyrics> call = api.getCachedLyrics(trackName, artistName, albumName, duration);
        call.enqueue(callback);
    }

    public void getLyricsById(int id, Callback<Lyrics> callback) {
        Call<Lyrics> call = api.getLyricsById(id);
        call.enqueue(callback);
    }

    public void searchLyrics(String query, String trackName, String artistName, String albumName,
                             Callback<List<Lyrics>> callback) {
        Call<List<Lyrics>> call = api.searchLyrics(query, trackName, artistName, albumName);
        call.enqueue(callback);
    }

    public void requestChallenge(Callback<ChallengeResponse> callback) {
        Call<ChallengeResponse> call = api.requestChallenge();
        call.enqueue(callback);
    }

    // Пример реализации publishLyrics - требует доработки для решения криптозадачи
    public void publishLyrics(
            // ... (параметры для публикации текста),
            String publishToken,
            Callback<Void> callback) {

        // ... (логика получения Publish Token -
        //     реализуйте ее, основываясь на документации LRCLIB)

        Call<Void> call = api.publishLyrics(
                // ... (параметры для публикации текста)
        );
        call.enqueue(callback);
    }

    /*
    parser.getLyrics(trackName, artistName, albumName, duration, new Callback<LrclibParser.Lyrics>() {
            @Override
            public void onResponse(Call<LrclibParser.Lyrics> call, Response<LrclibParser.Lyrics> response) {
                if (response.isSuccessful()) {
                    LrclibParser.Lyrics lyrics = response.body();
                    // Обработка полученного текста песни
                    Log.d("Lyrics", "plainLyrics: " + lyrics.plainLyrics);
                    Log.d("Lyrics", "syncedLyrics: " + lyrics.syncedLyrics);
                } else {
                    // Обработка ошибки
                    Log.e("Lyrics", "Ошибка при получении текста песни: " + response.code());
                }
            }
    */
}