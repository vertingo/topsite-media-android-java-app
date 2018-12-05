package com.vertin_go.topsiteapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import java.io.File;

public class Notifications extends AppCompatActivity implements View.OnClickListener
{
    public Button fullPageScreenshot;
    public LinearLayout rootContent;
    public ImageView imageView;
    public TextView hiddenText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                takeScreenshot(ScreenshotType.FULL);
            }
        });


        Intent intent = getIntent();
        // On suppose que tu as mis un String dans l'Intent via le putExtra()
        String message = intent.getStringExtra("ms");
        TextView NotificationTextView = findViewById(R.id.information_notification);
        NotificationTextView.setText(message);

        LikeView likeView = findViewById(R.id.like_view);
        likeView.setObjectIdAndType("https://www.facebook.com/vertingo/?ref=bookmarks", LikeView.ObjectType.PAGE);

        ShareButton shareButton = findViewById(R.id.share_btn);
        CallbackManager callbackManager = CallbackManager.Factory.create();
        ShareDialog shareDialog = new ShareDialog(this);

        if (ShareDialog.canShow(ShareLinkContent.class))
        {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://www.facebook.com/vertingo/?ref=bookmarks"))
                    .build();
            shareButton.setShareContent(linkContent);
            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>()
            {
                @Override
                public void onSuccess(Sharer.Result result)
                {

                }

                @Override
                public void onCancel()
                {

                }

                @Override
                public void onError(FacebookException error)
                {

                }});
        }

        findViews();
        implementClickEvents();
    }

    /*  Find all views Ids  */
    public void findViews()
    {
        fullPageScreenshot = findViewById(R.id.full_page_screenshot);
        //customPageScreenshot = (Button) findViewById(R.id.custom_page_screenshot);

        rootContent = findViewById(R.id.root_content);
        imageView = findViewById(R.id.image_view);
        hiddenText = findViewById(R.id.hidden_text);
    }

    /*  Implement Click events over Buttons */
    public void implementClickEvents()
    {
        fullPageScreenshot.setOnClickListener(this);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_notifications, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_accueil:
                Intent intent=new Intent(Notifications.this,MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_stats:
                Intent intent2=new Intent(Notifications.this,Statistiques.class);
                startActivity(intent2);
            /* DO INVENTAIRES */
                return true;
            case R.id.action_inventaires:
                Intent intent3=new Intent(Notifications.this,Inventaires.class);
                startActivity(intent3);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_envoyer_et_partager_document:
                Intent intent4=new Intent(Notifications.this,Menu_PartageActivity.class);
                startActivity(intent4);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_menu_principale:
                Intent intent5=new Intent(Notifications.this,Menu_Principale_Drawer.class);
                startActivity(intent5);
            /* DO NOTIFICATIONS */
                return true;
                /* case R.id.action_mes_videos_you_tube:
                Intent intent6=new Intent(Notifications.this,YouTubeListActivity.class);
                startActivity(intent6);*/
            /* DO NOTIFICATIONS */
                //return true;
            case R.id.action_envoyer_et_partager_you_tube:
                Intent intent7=new Intent(Notifications.this,YouTubeListActivity.class);
                startActivity(intent7);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_you_tube_playlist:
                Intent intent8=new Intent(Notifications.this,PlayerControlsDemoActivity.class);
                startActivity(intent8);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_you_tube_wall:
                Intent intent9=new Intent(Notifications.this,VideoWallDemoActivity.class);
                startActivity(intent9);
            /* DO NOTIFICATIONS */
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.full_page_screenshot:
                takeScreenshot(ScreenshotType.FULL);
                break;
        }
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
        if (b != null)
        {
            showScreenShotImage(b);//show bitmap over imageview

            File saveFile = ScreenshotUtils.getMainDirectoryName(this);//get the path to save screenshot
            File file = ScreenshotUtils.store(b, "screenshot" + screenshotType + ".jpg", saveFile);//save the screenshot to selected path
            shareScreenshot(file);//finally share screenshot
        }
        else
            //If bitmap is null show toast message
            Toast.makeText(this, R.string.screenshot_take_failed, Toast.LENGTH_SHORT).show();

    }

    /*  Show screenshot Bitmap */
    public void showScreenShotImage(Bitmap b)
    {
        imageView.setImageBitmap(b);
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
}
