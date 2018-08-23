package br.com.tutorialandroid.splashscreen;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.tutorialandroid.splashscreen.app.Config;
import br.com.tutorialandroid.splashscreen.conexao.AppConfig;
import br.com.tutorialandroid.splashscreen.executaAppEmSegundoPlano.service;
import br.com.tutorialandroid.splashscreen.loginActivity.Constants;
import br.com.tutorialandroid.splashscreen.telaDeCorrida.MainPagamentoTaxi;
import br.com.tutorialandroid.splashscreen.telaDeCorrida.MainPassageiroAguarda;
import br.com.tutorialandroid.splashscreen.util.NotificationUtils;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainNotification extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private LocationManager locationmanager;
    private Marker marcador;
    double lat = 0.0;
    double lng = 0.0;
    private boolean allowNetwork;

    private List<Location> locs = new ArrayList<Location>();
    private android.location.Address endereco;

    public Button btn_aceita, btn_rejeita;
    public TextView tv_msg;

    private AlertDialog alerta;
    private ProgressDialog pd = null;
    private ProgressDialog progress = null;

    private static final String TAG = br.com.tutorialandroid.splashscreen.activity.MainActiviity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtMessage, txt_title_message, status_taxi, aceita_corrida, taxi_id_taxista,
            nome_usu, email_usu, img_usu, id_usu, usu_lt, usu_lon, usu_formpgm, usu_dest;

    private ExtrairTask et;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_notification);
        startService(new Intent(this, service.class));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        txt_title_message = (TextView) findViewById(R.id.title_message);
        taxi_id_taxista = (TextView) findViewById(R.id.taxi_id_taxista);
        txtMessage = (TextView) findViewById(R.id.txt_push_message);
        nome_usu = (TextView) findViewById(R.id.usu_name);
        email_usu = (TextView) findViewById(R.id.usu_email);
        img_usu = (TextView) findViewById(R.id.usu_img);
        id_usu = (TextView) findViewById(R.id.usu_id);
        usu_dest = (TextView) findViewById(R.id.usu_dest);
        usu_lt = (TextView) findViewById(R.id.usu_lt);
        usu_lon = (TextView) findViewById(R.id.usu_lon);
        usu_formpgm = (TextView) findViewById(R.id.usu_formpgm);

        /*
        //CANCELA NOTIFICAÇÃO, mas já usamos no n.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(R.drawable.logomaraclets);
        */

        progress = ProgressDialog.show(MainNotification.this, "", "Aguarde...");



        //PEGAR DESTINO QUANDO O LUCAS MANDAR...
        //usu_destino.setText("Rua Doutor Guarani, próx. a escola");

        //PEGAR FORMA DE PAGAMENTO QUANDO O LUCAS MANDAR...
        //usu_forma_pagamento.setText("Á vista");

        //============================== DO FIREBASE ==========================================



        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm registrado com sucesso
                    // agora assinar o tópico `global` para receber notificações de aplicativo abrangente
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    String titulo = intent.getStringExtra("title");
                    txt_title_message.setText(titulo);
                    txtMessage.setText(message);
                    Toast.makeText(getApplicationContext(), "Notificação: " + message + "\n" + "Título: " + titulo, Toast.LENGTH_LONG).show();

                }
            }
        };



        //TESTANDO EXEÇÕES COM TRY CATCH
        try {
            displayFirebaseRegId();
        }catch (Exception e) {
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //=====================================================================================

        btn_aceita = (Button) findViewById(R.id.btn_aceita);
        btn_rejeita = (Button) findViewById(R.id.btn_rejeita);
        status_taxi = (TextView) findViewById(R.id.status_taxi);
        aceita_corrida = (TextView) findViewById(R.id.aceitarcorrida);

        //btn_aceita.setEnabled(true);
        //btn_rejeita.setEnabled(false);

        btn_aceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                //STATUS ENVIADO PARA O BANCO DO USUÁRIO
                status_taxi.setText("0");

                pd = ProgressDialog.show(MainNotification.this, "", "Aguarde...");
                editaDados(taxi_id_taxista.getText().toString());

                aceita_corrida.setText("1");
                botaoAceite(taxi_id_taxista.getText().toString());
/*
                AlertDialog.Builder builder = new AlertDialog.Builder(MainNotification.this);
                //define o titulo
                builder.setTitle("ACEITAR CORRIDA");
                //define a mensagem
                builder.setMessage("Aceitar a corrida?");
                //define um botão como positivo
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {


                        //btn_aceita.setEnabled(false);
                        //btn_rejeita.setEnabled(true);

                    }
                });


                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();
*/
            }

        });


        btn_rejeita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                pd = ProgressDialog.show(MainNotification.this, "", "Aguarde...");
                aceita_corrida.setText("0");
                botaoAceite(taxi_id_taxista.getText().toString());


                Intent corrida = new Intent(MainNotification.this, MainMapActivity.class);

                String ID_, Nome_, Email_;
                ID_ = taxi_id_taxista.getText().toString();
                Nome_ =  (Constants.NOME);
                Email_ = (Constants.EMAIL);


                Bundle bundle = new Bundle();
                bundle.putString("ID_UNI", ID_);
                bundle.putString("Nome", Nome_);
                bundle.putString("Email", Email_);

                corrida.putExtras(bundle);

                corrida.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(corrida);

                finish();


