package br.com.tutorialandroid.splashscreen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import br.com.tutorialandroid.splashscreen.Historico.MainHistoricoC;
import br.com.tutorialandroid.splashscreen.alarmeLembrete.MainAlarmeLembrete;
import br.com.tutorialandroid.splashscreen.app.Config;
import br.com.tutorialandroid.splashscreen.conexao.AppConfig;
import br.com.tutorialandroid.splashscreen.executaAppEmSegundoPlano.service;
import br.com.tutorialandroid.splashscreen.loginActivity.Constants;
import br.com.tutorialandroid.splashscreen.loginActivity.RequestInterface;
import br.com.tutorialandroid.splashscreen.loginActivity.conexoes.ServerRequest;
import br.com.tutorialandroid.splashscreen.loginActivity.conexoes.ServerResponse;
import br.com.tutorialandroid.splashscreen.loginActivity.conexoes.User;
import br.com.tutorialandroid.splashscreen.telaDeCorrida.MainPassageiroAguarda;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.extras.Base64;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.params.HttpParams;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainPerfil extends FragmentActivity {

    private String APP_DIRECTORY = "LetsGOApp/";
    private String MEDIA_DIRECTORY = APP_DIRECTORY + "LetsGO Imagens";

    public static final int CAMERA_REQUEST = 10;

    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;

    private ImageView img_perfil, opcoes_perfill, opcoes_perf;
    private ImageButton voltar_perf, button;
    private RelativeLayout mRLView;
    private TextView end_email, nome_perfil, atualiza_dados_usu, tv_message;
    private SharedPreferences pref;
    private AlertDialog dialog, alerta;


    //============================DA CAMERA===============================

    private static final int RESULT_LOAD_IMAGE = 9002;
    //private static final String SERVER_ADDRESS = "http://www.letsgosobral.esy.es/";
    //TROCADO POR
    private static final String SERVER_ADDRESS = "http://www.letsgosobral.com/";

    //====================================================================


    private TextView mostraId, mostranome, mostratelefone, mostraemail,
            mostradata_nasc, mostracpf, mostracep, mostraendereco, mostranum,
            mostracomplemento, mostrabairro, mostrauf, mostracidade, mostracnh, mostraplaca_veiculo,
            mostraalvara, mostranum_cracha, mostraagencia, mostraoperacao, mostraconta, mostrasexo, mostratoken;

    private String mPath;

    ImageButton img_voltar;
    private PopupMenu mPopupMenu;
    private ProgressDialog pDialog = null;
    private ProgressBar progress, progressoDeEnvio;
    private Button salvarlembreteBIO, btn_salvar, btn_definiralarme, btn_definirlembrete;
    private EditText et_lembrete;
    private TextView lembreteBIO, lembrete_alarme;

    private AppCompatButton btn_change_password,btn_logout, btn_update;
    private EditText et_old_password,et_new_password;

    ListView listView = null;

    private ProgressDialog pd = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_perfil);
        startService(new Intent(this, service.class));

        overridePendingTransition(R.anim.activity_entra, R.anim.activity_sai);

        mostraId = (TextView) findViewById(R.id.mostraid);
        mostranome = (TextView) findViewById(R.id.mostranome);
        mostratelefone = (TextView) findViewById(R.id.mostratelefone);
        mostraemail = (TextView) findViewById(R.id.mostraemail);
        mostradata_nasc = (TextView) findViewById(R.id.mostradata_nasc);
        mostrasexo = (TextView) findViewById(R.id.mostrasexo);
        mostracpf = (TextView) findViewById(R.id.mostracpf);
        mostracep = (TextView) findViewById(R.id.mostracep);
        mostraendereco = (TextView) findViewById(R.id.mostraendereco);
        mostranum = (TextView) findViewById(R.id.mostranum);
        mostracomplemento = (TextView) findViewById(R.id.mostracomplemento);
        mostrabairro = (TextView) findViewById(R.id.mostrabairro);
        mostrauf = (TextView) findViewById(R.id.mostrauf);
        mostracidade = (TextView) findViewById(R.id.mostracidade);
        mostracnh = (TextView) findViewById(R.id.mostracnh);
        mostraplaca_veiculo = (TextView) findViewById(R.id.mostraplaca_veiculo);
        mostraalvara = (TextView) findViewById(R.id.mostraalvara);
        mostranum_cracha = (TextView) findViewById(R.id.mostranum_cracha);
        mostraagencia = (TextView) findViewById(R.id.mostraagencia);
        mostraoperacao = (TextView) findViewById(R.id.mostraoperacao);
        mostraconta = (TextView) findViewById(R.id.mostraconta);
        mostratoken = (TextView) findViewById(R.id.mostratoken);

        //atualiza_dados_usu = (Button) findViewById(R.id.atualiza_dados_usu);
        img_perfil = (ImageView) findViewById(R.id.img_perfil);
        opcoes_perf = (ImageView) findViewById(R.id.opcoes_perf);
        button = (ImageButton) findViewById(R.id.btn_imagem);
        //voltar_perf = (ImageButton) findViewById(R.id.voltar_perf);
        mRLView = (RelativeLayout) findViewById(R.id.content_main_perfil);
        //img_voltar = (ImageButton) findViewById(R.id.img_voltar);
        end_email = (TextView) findViewById(R.id.email_perfil);
        nome_perfil = (TextView) findViewById(R.id.nome_perfil);
        progress = (ProgressBar) findViewById(R.id.progress);
        progressoDeEnvio = (ProgressBar) findViewById(R.id.progressoDeEnvio);

        progress.setVisibility(View.VISIBLE);
        //pDialog = ProgressDialog.show(MainPerfil.this, "", "Buscando Dados...");

        Intent i = getIntent();
        String id = i.getStringExtra("ID_UNI");
        String Nome = i.getStringExtra("Nome");
        String Email = i.getStringExtra("Email");

        end_email.setText(Email);
        nome_perfil.setText(Nome);
        mostraId.setText(id);

        //Classe que fará o reconhecimento de qual usuário está logado
        //então buscará os registros por id
        //TRATAMENTO DE EXCEÇÃO COM TRY CATCH
        try {
            MostraDadosdoTaxista(id);
        }catch (Exception e){
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Intent depoisdoErro = new Intent(this, MainActivity.class);
            startActivity(depoisdoErro);
        }
        //=================================

        //pDialog = ProgressDialog.show(MainPerfil.this, "", "Carregando imagem, aguarde...");
        //pDialog.setCancelable(true);
        //TRATAMENTO DE EXCEÇÃO COM TRY CATCH
        try {
            new DownloadImage(mostraId.getText().toString()).execute();
        }catch (Exception e){
            Toast.makeText(this, "Erro ao carregar imagem: \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Intent depoisdoErro = new Intent(this, MainActivity.class);
            startActivity(depoisdoErro);
        }

        if (mayRequestStoragePermission())
            button.setEnabled(true);
        else
            button.setEnabled(false);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showOptions();

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*"); //Todas as extensões de imagens
                startActivityForResult(Intent.createChooser(intent, "Selecione app de imagens"), SELECT_PICTURE);

            }
        });

