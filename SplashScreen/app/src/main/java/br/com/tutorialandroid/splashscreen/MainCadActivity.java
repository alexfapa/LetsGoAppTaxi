package br.com.tutorialandroid.splashscreen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.tutorialandroid.splashscreen.app.Config;
import br.com.tutorialandroid.splashscreen.executaAppEmSegundoPlano.service;
import br.com.tutorialandroid.splashscreen.loginActivity.Constants;
import br.com.tutorialandroid.splashscreen.loginActivity.RequestInterface;
import br.com.tutorialandroid.splashscreen.loginActivity.conexoes.ServerRequest;
import br.com.tutorialandroid.splashscreen.loginActivity.conexoes.ServerResponse;
import br.com.tutorialandroid.splashscreen.loginActivity.conexoes.User;
import br.com.tutorialandroid.splashscreen.util.NotificationUtils;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.params.HttpParams;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;

/**
 * Created by Renato Almeida on 06/10/2016.
 */
public class MainCadActivity extends Activity {

    //=====================URL DE CONEXÃO PARA ENVIO DE DADOS===========================
    //String BASE_URL = "http://www.letsgosobral.esy.es/cadTaxista/insertData.php";

    //=============================LINK QUASE OFICIAL===================================
    String BASE_URL = "http://www.letsgosobral.esy.es/taxi/insertData.php";
    //==================================================================================

    private ProgressDialog pDialog = null;

    //================IMAGENS DA CAMERA E DA GALERIA========================
    private String APP_DIRECTORY = "LetsGOApp/";    //CRIA PASTA NA MEMÓRIA INTERNA DO DISPOSITIVO
    private String MEDIA_DIRECTORY = APP_DIRECTORY + "LetsGO Imagens";     //PASTA LOCALIZADA DENTRO DA "LetsGOApp/"

    //CONSTANTES
    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int PHOTO_CODE_C = 201;
    private final int PHOTO_CODE_O = 202;
    private final int PHOTO_CODE_D = 203;
    private final int PHOTO_CODE_E = 204;
    private final int SELECT_PICTURE = 100;
    private final int SELECT_PICTURE_P = 110;
    private final int SELECT_PICTURE_I = 120;
    private final int SELECT_PICTURE_C = 130;
    private final int SELECT_PICTURE_T = 140;

    private ImageView image1, image2, image3, image4, img_cartao;
    private ImageButton button, button1, button2, button3, button4;
    private RelativeLayout lay_cadastro;
    private TextView termos_de_uso, pegasexo;
    private CheckBox cb_termos_de_uso;


    private String mPath;

    //======================================================================
/*
    public static final int CAMERA_REQUEST = 10;
    public static final int CAMERA_REQUESTT = 10;
    public static final int CAMERA_REQUESTY = 10;
    public static final int CAMERA_REQUESTV = 10;
*/
    private EditText edNome, edPhone, edEmail, edSenha, edNasc, edCEP,
            edEnd, edEndNum, edComp, edBairro, edUF, edCidade, edCPF,
            edCNH, edPlacacarro, edAlvara, edCracha, edAgencia, edOperacao, edConta;
    private Button bt_cancelar, bt_confirmar, bt_image;
    private RadioGroup rgsexo;
    private RadioButton rbM, rbF;
    //private ImageButton imageButton;

    private TextWatcher cpfMask;
    private TextWatcher cnhMask;
    private TextWatcher nascMask;
    private TextWatcher cepMask;
    private TextWatcher ufMask;
    private TextWatcher phoneMask;
    private TextWatcher carroMask;
    private TextWatcher alvaraMask;
    private TextWatcher agenciaMask;
    private TextWatcher operacaoMask;
    private TextWatcher contaMask;

    //private static final String SERVER_ADDRESS = "http://www.letsgosobral.esy.es/";
    //TROCADO POR
    private static final String SERVER_ADDRESS = "http://www.letsgosobral.com/";

    String url = "";
    String paramentros = "";

    private AlertDialog alerta;
    private ProgressBar progressAnexo;
    private ProgressDialog progDialog = null;

    //CONSTANTE SITUAÇÃO
    private final int SITUACAO_TAXI = 0;

    //|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||

    //===============================METODO DO FIREBASE============================
    private static final String TAG = br.com.tutorialandroid.splashscreen.activity.MainActiviity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtRegId, txtMessage;


    //======================MÉTODO PRINCIPAL=========================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cad);
        startService(new Intent(this, service.class));

        //====================ANIMAÇÃO ENTRE TELAS=====================
        overridePendingTransition(R.anim.activity_entra, R.anim.activity_sai);


        //========================DO FIREBASE==========================
        txtRegId = (TextView) findViewById(R.id.txt_reg_id);
        progressAnexo = (ProgressBar) findViewById(R.id.progressAnexo);


        pegasexo = (TextView) findViewById(R.id.pegasexo);
        // bt_image = (Button) findViewById(R.id.bt_image);
        rgsexo = (RadioGroup) findViewById(R.id.sexoselected);
        //rbM = (RadioButton) findViewById(R.id.rbM);
        //rbF = (RadioButton) findViewById(R.id.rbF);

        termos_de_uso = (TextView) findViewById(R.id.termos_de_uso);
        cb_termos_de_uso = (CheckBox) findViewById(R.id.cb_termos_de_uso);

        edPlacacarro = (EditText) findViewById(R.id.edPlacacarro);
        carroMask = MascaraCampos.insert("###-####", edPlacacarro);
        edPlacacarro.addTextChangedListener(carroMask);

