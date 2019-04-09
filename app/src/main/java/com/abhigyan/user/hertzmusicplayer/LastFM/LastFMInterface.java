package com.abhigyan.user.hertzmusicplayer.LastFM;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LastFMInterface {

    // http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=arijit%20singh&api_key=99982cc81479d1e700d279d25e838136&format=json
    @GET("/2.0/")
    Call<ArtistInfoClass> getArtistPics(

            @Query("method") String method,
            @Query("artist") String artist,
            @Query("api_key") String apikey,
            @Query("format") String format
    );
}
