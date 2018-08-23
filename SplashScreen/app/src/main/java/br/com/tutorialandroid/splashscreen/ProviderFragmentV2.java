package br.com.tutorialandroid.splashscreen;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;

import br.com.tutorialandroid.splashscreen.alarmeLembrete.BroadcastReceiverAux;
import br.com.tutorialandroid.splashscreen.conexao.AppConfig;
import br.com.tutorialandroid.splashscreen.loginActivity.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import br.com.tutorialandroid.splashscreen.ProviderFragmentV2.ExecutarTarefaa;


//public class ProviderFragmentV2 extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, LocationListener {
public class ProviderFragmentV2 extends SupportMapFragment
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener, LocationListener {

    private static final String TAG = "ProviderFragmentV2";

    private static final String PREF_NAME = "ProviderFragmentV2";

    private GoogleMap mMap;
    //Responsável pela minha localização...
    private LocationManager locationmanager;
    private Marker marcador, source, destination;
    double lat = 0.0;
    double lng = 0.0;
    private boolean allowNetwork;
    //private Polyline polyline;

    public ImageButton btn_livre, btn_ocupado; //showDialogClick
    public Button btn_ficarocupado, iniciarEnvioDeCoord, pararEnvioDeCoord;
    private AlertDialog alerta;
    private EditText ed_origem;
    //private TextView txtmetro;

    public TextView tv_ocupado, tv_porminuto, tv_cancelar,
            tv_ficarocupado, tv_coord_lat, tv_coord_lng, status_binario;
    private TextView switchStatus;
    private Switch mySwitch;

    //private List<Location> locs = new ArrayList<Location>();

    private android.location.Address endereco;
    private ProgressDialog pDialog = null;
    private ProgressDialog pd = null;

    public LinearLayout botao_livre, botao_ocupado;

    //|||||||||||||||||||||MOSTRA NOME E EMAIL: BEM VINDO: Cara|||||||||||||||||
    private TextView tv_status, msg_ocupado, tv_nome, tv_email, tv_id,
            tv_Notification, tv_situacao, token_id;
    private SharedPreferences pref, status;
    private SharedPreferences.Editor editor;
    private String STATUS = "STATUS";
    private ProgressBar progresso_status;
    //||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||

/* ESSE CODIGO PODERÁ SER DESCOMENTADO PARA ENVIAR COORDENADAS PARA O BANCO*/
    //TESTANTO    TESTANDO    TESTANDO    TESTANDO    TESTANDO
    //CONSTANTES DE ACESSO AO SHAREDPREFRENCES
    public static final String PREF_KEY = "pref_key";
    public static final String LATITUDE_KEY = "latitude_key";
    public static final String LONGITUDE_KEY = "longitude_key";
    public static final String ALTITUDE_KEY = "altitude_key";

    boolean stopCoord = true;
    boolean stopNotificationIcon = true;

    private Boolean clicou = true;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMapAsync(this);
        //getActivity().startService(new Intent(getActivity(), service.class));

/* ESSE CODIGO PODERÁ SER DESCOMENTADO PARA ENVIAR COORDENADAS PARA O BANCO
        //TESTANTO    TESTANDO    TESTANDO    TESTANDO    TESTANDO
        EventBus.getDefault().register(this);
*/


        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.fragmento_mapa);
        mapFragment.getMapAsync(this);

        tv_coord_lat = (TextView) getActivity().findViewById(R.id.tv_c_lat);
        tv_coord_lng = (TextView) getActivity().findViewById(R.id.tv_c_lng);
        switchStatus = (TextView) getActivity().findViewById(R.id.switchStatus);
        status_binario = (TextView) getActivity().findViewById(R.id.status_binario);
        tv_situacao = (TextView) getActivity().findViewById(R.id.tv_situacao);
        token_id = (TextView) getActivity().findViewById(R.id.token_id);
        //mySwitch = (Switch) getActivity().findViewById(R.id.mySwitch);

        tv_Notification = (TextView) getActivity().findViewById(R.id.tv_Notification);
        //Conectado(getContext());

        //progresso_status = (ProgressBar) getActivity().findViewById(R.id.progresso_status);

        tv_nome = (TextView) getActivity().findViewById(R.id.tv_nome);
        tv_email = (TextView) getActivity().findViewById(R.id.tv_email);
        tv_id = (TextView) getActivity().findViewById(R.id.tv_id);

        Intent i = getActivity().getIntent();
        String id = i.getStringExtra("ID_UNI");
        String Nome = i.getStringExtra("Nome");
        String Email = i.getStringExtra("Email");
        String Notificacao = i.getStringExtra("mesagem");

        tv_id.setText(id);
        tv_nome.setText(Nome);
        tv_email.setText(Email);
        tv_Notification.setText(Notificacao);


        pDialog = ProgressDialog.show(getActivity(), "", "Carregando localização atual...");
        pDialog.setCancelable(false);
        MostraStatusdoTaxista(tv_id.getText().toString());
        //atualizaToken();



        //ed_origem = (EditText) getActivity().findViewById(R.id.ed_origem);
        //btn_livre = (ImageButton) getActivity().findViewById(R.id.btn_livre);
        //btn_ocupado = (ImageButton) getActivity().findViewById(R.id.btn_ocupado);
        btn_ficarocupado = (Button) getActivity().findViewById(R.id.btn_ficarocupado);
        tv_status = (TextView) getActivity().findViewById(R.id.tv_status);
       // txtmetro = (TextView) getActivity().findViewById(R.id.txtmetro);
        //progress = (ProgressBar) getActivity().findViewById(R.id.progress);

        SharedPreferences preference = getActivity().getSharedPreferences("LetsGO", MODE_PRIVATE);
        switchStatus.setText(preference.getString("Status", "LIVRE"));

        btn_ficarocupado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                if (!clicou){
                    btn_ficarocupado.setText("FICAR OCUPADO");
                    btn_ficarocupado.setBackgroundColor(Color.parseColor("#be0300"));
                    clicou = true;

                    //EVENTOS DO BOTÃO OCUPADO
                    switchStatus.setText("LIVRE");

                    //EVENTOS DO BOTÃO LIVRE
                    pd = ProgressDialog.show(getActivity(), "", "Atualizando status LIVRE...");
                    pd.setCancelable(true);

                    status_binario.setText("1");
                    editaDados(tv_id.getText().toString());

                    stopCoord = false;
                    enviarCoordenadas();
                    alarmSituacaoLiv();

                    //ExtrairTask et = new ExtrairTask();
                    //et.execute();

                    SharedPreferences preference = getActivity().getSharedPreferences("LetsGO", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preference.edit();
                    editor.putString("Status", switchStatus.getText().toString());
                    editor.commit();

                }
                else {

                    btn_ficarocupado.setText("FICAR LIVRE");
                    btn_ficarocupado.setBackgroundColor(Color.parseColor("#00ab1f"));
                    clicou = false;

                    switchStatus.setText("OCUPADO");

                    pd = ProgressDialog.show(getActivity(), "", "Atualizando status OCUPADO...");
                    pd.setCancelable(true);

                    status_binario.setText("0");
                    editaDados(tv_id.getText().toString());

                    stopCoord = true; //MUDEI PARA TRUE, ERA FALSE

                    alarmSituacaoOcup();

                    //ARMAZENA NO SHAREDPREF
                    SharedPreferences preference = getActivity().getSharedPreferences("LetsGO", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preference.edit();
                    editor.putString("Status", switchStatus.getText().toString());
                    editor.apply();

                }





                //testeButton();
/*
                //CHAMA O XML DIALOG_FIN
                View mView = getLayoutInflater(savedInstanceState).inflate(R.layout.dialog_ficar_ocupado, null);

                //Button btn_cancelar = (Button) mView.findViewById(R.id.btn_cancelar);
                Button btn_ocupado = (Button) mView.findViewById(R.id.btn_ocupado);
                //Button btn_passageiroabordo = (Button) mView.findViewById(R.id.btn_passageiroabordo);

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                //mBuilder.setIcon(R.drawable.logomarcalets).setTitle("ESCOLHA");
                mBuilder.setCancelable(false);

                btn_ocupado.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        switchStatus.setText("OCUPADO");

                        pd = ProgressDialog.show(getActivity(), "", "Atualizando status OCUPADO...");
                        status_binario.setText("0");
                        editaDados(tv_id.getText().toString());

                        stopCoord = true; //MUDEI PARA TRUE, ERA FALSE

                        alarmSituacaoOcup();

                        mBuilder.setNegativeButton("", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
*/
                        //========================

                        //SharedPreferences.Editor editor = getActivity().getSharedPreferences("android.switch", MODE_PRIVATE).edit();
                        //editor.putBoolean("Status", true);
                        //editor.commit();


            /* ESSE CODIGO PODERÁ SER DESCOMENTADO PARA ENVIAR COORDENADAS PARA O BANCO
                        //PARA O ENVIO DE COORDENADAS PARA O BANCO DO USUARIO
                        PararEnvioDeCoord();
            */
/*
                        SharedPreferences preference = getActivity().getSharedPreferences("LetsGO", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preference.edit();
                        editor.putString("Status", switchStatus.getText().toString());
                        editor.apply();


                        //CHAMA O XML DIALOG_FIN
                        final View mView = getLayoutInflater(savedInstanceState).inflate(R.layout.dialog_ficarlivre, null);

                        final Button btn_livre = (Button) mView.findViewById(R.id.btn_livre);

                        final AlertDialog.Builder Builder = new AlertDialog.Builder(getActivity());
                        Builder.setCancelable(false);
                        //btn_livre.setEnabled(false);

                        btn_livre.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

*/
                        /* ESSE CODIGO PODERÁ SER DESCOMENTADO PARA ENVIAR COORDENADAS PARA O BANCO
                                //ENVIA COORDENADAS PARA O BANCO DO USUARIO
                                IniciaEnvioDeCoord();
                         */
/*
                                pd = ProgressDialog.show(getActivity(), "", "Atualizando status LIVRE...");
                                status_binario.setText("1");
                                editaDados(tv_id.getText().toString());
                                stopCoord = false;
                                enviarCoordenadas();
                                alarmSituacaoLiv();

                                //ExtrairTask et = new ExtrairTask();
                                //et.execute();

                                SharedPreferences preference = getActivity().getSharedPreferences("LetsGO", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preference.edit();
                                editor.putString("Status", switchStatus.getText().toString());
                                editor.commit();
                                switchStatus.setText("LIVRE");

                            }
                        });

                        Builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        Builder.setView(mView);
                        AlertDialog dialog = Builder.create();
                        dialog.show();

                    }
                });

                mBuilder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
*/
            }
        });