        edAlvara = (EditText) findViewById(R.id.edAlvara);
        alvaraMask = MascaraCampos.insert("########", edAlvara);
        edAlvara.addTextChangedListener(alvaraMask);

        edCracha = (EditText) findViewById(R.id.edCracha);

        //edAgencia = (EditText) findViewById(R.id.edAgencia);
        //agenciaMask = MascaraCampos.insert("####", edAgencia);
        //edAgencia.addTextChangedListener(agenciaMask);

        //edOperacao = (EditText) findViewById(R.id.edOperacao);
        //operacaoMask = MascaraCampos.insert("###", edOperacao);
        //edOperacao.addTextChangedListener(operacaoMask);

        //edConta = (EditText) findViewById(R.id.edConta);
        //contaMask = MascaraCampos.insert("#####-#", edConta);
        //edConta.addTextChangedListener(contaMask);

        edCNH = (EditText) findViewById(R.id.edCNH);
        cnhMask = MascaraCampos.insert("###########", edCNH);
        edCNH.addTextChangedListener(cnhMask);

        edNasc = (EditText) findViewById(R.id.edNasc);
        nascMask = MascaraCampos.insert("##/##/####", edNasc);
        edNasc.addTextChangedListener(nascMask);

        edCEP = (EditText) findViewById(R.id.edCEP);
        cepMask = MascaraCampos.insert("##.###-###", edCEP);
        edCEP.addTextChangedListener(cepMask);

        edEnd = (EditText) findViewById(R.id.edEnd);
        edEndNum = (EditText) findViewById(R.id.edEndNum);
        edComp = (EditText) findViewById(R.id.edComp);
        edBairro = (EditText) findViewById(R.id.edBairro);

        edUF = (EditText) findViewById(R.id.edUF);
        ufMask = MascaraCampos.insert("##", edUF);
        edUF.addTextChangedListener(ufMask);

        edCidade = (EditText) findViewById(R.id.edCidade);
        edNome = (EditText) findViewById(R.id.edNome);

        edCPF = (EditText) findViewById(R.id.edCPF);
        cpfMask = MascaraCampos.insert("###.###.###-##", edCPF);
        edCPF.addTextChangedListener(cpfMask);

        edEmail = (EditText) findViewById(R.id.edEmail);

        edPhone = (EditText) findViewById(R.id.edPhone);
        phoneMask = MascaraCampos.insert("(##)#.####-####", edPhone);
        edPhone.addTextChangedListener(phoneMask);

        edSenha = (EditText) findViewById(R.id.edSenha);

        bt_cancelar = (Button) findViewById(R.id.bt_cancelar);
        bt_confirmar = (Button) findViewById(R.id.bt_confirmar);


        //=========================Identificadores do layOut XML========================

        button4 = (ImageButton) findViewById(R.id.bt_cartao);
        button = (ImageButton) findViewById(R.id.bt_image);
        button1 = (ImageButton) findViewById(R.id.bt_image1);
        button2 = (ImageButton) findViewById(R.id.bt_image2);
        button3 = (ImageButton) findViewById(R.id.bt_image3);
        lay_cadastro = (RelativeLayout) findViewById(R.id.lay_cadastro);
        //==============================================================================

        //Imagem trazida da camera!
        img_cartao = (ImageView) findViewById(R.id.img_cartao);
        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);
        image4 = (ImageView) findViewById(R.id.image4);

        bt_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Cria o gerador do AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainCadActivity.this);
                //define o titulo
                builder.setTitle("CANCELAR!");
                //define a mensagem
                builder.setMessage("Deseja realmente cancelar?");
                //define um botão como positivo
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(MainCadActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });
                //define um botão como negativo.
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(MainCadActivity.this, "Você não está livre", Toast.LENGTH_SHORT).show();// + arg1, Toast.LENGTH_SHORT).show();
                    }
                });
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();
            }
        });

        bt_confirmar.setEnabled(false);
        cb_termos_de_uso.setEnabled(true);

        //====================EVENTO DE CLIQUE DO BOTÃO CONFIRMAR======================
        bt_confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bt_confirmar.setEnabled(false);

                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                String token_id = pref.getString("regId", null);
                //comenta isso depois e deixa só a string token_id,para chamar no metodo abaixo
                //sendIdToServer(token_id);
