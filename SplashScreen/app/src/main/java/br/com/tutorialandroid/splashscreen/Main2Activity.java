package br.com.tutorialandroid.splashscreen;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    private MenuItem action_sair;
    private Button bt_ocup, bt_liv;
    private AlertDialog alerta;

    private TextView txtnome, txtid;

    //String nomeUsuario, idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        overridePendingTransition(R.anim.activity_entra, R.anim.activity_sai);

        bt_ocup = (Button) findViewById(R.id.bt_ocup);
        bt_liv = (Button) findViewById(R.id.bt_liv);
        //txtnome = (TextView) findViewById(R.id.txtnome);
        //txtid = (TextView) findViewById(R.id.txtid);

        //nomeUsuario = getIntent().getExtras().getString("nome_usuario");
        //idUsuario = getIntent().getExtras().getString("id_usuario");

        //txtnome.setText(nomeUsuario);
        //txtid.setText(idUsuario);

        bt_ocup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent irmapa = new Intent(Main2Activity.this, MainMapActivity.class);
                startActivity(irmapa);
            }
        });

        bt_liv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cria o gerador do AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
                //define o titulo
                builder.setTitle("Ficar LIVRE");
                //define a mensagem
                builder.setMessage("Você deseja realmente ficar LIVRE?");
                //define um botão como positivo
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(Main2Activity.this, "Você está livre", Toast.LENGTH_SHORT).show();//+ arg1, Toast.LENGTH_SHORT).show();
                    }
                });
                //define um botão como negativo.
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(Main2Activity.this, "Você não está livre", Toast.LENGTH_SHORT).show();// + arg1, Toast.LENGTH_SHORT).show();
                    }
                });
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();
            }
        });

    }



   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);

            /*MenuItem m1 = menu.add(0, 0, 0, "Sair");
            m1.setIcon(R.drawable.taxi);
            m1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);*/

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                return true;

            case R.id.action_sair:
            finish();
                break;

            case R.id.action_perfil:
                break;
        }

        if (id == R.id.action_sair) {
            Toast.makeText(getApplicationContext(), "Saindo...", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_perfil) {
            Toast.makeText(getApplicationContext(), "Abrindo perfil...", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish(){
        super.finish();

        overridePendingTransition(R.anim.entrando_activity, R.anim.saindo_activity);
    }
}
