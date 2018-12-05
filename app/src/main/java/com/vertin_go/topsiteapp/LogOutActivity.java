package com.vertin_go.topsiteapp;

/**
 * Created by VertinGo on 30/09/2017.
 */

import android.app.Activity;

import com.google.firebase.appindexing.FirebaseAppIndex;

public class LogOutActivity extends Activity
{
    private void onLogOutButtonClicked()
    {
        FirebaseAppIndex.getInstance().removeAll();
    }
}
