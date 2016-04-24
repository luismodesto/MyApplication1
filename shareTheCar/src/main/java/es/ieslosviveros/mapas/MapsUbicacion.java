package es.ieslosviveros.mapas;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import es.ieslosviveros.kioto.DatosUsuario;
import es.ieslosviveros.www.myapplication.R;

public class MapsUbicacion extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_ubicacion);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //savedInstanceState.
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap1 = googleMap;
        LocationManager locat = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final String gpsLocationProvider = LocationManager.GPS_PROVIDER;
        final String networkLocationProvider = LocationManager.NETWORK_PROVIDER;
        Location lastKnownLocation_byGps = locat.getLastKnownLocation(gpsLocationProvider);
        Location lastKnownLocation_byNetwork = locat.getLastKnownLocation(networkLocationProvider);
        SharedPreferences prefs = getSharedPreferences("" + R.string.preferencias, Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);

        String lat=prefs.getString("ubicacionLatitud", "");
        String lng=prefs.getString("ubicacionLongitud", "");
        LatLng aqui;
        if (lat.length()>1){
            aqui = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        }else {
            aqui = new LatLng(lastKnownLocation_byNetwork.getLatitude(), lastKnownLocation_byNetwork.getLongitude());
        }
        mMap1.addMarker(new MarkerOptions().position(aqui).title("Ubicación"));
        // Add a marker in Sydney and move the camera
         //mMap.moveCamera(CameraUpdateFactory.newLatLng(aqui));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                     pongoUbicacion(latLng);

            }
        });

        mMap1.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                System.out.println("-----------strat --arrastro--" + arg0.getPosition() + " - " + arg0.getTitle() + " - " + arg0.getId() + "  - "  );
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // TODO Auto-generated method stub
                LatLng dragPosition = marker.getPosition();
                double dragLat = dragPosition.latitude;
                double dragLong = dragPosition.longitude;
                LatLng aqui=new LatLng(dragLat,dragLong);
               // waypoints.add(marker.getPosition());
                //Toast.makeText(FragmentActivity.this, "After onMarkerDragEnd position: " + dragLat + "  " + dragLong, Toast.LENGTH_LONG).show();
                System.out.println("------------fin -arrastro--");
                pongoUbicacion(aqui);
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                System.out.println("-------------arrastro--");
            }
        });


        pongoUbicacion(aqui);

    }


    public void pongoUbicacion(LatLng aqui){
        mMap1.clear();
        mMap1.addMarker(new MarkerOptions().position(aqui).title("Ubicación"));
        // Add a marker in Sydney and move the camera
        mMap1.moveCamera(CameraUpdateFactory.newLatLng(aqui));
        mMap1.animateCamera(CameraUpdateFactory.zoomTo(18));
        SharedPreferences prefs = getSharedPreferences("" + R.string.preferencias, Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ubicacionLongitud", "" + aqui.longitude);
        editor.putString("ubicacionLatitud", "" + aqui.latitude);
        editor.commit();
        EditText ubicacion=(EditText)findViewById(R.id.ubicacion);
        //ubicacion.setText(""+aqui.longitude+" - " +aqui.latitude);
        DatosUsuario.ubicacion.setText(""+aqui.longitude+" - " +aqui.latitude);
        System.out.println("-------------pongo ubicacion rrastro--");
    }

}
