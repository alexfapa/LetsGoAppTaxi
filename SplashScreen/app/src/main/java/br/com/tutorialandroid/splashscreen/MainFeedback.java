package br.com.tutorialandroid.splashscreen;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import br.com.tutorialandroid.splashscreen.executaAppEmSegundoPlano.service;

public class MainFeedback extends AppCompatActivity {

    ImageButton img_voltar;
    private Button btn_envfeed;
    private EditText et_feednome, et_feedemail, txt_feedb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_feedback);
        startService(new Intent(getBaseContext(), service.class));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //img_voltar = (ImageButton) findViewById(R.id.img_voltar);
        btn_envfeed = (Button) findViewById(R.id.btn_envfeed);
        et_feedemail = (EditText) findViewById(R.id.et_feedemail);
        et_feednome = (EditText) findViewById(R.id.et_feednome);
        txt_feedb = (EditText) findViewById(R.id.txt_feedb);

        //======================BOTÃO VOLTAR < ===========================
        /*img_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainFeedback.this, MainMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });*/

        btn_envfeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nome = et_feednome.getText().toString();
                String email = et_feedemail.getText().toString();
                String texto_feedback = txt_feedb.getText().toString();

                Intent intent = null;
                Intent chooser = null;

                intent = new Intent(Intent.ACTION_SEND);    //Chama a açao de envio
                intent.setData(Uri.parse("LetsGO:"));
                String[] to = {email, "letsgosobral@gmail.com"}; //Emails fixos
                intent.putExtra(Intent.EXTRA_EMAIL, to);
                intent.putExtra(Intent.EXTRA_SUBJECT, "FeedBack de " + nome + " - LETS GO");
                intent.putExtra(Intent.EXTRA_TEXT, texto_feedback);
                intent.setType("message/rfc822");
                chooser = Intent.createChooser(intent, "Enviar Email");
                startActivity(chooser);

            }
        });

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    /* Função para verificar existência de conexão com a internet

    public  boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
            Log.i("net", "-> Conectado");
            Toast.makeText(MainFeedback.this, "Conectado", Toast.LENGTH_SHORT).show();
        } else {
            conectado = false;
            Log.i("net", "-> Desconectado");
            Toast.makeText(MainFeedback.this, "Desconectado", Toast.LENGTH_SHORT).show();
        }
        return conectado;
    }

*/
}