/*
        Intent tarefaIntent = new Intent(getActivity(), ExecutarTarefaa.class);
        PendingIntent p = PendingIntent.getBroadcast(getActivity(), 0, tarefaIntent, 0);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 3);

        AlarmManager alarme = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        alarme.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 3000, p);
*/

    }


    /**
     * AQUI O MÉTODO QUE FAZ A AÇÃO PARA MUDAR O TEXTO
     */
    public void testeButton() {

        if (!clicou){
            btn_ficarocupado.setText("FICAR OCUPADO");
            btn_ficarocupado.setBackgroundColor(Color.parseColor("#be0300"));
            clicou = true;

            //EVENTOS DO BOTÃO OCUPADO
            switchStatus.setText("LIVRE");

            pd = ProgressDialog.show(getActivity(), "", "Atualizando status OCUPADO...");
            pd.setCancelable(true);

            status_binario.setText("0");
            editaDados(tv_id.getText().toString());

            stopCoord = true; //MUDEI PARA TRUE, ERA FALSE

            alarmSituacaoOcup();

            //ARMAZENA NO SHAREDPREF
            SharedPreferences preference = getActivity().getSharedPreferences("LetsGO", MODE_PRIVATE);
            SharedPreferences.Editor editor = preference.edit();
            editor.putString("Status", switchStatus.getText().toString());
            editor.apply();


        }
        else {
            btn_ficarocupado.setText("FICAR LIVRE");
            btn_ficarocupado.setBackgroundColor(Color.parseColor("#00ab1f"));
            clicou = false;

            //EVENTOS DO BOTÃO LIVRE
            pd = ProgressDialog.show(getActivity(), "", "Atualizando status LIVRE...");
            status_binario.setText("1");
            editaDados(tv_id.getText().toString());
            stopCoord = false;
            enviarCoordenadas();
            alarmSituacaoLiv();

            //ExtrairTask et = new ExtrairTask();
            //et.execute();

            SharedPreferences preference = getActivity().getSharedPreferences("LetsGO", MODE_PRIVATE);
            SharedPreferences.Editor editor = preference.edit();
            editor.putString("Status", switchStatus.getText().toString());
            editor.commit();
            switchStatus.setText("OCUPADO");


        }
    }



    //String BASE_URL = "http://www.letsgosobral.esy.es/taxi/status_Taxista/updateData.php";
    //TROCADO POR
    String BASE_URL = "http://www.letsgosobral.com/taxi/status_Taxista/updateData.php";

    public void editaDados(String taxi_id_unic) {

        final RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL) //Setting the Root URL
                .build();

        AppConfig.atualizaStatus api = adapter.create(AppConfig.atualizaStatus.class);

        api.atualizaDados(
                taxi_id_unic,
                Double.parseDouble(status_binario.getText().toString()),

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

                                Toast.makeText(getActivity(), "Status ativo!", Toast.LENGTH_SHORT).show();
                                pd.dismiss();

                            } else {
                                Toast.makeText(getActivity(), "Atualização de status falhou!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "Verifique sua conexão!", Toast.LENGTH_LONG).show();
                        pd.cancel();
                    }
                }
        );

    }

    //============================== LIVRE - OCUPADO ==============================

    //================ MOSTRA ICONE DE NOTIFICAÇÃO POR TEMPOS =====================
    public void mostraIconeDeStatusLivre(){
        boolean alarmeAtivo = (PendingIntent.getBroadcast(getActivity(), 0, new Intent("ALARME_DISPARADO"), PendingIntent.FLAG_NO_CREATE) == null);

        if(alarmeAtivo){
            Log.i("Script", "Novo alarme");

            Intent intent = new Intent("ALARME_DISPARADO");
            PendingIntent p = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            c.add(Calendar.SECOND, 3);

            AlarmManager alarme = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
            alarme.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 3000, p);
        }
        else{
            Log.i("Script", "Alarme já ativo");
        }
    }
    //=============================================================================

    //================ MOSTRA ICONE DE NOTIFICAÇÃO POR TEMPOS =====================
    public void mostraIconeDeStatusOcup(){
        boolean alarmeAtivo = (PendingIntent.getBroadcast(getActivity(), 0, new Intent("ALARME_DISPARADO_OCUP"), PendingIntent.FLAG_NO_CREATE) == null);

        if(alarmeAtivo){
            Log.i("Script", "Novo alarme");

            Intent intent = new Intent("ALARME_DISPARADO_OCUP");
            PendingIntent p = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            c.add(Calendar.SECOND, 3);

            AlarmManager alarme = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
            alarme.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 3000, p);
        }
        else{
            Log.i("Script", "Alarme já ativo");
        }
    }



    public class ExecutarTarefaa extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //      Código a executar
            Log.i("Script", "-> DeuCerto");
        }
    }




