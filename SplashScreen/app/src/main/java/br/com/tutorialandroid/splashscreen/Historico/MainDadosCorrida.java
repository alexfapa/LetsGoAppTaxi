package br.com.tutorialandroid.splashscreen.Historico;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.tutorialandroid.splashscreen.R;
import br.com.tutorialandroid.splashscreen.conexao.AppConfig;
import br.com.tutorialandroid.splashscreen.executaAppEmSegundoPlano.service;
import br.com.tutorialandroid.splashscreen.loginActivity.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainDadosCorrida extends AppCompatActivity {

    String URL_LIST = "http://www.letsgosobral.com/taxi/status_Taxista/getIdDadosTaxista.php";

    String id_cli, taxista_id, preco_c, descricao_c, taxi_origem, taxi_destino, taxi_hora, taxi_data;
    TextView nome_cli, caminho_img, email_cli, fone_cli, nasc_cli,
            data_corrida, hora_corrida, valor_corrida_reais,
            pagameno_forma, origem_corrida, destino_corrida;

    private ImageView img_usu;

    private static final String SERVER_ADDRESS = "http://www.letsgosobral.com/";

    private ProgressDialog progress = null;
    //private ExtrairTask et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, service.class));
        setContentView(R.layout.activity_main_dados_corrida);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nome_cli = (TextView) findViewById(R.id.nome_c);
        email_cli = (TextView) findViewById(R.id.email_c);
        fone_cli = (TextView) findViewById(R.id.fone_cli);
        nasc_cli = (TextView) findViewById(R.id.nasc_cli);
        data_corrida = (TextView) findViewById(R.id.data_corrida);
        hora_corrida = (TextView) findViewById(R.id.hora_corrida);
        pagameno_forma = (TextView) findViewById(R.id.pagameno_forma);
        origem_corrida = (TextView) findViewById(R.id.origem_corrida);
        destino_corrida = (TextView) findViewById(R.id.destino_corrida);
        valor_corrida_reais = (TextView) findViewById(R.id.valor_corrida_reais);
        //img_usu = (ImageView) findViewById(R.id.img_usuario);
        //caminho_img = (TextView) findViewById(R.id.caminho_img);

        progress = ProgressDialog.show(MainDadosCorrida.this, "", "Aguarde...");

        Intent i = getIntent();
        id_cli = i.getStringExtra("cli_id");
        taxista_id = i.getStringExtra("taxi_id_taxista");

        preco_c = i.getStringExtra("taxi_preco");
        valor_corrida_reais.setText(preco_c);

        descricao_c = i.getStringExtra("taxi_descricao");
        pagameno_forma.setText(descricao_c);

        taxi_origem = i.getStringExtra("taxi_origem");
        origem_corrida.setText(taxi_origem);

        taxi_destino = i.getStringExtra("taxi_destino");
        destino_corrida.setText(taxi_destino);

        taxi_hora = i.getStringExtra("taxi_hora");
        hora_corrida.setText(taxi_hora);

        taxi_data = i.getStringExtra("taxi_data");
        data_corrida.setText(taxi_data);

        pegaDadosCliente(id_cli);

        //caminho_img.getText().toString();
        //new PegaImageCli(caminho_img.getText().toString()).execute();


        // em resposta a uma ação do usuário, instanciamos a tarefa e executamos
        //et = new ExtrairTask();
        //et.execute();


    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
    //=-=-=-=-=-=|-=-=-=-=-=-=|PEGA OS DADOS DO CLIENTE PELO EMAIL VINDA DA NOTIFICAÇÃO|=-=-=-=-=-=-=-|=-=-=-=-=-=-=-

    public void pegaDadosCliente(String id_cli) {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL) //Setting the Root URL
                .build();

        AppConfig.getCliId api = adapter.create(AppConfig.getCliId.class);

        api.getDadosCliId(
                id_cli,
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


                                    nome_cli.setText(jo.getString("usu_nome"));
                                    fone_cli.setText(jo.getString("usu_fone"));
                                    email_cli.setText(jo.getString("usu_email"));
                                    nasc_cli.setText(jo.getString("usu_nasc"));
                                    //usu_cartao_cli.setText(jo.getString("usu_cartao"));            //sexo
                                    //caminho_img.setText(jo.getString("usu_img"));    //endereco

                                    //DEVO PAGAR O DESTINO E A FORMA DE PAGAMENTO MANDADO
                                    //usu_destino.setText(jo.getString("usu_destino"));
                                    //usu_forma_pagamento.setText(jo.getString("usu_pagamento"));

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
                        //Toast.makeText(MainDadosCorrida.this, error.toString(), Toast.LENGTH_LONG).show();
                        Toast.makeText(MainDadosCorrida.this, "Verifique sua conexão!", Toast.LENGTH_LONG).show();
                        progress.cancel();
                        //pDialog.cancel();
                    }
                }
        );


    }


    @Override
    public void finish() {
        Intent intent = new Intent();
        setResult(1, intent);
        super.finish();
    }

}

/*
    // classe que extrai o arquivo e estente AsyncTask
    public class ExtrairTask extends AsyncTask<Void, Void, Void> {

        // variável do dialog
        private ProgressDialog pDialog;

        // construtor padrão
        public ExtrairTask() {

            // instanciando o dialog
            pDialog = new ProgressDialog(MainDadosCorrida.this);
            pDialog.setMessage("Extraindo arquivo...");
            pDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... params) {



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
*/

/*
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
                //pDialog.dismiss();
            }
        }

    }
    //=============================DA IMAGEM DA PASTA DO BD============================
*/



