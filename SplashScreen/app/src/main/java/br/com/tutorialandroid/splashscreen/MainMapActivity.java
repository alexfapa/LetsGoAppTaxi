package br.com.tutorialandroid.splashscreen;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import br.com.tutorialandroid.splashscreen.Historico.MainHistoricoC;
import br.com.tutorialandroid.splashscreen.app.Config;
import br.com.tutorialandroid.splashscreen.conexao.AppConfig;
import br.com.tutorialandroid.splashscreen.executaAppEmSegundoPlano.MyService;
import br.com.tutorialandroid.splashscreen.executaAppEmSegundoPlano.service;
import br.com.tutorialandroid.splashscreen.loginActivity.Constants;
import br.com.tutorialandroid.splashscreen.util.NotificationUtils;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

//import br.com.tutorialandroid.splashscreen.NotificationFcm.MainActivityToken;
//import br.com.tutorialandroid.splashscreen.fcmNotification.MainFcmActivity;

public class MainMapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String PREF_NAME = "MainActivityPreferences";

    private static final String PREF_ = "LoginActivityPreferences";

    private FragmentManager fragmentManager;    //Responsável por gerenciar os fragmentos da aplicação.

    //private AlertDialog alerta;

    //|||||||||||||||||||||MOSTRA NOME E EMAIL: BEM VINDO: Cara|||||||||||||||||
    private TextView tv_nome,tv_email,tv_id, token_id, tv_situacao, status_binario;
    private SharedPreferences pref;
    private ProgressBar progress;
    private ProgressDialog pd = null;
    //||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        //startService(new Intent(getBaseContext(), service.class));
        startService(new Intent(this, service.class));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        overridePendingTransition(R.anim.activity_entra, R.anim.activity_sai);

        tv_nome = (TextView) findViewById(R.id.tv_nome);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_id = (TextView) findViewById(R.id.tv_id);
        token_id = (TextView) findViewById(R.id.token_id);
        tv_situacao = (TextView) findViewById(R.id.tv_situacao);
        status_binario = (TextView) findViewById(R.id.status_binario);

        Intent i = getIntent();
        String id = i.getStringExtra("ID_UNI");
        String Nome = i.getStringExtra("Nome");
        String Email = i.getStringExtra("Email");

        tv_id.setText(id);
        tv_nome.setText(Nome);
        tv_email.setText(Email);

        getSupportActionBar().setTitle("LetsGO");
        toolbar.setSubtitle(tv_email.getText());
        //toolbar.setLogo(R.drawable.logomaraclets);

        //........GUARDA EMAIL DO USUÁRIO NO SHAREDPREFERENCES........
        guardaIdNoSharedP(tv_id.getText().toString());

        try {
            AsyncTaskCarrega async = new AsyncTaskCarrega();
            async.execute();
            iniciaService();
            //situacaoID();
        }catch (Exception e) {
            Toast.makeText(this, "Erro ao carregar Token: \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Intent depoisdoErro = new Intent(this, MainActivity.class);
            startActivity(depoisdoErro);
        }



        //pref = getPreferences(0);
        //tv_nome.setText("Bem Vindo: " + pref.getString(Constants.NOME,""));
        //tv_email.setText(pref.getString(Constants.EMAIL,""));
        //tv_id.setText(pref.getString(Constants.ID_UNICO,""));



        //Onde chamamos o menu lateral... É Aki!!!
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Corresponde aos Itens do Menu
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //=================================CHAMAMOS O FRAGMENTO MAPA AQUI!!!=====================================

        fragmentManager = getSupportFragmentManager();

        //Responsável por iniciar uma transação de execução de um processo(adicionar, substituir remover...)
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        //fragmento_mapa se encontra em content_main_map.xml e no fragment_fragment_tela2.xml
        transaction.replace(R.id.fragmento_mapa, new ProviderFragmentV2(), "ProviderFragmentV2");

        //frame1 se encontra no mesmo local da fragmento_mapa
        //transaction.replace(R.id.fragmento_mapa, new ProviderTaximetro(), "ProviderTaximetro");

        transaction.commitAllowingStateLoss();
    }

    public void iniciaService(){
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
    }

    public void paraService(){
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
    }


    public void situacaoID(){
        MostraStatusdoTaxista(tv_id.getText().toString());
        String tvSituacao = tv_situacao.getText().toString();
        switch (tvSituacao) {
            case "1":

                break;
            case "0":

                break;
        }

    }

    // classe que extrai o arquivo e estente AsyncTask
    public class AsyncTaskCarrega extends AsyncTask<Void, Void, Void> {

        // variável do dialog
        private ProgressDialog pDialog;

        // construtor padrão
        public AsyncTaskCarrega() {

            // instanciando o dialog
            pDialog = new ProgressDialog(MainMapActivity.this);
            pDialog.setMessage("Atualizando Token...");
            pDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            //ATUALIZA TOKEN DE CHAMADA FIREBASE
            atualizaToken();
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
/*
    public class ExecutarTarefaProgramadaReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            //      Código a executar
        }
    }
*/


    //ATUALIZA TOKEN DE CHAMADA DA APP
    public void atualizaToken(){

        SharedPreferences pref = MainMapActivity.this.getSharedPreferences(Config.SHARED_PREF, 0);
        String token_id_ = pref.getString("regId", null);

        token_id.setText(token_id_);
        atualizaToken(tv_id.getText().toString());
        Log.i("token", "Sim");
    }


    //PEGA EMAIL E ARMAZENA EM SHAREDPREFERENCES MANDANDO PARA Config.class
    private void guardaIdNoSharedP(String taxi_id_unic) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_EMAIL, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("IDnoSharedP", taxi_id_unic);
        editor.commit();
    }


    //=======================BOTÃO VOLTA - FECHA NAVGIATION BAR=========================
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) { //Se eu pressionar back, meu menu fecha, se tiver aberto
            drawer.closeDrawer(GravityCompat.START);
        } else {        //Se não retorna a activity anterior!
            super.onBackPressed();
        }
    }

    //=====================================MENU=========================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_map, menu);
        return true;
    }


    //===================================LOGOUT - BOTÃO SAIR=============================
