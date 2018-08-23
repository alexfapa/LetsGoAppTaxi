package br.com.tutorialandroid.splashscreen;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainWS extends AppCompatActivity {

    //=====================================
    private Button btnInsertar;
    private Button btnActualizar;
    private Button btnEliminar;
    private Button btnObtener;
    private Button btnListar;

    private EditText txtPerfil;
    private EditText txtLogin;
    private EditText txtSenha;
    private EditText txtEmail;

    private TextView lblResultado;
    private ListView lstClientes;
    //=====================================

    // Progress Dialog
    private ProgressDialog pDialog;
    //AcessoRest acesso = new AcessoRest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ws);

        btnInsertar = (Button)findViewById(R.id.btnInsertar);
        btnActualizar = (Button)findViewById(R.id.btnActualizar);
        btnEliminar = (Button)findViewById(R.id.btnEliminar);
        btnObtener = (Button)findViewById(R.id.btnObtener);
        btnListar = (Button)findViewById(R.id.btnListar);

        txtPerfil = (EditText)findViewById(R.id.txtPerfil);
        txtLogin = (EditText)findViewById(R.id.txtLogin);
        txtSenha = (EditText)findViewById(R.id.txtSenha);
        txtEmail = (EditText)findViewById(R.id.txtEmail);

        lblResultado = (TextView)findViewById(R.id.lblResultado);
        lstClientes = (ListView)findViewById(R.id.lstClientes);


/*
        //===========================Layot===========================
        Button buscar = (Button) findViewById(R.id.buscar);
        Button deletar = (Button) findViewById(R.id.deletar);
        Button alterar = (Button) findViewById(R.id.alterar);

        //=====================ação do botão=========================
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Instancia a classe AcessoRest


                EditText texto = (EditText) findViewById(R.id.texto);
                String text = texto.getText().toString();   //Pega String digitado pelo usuário

                String chamadaWS;
                chamadaWS = "http://192.168.1.5:8080/WebService/webresources/ProjetoWS/Usuario/get/" + text; //Endereço WebService

                String resultado = acesso.chamadaGet(chamadaWS); // O AcessoRest verifica a URL e chama
                Log.i("JSON", resultado);

                //No NetBeans: HttpExemplo http = new HttpExemplo();
                //Gson g = new Gson();
                //Usuario u = new Usuario();
                //Type UsuarioType =new TypeToken<Usuario>(){}.getType();

                //String chamadaWS = "http://192.168.1.5:8080/.......";
                //String json = http.sendGet(chamadaWS, "GET");
                //u = g.fromJson(json, usuarioType);
                //System.out.println(u.getLogin());

                try {
                    //O JSONObject é responsável por fazer a leitura dos dados no formato JSON
                    JSONObject json = new JSONObject(resultado);
                    TextView nome = (TextView) findViewById(R.id.nome);
                    TextView perfil = (TextView) findViewById(R.id.perfil);
                    TextView email = (TextView) findViewById(R.id.email);
                    TextView senha = (TextView) findViewById(R.id.senha);

                                       //Busca no WebService
                    nome.setText(json.getString("login"));
                    perfil.setText(json.getString("perfil"));
                    email.setText(json.getString("email"));
                    senha.setText(json.getString("senha"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
                */



        //==================================AÇÃO DE BOTÕES===============================

        btnInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TareaWSInsertar tarea = new TareaWSInsertar();
                tarea.execute(
                        txtLogin.getText().toString(),
                            txtPerfil.getText().toString(),
                                txtEmail.getText().toString(),
                                    txtSenha.getText().toString());
            }
        });


        btnActualizar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TareaWSActualizar tarea = new TareaWSActualizar();
                tarea.execute(
                        txtLogin.getText().toString(),
                            txtPerfil.getText().toString(),
                                txtEmail.getText().toString(),
                                    txtSenha.getText().toString());
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TareaWSEliminar tarea = new TareaWSEliminar();
                tarea.execute(txtLogin.getText().toString());
            }
        });

        btnObtener.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TareaWSObtener tarea = new TareaWSObtener();
                tarea.execute(txtLogin.getText().toString());
            }
        });

        btnListar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TareaWSListar tarea = new TareaWSListar();
                tarea.execute();
            }
        });

    }



    //=====================================METODOS DO WS=====================================

    //Tarea As�ncrona para llamar al WS de listado en segundo plano
    private class TareaWSListar extends AsyncTask<String,Integer,Boolean> {

        private String[] usuario;

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            HttpClient httpClient = new DefaultHttpClient();

            HttpGet del =
                    new HttpGet("http://192.168.1.5:8080/WebService/webresources/ProjetoWS/Usuario/list");

            del.setHeader("content-type", "application/json");

            try
            {
                HttpResponse resp = httpClient.execute(del);
                String respStr = EntityUtils.toString(resp.getEntity());

                JSONArray respJSON = new JSONArray(respStr);

                usuario = new String[respJSON.length()];

                for(int i=0; i<respJSON.length(); i++)
                {
                    JSONObject obj = respJSON.getJSONObject(i);

                    String loginUsu = obj.getString("login");
                    String perfilUsu = obj.getString("perfil");
                    String senhaUsu = obj.getString("senha");
                    String emailUsu = obj.getString("email");

                    usuario[i] = "" + loginUsu + "-" + perfilUsu + "-" + emailUsu + "-" + senhaUsu;
                }
            }
            catch(Exception ex)
            {
                Log.e("ServicioRest","Error!", ex);
                resul = false;
            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {

            if (result)
            {
                //Rellenamos la lista con los nombres de los clientes
                //Rellenamos la lista con los resultados
                ArrayAdapter<String> adaptador =
                        new ArrayAdapter<String>(MainWS.this,
                                android.R.layout.simple_list_item_1, usuario);

                lstClientes.setAdapter(adaptador);
            }
        }
    }

    //Tarea As�ncrona para llamar al WS de consulta en segundo plano
    private class TareaWSObtener extends AsyncTask<String,Integer,Boolean> {

        private String loginUsu;
        private String perfilUsu;
        private String senhaUsu;
        private String emailUsu;

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            HttpClient httpClient = new DefaultHttpClient();

            String login = params[0];

            HttpGet del =
                    new HttpGet("http://192.168.1.5:8080/WebService/webresources/ProjetoWS/Usuario/get/" + login);

            del.setHeader("content-type", "application/json");

            try
            {
                HttpResponse resp = httpClient.execute(del);
                String respStr = EntityUtils.toString(resp.getEntity());

                JSONObject respJSON = new JSONObject(respStr);

                loginUsu = respJSON.getString("login");
                perfilUsu = respJSON.getString("perfil");
                senhaUsu = respJSON.getString("senha");
                emailUsu = respJSON.getString("email");
            }
            catch(Exception ex)
            {
                Log.e("ServicioRest","Error!", ex);
                resul = false;
            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {

            if (result)
            {
                lblResultado.setText("" + perfilUsu + "-" + loginUsu + "-" + emailUsu + "-" + senhaUsu);
            }
        }
    }

    //Tarea As�ncrona para llamar al WS de actualizaci�n en segundo plano
    private class TareaWSActualizar extends AsyncTask<String,Integer,Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            HttpClient httpClient = new DefaultHttpClient();

            HttpPut put = new HttpPut("http://192.168.1.5:8080/WebService/webresources/ProjetoWS/Usuario/inserir");
            put.setHeader("content-type", "application/json");

            try
            {
                //Construimos el objeto cliente en formato JSON
                JSONObject dato = new JSONObject();

                dato.put("login", params[0]);
                dato.put("perfil", params[1]);
                dato.put("senha", params[2]);
                dato.put("email", params[3]);

                StringEntity entity = new StringEntity(dato.toString());
                put.setEntity(entity);

                HttpResponse resp = httpClient.execute(put);
                String respStr = EntityUtils.toString(resp.getEntity());

                if(!respStr.equals("true"))
                    resul = false;
            }
            catch(Exception ex)
            {
                Log.e("ServicioRest","Error!", ex);
                resul = false;
            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {

            if (result)
            {
                lblResultado.setText("Actualizado OK.");
            }
        }
    }


    //Tarea As�ncrona para llamar al WS de eliminaci�n en segundo plano
    private class TareaWSEliminar extends AsyncTask<String,Integer,Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            HttpClient httpClient = new DefaultHttpClient();

            String login = params[0];

            HttpDelete del =
                    new HttpDelete("http://192.168.1.5:8080/WebService/webresources/ProjetoWS/Usuario/excluir/" + login);

            del.setHeader("content-type", "application/json");

            try
            {
                HttpResponse resp = httpClient.execute(del);
                String respStr = EntityUtils.toString(resp.getEntity());

                if(!respStr.equals("true"))
                    resul = false;
            }
            catch(Exception ex)
            {
                Log.e("ServicioRest","Error!", ex);
                resul = false;
            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {

            if (result)
            {
                lblResultado.setText("Eliminado OK.");
            }
        }
    }

    //Tarea As�ncrona para llamar al WS de inserci�n en segundo plano
    private class TareaWSInsertar extends AsyncTask<String,Integer,Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://192.168.1.5:8080/WebService/webresources/ProjetoWS/Usuario/inserir");
            post.setHeader("content-type", "application/json");

            try
            {
                //Construimos el objeto cliente en formato JSON
                JSONObject dato = new JSONObject();

                dato.put("login", params[0]);
                dato.put("perfil", params[1]);
                dato.put("email", params[2]);
                dato.put("senha", params[3]);

                StringEntity entity = new StringEntity(dato.toString());
                post.setEntity(entity);

                HttpResponse resp = httpClient.execute(post);
                String respStr = EntityUtils.toString(resp.getEntity());

                if(!respStr.equals("true"))
                    resul = false;
            }
            catch(Exception ex)
            {
                Log.e("ServicioRest","Error!", ex);
                resul = false;
            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {

            if (result)
            {
                lblResultado.setText("Insertado OK.");
            }
        }
    }

}