/*
                RadioGroup group = (RadioGroup) findViewById(R.id.sexoselected);
                int idSelect = group.getCheckedRadioButtonId();
                RadioButton radiobutton = (RadioButton) findViewById(idSelect);

                radiobutton.getText().toString();
*/
                double situacao_taxi = SITUACAO_TAXI;
                //============================================================//
                String sexo = pegasexo.getText().toString();

                //========================RADIOGROUP==========================//
                EditText nome = (EditText) findViewById(R.id.edNome);
                String camponome = nome.getText().toString();

                EditText tel = (EditText) findViewById(R.id.edPhone);
                String campotel = tel.getText().toString();

                EditText email = (EditText) findViewById(R.id.edEmail);
                String campoemail = email.getText().toString();

                EditText senha = (EditText) findViewById(R.id.edSenha);
                String camposenha = senha.getText().toString();

                EditText nasc = (EditText) findViewById(R.id.edNasc);
                String camponasc = nasc.getText().toString();

                EditText CEP = (EditText) findViewById(R.id.edCEP);
                String campocep = CEP.getText().toString();

                EditText end = (EditText) findViewById(R.id.edEnd);
                String campoend = end.getText().toString();

                EditText num = (EditText) findViewById(R.id.edEndNum);
                String camponum = num.getText().toString();

                EditText comp = (EditText) findViewById(R.id.edComp);
                String campocomp = comp.getText().toString();

                EditText bairro = (EditText) findViewById(R.id.edBairro);
                String campobairro = bairro.getText().toString();

                EditText uf = (EditText) findViewById(R.id.edUF);
                String campouf = uf.getText().toString();

                EditText cidade = (EditText) findViewById(R.id.edCidade);
                String campocid = cidade.getText().toString();

                EditText cpf = (EditText) findViewById(R.id.edCPF);
                String campocpf = cpf.getText().toString();

                EditText cnh = (EditText) findViewById(R.id.edCNH);
                String campocnh = cnh.getText().toString();

                EditText placa_veiculo = (EditText) findViewById(R.id.edPlacacarro);
                String campoplaca = placa_veiculo.getText().toString();

                EditText alvara = (EditText) findViewById(R.id.edAlvara);
                String campoalvara = alvara.getText().toString();

                EditText num_cracha = (EditText) findViewById(R.id.edCracha);
                String campocracha = num_cracha.getText().toString();

                EditText agencia = (EditText) findViewById(R.id.edAgencia);
                String campoagencia = agencia.getText().toString();

                EditText operacao = (EditText) findViewById(R.id.edOperacao);
                String campooperacao = operacao.getText().toString();

                EditText conta = (EditText) findViewById(R.id.edConta);
                String campoconta = conta.getText().toString();

                CheckBox termos_de_uso = (CheckBox) findViewById(R.id.cb_termos_de_uso);
                String campotermos = termos_de_uso.getText().toString();

                if (camponome.equals("") || camponome == null) {
                    nome.setError("Por favor, preencha este campo!");
                } else {
                    if (campotel.equals("") || campotel == null) {
                        tel.setError("Por favor, preencha este campo!");
                    } else {
                        if (campoemail.equals("") || campoemail == null) {
                            email.setError("Por favor, preencha este campo!");
                        } else {
                            if (camposenha.equals("") || camposenha == null) {
                                senha.setError("Por favor, preencha este campo!");
                            } else {
                                if (camponasc.equals("") || camponasc == null) {
                                    nasc.setError("Por favor, preencha este campo!");
                                } else {
                                    if (campocep.equals("") || campocep == null) {
                                        CEP.setError("Por favor, preencha este campo!");
                                    } else {
                                        if (campoend.equals("") || campoend == null) {
                                            end.setError("Por favor, preencha este campo!");
                                        } else {
                                            if (camponum.equals("") || camponum == null) {
                                                num.setError("Por favor, preencha este campo!");
                                            } else {
                                                if (campobairro.equals("") || campobairro == null) {
                                                    bairro.setError("Por favor, preencha este campo!");
                                                } else {
                                                    if (campouf.equals("") || campouf == null) {
                                                        uf.setError("Por favor, preencha este campo!");
                                                    } else {
                                                        if (campocid.equals("") || campocid == null) {
                                                            num.setError("Por favor, preencha este campo!");
                                                        } else {
                                                            if (campocpf.equals("") || campocpf == null) {
                                                                cpf.setError("Por favor, preencha este campo!");
                                                            } else {
                                                                /*if (campocnh.equals("") || campocnh == null) {
                                                                    cnh.setError("Por favor, preencha este campo!");
                                                                }else {
                                                                    if (campoplaca.equals("") || campoplaca == null) {
                                                                        placa_veiculo.setError("Por favor, preencha este campo!");
                                                                    } else {
                                                                        if (campoalvara.equals("") || campoalvara == null) {
                                                                            alvara.setError("Por favor, preencha este campo!");
                                                                        } else {
                                                                            if (campocracha.equals("") || campocracha == null) {
                                                                                num_cracha.setError("Por favor, preencha este campo!");
                                                                            } else {*/
                                                                if (campoagencia.equals("") || campoagencia == null) {
                                                                    agencia.setError("Por favor, preencha este campo!");
                                                                } else {
                                                                    if (campooperacao.equals("") || campooperacao == null) {
                                                                        operacao.setError("Por favor, preencha este campo!");
                                                                    } else {
                                                                                       /* if (campoconta.equals("") || campoconta == null) {
                                                                                            conta.setError("Por favor, preencha este campo!");
                                                                                        } else {*/
                                                                        pDialog = ProgressDialog.show(MainCadActivity.this, "ENVIANDO DADOS", "Realizando cadastro, aguarde...");
                                                                        //envia_dados();

                                                                        registerProcess(camponome, campoemail, camposenha, camponasc, sexo, campotel, campocpf, campocep, campoend, camponum, campocomp, campobairro, campouf, campocid, situacao_taxi, campocnh,
                                                                                campoplaca, campoalvara, campocracha, campoagencia, campooperacao, campoconta, token_id);//

                                                                        displayFirebaseRegId();
                                                                        //Toast.makeText(
                                                                        //      MainCadActivity.this,
                                                                        //    "Cadastro realizado com sucesso!",
                                                                        //  Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                                //}
                                                                //}
                                                                //}
                                                                //}
                                                                //}
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        termos_de_uso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent termos = new Intent(MainCadActivity.this, MainTermosdeUso.class);
                startActivity(termos);
            }
        });

        //AlertDialog para escolher como deve add imagem
        showOptions();

        //Botão de RadioButton SEXO
        sexoRadioButton();

    }

    public String sexoRadioButton(){

        final RadioGroup group = (RadioGroup) findViewById(R.id.sexoselected);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                RadioButton button = (RadioButton) group.findViewById(i);
                String sexo = button.getText().toString();

                pegasexo.setText(sexo);

                Toast.makeText(MainCadActivity.this, sexo, Toast.LENGTH_SHORT).show();
            }
        });

        return null;
    }


    //=============================ISSO TUDO É PRA ACESSAR A CAMERA E A GALERIA===================================

    //=======================VERIFICA SE O SISTEMA É COMPATÍVEL e INICIA A AÇÃO DO BOTÃO==========================
    private boolean mayRequestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)  //VERIFICA SE NOSSO SITEMA PERMITE
            return true;
        //================================PERMISSÃO PARA ACESSAR A CAMERA==================================
        if ((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
            return true;

        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))) { //SE ESSAS PERMISSÕES NÃO ESTIVEREM CONTIDAS NO
            Snackbar.make(lay_cadastro, "Permissões são necessárias para usar a aplicação!",                                    //PACOTE DO SDK ANDROID, ENTÃO MANDE ESSA MENSAGEM.
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
    private void showOptions() {        //Declarando AlertDialog

        //Adicionado------------------------------------------------------
        /*
        if (mayRequestStoragePermission()) {
            button.setEnabled(true);
            button1.setEnabled(true);
            button2.setEnabled(true);
            button3.setEnabled(true);
            button4.setEnabled(true);
        }else {
            button.setEnabled(false);
            button1.setEnabled(false);
            button2.setEnabled(false);
            button3.setEnabled(false);
            button4.setEnabled(false);
        }
        */
        //menos esse
        final CharSequence[] option = {"Câmera", "Galeria", "Cancelar"};

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  //Manda pro armazen.... do dispositivo
                intent.setType("image/*"); //Todas as extensões de imagens
                startActivityForResult(Intent.createChooser(intent, "Selecione app de imagens"), SELECT_PICTURE);

                /*
                    //Adicionado até aqui---------------------------------------------
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainCadActivity.this);  //AlertDialog
                    builder.setTitle("Escolha uma opção");
                    builder.setItems(option, new DialogInterface.OnClickListener() {       //Sequência de Itens da variável "option"
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {   //Se o dispositivo conter as permissões dos itens (Camera, galeria), então usamos "which"
                            if (option[which] == "Câmera") {
                                openCamera();   //ABRE CÂMERA
                            } else if (option[which] == "Galeria") {
                                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  //Manda pro armazen.... do dispositivo
                                intent.setType("image/*"); //Todas as extensões de imagens
                                startActivityForResult(Intent.createChooser(intent, "Selecione app de imagens"), SELECT_PICTURE);
                            } else {
                                dialogInterface.dismiss();
                            }
                        }
                    });
                    builder.show();
                */
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  //Manda pro armazen.... do dispositivo
                intent.setType("image/*"); //Todas as extensões de imagens
                startActivityForResult(Intent.createChooser(intent, "Selecione app de imagens"), SELECT_PICTURE_P);

            /*
                //Adicionado até aqui---------------------------------------------
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainCadActivity.this);  //AlertDialog
                builder.setTitle("Escolha uma opção");
                builder.setItems(option, new DialogInterface.OnClickListener() {       //Sequência de Itens da variável "option"
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {   //Se o dispositivo conter as permissões dos itens (Camera, galeria), então usamos "which"
                        if (option[which] == "Câmera") {
                            openCameraum();   //ABRE CÂMERA
                        } else if (option[which] == "Galeria") {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  //Manda pro armazen.... do dispositivo
                            intent.setType("image/*"); //Todas as extensões de imagens
                            startActivityForResult(Intent.createChooser(intent, "Selecione app de imagens"), SELECT_PICTURE_P);
                        } else {
                            dialogInterface.dismiss();
                        }
                    }
                });
                builder.show();
            */
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  //Manda pro armazen.... do dispositivo
                intent.setType("image/*"); //Todas as extensões de imagens
                startActivityForResult(Intent.createChooser(intent, "Selecione app de imagens"), SELECT_PICTURE_I);

            /*
                //Adicionado até aqui---------------------------------------------
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainCadActivity.this);  //AlertDialog
                builder.setTitle("Escolha uma opção");
                builder.setItems(option, new DialogInterface.OnClickListener() {       //Sequência de Itens da variável "option"
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {   //Se o dispositivo conter as permissões dos itens (Camera, galeria), então usamos "which"
                        if (option[which] == "Câmera") {
                            openCameradois();   //ABRE CÂMERA
                        } else if (option[which] == "Galeria") {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  //Manda pro armazen.... do dispositivo
                            intent.setType("image/*"); //Todas as extensões de imagens
                            startActivityForResult(Intent.createChooser(intent, "Selecione app de imagens"), SELECT_PICTURE_I);
                        } else {
                            dialogInterface.dismiss();
                        }
                    }
                });
                builder.show();
            */
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  //Manda pro armazen.... do dispositivo
                intent.setType("image/*"); //Todas as extensões de imagens
                startActivityForResult(Intent.createChooser(intent, "Selecione app de imagens"), SELECT_PICTURE_C);

            /*
                //Adicionado até aqui---------------------------------------------
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainCadActivity.this);  //AlertDialog
                builder.setTitle("Escolha uma opção");
                builder.setItems(option, new DialogInterface.OnClickListener() {       //Sequência de Itens da variável "option"
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {   //Se o dispositivo conter as permissões dos itens (Camera, galeria), então usamos "which"
                        if (option[which] == "Câmera") {
                            openCameratre();   //ABRE CÂMERA
                        } else if (option[which] == "Galeria") {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  //Manda pro armazen.... do dispositivo
                            intent.setType("image/*"); //Todas as extensões de imagens
                            startActivityForResult(Intent.createChooser(intent, "Selecione app de imagens"), SELECT_PICTURE_C);
                        } else {
                            dialogInterface.dismiss();
                        }
                    }
                });
                builder.show();
            */
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  //Manda pro armazen.... do dispositivo
                intent.setType("image/*"); //Todas as extensões de imagens
                startActivityForResult(Intent.createChooser(intent, "Selecione app de imagens"), SELECT_PICTURE_T);

             /*
                //Adicionado até aqui---------------------------------------------
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainCadActivity.this);  //AlertDialog
                builder.setTitle("Escolha uma opção");
                builder.setItems(option, new DialogInterface.OnClickListener() {       //Sequência de Itens da variável "option"
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {   //Se o dispositivo conter as permissões dos itens (Camera, galeria), então usamos "which"
                        if (option[which] == "Câmera") {
                            openCameraqua();   //ABRE CÂMERA
                        } else if (option[which] == "Galeria") {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  //Manda pro armazen.... do dispositivo
                            intent.setType("image/*"); //Todas as extensões de imagens
                            startActivityForResult(Intent.createChooser(intent, "Selecione app de imagens"), SELECT_PICTURE_T);
                        } else {
                            dialogInterface.dismiss();
                        }
                    }
                });
                builder.show();
             */
            }
        });

    }

    //==============================================================================================================
    //==============================================================================================================
    //======================================ABRE A CAMERA===========================================================

    /*

    private void openCamera() {     //Instanciamos o objeto FILE
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);   //CRIA UM DIRETÓRIO(PASTA) PARA ARMAZENAMENTO DE IMAGENS...
        boolean isDirectoryCreated = file.exists(); //Diretório criado

        if (!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs(); //o método mkdirs(), verifica se o diretório foi criado,
                                                // então cria uma sequencia de diretórios
        if (isDirectoryCreated) {
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";   //Para que os nomes não se repitam
            //Especifica O CAMINHO onde será guardado a imagem, o SEPARADOR É ULTILIZADO PARA QUE NÃO HAJA ICOMPATIBILIDADE
            mPath = Environment.getExternalStorageDirectory() + File.separator +
                    MEDIA_DIRECTORY + File.separator + imageName;

            File newFile = new File(mPath);    //newFile recebe o caminho até o diretório onde será armazenado a imagem

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    //COM ISSO, ABRIRIREMOS A CAMERA!!!
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));    //Guardamos a camera no diretório criado...
            startActivityForResult(intent, PHOTO_CODE);
        }
    }

    private void openCameraum() {     //Instanciamos o objeto FILE
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);   //CRIA UM DIRETÓRIO(PASTA) PARA ARMAZENAMENTO DE IMAGENS...
        boolean isDirectoryCreated = file.exists(); //Diretório criado

        if (!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs(); //o método mkdirs(), verifica se o diretório foi criado,
        // então cria uma sequencia de diretórios
        if (isDirectoryCreated) {
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";   //Para que os nomes não se repitam
            //Especifica O CAMINHO onde será guardado a imagem, o SEPARADOR É ULTILIZADO PARA QUE NÃO HAJA ICOMPATIBILIDADE
            mPath = Environment.getExternalStorageDirectory() + File.separator +
                    MEDIA_DIRECTORY + File.separator + imageName;

            File newFile = new File(mPath);    //newFile recebe o caminho até o diretório onde será armazenado a imagem

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    //COM ISSO, ABRIRIREMOS A CAMERA!!!
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));    //Guardamos a camera no diretório criado...
            startActivityForResult(intent, PHOTO_CODE_C);
        }
    }

    private void openCameradois() {     //Instanciamos o objeto FILE
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);   //CRIA UM DIRETÓRIO(PASTA) PARA ARMAZENAMENTO DE IMAGENS...
        boolean isDirectoryCreated = file.exists(); //Diretório criado

        if (!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs(); //o método mkdirs(), verifica se o diretório foi criado,
        // então cria uma sequencia de diretórios
        if (isDirectoryCreated) {
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";   //Para que os nomes não se repitam
            //Especifica O CAMINHO onde será guardado a imagem, o SEPARADOR É ULTILIZADO PARA QUE NÃO HAJA ICOMPATIBILIDADE
            mPath = Environment.getExternalStorageDirectory() + File.separator +
                    MEDIA_DIRECTORY + File.separator + imageName;

            File newFile = new File(mPath);    //newFile recebe o caminho até o diretório onde será armazenado a imagem

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    //COM ISSO, ABRIRIREMOS A CAMERA!!!
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));    //Guardamos a camera no diretório criado...
            startActivityForResult(intent, PHOTO_CODE_O);
        }
    }

    private void openCameratre() {     //Instanciamos o objeto FILE
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);   //CRIA UM DIRETÓRIO(PASTA) PARA ARMAZENAMENTO DE IMAGENS...
        boolean isDirectoryCreated = file.exists(); //Diretório criado

        if (!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs(); //o método mkdirs(), verifica se o diretório foi criado,
        // então cria uma sequencia de diretórios
        if (isDirectoryCreated) {
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";   //Para que os nomes não se repitam
            //Especifica O CAMINHO onde será guardado a imagem, o SEPARADOR É ULTILIZADO PARA QUE NÃO HAJA ICOMPATIBILIDADE
            mPath = Environment.getExternalStorageDirectory() + File.separator +
                    MEDIA_DIRECTORY + File.separator + imageName;

            File newFile = new File(mPath);    //newFile recebe o caminho até o diretório onde será armazenado a imagem

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    //COM ISSO, ABRIRIREMOS A CAMERA!!!
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));    //Guardamos a camera no diretório criado...
            startActivityForResult(intent, PHOTO_CODE_D);
        }
    }

    private void openCameraqua() {     //Instanciamos o objeto FILE
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);   //CRIA UM DIRETÓRIO(PASTA) PARA ARMAZENAMENTO DE IMAGENS...
        boolean isDirectoryCreated = file.exists(); //Diretório criado

        if (!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs(); //o método mkdirs(), verifica se o diretório foi criado,
        // então cria uma sequencia de diretórios
        if (isDirectoryCreated) {
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";   //Extensão
            //Especifica O CAMINHO onde será guardado a imagem, o SEPARADOR É ULTILIZADO PARA QUE NÃO HAJA ICOMPATIBILIDADE
            mPath = Environment.getExternalStorageDirectory() + File.separator +
                    MEDIA_DIRECTORY + File.separator + imageName;

            File newFile = new File(mPath);    //newFile recebe o caminho até o diretório onde será armazenado a imagem

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    //COM ISSO, ABRIRIREMOS A CAMERA!!!
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));    //Guardamos a camera no diretório criado...
            startActivityForResult(intent, PHOTO_CODE_E);
        }
    }

    */

    //====================METODOS ==================================================================================
    //==============================FORAM ==========================================================================
    //=======================================DUPLICADOS=============================================================

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


    //===========================================RESULTADO DA ESCOLHA DO USUARIO=============================================
    @Override       //Captura as respostas                          Intent data
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {   //Isso depende da chamada da camera no método OpenCamrea()

//===========================================================================================================================;
                case PHOTO_CODE:                        //mPath tem a rota de onde estão as imagens
                        /*
                        MediaScannerConnection.scanFile(this, new String[]{mPath}, null,   //mPath captura com CAMERA
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.i("ExternalStorage", "Scanned" + path + ":");
                                        Log.i("ExternalStorage", "-> Uri = " + uri);
                                    }
                                });

                        Bitmap bitmap = BitmapFactory.decodeFile(mPath);
                        image1.setImageBitmap(bitmap);      //DESTINO DA IMAGEM...
                        */
                    break;

                case SELECT_PICTURE:        //Busca imagens da galeria

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainCadActivity.this);
                    builder.setMessage("Anexar imagem da CNH?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // FIRE ZE MISSILES!

                                    Uri path = data.getData();
                                    image1.setImageURI(null);
                                    image1.setImageURI(path);

                                    //===================ENVIA IMAGEM DA CNH=====================//=|
                                    //progressAnexo.setVisibility(View.VISIBLE);
                                    progDialog = ProgressDialog.show(MainCadActivity.this, "", "Enviando anexos, aguarde...");
                                    Bitmap imageCNH;
                                    imageCNH = ((BitmapDrawable) image1.getDrawable()).getBitmap(); //=|
                                    new UploadImage(imageCNH, edCPF.getText().toString() + "_CNH_" //=|
                                            + edCNH.getText().toString()).execute();             //=|
                                    //===========================================================//=|

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


//===========================================================================================================================;
                case PHOTO_CODE_C:                        //mPath tem a rota de onde estão as imagens
                        /*
                        MediaScannerConnection.scanFile(this, new String[]{mPath}, null,   //mPath captura com CAMERA
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.i("ExternalStorage", "Scanned" + path + ":");
                                        Log.i("ExternalStorage", "-> Uri = " + uri);
                                    }
                                });

                        Bitmap bitmapp = BitmapFactory.decodeFile(mPath);
                        image2.setImageBitmap(bitmapp);      //DESTINO DA IMAGEM...
                        */
                    break;

                case SELECT_PICTURE_P:        //Busca imagens da galeria

                    AlertDialog.Builder builde = new AlertDialog.Builder(MainCadActivity.this);
                    builde.setMessage("Anexar imagem da Placa do Veículo?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // FIRE ZE MISSILES!

                                    Uri pathh = data.getData();
                                    image2.setImageURI(null);
                                    image2.setImageURI(pathh);

                                    //===================ENVIA IMAGEM DA PLACA=====================//=|
                                    progDialog = ProgressDialog.show(MainCadActivity.this, "", "Enviando anexos, aguarde...");
                                    //progressAnexo.setVisibility(View.VISIBLE);
                                    Bitmap imagePLACA;                                                //=|
                                    imagePLACA = ((BitmapDrawable) image2.getDrawable()).getBitmap(); //=|
                                    new UploadImage(imagePLACA, edCPF.getText().toString() + "_PLACA_" //=|
                                            + edPlacacarro.getText().toString()).execute();             //=|
                                    //===========================================================//=|

                                }
                            })
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog


                                }
                            });
                    // Create the AlertDialog object and return it
                    alerta = builde.create();
                    alerta.show();


                    break;


