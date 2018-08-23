package br.com.tutorialandroid.splashscreen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import br.com.tutorialandroid.splashscreen.conexao.AppConfig;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

//FAZ AS ALTERAÇÕES NOS CAMPOS
public class MainCRUDUpdateData extends AppCompatActivity {

    //String BASE_URL = "http://www.letsgosobral.esy.es/taxi/updateData.php";
    //TROCADO POR
    String BASE_URL = "http://www.letsgosobral.com/taxi/updateData.php";

    EditText et_nome, et_data_nasc, et_sexo, et_phone, et_cpf, et_email, et_cep, et_end, et_num, et_complemento, et_bairro,
            et_uf, et_cidade, et_cnh, et_placa_veiculo, et_alvara, et_cracha, et_agencia, et_operacao, et_conta;

    Button btn_update;

    String id, nome, data_nasc, sexo, phone, cpf, email, cep, end, num, complemento, bairro, uf,
            cidade, cnh, placa_veiculo, alvara, cracha, agencia, operacao, conta, senha;

    private ProgressBar progresso;
    private AlertDialog alerta;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_crudupdate_data);

        progresso = (ProgressBar) findViewById(R.id.progresso);

        et_nome = (EditText) findViewById(R.id.nome);

        et_data_nasc = (EditText) findViewById(R.id.data_nasc);
        nascMask = MascaraCampos.insert("##/##/####", et_data_nasc);
        et_data_nasc.addTextChangedListener(nascMask);

        et_phone = (EditText) findViewById(R.id.phone);
        phoneMask = MascaraCampos.insert("(##)#.####-####", et_phone);
        et_phone.addTextChangedListener(phoneMask);

        et_cpf = (EditText) findViewById(R.id.cpf);
        cpfMask = MascaraCampos.insert("###.###.###-##", et_cpf);
        et_cpf.addTextChangedListener(cpfMask);

        et_email = (EditText) findViewById(R.id.email);
        et_sexo = (EditText) findViewById(R.id.sexo);

        et_cep = (EditText) findViewById(R.id.cep);
        cepMask = MascaraCampos.insert("##.###-###", et_cep);
        et_cep.addTextChangedListener(cepMask);

        et_end = (EditText) findViewById(R.id.end);

        et_num = (EditText) findViewById(R.id.end_num);

        et_complemento = (EditText) findViewById(R.id.complemento);

        et_bairro = (EditText) findViewById(R.id.bairro);

        et_uf = (EditText) findViewById(R.id.uf);
        ufMask = MascaraCampos.insert("##", et_uf);
        et_uf.addTextChangedListener(ufMask);

        et_cidade = (EditText) findViewById(R.id.cidade);

        et_cnh = (EditText) findViewById(R.id.cnh);
        cnhMask = MascaraCampos.insert("###########", et_cnh);
        et_cnh.addTextChangedListener(cnhMask);

        et_placa_veiculo = (EditText) findViewById(R.id.placa_veiculo);
        carroMask = MascaraCampos.insert("###-####", et_placa_veiculo);
        et_placa_veiculo.addTextChangedListener(carroMask);

        et_alvara = (EditText) findViewById(R.id.alvara);
        alvaraMask = MascaraCampos.insert("########", et_alvara);
        et_alvara.addTextChangedListener(alvaraMask);

        et_cracha = (EditText) findViewById(R.id.cracha);

        et_agencia = (EditText) findViewById(R.id.agencia);
        agenciaMask = MascaraCampos.insert("####", et_agencia);
        et_agencia.addTextChangedListener(agenciaMask);

        et_operacao = (EditText) findViewById(R.id.operacao);
        operacaoMask = MascaraCampos.insert("###", et_operacao);
        et_operacao.addTextChangedListener(operacaoMask);

        et_conta = (EditText) findViewById(R.id.conta);
        contaMask = MascaraCampos.insert("#####-#", et_conta);
        et_conta.addTextChangedListener(contaMask);

        btn_update = (Button) findViewById(R.id.update);


        Intent i = getIntent();
        id = i.getStringExtra("id");
        nome = i.getStringExtra("nome");
        data_nasc = i.getStringExtra("data_nasc");
        phone = i.getStringExtra("telefone");
        cpf = i.getStringExtra("cpf");
        sexo = i.getStringExtra("sexo");
        email = i.getStringExtra("email");
        cep = i.getStringExtra("cep");
        end = i.getStringExtra("endereco");
        num  = i.getStringExtra("num");
        complemento = i.getStringExtra("complemento");
        bairro = i.getStringExtra("bairro");
        uf = i.getStringExtra("uf");
        cidade = i.getStringExtra("cidade");
        cnh = i.getStringExtra("cnh");
        placa_veiculo = i.getStringExtra("placa_veiculo");
        alvara = i.getStringExtra("alvara");
        cracha = i.getStringExtra("num_cracha");
        agencia = i.getStringExtra("agencia");
        operacao = i.getStringExtra("operacao");
        conta = i.getStringExtra("conta");

