package br.com.tutorialandroid.splashscreen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import br.com.tutorialandroid.splashscreen.executaAppEmSegundoPlano.service;

public class MainAjuda extends AppCompatActivity {

    private TextView ajuda_email, ajuda_cell;
    private EditText ajuda_numero;
    private Button botao_ligar;
    AlertDialog alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ajuda);
        startService(new Intent(this, service.class));

        ajuda_cell = (TextView) findViewById(R.id.ajuda_cell);
        ajuda_email = (TextView) findViewById(R.id.ajuda_email);
        ajuda_numero = (EditText) findViewById(R.id.ajuda_numero);
        botao_ligar = (Button) findViewById(R.id.botao_ligar);

        final String celular = " +55 88 ";

        //TextView info = new TextView(this);

        botao_ligar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Cria o gerador do AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainAjuda.this);
                //define o titulo
                //builder.setIcon(R.drawable.logomarcalets).setTitle("CANCELAR!");
                //define a mensagem
                builder.setCancelable(false);
                builder.setMessage("Ligue para a nossa central");
                //define um botão como positivo
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        String numero = "+55 88 " + ajuda_numero.getText().toString();

                        Uri uri = Uri.parse("tel: " + numero);
                        Intent intencao = new Intent(Intent.ACTION_CALL, uri);
                        startActivity(intencao);

                    }
                });
                //define um botão como negativo.
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(MainCadActivity.this, "Você não está livre", Toast.LENGTH_SHORT).show();// + arg1, Toast.LENGTH_SHORT).show();
                    }
                });
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();
            }
        });

        //================================ENVIAR EMAIL=====================================
        ajuda_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = null;
                Intent chooser = null;

                intent = new Intent(Intent.ACTION_SEND);    //Chama a açao de envio
                intent.setData(Uri.parse("LetsGO:"));
                String[] to = {"renatocostary@gmail.com", "renatocosta12345@hotmail.com"}; //Emails fixos
                intent.putExtra(Intent.EXTRA_EMAIL, to);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Lets GO pede que você digite o assunto!");
                intent.putExtra(Intent.EXTRA_TEXT, "Taxista digite sua mensagem aqui!");
                intent.setType("message/rfc822");
                chooser = Intent.createChooser(intent, "Enviar Email");
                startActivity(chooser);

            }
        });
/*
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        info.append(tm.getSimOperatorName() + "\n");

        switch (tm.getPhoneType()) {
            case TelephonyManager.PHONE_TYPE_GSM:
                info.append("GSM \n");
                break;
            case TelephonyManager.PHONE_TYPE_CDMA:
                info.append("CDMA \n");
                break;
        }*/
    }

}
