package com.abhigyan.user.hertzmusicplayer.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.abhigyan.user.hertzmusicplayer.Databases.FavouritesDB;
import com.abhigyan.user.hertzmusicplayer.R;
import com.abhigyan.user.hertzmusicplayer.RecyclerViewAdapters.FavouritesRVAdapter;
import com.abhigyan.user.hertzmusicplayer.RecyclerViewAdapters.SongListRVAdapter;

import java.util.ArrayList;

public class FavouritesFragment extends Fragment {

    private FavouritesDB favouritesDB;
    private ArrayList<String> trackNameAL = new ArrayList<>(),
                                albumNameAL = new ArrayList<>(),
                                artistNameAL = new ArrayList<>(),
                                composerNameAL = new ArrayList<>(),
                                durationAL = new ArrayList<>(),
                                sizeAL = new ArrayList<>(),
                                pathNameAL = new ArrayList<>();

    private ArrayList<Long> albumIDAL = new ArrayList<>();
    private RecyclerView favouritesRecyclerView;
    private LinearLayout linearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_favourites, container, false);
        favouritesRecyclerView = myView.findViewById(R.id.favouritesRV);
        linearLayout = myView.findViewById(R.id.favsCheckLL);
        linearLayout.setVisibility(View.GONE);
        return myView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        trackNameAL.clear();
        albumNameAL.clear();
        albumIDAL.clear();
        favouritesDB = new FavouritesDB(getContext());
        accessSongsFromFavourites();
        FavouritesRVAdapter adapter = new FavouritesRVAdapter(getContext(),
                trackNameAL,
                albumNameAL,
                artistNameAL,
                composerNameAL,
                durationAL,
                sizeAL,
                pathNameAL,
                albumIDAL);
        favouritesRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        favouritesRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void accessSongsFromFavourites()
    {
        Cursor cur = favouritesDB.getAllData();
        if(cur!=null)
        {
            if(cur.moveToFirst())
            {
                do{
                    trackNameAL.add(cur.getString(2));
                    albumNameAL.add(cur.getString(4));
                    artistNameAL.add(cur.getString(5));
                    composerNameAL.add(cur.getString(6));
                    durationAL.add(cur.getString(7));
                    sizeAL.add(cur.getString(8));
                    pathNameAL.add(cur.getString(3));
                    albumIDAL.add(cur.getLong(1));
                }while(cur.moveToNext());
            }
        }

        if(cur.getCount()==0)
        {
            linearLayout.setVisibility(View.VISIBLE);
        }
    }
}
