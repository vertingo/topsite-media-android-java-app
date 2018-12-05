package com.vertin_go.topsiteapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FacebookActivity extends AppCompatActivity {

    LoginButton loginButton;
    TextView textview;
    CallbackManager callbackManager;
    private ProgressBar mProgressBar;
    private Button mButton;
    private static final String REGISTER_URL = "http://platform-media.herokuapp.com/appmobileregister";
    public  String Estdeja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_facebook);

        final EditText pseudo = findViewById(R.id.pseudo);
        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);
        final EditText password2 =  findViewById(R.id.password2);

        Button connection = findViewById(R.id.connect);

        connection.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final String Pseudo = pseudo.getText().toString();
                final String Email = email.getText().toString();
                final String Pass = password.getText().toString();
                final String Pass2 = password2.getText().toString();

                // On déclare le pattern que l’on doit vérifier
                Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
                // On déclare un matcher, qui comparera le pattern avec la
                // string passée en argument
                Matcher m = p.matcher(Email);
                // Si l’adresse mail saisie ne correspond au format d’une
                // adresse mail on un affiche un message à l'utilisateur
                if (!m.matches()) {
                    // Toast est une classe fournie par le SDK Android
                    // pour afficher les messages (indications) à l'intention de
                    // l'utilisateur. Ces messages ne possédent pas d'interaction avec l'utilisateur
                    // Le premier argument représente le contexte, puis
                    // le message et à la fin la durée d'affichage du Toast (constante
                    // LENGTH_SHORT ou LENGTH_LONG). Sans oublier d'appeler la méthode
                    //show pour afficher le Toast
                    Toast.makeText(FacebookActivity.this, R.string.email_format_error,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(Pass.equals(Pass2))
                {
                    Intent intent = new Intent(FacebookActivity.this,
                            ResultActivity.class);
                    intent.putExtra("pseudo", Pseudo);
                    intent.putExtra("email", Email);
                    intent.putExtra("pass", Pass);
                    intent.putExtra("pass2", Pass2);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(FacebookActivity.this, R.string.password_non_identic,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        mProgressBar = findViewById(R.id.pBAsync);
        loginButton= findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        textview= findViewById(R.id.loginstatus);
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        textview.setText("Connexion réussie \n" + loginResult.getAccessToken().getUserId());

                        final AccessToken accessToken = loginResult.getAccessToken();
                        GraphRequest graphRequest=GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                if(response.getError()!=null)
                                {

                                }
                                else
                                {
                                        String email = object.optString("email");
                                        String id = object.optString("id");
                                        String name = object.optString("name");

                                        String[] splitArray; //tableau de chaînes
                                        //la chaîne à traiter
                                        String str = name;

                                        // On découpe la chaîne "str" à traiter et on récupère le résultat dans le tableau "splitArray"
                                        splitArray = str.split(" ");

                                        textview.setText("                Bienvenue sur le TopSite: " + splitArray[0] + "\n Vous allez recevoir un email avec vos informations\n                                   de connexion!\n                Bonne expérience avec l'Appli TopSite!");
                                        final String[] tab_url = {id, splitArray[0], splitArray[1], email, "fr_FR"};

                                        FaceBookRegister calcul = new FaceBookRegister(FacebookActivity.this);
                                        calcul.execute(tab_url);

                                }

                            }
                        });

                        Bundle bundle=new Bundle();
                        bundle.putString("fields","id,email,name");
                        graphRequest.setParameters(bundle);
                        graphRequest.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        textview.setText("Connexion annulé");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public class FaceBookRegister extends AsyncTask<String, Integer, String> {
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public FaceBookRegister(Context context) {
            this.context = context;
        }

        private void sendFacebook_InformationtoServer(String idFb,String Fname,String Lname,String Email, String Flocale) {

            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new FormEncodingBuilder()
                    .add("id", idFb)
                    .add("first_name", Fname)
                    .add("last_name", Lname)
                    .add("email", Email)
                    .add("locale", Flocale)
                    .add("facebook", "oui")
                    .build();

            Request request = new Request.Builder()
                    .url(REGISTER_URL)
                    .post(requestBody)
                    .build();

            try {
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

           // if(Estdeja.equals("non")) {
                final Toast toast = Toast.makeText(getApplicationContext(), "Création de votre compte en cours...", Toast.LENGTH_LONG);
                toast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Intent i = new Intent(FacebookActivity.this, Login.class);
                        startActivity(i);
                        toast.cancel();
                    }
                }, 5000);   //5 seconds
            /*}
            else
            {
                final Toast toast = Toast.makeText(getApplicationContext(), "Vous avez déjà un compte crée via Facebook sur notre application! Logger vous avec votre nom de profile Facebook! Si vous avez oubliez votre mot de passe rendez-vous sur le site pour effectuer une demande!", Toast.LENGTH_LONG);
                toast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Intent i = new Intent(FacebookActivity.this, Login.class);
                        startActivity(i);
                        toast.cancel();
                    }
                }, 5000);   //5 seconds
            }*/
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
            // Mise à jour de la ProgressBar
            mProgressBar.setProgress(values[0]);
        }

        protected void onPostExecute(Void result) {
            Toast.makeText(getApplicationContext(), "Le traitement asynchrone est terminé", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... sUrl) {

            sendFacebook_InformationtoServer(sUrl[0],sUrl[1],sUrl[2],sUrl[3], sUrl[4]);

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