/*
    @Override
    public void onDestroy(){
        super.onDestroy();

		Intent intent = new Intent("ALARME_DISPARADO");
		PendingIntent p = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);

		AlarmManager alarme = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
		alarme.cancel(p);
    }
*/
    //=============================================================================

    public void situacaoID(){

        String tvSituacao = tv_situacao.getText().toString();

        switch (tvSituacao) {
            case "1":
                alarmSituacaoLiv();
                switchStatus.setText("LIVRE");
                clicou = true;
                btn_ficarocupado.setText("FICAR OCUPADO");
                btn_ficarocupado.setBackgroundColor(Color.parseColor("#be0300"));
                break;
            case "0":
                alarmSituacaoOcup();
                switchStatus.setText("OCUPADO");
                clicou = false;
                btn_ficarocupado.setText("FICAR LIVRE");
                btn_ficarocupado.setBackgroundColor(Color.parseColor("#00ab1f"));
                break;
        }

    }

    //MOSTRA STATUS NA BARRA DE NOTIFICAÇÃO =================
    public void alarmSituacaoOcup(){
        stopNotificationIcon = true;
        //========================
        while (stopNotificationIcon) {
            mostraIconeDeStatusOcup();
            Intent intent = new Intent("ALARME_DISPARADO_OCUP");
            PendingIntent p = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            c.add(Calendar.SECOND, 3);

            AlarmManager alarme = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
            alarme.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 3000, p);
            break;
        }

        Intent intenti = new Intent("ALARME_DISPARADO");
        PendingIntent pii = PendingIntent.getBroadcast(getActivity(), 0, intenti, 0);

        AlarmManager alarmee = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        alarmee.cancel(pii);
    }

    public void alarmSituacaoLiv(){
        stopNotificationIcon = false;
        //====================
        while (!stopNotificationIcon) {
            mostraIconeDeStatusLivre();
            Intent intent = new Intent("ALARME_DISPARADO");
            PendingIntent p = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            c.add(Calendar.SECOND, 3);

            AlarmManager alarme = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
            alarme.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 3000, p);
            break;
        }

        Intent intents = new Intent("ALARME_DISPARADO_OCUP");
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, intents, 0);

        AlarmManager alarmes = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        alarmes.cancel(pi);
    }
    //=======================================================