//===========================================================================================================================;

                case PHOTO_CODE_O:                        //mPath tem a rota de onde estão as imagens
                        /*
                        MediaScannerConnection.scanFile(this, new String[]{mPath}, null,   //mPath captura com CAMERA
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.i("ExternalStorage", "Scanned" + path + ":");
                                        Log.i("ExternalStorage", "-> Uri = " + uri);
                                    }
                                });

                        Bitmap bitmapm = BitmapFactory.decodeFile(mPath);
                        image3.setImageBitmap(bitmapm);      //DESTINO DA IMAGEM...
                        */
                    break;

                case SELECT_PICTURE_I:        //Busca imagens da galeria

                    AlertDialog.Builder builden = new AlertDialog.Builder(MainCadActivity.this);
                    builden.setMessage("Anexar imagem do Alvará?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // FIRE ZE MISSILES!

                                    Uri pahh = data.getData();
                                    image3.setImageURI(null);
                                    image3.setImageURI(pahh);

                                    progDialog = ProgressDialog.show(MainCadActivity.this, "", "Enviando anexos, aguarde...");
                                    //progressAnexo.setVisibility(View.VISIBLE);
                                    //===================ENVIA IMAGEM DA ALVARÁ=====================//=|
                                    Bitmap imageALVARA;                                                //=|
                                    imageALVARA = ((BitmapDrawable) image3.getDrawable()).getBitmap(); //=|
                                    new UploadImage(imageALVARA, edCPF.getText().toString() + "_ALVARA_" //=|
                                            + edAlvara.getText().toString()).execute();             //=|
                                    //===========================================================//=|

                                }
                            })
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog


                                }
                            });
                    // Create the AlertDialog object and return it
                    alerta = builden.create();
                    alerta.show();


                    break;

