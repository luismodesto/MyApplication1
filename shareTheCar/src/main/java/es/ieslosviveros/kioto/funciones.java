package es.ieslosviveros.kioto;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by luis on 14/05/2016.
 */
public class Funciones extends FragmentActivity {

 public Funciones(){

 }
    public LatLng getAqui(){
        LatLng yaqui=new LatLng(37.373034,-6.0131858);

        try {
            LocationManager locat = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location lastKnownLocation_byGps = locat.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location lastKnownLocation_byNetwork = locat.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            yaqui = new LatLng(lastKnownLocation_byNetwork.getLatitude(), lastKnownLocation_byNetwork.getLongitude());
        } catch (Exception e) {
            //dialogGPS(this.getContext()); // lets the user know there is a problem with the gps
            e.printStackTrace();
        }
        return yaqui;
    }

}
