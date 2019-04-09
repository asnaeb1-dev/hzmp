package com.abhigyan.user.hertzmusicplayer.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.abhigyan.user.hertzmusicplayer.R;
import com.abhigyan.user.hertzmusicplayer.Utility.ApplicationSettings;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;
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
        setContentView(R.layout.activity_settings);
        settingsToolbar = findViewById(R.id.settingToolbar);
        setToolBar();

        Fragment fragment = new SettingsScreen();
        // this fragment must be from android.app.Fragment,
        // if you use support fragment, it will not work

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            transaction.add(R.id.settingsFrameLayout, fragment, "settings_screen");
        }
        transaction.commit();

    }

    private void setToolBar()
    {
        settingsToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        settingsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public static class SettingsScreen extends PreferenceFragment
    {
        private SwitchPreference autoEmotionPredictSwitch,
                                artistartSwitch;

        private CheckBoxPreference checkBoxPreferenceAutoDarkMode,
                                   moodSelectorDialogCheckBox,
                                   lastSongRememberCheckbox;

        public ApplicationSettings applicationSettings;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if(new ApplicationSettings(getContext()).getDarkMode() == 1)
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                getContext().setTheme(R.style.DarkTheme);

            }
            else
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                getContext().setTheme(R.style.AppTheme);
            }

            addPreferencesFromResource(R.xml.settings_screen);

            applicationSettings = new ApplicationSettings(getContext());
            checkBoxPreferenceAutoDarkMode =(CheckBoxPreference) findPreference("darkModeCheckBox");
            autoEmotionPredictSwitch = (SwitchPreference) findPreference("auto_emotion_predict");
            moodSelectorDialogCheckBox = (CheckBoxPreference) findPreference("showDialogMood");
            lastSongRememberCheckbox = (CheckBoxPreference) findPreference("rememberLastSong");
            artistartSwitch = (SwitchPreference) findPreference("artistartswitch");

            //access the settings and check if the automatic dark setter is enabled and if so then disable the manual selector else enable the respective user interfaces
       //-----------------------------------AUTO DARK MODE------------------------------------------------------
            if(applicationSettings.getDarkMode() == 1)
            {
                checkBoxPreferenceAutoDarkMode.setChecked(true);
            }
            else
            {
                checkBoxPreferenceAutoDarkMode.setChecked(false);
            }
        //-----------------------------------AUTO EMOTION DIALOG--------------------------------------------------
            if(applicationSettings.isAutoEmotionPredict())
            {
                autoEmotionPredictSwitch.setDefaultValue(true);
            }
            else
            {
                autoEmotionPredictSwitch.setDefaultValue(false);
            }
        //-----------------------------------MOOD SELECTOR DIALOG--------------------------------------------------
            if(applicationSettings.isShowMoodSetterDialog())
            {
                moodSelectorDialogCheckBox.setChecked(true);
            }
            else
            {
                moodSelectorDialogCheckBox.setChecked(false);
            }

         //-----------------------------------LAST SONG--------------------------------------------------------------
            if(applicationSettings.isRememberLastSongPlayed())
            {
                moodSelectorDialogCheckBox.setChecked(true);
            }
            else
            {
                moodSelectorDialogCheckBox.setChecked(false);

            }
        //------------------------------------ARTIST ART SWITCH------------------------------------------------------
            if(applicationSettings.isArtistArtDownloadFromLastFM())
            {
                artistartSwitch.setChecked(true);
            }
            else
            {
                artistartSwitch.setChecked(false);
            }
           darkModeCheckBoxListener();
        }

        private void darkModeCheckBoxListener()
        {
            checkBoxPreferenceAutoDarkMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {

                    if(((CheckBoxPreference)preference).isChecked())
                    {
                        //mood selector dialog will not show
                        Toast.makeText(getContext(), "dark mode disabled", Toast.LENGTH_SHORT).show();
                        checkBoxPreferenceAutoDarkMode.setChecked(false);
                        applicationSettings.setDarkMode(0);
                    }
                    else
                    {
                        //mood selector dialog will show
                        Toast.makeText(getContext(), "dark mode enabled", Toast.LENGTH_SHORT).show();
                        checkBoxPreferenceAutoDarkMode.setChecked(true);
                        applicationSettings.setDarkMode(1);
                    }
                    restartApp();
                    return false;
                }
            });

            autoEmotionPredictSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {

                    if(((SwitchPreference)preference).isChecked())
                    {
                        //auto emotion predict has been disabled
                        Toast.makeText(getContext(), "auto emotion predict disabled", Toast.LENGTH_SHORT).show();
                        autoEmotionPredictSwitch.setChecked(false);
                        applicationSettings.setAutoEmotionPredict(false);
                    }
                    else
                    {
                        //auto emotion predict has been enabled
                        //show a dialog regarding the auto emotion predict's inaccuracy
                        showAlertDialog();
                    }
                    return false;
                }
            });

            moodSelectorDialogCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {

                    if(((CheckBoxPreference)preference).isChecked())
                    {
                        //mood selector dialog will not show
                        Toast.makeText(getContext(), "mood selector dialog will not show", Toast.LENGTH_SHORT).show();
                        moodSelectorDialogCheckBox.setChecked(false);
                        applicationSettings.setShowMoodSetterDialog(false);
                    }
                    else
                    {
                        //mood selector dialog will show
                        Toast.makeText(getContext(), "mood selector dialog will show", Toast.LENGTH_SHORT).show();
                        moodSelectorDialogCheckBox.setChecked(true);
                        applicationSettings.setShowMoodSetterDialog(true);
                    }
                    return false;
                }
            });

            lastSongRememberCheckbox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {

                    if(((CheckBoxPreference)preference).isChecked())
                    {
                        //last song will not be saved
                        Toast.makeText(getContext(), "last song will not be saved", Toast.LENGTH_SHORT).show();
                        lastSongRememberCheckbox.setChecked(false);
                        applicationSettings.setRememberLastSongPlayed(false);
                    }
                    else
                    {
                        //last song will be saved
                        Toast.makeText(getContext(), "last song will be saved", Toast.LENGTH_SHORT).show();
                        lastSongRememberCheckbox.setChecked(true);
                        applicationSettings.setRememberLastSongPlayed(true);
                    }

                    return false;
                }
            });

            artistartSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {

                    if(((SwitchPreference)preference).isChecked())
                    {
                        //download not from source
                        Toast.makeText(getContext(), "will not be downloaded", Toast.LENGTH_SHORT).show();
                        artistartSwitch.setChecked(false);
                        applicationSettings.setArtistArtDownloadFromLastFM(false);
                    }
                    else
                    {
                        //will be downloaded
                        Toast.makeText(getContext(), "will be downloaded", Toast.LENGTH_SHORT).show();
                        artistartSwitch.setChecked(true);
                        applicationSettings.setArtistArtDownloadFromLastFM(true);

                    }

                    return false;
                }
            });

        }

        private void restartApp() {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
            ((Activity)getContext()).finish();
        }

        private void showAlertDialog()
        {//shows warning dialog for turning on the auto emotion predict
            new AlertDialog.Builder(getContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Auto Emotion Predict")
                    .setMessage("Auto emotion predict is inaccurate in nature because of the third party sources of the audio. Are you sure you want to enable this setting?")
                    .setPositiveButton("I don't care. Do it", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            applicationSettings.setAutoEmotionPredict(true);
                            Toast.makeText(getContext(), "auto emotion predict enabled", Toast.LENGTH_SHORT).show();
                            autoEmotionPredictSwitch.setChecked(true);
                        }
                    })
                    .setNegativeButton("Whatever you say!", null)
                    .show();
        }
    }
}
