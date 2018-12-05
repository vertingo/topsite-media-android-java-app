package com.vertin_go.topsiteapp;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.vertin_go.topsiteapp.RegistrationIntentService.Urls_users;
import static com.vertin_go.topsiteapp.RegistrationIntentService.Urls_users_ext;
import static com.vertin_go.topsiteapp.RegistrationIntentService.Urls_users_noms;

public class Inventaires extends AppCompatActivity
{
    ProgressDialog mProgressDialog;
    DownloadManager manager;
    private ArrayList<String> data = new ArrayList<String>();
    ArrayList<String> Urls_Adapt = new ArrayList<String>();
    static String type;
    public LinearLayout rootContent;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventaires);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());

        findViews();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                takeScreenshot(ScreenshotType.FULL);
            }
        });

        Intent intent = getIntent();
        // On suppose que tu as mis un String dans l'Intent via le putExtra()
        String choix = intent.getStringExtra("choix");

        ListView lv = findViewById(R.id.listview);
        String type_liste=choix;
        type=choix;

        if(type_liste.equals("pdf"))
        {
            generateListContent("pdf");
            lv.setAdapter(new MyListAdaper(this, R.layout.list_item, data));
        }
        else if(type_liste.equals("zip"))
        {
            generateListContent("zip");
            lv.setAdapter(new MyListAdaper(this, R.layout.list_item3, data));
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Inventaires.this, "List item was clicked at " + position, Toast.LENGTH_SHORT).show();

            }
        });

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
        if (b != null)
        {
            Toast.makeText(this, R.string.screenshot_take_failed, Toast.LENGTH_SHORT).show();

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


    private void generateListContent(String type)
    {
       if(type.equals("pdf"))
        {
            for(int i=0;i<Urls_users_noms.length;i++)
            {
                if (Urls_users_ext[i].equals("application/pdf")) {
                    data.add(Urls_users_noms[i]);
                    Urls_Adapt.add(Urls_users[i]);
                }
            }
        }
        else if(type.equals("zip"))
        {
            for(int i=0;i<Urls_users_noms.length;i++)
            {
                if (Urls_users_ext[i].equals("application/zip")) {
                    data.add(Urls_users_noms[i]);
                    Urls_Adapt.add(Urls_users[i]);
                }
            }
        }

    }

    public class DownloadTask extends AsyncTask<String, Integer, String>
    {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress)
        {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result)
        {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... sUrl)
        {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total*100/fileLength));
                }
            }
            catch (Exception e)
            {
                return e.toString();
            }
            finally
            {
                try
                {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                }
                catch (IOException ignored)
                {

                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

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
                Intent intent=new Intent(Inventaires.this,MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_envoyer_et_partager_document:
                Intent intent2=new Intent(Inventaires.this,PartageActivity.class);
                startActivity(intent2);
            /* DO INVENTAIRES */
                return true;
            case R.id.action_stats:
                Intent intent3=new Intent(Inventaires.this,Statistiques.class);
                startActivity(intent3);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_notifications:
                Intent intent4=new Intent(Inventaires.this,Notifications.class);
                startActivity(intent4);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_menu_principale:
                Intent intent5=new Intent(Inventaires.this,Menu_Principale_Drawer.class);
                startActivity(intent5);
            /* DO NOTIFICATIONS */
                return true;
             /* case R.id.action_mes_videos_you_tube:
                Intent intent6=new Intent(Inventaires.this,YouTubeListActivity.class);
                startActivity(intent6);*/
            /* DO NOTIFICATIONS */
            //return true;
            case R.id.action_envoyer_et_partager_you_tube:
                Intent intent7=new Intent(Inventaires.this,YouTubeListActivity.class);
                startActivity(intent7);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_you_tube_playlist:
                Intent intent8=new Intent(Inventaires.this,PlayerControlsDemoActivity.class);
                startActivity(intent8);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_you_tube_wall:
                Intent intent9=new Intent(Inventaires.this,VideoWallDemoActivity.class);
                startActivity(intent9);
            /* DO NOTIFICATIONS */
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MyListAdaper extends ArrayAdapter<String>
    {
        private int layout;
        private List<String> mObjects;
        private MyListAdaper(Context context, int resource, List<String> objects)
        {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            ViewHolder mainViewholder = null;
            if(convertView == null)
            {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.button = (Button) convertView.findViewById(R.id.list_item_download);

                if(type.equals("pdf"))
                {
                    viewHolder.button2 = (Button) convertView.findViewById(R.id.list_item_voir);
                }
                viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.list_item_pdf);
                viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_text);

                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            mainViewholder.button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

               /*     if(type.equals("banniere") || type.equals("annonce"))
                    {
                        String url = Urls_Adapt.get(position);
                        Intent intent = new Intent(Inventaires.this,ShareDialogPartageActivity.class);
                        intent.putExtra("inventaires","oui");
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                    else
                    {*/
                        mProgressDialog = new ProgressDialog(Inventaires.this);
                        mProgressDialog.setMessage("Téléchargement du fichier: " + data.get(position));
                        mProgressDialog.setIndeterminate(true);
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        mProgressDialog.setCancelable(true);
                        // execute this when the downloader must be fired

                        String url = Urls_Adapt.get(position);
                        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.setTitle(data.get(position));
                        // in order for this if to run, you must use the android 3.2 to compile your app

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                        {
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                            final DownloadTask downloadTask = new DownloadTask(Inventaires.this);
                            downloadTask.execute(url);

                            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
                            {
                                @Override
                                public void onCancel(DialogInterface dialog)
                                {
                                    downloadTask.cancel(true);
                                }
                            });
                        }

                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, data.get(position));
                        manager.enqueue(request);
                   // }
                }
            });

            if(type.equals("pdf")) {
                mainViewholder.button2.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        String url = Urls_Adapt.get(position);
                        Intent intent = new Intent(Inventaires.this, RemotePDFActivity.class);
                        intent.putExtra("inventaires", "oui");
                        intent.putExtra("url", url);
                        startActivity(intent);

                    }
                });
            }
            mainViewholder.title.setText(getItem(position));

            return convertView;
        }
    }
    public class ViewHolder
    {
        ImageView thumbnail;
        TextView title;
        Button button;
        Button button2;
    }
}



