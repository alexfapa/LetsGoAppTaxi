package br.com.tutorialandroid.splashscreen.telaDeCorrida;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonElement;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.tutorialandroid.splashscreen.MainActivity;
import br.com.tutorialandroid.splashscreen.MainMapActivity;
import br.com.tutorialandroid.splashscreen.MainNotification;
import br.com.tutorialandroid.splashscreen.MainPerfil;
import br.com.tutorialandroid.splashscreen.R;
import br.com.tutorialandroid.splashscreen.app.Config;
import br.com.tutorialandroid.splashscreen.conexao.AppConfig;
import br.com.tutorialandroid.splashscreen.executaAppEmSegundoPlano.service;
import br.com.tutorialandroid.splashscreen.loginActivity.Constants;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainPassageiroAguarda extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, LocationListener {

    //==================GOOGLE MAPS======================
    private GoogleMap mMap;
    private LocationManager locationmanager;
    private Marker marcador;
    double lat = 0.0;
    double lng = 0.0;
    private boolean allowNetwork;
    //==================GOOGLE MAPS======================

    //========ARRAYLIST RESPONSÁVEL PELA DISTÂNCIA=======
    private List<Location> locs = new ArrayList<Location>();
    private android.location.Address endereco;
    //===================================================

    private Button iniciar_corrida, btn_cancelar;
    private TextView mensagem_cliente, taxi_id, mensagem_rapida,
            tv_lat, tv_long, localizacao_destino,
            nome_cliente, email_cliente, tv_img_usu, forma_pagamento_cli,
            destino_cliente, txt_title_message, id_usu, chamadacancel;

    private ImageView opcoes_taxista, img_usu;

    private AlertDialog alerta;
    private ProgressDialog pd = null;
    private ProgressDialog progress = null;

    private static final String SERVER_ADDRESS = "http://www.letsgosobral.com/";

    boolean stopChamada = true;
    final String status = "1";

    private ExtrairTask et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, service.class));
        setContentView(R.layout.activity_main_passageiro_aguarda);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Toolbar toolbardois = (Toolbar) findViewById(R.id.toolbardois);
        setSupportActionBar(toolbardois);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa_cliente);
        mapFragment.getMapAsync(this);

        iniciar_corrida = (Button) findViewById(R.id.btn_iniciarcorridas);
        btn_cancelar = (Button) findViewById(R.id.btn_cancelar);
        mensagem_cliente = (TextView) findViewById(R.id.mensagem_cliente);
        nome_cliente = (TextView) findViewById(R.id.nome_cliente);
        email_cliente = (TextView) findViewById(R.id.email_cliente);
        localizacao_destino = (TextView) findViewById(R.id.localizacao_destino);
        opcoes_taxista = (ImageView) findViewById(R.id.opcoes_taxista);
        taxi_id = (TextView) findViewById(R.id.taxi_id);
        tv_lat = (TextView) findViewById(R.id.tv_coord_lat);
        tv_long = (TextView) findViewById(R.id.tv_coord_long);
        img_usu = (ImageView) findViewById(R.id.img_usu);
        tv_img_usu = (TextView) findViewById(R.id.tv_img_usu);
        txt_title_message = (TextView) findViewById(R.id.txt_title_message);
        id_usu = (TextView) findViewById(R.id.id_usu);
        forma_pagamento_cli = (TextView) findViewById(R.id.forma_pagamento_cli);
        destino_cliente = (TextView) findViewById(R.id.destino_cliente);
        chamadacancel = (TextView) findViewById(R.id.chamadacancel);


        progress = ProgressDialog.show(MainPassageiroAguarda.this, "", "Aguarde...");
        progress.setCancelable(true);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/



        //FALTA PEGAR OS DADOS DA TABELA HISTORICO =>> LAT, LONG(ORIGEM) | LAT, LONG(DESTINO) | FORMA DE PAGAMENTO



        //================= ID DO TAXISTA - MainNotification =============
        Intent i = getIntent();
        String id_taxista = i.getStringExtra("id_do_taxista");
        taxi_id.setText(id_taxista);
        String id = i.getStringExtra("id_usu");
        id_usu.setText(id);
        String email_usu = i.getStringExtra("email_usu");
        email_cliente.setText(email_usu);
        String nome_usu = i.getStringExtra("nome_usu");
        nome_cliente.setText(nome_usu);
        String img_usu = i.getStringExtra("img_usu");
        tv_img_usu.setText(img_usu);
        String lat_usu = i.getStringExtra("lat_usu");
        tv_lat.setText(lat_usu);
        String long_usu = i.getStringExtra("long_usu");
        tv_long.setText(long_usu);
        String formapgm = i.getStringExtra("formapgm_usu");
        forma_pagamento_cli.setText(formapgm);
        String destino_cli = i.getStringExtra("destino_usu");
        destino_cliente.setText(destino_cli);
        String origem_ = i.getStringExtra("origem");
        localizacao_destino.setText(origem_);



        //tv_lat.setText(coord_lat_Cliente);
        //tv_long.setText(coord_long_Cliente);

        //FORMA DE PAGAMENTO
        //forma_pagamento_cli.setText(forma_pagamento);
        //DESTINO ESCOLHIDO PELO CLIENTE
        //destino_cliente.setText(destino_do_cliente);
        //================================================================

        //O USEI UMA ASYNCTASK PARA EXECUTAR A CHAMADA DA IMAGEM
        try{
            et = new ExtrairTask();
            et.execute();
            verificaCancelamento();
        }catch (Exception e){
            Toast.makeText(this, "Erro carregar imagem do Cliente" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Intent voltaATela = new Intent(this, MainNotification.class);
            startActivity(voltaATela);
        }


        //pegaDadosIdCliente(id_usu.getText().toString());


        //============ MOSTRA ENDEREÇO QUE O CLIENE ESTÁ =================
        //localizacao_destino.setText(endereco_cliente);
        //================================================================

        String mensagem_Notification = getIntent().getStringExtra("mensagem_Notification");
        mensagem_cliente.setText(mensagem_Notification);
        String titulo_Notification = getIntent().getStringExtra("titulo_Notification");
        txt_title_message.setText(titulo_Notification);


        iniciar_corrida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(MainPassageiroAguarda.this);
                //define o titulo
                builder.setTitle("INICIAR CORRIDA...");
                //define a mensagem
                builder.setMessage("Passageiro a bordo? inicie uma corrida agora?");
                //define um botão como positivo
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent iniciarCorrida = new Intent(MainPassageiroAguarda.this, MainTelaDeCorrida.class);

                        //ENVIA A MENSAGEM PARA A OUTRA TELA, ONDE SERÁ MOSTRADO NA TELA DO PASSAGEIRO AGUARDANDO
                        iniciarCorrida.putExtra("nome_usu", nome_cliente.getText().toString());
                        iniciarCorrida.putExtra("email_usu", email_cliente.getText().toString());
                        iniciarCorrida.putExtra("caminho_img", tv_img_usu.getText().toString());
                        iniciarCorrida.putExtra("forma_pagamento", forma_pagamento_cli.getText().toString());
                        iniciarCorrida.putExtra("destino_cli", destino_cliente.getText().toString());
                        iniciarCorrida.putExtra("origem_cli", localizacao_destino.getText().toString());
                        iniciarCorrida.putExtra("id_usu", id_usu.getText().toString());
                        iniciarCorrida.putExtra("id_taxista", taxi_id.getText().toString());

                        stopChamada = false;
                        startActivity(iniciarCorrida);
                        finish();

                    }
                });
                //define um botão como negativo.
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(MainPassageiroAguarda.this, "Passageiro aguardando...", Toast.LENGTH_SHORT).show();// + arg1, Toast.LENGTH_SHORT).show();
                    }
                });
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();

            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = ProgressDialog.show(MainPassageiroAguarda.this, "", "Aguarde...");
                editaDadosStatus(taxi_id.getText().toString());

            }
        });


        mensagem_rapida = (TextView) findViewById(R.id.mensagem_rapida);
        opcoes_taxista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //CHAMA O XML DIALOG_FIN
                View mView = getLayoutInflater().inflate(R.layout.dialog_opcoes_taxista, null);

                Button btn_chegoemcinco = (Button) mView.findViewById(R.id.btn_chegoemcinco);
                Button btn_taxichegando = (Button) mView.findViewById(R.id.btn_taxichegando);
                Button btn_taxidobrando = (Button) mView.findViewById(R.id.btn_taxidobrando);
                Button btn_transitocheio = (Button) mView.findViewById(R.id.btn_transitocheio);
                Button btn_atrasando = (Button) mView.findViewById(R.id.btn_atrasando);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainPassageiroAguarda.this);
                //mBuilder.setIcon(R.drawable.logomarcalets).setTitle("ESCOLHA");
                mBuilder.setCancelable(false);

                btn_chegoemcinco.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainPassageiroAguarda.this, "Passageiro aguardando...", Toast.LENGTH_SHORT).show();

                        String opcao1 = "Chego em 5 minutos";
                        mensagem_rapida.setText(opcao1);

                        pd = ProgressDialog.show(MainPassageiroAguarda.this, "", "Enviando " + opcao1);
                        enviaMensagens(taxi_id.getText().toString());
                    }
                });

                btn_taxichegando.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainPassageiroAguarda.this, "Passageiro aguardando...", Toast.LENGTH_SHORT).show();

                        String opcao2 = "Táxi chegando";
                        mensagem_rapida.setText(opcao2);

                        pd = ProgressDialog.show(MainPassageiroAguarda.this, "", "Enviando " + opcao2);
                        enviaMensagens(taxi_id.getText().toString());
                    }
                });

                btn_taxidobrando.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainPassageiroAguarda.this, "Passageiro aguardando...", Toast.LENGTH_SHORT).show();

                        String opcao3 = "Táxi dando a volta no quarteirão";
                        mensagem_rapida.setText(opcao3);

                        pd = ProgressDialog.show(MainPassageiroAguarda.this, "", "Enviando " + opcao3);
                        enviaMensagens(taxi_id.getText().toString());
                    }
                });

                btn_transitocheio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainPassageiroAguarda.this, "Passageiro aguardando...", Toast.LENGTH_SHORT).show();

                        String opcao4 = "Trânsito cheio, chego em 10 minutos";
                        mensagem_rapida.setText(opcao4);

                        pd = ProgressDialog.show(MainPassageiroAguarda.this, "", "Enviando " + opcao4);
                        enviaMensagens(taxi_id.getText().toString());
                    }
                });

                btn_atrasando.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainPassageiroAguarda.this, "Passageiro aguardando...", Toast.LENGTH_SHORT).show();

                        String opcao5 = "Imprevisto, irei atrasar";
                        mensagem_rapida.setText(opcao5);

                        pd = ProgressDialog.show(MainPassageiroAguarda.this, "", "Enviando " + opcao5);
                        enviaMensagens(taxi_id.getText().toString());
                    }
                });

                mBuilder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                /*btn_cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent mesmatela = new Intent(getActivity(), MainMapActivity.class);
                        mesmatela.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mesmatela);
                    }
                });*/

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();

            }

        });

    }


    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }



    // classe que extrai o arquivo e estente AsyncTask
    public class ExtrairTask extends AsyncTask<Void, Void, Void> {

        // variável do dialog
        private ProgressDialog pDialog;

        // construtor padrão
        public ExtrairTask() {

            // instanciando o dialog
            pDialog = new ProgressDialog(MainPassageiroAguarda.this);
            pDialog.setMessage("Extraindo arquivo...");
            pDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... params) {

            new PegaImageCli(tv_img_usu.getText().toString()).execute();
            //verificaCancelamento();
            //  aqui vai o código para extrair o  arquivo...
            //  ele pode ser implementando aqui ou pode ser feita a chamada de uma função

            return null;
        }

        @Override
        protected void onPreExecute() {
            if (!pDialog.isShowing())
                pDialog.show();

            //DadosPassTask dadosPassTask = new DadosPassTask();
            //dadosPassTask.execute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (pDialog.isShowing())
                pDialog.dismiss();
        }

    }

