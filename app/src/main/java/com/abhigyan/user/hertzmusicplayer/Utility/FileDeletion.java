package com.abhigyan.user.hertzmusicplayer.Utility;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.File;
import java.util.ArrayList;

/**
 *This class enabled the user to delete audio files from the memory directly from the app.
 */

public class FileDeletion {

    private Context context;
    private ArrayList<Song> songsAL;

    public FileDeletion(Context context, ArrayList<Song> songsAL) {
        this.context = context;
        this.songsAL = songsAL;
    }

    public boolean removeAFile(int position)
    {
        File file = new File(songsAL.get(position).getSongLink());
        if(file.exists()) {
            file.delete();
        }

        Uri rootUri = MediaStore.Audio.Media.getContentUriForPath( songsAL.get(position).getSongLink() );  // Change file types here
        context.getContentResolver().delete( rootUri,
                MediaStore.MediaColumns.DATA + "=?", new String[]{ songsAL.get(position).getSongLink()});

        if(!file.exists())
        {
            return true;
        }
        return false;
    }
}
