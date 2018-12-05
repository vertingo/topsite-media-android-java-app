package com.vertin_go.topsiteapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by VertinGo on 23/08/2017.
 */

public class RegistrationIntentService extends IntentService
{
    private static final String TAG = "RegIntentService";
    private static final String REGISTER_URL = "https://platform-media.herokuapp.com/register_app_mobile";
    private static final String KEY_TOKEN = "gcm_token";
    private static final String KEY_EMAIL = "gcm_email";
    private static final String KEY_PASSWORD = "gcm_password";
    public static String Urls[];
    public static String Urls_ext[];
    public static String Urls_noms[];
    public static String Urls_deja_visite[];
    public static String Urls2_deja_visite[];
    public static String Urls_users[];
    public static String Urls_users_ext[];
    public static String Urls_users_noms[];
    public static String Urls_users_videos[];
    public static String Urls_videos[];
    public static String Pseudo="";
    public static String Estdeja="";
    public static String Success="";
    public static String Reponse="";
    public static String Credits="";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try
        {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG)
            {
                // [START register_for_gcm]
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                Log.i(TAG, "GCM Registration Token: " + token);

                // Si le token a déjà été engistre pas la peine de le renvoyer
                if(!sharedPreferences.getBoolean(QuickstartPreferences.REGISTERED_TOKEN, false))
                {
                    String email = intent.getStringExtra("user_email");
                    String password = intent.getStringExtra("user_password");
                    sendRegistrationToServer(token,email,password);
                    sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();

                    // You should store a boolean that indicates whether the generated token has been
                    // sent to your server. If the boolean is false, send the token to your server,
                    // otherwise your server should have already received the token.

                    if(Estdeja.equals("oui"))
                    {
                        // Notify UI that registration has completed, so the progress indicator can be hidden.
                        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
                    }
                    else
                    {
                        if(Success.equals("oui"))
                        {
                            // Notify UI that registration has completed, so the progress indicator can be hidden.
                            Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
                            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

                            sharedPreferences.edit().putBoolean(QuickstartPreferences.REGISTERED_TOKEN, true).apply();
                        }
                        else
                        {
                            // Notify UI that registration has completed, so the progress indicator can be hidden.
                            Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
                            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
                        }
                    }

                }
                else
                {
                    String email = intent.getStringExtra("user_email");
                    String password = intent.getStringExtra("user_password");
                    ConnexionToServer(token,email,password);

                    // Notify UI that registration has completed, so the progress indicator can be hidden.
                    Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
                }

            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }

    }

    public static String[] getStringArray(JSONArray jsonArray)
    {
        String[] stringArray = null;
        if (jsonArray != null)
        {
            int length = jsonArray.length();
            stringArray = new String[length];
            for (int i = 0; i < length; i++)
            {
                stringArray[i] = jsonArray.optString(i);
            }
        }
        return stringArray;
    }

    /**
     *  Ici nous allons envoyer le token de l'utilisateur au serveur
     *
     * @param token Le token
     */

    private void sendRegistrationToServer(String token, String email, String password)
    {

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormEncodingBuilder()
                .add(KEY_TOKEN, token)
                .add(KEY_EMAIL, email)
                .add(KEY_PASSWORD, password)
                .build();

        Request request = new Request.Builder()
                .url(REGISTER_URL)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();

            JSONObject json  = new JSONObject(response.body().string());
            String message = (String) json.get("message");
            String success = (String) json.get("success");
            String pseudo = (String) json.get("pseudo");
            String estdeja = (String) json.get("estdeja");
            JSONArray jsonarray= (JSONArray) json.getJSONArray("urls_users_ext");
            JSONArray jsonarray2= (JSONArray) json.getJSONArray("urls_users");
            JSONArray jsonarray3= (JSONArray) json.getJSONArray("urls_users_noms");
            JSONArray jsonarray4= (JSONArray) json.getJSONArray("urls_users_videos");
            /*String credits = (String) json.get("credits");
            JSONArray jsonarray5= (JSONArray) json.getJSONArray("urls_ext");
            JSONArray jsonarray6= (JSONArray) json.getJSONArray("urls");
            JSONArray jsonarray7= (JSONArray) json.getJSONArray("urls_noms");
            JSONArray jsonarray8= (JSONArray) json.getJSONArray("urls_deja_visite");
            JSONArray jsonarray9= (JSONArray) json.getJSONArray("urls_videos");
            JSONArray jsonarray10= (JSONArray) json.getJSONArray("urls_videos_deja_visite");*/

            Reponse=message;
            Success=success;
            Pseudo=pseudo;
            Estdeja=estdeja;
            String[] urls_users_ext = getStringArray(jsonarray);
            String[] urls_users = getStringArray(jsonarray2);
            String[] urls_users_noms = getStringArray(jsonarray3);
            String[] urls_users_videos = getStringArray(jsonarray4);
            /*Credits=credits;
            String[] urls_ext = getStringArray(jsonarray5);
            String[] urls = getStringArray(jsonarray6);
            String[] urls_users_noms = getStringArray(jsonarray7);
            String[] urls_noms = getStringArray(jsonarray8);
            String[] urls_videos = getStringArray(jsonarray9);
            String[] urls_deja_visite= getStringArray(jsonarray10);
            String[] urls2_deja_visite= getStringArray(jsonarray11);*/

            Urls_users_ext = urls_users_ext;
            Urls_users = urls_users;
            Urls_users_noms = urls_users_noms;
            Urls_users_videos=urls_users_videos;
          /*Urls_ext = urls_ext;
            Urls = urls;
            Urls_noms=urls_noms;
            Urls_videos=urls_videos;
            Urls_deja_visite=urls_deja_visite;
            Urls2_deja_visite=urls2_deja_visite;*/

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void ConnexionToServer(String token, String email, String password) {

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormEncodingBuilder()
                .add(KEY_TOKEN, token)
                .add(KEY_EMAIL, email)
                .add(KEY_PASSWORD, password)
                .build();

        Request request = new Request.Builder()
                .url(REGISTER_URL)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();

            JSONObject json  = new JSONObject(response.body().string());
            String message = (String) json.get("message");
            String success = (String) json.get("success");
            String pseudo = (String) json.get("pseudo");
            String estdeja = (String) json.get("estdeja");
            JSONArray jsonarray= (JSONArray) json.getJSONArray("urls_users_ext");
            JSONArray jsonarray2= (JSONArray) json.getJSONArray("urls_users");
            JSONArray jsonarray3= (JSONArray) json.getJSONArray("urls_users_noms");
            JSONArray jsonarray4= (JSONArray) json.getJSONArray("urls_users_videos");
            /*String credits = (String) json.get("credits");
            JSONArray jsonarray5= (JSONArray) json.getJSONArray("urls_ext");
            JSONArray jsonarray6= (JSONArray) json.getJSONArray("urls");
            JSONArray jsonarray7= (JSONArray) json.getJSONArray("urls_noms");
            JSONArray jsonarray8= (JSONArray) json.getJSONArray("urls_deja_visite");
            JSONArray jsonarray9= (JSONArray) json.getJSONArray("urls_videos");
            JSONArray jsonarray10= (JSONArray) json.getJSONArray("urls_videos_deja_visite");*/

            Reponse=message;
            Success=success;
            Pseudo=pseudo;
            Estdeja=estdeja;
            String[] urls_users_ext = getStringArray(jsonarray);
            String[] urls_users = getStringArray(jsonarray2);
            String[] urls_users_noms = getStringArray(jsonarray3);
            String[] urls_users_videos = getStringArray(jsonarray4);
            /*Credits=credits;
            String[] urls_ext = getStringArray(jsonarray5);
            String[] urls = getStringArray(jsonarray6);
            String[] urls_users_noms = getStringArray(jsonarray7);
            String[] urls_noms = getStringArray(jsonarray8);
            String[] urls_videos = getStringArray(jsonarray9);
            String[] urls_deja_visite= getStringArray(jsonarray10);
            String[] urls2_deja_visite= getStringArray(jsonarray11);*/

            Urls_users_ext = urls_users_ext;
            Urls_users = urls_users;
            Urls_users_noms = urls_users_noms;
            Urls_users_videos=urls_users_videos;
          /*Urls_ext = urls_ext;
            Urls = urls;
            Urls_noms=urls_noms;
            Urls_videos=urls_videos;
            Urls_deja_visite=urls_deja_visite;
            Urls2_deja_visite=urls2_deja_visite;*/

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}