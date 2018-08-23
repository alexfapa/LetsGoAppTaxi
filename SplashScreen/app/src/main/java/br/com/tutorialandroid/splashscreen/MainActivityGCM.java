package br.com.tutorialandroid.splashscreen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;

import java.lang.ref.WeakReference;

import br.com.tutorialandroid.splashscreen.extras.Util;
import br.com.tutorialandroid.splashscreen.service.CustomService;

public class MainActivityGCM extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_gcm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        changeButtonLabel();

    }

    private void changeTrackingStatus(){
        boolean status = Util.retrieveSP(this, Util.TRACKING_STATUS);
        Util.saveSP( this, Util.TRACKING_STATUS, !status );
    }

    private void changeButtonLabel(){
        Button bt = (Button) findViewById(R.id.bt_tracking);

        if( Util.retrieveSP(this, Util.TRACKING_STATUS) ){
            bt.setText( "Parar tracking" );
        }
        else{
            bt.setText( "Iniciar tracking" );
        }
    }

    public void startSchedule(){
        changeTrackingStatus();
        changeButtonLabel();

        if( !Util.retrieveSP(this, Util.TRACKING_STATUS) ){
            GcmNetworkManager.getInstance(this).cancelAllTasks(CustomService.class);
            return;
        }

        /*OneoffTask task = new OneoffTask.Builder()
                .setService(CustomService.class)
                .setExecutionWindow(0, 30)
                .setTag("1")
                .setUpdateCurrent(false)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setRequiresCharging(false)
                .build();*/

        PeriodicTask task = new PeriodicTask.Builder()
                .setService(CustomService.class)
                .setPeriod(10)
                .setPersisted(true)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setRequiresCharging(false)
                .setUpdateCurrent(true)
                .setTag("tracking")
                .build();

        GcmNetworkManager.getInstance(this).schedule(task);
    }

    public void tracking( View view ){
        String[] LOCATION_PERMISSIONS = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        };
/*
        Permission.PermissionBuilder permissionBuilder =
                new Permission
                        .PermissionBuilder(LOCATION_PERMISSIONS,775, new InternPermission(getApplicationContext()))
                        .enableDefaultRationalDialog("Ration dialog title", "Ration Dialog message")
                        .enableDefaultSettingDialog("Setting Dialog title", "Setting dialog message");

        requestAppPermissions(permissionBuilder.build());
*/
    }


/*
    private static class InternPermission implements Permission.PermissionCallback {
        private WeakReference<MainActivity> activity;

        InternPermission( MainActivity activity ){
            this.activity = new WeakReference<>(activity);
        }
        @Override
        public void onPermissionGranted(int i) {
            activity.get().startSchedule();
        }
        @Override
        public void onPermissionDenied(int i) {}
        @Override
        public void onPermissionAccessRemoved(int i) {}
    }
*/
}
