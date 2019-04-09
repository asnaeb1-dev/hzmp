package com.abhigyan.user.hertzmusicplayer.RecyclerViewAdapters;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.abhigyan.user.hertzmusicplayer.Activities.MainActivity;
import com.abhigyan.user.hertzmusicplayer.CentralQueue;
import com.abhigyan.user.hertzmusicplayer.DailogBoxes.DetailsDailog;
import com.abhigyan.user.hertzmusicplayer.Databases.FavouritesDB;
import com.abhigyan.user.hertzmusicplayer.Fragments.AlbumContentFragment;
import com.abhigyan.user.hertzmusicplayer.R;
import com.abhigyan.user.hertzmusicplayer.Services.MusicService;
import com.abhigyan.user.hertzmusicplayer.Utility.FileDeletion;
import com.abhigyan.user.hertzmusicplayer.Utility.ProcessorTool;
import com.abhigyan.user.hertzmusicplayer.Utility.Song;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CentralQueueRVAdapter extends RecyclerView.Adapter<CentralQueueRVAdapter.Viewholder> {

    private Context context;
    private ArrayList<Song> songAl;
    private ArrayList<String> songData;

    private MusicService musicService;
    private ServiceConnection serviceConnection;
    private boolean serviceBounded = false;
    private Intent binderIntent;

    public CentralQueueRVAdapter(Context context, ArrayList<Song> songAl, ArrayList<String>songData) {
        this.context = context;
        this.songAl = songAl;
        this.songData = songData;
    }

    @NonNull
    @Override
    public CentralQueueRVAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //responsible for inflating the view
        View view = LayoutInflater.from(context).inflate(R.layout.song_list_unit_ui, parent, false);
        //create the object of the Viewholder class down below
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CentralQueueRVAdapter.Viewholder holder, final int position) {
        //changes wrt to what the layout are and add a new item
        //takes the content and shows it on the imageView

        final ProcessorTool processorTool = new ProcessorTool(context);

        if(songAl.get(holder.getAdapterPosition()).getTrackName()!=null)
        {
            holder.trackNameTV.setText(processorTool.reformatTrackName(songAl.get(holder.getAdapterPosition()).getTrackName()));
        }
        else
        {
            holder.trackNameTV.setText("Unknown");
        }
        //------------------------------------------------------------------------------------------
        if(songAl.get(holder.getAdapterPosition()).getAlbumName()!=null)
        {
            holder.albumNameTV.setText("Album- "+processorTool.reformatTrackName(songAl.get(holder.getAdapterPosition()).getAlbumName()));
        }
        else
        {
            holder.albumNameTV.setText("Album- Unknown");
        }
        //------------------------------------------------------------------------------------------


        try {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, songAl.get(holder.getAdapterPosition()).getAlbumID());

            Glide.with(context)
                    .asBitmap()
                    .load(uri)
                    .apply(RequestOptions.placeholderOf(R.drawable.defaultalbumpic).error(R.drawable.defaultalbumpic))
                    .into(holder.coverPics);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //------------------------------------------------------------------------------------------

        holder.optionCIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popup = new PopupMenu(context,holder.optionCIV);
                //inflating menu from dailog xml resource
                popup.inflate(R.menu.central_queue_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.details1:
                                //open details dailog
                                DetailsDailog detailsDailog = new DetailsDailog(context);
                                detailsDailog.makeDetailsDailog(
                                        songAl.get(holder.getAdapterPosition()).getTrackName(),
                                        songAl.get(holder.getAdapterPosition()).getAlbumName(),
                                        songAl.get(holder.getAdapterPosition()).getArtistName(),
                                        processorTool.reformatTime(songAl.get(holder.getAdapterPosition()).getSongDuration()),
                                        processorTool.reformatSize(songAl.get(holder.getAdapterPosition()).getSongSize()),
                                        songAl.get(holder.getAdapterPosition()).getComposerName()
                                );
                                break;

                            case R.id.addTo:
                                popup.dismiss();
                                break;

                            case R.id.albumResource:

                                AlbumContentFragment albumContentsFragment = new AlbumContentFragment();
                                FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                Bundle args = new Bundle();
                                args.putInt("callsc",3 );
                                args.putString("albumname", songAl.get(holder.getAdapterPosition()).getAlbumName());
                                args.putLong("albumid", songAl.get(holder.getAdapterPosition()).getAlbumID());
                                albumContentsFragment.setArguments(args);
                                fragmentTransaction.add(R.id.frameLayoutMain, albumContentsFragment);
                                fragmentTransaction.commit();
                                break;

                            case R.id.artistResource:
                                //directlyOpen the artist resource
                                break;

                            case R.id.removeFromQueue:
                                CentralQueue centralQueue = new CentralQueue(context);
                                boolean success = centralQueue.deleteData(songAl.get(holder.getAdapterPosition()).getTrackName());
                                if(success)
                                {
                                    songAl.remove(holder.getAdapterPosition());
                                    notifyItemRemoved(holder.getAdapterPosition());
                                    MDToast mdToast = MDToast.makeText(context, "Successfully removed!", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                                    mdToast.show();
                                    if(centralQueue.getAllData().getCount()==0)
                                    {
                                        LinearLayout linearLayout = ((MainActivity)context).findViewById(R.id.favsCheckLL);
                                        linearLayout.setVisibility(View.VISIBLE);
                                    }
                                }
                                else
                                {
                                    MDToast mdToast = MDToast.makeText(context, "Failed to removed!", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                                    mdToast.show();
                                }
                                break;

                            case R.id.deleteFromDevice:
                                //confirmation Dailog
                                new FavouritesDB(context);
                                new AlertDialog.Builder(context)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("Delete file")
                                        .setMessage("Are you sure you want to delete this file?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                removeAFile(holder.getAdapterPosition());

                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        }).show();
                                break;

                            default:
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });


        holder.linearLayoutSongList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initializeServiceConnection();
                bindUIwithService(position);
            }
        });
    }

    private void bindUIwithService(int pos)
    {
        binderIntent = new Intent(context, MusicService.class);
        binderIntent.putExtra("callSource", 6);
        binderIntent.putExtra("songlist", songData);
        binderIntent.putExtra("songPos", pos);
        context.bindService(binderIntent,serviceConnection,Context.BIND_AUTO_CREATE);
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

    private void removeAFile(int position) {

        FileDeletion fileDeletion = new FileDeletion(context, songAl);
        boolean confirm = fileDeletion.removeAFile(position);

        if(confirm)
        {
            songAl.remove(position);
            notifyItemRemoved(position);
            MDToast mdToast = MDToast.makeText(context, "File successfully removed.", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
            mdToast.show();
        }
        else
        {
            MDToast mdToast = MDToast.makeText(context, "Sorry! failed to remove file.", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
            mdToast.show();
        }

    }

    public class Viewholder extends RecyclerView.ViewHolder
    {
        TextView trackNameTV, albumNameTV;
        CircleImageView coverPics, optionCIV;
        LinearLayout linearLayoutSongList;

        public Viewholder(View itemView) {
            super(itemView);

            linearLayoutSongList = itemView.findViewById(R.id.songListLL);
            trackNameTV = itemView.findViewById(R.id.songNameTV);
            albumNameTV = itemView.findViewById(R.id.albumNameTV);

            coverPics = itemView.findViewById(R.id.songListCIV);
            optionCIV = itemView.findViewById(R.id.optionsCIV);
        }
    }

}
