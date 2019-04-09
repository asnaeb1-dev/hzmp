package com.abhigyan.user.hertzmusicplayer.DailogBoxes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.abhigyan.user.hertzmusicplayer.Activities.NavigationMusicActivity;
import com.abhigyan.user.hertzmusicplayer.R;
import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener;

import jp.wasabeef.blurry.Blurry;

/**
 *
 *  MOOD PRESETS:
 *  1- depressed
 *  2- sad
 *  3- aimless
 *  4- well
 *  5- okay
 *  6- not bad
 *  7- good
 *  8- great
 *  9- awesome
 *  10- ecstatic
 */

public class MoodSelectorDailog {

    Context context;
    private Croller croller;
    private Button button;
    private ImageView backgroundIV;
    private int progress1;
    private AlertDialog mBuilder;

    public MoodSelectorDailog(Context context) {
        this.context = context;
    }

    public int generateMoodSelectorDailog()
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View moodSelectordailogView = inflater.inflate(R.layout.mood_selector_ui,null);
        mBuilder = new android.app.AlertDialog.Builder(context).create();
        croller = moodSelectordailogView.findViewById(R.id.croller);
        button = moodSelectordailogView.findViewById(R.id.playButton);
        croller.setMax(10);
        croller.setMin(1);
        int moodValue = crollerListener();
        backgroundIV = moodSelectordailogView.findViewById(R.id.mood_selector_back_img);
        Blurry.with(context).from(BitmapFactory.decodeResource(context.getResources(),R.drawable.defaultalbumpic)).into(backgroundIV);

        mBuilder.setView(moodSelectordailogView);
        mBuilder.show();

        return moodValue;
    }

    private void buttonClickListener(final Context localContext, final int pr)
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pr == 1 || pr ==2)
                {   //will play sad music
                    Intent mood1Intent = new Intent(localContext, NavigationMusicActivity.class);
                    mood1Intent.putExtra("callSource", 10);
                    localContext.startActivity(mood1Intent);
                }else if(pr ==3 || pr == 4)
                {
                    //will play normal music
                    Intent mood2Intent = new Intent(localContext, NavigationMusicActivity.class);
                    mood2Intent.putExtra("callSource", 11);
                    localContext.startActivity(mood2Intent);

                }else if (pr == 5 || pr == 6)
                {   //will play okay music

                    Intent mood3Intent = new Intent(localContext, NavigationMusicActivity.class);
                    mood3Intent.putExtra("callSource", 12);
                    localContext.startActivity(mood3Intent);

                }else if(pr == 7 || pr == 8)
                {
                    //will play happy song
                    Intent mood4Intent = new Intent(localContext, NavigationMusicActivity.class);
                    mood4Intent.putExtra("callSource", 13);
                    localContext.startActivity(mood4Intent);

                }else if(pr == 9 || pr == 10)
                {
                    //will play very happy song
                    Intent mood5Intent = new Intent(localContext, NavigationMusicActivity.class);
                    mood5Intent.putExtra("callSource", 14);
                    localContext.startActivity(mood5Intent);
                }
                mBuilder.dismiss();

            }
        });
    }

    private int crollerListener()
    {
        croller.setOnCrollerChangeListener(new OnCrollerChangeListener() {
            @Override
            public void onProgressChanged(Croller croller, int progress) {

                switch (progress)
                {
                    case 1:
                        croller.setLabel("Depressed");
                        progress1 = progress;
                        break;

                    case 2:
                        croller.setLabel("Sad");
                        progress1 = progress;

                        break;

                    case 3:
                        croller.setLabel("Aimless");
                        progress1 = progress;

                        break;

                    case 4:
                        croller.setLabel("Well");
                        progress1 = progress;
                        break;

                    case 5:
                        croller.setLabel("Okay");
                        progress1 = progress;

                        break;

                    case 6:
                        croller.setLabel("Not bad");
                        progress1 = progress;

                        break;

                    case 7:
                        croller.setLabel("Good");
                        progress1 = progress;

                        break;

                    case 8:
                        croller.setLabel("Great");
                        progress1 = progress;

                        break;

                    case 9:
                        croller.setLabel("Awesome");
                        progress1 = progress;

                        break;

                    case 10:
                        croller.setLabel("Ecstatic");
                        progress1 = progress;

                        break;

                    default:
                        break;


                }
                progress1 = progress;
                buttonClickListener(context,progress1);

            }

            @Override
            public void onStartTrackingTouch(Croller croller) {

            }

            @Override
            public void onStopTrackingTouch(Croller croller) {

            }
        });
        return progress1;
    }

}
