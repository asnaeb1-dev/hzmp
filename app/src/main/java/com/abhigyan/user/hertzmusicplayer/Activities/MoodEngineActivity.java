package com.abhigyan.user.hertzmusicplayer.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.abhigyan.user.hertzmusicplayer.DailogBoxes.InstructionsDialogMoodSetter;
import com.abhigyan.user.hertzmusicplayer.Databases.Mood1DB;
import com.abhigyan.user.hertzmusicplayer.Databases.Mood2DB;
import com.abhigyan.user.hertzmusicplayer.Databases.Mood3DB;
import com.abhigyan.user.hertzmusicplayer.Databases.Mood4DB;
import com.abhigyan.user.hertzmusicplayer.Databases.Mood5DB;
import com.abhigyan.user.hertzmusicplayer.R;
import com.abhigyan.user.hertzmusicplayer.RecyclerViewAdapters.MoodEngineRVAdapter;
import com.abhigyan.user.hertzmusicplayer.Utility.MemoryAccess;
import com.abhigyan.user.hertzmusicplayer.Utility.Song;
import com.davidecirillo.multichoicerecyclerview.MultiChoiceAdapter;
import com.github.clans.fab.FloatingActionMenu;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;

public class MoodEngineActivity extends AppCompatActivity {

    private RecyclerView moodEngineRV;
    MoodEngineRVAdapter moodEngineRVAdapter;
    private MemoryAccess memoryAccess = new MemoryAccess(this);
    private Toolbar moodEngineToolBar;
    private Mood1DB mood1DB;
    private Mood2DB mood2DB;
    private Mood3DB mood3DB;
    private Mood4DB mood4DB;
    private Mood5DB mood5DB;

