package com.vertin_go.topsiteapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.vertin_go.topsiteapp.RegistrationIntentService.Pseudo;

public class ShareDialogPartageActivity extends FragmentActivity implements View.OnClickListener
{
    private static final String NOTIFY_URL = "http://vertin-go.com/TopSite/configuration/GCM/notify_url.php";
    private static final String URL_SHARE = "url_share";
    private static final String PSEUDO = "pseudo";
    private static final String PERMISSION = "publish_actions";
    private ProgressBar mProgressBar;
    public Button fullPageScreenshot;
    public LinearLayout rootContent;
    public ImageView imageView;
    public TextView hiddenText;

    private static final Location SEATTLE_LOCATION = new Location("")
    {
        {
            setLatitude(47.6097);
            setLongitude(-122.3331);
        }
    };

    private final String PENDING_ACTION_BUNDLE_KEY = "com.vertin_go.topsiteapp";

    private Button postStatusUpdateButton;
    private Button postPhotoButton;
    private ProfilePictureView profilePictureView;
    private TextView greeting;
    private PendingAction pendingAction = PendingAction.NONE;
    private boolean canPresentShareDialog;
    private boolean canPresentShareDialogWithPhotos;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private ShareDialog shareDialog;
    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>()
    {
        @Override
        public void onCancel()
        {

        }

        @Override
        public void onError(FacebookException error)
        {
            String title = getString(R.string.error);
            String alertMessage = error.getMessage();
            showResult(title, alertMessage);
        }

        @Override
        public void onSuccess(Sharer.Result result)
        {
            //notifyurl(Url, Pseudo);
            if(result.getPostId() != null) {
                String title = getString(R.string.success);
                String id = result.getPostId();
                String alertMessage = getString(R.string.successfullypostedpost);
                showResult(title, alertMessage);
            }
        }

        private void showResult(String title, String alertMessage)
        {
            new AlertDialog.Builder(ShareDialogPartageActivity.this)
                    .setTitle(title)
                    .setMessage(alertMessage)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    };

    private enum PendingAction
    {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_share_dialog_partage);
        mProgressBar = findViewById(R.id.pBAsync);

        Intent intent = getIntent();
        // On suppose que tu as mis un String dans l'Intent via le putExtra()
        final String url = intent.getStringExtra("url");
        final String inventaires = intent.getStringExtra("inventaires");

        TextView text = findViewById(R.id.credits);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());

        if(inventaires.equals("oui"))
        {
            text.setText("");
            findViews();
            implementClickEvents();
        }

        WebView webView = findViewById(R.id.webView);
        webView.setInitialScale(1);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        String data_html = "<!DOCTYPE html><html> <head> <meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"target-densitydpi=high-dpi\" /> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"> <link rel=\"stylesheet\" media=\"screen and (-webkit-device-pixel-ratio:1.5)\" href=\"hdpi.css\" /></head> <body style=\"background:black;margin:0 0 0 0; padding:0 0 0 0;\"> <iframe style=\"background:black;\" width=' "+width+"' height='"+height+"' src=\""+url+"\" frameborder=\"0\"></iframe> </body> </html> ";

        webView.loadDataWithBaseURL(url, data_html, "text/html", "UTF-8", null);

