package com.abhigyan.user.hertzmusicplayer.Fragments;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abhigyan.user.hertzmusicplayer.Activities.MainActivity;
import com.abhigyan.user.hertzmusicplayer.CentralQueue;
import com.abhigyan.user.hertzmusicplayer.Databases.FavouritesDB;
import com.abhigyan.user.hertzmusicplayer.R;
import com.abhigyan.user.hertzmusicplayer.RecyclerViewAdapters.CentralQueueRVAdapter;
import com.abhigyan.user.hertzmusicplayer.RecyclerViewAdapters.FavouritesRVAdapter;
import com.abhigyan.user.hertzmusicplayer.RecyclerViewAdapters.SongListRVAdapter;
import com.abhigyan.user.hertzmusicplayer.Services.MusicService;
import com.abhigyan.user.hertzmusicplayer.Utility.Song;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

/**
 * Queue fragment.
 */
public class QueueFragment extends Fragment {

    RecyclerView queueRV;
    Toolbar queueToolbar;
    LinearLayout linearLayout;
    private ArrayList<Song> songAL = new ArrayList<>();
    private ArrayList<String> songDataAl = new ArrayList<>();
    private CentralQueue centralQueue;
    private CentralQueueRVAdapter adapter;
    FloatingActionButton floatingActionButton;
    boolean serviceBounded = false;
    ServiceConnection serviceConnection;
    MusicService musicService;
    Intent binderIntent;
    private View myView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.central_queue, container, false);
        queueRV = myView.findViewById(R.id.queueListRV);
        queueToolbar = myView.findViewById(R.id.toolBarQueue);
        linearLayout = myView.findViewById(R.id.SadQueue);
        floatingActionButton = myView.findViewById(R.id.queueFABbutton);
        queueToolbar.setTitle("Queue");
        queueToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        queueToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        enableNavigationClickListener();

        linearLayout.setVisibility(View.GONE);

        queueToolbar.inflateMenu(R.menu.central_queue_main_menu);
        queueToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                int id = menuItem.getItemId();
                if(id == R.id.clearQueue)
                {
                    createClearQueueAlertDialog();
                }
                return false;
            }
        });

        return myView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        centralQueue = new CentralQueue(getContext());
        // clearAllLists();
        addAllDataToDatabase();

        adapter = new CentralQueueRVAdapter(getContext(), songAL, songDataAl);
        queueRV.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        queueRV.setLayoutManager(linearLayoutManager);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Random rand = new Random();
                int x = rand.nextInt(songAL.size()-1);
                initializeServiceConnection();
                bindUIwithService(x);
            }
        });
    }

    private void enableNavigationClickListener()
    {
        queueToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrameLayout frameLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.frameLayoutMain);
                if(frameLayout.getChildCount()!=0) {
                    frameLayout.removeAllViews();
                    AlbumContentFragment albumContentsFragment = new AlbumContentFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    assert fragmentManager != null;
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(albumContentsFragment);
                    fragmentTransaction.commit();
                }

                AppBarLayout appBarLayout = getActivity().findViewById(R.id.appbarMain);
                appBarLayout.animate().alpha(1f).setDuration(400);
                appBarLayout.setVisibility(View.VISIBLE);

                ViewPager viewPager = getActivity().findViewById(R.id.containerVP);
                viewPager.setVisibility(View.VISIBLE);
            }
        });
    }

    private void createClearQueueAlertDialog()
    {
        new AlertDialog.Builder(getContext())
                .setMessage("Clear entire queue?")
                .setTitle("Are you sure?")
                .setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        boolean confirm = centralQueue.deleteThisDatabase(getContext());

                        if(confirm)
                        {
                            MDToast mdToast = MDToast.makeText(Objects.requireNonNull(getContext()), "Queue cleared!", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                            mdToast.show();
                            clearAllLists();
                            if(centralQueue.getAllData().getCount() == 0) {
                                adapter.notifyDataSetChanged();
                                linearLayout.setVisibility(View.VISIBLE);
                                FrameLayout frameLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.frameLayoutMain);
                                if (frameLayout.getChildCount() != 0) {
                                    frameLayout.removeAllViews();
                                    AlbumContentFragment albumContentsFragment = new AlbumContentFragment();
                                    FragmentManager fragmentManager = getFragmentManager();
                                    assert fragmentManager != null;
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.remove(albumContentsFragment);
                                    fragmentTransaction.commit();
                                }
                                AppBarLayout appBarLayout = getActivity().findViewById(R.id.appbarMain);
                                appBarLayout.animate().alpha(1f).setDuration(400);
                                appBarLayout.setVisibility(View.VISIBLE);

                                ViewPager viewPager = getActivity().findViewById(R.id.containerVP);
                                viewPager.setVisibility(View.VISIBLE);

                            }
                        }
                        else
                        {
                            MDToast mdToast = MDToast.makeText(Objects.requireNonNull(getContext()), "Failed to clear queue.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                            mdToast.show();
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }



    private void bindUIwithService(int pos)
    {
        binderIntent = new Intent(Objects.requireNonNull(getContext()), MusicService.class);
        binderIntent.putExtra("callSource", 6);
        binderIntent.putExtra("songlist", songDataAl);
        binderIntent.putExtra("songPos", pos);
        Objects.requireNonNull(getContext()).bindService(binderIntent,serviceConnection,Context.BIND_AUTO_CREATE);
        Objects.requireNonNull(getContext()).startService(binderIntent);
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

    private void clearAllLists()
    {
        songAL.clear();
    }

    private void addAllDataToDatabase()
    {
        Cursor cur = centralQueue.getAllData();
        if(cur!=null)
        {
            if(cur.moveToFirst()) {
                do {
                    songAL.add(new Song(Long.parseLong(cur.getString(1)),
                            cur.getString(2),
                            cur.getString(4),
                            cur.getString(3),
                            cur.getString(5),
                            cur.getString(7),
                            cur.getString(6),
                            cur.getString(8)));
                    songDataAl.add(cur.getString(3));
                } while (cur.moveToNext());
            }
        }

        if(Objects.requireNonNull(centralQueue.getAllData()).getCount() == 0)
        {
            linearLayout.setVisibility(View.VISIBLE);
        }
    }
}