/*
    public void atualizaToken(){

        SharedPreferences pref = getActivity().getSharedPreferences(Config.SHARED_PREF, 0);
        String token_id_ = pref.getString("regId", null);

        token_id.setText(token_id_);
        atualizaToken(tv_id.getText().toString());
        Log.i("token", "Sim");
    }
*/
    //=================BUSCA A LISTA DE ENDEREÇOS POSSÍVEIS NA COORDENADA POSICIONADA======================

    public android.location.Address buscaEndereco(double latitude, double longitude)throws IOException{
        Geocoder geocoder;
        android.location.Address address = null;
        List<android.location.Address> addresses;

        geocoder = new Geocoder(getContext());

        addresses = geocoder.getFromLocation(latitude, longitude, 1);

        if (addresses.size() > 0){
            address = addresses.get(0);
        }
        return address;
    }


    //==============================VERIFICA SE ESTÁ CONECTADO A INTERNET=======================================
/*
    public static boolean Conectado(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final Handler handler = new Handler();
            String LogSync = null;
            String LogToUserTitle = null;
            if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) {
                LogSync += "\nConectado a Internet 3G ";
                LogToUserTitle += "Conectado a Internet 3G ";
                handler.sendEmptyMessage(0);

                Toast.makeText(context, "você está conectado a rede 3G", Toast.LENGTH_LONG).show();

                Log.d(TAG,"Status de conexão 3G: "+cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected());
                return true;
            } else if(cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()){
                LogSync += "\nConectado a Internet WIFI ";
                LogToUserTitle += "Conectado a Internet WIFI ";
                handler.sendEmptyMessage(0);

                Toast.makeText(context, "você está conectado a rede WiFi", Toast.LENGTH_LONG).show();

                Log.d(TAG,"Status de conexão Wifi: "+cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());
                return true;
            } else {
                LogSync += "\nNão possui conexão com a internet ";
                LogToUserTitle += "Não possui conexão com a internet ";
                handler.sendEmptyMessage(0);
                Log.e(TAG,"Status de conexão Wifi: "+cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());

                //Crouton.makeText((Activity) context, "você está sem conexão! É necessário conectar seu dispositivo.", Style.ALERT)
                  //      .setConfiguration(new Configuration.Builder().setDuration(2000).build()).show();

                Toast.makeText(context, "você está sem conexão! É necessário conectar seu dispositivo.", Toast.LENGTH_LONG).show();

                Log.e(TAG,"Status de conexão 3G: "+cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected());

                //Crouton.makeText((Activity)context, "você está sem conexão! É necessário conectar seu dispositivo.", Style.ALERT).show();

                Toast.makeText(context, "você está sem conexão! É necessário conectar seu dispositivo.", Toast.LENGTH_LONG).show();

                return false;
            }
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
            return false;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //Crouton.cancelAllCroutons();
    }
*/


    //====================================POSICIONAMENTO DO GPS=========================================
    @Override
    public void onResume() {    //Executa sempre que meu fragmento está visível...
        super.onResume();
        //Ativa o GPS

        allowNetwork = true;
        locationmanager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locListener);
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locListener); //Aqui é This ao invés locListener

    }

    @Override
    public void onPause() {
        super.onPause();
        //Entra no modo de pausa, para economizar espaço na memoria, bateria...
        //locationmanager.removeUpdates(locListener);
        locationmanager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationmanager.removeUpdates(locListener);
    }
    //==================================================================================================

    /**
     * Manipula o mapa assim que estiverem disponíveis.
     * Este retorno é acionado quando o mapa está pronto para ser utilizado.
     * Este é o lugar onde podemos adicionar marcadores ou linhas, adicione ouvintes ou mover a câmera. Nesse caso,
     * Nós apenas adicionar um marcador perto de Sydney, Austrália.
     * Se os serviços do Google Play não está instalado no dispositivo, o usuário será solicitado a instalar
     * -Lo dentro do SupportMapFragment. Este método só será acionado quando o usuário tem
     * Instalados serviços do Google Play e voltou para o aplicativo.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        meulocal();

/* ESSE CODIGO PODERÁ SER DESCOMENTADO PARA ENVIAR COORDENADAS PARA O BANCO
        //TESTANTO    TESTANDO    TESTANDO    TESTANDO    TESTANDO
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        double latitude = Double.parseDouble( ProviderFragmentV2.getInSharedPreferences(getActivity(), LATITUDE_KEY, "-3.685668") );
        double longitude = Double.parseDouble( ProviderFragmentV2.getInSharedPreferences(getActivity(), LONGITUDE_KEY, "-40.344284") );
        LatLng latLng = new LatLng(latitude, longitude);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17).tilt(90).build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(update);

        customAddMarker(latLng, "Marcador 1", "O Marcador 1 foi reposicionado");
        //TESTANTO    TESTANDO    TESTANDO    TESTANDO    TESTANDO
*/

        mMap.setOnMapClickListener(this);
        //mMap.getUiSettings().setZoomControlsEnabled(true);

    }


