package com.vertin_go.topsiteapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;

import static com.vertin_go.topsiteapp.RegistrationIntentService.Pseudo;

public class MainActivity extends AppCompatActivity
{
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private boolean table_flg = false;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TableLayout table;
    private TextView mInformationTextView;
    private TextView mInformationTextView2;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    public View preView;
    private String mText="Login";
    private String mUrl="http://vertin-go.com/TopSite/";
    private String mDescription="TopSite Application";
    public Action TYPE_VIEW = new Action() {
        @Override
        public int hashCode() {
            return super.hashCode();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRegistrationProgressBar = findViewById(R.id.registrationProgressBar);
        mInformationTextView2 = findViewById(R.id.informationTextView2);
        table = findViewById(R.id.table);

        /*Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,
                        Menu_Principale_Drawer.class);
                startActivity(intent);
            }
        });*/

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

        String data_html = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/J2A_aMdinVY\" frameborder=\"0\" allowfullscreen></iframe>";
        webView.loadDataWithBaseURL("https://www.youtube.com/watch?v=J2A_aMdinVY", data_html, "text/html", "UTF-8", null);

        FacebookSdk.sdkInitialize(getApplicationContext());

        LikeView likeView = findViewById(R.id.like_view);
        likeView.setObjectIdAndType("https://www.facebook.com/vertingo/?ref=bookmarks", LikeView.ObjectType.PAGE);

        preView = getWindow().getDecorView();
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        mRegistrationBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(final Context context, Intent intent)
            {
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);

                if(sentToken)
                {
                    if(RegistrationIntentService.Estdeja.equals("oui"))
                    {
                        mInformationTextView.setText("Réponse du serveur: " + RegistrationIntentService.Reponse + "\n [Pseudo: " + Pseudo + "]");
                        mInformationTextView2.setText(R.string.notif_message);
                    }
                    else
                        {

                        if(RegistrationIntentService.Success.equals("oui"))
                        {
                            mInformationTextView.setText(getString(R.string.gcm_send_message) + "\nRéponse du serveur: " + RegistrationIntentService.Reponse + "\n [Pseudo: " + Pseudo + "]");
                            mInformationTextView2.setText(R.string.notif_message);
                            sharedPreferences.edit().putBoolean(QuickstartPreferences.REGISTERED_TOKEN, true).apply();
                        }
                        else
                            {
                            mInformationTextView.setText("Réponse du serveur: " + RegistrationIntentService.Reponse);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable()
                            {
                                public void run()
                                {
                                    Intent i = new Intent(context, Login.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(i);
                                }
                            }, 5000);   //5 seconds

                        }
                    }

                    // setColumnCollapsed(int columnIndex, boolean isCollapsed)
                    table.setColumnCollapsed(0, table_flg);
                    table.setColumnCollapsed(1, table_flg);

                    TableRow tableRow = new TableRow(getApplicationContext());

                    // Set new table row layout parameters.
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    tableRow.setLayoutParams(layoutParams);

                    // Add a TextView in the first column.
                    TextView textView = new TextView(getApplicationContext());
                    textView.setText(Pseudo);
                    textView.setTextColor(getResources().getColor(R.color.com_facebook_blue));
                    textView.setPadding(70,0,0,0);
                    tableRow.addView(textView, 0);

                    // Add a TextView in the first column.
                    TextView textView3 = new TextView(getApplicationContext());
                    textView3.setText(RegistrationIntentService.Reponse);
                    textView3.setTextColor(getResources().getColor(R.color.com_facebook_blue));
                    textView3.setPadding(180,0,0,0);
                    tableRow.addView(textView3, 1);

                    table.addView(tableRow);
                }
                else
                    {
                    if (RegistrationIntentService.Estdeja.equals("oui"))
                    {
                        mInformationTextView.setText("Réponse du serveur: " + RegistrationIntentService.Reponse + "\n [Pseudo: " + Pseudo + "]");
                        mInformationTextView2.setText(R.string.notif_message);
                    }
                    else
                        {
                        if(RegistrationIntentService.Success.equals("oui"))
                        {
                            mInformationTextView.setText(getString(R.string.gcm_send_message) + "\nRéponse du serveur: " + RegistrationIntentService.Reponse + "\n [Pseudo: " + Pseudo + "]");
                            mInformationTextView2.setText(R.string.notif_message);
                            sharedPreferences.edit().putBoolean(QuickstartPreferences.REGISTERED_TOKEN, true).apply();
                        }
                        else
                            {
                            mInformationTextView.setText("Réponse du serveur: " + RegistrationIntentService.Reponse);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable()
                            {
                                public void run()
                                {
                                    Intent i = new Intent(context, Login.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(i);
                                }
                            }, 5000);   //5 seconds
                        }
                    }

                }
            }

        };
        mInformationTextView = findViewById(R.id.informationTextView);

