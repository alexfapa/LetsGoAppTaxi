package br.com.tutorialandroid.splashscreen.alarmeLembrete;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Random;

import br.com.tutorialandroid.splashscreen.R;
import br.com.tutorialandroid.splashscreen.app.Config;

/**
 * Created by Renato Almeida on 22/06/2017.
 */

public class MainAlarmeLembrete extends AppCompatActivity {

    AlarmManager alarmManager;
    private PendingIntent pending_intent;

    private TimePicker alarmTimePicker;
    private TextView alarmTextView; private TextView lembrete_alarmes;

    private AlarmReceiver alarm;


    MainAlarmeLembrete inst;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alarme);

        lembrete_alarmes = (TextView) findViewById(R.id.lembrete_alarme);

        //Traz a frase do lembrete salvo para essa tela e mostra logo abaixo da frase Defina um horário para:
        String lembrete_alarme = getIntent().getStringExtra("lembrete_alarme");
        lembrete_alarmes.setText(lembrete_alarme);

        String lembrar = String.valueOf(lembrete_alarmes);

        recebeMensagemDoAlarmeEGuarda(lembrar);


        this.context = this;

        //alarm = new AlarmReceiver();
        alarmTextView = (TextView) findViewById(R.id.alarmText);

        final Intent myIntent = new Intent(this.context, AlarmReceiver.class);

        // Get the alarm manager service
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // set the alarm to the time that you picked
        final Calendar calendar = Calendar.getInstance();

        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);



        Button start_alarm= (Button) findViewById(R.id.start_alarm);
        start_alarm.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)

            @Override
            public void onClick(View v) {

                calendar.add(Calendar.SECOND, 3);
                //setAlarmText("You clicked a button");

                final int hour = alarmTimePicker.getCurrentHour();
                final int minute = alarmTimePicker.getCurrentMinute();;

                Log.e("MyActivity", "In the receiver with " + hour + " and " + minute);
                setAlarmText("You clicked a " + hour + " and " + minute);


                calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());

                myIntent.putExtra("extra", "yes");
                pending_intent = PendingIntent.getBroadcast(MainAlarmeLembrete.this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending_intent);

                // now you should change the set Alarm text so it says something nice


                setAlarmText("Alarme programado para " + hour + ":" + minute);
                //Toast.makeText(getApplicationContext(), "You set the alarm", Toast.LENGTH_SHORT).show();
            }

        });

        Button stop_alarm= (Button) findViewById(R.id.stop_alarm);
        stop_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int min = 1;
                int max = 9;

                Random r = new Random();
                int random_number = r.nextInt(max - min + 1) + min;
                Log.e("random number is ", String.valueOf(random_number));

                myIntent.putExtra("extra", "no");
                sendBroadcast(myIntent);

                alarmManager.cancel(pending_intent);
                setAlarmText("Alarme cancelado");
                //setAlarmText("You clicked a " + " canceled");
            }
        });

    }

    public void setAlarmText(String alarmText) {
        alarmTextView.setText(alarmText);
    }




    //PEGA NOTIFICAÇÃO E ARMAZENA EM SHAREDPREFERENCES MANDANDO PARA Config.class
    private void recebeMensagemDoAlarmeEGuarda(String alrameMessage) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_ALARME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("alarme_messaging", alrameMessage);
        editor.commit();
    }




    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("MyActivity", "on Destroy");
    }


}