/*
        Intent i = getIntent();
        id = i.getStringExtra("id");
        nome = i.getStringExtra("nome");
        phone = i.getStringExtra("telefone");
        email = i.getStringExtra("email");
*/

        et_nome.setText(nome);
        et_data_nasc.setText(data_nasc);
        et_phone.setText(phone);
        et_cpf.setText(cpf);
        et_email.setText(email);
        et_cep.setText(cep);
        et_sexo.setText(sexo);
        et_end.setText(end);
        et_num.setText(num);
        et_complemento.setText(complemento);
        et_bairro.setText(bairro);
        et_uf.setText(uf);
        et_cidade.setText(cidade);
        et_cnh.setText(cnh);
        et_placa_veiculo.setText(placa_veiculo);
        et_alvara.setText(alvara);
        et_cracha.setText(cracha);
        et_agencia.setText(agencia);
        et_operacao.setText(operacao);
        et_conta.setText(conta);
/*
        et_nome.setText(nome);
        et_phone.setText(phone);
        et_email.setText(email);
*/

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progresso.setVisibility(View.VISIBLE);
                editaDados(id);

            }
        });

    }

    /*
    * DENTRO DO METODO api.update
    * */

    /*
    * id,

    * */


    public void editaDados(String id) {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL) //Setting the Root URL
                .build();

        AppConfig.update api = adapter.create(AppConfig.update.class);

        api.updateData(
                id,
                et_nome.getText().toString(),
                et_data_nasc.getText().toString(),
                et_phone.getText().toString(),
                et_cpf.getText().toString(),
                et_email.getText().toString(),
                et_sexo.getText().toString(),
                et_cep.getText().toString(),
                et_end.getText().toString(),
                et_num.getText().toString(),
                et_complemento.getText().toString(),
                et_bairro.getText().toString(),
                et_uf.getText().toString(),
                et_cidade.getText().toString(),
                et_cnh.getText().toString(),
                et_placa_veiculo.getText().toString(),
                et_alvara.getText().toString(),
                et_cracha.getText().toString(),
                et_agencia.getText().toString(),
                et_operacao.getText().toString(),
                et_conta.getText().toString(),
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
                                Toast.makeText(getApplicationContext(), "Atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                                progresso.setVisibility(View.INVISIBLE);

                                AlertDialog.Builder builder = new AlertDialog.Builder(MainCRUDUpdateData.this);
                                builder.setTitle("É necessário que você faça o login novamente, para ser aplicado as alterações");
                                builder.setMessage("Deseja refazer o login?");
                                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Crouton.makeText(MainCRUDUpdateData.this, "Atualização realizada com sucesso!", Style.CONFIRM).show();

                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();

                                    }
                                });
                                //define um botão como negativo.
                                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        startActivity(new Intent(getApplicationContext(), MainMapActivity.class));
                                        finish();
                                    }
                                });
                                //cria o AlertDialog
                                alerta = builder.create();
                                //Exibe
                                alerta.show();

                            } else {
                                Toast.makeText(getApplicationContext(), "A atualização falhou!", Toast.LENGTH_SHORT).show();
                                progresso.setVisibility(View.INVISIBLE);
                            }

                        } catch (IOException e) {
                            Log.d("Exception", e.toString());
                        } catch (JSONException e) {
                            Log.d("JsonException", e.toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //Toast.makeText(MainCRUDUpdateData.this, error.toString(), Toast.LENGTH_LONG).show();
                        Toast.makeText(MainCRUDUpdateData.this, "Verifique sua conexão", Toast.LENGTH_LONG).show();
                        progresso.setVisibility(View.INVISIBLE);
                    }
                }
        );

    }





/*
    public void update_data(String id) {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL) //Setting the Root URL
                .build();

        AppConfig.update api = adapter.create(AppConfig.update.class);

        api.updateData(
                id,
                et_nome.getText().toString(),
                et_phone.getText().toString(),
                et_email.getText().toString(),
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
                                Toast.makeText(getApplicationContext(), "Atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),MainPerfil.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "A atualização falhou!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            Log.d("Exception", e.toString());
                        } catch (JSONException e) {
                            Log.d("JsonException", e.toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(MainCRUDUpdateData.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
*/
}