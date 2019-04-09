package com.abhigyan.user.hertzmusicplayer.DailogBoxes;

import android.app.AlertDialog;
import android.content.Context;

public class InstructionsDialogMoodSetter {

    Context context;

    public InstructionsDialogMoodSetter(Context context) {
        this.context = context;
    }

    public void makeDialog()
    {
        AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(context);
        mBuilder.setTitle("Instructions");
        mBuilder.setMessage("Long press on each audio and then select the mood where you want each audio to be placed. Deselect all the continue.");
        mBuilder.show();
    }

}
