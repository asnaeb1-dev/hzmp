package com.abhigyan.user.hertzmusicplayer;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class NowPlaying {

    Context context;
    private String SongName;
    private String SingerName;
    private String AlbumName;
    private String SongDuration;
    private String SongData;
    private ArrayList<String> songList;
    SharedPreferences pref;

    public NowPlaying(Context context, String songName, String singerName, String albumName, String songDuration, String songData, ArrayList<String> songList) {
        this.context = context;
        SongName = songName;
        SingerName = singerName;
        AlbumName = albumName;
        SongDuration = songDuration;
        SongData = songData;
        this.songList = songList;
        }

    private void saveNowPlaying(String songName, String singerName, String albumName, String songDuration, String songData)
    {
        pref = context.getSharedPreferences("NowPlayingPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();

        editor.putString("songname", songName);
        editor.putString("artistname",singerName);
        editor.putString("album", albumName);
        editor.putString("duration", songDuration);
        editor.putString("data", songData);
        editor.apply();
    }

    public String getSongName()
    {
        return pref.getString("songname",null);
    }

    public String getSongArtist()
    {
        return pref.getString("artistname", null);
    }

    public String getAlbumName()
    {
        return pref.getString("album", null);
    }

    public String getSongData()
    {
        return pref.getString("data", null);
    }
}
