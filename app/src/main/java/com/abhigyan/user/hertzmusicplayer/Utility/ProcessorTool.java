package com.abhigyan.user.hertzmusicplayer.Utility;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.abhigyan.user.hertzmusicplayer.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.FileDescriptor;
import java.text.DecimalFormat;

public class ProcessorTool {

    private Context context;

    //constructor
    public ProcessorTool(Context context) {
        this.context = context;
    }

    public String reformatTrackName(String trackName)
    {
        //trims the long track name to a comfortable length

        if(trackName.length()>20)
        {
            trackName = trackName.substring(0,20)+"...";
        }
        return trackName;
    }

    public String reformatTime(String time)
    {
        //reformats the time given in milliseconds to hr:min:sec format

        int firstvalue = Integer.parseInt(time);
        firstvalue = Math.round(firstvalue/1000);
        String seconds = String.valueOf(Math.round(firstvalue%60));
        String minutes = String.valueOf(Math.round(firstvalue/60));
        if(seconds.length()!=2)
        {
            seconds = "0"+seconds;
        }

        if(minutes.length()!=2)
        {
            minutes = "0"+minutes;
        }

        return  String.valueOf(minutes)+":"+String.valueOf(seconds);
    }

    public String reformatSize(String size)
    {
        //reformats the audio size given in bytes to MB's or KB's
        float data = Float.parseFloat(size);
        float mbs = data/(1024*1024);

        DecimalFormat df = new DecimalFormat("###.##");
        String mbs1 = String.valueOf(df.format(mbs));

        return mbs1+" MB";
    }

    public String getFileFormat(String fileFormat)
    {
        return fileFormat;
    }

    public String reformatArtistName(String an)
    {
      String[] str = an.replaceAll("[^a-zA-Z0-9]"," ").split(" ");
      if(str.length>1) {
          return str[0] + " " + str[1];
      }return str[0];
    }

    public String reformatAlbumName(String albumName)
    {
        String[] str = albumName.replaceAll("[^a-zA-Z0-9]"," ").split(" ");
        if(str.length>1) {
            return str[0] + " " + str[1];
        }return str[0];
    }
}
