package com.abhigyan.user.hertzmusicplayer.ViewPager;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.abhigyan.user.hertzmusicplayer.R;
import com.abhigyan.user.hertzmusicplayer.Utility.Song;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {

    Activity activity;
    LayoutInflater layoutInflater;
    ArrayList<Song> imageids;

    public ViewPagerAdapter(Activity activity, ArrayList<Song> imageids) {
        this.activity = activity;
        this.imageids = imageids;
    }

    @Override
    public int getCount() {
        return imageids.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myView = layoutInflater.inflate(R.layout.view_pager_content, container, false);

        ImageView imageView = myView.findViewById(R.id.viewPagerImage);

        final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, imageids.get(position).getAlbumID());
        Glide.with(activity)
                .asBitmap()
                .load(uri)
                .apply(RequestOptions.placeholderOf(R.drawable.defaultalbumpic).error(R.drawable.defaultalbumpic))
                .into(imageView);

        container.addView(myView);
        return myView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View) object);
    }

}