/*    private void logout() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Constants.IS_LOGGED_IN,false);
        editor.putString(Constants.EMAIL,"");
        editor.putString(Constants.NOME,"");
        editor.putString(Constants.ID_UNICO,"");
        editor.apply();

        Intent intent = new Intent(MainMapActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
*/
    //=================================ENVIA ID PARA MAINPERFIL===========================
    public void EnviaIdParaPerfil(){
        Intent perfil = new Intent(MainMapActivity.this, MainPerfil.class);

        String ID_, Nome_, Email_;
        ID_ = tv_id.getText().toString();
        Nome_ =  tv_nome.getText().toString();
        Email_ = tv_email.getText().toString();


        Bundle bundle = new Bundle();
        bundle.putString("ID_UNI", ID_);
        bundle.putString("Nome", Nome_);
        bundle.putString("Email", Email_);


        perfil.putExtras(bundle);
        startActivity(perfil);
    }

    //=================================BOTÃO SAIR=========================================
    public void signOut(){
        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();
        cancelarNotificacaoLivre();
        cancelarNotificacaoOcupado();
        NotificationUtils.clearNotifications(getApplicationContext());
        //pd = ProgressDialog.show(MainMapActivity.this, "", "Saindo...");
        //status_binario.setText("0");
        //editaDados(tv_id.getText().toString());
        finish();
    }

    public void cancelarNotificacaoLivre(){
        Intent intent = new Intent("ALARME_DISPARADO");
        PendingIntent p = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarme.cancel(p);
    }
    public void cancelarNotificacaoOcupado(){
        Intent intent = new Intent("ALARME_DISPARADO_OCUP");
        PendingIntent p = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarme.cancel(p);
    }


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

                                    Log.i("situacao", "Situacao carregada");
                                }

                                //details_list.setAdapter(displayAdapter);
                                //pDialog.dismiss();

                            } else {
                                Toast.makeText(MainMapActivity.this, "Falha ao atualizar status", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainMapActivity.this, "Verifique sua Conexão! Status inativo!", Toast.LENGTH_LONG).show();
                        //pDialog.cancel();
                    }
                }
        );


    }


