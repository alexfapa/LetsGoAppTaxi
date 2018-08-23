package br.com.tutorialandroid.splashscreen.mapacomweb;

/**
 * Created by Renato Almeida on 09/06/2017.
 */

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import br.com.tutorialandroid.splashscreen.R;


public class LastLocationActivity extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private TextView tvLastLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_location);
        Log.i("LOG", "LastLocationActivity.onCreate()");

        tvLastLocation = (TextView) findViewById(R.id.tv_last_location);

        callConnection();
    }


    private synchronized void callConnection(){
        Log.i("LOG", "LastLocationActivity.callConnection()");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    // LISTENERS
    @Override
    public void onConnected(Bundle bundle) {
        Log.i("LOG", "LastLocationActivity.onConnected(" + bundle + ")");

        Location l = LocationServices
                .FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if(l != null){
            tvLastLocation
                    .setText( Html.fromHtml("<b>Last coordinate:</b><br />Latitude: "+l.getLatitude()+"<br />Longitude: "+l.getLongitude()) );
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "LastLocationActivity.onConnectionSuspended(" + i + ")");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("LOG", "LastLocationActivity.onConnectionFailed(" + connectionResult + ")");
    }
}
