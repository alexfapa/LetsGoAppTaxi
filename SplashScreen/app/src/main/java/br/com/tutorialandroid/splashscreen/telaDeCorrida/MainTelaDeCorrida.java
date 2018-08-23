package br.com.tutorialandroid.splashscreen.telaDeCorrida;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.tutorialandroid.splashscreen.MainNotification;
import br.com.tutorialandroid.splashscreen.MainPayPal;
import br.com.tutorialandroid.splashscreen.R;
import br.com.tutorialandroid.splashscreen.conexao.AppConfig;
import br.com.tutorialandroid.splashscreen.executaAppEmSegundoPlano.service;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainTelaDeCorrida extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, LocationListener {


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

    //====================CRONOMETRO=====================
    Chronometer m_chronometer;
    Button btIniciar, btPausar, btResetar;

    boolean isClickPause = false;
    long tempoQuandoParado = 0;
    long valorzim = 0;
    public TextView valores, tv_lat, tv_long;
    private TextView txtmetro, nome_cliente, usu_pagamento, img_cliente, end_partida, zeraNotif;//,localizacao_destino
    private ImageView img_cli;
    //====================CRONOMETRO=====================

    private Button btnFindPath, btn_iniciarcorrida, btn_finalizar, teste_paypal;

    //==================CONSTANTES TAXIMETRO==============
    boolean finishCorrida = false;

    public static final double CONSTANTE_TAXIMENTRO = 1.284;
    public static final double CONSTANTE_HORA = 20 / 60;
    public static final double CONSTANTE_VALOR_HORA = 1440;
    public static final double CONSTANTE_BANDEIRA = 4.0;
    //====================================================

    private AlertDialog alerta;
    private ProgressDialog progress = null;
    private ProgressDialog pd = null;

    private static final String SERVER_ADDRESS = "http://www.letsgosobral.com/";

    private ExtrairTask et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, service.class));
        setContentView(R.layout.activity_main_tela_de_corrida);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Toolbar toolbardois = (Toolbar) findViewById(R.id.toolbardois);
        setSupportActionBar(toolbardois);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        m_chronometer = (Chronometer) findViewById(R.id.chronometro);
        txtmetro = (TextView) findViewById(R.id.txt_metro);
        valores = (TextView) findViewById(R.id.valor_corrida);
        nome_cliente = (TextView) findViewById(R.id.nome_cliente);
        usu_pagamento = (TextView) findViewById(R.id.usu_pagamento);
        img_cli = (ImageView) findViewById(R.id.img_cli);
        img_cliente = (TextView) findViewById(R.id.img_cliente);
        //localizacao_destino = (TextView) findViewById(R.id.localizacao_destino);
        end_partida = (TextView) findViewById(R.id.end_partida);
        zeraNotif = (TextView) findViewById(R.id.zeraNotif);

        //btn_iniciarcorrida = (Button) findViewById(R.id.btn_iniciarcorrida);
        btn_finalizar = (Button) findViewById(R.id.btn_finalizarc);

        progress = ProgressDialog.show(MainTelaDeCorrida.this, "", "Aguarde...");
        progress.setCancelable(true);

        //INFORMAÇÕES DO CLIENTE NA ABA SUPERIOR=========================
        String nome_do_cliente = getIntent().getStringExtra("nome_usu");
        nome_cliente.setText(nome_do_cliente);

        String forma_de_pagamento = getIntent().getStringExtra("forma_pagamento");
        switch (forma_de_pagamento) {
            case "1":
                usu_pagamento.setText("Pagamento á vista");

                break;
            case "2":
                usu_pagamento.setText("Pagamento no Cartão");

                break;
            case "3":
                usu_pagamento.setText("Pagamento na Conta Let's GO");
                break;
        }

        String image_cliente = getIntent().getStringExtra("caminho_img");
        img_cliente.setText(image_cliente);

        //String destino_cliente = getIntent().getStringExtra("destino_cli");
        //localizacao_destino.setText(destino_cliente);

        String end_partida_cli = getIntent().getStringExtra("origem_cli");
        end_partida.setText(end_partida_cli);

        final String id_usu = getIntent().getStringExtra("id_usu");
        final String id_taxista = getIntent().getStringExtra("id_taxista");

        //================================================================


        //O USEI UMA ASYNCTASK PARA EXECUTAR A CHAMADA DA IMAGEM
        et = new ExtrairTask();
        et.execute();

        //==============================================CRONOMETRO=============================================================
        IniciarCorrida();

        //Botão RESETAR
        btn_finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainTelaDeCorrida.this);
                //define o titulo
                builder.setTitle("FINALIZE...");
                //define a mensagem
                builder.setMessage("Deseja realmente finalizar a corrida?");
                //define um botão como positivo
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        m_chronometer.stop();       //é chamado para parar contagem em segundos a partir da base ajustada.
                        m_chronometer.setText(" 00:00 ");  //é chamado para resetar o tempo exibido na tela/label de contagem em segundos.
                        tempoQuandoParado = 0;   //essa variável é zerada para que a lógica do botão resetar funcione corretamente sem gerar problemas e continuar o tempo quando o usuário clicar em "Iniciar" ou "Pausar > Iniciar".
                        finishCorrida = true;

                        //Intent pagamentoPayPal = new Intent(MainTelaDeCorrida.this, MainPayPal.class);
                        //startActivity(pagamentoPayPal);
                        //finish();

                        Intent valorreal = new Intent(MainTelaDeCorrida.this, MainPayPal.class);
                        valorreal.putExtra("VCorrida", valores.getText().toString());
                        valorreal.putExtra("nome_do_cliente", nome_cliente.getText().toString());
                        valorreal.putExtra("ponto_partida_cli", end_partida.getText().toString());
                        //valorreal.putExtra("destino_cli", localizacao_destino.getText().toString());
                        valorreal.putExtra("pagamento_forma", usu_pagamento.getText().toString());
                        valorreal.putExtra("id_usu", id_usu);
                        valorreal.putExtra("id_taxista", id_taxista);
                        startActivity(valorreal);
                        finish();

                        pd = ProgressDialog.show(MainTelaDeCorrida.this, "", "Aguarde...");
                        zeraNotif.setText("0");
                        botaoZeraNotif(id_taxista);

                    }
                });
                //define um botão como negativo.
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(MainTelaDeCorrida.this, "Continuando corrida...", Toast.LENGTH_SHORT).show();// + arg1, Toast.LENGTH_SHORT).show();
                    }
                });
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();
            }


