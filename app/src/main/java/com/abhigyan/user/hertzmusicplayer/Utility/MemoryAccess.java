package com.abhigyan.user.hertzmusicplayer.Utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import java.util.ArrayList;

public class MemoryAccess {

    private String[] projection ={"*"};
    private Context context;
    private ArrayList<Song> songAL = new ArrayList<>();

    public MemoryAccess(Context context)
    {
        this.context = context;
    }

    //accesses the main memory for songs and all its details
    public void accessMemoryForSongs()
    {
        Cursor detailsCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,null,null,null);
        {
            if(detailsCursor!=null)
            {
                if(detailsCursor.moveToFirst())
                {
                    do{
                        long albumId = detailsCursor.getLong(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                        String trackName = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                        String albumName = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                        String artistName = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                        String composerName = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER));

                        String songLocation = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                        String duration = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                        String size  = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));//main

                        songAL.add(new Song(albumId,trackName,albumName,songLocation,artistName,duration,composerName,size));

                    }while(detailsCursor.moveToNext());
                }
            }
        }
        setSongAL(songAL);
    }

    public void getSongsByAlbum(String albumName)
    {
        albumName = "'"+albumName+"'";
        @SuppressLint("Recycle") Cursor detailsCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,MediaStore.Audio.Media.ALBUM+" = "+albumName,null,null);
        {
            if(detailsCursor!=null)
            {
                if(detailsCursor.moveToFirst())
                {
                    do{
                        long albumId = detailsCursor.getLong(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                        String trackName = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                        String artistName = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                        String composerName = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER));

                        String songLocation = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                        String duration = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                        String size  = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));//main

                        songAL.add(new Song(albumId,trackName,albumName,songLocation,artistName,duration,composerName,size));

                    }while(detailsCursor.moveToNext());
                }
            }
        }
        setSongAL(songAL);
    }

    public void getSongsByArtist(String artistname)
    {
        artistname = "'"+artistname+"'";
        Cursor detailsCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,MediaStore.Audio.Media.ARTIST+" = "+artistname,null,null);
        {
            if(detailsCursor!=null)
            {
                if(detailsCursor.moveToFirst())
                {
                    do{
                        long albumId = detailsCursor.getLong(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                        String trackName = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                        String albumname = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                        String composerName = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER));

                        String songLocation = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                        String duration = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                        String size  = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));//main

                        songAL.add(new Song(albumId,trackName,albumname,songLocation,artistname,duration,composerName,size));

                    }while(detailsCursor.moveToNext());
                }
            }
        }
        setSongAL(songAL);
    }

    public void clearAllData()
    {
      songAL.clear();
    }

    public ArrayList<Song> getSongAL() {
        return songAL;
    }

    public void setSongAL(ArrayList<Song> songAL) {
        this.songAL = songAL;
    }
}
