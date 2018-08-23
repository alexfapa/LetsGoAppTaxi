package br.com.tutorialandroid.splashscreen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import br.com.tutorialandroid.splashscreen.conexao.AppConfig;
import br.com.tutorialandroid.splashscreen.conexao.ListViewAdapter;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


//MOSTRA TODOS OS DADOS CADASTRADOS NO BANCO
public class MainCRUDDisplayData extends AppCompatActivity {

    String BASE_URL = "http://www.letsgosobral.esy.es";

    ListView details_list;
    ListViewAdapter displayAdapter;
    ArrayList<String> id = new ArrayList<>();
    ArrayList<String> nome = new ArrayList<>();
    ArrayList<String> data_nasc = new ArrayList<>();
    ArrayList<String> phone = new ArrayList<>();
    ArrayList<String> cpf = new ArrayList<>();
    ArrayList<String> email = new ArrayList<>();
    //ArrayList<String> senha = new ArrayList<>();
    ArrayList<String> cep = new ArrayList<>();
    ArrayList<String> end = new ArrayList<>();
    ArrayList<String> end_num = new ArrayList<>();
    ArrayList<String> complemento = new ArrayList<>();
    ArrayList<String> bairro = new ArrayList<>();
    ArrayList<String> uf = new ArrayList<>();
    ArrayList<String> cidade = new ArrayList<>();
    ArrayList<String> cnh = new ArrayList<>();
    ArrayList<String> placa_carro = new ArrayList<>();
    ArrayList<String> alvara = new ArrayList<>();
    ArrayList<String> cracha = new ArrayList<>();
    ArrayList<String> agencia = new ArrayList<>();
    ArrayList<String> operacao = new ArrayList<>();
    ArrayList<String> conta = new ArrayList<>();


    private TextView banco_dados;

    AlertDialog alerta;
    private ProgressDialog pDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cruddisplay_data);

        details_list = (ListView) findViewById(R.id.retrieve);
        displayAdapter = new ListViewAdapter(getApplicationContext(), id, nome, data_nasc, phone, cpf, email, cep, end, end_num, complemento,
                bairro, uf, cidade, cnh, placa_carro, alvara, cracha, agencia, operacao, conta);
        banco_dados = (TextView) findViewById(R.id.banco_dados);
        displayData();

        pDialog = ProgressDialog.show(MainCRUDDisplayData.this, "", "Carregando registros...");

        //TROCADO PARA TESTES
        details_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long ids) {

                Intent i = new Intent(getApplicationContext(),MainCRUDUpdateData.class);
                i.putExtra("id",id.get(position));
                i.putExtra("nome",nome.get(position));
                i.putExtra("data_nasc",data_nasc.get(position));
                i.putExtra("telefone",phone.get(position));
                i.putExtra("cpf",cpf.get(position));
                i.putExtra("email", email.get(position));
                i.putExtra("cep", cep.get(position));
                i.putExtra("endereco", end.get(position));
                i.putExtra("num", end_num.get(position));
                i.putExtra("complemento", complemento.get(position));
                i.putExtra("bairro", bairro.get(position));
                i.putExtra("uf", uf.get(position));
                i.putExtra("cidade", cidade.get(position));
                i.putExtra("cnh", cnh.get(position));
                i.putExtra("placa_veiculo", placa_carro.get(position));
                i.putExtra("alvara", alvara.get(position));
                i.putExtra("num_cracha", cracha.get(position));
                i.putExtra("agencia", agencia.get(position));
                i.putExtra("operacao", operacao.get(position));
                i.putExtra("conta", conta.get(position));

                startActivity(i);
         }
        });

        //DELETA OS DADOS COM UM CLICK PRESSIONADO
        details_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long ids) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainCRUDDisplayData.this);
                builder.setTitle("EXCLUIR");
                builder.setMessage("Deseja realmente excluir este registro?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        delete(id.get(position));

                    }
                });
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(MainCRUDDisplayData.this, "Não excluido!", Toast.LENGTH_SHORT).show();// + arg1, Toast.LENGTH_SHORT).show();
                    }
                });
                alerta = builder.create();
                alerta.show();

                return true;
            }
        });
    }

    public void displayData() {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL) //Setting the Root URL
                .build();

        AppConfig.read api = adapter.create(AppConfig.read.class);

        api.readData(new Callback<JsonElement>() {
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

                                         id.add(jo.getString("id"));
                                         nome.add(jo.getString("nome"));
                                         data_nasc.add(jo.getString("data_nasc"));
                                         phone.add(jo.getString("telefone"));
                                         cpf.add(jo.getString("cpf"));
                                         email.add(jo.getString("email"));
                                         cep.add(jo.getString("cep"));
                                         end.add(jo.getString("endereco"));
                                         end_num.add(jo.getString("num"));
                                         complemento.add(jo.getString("complemento"));
                                         bairro.add(jo.getString("bairro"));
                                         uf.add(jo.getString("uf"));
                                         cidade.add(jo.getString("cidade"));
                                         cnh.add(jo.getString("cnh"));
                                         placa_carro.add(jo.getString("placa_veiculo"));
                                         alvara.add(jo.getString("alvara"));
                                         cracha.add(jo.getString("num_cracha"));
                                         agencia.add(jo.getString("agencia"));
                                         operacao.add(jo.getString("operacao"));
                                         conta.add(jo.getString("conta"));

                                         /*
                                         id.add(jo.getString("id"));
                                         name.add(jo.getString("nome"));
                                         age.add(jo.getString("idade"));
                                         mobile.add(jo.getString("telefone"));
                                         email.add(jo.getString("email"));
*/
                                     }

                                     details_list.setAdapter(displayAdapter);
                                     pDialog.dismiss();
                                 } else {
                                     Toast.makeText(getApplicationContext(), "Sem registros encontrados!", Toast.LENGTH_SHORT).show();
                                 }
                             } catch (JSONException e) {
                                 Log.d("exception", e.toString());
                             }
                         }

                         @Override
                         public void failure(RetrofitError error) {
                             Log.d("Failure", error.toString());
                             Toast.makeText(MainCRUDDisplayData.this, error.toString(), Toast.LENGTH_LONG).show();
                             pDialog.cancel();
                         }
                     }
        );
    }

    public void delete(String id){

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL) //Setting the Root URL
                .build();

        AppConfig.delete api = adapter.create(AppConfig.delete.class);

        api.deleteData(
                id,
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
                                Toast.makeText(getApplicationContext(), "Deletado com sucesso!", Toast.LENGTH_SHORT).show();
                                recreate();
                            } else{
                                Toast.makeText(getApplicationContext(), "Falhou ao deletar registro!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            Log.d("Exception", e.toString());
                        } catch (JSONException e) {
                            Log.d("JsonException", e.toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(MainCRUDDisplayData.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );

    }

}