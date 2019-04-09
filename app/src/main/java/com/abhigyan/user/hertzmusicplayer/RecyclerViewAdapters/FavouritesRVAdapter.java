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
import com.abhigyan.user.hertzmusicplayer.Utility.ProcessorTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FavouritesRVAdapter extends RecyclerView.Adapter<FavouritesRVAdapter.Viewholder>
{
    private Context context;
    private ArrayList<String> trackNameAL;
    private ArrayList<String> albumNameAL;
    private ArrayList<String> artistNameAL;
    private ArrayList<String> composerNameAl;
    private ArrayList<String> durationAL;
    private ArrayList<String> sizeAL;
    private ArrayList<String> trackDatAL;
    private ArrayList<Long> albumIDAL;

    private MusicService musicService;
    private ServiceConnection serviceConnection;
    private boolean serviceBounded = false;
    private int positionOfPointerOnList;

    public FavouritesRVAdapter(Context context, ArrayList<String> trackNameAL, ArrayList<String> albumNameAL, ArrayList<String> artistNameAL, ArrayList<String> composerNameAl, ArrayList<String> durationAL, ArrayList<String> sizeAL,ArrayList<String> trackDatAL ,ArrayList<Long> albumIDAL) {
        this.context = context;
        this.trackNameAL = trackNameAL;
        this.albumNameAL = albumNameAL;
        this.artistNameAL = artistNameAL;
        this.composerNameAl = composerNameAl;
        this.durationAL = durationAL;
        this.sizeAL = sizeAL;
        this.trackDatAL = trackDatAL;
        this.albumIDAL = albumIDAL;
    }


    @NonNull
    @Override
    public FavouritesRVAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //responsible for inflating the view
        View view = LayoutInflater.from(context).inflate(R.layout.song_list_unit_ui, parent, false);
        //create the object of the Viewholder class down below
        FavouritesRVAdapter.Viewholder viewholder = new FavouritesRVAdapter.Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FavouritesRVAdapter.Viewholder holder, final int position) {
        //changes wrt to what the layout are and add a new item
        //takes the content and shows it on the imageView

        final ProcessorTool processorTool = new ProcessorTool(context);

        if(trackNameAL.get(position)!=null)
        {
            holder.trackNameTV.setText(processorTool.reformatTrackName(trackNameAL.get(position)));
        }
        else
        {
            holder.trackNameTV.setText("Unknown");
        }
        //------------------------------------------------------------------------------------------
        if(albumNameAL.get(position)!=null)
        {
            holder.albumNameTV.setText("Album- "+processorTool.reformatTrackName(albumNameAL.get(position)));
        }
        else
        {
            holder.albumNameTV.setText("Album- Unknown");
        }
        //------------------------------------------------------------------------------------------


        try {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, albumIDAL.get(position));

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
                popup.inflate(R.menu.favourites_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.addToQueue:
                                CentralQueue centralQueue = new CentralQueue(context);
                                boolean confirmation = centralQueue.insertData(
                                        String.valueOf(albumIDAL.get(position)),
                                        trackNameAL.get(position),
                                        trackDatAL.get(position),
                                        albumNameAL.get(position),
                                        artistNameAL.get(position),
                                        composerNameAl.get(position),
                                        durationAL.get(position),
                                        sizeAL.get(position));

                                if(confirmation)
                                {
                                    Toast.makeText(context, "Done!", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(context, "Failed to add.", Toast.LENGTH_SHORT).show();
                                }

                                popup.dismiss();
                                break;
                            case R.id.details1:
                                //open details dailog
                                DetailsDailog detailsDailog = new DetailsDailog(context);
                                detailsDailog.makeDetailsDailog(
                                        trackNameAL.get(position),
                                        albumNameAL.get(position),
                                        artistNameAL.get(position),
                                        processorTool.reformatTime(durationAL.get(position)),
                                        processorTool.reformatSize(sizeAL.get(position)),
                                        composerNameAl.get(position)
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
                                args.putString("albumname", albumNameAL.get(position));
                                args.putLong("albumid", albumIDAL.get(position));
                                albumContentsFragment.setArguments(args);
                                fragmentTransaction.add(R.id.frameLayoutMain, albumContentsFragment);
                                fragmentTransaction.commit();
                                break;

                            case R.id.artistResource:
                                //directlyOpen the artist resource
                                break;

                            case R.id.removeFromFavs:
                                FavouritesDB favouritesDB = new FavouritesDB(context);
                                boolean success = favouritesDB.deleteData(trackNameAL.get(position));
                                if(success)
                                {
                                    trackNameAL.remove(position);
                                    albumNameAL.remove(position);
                                    albumIDAL.remove(position);
                                    artistNameAL.remove(position);
                                    trackDatAL.remove(position);
                                    composerNameAl.remove(position);
                                    durationAL.remove(position);
                                    sizeAL.remove(position);
                                    notifyItemRemoved(position);
                                    MDToast mdToast = MDToast.makeText(context, "Successfully removed!", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                                    mdToast.show();
                                    if(favouritesDB.getAllData().getCount()==0)
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
        });


       holder.linearLayoutSongList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                positionOfPointerOnList = position;
                initializeServiceConnection();
                bindUIwithService(positionOfPointerOnList);

            }
        });
    }

    private void bindUIwithService(int pos)
    {
        Intent binderIntent = new Intent(context, MusicService.class);
        binderIntent.putExtra("callSource", 7);
        binderIntent.putExtra("songlist", trackDatAL);
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
        return trackNameAL.size();
    }

    private void removeAFile(int position)
    {
        File file = new File(trackDatAL.get(position));
        if(file.exists()) {
            file.delete();
        }

        Uri rootUri = MediaStore.Audio.Media.getContentUriForPath( trackDatAL.get(position) );  // Change file types here
        context.getContentResolver().delete( rootUri,
                MediaStore.MediaColumns.DATA + "=?", new String[]{ trackDatAL.get(position)});

        String message = "File successfully removed.",
                message2 = "Sorry! failed to remove file.";
        if(!file.exists())
        {
            if(new FavouritesDB(context).deleteData(trackNameAL.get(position))) {
                trackNameAL.remove(position);
                albumNameAL.remove(position);
                albumIDAL.remove(position);
                artistNameAL.remove(position);
                trackDatAL.remove(position);
                composerNameAl.remove(position);
                durationAL.remove(position);
                sizeAL.remove(position);

                notifyItemRemoved(position);
                MDToast mdToast = MDToast.makeText(context, message, Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                mdToast.show();
            }
        }
        else
        {
            MDToast mdToast = MDToast.makeText(context, message2, Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
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