/*
    // classe que extrai o arquivo e estente AsyncTask
    public class DadosPassTask extends AsyncTask<Void, Void, Void> {

        // variável do dialog
        private ProgressDialog pDialog;

        // construtor padrão
        public DadosPassTask() {

            // instanciando o dialog
            pDialog = new ProgressDialog(MainPassageiroAguarda.this);
            pDialog.setMessage("Aguarde...");
            pDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... params) {

            pegaDadosIdCliente(id_usu.getText().toString());
            Log.i("DTP", "sucesso");

            return null;
        }

        @Override
        protected void onPreExecute() {
            if (!pDialog.isShowing())
                pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (pDialog.isShowing())
                pDialog.dismiss();
        }

    }
*/



    public void alarmSituacaoLiv(){
        //====================
        Intent intent = new Intent("ALARME_DISPARADO");
        PendingIntent p = PendingIntent.getBroadcast(MainPassageiroAguarda.this, 0, intent, 0);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 3);

        AlarmManager alarme = (AlarmManager) MainPassageiroAguarda.this.getSystemService(ALARM_SERVICE);
        alarme.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 3000, p);

        cancelaAlarmNotificationatual();

    }

    public void cancelaAlarmNotificationatual(){
        Intent intents = new Intent("ALARME_DISPARADO_OCUP");
        PendingIntent pi = PendingIntent.getBroadcast(MainPassageiroAguarda.this, 0, intents, 0);

        AlarmManager alarmes = (AlarmManager) MainPassageiroAguarda.this.getSystemService(ALARM_SERVICE);
        alarmes.cancel(pi);
    }


    //================ ENVIA PARA O BANCO DE DADOS = AS MENSAGENS RÁPIDAS DA TELA ==============
    //COMENTADO PARA TESTAR O DEBAIXO LINK QUASE OFICIAL
    //String BASE_URL = "http://www.letsgosobral.esy.es/firebase_TokeneStatus/updateData.php";
    //O LINK OFICIAL SERÁ String BASE_URL = "http://www.letsgosobral.com.br/cad_Taxista/firebase_TokeneStatus/updateData.php";

    //String BASE_URL = "http://www.letsgosobral.esy.es/taxi/status_Taxista/mensagemRapida.php";
    //TROCADO POR
    String BASE_URL = "http://www.letsgosobral.com/taxi/status_Taxista/mensagemRapida.php";

    public void enviaMensagens(String taxi_id_unic) {

        final RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL) //Setting the Root URL
                .build();

        AppConfig.mensagemRapida api = adapter.create(AppConfig.mensagemRapida.class);

        api.atualizaMsgm(
                taxi_id_unic,
                mensagem_rapida.getText().toString(),

                new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response) {

                        try {

                            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                            String resp;
                            resp = reader.readLine();
                            Log.d("success", "" + resp);

                            JSONObject jObj = new JSONObject(resp);
                            int success = jObj.getInt("success");

                            if (success == 1) {
                                Toast.makeText(MainPassageiroAguarda.this, "Passageiro aguardando!", Toast.LENGTH_SHORT).show();
                                pd.dismiss();

                            } else {
                                Toast.makeText(MainPassageiroAguarda.this, "Mensagem não enviada!", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                        } catch (IOException e) {
                            Log.d("Exception", e.toString());
                        } catch (JSONException e) {
                            Log.d("JsonException", e.toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                        Toast.makeText(MainPassageiroAguarda.this, "Verifique sua conexão!", Toast.LENGTH_LONG).show();
                        pd.cancel();
                    }
                }
        );

    }

    //============================== MENSAGENS RÁPIDAS ==============================

    /*
    //=-=-=-=-=-=|-=-=-=-=-=-=|PEGA OS DADOS DO CLIENTE PELO EMAIL VINDA DA NOTIFICAÇÃO|=-=-=-=-=-=-=-|=-=-=-=-=-=-=-

    public void pegaDadosIdCliente(final String id_usu) {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL) //Setting the Root URL
                .build();

        AppConfig.getIdPassageiro api = adapter.create(AppConfig.getIdPassageiro.class);

        api.getDadosPassageiro(
                id_usu,
                new Callback<JsonElement>() {
                    @Override
                    public void success(JsonElement result, Response response) {

                        String myResponse = result.toString();
                        Log.d("response", "" + myResponse);

                        try {
                            JSONObject jObj = new JSONObject(myResponse);

                            int success = jObj.getInt("success");

                            if (success == 1) {

                                JSONArray jsonArray = jObj.getJSONArray("details");
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jo = jsonArray.getJSONObject(i);

                                    tv_lat.setText(jo.getString("usu_latitude"));
                                    tv_long.setText(jo.getString("usu_longitude"));
                                    forma_pagamento_cli.setText(jo.getString("hist_form_pag"));
                                    destino_cliente.setText(jo.getString("hist_destino"));
                                }

                                //details_list.setAdapter(displayAdapter);
                                progress.dismiss();
                                //pDialog.dismiss();

                            } else {
                                Toast.makeText(getApplicationContext(), "Sem registros encontrados!", Toast.LENGTH_SHORT).show();
                                //pDialog.cancel();
                                progress.cancel();
                            }
                        } catch (JSONException e) {
                            Log.d("exception", e.toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("Failure", error.toString());
                        Toast.makeText(MainPassageiroAguarda.this, error.toString(), Toast.LENGTH_LONG).show();
                        progress.cancel();
                        //pDialog.cancel();
                    }
                }
        );


    }
    */


    //========================PEGA A IMAGEM DE ACORDO COM ID_UNICO======================
    private class PegaImageCli extends AsyncTask<Void, Void, Bitmap> {
        String image_usu;

        public PegaImageCli(String tv_img_usu){  //AQUI SERÁ O CAMINHO
            this.image_usu = tv_img_usu;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            //COMENTADO PARA TESTAR O DE BAIXO
            //String url = SERVER_ADDRESS + "cad_Taxista/imagens_Taxista/foto_perfil/" + nome + ".JPG";
            String url = SERVER_ADDRESS + image_usu;//AQUI EU COLOCO O TEXTVIEW QUE CHAMA O CAMINHO DA IMAGEM

            try{

                URLConnection connection = new URL(url).openConnection();
                connection.setConnectTimeout(1000 * 30);
                connection.setReadTimeout(1000 * 30);

                return BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null){
                img_usu.setImageBitmap(bitmap);
                progress.cancel();
                //pDialog.dismiss();
            }
        }

    }
    //=============================DA IMAGEM DA PASTA DO BD============================


    //====================================POSICIONAMENTO DO GPS=========================================
    @Override
    public void onResume() {    //Executa sempre que meu fragmento está visível...
        super.onResume();
        //Ativa o GPS

        allowNetwork = true;
        locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, locListener); //Aqui é This ao invés locListener
    }

    @Override
    public void onPause() {
        super.onPause();
        //Entra no modo de pausa, para economizar espaço na memoria, bateria...
        //locationmanager.removeUpdates(locListener);
        locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationmanager.removeUpdates(locListener);
    }
    //==================================================================================================


    //=================BUSCA A LISTA DE ENDEREÇOS POSSÍVEIS NA COORDENADA POSICIONADA======================
    public android.location.Address buscaEndereco(double latitude, double longitude)throws IOException{
        Geocoder geocoder;
        android.location.Address address = null;
        List<android.location.Address> addresses;

        geocoder = new Geocoder(MainPassageiroAguarda.this);

        addresses = geocoder.getFromLocation(latitude, longitude, 1);

        if (addresses.size() > 0){
            address = addresses.get(0);
        }
        return address;
    }


    //===============================MÉTODOS DE POSICIONAMENTO DO GPS======================================
    LocationListener locListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

            if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                allowNetwork = false;
            }
            if (allowNetwork || location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                try {
                    atualizarlocal(location);
                }catch (Exception e){
                    Toast.makeText(MainPassageiroAguarda.this, "Carregamento lento \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            locs.add(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
            Toast.makeText(MainPassageiroAguarda.this, "GPS ativado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String s) {
            Toast.makeText(MainPassageiroAguarda.this, "GPS desativado", Toast.LENGTH_SHORT).show();

            //Alert responsável por lembrar que o GPS está desativado
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainPassageiroAguarda.this);
            builder.setMessage("Seu GPS esta desabilitado, deseja habilita-lo?")
                    .setCancelable(false)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    };

    //=======================================MINHA LOCALIZAÇÃO ALTERADA A CADA 3 SEGUNDOS====================================
    public void meulocal() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        atualizarlocal(location);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000,0, locListener);
    }



    @Override
    public void onMapClick(LatLng latLng) {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        try {
            meulocal();
        }catch (Exception e){
            e.getMessage();
        }

        mMap.setOnMapClickListener(this);
        //mMap.getUiSettings().setZoomControlsEnabled(true);
    }


    //MARCADOR DA MINHA POSIÇÃO!!!
    private void agregadormarcador(final double lat, final double lng) {
        final LatLng coordenadas = new LatLng(lat, lng);
        final CameraUpdate meulocal = CameraUpdateFactory.newLatLngZoom(coordenadas, 18);
        if (marcador != null)
            marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions().position(coordenadas).title("Minha localização atual").
                icon(BitmapDescriptorFactory.fromResource(R.mipmap.taxi2))); //Essas são propriedades de titulo e imagem ao nosso marcador.
        mMap.animateCamera(meulocal);   //e Aqui agregamos uma camera animada para se lcomover a nossa posição.


        //Evento do marcador
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {

                if (arg0.getTitle().equals("Minha localização atual")) {
                    //Toast.makeText(getActivity(), arg0.getTitle(), 1000).show();// display toast

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainPassageiroAguarda.this);
                    //define o titulo
                    builder.setTitle("TESTE");
                    //define a mensagem
                    builder.setMessage("Teste de evento em marcador!!!");
                    //define um botão como positivo
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                            try {
                                endereco = buscaEndereco(lat, lng);
                                //ed_origem.setText(endereco.getAddressLine(0));
                                Toast.makeText(MainPassageiroAguarda.this, "Aqui: " + endereco.getAddressLine(0), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Log.i("GPS", e.getMessage());
                            }
                            //Intent intent = new Intent(getActivity(), MapsFragment.class);
                            //startActivity(intent);
                            //Toast.makeText(getContext(), "Teste deu certo!", Toast.LENGTH_SHORT).show();

                        }
                    });
                    //define um botão como negativo.
                    builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            //Toast.makeText(getContext(), "Você ainda não está Ocupado", Toast.LENGTH_SHORT).show();// + arg1, Toast.LENGTH_SHORT).show();
                            Crouton.makeText(MainPassageiroAguarda.this, "Está tudo Ok!.", Style.CONFIRM).show();
                        }
                    });
                    //cria o AlertDialog
                    alerta = builder.create();
                    //Exibe
                    alerta.show();
                }

                return true;

            }

        });


        //Recuperamos a URL passando as cordenadas de origem como sendo a cordenada que definimos
        //para a nossa residência e as coordenadas de destino como sendo a do escritório da Google em SP.

        Double lat_cliente = Double.parseDouble(tv_lat.getText().toString());
        Double long_cliente = Double.parseDouble(tv_long.getText().toString());
        final LatLng local_cliente = new LatLng(lat_cliente, long_cliente);

        String url = montarURLRotaMapa(coordenadas.latitude, coordenadas.longitude, lat_cliente, long_cliente);
        mMap.addMarker(new MarkerOptions().position(local_cliente).title("Cliente Aqui").
                icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_usu))); //Essas são propriedades de titulo e imagen ao nosso marcador.
        //Criamos uma instância de nossa AsyncTask (para cada requisição deverá ser criada uma nova instância).
        MainPassageiroAguarda.MinhaAsyncTask tarefa = new MainPassageiroAguarda.MinhaAsyncTask();

        //Se a versão de SDK do Android do aparelho que está executando o aplicativo for menor que a HONEYCOMB (11)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            //Executa a tarefa passando a URL recuperada
            tarefa.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        } else {
            //Executa a tarefa passando a URL recuperada
            tarefa.execute(url);
        }

        //return new LatLng(lat, lng);

    }


    //RESPONSÃVEL PELA ATUALIZAÇÃO DAS COORDENADAS
    private void atualizarlocal(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            agregadormarcador(lat, lng);
        }
    }



    //MÉTODOS JÁ CHAMADOS ACIMA... ANDROID STUDIO MALUCO! :/
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    //============================ MÉTODOS DO MAPA ========================




    //=========================MÉTODO PARA DESENHAR UMA ROTA COM HTTP==============================
    private String montarURLRotaMapa(double latOrigen, double lngOrigen, double latDestino, double lngDestino){
        //Base da URL
        String url = "http://maps.googleapis.com/maps/api/directions/json?origin=";
        //Local de origem
        url += latOrigen + "," + lngOrigen;
        url += "&destination=";
        //Local de destino
        url += latDestino + "," + lngDestino;
        //Outros parametros
        url += "&sensor=false&mode=driving&alternatives=true";

        return url;
    }
    //=============================================================================================

    public JSONObject requisicaoHTTP(String url) {
        JSONObject resultado = null;

        try {
            //Criamos um cliente HTTP para que possamos realizar a
            //requisição a um Servidor HTTP
            DefaultHttpClient httpClient = new DefaultHttpClient();
            //Definimos o método de requisição como sendo POST
            HttpPost httpPost = new HttpPost(url);
            //Recuperamos a resposta do Servidor HTTP
            HttpResponse httpResponse = httpClient.execute(httpPost);
            //Recuperamos o conteúdo enviado do Servidor HTTP
            HttpEntity httpEntity = httpResponse.getEntity();
            //Transformamos tal conteúdo em 'String'
            String conteudo = EntityUtils.toString(httpEntity);

            //Transformamos a 'String' do conteúdo em Objeto JSON
            resultado = new JSONObject(conteudo);

        } catch (UnsupportedEncodingException e) {
            Log.e("ProjetoMapas", e.getMessage());
        } catch (ClientProtocolException e) {
            Log.e("ProjetoMapas", e.getMessage());
        } catch (IOException e) {
            Log.e("ProjetoMapas", e.getMessage());
        } catch (JSONException e) {
            Log.e("ProjetoMapas", e.getMessage());
        }

        //Retornamos o conteúdo em formato JSON
        return resultado;
    }

    public void pintarCaminho(JSONObject json) {
        try {
            //Recupera a lista de possíveis rotas
            JSONArray listaRotas = json.getJSONArray("routes");
            //Para efeito de aprendizado iremos utilizar apenas a primeira opção
            JSONObject rota = listaRotas.getJSONObject(0);
            //Recuperamos os pontos a serem pintados para que surga a 'linha' no mapa
            String pontosPintar = rota.getJSONObject("overview_polyline").getString("points");
            //Recuperamos a lista de latitudes e longitudes para sabermos exatamente onde pintar
            List<LatLng> listaCordenadas = extrairLatLngDaRota(pontosPintar);

            //Percorremos por cada cordenada obtida
            for(int ponto = 0; ponto < listaCordenadas.size()-1 ; ponto++){
                //Definimos o ponto atual como origem
                LatLng pontoOrigem= listaCordenadas.get(ponto);
                //Definimos o próximo ponto como destino
                LatLng pontoDestino= listaCordenadas.get(ponto + 1);
                //Criamos um objeto do tipo PolylineOption para adicionarmos os pontos de origem e destino
                PolylineOptions opcoesDaLinha = new PolylineOptions();
                //Adicionamos os pontos de origem e destino da linha que vamos traçar
                opcoesDaLinha.add(new LatLng(pontoOrigem.latitude, pontoOrigem.longitude),
                        new LatLng(pontoDestino.latitude,  pontoDestino.longitude));
                //Criamos a linha de acordo com as opções que configuramos acima e adicionamos em nosso mapa
                Polyline line = mMap.addPolyline(opcoesDaLinha);
                //Mudamos algumas propriedades da linha que acabamos de adicionar em nosso mapa
                line.setWidth(20);
                line.setColor(Color.BLUE);
                line.setGeodesic(true);
            }
        }
        catch (JSONException e) {
            Log.e("ProjetoMapas", e.getMessage());
        }
    }

    //======================RESPONSÁVEL POR DECODIFICAR AS COORDENADAS====================
    private List<LatLng> extrairLatLngDaRota(String pontosPintar) {
        List<LatLng> listaResult = new ArrayList<LatLng>();
        int index = 0, len = pontosPintar.length();
        int lat = 0, lng = 0;

        while (index < len) {

            int b, shift = 0, result = 0;
            do {
                b = pontosPintar.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = pontosPintar.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            listaResult.add(p);
        }

        return listaResult;
    }


    private class MinhaAsyncTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject resultJSON = requisicaoHTTP(params[0]);

            return resultJSON;
        }

        @Override
        protected void onPostExecute(JSONObject resultadoRequisicao) {
            super.onPostExecute(resultadoRequisicao);

            if(resultadoRequisicao != null){
                pintarCaminho(resultadoRequisicao);
            }
        }
    }

    //==================================================================================

    String URL = "http://www.letsgosobral.com/taxi/status_Taxista/updateData.php";
    public void editaDadosStatus(final String taxi_id_unic) {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(URL) //Setting the Root URL
                .build();

        AppConfig.atualizaStatus api = adapter.create(AppConfig.atualizaStatus.class);

        api.atualizaDados(
                taxi_id_unic,
                Double.parseDouble(status),

                new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response) {

                        try {

                            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                            String resp;
                            resp = reader.readLine();
                            Log.d("success", "" + resp);

                            JSONObject jObj = new JSONObject(resp);
                            int success = jObj.getInt("success");

                            if (success == 1) {
                                Toast.makeText(MainPassageiroAguarda.this, "Corrida cancelada!", Toast.LENGTH_SHORT).show();

                                //POR ENQUANTO DE TESTE ... ISSO É DO BOTÃO ACEITAR
                                Intent telaPrincipal = new Intent(MainPassageiroAguarda.this, MainMapActivity.class);

                                String ID_, Nome_, Email_;
                                ID_ = taxi_id.getText().toString();
                                Nome_ =  (Constants.NOME);
                                Email_ = (Constants.EMAIL);

                                Bundle bundle = new Bundle();
                                bundle.putString("ID_UNI", ID_);
                                bundle.putString("Nome", Nome_);
                                bundle.putString("Email", Email_);

                                telaPrincipal.putExtras(bundle);

                                telaPrincipal.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(telaPrincipal);
                                alarmSituacaoLiv();
                                finish();

                                pd.dismiss();

                            } else {
                                Toast.makeText(MainPassageiroAguarda.this, "Verifique sua conexão!", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                        } catch (IOException e) {
                            Log.d("Exception", e.toString());
                        } catch (JSONException e) {
                            Log.d("JsonException", e.toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //Toast.makeText(MainNotification.this, error.toString(), Toast.LENGTH_LONG).show();
                        Toast.makeText(MainPassageiroAguarda.this, "Verifique sua conexão", Toast.LENGTH_SHORT).show();
                        pd.cancel();
                    }
                }
        );

    }

    //============================== LIVRE - OCUPADO ==============================



    //..........METODOS DE CANCELAMENTO DE CORRIDA.........

    public long verificaCancelamento() {

    final Handler handler = new Handler();      //Faz uma pilha de ações com a ajuda da Thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(3000);
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                // Write your code here to update the UI.
                                //=================================================
                                while (stopChamada) {
                                    situacaoCancelamento(taxi_id.getText().toString());
                                    //Toast.makeText(MainPassageiroAguarda.this, "Verificado", Toast.LENGTH_SHORT).show();

                                    break;
                                }
                                //=================================================
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();

        return 0;


    }


    public void situacaoID(){

        String tvCorridaCancelada = chamadacancel.getText().toString();

        switch (tvCorridaCancelada) {
            case "1":

                break;

            case "0":

                AlertDialog.Builder builder = new AlertDialog.Builder(MainPassageiroAguarda.this);
                //define o titulo
                builder.setTitle("CORRIDA CANCELADA");
                builder.setCancelable(false);
                //define a mensagem
                builder.setMessage(nome_cliente.getText().toString() + " CANCELOU a corrida!");
                //define um botão como positivo
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        pd = ProgressDialog.show(MainPassageiroAguarda.this, "", "Aguarde...");
                        editaDadosStatus(taxi_id.getText().toString());
                        stopChamada = false;

                    }
                });
                //define um botão como negativo.
                /*
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                */
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();


                break;
        }

    }