/*
        // Create an instance of a ListView
        listView = new ListView(this);
        // Add data to the ListView
        String[] items = {"Editar", "Mudar Senha", "Histórico de corridas"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.list_item_perfil, R.id.txtitem, items);

        listView.setAdapter(adapter);

        // Perform action when an item is clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ViewGroup vg = (ViewGroup) view;
                TextView txt = (TextView) vg.findViewById(R.id.txtitem);

                if (txt.getText() == "Editar") {
                    Intent i = new Intent(getApplicationContext(), MainCRUDUpdateData.class);

                    String id_, nome, telefone, email, data_nasc, sexo, cpf, cep, endereco, num,
                            complemento, bairro, uf, cidade, cnh, placa_veiculo, alvara, num_cracha,
                            agencia, operacao, conta = "";

                    id_ = mostraId.getText().toString();
                    nome = mostranome.getText().toString();
                    telefone = mostratelefone.getText().toString();
                    email = mostraemail.getText().toString();
                    data_nasc = mostradata_nasc.getText().toString();
                    sexo = mostrasexo.getText().toString();
                    cpf = mostracpf.getText().toString();
                    cep = mostracep.getText().toString();
                    endereco = mostraendereco.getText().toString();
                    num = mostranum.getText().toString();
                    complemento = mostracomplemento.getText().toString();
                    bairro = mostrabairro.getText().toString();
                    uf = mostrauf.getText().toString();
                    cidade = mostracidade.getText().toString();
                    cnh = mostracnh.getText().toString();
                    placa_veiculo = mostraplaca_veiculo.getText().toString();
                    alvara = mostraalvara.getText().toString();
                    num_cracha = mostranum_cracha.getText().toString();
                    agencia = mostraagencia.getText().toString();
                    operacao = mostraoperacao.getText().toString();
                    conta = mostraconta.getText().toString();

                    Bundle bundle = new Bundle();

                    bundle.putString("id", id_);
                    bundle.putString("nome", nome);
                    bundle.putString("telefone", telefone);
                    bundle.putString("email", email);
                    bundle.putString("data_nasc", data_nasc);
                    bundle.putString("sexo", sexo);
                    bundle.putString("cpf", cpf);
                    bundle.putString("cep", cep);
                    bundle.putString("endereco", endereco);
                    bundle.putString("num", num);
                    bundle.putString("complemento", complemento);
                    bundle.putString("bairro", bairro);
                    bundle.putString("uf", uf);
                    bundle.putString("cidade", cidade);
                    bundle.putString("cnh", cnh);
                    bundle.putString("placa_veiculo", placa_veiculo);
                    bundle.putString("alvara", alvara);
                    bundle.putString("num_cracha", num_cracha);
                    bundle.putString("agencia", agencia);
                    bundle.putString("operacao", operacao);
                    bundle.putString("conta", conta);


                    i.putExtras(bundle);
                    startActivity(i);

                }else if (txt.getText() == "Mudar Senha"){
                    showDialog();
                    //Toast.makeText(MainPerfil.this, "Faça isso logo!!!", Toast.LENGTH_LONG).show();
                }else if (txt.getText() == "Histórico de corridas"){
                    Toast.makeText(MainPerfil.this, "Será feito logo!!!", Toast.LENGTH_LONG).show();
                }
            }

        });



        ImageButton imageButton = (ImageButton) findViewById(R.id.opcoes_perf);

        mPopupMenu = new PopupMenu(this, imageButton);
        final MenuInflater menuInflater = mPopupMenu.getMenuInflater();

        menuInflater.inflate(R.menu.menu_opcoes_perfil, mPopupMenu.getMenu());
        imageButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mPopupMenu.show();
                    }
                });
*/
        //======================BOTÃO VOLTAR < ===========================
     /*   voltar_perf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainPerfil.this, MainMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });*/


        //================= >>BOTÃO ATUALIZA DADOS<< =====================
