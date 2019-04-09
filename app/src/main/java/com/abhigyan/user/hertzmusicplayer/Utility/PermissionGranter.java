package com.abhigyan.user.hertzmusicplayer.Utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.abhigyan.user.hertzmusicplayer.Activities.MainActivity;

/**
 * THIS CLASS GRANTS ALL THE REQUIRED PERMISSIONS FOR RUNNING THE APP
 */

public class PermissionGranter {

    private Context context;
    private static final int FAILURE_CODE = -10;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 100;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 200;
    private static final int READ_PHONE_STATE_REQUEST_CODE = 300;
    private static final int RECORD_AUDIO_REQUEST_CODE = 400;

    public PermissionGranter(Context context) {
        this.context = context;
    }

    private void checkReadExternalStoragePermission()
    {//this method checks if permission is granted or not
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    public boolean checkReadPhoneStatePermission()
    {

        return false;
    }

    /*private boolean checkwriteExternalStoragePermission()
    {
    }*/

    /*public int askWriteExternalStoragePermission()
    {
    }*/

    public boolean recordAudio()
    {
        return  false;
    }

}