/*
                AlertDialog.Builder builder = new AlertDialog.Builder(MainNotification.this);
                //define o titulo
                builder.setTitle("REJEITAR CORRIDA");
                //define a mensagem
                builder.setMessage("Rejeitar a corrida?");
                //define um botão como positivo
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {



                        //btn_aceita.setEnabled(true);
                        //btn_rejeita.setEnabled(false);

                    }
                });
                //define um botão como negativo.
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(MainNotification.this, "Aceite o passageiro pressionando o botão de ACEITAR...", Toast.LENGTH_SHORT).show();// + arg1, Toast.LENGTH_SHORT).show();

                    }
                });
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();
*/
            }

        });

    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    public void alarmSituacaoOcup(){
        //========================

        Intent intent = new Intent("ALARME_DISPARADO_OCUP");
        PendingIntent p = PendingIntent.getBroadcast(MainNotification.this, 0, intent, 0);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 3);

        AlarmManager alarme = (AlarmManager) MainNotification.this.getSystemService(ALARM_SERVICE);
        alarme.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 3000, p);

        cancelaAlarmNotificationatual();
    }

    public void cancelaAlarmNotificationatual(){
        Intent intenti = new Intent("ALARME_DISPARADO");
        PendingIntent pii = PendingIntent.getBroadcast(MainNotification.this, 0, intenti, 0);

        AlarmManager alarmee = (AlarmManager) MainNotification.this.getSystemService(ALARM_SERVICE);
        alarmee.cancel(pii);
    }

    //=========================== DA NOTIFICAÇÃO PUSH =============================

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        SharedPreferences msg_notify = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_MSG, 0);
        String msgNotify = msg_notify.getString("notificacaoMsg", null);

        SharedPreferences titulo_notify = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_TITLE_MSG, 0);
        String tituloNotify = titulo_notify.getString("notificacaoTitulo", null);


        txt_title_message.setText(tituloNotify);
        txtMessage.setText(msgNotify);

        if (txt_title_message.getText().equals("CHAMADA PERDIDA!")){

            //btn_aceita.setEnabled(false);
            //btn_rejeita.setEnabled(false);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainNotification.this);
            //define o titulo
            builder.setCancelable(false);
            builder.setTitle("CHAMADA PERDIDA");
            //define a mensagem
            builder.setMessage("Prezado taxista, tempo de limite de chamada atingido!");
            //define um botão como positivo
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {

                    Intent chamadap = new Intent(MainNotification.this, MainActivity.class);
                    startActivity(chamadap);

                }
            });


            //cria o AlertDialog
            alerta = builder.create();
            //Exibe
            alerta.show();


        } else{
            //PEGA EMAIL DO CLIENTE... E FAZ A REQUISIÇÃO NO BANCO DE DADOS
            pegaDadosCliente(txt_title_message.getText().toString());//txt_title_message.getText().toString());//"lucas201293@gmail.com");
        }


        Log.e(TAG, "Notificação: " + msgNotify);
        Log.e(TAG, "Firebase reg id: " + regId);

        /*
        // Get token
        String token = FirebaseInstanceId.getInstance().getToken();

        // Log and toast
        String msg = getString(R.string.msg_token_fmt, token);
        Log.e(TAG, msg);
        Toast.makeText(MainActiviity.this, msg, Toast.LENGTH_SHORT).show();

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            txtRegId.setText("Firebase Reg Id: " + regId);
        else
            txtRegId.setText("Firebase Reg Id is not received yet!");
        */

        SharedPreferences email_Shared = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_EMAIL, 0);
        String taxi_id_shared = email_Shared.getString("IDnoSharedP", null);
        taxi_id_taxista.setText(taxi_id_shared);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());


        allowNetwork = true;
        locationmanager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener); //Aqui é This ao invés locListener
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();

        //Entra no modo de pausa, para economizar espaço na memoria, bateria...
        //locationmanager.removeUpdates((android.location.LocationListener) locListener);
        locationmanager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationmanager.removeUpdates(locListener);
    }

    //=============================================================================




    //=================BUSCA A LISTA DE ENDEREÇOS POSSÍVEIS NA COORDENADA POSICIONADA======================

    public android.location.Address buscaEndereco(double latitude, double longitude)throws IOException{
        Geocoder geocoder;
        android.location.Address address = null;
        List<Address> addresses;

        geocoder = new Geocoder(MainNotification.this);

        addresses = geocoder.getFromLocation(latitude, longitude, 1);

        if (addresses.size() > 0){
            address = addresses.get(0);
        }
        return address;
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //COORDENADA DO CLIENTE
        //Double lat_cli = Double.parseDouble(usu_lat_cli.getText().toString());
        //Double long_cli = Double.parseDouble(usu_long_cli.getText().toString());

        // Add a marker in Sydney, Australia, and move the camera.
        final LatLng sobral = new LatLng(-3.685728, -40.344364);//lat_cli, long_cli);
        mMap.addMarker(new MarkerOptions().position(sobral).title("Cliente Aqui!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sobral));
        mMap.addMarker(new MarkerOptions().position(sobral).title("Cliente Aqui!").icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_usu)));
        final CameraUpdate localizacao_usu = CameraUpdateFactory.newLatLngZoom(sobral, 19);
        mMap.animateCamera(localizacao_usu);


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                //if (arg0.getTitle().equals("MyHome")) // if marker source is clicked
                //  Toast.makeText(getActivity(), arg0.getTitle(), 1000).show();// display toast
/*
                try{

                    Double lat_cli = Double.parseDouble(usu_lat_cli.getText().toString());
                    Double long_cli = Double.parseDouble(usu_long_cli.getText().toString());

                    endereco = buscaEndereco(lat_cli, long_cli);
                    //ed_origem.setText(endereco.getAddressLine(0));
                    //Toast.makeText(MainNotification.this, "Aqui: " + endereco.getAddressLine(0), Toast.LENGTH_SHORT).show();
                }catch (IOException e){
                    Log.i("GPS", e.getMessage());
                }

                if (arg0.getTitle().equals("Cliente Aqui!")) {
                    //Toast.makeText(getActivity(), arg0.getTitle(), 1000).show();// display toast

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainNotification.this);
                    //define o titulo
                    builder.setTitle("INFORMAÇÕES");
                    //define a mensagem
                    builder.setMessage("Localização: " + endereco.getAddressLine(0) + "\n" + "Destino: " + "\n\n" + "Cliente: " + usu_nome_cli);
                    //define um botão como positivo
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                            //meulocal();
                            //locs = new ArrayList<Location>();
                            //agregadormarcador(lat, lng);

                            //Limpamos quaisquer configurações anteriores em nosso mapa
                            //mMap.setOnMyLocationChangeListener(null);
                            //mMap.clear();

                        }
                    });
                    //define um botão como negativo.
                    builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            //Toast.makeText(getContext(), "Você ainda não está Ocupado", Toast.LENGTH_SHORT).show();// + arg1, Toast.LENGTH_SHORT).show();
                            Crouton.makeText(MainNotification.this, "Está tudo Ok!.", Style.CONFIRM).show();
                        }
                    });
                    //cria o AlertDialog
                    alerta = builder.create();
                    //Exibe
                    alerta.show();

                }

                //if(arg0.getTitle().equals("MapleBear Head Office")) // if marker source is clicked
                //  Toast.makeText(getActivity(), arg0.getTitle(),1000).show();// display toast
*/
                return true;

            }

        });


        /*
        LatLng coordenadas = new LatLng(lat, lng);
        CameraUpdate meulocal = CameraUpdateFactory.newLatLngZoom(coordenadas, 19);
        if (marcador != null)
            marcador.remove(); //Se o marcador for diferente de null, deverá ser removido
        marcador = mMap.addMarker(new MarkerOptions().position(coordenadas).title("Cliente Aqui!").
                icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_usu))); //Essas são propriedades de titulo e imagen ao nosso marcador.
        mMap.animateCamera(meulocal);   //e Aqui agregamos uma camera animada para se lcomover a nossa posição.
*/

    }

    private void agregadormarcador(double lat, double lng) {
        LatLng coordenadas = new LatLng(lat, lng);
        CameraUpdate meulocal = CameraUpdateFactory.newLatLngZoom(coordenadas, 17);
        if (marcador != null)
            marcador.remove(); //Se o marcador for diferente de null, deverá ser removido
        marcador = mMap.addMarker(new MarkerOptions().position(coordenadas).title("Minha localização atual").
                icon(BitmapDescriptorFactory.fromResource(R.mipmap.taxi2))); //Essas são propriedades de titulo e imagen ao nosso marcador.
        //mMap.animateCamera(meulocal);   //e Aqui agregamos uma camera animada para se lcomover a nossa posição.


        //Recuperamos a URL passando as cordenadas de origem como sendo a cordenada que definimos
        //para a nossa residência e as coordenadas de destino como sendo a do escritório da Google em SP.
/*
        String url = montarURLRotaMapa(coordenadas.latitude, coordenadas.longitude, -3.688811, -40.348535);
        //Criamos uma instância de nossa AsyncTask (para cada requisição deverá ser criada uma nova instância).
        MainNotification.MinhaAsyncTask tarefa = new MainNotification.MinhaAsyncTask();

        //Se a versão de SDK do Android do aparelho que está executando o aplicativo for menor que a HONEYCOMB (11)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            //Executa a tarefa passando a URL recuperada
            tarefa.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        } else {
            //Executa a tarefa passando a URL recuperada
            tarefa.execute(url);
        }
*/
        //return new LatLng(lat, lng);
    }



    private void atualizarlocal(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            agregadormarcador(lat, lng);
        }
    }

    //===============================MÉTODOS DE POSICIONAMENTO DO GPS======================================

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                allowNetwork = false;
            }
            if (allowNetwork || location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                atualizarlocal(location);
            }

            //======================MOSTRA MINHA COORDENADA=====================
            //Toast.makeText(getActivity(), "Coordenadas: \n" + "Lat: " + lat + " , \n" + "Long: " + lng, Toast.LENGTH_SHORT).show();

            /*
            if( location != null ) {
                locs.add(location);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Toast.makeText(getActivity(), "Data:" + sdf.format(location.getTime()), Toast.LENGTH_SHORT).show();
            }*/

            locs.add(location);
            /*
            TextView velocidade = (TextView) findViewById(R.id.txtspeed);
            double speed = (double) ((location.getSpeed() * 3600) / 1000);

            DecimalFormat df = new DecimalFormat("0.##");   //CONVERTE PARA FORMATO DECIMAL COM DUAS CASAS!!!
            String dx = df.format(speed);

            velocidade.setText("" + dx + " km");*/
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            //Toast.makeText(getActivity(), "Status do provider foi alterado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String s) {
            Toast.makeText(MainNotification.this, "GPS ativado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String s) {
            Toast.makeText(MainNotification.this, "GPS desativado", Toast.LENGTH_SHORT).show();

            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainNotification.this);
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
            final android.app.AlertDialog alert = builder.create();
            alert.show();
        }
    };


    //=======================================MINHA LOCALIZAÇÃO ALTERADA A CADA 3 SEGUNDOS====================================
    public void meulocal() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        atualizarlocal(location);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000,0, locListener);
    }





    //================ ENVIA PARA O BANCO DE DADOS = LIVRE / OCUPADO ==============

    //String BASE_URL = "http://www.letsgosobral.esy.es/firebase_TokeneStatus/updateData.php";


    //String BASE_URL = "http://www.letsgosobral.esy.es/taxi/status_Taxista/updateData.php";
    //TROCADO POR
    String BASE_URL = "http://www.letsgosobral.com/taxi/status_Taxista/updateData.php";
    public void editaDados(final String taxi_id_unic) {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL) //Setting the Root URL
                .build();

        AppConfig.atualizaStatus api = adapter.create(AppConfig.atualizaStatus.class);

        api.atualizaDados(
                taxi_id_unic,
                Double.parseDouble(status_taxi.getText().toString()),

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
                                Toast.makeText(MainNotification.this, "Pronto para buscar passageiro!", Toast.LENGTH_SHORT).show();

                                //POR ENQUANTO DE TESTE ... ISSO É DO BOTÃO ACEITAR
                                Intent corrida = new Intent(MainNotification.this, MainPassageiroAguarda.class);

                                // em resposta a uma ação do usuário, instanciamos a tarefa e executamos
                                et = new ExtrairTask();
                                et.execute();

                                //ENVIA A MENSAGEM PARA A OUTRA TELA, ONDE SERÁ MOSTRADO NA TELA DO PASSAGEIRO AGUARDANDO
                                corrida.putExtra("mensagem_Notification", txtMessage.getText().toString());
                                corrida.putExtra("titulo_Notification", txt_title_message.getText().toString());

                                Double lat_ = Double.valueOf(usu_lt.getText().toString());
                                Double lng_ = Double.valueOf(usu_lon.getText().toString());
                                endereco = buscaEndereco(lat_, lng_);

                                Bundle bundle = new Bundle();
                                bundle.putString("id_do_taxista", taxi_id_unic);
                                bundle.putString("id_usu", id_usu.getText().toString());
                                bundle.putString("email_usu", email_usu.getText().toString());
                                bundle.putString("nome_usu", nome_usu.getText().toString());
                                bundle.putString("img_usu", img_usu.getText().toString());
                                bundle.putString("lat_usu", usu_lt.getText().toString());
                                bundle.putString("long_usu", usu_lon.getText().toString());
                                bundle.putString("formapgm_usu", usu_formpgm.getText().toString());
                                bundle.putString("destino_usu", usu_dest.getText().toString());
                                bundle.putString("origem", endereco.getAddressLine(0));

                                corrida.putExtras(bundle);

                                corrida.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                startActivity(corrida);
                                finish();

                                pd.dismiss();

                            } else {
                                Toast.makeText(MainNotification.this, "Verifique sua conexão!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainNotification.this, "Verifique sua conexão", Toast.LENGTH_SHORT).show();
                        pd.cancel();
                    }
                }
        );

    }
    //============================== LIVRE - OCUPADO ==============================


    //==================== 1 PARA ACEITAR CORRIDA 0 PARA RECUSAR ==================
    String URL_NOTIF = "http://www.letsgosobral.com/taxi/status_Taxista/taxiNotificacao.php";
    public void botaoAceite(final String taxi_id) {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(URL_NOTIF) //Setting the Root URL
                .build();

        AppConfig.aceiteCorrida api = adapter.create(AppConfig.aceiteCorrida.class);

        api.corridaaceite(
                taxi_id,
                Double.parseDouble(aceita_corrida.getText().toString()),

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
                                Toast.makeText(MainNotification.this, "Pronto para buscar passageiro!", Toast.LENGTH_SHORT).show();
                                pd.dismiss();

                            } else {
                                Toast.makeText(MainNotification.this, "Verifique sua conexão!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainNotification.this, "Verifique sua conexão", Toast.LENGTH_SHORT).show();
                        pd.cancel();
                    }
                }
        );

    }
    //=============================================================================

    // classe que extrai o arquivo e estente AsyncTask
    public class ExtrairTask extends AsyncTask<Void, Void, Void> {

        // variável do dialog
        private ProgressDialog pDialog;

        // construtor padrão
        public ExtrairTask() {

            // instanciando o dialog
            pDialog = new ProgressDialog(MainNotification.this);
            pDialog.setMessage("Extraindo arquivo...");
            pDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... params) {

            alarmSituacaoOcup();


            //  aqui vai o código para extrair o  arquivo...
            //  ele pode ser implementando aqui ou pode ser feita a chamada de uma função

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





    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
    //=-=-=-=-=-=|-=-=-=-=-=-=|PEGA OS DADOS DO CLIENTE PELO EMAIL VINDA DA NOTIFICAÇÃO|=-=-=-=-=-=-=-|=-=-=-=-=-=-=-

    public void pegaDadosCliente(final String usu_email) {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL) //Setting the Root URL
                .build();

        AppConfig.getCliEmail api = adapter.create(AppConfig.getCliEmail.class);

        api.getDadosCli(
                usu_email,
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


                                    //mostraId.setText(jo.getString("id"));
                                    nome_usu.setText(jo.getString("usu_nome"));
                                    //fone_cli.setText(jo.getString("usu_fone"));
                                    email_usu.setText(jo.getString("usu_email"));
                                    //usu_nasc_cli.setText(jo.getString("usu_nasc"));
                                    //usu_cartao_cli.setText(jo.getString("usu_cartao"));
                                    img_usu.setText(jo.getString("usu_img"));
                                    id_usu.setText(jo.getString("usu_id"));

                                    Log.i("CLIENTE", "Sucesso");
                                    pegaDadosIdCliente(id_usu.getText().toString());
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
                        Toast.makeText(MainNotification.this, error.toString(), Toast.LENGTH_LONG).show();
                        progress.cancel();
                        //pDialog.cancel();
                    }
                }
        );


    }



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

                                    usu_lt.setText(jo.getString("usu_latitude"));
                                    usu_lon.setText(jo.getString("usu_longitude"));
                                    usu_formpgm.setText(jo.getString("hist_form_pag"));
                                    usu_dest.setText(jo.getString("hist_destino"));
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
                        Toast.makeText(MainNotification.this, error.toString(), Toast.LENGTH_LONG).show();
                        progress.cancel();
                        //pDialog.cancel();
                    }
                }
        );


    }



/*
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
                line.setWidth(5);
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

*/

}
