package br.com.tutorialandroid.splashscreen;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.text.DecimalFormat;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTela1 extends Fragment {

    //==================CRONOMETRO==================
    Chronometer m_chronometer;
    Button btIniciar, btPausar, btResetar;

    boolean isClickPause = false;
    long tempoQuandoParado = 0;

    //===============================================

    public FragmentTela1() {
        // Required empty public constructor
    }
    public TextView valores;

    private Polyline polyline;
    private List<LatLng> list;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_fragment_tela1, container, false);

        //piscar();

        m_chronometer = (Chronometer)view.findViewById(R.id.chronometer);

        btIniciar = (Button)view.findViewById(R.id.btIniciar);
        btPausar = (Button)view.findViewById(R.id.btPausar);
        btResetar = (Button)view.findViewById(R.id.btResetar);


        valores = (TextView)view.findViewById(R.id.valores);
        valores.setText("");
        /*while (tempoQuandoParado >= 0){
            String string = Double.toString(tempoQuandoParado);
            Toast.makeText(getActivity(), "O valor é: " + string, Toast.LENGTH_SHORT).show();
            tempoQuandoParado ++;
        }*/


        //Botão INICIAR
        btIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(isClickPause) {
                    m_chronometer.setBase(SystemClock.elapsedRealtime() + tempoQuandoParado);  //Retorna os mulisegundos desde boot do sistema,
                    m_chronometer.start();   //Contagem em segundos         // incluindo o tempo gasto dormindo.
                    tempoQuandoParado = 0;
                    isClickPause = false;
                }
                else{
                    m_chronometer.setBase(SystemClock.elapsedRealtime());
                    m_chronometer.start();
                    tempoQuandoParado = 0;
                }

                //MÉTODO PISCAR!!!
                piscar();
            }

        });

        //Botão PAUSAR
        btPausar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(isClickPause == false){ //entra para false;
                    tempoQuandoParado = m_chronometer.getBase() - SystemClock.elapsedRealtime(); //O método m_chronometer.getBase() retorna
                }                                                                                //a base que foi ajustada através do método setBase();
                m_chronometer.stop();       //É feita uma conta e o resultado é armazenado em "tempoQuandoParado" para que quando o usuário iniciar
                isClickPause = true;        //a contagem novamente esse valor armazenado seja usado para CONTINUAR A CONTAGEM de onde parou
                piscar();
            }
        });

        //Botão RESETAR
        btResetar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                m_chronometer.stop();       //é chamado para parar contagem em segundos a partir da base ajustada.
                m_chronometer.setText(" 00:00 ");  //é chamado para resetar o tempo exibido na tela/label de contagem em segundos.
                tempoQuandoParado = 0;   //essa variável é zerada para que a lógica do botão resetar funcione corretamente sem gerar problemas
            }                            //de continuar o tempo quando o usuário clicar em "Iniciar" ou "Pausar > Iniciar".
        });


        return view;

    }

    //=============================MÉTODO MUDA VALOR DO TAXIMETRO=================================
    private void piscar(){
        final Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(1000);   //A cada 10 segundos muda valor!!!
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                // Write your code here to update the UI.

                                long pegavalor = SystemClock.elapsedRealtime() - m_chronometer.getBase();

                                double real = (double) pegavalor;
                                valores.setText("");

                                while (real == pegavalor && real > 0){
                                    double re = (real / 137200);
                                    String stringInt = Double.toString(re);
                                    double converter = Double.parseDouble(stringInt);

                                    DecimalFormat df = new DecimalFormat("0.##");   //CONVERTE PARA FORMATO DECIMAL COM DUAS CASAS!!!
                                    String dx = df.format(converter);

                                    valores.append("R$ " + (dx) + "\n");
                                    valores.refreshDrawableState();
                                    real ++;
                                }

                                /*
                                while (real == pegavalor){
                                    double re = (real+pegavalor);
                                    String stringInt = Double.toString(re);
                                    valores.append("R$ " + (stringInt) + "\n");
                                    valores.refreshDrawableState();
                                    real ++;

                                    //Toast.makeText(getActivity(), "Deu certo??: " + re, Toast.LENGTH_SHORT).show();

                                }
                                */

                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();




        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                int tempoparapiscar = 1000; //em Milisegundos

                try{
                    Thread.sleep(tempoparapiscar);
                } catch (Exception e) {
                    //e.printStackTrace();
                }

                handler.post(new Runnable() {   //a variavel handler chama o objeto Thread
                    @Override
                    public void run() {
                        TextView txt = (TextView) getView().findViewById(R.id.valores);

                        if (txt.getVisibility() == View.VISIBLE) {
                            txt.setVisibility(View.INVISIBLE);
                        }else{
                            txt.setVisibility(View.VISIBLE);
                        }piscar();
                    }
                });
            }
        }).start();*/
    }



//================================================================================================

}