/*
        atualiza_dados_usu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               Intent i = new Intent(getApplicationContext(), MainCRUDUpdateData.class);

                String id, nome, telefone, email, data_nasc, sexo, cpf, cep, endereco, num,
                complemento, bairro, uf, cidade, cnh, placa_veiculo, alvara, num_cracha,
                agencia, operacao, conta = "";

                id = mostraId.getText().toString();
                nome = mostranome.getText().toString();
                telefone = mostratelefone.getText().toString();
                email = mostraemail.getText().toString();
                data_nasc = mostradata_nasc.getText().toString();
                sexo = mostrasexo.getText().toString();
                cpf = mostracpf.getText().toString();
                cep = mostracep.getText().toString();
                endereco = mostraendereco.getText().toString();
                num = mostranum.getText().toString();
                complemento = mostracomplemento.getText().toString();
                bairro = mostrabairro.getText().toString();
                uf = mostrauf.getText().toString();
                cidade = mostracidade.getText().toString();
                cnh = mostracnh.getText().toString();
                placa_veiculo = mostraplaca_veiculo.getText().toString();
                alvara = mostraalvara.getText().toString();
                num_cracha = mostranum_cracha.getText().toString();
                agencia = mostraagencia.getText().toString();
                operacao = mostraoperacao.getText().toString();
                conta = mostraconta.getText().toString();

                Bundle bundle = new Bundle();

                bundle.putString("id", id);
                bundle.putString("nome", nome);
                bundle.putString("telefone", telefone);
                bundle.putString("email", email);
                bundle.putString("data_nasc", data_nasc);
                bundle.putString("sexo", sexo);
                bundle.putString("cpf", cpf);
                bundle.putString("cep", cep);
                bundle.putString("endereco", endereco);
                bundle.putString("num", num);
                bundle.putString("complemento", complemento);
                bundle.putString("bairro", bairro);
                bundle.putString("uf", uf);
                bundle.putString("cidade", cidade);
                bundle.putString("cnh", cnh);
                bundle.putString("placa_veiculo", placa_veiculo);
                bundle.putString("alvara", alvara);
                bundle.putString("num_cracha", num_cracha);
                bundle.putString("agencia", agencia);
                bundle.putString("operacao", operacao);
                bundle.putString("conta", conta);


                i.putExtras(bundle);
                startActivity(i);
            }
        });

        */

        //pref = getPreferences(0);
        //nome_perfil.setText(pref.getString(Constants.NOME,""));
        //end_email.setText(pref.getString(Constants.EMAIL,""));
        //pDialog.dismiss();


        salvarlembreteBIO = (Button) findViewById(R.id.salvarlembreteBIO);
        lembreteBIO = (TextView) findViewById(R.id.lembreteBIO);

        SharedPreferences preference = getSharedPreferences("LetsGO", Context.MODE_PRIVATE);
        lembreteBIO.setText(preference.getString("lembrete", "Sem lembretes!"));

        salvarlembreteBIO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View mView = getLayoutInflater().inflate(R.layout.dialog_lembrete, null);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainPerfil.this);

                btn_definiralarme = (Button) mView.findViewById(R.id.btn_definiralarme);
                btn_definirlembrete = (Button) mView.findViewById(R.id.btn_definirlembrete);
                et_lembrete = (EditText) mView.findViewById(R.id.et_lembrete);

                btn_definiralarme.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(MainPerfil.this, "Lembrete salvo!", Toast.LENGTH_LONG).show();

                        String lembreteee = et_lembrete.getText().toString();
                        lembreteBIO.setText(lembreteee);

                        SharedPreferences preference = getSharedPreferences("LetsGO", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preference.edit();
                        editor.putString("lembrete", (lembreteee));
                        editor.commit();

                        //============================Manda pra tela de alarme=========================
                        Intent corrida = new Intent(getApplicationContext(), MainAlarmeLembrete.class);
                        //ENVIA A MENSAGEM PARA A OUTRA TELA, ONDE SERÁ MOSTRADO NA TELA DO PASSAGEIRO AGUARDANDO
                        corrida.putExtra("lembrete_alarme", lembreteBIO.getText().toString());

                        corrida.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(corrida);
                        finish();

                    }
                });

                btn_definirlembrete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainPerfil.this, "Lembrete salvo!", Toast.LENGTH_LONG).show();

                        String lembreteee = et_lembrete.getText().toString();
                        lembreteBIO.setText(lembreteee);

                        SharedPreferences preference = getSharedPreferences("LetsGO", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preference.edit();
                        editor.putString("lembrete", (lembreteee));
                        editor.commit();
                    }
                });

                /*
                mBuilder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(MainPerfil.this, "Lembrete salvo!", Toast.LENGTH_LONG).show();

                        String lembreteee = et_lembrete.getText().toString();
                        lembreteBIO.setText(lembreteee);

                        SharedPreferences preference = getSharedPreferences("LetsGO", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preference.edit();
                        editor.putString("lembrete", (lembreteee));
                        editor.commit();

                    }
                });

                mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                */

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });


        opcoes_perf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //CHAMA O XML DIALOG_OPCOESPERFIL
                View mView = getLayoutInflater().inflate(R.layout.dialog_opcoesperfil, null);

                Button btn_editar = (Button) mView.findViewById(R.id.btn_editar);
                Button btn_mudarsenha = (Button) mView.findViewById(R.id.btn_mudarsenha);
                Button btn_historicocorridas = (Button) mView.findViewById(R.id.btn_historicocorridas);
                Button btn_atualizatoken = (Button) mView.findViewById(R.id.btn_atualizatoken);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainPerfil.this);
                //mBuilder.setIcon(R.drawable.logomarcalets).setTitle("ESCOLHA");
                mBuilder.setCancelable(false);

                btn_editar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent i = new Intent(getApplicationContext(), MainCRUDUpdateData.class);

                        String id_, nome, telefone, email, data_nasc, sexo, cpf, cep, endereco, num,
                                complemento, bairro, uf, cidade, cnh, placa_veiculo, alvara, num_cracha,
                                agencia, operacao, conta = "";

                        id_ = mostraId.getText().toString();
                        nome = mostranome.getText().toString();
                        telefone = mostratelefone.getText().toString();
                        email = mostraemail.getText().toString();
                        data_nasc = mostradata_nasc.getText().toString();
                        sexo = mostrasexo.getText().toString();
                        cpf = mostracpf.getText().toString();
                        cep = mostracep.getText().toString();
                        endereco = mostraendereco.getText().toString();
                        num = mostranum.getText().toString();
                        complemento = mostracomplemento.getText().toString();
                        bairro = mostrabairro.getText().toString();
                        uf = mostrauf.getText().toString();
                        cidade = mostracidade.getText().toString();
                        cnh = mostracnh.getText().toString();
                        placa_veiculo = mostraplaca_veiculo.getText().toString();
                        alvara = mostraalvara.getText().toString();
                        num_cracha = mostranum_cracha.getText().toString();
                        agencia = mostraagencia.getText().toString();
                        operacao = mostraoperacao.getText().toString();
                        conta = mostraconta.getText().toString();

                        Bundle bundle = new Bundle();

                        bundle.putString("id", id_);
                        bundle.putString("nome", nome);
                        bundle.putString("telefone", telefone);
                        bundle.putString("email", email);
                        bundle.putString("data_nasc", data_nasc);
                        bundle.putString("sexo", sexo);
                        bundle.putString("cpf", cpf);
                        bundle.putString("cep", cep);
                        bundle.putString("endereco", endereco);
                        bundle.putString("num", num);
                        bundle.putString("complemento", complemento);
                        bundle.putString("bairro", bairro);
                        bundle.putString("uf", uf);
                        bundle.putString("cidade", cidade);
                        bundle.putString("cnh", cnh);
                        bundle.putString("placa_veiculo", placa_veiculo);
                        bundle.putString("alvara", alvara);
                        bundle.putString("num_cracha", num_cracha);
                        bundle.putString("agencia", agencia);
                        bundle.putString("operacao", operacao);
                        bundle.putString("conta", conta);


                        i.putExtras(bundle);
                        startActivity(i);

                    }
                });

                btn_mudarsenha.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        showDialog();

                    }
                });

                btn_historicocorridas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent teladecorrida = new Intent(MainPerfil.this, MainHistoricoC.class);
                        startActivity(teladecorrida);

                    }
                });

                btn_atualizatoken.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pd = ProgressDialog.show(MainPerfil.this, "", "Atualizando token de Notificação...");

                        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                        String token_id = pref.getString("regId", null);

                        mostratoken.setText(token_id);
                        atualizaToken(mostraId.getText().toString());

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

            }
        });


    }
    //=====================================MUDAR SENHA=====================================================

    private void showDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainPerfil.this);
        LayoutInflater inflater = MainPerfil.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_muda_senha, null);
        et_old_password = (EditText)view.findViewById(R.id.et_old_password);
        et_new_password = (EditText)view.findViewById(R.id.et_new_password);
        tv_message = (TextView)view.findViewById(R.id.tv_message);
        progress = (ProgressBar)view.findViewById(R.id.progress);
        builder.setView(view);
        builder.setTitle("Mudar Senha");
        builder.setPositiveButton("Mudar Senha", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old_password = et_old_password.getText().toString();
                String new_password = et_new_password.getText().toString();
                if(!old_password.isEmpty() && !new_password.isEmpty()){

                    progress.setVisibility(View.VISIBLE);
                    changePasswordProcess(end_email.getText().toString(),old_password,new_password);

                }else {

                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText("Os campos estão vazios!");
                }
            }
        });
    }


    private void changePasswordProcess(String email,String old_password,String new_password){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setOld_password(old_password);
        user.setNew_password(new_password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.CHANGE_PASSWORD_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new retrofit2.Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)){
                    progress.setVisibility(View.GONE);
                    tv_message.setVisibility(View.GONE);
                    dialog.dismiss();
                    Snackbar.make(getCurrentFocus(), resp.getMessage(), Snackbar.LENGTH_LONG).show();

                }else {
                    progress.setVisibility(View.GONE);
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText(resp.getMessage());

                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

                Log.d(Constants.TAG,"failed");
                progress.setVisibility(View.GONE);
                tv_message.setVisibility(View.VISIBLE);
                tv_message.setText(t.getLocalizedMessage());


            }
        });


    }



    //====================================================================================================


    // This method is called to display a dialog listview when youclick the button
    // OPÇÕES PERFIL
    public void showDialogListView(){

        AlertDialog.Builder builder=new AlertDialog.Builder(MainPerfil.this);
        builder.setCancelable(true);
        builder.setPositiveButton("OK",null);
        builder.setView(listView);
        AlertDialog dialog=builder.create();
        dialog.show();

    }






    //================================VERIFICA SE O SISTEMA É COMPATÍVEL===============================
    private boolean mayRequestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)  //VERIFICA SE NOSSO SITEMA PERMITE
            return true;
        //================================PERMISSÃO PARA ACESSAR A CAMERA==================================
        if ((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
            return true;

        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))) {
            Snackbar.make(mRLView, "Permissões são necessárias para usar a aplicação!",
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View view) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
                }
            }).show();
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }

        return false;
    }
    //==================================MOSTRA A MENSAGEM DE ESCOLHA DE OPÇÕES==========================
