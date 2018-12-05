package com.vertin_go.topsiteapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;

public class Menu_Principale_Drawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    public LinearLayout rootContent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__principale__drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViews();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    /*  Find all views Ids  */
    public void findViews()
    {
        rootContent = findViewById(R.id.root_content);
    }

    /*  Method which will take screenshot on Basis of Screenshot Type ENUM  */
    public void takeScreenshot(ScreenshotType screenshotType)
        {
        Bitmap b = null;
        switch (screenshotType)
        {
            case FULL:
                //If Screenshot type is FULL take full page screenshot i.e our root content.
                b = ScreenshotUtils.getScreenShot(rootContent);
                break;
        }

        //If bitmap is not null
        if(b!=null)
        {
            File saveFile = ScreenshotUtils.getMainDirectoryName(this);//get the path to save screenshot
            File file = ScreenshotUtils.store(b, "screenshot" + screenshotType + ".jpg", saveFile);//save the screenshot to selected path
            shareScreenshot(file);//finally share screenshot
        }
        else
            //If bitmap is null show toast message
            Toast.makeText(this, R.string.screenshot_take_failed, Toast.LENGTH_SHORT).show();

    }


    /*  Share Screenshot  */
    public void shareScreenshot(File file)
    {
        Uri uri = Uri.fromFile(file);//Convert file path into Uri for sharing
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.sharing_text));
        intent.putExtra(Intent.EXTRA_STREAM, uri);//pass uri here
        startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera)
        {
            // Handle the camera action
            takeScreenshot(ScreenshotType.FULL);
        }
        else if(id == R.id.nav_gallery)
        {
            Intent intent = new Intent(Menu_Principale_Drawer.this,
                    Menu_Inventaires.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_slideshow)
        {
            Intent intent = new Intent(Menu_Principale_Drawer.this,
                    YouTubeListActivity.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_share)
        {
            Intent intent = new Intent(Menu_Principale_Drawer.this,
                    PartageActivity.class);
            String choix="share";
            intent.putExtra("choix", choix);
            startActivity(intent);

        }
        else if (id == R.id.nav_send)
        {
            Intent intent = new Intent(Menu_Principale_Drawer.this,
                    YouTubeListCreditActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_download)
        {
            Intent intent = new Intent(Menu_Principale_Drawer.this,
                    PartageActivity.class);
            String choix="download";
            intent.putExtra("choix", choix);
            startActivity(intent);
        }
        else if (id == R.id.nav_stats)
        {
            Intent intent = new Intent(Menu_Principale_Drawer.this,
                    Statistiques.class);
            startActivity(intent);
        }

        return true;
    }

}
