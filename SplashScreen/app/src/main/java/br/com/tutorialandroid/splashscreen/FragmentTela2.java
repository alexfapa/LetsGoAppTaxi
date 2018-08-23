package br.com.tutorialandroid.splashscreen;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.List;

public class FragmentTela2 extends FragmentActivity {

    private SupportMapFragment mapFrag;
    private GoogleMap mMap;
    private Marker marker;
    private Polyline polyline;
    private List<LatLng> list;
    private long distance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_fragment_tela2);



    }
}