package com.abhigyan.user.hertzmusicplayer.Activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abhigyan.user.hertzmusicplayer.DailogBoxes.DetailsDailog;
import com.abhigyan.user.hertzmusicplayer.DailogBoxes.VisualizerSelectionDialog;
import com.abhigyan.user.hertzmusicplayer.Databases.FavouritesDB;
import com.abhigyan.user.hertzmusicplayer.Databases.Mood1DB;
import com.abhigyan.user.hertzmusicplayer.Databases.Mood2DB;
import com.abhigyan.user.hertzmusicplayer.Databases.Mood3DB;
import com.abhigyan.user.hertzmusicplayer.R;
import com.abhigyan.user.hertzmusicplayer.Services.MusicService;
import com.abhigyan.user.hertzmusicplayer.Utility.MemoryAccess;
import com.abhigyan.user.hertzmusicplayer.Utility.ProcessorTool;
import com.abhigyan.user.hertzmusicplayer.ViewPager.ViewPagerAdapter;
import com.bumptech.glide.Glide;
import com.gauravk.audiovisualizer.base.BaseVisualizer;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.gauravk.audiovisualizer.visualizer.BlastVisualizer;
import com.gauravk.audiovisualizer.visualizer.BlobVisualizer;
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.gigamole.infinitecycleviewpager.OnInfiniteCyclePageTransformListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import jp.wasabeef.blurry.Blurry;

public class NavigationMusicActivity extends AppCompatActivity /* implements NavigationView.OnNavigationItemSelectedListener */{