/* //ESSE CODIGO PODERÁ SER DESCOMENTADO PARA ENVIAR COORDENADAS PARA O BANCO
    //TESTANTO    TESTANDO    TESTANDO    TESTANDO    TESTANDO ATÉ O MÉTODO getInSharedPreferences()
    public void customAddMarker(LatLng latLng, String title, String snippet){
        MarkerOptions options = new MarkerOptions();
        options.position(latLng).title(title).snippet(snippet).draggable(false);
        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.taxi2));

        marcador = mMap.addMarker(options);
    }

    private void updatePosition(LatLng latLng){
        mMap.animateCamera(CameraUpdateFactory.newLatLng( latLng ));
        marcador.setPosition( latLng );
    }

    // LISTENERS
    // EVENTOS DOS BOT
    public void IniciaEnvioDeCoord(){
        ComponentName cp = new ComponentName(getActivity(), JobSchedulerService.class);

        JobInfo jb = new JobInfo.Builder(1, cp)
                .setBackoffCriteria(4000, JobInfo.BACKOFF_POLICY_LINEAR)
                .setPersisted(true)
                .setPeriodic(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();

        JobScheduler js = JobScheduler.getInstance(getActivity());
        js.schedule(jb);
    }

    public void PararEnvioDeCoord(){
        JobScheduler js = JobScheduler.getInstance(getActivity());
        js.cancelAll();
    }

    public void onEvent(MessageEB m){
        if( m.getClassName().equalsIgnoreCase( TrackingActivity.class.getName() ) ) {
            LatLng latLng = new LatLng(m.getLocation().getLatitude(), m.getLocation().getLongitude());
            updatePosition( latLng );
        }
    }

*/


    //======================================CÓDIGO ATUAL============================================
    //MARCADOR DA MINHA POSIÇÃO!!!
    private void agregadormarcador(final double lat, final double lng) {
        final LatLng coordenadas = new LatLng(lat, lng);
        final CameraUpdate meulocal = CameraUpdateFactory.newLatLngZoom(coordenadas, 17);
        if (marcador != null)
            marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions().position(coordenadas).title("Minha localização atual").
                icon(BitmapDescriptorFactory.fromResource(R.mipmap.icontaxi))); //Essas são propriedades de titulo e imagem ao nosso marcador.
        mMap.animateCamera(meulocal);   //e Aqui agregamos uma camera animada para se lcomover a nossa posição.
