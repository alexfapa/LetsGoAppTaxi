package br.com.tutorialandroid.splashscreen;

/**
 * Created by Renato Almeida on 03/02/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.com.tutorialandroid.splashscreen.telaDeCorrida.MainPagamentoTaxi;

public class MainPayPal extends AppCompatActivity {

    //The views
    private Button buttonPay,buttonPaynoTaxi;
    private EditText editTextAmounts;
    private TextView valorCorrida, nome_cli,
    usu_ponto_de_partida, usu_destino, pagamento;

    //Payment Amount
    private String paymentAmount;

    private TextWatcher valorDigitado;
    private TextWatcher valorMask;


    //======================PAYPAL PAYMENT====================

    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;

    //Para obter o Pagamento PayPal precisamos de um objeto de configuração
    //Paypal intent request code to track onActivityResult method
    public static final int PAYPAL_REQUEST_CODE = 1;


    //Paypal Configuration Object
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(CONFIG_ENVIRONMENT)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);
            //.merchantName("Lets GO App")
            //.merchantPrivacyPolicyUri(Uri.parse("http://www.exemplo.com.br/privacy"))
            //.merchantUserAgreementUri(Uri.parse("http://www.exemplo.com.br/legal"));

    //======================PAYPAL PAYMENT====================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);

        buttonPay = (Button) findViewById(R.id.buttonPay);
        buttonPaynoTaxi = (Button) findViewById(R.id.buttonPaynoTaxi);
        //editTextAmount = (EditText) findViewById(R.id.editTextAmount);
        valorCorrida = (EditText) findViewById(R.id.valorCorrida);
        nome_cli = (TextView) findViewById(R.id.nome_cli);
        usu_ponto_de_partida = (TextView) findViewById(R.id.usu_ponto_de_partida);
        usu_destino = (TextView) findViewById(R.id.usu_destino);
        pagamento = (TextView) findViewById(R.id.pagamento);
        //valorMask = MascaraCampos.insert("##.##,##", editTextAmount);
        //editTextAmount.addTextChangedListener(valorMask);



        //Locale mLocale = new Locale("pt", "BR");
        //editTextAmount.addTextChangedListener(new MoneyTextWatcher(editTextAmount, mLocale));

        //editTextAmounts = (EditText) findViewById(R.id.editTextAmount);
        //valorDigitado = new MoneyTextWatcher(editTextAmounts);
        //editTextAmounts.addTextChangedListener(valorDigitado);


        //PEGA HORA E DATA DP SISTEMA...
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"+ " " +"HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // OU
        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm:ss");
        Date data = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();

        final String data_completa = dateFormat.format(data_atual);
        final String hora_atual = dateFormat_hora.format(data_atual);
        // String hora_atual = dateFormat_hora.format(data_atual);
        Log.i("data_completa", data_completa);
        //Log.i("data_atual", data_atual.toString());
        //Log.i("hora_atual", hora_atual);


        String valorreal = getIntent().getStringExtra("VCorrida");
        valorCorrida.setText(valorreal);
        String nome_cliente = getIntent().getStringExtra("nome_do_cliente");
        nome_cli.setText(nome_cliente);
        String partida_cli = getIntent().getStringExtra("ponto_partida_cli");
        usu_ponto_de_partida.setText(partida_cli);
        String chegada_cli = getIntent().getStringExtra("destino_cli");
        usu_destino.setText(chegada_cli);
        String forma_pagamento_cli = getIntent().getStringExtra("pagamento_forma");
        pagamento.setText(forma_pagamento_cli);

        final String id_usu = getIntent().getStringExtra("id_usu");
        final String id_taxista = getIntent().getStringExtra("id_taxista");

        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPayment();

            }
        });

        buttonPaynoTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText valor = (EditText) findViewById(R.id.editTextAmount);
                final String valorcorrida = valor.getText().toString();
                if (valorcorrida.equals("") || valorcorrida == null) {
                    valor.setError("Campo vazio! digite um valor");
                }else {

                    Intent telaPagamentoTaxi = new Intent(MainPayPal.this, MainPagamentoTaxi.class);
                    telaPagamentoTaxi.putExtra("nome_cliente", nome_cli.getText().toString());
                    telaPagamentoTaxi.putExtra("forma_de_pagamento", pagamento.getText().toString());
                    telaPagamentoTaxi.putExtra("valorpago", valorcorrida);
                    telaPagamentoTaxi.putExtra("data_corrida", data_completa);
                    telaPagamentoTaxi.putExtra("hora_corrida", hora_atual);
                    telaPagamentoTaxi.putExtra("ponto_partida_cli", usu_ponto_de_partida.getText().toString());
                    telaPagamentoTaxi.putExtra("destino_cli", usu_destino.getText().toString());
                    telaPagamentoTaxi.putExtra("id_usu", id_usu);
                    telaPagamentoTaxi.putExtra("id_taxista", id_taxista);
                    startActivity(telaPagamentoTaxi);
                    finish();

                }
            }
        });


        //Inicia o PayPal
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);


    }

    private void getPayment() {


        EditText valor = (EditText) findViewById(R.id.editTextAmount);
        String valorcorrida = valor.getText().toString();

        if (valorcorrida.equals("") || valorcorrida == null){
            valor.setError("Campo vazio! repita o valor do Vaucher ou digite outro valor");
        //}else {   ASSIM Q TAVA ANTERIORMENTE
        }else {

            //Double valorVaucher = Double.parseDouble(valorCorrida.getText().toString());
            //Double valorDigitado = Double.parseDouble(valorcorrida);

            //if (valorDigitado > (valorVaucher + 3.5)) {
              //  valor.setError("Valor limite ultrapassado");
            //}else{

                //Getting the amount from editText
                paymentAmount = valor.getText().toString();

                //Creating a paypalpayment
                PayPalPayment payment = new PayPalPayment(new BigDecimal(Double.valueOf(paymentAmount)), "BRL", "Teste de pagamento",
                        PayPalPayment.PAYMENT_INTENT_SALE);

                //Creating Paypal Payment activity intent
                Intent intent = new Intent(this, PagamentoPagSeguro.class);

                //putting the paypal configuration to the intent
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

                //Puting paypal payment to the intent
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

                //Starting the intent activity for result
                //the request code will be used on the method onActivityResult
                startActivityForResult(intent, PAYPAL_REQUEST_CODE);

            //}

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);

                        //Starting a new activity for the payment details and also putting the payment details with intent
                        startActivity(new Intent(this, ConfirmacaoPayPal.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", paymentAmount));

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Uma falha extremamente improvável" + " ocorreu: ", Toast.LENGTH_LONG).show();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "O usuário cancelou.", Toast.LENGTH_LONG).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(getApplicationContext(), "Um Pagamento inválido ou uma Configuração PayPal" + " Foi submetido. Consulte os documentos.", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}
