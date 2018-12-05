package com.vertin_go.topsiteapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity
{
    final String EXTRA_EMAIL = "user_email";
    final String EXTRA_PASSWORD = "user_password";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final EditText login = findViewById(R.id.email);
        final EditText pass = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.connect);
        final Button createAccount = findViewById(R.id.createAcount);

        createAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Login.this,FacebookActivity.class);
                startActivity(intent);
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String loginTxt = login.getText().toString();
                final String passTxt = pass.getText().toString();

                //Si un des deux champs est vide, alors on affiche l'erreurs

                if (loginTxt.equals("") || passTxt.equals(""))
                {
                    Toast.makeText(Login.this,
                            R.string.email_or_password_empty,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // On déclare le pattern que l’on doit vérifier
                Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
                // On déclare un matcher, qui comparera le pattern avec la
                // string passée en argument
                Matcher m = p.matcher(loginTxt);
                // Si l’adresse mail saisie ne correspond au format d’une
                // adresse mail on un affiche un message à l'utilisateur
                if(!m.matches())
                {
                    // Toast est une classe fournie par le SDK Android
                    // pour afficher les messages (indications) à l'intention de
                    // l'utilisateur. Ces messages ne possédent pas d'interaction avec l'utilisateur
                    // Le premier argument représente le contexte, puis
                    // le message et à la fin la durée d'affichage du Toast (constante
                    // LENGTH_SHORT ou LENGTH_LONG). Sans oublier d'appeler la méthode
                    //show pour afficher le Toast
                    Toast.makeText(Login.this, R.string.email_format_error,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(Login.this, MainActivity.class);
                intent.putExtra(EXTRA_EMAIL, loginTxt);
                intent.putExtra(EXTRA_PASSWORD, passTxt);
                startActivity(intent);
            }
        });
    }
}