        ShareButton shareButton = findViewById(R.id.share_btn);
        shareButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getApplicationContext(),
                        "Share Dialog Opened!",
                        Toast.LENGTH_SHORT).show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable()
                {
                    public void run()
                    {
                        final String[] tab_url = {url,Pseudo};
                        NotifyShare notify=new NotifyShare(ShareDialogPartageActivity.this);
                        notify.execute(tab_url);
                    }
                }, 15000);   //5 seconds

            }
        });

        CallbackManager callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(url))
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

        if(savedInstanceState != null)
        {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            pendingAction = PendingAction.valueOf(name);
        }

        profileTracker = new ProfileTracker()
        {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile)
            {
                updateUI();
                // It's possible that we were waiting for Profile to be populated in order to
                // post a status update.
                handlePendingAction();
            }
        };

        profilePictureView = findViewById(R.id.profilePicture);
        greeting = findViewById(R.id.greeting);

        postStatusUpdateButton = findViewById(R.id.vertingowebsite);
        postStatusUpdateButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                onClickPostStatusUpdate();
            }
        });

        postPhotoButton = findViewById(R.id.visiter);
        postPhotoButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
                startActivity(intent);
            }
        });

        // Can we present the share dialog for regular links?
        canPresentShareDialog = ShareDialog.canShow(ShareLinkContent.class);

        // Can we present the share dialog for photos?
        canPresentShareDialogWithPhotos = ShareDialog.canShow(SharePhotoContent.class);
    }

    /*  Find all views Ids  */
    public void findViews()
    {
        fullPageScreenshot = findViewById(R.id.full_page_screenshot);
        rootContent = findViewById(R.id.root_content);
        imageView = findViewById(R.id.image_view);
        hiddenText = findViewById(R.id.hidden_text);
    }

    /*  Implement Click events over Buttons */
    public void implementClickEvents()
    {
        fullPageScreenshot.setOnClickListener(this);
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


    @Override
    protected void onResume()
    {
        super.onResume();

        // Call the 'activateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onResume methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.activateApp(this);

        updateUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        profileTracker.stopTracking();
    }

    private void updateUI()
    {
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;

        postStatusUpdateButton.setEnabled(enableButtons || canPresentShareDialog);
        postPhotoButton.setEnabled(enableButtons || canPresentShareDialogWithPhotos);

        Profile profile = Profile.getCurrentProfile();
        if (enableButtons && profile != null)
        {
            profilePictureView.setProfileId(profile.getId());
           // greeting.setText(getString(R.string.hello_user));
        }
        else
            {
            profilePictureView.setProfileId(null);
            greeting.setText(null);
        }
    }

    private void handlePendingAction()
    {
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction)
        {
            case NONE:
                break;
            case POST_PHOTO:
                postPhoto();
                break;
            case POST_STATUS_UPDATE:
                postStatusUpdate();
                break;
        }
    }

    private void onClickPostStatusUpdate()
    {
        performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
    }

    private void postStatusUpdate()
    {
        Profile profile = Profile.getCurrentProfile();
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://www.facebook.com/vertingo/"))
                .build();
        if (canPresentShareDialog)
        {
            shareDialog.show(linkContent);
        }
        else if(profile != null && hasPublishPermission())
        {
            ShareApi.share(linkContent, shareCallback);
        }
        else
            {
            pendingAction = PendingAction.POST_STATUS_UPDATE;
        }
    }

    private void onClickPostPhoto()
    {
        performPublish(PendingAction.POST_PHOTO, canPresentShareDialogWithPhotos);
    }

    private void postPhoto()
    {
        Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.drawable.pub_cible);
        SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(image).build();
        ArrayList<SharePhoto> photos = new ArrayList<>();
        photos.add(sharePhoto);

        SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder().setPhotos(photos).build();

        if (canPresentShareDialogWithPhotos)
        {
            shareDialog.show(sharePhotoContent);
        }
        else if (hasPublishPermission())
        {
            ShareApi.share(sharePhotoContent, shareCallback);
        }
        else
            {
            pendingAction = PendingAction.POST_PHOTO;
            // We need to get new permissions, then complete the action when we get called back.
            LoginManager.getInstance().logInWithPublishPermissions(
                    this,
                    Arrays.asList(PERMISSION));
        }
    }

    private boolean hasPublishPermission()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }

    private void performPublish(PendingAction action, boolean allowNoToken)
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null || allowNoToken)
        {
            pendingAction = action;
            handlePendingAction();
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
        protected void onPreExecute()
        {
            super.onPreExecute();
          //  Toast.makeText(getApplicationContext(), "Notification en cours d'acheminement...!", Toast.LENGTH_LONG).show();
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
            //sendFacebook_InformationtoServer(sUrl[0],sUrl[1],sUrl[2],sUrl[3], sUrl[4]);
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