package com.abhigyan.user.hertzmusicplayer.Utility;
import android.content.Context;
import android.content.Intent;
import com.abhigyan.user.hertzmusicplayer.BuildConfig;
import com.abhigyan.user.hertzmusicplayer.R;

public class ShareAPP {

    public void shareThisApp(Context context)
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.complete_app_name));
        String shareMessage= "\nHey! Try this application\n\n";
        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        context.startActivity(Intent.createChooser(shareIntent, "Share Hertz app on"));
    }
}
