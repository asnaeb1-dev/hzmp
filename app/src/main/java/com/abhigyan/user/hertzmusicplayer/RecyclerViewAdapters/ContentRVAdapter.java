package com.abhigyan.user.hertzmusicplayer.RecyclerViewAdapters;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abhigyan.user.hertzmusicplayer.R;
import com.abhigyan.user.hertzmusicplayer.Services.MusicService;
import com.abhigyan.user.hertzmusicplayer.Utility.ProcessorTool;
import com.abhigyan.user.hertzmusicplayer.Utility.Song;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContentRVAdapter extends RecyclerView.Adapter<ContentRVAdapter.Viewholder> {
    private Context context;
    private Intent binderIntent;
    private ServiceConnection serviceConnection;
    protected BroadcastReceiver broadcastReceiver1;
    private MusicService musicService;
    private boolean serviceBounded = false;
    private int positionOfPointerOnList;
    private int callSource;

    private ArrayList<Song> songAl;

    public ContentRVAdapter(Context context,ArrayList<Song> songAl, int callSource) {
        this.context = context;
        this.songAl = songAl;
        this.callSource = callSource;
    }

    @NonNull
    @Override
    public ContentRVAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //responsible for inflating the view
        View view = LayoutInflater.from(context).inflate(R.layout.album_content_unit_ui, parent, false);
        //create the object of the Viewholder class down below
        ContentRVAdapter.Viewholder viewholder = new ContentRVAdapter.Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ContentRVAdapter.Viewholder holder, final int position) {
        //changes wrt to what the layout are and add a new item
        //takes the content and shows it on the imageView

        ProcessorTool processorTool = new ProcessorTool(context);

        if (songAl.get(holder.getAdapterPosition()).getTrackName() != null) {
            holder.trackNameTVContent.setText(processorTool.reformatTrackName(songAl.get(holder.getAdapterPosition()).getTrackName()));
        } else {
            holder.trackNameTVContent.setText("Unknown");
        }
        //------------------------------------------------------------------------------------------
        if(callSource == 1) {
            if (songAl.get(holder.getAdapterPosition()).getArtistName() != null) {
                holder.albumNameTVContent.setText("Artist- " + processorTool.reformatTrackName(songAl.get(holder.getAdapterPosition()).getArtistName()));
            } else {
                holder.albumNameTVContent.setText("Artist- Unknown");
            }
        }else if(callSource == 2)
        {
            if(songAl.get(holder.getAdapterPosition()).getAlbumName()!=null)
            {
                holder.albumNameTVContent.setText("Album- " + processorTool.reformatTrackName(songAl.get(holder.getAdapterPosition()).getAlbumName()));
            }
            else
            {
                holder.albumNameTVContent.setText("Album- Unknown");
            }
        }
        //------------------------------------------------------------------------------------------
        //------------------------------------------------------------------------------------------
        holder.linearLayoutSongListContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                positionOfPointerOnList = holder.getAdapterPosition();
                initializeServiceConnection();
                bindUIwithService(positionOfPointerOnList);
            }
        });
    }

    //call source = 9 means from song list frag
    private void bindUIwithService(int pos) {
        binderIntent = new Intent(context, MusicService.class);
        binderIntent.putExtra("callSource", 8);
        binderIntent.putExtra("songlist", songAl);
        binderIntent.putExtra("songPos", pos);
        context.bindService(binderIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        context.startService(binderIntent);
    }


    private void initializeServiceConnection() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

                MusicService.MusicBinder binder = (MusicService.MusicBinder) iBinder;
                musicService = binder.getMusicService();
                serviceBounded = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                serviceBounded = false;
            }
        };
    }

    @Override
    public int getItemCount() {
        //this sets the size of the recycler view
        //without this the recycler view will show 0 items
        return songAl.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView trackNameTVContent, albumNameTVContent;
        CircleImageView optionCIVContent;
        LinearLayout linearLayoutSongListContent;

        public Viewholder(View itemView) {
            super(itemView);

            linearLayoutSongListContent = itemView.findViewById(R.id.songListLLContent);
            trackNameTVContent = itemView.findViewById(R.id.songNameTVContent);
            albumNameTVContent = itemView.findViewById(R.id.albumNameTVContent);
            optionCIVContent = itemView.findViewById(R.id.optionsCIVContent);
        }
    }
}