    private ArrayList<Song> songAL = new ArrayList<>();
    private FloatingActionMenu fabMenu;
    private int itemsel = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mood_engine_menu, menu);
        //MenuItem addfavs = menu.findItem(R.id.action_add_favorites);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_engine);

        findAllIDs();

        fabMenu.setTranslationX(1000f);
        setAllToolbarOptions();
        memoryAccess.accessMemoryForSongs();
        moodEngineRVAdapter = new MoodEngineRVAdapter(this,memoryAccess.getSongAL());
        moodEngineRV.setAdapter(moodEngineRVAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        moodEngineRV.setLayoutManager(linearLayoutManager);

        moodEngineRVAdapter.setMultiChoiceSelectionListener(new MultiChoiceAdapter.Listener() {
            @Override
            public void OnItemSelected(int selectedPosition, int itemSelectedCount, int allItemCount) {

                if(itemsel == 0)
                {
                    fabMenu.animate().translationXBy(-1000f).setDuration(500);
                    itemsel = 1;
                }
                songAL.add(memoryAccess.getSongAL().get(selectedPosition));
            }

            @Override
            public void OnItemDeselected(int deselectedPosition, int itemSelectedCount, int allItemCount) {

                if(itemSelectedCount == 0)
                {
                    itemsel = 0;
                    fabMenu.animate().translationXBy(1000f).setDuration(500);
                }
            }

            @Override
            public void OnSelectAll(int itemSelectedCount, int allItemCount) {

            }

            @Override
            public void OnDeselectAll(int itemSelectedCount, int allItemCount) {
                fabMenu.animate().translationXBy(1000f).setDuration(500);
                itemsel = 0;
                fabMenu.close(true);
                clearAllArrayLists();
            }
        });
    }

    private void setAllToolbarOptions()
    {
        moodEngineToolBar.setTitle(getResources().getString(R.string.mood_select_title));
        moodEngineToolBar.setTitleTextColor(getResources().getColor(android.R.color.white));
        moodEngineToolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        moodEngineToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void findAllIDs()
    {
        moodEngineRV = findViewById(R.id.moodEngineRecyclerView);
        moodEngineToolBar = findViewById(R.id.moodEngineToolBar);
        fabMenu = findViewById(R.id.menuFAB);
    }
    public void addSongsToCry(View view)
    {
        mood1DB = new Mood1DB(this);
        for(int i = 0;i<songAL.size();i++)
        {
            mood1DB.insertData(String.valueOf(songAL.get(i).getAlbumID()),
                    songAL.get(i).getTrackName(),
                    songAL.get(i).getSongLink(),
                    songAL.get(i).getAlbumName(),
                    songAL.get(i).getArtistName(),
                    songAL.get(i).getComposerName(),
                    songAL.get(i).getSongDuration(),
                    songAL.get(i).getSongSize());
            }
            moodEngineRVAdapter.deselectAll();

        clearAllArrayLists();
        MDToast mdToast = MDToast.makeText(getApplicationContext(), "Done! Deselect all to continue.", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
        mdToast.show();
    }
    public void addSongsToSad(View view)
    {
        //add audio song to sad database
        mood2DB = new Mood2DB(this);
        for(int i = 0;i<songAL.size();i++)
        {
            mood2DB.insertData(String.valueOf(songAL.get(i).getAlbumID()),
                    songAL.get(i).getTrackName(),
                    songAL.get(i).getSongLink(),
                    songAL.get(i).getAlbumName(),
                    songAL.get(i).getArtistName(),
                    songAL.get(i).getComposerName(),
                    songAL.get(i).getSongDuration(),
                    songAL.get(i).getSongSize());
        }
        moodEngineRVAdapter.deselectAll();

        clearAllArrayLists();
        MDToast mdToast = MDToast.makeText(getApplicationContext(), "Done! Deselect all to continue.", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
        mdToast.show();
    }
    public void addSongsToMeh(View view)
    {
        //add audio song to meh database
        mood3DB = new Mood3DB(this);
        for(int i = 0;i<songAL.size();i++)
        {
            mood3DB.insertData(String.valueOf(songAL.get(i).getAlbumID()),
                    songAL.get(i).getTrackName(),
                    songAL.get(i).getSongLink(),
                    songAL.get(i).getAlbumName(),
                    songAL.get(i).getArtistName(),
                    songAL.get(i).getComposerName(),
                    songAL.get(i).getSongDuration(),
                    songAL.get(i).getSongSize());
        }
        moodEngineRVAdapter.deselectAll();

        clearAllArrayLists();
        MDToast mdToast = MDToast.makeText(getApplicationContext(), "Done! Deselect all to continue.", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
        mdToast.show();
        }
    public void addSongsToSmile(View view)
    {
        //add audio song to smile database
        mood4DB = new Mood4DB(this);
        for(int i = 0;i<songAL.size();i++)
        {
            mood4DB.insertData(String.valueOf(songAL.get(i).getAlbumID()),
                    songAL.get(i).getTrackName(),
                    songAL.get(i).getSongLink(),
                    songAL.get(i).getAlbumName(),
                    songAL.get(i).getArtistName(),
                    songAL.get(i).getComposerName(),
                    songAL.get(i).getSongDuration(),
                    songAL.get(i).getSongSize());
        }

        moodEngineRVAdapter.deselectAll();

        clearAllArrayLists();
        MDToast mdToast = MDToast.makeText(getApplicationContext(), "Done! Deselect all to continue.", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
        mdToast.show();
    }
    public void addSongsToHappy(View view)
    {
        //add audio song to happy database
        mood5DB = new Mood5DB(this);
        for(int i = 0;i<songAL.size();i++)
        {
            mood5DB.insertData(String.valueOf(songAL.get(i).getAlbumID()),
                    songAL.get(i).getTrackName(),
                    songAL.get(i).getSongLink(),
                    songAL.get(i).getAlbumName(),
                    songAL.get(i).getArtistName(),
                    songAL.get(i).getComposerName(),
                    songAL.get(i).getSongDuration(),
                    songAL.get(i).getSongSize());
        }

        moodEngineRVAdapter.deselectAll();

        clearAllArrayLists();
        MDToast mdToast = MDToast.makeText(getApplicationContext(), "Done! Deselect all to continue.", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
        mdToast.show();
        }

    private void clearAllArrayLists()
    {
        songAL.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.gc();
        finish();
    }
}