/*  // FUNÇÕES DA CAMERA...
    private void showOptions() {        //Declarando AlertDialog

        final CharSequence[] option = {"Câmera", "Galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainPerfil.this);

        builder.setTitle("Escolha uma opção");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (option[which] == "Câmera") {
                    openCamera();
                } else if (option[which] == "Galeria") {      //Manda pro armazen.... do dispositivo
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*"); //Todas as extensões de imagens
                    startActivityForResult(Intent.createChooser(intent, "Selecione app de imagens"), SELECT_PICTURE);
                } else {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    //======================================ABRE A CAMERA========================================
    private void openCamera() {     //aRMAZENA.. INTERNO DO DISPOSITIVO

        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if (!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs();

        if (isDirectoryCreated) {
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";   //Para que os nomes não se repitam
            //Especifica onde será guaraddo a imagem
            mPath = Environment.getExternalStorageDirectory() + File.separator +
                    MEDIA_DIRECTORY + File.separator + imageName;

            File newFile = new File(mPath);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    //COM ISSO, QUEREMOS ABRIR A CAMERA!!!
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
            startActivityForResult(intent, PHOTO_CODE);
        }
    }
*/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file_path", mPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPath = savedInstanceState.getString("file_path");
    }


    //============================================VALIDA O ACESSO A CAMERA=============================================
    @Override       //Captura as respostas                          Intent data
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {   //Isso depende da chamada da camera no método OpenCamrea()
                case PHOTO_CODE:                        //mPath tem a rota de onde estão as imagens
                /*
                    MediaScannerConnection.scanFile(this, new String[]{mPath}, null,   //mPath tem a rota de onde estão guardadas as imagens
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned" + path + ":");
                                    Log.i("ExternalStorage", "-> Uri = " + uri);
                                }
                            });

                    Bitmap bitmap = BitmapFactory.decodeFile(mPath);    //Já reconhece o destino da foto
                    img_perfil.setImageBitmap(bitmap);
                */
                    break;
                case SELECT_PICTURE:

                    //=============IMAGENS============
                    final Bitmap[] imagem = new Bitmap[1];
                    //=====================YES OK!============================

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainPerfil.this);
                    builder.setMessage("Mudar para imagem atual?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // FIRE ZE MISSILES!

                                    Uri path = data.getData();
                                    img_perfil.setImageURI(path);

                                    imagem[0] = ((BitmapDrawable) img_perfil.getDrawable()).getBitmap();
                                    //name = uploadImageName.getText().toString();
                                    new UploadImage(imagem[0], mostraId.getText().toString()).execute();

                                }
                            })
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog


                                }
                            });
                    // Create the AlertDialog object and return it
                    alerta = builder.create();
                    alerta.show();

                    break;
            }
        }
    }


    //==================================MÉTODOS DE PERMISSÕES PARA VERSÕES APARTIR DE 6.0=============================================
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainPerfil.this, "Permissões aceitas!", Toast.LENGTH_SHORT).show();
                button.setEnabled(true);
            } else {
                showExplanation();
            }
        }

    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainPerfil.this);
        builder.setTitle("Permissões negadas!");
        builder.setMessage("Para acessar as funções do app, é preciso aceitar as permissões");
        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {        //Digamos que abre as configurações para acessar permissões
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("Package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                finish();
            }
        });
        builder.show();
    }




    //================ ENVIA PARA O BANCO DE DADOS = TOKEN ATUALIZADO ==============

    //String BASE_URL = "http://www.letsgosobral.esy.es/firebase_TokeneStatus/updateData.php";

    //String BASE_URL = "http://www.letsgosobral.esy.es/taxi/status_Taxista/atualizaToken.php";
    //TROCADO POR
    String BASE_URL = "http://www.letsgosobral.com/taxi/status_Taxista/atualizaToken.php";

    public void atualizaToken(final String taxi_id_unic) {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL) //Setting the Root URL
                .build();

        AppConfig.atualizaTokenN api = adapter.create(AppConfig.atualizaTokenN.class);

        api.atualizaTokenDeN(
                taxi_id_unic,
                mostratoken.getText().toString(),

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
                                Toast.makeText(MainPerfil.this, "Token atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                                pd.dismiss();

                            } else {
                                Toast.makeText(MainPerfil.this, "Verifique sua conexão!", Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(MainPerfil.this, error.toString(), Toast.LENGTH_LONG).show();
                        Toast.makeText(MainPerfil.this, "Verifique sua conexão!", Toast.LENGTH_LONG).show();
                        pd.cancel();
                    }
                }
        );

    }

    //============================== ATUALIZA - TOKEN ==============================





    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
    //=-=-=-=-=-=|-=-=-=-=-=-=|PEGA OS DADOS DO USUÁRIO PELO ID_UNICO|=-=-=-=-=-=-=-|=-=-=-=-=-=-=-

    public void MostraDadosdoTaxista(final String taxi_id_unic) {

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

                                    //mostraId.setText(jo.getString("id"));
                                    mostranome.setText(jo.getString("taxi_nome"));  //nome
                                    mostratelefone.setText(jo.getString("taxi_fone"));   //telefone
                                    mostraemail.setText(jo.getString("taxi_email"));   //email
                                    mostradata_nasc.setText(jo.getString("taxi_nascimento"));   //data_nasc
                                    mostrasexo.setText(jo.getString("taxi_sexo"));            //sexo
                                    mostracpf.setText(jo.getString("taxi_cpf"));            //cpf
                                    mostracep.setText(jo.getString("taxi_cep"));             //cep
                                    mostraendereco.setText(jo.getString("taxi_endereco"));    //endereco
                                    mostranum.setText(jo.getString("taxi_num"));             //num
                                    mostracomplemento.setText(jo.getString("taxi_complemento"));  //complemento
                                    mostrabairro.setText(jo.getString("taxi_bairro"));           //bairro
                                    mostrauf.setText(jo.getString("taxi_uf"));               //uf
                                    mostracidade.setText(jo.getString("taxi_cidade"));         //cidade
                                    mostracnh.setText(jo.getString("taxi_cnh"));               //cnh
                                    mostraplaca_veiculo.setText(jo.getString("taxi_placa")); //placa_veiculo
                                    mostraalvara.setText(jo.getString("taxi_alvara"));           //alvara
                                    mostranum_cracha.setText(jo.getString("taxi_cracha"));     //num_cracha
                                    mostraagencia.setText(jo.getString("taxi_agencia"));           //agencia
                                    mostraoperacao.setText(jo.getString("taxi_operacao"));          //operacao
                                    mostraconta.setText(jo.getString("taxi_conta"));                //conta
                                    mostratoken.setText(jo.getString("taxi_token"));

                                    /*
                                    //mostraId.setText(jo.getString("id"));
                                    mostranome.setText(jo.getString("nome"));  //
                                    mostratelefone.setText(jo.getString("telefone"));   //
                                    mostraemail.setText(jo.getString("email"));   //
                                    mostradata_nasc.setText(jo.getString("data_nasc"));   //
                                    mostrasexo.setText(jo.getString("sexo"));            //
                                    mostracpf.setText(jo.getString("cpf"));            //
                                    mostracep.setText(jo.getString("cep"));             //
                                    mostraendereco.setText(jo.getString("endereco"));    //
                                    mostranum.setText(jo.getString("num"));             //
                                    mostracomplemento.setText(jo.getString("complemento"));  //
                                    mostrabairro.setText(jo.getString("bairro"));           //
                                    mostrauf.setText(jo.getString("uf"));               //
                                    mostracidade.setText(jo.getString("cidade"));         //
                                    mostracnh.setText(jo.getString("cnh"));               //
                                    mostraplaca_veiculo.setText(jo.getString("placa_veiculo"));
                                    mostraalvara.setText(jo.getString("alvara"));
                                    mostranum_cracha.setText(jo.getString("num_cracha"));
                                    mostraagencia.setText(jo.getString("agencia"));
                                    mostraoperacao.setText(jo.getString("operacao"));
                                    mostraconta.setText(jo.getString("conta"));
                                    */

                                }

                                //details_list.setAdapter(displayAdapter);
                                progress.setVisibility(View.INVISIBLE);
                                //pDialog.dismiss();

                            } else {
                                Toast.makeText(getApplicationContext(), "Sem registros encontrados!", Toast.LENGTH_SHORT).show();
                                //pDialog.cancel();
                                progress.setVisibility(View.INVISIBLE);
                            }
                        } catch (JSONException e) {
                            Log.d("exception", e.toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("Failure", error.toString());
                        Toast.makeText(MainPerfil.this, error.toString(), Toast.LENGTH_LONG).show();
                        progress.setVisibility(View.INVISIBLE);
                        //pDialog.cancel();
                    }
                }
        );


    }




    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
    //=-=-=-=-=-=|-=-=-=|ENVIA IMAGEM DA GALERIA PARA A PASRA NO SERVIDOR|=-=-=-=-=-|=-=-=-=-=-=-=-

    private class UploadImage extends AsyncTask<Void, Void, Void> {

        Bitmap imagem;
        String nome;

        public UploadImage(Bitmap imagem, String nome){
            this.imagem = imagem;
            this.nome = nome;
        }

        @Override
        protected Void doInBackground(Void... params) {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imagem.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("imagem", encodeImage));
            dataToSend.add(new BasicNameValuePair("nome", nome));

            HttpParams httpRequestParams = getHttpRequestParams();

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            //HttpPost post = new HttpPost(SERVER_ADDRESS + "cad_Taxista/imagens_Taxista/SalvaImagem.php");

            //LINK COMENTADO PARA TESTAR O OUTRO ABAIXO
            //HttpPost post = new HttpPost(SERVER_ADDRESS + "cad_Taxista/imagens_Taxista/UploadImagem.php");

            HttpPost post = new HttpPost(SERVER_ADDRESS + "taxi/imagens_Taxista/UploadImagem.php");
            try{

                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(MainPerfil.this, "Foto de perfil alterada com sucesso", Toast.LENGTH_SHORT).show();
        }
    }
    //==============================================================================

    private HttpParams getHttpRequestParams(){
        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams, 1000 * 30);
        HttpConnectionParams.setSoTimeout(httpRequestParams, 1000 * 30);
        return httpRequestParams;
    }
    //===========================CONT. IMAGEM UPLOAD===============================


    //========================PEGA A IMAGEM DE ACORDO COM ID_UNICO======================
    private class DownloadImage extends AsyncTask<Void, Void, Bitmap> {
        String nome;

        public DownloadImage(String nome){
            this.nome = nome;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            //COMENTADO PARA TESTAR O DE BAIXO
            //String url = SERVER_ADDRESS + "cad_Taxista/imagens_Taxista/foto_perfil/" + nome + ".JPG";
            String url = SERVER_ADDRESS + "taxi/imagens_Taxista/foto_perfil/" + nome + ".JPG";

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
                img_perfil.setImageBitmap(bitmap);
                progress.setVisibility(View.INVISIBLE);
                //pDialog.dismiss();
            }
        }

    }
    //=============================DA IMAGEM DA PASTA DO BD============================

}