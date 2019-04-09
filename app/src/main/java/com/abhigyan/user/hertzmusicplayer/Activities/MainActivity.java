package com.abhigyan.user.hertzmusicplayer.Activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abhigyan.user.hertzmusicplayer.BuildConfig;
import com.abhigyan.user.hertzmusicplayer.CentralQueue;
import com.abhigyan.user.hertzmusicplayer.DailogBoxes.MoodSelectorDailog;
import com.abhigyan.user.hertzmusicplayer.Databases.FavouritesDB;
import com.abhigyan.user.hertzmusicplayer.Fragments.AlbumFragment;
import com.abhigyan.user.hertzmusicplayer.Fragments.ArtistFragment;
import com.abhigyan.user.hertzmusicplayer.Fragments.FavouritesFragment;
import com.abhigyan.user.hertzmusicplayer.Fragments.PlaylistFragment;
import com.abhigyan.user.hertzmusicplayer.Fragments.QueueFragment;
import com.abhigyan.user.hertzmusicplayer.Fragments.SongListFragment;
import com.abhigyan.user.hertzmusicplayer.R;
import com.abhigyan.user.hertzmusicplayer.Services.MusicService;
import com.abhigyan.user.hertzmusicplayer.Utility.ApplicationSettings;
import com.abhigyan.user.hertzmusicplayer.Utility.DateTracker;
import com.abhigyan.user.hertzmusicplayer.Utility.MemoryAccess;
import com.abhigyan.user.hertzmusicplayer.Utility.PermissionGranter;
import com.abhigyan.user.hertzmusicplayer.Utility.ProcessorTool;
import com.abhigyan.user.hertzmusicplayer.Utility.ShareAPP;
import com.abhigyan.user.hertzmusicplayer.Utility.Song;
import com.abhigyan.user.hertzmusicplayer.ViewPager.DepthTransformation;
import com.abhigyan.user.hertzmusicplayer.ViewPager.ViewPagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.gigamole.infinitecycleviewpager.OnInfiniteCyclePageTransformListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.blurry.Blurry;