/*
    @Override
    public void onDestroy(){
        super.onDestroy();

        Intent intent = new Intent("ALARME_DISPARADO");
        PendingIntent p = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarme.cancel(p);
    }
*/


    //================ ENVIA PARA O BANCO DE DADOS = TOKEN ATUALIZADO ==============

    String _URL = "http://www.letsgosobral.com/taxi/status_Taxista/atualizaToken.php";
    public void atualizaToken(final String taxi_id_unic) {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(_URL) //Setting the Root URL
                .build();

        AppConfig.atualizaTokenN api = adapter.create(AppConfig.atualizaTokenN.class);

        api.atualizaTokenDeN(
                taxi_id_unic,
                token_id.getText().toString(),

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
                                //Toast.makeText(MainMapActivity.this, "Token atualizado sucesso!", Toast.LENGTH_SHORT).show();
                                //pd.dismiss();

                            } else {
                                Toast.makeText(MainMapActivity.this, "Erro ao atualizar token!", Toast.LENGTH_SHORT).show();
                                //pd.dismiss();
                            }

                        } catch (IOException e) {
                            Log.d("Exception", e.toString());
                        } catch (JSONException e) {
                            Log.d("JsonException", e.toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //Toast.makeText(MainPerfil.this, error.toString(), Toast.LENGTH_LONG).show();
                        Toast.makeText(MainMapActivity.this, "Verifique sua conexão!", Toast.LENGTH_LONG).show();
                        //pd.cancel();
                    }
                }
        );

    }

    //============================== ATUALIZA - TOKEN ==============================

    //============================== ENVIA OCUPADO =================================
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

                                Toast.makeText(MainMapActivity.this, "Volte logo", Toast.LENGTH_SHORT).show();
                                pd.dismiss();

                            } else {
                                Toast.makeText(MainMapActivity.this, "Atualização de status falhou!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainMapActivity.this, "Tente Novamente!", Toast.LENGTH_LONG).show();
                        pd.cancel();
                    }
                }
        );

    }
    //==============================================================================

    //==========================ITENS DA ACTIONBAR=======================================
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        /*switch (item.getItemId()) {
            case android.R.id.home:
                //getActionBar().setIcon(R.drawable.voltar);
                Intent intent = new Intent(MainMapActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
        }*/
        if (id == R.id.map_sair) {
            /*
            Intent intent = new Intent(MainMapActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            */
            signOut();
            paraService();
            return true;

        } else {
            if (id == R.id.map_perfil) {

                EnviaIdParaPerfil();

                return true;
            }else {
                if (id == R.id.map_feedback){
                    Intent feed = new Intent(MainMapActivity.this, MainFeedback.class);
                    startActivity(feed);
                    return true;
                }else {
                    if (id == R.id.map_ajuda){
                        Intent feed = new Intent(MainMapActivity.this, MainAjuda.class);
                        startActivity(feed);
                        return true;
                    }else {
                        if(id == R.id.map_corrida){
                            Intent teladecorrida = new Intent(MainMapActivity.this, MainNotification.class);
                            startActivity(teladecorrida);
                            return true;
                        }//else {
                           // if(id == R.id.map_paypal){
                             //   Intent telapaypal = new Intent(MainMapActivity.this, MainPerfilInfo.class);
                               // startActivity(telapaypal);
                                //return true;
                            //}
                        //}//else {
                           // if (id == R.id.map_teste) {
                             //   Intent teste = new Intent(MainMapActivity.this, MainWS.class);
                               // startActivity(teste);
                               // return true;
                            //}
                        //}
                        else{
                            if (id == R.id.map_teste){
                                //Toast.makeText(MainMapActivity.this, "Aplicando mudanças...", Toast.LENGTH_SHORT).show();
                                //Intent teladecorrida = new Intent(MainMapActivity.this, MainHistoricoDeCorrida.class);
                                //Intent teladecorrida = new Intent(MainMapActivity.this, MainHistoricoC.class);
                                Intent teladecorrida = new Intent(MainMapActivity.this, MainHistoricoC.class);
                                startActivity(teladecorrida);
                            }
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //==============================ANIMAÇÃO DE TELAS PASSANDO===========================
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.entrando_activity, R.anim.saindo_activity);
    }

    //============================Mostra telas de Fragmentos=============================
    private void showFragment(ProviderFragmentV2 fragment, String name){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmento_mapa, fragment, name);
        transaction.commit();
    }

    //=====================================ITENS DA BARRA LATERAL========================
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_providerfragmentv2:
                //Responsável por iniciar uma transação de execução de um processo(adicionar, substituir remover...)
                showFragment(new ProviderFragmentV2(), "ProviderFragmentV2"); //era assim: showFragment(new ProviderFragmentV2(), "ProviderFragmentV2");
/*
                NotificationManager no = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                PendingIntent p = PendingIntent.getActivity(this, 0, new Intent(this, MainNotification.class), 0);  //Intent pendente da notificação

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                builder.setTicker("Ticker Texto");  //Mensagem rápida
                builder.setContentTitle("Titulo");
                //builder.setContentText("Descrição");
                builder.setSmallIcon(R.drawable.logomaraclets);
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logomaraclets)); //Aqui é quando abrimos a
                builder.setContentIntent(p);                                    // barra de notificação e vemos a notificação

                //AQUI SERÁ REAJUSTADO UM MODO DE DESCRIÇÃO MAIOR...
                NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
                String [] descricao = new String[]{"Descrição 1", "Descrição 2", "Descrição 3", "Descrição 4"};
                builder.setNumber(descricao.length);
                style.setSummaryText("www.letsgosobral.com");
                for (int i = 0; i < descricao.length; i++){
                    style.addLine(descricao[i]);
                }
                //ADICIONANDO AO builder, NOSSO ESTILO DE DESCRIÇÃO
                builder.setStyle(style);

                //ACTION BUTTON
                //==================FAZER O ACTION BUTTON==================

                Notification n = builder.build(); //Chamamos nossa notificação
                n.vibrate = new long[]{150, 300, 150, 600};  //150 parado, 300 de vibração...
                n.flags = Notification.FLAG_AUTO_CANCEL;  //FAZ O CANCELAMENTO DA NOTIFICAÇÃO AUTOMÁTICO, ENTÃO NÃO PRECISAMOS DO SCRIPT NO MainNotification.class
                no.notify(R.mipmap.icon_notification, n);

                try {   //PEGAMOS O CAMINHO DIRETO PARA CHAMAR O TOQUE DE NOTIFICAÇÃO
                    Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone toque = RingtoneManager.getRingtone(this, som);
                    toque.play();
                }

                catch (Exception e){
                }
 */               break;

            case R.id.nav_corridas:
                //Responsável por iniciar uma transação de execução de um processo(adicionar, substituir remover...)
                //Toast.makeText(MainMapActivity.this, "Aplicando mudanças...", Toast.LENGTH_SHORT).show();

                Intent teladecorrida = new Intent(MainMapActivity.this, MainHistoricoC.class);
                startActivity(teladecorrida);

                break;

            case R.id.nav_feedback:
                //Responsável por iniciar uma transação de execução de um processo(adicionar, substituir remover...)

                Intent feed = new Intent(MainMapActivity.this, MainFeedback.class);
                startActivity(feed);
                break;

            case R.id.nav_perfil:
                //Responsável por iniciar uma transação de execução de um processo(adicionar, substituir remover...)
                EnviaIdParaPerfil();
                //Intent perfil = new Intent(MainMapActivity.this, MainPerfil.class);
                //startActivity(perfil);
                break;

            case R.id.nav_sair:
                signOut();
                paraService();
                /*
                Intent intent = new Intent(MainMapActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                */
                break;

            case R.id.nav_termosdeuso:
                //Intent termosdeuso = new Intent(MainMapActivity.this, ScrollingTermosdeUso.class);
                Intent termosdeuso = new Intent(MainMapActivity.this, MainTermosdeUso.class);
                startActivity(termosdeuso);
                break;

            case R.id.nav_ajuda:
                Intent ajuda = new Intent(MainMapActivity.this, MainAjuda.class);
                startActivity(ajuda);
                break;
        }

       // if (id == R.id.nav_escolha) {
            // Handle the camera action
        //} //else if (id == R.id.nav_gallery) {}

        //else if (id == R.id.nav_slideshow) {


        //} else if (id == R.id.nav_ferramentas) {

        //} /*else if (id == R.id.nav_share) {

      //  } else if (id == R.id.nav_send) {

        //} else if (id == R.id.nav_sair) {}
        // */

        //Fecha o menu sempre que clico em um item!!!
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Pronto, você tem o retorno para a sua Activity, ai se você quiser retornar algum valor da outra
        //Activity, isso tambem é possivel
    }
}

