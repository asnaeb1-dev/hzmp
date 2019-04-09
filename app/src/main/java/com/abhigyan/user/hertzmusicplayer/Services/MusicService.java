package com.abhigyan.user.hertzmusicplayer.Services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import com.abhigyan.user.hertzmusicplayer.Utility.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is a service class that will play the audio in the background and also bind the UI of the player class
 * with the with the background play
 */

public class MusicService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private MediaPlayer mediaPlayer;
    private IBinder iBinder = new MusicBinder();
    private ArrayList<String> songData = new ArrayList<>();
    private int callSource, previousCallSource = -2;
    private int songPosition =0;
    boolean loopcheck = false;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //get an intent from player class which will play the song
        songPosition = intent.getIntExtra("songPos", 0);
        callSource = intent.getIntExtra("callSource", -1);
        if(previousCallSource != callSource)
        {
            if(callSource == 9) {
                //source is main song list
                songData = intent.getStringArrayListExtra("songlist");
                previousCallSource = callSource;
            }
            else if(callSource == 8)
            {//UNKNOWN
                songData = intent.getStringArrayListExtra("songlist");
                previousCallSource = callSource;
            }
            else if(callSource == 7)
            {//source is favourites
                songData = intent.getStringArrayListExtra("songlist");
                previousCallSource = callSource;
            }
            else if(callSource == 6)
            {//source is queue
                songData = intent.getStringArrayListExtra("songlist");
                previousCallSource = callSource;
            }
        }
        initalizeMediaPlayer(songPosition);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        playNextMedia();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.reset();
        initalizeMediaPlayer(songPosition);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    private void sendPositionBroadcast(int position)
    {
        Intent positionIntent = new Intent("abc");
        positionIntent.putExtra("position", position);
        positionIntent.putExtra("callsource", callSource);
        positionIntent.putExtra("audioSession", mediaPlayer.getAudioSessionId());
        sendBroadcast(positionIntent);
    }


    private void initalizeMediaPlayer(int position)
    {
        //reset previous media player
        mediaPlayer.reset();
        try {//set a data source
            mediaPlayer.setDataSource(songData.get(position));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //prepare media player asynchronously so that mp doesnt block up main thread
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        sendPositionBroadcast(position);
    }

    public void playMedia()
    {
        mediaPlayer.start();
    }

    public void pauseMedia()
    {
        mediaPlayer.pause();
    }

    public void stopAudioPlayback()
    {
        mediaPlayer.stop();
    }

    public void playNextMedia()
    {
        if(songPosition<songData.size()) {
            songPosition++;
        }
        else
        {
            songPosition = 0;
        }
        if(!loopcheck) {
            mediaPlayer.setLooping(true);
        }

        initalizeMediaPlayer(songPosition);
    }

    public void loopSong()
    {
        if(!mediaPlayer.isLooping())
        {
            mediaPlayer.setLooping(true);
            loopcheck = true;
        }
        else
        {
            mediaPlayer.setLooping(false);
            loopcheck = false;
        }
    }

    public void playPreviousMedia()
    {
        if(songPosition>0) {
            songPosition--;
        }
        else
        {
            songPosition = songData.size();
        }

        if(!loopcheck) {
            mediaPlayer.setLooping(true);
        }

        initalizeMediaPlayer(songPosition);
    }
    public class MusicBinder extends Binder
    {
        public MusicService getMusicService()
        {
            return MusicService.this;
        }
    }
}
