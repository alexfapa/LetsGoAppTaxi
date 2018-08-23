package br.com.tutorialandroid.splashscreen.executaAppEmSegundoPlano;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Renato Almeida on 25/05/2017.
 */

public class BootCompletedIntentReceiver extends BroadcastReceiver {

    private static final String TAG = "RestartServiceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");
        context.startService(new Intent(context.getApplicationContext(), service.class));

    }

    /*
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, service.class);
            context.startService(pushIntent);
        }
    }
    */
}