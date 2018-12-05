package com.vertin_go.topsiteapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.SendButton;
import com.facebook.share.widget.ShareButton;

import java.util.List;
import java.util.Vector;

import static com.vertin_go.topsiteapp.RegistrationIntentService.Urls_users_videos;
import static com.vertin_go.topsiteapp.YouTubeListActivity.e;

public class YouTubeListActivity extends AppCompatActivity
{
    //RECYCLER VIEW FIELD
    RecyclerView recyclerView;
    //VECTOR FOR VIDEO URLS
    Vector<YoutubeVideo> youtubeVideos = new Vector<YoutubeVideo>();
    static int e=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.you_tube_list_activity);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));

        int i=0;
        while(i<Urls_users_videos.length)
        {
            //Load video List
            String[] splitArray;
            String str = Urls_users_videos[i];
            splitArray = str.split("=");
            youtubeVideos.add(new YoutubeVideo("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/"+splitArray[1]+"\" frameborder=\"0\" allowfullscreen></iframe>"));
            i=i+1;
        }
        VideoAdapter videoAdapter = new VideoAdapter(youtubeVideos);
        recyclerView.setAdapter(videoAdapter);
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
                Intent intent=new Intent(YouTubeListActivity.this,MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_inventaires:
                Intent intent2=new Intent(YouTubeListActivity.this,Inventaires.class);
                startActivity(intent2);
            /* DO INVENTAIRES */
                return true;
            case R.id.action_notifications:
                Intent intent3=new Intent(YouTubeListActivity.this,Notifications.class);
                startActivity(intent3);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_envoyer_et_partager_document:
                Intent intent4=new Intent(YouTubeListActivity.this,Menu_PartageActivity.class);
                startActivity(intent4);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_menu_principale:
                Intent intent5=new Intent(YouTubeListActivity.this,Menu_Principale_Drawer.class);
                startActivity(intent5);
            /* DO NOTIFICATIONS */
                return true;
           /* case R.id.action_envoyer_et_partager_youtube:
                Intent intent7=new Intent(YouTubeListActivity.this,YouTubeListCreditActivity.class);
                startActivity(intent7);*/
            /* DO NOTIFICATIONS */
             //   return true;
            case R.id.action_stats:
                Intent intent6=new Intent(YouTubeListActivity.this,Statistiques.class);
                startActivity(intent6);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_you_tube_playlist:
                Intent intent8=new Intent(YouTubeListActivity.this,PlayerControlsDemoActivity.class);
                startActivity(intent8);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_you_tube_wall:
                Intent intent9=new Intent(YouTubeListActivity.this,VideoWallDemoActivity.class);
                startActivity(intent9);
            /* DO NOTIFICATIONS */
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

class YoutubeVideo
{

    String videoUrl;

    public YoutubeVideo()
    {

    }

    public YoutubeVideo(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}

class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder>
{

    List<YoutubeVideo> youtubeVideoList;

    public VideoAdapter()
    {

    }

    public VideoAdapter(List<YoutubeVideo> youtubeVideoList)
    {
        this.youtubeVideoList = youtubeVideoList;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from( parent.getContext()).inflate(R.layout.card_video, parent, false);

        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position)
    {
        holder.videoWeb.loadData( youtubeVideoList.get(position).getVideoUrl(), "text/html" , "utf-8" );
    }

    @Override
    public int getItemCount()
    {
        return youtubeVideoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder
    {
        WebView videoWeb;

        public VideoViewHolder(View itemView)
        {
            super(itemView);

            videoWeb = itemView.findViewById(R.id.webVideoView);

            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(Urls_users_videos[e]))
                    .build();

            SendButton sendButton = itemView.findViewById(R.id.send_btn);
            sendButton.setShareContent(linkContent);
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

            ShareLinkContent linkContent2 = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(Urls_users_videos[e]))
                    .build();

            ShareButton shareButton = itemView.findViewById(R.id.share_btn);
            shareButton.setShareContent(linkContent2);
            CallbackManager callbackManager2 = CallbackManager.Factory.create();
            sendButton.registerCallback(callbackManager2, new FacebookCallback<Sharer.Result>()
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

            e=e+1;

            videoWeb.getSettings().setJavaScriptEnabled(true);
            videoWeb.setWebChromeClient(new WebChromeClient()
            {

            } );
        }

    }

}