   /* ImageView backgroundIV, shuffleButton, loopButton, favsButton, navBackgroundIVMSC, songCoverIVMSC;
    HorizontalInfiniteCycleViewPager horizontalInfiniteCycleViewPager;
    FloatingActionButton playPauseButton, nextButton, previousButton;
    TextView trackNameTextView, artistNameTextView, songNameTVMSC, navTimertext;
    ConstraintLayout constraintLayout;
    NavigationView navigationView;
    DrawerLayout drawerLayoutMSC;
    SeekBar seekBar;

    private Intent binderIntent;
    private BroadcastReceiver broadcastReceiver, broadcastReceiver1;
    private ServiceConnection serviceConnection;
    private MusicService musicService;
    private CountDownTimer countDown;

    private ArrayList<Long> albumIDAL = new ArrayList<>();
    private ArrayList<String> trackNameAL = new ArrayList<>(),
            songDataAL = new ArrayList<>(),
            albumNameAL = new ArrayList<>(),
            artistNameAL = new ArrayList<>(),
            composerNameAL = new ArrayList<>(),
            durationAL = new ArrayList<>(),
            sizeAL = new ArrayList<>();

    private MemoryAccess memoryAccess = new MemoryAccess(this);
    private ProcessorTool processorTool = new ProcessorTool(this);
   // private MediaPlayer mediaPlayer;
    private FavouritesDB favouritesDB = new FavouritesDB(this);
    private Random random = new Random();
    private BarVisualizer barVisualizer;

    int previousposition;
    private int positionOfPointerOnList = 0;
    private int callSource;
    private int audioSession;
    private boolean audioIsPlaying = false;
    private boolean addedToFavourites = false;
    private boolean isShuffling = false;
    private boolean serviceBounded = false;
    private boolean loops = false;
    private boolean sleepTimerEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_music);

        drawerLayoutMSC = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        songNameTVMSC = headerView.findViewById(R.id.trackNameNavTVMSC);
        navBackgroundIVMSC = headerView.findViewById(R.id.navHeaderBackgroundMSC);
        songCoverIVMSC = headerView.findViewById(R.id.navCoverPicMSC);
        navTimertext = headerView.findViewById(R.id.sleepTimer);
        navTimertext.setVisibility(View.INVISIBLE);

        navigationView.setNavigationItemSelectedListener(this);

        findAllIDS();

        Intent intent = getIntent();
        callSource = intent.getIntExtra("callSource", 0);

        dealWithCallSources(callSource, intent);

        initializeServiceConnection();
        bindUIwithService();
        getAllBroadcasts();

       // mediaPlayer = new MediaPlayer();
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, albumIDAL);
        horizontalInfiniteCycleViewPager.setAdapter(viewPagerAdapter);
        horizontalInfiniteCycleViewPager.setCurrentItem(positionOfPointerOnList,true);

        setAllViews(trackNameAL.get(positionOfPointerOnList),artistNameAL.get(positionOfPointerOnList),albumIDAL.get(positionOfPointerOnList));

        positionOfPointerOnList = horizontalInfiniteCycleViewPager.getRealItem();

        engagePageChangeListener();

        shuffleButton.setImageResource(R.drawable.shuffle_diabled);

        Toast.makeText(this, String.valueOf(trackNameAL.size()), Toast.LENGTH_SHORT).show();

    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       if (id == R.id.openPlaylist) {
            //open currently playing playlist

        } else if (id == R.id.equalizerMsc) {
            //open equalizer

        } else if (id == R.id.settings_msc) {
            //open setting

        } else if (id == R.id.details_nav) {
            //open details
           DetailsDailog detailsDailog = new DetailsDailog(this);
           detailsDailog.makeDetailsDailog(trackNameAL.get(positionOfPointerOnList),
                                        albumNameAL.get(positionOfPointerOnList),
                                        artistNameAL.get(positionOfPointerOnList),
                                        processorTool.reformatTime(durationAL.get(positionOfPointerOnList)),
                                        processorTool.reformatSize(sizeAL.get(positionOfPointerOnList)),
                                        composerNameAL.get(positionOfPointerOnList));

        } else if (id == R.id.visualizer_nav) {
            //open visualizer


        } else if (id == R.id.sleepTimer_nav) {
            //open sleep timer
           if(sleepTimerEnabled == false)
           {
               navTimertext.setVisibility(View.VISIBLE);
               enableSleepTimer();
               sleepTimerEnabled = true;
           }
           else
           {
               navTimertext.setVisibility(View.INVISIBLE);
               disableSleepTimer();
               sleepTimerEnabled = false;
           }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void disableSleepTimer() {
        countDown.cancel();
    }

    private void enableSleepTimer()
    {
        countDown = new CountDownTimer(1800000, 1000) {
            @Override
            public void onTick(long l) {

                navTimertext.setText(processorTool.reformatTime(String.valueOf(l)));
            }

            @Override
            public void onFinish() {

                musicService.stopAudioPlayback();
            }
        }.start();
    }

    public void engagePageChangeListener()
    {
        horizontalInfiniteCycleViewPager.setOnInfiniteCyclePageTransformListener(new OnInfiniteCyclePageTransformListener() {
            @Override
            public void onPreTransform(View page, float position) {

            }

            @Override
            public void onPostTransform(View page, float position) {

                //this anon inner class checks the relative position of the two pages and wrt to that the audio is played
                previousposition = positionOfPointerOnList;
                positionOfPointerOnList = horizontalInfiniteCycleViewPager.getRealItem();
                setAllViews(trackNameAL.get(positionOfPointerOnList),artistNameAL.get(positionOfPointerOnList),albumIDAL.get(positionOfPointerOnList));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(previousposition < horizontalInfiniteCycleViewPager.getRealItem())
                        {
                            musicService.playNextMedia();
                        }
                        else if(previousposition > positionOfPointerOnList)
                        {
                            musicService.playPreviousMedia();
                        }

                    }
                },100);
            }
        });
    }

    public void saveSongToFavourites(long albumID, String trackName, String path, String albumName, String artistName, String composerName, String duration, String size) {
        favouritesDB.insertData(String.valueOf(albumID), trackName,path,albumName,artistName,composerName,duration,size);
    }

    public void removeSongFromFavourites(String trackName)
    {
        favouritesDB.deleteData(trackName);
    }

    public void checkIfFavourite(String trackName)
    {
        //check if a song exists in the favourites database
    }

    private void findAllIDS()
    {
        horizontalInfiniteCycleViewPager = findViewById(R.id.viewPagerHorizontal);

        shuffleButton = findViewById(R.id.shuffleButton);
        favsButton = findViewById(R.id.favsButton);
        loopButton = findViewById(R.id.loopButton);
        barVisualizer = findViewById(R.id.barVisualizer);
        playPauseButton = findViewById(R.id.playPause);
        nextButton = findViewById(R.id.playNext);
        previousButton = findViewById(R.id.playPrevious);
        trackNameTextView = findViewById(R.id.trackNameTV);
        trackNameTextView.setSelected(true);
        artistNameTextView = findViewById(R.id.artistTV);
        artistNameTextView.setSelected(true);
        backgroundIV = findViewById(R.id.backgroundIV);
        seekBar = findViewById(R.id.seekBar);
        constraintLayout = findViewById(R.id.mainCLayout);
        barVisualizer.setEnabled(false);
    }

    public void setAllViews(String trackName, String artistName, final long albumid)
    {
        trackNameTextView.setText(processorTool.reformatTrackName(trackName));
        artistNameTextView.setText(processorTool.reformatTrackName(artistName));
        songNameTVMSC.setText(processorTool.reformatTrackName(trackName));

        final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
        try {
            Blurry.with(getApplicationContext()).from(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri)).into(backgroundIV);
            Blurry.with(getApplicationContext()).from(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri)).into(navBackgroundIVMSC);

        } catch (IOException e) {
            Blurry.with(getApplicationContext()).from( BitmapFactory.decodeResource(getResources(), R.drawable.defaultalbumpic)).into(backgroundIV);
            e.printStackTrace();
        }
        Glide.with(getApplicationContext()).load(uri).into(songCoverIVMSC);
    }

    private void bindUIwithService()
    {
        binderIntent = new Intent(getApplicationContext(), MusicService.class);
        binderIntent.putExtra("songlist", songDataAL);
        binderIntent.putExtra("songPos", positionOfPointerOnList);
        bindService(binderIntent,serviceConnection,Context.BIND_AUTO_CREATE);
        startService(binderIntent);
        audioIsPlaying = true;
    }

    private void getAllBroadcasts()
    {
        broadcastReceiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                positionOfPointerOnList = intent.getIntExtra("position", 0);
                audioSession = intent.getIntExtra("audioSession", -1);
                horizontalInfiniteCycleViewPager.setCurrentItem(positionOfPointerOnList, true);
                if(audioSession!=-1)
                {
                    barVisualizer.setAudioSessionId(audioSession);
                }
                memoryAccess.setAudioSession(audioSession);
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(broadcastReceiver1,new IntentFilter("abc"));

    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(broadcastReceiver1);
    }

    private void initializeServiceConnection()
    {
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

    private void dealWithCallSources(int callsource, Intent intent)
    {
        switch (callsource)
        {
            case 1:
                positionOfPointerOnList = intent.getIntExtra("positionOfPointerOnList", 0);

                memoryAccess.accessMemoryForSongs();
                albumIDAL = memoryAccess.getAlbumIDAL();
                trackNameAL = memoryAccess.getTrackNameAL();
                songDataAL = memoryAccess.getSongDataAL();
                albumNameAL = memoryAccess.getAlbumNameAL();
                artistNameAL = memoryAccess.getSingerNameAL();
                composerNameAL = memoryAccess.getComposerNameAL();
                durationAL = memoryAccess.getTimeListAL();
                sizeAL = memoryAccess.getSizeOfSongListAL();
                break;

            case 2:

                break;

            case 3:
                break;

            case 4:
                positionOfPointerOnList = intent.getIntExtra("positionOfPointerOnList", 0);
                break;

            case 5:
                break;

            case 10:
                Mood1DB mood1DB = new Mood1DB(this);
                mood1DB.getAllData();
                Cursor cur1 = mood1DB.getAllData();
                if(cur1!=null)
                {
                    if(cur1.moveToFirst())
                    {
                        do{
                            albumIDAL.add(Long.parseLong(cur1.getString(1)));
                            trackNameAL.add(cur1.getString(2));
                            songDataAL.add(cur1.getString(3));
                            albumNameAL.add(cur1.getString(4));
                            artistNameAL.add(cur1.getString(5));
                            composerNameAL.add(cur1.getString(6));
                            durationAL.add(cur1.getString(7));
                            sizeAL.add(cur1.getString(8));

                        }while(cur1.moveToNext());
                    }
                }
                break;

            case 11:
                Mood2DB mood2DB = new Mood2DB(this);
                mood2DB.getAllData();
                Cursor cur2 = mood2DB.getAllData();
                if(cur2!=null)
                {
                    if(cur2.moveToFirst())
                    {
                        do{
                            albumIDAL.add(Long.parseLong(cur2.getString(1)));
                            trackNameAL.add(cur2.getString(2));
                            songDataAL.add(cur2.getString(3));
                            albumNameAL.add(cur2.getString(4));
                            artistNameAL.add(cur2.getString(5));
                            composerNameAL.add(cur2.getString(6));
                            durationAL.add(cur2.getString(7));
                            sizeAL.add(cur2.getString(8));

                        }while(cur2.moveToNext());
                    }
                }
                break;

            case 12:
                Mood3DB mood3DB = new Mood3DB(this);
                mood3DB.getAllData();
                Cursor cur = mood3DB.getAllData();
                if(cur!=null)
                {
                    if(cur.moveToFirst())
                    {
                        do{
                            albumIDAL.add(Long.parseLong(cur.getString(1)));
                            trackNameAL.add(cur.getString(2));
                            songDataAL.add(cur.getString(3));
                            albumNameAL.add(cur.getString(4));
                            artistNameAL.add(cur.getString(5));
                            composerNameAL.add(cur.getString(6));
                            durationAL.add(cur.getString(7));
                            sizeAL.add(cur.getString(8));

                        }while(cur.moveToNext());
                    }
                }
                break;

            case 13:
                break;

            case 14:
                break;
        }
    }

    //##############################################################################################
    public void addremoveFavsFunction(View view)
    {
        //add or remove favourites
        if(addedToFavourites == false)
        {
            saveSongToFavourites(albumIDAL.get(positionOfPointerOnList),
                    trackNameAL.get(positionOfPointerOnList),
                    songDataAL.get(positionOfPointerOnList),
                    albumNameAL.get(positionOfPointerOnList),
                    artistNameAL.get(positionOfPointerOnList),
                    composerNameAL.get(positionOfPointerOnList),
                    durationAL.get(positionOfPointerOnList),
                    sizeAL.get(positionOfPointerOnList));

            favsButton.setImageResource(R.drawable.favourites_select);
            Snackbar snackbar = Snackbar.make(constraintLayout,trackNameAL.get(positionOfPointerOnList)+" has been added to favourites! ",Snackbar.LENGTH_SHORT);
            snackbar.show();
            addedToFavourites = true;
        }
        else
        {
            removeSongFromFavourites(trackNameAL.get(positionOfPointerOnList));
            favsButton.setImageResource(R.drawable.favorite_deselect);
            Snackbar snackbar = Snackbar.make(constraintLayout,trackNameAL.get(positionOfPointerOnList)+" has been removed from favourites! ",Snackbar.LENGTH_SHORT);
            snackbar.show();
            addedToFavourites = false;
        }
    }

    public void shuffleFunction(View view)
    {
        //shuffle the songs
        if(isShuffling == false)
        {
            shuffleButton.setImageResource(R.drawable.ic_shuffle_black_24dp);
            Snackbar snackbar = Snackbar.make(constraintLayout," Shuffling. ",Snackbar.LENGTH_SHORT);
            snackbar.show();
            isShuffling = true;
        }
        else
        {
            shuffleButton.setImageResource(R.drawable.shuffle_diabled);
            Snackbar snackbar = Snackbar.make(constraintLayout," Shuffle disabled. ",Snackbar.LENGTH_SHORT);
            snackbar.show();
            isShuffling = false;
        }
    }

    public void loopFunction(View view)
    {
        //enable looping of songs
        if(loops == false)
        {
            musicService.loopSong();
            loopButton.setImageResource(R.drawable.ic_repeat_one_black_24dp);
            loops = true;
        }
        else
        {
            musicService.loopSong();
            loopButton.setImageResource(R.drawable.repeat_hertz);
            loops =  false;
        }
    }

    public void playPauseFunction(View view)
    {
        //play or pause a song
        if(audioIsPlaying == false)
        {
            musicService.playMedia();
            audioIsPlaying = true;
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
        }
        else
        {
            musicService.pauseMedia();
            audioIsPlaying = false;
            playPauseButton.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    public void playNextFunction(View view)
    {
        //play next song

        musicService.playNextMedia();
    }

    public void playPreviousFunction(View view)
    {
        //play previous song
        musicService.playPreviousMedia();
    }
    //##############################################################################################

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/
}
