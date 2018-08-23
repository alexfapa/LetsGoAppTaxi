package br.com.tutorialandroid.splashscreen.telaDeCorrida;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

import br.com.tutorialandroid.splashscreen.MainActivity;
import br.com.tutorialandroid.splashscreen.MainCadActivity;
import br.com.tutorialandroid.splashscreen.MainMapActivity;
import br.com.tutorialandroid.splashscreen.ProviderFragmentV2;
import br.com.tutorialandroid.splashscreen.R;
import br.com.tutorialandroid.splashscreen.conexao.AppConfig;
import br.com.tutorialandroid.splashscreen.executaAppEmSegundoPlano.service;
import br.com.tutorialandroid.splashscreen.loginActivity.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainPagamentoTaxi extends AppCompatActivity {

    String BASE_URL_HISTORICO = "http://www.letsgosobral.com/taxi/status_Taxista/insertDataHistorico.php";

    public TextView id_taxi, id_cli, hora_data, destino_usu, origem_usu, data_hora, valor_pago,
            forma_de_pagamento, nome_cliente;

    public Button buttonPaynoTaxi;

    private ProgressDialog progress = null;
    final String status = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pagamento_taxi);
        startService(new Intent(this, service.class));

        buttonPaynoTaxi = (Button) findViewById(R.id.buttonPaynoTaxi);
        nome_cliente = (TextView) findViewById(R.id.nome_cliente);
        forma_de_pagamento = (TextView) findViewById(R.id.forma_de_pagamento);
        valor_pago = (TextView) findViewById(R.id.valor_pago);
        data_hora = (TextView) findViewById(R.id.data_hora);
        origem_usu = (TextView) findViewById(R.id.origem_usu);
        destino_usu = (TextView) findViewById(R.id.destino_usu);
        hora_data = (TextView) findViewById(R.id.hora_data);
        id_cli = (TextView) findViewById(R.id.id_cli);
        id_taxi = (TextView) findViewById(R.id.id_taxi);

        String nome_cli = getIntent().getStringExtra("nome_cliente");
        nome_cliente.setText(nome_cli);

        String forma_pagamento = getIntent().getStringExtra("forma_de_pagamento");
        forma_de_pagamento.setText(forma_pagamento);

        String valorpago = getIntent().getStringExtra("valorpago");
        valor_pago.setText(valorpago);

        String data = getIntent().getStringExtra("data_corrida");
        data_hora.setText(data);

        String hora = getIntent().getStringExtra("hora_corrida");
        hora_data.setText(hora);

        String partida = getIntent().getStringExtra("ponto_partida_cli");
        origem_usu.setText(partida);

        String destino = getIntent().getStringExtra("destino_cli");
        destino_usu.setText(destino);

        String id_usu = getIntent().getStringExtra("id_usu");
        id_cli.setText(id_usu);

        String id_taxista = getIntent().getStringExtra("id_taxista");
        id_taxi.setText(id_taxista);

        buttonPaynoTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //MANDA PRO BANCO E SHAREDPREFERENCES E MOSTRA NA TELA DE HISTÓRICO
                progress = ProgressDialog.show(MainPagamentoTaxi.this, "", "Aguarde...");
                insert_data_Historico();
            }
        });
    }

    // ENVIA DADOS PARA A TABELA HISTÓRICO DE CORRIDA
    public void insert_data_Historico() {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL_HISTORICO) //Setting the Root URL
                .build();

        AppConfig.insertHistorico api = adapter.create(AppConfig.insertHistorico.class);

        api.insertHist(
                id_taxi.getText().toString(),
                Integer.parseInt(id_cli.getText().toString()),
                data_hora.getText().toString(),
                hora_data.getText().toString(),
                origem_usu.getText().toString(),
                destino_usu.getText().toString(),
                Double.parseDouble(valor_pago.getText().toString()),
                forma_de_pagamento.getText().toString(),
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

                            if(success == 1){
                                Toast.makeText(getApplicationContext(), "Dados inseridos com sucesso", Toast.LENGTH_SHORT).show();
                                editaDadosStatus(id_taxi.getText().toString());
                                //startActivity(new Intent(MainPagamentoTaxi.this, MainMapActivity.class));

                                Intent telaPrincipal = new Intent(MainPagamentoTaxi.this, MainMapActivity.class);

                                String ID_, Nome_, Email_;
                                ID_ = id_taxi.getText().toString();
                                Nome_ =  (Constants.NOME);
                                Email_ = (Constants.EMAIL);


                                Bundle bundle = new Bundle();
                                bundle.putString("ID_UNI", ID_);
                                bundle.putString("Nome", Nome_);
                                bundle.putString("Email", Email_);

                                telaPrincipal.putExtras(bundle);

                                telaPrincipal.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(telaPrincipal);

                                finish();

                                progress.dismiss();
                            } else{
                                Toast.makeText(getApplicationContext(), "Inserção de dados falhou", Toast.LENGTH_SHORT).show();
                                progress.cancel();
                            }

                        } catch (IOException e) {
                            Log.d("Exception", e.toString());
                        } catch (JSONException e) {
                            Log.d("JsonException", e.toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //Toast.makeText(MainPagamentoTaxi.this, error.toString(), Toast.LENGTH_LONG).show();
                        Toast.makeText(MainPagamentoTaxi.this, "Verifique sua conexão", Toast.LENGTH_LONG).show();
                        progress.cancel();
                    }
                }
        );
    }


    public void alarmSituacaoLiv(){
        //====================
        Intent intent = new Intent("ALARME_DISPARADO");
        PendingIntent p = PendingIntent.getBroadcast(MainPagamentoTaxi.this, 0, intent, 0);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 3);

        AlarmManager alarme = (AlarmManager) MainPagamentoTaxi.this.getSystemService(ALARM_SERVICE);
        alarme.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 3000, p);

        cancelaAlarmNotificationatual();

    }

    public void cancelaAlarmNotificationatual(){
        Intent intents = new Intent("ALARME_DISPARADO_OCUP");
        PendingIntent pi = PendingIntent.getBroadcast(MainPagamentoTaxi.this, 0, intents, 0);

        AlarmManager alarmes = (AlarmManager) MainPagamentoTaxi.this.getSystemService(ALARM_SERVICE);
        alarmes.cancel(pi);
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
                                //Toast.makeText(MainPagamentoTaxi.this, "Corrida cancelada!", Toast.LENGTH_SHORT).show();
                                alarmSituacaoLiv();

                            } else {
                                //Toast.makeText(MainPagamentoTaxi.this, "Verifique sua conexão!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainPagamentoTaxi.this, "Verifique sua conexão", Toast.LENGTH_SHORT).show();

                    }
                }
        );

    }

    //============================== LIVRE - OCUPADO ==============================

}