/*
    //=-=-=-=-=-=|-=-=-=-=-=-=|PEGA OS DADOS DO CLIENTE PELO ID VINDA DO MÉTODO ACIMA|=-=-=-=-=-=-=-|=-=-=-=-=-=-=-
    public void pegaDadosIdCliente(final String id_usu) {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL) //Setting the Root URL
                .build();

        AppConfig.getIdPassageiro api = adapter.create(AppConfig.getIdPassageiro.class);

        api.getDadosPassageiro(
                id_usu,
                new Callback<JsonElement>() {
                    @Override
                    public void success(JsonElement result, Response response) {

                        String myResponse = result.toString();
                        Log.d("response", "" + myResponse);

                        try {
                            JSONObject jObj = new JSONObject(myResponse);

                            int success = jObj.getInt("success");

                            if (success == 1) {

                                JSONArray jsonArray = jObj.getJSONArray("details");
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jo = jsonArray.getJSONObject(i);

                                    chamadacancel.setText(jo.getString("chamada"));
                                    situacaoID();
                                }

                                //details_list.setAdapter(displayAdapter);
                                //progress.dismiss();
                                //pDialog.dismiss();

                            } else {
                                Toast.makeText(getApplicationContext(), "Sem registros encontrados!", Toast.LENGTH_SHORT).show();
                                //pDialog.cancel();
                                //progress.cancel();
                            }
                        } catch (JSONException e) {
                            Log.d("exception", e.toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("Failure", error.toString());
                        Toast.makeText(MainPassageiroAguarda.this, error.toString(), Toast.LENGTH_LONG).show();
                        //progress.cancel();
                        //pDialog.cancel();
                    }
                }
        );


    }
*/

    //=-=-=-=-=-=|-=-=-=-=-=-=|PEGA OS DADOS DO USUÁRIO PELO ID_UNICO|=-=-=-=-=-=-=-|=-=-=-=-=-=-=-

    public void situacaoCancelamento(final String taxi_id_unic) {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL) //Setting the Root URL
                .build();

        AppConfig.getTaxistaId api = adapter.create(AppConfig.getTaxistaId.class);

        api.getDados(
                taxi_id_unic,
                new Callback<JsonElement>() {
                    @Override
                    public void success(JsonElement result, Response response) {

                        String myResponse = result.toString();
                        Log.d("response", "" + myResponse);

                        try {
                            JSONObject jObj = new JSONObject(myResponse);

                            int success = jObj.getInt("success");

                            if (success == 1) {

                                JSONArray jsonArray = jObj.getJSONArray("details");
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jo = jsonArray.getJSONObject(i);

                                    chamadacancel.setText(jo.getString("taxi_notificacao"));
                                    situacaoID();

                                    Log.i("situacao", "Situacao carregada");
                                }

                                //details_list.setAdapter(displayAdapter);
                                //pDialog.dismiss();

                            } else {
                                Toast.makeText(MainPassageiroAguarda.this, "Falha ao atualizar status", Toast.LENGTH_SHORT).show();
                                Log.i("situacao", "Situacao falhou");
                                //pDialog.cancel();
                            }
                        } catch (JSONException e) {
                            Log.d("exception", e.toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("Failure", error.toString());
                        //Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                        Toast.makeText(MainPassageiroAguarda.this, "Verifique sua Conexão! Status inativo!", Toast.LENGTH_LONG).show();
                        //pDialog.cancel();
                    }
                }
        );


    }


}
