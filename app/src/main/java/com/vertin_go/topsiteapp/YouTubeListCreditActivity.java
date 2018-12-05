package com.vertin_go.topsiteapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.SendButton;
import com.facebook.share.widget.ShareButton;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Random;

import static com.vertin_go.topsiteapp.RegistrationIntentService.Pseudo;
import static com.vertin_go.topsiteapp.RegistrationIntentService.Urls2_deja_visite;
import static com.vertin_go.topsiteapp.RegistrationIntentService.Urls_videos;


public class YouTubeListCreditActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;
    private static final String NOTIFY_URL = "http://vertin-go.com/TopSite/configuration/GCM/notify_url3.php";
    private static final String URL_SHARE = "url_share";
    private static final String PSEUDO = "pseudo";
    private ProgressBar mProgressBar;
    static int j;
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {

        @Override
        public boolean onNavigationItemSelected(MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.navigation_home:

                    Intent intent = new Intent(YouTubeListCreditActivity.this,
                            MainActivity.class);
                    startActivity(intent);

                    break;
                case R.id.navigation_dashboard:

                    Intent intent2 = new Intent(YouTubeListCreditActivity.this,
                            Menu_Principale_Drawer.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent2);

                    break;
                case R.id.navigation_notifications:

                    Intent intent3 = new Intent(YouTubeListCreditActivity.this,
                            Notifications.class);
                    startActivity(intent3);

                    break;
            }
            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_list_credit);

        mProgressBar = findViewById(R.id.pBAsync);
        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        youTubeView = findViewById(R.id.youtube_view);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);

        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();

        Random r = new Random();
        int valeur = 0 + r.nextInt(Urls_videos.length - 0);
        j=valeur;

        String deja_vu=Urls2_deja_visite[j];

        SendButton sendButton = findViewById(R.id.send_btn);

        if(deja_vu.equals("non"))
        {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(Urls_videos[j]))
                    .build();
            sendButton.setShareContent(linkContent);
        }

        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable()
                            {
                                public void run()
                                {
                                    final String[] tab_url = {Urls_videos[j], Pseudo};
                                    YouTubeListCreditActivity.NotifyShare notify = new YouTubeListCreditActivity.NotifyShare(YouTubeListCreditActivity.this);
                                    notify.execute(tab_url);
                                }
                            }, 15000);

            }
        });

        CallbackManager callbackManager = CallbackManager.Factory.create();
        sendButton.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>()
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

            } });


        ShareButton shareButton = findViewById(R.id.share_btn);

        if(deja_vu.equals("non"))
        {
            ShareLinkContent linkContent2 = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(Urls_videos[j]))
                    .build();
            shareButton.setShareContent(linkContent2);
        }

        shareButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable()
                    {
                        public void run()
                        {
                            final String[] tab_url = {Urls_videos[j],Pseudo};
                            NotifyShare notify=new NotifyShare(YouTubeListCreditActivity.this);
                            notify.execute(tab_url);
                        }
                    }, 15000);

            }
        });

        CallbackManager callbackManager2 = CallbackManager.Factory.create();
        shareButton.registerCallback(callbackManager2, new FacebookCallback<Sharer.Result>()
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

            } });

    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_you_tube, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_accueil:
                Intent intent=new Intent(YouTubeListCreditActivity.this,MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_inventaires:
                Intent intent2=new Intent(YouTubeListCreditActivity.this,Inventaires.class);
                startActivity(intent2);
            /* DO INVENTAIRES */
                return true;
            case R.id.action_notifications:
                Intent intent3=new Intent(YouTubeListCreditActivity.this,Notifications.class);
                startActivity(intent3);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_envoyer_et_partager_document:
                Intent intent4=new Intent(YouTubeListCreditActivity.this,Menu_PartageActivity.class);
                startActivity(intent4);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_menu_principale:
                Intent intent5=new Intent(YouTubeListCreditActivity.this,Menu_Principale_Drawer.class);
                startActivity(intent5);
            /* DO NOTIFICATIONS */
                return true;
            /* case R.id.action_mes_videos_you_tube:
                Intent intent6=new Intent(YouTubeListCreditActivity.this,YouTubeListActivity.class);
                startActivity(intent6);*/
            /* DO NOTIFICATIONS */
            //return true;
            case R.id.action_stats:
                Intent intent6=new Intent(YouTubeListCreditActivity.this,Statistiques.class);
                startActivity(intent6);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_you_tube_playlist:
                Intent intent8=new Intent(YouTubeListCreditActivity.this,PlayerControlsDemoActivity.class);
                startActivity(intent8);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_you_tube_wall:
                Intent intent9=new Intent(YouTubeListCreditActivity.this,VideoWallDemoActivity.class);
                startActivity(intent9);
            /* DO NOTIFICATIONS */
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored)
    {
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);

        String[] splitArray;
        String str = Urls_videos[j];
        splitArray = str.split("=");

        if(!wasRestored)
        {
            player.cueVideo(splitArray[1]); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason)
    {
        if(errorReason.isUserRecoverableError())
        {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        }
        else
            {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == RECOVERY_REQUEST)
        {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener
    {
        @Override
        public void onPlaying()
        {
            // Called when playback starts, either due to user action or call to play().
            showMessage("Playing");
        }

        @Override
        public void onPaused()
        {
            // Called when playback is paused, either due to user action or call to pause().
            showMessage("Paused");
        }

        @Override
        public void onStopped()
        {
            // Called when playback stops for a reason other than being paused.
            showMessage("Stopped");
        }

        @Override
        public void onBuffering(boolean b)
        {
            // Called when buffering starts or ends.
        }

        @Override
        public void onSeekTo(int i)
        {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
        }
    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener
    {
        @Override
        public void onLoading()
        {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        @Override
        public void onLoaded(String s)
        {
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
        }

        @Override
        public void onAdStarted()
        {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted()
        {
            // Called when playback of the video starts.
        }

        @Override
        public void onVideoEnded()
        {
            // Called when the video reaches its end.
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason)
        {
            // Called when an error occurs.
        }
    }


    public class NotifyShare extends AsyncTask<String, Integer, String>
    {
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public NotifyShare(Context context) {
            this.context = context;
        }

        private void notifyurl( String url_share, String pseudo)
        {
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new FormEncodingBuilder()
                    .add(URL_SHARE, url_share)
                    .add(PSEUDO, pseudo)
                    .build();

            Request request = new Request.Builder()
                    .url(NOTIFY_URL)
                    .post(requestBody)
                    .build();

            try
            {
                Response response = client.newCall(request).execute();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
            // Mise à jour de la ProgressBar
            mProgressBar.setProgress(values[0]);
        }

        protected void onPostExecute(Void result)
        {
            Toast.makeText(getApplicationContext(), "Le traitement asynchrone est terminé", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... sUrl)
        {
            notifyurl(sUrl[0],sUrl[1]);

            int progress;
            for (progress=0;progress<=100;progress++)
            {
                for (int i=0; i<1000000; i++){}
                //la méthode publishProgress met à jour l'interface en invoquant la méthode onProgressUpdate
                publishProgress(progress);
                progress++;
            }

            return null;
        }
    }
}