/*
        //==============================================MARCADORES=====================================
        mMap.addMarker(new MarkerOptions().position(new LatLng(-3.676316, -40.340138)).title("Universidade Estadual Vale do Acaraú").icon(BitmapDescriptorFactory.fromResource(R.mipmap.universidade)));
*/
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                //if (arg0.getTitle().equals("MyHome")) // if marker source is clicked
                  //  Toast.makeText(getActivity(), arg0.getTitle(), 1000).show();// display toast

                if (arg0.getTitle().equals("Minha localização atual")) {
                    //Toast.makeText(getActivity(), arg0.getTitle(), 1000).show();// display toast

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    //define o titulo
                    //builder.setTitle("TESTE");
                    //define a mensagem
                    builder.setMessage("Meu endereço atual");
                    //define um botão como positivo
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                            try{
                                endereco = buscaEndereco(lat, lng);
                                //ed_origem.setText(endereco.getAddressLine(0));
                                Toast.makeText(getActivity(), endereco.getAddressLine(0), Toast.LENGTH_SHORT).show();
                            }catch (IOException e){
                                Log.i("GPS", e.getMessage());
                            }

                        }
                    });
                    //define um botão como negativo.
                    /*builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            //Toast.makeText(getContext(), "Você ainda não está Ocupado", Toast.LENGTH_SHORT).show();// + arg1, Toast.LENGTH_SHORT).show();
                            Crouton.makeText(getActivity(), "Está tudo Ok!.", Style.CONFIRM).show();
                        }
                    });*/
                    //cria o AlertDialog
                    alerta = builder.create();
                    //Exibe
                    alerta.show();
                }

                //if(arg0.getTitle().equals("MapleBear Head Office")) // if marker source is clicked
                  //  Toast.makeText(getActivity(), arg0.getTitle(),1000).show();// display toast

                return true;

            }

        });

    }




    //RESPONSÃVEL PELA ATUALIZAÇÃO DAS COORDENADAS
    private void atualizarlocal(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            agregadormarcador(lat, lng);
            btn_ficarocupado.setEnabled(true);
            pDialog.dismiss();
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



            Double coordenadasLat = lat;
            Double coordenadasLong = lng;
            tv_coord_lat.setText(String.valueOf(coordenadasLat));//"-3.676316");
            tv_coord_lng.setText(String.valueOf(coordenadasLong));//"-40.340138");


            //PEGA AS COORDENADAS EM TV E PEGA NO MÉTODO E TODA VEZ QUE PISCAR TEM
            //UM VALOR DIFERENTE...

            //======================MOSTRA MINHA COORDENADA=====================
            //Toast.makeText(getActivity(), "Coordenadas: \n" + "Lat: " + lat + " , \n" + "Long: " + lng, Toast.LENGTH_SHORT).show();
            /*
            if( location != null ) {
                locs.add(location);


                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Toast.makeText(getActivity(), "Data:" + sdf.format(location.getTime()), Toast.LENGTH_SHORT).show();
            }*/

            //locs.add(location);
            //getDistance(getView());

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            //Toast.makeText(getActivity(), "Status do provider foi alterado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String s) {
            Toast.makeText(getActivity(), "GPS ativado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String s) {
            Toast.makeText(getActivity(), "GPS desativado", Toast.LENGTH_SHORT).show();
            btn_ficarocupado.setEnabled(false);

            //Alert responsável por lembrar que o GPS está desativado
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                            pDialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    };



    //=======================================MINHA LOCALIZAÇÃO ALTERADA A CADA 3 SEGUNDOS====================================
    public void meulocal() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        atualizarlocal(location);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000,0, locListener);
    }

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

    @Override
    public void onMapClick(LatLng latLng) {

    }



    //=============================MÉTODO ENVIA COORDENADAS=================================

    public long enviarCoordenadas() {

/*
        PendingIntent p = PendingIntent.getBroadcast(getActivity(), 0, new Intent(getActivity(), ExecutarTarefaa.class), 0);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 3);

        AlarmManager alarme = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        alarme.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 2000, p);
*/



        final Handler handler = new Handler();      //Faz uma pilha de ações com a ajuda da Thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(4000);
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                // Write your code here to update the UI.
                                //=================================================
                                while (!stopCoord) {

                                    enviaCoordenadas(tv_id.getText().toString());
                                    Log.i("Testando", "enviado");

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

    //=================================================================================================
    //METODO DE ENVIO DE COORDENADAS AQUI....


    //===================== ENVIA COORDENADAS PARA O BANCO DE DADOS ===================================
    String BASE_URL_COORDENADAS = "http://www.letsgosobral.com/taxi/status_Taxista/atualizaCoordenadas.php";
    //String BASE_URL_COORDENADAS = "http://www.letsgosobral.esy.es/taxi/status_Taxista/atualizaCoordenadas.php";

    public void enviaCoordenadas(String taxi_id_unic) {

        final RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL_COORDENADAS) //Setting the Root URL
                .build();

        AppConfig.atualizaCoordenadas api = adapter.create(AppConfig.atualizaCoordenadas.class);

        api.atualizaCoord(
                taxi_id_unic,
                Double.parseDouble(tv_coord_lat.getText().toString()),
                Double.parseDouble(tv_coord_lng.getText().toString()),

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
                                //Toast.makeText(getActivity(), "localização atualizada!", Toast.LENGTH_SHORT).show();
                                //progresso_status.setVisibility(View.INVISIBLE);
                                Log.i("HASHTAG", "Enviada");
                                pd.dismiss();

                            } else {
                                Toast.makeText(getActivity(), "Atualização de localização falhou!", Toast.LENGTH_SHORT).show();
                                //progresso_status.setVisibility(View.INVISIBLE);
                                Log.i("HASHTAG", "Falha");
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
                        Toast.makeText(getActivity(), "Verifique sua conexão!", Toast.LENGTH_LONG).show();
                        //progresso_status.setVisibility(View.INVISIBLE);
                        pd.cancel();
                    }
                }
        );
    }


    //=================================================================================================

    //=-=-=-=-=-=|-=-=-=-=-=-=|PEGA OS DADOS DO USUÁRIO PELO ID_UNICO|=-=-=-=-=-=-=-|=-=-=-=-=-=-=-

    public void MostraStatusdoTaxista(final String taxi_id_unic) {

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

                                    tv_situacao.setText(jo.getString("taxi_situacao"));
                                    situacaoID();

                                    Log.i("situacao", "Situacao carregada");
                                }

                                //details_list.setAdapter(displayAdapter);
                                //pDialog.dismiss();

                            } else {
                                Toast.makeText(getActivity(), "Falha ao atualizar status", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "Verifique sua Conexão! Status inativo!", Toast.LENGTH_LONG).show();
                        //pDialog.cancel();
                    }
                }
        );


    }

}
//======================================CÓDIGO ATUAL============================================