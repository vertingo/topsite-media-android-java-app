package com.vertin_go.topsiteapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ResultActivity extends AppCompatActivity
{
    private static final String REGISTER2_URL = "http://platform-media.herokuapp.com/appmobileregister";
    private ProgressBar mProgressBar;
    public TextView textView ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.result_activity);

        mProgressBar = findViewById(R.id.pBAsync);
        textView = findViewById(R.id.textView1);

        Intent intent = getIntent();
        // On suppose que tu as mis un String dans l'Intent via le putExtra()
        String pseudo = intent.getStringExtra("pseudo");
        String email = intent.getStringExtra("email");
        String pass = intent.getStringExtra("pass");
        String pass2 = intent.getStringExtra("pass2");

        final String[] tab_url = {pseudo,email,pass,pass2};
        Registration registration=new Registration(this);
        registration.execute(tab_url);

        //On supprime la notification de la liste de notification comme dans la méthode cancelNotify de l'Activity principale
        // NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        // notificationManager.cancel(Tutoriel16_Android.ID_NOTIFICATION);
    }


    public class Registration extends AsyncTask<String, Integer, String>
    {
        private Context context;
        private PowerManager.WakeLock mWakeLock;
        public  String Estdeja;

        public Registration(Context context) {
            this.context = context;
        }

        private void send(String pseudo,String email,String pass,String pass2)
        {

            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new FormEncodingBuilder()
                    .add("pseudo",pseudo)
                    .add("email", email)
                    .add("pass", pass)
                    .add("pass2", pass2)
                    .add("facebook", "non")
                    .build();

            Request request = new Request.Builder()
                    .url(REGISTER2_URL)
                    .post(requestBody)
                    .build();

            try
            {
                Response response = client.newCall(request).execute();

                JSONObject json  = new JSONObject(response.body().string());
                String estdeja = (String) json.get("estdeja");

                Estdeja=estdeja;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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

            if(Estdeja.equals("non"))
            {
                final Toast toast = Toast.makeText(getApplicationContext(), "Création de votre compte en cours vous allez recevoir un email avec vos identiants de connexion et lien d'activation de votre compte!", Toast.LENGTH_LONG);
                toast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Intent i = new Intent(ResultActivity.this, Login.class);
                        startActivity(i);
                        toast.cancel();
                    }
                }, 4000);   //5 seconds
            }
            else
            {
                final Toast toast = Toast.makeText(getApplicationContext(), "Veuillez-changer de pseudo celui-ci est déjà pris!", Toast.LENGTH_LONG);
                toast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Intent i = new Intent(ResultActivity.this, FacebookActivity.class);
                        startActivity(i);
                        toast.cancel();
                    }
                }, 3000);   //5 seconds

            }

        }

        protected void onPostExecute(Void result) {
            Toast.makeText(getApplicationContext(), "Le traitement asynchrone est terminé", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... sUrl)
        {
            send(sUrl[0],sUrl[1],sUrl[2],sUrl[3]);

            int progress;
            for(progress=0;progress<=100;progress++)
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
