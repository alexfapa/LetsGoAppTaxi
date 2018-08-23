package br.com.tutorialandroid.splashscreen.service;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.tutorialandroid.splashscreen.MainNotification;
import br.com.tutorialandroid.splashscreen.app.Config;
import br.com.tutorialandroid.splashscreen.util.NotificationUtils;


/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Verificar se a mensagem contém uma carga de notificação.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Verificar se a mensagem contém uma carga de dados.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app está em primeiro plano, transmitir a mensagem push
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);



            // reproduzir som de notificação
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();


        }else{
            // Se o aplicativo estiver em segundo plano, o firebase manipula a notificação
        }
    }







    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);


            recebenotificacaoeguarda(message);
            recebenotificacaotitulo(title);


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app está em primeiro plano, transmitir a mensagem push
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                pushNotification.putExtra("title", title);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);





                // app está no plano de fundo, mostra a notificação na bandeja de notificação
                Intent resultIntent = new Intent(getApplicationContext(), MainNotification.class);   //Intent resultIntent = new Intent(getApplicationContext(), MainActiviity.class);
                resultIntent.putExtra("message", message);
                resultIntent.putExtra("title", title);

                //NotificationManager mng = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                //mng.cancelAll();

                // verificar o anexo da imagem
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // imagem está presente, mostrar notificação com imagem
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }



                /*
                if(title.equals("CHAMADA PERDIDA!")){
                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                    notificationUtils.playSecondNotificationSound();
                }*/

                // reproduzir som de notificação
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();


            } else {
                // app está no plano de fundo, mostra a notificação na bandeja de notificação
                Intent resultIntent = new Intent(getApplicationContext(), MainNotification.class);   //Intent resultIntent = new Intent(getApplicationContext(), MainActiviity.class);
                resultIntent.putExtra("message", message);
                resultIntent.putExtra("title", title);




                // verificar o anexo da imagem
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // imagem está presente, mostrar notificação com imagem
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }



    //PEGA NOTIFICAÇÃO E ARMAZENA EM SHAREDPREFERENCES MANDANDO PARA Config.class
    private void recebenotificacaoeguarda(String notificacao) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_MSG, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("notificacaoMsg", notificacao);
        editor.commit();
    }


    //PEGA NOTIFICAÇÃO E ARMAZENA EM SHAREDPREFERENCES MANDANDO PARA Config.class
    private void recebenotificacaotitulo(String titulo) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_TITLE_MSG, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("notificacaoTitulo", titulo);
        editor.commit();
    }




    /**
     * Mostrando notificação apenas com texto
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Mostrando notificação com texto e imagem
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}

