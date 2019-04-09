package com.abhigyan.user.hertzmusicplayer.Fragments;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abhigyan.user.hertzmusicplayer.LastFM.AlbumInfoClass;
import com.abhigyan.user.hertzmusicplayer.LastFM.ArtistInfoClass;
import com.abhigyan.user.hertzmusicplayer.LastFM.LastFMAlbumInterface;
import com.abhigyan.user.hertzmusicplayer.LastFM.LastFMInterface;
import com.abhigyan.user.hertzmusicplayer.R;
import com.abhigyan.user.hertzmusicplayer.Utility.ApplicationSettings;
import com.abhigyan.user.hertzmusicplayer.Utility.Config;
import com.abhigyan.user.hertzmusicplayer.Utility.MemoryAccess;
import com.abhigyan.user.hertzmusicplayer.Utility.ProcessorTool;
import com.abhigyan.user.hertzmusicplayer.Utility.Song;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlbumFragment extends Fragment {

    GridLayout albumGridLayout;
    private LinkedHashSet<Song> songLHS = new LinkedHashSet<>();

    private int isCalled =0;
    private MemoryAccess memoryAccess;
    //private ApplicationSettings applicationSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_album, container, false);
        albumGridLayout = myView.findViewById(R.id.albumGridLayout);
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
       // applicationSettings = new ApplicationSettings(getContext());
        songLHS.addAll(memoryAccess.getSongAL());
        ArrayList<Song> temp = new ArrayList<>(songLHS);

        for(int i = 0;i<temp.size();i++)
        {
            populateLayout(temp.get(i).getAlbumName(), temp.get(i).getAlbumID());
        }
    }

    private void populateLayout(final String albumNm, final long albumID )
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) Objects.requireNonNull(getContext())).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        CardView cardView = new CardView(getContext());
        cardView.setRadius(20f);
        cardView.setCardElevation(20f);
        cardView.setBackground(getResources().getDrawable(android.R.drawable.screen_background_dark_transparent));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width/2,height/3);
        params.setMargins(5, 15, 5, 15);
        cardView.setLayoutParams(params);

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myView = layoutInflater.inflate(R.layout.album_content,cardView, true);
        ImageView imageView = myView.findViewById(R.id.albumIMG);
        TextView textView = myView.findViewById(R.id.albumTEXT);
        textView.setText(albumNm);

        //checks if the downloading from internet is enabled
        if(!new ApplicationSettings(getContext()).isAlbumArtDownloadFromLastFM()) {
            //false- downloading is disabled and hence the predefined images shall be shown
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri, albumID);
            Glide.with(getContext())
                    .asBitmap()
                    .load(uri)
                    .apply(RequestOptions.placeholderOf(R.drawable.defaultalbumpic).error(R.drawable.defaultalbumpic))
                    .into(imageView);
        }
        else
       {
            //true- download from the internet
            getAlbumImagesFromNet(new ProcessorTool(getContext()).reformatAlbumName(albumNm),imageView);
       }

        albumGridLayout.addView(cardView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppBarLayout appBarLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.appbarMain);
                appBarLayout.animate().alpha(0f).setDuration(400);
                appBarLayout.setVisibility(View.GONE);

                ViewPager viewPager = getActivity().findViewById(R.id.containerVP);
                viewPager.setVisibility(View.GONE);

                AlbumContentFragment albumContentsFragment = new AlbumContentFragment();
                FragmentManager fragmentManager = getFragmentManager();
                assert fragmentManager != null;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle args = new Bundle();
                args.putInt("callsc",3 );
                args.putString("albumname", albumNm);
                args.putLong("albumid", albumID);
                albumContentsFragment.setArguments(args);
                fragmentTransaction.add(R.id.frameLayoutMain, albumContentsFragment);
                fragmentTransaction.commit();
            }
        });
    }

    private void getAlbumImagesFromNet(String albumName, final ImageView imgx)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.MAIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LastFMAlbumInterface lastFMInterface = retrofit.create(LastFMAlbumInterface.class);

        Call<AlbumInfoClass> call = lastFMInterface.getArtistPics("album.search",albumName, Config.LAST_FM_API_KEY, "json");
        call.enqueue(new Callback<AlbumInfoClass>() {
            @Override
            public void onResponse(@NonNull Call<AlbumInfoClass> call, @NonNull Response<AlbumInfoClass> response) {

                AlbumInfoClass album = response.body();
                if(album!=null) {
                    AlbumInfoClass.Results alb = album.getResults();
                    if (alb != null) {
                        List<AlbumInfoClass.Album> albumInfo = alb.getAlbummatches().getAlbum();
                        for (int i = 0; i < albumInfo.size(); i++) {
                            if (albumInfo.get(i).getImage().get(i).getSize().equals("large")) {
                                Glide.with(Objects.requireNonNull(getContext()))
                                        .asDrawable()
                                        .load(albumInfo.get(i).getImage().get(i).getText())
                                        .apply(RequestOptions.placeholderOf(R.drawable.defaultalbumpic).error(R.drawable.defaultalbumpic))
                                        .into(imgx);
                                break;
                            }
                        }
                    }
                }
                else
                {
                    imgx.setImageResource(R.drawable.defaultalbumpic);
                }
            }
            @Override
            public void onFailure(@NonNull Call<AlbumInfoClass> call, @NonNull Throwable t) {

                Log.e("abhigyans error", t.getMessage());
            }
        });
    }
}