//===========================================================================================================================;

                case PHOTO_CODE_D:                        //mPath tem a rota de onde estão as imagens
                        /*
                        MediaScannerConnection.scanFile(this, new String[]{mPath}, null,   //mPath captura com CAMERA
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.i("ExternalStorage", "Scanned" + path + ":");
                                        Log.i("ExternalStorage", "-> Uri = " + uri);
                                    }
                                });

                        Bitmap bitma = BitmapFactory.decodeFile(mPath);
                        image4.setImageBitmap(bitma);      //DESTINO DA IMAGEM...
                        */
                    break;

                case SELECT_PICTURE_C:        //Busca imagens da galeria

                    AlertDialog.Builder build = new AlertDialog.Builder(MainCadActivity.this);
                    build.setMessage("Anexar imagem do Crachá?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // FIRE ZE MISSILES!

                                    Uri pat = data.getData();
                                    image4.setImageURI(null);
                                    image4.setImageURI(pat);

                                    progDialog = ProgressDialog.show(MainCadActivity.this, "", "Enviando anexos, aguarde...");
                                    //progressAnexo.setVisibility(View.VISIBLE);
                                    //===================ENVIA IMAGEM DA CRACHA=====================//=|
                                    Bitmap imageCRACHA;                                                //=|
                                    imageCRACHA = ((BitmapDrawable) image4.getDrawable()).getBitmap(); //=|
                                    new UploadImage(imageCRACHA, edCPF.getText().toString() + "_CRACHA_" //=|
                                            + edCracha.getText().toString()).execute();             //=|
                                    //===========================================================//=|


                                }
                            })
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog


                                }
                            });
                    // Create the AlertDialog object and return it
                    alerta = build.create();
                    alerta.show();

                    break;

