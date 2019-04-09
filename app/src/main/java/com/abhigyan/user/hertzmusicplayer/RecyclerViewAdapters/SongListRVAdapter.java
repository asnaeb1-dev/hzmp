package com.abhigyan.user.hertzmusicplayer.RecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import de.hdodenhof.circleimageview.CircleImageView;

public class SongListRVAdapter extends RecyclerView.Adapter<SongListRVAdapter.Viewholder>
{
    private Context context;
    private ArrayList<Song> songsAL;

    private Intent binderIntent;
    private ServiceConnection serviceConnection;
    protected BroadcastReceiver broadcastReceiver1;
    private MusicService musicService;
    private boolean serviceBounded = false;
    private int positionOfPointerOnList;
    private ProcessorTool processorTool;

    private ArrayList<String> songDat;

    public SongListRVAdapter(Context context, ArrayList<Song> songsAL) {
        this.context = context;
        this.songsAL = songsAL;
    }

    @NonNull
    @Override
    public SongListRVAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //responsible for inflating the view
        View view = LayoutInflater.from(context).inflate(R.layout.song_list_unit_ui, parent, false);
        //create the object of the Viewholder class down below
        return new Viewholder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final SongListRVAdapter.Viewholder holder, final int position) {
        //changes wrt to what the layout are and add a new item
        //takes the content and shows it on the imageView

        processorTool = new ProcessorTool(context);

        if(songsAL.get(holder.getAdapterPosition()).getTrackName()!=null)
        {
            holder.trackNameTV.setText(processorTool.reformatTrackName(songsAL.get(holder.getAdapterPosition()).getTrackName()));
        }
        else
        {
            holder.trackNameTV.setText("Unknown");
        }
        //------------------------------------------------------------------------------------------
        if(songsAL.get(position).getAlbumName()!=null)
        {
            holder.albumNameTV.setText("Album- "+processorTool.reformatTrackName(songsAL.get(holder.getAdapterPosition()).getAlbumName()));
        }
        else
        {
            holder.albumNameTV.setText("Album- Unknown");
        }
        //------------------------------------------------------------------------------------------

        holder.optionCIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateOptionsMenuOnList(holder, holder.getAdapterPosition());

            }
        });
        try {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, songsAL.get(holder.getAdapterPosition()).getAlbumID());

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

        songDat = put(songsAL);
        holder.linearLayoutSongList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                positionOfPointerOnList = holder.getAdapterPosition();
                initializeServiceConnection();
                bindUIwithService(positionOfPointerOnList);
                getAllBroadcasts();

            }
        });
    }

    private void activateOptionsMenuOnList(final Viewholder holder, final int position)
    {
        final PopupMenu popup = new PopupMenu(context,holder.optionCIV);
                //inflating menu from dailog xml resource
        popup.inflate(R.menu.options_menu_list);
                //adding click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.addToQueue:
                        CentralQueue centralQueue = new CentralQueue(context);
                        boolean confirmation = centralQueue.insertData(
                                String.valueOf(songsAL.get(position).getAlbumID()),
                                                songsAL.get(position).getTrackName(),
                                                songsAL.get(position).getSongLink(),
                                                    songsAL.get(position).getAlbumName(),
                                                songsAL.get(position).getArtistName(),
                                                songsAL.get(position).getAlbumName(),
                                                songsAL.get(position).getSongDuration(),
                                                songsAL.get(position).getSongSize());

                        if(confirmation)
                        {
                            Toast.makeText(context, "Done!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(context, "Failed to add.", Toast.LENGTH_SHORT).show();
                        }

                        popup.dismiss();
                        break;

                        case R.id.addToF:
                            FavouritesDB favouritesDB = new FavouritesDB(context);
                            boolean confirmationFavs = favouritesDB.insertData(
                                    String.valueOf(songsAL.get(position).getAlbumID()),
                                    songsAL.get(position).getTrackName(),
                                    songsAL.get(position).getSongLink(),
                                    songsAL.get(position).getAlbumName(),
                                    songsAL.get(position).getArtistName(),
                                    songsAL.get(position).getAlbumName(),
                                    songsAL.get(position).getSongDuration(),
                                    songsAL.get(position).getSongSize());

                            if(confirmationFavs)
                            {
                                Toast.makeText(context, "Done!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(context, "Failed to add.", Toast.LENGTH_SHORT).show();
                            }
                            break;

                            case R.id.details1:
                                //open details dailog
                                DetailsDailog detailsDailog = new DetailsDailog(context);
                                detailsDailog.makeDetailsDailog(songsAL.get(position).getTrackName(),
                                        songsAL.get(position).getAlbumName(),
                                        songsAL.get(position).getArtistName(),
                                        processorTool.reformatTime(songsAL.get(position).getSongDuration()),
                                        processorTool.reformatSize(songsAL.get(position).getSongSize()),
                                        songsAL.get(position).getComposerName()
                                );
                                break;

                            case R.id.addTo:
                                popup.dismiss();
                                break;

                            case R.id.albumResource:
                                AppBarLayout appBarLayout =((MainActivity)context).findViewById(R.id.appbarMain);
                                appBarLayout.animate().alpha(0f).setDuration(400);
                                appBarLayout.setVisibility(View.GONE);

                                ViewPager viewPager = ((MainActivity)context).findViewById(R.id.containerVP);
                                viewPager.setVisibility(View.GONE);

                                AlbumContentFragment albumContentsFragment = new AlbumContentFragment();
                                FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                Bundle args = new Bundle();
                                args.putInt("callsc",3 );
                                args.putString("albumname", songsAL.get(position).getAlbumName());
                                args.putLong("albumid", songsAL.get(position).getAlbumID());
                                albumContentsFragment.setArguments(args);
                                fragmentTransaction.add(R.id.frameLayoutMain, albumContentsFragment);
                                fragmentTransaction.commit();
                                break;

                            case R.id.artistResource:
                                //directlyOpen the artist resource
                                break;

                            case R.id.deleteFromDevice:
                                //confirmation Dailog
                                new AlertDialog.Builder(context)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("Delete file")
                                        .setMessage("Are you sure you want to delete this file?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                removeAFile(position);
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

    private void removeAFile(int position) {

        FileDeletion fileDeletion = new FileDeletion(context, songsAL);
        boolean confirm = fileDeletion.removeAFile(position);

        if(confirm)
        {
            songsAL.remove(position);
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

    private static ArrayList<String> put(ArrayList<Song> song)
    {
        ArrayList<String> songData = new ArrayList<>();
        for(int i = 0;i<song.size();i++)
        {
            songData.add(song.get(i).getSongLink());
        }
        return songData;
    }

    //call source = 9 means from song list frag
    private void bindUIwithService(int pos)
    {
        binderIntent = new Intent(context, MusicService.class);
        binderIntent.putExtra("callSource", 9);
        binderIntent.putExtra("songlist", songDat);
        binderIntent.putExtra("songPos", pos);
        context.bindService(binderIntent,serviceConnection,Context.BIND_AUTO_CREATE);
        context.startService(binderIntent);
    }

    private void getAllBroadcasts()
    {
        broadcastReceiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                positionOfPointerOnList = intent.getIntExtra("position", 0);
            }
        };
        context.registerReceiver(broadcastReceiver1, new IntentFilter("abc"));
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
        return songsAL.size();
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
