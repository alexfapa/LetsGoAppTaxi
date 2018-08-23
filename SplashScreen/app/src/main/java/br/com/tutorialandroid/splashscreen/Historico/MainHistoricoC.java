package br.com.tutorialandroid.splashscreen.Historico;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.com.tutorialandroid.splashscreen.MainActivity;
import br.com.tutorialandroid.splashscreen.R;
import br.com.tutorialandroid.splashscreen.app.Config;
import br.com.tutorialandroid.splashscreen.conexao.AppConfig;
import br.com.tutorialandroid.splashscreen.executaAppEmSegundoPlano.service;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainHistoricoC extends AppCompatActivity {

    String URL_LIST = "http://www.letsgosobral.com/taxi/status_Taxista/getIdDadosTaxista.php";

    ListView details_list;
    ListViewHistorico displayAdapter;
    ArrayList<String> id = new ArrayList<>();
    ArrayList<String> origem = new ArrayList<>();
    ArrayList<String> destino = new ArrayList<>();
    ArrayList<String> data = new ArrayList<>();
    ArrayList<String> hora = new ArrayList<>();
    ArrayList<String> id_taxista = new ArrayList<>();
    ArrayList<String> preco = new ArrayList<>();
    ArrayList<String> descricao = new ArrayList<>();

    private ProgressDialog pDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_historico_c);
        startService(new Intent(this, service.class));

        details_list = (ListView) findViewById(R.id.lista_historico);
        displayAdapter = new ListViewHistorico(getApplicationContext(), id, id_taxista, origem, destino, data, hora, preco, descricao);

        SharedPreferences email_Shared = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_EMAIL, 0);
        String taxi_id_shared = email_Shared.getString("IDnoSharedP", null);

        try {
            MostraDadosdoTaxista(taxi_id_shared);
        }catch (Exception e){
            Toast.makeText(this, "Erro ao carregar dados do taxista \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Intent depoisdoErro = new Intent(this, MainActivity.class);
            startActivity(depoisdoErro);
        }

        pDialog = ProgressDialog.show(MainHistoricoC.this, "", "Carregando registros...");


        details_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long ids) {

                Intent i = new Intent(getApplicationContext(),MainDadosCorrida.class);
                i.putExtra("cli_id",id.get(position));
                i.putExtra("taxi_id_taxista",id_taxista.get(position));
                i.putExtra("taxi_preco",preco.get(position));
                i.putExtra("taxi_descricao",descricao.get(position));

                i.putExtra("taxi_origem",origem.get(position));
                i.putExtra("taxi_destino",destino.get(position));
                i.putExtra("taxi_data",data.get(position));
                i.putExtra("taxi_hora",hora.get(position));

                startActivityForResult(i, 1);
            }
        });
    }


    public void MostraDadosdoTaxista(String ht_idtaxista) {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(URL_LIST)
                .build();

        AppConfig.getDadosIdTaxista api = adapter.create(AppConfig.getDadosIdTaxista.class);

        api.getIdDados(
                ht_idtaxista,
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

                                    id.add(jo.getString("ht_idcliente"));
                                    id_taxista.add(jo.getString("ht_idtaxista"));
                                    preco.add(jo.getString("ht_valor"));
                                    descricao.add(jo.getString("ht_formapgto"));

                                    data.add(jo.getString("ht_dtcorrida"));
                                    hora.add(jo.getString("ht_hrcorrida"));
                                    origem.add(jo.getString("ht_origem"));
                                    destino.add(jo.getString("ht_destino"));

                                }

                                details_list.setAdapter(displayAdapter);
                                pDialog.dismiss();

                            } else {
                                Toast.makeText(getApplicationContext(), "Sem registros encontrados!", Toast.LENGTH_SHORT).show();
                                pDialog.cancel();
                            }
                        } catch (JSONException e) {
                            Log.d("exception", e.toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("Failure", error.toString());
                        Toast.makeText(MainHistoricoC.this, error.toString(), Toast.LENGTH_LONG).show();
                        pDialog.cancel();
                    }
                }
        );


    }


}