import static android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton playPauseEnlarged, playNextEnlarged, playPreviousEnlarged;
    private HorizontalInfiniteCycleViewPager horizontalInfiniteCycleViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private ConstraintLayout smallPlayerLinearLAyout, mainConstraintLayout;

    private BarVisualizer  barVisualizerSmall;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ViewPager centralViewPager;
    private ImageButton imageButton;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private SeekBar seekBar;

    private AppBarLayout appBarLayoutMain;
    private TextView smallPlayerSongNameTV,
            smallPlayerAlbumNameTV,
            enlargedSongTV,
            enlargedAlbumTV,
            songNameTVMSC;

    private CircleImageView smallPlayerIV;
    private ImageView backgroundIV,
            smallPlayerPlay,
            navBackgroundIVMSC,
            songCoverIVMSC,
            favsButton,
            repeatButton;

    private ProcessorTool processorTool = new ProcessorTool(this);
    private MemoryAccess memoryAccess = new MemoryAccess(this);

    private BroadcastReceiver broadcastReceiver1,broadcastReceiver;
    private MusicService musicService;
    private boolean serviceBounded = false;
    private ServiceConnection serviceConnection;
    private PhoneStateListener phoneStateListener;

    private boolean doubleBackToExitPressedOnce = false;
    private boolean blockView = true;
    private int positionOfPointerOnList;
    private int presentMood, currentPosOfSeekBar, totalSize;

    private ArrayList<Song> songAL = new ArrayList<>();

    private long albumIDrcvd;
    private int callSource, prevCallSource =-2, audioSession, previousposition;

    private boolean audioIsPlaying = false;
    private boolean added = false;
    private boolean songLoops = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main,menu);
        MenuItem menuItem = menu.findItem(R.id.search_Option_main);
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageButton.setVisibility(View.GONE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                imageButton.setVisibility(View.VISIBLE);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        MenuItem menuItem1 = menu.findItem(R.id.interchange);
        imageButton = (ImageButton) menuItem1.getActionView();
        imageButton.setImageResource(R.drawable.view_list);
        imageButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(centralViewPager.getCurrentItem() == 1 || centralViewPager.getCurrentItem() == 2) {
                    if (blockView) {
                        imageButton.setImageResource(R.drawable.album_view);
                        blockView = false;
                    } else {
                        imageButton.setImageResource(R.drawable.view_list);
                        blockView = true;
                    }
                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {

        if(new ApplicationSettings(this).getDarkMode() == 1)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            setTheme(R.style.DarkTheme);
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.AppTheme);
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        if(new ApplicationSettings(this).getDarkMode() == 1)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            setTheme(R.style.DarkTheme);
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.AppTheme);
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(new ApplicationSettings(this).getDarkMode() == 1)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            setTheme(R.style.DarkTheme);
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findAllUIS();
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerSlideAnimationEnabled(true);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        songNameTVMSC = headerView.findViewById(R.id.trackNameNavTVMSC);
        navBackgroundIVMSC = headerView.findViewById(R.id.navHeaderBackgroundMSC);
        songCoverIVMSC = headerView.findViewById(R.id.navCoverPicMSC);
        //navTimertext = headerView.findViewById(R.id.sleepTimer);
        Blurry.with(getApplicationContext()).from(BitmapFactory.decodeResource(getResources(), R.drawable.defaultalbumpic)).into(navBackgroundIVMSC);
        DepthTransformation depthTransformation = new DepthTransformation();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        centralViewPager.setAdapter(mSectionsPagerAdapter);
        centralViewPager.setPageTransformer(true, depthTransformation);
        centralViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(centralViewPager));

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        getSystemSettings();
        //centralViewPagerSliderEffects();
        //contains the panel slide listener. arranged ui according to the position of the slide
        panelSlideListener();

        //initialize the service connection
        initializeServiceConnection();
        //bind the use interface with the music service
        bindUIwithService();

        /*this contains the broadcast receiver for
            i) get the current position of the song
            ii) get the call source so that the respective list can be called and used
            iii) audio session id for the visualizer
         */
        getAllBroadcasts();

        //activate all the ui that the panel holds
        activateAllPanelUI();
        //show the mood selector by date
        DateTracker dateTracker = new DateTracker(this);
        presentMood = dateTracker.showMoodSelectorAccordingToDate();
        //populate the drawer views only when the drawer is opened
        enableDrawerMovements();
    }

    private void engagePresentMood(int presentMood)
    {
        if(presentMood == 1 || presentMood == 2)
        {
            //call source is 16, coming from sad music

        }
        else if(presentMood == 3 || presentMood == 4)
        {
            //callsource is 15, sending for normal music
        }
        else if(presentMood == 5 || presentMood == 6)
        {
           //callsource is 14, sending for okay music
        }
        else if(presentMood == 7 || presentMood == 8)
        {
            //call source is 13, sending for happy
        }
        else if(presentMood == 9 || presentMood == 10)
        {
            //call source is 12, sending for v.happy
        }
    }

    private void enableDrawerMovements()
    {
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {

                if(smallPlayerSongNameTV.getText()!=null)
                {
                    songNameTVMSC.setText(smallPlayerSongNameTV.getText());
                }

                if(albumIDrcvd != 0)
                {
                    final Uri sArtworkUri = Uri
                            .parse("content://media/external/audio/albumart");

                    Uri uri = ContentUris.withAppendedId(sArtworkUri, albumIDrcvd);
                    Glide.with(getApplicationContext()).load(uri).into(songCoverIVMSC);
                    try {
                        Blurry.with(getApplicationContext()).from(MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri)).into(navBackgroundIVMSC);

                    } catch (IOException e) {
                        Blurry.with(getApplicationContext()).from( BitmapFactory.decodeResource(getResources(), R.drawable.defaultalbumpic)).into(navBackgroundIVMSC);
                    }
                }
                else
                {
                    Glide.with(getApplicationContext()).asBitmap().load(getResources().getDrawable(R.drawable.defaultalbumpic)).into(songCoverIVMSC);
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {

            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
    }

    private void findAllUIS() {
        //finds all the views by id
        toolbar = findViewById(R.id.mainToolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        centralViewPager = findViewById(R.id.containerVP);
        tabLayout = findViewById(R.id.tabs);
        slidingUpPanelLayout = findViewById(R.id.sliding_layout);
        smallPlayerLinearLAyout = findViewById(R.id.smallPlayerLL);
        appBarLayoutMain = findViewById(R.id.appbarMain);
        smallPlayerSongNameTV = findViewById(R.id.songNmSPTV);
        smallPlayerAlbumNameTV = findViewById(R.id.albumNmSPTV);
        smallPlayerIV = findViewById(R.id.smallPlayerpic);
        horizontalInfiniteCycleViewPager = findViewById(R.id.viewPagerHorizontal);
        backgroundIV = findViewById(R.id.backgroundIV);
        playPauseEnlarged = findViewById(R.id.playPauseEnlarged);
        playNextEnlarged = findViewById(R.id.playNext);
        playPreviousEnlarged = findViewById(R.id.playPrevious);
        enlargedSongTV = findViewById(R.id.trackNameTV);
        enlargedAlbumTV = findViewById(R.id.artistTV);

        barVisualizerSmall = findViewById(R.id.barVisualizerSmall);

        smallPlayerPlay = findViewById(R.id.sPPlayPauseButton);
        favsButton = findViewById(R.id.favsButton);
        repeatButton = findViewById(R.id.loopButton);
        mainConstraintLayout = findViewById(R.id.mainCLayout);
        seekBar = findViewById(R.id.seekBar);
    }


    public void panelSlideListener()
    {
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                float presentVal = 0f;
                if(slideOffset>presentVal)
                {
                    smallPlayerLinearLAyout.animate().alpha(0).setDuration(200);
                    smallPlayerLinearLAyout.setVisibility(View.GONE);
                }
                else
                {
                    smallPlayerLinearLAyout.setVisibility(View.VISIBLE);
                    smallPlayerLinearLAyout.animate().alpha(1).setDuration(200);
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                }
        });
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        FrameLayout frameLayout = findViewById(R.id.frameLayoutMain);
        //if the sliding panel is open close it
        if (slidingUpPanelLayout != null &&
                (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        //else if the drawer is open close it
        else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        //else if nothing is open then show msg
        else if(frameLayout.getChildCount()!= 0)
        {
            frameLayout.removeAllViews();
            ViewPager viewPager = findViewById(R.id.containerVP);
            viewPager.setVisibility(View.VISIBLE);
            AppBarLayout appBarLayout = findViewById(R.id.appbarMain);
            appBarLayout.animate().alpha(1f).setDuration(400);
            appBarLayout.setVisibility(View.VISIBLE);
        }
        else
            {
            String message= "Click BACK again to exit";
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                if(serviceBounded) {
                    if(serviceBounded) {
                        unregisterReceiver(broadcastReceiver1);
                        unregisterReceiver(broadcastReceiver);
                        unbindService(serviceConnection);
                    }
                }
                TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                if(mgr != null) {
                    mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
                }
                System.gc();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            MDToast mdToast = MDToast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT, MDToast.TYPE_INFO);
            mdToast.show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.favs) {
            centralViewPager.setCurrentItem(3, true);

        }
        else if(id == R.id.queue)
        {
            AppBarLayout appBarLayout = findViewById(R.id.appbarMain);
            appBarLayout.animate().alpha(0f).setDuration(400);
            appBarLayout.setVisibility(View.GONE);

            ViewPager viewPager = findViewById(R.id.containerVP);
            viewPager.setVisibility(View.GONE);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frameLayoutMain, new QueueFragment());
            fragmentTransaction.commit();
        }
        else if (id == R.id.playlists) {
            centralViewPager.setCurrentItem(4, true);
        } else if (id == R.id.equalizer) {
            Toast.makeText(this, "equalizer", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.moodSelector) {

            MoodSelectorDailog moodSelectorDailog = new MoodSelectorDailog(this);
            presentMood = moodSelectorDailog.generateMoodSelectorDailog();
            //moodEditor(presentMood);
        } else if (id == R.id.moodSetter) {

            Intent intent = new Intent(this, MoodEngineActivity.class);
            startActivity(intent);

        } else if (id == R.id.shareTheApp) {
            //this creates an object for the sharing class and shares the app with all available social media platforms
            new ShareAPP().shareThisApp(this);
        }else if (id == R.id.rate) {
            Toast.makeText(this, "Rate", Toast.LENGTH_SHORT).show();

        }else if (id == R.id.settings) {
            //this option opens the settings
            Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(intent);
            finish();

        }else if (id == R.id.about) {
            Toast.makeText(this, "about", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    private void bindUIwithService()
    {
        Intent binderIntent = new Intent(this, MusicService.class);
        bindService(binderIntent,serviceConnection,Context.BIND_AUTO_CREATE);
    }

    private void getAllBroadcasts()
    {
        broadcastReceiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                positionOfPointerOnList = intent.getIntExtra("position", 0);
                callSource = intent.getIntExtra("callsource", callSource);
                audioSession = intent.getIntExtra("audioSession",-1);
                totalSize= intent.getIntExtra("duration",0);
                if(prevCallSource != callSource)
                {
                    clearList();
                    putSongsInUI(callSource);
                }

                setAllTexts(songAL.get(positionOfPointerOnList).getTrackName(),songAL.get(positionOfPointerOnList).getAlbumName(),songAL.get(positionOfPointerOnList).getAlbumID(), songAL);
                audioIsPlaying = true;
                playPauseEnlarged.setImageResource(android.R.drawable.ic_media_pause);
                smallPlayerPlay.setImageResource(android.R.drawable.ic_media_pause);

                enableHeadSetControls();
                enablePhoneStateControls();
            }
        };
        registerReceiver(broadcastReceiver1, new IntentFilter("abc"));

    }

    private void putSongsInUI(int call)
    {
        if(call == 9) {
            //if the call source is 9 that means the click event happened in the songlistRVadapter
            clearList();
            memoryAccess.accessMemoryForSongs();
            songAL = memoryAccess.getSongAL();
            prevCallSource = call;
        }
        else if(call == 7)
        {//if the call source is 7 that means the click event happened in the favourites
            FavouritesDB favouritesDB = new FavouritesDB(this);
            Cursor cur = favouritesDB.getAllData();
            if(cur!=null)
            {
                if(cur.moveToFirst())
                {
                    do{
                        songAL.add(new Song(cur.getLong(1),
                                cur.getString(2),
                                cur.getString(4),
                                cur.getString(3),
                                cur.getString(5),
                                cur.getString(7),
                                cur.getString(6),
                                cur.getString(8)
                                ));
                    }while(cur.moveToNext());
                }
            }
            prevCallSource = call;
        }
        else if(call == 6)
        {//coming from queue database
            Cursor cur = new CentralQueue(this).getAllData();
            if(cur!=null)
            {
                if(cur.moveToFirst())
                {
                    do{
                        songAL.add(new Song(cur.getLong(1),
                                cur.getString(2),
                                cur.getString(4),
                                cur.getString(3),
                                cur.getString(5),
                                cur.getString(7),
                                cur.getString(6),
                                cur.getString(8)
                        ));
                    }while(cur.moveToNext());
                }
            }
            prevCallSource = call;
        }
    }

    private void clearList()
    {
       songAL.clear();
    }

    private void setAllTexts(String trackName, String albumName, long albumID, ArrayList<Song> song_al)
    {

        albumIDrcvd = albumID;
        if(trackName !=null) {
            smallPlayerSongNameTV.setText(processorTool.reformatTrackName(trackName));
            enlargedSongTV.setText(trackName);
        }
        else {
            smallPlayerSongNameTV.setText("Unknown");
            enlargedSongTV.setText("Unknown");
        }

        if(albumName != null)
        {
            smallPlayerAlbumNameTV.setText(processorTool.reformatTrackName(albumName));
            enlargedAlbumTV.setText(albumName);
        }
        else
        {
            smallPlayerAlbumNameTV.setText("Unknown");
            enlargedAlbumTV.setText("Unknown");
        }
        barVisualizerSmall.setAudioSessionId(audioSession);
        if(albumID != 0)
        {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, albumID);
            Glide.with(this)
                    .asBitmap()
                    .load(uri)
                    .apply(RequestOptions.placeholderOf(R.drawable.defaultalbumpic).error(R.drawable.defaultalbumpic))
                    .into(smallPlayerIV);
            try {
               Blurry.with(getApplicationContext()).from(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri)).into(backgroundIV);

            } catch (IOException e) {
               Blurry.with(getApplicationContext()).from( BitmapFactory.decodeResource(getResources(), R.drawable.defaultalbumpic)).into(backgroundIV);
            }
        }

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, song_al);
        horizontalInfiniteCycleViewPager.setAdapter(viewPagerAdapter);
        horizontalInfiniteCycleViewPager.setCurrentItem(positionOfPointerOnList,true);
    }

    private void enableHeadSetControls()
    {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                boolean plugged, unplugged;

                if (Intent.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
                    unplugged = intent.getIntExtra("state", -1) == 0;
                    plugged = intent.getIntExtra("state", -1) == 1;
                } else {
                    unplugged = plugged = false;
                }

                boolean becomingNoisy = ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction());

                if (unplugged || becomingNoisy) {
                    musicService.pauseMedia();
                    playPauseEnlarged.setImageResource(android.R.drawable.ic_media_play);
                    smallPlayerPlay.setImageResource(android.R.drawable.ic_media_play);
                    audioIsPlaying = false;

                } else if (plugged) {

                        musicService.playMedia();
                        playPauseEnlarged.setImageResource(android.R.drawable.ic_media_pause);
                        smallPlayerPlay.setImageResource(android.R.drawable.ic_media_pause);
                        audioIsPlaying = true;
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
    }

    private void enablePhoneStateControls()
    {
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    //Incoming call: Pause music

                    audioIsPlaying = false;
                    playPauseEnlarged.setImageResource(android.R.drawable.ic_media_play);
                    smallPlayerPlay.setImageResource(android.R.drawable.ic_media_play);
                    musicService.pauseMedia();


                } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                    //Not in call: Play music

                        audioIsPlaying = true;
                        playPauseEnlarged.setImageResource(android.R.drawable.ic_media_pause);
                        smallPlayerPlay.setImageResource(android.R.drawable.ic_media_pause);
                        musicService.playMedia();
                } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    //A call is dialing, active or on hold
                    audioIsPlaying = false;
                    playPauseEnlarged.setImageResource(android.R.drawable.ic_media_play);
                    smallPlayerPlay.setImageResource(android.R.drawable.ic_media_play);
                    musicService.pauseMedia();
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void activateAllPanelUI()
    {
        smallPlayerPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(audioIsPlaying)
                {
                    musicService.pauseMedia();
                    smallPlayerPlay.setImageResource(android.R.drawable.ic_media_play);
                    playPauseEnlarged.setImageResource(android.R.drawable.ic_media_play);
                    audioIsPlaying = false;
                }
                else
                {
                    musicService.playMedia();
                    smallPlayerPlay.setImageResource(android.R.drawable.ic_media_pause);
                    playPauseEnlarged.setImageResource(android.R.drawable.ic_media_pause);
                    audioIsPlaying = true;
                }
            }
        });

        //play and pause button of the main screen
        playPauseEnlarged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(audioIsPlaying)
                {
                    musicService.pauseMedia();
                    playPauseEnlarged.setImageResource(android.R.drawable.ic_media_play);
                    smallPlayerPlay.setImageResource(android.R.drawable.ic_media_play);
                    audioIsPlaying = false;
                }
                else
                {
                    musicService.playMedia();
                    playPauseEnlarged.setImageResource(android.R.drawable.ic_media_pause);
                    smallPlayerPlay.setImageResource(android.R.drawable.ic_media_pause);
                    audioIsPlaying = true;
                }
            }
        });

        //play next music
        playNextEnlarged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                musicService.playNextMedia();
            }
        });

        //play previous music button
        playPreviousEnlarged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                musicService.playPreviousMedia();
            }
        });

        //add as favourite
        favsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FavouritesDB favouritesDB = new FavouritesDB(getApplicationContext());

                if(!added) {
                    favouritesDB.insertData(String.valueOf( songAL.get(positionOfPointerOnList).getAlbumID()),
                                                            songAL.get(positionOfPointerOnList).getTrackName(),
                                                            songAL.get(positionOfPointerOnList).getSongLink(),
                                                            songAL.get(positionOfPointerOnList).getAlbumName(),
                                                            songAL.get(positionOfPointerOnList).getArtistName(),
                                                            songAL.get(positionOfPointerOnList).getComposerName(),
                                                            songAL.get(positionOfPointerOnList).getSongDuration(),
                                                            songAL.get(positionOfPointerOnList).getSongSize());

                    Snackbar snackbar = Snackbar.make(drawer, songAL.get(positionOfPointerOnList).getTrackName() + " has been added to favourites! ", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    favsButton.setImageResource(R.drawable.favourites_select);
                    added = true;
                }
                else {
                    favouritesDB.deleteData(songAL.get(positionOfPointerOnList).getTrackName());
                    Snackbar snackbar = Snackbar.make(drawer, songAL.get(positionOfPointerOnList).getTrackName() + " has been removed from favourites. ", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    favsButton.setImageResource(R.drawable.favorite_deselect);
                    added = false;
                }
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!songLoops)
                {
                    musicService.loopSong();
                    repeatButton.setImageResource(R.drawable.repeat_one_hertz);
                    songLoops = true;
                }
                else
                {
                    musicService.loopSong();
                    repeatButton.setImageResource(R.drawable.repeat_hertz);
                    songLoops = false;
                }
            }
        });


         horizontalInfiniteCycleViewPager.setOnInfiniteCyclePageTransformListener(new OnInfiniteCyclePageTransformListener() {
            @Override
            public void onPreTransform(View page, float position) {

            }

            @Override
            public void onPostTransform(View page, float position) {
                previousposition = positionOfPointerOnList;
                positionOfPointerOnList = horizontalInfiniteCycleViewPager.getRealItem();

                smallPlayerSongNameTV.setText(processorTool.reformatTrackName(songAL.get(positionOfPointerOnList).getTrackName()));
                smallPlayerAlbumNameTV.setText(processorTool.reformatTrackName(songAL.get(positionOfPointerOnList).getAlbumName()));
                enlargedSongTV.setText(songAL.get(positionOfPointerOnList).getTrackName());
                enlargedAlbumTV.setText(songAL.get(positionOfPointerOnList).getTrackName());

                if(songAL.get(positionOfPointerOnList).getAlbumID() != 0)
                {
                    final Uri sArtworkUri = Uri
                            .parse("content://media/external/audio/albumart");

                    Uri uri = ContentUris.withAppendedId(sArtworkUri, songAL.get(positionOfPointerOnList).getAlbumID());
                    Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(uri)
                            .apply(RequestOptions.placeholderOf(R.drawable.defaultalbumpic).error(R.drawable.defaultalbumpic))
                            .into(smallPlayerIV);
                    try {
                        Blurry.with(getApplicationContext()).async().from(MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri)).into(backgroundIV);

                    } catch (IOException e) {
                        Blurry.with(getApplicationContext()).async().from( BitmapFactory.decodeResource(getResources(), R.drawable.defaultalbumpic)).into(backgroundIV);
                    }
                }

                if(previousposition < horizontalInfiniteCycleViewPager.getRealItem())
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                             musicService.playNextMedia();
                        }
                    },100);
                }
                else if(previousposition > positionOfPointerOnList)
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            musicService.playPreviousMedia();
                        }
                    },100);
                }
            }
        });
    }

    private void getSystemSettings()
    {
        ApplicationSettings applicationSettings = new ApplicationSettings(this);
        Log.i("settings", String.valueOf(applicationSettings.getDarkMode()) );
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position)
            {
                case 0:
                    //this case will take the user to the songs fragment
                    return new SongListFragment();

                case 1:
                    return new AlbumFragment();

                    case 2:
                        return new ArtistFragment();

                case 3:
                    return new FavouritesFragment();

                case 4:
                    return new PlaylistFragment();

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 5;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }
    }
}
