package com.abhigyan.user.hertzmusicplayer.Utility;

import android.content.Context;
import android.content.SharedPreferences;

public class ApplicationSettings {

    private Context context;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public ApplicationSettings(Context context) {
        this.context = context;
        initializeSharedPreference(context);
    }

    public  void initializeSharedPreference(Context context)
    {//instantiate the shared preference
        sharedPreferences = context.getSharedPreferences("APPLICATION_SETTINGS", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    private void applyChanges()
    {// apply changed made to the settings
        editor.apply();
    }

    public int getDarkMode() {
        //dark = 1 means the theme will be set to dark
        // dark(default) = 0 means above feature is disabled
        return sharedPreferences.getInt("dark",0);
    }

    public void setDarkMode(int darkModeAuto) {
        editor.putInt("dark", darkModeAuto);
        applyChanges();
    }

    public void setAlbumArtDownloadFromLastFM(boolean albumArtDownloadFromLastFM) {
        editor.putBoolean("download_album_art", albumArtDownloadFromLastFM);
        applyChanges();
    }

    public boolean isAlbumArtDownloadFromLastFM() {
        //albumArtDownloadFromLastFM = true; let the app download artist art from the internet
        //albumArtDownloadFromLastFM(default) = false; doesnt let the app download art from the internet
        return sharedPreferences.getBoolean("download_album_art", false);
    }

    public boolean isArtistArtDownloadFromLastFM() {
        //artistArtDownloadFromLastFM (default)= true; let the app download artist art from the internet
        //artistArtDownloadFromLastFM = false; doesnt let the app download art from the internet
        return sharedPreferences.getBoolean("download_artist_art", true);
    }

    public void setArtistArtDownloadFromLastFM(boolean artistArtDownloadFromLastFM) {
        editor.putBoolean("download_artist_art", artistArtDownloadFromLastFM);
        applyChanges();

    }

    public boolean isAutoLoop() {
        //autoloop(default) = true; the songs will automatically loop after the last song no need to select options
        //autoloop = false; the song will not loop automatically; however on pressing assigned button it will
        return sharedPreferences.getBoolean("auto_loop", true);
    }

    public void setAutoLoop(boolean autoLoop) {
        editor.putBoolean("auto_loop", autoLoop);
        applyChanges();
    }

    public boolean isRememberLastSongPlayed() {
        //(default)rememberLastSong = true; the app will remember the last song being played and the position as well as the list
        //rememberLastSong = false; the app will not remember anything. start anew
        return sharedPreferences.getBoolean("last_song_played", true);
    }

    public void setRememberLastSongPlayed(boolean rememberLastSongPlayed) {
        editor.putBoolean("last_song_played", rememberLastSongPlayed);
        applyChanges();

    }

    public boolean isAutoEmotionPredict() {
        //autoEmotionPredict = true; the app will automatically put songs into the respective mood databases. very inefficient
        //autoEmotionPredict = false(default); the users have to put songs
        return sharedPreferences.getBoolean("auto_emotion_predict", false);
    }

    public void setAutoEmotionPredict(boolean autoEmotionPredict) {
        editor.putBoolean("auto_emotion_predict", autoEmotionPredict);
        applyChanges();

    }

    public boolean isShowMoodSetterDialog() {
        //show emotion dialog = true(default); will show emotions dialog once every day
        //show emotion dialog = false; will not show emotion dialog; user will manually bring it up
        return sharedPreferences.getBoolean("mood_selector_dialog", true);
    }

    public void setShowMoodSetterDialog(boolean showMoodSetterDialog) {
        editor.putBoolean("mood_selector_dialog", showMoodSetterDialog);
        applyChanges();
    }
}
