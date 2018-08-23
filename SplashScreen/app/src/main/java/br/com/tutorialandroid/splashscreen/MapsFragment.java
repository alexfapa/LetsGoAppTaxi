package br.com.tutorialandroid.splashscreen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.tutorialandroid.splashscreen.executaAppEmSegundoPlano.service;

import static android.telephony.PhoneNumberUtils.formatNumber;


public class MapsFragment extends FragmentActivity
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener, LocationListener {
// DirectionFinderListener

    //==================GOOGLE MAPS======================
    private GoogleMap mMap;
    private LocationManager locationmanager;
    private Marker marcador;
    double lat = 0.0;
    double lng = 0.0;
    private boolean allowNetwork;
    //==================GOOGLE MAPS======================

    //====================CRONOMETRO=====================
    Chronometer m_chronometer;
    Button btIniciar, btPausar, btResetar;

    boolean isClickPause = false;
    long tempoQuandoParado = 0;
    long valorzim = 0;
    public TextView valores, tv_lat, tv_long;
    private TextView txtmetro;
    //====================CRONOMETRO=====================

    private Button btnFindPath, btn_iniciarcorrida, btn_finalizar, teste_paypal;
    // private ImageButton ib_livre;

    //========ARRAYLIST RESPONSÁVEL PELA DISTÂNCIA=======
    private List<Location> locs = new ArrayList<Location>();
    private android.location.Address endereco;


    boolean finishCorrida = false;

    public static final double CONSTANTE_TAXIMENTRO = 1.284;
    public static final double CONSTANTE_HORA = 20 / 60;
    public static final double CONSTANTE_VALOR_HORA = 1440;
    public static final double CONSTANTE_BANDEIRA = 4.0;

    public MapsFragment() {
    }

    private AlertDialog alerta;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        startService(new Intent(getBaseContext(), service.class));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        overridePendingTransition(R.anim.activity_entra, R.anim.activity_sai);

        tv_lat = (TextView) findViewById(R.id.tv_coord_lat);
        tv_long = (TextView) findViewById(R.id.tv_coord_long);
        m_chronometer = (Chronometer) findViewById(R.id.chronometer);
        txtmetro = (TextView) findViewById(R.id.txtmetro);
        valores = (TextView) findViewById(R.id.valores);

        //btIniciar = (Button)findViewById(R.id.btIniciar);
        //btPausar = (Button) findViewById(R.id.btPausar);
        //btResetar = (Button)findViewById(R.id.btResetar);
/*
        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        etOrigin = (EditText) findViewById(R.id.etOrigin);
        etDestination = (EditText) findViewById(R.id.etDestination);
*/
        btn_iniciarcorrida = (Button) findViewById(R.id.btn_iniciarcorrida);
        btn_finalizar = (Button) findViewById(R.id.btn_finalizar);
        //teste_paypal = (Button) findViewById(R.id.teste_paypal);
 /*       btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
*/


        btn_iniciarcorrida.setEnabled(true);
        btn_finalizar.setEnabled(false);

//==============================================CRONOMETRO=============================================================

        //Botão INICIAR
        btn_iniciarcorrida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MapsFragment.this);
                //define o titulo
                builder.setTitle("INICIAR CORRIDA...");
                //define a mensagem
                builder.setMessage("Seu passageiro está a bordo? inicie uma corrida agora?");
                //define um botão como positivo
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

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

                        btn_iniciarcorrida.setEnabled(false);
                        btn_finalizar.setEnabled(true);

                        //MÉTODO PISCAR!!!
                        piscar();   //MÉTODO QUE MUDA O VALOR DO PREÇO
                        getDistance();  //MÉTODO DA DISTÂNCIA

                    }
                });
                //define um botão como negativo.
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(MapsFragment.this, "Em pausa, até que você inicie uma corrida...", Toast.LENGTH_SHORT).show();// + arg1, Toast.LENGTH_SHORT).show();
                    }
                });
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();

            }

        });


        //========================================COMENTÁRIO: BOTÃO REMOVIDO
        //Botão PAUSAR
       /* btPausar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isClickPause == false) { //entra para false;
                    tempoQuandoParado = m_chronometer.getBase() - SystemClock.elapsedRealtime(); //O método m_chronometer.getBase() retorna
                }                                                                                //a base que foi ajustada através do método setBase();
                m_chronometer.stop();       //É feita uma conta e o resultado é armazenado em "tempoQuandoParado" para que quando o usuário iniciar
                isClickPause = true;        //a contagem novamente esse valor armazenado seja usado para CONTINUAR A CONTAGEM de onde parou

            }
        });*/

        //Botão RESETAR
        btn_finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MapsFragment.this);
                //define o titulo
                builder.setIcon(R.drawable.sair).setTitle("FINALIZE...");
                //define a mensagem
                builder.setMessage("Deseja realmente finalizar a corrida?");
                //define um botão como positivo
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        m_chronometer.stop();       //é chamado para parar contagem em segundos a partir da base ajustada.
                        m_chronometer.setText(" 00:00 ");  //é chamado para resetar o tempo exibido na tela/label de contagem em segundos.
                        tempoQuandoParado = 0;   //essa variável é zerada para que a lógica do botão resetar funcione corretamente sem gerar problemas e continuar o tempo quando o usuário clicar em "Iniciar" ou "Pausar > Iniciar".
                        finishCorrida = true;

                        btn_iniciarcorrida.setEnabled(true);
                        btn_finalizar.setEnabled(false);

                        Intent pagamentoPayPal = new Intent(MapsFragment.this, MainPayPal.class);
                        startActivity(pagamentoPayPal);
                        finish();

                        Intent valorreal = new Intent(MapsFragment.this, MainPayPal.class);
                        valorreal.putExtra("VCorrida", valores.getText().toString());
                        startActivity(valorreal);
                        finish();

                    }
                });
                //define um botão como negativo.
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(MapsFragment.this, "Continuando corrida...", Toast.LENGTH_SHORT).show();// + arg1, Toast.LENGTH_SHORT).show();
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

                                long tempoTranscorrido = (SystemClock.elapsedRealtime() - m_chronometer.getBase()) / 36000;   //3600000
                                //long cronoparado = m_chronometer.getBase() - SystemClock.elapsedRealtime();

                                //double real = (double) pegavalor;

                                //=================================================
                                while (!finishCorrida) {
                                    getDistance();

                                    double distanciaPercorrida = getDistance() / 550;
                                    double kminicial = 0;

                                    //calculo da tarifa 1
                                    double result = (distanciaPercorrida - kminicial) * CONSTANTE_TAXIMENTRO +
                                            (tempoTranscorrido * CONSTANTE_HORA * CONSTANTE_VALOR_HORA +
                                                    (tempoTranscorrido + CONSTANTE_BANDEIRA));

                                    DecimalFormat dft = new DecimalFormat("0.##");   //CONVERTE PARA FORMATO DECIMAL COM DUAS CASAS!!!
                                    String converte = dft.format(result);

                                    //valores.setVisibility(View.INVISIBLE);
                                    valores.setText("R$ " + converte + "\n");
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


    //====================================POSICIONAMENTO DO GPS=========================================
    @Override
    public void onResume() {    //Executa sempre que meu fragmento está visível...
        super.onResume();
        //Ativa o GPS

        allowNetwork = true;
        locationmanager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener); //Aqui é This ao invés locListener

        //=======================SE O GPS NÃO ESTIVER ATIVADO, ENCAMINHAR PARA CONFIGURAÇÕES!========================

        //===========================================================================================================
    }

    @Override
    public void onPause() {
        super.onPause();
        //Entra no modo de pausa, para economizar espaço na memoria, bateria...
        //locationmanager.removeUpdates((android.location.LocationListener) locListener);
        locationmanager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationmanager.removeUpdates(locListener);

        //==================================================================================================

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        meulocal();

        locs = new ArrayList<Location>();

        mMap.setOnMapClickListener(this);
        //mMap.getUiSettings().setZoomControlsEnabled(true);

    }


    private void agregadormarcador(double lat, double lng) {
        LatLng coordenadas = new LatLng(lat, lng);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(coordenadas).zoom(17).tilt(90).build();
        CameraUpdate meulocal = CameraUpdateFactory.newCameraPosition(cameraPosition);

        if (marcador != null)
            marcador.remove(); //Se o marcador for diferente de null, deverá ser removido
        marcador = mMap.addMarker(new MarkerOptions().position(coordenadas).title("Minha localização atual").
                icon(BitmapDescriptorFactory.fromResource(R.mipmap.taxi2))); //Essas são propriedades de titulo e imagen ao nosso marcador.
        mMap.animateCamera(meulocal);   //e Aqui agregamos uma camera animada para se lcomover a nossa posição.



        //CameraUpdate meulocal = CameraUpdateFactory.newLatLngZoom(coordenadas, 19);


        Intent i = getIntent();
        String coordenada_lat = i.getStringExtra("coordenada_lat_cli");
        String coordenada_long = i.getStringExtra("coordenada_long_cli");

        tv_lat.setText(coordenada_lat);
        tv_long.setText(coordenada_long);

/*
        //Recuperamos a URL passando as cordenadas de origem como sendo a cordenada que definimos
        //para a nossa residência e as coordenadas de destino como sendo a do escritório da Google em SP.
        String url = montarURLRotaMapa(coordenadas.latitude, coordenadas.longitude, Double.valueOf(coordenada_lat), Double.valueOf(coordenada_long));
        //Criamos uma instância de nossa AsyncTask (para cada requisição deverá ser criada uma nova instância).
        MapsFragment.MinhaAsyncTask tarefa = new MapsFragment.MinhaAsyncTask();

        //Se a versão de SDK do Android do aparelho que está executando o aplicativo for menor que a HONEYCOMB (11)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            //Executa a tarefa passando a URL recuperada
            tarefa.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        } else {
            //Executa a tarefa passando a URL recuperada
            tarefa.execute(url);
        }
*/

/*
        LatLng posicaoInicial = new LatLng(lat, lng);
        LatLng posicaiFinal = new LatLng(Double.valueOf(coordenada_lat), Double.valueOf(coordenada_long));

        double distance = SphericalUtil.computeDistanceBetween(posicaoInicial, posicaiFinal);
        //Log.i("LOG","A Distancia é = "+ formatNumber(String.valueOf(distance)));
        Log.e("LOG","A Distancia é = "+ formatNumber(String.valueOf(distance + 300)));
*/
    }

