package com.abhigyan.user.hertzmusicplayer.Utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.abhigyan.user.hertzmusicplayer.DailogBoxes.MoodSelectorDailog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class DateTracker {

    Context context;

    public DateTracker(Context context) {
        this.context = context;
    }

    public int showMoodSelectorAccordingToDate()
    {
        int presentMood = -1;
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c);
        SharedPreferences dateSharedPrefs = context.getSharedPreferences("DatePref",MODE_PRIVATE);

        if(!dateSharedPrefs.getString("date","").equals(formattedDate))
        {
            SharedPreferences.Editor editor = dateSharedPrefs.edit();
            editor.putString("date",formattedDate);
            editor.apply();
            MoodSelectorDailog moodSelectorDailog = new MoodSelectorDailog(context);
            presentMood = moodSelectorDailog.generateMoodSelectorDailog();
        }
        return presentMood;
    }
}