//===========================================================================================================================;

                case PHOTO_CODE_E:                        //mPath tem a rota de onde estão as imagens
                        /*
                        MediaScannerConnection.scanFile(this, new String[]{mPath}, null,   //mPath captura com CAMERA
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.i("ExternalStorage", "Scanned" + path + ":");
                                        Log.i("ExternalStorage", "-> Uri = " + uri);
                                    }
                                });

                        Bitmap bitmaq = BitmapFactory.decodeFile(mPath);
                        img_cartao.setImageBitmap(bitmaq);      //DESTINO DA IMAGEM...
                        */
                    break;

                case SELECT_PICTURE_T:        //Busca imagens da galeria

                    AlertDialog.Builder buildi = new AlertDialog.Builder(MainCadActivity.this);
                    buildi.setMessage("Anexar imagem do Cartão?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // FIRE ZE MISSILES!

                                    Uri pah = data.getData();
                                    img_cartao.setImageURI(null);
                                    img_cartao.setImageURI(pah);

                                    progDialog = ProgressDialog.show(MainCadActivity.this, "", "Enviando anexos, aguarde...");
                                    //progressAnexo.setVisibility(View.VISIBLE);
                                    //===================ENVIA IMAGEM DA CARTÃO=====================//=|
                                    Bitmap imageCONTA;                                                //=|
                                    imageCONTA = ((BitmapDrawable) img_cartao.getDrawable()).getBitmap(); //=|
                                    new UploadImage(imageCONTA, edCPF.getText().toString() + "_CARTAO_" //=|
                                            + edConta.getText().toString()).execute();             //=|
                                    //===========================================================//=


                                }
                            })
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog


                                }
                            });
                    // Create the AlertDialog object and return it
                    alerta = buildi.create();
                    alerta.show();

                    break;
            }
        }
    }


    //==========================ENVIA IMAGEM E NOME==================================
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
            //LINK COMENTADO PARA TESTAR LINK OFICIAL
            //HttpPost post = new HttpPost(SERVER_ADDRESS + "cad_Taxista/imagens_Taxista/SalvaImgDocs.php");
            HttpPost post = new HttpPost(SERVER_ADDRESS + "taxi/imagens_Taxista/SalvaImgDocs.php");

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
            progDialog.dismiss();
            //progressAnexo.setVisibility(View.INVISIBLE);
            Toast.makeText(MainCadActivity.this, "Imagem anexada com sucesso", Toast.LENGTH_SHORT).show();

        }
    }
    //==============================================================================

    private HttpParams getHttpRequestParams(){
        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams, 1000 * 30);
        HttpConnectionParams.setSoTimeout(httpRequestParams, 1000 * 30);
        return httpRequestParams;
    }







    //==================================MÉTODOS DE PERMISSÕES PARA VERSÕES APARTIR DE 6.0=============================================
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS) {
            if (grantResults.length == 2 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainCadActivity.this, "Permissões aceitas!", Toast.LENGTH_SHORT).show();
                button.setEnabled(true);
            } else {
                showExplanation();
            }
        }

    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainCadActivity.this);
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

    //=====================================||||||||||||||||||||||||\||============================================


    public boolean cb_termos(View view){
        boolean checado = ((CheckBox) view).isChecked();
        switch (view.getId()){
            case R.id.cb_termos_de_uso:
                if (checado){
                    Toast.makeText(MainCadActivity.this, "Termos de uso aceito", Toast.LENGTH_SHORT).show();
                    bt_confirmar.setEnabled(true);
                }else{
                    Toast.makeText(MainCadActivity.this, "Aceite os Termos de Uso", Toast.LENGTH_SHORT).show();
                    bt_confirmar.setEnabled(false);
                }
        }

        return checado;
    }

    //=====================================================================

    //ESSE MÉTODO RECEBE A INTENT ENVIADA PELO BOTÃO DE CADASTRE-SE
    // "INTENT NORMALIZA O FUNCIONAMENTO DO OnBackPressed""
    @Override
    public void finish() {
        Intent intente = new Intent();
        setResult(1, intente);
        super.finish();
    }



    //|||||||||||||||||||||||||||CADASTRO DE USUÁRIO||||||||||||||||||||||||||

    private void registerProcess(String nome, String email, String senha, String dn, String sexo, String tel, String cpf, String cep, String end, String num, String complemento,
                                 String bairro, String uf, String cidade, double situacao_taxi, String cnh, String placa, String alvara, String cracha, String agencia, String operacao,
                                 String conta, String token_id){ //
/*
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
*/
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) //(GsonConverterFactory.create(gson))
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setNome(nome);
        user.setEmail(email);
        user.setSenha(senha);
        user.setSexo(sexo);
        //Adicionado mais campos======================
        user.setData_nasc(dn);
        user.setTelefone(tel);
        user.setCpf(cpf);
        user.setCep(cep);
        user.setEndereco(end);
        user.setNum(num);
        user.setComplemento(complemento);
        user.setBairro(bairro);
        user.setUf(uf);
        user.setCidade(cidade);
        user.setSituacao(situacao_taxi);
        user.setCnh(cnh);
        user.setPlaca_veiculo(placa);
        user.setAlvara(alvara);
        user.setNum_cracha(cracha);
        user.setAgencia(agencia);
        user.setOperacao(operacao);
        user.setConta(conta);
        user.setToken(token_id);    //ADICIONADO PARA TESTAR LINK QUASE OFICIAL
        //======================Adicionado mais campos

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.REGISTER_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                Snackbar.make(getCurrentFocus(), resp.getMessage(), Snackbar.LENGTH_LONG).show();
                //progress.setVisibility(View.INVISIBLE);
                startActivity(new Intent(MainCadActivity.this, MainActivity.class));
                finish();
                pDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

                //progress.setVisibility(View.INVISIBLE);
                pDialog.cancel();
                Log.d(Constants.TAG,"failed " + t.getLocalizedMessage());
                Snackbar.make(getCurrentFocus(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();

            }
        });
    }



    //=================================== MÉTODOS =======================================
    //======================================== DO =======================================
    //=================================== FIREBASE ======================================

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {       //PEGA POR SHAREDPREFERENCES O TOKEN ARMAZENADO GERADO DA TELA DE MyFirebaseInstanceIDService
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

/*
        if (!TextUtils.isEmpty(regId))
            txtRegId.setText("Firebase Reg Id: " + regId);
        else
            txtRegId.setText("Firebase Reg Id is not received yet!");
*/
        //sendIdToServer(regId);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    //=============================== ENVIA TOKEN ===============================
    //================================ FIREBASE =================================

    private void sendIdToServer(final String uniqueId) {
        //Criando um diálogo de progresso para mostrar enquanto ele armazena os dados no servidor
        //final ProgressDialog progressDialog = new ProgressDialog(this);
        //progressDialog.setMessage("Registering device...");
        //progressDialog.show();

        // Recebendo o e-mail inserido
        final String email = edEmail.getText().toString().trim();

        // Criando uma solicitação de seqüência de caracteres
        StringRequest req = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Descartando o diálogo de progresso
                        //progressDialog.dismiss();

                        // Se o servidor retornou o sucesso da seqüência de caracteres
                        if (response.trim().equalsIgnoreCase("success")) {
                            // Exibindo um brinde de sucesso
                            Toast.makeText(MainCadActivity.this, "Registrado com sucesso", Toast.LENGTH_SHORT).show();

                            // Abrindo a preferência compartilhada
                            SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE);

                            // Abrindo o editor de preferências compartilhadas para salvar os valores
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            //Armazenando o ID exclusivo
                            editor.putString(Config.UNIQUE_ID, uniqueId);

                            // Salvando o booleano como verdadeiro, isto é, o dispositivo está registrado
                            editor.putBoolean(Config.REGISTERED, true);

                            //Aplicando as alterações nas preferências compartilhadas
                            editor.apply();

                        } else {
                            email.isEmpty();
                            uniqueId.isEmpty();
                            Toast.makeText(MainCadActivity.this, "Tente um email diferente", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adicionando parâmetros para postar solicitação, pois precisamos enviar ID de firebase e e-mail
                params.put("firebaseid", uniqueId);
                params.put("email", email);
                return params;
            }
        };

        // Adicionando o pedido à fila
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);
    }

}