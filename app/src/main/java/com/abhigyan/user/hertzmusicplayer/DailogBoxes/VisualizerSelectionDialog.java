package com.abhigyan.user.hertzmusicplayer.DailogBoxes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

public class VisualizerSelectionDialog {

    Context context;
    final String[] visualizers ={"Bar Visualizer","Blast Visualizer", "Wave Visualizer", "Blob Visualizer"};
    private Dialog visualizerDialog;
    private int position  = 0;

    public VisualizerSelectionDialog(Context context) {
        this.context = context;
    }

    public int startMakingDailog(String dailogTitle, String positiveText, String negativeText)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(dailogTitle);
        builder.setSingleChoiceItems(visualizers, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                position = ((AlertDialog)visualizerDialog).getListView().getCheckedItemPosition();
            }
        }).setNegativeButton(negativeText, null );

        visualizerDialog = builder.create();
        visualizerDialog.show();
        return position;
    }
}
