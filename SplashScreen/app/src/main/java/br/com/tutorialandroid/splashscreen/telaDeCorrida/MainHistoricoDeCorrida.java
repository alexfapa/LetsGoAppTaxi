package br.com.tutorialandroid.splashscreen.telaDeCorrida;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import java.util.List;

import br.com.tutorialandroid.splashscreen.R;
import br.com.tutorialandroid.splashscreen.conexao.AppConfig;
import br.com.tutorialandroid.splashscreen.loginActivity.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainHistoricoDeCorrida extends AppCompatActivity {

    private ListView lv_Historico;
    private HistoricoListAdapter adapter;

    private List<Historico> mHistoricoList;

    private ProgressDialog pDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_historico_de_corrida);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pDialog = ProgressDialog.show(MainHistoricoDeCorrida.this, "", "Buscando Dados...");

        lv_Historico = (ListView) findViewById(R.id.listview_historico);

        MostraDadosdoTaxista("596a673c4b0b97.69581196");

        mHistoricoList = new ArrayList<>();


        //Adiciona cada dado a lista
        //Podemos pegar dados do banco de dados por aqui
        //mHistoricoList.add(new Historico(1, "Nome", 10, "Pago no cartão"));
/*      mHistoricoList.add(new Historico(2, "Alex Pinto", 12, "Pago no táxi"));
        mHistoricoList.add(new Historico(3, "Samira Alves", 10, "Pago no táxi"));
        mHistoricoList.add(new Historico(4, "Renata Almeida", 15, "Pago no táxi"));
        mHistoricoList.add(new Historico(5, "Misael de Oliveira", 10, "Pago no táxi"));
        mHistoricoList.add(new Historico(6, "Ivo Oliveira", 12, "Pago no táxi"));
        mHistoricoList.add(new Historico(7, "João Sousa", 17, "Pago no táxi"));
        mHistoricoList.add(new Historico(8, "Renato Almeida", 15, "Pago no táxi"));
        mHistoricoList.add(new Historico(9, "Junior Ferreira", 10, "Pago no táxi"));
        mHistoricoList.add(new Historico(10, "Tupinambá Parente", 18, "Pago no táxi"));
*/


/*
        //Init Adapter
        adapter = new HistoricoListAdapter(getApplicationContext(), mHistoricoList);
        lv_Historico.setAdapter(adapter);

        lv_Historico.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int posicao, long id) {
                Toast.makeText(getApplicationContext(), "Dados clicado id =" + view.getTag(), Toast.LENGTH_SHORT).show();
            }
        });
*/

        //Init Adapter
        adapter = new HistoricoListAdapter(getApplicationContext(), mHistoricoList);
        lv_Historico.setAdapter(adapter);

        lv_Historico.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int posicao, long id) {
                Toast.makeText(getApplicationContext(), "Dados clicado id =" + view.getTag(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void MostraDadosdoTaxista(final String ht_idtaxista) {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL) //Setting the Root URL
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
                                    /*
                                    id.add(jo.getString("ht_idcliente"));
                                    nome.add(jo.getString("ht_idtaxista"));
                                    preco.add(jo.getString("ht_valor"));
                                    descricao.add(jo.getString("ht_formapgto"));
                                    */
                                }

                                //details_list.setAdapter(displayAdapter);
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
                        Toast.makeText(MainHistoricoDeCorrida.this, error.toString(), Toast.LENGTH_LONG).show();
                        pDialog.cancel();
                    }
                }
        );


    }


}
