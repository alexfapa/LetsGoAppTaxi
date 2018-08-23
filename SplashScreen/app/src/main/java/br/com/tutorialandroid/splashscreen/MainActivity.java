package br.com.tutorialandroid.splashscreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.com.tutorialandroid.splashscreen.HoraDeTrabalho.MainHoraDeTrabalho;
import br.com.tutorialandroid.splashscreen.executaAppEmSegundoPlano.service;
import br.com.tutorialandroid.splashscreen.loginActivity.Constants;
import br.com.tutorialandroid.splashscreen.loginActivity.RedefineSenhaFragment;
import br.com.tutorialandroid.splashscreen.loginActivity.RequestInterface;
import br.com.tutorialandroid.splashscreen.loginActivity.conexoes.ServerRequest;
import br.com.tutorialandroid.splashscreen.loginActivity.conexoes.ServerResponse;
import br.com.tutorialandroid.splashscreen.loginActivity.conexoes.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    //==========TESTE DE SHAREDPREFERENCES========
    private static final String PREF_NAME = "MainActivityPreferences";
    //============================================

    //private static final String TAG = "MainActivity";

    private Button bt_entrar, btn_esqueceu, btn_confirma;
    private EditText nome_usu, senha_usu, et_esqemail;
    private Button txtcadastro;
    private TextView data;
    //private ImageButton imageButton;

    String url = "";
    String paramentros = "";

    //||||||||||LOGIN COM SHAREDPREFERENCES||||||||||||
    private SharedPreferences pref;
    private ProgressBar progress;

    //=======================================MÉTODO PRINCIPAL==========================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(getBaseContext(), service.class));


        //============VERIFICA SHAREDPREFERENCES=========
        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String email = sp.getString("email", "");
        String senha = sp.getString("senha", "");

        if(!email.isEmpty() && !senha.isEmpty()) {
            loginProcess(email,senha);
        }
        //===============================================


        pref = MainActivity.this.getPreferences(0);

        //================ANIMAÇÃO DE PASSAGEM DE TELA====================
        overridePendingTransition(R.anim.activity_entra, R.anim.activity_sai);

        //imageButton = (ImageButton) findViewById(R.id.imageButton);
        nome_usu = (EditText) findViewById(R.id.nome_usu);
        senha_usu = (EditText) findViewById(R.id.senha_usu);
        txtcadastro = (Button) findViewById(R.id.txtcadastro);
        bt_entrar = (Button) findViewById(R.id.bt_entrar);
        btn_esqueceu = (Button) findViewById(R.id.btn_esqueceu);
        progress = (ProgressBar) findViewById(R.id.progress);
        //data = (TextView) findViewById(R.id.data);
        //=============================================================================================


        //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        //data.setText("Hoje é: " + sdf.format(data.getDrawingTime()));

        //Toast.makeText(MainActivity.this, "Data:" + sdf.format(data.getTime()), Toast.LENGTH_SHORT).show();


        //============================EVENTO DO BOTÃO ENTRAR=============================
        bt_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        //|||||||||||||ENVIA DADOS PARA O MÉTODO loginProcess||||||||

                CheckBox saveLogin = (CheckBox) findViewById(R.id.saveLogin);
                String email = nome_usu.getText().toString();
                String senha = senha_usu.getText().toString();

                if(!email.isEmpty() && !senha.isEmpty()) {

                    if(saveLogin.isChecked()){
                        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();

                        editor.putString("email", email);
                        editor.putString("senha", senha);
                        editor.commit();
                    }

                    progress.setVisibility(View.VISIBLE);
                    loginProcess(email,senha);

                } else {

                    Snackbar.make(getCurrentFocus(), "Os campos estão vazios!", Snackbar.LENGTH_LONG).show();
                }

        //||||||||||||||||||||||||||||||||||||||||||||||||||||||||||

