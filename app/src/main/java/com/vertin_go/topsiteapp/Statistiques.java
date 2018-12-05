package com.vertin_go.topsiteapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import static com.vertin_go.topsiteapp.RegistrationIntentService.Pseudo;

public class Statistiques extends AppCompatActivity
{
    public LinearLayout rootContent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistiques);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViews();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeScreenshot(ScreenshotType.FULL);
            }
        });

        String user=Pseudo;
        final String[] tab_url =
                {
                "http://vertin-go.com/TopSite/configuration/pChart2.1.4/examples/pictures/statistiques_personnels/"+user+"%7Cdraw2DRingValue.png",
                "http://vertin-go.com/TopSite/configuration/pChart2.1.4/examples/pictures/statistiques_personnels/"+user+"%7CPartage(s)%7CdrawBarChart.vertical.png",
                "http://vertin-go.com/TopSite/configuration/pChart2.1.4/examples/pictures/statistiques_personnels/"+user+"%7CTelechargement(s)%7CdrawBarChart.vertical.png",
                "http://vertin-go.com/TopSite/configuration/pChart2.1.4/examples/pictures/statistiques_personnels/"+user+"%7CTotal(s)%20Clic(s)%7CdrawBarChart.vertical.png",
                "http://vertin-go.com/TopSite/configuration/pChart2.1.4/examples/pictures/statistiques_personnels/"+user+"%7CTotal(s)%20Vote(s)%7CdrawBarChart.vertical.png",
                "http://vertin-go.com/TopSite/configuration/pChart2.1.4/examples/pictures/statistiques_personnels/"+user+"%7CAffichage(s)%20Popup(s)%7CdrawBarChart.vertical.png",
                "http://vertin-go.com/TopSite/configuration/pChart2.1.4/examples/pictures/statistiques_personnels/"+user+"%7Cdocuments.drawAreaMirror.png",
                "http://vertin-go.com/TopSite/configuration/pChart2.1.4/examples/pictures/statistiques_personnels/"+user+"%7CCombo.area.lines.png"
        };

        Button btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int i=0;
                while(i<tab_url.length)
                {
                    if(i==0)
                    {
                        final ImageView imageview = (ImageView) findViewById(R.id.imageViewStats0);
                        new DownLoadImageTask(imageview).execute(tab_url[i]);
                    }
                    else if(i==1)
                    {
                        final ImageView imageview = (ImageView) findViewById(R.id.imageViewStats1);
                        new DownLoadImageTask(imageview).execute(tab_url[i]);
                    }
                    else if(i==2)
                    {
                        final ImageView imageview = (ImageView) findViewById(R.id.imageViewStats2);
                        new DownLoadImageTask(imageview).execute(tab_url[i]);
                    }
                    else if(i==3)
                    {
                        final ImageView imageview = (ImageView) findViewById(R.id.imageViewStats3);
                        new DownLoadImageTask(imageview).execute(tab_url[i]);
                    }
                    else if(i==4)
                    {
                        final ImageView imageview = (ImageView) findViewById(R.id.imageViewStats4);
                        new DownLoadImageTask(imageview).execute(tab_url[i]);
                    }
                    else if(i==5)
                    {
                        final ImageView imageview = (ImageView) findViewById(R.id.imageViewStats5);
                        new DownLoadImageTask(imageview).execute(tab_url[i]);
                    }
                    else if(i==6)
                    {
                        final ImageView imageview = (ImageView) findViewById(R.id.imageViewStats6);
                        new DownLoadImageTask(imageview).execute(tab_url[i]);
                    }
                    else if(i==7)
                    {
                        final ImageView imageview = (ImageView) findViewById(R.id.imageViewStats7);
                        new DownLoadImageTask(imageview).execute(tab_url[i]);
                    }
                    i=i+1;
                }
            }
        });

    }

    /*  Find all views Ids  */
    public void findViews()
    {
        rootContent = (LinearLayout) findViewById(R.id.root_content);
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
        if(b != null)
        {
            File saveFile = ScreenshotUtils.getMainDirectoryName(this);//get the path to save screenshot
            File file = ScreenshotUtils.store(b, "screenshot" + screenshotType + ".jpg", saveFile);//save the screenshot to selected path
            shareScreenshot(file);//finally share screenshot
        } else
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

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_inventaires, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_accueil:
                Intent intent=new Intent(Statistiques.this,MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_inventaires:
                Intent intent2=new Intent(Statistiques.this,Inventaires.class);
                startActivity(intent2);
            /* DO INVENTAIRES */
                return true;
            case R.id.action_notifications:
                Intent intent3=new Intent(Statistiques.this,Notifications.class);
                startActivity(intent3);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_envoyer_et_partager_document:
                Intent intent4=new Intent(Statistiques.this,Menu_PartageActivity.class);
                startActivity(intent4);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_menu_principale:
                Intent intent5=new Intent(Statistiques.this,Menu_Principale_Drawer.class);
                startActivity(intent5);
            /* DO NOTIFICATIONS */
                return true;
            /* case R.id.action_mes_videos_you_tube:
                Intent intent6=new Intent(Statistiques.this,YouTubeListActivity.class);
                startActivity(intent6);*/
            /* DO NOTIFICATIONS */
            //return true;
            case R.id.action_envoyer_et_partager_you_tube:
                Intent intent7=new Intent(Statistiques.this,YouTubeListActivity.class);
                startActivity(intent7);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_you_tube_playlist:
                Intent intent8=new Intent(Statistiques.this,PlayerControlsDemoActivity.class);
                startActivity(intent8);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_you_tube_wall:
                Intent intent9=new Intent(Statistiques.this,VideoWallDemoActivity.class);
                startActivity(intent9);
            /* DO NOTIFICATIONS */
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}


class DownLoadImageTask extends AsyncTask<String,Void,Bitmap>
{
    ImageView imageView;

    public DownLoadImageTask(ImageView imageView){
        this.imageView = imageView;
    }

    /*
        doInBackground(Params... params)
            Override this method to perform a computation on a background thread.
     */
    protected Bitmap doInBackground(String...urls)
    {
        String urlOfImage = urls[0];
        Bitmap logo = null;
        try{
            InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
            logo = BitmapFactory.decodeStream(is);
        }
        catch(Exception e)
        { // Catch the download exception
            e.printStackTrace();
        }
        return logo;
    }

    /*
        onPostExecute(Result result)
            Runs on the UI thread after doInBackground(Params...).
     */
    protected void onPostExecute(Bitmap result){
        imageView.setImageBitmap(result);
    }
}