/*
                //CHAMA O XML DIALOG_FIN
                View mView = getLayoutInflater().inflate(R.layout.dialog_fin, null);

                final EditText mVCobrado = (EditText) mView.findViewById(R.id.et_VCobrado);
                final EditText valorcobrado = (EditText) mView.findViewById(R.id.editTextAmount);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsFragment.this);
                mBuilder.setIcon(R.drawable.logomarcalets).setTitle("PAGAMENTO");

                //mBuilder.setCancelable(false);
                final TextView mValorCorrida = (TextView) mView.findViewById(R.id.txt_VC);
                Button mCobrar = (Button) mView.findViewById(R.id.btn_cobrar);
                Button pagarcompaypal = (Button) mView.findViewById(R.id.buttonPay);

                mValorCorrida.setText(valores.getText());

                pagarcompaypal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Aqui diz --> Se o campo EditText for diferente de vazio, chamar dialog_cli, senão mandar preencher campo!
                        if (!valorcobrado.getText().toString().isEmpty()) {
*/
                            /*
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsFragment.this);
                            mBuilder.setCancelable(false);
                            mBuilder.setIcon(R.drawable.logomarcalets).setTitle("PAGAMENTO");
                            final View mView = getLayoutInflater().inflate(R.layout.dialog_cli, null);

                            final TextView txt_Corridareal = (TextView) mView.findViewById(R.id.txt_Corridareal);
                            final EditText et_CodCli = (EditText) mView.findViewById(R.id.et_CodCli);
                            Button BotaoConfirma = (Button) mView.findViewById(R.id.btn_confirma);

                            txt_Corridareal.setText("R$ " + mVCobrado.getText());

                            BotaoConfirma.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (et_CodCli.getText().toString().isEmpty()) {

                                        Toast.makeText(MapsFragment.this, "Cliente! Digite seu código.", Toast.LENGTH_LONG).show();
                                        et_CodCli.setError("Por favor, preencha este campo!");

                                    } else {

                                        Intent mpas = new Intent(MapsFragment.this, MainMapActivity.class);
                                        mpas.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(mpas);
                                        finish();

                                        Toast.makeText(MapsFragment.this, "Pagamento relaizado com sucesso! \n O dinheiro será depositado em sua conta em até 48 Horas.", Toast.LENGTH_LONG).show();
                                    }
                                }

                            });

                            mBuilder.setView(mView);
                            AlertDialog dialog = mBuilder.create();
                            dialog.show();
                            */
                            /*
                        } else {
                            valorcobrado.setError("Por favor, preencha este campo!");
                        }
                    }

                });

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
                */

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
            pDialog = new ProgressDialog(MainTelaDeCorrida.this);
            pDialog.setMessage("Aguarde...");
            pDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... params) {

            new PegaImageCli(img_cliente.getText().toString()).execute();

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



    public void IniciarCorrida(){

/*        AlertDialog.Builder builder = new AlertDialog.Builder(MainTelaDeCorrida.this);
        //define o titulo
        builder.setTitle("INICIAR CORRIDA...");
        //define a mensagem
        builder.setMessage("Seu passageiro está a bordo? inicie uma corrida agora?");
        //define um botão como positivo
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
*/
                if (isClickPause) {
                    m_chronometer.setBase(SystemClock.elapsedRealtime() + tempoQuandoParado);  //Retorna os mulisegundos desde boot do sistema,
                    m_chronometer.start();   //Contagem em segundos         // incluindo o tempo gasto dormindo.
                    tempoQuandoParado = 0;
                    isClickPause = false;
                } else {
                    m_chronometer.setBase(SystemClock.elapsedRealtime());
                    m_chronometer.start();
                    tempoQuandoParado = 0;
                }

                //MÉTODO PISCAR!!!
                piscar();   //MÉTODO QUE MUDA O VALOR DO PREÇO
                getDistance();  //MÉTODO DA DISTÂNCIA
/*
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(MainTelaDeCorrida.this, "Em pausa, até que você inicie uma corrida...", Toast.LENGTH_SHORT).show();// + arg1, Toast.LENGTH_SHORT).show();
            }
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();
*/
    }


    //=============================MÉTODO MUDA VALOR DO TAXIMETRO=================================

    private long piscar() {
        final Handler handler = new Handler();      //Faz uma pilha de ações com a ajuda da Thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(1000);   //A cada 10 segundos muda valor!!!
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                // Write your code here to update the UI.

                                long tempoTranscorrido = (SystemClock.elapsedRealtime() - m_chronometer.getBase()) / 156000;   //3600000
                                //long cronoparado = m_chronometer.getBase() - SystemClock.elapsedRealtime();

                                //double real = (double) pegavalor;

                                //=================================================
                                while (!finishCorrida) {
                                    getDistance();

                                    double distanciaPercorrida = getDistance() / 9000;
                                    double kminicial = 0;

                                    //calculo da tarifa 1
                                    double result = (distanciaPercorrida - kminicial) * CONSTANTE_TAXIMENTRO +
                                            (tempoTranscorrido * CONSTANTE_HORA * CONSTANTE_VALOR_HORA +
                                                    (tempoTranscorrido + CONSTANTE_BANDEIRA));

                                    DecimalFormat dft = new DecimalFormat("0.##");   //CONVERTE PARA FORMATO DECIMAL COM DUAS CASAS!!!
                                    String converte = dft.format(result);

                                    //valores.setVisibility(View.INVISIBLE);
                                    //valores.setText("R$ " + converte + "\n");
                                    valores.setText(converte);
                                    //valores.append("R$ " + (converte) + "\n");
                                    //valores.refreshDrawableState();

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





    //=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-PEGA DISTÂNCIA DADA PELO GPS E CONVERTE EM RADIANOS=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
    public double getDistance() {
        double distance = 0;

        //DecimalFormat df = new DecimalFormat("0.##");   //CONVERTE PARA FORMATO DECIMAL COM DUAS CASAS!!!
        //String dx = df.format(distance);

        for (int i = 0, tam = locs.size(); i < tam; i++) {
            if (i < tam - 1) {
                distance += distance(locs.get(i), locs.get(i + 1));    //Era assim:distance += distance(locs.get(i), locs.get(i + 1));
            }
        }

        DecimalFormat df = new DecimalFormat("0.##");   //CONVERTE PARA FORMATO DECIMAL COM DUAS CASAS!!!
        String dx = df.format(distance);
        txtmetro.setText(dx + " m");

        //Toast.makeText(MapsFragment.this, "Distancia: " + distance + " metros", Toast.LENGTH_LONG).show();
        return distance;
    }


    public static double distance(Location StartP, Location EndP) {
        double lat1 = StartP.getLatitude();
        double lat2 = EndP.getLatitude();
        double lon1 = StartP.getLongitude();
        double lon2 = EndP.getLongitude();
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) *
                Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return 6366000 * c;
    }




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

            locs.add(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
            Toast.makeText(MainTelaDeCorrida.this, "GPS ativado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String s) {
            Toast.makeText(MainTelaDeCorrida.this, "GPS desativado", Toast.LENGTH_SHORT).show();

            //Alert responsável por lembrar que o GPS está desativado
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainTelaDeCorrida.this);
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
        meulocal();

        mMap.setOnMapClickListener(this);
        //mMap.getUiSettings().setZoomControlsEnabled(true);
    }


    //MARCADOR DA MINHA POSIÇÃO!!!
    private void agregadormarcador(final double lat, final double lng) {
        final LatLng coordenadas = new LatLng(lat, lng);
        final CameraUpdate meulocal = CameraUpdateFactory.newLatLngZoom(coordenadas, 17);
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainTelaDeCorrida.this);
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
                                Toast.makeText(MainTelaDeCorrida.this, "Aqui: " + endereco.getAddressLine(0), Toast.LENGTH_SHORT).show();
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
                            Crouton.makeText(MainTelaDeCorrida.this, "Está tudo Ok!.", Style.CONFIRM).show();
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

    }


    //RESPONSÃVEL PELA ATUALIZAÇÃO DAS COORDENADAS
    private void atualizarlocal(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            agregadormarcador(lat, lng);
        }
    }



    //=================BUSCA A LISTA DE ENDEREÇOS POSSÍVEIS NA COORDENADA POSICIONADA======================

    public android.location.Address buscaEndereco(double latitude, double longitude)throws IOException{
        Geocoder geocoder;
        android.location.Address address = null;
        List<android.location.Address> addresses;

        geocoder = new Geocoder(MainTelaDeCorrida.this);

        addresses = geocoder.getFromLocation(latitude, longitude, 1);

        if (addresses.size() > 0){
            address = addresses.get(0);
        }
        return address;
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


    String URL_NOTIF = "http://www.letsgosobral.com/taxi/status_Taxista/taxiNotificacao.php";
    public void botaoZeraNotif(final String taxi_id) {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(URL_NOTIF) //Setting the Root URL
                .build();

        AppConfig.aceiteCorrida api = adapter.create(AppConfig.aceiteCorrida.class);

        api.corridaaceite(
                taxi_id,
                Double.parseDouble(zeraNotif.getText().toString()),

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
                                Toast.makeText(MainTelaDeCorrida.this, "Pronto para buscar passageiro!", Toast.LENGTH_SHORT).show();
                                pd.dismiss();

                            } else {
                                Toast.makeText(MainTelaDeCorrida.this, "Verifique sua conexão!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainTelaDeCorrida.this, "Verifique sua conexão", Toast.LENGTH_SHORT).show();
                        pd.cancel();
                    }
                }
        );

    }
    //=============================================================================


    //========================PEGA A IMAGEM DE ACORDO COM ID_UNICO======================
    private class PegaImageCli extends AsyncTask<Void, Void, Bitmap> {
        String image_cli;

        public PegaImageCli(String img_cliente){  //AQUI SERÁ O CAMINHO
            this.image_cli = img_cliente;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            //COMENTADO PARA TESTAR O DE BAIXO
            //String url = SERVER_ADDRESS + "cad_Taxista/imagens_Taxista/foto_perfil/" + nome + ".JPG";
            String url = SERVER_ADDRESS + image_cli;//AQUI EU COLOCO O TEXTVIEW QUE CHAMA O CAMINHO DA IMAGEM

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
                img_cli.setImageBitmap(bitmap);
                progress.cancel();
                //pDialog.dismiss();
            }
        }

    }
    //=============================DA IMAGEM DA PASTA DO BD============================



}
