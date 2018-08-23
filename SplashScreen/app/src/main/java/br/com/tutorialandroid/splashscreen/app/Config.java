package br.com.tutorialandroid.splashscreen.app;

/**
 * Created by Ravi Tamada on 28/09/16.
 * www.androidhive.info
 */

public class Config {

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";


    // ADICIONADO AGORA PARA RECEBER MENSAGEM DA NOTIFICACAO - MyFirebaseMessagingService.java
    public static final String SHARED_PREF_MSG = "mensagem_notificacao";
    public static final String SHARED_PREF_TITLE_MSG = "titulo_notificacao";


    //GUARDA EMAIL DO USUÁRIO LOGADO EM SHAREDPREFERENCES
    public static final String SHARED_PREF_EMAIL = "taxi_id_shared";
    //====================== ================================== =======================

    //====================== ADICIONADO AGORA do Constants.java =======================

    //To store the firebase id in shared preferences
    public static final String UNIQUE_ID = "uniqueid";

    //To store boolean in shared preferences for if the device is registered to not
    public static final String REGISTERED = "registered";

    //register.php address in your server
    public static final String REGISTER_URL = "http://www.letsgosobral.esy.es/firebase_TokeneStatus/register.php";  //ESSE LINK NÃO SERÁ MAIS USADO, O MESMO CADASTRARÁ DIRETO NA TABELA

    //=================================================================================


    //======================CONSTANTE PARA ARMAZENAR LEMBRETE DO ALARME================
    // ADICIONADO AGORA PARA RECEBER LEMBRETE DO ALARME MainAlarmeLembrete.java
    public static final String SHARED_PREF_ALARME = "mensagem_alarme";
}
