package br.com.tutorialandroid.splashscreen.alarmeLembrete;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import br.com.tutorialandroid.splashscreen.MainActivity;
import br.com.tutorialandroid.splashscreen.MainMapActivity;
import br.com.tutorialandroid.splashscreen.ProviderFragmentV2;
import br.com.tutorialandroid.splashscreen.R;
import retrofit.client.Client;
/**
 * Created by Renato Almeida on 14/08/2017.
 */

public class BroadcastReceiverAuxOcup extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("Script", "-> Alarme");

        //gerarNotificacao(context, new Intent(context, MainActivityAlarmManager.class), "Você está Livre", "LIVRE", "Aguardando chamadas...");
        gerarNotificacao(context, new Intent(context, ProviderFragmentV2.class), "Você está Ocupado", "Ocupado", "Fique livre para receber chamadas...");
    }

    public void gerarNotificacao(Context context, Intent intent, CharSequence ticket, CharSequence titulo, CharSequence descricao){
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //PendingIntent p = PendingIntent.getActivity(context, 0, intent, 0);
        PendingIntent p = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setTicker(ticket);
        builder.setContentTitle(titulo);
        builder.setContentText(descricao);
        builder.setSmallIcon(R.mipmap.logo_notificacao);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logomaraclets));
        builder.setContentIntent(p);

        Notification n = builder.build();
        //n.vibrate = new long[]{150, 300, 150, 600};
        n.flags = Notification.FLAG_AUTO_CANCEL;
        nm.notify(R.mipmap.logo_notificacao, n);

        /*try{
            Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone toque = RingtoneManager.getRingtone(context, som);
            toque.play();
        }
        catch(Exception e){}*/
    }
}