        if (checkPlayServices())
        {
            // Start IntentService to register this application with GCM.
            Intent intent = getIntent();
            // On suppose que tu as mis un String dans l'Intent via le putExtra()
            String login = intent.getStringExtra("user_email");
            String password = intent.getStringExtra("user_password");
            Intent intent2 = new Intent(this, RegistrationIntentService.class);
            intent2.putExtra("user_email", login);
            intent2.putExtra("user_password", password);
            startService(intent2);
        }

        onNewIntent(getIntent());
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }

    // After
    public Action getAction() {
        return Actions.newView(mText, mUrl);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        /* If you’re logging an action on an item that has already been added to the index,
        you don’t have to add the following update line. See
        https://firebase.google.com/docs/app-indexing/android/personal-content#update-the-index for
        adding content to the index */
        //FirebaseAppIndex.getInstance().update(getIndexable());
        FirebaseUserActions.getInstance().start(getAction());
    }

    @Override
    protected void onStop()
    {
        FirebaseUserActions.getInstance().end(getAction());
        super.onStop();
    }

    protected void onNewIntent(Intent intent)
    {
        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null)
        {
            String recipeId = data.substring(data.lastIndexOf("/") + 1);
            Uri contentUri = Uri.parse("content://com.vertin_go.topsiteapp/TopSite/").buildUpon()
                    .appendPath(recipeId).build();
            //showRecipe(contentUri);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_stats:
                Intent intent=new Intent(MainActivity.this,Statistiques.class);
                startActivity(intent);
                return true;
            case R.id.action_inventaires:
                Intent intent2=new Intent(MainActivity.this,Menu_Inventaires.class);
                startActivity(intent2);
                /* DO INVENTAIRES */
                return true;
            case R.id.action_notifications:
                Intent intent3=new Intent(MainActivity.this,Notifications.class);
                startActivity(intent3);
                /* DO NOTIFICATIONS */
                return true;
            case R.id.action_envoyer_et_partager_document:
                Intent intent4=new Intent(MainActivity.this,Menu_PartageActivity.class);
                startActivity(intent4);
                /* DO NOTIFICATIONS */
                return true;
            case R.id.action_menu_principale:
                Intent intent5=new Intent(MainActivity.this,Menu_Principale_Drawer.class);
                startActivity(intent5);
                /* DO NOTIFICATIONS */
            /* case R.id.action_mes_videos_you_tube:
                Intent intent6=new Intent(MainActivity.this,YouTubeListActivity.class);
                startActivity(intent6);*/
                /* DO NOTIFICATIONS */
                //return true;
            case R.id.action_envoyer_et_partager_you_tube:
                Intent intent7=new Intent(MainActivity.this,YouTubeListActivity.class);
                startActivity(intent7);
                /* DO NOTIFICATIONS */
                return true;
            case R.id.action_you_tube_playlist:
                Intent intent8=new Intent(MainActivity.this,PlayerControlsDemoActivity.class);
                startActivity(intent8);
                /* DO NOTIFICATIONS */
                return true;
            case R.id.action_you_tube_wall:
                Intent intent9=new Intent(MainActivity.this,VideoWallDemoActivity.class);
                startActivity(intent9);
                /* DO NOTIFICATIONS */
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Vérifier si notre utilisateur a l'application Google Play Service
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}