/*
                // ESTE COMENTÁRIO É OCÓDIGO ANTERIOR AO QUE ESTÁ ACIMA ^^^^
                EditText login = (EditText) findViewById(R.id.nome_usu);
                String campotxt = login.getText().toString();

                EditText senha = (EditText) findViewById(R.id.senha_usu);
                String camposen = senha.getText().toString();

                if (campotxt.equals("") || campotxt == null) {
                    login.setError("Por favor, preencha este campo!");
                } else
                    if (camposen.equals("") || camposen == null) {
                        senha.setError("Por favor, preencha este campo!");
                    } else {
                        Intent telamap = new Intent(MainActivity.this, MainMapActivity.class);
                        startActivity(telamap);
                    }
*/
            }
        });

        //=====================EVENTO DE CLIQUE NO BOTÃO CADASTRAR=====================
        TextView botao = (TextView) findViewById(R.id.txtcadastro);

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent it2 = new Intent(MainActivity.this, MainCadActivity.class);
                //startActivity(it2);

                //ISSO CHAMA A ACTIVITY CADASTRO "INTENT NORMALIZA O FUNCIONAMENTO DO OnBackPressed"
                Intent meuIntent = new Intent(MainActivity.this, MainCadActivity.class);
                startActivityForResult(meuIntent, 1);
            }
        });


        btn_esqueceu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent redefinesenha = new Intent(MainActivity.this, RedefineSenhaFragment.class);
                startActivity(redefinesenha);

                /*
                View mView = getLayoutInflater().inflate(R.layout.dialog_esq_senha, null);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                mBuilder.setTitle("ESQUECEU A SENHA?");

                btn_confirma = (Button) mView.findViewById(R.id.btn_confirma);
                et_esqemail = (EditText) mView.findViewById(R.id.et_esqemail);

                btn_confirma.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (et_esqemail.getText().toString().isEmpty()) {

                            Toast.makeText(MainActivity.this, "Usuário! Preencha o campo email.", Toast.LENGTH_LONG).show();

                            et_esqemail.setError("Por favor, preencha este campo!");
                        }else {
                            Toast.makeText(MainActivity.this, "Usuário! enviamos uma mensagem para o seu email.\n Verifique!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
                */
            }
        });


    }


    private void loginProcess(String email,String senha){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setSenha(senha);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.LOGIN_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                Snackbar.make(getCurrentFocus(), resp.getMessage(), Snackbar.LENGTH_LONG).show();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(Constants.IS_LOGGED_IN,true);
                    editor.putString(Constants.EMAIL,resp.getUser().getEmail());
                    editor.putString(Constants.NOME,resp.getUser().getNome());
                    editor.putString(Constants.ID_UNICO,resp.getUser().getId_unico());
                    editor.apply();
                    goToProfile();

                }
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

                progress.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG,"failed: " + t.getLocalizedMessage());
                //Snackbar.make(getCurrentFocus(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                Snackbar.make(getCurrentFocus(), "Verifique sua conexão", Snackbar.LENGTH_LONG).show();

            }
        });
    }

    private void goToProfile(){

        Intent telaPrincipal = new Intent(MainActivity.this, MainMapActivity.class);
        //Intent telaPrincipal = new Intent(MainActivity.this, MainHoraDeTrabalho.class);

        String ID_, Nome_, Email_;
        ID_ = (pref.getString(Constants.ID_UNICO,""));
        Nome_ =  (pref.getString(Constants.NOME,""));
        Email_ = (pref.getString(Constants.EMAIL,""));


        Bundle bundle = new Bundle();
        bundle.putString("ID_UNI", ID_);
        bundle.putString("Nome", Nome_);
        bundle.putString("Email", Email_);

        telaPrincipal.putExtras(bundle);

        telaPrincipal.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(telaPrincipal);

        finish();
    }



    //ESSE MÉTODO RETORNA O VALOR ENVIADO DE OUTRAS ACTIVITYS "INTENT NORMALIZA O FUNCIONAMENTO DO OnBackPressed""
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //retorno para a Activity, se quiser retornar algum valor da outra
        //Activity, isso tambem é possivel
    }

}