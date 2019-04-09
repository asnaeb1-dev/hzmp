package com.abhigyan.user.hertzmusicplayer.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import com.abhigyan.user.hertzmusicplayer.LastFM.ArtistInfoClass;
import com.abhigyan.user.hertzmusicplayer.LastFM.LastFMInterface;
import com.abhigyan.user.hertzmusicplayer.R;
import com.abhigyan.user.hertzmusicplayer.Utility.Config;
import com.abhigyan.user.hertzmusicplayer.Utility.MemoryAccess;
import com.abhigyan.user.hertzmusicplayer.Utility.ProcessorTool;
import com.abhigyan.user.hertzmusicplayer.Utility.Song;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArtistFragment extends Fragment {

    GridLayout artistGridLayout;
    private int isCalled = 0;
    private MemoryAccess memoryAccess;
    private LinkedHashSet<Song> song = new LinkedHashSet<>();
    ProcessorTool pt = new ProcessorTool(getContext());
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_artist, container, false);
        artistGridLayout = myView.findViewById(R.id.artistGridLayout);
        return myView;
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(isCalled == 0)
        {
            memoryAccess = new MemoryAccess(getContext());
            memoryAccess.accessMemoryForSongs();
            song.addAll(memoryAccess.getSongAL());
            isCalled = 1;
        }

        ArrayList<Song> al1 = new ArrayList<>(song);

        for(int i = 0;i<al1.size();i++)
        {
            populateLayout(al1.get(i).getArtistName());
        }
    }

    private void populateLayout(final String artistName)
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
        final ImageView imageView = myView.findViewById(R.id.albumIMG);
        TextView textView = myView.findViewById(R.id.albumTEXT);
        textView.setText(artistName);

        getArtistPictureFromSite(pt.reformatArtistName(artistName),imageView);

        artistGridLayout.addView(cardView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();

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
                args.putInt("callsc", 2);
                args.putString("artistnm", artistName);
                args.putByteArray("image_array", imageInByte);
                albumContentsFragment.setArguments(args);
                fragmentTransaction.add(R.id.frameLayoutMain, albumContentsFragment);
                fragmentTransaction.commit();
            }
        });
    }

    private void getArtistPictureFromSite(String artistName, final ImageView imageView)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.MAIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LastFMInterface lastFMInterface = retrofit.create(LastFMInterface.class);
        Call<ArtistInfoClass> call = lastFMInterface.getArtistPics("artist.getinfo",artistName, Config.LAST_FM_API_KEY, "json");
        call.enqueue(new Callback<ArtistInfoClass>() {
            @Override
            public void onResponse(@NonNull Call<ArtistInfoClass> call, @NonNull Response<ArtistInfoClass> response) {

                ArtistInfoClass artist = response.body();
                if(artist!=null) {
                    ArtistInfoClass.Artist art = artist.getArtist();
                    if (art != null) {
                        List<ArtistInfoClass.Image> img = art.getImage();
                        for (int i = 0; i < img.size(); i++) {
                            if (img.get(i).getSize().equals("mega")) {
                                Glide.with(Objects.requireNonNull(getContext()))
                                        .asDrawable()
                                        .load(img.get(i).getText())
                                        .apply(RequestOptions.placeholderOf(R.drawable.defaultalbumpic).error(R.drawable.defaultalbumpic))
                                        .into(imageView);
                                break;
                            }
                        }
                    }
                }
                else
                {
                    imageView.setImageResource(R.drawable.defaultalbumpic);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ArtistInfoClass> call, @NonNull Throwable t) {

                Log.e("abhigyans error", t.getMessage());
            }
        });
    }
}
