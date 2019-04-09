package com.abhigyan.user.hertzmusicplayer.RecyclerViewAdapters;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.abhigyan.user.hertzmusicplayer.R;
import com.abhigyan.user.hertzmusicplayer.Utility.ProcessorTool;
import com.abhigyan.user.hertzmusicplayer.Utility.Song;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.davidecirillo.multichoicerecyclerview.MultiChoiceAdapter;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class MoodEngineRVAdapter extends MultiChoiceAdapter<MoodEngineRVAdapter.Viewholder> {

    private Context context;
    private ArrayList<Song> songAL;

    public MoodEngineRVAdapter(Context context, ArrayList<Song> songAL) {
        this.context = context;
        this.songAL = songAL;
    }
    @Override
    public void onBindViewHolder(Viewholder holder, int position) {
        super.onBindViewHolder(holder, position);

        ProcessorTool processorTool = new ProcessorTool(context);

        if(songAL.get(holder.getAdapterPosition()).getTrackName()!=null)
        {
            holder.trackNameTV.setText(processorTool.reformatTrackName(songAL.get(holder.getAdapterPosition()).getTrackName()));
        }
        else
        {
            holder.trackNameTV.setText("Unknown");
        }
        //------------------------------------------------------------------------------------------
        if(songAL.get(holder.getAdapterPosition()).getAlbumName()!=null)
        {
            holder.albumNameTV.setText("Album- "+processorTool.reformatTrackName(songAL.get(holder.getAdapterPosition()).getAlbumName()));
        }
        else
        {
            holder.albumNameTV.setText("Album- Unknown");
        }
        //------------------------------------------------------------------------------------------
        if(songAL.get(holder.getAdapterPosition()).getArtistName()!=null)
        {
            holder.artistNameTV.setText("Artist- "+processorTool.reformatTrackName(songAL.get(holder.getAdapterPosition()).getArtistName()));
        }
        else
        {
            holder.artistNameTV.setText("Artist- Unknown");
        }
        try {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, songAL.get(holder.getAdapterPosition()).getAlbumID());
            Glide.with(context)
                    .asBitmap()
                    .load(uri)
                    .apply(RequestOptions.placeholderOf(R.drawable.defaultalbumpic).error(R.drawable.defaultalbumpic))
                    .into(holder.coverPics);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            holder.coverPics.setImageResource(R.drawable.defaultalbumpic);
        }

    }

    @Override
    protected View.OnClickListener defaultItemViewClickListener(Viewholder holder, int position) {


        return super.defaultItemViewClickListener(holder, position);
    }

    @NonNull
    @Override
    public MoodEngineRVAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.mood_engine_unit_ui, viewGroup, false);
        //create the object of the Viewholder class down below
        MoodEngineRVAdapter.Viewholder viewholder = new MoodEngineRVAdapter.Viewholder(view);
        return viewholder;
    }

    @Override
    public int getItemCount() {
        return songAL.size();
    }



    public class Viewholder extends RecyclerView.ViewHolder
    {
        CircleImageView coverPics;
        TextView trackNameTV, albumNameTV, artistNameTV;
        LinearLayout linearLayoutSongList;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            linearLayoutSongList = itemView.findViewById(R.id.songListLLME);
            trackNameTV = itemView.findViewById(R.id.songNameTVME);
            albumNameTV = itemView.findViewById(R.id.albumNameTVME);
            artistNameTV = itemView.findViewById(R.id.artistNameTVME);
            coverPics = itemView.findViewById(R.id.songListCIVME);
        }
    }
}
