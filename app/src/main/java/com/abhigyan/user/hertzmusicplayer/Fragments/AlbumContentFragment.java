package com.abhigyan.user.hertzmusicplayer.Fragments;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.abhigyan.user.hertzmusicplayer.R;
import com.abhigyan.user.hertzmusicplayer.RecyclerViewAdapters.ContentRVAdapter;
import com.abhigyan.user.hertzmusicplayer.RecyclerViewAdapters.SongListRVAdapter;
import com.abhigyan.user.hertzmusicplayer.Utility.MemoryAccess;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Objects;

public class AlbumContentFragment extends Fragment {

    private String albumName, artistName;
    private long albumID;
    private RecyclerView recyclerView;
    private MemoryAccess memoryAccess;
    private int callSource;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View myView = inflater.inflate(R.layout.album_content_layout, container, false);
        ImageView imageView = myView.findViewById(R.id.albumContentImageView);
        recyclerView = myView.findViewById(R.id.albumContentRecyclerView);

        callSource = getArguments().getInt("callsc", 0);
        if( callSource== 3) {

            albumName = getArguments().getString("albumname");
            albumID = getArguments().getLong("albumid");
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri, albumID);
            Glide.with(Objects.requireNonNull(getContext()))
                    .asBitmap()
                    .load(uri)
                    .apply(RequestOptions.placeholderOf(R.drawable.defaultalbumpic).error(R.drawable.defaultalbumpic))
                    .into(imageView);
        }
        else if(callSource == 2)
        {
            artistName = getArguments().getString("artistnm");
            byte[] arr = getArguments().getByteArray("image_array");
            assert arr != null;
            Bitmap bitmap = BitmapFactory.decodeByteArray(arr, 0, arr.length);
            imageView.setImageBitmap(bitmap);
        }
        Toolbar toolbar = myView.findViewById(R.id.toolbarAlbumContent);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
        return myView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        memoryAccess = new MemoryAccess(getContext());
        memoryAccess.clearAllData();
        int cs =0;
        if(callSource == 3) {
            memoryAccess.getSongsByAlbum(albumName);
            cs = 1;
        }
        else if (callSource == 2)
        {
            cs = 2;
            memoryAccess.getSongsByArtist(artistName);
        }
        ContentRVAdapter adapter = new ContentRVAdapter(getContext(), memoryAccess.getSongAL(), cs);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
    }
}
