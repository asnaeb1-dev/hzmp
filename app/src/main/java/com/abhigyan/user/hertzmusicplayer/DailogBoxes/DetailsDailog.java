package com.abhigyan.user.hertzmusicplayer.DailogBoxes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.abhigyan.user.hertzmusicplayer.R;

import java.util.ArrayList;

public class DetailsDailog {

    Context context;

    public DetailsDailog(Context context) {
        this.context = context;

    }

    public void makeDetailsDailog(String trackName, String albumName, String artistname,String  duration,String size,String composerName)
    {
        ArrayList<String> detailsarrayList = new ArrayList<>();
        detailsarrayList.add("Track- "+trackName);
        detailsarrayList.add("Album- "+albumName);
        detailsarrayList.add("Artist- "+artistname);
        detailsarrayList.add("Composer- "+composerName);
        detailsarrayList.add("Duration- "+duration);
        detailsarrayList.add("Size- "+size);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View detailsViewDailog = inflater.inflate(R.layout.details_dailog_ui,null);
        android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(context);
        ListView listView = detailsViewDailog.findViewById(R.id.detailsListView);
        ArrayAdapter arrayAdapter = new ArrayAdapter(context,android.R.layout.simple_list_item_1,detailsarrayList);
        listView.setAdapter(arrayAdapter);

        mBuilder.setView(detailsViewDailog);
        mBuilder.show();
    }
}
