package com.abhigyan.user.hertzmusicplayer.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abhigyan.user.hertzmusicplayer.R;
import com.abhigyan.user.hertzmusicplayer.RecyclerViewAdapters.SongListRVAdapter;
import com.abhigyan.user.hertzmusicplayer.Utility.MemoryAccess;

public class SongListFragment extends Fragment {

    private RecyclerView songListRecyclerView;
    private int isCalled = 0;
    private MemoryAccess memoryAccess;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_song_list, container, false);
        songListRecyclerView = myView.findViewById(R.id.songListRV);
        return myView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(isCalled == 0) {
            memoryAccess = new MemoryAccess(getContext());
            memoryAccess.accessMemoryForSongs();
            isCalled = 1;
        }

        SongListRVAdapter adapter = new SongListRVAdapter(getContext(),memoryAccess.getSongAL());
        songListRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        songListRecyclerView.setLayoutManager(linearLayoutManager);
    }
}