/*
    private String formatNumber(double distance) {
        String unit = "m";
        if (distance 1000) {
            distance /= 1000;
            unit = "km";
        }

        return String.format("%4.3f%s", distance, unit);
    }
*/

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
            Toast.makeText(MapsFragment.this, "GPS ativado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String s) {
            Toast.makeText(MapsFragment.this, "GPS desativado", Toast.LENGTH_SHORT).show();

            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MapsFragment.this);
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

    @Override
    public void onMapClick(LatLng latLng) {

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

        txtmetro.setText(distance + " metros");

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

/*
    public static double CalculationByDistance(Location StartP, Location EndP) {
        int Radius=6371;//radius of earth in Km
        double lat1 = StartP.getLatitude()/1E6;
        double lat2 = EndP.getLatitude()/1E6;
        double lon1 = StartP.getLongitude()/1E6;
        double lon2 = EndP.getLongitude()/1E6;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult= Radius*c;
        double km = valueResult/1;
        return Radius * c;
    }
*/

//================================MÉTODO DE ORIENTAÇÃO GPS==================================
/*
    public String convertDirOrient (Location location) {

        locListener = (LocationListener) location;
        String orientacao = "Nao obtida ";
        if (locListener == null) {
            orientacao = " null ";
        } else if ( locListener.hasBearing()  !=  false ){
            float sentido = locListener.getBearing() ;
            if (sentido >= 0 && sentido <= 10){
                orientacao = "N" ;
            }else if ( sentido >= 11 && sentido <= 33){
                orientacao = "N";
            }

        }

    }
*/

/*
    //-_retirado de_-_-_-_-_-_-http://passoapassodascoisas.blogspot.com.br/2014/11/android-trabalhando-com-google-maps.html

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